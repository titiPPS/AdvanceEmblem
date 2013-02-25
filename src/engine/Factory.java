package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Factory {
	private static final String FileImageUsed = "GreyFactory.png";
	private static final String FileImageRed = "RedFactory.png";
	private static final String FileImageBlue = "BlueFactory.png";
	private static final String FileImageCtrlUsed = "GreyFactory_100.png";
	private static final String FileImageCtrlRed = "RedFactory_100.png";
	private static final String FileImageCtrlBlue = "BlueFactory_100.png";
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	private boolean _used;
	private boolean _isRed;
	private int _x,_y;
	
	
	public Factory(int x, int y,boolean isRed) {
		_x = x;
		_y = y;
		_isRed = isRed;
	}
	
	
	/**
	 * Methode retournant l'objet BufferedImage correspondant
	 * à l'état courant de l'objet (joueur,état)
	 * @return BufferedImage
	 */
	public BufferedImage getImage() {
		if(_used) {
			return imageUsed;
		}else {
			if(_isRed) {
				return imageRed;
			}else {
				return imageBlue;
			}
		}
	}
	
	public BufferedImage getImageCtrl() {
		if(_used) {
			return imageCtrlUsed;
		}else {
			if(_isRed) {
				return imageCtrlRed;
			}else {
				return imageCtrlBlue;
			}
		}
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Factory)) {
			return false;
		}else {
			Factory f = (Factory) o;
			return (f._isRed == _isRed) && (_x == f._x) && (_y == f._y);
		}
	}

	public int getX() {
		return _x;
	}


	public int getY() {
		return _y;
	}


	public boolean isUsed() {
		return _used;
	}

	public void setUsed(boolean b) {
		_used = b;
	}

	public boolean appartientA(Player joueurCourant) {
		return joueurCourant.isRed() == _isRed;
	}


	public static void initImage(String directory) {
		try {
			imageUsed = ImageIO.read(new File(directory + File.separator + FileImageUsed));
			imageRed = ImageIO.read(new File(directory + File.separator + FileImageRed));
			imageBlue = ImageIO.read(new File(directory + File.separator + FileImageBlue));
			imageCtrlUsed = ImageIO.read(new File(directory + File.separator + FileImageCtrlUsed));
			imageCtrlRed = ImageIO.read(new File(directory + File.separator + FileImageCtrlRed));
			imageCtrlBlue = ImageIO.read(new File(directory + File.separator + FileImageCtrlBlue));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
}
