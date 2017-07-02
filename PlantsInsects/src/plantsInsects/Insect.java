package plantsInsects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Paint;
import java.awt.Stroke;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;
import repast.simphony.util.SimUtilities;

public class Insect {

	private Grid<Object> grid;
	private ArrayList<Plant> visitedPlants;
	private final InsectParams speciesParams;
	private int age;
	// adding in new parameters
	private String lastPlantType;
	private boolean hopped;
	private boolean moved;
	private boolean dead;
	private double emigrationProb;
	

	public Insect(InsectParams params, Context<Object> context) { 
		grid = (Grid<Object>) context.getProjection("grid");
		visitedPlants = new ArrayList<Plant>();
		speciesParams = params;
		age = 0;
		//lastPlantType = "";
		hopped = false;
		moved = false;
		dead = false;
		emigrationProb = speciesParams.getMigrationOutRate();
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 2)
	public void step() {
		
		Context<Object> context = ContextUtils.getContext(this);
		double chanceOfMort = 1 - (age * speciesParams.getMortalityRate());	
		if(RandomHelper.nextDoubleFromTo(0, 1) > chanceOfMort) { // daily mortality is age dependent
			killInsect();
			return;
		}
		lastPlantType = getLastPlant();
		GridPoint thisPoint = grid.getLocation(this);
		int thisX = thisPoint.getX();
		int thisY = thisPoint.getY();
		Plant thisPlant = getPlantAt(thisX,thisY);
		if (moved == true){
		emigration(thisPlant);} // emigration only happens if the insect makes a flight
		repellentEncounters(thisPlant); //emigration from encountering repellent plants can happen regardless
		 //killing insects that have visited normal or deterrent plants and have emigrated
		if (dead == true) {
			killInsect();
			return;
		}
		Plant plant = getNextPlant();
		Plant moveToPlant = moveTo(plant);
		GridPoint newPt = grid.getLocation(moveToPlant);
		grid.moveTo(this, newPt.getX(), newPt.getY());
		if (thisPlant != moveToPlant && hopped != true){
			addMovement();// if the insect has moved from a plant that is different from the one it is on now the movement is recorded
			moved = true;
		}
		if (dead == true) {// add the plant to memory then do the repellentEncounters
			killInsect();
			return;
		}
		plant.updateDamage();// update the damage to the all of the plants (damage threshold - 1 for each insect present on it)
		plant.updateMigrationRate();
		insectUpdateEmigrationRate();
		age++;//   increase age of insects by 1
	}
	
	public void checkPopSize(){
		int totalPopulation =  speciesParams.getCount();
		if (totalPopulation == 0) {
			RunEnvironment.getInstance().endRun(); // end the simulation if all insects are dead
		}
	}
	
	public void checkTickCount(){
		double tickCount = RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		if (tickCount >= 50.00 ) {
			RunEnvironment.getInstance().endRun();
			}
		}
	
	private Plant moveTo(Plant p) {
		dead = false;
		Plant chosenPlant = null;
		boolean rep = p.getSpeciesParams().getRepellent();
		double fprob = p.getSpeciesParams().getFlightChance();
		
		if (!visitedPlants.contains(p)){ //if the plant isn't already in the memory
			visitedPlants.add(p); //add plant to memory
		}
		if (visitedPlants.size() > speciesParams.getMemorySize()) {// sliding window memory
			visitedPlants.remove(0);
		}
		if (fprob > 0.99){ 
			if (rep == true){
				boolean repCheck = true;
				while (repCheck == true){
					Plant potentialP = getNextPlant();
					insectUpdateEmigrationRate();
					emigration(potentialP);
					repellentEncounters(potentialP);
					GridPoint newPt = grid.getLocation(potentialP);
					grid.moveTo(this, newPt.getX(), newPt.getY());
					repCheck = potentialP.getSpeciesParams().getRepellent();
					if (repCheck == false || dead == true){
						chosenPlant = potentialP;
						break;
					}
				}
			}
			else { 
				chosenPlant = p;
			}
		}
		else {
			chosenPlant = p; //normal plant
		}  
		return chosenPlant; 
	} 

	public void insectUpdateEmigrationRate(){
		GridPoint insPoint = grid.getLocation(this);
		int x = insPoint.getX();
		int y = insPoint.getY();
		Plant currentPlant = getPlantAt(x,y);
		emigrationProb = currentPlant.getSpeciesParams().getMigrationRate();
	}
	
