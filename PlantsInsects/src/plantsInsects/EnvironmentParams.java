package plantsInsects;

import plantsInsects.enums.PlantSpacialDistribution; 

public class EnvironmentParams {

	private int gridSize; // size of the experimental plot, will always be square 
	private int numInsects;
	private int numPlants;
	private PlantSpacialDistribution distribution;
	private static int rowNum; // ROW NUMBER METHODOLOGY
	private static boolean repellentTrue; 


	public EnvironmentParams(){//int GridSize, int numInsects, int NumPlants, PlantSpacialDistribution dist, int rowNum){
		this.gridSize = 100; // grid is always square 
		this.numInsects = 1;
		this.numPlants = 2;
		this.distribution = PlantSpacialDistribution.Borders;
		this.rowNum = 5;
	}

	public EnvironmentParams(int gridSize, int numInsects, int numPlants, PlantSpacialDistribution dist, int rowNum){
		this.gridSize = gridSize;
		this.numInsects = numInsects;
		this.numPlants = numPlants;
		this.distribution = dist;
		this.rowNum = rowNum;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public int getNumInsects() {
		return numInsects;
	}

	public void setNumInsects(int numInsects) {
		this.numInsects = numInsects;
	}

	public int getNumPlants() {
		return numPlants;
	}

	public void setNumPlants(int numPlants) {
		this.numPlants = numPlants;
	}

	public PlantSpacialDistribution getDistribution() {
		return distribution;
	}

	public void setDistribution(PlantSpacialDistribution distribution) {
		this.distribution = distribution;
	}
	
	public static int getRowNum(){
		return rowNum;
	}
	
	public void setRowNum(int rowNum){
		this.rowNum = rowNum;
	}

}
