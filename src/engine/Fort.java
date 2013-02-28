package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Fort extends Terrain {
	private static final int GUERISON = 3;
	private static final int MVTCOST = 2;
	private static final boolean ISACCESSIBLE = true;
	private static final int COEFFDEF = 2;
	public static final int ID_TERRAIN = 5;
	private static final String FileImage = "fort.png";
	private static final String FileImageSelect = "fortSelected.png";
	private static final String FileImageCtrl = "fort_100.png";
	
	private static BufferedImage image;
	private static BufferedImage imageCtrl;
	private static BufferedImage imageSelected;
	
	
	
	
	public Fort(int x, int y) {
		super(x,y,MVTCOST,ISACCESSIBLE,COEFFDEF);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected BufferedImage getBaseImage() {
		return image;
	}

	@Override
	protected BufferedImage getCtrlImage() {
		return imageCtrl;
	}

	@Override
	protected BufferedImage getSelectImage() {
		return imageSelected;
	}

	@Override
	public String getName() {
		return "Fort";
	}

	public static void initImage(String directory) {
		try {
			image = ImageIO.read(new File(directory + File.separator + FileImage));
			imageCtrl = ImageIO.read(new File(directory + File.separator + FileImageCtrl));
			imageSelected = ImageIO.read(new File(directory + File.separator + FileImageSelect));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void effetSurOccupantStart(Player p) {
		if(super._occupant != null && super._occupant.appartientA(p)) {
			super._occupant.setPV(super._occupant.getPV() + GUERISON);
		}
	}

}
