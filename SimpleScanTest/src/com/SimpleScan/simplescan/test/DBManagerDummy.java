package com.SimpleScan.simplescan.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.SimpleScan.simplescan.sqlite.DBHelper;

public class DBManagerDummy {

	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	
	/**
	 * Default Constructor
	 */
	public DBManagerDummy(Context context){		
		dbHelper = new DBHelper(context);
	}
	
	public void expenseData1(){
		
	}
	
	public void userData1(){
		
	}
	
	public void budgetData1(){
		
	}
	
	public void imageData1(){
		
	}
	
	public void sharedExpenseData1(){
		
	}
	
	public void categoryData1(){
		
	}
	
	public void contactData1(){
		
	}
	
	public void reminderData1(){
		
	}
	
}
