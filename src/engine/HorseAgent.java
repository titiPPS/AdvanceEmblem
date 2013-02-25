package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HorseAgent extends Agent {
	private final static int SPEEDHORSE = 40;
	private final static int POINTSDEVIE = 40;
	private final static int ATQ = 9;
	private final static int DEF = 3;
	private static final String FileImageUsed = "GreyHorseAgent.png";
	private static final String FileImageRed = "RedHorseAgent.png";
	private static final String FileImageBlue = "BlueHorseAgent.png";
	private static final String FileImageCtrlUsed = "GreyHorseAgent_100.png";
	private static final String FileImageCtrlRed = "RedHorseAgent_100.png";
	private static final String FileImageCtrlBlue = "BlueHorseAgent_100.png";
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	public HorseAgent(int x, int y, Player joueur) {
		super(x, y, SPEEDHORSE, joueur,POINTSDEVIE,ATQ,DEF);
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
		return "Cavalier";
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
