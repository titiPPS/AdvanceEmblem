package ia;

import engine.Agent;
import game.GameEngine;

public interface IAlgorithme{
	public void jouer(GameEngine gEngine);

	public boolean traiteEvent();

	public abstract void setFinDeTour(boolean b);

	public void remove(Agent agent);
}
