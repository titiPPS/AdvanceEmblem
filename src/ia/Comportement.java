package ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import engine.Agent;
import engine.KingAgent;
import engine.Player;
import engine.Terrain;
import game.GameEngine;

public class Comportement {
	
	private Player _joueur,_ennemi;
	private GameEngine gEngine;
	private float score = 0f;
	
	private static int MAX_COEFF = 200;
	private static int MIN_COEFF = 10;
	private static int MAX_TURNS_LONG_TERME = 6;
	private float penaliteMort,recompenseCmdt,recUsine,penUsine;
	
	/*Long terme*/
	private float pLongTerme;
	private int longTermeTurnsLeft = 0;
	
	/*ATQ*/
	private float coeffTuer,coeffDmgInfl,coeffDmgRec,coeffCibleCommune;
	
	/*Type de comportement*/
	private float objDEF,objATQ;
	
	public Comportement(GameEngine ge,Player joueur,Player ennemi,float penaliteMort,float recompenseCmdt,float recUsine, float penUsine) {
		this._joueur = joueur;
		this._ennemi = ennemi;
		this.gEngine = ge;
		this.penaliteMort = penaliteMort;
		this.recompenseCmdt = recompenseCmdt;
		this.penUsine = penUsine;
		this.recUsine = recUsine;
	}
	
	/**
	 * Setter permettant de définir la priorité entre ATQ et DEF
	 * @param ATQ : le coefficient appliqué au calcul des objectifs offensifs
	 * @param DEF : le coefficient appliqué au calcul des objectifs deffensifs
	 */
	public void setDistribution(float ATQ, float DEF) {
		objDEF = DEF;
		objATQ = ATQ;
	}
	
	/**
	 * Methode permettant de définir les coefficients déterminants dans le choix d'une cible
	 * 
	 * @param coeffTUER : le poids de la décision de détruire une unité adverse
	 * @param coeffDmgInfl : le poids de la décision de maximiser les domages infligés
	 * @param coeffDmgRec : le poids de la décision de minimiser les domages reçus
	 * @param coeffCibleCommune : le poids de la décision de privilégier une cible commune à des alliés
	 */
	public void setCoeffATQ(float coeffTUER, float coeffDmgInfl, float coeffDmgRec,float coeffCibleCommune) {
		this.coeffTuer = coeffTUER;
		this.coeffDmgInfl = coeffDmgInfl;
		this.coeffDmgRec = coeffDmgRec;
		this.coeffCibleCommune = coeffCibleCommune;
	}
	
	/**
	 * Methode permettant de fixer la probabilité de choisir une stratégie à long
	 * terme
	 * 
	 * @param pLongTerme : la probabilité de privilégier, pour le tour courant, une stratégie à 
	 * long terme
	 */
	public void setCoeffLongTerme(float pLongTerme) {
		this.pLongTerme = pLongTerme;
	}
	
	/**
	 * Methode permettant de choisir la meilleure destination possible pour un agent donné.
	 * 
	 * @param agent : l'agent qui se déplace
	 * @param lstTerrain : la liste des destinations possibles
	 * @param ennemi : le joueur ennemi
	 * @return la destination choisie (ou null si pas de déplacement)
	 */
	public Terrain choisirDestination(Agent agent, ArrayList<Terrain> lstTerrain, Player ennemi) {
		if(longTermeTurnsLeft > 0) {
			longTermeTurnsLeft --;
			return destinationObjectif(agent,ennemi);
		}else {
			if(Math.random() < pLongTerme) {
				return evaluerDestination(agent,lstTerrain);
			}else {
				longTermeTurnsLeft = (int)(Math.random() * MAX_TURNS_LONG_TERME);
				return destinationObjectif(agent,ennemi);
			}
		}
	}
	
	/**
	 * 
	 * 
	 * @param posEnnemisAdj les cases adjacentes à l'agent qui contiennent des ennemis
	 * @param position le terrain de l'agent
	 * @param a l'agent
	 * @return la position de la meilleure cible ou null s'il vaut mieux ne pas attaquer
	 */
	public Terrain choixATQ(ArrayList<Terrain> posEnnemisAdj,Terrain position,Agent a) {
		Terrain res = null;
		float[] scores = new float[posEnnemisAdj.size()];
		float total = 0.0f;
		for(int i = 0 ; i < posEnnemisAdj.size(); i ++) {
			scores[i] = 0;
			Terrain t = posEnnemisAdj.get(i);
			Agent ennemi = t.getOccupant();
			int dmgInfliges = a.calculDegats(ennemi, t), dmgRecus = ennemi.calculDegats(a, position);
			boolean ennemiTue = (ennemi.getPV() - dmgInfliges <= 0); 
			if(ennemiTue) {
				dmgRecus = 0;
				scores[i] += coeffTuer * Agent.valueOf(ennemi);
			}
			boolean agentTue = (a.getPV() - dmgRecus <= 0);
			if(agentTue) {
				scores[i] -= penaliteMort;
			}
			ArrayList<Agent> lstAllies = calculerMenaces(a, _joueur);
			boolean cibleCommune = lstAllies.size() > 0;
			if(cibleCommune) {
				scores[i] += Math.min(4, lstAllies.size()) * coeffCibleCommune;
			}
			if(a instanceof KingAgent) {
				scores[i] += recompenseCmdt;
			}
			scores[i] += dmgInfliges * coeffDmgInfl + dmgRecus * coeffDmgRec;
			total += scores[i];
		}
		for(int i = 0; i < scores.length ; i++) {
			scores[i] = scores[i] / total;
		}
		double decision = Math.random();
		total = 0;
		for(int i = 0; i < scores.length ; i++) {
			if(decision < total + scores[i]) {
				res = posEnnemisAdj.get(i);
			}else {
				total += (scores[i] < 0 ? 0 : scores[i]);
			}
		}
		return res;
	}
	
