package ui;

import game.GameEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.GUI.*;

public class ListenerMenu implements ActionListener{
	private EventUsine eu;
	private EventAgent ea;
	private GameEngine _ge;
	private boolean menuUsine;
	
	public ListenerMenu(EventUsine resultatClic,GameEngine ge) {
		eu = resultatClic;
		menuUsine = true;
		_ge = ge;
	}
	
	public ListenerMenu(EventAgent resultatClic, GameEngine ge) {
		ea = resultatClic;
		menuUsine = false;
		_ge = ge;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(menuUsine) {
			_ge.resultatMenu(eu);
		}else {
			_ge.resultatMenu(ea);
		}
		
	}

}
