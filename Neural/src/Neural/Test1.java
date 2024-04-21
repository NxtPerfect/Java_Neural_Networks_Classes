package Neural;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class Test1 extends JFrame {
	// Interfejs
	private JButton buttonRozpoznaj, buttonWyczysc, buttonTrenuj, buttonDodajCiag, buttonZapiszCiag, buttonTest;
	private JRadioButton radioM, radioV, radioW, radioInna;
	private JLabel labelRozpoznanaLitera;
	static JLabel labelSkutecznoscSieci, labelLiczbaEpok;

	// Pola
	private ArrayList<boolean[]> CiagLiter;
	private ArrayList<boolean[]> CiagMacierzy;
	private int[] IleNeuronowWarstwa;
	private static final int LiczbaLiter = 3;
	private static final int LiczbaWarstw = 3;
	private static final int MacierzWiersze = 8;
	private static final int MacierzKolumny = 8;
	private static final int WarstwaUkryta = 10;
	private int LiterWZbiorze;
	private int LiczbaPikseli;
	private Siec siec;

	public Test1() {
		setTitle("Sieć MLP");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(1, 1));

		// Stworzenie sieci
		IleNeuronowWarstwa = new int[LiczbaWarstw];
		IleNeuronowWarstwa[0] = MacierzKolumny * MacierzWiersze;
		IleNeuronowWarstwa[1] = WarstwaUkryta;
		IleNeuronowWarstwa[2] = LiczbaLiter;
		inicjujSiec(IleNeuronowWarstwa);

		// Listy labeli i danych
		CiagLiter = new ArrayList<boolean[]>();
		CiagMacierzy = new ArrayList<boolean[]>();

		// Wyśrodkowanie ekranu
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = d.width / 2;
		int centerY = d.height / 2;
		int windowWidth = d.width * 3 / 4;
		int windowHeight = d.height * 3 / 4;
		setSize(windowWidth, windowHeight);
		setBounds(centerX - windowWidth / 2, centerY - windowHeight / 2, windowWidth, windowHeight);

		// Tworzenie panelu do rysowania
		PanelRysujacy dp = new PanelRysujacy();
		add(dp);

		// Layout przycisków
		JPanel buttonPanel = new JPanel(new GridLayout(7, 1));

		// Przycisk rozpoznaj
		buttonRozpoznaj = new JButton("Rozpoznaj");
		buttonRozpoznaj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean[] macierzBoolLiter = dp.getObszarLitera(MacierzKolumny, MacierzWiersze);
				double[] wejsciePikseleLiter = tabBolNaDouble(macierzBoolLiter);
				double[] rozpoznanaLitera = siec.ObliczWyjscie(wejsciePikseleLiter);
				sprJakaLitera(rozpoznanaLitera);
			}
		});

		buttonPanel.add(buttonRozpoznaj);

		// Przycisk wyczyść
		// czyści panel rysujący oraz predykcje litery
		buttonWyczysc = new JButton("Wyczyść");
		buttonPanel.add(buttonWyczysc);
		buttonWyczysc.addActionListener(e -> {
			dp.clear();
			labelRozpoznanaLitera.setText("Rozpoznana litera:");
		});

		// Przycisk trenuj
		// trenuje sieć neuronową i podaje skuteczność
		// wykorzystuje plik src/Neural/ciagUczacyt1.txt do trenowania
		// oraz plik src/Neural/ciagTreningowyt1.txt do testowania skuteczności
		buttonTrenuj = new JButton("Trenuj");
		buttonPanel.add(buttonTrenuj);

		buttonTrenuj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				buttonTrenuj.setText("Trwa uczenie...");
				buttonTrenuj.setEnabled(false);
				wczytajPlik(e);
				buttonTrenuj.setText("Trenuj");
				buttonTrenuj.setEnabled(true);
				setCursor(Cursor.getDefaultCursor());
			}
		});

		// Przycisk testuj
		// testuje zapisany ciąg na ciągu treningowym i podaje skuteczność
		// zbędny
		buttonTest = new JButton("Testuj");
		buttonPanel.add(buttonTest);
		buttonTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Testuj();
			}

		});

		// Przycisk dodaj do ciągu
		// dodaje narysowany element do ciągu
		buttonDodajCiag = new JButton("Dodaj do ciągu");
		buttonPanel.add(buttonDodajCiag);
		buttonDodajCiag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean[] radioValues = null;
				/*
				 * M - 1 0 0 V - 0 1 0 W - 0 0 1 Inna - 0 0 0
				 */
				radioValues = new boolean[] { radioM.isSelected(), radioV.isSelected(), radioW.isSelected() };
				CiagMacierzy.add(dp.getObszarLitera(MacierzKolumny, MacierzWiersze));
				CiagLiter.add(radioValues);
				dp.clear();
			}
		});

		// Przycisk zapisz ciąŋ
		// zapisuje elementy dodane do ciągu z poprzedniego przycisku
		// i wywołuje systemowe okienko modalne do zapisu pliku .txt
		buttonZapiszCiag = new JButton("Zapisz ciąg");
		buttonPanel.add(buttonZapiszCiag);
		buttonZapiszCiag.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zapiszCiag(e);
			}
		});

		// Label skuteczność sieci
		// pokazuje procentowo ile predykcji było poprawnych
		// na ciągu treningowym
		labelSkutecznoscSieci = new JLabel("Skuteczność sieci: brak");
		buttonPanel.add(labelSkutecznoscSieci);

		// Label liczba epok
		// pokazuje ile epok sieć wykorzystała do nauki
		// zanim trenowanie zostało zatrzymane
		labelLiczbaEpok = new JLabel("Liczba Epok Trenowania: brak");
		buttonPanel.add(labelLiczbaEpok);

		// Label rozpoznana litera
		// pokazuje jaką literę rozpoznała sieć
		labelRozpoznanaLitera = new JLabel("Rozpoznana litera: ");
		buttonPanel.add(labelRozpoznanaLitera);

		buttonPanel.add(new JPanel());

		// Radiobuttony liter
		// potrzebne do zapisania ciągu wraz z labelem
		// wykorzystywane do zapisu labelu do ciągu
		ButtonGroup grupa = new ButtonGroup();

		// Radio M
		radioM = new JRadioButton("M");
		radioM.setSelected(true);
		buttonPanel.add(radioM);
		grupa.add(radioM);

		// Radio V
		radioV = new JRadioButton("V");
		buttonPanel.add(radioV);
		grupa.add(radioV);

		// Radio W
		radioW = new JRadioButton("W");
		buttonPanel.add(radioW);
		grupa.add(radioW);

		// Radio inna
		radioInna = new JRadioButton("Inna literka");
		buttonPanel.add(radioInna);
		grupa.add(radioInna);

		add(buttonPanel);
		setVisible(true);
	}

	// Inicjalizacja sieci
	private void inicjujSiec(int[] Warstwy) {
		siec = new Siec(Warstwy[0], Warstwy.length, Warstwy);
	}

	// ***************MAIN****************
	public static void main(String[] args) {
		new Test1();
	}

	private String[] wczytajPlik(String NazwaPliku) {
		File file = new File(NazwaPliku);
		String[] test;
		try {
			Scanner in = new Scanner(file);
			ArrayList<String> temp = new ArrayList<String>();
			while (in.hasNext()) {
				temp.add(in.next());
			}
			test = temp.toArray(new String[0]);
			in.close();
			return test;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Wczytuje ciąg uczący
	// tworzy nową sięc neuronów i wypełnia dane
	// następnie ją trenuje
	private void wczytajPlik(ActionEvent e) {
		try {
			String sciezka = "src/Neural/ciagUczacyt1.txt";
			File file = new File(sciezka);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "Plik nie istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE);
				return;
			}
			CiagLiter = new ArrayList<boolean[]>();
			CiagMacierzy = new ArrayList<boolean[]>();
			FileInputStream fstream = new FileInputStream(sciezka);
			DataInputStream dataInput = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(dataInput));
			String liniaPliku;
			LiterWZbiorze = Integer.parseInt(br.readLine()); // 3
			LiczbaPikseli = Integer.parseInt(br.readLine()); // 64
			while ((liniaPliku = br.readLine()) != null) {
				if (CiagLiter.size() == CiagMacierzy.size()) {
					boolean[] litery = new boolean[LiterWZbiorze];
					for (int i = 0; i < LiterWZbiorze; i++) {
						litery[i] = liniaPliku.charAt(i) == '0' ? false : true;
					}
					CiagLiter.add(litery);
				} else {
					boolean[] macierz = new boolean[LiczbaPikseli];
					for (int i = 0; i < LiczbaPikseli; i++) {
						macierz[i] = liniaPliku.charAt(i) == '0' ? false : true;
					}
					CiagMacierzy.add(macierz);
				}
			}
			br.close();
			JOptionPane.showMessageDialog(null, "Sukces odczytu pliku.\nKliknij ok aby trenować.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Błąd odczytu pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;
		}

		IleNeuronowWarstwa = new int[LiczbaWarstw];
		IleNeuronowWarstwa[0] = LiczbaPikseli;
		IleNeuronowWarstwa[1] = WarstwaUkryta;
		IleNeuronowWarstwa[2] = LiterWZbiorze;

		inicjujSiec(IleNeuronowWarstwa);

		ArrayList<double[]> listaLiterDouble = new ArrayList<double[]>();
		ArrayList<double[]> listaMacierzyDouble = new ArrayList<double[]>();

		for (int i = 0; i < CiagLiter.size(); i++) {
			boolean[] literyBoolean = CiagLiter.get(i);
			boolean[] macierzBoolean = CiagMacierzy.get(i);

			double[] listaLiter = new double[literyBoolean.length];
			double[] listaMacierzy = new double[macierzBoolean.length];

			for (int j = 0; j < listaLiter.length; j++) {
				listaLiter[j] = literyBoolean[j] ? 1.0 : 0.0;
			}
			for (int j = 0; j < listaMacierzy.length; j++) {
				listaMacierzy[j] = macierzBoolean[j] ? 1.0 : 0.0;
			}

			listaLiterDouble.add(listaLiter);
			listaMacierzyDouble.add(listaMacierzy);
		}

		siec.Trenuj(listaMacierzyDouble, listaLiterDouble);
		CiagLiter = null;
		CiagMacierzy = null;
	}

	// Zapisz ciąg do pliku
	// wykorzystuje systemowe okienko modalne
	private void zapiszCiag(ActionEvent e) {
		try {
			if (CiagLiter == null || CiagMacierzy == null)
				return;

			final JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File("ciag.txt"));
			fc.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
			int potwierdzenieOkna = fc.showSaveDialog(Test1.this);

			if (potwierdzenieOkna != JFileChooser.APPROVE_OPTION) {
				return;
			}

			File file = fc.getSelectedFile();
			String sciezka = file.getAbsolutePath();
			PrintWriter writer = new PrintWriter(sciezka, "UTF-8");

			writer.println(CiagLiter.get(0).length);
			writer.println(CiagMacierzy.get(0).length);

			for (int i = 0; i < CiagLiter.size(); i++) {
				boolean[] ObecnaLitera = CiagLiter.get(i);
				boolean[] ObecnaMacierz = CiagMacierzy.get(i);
				StringBuilder sb = new StringBuilder();

				for (boolean znak : ObecnaLitera) {
					sb.append(znak ? "1" : "0");
				}

				writer.println(sb);

				sb = new StringBuilder();
				for (boolean znak : ObecnaMacierz) {
					sb.append(znak ? "1" : "0");
				}
				writer.println(sb);
			}
			writer.close();
		} catch (Exception ex) {
		}
	}

	// Testuj ciąŋ
	// testuje ciąg odczytując plik src/Neural/ciagTestowyt1.txt
	public void Testuj() {
		int pktDlaSieci = 0;
		String[] trainingData = wczytajPlik("src/Neural/ciagTestowyt1.txt");

		double[] data = new double[64];
		int[] label = new int[3];

		for (int i = 2; i < trainingData.length; i++) {
			label[0] = (int) trainingData[i].charAt(0) - '0';
			label[1] = (int) trainingData[i].charAt(1) - '0';
			label[2] = (int) trainingData[i].charAt(2) - '0';
			i++;
			for (int j = 0; j < trainingData[i].length(); j++) {
				data[j] = (int) (trainingData[i].charAt(j) - '0');
			}
			double[] wynik = siec.ObliczWyjscie(data);
			int[] znak = { wynik[0] > 0.5 ? 1 : 0, wynik[1] > 0.5 ? 1 : 0, wynik[2] > 0.5 ? 1 : 0 };
			if ((znak[0] == label[0]) && (znak[1] == label[1]) && (znak[2] == label[2])) {
				pktDlaSieci++;
			}
		}
		double skutecznoscSieci = (double) pktDlaSieci / (trainingData.length / 2) * 100.0;
		labelSkutecznoscSieci.setText("Skuteczność sieci: " + String.format("%.2f", skutecznoscSieci) + "%");
	}

	private double[] tabBolNaDouble(boolean[] source) {
		double[] result = new double[source.length];
		for (int i = 0; i < source.length; i++) {
			result[i] = source[i] ? 1 : 0;
		}
		return result;
	}

	private void sprJakaLitera(double[] source) {
		int outCount = 0;
		int outIndex = -1;

		for (int i = 0; i < source.length; i++) {
			if (source[i] > 0.5) {
				outCount++;
				outIndex = i;
			}
		}

		if (outCount != 1) {
			outIndex = -1;
			labelRozpoznanaLitera.setText("Nie rozpoznano litery");
			return;
		}

		if (outIndex == 0) {
			labelRozpoznanaLitera.setText("Rozpoznana litera: M");
			return;
		}
		if (outIndex == 1) {
			labelRozpoznanaLitera.setText("Rozpoznana litera: V");
			return;
		}
		labelRozpoznanaLitera.setText("Rozpoznana litera: W");
		return;
	}

}