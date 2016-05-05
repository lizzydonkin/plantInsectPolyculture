package plantsInsects;

import java.util.ArrayList;

import org.apache.poi.util.SystemOutLogger;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

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