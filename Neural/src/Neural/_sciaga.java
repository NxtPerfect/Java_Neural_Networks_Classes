package Neural;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Scanner;
import java.text.DecimalFormat;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import javax.swing.*;
import java.io.PrintWriter;


public class _sciaga extends JFrame {
	//Interfejs
    private JButton buttonRozpoznaj, buttonWyczysc, buttonUczSiec, buttonDodajCU, buttonZapiszCU, buttonZapiszCT, buttonTest, testowy;
    private JRadioButton radioS, radio5, radioZ, radioBrak;
    private JLabel labelRozpoznanaLiterka;
	static JLabel labelSkutecznoscSieci, labelIloscCU;
	static JLabel  labelLiczbaEpok;
    
    //Pola
    private ArrayList<boolean[]> CiagLiterek;
	private ArrayList<boolean[]> CiagMacierzy;
	private int[] IleNeuronowWarstwa;
	private static final int LiczbaLiter = 3;
	private static final int LiczbaWarstw = 3;
	private static final int MacierzWiersze = 8;
	private static final int MacierzKolumny = 8;
	private static final int WarstwaUkryta = 10;
	private int LiterekWZbiorze;
	private int LPikseli;
	private  JLabel labelRozpoznanaLiterk = null;   
	private Siec siec;
        
