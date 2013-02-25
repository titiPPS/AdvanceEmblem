package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpearAgent extends Agent {
	private final static int SPEEDSPEAR = 5;
	private final static int POINTSDEVIE = 10;
	private final static int ATQ = 7;
	private final static int DEF = 1;
	private static final String FileImageUsed = "GreySpearAgent.png";
	private static final String FileImageRed = "RedSpearAgent.png";
	private static final String FileImageBlue = "BlueSpearAgent.png";
	private static final String FileImageCtrlUsed = "GreySpearAgent_100.png";
	private static final String FileImageCtrlRed = "RedSpearAgent_100.png";
	private static final String FileImageCtrlBlue = "BlueSpearAgent_100.png";
	
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	public SpearAgent(int x, int y,Player p) {
		super(x, y,SPEEDSPEAR,p,POINTSDEVIE,ATQ,DEF);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public BufferedImage getImage() {
		if(super._used) {
			return imageUsed;
		}else {
			if (super._joueur.isRed()) {
				return imageRed;
			}else {
				return imageBlue;
			}
		}
	}

	@Override
	public BufferedImage getImageCtrl() {
		if(super._used) {
			return imageCtrlUsed;
		}else {
			if (super._joueur.isRed()) {
				return imageCtrlRed;
			}else {
				return imageCtrlBlue;
			}
		}
	}
	
	@Override
	public String getName() {
		return "Lancier";
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
	

	@Override
	public boolean estFortContre(SwordAgent a) {
		return true;
	}

	@Override
	public boolean estFaibleContre(AxeAgent a) {
		return true;
	}
}