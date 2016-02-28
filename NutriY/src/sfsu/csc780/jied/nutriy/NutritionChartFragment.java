package sfsu.csc780.jied.nutriy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NutritionChartFragment extends Fragment {
	
	private View rootView;
	private DatabaseHandler db;
	private double calorie_total;
	private int carbs_percentage, fat_percentage, protein_percentage;
	private final static String[] COLUMNS = new String[] { "totalCarbs", "totalFat", "protein"};
	private final static HashMap<String, Double> FACTOR_MAP;
	
	static {
		FACTOR_MAP = new HashMap<String, Double> ();
		FACTOR_MAP.put(COLUMNS[0], 4.1);
		FACTOR_MAP.put(COLUMNS[1], 8.8);
		FACTOR_MAP.put(COLUMNS[2], 4.1);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_nutrition_chart, container, false); 
        
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	init();   	
    }
    
    private void init() {
    	//initObjects();
    	initView();
    }
    
    private void initObjects() {
    	List<DiaryItem> itemList = db.getAllDiaryItems();
    	List<Nutrition> nutritionList = new ArrayList<Nutrition> ();
    	for (DiaryItem item: itemList) {
    		nutritionList.add(db.serachForNutrition(item));
    	}
    	initTotal(nutritionList);
    	calorie_total = (double) getTotalCalorie(itemList);    	
    }
    
    private void initTotal(List<Nutrition> list) {
    	double value = 0.0, total = 0.0;
    	double[] valueArray = new double[] {0.0, 0.0, 0.0};
    	int index = 0;
    	for (String column: COLUMNS) {    		
        	for (Nutrition nutrition: list) {
        		value = Double.valueOf(nutrition.getValue(column));
        		total += value;
        	}
        	total *= FACTOR_MAP.get(column);
        	valueArray[index++] = total;
    	}
    	
    	carbs_percentage = (int) (valueArray[0] * 100.0 / calorie_total);
    	fat_percentage = (int) (valueArray[1] * 100.0 / calorie_total);
    	protein_percentage = (int) (valueArray[2] * 100.0/ calorie_total);
    }
    
    private void initView() {    	
    	View rootView = getView();
    	
    	// dynamically change Pie slice
    	final PieChart pie = (PieChart) rootView.findViewById(R.id.piechart);
    	pie.setShowChart(true);  	
    	pie.setPercentage(carbs_percentage, fat_percentage, protein_percentage);
    	
    	TextView tv = (TextView) rootView.findViewById(R.id.carb_percentage);
    	tv.setText(String.valueOf(carbs_percentage));
    	tv = (TextView) rootView.findViewById(R.id.fat_percentage);
    	tv.setText(String.valueOf(fat_percentage));
    	tv = (TextView) rootView.findViewById(R.id.protein_percentage);
    	tv.setText(String.valueOf(protein_percentage));
    }
    
    
    
    private int getTotalCalorie(List<DiaryItem> itemList) {
		int cTotal = 0, calorie;
		for (DiaryItem item: itemList) {
			cTotal += item.getCalorie();
		}
		return cTotal;
	}
}