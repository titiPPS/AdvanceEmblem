package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SwordAgent extends Agent{
	private final static int SPEEDSWORD = 5;
	private final static int POINTSDEVIE = 10;
	private final static int ATQ = 7;
	private final static int DEF = 1;
	
	private static final String FileImageUsed = "GreySwordAgent.png";
	private static final String FileImageRed = "RedSwordAgent.png";
	private static final String FileImageBlue = "BlueSwordAgent.png";
	private static final String FileImageCtrlUsed = "GreySwordAgent_100.png";
	private static final String FileImageCtrlRed = "RedSwordAgent_100.png";
	private static final String FileImageCtrlBlue = "BlueSwordAgent_100.png";
	
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	
	public SwordAgent(int x, int y,Player p) {
		super(x, y,SPEEDSWORD,p,POINTSDEVIE,ATQ,DEF);
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
		return "Epéiste";
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
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean estFortContre(AxeAgent a) {
		return true;
	}

	@Override
	public boolean estFaibleContre(SpearAgent a) {
		return true;
	}
	
}
