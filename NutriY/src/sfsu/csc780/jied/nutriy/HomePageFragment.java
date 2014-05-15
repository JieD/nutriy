package sfsu.csc780.jied.nutriy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HomePageFragment extends Fragment {
	
	private View rootView;
	OnAddEntryClickListener mCallback;
	
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
    	
    	// programmatically setup button click listener due to to XML way only works for Activity 
    	final Button button = (Button) rootView.findViewById(R.id.add_to_diary);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	mCallback.onAddEntryClick();
            }
        });
        
        Fragment nutritionTableFragment = new NutritionTableFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.nutritionTableFragment, nutritionTableFragment).commit();
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
}