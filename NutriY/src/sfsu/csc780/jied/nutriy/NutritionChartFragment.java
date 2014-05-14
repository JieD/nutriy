package sfsu.csc780.jied.nutriy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NutritionChartFragment extends Fragment {
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    	return inflater.inflate(R.layout.fragment_nutrition_chart, container, false);        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	initializeView();    	
    }
    
    private void initializeView() {    	
    	View rootView = getView();
    	
    	// dynamically change Pie slice
    	final PieChart pie = (PieChart) rootView.findViewById(R.id.piechart);
    	pie.setShowChart(true);
    	
    	
    }
}