package sfsu.csc780.jied.nutriy;

import java.util.List;

import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class HomePageFragment extends Fragment {
	
	private View rootView;
	private DatabaseHandler db;
	OnAddEntryClickListener mCallback;
	private int calorie_goal = 1200, calorie_exercise, calorie_food, calorie_remaining, calorie_net;
	
	public HomePageFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
          
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	init();       
    }
    
    private void init() {
    	initObjects();
    	initViews();
    }
    
    private void initObjects() {
    	db = ((MainActivity)getActivity()).getDb();
    	List<DiaryItem> itemList = db.getAllDiaryItems();
    	calorie_food = getTotalCalorie(itemList);
    	calorie_remaining = calorie_goal + calorie_exercise - calorie_food;
    	calorie_net = calorie_food - calorie_exercise;
    	
    	// programmatically setup button click listener due to to XML way only works for Activity 
    	final Button button = (Button) rootView.findViewById(R.id.add_to_diary);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mCallback.onAddEntryClick();
            }
        });
    }
    
    private void initViews() {
    	TextView tv = (TextView) rootView.findViewById(R.id.calories_remaining_number);
    	tv.setText(String.valueOf(calorie_remaining));
    	tv = (TextView) rootView.findViewById(R.id.calorie_goal_number);
    	tv.setText(String.valueOf(calorie_goal));
    	tv = (TextView) rootView.findViewById(R.id.calorie_food_number);
    	tv.setText(String.valueOf(calorie_food));
    	tv = (TextView) rootView.findViewById(R.id.calorie_exercise_number);
    	tv.setText(String.valueOf(calorie_exercise));
    	tv = (TextView) rootView.findViewById(R.id.calorie_net_number);
    	tv.setText(String.valueOf(calorie_net));
    	
    	// add nutrition table fragment into the view
        Fragment nutritionTableFragment = new NutritionTableFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.nutritionTableFragment, nutritionTableFragment).commit();
    }
    
    private String addSign(int value) {
    	String result = String.valueOf(value);
    	if (value < 0) result += "- " + result;
    	return result;
    }
    
    // Container Activity must implement this interface
    public interface OnAddEntryClickListener {
        public void onAddEntryClick();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAddEntryClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddEntryClickListener");
        }
    }
    
    private int getTotalCalorie(List<DiaryItem> itemList) {
		int cTotal = 0, calorie;
		for (DiaryItem item: itemList) {
			cTotal += item.getCalorie();
		}
		return cTotal;
	}
}