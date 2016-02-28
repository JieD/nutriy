package sfsu.csc780.jied.nutriy;

import java.util.HashMap;

import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EditFragment extends Fragment {
	
	private String mTitle = "Edit";
	private View rootView;
	private Nutrition mNutrition;
	private DatabaseHandler db;
	private String[] nutritionTitleArray;
	private HashMap<Integer, String> VALUE_MAP = new HashMap<Integer, String> ();
	private OnAddToDiaryClickListener mCallback;
	private String mName, mCategory;
	
	public EditFragment() {}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        
        /** Report that this fragment would like to participate in populating the options menu by 
         * receiving a call to onCreateOptionsMenu(Menu, MenuInflater) and related methods.
         */
        setHasOptionsMenu(true);  
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getActivity().setTitle(mTitle);
    	init();
	}
	
	private void init() {
		initObjects();
		initHashMap();
		initViews();
	}
	
	private void initObjects() {
		nutritionTitleArray = Nutrition.nutritionTitleArray;
		db = ((MainActivity)getActivity()).getDb();
		Bundle bundle=getArguments();
    	mName = bundle.getString("name");
    	mCategory = bundle.getString("category");
    	mNutrition = db.searchForNutrition(mName);
    	// mNutrition.enableDouble();
	}
	
	private void initHashMap() {
		VALUE_MAP.put(R.id.edit_item_name,       mNutrition.getValue(nutritionTitleArray[0]));
		VALUE_MAP.put(R.id.number_of_servings,   "1");
		VALUE_MAP.put(R.id.serving_size,         mNutrition.getValue(nutritionTitleArray[2]) + " (" 
		+ mNutrition.getValue(nutritionTitleArray[1]) + "g)");
		VALUE_MAP.put(R.id.mCalorie,             mNutrition.getValue(nutritionTitleArray[3]));
		VALUE_MAP.put(R.id.mTotal_fat,           mNutrition.getValue(nutritionTitleArray[4]) + " g");
		VALUE_MAP.put(R.id.mSaturated_fat,       mNutrition.getValue(nutritionTitleArray[5]) + " g");
		VALUE_MAP.put(R.id.mPolyunsaturated_fat, mNutrition.getValue(nutritionTitleArray[6]) + " g");
		VALUE_MAP.put(R.id.mMonounsaturated_fat, mNutrition.getValue(nutritionTitleArray[7]) + " g");
		VALUE_MAP.put(R.id.mCholesterol,         mNutrition.getValue(nutritionTitleArray[8]) + " mg");
		VALUE_MAP.put(R.id.mSodium,              mNutrition.getValue(nutritionTitleArray[9]) + " mg");
		VALUE_MAP.put(R.id.mPotassium,           mNutrition.getValue(nutritionTitleArray[10]) + " mg");
		VALUE_MAP.put(R.id.mTotal_carbs,         mNutrition.getValue(nutritionTitleArray[11]) + " g");
		VALUE_MAP.put(R.id.mFiber,               mNutrition.getValue(nutritionTitleArray[12]) + " g");
		VALUE_MAP.put(R.id.mSugar,               mNutrition.getValue(nutritionTitleArray[13]) + " g");
		VALUE_MAP.put(R.id.mProtein,             mNutrition.getValue(nutritionTitleArray[14]) + " g"); 
		VALUE_MAP.put(R.id.mVit_A,               mNutrition.getValue(nutritionTitleArray[15]) + "%"); 
		VALUE_MAP.put(R.id.mVit_C,               mNutrition.getValue(nutritionTitleArray[16]) + "%"); 
		VALUE_MAP.put(R.id.mCalcium,             mNutrition.getValue(nutritionTitleArray[17]) + "%"); 
		VALUE_MAP.put(R.id.mIron,                mNutrition.getValue(nutritionTitleArray[18]) + "%"); 
	}
	
	private void initViews() {
		TextView tv;
		for (int id: VALUE_MAP.keySet()) {
			tv = (TextView) (rootView.findViewById(id));
			tv.setText(VALUE_MAP.get(id));
		}
	}
	
	@Override	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);		
	    inflater.inflate(R.menu.edit_fragment, menu);	
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
       switch (item.getItemId()) {
          case R.id.action_add:
        	 addDiaryItem();
             mCallback.OnAddToDiaryClick();
             return true;
          default:
             return super.onOptionsItemSelected(item);
       }
    }  
	
	private void addDiaryItem() {
		int calorie = roundToInt(db.searchForNutrition(mName).getValue("calorie"));
		DiaryItem item = new DiaryItem(mName, getNumberOfServings(), calorie, mCategory);
		/** String log = "Id: "+item.getId()+" ,Name: " + item.getName() + " ,Number of Servings: " 
        		+ item.getNumberOfServings() + " ,Calorie: " + item.getCalorie() 
        		+ " ,Category: " + item.getCategory(); */
		if (!db.isItemExisted(mName, mCategory)) {		
			// Log.d("Add: ", log);
	    	db.addDiaryItem(item);
		} else {
			// Log.d("Update: ", log);
			db.updateDiaryItem(item);
		} 	
	}
	
	private Double getNumberOfServings() {
		TextView tv = (TextView) (rootView.findViewById(R.id.number_of_servings));
		String value = tv.getText().toString();
		return Double.valueOf(value);
	}
	
	// Container Activity must implement this interface
    public interface OnAddToDiaryClickListener {
        public void OnAddToDiaryClick();
    }
    
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAddToDiaryClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddToDiaryClickListener");
        }
	}
	
	private int roundToInt(String value) {
		double dValue = Double.valueOf(value);
		return (int) Math.round(dValue);
	}

}