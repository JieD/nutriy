package sfsu.csc780.jied.nutriy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sfsu.csc780.jied.nutriy.adapter.NavDrawerListAdapter;
import sfsu.csc780.jied.nutriy.db.handler.DatabaseHandler;
import sfsu.csc780.jied.nutriy.db.helper.ParseFile;
import sfsu.csc780.jied.nutriy.model.DiaryItem;
import sfsu.csc780.jied.nutriy.model.NavDrawerItem;
import sfsu.csc780.jied.nutriy.model.Nutrition;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author jied
 * @date 5/12/2014
 * 
 * Add NavigationDrawer 
 * 1. Creating a NavDrawerListAdapter instance and adding list items.
 * 2. Assigning the adapter to Navigation Drawer ListView
 * 3. Creating click event listener for list items
 * 4. Creating and displaying fragment activities on selecting list item.
 */
public class MainActivity extends FragmentActivity 
	implements HomePageFragment.OnAddEntryClickListener,  
	AddEntryFragment.OnAddClickListener, AddFragment.OnSelectClickListener,
	EditFragment.OnAddToDiaryClickListener,
	DiaryPageFragment.OnEditClickListener,
	DiaryPageFragment.OnEditActionListener,
	DiaryPageFragment.OnAddActionListener,
	DiaryPageFragment.OnDeleteActionListener {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
    private CharSequence mDrawerTitle;
 
    // used to store app title
    private CharSequence mTitle;
 
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;  // use TypedArray to retrieve the icon ID
 
    private ArrayList<NavDrawerItem> navDrawerItems;
    
    private Bundle mSavedInstanceState;
    
    private DatabaseHandler db;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;       
        setContentView(R.layout.activity_main);   
        initNavigationDrawer();
        
        // on first time display view for first nav item
        if (mSavedInstanceState == null) {          
            selectItem(0);
        }        
        
        db = new DatabaseHandler(this);
        File database = getApplicationContext().getDatabasePath(DatabaseHandler.NUTRITION_DATABASE);

        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            Log.i("Database", "Not Found");    
            
            /**
             * CRUD Operations
             */
            // Inserting Nutritions
            Log.d("Insert: ", "Inserting .."); 
            
            ParseFile pf = new ParseFile(this); 
            db.addNutritionList(pf.getNutritionList());
                       
            // Reading Nutritions
            Log.d("Reading: ", "Reading some Nutritions.."); 

            int[] ids = {1, 100, 200, 300};                 
            for (int id: ids) {
            	Nutrition nutrition = db.getNutrition(id);
            	Log.d("id:", String.valueOf(nutrition.getId()));
                Nutrition.log(nutrition.getNutritionMap());
            } 
            
        } else {
            Log.i("Database", "Found");     
            
            /** SQLiteDatabase sqldb = db.getWritableDatabase();	
            db.onUpgrade(sqldb, 1, 1); */
            
            // update database
            /** Log.d("Delete: ", "Deleting .."); 
  			// need to add delete method
            ParseFile pf = new ParseFile(this); 
            db.addNutritionList(pf.getNutritionList()); */
            
         // Test database DiaryItems
            /** Log.d("Delete: ", "Deleting .."); 
            List<DiaryItem> items = db.getAllDiaryItems(); 
            for (DiaryItem di : items) {
                db.deleteDiaryItem(di);
            } */
                 
            // Inserting Contacts
            /** Log.d("Insert: ", "Inserting .."); 
            db.addDiaryItem(new DiaryItem("CAMPBELL'S BRN SUGAR&BACON FLAV BKD BNS", 1.0, 150, "LUNCH"));        
            db.addDiaryItem(new DiaryItem("VITASOY USA,NASOYA LITE FIRM TOFU", 2.0, 100, "LUNCH"));
            db.addDiaryItem(new DiaryItem("SOYMILK (ALL FLAVORS),UNSWTND,W/ ADDED CA,VITAMINS A & D", 0.5, 120, "BREAKFAST"));
            db.addDiaryItem(new DiaryItem("LAMB,DOM,COMP OF RTL CUTS,LN&FAT,1/8\"FAT,CHOIC,RAW", 0.2, 300, "DINNER")); 
             
            List<DiaryItem> items = db.searchDiaryItemByCategoty("LUNCH");
            // Reading all contacts
            /** Log.d("Reading: ", "Reading all diary item.."); 
            items = db.getAllDiaryItems();      
             
            for (DiaryItem di : items) {
                String log = "Id: "+di.getId()+" ,Name: " + di.getName() + " ,Number of Servings: " 
                		+ di.getNumberOfServings() + " ,Calorie: " + di.getCalorie() 
                		+ " ,Category: " + di.getCategory();
                // Writing Contacts to log
                Log.d("Name: ", log);
            }  */          
            
        }   
        
        
    }
    
    private void initNavigationDrawer() {
		initNavigationList();
		initNavigationListener();
	}

	private void initNavigationList() {		
		// load navigation menu titles
        navMenuTitles = getResources().getStringArray(R.array.navigations_array);
 
        // navigation drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
 
        navDrawerItems = new ArrayList<NavDrawerItem>();
 
        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Diary
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Nutrition
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Progress
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Goals
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Settings
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
         
        // Recycle the typed array
        navMenuIcons.recycle();
 
        // setting the navigation drawer list adapter - use the custom Adapter
        mDrawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(), navDrawerItems));
        
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	/** Swaps fragments in the main content view */
	public void selectItem(int position) {
		// update the main content by replacing fragments		
		Fragment fragment = null;
        switch (position) {
	        case 0:
	            fragment = new HomePageFragment();
	            break;
	        case 1:
	            fragment = new DiaryPageFragment();
	            break;
	        case 2:
	            fragment = new NutritionPageFragment();
	            break;
	        case 3:
	            //fragment = new ProgressPageFragment();
	            break;
	        case 4:
	            //fragment = new GoalPageFragment();
	            break;
	        case 5:
	            //fragment = new SettingPageFragment();
	            break;
	 
	        default:
	            break;
        }
 
        if (fragment != null) {
        	replaceFragment(fragment);
 
            // Highlight the selected item, update the title, and close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
	}
	
	@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	
	private void initNavigationListener() {
		 mTitle = mDrawerTitle = getTitle();
		 mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		 // associate Navigation Drawer with ActionBar
		 mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
	                R.drawable.ic_drawer, //nav menu toggle icon
	                R.string.app_name, // nav drawer open - description for accessibility
	                R.string.app_name // nav drawer close - description for accessibility
	        ){
			 	// Called when a drawer has settled in a completely closed state.
	            public void onDrawerClosed(View view) {
	                getActionBar().setTitle(mTitle);
	                // calling onPrepareOptionsMenu() to show action bar icons
	                invalidateOptionsMenu();
	            }
	 
	            // Called when a drawer has settled in a completely open state.
	            public void onDrawerOpened(View drawerView) {
	                getActionBar().setTitle(mDrawerTitle);
	                // calling onPrepareOptionsMenu() to hide action bar icons
	                invalidateOptionsMenu();
	            }
	        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle navigation drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
        }
    }
 
    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
 
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
 
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     *  on HomePage, user clicks 'Add to Diary' button
     *  go to AddEntryFragment
     */
	@Override
	public void onAddEntryClick() {		
    	Fragment addEntryFragment = new AddEntryFragment();
    	replaceFragment(addEntryFragment);
	}
	
	private void replaceFragment(Fragment newFragment) {
		if (newFragment != null) {
            
			// Insert the fragment by replacing any existing fragment
	    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	   	
	    	// Replace whatever is in the fragment_container view with this fragment,
	    	// and add the transaction to the back stack
	    	transaction.replace(R.id.content_frame, newFragment);
	    	transaction.addToBackStack(null);
	    	
	    	// Commit the transaction
	    	transaction.commit();
		}
	}

	/**
	 * on AddEntryFragment, user clicks a specific entry to add item
	 * go to AddFragment, pass the entry name along
	 */
	@Override
	public void onAddClick(String category) {
		Fragment addFragment = new AddFragment();
		Bundle args = new Bundle();
		args.putString("title", category);
		addFragment.setArguments(args);
    	replaceFragment(addFragment);		
	}
	
	public DatabaseHandler getDb() {
		return db;
	}

	public void setDb(DatabaseHandler db) {
		this.db = db;
	}

	/**
	 * on AddFragment, user selects a specific item to add to the diary
	 * go to EditFragment, pass the selected item name and category along
	 */
	@Override
	public void onSelectClick(String name, String category) {
		Fragment editFragment = new EditFragment();
		Bundle args = new Bundle();
		args.putString("name", name);
		args.putString("category", category);
        editFragment.setArguments(args);
		replaceFragment(editFragment);		
	}

	@Override
	public void OnAddToDiaryClick() {
		Fragment diary = new DiaryPageFragment();
		replaceFragment(diary);		
	}

	@Override
	public void onEditClick(DiaryItem item) {
		Fragment editFragment = new EditFragment();
		Bundle args = new Bundle();
		args.putString("name", item.getName());
		// currently not enable edit number of servings
		//args.putString("numberOfServings", String.valueOf(item.getNumberOfServings()));
		args.putString("category", item.getCategory());
        editFragment.setArguments(args);
		replaceFragment(editFragment); 		
	}

	@Override
	public void onAddAction() {
		onAddEntryClick();		
	}

	@Override
	public void onEditAction() {
		DiaryPageFragment diary = new DiaryPageFragment();
		diary.enableEditMode();
		replaceFragment(diary);	
	}

	@Override
	public void onDeleteAction() {
		Fragment diary = new DiaryPageFragment();
		replaceFragment(diary);			
	}
}