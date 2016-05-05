package plantsInsects;

import java.awt.Color;

import plantsInsects.enums.PlantType;

public class PlantParams {
	private String plantId;
	private PlantType type; // crop or trap
	private double percentage;
	private int damageThreshold;
	private Color displayCol;
	private double flightChance;
	private double migrationRate; 
	private boolean repellent;  
	private int count;
	private int totalDamage; 

	public PlantParams(String plantId, PlantType type, double percentage, int damageThreshold, Color col, double flightChance, double migrationRate, boolean repellent) {
		this.plantId = plantId;
		this.type = type;
		this.percentage = percentage;
		this.damageThreshold = damageThreshold;
		this.displayCol = col;
		this.flightChance = flightChance; 
		this.migrationRate = migrationRate;  
		this.repellent = repellent; 
		this.count = 0;
		this.totalDamage = 0;
	}

	public PlantParams() {
		this.plantId = "";
		this.percentage = 0.5;
		this.damageThreshold = 100;
		this.displayCol = Color.GREEN;
		this.flightChance = 0.5;
		this.migrationRate = 0.05;  
		this.repellent = false;  
	}

	public String getPlantId() {
		return plantId;
	}

	public void setPlantId(String plantId) {
		this.plantId = plantId;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public int getDamageThreshold() {
		return damageThreshold;
	}

	public void setDamageThreshold(int damageThreshold) {
		this.damageThreshold = damageThreshold;
	}
	
	public PlantType getType(){
		return type;
	}
    
	public void setType (PlantType type){
		this.type = type;
	}	

	public Color getDisplayCol() {
		return displayCol;
	}

	public void setDisplayCol(Color displayCol) {
		this.displayCol = displayCol;
	}

	public void increaseTotalDamage() {
		totalDamage++;
	}

	public void setTotalDamage(int td) {
		totalDamage = td;
	}

	public int getTotalDamge() {
		return totalDamage;
	}

	public int getMaxDamage() {
		return count * damageThreshold;
	}

	public void setCount(int c) {
		count = c;
	}
	
	public void increaseFlightChance(){ // eld 
		//System.out.println("increase flight chance is called");
		flightChance = 1;
	}
	
	public double getFlightChance(){ // don't know why this is static
		//System.out.println("flight chance" + flightChance);
		return flightChance;
	}
	
	public double getMigrationRate(){  //eld 
		return migrationRate;
	}
	
	public void increaseMigrationRate(){
		migrationRate = 1;		
	}
	
	public boolean getRepellent(){
		return repellent;
	}
	
	public void getDamageTake(){
		
	}
		
}
