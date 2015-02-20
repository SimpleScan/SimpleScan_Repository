package com.SimpleScan.simplescan;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class Main extends Activity implements OnItemClickListener 
{
	private DrawerLayout drawerLayout;
	private ListView listView;
	private String[] menu;
	private ActionBarDrawerToggle drawerListener;
	protected void onCreate(Bundle saveInstatnceState)
	{
		super.onCreate(saveInstatnceState);
		setContentView(R.layout.activity_main);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);		
		menu = getResources().getStringArray(R.array.menu);
		listView = (ListView)findViewById(R.id.drawerList);		
		listView.setAdapter(new ArrayAdapter<String>(this,R.layout.custom_list_item,menu));
		listView.setOnItemClickListener(this);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		drawerLayout.setDrawerListener(drawerListener);
	
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
	}
	public void setTitle(String title)
	{
		getActionBar().setTitle(title);
		
	}
	

}