	public void emigration(Plant p){
		GridPoint newPt= grid.getLocation(p);
		boolean isOnBorder = false; // find out if the insect is on the border of the plot
		int edgeY = grid.getDimensions().getHeight() - 1;
		int edgeX = grid.getDimensions().getWidth() - 1;
		if (newPt.getX() == 0 || newPt.getX() == edgeX || newPt.getY() == 0
				|| newPt.getY() == edgeY) {
			isOnBorder = true;
		}
		if (moved = true){ // this migration chance should only be applied if the insect has made a flight, does not apply if the insect has not moved or has 'hopped'
			 if ((RandomHelper.nextDoubleFromTo(0, 1) < emigrationProb)
				|| (isOnBorder && RandomHelper.nextDoubleFromTo(0,1) < 0.05)) { // border emigration is fixed at 0.05 
				dead = true;
				return;
				}
		}
	}

	public void killInsect(){
		Context<Object> context = ContextUtils.getContext(this);
		context.remove(this);
		speciesParams.decrementCount();
		checkPopSize();
		}
	
	public void repellentEncounters(Plant p){
		int repellentPlantsInMemory = 0;
		for (Plant visited :visitedPlants) {
			if (visited.getSpeciesParams().getFlightChance() > 0.99) {
			 repellentPlantsInMemory++ ;
			}
		}
		
		if (repellentPlantsInMemory == 5) {
			dead = true;
			return;
		}
	}
	
	private double getMigOutChance() { // the migration rate from the plant the insect is currently on 
		GridPoint insPoint = grid.getLocation(this);
		int x = insPoint.getX();
		int y = insPoint.getY();
		Plant currentPlant = getPlantAt(x,y);
		double migOutChance = currentPlant.getSpeciesParams().getMigrationRate();
	    return migOutChance;
	}
	
	private Plant getNextPlant() {
        hopped = false; 
        moved = false;
		GridPoint insPoint = grid.getLocation(this);
		int x = insPoint.getX();
		int y = insPoint.getY();
		Plant currentPlant = getPlantAt(x,y);
		Plant plant = null;		
		double fChance = currentPlant.getSpeciesParams().getFlightChance();
		if (RandomHelper.nextDoubleFromTo(0,1) < fChance){ // the probability of an insect performing a foraging flight is dependent on the flight probability of the plant
			hopped = false;
			switch (speciesParams.getSensoryMode()) {
			case Visual:
				plant = getNextPlantVisual(insPoint);
				break;
			case Olfactory:
				plant =  getNextPlantOlfactory();
				break;
			case Contact:
				plant = getNextPlantContact();
				break;
			default:
				break;
			}
			return plant;
		}
		else {
			if (RandomHelper.nextDoubleFromTo(0,1) <= 0.5){ // if the insect doesn't fly it has a 50/50 chance of staying where it is or hopping to an adjacent plant
				hopped = false;
				return currentPlant;
				
			}
			else {
		    plant = getNeighbourPlant();
		    hopped = true; 
		    return plant;
		    
			}
		}
	}
	
	private Plant getNeighbourPlant(){
			GridCellNgh<Plant> nghCreator = new GridCellNgh<Plant>(grid,
					grid.getLocation(this), Plant.class,
					1, 1);
			List<GridCell<Plant>> gridCells = nghCreator.getNeighborhood(false);
			SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
			// return the first unvisited plant since they are shuffled, it will be random
			Plant result = null;
			for (GridCell<Plant> cell : gridCells) { 
				Plant current = cell.items().iterator().next();
				if (!visitedPlants.contains(current)) { //check the plant is not in the insect memory as previously visited
					result = current;
					break;
				}
			}
			if (result == null){ // if all neighbouring plants have been visited, selected from the shuffled list of neighbouring plants 
				GridCell<Plant> cell = gridCells.get(gridCells.size()-1);
				Plant current = cell.items().iterator().next();
				result = current;
			}
			return result; 
		}
		
	private Plant getNextPlantVisual(GridPoint gi) {
		double lowestFlightChance = 100; // far higher than any
		double distance = grid.getDimensions().getWidth(); //further than any plant
		Plant chosen = null;
		int flightLength = speciesParams.getMaxFlightLength(); //flight length is a random number between 0 and the maximum flight length 
		int radius = RandomHelper.nextIntFromTo(1,flightLength); 
		ArrayList<Plant> plantsInCircle = getPlantsInRadius(radius);
		SimUtilities.shuffle(plantsInCircle, RandomHelper.getUniform()); // shuffle the plants in the radius of the search area
		for(Plant plant : plantsInCircle) {	
			double fc = plant.getSpeciesParams().getFlightChance();
			double d = grid.getDistance(gi,grid.getLocation(plant));
			if(!visitedPlants.contains(plant) && 
					((fc < lowestFlightChance)) || (fc < lowestFlightChance && d < distance)) { //chooses the closest most preferred plant
				chosen             = plant;
				lowestFlightChance = fc;
				distance           = d;		
			}	
		}
		return chosen;
	}

