package sfsu.csc780.jied.nutriy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NutritionTableFragment extends Fragment {
	
	private View rootView;
	private DatabaseHandler db;
	
	// nutrition goal values
	private final static String FAT_GOAL = "40", SATURATED_FAT_GOAL = "13";
	private final static String	POLYUNSATURATED_FAT_GOAL = "0";
	private final static String MONOUNSATURATED_FAT_GOAL = "0", CHOLESTEROL_GOAL = "300";
	private final static String SODIUM_GOAL = "2300", POTASSIUM_GOAL = "3500";
	private final static String CARBS_GOAL = "150", FIBER_GOAL = "25";
	private final static String SUGAR_GOAL = "45", PROTEIN_GOAL = "60";
	private final static String VIT_A_GOAL = "100", VIT_C_GOAL = "100";
	private final static String CALCIUM_GOAL = "100", IRON_GOAL = "100";
	
	private final static String[] GOAL_VALUES = new String[] {FAT_GOAL, SATURATED_FAT_GOAL,
		POLYUNSATURATED_FAT_GOAL, MONOUNSATURATED_FAT_GOAL, CHOLESTEROL_GOAL, 
		SODIUM_GOAL, POTASSIUM_GOAL, CARBS_GOAL, FIBER_GOAL, SUGAR_GOAL, PROTEIN_GOAL, 
		VIT_A_GOAL, VIT_C_GOAL, CALCIUM_GOAL, IRON_GOAL};
	private String[] total_values;
	private String[] left_values;
	
	private final static String[] TABLE_COLUMNS = new String[] { "totalFat", "saturatedFat", 
		"polyunsaturatedFat", "monounsaturatedFat", "cholesterol", 
		"sodium", "potassium", "totalCarbs", "fiber", "sugar", "protein", "vit_A", "vit_C", 
		"calcium", "iron" };
	
	public final static HashMap<String, String> NUTRITION_GOAL_TABLE;
	public final static HashMap<Integer, String> GOAL_MAP;
	public final HashMap<Integer, String> TOTAL_MAP = new HashMap<Integer, String> ();
	public final HashMap<Integer, String> LEFT_MAP = new HashMap<Integer, String> ();
	
	static {
		NUTRITION_GOAL_TABLE = new HashMap<String, String> ();
		NUTRITION_GOAL_TABLE.put("Calories", "1200");
		NUTRITION_GOAL_TABLE.put("Total fat", "40");
		NUTRITION_GOAL_TABLE.put("Saturated fat", "13");
		NUTRITION_GOAL_TABLE.put("Trans fat", "0");
		NUTRITION_GOAL_TABLE.put("Cholesterol", "300");
		NUTRITION_GOAL_TABLE.put("Sodium", "2300");
		NUTRITION_GOAL_TABLE.put("Potassium", "3500");
		NUTRITION_GOAL_TABLE.put("Total carbs", "150");
		NUTRITION_GOAL_TABLE.put("Dietary fiber", "25");
		NUTRITION_GOAL_TABLE.put("Sugars", "45");
		NUTRITION_GOAL_TABLE.put("Protein", "60");
		NUTRITION_GOAL_TABLE.put("Vit_A", "100%");
		NUTRITION_GOAL_TABLE.put("Vit_C", "100%");
		NUTRITION_GOAL_TABLE.put("Calcium", "100%");
		NUTRITION_GOAL_TABLE.put("Iron", "100%");
		
		GOAL_MAP = new HashMap<Integer, String> ();
		GOAL_MAP.put(R.id.fat_goal, FAT_GOAL);
		GOAL_MAP.put(R.id.saturated_fat_goal, SATURATED_FAT_GOAL);
		GOAL_MAP.put(R.id.polyunsaturated_fat_goal, POLYUNSATURATED_FAT_GOAL);
		GOAL_MAP.put(R.id.monounsaturated_fat_goal, MONOUNSATURATED_FAT_GOAL);
		GOAL_MAP.put(R.id.cholesterol_goal, CHOLESTEROL_GOAL);
		GOAL_MAP.put(R.id.sodium_goal, SODIUM_GOAL);
		GOAL_MAP.put(R.id.potassium_goal, POTASSIUM_GOAL);
		GOAL_MAP.put(R.id.carbs_goal, CARBS_GOAL);
		GOAL_MAP.put(R.id.fiber_goal, FIBER_GOAL);
		GOAL_MAP.put(R.id.sugar_goal, SUGAR_GOAL);
		GOAL_MAP.put(R.id.protein_goal, PROTEIN_GOAL);
		GOAL_MAP.put(R.id.vit_A_goal, VIT_A_GOAL);
		GOAL_MAP.put(R.id.vit_C_goal, VIT_C_GOAL);
		GOAL_MAP.put(R.id.calcium_goal, CALCIUM_GOAL);
		GOAL_MAP.put(R.id.iron_goal, IRON_GOAL);
	} 
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	rootView = inflater.inflate(R.layout.fragment_nutrition_table, container, false);      
    	return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	init();   	
    }
    
    private void init() {
    	initObjects();
    	initializeView();
    }
    
    private void initObjects() {
    	db = ((MainActivity)getActivity()).getDb();
    	List<DiaryItem> itemList = db.getAllDiaryItems();
    	List<Nutrition> nutritionList = new ArrayList<Nutrition> ();
    	for (DiaryItem item: itemList) {
    		nutritionList.add(db.serachForNutrition(item));
    	}
    	initTotal(nutritionList);
    	initLeft();
    }
    
    public void initializeView() {
    	View rootView = getView();
    	TextView tv;
    	if (rootView != null) {
    		initViewSet(GOAL_MAP);
    		initViewSet(TOTAL_MAP);
    		initViewSet(LEFT_MAP);
    		//addPercentage();
    		//checkExcess();
    	}
    }
    
    private void initViewSet(HashMap<Integer, String> map) {
    	TextView tv;
    	for (int id: map.keySet()) {
			tv = (TextView) rootView.findViewById(id);
			tv.setText(map.get(id));
		}
    }
    
    private void initTotal(List<Nutrition> list) {
    	total_values = new String[] {"0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0", 
    			"0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0"};
    	String value = "0.0";
    	int i = 0;
    	for (String column: TABLE_COLUMNS) {  
    		String total = "0.0";
    		for (Nutrition nutrition: list) {
    			value = nutrition.getValue(column);
    			total = add(value, total);
    		}
    		total_values[i++] = total;
    	}
    	initTotalMap();
    }
    
    private void initLeft() {
    	left_values = new String[] {"0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0", 
    			"0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0"};
    	String goal = "0.0", total = "0.0", left = "0.0";
    	for (int i = 0; i < left_values.length; i++) {
    		goal = GOAL_VALUES[i];
    		total = total_values[i];
    		left_values[i] = subtract(total, goal);
    	}
    	initLeftMap();
    }
    
    private String subtract(String value, String total) {
    	return calculate(value, total, false);
    }
    
    private String add(String value, String total) {
    	return calculate(value, total, true);
    }
    
    private String calculate(String value1, String value2, Boolean b) {
    	double dValue1 = Double.valueOf(value1);
    	double dValue2 = Double.valueOf(value2);
    	if (b) dValue2 += dValue1;
    	else dValue2 -= dValue1;
    	int iValue = (int) dValue2;
    	return String.valueOf(iValue);
    }
    
    private void initTotalMap() {
    	GOAL_MAP.put(R.id.fat_total, total_values[0]);
		GOAL_MAP.put(R.id.saturated_fat_total, total_values[1]);
		GOAL_MAP.put(R.id.polyunsaturated_fat_total, total_values[2]);
		GOAL_MAP.put(R.id.monounsaturated_fat_total, total_values[3]);
		GOAL_MAP.put(R.id.cholesterol_total, total_values[4]);
		GOAL_MAP.put(R.id.sodium_total, total_values[5]);
		GOAL_MAP.put(R.id.potassium_total, total_values[6]);
		GOAL_MAP.put(R.id.carbs_total, total_values[7]);
		GOAL_MAP.put(R.id.fiber_total, total_values[8]);
		GOAL_MAP.put(R.id.sugar_total, total_values[9]);
		GOAL_MAP.put(R.id.protein_total, total_values[10]);
		GOAL_MAP.put(R.id.vit_A_total, total_values[11]);
		GOAL_MAP.put(R.id.vit_C_total, total_values[12]);
		GOAL_MAP.put(R.id.calcium_total, total_values[13]);
		GOAL_MAP.put(R.id.iron_total, total_values[14]);
    }
    
    private void initLeftMap() {
    	GOAL_MAP.put(R.id.fat_left, left_values[0]);
		GOAL_MAP.put(R.id.saturated_fat_left, left_values[1]);
		GOAL_MAP.put(R.id.polyunsaturated_fat_left, left_values[2]);
		GOAL_MAP.put(R.id.monounsaturated_fat_left, left_values[3]);
		GOAL_MAP.put(R.id.cholesterol_left, left_values[4]);
		GOAL_MAP.put(R.id.sodium_left, left_values[5]);
		GOAL_MAP.put(R.id.potassium_left, left_values[6]);
		GOAL_MAP.put(R.id.carbs_left, left_values[7]);
		GOAL_MAP.put(R.id.fiber_left, left_values[8]);
		GOAL_MAP.put(R.id.sugar_left, left_values[9]);
		GOAL_MAP.put(R.id.protein_left, left_values[10]);
		GOAL_MAP.put(R.id.vit_A_left, left_values[11]);
		GOAL_MAP.put(R.id.vit_C_left, left_values[12]);
		GOAL_MAP.put(R.id.calcium_left, left_values[13]);
		GOAL_MAP.put(R.id.iron_left, left_values[14]);
    }

}