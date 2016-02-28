package sfsu.csc780.jied.nutriy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import sfsu.csc780.jied.nutriy.adapter.DiaryItemListAdapter;
import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class DiaryPageFragment extends Fragment {
	
	private View rootView;
	private String mTitle = "Diary";
	private final HashMap<Integer, String> CALORIE_MAP = new HashMap<Integer, String> ();
	private List<DiaryItem> itemList;
	DatabaseHandler db;
	OnEditClickListener mEditCallback;
	OnEditActionListener mEditActionCallback;
	OnAddActionListener mAddActionCallback;
	OnDeleteActionListener mDeleteActionCallback;
	DiaryItemListAdapter listAdapter;
    ExpandableListView expListView;
	private List<String> listDataHeader;
	private List<String> listDataHeaderValue;
	private HashMap<String, List<DiaryItem>> listDataChild;
	private List<DiaryItem> selectedItems;
	
	private int mMode = VIEW_MODE;
	public final static int VIEW_MODE = 0;
	public final static int EDIT_MODE = 1;
	
	private final static int MENU = R.menu.diary_fragment;
	private final static int MENU_EDIT = R.menu.diary_edit_fragment;
	private int mMenu = VIEW_MODE;
	
	public DiaryPageFragment() {}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  
        rootView = inflater.inflate(R.layout.fragment_diary_page, container, false); 
        setHasOptionsMenu(true); 
        return rootView;
    }
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);   	
    	init();   	
    	
    	/** int count = db.getDiaryItemsCount();
    	DiaryItem item = db.getDiaryItem(count);
    	String log = "Id: "+item.getId()+" ,Name: " + item.getName() + " ,Number of Servings: " 
        		+ item.getNumberOfServings() + " ,Category: " + item.getCategory();
        // Writing Contacts to log
        Log.d("Name: ", log); */
	}
	
	@Override	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		mMenu = (mMode == VIEW_MODE) ? MENU : MENU_EDIT;
	    inflater.inflate(mMenu, menu);	
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
       switch (item.getItemId()) {
          case R.id.action_edit:
        	  mEditActionCallback.onEditAction();
              return true;
          case R.id.action_add:
        	  mAddActionCallback.onAddAction();
        	  return true;
          case R.id.action_delete:
        	  deleteSelectedItems();
        	  mDeleteActionCallback.onDeleteAction();
        	  return true;
          default:
             return super.onOptionsItemSelected(item);
       }
    } 
	
	private void init() {
		getActivity().setTitle(mTitle);
		db = ((MainActivity) getActivity()).getDb();
		itemList = db.getAllDiaryItems();
		initMap();
		initViews();
		setUpExpListView();
        setUpClickListener(); 
	}
	
	private void initMap() {
		int cGoal = 1200, cFood = 0, cExercise = 0, cNet = 0, cRemaining = cGoal;
		cFood = getTotalCalorie(itemList);
		cNet = cFood - cExercise;
		cRemaining = cGoal - cNet;
		CALORIE_MAP.put(R.id.calorie_goal, String.valueOf(cGoal));
		CALORIE_MAP.put(R.id.calorie_food, String.valueOf(cFood));
		CALORIE_MAP.put(R.id.calorie_exercise,  String.valueOf(cExercise));
		CALORIE_MAP.put(R.id.calorie_net, 	    String.valueOf(cNet));
		CALORIE_MAP.put(R.id.calorie_remaining, String.valueOf(cRemaining));	
		flagMinusRemaining(cRemaining) ;
	}
	
	private void initViews() {
		TextView tv;
		for (int id: CALORIE_MAP.keySet()) {
			tv = (TextView) rootView.findViewById(id);
			tv.setText(CALORIE_MAP.get(id));
		}		
	}
	
	private String toIntString(double value) {
		int newValue = (int) value;
		return String.valueOf(newValue);
	}
	
	private void flagMinusRemaining(int remainingValue) {
		if (remainingValue < 0) {
			TextView tv = (TextView) rootView.findViewById(R.id.calorie_remaining);
			tv.setTextColor(getResources().getColor(R.color.my_red));
		}
	}
	
	private void setUpExpListView() {
		// get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.diaryItemLVExp);
 
        // preparing list data
        prepareListData();
 
        listAdapter = new DiaryItemListAdapter(getActivity(), listDataHeader, 
        		listDataHeaderValue, listDataChild, mMode);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        // expand all children
        int groupCount = listAdapter.getGroupCount();
        for (int position = 0; position < groupCount; position++)
        	expListView.expandGroup(position);
	}
	
	private void prepareListData() {
		// load add entry list header titles
		String[] itemHeaders = getResources().getStringArray(R.array.diary_item_list_header_array);
        listDataHeader = Arrays.asList(itemHeaders);
        listDataHeaderValue = new ArrayList<String> ();
        listDataChild = new HashMap<String, List<DiaryItem>> ();
        
        for (String category: itemHeaders) {
        	List<DiaryItem> itemList = db.searchDiaryItemByCategoty(category);
        	listDataHeaderValue.add(toIntString(getTotalCalorie(itemList)));
        	listDataChild.put(category, itemList);
        }      
	}
	
	private void setUpClickListener() {
		// Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {    
            	DiaryItem item = getChild(groupPosition, childPosition);
            	if (mMode == EDIT_MODE) {
            		ViewGroup container = (ViewGroup) v;
            		CheckBox cb = (CheckBox) container.getChildAt(0);
            		if (cb.isChecked()) cb.setChecked(false);
            		else cb.setChecked(true);
            		
            		selectedItems.add(item);
            	} else {
            		mEditCallback.onEditClick(item);
            	}         	
                return true;
            }
        });
	}
	
	// Container Activity must implement this interface
    public interface OnEditClickListener {
        public void onEditClick(DiaryItem item);
    }
    
    public interface OnEditActionListener {
    	public void onEditAction();
    }
    
    public interface OnAddActionListener {
    	public void onAddAction();
    }
    
    public interface OnDeleteActionListener {
    	public void onDeleteAction();
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mEditCallback = (OnEditClickListener) activity;
            mEditActionCallback = (OnEditActionListener) activity;
            mAddActionCallback = (OnAddActionListener) activity;
            mDeleteActionCallback = (OnDeleteActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnEditClickListener, OnEditActionListener, "
                    + "OnAddActionListener, OnDeleteActionListener");
        }
    }
	
	private int getTotalCalorie(List<DiaryItem> itemList) {
		int cTotal = 0, calorie;
		for (DiaryItem item: itemList) {
			cTotal += item.getCalorie();
		}
		return cTotal;
	}
	
	private DiaryItem getChild(int groupPosition, int childPosition) {
		List<DiaryItem> childList = listDataChild.get(listDataHeader.get(groupPosition));
		return childList.get(childPosition);
	}
	
	public void enableEditMode() {
		mMode = EDIT_MODE;
		selectedItems = new ArrayList<DiaryItem> ();
	}
	
	// Container Activity must implement this interface
    public interface OnAddToDiaryClickListener {
        public void OnAddToDiaryClick();
    }
    
    private void deleteSelectedItems() {
    	Log.d("selectedItems is null? ", String.valueOf(selectedItems == null));
    	Log.d("count: ", String.valueOf(selectedItems.size()));
    	if (selectedItems != null) {
    		for (DiaryItem item: selectedItems) {
    			db.deleteDiaryItem(item);
    		}
    	}
    }
}