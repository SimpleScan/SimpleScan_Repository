package com.SimpleScan.simplescan;

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
import android.widget.ListView;
import android.widget.Toast;


public class Main extends FragmentActivity implements OnItemClickListener 
{
	private DrawerLayout drawerLayout;
	private ListView listView;
	private String[] menu;
	private ActionBarDrawerToggle drawerListener;
	private FragmentManager fManager;
	
	//key pair for restore the instance state 
	static final String STATE_SCORE = "mainScore";
	static final String STATE_LEVEL = "mainLevel";
	private int mCurrentScore;
	private int mCurrentLevel;

	protected void onCreate(Bundle saveInstatnceState)
	{
		super.onCreate(saveInstatnceState);
		setContentView(R.layout.activity_main);
		
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
		// fragment manager
		fManager = getSupportFragmentManager();
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		FragmentOverview fragmentOverall = new FragmentOverview();
		fTransaction.add(R.id.mainContent,fragmentOverall);
		fTransaction.commit();
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
	public boolean onOptionsItemSelected(MenuItem menuItem)
	{
		if(drawerListener.onOptionsItemSelected(menuItem))
		{
			return true;
		}
		return super.onOptionsItemSelected(menuItem);
	}
	
	@Override
	public void onConfigurationChanged(Configuration config)
	{
	
		super.onConfigurationChanged(config);
		drawerListener.onConfigurationChanged(config);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		drawerListener.syncState();
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
		selectItem(position);
		
	}
	
	public void selectItem(int position) 
	{
		listView.setItemChecked(position, true);
		setTitle(menu[position]);
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		Toast.makeText(getBaseContext(), "On fragment : "+menu[position],Toast.LENGTH_SHORT).show();
		/*
		 * need to research how to implement backstack here
		 * 
		 * --That would be fTransaction.addToBackStack(String optionalTransactionName), 
		 * --but I don't think it's necessary for the menu. It could get really 
		 * --cluttered for anyone who likes to navigate with the back button.
		 * --Dan
		 */
		switch (position)
		{
			case 0:
				fTransaction.replace(R.id.mainContent, new FragmentOverview());
				fTransaction.commit();	
				break;
			case 1:
				fTransaction.replace(R.id.mainContent, new FragmentExpenses());
				fTransaction.commit();	
				break;
			case 2:
				fTransaction.replace(R.id.mainContent, new FragmentContact());
				fTransaction.commit();	
				break;
			case 3:
				fTransaction.replace(R.id.mainContent, new FragmentShareExpense());
				fTransaction.commit();	
			default:
				break;
			
		}
		drawerLayout.closeDrawers();
		
	}
	
	public void setTitle(String title)
	{
		getActionBar().setTitle(title);
	}
	
	/**
	 * Replaces the current mainContent fragment with a new one.
	 * 
	 * @param newFragment The new Fragment to display
	 * @param fragmentName The Fragment's name, to display as the title
	 * @param addToBackStack true if it should be added to the backstack
	 * 
	 * TODO: If there will be advanced backstack operations, a way to add backstack names
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
}
