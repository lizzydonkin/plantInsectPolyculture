package plantsInsects;

import java.awt.Color;
import java.util.ArrayList;

import plantsInsects.enums.InsectInitialDistribution;
import plantsInsects.enums.InsectSensoryMode;
import repast.simphony.context.Context;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.util.ContextUtils;

public class InsectParams {

	private String insectId;
	private int initialCount;
	private InsectSensoryMode sensoryMode;
	private InsectInitialDistribution initialDist;
	private double mortalityRate;
	private int memorySize;
	private int maxFlightLength;
	private double migrationOutRate;
	private double migrationInRate;
	private Color displayCol;
	private ArrayList<String> preferredPlantIds;
	private int age;  
	private int count;
    private int fromTraptoCrop;
	private int fromCroptoTrap;
	private int fromCroptoCrop;
	private int fromTraptoTrap;
	private double weight; // new additions
	private String devStage; //new additions
	
	public InsectParams(
			String insectId, 
			int initialCount, 
			InsectSensoryMode sensoryMode, 
			InsectInitialDistribution initialDist,
			double mortalityRate,
			int memorySize,
			int maxFlightLength,
			double migrationOutRate,
			double migrationInRate,
			//double weight, // new addition
			//String devStage,
			Color col)
	 {
		this.insectId = insectId;
		this.initialCount = initialCount;
		this.mortalityRate = mortalityRate;
		this.maxFlightLength = maxFlightLength;
		this.sensoryMode = sensoryMode;
		this.initialDist = initialDist;
		this.memorySize = memorySize;
		this.migrationOutRate = migrationOutRate;
		this.migrationInRate = migrationInRate;
		this.displayCol = col;
		this.preferredPlantIds = new ArrayList<String>();
		this.count = initialCount;
	}

	public InsectParams(ArrayList<PlantParams> plantParams) {

		this.insectId = "";
		this.initialCount = 500;
		this.mortalityRate = 0.001;
		this.maxFlightLength = 5;
		this.sensoryMode = InsectSensoryMode.Visual;
		this.initialDist = InsectInitialDistribution.Random;
		this.memorySize = 5;
		this.migrationOutRate = 0.05;
		this.migrationInRate = 0.005;
		this.displayCol = Color.RED;
		this.preferredPlantIds = new ArrayList<String>();
		this.age = 0;
		this.count = initialCount;
		this.fromTraptoCrop = 0;
		this.fromCroptoTrap = 0;
		this.fromCroptoTrap = 0;
		this.fromTraptoTrap = 0;
		//this.weight = 0; // new addition check this against paper
		//this.devStage = "adult"; // new addition 
	}

	public String getInsectId() {
		return insectId;
	}

	public void setInsectId(String insectId) {
		this.insectId = insectId;
	}

	public int getInitialCount() {
		return initialCount;
	}

	public void setInitialCount(int initialCount) {
		this.initialCount = initialCount;
	}

	public double getMortalityRate() {
		return mortalityRate;
	}

	public void setMortalityRate(double mortalityRate) {
		this.mortalityRate = mortalityRate;
	}

	public int getMaxFlightLength() {
		return maxFlightLength;
	}

	public void setMaxFlightLength(int maxFlightLength) {
		this.maxFlightLength = maxFlightLength;
	}

	public InsectSensoryMode getSensoryMode() {
		return sensoryMode;
	}

	public void setSensoryMode(InsectSensoryMode sensoryMode) {
		this.sensoryMode = sensoryMode;
	}

	public InsectInitialDistribution getInitialDist() {
		return initialDist;
	}

	public void setInitialDist(InsectInitialDistribution initialDist) {
		this.initialDist = initialDist;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public double getMigrationOutRate() {
		return migrationOutRate;
	}

	public void setMigrationOutRate(double migrationOutRate) {
		this.migrationOutRate = migrationOutRate;
	}

	public double getMigrationInRate() {
		return migrationInRate;
	}

	public void setMigrationInRate(double migrationInRate) {
		this.migrationInRate = migrationInRate;
	}

	public Color getDisplayCol() {
		return displayCol;
	}

	public void setDisplayCol(Color displayCol) {
		this.displayCol = displayCol;
	}

	public ArrayList<String> getPreferredPlantIds() {
		return preferredPlantIds;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int c) {
		count = c;
	}

	public void incrementCount() {
		count++;
	}

	public void decrementCount() {
		count--;
	}
	
	public int getAge(){
		return age;
	}
	
	public void setAge(){
		age = 0;
	}
	
	public void increaseAge(){
		age = age + 1;
	}
	
	public int getFromTrapToCrop(){
		return fromTraptoCrop;
	}
	
	public void setFromTrapToCrop(){
		fromTraptoCrop = 0;
	}
	
	public void increaseFromTrapToCrop(){
		fromTraptoCrop++;
	}

	
	public int getFromCropToTrap(){
		return fromCroptoTrap;
	}
	
	public void setFromCropToTrap(){
		fromCroptoTrap = 0;
	}
	
	public void increaseFromCropToTrap(){
		fromCroptoTrap++;
	}
	
	public int getFromCropToCrop(){
		return fromCroptoCrop;
	}
	
	public void setFromCropToCrop(){
		fromCroptoCrop = 0;
	}
	
	public void increaseFromCropToCrop(){
		fromCroptoCrop++;
	}
	
	public int getFromTrapToTrap(){
		return fromTraptoTrap;
	}
	
	public void setFromTrapToTrap(){
		fromTraptoTrap = 0;
	}
	
	public void increaseFromTrapToTrap(){
		fromTraptoTrap++;
	}
}
