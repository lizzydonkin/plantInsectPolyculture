package plantsInsects;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Plant {
	private Grid<Object> grid;
	private int damageTaken;
	private ArrayList <Insect> insects; 
	private int x;
	private int y;
	private final PlantParams speciesParams;

	public Plant(PlantParams params, Context<Object> context) {
		grid = (Grid<Object>) context.getProjection("grid");
		damageTaken = 0;
		insects = new ArrayList<Insect>();
		x = 0;
		y = 0;
		speciesParams = params;
	}	
	public int getX(){ //get xy coordinates of the plants
		GridPoint location =  grid.getLocation(this);
		x = location.getX();
		return x; 
	}
	
	public int getY(){ //get xy coordinates of the plants
		GridPoint location =  grid.getLocation(this);
		y = location.getY();
		return y; 
	}
	
	public int getInsectCount() {
		return insects.size();
	}

	public int getDamageTaken() {
		return damageTaken;
	}

	public void setDamageTaken(int damage) {
		this.damageTaken = damage;
	}
	
	public PlantParams getSpeciesParams() {
		return speciesParams;
	}
	
	public void updateDamage() {
		if (damageTaken != speciesParams.getDamageThreshold()) {
			speciesParams.increaseTotalDamage();
			damageTaken++; 
		}
		else {
			speciesParams.increaseFlightChance();
		}
	}
		
   public void updateMigrationRate() { 
	   double flightProb = speciesParams.getFlightChance();
	   if (flightProb >= 1 && speciesParams.getRepellent() == true ) {
		   speciesParams.increaseMigrationRate();
	   }
   }
	}