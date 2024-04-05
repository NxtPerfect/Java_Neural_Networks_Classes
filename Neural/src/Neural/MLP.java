package Neural;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MLP extends JFrame {
	// Elementy GUI
	private final JButton buttonRozpoznaj, buttonWyczysc, buttonUczSiec, buttonDodajCU, buttonZapiszCU, buttonZapiszCT,
			buttonTest;
	private final JRadioButton radioM, radioV, radioW, radioInny;
	private JLabel labelRozpoznanyZnak;
	private String rozpoznanyZnak = "_";
	private final DrawingPanel drawingPanel;
	static JLabel labelSkutecznoscSieci, labelIloscCU;
	static JLabel labelLiczbaEpok;

	// Dane sieci
	private ArrayList<boolean[]> ciagLiter;
	private ArrayList<boolean[]> ciagMacierzy;
	private int[] ileNeuronowWarstwa;
	private static final int liczbaLiter = 3;
	private static final int liczbaWarstw = 3;
	private static final int macierzWierszy = 8;
	private static final int macierzKolumn = 8;
	private static final int warstwaUkryta = 10;
	private int literWZbiorze;
	private int liczbaPikseli;
	private JLabel labelRozpoznanaLiterk = null;
	private Siec siec;

	public MLP(String name) {
		super(name);

		setTitle("Projekt 1");
		setSize(1000, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(1, 2));

		// Ustawienie okna na środku ekranu
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		int centerX = d.width / 2;
		int centerY = d.height / 2;
		int windowWidth = 1000;
		int windowHeight = 600;
		setBounds(centerX - windowWidth / 2, centerY - windowHeight / 2, windowWidth, windowHeight);

		// Inicjalizacja list
		ciagMacierzy = new ArrayList<boolean[]>();
		ciagLiter = new ArrayList<boolean[]>();

		// Dodanie panelu rysującego
		drawingPanel = new DrawingPanel();
		add(drawingPanel);

		// Utworzenie panelu z przyciskami
		JPanel buttonPanel = new JPanel(new GridLayout(11, 1, 5, 5));

		buttonRozpoznaj = new JButton("Rozpoznaj");
		buttonPanel.add(buttonRozpoznaj);
		buttonRozpoznaj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean[] macierzBoleanLiter = drawingPanel.getObszarLitera(macierzKolumn, macierzWierszy);
				double[] wejsciePikseleLiter = zamienTabliceBolNaDouble(macierzBoleanLiter);
				double[] rozpoznanaLitera = siec.oblicz_wyjscie(wejsciePikseleLiter);
				sprawdzLitere(rozpoznanaLitera);
			}
		});

		buttonWyczysc = new JButton("Wyczyść");
		buttonPanel.add(buttonWyczysc);
		buttonWyczysc.addActionListener(e -> {
			drawingPanel.wyczysc();
		});

		buttonUczSiec = new JButton("Ucz Sieć");
		buttonPanel.add(buttonUczSiec);
		buttonUczSiec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				wczytajPlik(arg0);
			}
		});

		buttonDodajCU = new JButton("Dodaj do ciągu uczącego");
		buttonPanel.add(buttonDodajCU);
		buttonDodajCU.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean[] radioValues = null;
				radioValues = new boolean[] { radioM.isSelected(), radioV.isSelected(), radioW.isSelected() };
//        	    if(radioM.isSelected())
//        	        radioValues = new boolean[] { true, false, false};
//        	    else if(radioV.isSelected())
//        	        radioValues = new boolean[] { false, true, false};
//        	    else if(radioW.isSelected())
//        	        radioValues = new boolean[] { false, false, true};
//        	    else if(radioInny.isSelected())
//        	        radioValues = new boolean[] { false, false, false};
				ciagMacierzy.add(drawingPanel.getObszarLitera(macierzKolumn, macierzWierszy));
				ciagLiter.add(radioValues);
				drawingPanel.wyczysc();
			}
		});
		buttonZapiszCU = new JButton("Zapisz ciąg uczący");
		buttonPanel.add(buttonZapiszCU);

		buttonZapiszCT = new JButton("Zapisz ciąg testowy");
		buttonPanel.add(buttonZapiszCT);

		buttonTest = new JButton("Testuj");
		buttonPanel.add(buttonTest);

		labelRozpoznanyZnak = new JLabel("Rozpoznany znak to: " + rozpoznanyZnak);
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

		add(buttonPanel);
		setVisible(true);
	}
	
	private double[] zamienTabliceBolNaDouble(boolean[] macierz){
		double[] wynik = new double[] {};
		for(int i = 0; i < macierz.length; i++){
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
			rozpoznanyZnak = "Brak";
			return;
		}

		if (outIndex == 0) {
			rozpoznanyZnak = "M";
			return;
		}
		if (outIndex == 1) {
			rozpoznanyZnak = "V";
			return;
		}
		rozpoznanyZnak = "W";
		return;
	}
	
	private void wczytajPlik(ActionEvent e) {
		String sciezka = "src/Neural/ciagUczacy.txt";
		try {
			File file = new File(sciezka);
			if (!file.exists()) {
				JOptionPane.showMessageDialog(null, "Plik nie istnieje.", "Błąd", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, "Nie można znaleźć pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;
		}
		ciagLiter = new ArrayList<boolean []>();
		ciagMacierzy = new ArrayList<boolean []>();
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
				ciagMacierzy.add(litera);
			}
			br.close();
			JOptionPane.showMessageDialog(null, "Sukces odczytu pliku.", "Sukces", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Błąd odczytu pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			return;
		}
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
