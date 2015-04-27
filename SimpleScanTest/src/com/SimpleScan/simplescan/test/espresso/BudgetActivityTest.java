package com.SimpleScan.simplescan.test.espresso;

//Some Standard Imports
import java.util.List;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.test.RenamingDelegatingContext;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.SimpleScan.simplescan.FragmentCategories;
import com.SimpleScan.simplescan.FragmentEditBudget;
import com.SimpleScan.simplescan.FragmentOverview;
import com.SimpleScan.simplescan.Main;
import com.SimpleScan.simplescan.R;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract;
import com.SimpleScan.simplescan.test.DBManagerDummy;


//Some Standard Imports
import android.util.Log;

//Some Espresso Related imports
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView; //for finding controls

import com.google.android.apps.common.testing.ui.espresso.ViewInteraction; //stores found controls
import com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions;//checking/asserting view state is correct
import com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers;


//static imports for quick access to frequently used methods.
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.*; //click()
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.*; //withId()

//import static org.hamcrest.Matchers.*; //not()
import com.google.android.apps.common.testing.ui.espresso.ViewInteraction;

import android.test.RenamingDelegatingContext;

public class BudgetActivityTest extends android.test.ActivityInstrumentationTestCase2<Main>
{
	EditText editBudgetText;
	EditText editStartDateText;
	EditText editEndDateText;
	
	private DBManager db;
	private DBManagerDummy dbTest;
	
	public BudgetActivityTest()
	{
		super(Main.class);
	}

	@Override
	protected void setUp() throws Exception
	{		
		getInstrumentation().getTargetContext().deleteDatabase(SimpleScanContract.DATABASE_NAME);
		// create a separate test database
        RenamingDelegatingContext context = new RenamingDelegatingContext(getInstrumentation().getTargetContext().getApplicationContext(), "test_");
        db = new DBManager(context);
        dbTest = new DBManagerDummy(context);
                
		getActivity();
		super.setUp();
	}
		 
	public void tearDown() throws Exception {
		Log.d( "TEARDOWN", "TEARDOWN");
		super.tearDown();
	}
		
	@UiThreadTest
	public void testCreateBudget()
	{
		FragmentManager fManager = getActivity().getSupportFragmentManager();		
		List<Fragment> fragments = fManager.getFragments();
		assertNotNull(fragments);
		for(Fragment f : fragments){
			if(f != null && f.isVisible()){
				//ViewInteraction fBudgetText = onView(withId(R.id.EB_editBudget)); 
				//ViewInteraction fStartDateText = onView(withId(R.id.EB_editStartDate));
				//ViewInteraction fEndDateText = onView(withId(R.id.EB_editEndDate));
				editBudgetText = (EditText) getActivity().findViewById(R.id.EB_editBudget);
				editStartDateText = (EditText) getActivity().findViewById(R.id.EB_editStartDate);
				editEndDateText = (EditText) getActivity().findViewById(R.id.EB_editEndDate);
				
				getActivity().runOnUiThread(new Runnable() {
		            @Override
		            public void run() {
		                // This code will always run on the UI thread, therefore is safe to modify UI elements.		            	
						editBudgetText.setText("400.00");
						editStartDateText.setText("04/15/2015");
						editEndDateText.setText("05/15/2015");
		            }
		        });
				assertEquals("400.00", editBudgetText.getText().toString());
				assertEquals("04/15/2015", editStartDateText.getText().toString());
				assertEquals("05/15/2015", editEndDateText.getText().toString());
				
				Button saveButton = (Button) getActivity().findViewById(R.id.EB_btnSave);
				saveButton.performClick();
				ListView menu = (ListView) getActivity().findViewById(R.id.drawerList);
				menu.getChildAt(0).performClick();				
				
				//fBudgetText.perform(typeText("400.00"), closeSoftKeyboard());
				//pauseTestFor(1000);
			}			
		}
		
		/******************************************************/
		FragmentCategories fragCate = new FragmentCategories();
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		fTransaction.replace(android.R.id.content, fragCate,"Cate");
		fTransaction.commit();
		fManager = getActivity().getSupportFragmentManager();
		fragments = fManager.getFragments();
		/******************************************************/
		assertNotNull(fragments);
		for(Fragment f : fragments){
			if(f != null && f.isVisible() && (f instanceof FragmentOverview)){		
				TextView remainingBudget = (TextView) getActivity().findViewById(R.id.O_remainingBudget);
				assertEquals(remainingBudget.getText().toString(), "400.00");
			}
		}
		//android.app.Fragment myFragment1 = getActivity().getFragmentManager().findFragmentById(R.id.mainContent);
		//assertTrue(myFragment1.isVisible());
		//android.app.Fragment myFragment2 = getActivity().getFragmentManager().;
		//assertTrue(myFragment2.isVisible());
		//if (myFragment.isVisible()) {}
		//onView(withId(R.id.EB_editBudget)).perform(typeText("400.00"));
		//onView(withId(R.id.EB_editStartDate)).perform(typeText("04/09/2015"));
		//onView(withId(R.id.EB_editEndDate)).perform(typeText("05/09/2015"));
		
		//onView(withId(R.id.EB_btnSave)).perform(click());
		
		//onView(withId(R.id.EB_editBudget)).check(ViewAssertions.matches(withText("0.0")));
	}
	
	private void pauseTestFor(long milliseconds) {
	    try {
	        Thread.sleep(milliseconds);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	}
}