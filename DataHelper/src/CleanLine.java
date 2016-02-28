import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 
 * @author jied
 * 
 * Recommended Daily Intakes (Male & Female):
 * VitaminA - 900mcg 700mcg (1000 Microgram = 1 Milligram)
 * VitaminC - 90mg 75mg (1000 Milligram = 1 gram)
 * Calcium - 1000mg 1000mg
 * Iron - 8mg 18mg
 */

public class CleanLine {
	
	private String[] rawLine, cleanLine;
	private final static String FIELD_DELIMITER = "\\^";
	public final static int NUTRITION_COLUMN_NUM = 19;
	private final static LinkedHashMap<String, Integer> NUTRITION_INDEX_MAP; // use LinkedHashMap to ensure the sequence of extracting data
	private final static HashMap<String, Integer> METADATA_INDEX_MAP;
	private final static HashMap<String, Integer> REC_INTAKE_MAP;
	private String sSize = "";
	private String weight = "0.0";
	private double scale = 1.0;
	private final static double DEFAULT_WEIGHT = 100.0;
	private DecimalFormat df;
	private Boolean isEmptySSize = false;
	
	static {
		// map nutrition category to its position in the array 
		NUTRITION_INDEX_MAP = new LinkedHashMap<String, Integer>();
		NUTRITION_INDEX_MAP.put("Calories", 3);
		NUTRITION_INDEX_MAP.put("Total fat", 5);
		NUTRITION_INDEX_MAP.put("Saturated fat", 44);
		NUTRITION_INDEX_MAP.put("Polyunsaturated fat", 46);
		NUTRITION_INDEX_MAP.put("Monounsaturated fat", 45);
		//INDEX_MAP.put("Trans fat", ??); not available from UADA Database
		NUTRITION_INDEX_MAP.put("Cholesterol", 47);
		NUTRITION_INDEX_MAP.put("Sodium", 15);
		NUTRITION_INDEX_MAP.put("Potassium", 14);
		NUTRITION_INDEX_MAP.put("Total Carbs", 7);
		NUTRITION_INDEX_MAP.put("Dietary fiber", 8);
		NUTRITION_INDEX_MAP.put("Sugars", 9);
		NUTRITION_INDEX_MAP.put("Protein", 4);
		NUTRITION_INDEX_MAP.put("Vit_A", 33); // in microgram - µg
		NUTRITION_INDEX_MAP.put("Vit_C", 20); // in milligram - mg
		NUTRITION_INDEX_MAP.put("Calcium", 10);
		NUTRITION_INDEX_MAP.put("Iron", 11);
		
		METADATA_INDEX_MAP = new HashMap<String, Integer> ();
		METADATA_INDEX_MAP.put("Name", 1);
		METADATA_INDEX_MAP.put("Weight1", 48);
		METADATA_INDEX_MAP.put("ServingSize1", 49);
		METADATA_INDEX_MAP.put("Weight2", 50);
		METADATA_INDEX_MAP.put("ServingSize2", 51);
				
		// map mineral category to its recommended daily intakes value
		REC_INTAKE_MAP = new HashMap<String, Integer> ();
		REC_INTAKE_MAP.put("Vit_A", 700);
		REC_INTAKE_MAP.put("Vit_C", 75);
		REC_INTAKE_MAP.put("Calcium", 1000);
		REC_INTAKE_MAP.put("Iron", 18);
    }
	
	public CleanLine() {
		cleanLine = new String[NUTRITION_COLUMN_NUM];
		df = new DecimalFormat("#.#");
	}
	
	/**
	 * 
	 * @param in - input line
	 * @return a string containing necessary nutrition data
	 */
	public String clean(String in) {
		String[] raw = split(in);
		chooseSSize(raw);
		String result = "";
		if (!isEmptySSize) {
			fillMetaData(raw);
			calculateNutritionData(raw);		
			for (String item : cleanLine) {
				result += item + "^";
			}
		}		
		return result;
	}
	
	public String[] split(String in) {
		return in.split(FIELD_DELIMITER);
	}
	
