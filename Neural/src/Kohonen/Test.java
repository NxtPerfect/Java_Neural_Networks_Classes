package Kohonen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Test extends JFrame {
	private JPanel gridLewo, gridPrawo;
	private Timer timer;
	private MyComponent komponent;
	private BufferedImage obraz1, obraz2;
	private JRadioButton radioWTA, radioWTM;
	private SOM som;
	private String[] figures = new String[] {"Koło", "Kwadrat", "Trójkąt"};
	private JComboBox<String> comboPierwszy = new JComboBox<String>(figures);
	private JComboBox<String> comboDrugi = new JComboBox<String>(figures);
	private final int WIERSZE = 10, KOLUMNY = 10;
	private final double AETA = 0.1, AEPSETA = 0.999, AEPSS = 0.9999;
	private final String sciezkaKolo = "src/Kohonen/kolo.png", sciezkaTrojkat = "src/Kohonen/trojkat.png", sciezkaKwadrat = "src/Kohonen/kwadrat.png";
	private final int WYMIARY_OBRAZKA = 250;
	
	private class MyComponent extends JComponent {
		@Override
		protected void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			som.draw(g, 0, 0, w, h);
			Random rand = new Random();
			int a = rand.nextInt(WYMIARY_OBRAZKA);
			int b = rand.nextInt(WYMIARY_OBRAZKA);
			int ktory = rand.nextInt(2);
			BufferedImage obrazek = obraz1;
			// Jeśli wybrany obrazek to prawy
			if (ktory == 1) {
				obrazek = obraz2;
			}
			// Obrazek jest null
			// Trzeba jakoś go wczytać jak wczytamy obraz1/obraz2
			System.out.println(obrazek.getRGB(a, b));
			if (obrazek.getRGB(a, b) != Color.BLACK.getRGB()) {
				super.paintComponent(g);
				return;
			}
			// Oblicz wektor wejścia
			double x = (-1 * (w / 2.0 - a * 2) * 2 / w - 0.5) * 2 / 3;
			double y = (-1 * (h / 2.0 - b * 2) * 2 / h) * 2 / 3;
			Vec2D wejscia = new Vec2D(x, y);
			// WTA
			if (radioWTA.isSelected()) {
				som.ucz2(wejscia);
				super.paintComponent(g);
				return;
			}
			// WTM
			som.ucz(wejscia);
			super.paintComponent(g);
		}
	}

	public Test() throws HeadlessException {
		super("Test SOM");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(400, 400);
		setVisible(true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 4, d.height / 4, d.width / 2, d.height / 2);
		setLayout(new GridLayout(1,2));
		gridLewo = new JPanel(new GridLayout(1, 3, 2, 2));
		gridPrawo = new JPanel(new GridLayout(3, 1, 2, 2));
		add(gridLewo);
		add(gridPrawo);
		
		JLabel img1 = new JLabel();
		img1.setBackground(Color.gray);
		JLabel img2 = new JLabel();
		img2.setBackground(Color.gray);

		som = new SOM(WIERSZE, KOLUMNY, AETA, AEPSETA, AEPSS);
		timer = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				komponent.repaint();
			}
		});
		
		// Inicjalizacja obrazków i ustawienie obrazków combo boxem
		ustawObraz(obraz1, sciezkaKolo, img1);
		comboPierwszy.setSelectedIndex(0);
		ustawObraz(obraz2, sciezkaKwadrat, img2);
		comboDrugi.setSelectedIndex(1);
		
		comboPierwszy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String wybranyKsztalt = comboPierwszy.getSelectedItem().toString();
				switch (wybranyKsztalt) {
				case "Koło": {
					ustawObraz(obraz1, sciezkaKolo, img1);
					break;
				}
				case "Trójkąt": {
					ustawObraz(obraz1, sciezkaTrojkat, img1);
					break;
				}
				case "Kwadrat": {
					ustawObraz(obraz1, sciezkaKwadrat, img1);
					break;
				}
				default:
					ustawObraz(obraz1, sciezkaKolo, img1);
				}
			}
		});
		
		comboDrugi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String wybranyKsztalt = comboDrugi.getSelectedItem().toString();
				switch (wybranyKsztalt) {
				case "Koło": {
					ustawObraz(obraz1, sciezkaKolo, img2);
					break;
				}
				case "Trójkąt": {
					ustawObraz(obraz1, sciezkaTrojkat, img2);
					break;
				}
				case "Kwadrat": {
					ustawObraz(obraz1, sciezkaKwadrat, img2);
					break;
				}
				default:
					ustawObraz(obraz1, sciezkaKolo, img2);
				}
			}
		});

		radioWTA = new JRadioButton("WTA");
		radioWTA.setSelected(true);
		radioWTM = new JRadioButton("WTM");

		MyComponent komponent = new MyComponent();
		gridLewo.add(img1);
		gridLewo.add(komponent);
		gridLewo.add(img2);

		gridPrawo.add(comboPierwszy);
		gridPrawo.add(comboDrugi);
		JButton trenuj = new JButton("Trenuj");
		trenuj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Trenujemy");
				komponent.repaint();
			}
		});
		JPanel p = new JPanel(new GridLayout(1, 3, 2, 2));
		p.add(trenuj);
		ButtonGroup box = new ButtonGroup();
		box.add(radioWTA);
		p.add(radioWTA);
		box.add(radioWTM);
		p.add(radioWTM);
		
		gridPrawo.add(p);
	}
	
	private void ustawObraz(BufferedImage obraz, String sciezka, JLabel img) {
		try {
			obraz = ImageIO.read(new File(sciezka));
		} catch (Exception e) {
			System.err.println("Obraz nie został wczytany!");
			System.err.println(e);
		}
		ImageIcon icon = new ImageIcon(obraz);
		img.setIcon(icon);
		img.setHorizontalAlignment(SwingConstants.CENTER);
		img.setVerticalAlignment(SwingConstants.CENTER);
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Test();
			}
		});
	}

}
