package Neural;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MLP extends JFrame {
	// Elementy GUI
	private final JButton buttonRozpoznaj, buttonWyczysc, buttonUczSiec, buttonDodajCU, buttonZapiszCU, buttonTest;
	private final JRadioButton radioM, radioV, radioW, radioInny;
	private JLabel labelRozpoznanyZnak;
	private final DrawingPanel drawingPanel;
	static JLabel labelSkutecznoscSieci, labelIloscCU, labelLiczbaEpok;

	// Dane sieci
	private ArrayList<boolean[]> ciagLiter;
	private ArrayList<boolean[]> ciagMacierzy;
	private int[] ileNeuronowWarstwa;
	private static final int liczbaLiter = 3; // 3
	private static final int liczbaWarstw = 3; // 3
	private static final int macierzWierszy = 8; // 8
	private static final int macierzKolumn = 8; // 8
	private static final int warstwaUkryta = 10; // 10
	private int literWZbiorze;
	private int liczbaPikseli;
	private Siec siec;

	public MLP(String name) {
		super(name);

		setTitle(name);
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(1, 1));

		// Ustawienie okna na środku ekranu
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		int centerX = d.width / 2;
		int centerY = d.height / 2;
		int windowWidth = 1000;
		int windowHeight = 600;
		setBounds(centerX - windowWidth / 2, centerY - windowHeight / 2, windowWidth, windowHeight);
		setMinimumSize(new Dimension(900, 300));

		// Inicjalizacja sieci
		ileNeuronowWarstwa = new int[liczbaWarstw];
		ileNeuronowWarstwa[0] = macierzKolumn * macierzWierszy;
		ileNeuronowWarstwa[1] = warstwaUkryta;
//		ileNeuronowWarstwa[2] = warstwaUkryta;
//		ileNeuronowWarstwa[3] = warstwaUkryta;
		ileNeuronowWarstwa[2] = liczbaLiter;
		nowaSiec(ileNeuronowWarstwa);

		// Inicjalizacja list
		ciagMacierzy = new ArrayList<boolean[]>();
		ciagLiter = new ArrayList<boolean[]>();

		// Dodanie panelu rysującego
		drawingPanel = new DrawingPanel();
		add(drawingPanel);

		// Utworzenie panelu z przyciskami
		JPanel buttonPanel = new JPanel(new GridLayout(14, 1));

		buttonRozpoznaj = new JButton("Rozpoznaj");
		buttonPanel.add(buttonRozpoznaj);
		buttonRozpoznaj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean[] macierzBoleanLiter = drawingPanel.getObszarLitera(macierzKolumn, macierzWierszy);
				double[] wejsciePikseleLiter = zamienTabliceBolNaDouble(macierzBoleanLiter);
				double[] rozpoznanaLitera = siec.obliczWyjscie(wejsciePikseleLiter, false);
				sprawdzLitere(rozpoznanaLitera);
			}
		});

		buttonWyczysc = new JButton("Wyczyść");
		buttonPanel.add(buttonWyczysc);
		buttonWyczysc.addActionListener(e -> {
			drawingPanel.wyczysc();
			labelRozpoznanyZnak.setText("Rozpoznany znak to: brak");
		});

		buttonUczSiec = new JButton("Ucz Sieć");
//		buttonUczSiec.setEnabled(false); // do dodawania do ciagu
		buttonPanel.add(buttonUczSiec);
		buttonUczSiec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				buttonUczSiec.setEnabled(false);
				buttonUczSiec.setText("Trwa uczenie...");
				wczytajPlik(arg0);
				buttonUczSiec.setEnabled(true);
				setCursor(Cursor.getDefaultCursor());
				buttonUczSiec.setText("Ucz Sieć");
			}
		});

		buttonDodajCU = new JButton("Dodaj do ciągu uczącego");
		buttonPanel.add(buttonDodajCU);
		buttonDodajCU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean[] radioValues = null;
				/*
				 * prawda fałsz fałsz dla M \\ fałsz prawda fałsz dla V \\ fałsz fałsz prawda
				 * dla W \\
				 */
				radioValues = new boolean[] { radioM.isSelected(), radioV.isSelected(), radioW.isSelected() };
				ciagMacierzy.add(drawingPanel.getObszarLitera(macierzKolumn, macierzWierszy));
				ciagLiter.add(radioValues);
				labelRozpoznanyZnak.setText("Rozpoznany znak to: brak");
				drawingPanel.wyczysc();
			}
		});
		buttonZapiszCU = new JButton("Zapisz ciąg");
		buttonPanel.add(buttonZapiszCU);
		buttonZapiszCU.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				zapiszCiag(arg0);
			}
		});

		buttonTest = new JButton("Testuj");
		buttonPanel.add(buttonTest);
		buttonTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int pktDlaSieci = 0;
				String[] trainingData = wczytajPlik("src/Neural/ciagTestowy1.txt");
				String[] labels = new String[Math.round(trainingData.length / 2)];

