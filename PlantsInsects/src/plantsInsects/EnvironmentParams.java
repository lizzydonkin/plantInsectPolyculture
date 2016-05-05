package plantsInsects;

import plantsInsects.enums.PlantSpacialDistribution;

public class EnvironmentParams {

	private int gridSize; // size of the experimental plot, will always be square 
	private int numInsects;
	private int numPlants;
	private PlantSpacialDistribution distribution;


	public EnvironmentParams() {
		this.gridSize = 100;
		this.numInsects = 1;
		this.numPlants = 2;
		this.distribution = PlantSpacialDistribution.Borders;
	}

	public EnvironmentParams(int gridSize, int numInsects,
			int numPlants, PlantSpacialDistribution dist) {
		this.gridSize = gridSize;
		this.numInsects = numInsects;
		this.numPlants = numPlants;
		this.distribution = dist;
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

}
