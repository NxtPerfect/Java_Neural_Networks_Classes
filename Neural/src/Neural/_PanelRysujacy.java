package Neural;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class _PanelRysujacy extends JPanel implements MouseListener, MouseMotionListener{
	//Przechowywanie współrzednych pióra na poza obszar
	private int wspolX = -1;
	private int wspolY = -1;
	private BufferedImage obraz;
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 1000;
	
	//Konstruktor
	public _PanelRysujacy() {	
		this.addMouseMotionListener(this);
		this.addMouseListener(this);	
		obraz = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
	}
	
	//Rysowanie obrazu
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(obraz, 0, 0, this);
	}
	
	//Czyści obraz i ustawia mysz na poza obszar rysowania
	public void clear() {
		wspolX = -1;
		wspolY = -1;
		obraz = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		repaint();
	}

	//*****WYNIKOWY
	//Zwraca skalowany obraz rysowany w postaci macierzy boolean w postaci liczby kolumn i wierszy oraz prostokąta określającego granice obrazu
	public boolean[] getObszarLitera(int kolumna, int wiersz){
		boolean[] macierzLitery = new boolean[kolumna*wiersz];
		Rectangle rozmiarLitery = getRozmiarLitery();
		
		//Metoda iteruje po każdym pikselu w prostokącie
		for (int i = 0; i < rozmiarLitery.width; i++) {
			for (int j = 0; j < rozmiarLitery.height; j++) {
				//Jeżeli piksel jest różny od 0
				if(obraz.getRGB(rozmiarLitery.x+i, rozmiarLitery.y+j) != 0) {				
					int x = getKoordynaty(i, rozmiarLitery.width, kolumna);
					int y = getKoordynaty(j, rozmiarLitery.height, wiersz);
					//Macierz zawierająca true dla pikseli, które nie są czarne
					macierzLitery[x + y*kolumna] = true;
				}
			}
		}
		return macierzLitery;
	}
	
	//Rysowanie linii
	@Override
	public void mouseDragged(MouseEvent ev) {
		if(wspolX != -1 && wspolY != -1) {			
			Graphics g = obraz.getGraphics();
			g.setColor(Color.BLACK);
			g.drawLine(wspolX, wspolY, ev.getX(), ev.getY());
			repaint();
		}		
		wspolX = ev.getX();
		wspolY = ev.getY();
	}

	//Resetuje pola po opuszczeniu myszy
	@Override
	public void mouseReleased(MouseEvent arg0) {
		wspolX = -1;
		wspolY = -1;
	}

	//Zwraca granice rysowanego obrazu
	private Rectangle getRozmiarLitery(){
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		//Iterowanie przez piksele obrazu
        for (int i = 0; i < obraz.getWidth(); i++) {
			for (int j = 0; j < obraz.getHeight(); j++) {
				//Czy piksel nie ma wartości 0
				if(obraz.getRGB(i, j) != 0) {
					//Lewy górny róg
					minX = Math.min(minX, i);
					minY = Math.min(minY, j);
					//Prawy dolny róg
					maxX = Math.max(maxX, i);
					maxY = Math.max(maxY, j);
				}
			}
		}		
		return new Rectangle(minX, minY, maxX-minX, maxY-minY);
	}
	
	//Oblicza nową skalowaną pozycję
	private int getKoordynaty(int oryginalnaPozycja, int dlugoscOryginalnejOsi, int dlugoscDocelowa) {
		if(dlugoscOryginalnejOsi < dlugoscDocelowa) return 0;
		//Nowa pozycja
		return oryginalnaPozycja*dlugoscDocelowa/dlugoscOryginalnejOsi;
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {}
	@Override
	public void mouseClicked(MouseEvent arg0) {}
	@Override
	public void mouseEntered(MouseEvent arg0) {}
	@Override
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent arg0) {}
}
