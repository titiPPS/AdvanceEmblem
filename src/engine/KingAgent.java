package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class KingAgent extends Agent{
	public  final static int ID = 1;
	private final static int SPEEDKING = 4;
	private final static int POINTSDEVIE = 20;
	private final static int ATQ = 7;
	private final static int DEF = 3;
	private static final String FileImageUsed = "GreyKingAgent.png";
	private static final String FileImageRed = "RedKingAgent.png";
	private static final String FileImageBlue = "BlueKingAgent.png";
	private static final String FileImageCtrlUsed = "GreyKingAgent_100.png";
	private static final String FileImageCtrlRed = "RedKingAgent_100.png";
	private static final String FileImageCtrlBlue = "BlueKingAgent_100.png";
	
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	
	
	public KingAgent(int x, int y,Player p) {
		super(x, y, SPEEDKING,p,POINTSDEVIE,ATQ,DEF);
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
		return "Commandant";
	}
	
	@Override
	public int getPrix() {
		return 1;
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
	public int getIDClass() {
		return ID;
	}

	@Override
	public int[] getFaiblesses() {
		return null;
	}

	@Override
	public int[] getForces() {
		return null;
	}
}
