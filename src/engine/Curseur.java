package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Curseur {
	private static final String FileImageCurseur = "Curseur2.png";
	private static final String FileImageCurseurAtq = "Curseur.png";
	private static BufferedImage image;
	private static BufferedImage imageAtq;
	
	private int x,y;
	private int xMax, yMax;
	private boolean _visible;
	private boolean _atq;
	
	public Curseur() {
		x = 0;
		y = 0;
		xMax = 0;
		yMax = 0;
		_visible = true;
		_atq = false;
	}

	public Curseur(boolean atq) {
		this();
		_atq = atq;
	}

	public Curseur(int xMax, int yMax) {
		this();
		this.xMax = xMax;
		this.yMax = yMax;
	}

	public Curseur(int xMax, int yMax,boolean atq) {
		this(atq);
		this.xMax = xMax;
		this.yMax = yMax;
	}

	/**
	 * Méthode qui met à jour la position du curseur si les paramètres ne font
	 * pas sortir ce dernier de son périmetre.
	 * 
	 * @param vx le déplacement en x
	 * @param vy le déplacement en y
	 */
	public void deplacer(int vx, int vy) {
		if((vx + x < xMax) && (vx + x >= 0)) {
			if((vy + y < yMax) && (vy + y >= 0)) {
				x += vx;
				y += vy;
			}
		}
		
	}

	public BufferedImage getImage(boolean atq) {
		return (atq ? imageAtq : image);
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public int getxMax() {
		return xMax;
	}

	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	public int getyMax() {
		return yMax;
	}

	public void setyMax(int yMax) {
		this.yMax = yMax;
	}

	public boolean isVisible() {
		return _visible;
	}

	public void set_visible(boolean _visible) {
		this._visible = _visible;
	}

	public static void initImage(String directory) {
		try {
			image = ImageIO.read(new File(directory + File.separator + FileImageCurseur));
			imageAtq = ImageIO.read(new File(directory + File.separator + FileImageCurseurAtq));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPosition(int newX, int newY) {
		this.x = (newX > xMax ? xMax : newX);
		this.y = (newY > yMax ? yMax : newY);
	}
}
