package com.SimpleScan.simplescan;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;

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
	
	public void addExpenseFragment(View view) {
		// Get today's date to set as default
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		// Create the expense and save it
		Expense newExpense = new Expense();
		newExpense.setAmount(0.);
		newExpense.setDate(sdf.format(date));
		newExpense.setTitle("expense");
		DBManager dbManager = new DBManager(this);
		dbManager.addExpense(
				newExpense.getAmount(), 
				newExpense.getDate(), 
				newExpense.getTitle(), 
				newExpense.getCategory(), 
				newExpense.getImageTitle(), 
				newExpense.getImagePath()
				);
		
		Toast.makeText(getBaseContext(), "Expense Created",Toast.LENGTH_SHORT).show();
		openEditExpenseFragment(view, newExpense);
	}
	
	public void openEditExpenseFragment(View view, Expense expense) {
		setTitle("Edit Expense");
		FragmentTransaction fTransaction  = fManager.beginTransaction();
		Fragment fragment = FragmentEditExpense.createNewInstance(expense);
		fTransaction.replace(R.id.mainContent, fragment);
		fTransaction.addToBackStack("Opening EditExpenseFragment");
		fTransaction.commit();	
	}
}
