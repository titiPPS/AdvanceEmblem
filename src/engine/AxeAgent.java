package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AxeAgent extends Agent {
	public  final static int ID = 2;
	private final static int SPEEDAXE = 6;
	private final static int POINTSDEVIE = 12;
	private final static int ATQ = 7;
	private final static int DEF = 1;
	private static final String FileImageUsed = "GreyAxeAgent.png";
	private static final String FileImageRed = "RedAxeAgent.png";
	private static final String FileImageBlue = "BlueAxeAgent.png";
	private static final String FileImageCtrlUsed = "GreyAxeAgent_100.png";
	private static final String FileImageCtrlRed = "RedAxeAgent_100.png";
	private static final String FileImageCtrlBlue = "BlueAxeAgent_100.png";
	private static BufferedImage imageUsed;
	private static BufferedImage imageRed;
	private static BufferedImage imageBlue;
	private static BufferedImage imageCtrlUsed;
	private static BufferedImage imageCtrlRed;
	private static BufferedImage imageCtrlBlue;
	
	private static AxeAgent instanceTest = new AxeAgent(-1, -1, null);
	private static int[] faiblesses = {4}, forces = {3};
	
	public AxeAgent(int x, int y,Player p) {
		super(x, y,SPEEDAXE,p,POINTSDEVIE,ATQ,DEF);
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
		return "Berserk";
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
	
	
	public static AxeAgent getInstance() {
		return instanceTest;
	}


	@Override
	public int getIDClass() {
		return ID;
	}


	@Override
	public int[] getFaiblesses() {
		return faiblesses.clone();
	}


	@Override
	public int[] getForces() {
	return forces.clone();
	}

}
