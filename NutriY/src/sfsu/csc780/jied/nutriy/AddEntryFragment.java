package sfsu.csc780.jied.nutriy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.adapter.ExpandableListAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class AddEntryFragment extends Fragment {
	
	private String mTitle = "Add Entry";
	private View rootView;
	OnAddClickListener mCallback;
	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    final int[] array_ids = {R.array.meal_child_array, R.array.exercise_child_array, 
			R.array.water_child_array, R.array.note_child_array};
	
	public AddEntryFragment() {}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.add_entry_list, container, false);
          
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	getActivity().setTitle(mTitle);
    	setUpExpListView();
        setUpClickListener(); 
	}
	
	private void setUpExpListView() {
		// get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
 
        // preparing list data
        prepareListData();
 
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        // expand all children
        int groupCount = listAdapter.getGroupCount();
        for (int position = 0; position < groupCount; position++)
        	expListView.expandGroup(position);
	}
	
	private void prepareListData() {		
		// load add entry list header titles
		String[] itemHeaders = getResources().getStringArray(R.array.add_entry_list_header_array);
        listDataHeader = Arrays.asList(itemHeaders);
        listDataChild = new HashMap<String, List<String>>();
        
     // load add entry list item titles
        String[] headerChild;
        for (int i = 0; i < itemHeaders.length; i++) {
        	headerChild = getResources().getStringArray(array_ids[i]);
        	listDataChild.put(itemHeaders[i], Arrays.asList(headerChild));
        } 
	}
	
	private void setUpClickListener() {
		// Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
            	mCallback.onAddClick(getCategory(groupPosition, childPosition));
                return true;
            }
        });
	}
	
	private String getCategory(int groupPosition, int childPosition) {
		String[] children = getResources().getStringArray(array_ids[groupPosition]);
		return children[childPosition];
	}
	
	// Container Activity must implement this interface
    public interface OnAddClickListener {
        public void onAddClick(String category);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnAddClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAddEntryClickListener");
        }
    }
}