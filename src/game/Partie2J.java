package game;

import javax.swing.SwingWorker;

import ui.GUI;

import engine.Player;

public class Partie2J implements Runnable {
	private Player _jBleu,_jRouge;
	private GameEngine _ge;
	private GUI _usrInterface;
	private boolean finDePartie = false;
	
	public Partie2J(GameEngine gEngine,Player joueurRouge, Player joueurBleu,GUI ui) {
		_jBleu = joueurBleu;
		_jRouge = joueurRouge;
		_ge = gEngine;
	}

	public void arreter() {
		finDePartie = true;
	}

	@Override
	public void run() {
		boolean fin = false;
		while(!fin && !finDePartie) {
			_ge.initJoueurCourant(_jRouge);
			_jRouge.jouer(_ge);
			_jRouge.initTour();
			_ge.repaintJoueurMap(_jRouge);
			if(!_jRouge.aPerdu()) {
				_ge.initJoueurCourant(_jBleu);
				_jBleu.jouer(_ge);
				_jBleu.initTour();
				_ge.repaintJoueurMap(_jBleu);
			}
			fin = _jBleu.aPerdu() || _jRouge.aPerdu();
		}
	}
}
