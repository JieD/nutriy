package sfsu.csc780.jied.nutriy.model;

public class DiaryItem {
	
	private int id;
	private String name;
	private Double numberOfServings;
	private int calorie;
	private String category;
	
	public DiaryItem() {}
	
	public DiaryItem(String name, double numberOfServings, int calorie, String category) {
		this.name = name;
		this.numberOfServings = numberOfServings;
		this.calorie = calorie;
		this.category = category;
	}
	
	public DiaryItem(int id, String Name, double numberOfServings, int calorie, String category) {
		this(Name, numberOfServings, calorie, category);
		this.id = id;
	}

	public int getCalorie() {
		return calorie;
	}

	public void setCalorie(int calorie) {
		this.calorie = calorie;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public Double getNumberOfServings() {
		return numberOfServings;
	}

	public void setNumberOfServings(Double numberOfServings) {
		this.numberOfServings = numberOfServings;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
}