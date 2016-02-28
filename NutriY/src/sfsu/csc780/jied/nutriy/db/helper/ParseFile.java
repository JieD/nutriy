package sfsu.csc780.jied.nutriy.db.helper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.content.Context;
import android.util.Log;

public class ParseFile {
	
	private final static String FILE_NAME = "nutrition_db.txt";
	private Context context;
	private List<Nutrition> nutritionList;
	
	public ParseFile(Context context) {
		this.context = context;
		this.nutritionList = new ArrayList<Nutrition>();
	}
	
	public List<Nutrition> getNutritionList() {
		readFile(openFile(FILE_NAME));
		return nutritionList;
	}
	
	private Scanner openFile(String fileName) {
		Scanner reader = null;
	      try {
	    	 InputStream is = context.getAssets().open(fileName);
	         reader = new Scanner(is);
	      } catch (Exception e) {
	         Log.d("IO Error", "Fail to open " + FILE_NAME);
	      }
         return reader;
	}
	
	private void readFile(Scanner in) {
		assert in != null;
		while (in.hasNextLine()) {
			nutritionList.add(new Nutrition(in.nextLine()));			
	    }      
	    in.close();
	}
}