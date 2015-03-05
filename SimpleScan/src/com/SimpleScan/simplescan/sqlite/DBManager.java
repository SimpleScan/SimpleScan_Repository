package com.SimpleScan.simplescan.sqlite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Category;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.BudgetTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.ExpenseTable;
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
		return getAllExpenses(""); // no LIMIT
	}
	
	/**
	 * Gets a list of expenses.
	 * @param numOfExpenses number of "latest" expenses to retrieve
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses(int numOfExpenses){
		return getAllExpenses(" LIMIT " + numOfExpenses); // has LIMIT
	}
	
	/**
	 * Helper function for getExpenses
	 * @param limit number of last expenses to retrieve.
	 * @return List<Expense>
	 */
	private List<Expense> getAllExpenses(String limit){
		db = dbHelper.getReadableDatabase();		
		
		// Define a projection that specifies which columns from the database
        String[] columns = {
                ExpenseTable._ID,
                ExpenseTable.COLUMN_NAME_AMOUNT,
                ExpenseTable.COLUMN_NAME_DATE,
                ExpenseTable.COLUMN_NAME_TITLE,
        };
		
        String sortBy = ExpenseTable.COLUMN_NAME_DATE + " DESC " + limit;
        
        Cursor c = queryDatabase(ExpenseTable.TABLE_NAME, columns, null, null, null, null, sortBy);        
        
		return loadExpenses(c);		
	}
	
	/**
	 * Moves over the returned results from the database and loads the information into
	 * a list of expenses.
	 * 
	 * @param c Cursor
	 * @return List<Expense>
	 */
	private List<Expense> loadExpenses(Cursor c){
		List<Expense> expenses = new ArrayList<Expense>();
		c.moveToFirst();
		
		while(!c.isAfterLast()){
        	Expense e = new Expense();
			e.setTitle(c.getString(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_TITLE)));
			e.setAmount(c.getDouble(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_AMOUNT)));
			e.setDate(c.getString(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_DATE)));
			
			expenses.add(e);
			c.moveToNext();
        }
		
		return expenses;
	}
	
	/**
	 * Adds an expense to the database
	 * 
	 * @param amount the amount
	 * @param date the date of the expense
	 * @param title the title/description
	 * TODO: Implement these parameters
	 * @param category the category
	 * @param image a scanned image of the expense.
	 */
	public void addExpense(double amount, String date, String title, Category category, String imageTitle, String imagePath){
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
        values.put(ExpenseTable.COLUMN_NAME_AMOUNT, amount);
        values.put(ExpenseTable.COLUMN_NAME_DATE, date);
        values.put(ExpenseTable.COLUMN_NAME_TITLE, title);     
        // TODO: Optional category/imagetitle/imagepath
        db.insert(ExpenseTable.TABLE_NAME, null, values);
        //insertIntoDatabase(ExpenseTable.TABLE_NAME, null, values);
        
        updateBudget(amount);
	}
	
	/**
	 * Gets the user's budget(s?)
	 * @return Budget
	 */
	public Budget getBudget(){
		db = dbHelper.getReadableDatabase();		
				
		// Define a projection that specifies which columns from the database
        String[] columns = {
                BudgetTable._ID,
                BudgetTable.COLUMN_NAME_ORIGINAL_AMOUNT,
                BudgetTable.COLUMN_NAME_CURRENT_AMOUNT,
                BudgetTable.COLUMN_NAME_START_DATE,
                BudgetTable.COLUMN_NAME_END_DATE,
        };        
        
        Cursor c = queryDatabase(BudgetTable.TABLE_NAME, columns, null, null, null, null, null);        
        c.moveToFirst();
        Budget b = new Budget();
        b.setAmount(c.getDouble(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT)));
        b.setStartDate(c.getString(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_START_DATE)));
        b.setEndDate(c.getString(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_END_DATE)));
        
		return b;	
	}
	
	/**
	 * Creates a new budget in the database
	 * 
	 * @param amount the amount
	 * @param startDate start date
	 * @param endDate end date
	 */
	public void createBudget(double amount, String startDate, String endDate){
		
	}
	
	/**
	 * Updates the budget to new values
	 * 
	 * @param amount the amount
	 * @param startDate start date
	 * @param endDate end date
	 */
	public void updateBudget(double amount){
		db = dbHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
        String[] columns = {                
                BudgetTable.COLUMN_NAME_CURRENT_AMOUNT,                
        };
		
        String sortBy = BudgetTable.COLUMN_NAME_CURRENT_AMOUNT + " DESC LIMIT 1";
        
        Cursor c = queryDatabase(BudgetTable.TABLE_NAME, columns, null, null, null, null, sortBy); 
        c.moveToFirst();
        double currAmount = c.getDouble(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT));
		currAmount -= amount;
		db.close();
		
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT, currAmount);
		
		db.update(BudgetTable.TABLE_NAME, values, null, null);
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
	
	/**
	 * Main query function.
	 * 
	 * @param tableName the table name
	 * @param columns the columns to return
	 * @param selection WHERE columns
	 * @param selectionArgs WHERE values
	 * @param groupBy group by
	 * @param having having
	 * @param orderBy ordering/limit
	 * @return
	 */
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
	
	/**
	 * Main insert function
	 * 
	 * @param tableName the table name
	 * @param nullColumnHack real android sqlite docs on this one
	 * @param values values to be inserted
	 */
	private void insertIntoDatabase(String tableName, String nullColumnHack, ContentValues values){
		db.insert(tableName, nullColumnHack, values);
	}
}
