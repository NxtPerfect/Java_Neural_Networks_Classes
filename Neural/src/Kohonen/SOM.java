package Kohonen;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

public class SOM {
	Vec2D[][] neurony;
	double eta, epsEta;
	double s, epsS;
	double S, ETA;

	public SOM() {
		neurony = null;
		eta = 0.0;
	}

	public SOM(int w, int h, double aeta, double aepsEta, double aepsS) {
		ETA = eta = aeta;
		epsEta = aepsEta;
		S = s = Math.sqrt(w * h);
//		S = s = (w + h) / 2;
		epsS = aepsS;
		neurony = new Vec2D[h][];
		for (int i = 0; i < h; i++)
			neurony[i] = new Vec2D[w];
		Random r = new Random();
		for (int i = 0; i < neurony.length; i++)
			for (int j = 0; j < neurony[i].length; j++) {
				double a = 0.01 * (r.nextDouble() - 0.5) / 0.5;
				double b = 0.01 * (r.nextDouble() - 0.5) / 0.5;
				neurony[i][j] = new Vec2D(a, b);
			}
	}

	public void draw(Graphics g, int x0, int y0, int w, int h) {
		for (int i = 0; i < neurony.length; i++)
			for (int j = 0; j < neurony[i].length; j++) {
				int x = w2x(x0, w, neurony[i][j]);
				int y = w2y(y0, h, neurony[i][j]);
				int x1, y1;
				g.setColor(Color.DARK_GRAY);
				if (i + 1 < neurony.length) {
					x1 = w2x(x0, w, neurony[i + 1][j]);
					y1 = w2y(y0, h, neurony[i + 1][j]);
					g.drawLine(x, y, x1, y1);
				}
				if (j + 1 < neurony[i].length) {
					x1 = w2x(x0, w, neurony[i][j + 1]);
					y1 = w2y(y0, h, neurony[i][j + 1]);
					g.drawLine(x, y, x1, y1);
				}
				g.setColor(Color.BLACK);
				g.fillOval(x - 3, y - 3, 6, 6);
			}
	}

	public int w2x(int a, int b, Vec2D v) {
		return a + (int) Math.round(b * (v.x + 1.0) / 2.0);
	}

	public int w2y(int a, int b, Vec2D v) {
		return a + (int) Math.round(b * (v.y + 1.0) / 2.0);
	}

	public void ucz(Vec2D wejscia) {
		int idxW = 0, idxK = 0;
		double minDist = dist2(neurony[idxW][idxK], wejscia);
		for (int i = 0; i < neurony.length; i++) {
			for (int j = 0; j < neurony[i].length; j++) {
				double dist = dist2(neurony[i][j], wejscia);
				if (dist >= minDist) {
					continue;
				}
				idxW = i;
				idxK = j;
				minDist = dist;
			}
		}
		double d = 0.0;

		int SS = (int) s;
		for (int i = idxW - SS; i <= idxW + SS; i++) {
			if (i < 0 || i >= neurony.length)
				continue;
			for (int j = idxK - SS; j <= idxK + SS; j++) {
				if (j < 0 || j >= neurony[i].length) {
					continue;
				}
				d = Math.sqrt(Math.pow(idxW - i, 2.0) + Math.pow(idxK - j, 2.0));
				if (d >= s) {
					continue;
				}
				neurony[i][j].add(Vec2D.sub(wejscia, neurony[i][j]).mul(eta).mul(fS(d)));
			}
		}
		eta *= epsEta;
		s *= epsS;
	}

	public void uczWTA(Vec2D wejscia) {
		Vec2D winner = neurony[0][0];

		for (int i = 0; i < neurony.length; i++) {
			for (int j = 0; j < neurony[i].length; j++) {
				double dist = dist2(neurony[i][j], wejscia);
				if (dist >= dist2(winner, wejscia)) {
					continue;
				}
				winner = neurony[i][j];
			}
		}

		winner.add(Vec2D.sub(wejscia, winner).mul(eta));

		eta *= epsEta;
	}

	public double fS(double d) {
		return 1.0 - d / s;
	}

	public double dist2(Vec2D a, Vec2D b) {
		return Math.pow(a.x - b.x, 2.0) + Math.pow(a.y - b.y, 2.0);
	}

    public void resetLearningRate(double resetFactor) {
        eta = ETA * resetFactor;
        s = S * resetFactor;
    }
}
