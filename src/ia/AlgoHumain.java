package ia;

import engine.Agent;
import engine.Player;
import game.GameEngine;

public class AlgoHumain implements IAlgorithme{
	private boolean _finDeTour;
	private Player _joueur;
	
	public AlgoHumain(Player pRed) {
		_joueur = pRed;
	}


	@Override
	public void jouer(GameEngine gEngine) {
		_finDeTour = false;
		while(!_finDeTour) {
			try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {

            }
		}
	}


	@Override
	public boolean traiteEvent() {
		return true;
	}


	@Override
	public void setFinDeTour(boolean b) {
		_finDeTour = b;
		
	}


	@Override
	public void remove(Agent agent) {
		
	}

}