	private Plant getNextPlantContact() {
		int flightLength = speciesParams.getMaxFlightLength(); // get the random movement length 
		int radius = RandomHelper.nextIntFromTo(1, flightLength);
		GridCellNgh<Plant> nghCreator = new GridCellNgh<Plant>(grid, // create an array of possible plants to move to, within the search radius
				grid.getLocation(this), Plant.class,
				radius, radius);
		List<GridCell<Plant>> gridCells = nghCreator.getNeighborhood(false);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());
		// return the first unvisited plant since they are shuffled, it will be random
		Plant result = null;
		for (GridCell<Plant> cell : gridCells) {
			Plant current = cell.items().iterator().next();
			if (!visitedPlants.contains(current)) {  
				result = current;
				break;
			}
			else {
			result = current; 
			break;
			}
		}
		return result;
	}
	
	public double getLowestFlightChance(){ // establishes what the lowest flight chance in the population is 
		ArrayList <Double> flightChances = new ArrayList<>();
		int x = 0;
		int y = 0;
		int width = grid.getDimensions().getWidth();
		int height = grid.getDimensions().getHeight();
		for (x= 0; x <= width - 1; x++){
			for (y= 0; y <= height - 1; y++){
				int newXCor = x;
				int newYCor = y;
				
				Plant p = getPlantAt(newXCor, newYCor);
				if (p != null){
				double fc = p.getSpeciesParams().getFlightChance();
					flightChances.add(fc);}
				}
			}
		Collections.sort(flightChances);
		double lowestFC = flightChances.get(0);
		return lowestFC; 
	}

	private Plant getNextPlantOlfactory() {
		int flightLength = speciesParams.getMaxFlightLength(); //Flight length (and therefore search radius) is determined as a random number between 0 and the max flight length 
		int radius = RandomHelper.nextIntFromTo(1, flightLength);
		ArrayList<Plant> plantsToAimFor = getPlantsAtRadius(radius); //Creates an array of the plants at the edge of the search radius
		Plant chosen = plantsToAimFor.get(RandomHelper.nextIntFromTo(0,plantsToAimFor.size() - 1)); //Chooses a random plant within the radius  
		ArrayList<Plant> plantsInLine = getPlantsInLine(grid //Creates a flight path towards the chosen plant
				.getLocation(chosen));
		Plant p = null;
		double lowestFlightChance = getLowestFlightChance();//100;
		for (Plant o : plantsInLine) { // Chooses preferred plants, landing is stochastic, with 70% of detecting/landing on the plant
			if(o.getSpeciesParams().getFlightChance() <= lowestFlightChance//(o.getSpeciesParams().getFlightChance() < 0.5  // was <= 0.05
					&& RandomHelper.nextDoubleFromTo(0,1) < 0.7 
					&& !visitedPlants.contains(o)) {
				p = o;
				break;
			}
		}
		
	 if (p == null) {
			p = plantsInLine.get(plantsInLine.size()- 1); // if the insects doesn't land, it moves to last plant in the plants in line array
		}
		return p;
	}
		
	private Plant getPlantAt(int x, int y) { //get a plant at a specific point
		Plant result = null;
		for (Object o : grid.getObjectsAt(x, y)) {
			if (o.getClass() == Plant.class) {
				result = (Plant) o;
				break;
			}
		}
		return result;
	}

	private ArrayList<Plant> getPlantsInRadius(int radius) { //creates an array of plants within a circle with a given radius
		GridPoint insPoint = grid.getLocation(this);
		ArrayList<Plant> plantsInCircle = new ArrayList<Plant>();
		for (int i = radius; i > 0; i--){
			for (int p = -i; p <= i; p++) {
				int x1 = insPoint.getX() - i;
				int y1 = insPoint.getY() + p;
				if (i != p)
					if (x1 >= 0 && x1 < ((grid.getDimensions().getWidth()) - 1) && y1 >= 0
					&& y1 < ((grid.getDimensions().getHeight()) -1))
						plantsInCircle.add(getPlantAt(x1, y1));
				int x2 = insPoint.getX() + i;
				int y2 = insPoint.getY() + p;
				if (radius != -p)
					if (x2 >= 0 && x2 < ((grid.getDimensions().getWidth()) - 1) && y2 >= 0
					&& y2 < ((grid.getDimensions().getHeight())-1))
						plantsInCircle.add(getPlantAt(x2, y2));
				int x3 = insPoint.getX() + p;
				int y3 = insPoint.getY() - i;
				if (radius != -p)
					if (x3 >= 0 && x3 < ((grid.getDimensions().getWidth()) -1) && y3 >= 0
					&& y3 < ((grid.getDimensions().getHeight())-1))
						plantsInCircle.add(getPlantAt(x3, y3));
				int x4 = insPoint.getX() + p;
				int y4 = insPoint.getY() + i;
				if (radius != p)
					if (x4 >= 0 && x4 < ((grid.getDimensions().getWidth()) -1) && y4 >= 0
					&& y4 < ((grid.getDimensions().getHeight())-1))
						plantsInCircle.add(getPlantAt(x4, y4));
			}
		} 
		return plantsInCircle;
	}
	
	private ArrayList<Plant> getPlantsAtRadius(int r) { //creates an array of plants within a circle with a given radius
		GridPoint insPoint = grid.getLocation(this);
		ArrayList<Plant> plantsAtRadius = new ArrayList<Plant>();
		int i = r;
			for (int p = -i; p <= i; p++) {
				int x1 = insPoint.getX() - i;
				int y1 = insPoint.getY() + p;
				if (i != p)
					if (x1 >= 0 && x1 < ((grid.getDimensions().getWidth()) - 1) && y1 >= 0
					&& y1 < ((grid.getDimensions().getHeight()) -1))
						plantsAtRadius.add(getPlantAt(x1, y1));
				int x2 = insPoint.getX() + i;
				int y2 = insPoint.getY() + p;
				if (r != -p)
					if (x2 >= 0 && x2 < ((grid.getDimensions().getWidth()) - 1) && y2 >= 0
					&& y2 < ((grid.getDimensions().getHeight())-1))
						plantsAtRadius.add(getPlantAt(x2, y2));
				int x3 = insPoint.getX() + p;
				int y3 = insPoint.getY() - i;
				if (r != -p)
					if (x3 >= 0 && x3 < ((grid.getDimensions().getWidth()) -1) && y3 >= 0
					&& y3 < ((grid.getDimensions().getHeight())-1))
						plantsAtRadius.add(getPlantAt(x3, y3));
				int x4 = insPoint.getX() + p;
				int y4 = insPoint.getY() + i;
				if (r != p)
					if (x4 >= 0 && x4 < ((grid.getDimensions().getWidth()) -1) && y4 >= 0
					&& y4 < ((grid.getDimensions().getHeight())-1))
						plantsAtRadius.add(getPlantAt(x4, y4));
			
		} 
		return plantsAtRadius;
	}

	private ArrayList<Plant> getPlantsInLine(GridPoint toPoint) {
		GridPoint insPoint = grid.getLocation(this);
		ArrayList<Plant> plants = new ArrayList<Plant>();
		// Bresenham's line algorithm
		int dx = Math.abs(toPoint.getX() - insPoint.getX());
		int dy = Math.abs(toPoint.getY() - insPoint.getY());
		int sx, sy;
		int err = dx - dy;
		if (insPoint.getX() < toPoint.getX())
			sx = 1;
		else
			sx = -1;
		if (insPoint.getY() < toPoint.getY())
			sy = 1;
		else
			sy = -1;
		int x = insPoint.getX();
		int y = insPoint.getY();
		while (true) {
			plants.add(getPlantAt(x, y));
			if (x == toPoint.getX() && y == toPoint.getY())
				break;
			int e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x = x + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y = y + sy;
			}
		}
		return plants;
		}

	public String getLastPlant(){
		GridPoint insPoint = grid.getLocation(this);
		int x = insPoint.getX();
		int y = insPoint.getY();
		Plant lastPlant = getPlantAt(x,y);
		String plantType = lastPlant.getSpeciesParams().getType().toString();
	    return plantType;
	}
	
	public void addMovement(){
		GridPoint insPoint = grid.getLocation(this);
		int x = insPoint.getX();
		int y = insPoint.getY();
		Plant currentPlant = getPlantAt(x,y);
		String currentPlantType = currentPlant.getSpeciesParams().getType().toString();
		String previousPlant = lastPlantType;
		String c = "Crop";
		String t = "Trap";
		if(currentPlantType.equals(previousPlant)){
			if (currentPlantType.equals(c)){
			speciesParams.increaseFromCropToCrop();
			}
			else {
				speciesParams.increaseFromTrapToTrap();
			}
		}
		else {	
			if(currentPlantType.equals(c)){
				speciesParams.increaseFromTrapToCrop();
			}
			else { 
				speciesParams.increaseFromCropToTrap();
			}	
		}
	}
			
	public ArrayList<Plant> getVisitedPlants() {
		return visitedPlants;
	}

	public void setVisitedPlants(ArrayList<Plant> visitedPlants) {
		this.visitedPlants = visitedPlants;
	}

	public InsectParams getSpeciesParams() {
		return speciesParams;
	}
}
