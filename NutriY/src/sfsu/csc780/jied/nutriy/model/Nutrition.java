package sfsu.csc780.jied.nutriy.model;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;

public class Nutrition {
	
	private int id;
	
	public static final int NUTRITION_CATEGORY_NUM = 19;
	public static final String[] nutritionTitleArray = {"name", "weight", "servingSize", "calorie", 
		"totalFat", "saturatedFat", "polyunsaturatedFat", "monounsaturatedFat", "cholesterol", 
		"sodium", "potassium", "totalCarbs", "fiber", "sugar", "protein", "vit_A", "vit_C", 
		"calcium", "iron"};
	
	private final static String FIELD_DELIMITER = "\\^";
	private final static String STRING_DELIMITER = "\\~";
	
	private HashMap<String, String> nutritionMap = new HashMap<String, String>(NUTRITION_CATEGORY_NUM);
		
	// Empty constructor
    public Nutrition(){}
	
	public Nutrition(String fileLine) {
		String[] nutritionValueArray = split(fileLine);
		initMap(nutritionValueArray);
	}
	
	// dbNutritionValue is an array without record ID
	public Nutrition(int id, String[] dbNutritionValue) {
		this.id = id;
		initMap(dbNutritionValue);				
	}
	
	private String[] split(String in) {
		return in.split(FIELD_DELIMITER);
	}
	
	private Map<String, String> initMap(String[] nutritionValueArray) { 
		assert nutritionValueArray.length == NUTRITION_CATEGORY_NUM;
		for (int i = 0; i < nutritionValueArray.length; i++) {
			nutritionMap.put(nutritionTitleArray[i], cleanString(nutritionValueArray[i]));
		}
		//log(values);
		return nutritionMap;
	}
	
	/**
	 * ensure no empty value
	 * if real string value, need to remove "~"
	 *  
	 * @param oldStr
	 * @return newStr - cleaned up string
	 */
	private String cleanString(String oldStr) {
		assert !oldStr.isEmpty();
		String newStr = oldStr;
		if (!DatabaseHandler.isNumeric(oldStr)) {
			newStr = oldStr.replaceAll(STRING_DELIMITER, "");			
		}
		return newStr;
	}
	
	public static void log(Map<String, String> values) {
		String key = "";
		for (int i = 0; i < NUTRITION_CATEGORY_NUM; i++) {
			key = nutritionTitleArray[i];
			Log.d(key, values.get(key));
		}	
	}
	
	// if value is integer, convert to double
	public void enableDouble() {
		String value;
		for (String key: nutritionTitleArray) {
			value = nutritionMap.get(key);
			if (DatabaseHandler.isNumeric(value) && isInt(value)) {
				value = value + ".0";
				nutritionMap.put(key, value);
			}
		}
	}
	
	private Boolean isInt(String str) {
		Boolean result = false;
		result = str.matches("(^\\.)+");
		return result;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue(String title) {
		return nutritionMap.get(title);
	}

	public void setValue(String title, String newValue) {
		nutritionMap.put(title, newValue);
	}

	public HashMap<String, String> getNutritionMap() {
		return nutritionMap;
	}

	public void setNutritionMap(HashMap<String, String> values) {
		this.nutritionMap = values;
	}	
}