	/**
	 * Methode permettant d'établir une grossière liste des menaces pesant sur l'agent
	 * donné en paramètre, menaces détenues par le joueur <ennemi>
	 * 
	 * @param agent : l'agent concerné
	 * @param ennemi : le joueur dont on essaye de déterminer la menace
	 * @return la liste des agents à "porté" de l'agent courant
	 */
	private ArrayList<Agent> calculerMenaces(Agent agent, Player ennemi) {
		ArrayList<Agent> resultat = new ArrayList<Agent>();
		for(Agent a : ennemi.getListOfUniteLarge()) {
			if(a.getX() <= agent.getX() + a.getMouvement() && a.getX() >= agent.getX() + a.getMouvement()) {
				if(a.getY() <= agent.getY() + a.getMouvement() && a.getY() >= agent.getY() + a.getMouvement()) {
					resultat.add(a);
				}
			}
		}
		return resultat;
	}
	
	/**
	 * Methode permettant de choisir un terrain parmis une liste de destination
	 * 
	 * @param agent : l'agent qui se déplace
	 * @param lstTerrain : la liste de destinations à évaluer
	 * @return la destination ou null s'il vaut mieux ne pas bouger
	 */
	private Terrain evaluerDestination(Agent agent,ArrayList<Terrain> lstTerrain) {
		Terrain res = null;
		float aAjouter = 0.0f,total = 0.0f;
		float[] scores = new float[lstTerrain.size()];
		for(int i = 0 ; i < lstTerrain.size(); i ++) {
			scores[i] = evaluerTerrain(agent,lstTerrain.get(i));
			if(scores[i] < 0) {
				aAjouter = - scores[i];
			}else{
				total += scores[i];
			}
		}
		double prec = 0.0,decision = Math.random();
		for(int i = 0; i < scores.length && res == null ; i++) {
			scores[i] = (scores[i] + aAjouter) / (scores.length * aAjouter + total);
			if(decision < prec + scores[i]) {
				res = lstTerrain.get(i);
			}
			prec += scores[i];
		}
		return res;
	}
	
	/**
	 * 
	 * @param a
	 * @param terrain
	 * @return
	 */
	private float evaluerTerrain(Agent a,Terrain terrain) {
		float score = 0f;
		if(terrain.getUsine() != null) {
			score = (terrain.getUsine().appartientA(_joueur) ? -penUsine : recUsine);
		}
		int alliesAdj = 0, ennemiAdj = 0;
		int dmgInfl = 0,dmgRec = 0,nbTues = 0,nbMorts = 0;
		ArrayList<Terrain> tmp = terrain.getListeVoisins();
		/*Calcul du nombre de cibles et du nombre d'alliés*/
		for(Terrain t : tmp) {
			if(t.getOccupant() != null) {
				if(t.getOccupant().appartientA(_joueur)) {
					alliesAdj++;
				}else {
					ennemiAdj++;
					dmgInfl = a.calculDegats(t.getOccupant(), t);
					boolean tue = (t.getOccupant().getPV() - dmgInfl <= 0);
					dmgRec = t.getOccupant().calculDegats(a, terrain);
					boolean mort = (!tue && (a.getPV() - dmgRec <= 0));
					nbMorts += (mort ? 1 : 0);
					nbTues += (tue ? 1 : 0);
					if(t.getOccupant() instanceof KingAgent) {
						score += (tue ? recompenseCmdt * 4 : recompenseCmdt);
					}
				}
			}
		}
		ArrayList<Terrain> set = PathFinder.listeDestination(terrain, a.getMouvement());
		dmgInfl = 0;
		dmgRec = 0;
		for(Terrain t : set) {
			if(t.getOccupant() != null) {
				if(t.getOccupant().appartientA(_joueur)) {
					alliesAdj++;
				}else {
					ennemiAdj++;
					dmgInfl += a.calculDegats(t.getOccupant(), t);
					dmgRec += t.getOccupant().calculDegats(a, terrain);
					if(t.getOccupant() instanceof KingAgent) {
						score += recompenseCmdt;
					}
				}
			}
		}
		
		score += (((terrain.getDef() + alliesAdj) / (1+dmgRec)) - 2 * nbMorts * penaliteMort) * 10 * objDEF;
		score += (dmgInfl + nbTues * 2 * coeffTuer + ennemiAdj) *10 * objATQ; 
		score += (objATQ / 2) * (Math.abs(_ennemi.getCommandant().getX() - terrain.getX()) + Math.abs(_ennemi.getCommandant().getY() - terrain.getY()));
		score += (objDEF / 2) * (Math.abs(_joueur.getCommandant().getX() - terrain.getX()) + Math.abs(_joueur.getCommandant().getY() - terrain.getY()));
		return score;
	}

