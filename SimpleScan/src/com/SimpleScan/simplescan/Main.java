package com.SimpleScan.simplescan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.Tools.Filesystem;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class Main extends FragmentActivity implements OnItemClickListener {
	
	private DrawerLayout drawerLayout;
	private ListView listView;
	private String[] menu;
	private ActionBarDrawerToggle drawerListener;
	private FragmentManager fManager;
	private DBManager dbManager; 
	
	//key pair for restore the instance state 
	static final String STATE_SCORE = "mainScore";
	static final String STATE_LEVEL = "mainLevel";
	private int mCurrentScore;
	private int mCurrentLevel;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void onCreate(Bundle saveInstatnceState) {
		super.onCreate(saveInstatnceState);
		setContentView(R.layout.activity_main);
		
		Filesystem.init(this);
		//this.myID = Integer.parseInt(ParseUser.getCurrentUser().getUsername());
		dbManager = new DBManager(this);
		retrieveSharedExpense();
		//enable the action bar
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);		
		menu = getResources().getStringArray(R.array.menu);
		//Set up the List view for the menu
		listView = (ListView)findViewById(R.id.drawerList);		
		listView.setAdapter(new ArrayAdapter<String>(this,R.layout.custom_list_item,menu));
		listView.setOnItemClickListener(this);
		// drawer menu 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerListener = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
		drawerLayout.setDrawerListener(drawerListener);
		// dbManager 
		dbManager = new DBManager(this);
		// fragment manager
		fManager = getSupportFragmentManager();
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		// check if the user have set up the profile before
		User userInfo = dbManager.getUserInfo();
		if(loadUserName(userInfo))
		{
			FragmentOverview fragmentOverall = new FragmentOverview();
			fTransaction.add(R.id.mainContent,fragmentOverall);
			fTransaction.commit();
		}
		else
		{
			FragmentConnect fragmentConnect = new FragmentConnect();
			fTransaction.add(R.id.mainContent,fragmentConnect);
			fTransaction.commit();
		}
		
		
	}
	
	/*
	 * Kevin
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current state
	    savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
	    savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}
	/*
	 * Kevin
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);
	   
	    // Restore state members from saved instance
	    mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
	    mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)	{
		if(drawerListener.onOptionsItemSelected(menuItem))
		{
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	@Override
	public void onConfigurationChanged(Configuration config)	{
	
		super.onConfigurationChanged(config);
		drawerListener.onConfigurationChanged(config);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)	{
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		selectItem(position);
		
	}
	
	public void selectItem(int position) {
		listView.setItemChecked(position, true);
		Fragment newFragment = null;
		switch (position) {
			case 0:
				newFragment = new FragmentOverview();
				break;
			case 1:
				newFragment = new FragmentExpenses();
				break;
			case 2:
				newFragment = new FragmentViewShared();
				break;
			case 3:
				newFragment = new FragmentCategories();
				break;
			case 4:
				newFragment = new FragmentContact();
				break;
			case 5:
				newFragment = new FragmentConnect();
				break;
			case 6:
				startActivity(new Intent(this, ReminderActivity.class));
				return;
			default:
				break;
		}
		makeToast("On fragment : "+menu[position]);
		changeFragment(newFragment, menu[position], false);
		drawerLayout.closeDrawers();
	}
	
	public void setTitle(String title) {
		getActionBar().setTitle(title);
	}
	
	/**
	 * Replaces the current mainContent fragment with a new one.
	 * 
	 * @param newFragment The new Fragment to display
	 * @param fragmentName The Fragment's name, to display as the title
	 * @param addToBackStack true if it should be added to the backstack
	 * 
	 * TODO: Set current page name as backstack name
	 */
	public void changeFragment(Fragment newFragment, String fragmentName, boolean addToBackStack) {
		setTitle(fragmentName);
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		fTransaction.replace(R.id.mainContent, newFragment);
		if (addToBackStack) {
			fTransaction.addToBackStack(null);
		}
		fTransaction.commit();	
	}
	
	/**
	 * Goes back. Funcationally the same as pressing the back button.
	 * 
	 * TODO: Get page name from the backstack name.
	 */
	public void goBack() {
		if (fManager.getBackStackEntryCount() > 0) {
			fManager.popBackStack();
		} else {
			makeToast("Nothing to go back to");
		}
	}
	
	/**
	 * Makes a short toast.
	 * 
	 * @param message
	 */
	public void makeToast(String message) {
		Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	/*
	*  Method that check for the existence of the user info in the DB
	*  if exists, load the user name, ID and display in the UI
	*  if no, do nothing
	*/
	public boolean loadUserName(User userInfo)
	{
		// check if the user set up the user name before
		if(!userInfo.getName().equals("-1"))
		{
			Log.i("Fragement Profile -->"," userInfo exist");
			if(!userInfo.getName().isEmpty())
			{
				return true;
			}
			else return false;
		}
		else
			return false;
	}
	/**
	 * Retrieves a shared list of expenses to you when you were offline.
	 */
	public void retrieveSharedExpense() {
		if(ParseUser.getCurrentUser()!=null) {
			ParseQuery<ParseObject> pending_query = ParseQuery.getQuery("ShareRequest");
			pending_query.whereEqualTo("id_receiver", Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
			pending_query.whereEqualTo("status", "pending");
			pending_query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(List<ParseObject> objects, com.parse.ParseException e) {
					if (e == null) {
						for (int i = 0; i < objects.size(); i++) {
							SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
							Calendar calendar = Calendar.getInstance();
							// Create the expense and save it
							Expense newExpense = new Expense();
							newExpense.setAmount(objects.get(i).getInt("amount"));
							newExpense.setDate(sdf.format(calendar.getTime()));
							newExpense.setTitle(objects.get(i).getString("title"));
							newExpense.setId(1);
							newExpense.setSharedId(objects.get(i).getInt("id_receiver"));
							dbManager.addExpense(newExpense.getAmount(), newExpense.getDate(), newExpense.getTitle(), null, null, null);
							dbManager.editExpense(newExpense.getId(), 1, -1, null, null, null, null, null);
							objects.get(i).put("status", "viewed");
							objects.get(i).saveInBackground();
						}
	
					} else {
						Log.d("contact", "Error: " + e.getMessage());
						// Something went wrong.
					}
				}
			});
		}
	}
}