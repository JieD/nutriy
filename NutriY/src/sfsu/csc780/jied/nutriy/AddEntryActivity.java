package sfsu.csc780.jied.nutriy;

import android.app.Activity;
import android.os.Bundle;

public class AddEntryActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_entry);
	}
	
	@Override
    public void onBackPressed() {
        //storeNumberOfCalls();
        super.onBackPressed();
    }
	
}
