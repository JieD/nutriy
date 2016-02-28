package sfsu.csc780.jied.nutriy.model;

public class FoodDiary {
	
	private int id;
	
	public FoodDiary() {
	}
	
	// dbNutritionValue is an array without record ID
	public FoodDiary(int id) {
		this.id = id;			
	}
}