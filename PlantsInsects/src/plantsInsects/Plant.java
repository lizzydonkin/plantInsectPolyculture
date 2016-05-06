package plantsInsects;

import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;

public class Plant {
	private Grid<Object> grid;
	private int damageTaken;
	private ArrayList <Insect> insects; 
	private final PlantParams speciesParams;

	public Plant(PlantParams params, Context<Object> context) {
		grid = (Grid<Object>) context.getProjection("grid");
		damageTaken = 0;
		insects = new ArrayList<Insect>(); 
		speciesParams = params;
	}	
	public GridPoint getXY(){ //get xy coordinates of the plants
		return grid.getLocation(this);
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