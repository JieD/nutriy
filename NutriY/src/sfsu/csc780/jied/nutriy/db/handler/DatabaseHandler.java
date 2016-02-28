package sfsu.csc780.jied.nutriy.db.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.model.DiaryItem;
import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    public static final String NUTRITION_DATABASE = "NutritionsManager";
 
    // Table names
    public static final String TABLE_NUTRITIONS = "nutritions";
    public static final String TABLE_DIARY_ITEMS = "diary_items";
    
    // Common column names
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
 
    // Nutritions Table Columns names
    public static final String[] COLUMN_NAME;
    
    // Nutritions Table Columns and its corresponding types
    public static final HashMap<String, String> COLUMN_TYPE_MAP;
    
    // Diary_Items Table - column names
    private static final String KEY_NUMBER_OF_SERVINGS = "numberOfServings";
    private static final String KEY_CALORIE  = "calorie";
    private static final String KEY_CATEGORY = "category";
      
    // public static final String INTEGER_TYPE = "INTEGER";
    public static final String STRING_TYPE = "TEXT";
    public static final String DOUBLE_TYPE = "REAL";
    
    // Table Create Statements
    // Diary_Items table create statement
    private static final String CREATE_TABLE_DIARY_ITEMS = "CREATE TABLE "
            + TABLE_DIARY_ITEMS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_NUMBER_OF_SERVINGS + " REAL," + KEY_CALORIE
            + " INTEGER," + KEY_CATEGORY + " TEXT" + ")";
    
    static {
    	String[] oldArray = Nutrition.nutritionTitleArray;
    	COLUMN_NAME = new String[oldArray.length + 1];
    	COLUMN_NAME[0] = KEY_ID;
	    System.arraycopy(oldArray, 0, COLUMN_NAME, 1, oldArray.length);
	    
	    COLUMN_TYPE_MAP = new HashMap<String, String>();
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[0], "INTEGER PRIMARY KEY");
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[1], STRING_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[2], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[3], STRING_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[4], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[5], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[6], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[7], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[8], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[9], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[10], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[11], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[12], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[13], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[14], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[15], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[16], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[17], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[18], DOUBLE_TYPE);
	    COLUMN_TYPE_MAP.put(COLUMN_NAME[19], DOUBLE_TYPE);
    }
    
	public DatabaseHandler(Context context) {
		super(context, NUTRITION_DATABASE, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_NUTRITIONS_TABLE = "CREATE TABLE " + TABLE_NUTRITIONS + "(" + constructCreateQueryBody() + ")";
        Log.d("DBHandler", "onCreate");
		db.execSQL(CREATE_NUTRITIONS_TABLE);	
        db.execSQL(CREATE_TABLE_DIARY_ITEMS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRITIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY_ITEMS);
        
        // Create tables again
        onCreate(db);
	}
	
	// Adding new nutrition
	public void addNutrition(Nutrition nutrition) {
	    SQLiteDatabase db = this.getWritableDatabase();	    
	    ContentValues values = new ContentValues();
	    
	    String[] keys = Nutrition.nutritionTitleArray;
	    String key = "", value = "";
	    Double dValue = 0.0;
	    
	    for (int i = 0; i < Nutrition.NUTRITION_CATEGORY_NUM; i++) {
	    	key = keys[i];
	    	value = nutrition.getValue(key);
	    	if (isNumeric(value)) { // convert to double if necessary
	    		dValue = Double.valueOf(value);
	    		values.put(key, dValue);
	    	} else {
	    		values.put(key, value);
	    	}    		
    	}
	 
	    // Inserting Row
	    db.insert(TABLE_NUTRITIONS, null, values);
	    db.close(); // Closing database connection
	}
	
	// Bulk inserting new nutrition, using Transaction & SQLiteStatement
	public void addNutritionList(List<Nutrition> nutritionList) {		
		String sql = constructInsertQuery();
	    SQLiteDatabase db = this.getWritableDatabase();
	    
	    try
	    {
	      db.beginTransaction();
	      SQLiteStatement stmt = db.compileStatement(sql);
	      
	      String[] keys = Nutrition.nutritionTitleArray;
		  String key = "", value = "";
		  Double dValue = 0.0;
	      for (int i = 0; i < nutritionList.size(); i++) {
	    	  Nutrition nutrition = nutritionList.get(i); 	
	  	      for (int j = 0; j < Nutrition.NUTRITION_CATEGORY_NUM; j++) {
	  	    	key = keys[j];
	  	    	value = nutrition.getValue(key);;
	  	    	if (isNumeric(value)) { // convert to double if necessary
		    		dValue = Double.valueOf(value);
		    		stmt.bindDouble(j + 1, dValue);
		    	} else {
		    		stmt.bindString(j + 1, value);
		    	}  	    	
	      	 }	
	  	     stmt.execute();
		     stmt.clearBindings();
	      }
	      db.setTransactionSuccessful();
	    }
	    catch (SQLException e) {}
	    finally
	    {
	      db.endTransaction();
	    }
	    close();
	}	
	
	// Getting single nutrition
	public Nutrition getNutrition(int id) {
	    SQLiteDatabase db = this.getReadableDatabase();
	    Nutrition nutrition = new Nutrition();
	    Cursor cursor = db.query(TABLE_NUTRITIONS, COLUMN_NAME, COLUMN_NAME[0] + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if( cursor != null && cursor.moveToFirst() ) {
	    	parseDbRecord(nutrition, cursor);
	    }
	    cursor.close();	    
	    // return nutrition
	    return nutrition;
	}
	
	private void parseDbRecord(Nutrition nutrition, Cursor cursor) {
		nutrition.setId(Integer.parseInt(cursor.getString(0)));
    	for (int index = 1; index < COLUMN_NAME.length; index++) {
        	nutrition.setValue(COLUMN_NAME[index], cursor.getString(index));
        }
	}
	
	// Getting All Nutritions
	 public List<Nutrition> getAllNutritions() {
	    List<Nutrition> nutritionList = new ArrayList<Nutrition>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_NUTRITIONS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor != null && cursor.moveToFirst()) {
	        do {
	            Nutrition nutrition = new Nutrition();
	            parseDbRecord(nutrition, cursor);
	            // Adding nutrition to list
	            nutritionList.add(nutrition);
	        } while (cursor.moveToNext());
	    }
	 
	    // return nutrition list
	    return nutritionList;
	}
	 
	// Getting Nutritions Count
    public int getNutritionsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NUTRITIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
    }
    
    // return cursor to rows with similar name
    public Cursor searchNutritionsByName(String filter) {
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(true, TABLE_NUTRITIONS, new String[] { COLUMN_NAME[0], COLUMN_NAME[1] }, 
    			COLUMN_NAME[1] + " LIKE ?",
	            new String[] {"%"+ filter+ "%" }, null, null, null, null);
    	return cursor;
    	/** looping through all rows and adding to list
	    if (cursor != null && cursor.moveToFirst()) {
	        do {
	            Nutrition nutrition = new Nutrition();
	            parseDbRecord(nutrition, cursor);
	            // Adding nutrition to list
	            nutritionList.add(nutrition);
	        } while (cursor.moveToNext());
	    } */  	
    }
    
    
    public int searchForNutritionID(String name) {
    	int id = 0;
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(true, TABLE_NUTRITIONS, COLUMN_NAME, COLUMN_NAME[1] + " =?",
	            new String[] { name }, null, null, null, null);
    	if (cursor != null && cursor.moveToFirst()) {
    		id = Integer.valueOf(cursor.getString(0));
    	}
    	return id;
    }
    
    public Nutrition searchForNutrition(String name) {
    	return getNutrition(searchForNutritionID(name));
    }
    
    public Nutrition serachForNutrition(DiaryItem item) {
    	return searchForNutrition(item.getName());
    }

	private String constructCreateQueryBody() {
		String body = "";
		for (String column: COLUMN_NAME) {
			body += column  + " " + COLUMN_TYPE_MAP.get(column)  + ",";
		}
		return removeLast(body, 1);
	}
	
	private String constructInsertQuery() {
		return "INSERT INTO " + TABLE_NUTRITIONS + " (" + getColumnString() + ") " 
				+ "VALUES (" + getEmptyValueString() + ");";
	}
	
	private String getColumnString() {
		String columns = "";
		for (String column: Nutrition.nutritionTitleArray) {
			columns += column + ", ";
		}
		return removeLast(columns, 2);
	}
	
	private String getEmptyValueString() {
		String emptyValue = "";
		for (int i = 0; i < Nutrition.NUTRITION_CATEGORY_NUM; i++) {
			emptyValue += "?, ";
		}
		return removeLast(emptyValue, 2);
	}
	
	private String removeLast(String oldStr, int length) {
		return oldStr.substring(0, oldStr.length() - length);
	}
	
	private Object changeFormat(String str) {
		return isNumeric(str)? Float.valueOf(str) : str;
	}

	public static boolean isNumeric(String str)
	{
	  return str.matches("\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	
	public void addDiaryItem(DiaryItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(KEY_NAME, item.getName());
		values.put(KEY_NUMBER_OF_SERVINGS, item.getNumberOfServings());
		values.put(KEY_CALORIE, item.getCalorie());
		values.put(KEY_CATEGORY, item.getCategory());
		db.insert(TABLE_DIARY_ITEMS, null, values);
		db.close();
	}
	
	public DiaryItem getDiaryItem(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		DiaryItem item = new DiaryItem();
	    Cursor cursor = db.query(TABLE_DIARY_ITEMS, new String[] {KEY_ID, KEY_NAME,  
	    		KEY_NUMBER_OF_SERVINGS, KEY_CALORIE, KEY_CATEGORY}, 
	    		KEY_ID + "=?", 
	    		new String[] { String.valueOf(id) }, 
	    		null, null, null, null);
	    if( cursor != null && cursor.moveToFirst() ) {
	    	item = new DiaryItem(Integer.parseInt(cursor.getString(0)),
	                cursor.getString(1), cursor.getDouble(2), 
	                Integer.parseInt(cursor.getString(3)), cursor.getString(4));
	    }
	    cursor.close();	    
	    // return nutrition
	    return item;
	}
	
	// return cursor to rows with similar name
    public List<DiaryItem> searchDiaryItemByCategoty(String category) {
    	List<DiaryItem> itemList = new ArrayList<DiaryItem> ();
    	SQLiteDatabase db = this.getReadableDatabase();
    	Cursor cursor = db.query(TABLE_DIARY_ITEMS, new String[] { KEY_ID, KEY_NAME, KEY_NUMBER_OF_SERVINGS,
    			KEY_CALORIE, KEY_CATEGORY }, KEY_CATEGORY + "=?", new String[] { category }, 
    			null, null, null, null);
    	// looping through all rows and adding to list
	    if (cursor != null && cursor.moveToFirst()) {
	    	do {
	        	DiaryItem item = new DiaryItem();
	        	item.setId(Integer.parseInt(cursor.getString(0)));
	        	item.setName(cursor.getString(1));
	        	item.setNumberOfServings(cursor.getDouble(2));
	        	item.setCalorie(Integer.parseInt(cursor.getString(3)));
	        	item.setCategory(cursor.getString(4));
	            // Adding item to list
	            itemList.add(item);
	        } while (cursor.moveToNext());
	    } 
	    return itemList;
    }
	
	// Getting All DiaryItems
	public List<DiaryItem> getAllDiaryItems() {
		List<DiaryItem> itemList = new ArrayList<DiaryItem>();
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_DIARY_ITEMS;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	DiaryItem item = new DiaryItem();
	        	item.setId(Integer.parseInt(cursor.getString(0)));
	        	item.setName(cursor.getString(1));
	        	item.setNumberOfServings(cursor.getDouble(2));
	        	item.setCalorie(Integer.parseInt(cursor.getString(3)));
	        	item.setCategory(cursor.getString(4));
	            // Adding item to list
	            itemList.add(item);
	        } while (cursor.moveToNext());
	    }
	 
	    // return item list
	    return itemList;
	}
	 
	// Getting DiaryItems Count
	public int getDiaryItemsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_DIARY_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        //cursor.close();
 
        // return count
        return cursor.getCount();
	}
	
	// Updating single DiaryItem
	public int updateDiaryItem(DiaryItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NAME, item.getName());
	    values.put(KEY_NUMBER_OF_SERVINGS, item.getNumberOfServings());
	    values.put(KEY_CALORIE, item.getCalorie());
	    values.put(KEY_CATEGORY, item.getCategory());
	    
	    // updating row
	    return db.update(TABLE_DIARY_ITEMS, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(item.getId()) });
	}
	 
	// Deleting single DiaryItem
	public void deleteDiaryItem(DiaryItem item) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_DIARY_ITEMS, KEY_ID + " =?", 
				new String[] { String.valueOf(item.getId()) });
		db.close();
	}
	
	public boolean isItemExisted(String name, String category) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_DIARY_ITEMS, new String[] { KEY_ID }, 
				KEY_NAME + "=? and " + KEY_CATEGORY + "=? ", new String[] { name, category }, 
    			null, null, null, null);
		return cursor.getCount() != 0;
	}

	// closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
	
}
