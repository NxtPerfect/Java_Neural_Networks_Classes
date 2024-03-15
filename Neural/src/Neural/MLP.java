package Neural;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MLP extends JFrame {
	protected volatile JButton clear = new JButton("Clear");

	public MyComponent component;

	public class MyComponent extends JComponent {

		private int lastX = -1;
		private int lastY = -1;
		private int currentX = -1;
		private int currentY = -1;

		public MyComponent() {

			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					drawNew(e.getX(), e.getY());
				}
			});

			addMouseMotionListener(new MouseAdapter() {
				public void mouseDragged(MouseEvent e) {
					drawNew(e.getX(), e.getY());
				}
			});
		}

		private void drawNew(int x, int y) {
			if (lastX == x && lastY == y) {
				return;
			}
			currentX = x;
			currentY = y;
			repaint(lastX, lastY, currentX, currentY);
//			lastX = x;
//			lastY = y;
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			if (lastX > currentX && lastY <= currentY)
				g.drawLine(currentX, lastY, lastX, currentY);
			else if (lastX > currentX && lastY > currentY)
				g.drawLine(currentX, currentY, lastX, lastY);
			else
				g.drawLine(lastX, lastY, currentX, currentY);
			System.out.println("Drawing " + lastX + " " + lastY + " " + currentX + " " + currentY);
			lastX = currentX;
			lastY = currentY;
			super.paintComponent(g);
		}
	}

	public MLP(String name) {
		super(name);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension d = kit.getScreenSize();
		setBounds(d.width / 4, d.height / 4, d.width / 2, d.height / 2);

		JPanel panel = new JPanel(new BorderLayout());
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Aha clear");
			}
		});
		panel.add(clear);
		add(component = new MyComponent());
		add(panel, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
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
