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
	
	public void createUserData1(){
		db = dbHelper.getWritableDatabase();
		db.execSQL(DBTestScripts.UserScripts.INSERT_USER1);
	}
	
	public void createExpenseData1(){
		db = dbHelper.getWritableDatabase();
		db.execSQL(DBTestScripts.ExpenseScripts.INSERT_EXPENSE1_5);
	}	
	
	public void budgetData1(){
		db = dbHelper.getWritableDatabase();
		db.execSQL(DBTestScripts.BudgetScripts.INSERT_BUDGET1);
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
	
	public void close(){
		if(dbHelper != null){
			dbHelper.close();
		}
		if(db != null){
			db.close();
		}
	}
	
}
