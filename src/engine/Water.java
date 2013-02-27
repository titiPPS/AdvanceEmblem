package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Water extends Terrain {
	private static final int MVTCOST = Integer.MAX_VALUE;
	private static final boolean ISACCESSIBLE = false;
	private static final int COEFFDEF = 1;
	public static final int ID_TERRAIN = 4;
	private static final String FileImage = "water.png";
	private static final String FileImageCtrl = "water_100.png";
	
	private static BufferedImage image;
	private static BufferedImage imageCtrl;
	
	public Water(int x, int y) {
		super(x,y,MVTCOST,ISACCESSIBLE,COEFFDEF);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String printTerrain() {
		return "~";
	}

	@Override
	public BufferedImage getImage() {
		return image;
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
		return image;
	}

	@Override
	public String getName() {
		return "Eau";
	}

	public static void initImage(String directory) {
		try {
			image = ImageIO.read(new File(directory + File.separator + FileImage));
			imageCtrl = ImageIO.read(new File(directory + File.separator + FileImageCtrl));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
