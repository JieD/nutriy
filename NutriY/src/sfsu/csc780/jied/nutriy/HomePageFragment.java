package sfsu.csc780.jied.nutriy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomePageFragment extends Fragment {
	
	public HomePageFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_home_page, container, false);
          
        return rootView;
    }
    
    public void onAddEntryClick(View v) { 	
    	Fragment currentFragment = getFragmentManager().findFragmentById(R.layout.fragment_home_page);
    	// check that the fragment is currently shown
        if (currentFragment != null) {
        	
        	// Create new fragment and transaction
        	Fragment addEntryFragment = new AddEntryFragment();
        	FragmentTransaction transaction = getFragmentManager().beginTransaction();
        	
        	// Replace whatever is in the fragment_container view with this fragment,
        	// and add the transaction to the back stack
        	transaction.replace(R.id.content_frame, addEntryFragment);
        	transaction.addToBackStack(null);
        	
        	// Commit the transaction
        	transaction.commit();

        	// -----------------------------------------------------------------
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            HeadlinesFragment firstFragment = new HeadlinesFragment();
            
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());
            
            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
	} 
}