package plantsInsects;

import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cern.jet.random.Exponential;
import plantsInsects.ui.ModelUserPanel;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.data2.BatchParamMapFileWriter;
import repast.simphony.data2.engine.DataSetComponentControllerAction;
import repast.simphony.data2.engine.DataSetDescriptor;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.space.grid.StickyBorders;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.ui.RSApplication;
import repast.simphony.userpanel.ui.UserPanelCreator;

public class PlantInsectBuilder implements ContextBuilder<Object> {

	private Context<Object> currentContext;

	public PlantInsectBuilder() {
		try {
			Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass",
					new Class[] { String.class });
			m.setAccessible(true);
			ClassLoader cl = PlantInsectBuilder.class.getClassLoader();
			Object test = m.invoke(cl, "repast.simphony.ui.RSApplication");

			if (test != null) {
				if (!RSApplication.getRSApplicationInstance()
						.hasCustomUserPanelDefined()) {
					RSApplication.getRSApplicationInstance()
							.addCustomUserPanel(ModelUserPanel.getInstance());
				}
			}

		} catch (NoSuchMethodException | SecurityException e) {
			// do nothing
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// do nothing
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// do nothing
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// do nothing
			e.printStackTrace();
		}

		//BatchParamMapFileWriter a;
	}

	@Override
	public Context build(Context<Object> context) {
		context.setId("PlantsInsects");

		ParameterSerializationHelper helper = new ParameterSerializationHelper();
		EnvironmentParams envParams = helper.getEnvironmentParams();
		ArrayList<PlantParams> plantParams = helper.getPlantParams();
		ArrayList<InsectParams> insectParams = helper.getInsectParams();

		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid(
				"grid",
				context,
				new GridBuilderParameters<Object>(new StickyBorders(),
						new RandomGridAdder<Object>(), true, envParams
								.getGridSize(), envParams.getGridSize()));

		/*SeasonHelper.setWinterSeason(false);
		SeasonHelper seasonHelper = new SeasonHelper(envParams.getNumYears(),
				envParams.getPause(), context);
		context.add(seasonHelper);*/

		// sort plantParams by percentage
		Collections.sort(plantParams, new Comparator<PlantParams>() {
			public int compare(PlantParams p1, PlantParams p2) {
				return (int) (p1.getPercentage() * 100 - p2.getPercentage() * 100);
			}
		});

		ArrayList<Plant> plants = new ArrayList<Plant>();
		ArrayList<GridPoint> occupied = new ArrayList<GridPoint>();

		for (int i = 0; i < plantParams.size(); i++) {

			ArrayList<GridPoint> plantPoints;
			if (i == plantParams.size() - 1) {
				plantPoints = getUnoccupied(envParams.getGridSize(), occupied);
			} else {
				plantPoints = getPlantPoints(
						plantParams.get(i).getPercentage(), envParams, occupied);
			}
			occupied.addAll(plantPoints);

			for (GridPoint p : plantPoints) {
				Plant plant = new Plant(plantParams.get(i), context);
				//System.out.println("plant id: "+ plant.getSpeciesParams().getPlantId());
				context.add(plant);
				grid.moveTo(plant, p.getX(), p.getY());
				plants.add(plant);
			}
			context.add(plantParams.get(i));
			plantParams.get(i).setCount(plantPoints.size());
		}
		// System.out.println(plants.size());

		for (InsectParams insParams : insectParams) {
			for (int i = 0; i < insParams.getInitialCount(); i++) {
				/*int eggCount = 0;
				if (insParams.getMaxEggs() > 0)
					eggCount = RandomHelper.nextIntFromTo(
							Math.max(1, insParams.getMaxEggs() / 2),
							insParams.getMaxEggs());*/ // ELD

				//Insect ins = new Insect(insParams, eggCount, context); /// ELD
				Insect ins = new Insect(insParams, context); //ELD
				context.add(ins);
				int x, y;
				double exMean = 10;
				Exponential exDist = RandomHelper.createExponential(exMean);
				switch (insParams.getInitialDist()) {
				case North:
					x = RandomHelper.nextIntFromTo(0,
							envParams.getGridSize() - 1);
					y = (envParams.getGridSize()-1) - exDist.nextInt();/*= RandomHelper.nextIntFromTo(envParams.getGridSize()
							- envParams.getGridSize() / 20,
							envParams.getGridSize() - 1)*/
					grid.moveTo(ins, x, y);
					break;
				case West:
					x = exDist.nextInt();
					//x = RandomHelper.nextIntFromTo(0,
							//envParams.getGridSize() / 20); // needs to be drawn from an exponential dist with mean 0.5 
					y = RandomHelper.nextIntFromTo(0,
							envParams.getGridSize() - 1); // correct 
					grid.moveTo(ins, x, y);
					break;
				case NorthWest:
					x = RandomHelper.nextIntFromTo(0,
							envParams.getGridSize() / 20);
					y = RandomHelper.nextIntFromTo(envParams.getGridSize()
							- envParams.getGridSize() / 20,
							envParams.getGridSize() - 1);
					grid.moveTo(ins, x, y);
					break;
				case East:
					x = (envParams.getGridSize() - 1) - exDist.nextInt();
					//x = RandomHelper.nextIntFromTo(0,
							//envParams.getGridSize() / 20); // needs to be drawn from an exponential dist with mean 0.5 
					y = RandomHelper.nextIntFromTo(0,
							envParams.getGridSize() - 1); // correct 
					grid.moveTo(ins, x, y);
					break;
				default:
					break;
				}
			}
			context.add(insParams);
		}

		currentContext = context;
		return context;
	}

	private ArrayList<GridPoint> getPlantPoints(double plantPerc,
			EnvironmentParams envParams, ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> result = new ArrayList<GridPoint>();

		int plantCoef = (int) (1 / plantPerc);
		int size = Math.round((envParams.getGridSize() * envParams
				.getGridSize()) / plantCoef);

		switch (envParams.getDistribution()) {
		case Random:
			result = getRandomPlantPoints(envParams.getGridSize(), size,
					occupied);
			break;
		case Borders:
			result = getBorderPlantPoints(envParams.getGridSize(), size,
					occupied);
			break;
		case Rows:
			result = getRowPlantPoints(envParams.getGridSize(), plantPerc,
					occupied);
			break;
		case Blocks:
			result = getBlockPlantPoints(envParams.getGridSize(), occupied);
		/*	result = getBlockPlantPoints(envParams.getGridSize(), plantPerc,
					occupied);*/
		default:
			break;
		}

		return result;
	}

	private ArrayList<GridPoint> getBorderPlantPoints(int gridSize,
			int plantCount, ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> result = new ArrayList<GridPoint>();

		for (int i = 0; i < gridSize; i++) {
			// Go through points with same X and Y - the diagonal
			GridPoint point = new GridPoint(i, i);
			if (!occupied.contains(point)) {
				// If the point of the diagonal is not occupied, loop through
				// the rectangle defined by (i,i) and (gridSize-i,gridSize-i),
				// adding 4 points on the 4 sides as you go.
				for (int j = i; j <= gridSize - i; j++) {
					if (j == gridSize)
						continue;
					GridPoint p1 = new GridPoint(i, j);
					GridPoint p2 = new GridPoint(gridSize - 1 - i, gridSize - 1
							- j);
					if (!occupied.contains(p1)) {
						result.add(p1);
						plantCount--;
						//System.out.println("p1: (" + i + "," + j + ")");
					}
					if (!occupied.contains(p2)) {
						result.add(p2);
						plantCount--;
						//System.out.println("p2: (" + (gridSize - 1 - i) + "," + (gridSize - 1 - j) + ")");
					}

					if (j != i) {
						GridPoint p3 = new GridPoint(j, i);
						GridPoint p4 = new GridPoint(gridSize - 1 - j, gridSize
								- 1 - i);
						if (!occupied.contains(p3)) {
							result.add(p3);
							plantCount--;
							//System.out.println("p3: (" + j + "," + i + ")");
						}
						if (!occupied.contains(p4)) {
							result.add(p4);
							plantCount--;
							//System.out.println("p4: (" + (gridSize - 1 - j) + "," + (gridSize - 1 - i) + ")");
						}
					}
				}
			}

			if (plantCount <= 0) {
				break;
			}
		}

		// Remove duplicates. There is probably a better way of determining
		// duplicates when building the points with i and j, should fix later.
		Set<GridPoint> set = new HashSet<GridPoint>(result);
		result = new ArrayList<GridPoint>(set);

		return result;
	}

	private ArrayList<GridPoint> getRowPlantPoints(int gridSize,
			double plantPerc, ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> result = new ArrayList<GridPoint>();

		final int VISUAL_ROW_COUNT = 5; // was 8 eld

		int rowDistance = Math.max(4, gridSize / VISUAL_ROW_COUNT);
		// int plantsPerVisualRow = Math.max(1, plantCount / VISUAL_ROW_COUNT);
		int actualRowsPerVisualRow = (int) Math.round(rowDistance * plantPerc);// (int)
																				// Math.round(Math.max(1.0,
																				// (double)plantsPerVisualRow
																				// /
																				// gridSize));

		int firstRow = 0;

		// Find first unoccupied row
		for (; firstRow < gridSize; firstRow++) {
			GridPoint point = new GridPoint(firstRow, 0);
			if (!occupied.contains(point)) {
				break;
			}
		}

		for (int i = firstRow; i < gridSize; i++) {
			// if (i - firstRow) is multiple of rowDistance, add
			// actualRowsPerVisualRow number of rows
			if ((i - firstRow) % rowDistance == 0) {
				for (int j = 0; j < gridSize; j++) {
					for (int k = i; k < i + actualRowsPerVisualRow; k++) {
						if (k < gridSize) {
							GridPoint point = new GridPoint(k, j);
							result.add(point);
						}
					}
				}
			}
		}

		return result;
	}

	private ArrayList<GridPoint> getRandomPlantPoints(int gridSize,
			int plantCount, ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> randomPoints = new ArrayList<GridPoint>();
		for (int i = 0; i < plantCount; i++) {
			int x = RandomHelper.nextIntFromTo(0, gridSize - 1);
			int y = RandomHelper.nextIntFromTo(0, gridSize - 1);
			GridPoint p = new GridPoint(x, y);
			if (randomPoints.contains(p) || occupied.contains(p)) {
				i--;
			} else {
				randomPoints.add(p);
			}
		}
		return randomPoints;
	}
	
	
	private ArrayList<GridPoint> getBlockPlantPoints(int gridSize,  ArrayList<GridPoint> occupied) {
		//System.out.println("getBlockPlantPoints was called!");
		double patchCount = 25;
		int patchSize = 10;
		ArrayList<GridPoint> blockPlantPoints = new ArrayList<GridPoint>();
		while (patchCount > 0) {	
		  int x = RandomHelper.nextIntFromTo(0, gridSize - 1 - patchSize);
		  int y = RandomHelper.nextIntFromTo(0, gridSize - 1 - patchSize);
		  GridPoint p = new GridPoint(x, y);
		  //System.out.println("Trying: "+x+" "+y);
		  ArrayList<GridPoint> patchPoints = new ArrayList<GridPoint>();
		  patchPoints.add(p);
		  //System.out.println(patchPoints);
		  
		  boolean isGoodPatch = true;
		 for (int j = x ; j < x + patchSize; j++){// for (int j = x + 1 ; j < x + patchSize; j++){
			    if (!isGoodPatch) {break;} 
				for (int k = y; k < y + patchSize; k++){
						GridPoint point = new GridPoint(j,k);
						if(blockPlantPoints.contains(point) || occupied.contains(point)) {
							//System.out.println("No good: "+j + " " +k);
							 isGoodPatch = false;
							 break;
						} else {
							patchPoints.add(point);
						}
				} // k
		  } // j
		  if (isGoodPatch){
			  //System.out.println("Good patch found at: "+x + " " + y);
			  blockPlantPoints.addAll(patchPoints);
			  patchCount--;
		  } else {
			  patchPoints.clear();
		  }
					
		} // for patchCount
		return blockPlantPoints;
	}

	/*private ArrayList<GridPoint> getBlockPlantPoints(int gridSize,
			double plantPerc, ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> result = new ArrayList<GridPoint>();

		int rows = (int) (gridSize * plantPerc);

		for (int i = 0; i < gridSize; i++) {
			GridPoint testPoint = new GridPoint(i, 0);
			if (!occupied.contains(testPoint)) {
				for (int j = 0; j < gridSize / 2; j++) {
					GridPoint p1 = new GridPoint(i, j);
					GridPoint p2 = new GridPoint(gridSize - 1 - i, gridSize - 1
							- j);
					result.add(p1); 
					System.out.println ("p1" + p1);
					result.add(p2);
					System.out.println ("p2" + p2);
				}

				rows--;
				if (rows <= 0)
					break;
			}
		}

		return result;
	}*/

	private ArrayList<GridPoint> getUnoccupied(int gridSize,
			ArrayList<GridPoint> occupied) {
		ArrayList<GridPoint> unoccupied = new ArrayList<GridPoint>();
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				GridPoint point = new GridPoint(i, j);
				if (!occupied.contains(point) && !unoccupied.contains(point)) {
					unoccupied.add(point);
				}
			}
		}
		return unoccupied;
	}

}
