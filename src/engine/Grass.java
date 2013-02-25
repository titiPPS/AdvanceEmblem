package engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Grass extends Terrain{
	private static final int MVTCOST = 1;
	private static final boolean ISACCESSIBLE = true;
	private static final int COEFFDEF = 0;
	public static final int ID_TERRAIN = 1;
	private static final String FileImage = "grass.png";
	private static final String FileImageSelect = "grassSelected.png";
	private static final String FileImageCtrl = "grass_100.png";
	
	private static BufferedImage image;
	private static BufferedImage imageCtrl;
	private static BufferedImage imageSelected;
	
	
	
	
	public Grass(int x, int y) {
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
		return "Plaine";
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

}
