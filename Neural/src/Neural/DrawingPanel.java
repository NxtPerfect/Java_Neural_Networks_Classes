package Neural;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DrawingPanel extends JPanel {

	private int prevX, prevY, currX, currY;
	private BufferedImage obraz;
	private static int WIDTH = 500;
	private static int HEIGHT = 500;

	public DrawingPanel() {
		setPreferredSize(new Dimension(500, 500));
		setBackground(Color.white);
		obraz = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				prevX = currX;
				prevY = currY;
				currX = e.getX();
				currY = e.getY();

				Graphics g = obraz.getGraphics();
				g.setColor(Color.black);
				g.drawLine(prevX, prevY, currX, currY);
				g.dispose();
				repaint();
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				prevX = currX = e.getX();
				prevY = currY = e.getY();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				repaint();
			}
		});
	}

	public void wyczysc() {
		prevX = prevY = currX = currY = -1;
		obraz = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		repaint();
	}

	public boolean[] getObszarLitera(int kolumna, int wiersz) {
		// True jeśli piksel jest pusty
		// False jeśli zamalowany
		boolean[] macierzLitery = new boolean[kolumna * wiersz];
		Rectangle rozmiarLitery = getRozmiarLitery();

		// Metoda iteruje po każdym pikselu w prostokącie
		for (int i = 0; i < rozmiarLitery.width; i++) {
			for (int j = 0; j < rozmiarLitery.height; j++) {
				if (obraz.getRGB(rozmiarLitery.x + i, rozmiarLitery.y + j) == 0) {
					continue;
				}
				int x = getKoordynaty(i, rozmiarLitery.width, kolumna);
				int y = getKoordynaty(j, rozmiarLitery.height, wiersz);

				macierzLitery[x + y * kolumna] = true;
			}
		}
		return macierzLitery;
	}

	// Zwraca granice rysowanego obrazu
	private Rectangle getRozmiarLitery() {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

		// Iterowanie przez piksele obrazu
		for (int i = 0; i < obraz.getWidth(); i++) {
			for (int j = 0; j < obraz.getHeight(); j++) {
				if (obraz.getRGB(i, j) == 0) {
					continue;
				}
				// Lewy górny róg
				minX = Math.min(minX, i);
				minY = Math.min(minY, j);
				// Prawy dolny róg
				maxX = Math.max(maxX, i);
				maxY = Math.max(maxY, j);
			}
		}
		return new Rectangle(minX, minY, maxX - minX, maxY - minY);
	}

	// Oblicza nową skalowaną pozycję
	private int getKoordynaty(int oryginalnaPozycja, int dlugoscOryginalnejOsi, int dlugoscDocelowa) {
		if (dlugoscOryginalnejOsi < dlugoscDocelowa)
			return 0;
		return oryginalnaPozycja * dlugoscDocelowa / dlugoscOryginalnejOsi;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(obraz, 0, 0, this);
	}
}