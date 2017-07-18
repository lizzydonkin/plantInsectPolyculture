package plantsInsects;

import plantsInsects.enums.PlantSpacialDistribution; 
import repast.simphony.context.Context;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Climate {

	private static double temp; 
	private static double precipitation;


	public Climate(Context<Object> context) {
		temp = 0; 
		precipitation = 0;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
    public void step(){
		//System.out.println("is called");
		temp = setTemp();
		precipitation = setPrecip();
	}

	public double setTemp() {
	/*double T = 0;
	double Tl = 0;
	double Tu = 0;
	double T0 = 0;
	double c = 0;
	
	if(T < Tu && T > Tl ){
		T = 0.5*(1 + Math.cos(T-T0/c)* Math.PI); // check pi and cosine 
	}	
	else (0);*/
	double temp= RandomHelper.nextDoubleFromTo(13, 29);
	//System.out.println(temp);
	return temp;
	}
	

	public double setPrecip() { 
	double rain = RandomHelper.nextDoubleFromTo(100,200);
/*	double R = 0;
	double Ru = 0;
	if (R < Ru && R > 0){
		R = 0.5 *(R) 
	}
			*/
	//System.out.println(rain);
	return rain;
	}
	
	public static double getTemp(){
		return temp;
	}
	
	public static double getRain(){
		return precipitation;
	}
}	