//				double[] data = new double[256+3];
				double[] data = new double[64];

				int k = 0; // Label index
				for (int i = 2; i < trainingData.length; i++) {
					// Usuwa spacje i nowe linie ze stringa
					trainingData[i] = trainingData[i].replaceAll("\\s", "");
					trainingData[i] = trainingData[i].replaceAll("\\n", "");
					if (trainingData[i].length() == 3) {
						labels[k] = trainingData[i];
						k += 1;
						continue;
					}
					for (int j = 0; j < trainingData[i].length(); j++) {
						data[j] = (int) (trainingData[i].charAt(j) - '0');
					}
					double[] wynik = siec.obliczWyjscie(data, false);
					int[] znak = { wynik[0] > 0.5 ? 1 : 0, wynik[1] > 0.5 ? 1 : 0, wynik[2] > 0.5 ? 1 : 0 };
					if (znak[0] == (int) (labels[k - 1].charAt(0) - '0')
							&& znak[1] == (int) (labels[k - 1].charAt(1) - '0')
							&& znak[2] == (int) (labels[k - 1].charAt(2) - '0')) {
						pktDlaSieci++;
					}
					System.out.println("Oczekiwany Znak: " + (labels[k - 1].equals("100") ? "M"
							: labels[k - 1].equals("010") ? "V" : labels[k - 1].equals("001") ? "W" : "Inny"));
					System.out.println("Znak: " + (znak[0] > 0 ? "M" : znak[1] > 0 ? "V" : znak[2] > 0 ? "W" : "Inny"));
				}
				double skutecznoscSieci = (double) pktDlaSieci / ((trainingData.length - 2.0) / 2.0) * 100.0;
				labelSkutecznoscSieci.setText("Skuteczność: " + String.format("%.2f", skutecznoscSieci) + "%");
				if (String.format("%.2f", skutecznoscSieci).equals("36.11") || skutecznoscSieci < 50.0) {
					labelSkutecznoscSieci.setForeground(Color.RED);
					return;
				}
				labelSkutecznoscSieci.setForeground(Color.BLACK);
				if (skutecznoscSieci > 75.0) {
					labelSkutecznoscSieci.setForeground(Color.GREEN);
					return;
				}
				ciagLiter.clear();
				ciagMacierzy.clear();
			}
		});

		labelRozpoznanyZnak = new JLabel("Rozpoznany znak to: brak");
		labelRozpoznanyZnak.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		buttonPanel.add(labelRozpoznanyZnak);

		ButtonGroup radioGroup = new ButtonGroup();

		radioM = new JRadioButton("M", true);
		buttonPanel.add(radioM);
		radioGroup.add(radioM);

		radioV = new JRadioButton("V");
		buttonPanel.add(radioV);
		radioGroup.add(radioV);

		radioW = new JRadioButton("W");
		buttonPanel.add(radioW);
		radioGroup.add(radioW);

		radioInny = new JRadioButton("Inny znak");
		buttonPanel.add(radioInny);
		radioGroup.add(radioInny);

		// ---------------Label
		labelSkutecznoscSieci = new JLabel("Skuteczność:");
		buttonPanel.add(labelSkutecznoscSieci);

		// ---------------Label
		labelLiczbaEpok = new JLabel("Epoki:");
		buttonPanel.add(labelLiczbaEpok);

		// ---------------Label
		labelIloscCU = new JLabel("Ilosc CU: ");
		buttonPanel.add(labelIloscCU);

		add(buttonPanel);
		setVisible(true);
	}

	private double[] zamienTabliceBolNaDouble(boolean[] macierz) {
		double[] wynik = new double[macierz.length];
		for (int i = 0; i < macierz.length; i++) {
			wynik[i] = macierz[i] ? 1.0 : 0.0;
		}
		return wynik;
	}

	private void sprawdzLitere(double[] wejscie) {
		int outCount = 0;
		int outIndex = -1;

		for (int i = 0; i < wejscie.length; i++) {
			if (wejscie[i] > 0.5) {
				outCount++;
				outIndex = i;
			}
		}
		if (outCount != 1) {
			outIndex = -1;
			labelRozpoznanyZnak.setText("Rozpoznany znak to: inny");
			return;
		}

		if (outIndex == 0) {
			labelRozpoznanyZnak.setText("Rozpoznany znak to: M");
			return;
		}
		if (outIndex == 1) {
			labelRozpoznanyZnak.setText("Rozpoznany znak to: V");
			return;
		}
		if (outIndex == 2) {
			labelRozpoznanyZnak.setText("Rozpoznany znak to: W");
			return;
		}
		labelRozpoznanyZnak.setText("Rozpoznany znak to: inny");
		return;
	}

	// Wczytanie ciągu uczącego z pliku
	// w folderze src/NazwaProjektu/ciagUczacy.txt
	private void wczytajPlik(ActionEvent e) {
		String sciezka = "src/Neural/ciagUczacy1.txt";
		try {
			File file = new File(sciezka);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "Plik nie istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Nie można znaleźć pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;
		}
		ciagLiter = new ArrayList<boolean[]>();
		ciagMacierzy = new ArrayList<boolean[]>();
		try {
			FileInputStream fstream = new FileInputStream(sciezka);
			DataInputStream dataInput = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(dataInput));
			String liniaPliku;
			literWZbiorze = Integer.parseInt(br.readLine());
			liczbaPikseli = Integer.parseInt(br.readLine());
			while ((liniaPliku = br.readLine()) != null) {
				String[] tokeny = liniaPliku.split(" ");
				boolean[] litera = new boolean[literWZbiorze];
				for (int i = 0; i < tokeny.length; i++) {
					litera[i] = tokeny[i].equals("1");
				}
				ciagLiter.add(litera);

				// Przeczytaj kolejną linię
				liniaPliku = br.readLine();
				if (liniaPliku == null) {
					break;
				}
				boolean[] macierz = new boolean[liczbaPikseli];
				for (int i = 0; i < tokeny.length; i++) {
					macierz[i] = tokeny[i].equals("1");
				}
				ciagMacierzy.add(macierz);
			}
			br.close();
			JOptionPane.showMessageDialog(null, "Sukces odczytu pliku.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Błąd odczytu pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;
		}

		ileNeuronowWarstwa = new int[liczbaWarstw];
		ileNeuronowWarstwa[0] = liczbaPikseli;
		ileNeuronowWarstwa[1] = warstwaUkryta;
//		ileNeuronowWarstwa[2] = warstwaUkryta;
//		ileNeuronowWarstwa[3] = warstwaUkryta;
		ileNeuronowWarstwa[2] = literWZbiorze;

		nowaSiec(ileNeuronowWarstwa);

		ArrayList<double[]> ListaLiterekDouble = new ArrayList<double[]>();
		ArrayList<double[]> ListaMacierzyDouble = new ArrayList<double[]>();

		for (int i = 0; i < ciagLiter.size(); i++) {
			boolean[] LiterkiBoolean = ciagLiter.get(i);
			boolean[] MacierzBoolean = ciagMacierzy.get(i);

			double[] ListaLiterek = new double[LiterkiBoolean.length];
			double[] ListaMacierzy = new double[MacierzBoolean.length];

			for (int j = 0; j < ListaLiterek.length; j++) {
				ListaLiterek[j] = LiterkiBoolean[j] ? 1.0 : 0.0;
			}
			for (int j = 0; j < ListaMacierzy.length; j++) {
				ListaMacierzy[j] = MacierzBoolean[j] ? 1.0 : 0.0;
			}

			ListaLiterekDouble.add(ListaLiterek);
			ListaMacierzyDouble.add(ListaMacierzy);
		}

		siec.UczSieZCiagu(ListaMacierzyDouble, ListaLiterekDouble, true);
	}

	private String[] wczytajPlik(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		try {
			ArrayList<String> temp = new ArrayList<String>();
			Scanner in = new Scanner(file);
			while (in.hasNextLine()) {
				temp.add(in.nextLine() + "\n");
			}
			in.close();
			return temp.toArray(new String[0]);
		} catch (Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
			return null;
		}
	}

	private void zapiszCiag(ActionEvent e) {
		if (ciagLiter == null || ciagMacierzy == null) {
			return;
		}
		try {
			final JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File("ciagUczacy.txt"));
			fc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
			int potwierdz = fc.showSaveDialog(MLP.this);
			if (potwierdz != JFileChooser.APPROVE_OPTION) {
				return;
			}
			// Zapisz ciąg uczący do pliku
			File file = fc.getSelectedFile();
			String path = file.getAbsolutePath();
			PrintWriter pw = new PrintWriter(path, "UTF-8");
			pw.println(ciagLiter.get(0).length);
			pw.println(ciagMacierzy.get(0).length);

			for (int i = 0; i < ciagLiter.size(); i++) {
				boolean[] obecnyCiagLiter = ciagLiter.get(i);
				boolean[] obecnyCiagMacierz = ciagMacierzy.get(i);
				StringBuilder sb = new StringBuilder();
				for (boolean litera : obecnyCiagLiter) {
					sb.append((litera ? "1" : "0") + " ");
//					sb.append(litera ? "1" : "0");
				}

				sb.append("\n");

				for (boolean litera : obecnyCiagMacierz) {
					sb.append((litera ? "1" : "0") + " ");
				}
				pw.println(sb);
			}
			pw.close();
			ciagMacierzy = new ArrayList<boolean[]>();
			ciagLiter = new ArrayList<boolean[]>();
			JOptionPane.showMessageDialog(null, "Sukces zapisu pliku.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			System.err.println(ex);
			ex.printStackTrace();
			return;
		}
	}

	private void nowaSiec(int[] warstwa) {
		siec = new Siec(warstwa[0], warstwa.length, warstwa);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new MLP("Neural Network");
			}
		});
	}

}
