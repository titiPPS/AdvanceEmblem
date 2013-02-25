package ui;

import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;

import engine.Agent;
import engine.Player;
import engine.Terrain;
import game.GameEngine;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import javax.swing.JPopupMenu;
import java.awt.Component;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JInternalFrame;

public class GUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int xMenu = 350;
	private static final int yMenu = 150;

	public static enum ActionClavier {
		up, down, left, right, ok, cancel,nothing;
	}
	
	public static enum EventUsine {
		horse, sword, spear, axe, gold, nothing;
	}
	
	public static enum EventAgent {
		attendre, attaquer, nothing;
	}
	
	private static String dirImage = "pic";
	private static String fileSablier = "Sablier.png";
	private static String fileBaniere = "AdvanceEmblem.png";
	private static String fileFooter= "Footer.png";

	
	private static BufferedImage imageSablier;
	private static BufferedImage imageBaniere;
	private static BufferedImage imageFooter;
	
	private GameEngine gEngine = null;
	private JPanel contentPane;
	private JLabel lblImage,lblImage100px,lblRessources,lblCroissance,lblJoueur,lblBaniereJoueur;
	private JLabel lblTypeInfo, lblInfo1, lblInfo2;
	private JPopupMenu jpmAgent, jpmUsine;
	private JMenuItem itemWait, itemATQ;
	private JMenuItem itemSword, itemSpear,itemAxe, itemHorse, itemGold;
	private JProgressBar barrePV;
	
	/**
	 * Create the frame.
	 */
	public GUI() {
		gEngine = new GameEngine(this);
		jpmAgent = new JPopupMenu();
		itemWait = new JMenuItem("Attendre");
		itemWait.addActionListener(new ListenerMenu(EventAgent.attendre,gEngine));
		jpmAgent.add(itemWait);
		
	
		
		itemATQ = new JMenuItem("Attaquer");
		itemATQ.addActionListener(new ListenerMenu(EventAgent.attaquer,gEngine));
		jpmAgent.add(itemATQ);
		
		jpmUsine = new JPopupMenu();
		itemHorse = new JMenuItem("Cavalier (" + GameEngine.COUT_SUPER+")");
		itemHorse.addActionListener(new ListenerMenu(EventUsine.horse,gEngine));
		jpmUsine.add(itemHorse);
		itemSword = new JMenuItem("Epéiste");
		itemSword.setText(itemSword.getText() +" (" + GameEngine.COUT_BASE+")");
		itemSword.addActionListener(new ListenerMenu(EventUsine.sword, gEngine));
		jpmUsine.add(itemSword);
		itemSpear = new JMenuItem("Lancier");
		itemSpear.setText(itemSpear.getText() +" (" + GameEngine.COUT_BASE+")");
		itemSpear.addActionListener(new ListenerMenu(EventUsine.spear, gEngine));
		jpmUsine.add(itemSpear);
		itemAxe = new JMenuItem("Berserk");
		itemAxe.setText(itemAxe.getText() +" (" + GameEngine.COUT_BASE+")");
		itemAxe.addActionListener(new ListenerMenu(EventUsine.axe, gEngine));
		jpmUsine.add(itemAxe);
		itemGold = new JMenuItem("Augmenter Ressources");
		itemGold.setText(itemGold.getText() +": " + GameEngine.AUGMENTATION_CROISSANCE +" / tour (" + GameEngine.COUT_AUGMENTATION_RESSOURCES+")");
		itemGold.addActionListener(new ListenerMenu(EventUsine.gold, gEngine));
		jpmUsine.add(itemGold);
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				ActionClavier ac = ActionClavier.nothing;
				if(arg0.getKeyCode() == KeyEvent.VK_UP || arg0.getKeyCode() == KeyEvent.VK_Z) {
					ac = ActionClavier.up;
				}else if(arg0.getKeyCode() == KeyEvent.VK_LEFT || arg0.getKeyCode() == KeyEvent.VK_Q) {
					ac = ActionClavier.left;
				}else if(arg0.getKeyCode() == KeyEvent.VK_RIGHT || arg0.getKeyCode() == KeyEvent.VK_D) {
					ac = ActionClavier.right;
				}else if(arg0.getKeyCode() == KeyEvent.VK_DOWN || arg0.getKeyCode() == KeyEvent.VK_S) {
					ac = ActionClavier.down;
				}else if(arg0.getKeyCode() == KeyEvent.VK_ENTER || arg0.getKeyCode() == KeyEvent.VK_L) {
					ac = ActionClavier.ok;
				}else if(arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE || arg0.getKeyCode() == KeyEvent.VK_M) {
					ac = ActionClavier.cancel;
				}
				gEngine.traiterEvent(ac);
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				keyTyped(arg0);
			}
		});
		setResizable(false);
		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem miUnits = new JMenuItem("Unités");
		mnHelp.add(miUnits);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panGame = new JPanel();
		panGame.setAlignmentY(Component.TOP_ALIGNMENT);
		panGame.setAlignmentX(Component.LEFT_ALIGNMENT);
		panGame.setBounds(0, 0, 800, 433);
		panGame.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panGame);
		panGame.setLayout(null);
		
		JLabel labelImage = new JLabel("");
		labelImage.setAlignmentY(Component.TOP_ALIGNMENT);
		labelImage.setBounds(0, 0, 800, 400);
		lblImage = labelImage;
		panGame.add(labelImage);
		ImageIcon iconBaniere = new ImageIcon(imageBaniere);
		
		JLabel labelBaniere = new JLabel("");
		labelBaniere.setBounds(0, 401, 800, 30);
		labelBaniere.setIcon(iconBaniere);
		panGame.add(labelBaniere);
		
		JPanel panCommande = new JPanel();
		panCommande.setBounds(0, 433, 800, 123);
		panCommande.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panCommande);
		panCommande.setLayout(null);
		
		JPanel panImageCommande = new JPanel();
		panImageCommande.setBackground(Color.LIGHT_GRAY);
		panImageCommande.setAlignmentY(Component.TOP_ALIGNMENT);
		panImageCommande.setAlignmentX(Component.LEFT_ALIGNMENT);
		panImageCommande.setMinimumSize(new Dimension(100, 100));
		panImageCommande.setMaximumSize(new Dimension(100, 100));
		panImageCommande.setPreferredSize(new Dimension(100, 100));
		panImageCommande.setBounds(5, 2, 100, 100);
		panCommande.add(panImageCommande);
		panImageCommande.setLayout(null);
		
		JLabel labelImage100px = new JLabel("");
		labelImage100px.setBounds(new Rectangle(0, 0, 100, 100));
		labelImage100px.setMinimumSize(new Dimension(100, 100));
		labelImage100px.setMaximumSize(new Dimension(100, 100));
		labelImage100px.setPreferredSize(new Dimension(100, 100));
		labelImage100px.setAlignmentY(Component.TOP_ALIGNMENT);
		panImageCommande.add(labelImage100px);
		lblImage100px = labelImage100px;
		
		JButton btnFinDeTour = new JButton("");
		btnFinDeTour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gEngine.finDeTour();
			}
		});
		btnFinDeTour.setFocusable(false);
		btnFinDeTour.setPreferredSize(new Dimension(64, 87));
		btnFinDeTour.setMinimumSize(new Dimension(64, 87));
		btnFinDeTour.setMaximumSize(new Dimension(64, 87));
		btnFinDeTour.setAlignmentY(Component.TOP_ALIGNMENT);
		btnFinDeTour.setSize(new Dimension(64, 87));
		ImageIcon iconFindeTour = new ImageIcon(imageSablier);
		btnFinDeTour.setBounds(730, 10, 70, 90);
		btnFinDeTour.setIcon(iconFindeTour);
		panCommande.add(btnFinDeTour);
		
		JPanel panDetail = new JPanel();
		panDetail.setBackground(Color.LIGHT_GRAY);
		panDetail.setBounds(105, 2, 100, 100);
		panCommande.add(panDetail);
		panDetail.setLayout(null);
		
		JLabel labelTypeInfo = new JLabel("");
		labelTypeInfo.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 14));
		lblTypeInfo = labelTypeInfo;
		labelTypeInfo.setHorizontalAlignment(SwingConstants.CENTER);
		labelTypeInfo.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTypeInfo.setBounds(6, 6, 88, 15);
		panDetail.add(labelTypeInfo);
		
		JProgressBar progressBar = new JProgressBar();
		barrePV = progressBar;
		barrePV.setVisible(false);
		progressBar.setBounds(6, 75, 88, 19);
		panDetail.add(progressBar);
		
		JLabel labelInfo1 = new JLabel("");
		labelInfo1.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 14));
		labelInfo1.setHorizontalAlignment(SwingConstants.CENTER);
		lblInfo1 = labelInfo1;
		labelInfo1.setBounds(6, 30, 88, 15);
		panDetail.add(labelInfo1);
		
		JLabel labelInfo2 = new JLabel("");
		labelInfo2.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 13));
		labelInfo2.setHorizontalAlignment(SwingConstants.CENTER);
		lblInfo2 = labelInfo2;
		labelInfo2.setBounds(6, 55, 88, 15);
		panDetail.add(labelInfo2);
		
		
		JLabel labelRessource = new JLabel("500");
		lblRessources = labelRessource;
		labelRessource.setHorizontalAlignment(SwingConstants.RIGHT);
		labelRessource.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 18));
		labelRessource.setBounds(211, 80, 105, 22);
		panCommande.add(labelRessource);
		
		JLabel lblImgFooter = new JLabel("");
		lblImgFooter.setBounds(0, 105, 800, 14);
		ImageIcon iconFoot = new ImageIcon(imageFooter);
		lblImgFooter.setIcon(iconFoot);
		panCommande.add(lblImgFooter);
		
		JLabel labelCroissance = new JLabel("0");
		lblCroissance = labelCroissance;
		labelCroissance.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCroissance.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 18));
		labelCroissance.setBounds(211, 54, 55, 22);
		panCommande.add(labelCroissance);
		
		JLabel labelTxtCrois = new JLabel("$ / Tour");
		labelTxtCrois.setHorizontalAlignment(SwingConstants.LEFT);
		labelTxtCrois.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 18));
		labelTxtCrois.setBounds(278, 54, 76, 22);
		panCommande.add(labelTxtCrois);
		
		JLabel labelJoueur = new JLabel("");
		labelJoueur.setForeground(new Color(128, 0, 0));
		lblJoueur = labelJoueur;
		labelJoueur.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 20));
		labelJoueur.setBounds(223, 10, 135, 40);
		panCommande.add(labelJoueur);
		
		JLabel labelBaniereJoueur = new JLabel("");
		lblBaniereJoueur = labelBaniereJoueur;
		labelBaniereJoueur.setBounds(359, 2, 360, 100);
		panCommande.add(labelBaniereJoueur);
		
		JLabel label = new JLabel("$");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setFont(new Font("Matura MT Script Capitals", Font.BOLD, 18));
		label.setBounds(328, 80, 18, 22);
		panCommande.add(label);
		
		JMenuItem miLoadMap = new JMenuItem("Charger carte");
		miLoadMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jChooser = new JFileChooser();
				if (jChooser.showOpenDialog(jChooser.getParent()) == JFileChooser.APPROVE_OPTION) {
					
					ImageIcon icon = new ImageIcon(gEngine.initMap(jChooser.getSelectedFile()));
					lblImage.setIcon(icon);
					lblImage.repaint();
				}
			}
		});
		menuFile.add(miLoadMap);
	}
	


	protected void initEngine() {
		gEngine.initImage(dirImage);
	}
	
	public boolean repaintMap(BufferedImage imageMap) {
		ImageIcon icon = new ImageIcon(imageMap);
		lblImage.setIcon(icon);
		lblImage.repaint();
		return true;
	}
	
	/**
	 * Methode permettant d'effectuer un repaint local de manière immédiate
	 * 
	 * @param imageMap ; l'image de la carte
	 * @param x : début du repaint sur l'axe des x
	 * @param y : début du repaint sur l'axe des y
	 * @param w : longueur du rectangle de repaint
	 * @param h : hauteur du rectangle de repaint
	 */
	public void repaintMap(BufferedImage imageMap,int x,int y,int w, int h) {
		ImageIcon icon = new ImageIcon(imageMap);
		lblImage.setIcon(icon);
		lblImage.paintImmediately(x,y,w,h);
	}
	
	/**
	 * Methode permettant de déclancher le repaint de l'image du panneau de commande.
	 * Cette image correspond à la position du curseur et doit faire 100px X 100px
	 * @param image : l'image à afficher
	 */
	/*public void repaintImageCommande(BufferedImage image) {
		ImageIcon icon = new ImageIcon(image);
		lblImage100px.setIcon(icon);
		lblImage100px.repaint();
	}*/
	
	public void repaintPaneauCtrl(Terrain t) {
		ImageIcon icon = new ImageIcon(t.getImageCtrl());
		lblImage100px.setIcon(icon);
		lblImage100px.repaint();
		if(t.getOccupant() != null) {
			Agent a = t.getOccupant();
			barrePV.setVisible(true);
			barrePV.setMaximum(a.getPVMax());
			barrePV.setValue(a.getPV());
			lblTypeInfo.setText(a.getName());
			lblTypeInfo.setForeground(a.getJoueur().getColor());
			lblInfo1.setText("");
			lblInfo2.setText(a.getPV() + "  /  " + a.getPVMax());
		}else {
			lblTypeInfo.setForeground(Color.BLACK);
			barrePV.setVisible(false);
			lblTypeInfo.setText(t.getName());
			if(t.getAccessible()) {
				lblInfo1.setText(t.precisionToString());
				lblInfo2.setText(t.defenseToString());
			}else {
				lblInfo1.setText("Non");
				lblInfo2.setText("Accessible");
			}
			
		}
	}
	
	/**
	 * Methode pour montrer le menu de construction d'unité.
	 * Le menu ne contient que les options de construction disponibles
	 * en fonction des ressources du joueur.
	 * 
	 * Ne concerne que les joueurs humains
	 * @param p : le joueur courant
	 */
	public void showMenuUsine(Player p) {
		jpmUsine = new JPopupMenu();
		if(p.peutConstruireSuperAgent()) {
			jpmUsine.add(itemHorse);
		}
		if(p.peutConstruireBaseAgent()) {
			jpmUsine.add(itemSword);
			jpmUsine.add(itemSpear);
			jpmUsine.add(itemAxe);
		}
		if(p.peutAugmenterRessources()) {
			jpmUsine.add(itemGold);
		}else {
			jpmUsine.add(new JMenuItem("Ressources insufisantes"));
		}
		jpmUsine.show(this, xMenu, yMenu);
	}
	
	/**
	 * Methode pour montrer le menu d'action d'une unité.
	 * Ne peut contenir que "Attendre" et "Attaquer" 
	 * 
	 * Ne concerne que les joueurs humains
	 * @param atq : vaut vrai si un ennemi est adjacent à la destination.
	 */
	public void showMenuAgent(boolean atq) {
		jpmAgent = new JPopupMenu();
		jpmAgent.add(itemWait);
		//Si l'attaque est disponible
		if(atq) {
			jpmAgent.add(itemATQ);
		}
		jpmAgent.show(this, xMenu, yMenu);
	}
	
	public void initJoueur(Player p) {
		updateInfo(p);
		lblBaniereJoueur.setIcon(new ImageIcon(p.getImageBaniere()));
	}
	
	public void updateInfo(Player p) {
		lblRessources.setText(Integer.toString(p.getRessources()));
		lblCroissance.setText(Integer.toString(p.getCroissance()));
		lblJoueur.setText(p.getName());
		lblJoueur.setForeground(p.getColor());
	}
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initImages();
					GUI frame = new GUI();
					frame.initEngine();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Initialisation des images propres à l'application et non à la carte.
	 * Actuellement, ces images sont les images des boutons ainsi que la banière du
	 * jeu.
	 */
	private static void initImages() {
		/*initialisations des images propres à l'interface*/
		try {
			imageSablier = ImageIO.read(new File(dirImage + File.separator + fileSablier));
			imageBaniere = ImageIO.read(new File(dirImage + File.separator + fileBaniere));
			imageFooter = ImageIO.read(new File(dirImage + File.separator + fileFooter));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
