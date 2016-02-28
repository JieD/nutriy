package sfsu.csc780.jied.nutriy;

import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AddFragment extends Fragment {
	
	private String mTitle = "Add";
	private View rootView;
	private ListView listView;
	private DatabaseHandler db;
	private Cursor cursor;
	OnSelectClickListener mCallback;
	
	public AddFragment() {}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_add, container, false);
          
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);   
    	setTitle();
    	db = ((MainActivity)getActivity()).getDb();
    	
    	// programmatically setup button click listener due to to XML way only works for Activity 
    	final ImageButton button = (ImageButton) rootView.findViewById(R.id.search_button);
		button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	hideKeyboard();
            	setUpListView();
                setUpClickListener();
            }
        });       
	}
	
	private void setTitle() {
		Bundle bundle=getArguments();
    	mTitle = bundle.getString("title");
    	getActivity().setTitle(mTitle);
	}
	
	private void setUpListView() {
		// get the listview
        listView = (ListView) rootView.findViewById(R.id.search_result_list);
        EditText et = (EditText)rootView.findViewById(R.id.search_item);
        String filter = et.getText().toString();
        // Log.d("search filter:", filter);
        if (filter != "") { // make sure user input search item
        	cursor = db.searchNutritionsByName(filter);
            assert (cursor != null && cursor.moveToFirst());

            // THE DESIRED COLUMNS TO BE BOUND
            String[] columns = new String[] { DatabaseHandler.COLUMN_NAME[1] };
            // THE XML DEFINED VIEWS WHICH THE DATA WILL BE BOUND TO
            int[] to = new int[] { R.id.search_result_list_text};

            // CREATE THE ADAPTER USING THE CURSOR POINTING TO THE DESIRED DATA AS WELL AS THE LAYOUT INFORMATION
            SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.search_result_list_item, cursor, columns, to, 0);

            // SET THIS ADAPTER AS YOUR LISTVIEW's ADAPTER
            listView.setAdapter(mAdapter);
        }               
	}
	
	public void setUpClickListener() {
		// Set the list's click listener
        listView.setOnItemClickListener(new SearchResultItemClickListener());
	}
	
	private class SearchResultItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(view);	        
	    }	    
	}
	
	private void selectItem(View view) {
		LinearLayout container = (LinearLayout)view;
		TextView tv = (TextView)container.getChildAt(0);
		String name = tv.getText().toString();	
		mCallback.onSelectClick(name, mTitle.toUpperCase());
	}
	
	// Container Activity must implement this interface
    public interface OnSelectClickListener {
        public void onSelectClick(String name, String category);
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSelectClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSelectClickListener");
        }
    }
    
    // hide keyboard after typing the search keyword
    private void hideKeyboard() {
    	InputMethodManager inputManager = 
    	        (InputMethodManager) getActivity().
    	            getSystemService(Context.INPUT_METHOD_SERVICE); 
    	inputManager.hideSoftInputFromWindow(
    			getActivity().getCurrentFocus().getWindowToken(),
    	        InputMethodManager.HIDE_NOT_ALWAYS);
    }

}