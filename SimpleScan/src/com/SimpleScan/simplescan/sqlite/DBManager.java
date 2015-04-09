package com.SimpleScan.simplescan.sqlite;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Category;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.BudgetTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.CategoryTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.ExpenseTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;

/**
 * @author pearse1
 *
 *         This class provides an interface for users to interact with the
 *         database.
 */
public class DBManager {

	private DBHelper dbHelper = null;
	private SQLiteDatabase db = null;

	/**
	 * Default Constructor
	 */
	public DBManager(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * Gets the user's info.
	 * 
	 * @return user info
	 */
	public User getUserInfo() {
		db = dbHelper.getReadableDatabase();

		User user = new User();

		// Define a projection that specifies which columns from the database
		//String[] columns = { UserTable._ID, UserTable.COLUMN_NAME_USERNAME,UserTable.COLUMN_NAME_USERID, };
		String[] columns = { UserTable._ID, UserTable.COLUMN_NAME_USERNAME,};
		Cursor c = queryDatabase(UserTable.TABLE_NAME, columns, null, null,
				null, null, null);
		if( c != null && c.moveToFirst())
		{
			Log.i("getUserInfo -->"," exist");
			
			user.setName(c.getString(c
					.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERNAME)));
			//user.setId(c.getString(c
					//.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERID)));
		}
		return user;
	}
	
	/**
	 * added by Kevin Chen (may need code reivew with Tyler)
	 * Adds the Profile info to the database
	 * @param name the User name
	 * @param color the android device ID
	 */
	public void updateUser(String name) {
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { UserTable.COLUMN_NAME_USERNAME, };

		Cursor c = queryDatabase(UserTable.TABLE_NAME, columns, null, null,
				null, null, null);
		// create for the existence 
		if( c != null && c.moveToFirst() )
		{			
			db = dbHelper.getWritableDatabase();
			Log.i("update User -->"," update");
			ContentValues values = new ContentValues();
			values.put(UserTable.COLUMN_NAME_USERNAME, name);	
			db.update(UserTable.TABLE_NAME, values, null, null);
			
		}
		else
		{
			db = dbHelper.getWritableDatabase();
			Log.i("update User -->"," Insert");
			ContentValues values = new ContentValues();
	        values.put(UserTable.COLUMN_NAME_USERNAME, name);
	        //values.put(UserTable.COLUMN_NAME_USERID, id);
	        db.insert(UserTable.TABLE_NAME, null, values);
		}
	}
	

	
	
	
	/**
	 * Gets a list of all expenses.
	 * 
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses() {
		return getAllExpenses(""); // no LIMIT
	}

	/**
	 * Gets a list of expenses.
	 * 
	 * @param numOfExpenses
	 *            number of "latest" expenses to retrieve
	 * @return List<Expense>
	 */
	public List<Expense> getExpenses(int numOfExpenses) {
		return getAllExpenses(" LIMIT " + numOfExpenses); // has LIMIT
	}

	/**
	 * Helper function for getExpenses
	 * 
	 * @param limit
	 *            number of last expenses to retrieve.
	 * @return List<Expense>
	 */
	private List<Expense> getAllExpenses(String limit) {
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { ExpenseTable._ID, ExpenseTable.COLUMN_NAME_AMOUNT,
				ExpenseTable.COLUMN_NAME_DATE, ExpenseTable.COLUMN_NAME_TITLE, };

		String sortBy = ExpenseTable.COLUMN_NAME_DATE + " DESC " + limit;

		Cursor c = queryDatabase(ExpenseTable.TABLE_NAME, columns, null, null,
				null, null, sortBy);
		
		return loadExpenses(c);
	}

