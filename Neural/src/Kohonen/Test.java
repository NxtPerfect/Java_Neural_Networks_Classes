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

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

public class Test extends JFrame {
	private JPanel gridLewo, gridPrawo;
	private Timer timer;
	private MyComponent komponent;
	private BufferedImage obraz1, obraz2;
	private SOM som;
	private String[] figures = new String[] {"Koło", "Kwadrat", "Trójkąt"};
	private JComboBox<String> comboLeft = new JComboBox<String>(figures);
	private JComboBox<String> comboRight = new JComboBox<String>(figures);
	private final int WIERSZE = 10, KOLUMNY = 10;
	private final double AETA = 0.1, AEPSETA = 0.999, AEPSS = 0.9999;
	
	private class MyComponent extends JComponent {
		@Override
		protected void paintComponent(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			som.draw(g, 0, 0, w, h);
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
		MyComponent komponent = new MyComponent();
		gridLewo.add(img1);
		gridLewo.add(komponent);
		gridLewo.add(img2);
		
		try {
			obraz1 = ImageIO.read(new File("src/Kohonen/Kolo.png"));
		} catch (Exception e) {
			System.err.println("Obraz 1 nie został wczytany");
		}
		
		gridPrawo.add(comboLeft);
		gridPrawo.add(comboRight);
		JButton trenuj = new JButton("Trenuj");
		trenuj.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				komponent.repaint();
			}
		});
		JPanel p = new JPanel(new GridLayout(1, 3, 2, 2));
		p.add(trenuj);
		ButtonGroup box = new ButtonGroup();
		JRadioButton radioWTA = new JRadioButton("WTA");
		radioWTA.setSelected(true);
		box.add(radioWTA);
		p.add(radioWTA);
		JRadioButton radioWTM = new JRadioButton("WTM");
		box.add(radioWTM);
		p.add(radioWTM);
		
		gridPrawo.add(p);
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
