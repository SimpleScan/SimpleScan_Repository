package com.SimpleScan.simplescan.sqlite;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;

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
	 * Gets the user's info.
	 * 
	 * @return user info
	 */
	public User getUserInfo(){		
		db = dbHelper.getReadableDatabase();
		
		User user = new User();
		
		// Define a projection that specifies which columns from the database
        String[] columns = {
                UserTable._ID,
                UserTable.COLUMN_NAME_USERNAME,                
        };

        Cursor c = queryDatabase(UserTable.TABLE_NAME, columns, null, null, null, null, null);

        c.moveToFirst();
        user.setName(c.getString(c.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERNAME)));
		
		return user;
	}
	
	/**
	 * Gets a list of all expenses.
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses(){
		db = dbHelper.getReadableDatabase();
		
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
	 * Adds an expense to the database
	 * 
	 * @param amount the amount
	 * @param date the date of the expense
	 * @param title the title/description
	 * @param category the category
	 * @param image a scanned image of the expense.
	 */
	public void addExpense(Expense expense){
		
	}
	
	/**
	 * Gets the user's budget(s?)
	 * @return Budget
	 */
	public Budget getBudget(){
		return null;		
	}
	
	/**
	 * Creates a new budget in the database
	 * 
	 * @param amount the amount
	 * @param startDate start date
	 * @param endDate end date
	 */
	public void createBudget(int amount, Date startDate, Date endDate){
		
	}
	
	/**
	 * Updates the budget to new values
	 * 
	 * @param amount the amount
	 * @param startDate start date
	 * @param endDate end date
	 */
	public void updateBudget(int amount, Date startDate, Date endDate){
		
	}
	
	/**
	 * Closes any remaining open connections.
	 */
	public void close(){
		if(dbHelper != null){
			dbHelper.close();
		}
		if(db != null){
			db.close();
		}
	}
	
	private Cursor queryDatabase(String tableName, String [] columns, String selection,
								String [] selectionArgs, String groupBy, String having, String orderBy){
		Cursor c = db.query(
                tableName,  // The table to query
                columns,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                groupBy,                                     // don't group the rows
                having,                                     // don't filter by row groups
                orderBy                                 // The sort order
        );
		
		return c;
	}
}
