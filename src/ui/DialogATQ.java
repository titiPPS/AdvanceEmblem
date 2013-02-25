package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import engine.Agent;
import engine.Terrain;
import java.awt.Font;
import javax.swing.SwingConstants;


public class DialogATQ extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1769456378631850670L;
	
	private final JPanel contentPanel = new JPanel();

	private JButton btnUP,btnDOWN,btnLEFT,btnRIGHT;
	private JLabel lblAgent;
	
	private Agent agent;
	private Terrain tCentral,tUP,tDOWN,tLEFT,tRIGHT;
	private Terrain result;
	private JLabel lblDegatsEnnemi;
	private JLabel lblDegatsAgent;
	private JLabel lblEnnemi;
	private JButton okButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DialogATQ dialog = new DialogATQ();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogATQ() {
		result = null;
		setBounds(100, 100, 344, 160);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		btnUP = new JButton("");
		btnUP.setBounds(30, 5, 24, 24);
		btnUP.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tUP != null) {
					updateTableWithEnnemi(tUP);
					btnLEFT.setBackground(okButton.getBackground());
					btnUP.setBackground(Color.RED);
					btnDOWN.setBackground(okButton.getBackground());
					btnRIGHT.setBackground(okButton.getBackground());
				}
			}
		});
		contentPanel.add(btnUP);
		
		btnLEFT = new JButton("");
		btnLEFT.setBounds(5, 30, 24, 24);
		btnLEFT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tLEFT != null) {
					updateTableWithEnnemi(tLEFT);
					btnLEFT.setBackground(Color.RED);
					btnUP.setBackground(okButton.getBackground());
					btnDOWN.setBackground(okButton.getBackground());
					btnRIGHT.setBackground(okButton.getBackground());
				}
			}
		});
		contentPanel.add(btnLEFT);
		
		btnRIGHT = new JButton("");
		btnRIGHT.setBounds(55, 30, 24, 24);
		btnRIGHT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tRIGHT != null) {
					updateTableWithEnnemi(tRIGHT);
					btnRIGHT.setBackground(Color.RED);
					btnUP.setBackground(okButton.getBackground());
					btnDOWN.setBackground(okButton.getBackground());
					btnLEFT.setBackground(okButton.getBackground());
				}
				
			}
		});
		contentPanel.add(btnRIGHT);
		
		btnDOWN = new JButton("");
		btnDOWN.setBounds(30, 55, 24, 24);
		btnDOWN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(tDOWN != null) {
					updateTableWithEnnemi(tDOWN);
					btnLEFT.setBackground(okButton.getBackground());
					btnUP.setBackground(okButton.getBackground());
					btnDOWN.setBackground(Color.RED);
					btnRIGHT.setBackground(okButton.getBackground());
				}
			}
		});
		contentPanel.add(btnDOWN);
		
		lblAgent = new JLabel("");
		lblAgent.setBounds(32, 32, 20, 20);
		contentPanel.add(lblAgent);
		
		JLabel lblJoueur = new JLabel("Joueur");
		lblJoueur.setHorizontalAlignment(SwingConstants.CENTER);
		lblJoueur.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 16));
		lblJoueur.setBounds(168, 5, 68, 24);
		contentPanel.add(lblJoueur);
		
		lblEnnemi = new JLabel("Ennemi");
		lblEnnemi.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnnemi.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 16));
		lblEnnemi.setBounds(250, 5, 68, 24);
		contentPanel.add(lblEnnemi);
		
		JLabel lblDommages = new JLabel("Dommages");
		lblDommages.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 16));
		lblDommages.setBounds(89, 40, 89, 14);
		contentPanel.add(lblDommages);
		
		lblDegatsAgent = new JLabel("-");
		lblDegatsAgent.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 16));
		lblDegatsAgent.setHorizontalAlignment(SwingConstants.CENTER);
		lblDegatsAgent.setBounds(178, 40, 58, 14);
		contentPanel.add(lblDegatsAgent);
		
		lblDegatsEnnemi = new JLabel("-");
		lblDegatsEnnemi.setHorizontalAlignment(SwingConstants.CENTER);
		lblDegatsEnnemi.setFont(new Font("Matura MT Script Capitals", Font.PLAIN, 16));
		lblDegatsEnnemi.setBounds(260, 40, 58, 14);
		contentPanel.add(lblDegatsEnnemi);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public DialogATQ(Terrain central,Terrain tUP, Terrain tDOWN, Terrain tLEFT, Terrain tRIGHT) {
		this();
		this.tUP = tUP;
		this.tDOWN = tDOWN;
		this.tRIGHT = tRIGHT;
		this.tLEFT = tLEFT;
		this.tCentral = central;
		this.agent = central.getOccupant();
		lblAgent.setIcon(new ImageIcon(central.getImage()));
		if(tUP != null && tUP.getOccupant() != null) {
			btnUP.setIcon(new ImageIcon(tUP.getImage()));
			btnUP.setVisible(true);
		}else {
			btnUP.setVisible(false);
		}
		if(tDOWN != null && tDOWN.getOccupant() != null) {
			btnDOWN.setIcon(new ImageIcon(tDOWN.getImage()));
			btnDOWN.setVisible(true);
		}else {
			btnDOWN.setVisible(false);
		}
		if(tLEFT != null && tLEFT.getOccupant() != null) {
			btnLEFT.setIcon(new ImageIcon(tLEFT.getImage()));
			btnLEFT.setVisible(true);
		}else {
			btnLEFT.setVisible(false);
		}
		if(tRIGHT != null && tRIGHT.getOccupant() != null) {
			btnRIGHT.setIcon(new ImageIcon(tRIGHT.getImage()));
			btnRIGHT.setVisible(true);
		}else {
			btnRIGHT.setVisible(false);
		}
	}
	
	private void updateTableWithEnnemi(Terrain t) {
		if(t != null && t.getOccupant() != null) {
			lblDegatsAgent.setText(Integer.toString(agent.calculDommages(t.getOccupant(), t)));
			lblDegatsEnnemi.setText(Integer.toString(t.getOccupant() .calculDommages(agent, tCentral)));
		}
	}
	
	public JButton getOkButton() {
		return okButton;
	}
}
