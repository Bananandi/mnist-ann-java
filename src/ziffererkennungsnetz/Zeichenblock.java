package ziffererkennungsnetz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Zeichenblock extends JPanel {
	BufferedImage img;
	private JLabel lab;
	private int scale;

	int breite, hoehe;
	int[][] eingabe;
	final int schraeg = 150, gerade = 75;
		
	 // Malt den neuen Pixel auch den Zeichenblock.
	private void paintPixel(int x, int y, int wert) {
		for (int i = x * scale; i < (x + 1) * scale; i++) {
			for (int j = y * scale; j < (y + 1) * scale; j++) {
				Color col = new Color(wert, wert, wert);
				img.setRGB(i, j, col.getRGB());
			}
		}
	}

	// trägt den gemalten Pixel in den Speicher ein
	private void setPixel(int x, int y, int wert) {
		eingabe[x][y] = 0;
		paintPixel(x, y, wert);
	}

	Zeichenblock(int b, int h, int s) {
		super(true);
		scale = s;
		breite = b;
		hoehe = h;
		
		eingabe = new int[b][h];
		for (int i = 0; i < b; i++) {
			for (int j = 0; j < h; j++) {
				eingabe[i][j] = 255;
			}
		}

		this.setLayout(new FlowLayout());
		this.setPreferredSize(new Dimension(b * scale, h * scale));

		img = new BufferedImage(b * scale, h * scale, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(new Color(255, 255, 255));
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());

		lab = new JLabel();
		lab.setIcon(new ImageIcon(img));
		this.add(lab);

		MouseAdapter mouseAdapter = new MouseAdapter() {

			public void mouseDragged(MouseEvent e) {
				Point p = e.getPoint();
				int x = p.x - (getWidth()-scale*breite)/2;
				int y = p.y;
				if (x >= 0 && y >= 0 && x < img.getWidth() && y < img.getHeight()) {
					setPixel(x / scale, y / scale, 0);
				}
				lab.repaint();
			}
			
            public void mouseReleased(MouseEvent e) {
            	weichzeichner();
                repaint();
            }
            
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}
	
	void rueckwerts(double[] input) {
		for(int i=0; i<breite; i++) {
			for(int j=0; j<hoehe; j++) {
				eingabe[i][j] = -(int)(input[i+28*j]*255)+255;
				paintPixel(i, j, -(int)(input[i+28*j]*255)+255);
			}
		}
		repaint();
	}
	
	void malweich(int x, int y, int wert) {
		if (eingabe[x][y] > wert) {
			eingabe[x][y] = wert;
			paintPixel(x, y, wert);
		}
	}
	
	void weichzeichner() {
		for (int i = 0; i < breite; i++) {
			for (int j = 0; j < hoehe; j++) {
				if(eingabe[i][j] == 0) {
					if(i>0) {
						malweich(i-1, j, gerade);
					}
					if(i<breite-1){
						malweich(i+1, j, gerade);
					}
					if(j>0) {
						malweich(i, j-1, gerade);
					}
					if(j<hoehe-1) {
						malweich(i, j+1, gerade);
					}
					if(i>0 && j>0) {
						malweich(i-1, j-1, schraeg);
					}
					if(i>0 && j<hoehe-1) {
						malweich(i-1, j+1, schraeg);
					}
					if(i<breite-1 && j>0) {
						malweich(i+1, j-1, schraeg);
					}
					if(i<breite-1 && j<hoehe-1) {
						malweich(i+1, j+1, schraeg);
					}
				}
			}
		}
	}
	
	double[] ausgeben(double[] input) {
		for(int i=0; i<hoehe; i++) {
			for(int j=0; j<breite; j++) {
				input[i*28+j] = -(double)eingabe[j][i]+255;
			}
		}
		return input;
	}

	void clear() {
		for (int i = 0; i < breite; i++) {
			for (int j = 0; j < hoehe; j++) {
				eingabe[i][j] = 255;
			}
		}
		Graphics2D graphics = img.createGraphics();
		graphics.setPaint(new Color(255, 255, 255));
		graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
		lab.repaint();
	}
}