	/**
	 * Moves over the returned results from the database and loads the
	 * information into a list of expenses.
	 * 
	 * @param c
	 *            Cursor
	 * @return List<Expense>
	 */
	private List<Expense> loadExpenses(Cursor c) {
		List<Expense> expenses = new ArrayList<Expense>();
		c.moveToFirst();

		while (!c.isAfterLast()) {
			Expense e = new Expense();
			e.setId(c.getInt(c.getColumnIndexOrThrow(ExpenseTable._ID)));
			e.setTitle(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_TITLE)));
			e.setAmount(c.getDouble(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_AMOUNT)));
			e.setDate(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_DATE)));

			expenses.add(e);
			c.moveToNext();
		}

		return expenses;
	}

	/**
	 * Adds an expense to the database
	 * 
	 * @param amount
	 *            the amount
	 * @param date
	 *            the date of the expense
	 * @param title
	 *            the title/description TODO: Implement these parameters
	 * @param category
	 *            the category
	 * @param image
	 *            a scanned image of the expense.
	 */
	public void addExpense(double amount, String date, String title,
			Category category, String imageTitle, String imagePath) {
		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(ExpenseTable.COLUMN_NAME_AMOUNT, amount);
		values.put(ExpenseTable.COLUMN_NAME_DATE, date);
		values.put(ExpenseTable.COLUMN_NAME_TITLE, title);
		// TODO: Optional category/imagetitle/imagepath
		// db.insert(ExpenseTable.TABLE_NAME, null, values);
		insert(ExpenseTable.TABLE_NAME, null, values);

		updateBudget(amount);
	}
	
	public void editExpense(int id, double amount, String date, String title,
			String imageTitle, String imagePath){
	
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		if(amount >= 0.0){
			values.put(ExpenseTable.COLUMN_NAME_AMOUNT, amount);
		}
		if(date != null){
			values.put(ExpenseTable.COLUMN_NAME_DATE, date);
		}
		if(title != null){
			values.put(ExpenseTable.COLUMN_NAME_TITLE, title);
		}
		if(imageTitle != null){
			values.put(ExpenseTable.COLUMN_NAME_IMAGE_TITLE , imageTitle);
		}
		if(imagePath != null){
			values.put(ExpenseTable.COLUMN_NAME_IMAGE_PATH, imagePath);
		}
		
		String[] whereArgs = {Integer.toString(id), };
		
		db.update(ExpenseTable.TABLE_NAME, values, "_id=?", whereArgs);
	}
	
	/**
	 * Creates a new budget in the database, affectively ending the last budget.
	 * 
	 * @param amount
	 *            the amount
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 */
	public void createBudget(double amount, String startDate, String endDate) {
		// Date Format: yyyy-MM-dd hh:mm:ss
		// Also, see here for formatting a date string properly:
		// http://stackoverflow.com/questions/454315/how-do-you-format-date-and-time-in-android		

		// We update the previous budget to end on the start date of this new budget.
		// We are not allowing to have multiple budgets at this time.
		endBudget(startDate);
		
		// Insert the new values.
		ContentValues values = new ContentValues();
		values.put(BudgetTable.COLUMN_NAME_ORIGINAL_AMOUNT, amount);
		values.put(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT, amount);
		values.put(BudgetTable.COLUMN_NAME_START_DATE, startDate);
		values.put(BudgetTable.COLUMN_NAME_END_DATE, endDate);
		insert(BudgetTable.TABLE_NAME, null, values);		
	}

	/**
	 * Updates the latest budget to end of the new budget's start date.
	 * 
	 * @param endDate the new end date
	 */
	private void endBudget(String endDate) {
		db = dbHelper.getWritableDatabase();
		
		try{
			db.execSQL("UPDATE " + BudgetTable.TABLE_NAME + 
				   " SET " + BudgetTable.COLUMN_NAME_END_DATE + " = '" + endDate +
				   "' WHERE " + BudgetTable._ID + 
				   " = (SELECT max(" + BudgetTable._ID + ") FROM " + BudgetTable.TABLE_NAME + ");");
		} catch (SQLException e){
			// log something here but for now do nothing.
		}
	}

	/**
	 * Updates the budget to new values
	 * 
	 * @param amount
	 *            the amount
	 * @param startDate
	 *            start date
	 * @param endDate
	 *            end date
	 */
	public void updateBudget(double amount) {
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { BudgetTable.COLUMN_NAME_CURRENT_AMOUNT, };

		String sortBy = BudgetTable.COLUMN_NAME_CURRENT_AMOUNT
				+ " DESC LIMIT 1";

		Cursor c = queryDatabase(BudgetTable.TABLE_NAME, columns, null, null,
				null, null, sortBy);
		c.moveToFirst();
		double currAmount = c.getDouble(c
				.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT));
		currAmount -= amount;
		db.close();

		db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT, currAmount);

		db.update(BudgetTable.TABLE_NAME, values, null, null);
	}
	
	/**
	 * Gets the current/latest budget being used.
	 * 
	 * @return the latest budget
	 */
	public Budget getBudget(){
		return getBudgets().get(0);
	}

	/**
	 * Gets the user's budget(s?)
	 * 
	 * @return Budget
	 */
	public List<Budget> getBudgets() {
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { BudgetTable._ID,
				BudgetTable.COLUMN_NAME_ORIGINAL_AMOUNT,
				BudgetTable.COLUMN_NAME_CURRENT_AMOUNT,
				BudgetTable.COLUMN_NAME_START_DATE,
				BudgetTable.COLUMN_NAME_END_DATE, };

		String sortBy = BudgetTable._ID + " DESC";

		Cursor c = queryDatabase(BudgetTable.TABLE_NAME, columns, null, null,
				null, null, sortBy);
		
		return loadBudgets(c);
	}

	/**
	 * This function loads returned budget records into a list.
	 * 
	 * @param c the cursor to the return rows
	 * @return List<Budget>
	 */
	private List<Budget> loadBudgets(Cursor c) {
		List<Budget> budgets = new ArrayList<Budget>();
		c.moveToFirst();

		while (!c.isAfterLast()) {
			Budget b = new Budget();
			b.setOrigAmount(c.getDouble(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_ORIGINAL_AMOUNT)));
			b.setCurrAmount(c.getDouble(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_CURRENT_AMOUNT)));
			b.setStartDate(c.getString(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_START_DATE)));
			b.setEndDate(c.getString(c.getColumnIndexOrThrow(BudgetTable.COLUMN_NAME_END_DATE)));
			
			budgets.add(b);
			c.moveToNext();
		}
		
		return budgets;
	}

	/**
	 * added by Kevin Chen (may need code reivew with Tyler)
	 * get a list of categories from the database
	 */
	public List<Category>getCategories()
	{
		db = dbHelper.getReadableDatabase();		
		List<Category> cateList = new ArrayList<Category>();
				
		// Define a projection that specifies which columns from the database
        String[] columns = {
        		CategoryTable.COLUMN_NAME_TITLE,
        		CategoryTable.COLUMN_NAME_COLOR,
        };
        Cursor c = queryDatabase(CategoryTable.TABLE_NAME, columns, null, null, null, null, null);      
        c.moveToFirst();
        while(!c.isAfterLast())
        {
        	Category cate = new Category();
        	cate.setTitle(c.getString(c.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_TITLE)));
        	cate.setColor(c.getString(c.getColumnIndexOrThrow(CategoryTable.COLUMN_NAME_COLOR)));
        	cateList.add(cate);
        	c.moveToNext();
        }
		return cateList;	
	}
	
	/**
	 * added by Kevin Chen (may need code reivew with Tyler)
	 * Adds a category to the database
	 * @param name the category name
	 * @param color the category color
	 */
	public void addCategory(String name, String color)
	{
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
        values.put(CategoryTable.COLUMN_NAME_TITLE, name);
        values.put(CategoryTable.COLUMN_NAME_COLOR, color);

        db.insert(CategoryTable.TABLE_NAME, null, values);
	}
	/**
	 * Closes any remaining open connections.
	 */
	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
		if (db != null) {
			db.close();
		}
	}

	/**
	 * Main query function.
	 * 
	 * @param tableName
	 *            the table name
	 * @param columns
	 *            the columns to return
	 * @param selection
	 *            WHERE columns
	 * @param selectionArgs
	 *            WHERE values
	 * @param groupBy
	 *            group by
	 * @param having
	 *            having
	 * @param orderBy
	 *            ordering/limit
	 * @return
	 */
	private Cursor queryDatabase(String tableName, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		Cursor c = db.query(tableName, // The table to query
				columns, // The columns to return
				selection, // The columns for the WHERE clause
				selectionArgs, // The values for the WHERE clause
				groupBy, // don't group the rows
				having, // don't filter by row groups
				orderBy // The sort order
				);

		return c;
	}

	/**
	 * Main insert function
	 * 
	 * @param tableName
	 *            the table name
	 * @param nullColumnHack
	 *            real android sqlite docs on this one
	 * @param values
	 *            values to be inserted
	 */
	private void insert(String tableName, String nullColumnHack,
			ContentValues values) {
		db.insert(tableName, nullColumnHack, values);
	}
}
