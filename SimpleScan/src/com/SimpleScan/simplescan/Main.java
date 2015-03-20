package com.SimpleScan.simplescan;

import com.parse.Parse;
import com.parse.ParseObject;
import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
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

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected void onCreate(Bundle saveInstatnceState)
	{
		super.onCreate(saveInstatnceState);
		setContentView(R.layout.activity_main);
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
		FragmentOverall fragmentOverall = new FragmentOverall();
		fTransaction.add(R.id.mainContent,fragmentOverall);
		fTransaction.commit();
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
		 */
		switch (position)
		{
			case 0:
				fTransaction.replace(R.id.mainContent,new FragmentOverall());
				fTransaction.commit();	
				break;
			case 1:
				fTransaction.replace(R.id.mainContent, new FragmentExpense());
				fTransaction.commit();	
				break;
			case 2:
				fTransaction.replace(R.id.mainContent, new FragmentContact());
				fTransaction.commit();	
				break;
			case 3:
				fTransaction.replace(R.id.mainContent, new FragmentConnect());
				fTransaction.commit();	
				break;
			default:
				break;
			
		}
		drawerLayout.closeDrawers();
		
	}
	public void setTitle(String title)
	{
		getActionBar().setTitle(title);
		
	}
	
}