	private Terrain destinationObjectif(Agent a,Player ennemi) {
		Terrain init = gEngine.getTerrain(a.getX(), a.getY());
		if(objATQ > objDEF) {
			Agent comdt = ennemi.getCommandant();
			Terrain dest = gEngine.getTerrain(comdt.getX(), comdt.getY());
			ArrayList<Terrain> cheminToCmdt = PathFinder.aStarForATQ(init, dest, a.getMouvement(), _joueur);
			if(cheminToCmdt != null) {
				for(int j = 1; j < cheminToCmdt.size(); j++) {
					if(cheminToCmdt.get(j).getOccupant() != null) {
						return cheminToCmdt.get(j - 1);
					}
				}
			}
		}else {
			boolean xCondition = (a.getX() + a.getMouvement() >= _joueur.getCommandant().getX() && a.getX() - a.getMouvement() <= _joueur.getCommandant().getX());
			boolean yCondition = (a.getY() + a.getMouvement() >= _joueur.getCommandant().getY() && a.getY() - a.getMouvement() <= _joueur.getCommandant().getY());
			if (xCondition && yCondition){
				//Perimetre de protection
				ArrayList<Agent> lstMenaces = calculerMenaces(_joueur.getCommandant(), ennemi);
				Collections.sort(lstMenaces, new Comparator<Agent>() {
					@Override
					public int compare(Agent o1, Agent o2) {
					return Agent.valueOf(o2) - Agent.valueOf(o1);
					}
				});
				Terrain[] tab = new Terrain[a.getMouvement()];
				ArrayList<Terrain> chemin;
				
				/*On évalue la menace la plus pertinente (il vaut mieux tuer qqn
				 * plutôt que de se diriger vers qqn d'autre et ne rien faire)
				 * 
				 */
				for(int i = 0; i < lstMenaces.size() ; i++) {
					chemin = PathFinder.aStarForATQ(init, gEngine.getTerrain(a.getX(), a.getY()), a.getMouvement(), _joueur);
					if(chemin != null) {
						for(int j = 1; j < chemin.size(); j++) {
							if(chemin.get(j).getOccupant() != null) {
								return chemin.get(j);
							}
						}
					}
				}
				
			}else {
				//on essaye de rejoindre le commandant et on dézingue ce qu'il y a sur le chemin
				boolean destinationFound = false;
				int cpt = 0;
				//Terrain init = gEngine.getTerrain(_joueur.getCommandant().getX(), _joueur.getCommandant().getY());
				ArrayList<Terrain> al;
				ArrayList<Terrain> lstDestCmdt = PathFinder.listeDestination(init, _joueur.getCommandant().getMouvement());
				while(!destinationFound && cpt < lstDestCmdt.size()) {
					al = PathFinder.aStar(init, lstDestCmdt.get(cpt), a.getMouvement());
					if(al != null) {
						return lstDestCmdt.get(cpt);
					}
					cpt ++;
				}
				
			}
		}
		ArrayList<Terrain> al = PathFinder.listeDestination( gEngine.getTerrain( a.getX(), a.getY()), a.getMouvement());
		return evaluerDestination(a,al);
	}
	
	public Comportement clone() {
		Comportement c = new Comportement(gEngine, _joueur,_ennemi, penaliteMort, recompenseCmdt,recUsine,penUsine);
		c.setCoeffATQ(coeffTuer, coeffDmgInfl, coeffDmgRec, coeffCibleCommune);
		c.setCoeffLongTerme(pLongTerme);
		c.setDistribution(objATQ, objDEF);
		return c;
	}
	
	public static Comportement comportementAleatoire(GameEngine ge, Player p,Player ennemi) {
		Comportement c = new Comportement(ge, p,ennemi,MIN_COEFF + (float)Math.random() * MAX_COEFF, MIN_COEFF + (float)Math.random() * MAX_COEFF
				,MIN_COEFF + (float)Math.random() * MAX_COEFF,MIN_COEFF + (float)Math.random() * MAX_COEFF);
		c.setCoeffATQ( (float)Math.random() * MIN_COEFF, (float) Math.random() * MIN_COEFF, (float)Math.random()* MIN_COEFF, (float)Math.random()* MIN_COEFF);
		c.setCoeffLongTerme((float)Math.random());
		c.setDistribution((float)Math.random(), (float)Math.random());
		return c;
	}
	
	public float getScore() {
		return score;
	}


	public void setScore(float score) {
		this.score = score;
	}

}
