package com.SimpleScan.simplescan.sqlite;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Category;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.SimpleScanImage;

/**
 * @author pearse1
 *
 * This class provides an interface for users to interact with the database.
 */
public class DBManager {

	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;
	/**
	 * Default Constructor
	 */
	public DBManager(Context context){		
		dbHelper = new DBHelper(context);
	}

	/**
	 * Gets a list of all expenses.
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses(){
		return null;
	}
	
	/**
	 * Gets a list of expenses.
	 * @param numOfExpenses number of "latest" expenses to retrieve
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses(int numOfExpenses){
		return null;
	}
	
	/**
	 * Adds an expense into the database.
	 */
	public void addExpense(int amount, Date date, String title, Category category, SimpleScanImage image){
		// TODO: Image id? Shared id?
	}
	
	/**
	 * Gets the user's budget(s?)
	 * @return Budget
	 */
	public Budget getBudget(){
		return null;		
	}
	
	
}