	private void chooseSSize(String[] in) {		
		String sSize1 = in[METADATA_INDEX_MAP.get("ServingSize1")];
		String sSize2 = in[METADATA_INDEX_MAP.get("ServingSize2")];	
		if (sSize1.matches("~~") && sSize2.matches("~~")) isEmptySSize = true;
		else {
			isEmptySSize = false;
			if (sSize2.matches("~~")) { // no ServingSize2
				sSize = sSize1;
				weight = in[METADATA_INDEX_MAP.get("Weight1")];			
			} else {
				sSize = sSize2;
				weight = in[METADATA_INDEX_MAP.get("Weight2")];
			}	
			scale = Double.valueOf(weight) / DEFAULT_WEIGHT;
		}		
	}
	
	// only extract necessary nutrition data
	public void calculateNutritionData(String[] in) {	
		int index = 3;
		String data = "";
		for(Integer position: NUTRITION_INDEX_MAP.values()) {
			data = in[position];
			if (data.equals("")) data = "0.0"; // if data is empty, put default value 0.0
			else {
				if (scale != 1.0) {
					data = String.valueOf(Double.valueOf(data) * scale);
				}				
				// if data for minerals, need to convert to percentage data
				data = convertMineralToPercentage(position, data);
			}				
			//System.out.println();
			cleanLine[index++] = round(data); // round to only one digit after decimal
		} 
	}
	
	// add name, weight and servingSize
	private void fillMetaData(String[] in) {
		cleanLine[0] = in[METADATA_INDEX_MAP.get("Name")];
		cleanLine[1] = round(weight);
		cleanLine[2] = sSize;
	}
	
	private String convertMineralToPercentage(int position, String data) {
		final String[] minerals = {"Vit_A", "Vit_C", "Calcium", "Iron"};
		for (String mineral: minerals) {
			if (position == NUTRITION_INDEX_MAP.get(mineral)) {
				Double value = Double.valueOf(data) * 100.0 / REC_INTAKE_MAP.get(mineral);  
				//value = Double.valueOf(df.format(value));
				data = String.valueOf((value));
				return data;
			}
		}		
		return data;
	}
	
	// round the data to keep only one digit after decimal
	private String round(String data) {
		if (isNumeric(data)) {
			Double value = Double.valueOf(data);
			value = Double.valueOf(df.format(value));
			return String.valueOf((value));
		}
		return data;
	}
	
	private static boolean isNumeric(String str)
	{
	  return str.matches("\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	private static void log(Object aMsg) {
	    System.out.println(String.valueOf(aMsg));
    }

	public static void main(String[] args) {
		String sample1 = "~16200~^~CAMPBELL'S BRN SUGAR&BACON FLAV BKD BNS~^69.40^123^3.85^1.92^1.75^23.08^6.2^10.00^31^1.11^^^^362^^^^^0.0^^^^^^^^^^^^0^^^^^^^^^^^^0.385^^^4^130^~.5 cup~^130^~1 serving~^0";
		String sample2 = "~16210~^~VITASOY USA,NASOYA LITE FIRM TOFU~^87.10^54^8.30^1.70^1.60^1.30^0.6^0.20^184^1.60^^181^^34^^^^^^^^^^^^^^^^2.36^1902^95^^^^^^^5.22^3.9^157^^0.200^0.300^1.200^0^79^~.2 package~^^~~^0";
		String sample3 = "~17226~^~LAMB,DOM,COMP OF RTL CUTS,LN&FAT,1/8\"FAT,CHOIC,RAW~^63.17^243^17.54^18.66^0.92^0.00^0.0^^12^1.62^22^166^239^59^3.52^0.108^0.020^19.7^0.0^0.120^0.220^6.070^0.680^0.140^19^0^19^19^^2.44^0^0^0^^^^^^^^^^8.070^7.650^1.480^70^28.35^~1 oz~^453.6^~1 lb~^24";
		CleanLine test = new CleanLine();
		/** String[] array = test.split(sample1);
		System.out.println(Arrays.toString(array));		
		System.out.println(test.clean(sample1));
		System.out.println(Arrays.toString(test.cleanLine)); */
		String value = "0.0";
		test.log(Double.valueOf(value));
		
	}

}