    public _sciaga() {
        setTitle("Projekt nr 1");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));
        
        //Inicjalizacja sieci
        IleNeuronowWarstwa = new int[LiczbaWarstw];
        IleNeuronowWarstwa[0] = MacierzKolumny*MacierzWiersze;
        IleNeuronowWarstwa[1] = WarstwaUkryta;
        IleNeuronowWarstwa[2] = LiczbaLiter;
		initializeNetwork(IleNeuronowWarstwa);
		
		//Listy
		CiagLiterek = new ArrayList<boolean[]>();
    	CiagMacierzy = new ArrayList<boolean[]>();
		     
        //-------------------Ustawienie ekrnau na środku
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;
        int windowWidth = 800;
        int windowHeight = 600;
        setBounds(centerX - windowWidth / 2, centerY - windowHeight / 2, windowWidth, windowHeight);
        
        // Tworzenie panelu do rysowania
        _PanelRysujacy dp = new _PanelRysujacy();
        add(dp);
        
        //----------------- Tworzenie przycisków
        
        JPanel buttonPanel = new JPanel(new GridLayout(11, 1, 5, 5));
        //1------------------------- ROZPOZNAJ
        buttonRozpoznaj = new JButton("Rozpoznaj");
        buttonRozpoznaj.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean[] MacierzBoleanLiter = dp.getObszarLitera(MacierzKolumny, MacierzWiersze);
				double[] WejsciePikseleLiter = TabBolNaDouble(MacierzBoleanLiter);
				double[] RozpoznanaLitera = siec.ObliczWyjscie(WejsciePikseleLiter);
				SprJakaLitera(RozpoznanaLitera);
			}
		});
        
        buttonPanel.add(buttonRozpoznaj);
        //2-------------------------- WYCZYŚĆ
        buttonWyczysc = new JButton("Wyczyść");
        buttonPanel.add(buttonWyczysc);       
        buttonWyczysc.addActionListener(e -> {
            dp.clear();
        });
        
        
        //3-------------------------- WCZYTAJ CIĄG UCZĄCY
        buttonUczSiec = new JButton("Ucz Sieć");
        buttonPanel.add(buttonUczSiec);
        
        buttonUczSiec.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				WczytajPlik(e);
			}
		});
        //4-------------------------- DODAJ DO CIĄGU UCZĄCEGO 
        buttonDodajCU = new JButton("Dodaj do ciągu uczącego");
        buttonPanel.add(buttonDodajCU);
        buttonDodajCU.addActionListener(new ActionListener() {			
        	@Override
        	public void actionPerformed(ActionEvent e) {
        	    // TODO Auto-generated method stub
        	    boolean[] radioValues = null;
        	    if(radioS.isSelected())
        	        radioValues = new boolean[] { true, false, false};
        	    else if(radio5.isSelected())
        	        radioValues = new boolean[] { false, true, false};
        	    else if(radioZ.isSelected())
        	        radioValues = new boolean[] { false, false, true};
        	    else if(radioBrak.isSelected())
        	        radioValues = new boolean[] { false, false, false};
        	    CiagMacierzy.add(dp.getObszarLitera(MacierzKolumny, MacierzWiersze));
        	    CiagLiterek.add(radioValues);
        	    dp.clear();
        	}
		});
        //5-------------------------- ZAPISZ CIĄG UCZĄCY
        buttonZapiszCU = new JButton("Zapisz ciąg uczący");
        buttonPanel.add(buttonZapiszCU);
        buttonZapiszCU.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ZapiszCU(e);
			}
		});
        //6-------------------------- ZAPISZ CIĄG TESTOWY
        buttonZapiszCT = new JButton("Zapisz ciąg testowy");
        buttonPanel.add(buttonZapiszCT);
        buttonZapiszCT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ZapiszCT(e);
				
			}
		});
        //7-------------------------- TESTUJ
        buttonTest = new JButton("Testuj");
        buttonPanel.add(buttonTest);
        buttonTest.addActionListener(new ActionListener() {       	
        	@Override
			public void actionPerformed(ActionEvent e) {
				int pktDlaSieci = 0;
                String[] trainingData = WczytajPlik("CiagTreningowy.txt");
              
                double[] data = new double[64];

                for (int i = 0; i < 100; i++) {
                    for (int j = 0; j < 64; j++) {
                        data[j] = (int) (trainingData[i].charAt(j) - '0');
                    }
                    double[] wynik = siec.ObliczWyjscie(data);
                    int[] znak = {wynik[0]>0.5?1:0, wynik[1]>0.5?1:0, wynik[2]>0.5?1:0};
                        if (znak[0]==(int)(trainingData[i].charAt(64)-'0') 
                        		&& znak[1]==(int)(trainingData[i].charAt(65)-'0') 
                        		&& znak[2]==(int)(trainingData[i].charAt(66)-'0')){
                        	pktDlaSieci++;
                        }
                } 
                double skutecznoscSieci = (double)pktDlaSieci/100.0 * 100;
                labelSkutecznoscSieci.setText("Skuteczność sieci: "+String.format("%.2f",skutecznoscSieci)+"%");
        	}
        	
        	
		});
        
        //***
        buttonPanel.add(new JPanel());
                
        //---------------Label
        labelRozpoznanaLiterka = new JLabel("Rozpoznana literka to: ");
        buttonPanel.add(labelRozpoznanaLiterka);
        
        //***
        buttonPanel.add(new JPanel());
        
        //--------------- Tworzenie radiobuttonów
        ButtonGroup grupa = new ButtonGroup();
        //1
        radioS = new JRadioButton("S");
        buttonPanel.add(radioS);
        grupa.add(radioS);
        //2
        radio5 = new JRadioButton("5");
        buttonPanel.add(radio5);
        grupa.add(radio5);
        //3
        radioZ = new JRadioButton("Z");
        buttonPanel.add(radioZ);
        grupa.add(radioZ);
        //4
        radioBrak = new JRadioButton("Inna literka");
        buttonPanel.add(radioBrak);
        grupa.add(radioBrak);
        
        //---------------Label
        labelSkutecznoscSieci = new JLabel("");
        buttonPanel.add(labelSkutecznoscSieci);

        //---------------Label
        labelLiczbaEpok = new JLabel("");
        buttonPanel.add(labelLiczbaEpok);
        
        //---------------Label
        labelIloscCU = new JLabel("");
        buttonPanel.add(labelIloscCU);
        
        add(buttonPanel);       
        setVisible(true);
    }
    
    //Inicjalizacja sieci
    private void initializeNetwork(int[] Warstwy) {
    	siec = new Siec(Warstwy[0], Warstwy.length, Warstwy);		
	}

    //***************MAIN****************
	public static void main(String[] args) {
        new _sciaga();
    }

	private String[] WczytajPlik(String NazwaPliku) {
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

	
	//*******************************
	private void WczytajPlik(ActionEvent e) {
		try {
			String sciezka = "CiagUczacy.txt";
			File file = new File(sciezka);
			if (file.exists())  {		
	        	CiagLiterek = new ArrayList<boolean[]>();
	        	CiagMacierzy = new ArrayList<boolean[]>();      
	            FileInputStream fstream = new FileInputStream(sciezka);
	            DataInputStream dataInput = new DataInputStream(fstream);
	            BufferedReader br = new BufferedReader(new InputStreamReader(dataInput));            
	            String liniaPliku;	            
	            LiterekWZbiorze = Integer.parseInt(br.readLine()); //3
	            LPikseli = Integer.parseInt(br.readLine()); //64	            
	            while((liniaPliku = br.readLine()) != null){
				    String[] tokens = liniaPliku.split(" ");				    
				    if(CiagLiterek.size() == CiagMacierzy.size()){
				    	boolean[] letter = new boolean[LiterekWZbiorze];				    	
				    	for (int i = 0; i < LiterekWZbiorze; i++) {
							letter[i] = tokens[i].equals("0") ? false : true;
						}				    	
				    	CiagLiterek.add(letter);
				    }
				    else {
				    	boolean[] macierz = new boolean[LPikseli];
				    	for (int i = 0; i < LPikseli; i++) {
							macierz[i] = tokens[i].equals("0") ? false : true;
						}				    	
				    	CiagMacierzy.add(macierz);
				    }
	            }
	            br.close();
	            labelRozpoznanaLiterka.setText("Sieć odebrała dane!");
	        }
			else {
				labelRozpoznanaLiterka.setText("Nie udało się załadować CU");
			}			
		}catch(Exception ex) {}	
		
		IleNeuronowWarstwa = new int[LiczbaWarstw];	
		IleNeuronowWarstwa[0] = LPikseli;
		IleNeuronowWarstwa[1] = WarstwaUkryta;
		IleNeuronowWarstwa[2] = LiterekWZbiorze;

		initializeNetwork(IleNeuronowWarstwa);

		ArrayList<double[]> ListaLiterekDouble = new ArrayList<double[]>();
		ArrayList<double[]> ListaMacierzyDouble = new ArrayList<double[]>();
			
		for(int i = 0; i< CiagLiterek.size(); i++) {
			boolean[] LiterkiBoolean = CiagLiterek.get(i);
			boolean[] MacierzBoolean = CiagMacierzy.get(i);

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
		
		siec.UczSieZCiagu(ListaMacierzyDouble, ListaLiterekDouble);		
	}
	
	//*******************************
	private void ZapiszCU(ActionEvent e) {
		try {
			if(CiagLiterek == null || CiagMacierzy == null) return;
			
			final JFileChooser fc = new JFileChooser();
	        int potwierdzenieOkna = fc.showSaveDialog(_sciaga.this);
	        
	        if (potwierdzenieOkna == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String sciezka = file.getAbsolutePath();
	            PrintWriter writer = new PrintWriter(sciezka, "UTF-8");
	              
	            writer.println(CiagLiterek.get(0).length); 	            
	            writer.println(CiagMacierzy.get(0).length); 
	            
	            for (int i = 0; i < CiagLiterek.size(); i++) {
		            boolean[] ObecnaLitera = CiagLiterek.get(i);
		            boolean[] ObecnaMacierz = CiagMacierzy.get(i);	            
		            StringBuilder sb = new StringBuilder();
		            
		            for (boolean znak : ObecnaLitera) {
						sb.append((znak ? "1" : "0") + " ");
					}
		            writer.println(sb);
		            
		            sb = new StringBuilder();
		            for (boolean znak : ObecnaMacierz) {
						sb.append((znak ? "1" : "0") + " ");
					}
		            writer.println(sb);
				}	            
	            writer.close();
	        }
		}
		catch(Exception ex) {}		
	}
	
	
	//*******************************
	private double[] TabBolNaDouble(boolean[] source) {
		double[] result = new double[source.length];
		for (int i = 0; i < source.length; i++) {
			result[i] = source[i] ? 1 : 0;
		}		
		return result;
	}	
	
	//*******************************
	private void ZapiszCT(ActionEvent e) {
		try {
			if(CiagLiterek == null || CiagMacierzy == null) return;
			
			final JFileChooser fc = new JFileChooser();
	        int potwierdzenieOkna = fc.showSaveDialog(_sciaga.this);
	        
	        if (potwierdzenieOkna == JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            String sciezka = file.getAbsolutePath();
	            PrintWriter writer = new PrintWriter(sciezka, "UTF-8");
	            
	            for (int i = 0; i < CiagLiterek.size(); i++) {
		            boolean[] ObecnaLitera = CiagLiterek.get(i);
		            boolean[] ObecnaMacierz = CiagMacierzy.get(i);	            
		            StringBuilder sb = new StringBuilder();	            
		            
		            //Macierz pikseli
		            sb = new StringBuilder();
		            for (boolean znak : ObecnaMacierz) {
						sb.append((znak ? "1" : "0"));
					}
		            
		            //Znak
		            for (boolean znak : ObecnaLitera) {
						sb.append((znak ? "1" : "0"));
					}
		            writer.println(sb);
				}	            
	            writer.close();
	        }
		}
		catch(Exception ex) {}		
	}
	
	//*******************************
	private void SprJakaLitera(double[] source) {
		int outCount = 0;
		int outIndex = -1;
		
		for (int i = 0; i < source.length; i++) {
			if(source[i] > 0.5) {
				outCount++;
				outIndex = i;
			}
		}
		
		if(outCount != 1)
			outIndex = -1;		
		
		if(outIndex == 0) {
		    labelRozpoznanaLiterka.setText("Rozpoznana literka: S");
		} else if(outIndex == 1) {
		    labelRozpoznanaLiterka.setText("Rozpoznana literka: 5");
		} else if(outIndex == 2) {
		    labelRozpoznanaLiterka.setText("Rozpoznana literka: Z");
		} else {
		    labelRozpoznanaLiterka.setText("Nie rozpoznano litery");
		}	
	}
	
}