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
import com.SimpleScan.simplescan.Entities.Reminder;
import com.SimpleScan.simplescan.Entities.SharedExpense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.BudgetTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.CategoryTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.ExpenseTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.ReminderTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.SharedExpenseTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;

/**
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
		String[] columns = { UserTable._ID, UserTable.COLUMN_NAME_USERNAME,UserTable.COLUMN_NAME_USERID, };
		Cursor c = queryDatabase(UserTable.TABLE_NAME, columns, null, null,
				null, null, null);
		if( c != null && c.moveToFirst())
		{
			Log.i("getUserInfo -->"," exist");
			
			user.setName(c.getString(c
					.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERNAME)));
			user.setId(c.getString(c
					.getColumnIndexOrThrow(UserTable.COLUMN_NAME_USERID)));
		}
		db.close();
		return user;
	}
	
	/**
	 * 
	 * Adds the Profile info to the database
	 * 
	 * @param name the User name
	 * @param the android device ID
	 */
	public void updateUser(String name, String userId) {
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { UserTable.COLUMN_NAME_USERNAME, };

		Cursor c = queryDatabase(UserTable.TABLE_NAME, columns, null, null,
				null, null, null);
		// check for the existence, if exists, update the user name 
		if( c != null && c.moveToFirst() )
		{			
			db = dbHelper.getWritableDatabase();
			Log.i("update User -->"," update");
			ContentValues values = new ContentValues();
			values.put(UserTable.COLUMN_NAME_USERNAME, name);	
			db.update(UserTable.TABLE_NAME, values, null, null);		
			db.close();
		}
		else
		{
			db = dbHelper.getWritableDatabase();
			Log.i("update User -->"," Insert");
			ContentValues values = new ContentValues();
	        values.put(UserTable.COLUMN_NAME_USERNAME, name);
	        // only insert the android device id if the it doesn't exist in the DB
	        values.put(UserTable.COLUMN_NAME_USERID, userId);
	        db.insert(UserTable.TABLE_NAME, null, values);
	        db.close();
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
				ExpenseTable.COLUMN_NAME_DATE, ExpenseTable.COLUMN_NAME_TITLE, 
				ExpenseTable.COLUMN_NAME_CATEGORY_NAME, ExpenseTable.COLUMN_NAME_IMAGE_TITLE,
				ExpenseTable.COLUMN_NAME_IMAGE_PATH, };

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
		List<Category> categories = getCategories();

		c.moveToFirst();

		while (!c.isAfterLast()) {
			Expense e = new Expense();
			e.setId(c.getInt(c.getColumnIndexOrThrow(ExpenseTable._ID)));
			try{
				// an exception will be thrown if sharedId is null
				int sharedId = c.getInt(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_SHARED_ID));
				e.setSharedId(sharedId);
			} catch (Exception exception) 
			{
				exception.printStackTrace();
			}
			e.setTitle(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_TITLE)));
			e.setAmount(c.getDouble(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_AMOUNT)));
			e.setDate(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_DATE)));
			e.setImageTitle(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_IMAGE_TITLE)));
			e.setImagePath(c.getString(c
					.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_IMAGE_PATH)));
			
			// get category name and find its category color.
			String cName = c.getString(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_CATEGORY_NAME));
			if(cName != null){
				Category category = findCategoryInList(categories, cName);
				e.setCategory(category);
			}

			expenses.add(e);
			c.moveToNext();
		}

		return expenses;
	}

	/**
	 * Private helper function that finds a category in a list given a name.
	 * 
	 * @param categories the list to search through
	 * @param categoryName the name of the category to be searched for.
	 * @return a category from the list or null
	 */
	private Category findCategoryInList(List<Category> categories, String categoryName) {
		for(Category c : categories){
			if(c.getTitle().equals(categoryName))
				return c;
		}
		return null;
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
		
		if(category != null){
			values.put(ExpenseTable.COLUMN_NAME_CATEGORY_NAME, category.getTitle());
		}
		
		if(imageTitle != null) {
			values.put(ExpenseTable.COLUMN_NAME_IMAGE_TITLE, imageTitle);
		}
		
		if(imagePath != null) { 
			values.put(ExpenseTable.COLUMN_NAME_IMAGE_PATH, imagePath);
		}

		insert(ExpenseTable.TABLE_NAME, null, values);

		updateBudget(amount);
	}
	
	/**
	 * Edits an expense already in the database.
	 * 
	 * @param id the expense's id
	 * @param sharedExpenseId the shared epense id
	 * @param amount the new amount
	 * @param date the new date
	 * @param title the new title
	 * @param category the new title
	 * @param imageTitle the new image title
	 * @param imagePath the new image path
	 */
	public void editExpense(int id, int sharedExpenseId, double amount, String date, String title, Category category,
			String imageTitle, String imagePath){
	
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		if(sharedExpenseId >= 0)
		{
			values.put(ExpenseTable.COLUMN_NAME_SHARED_ID, sharedExpenseId);
		}
		if(amount >= 0.0){
			values.put(ExpenseTable.COLUMN_NAME_AMOUNT, amount);
		}		
		if(date != null){
			values.put(ExpenseTable.COLUMN_NAME_DATE, date);
		}
		if(title != null){
			values.put(ExpenseTable.COLUMN_NAME_TITLE, title);
		}
		if(category != null){
			values.put(ExpenseTable.COLUMN_NAME_CATEGORY_NAME, category.getTitle());
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
	 * Deletes a specific expense (along with it's shared expense if it exists) from the expense table (and possibly shared
	 * expense table)
	 * 
	 * @param expenseId the id of the expense
	 */
	public void deleteExpense(int expenseId){
		SharedExpense se = getSharedExpense(expenseId);
		
		if(se != null){
			int sharedExpenseId = se.getId();
			
			deleteSharedExpense(sharedExpenseId);
		}
		
		String[] whereArgs = {Integer.toString(expenseId), };		
		db = dbHelper.getWritableDatabase();
		db.delete(ExpenseTable.TABLE_NAME, "_id=?", whereArgs);
		close();
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
	 * Get all user reminders.
	 * 
	 * @return list of reminders
	 */
	public List<Reminder> getReminders(){
		db = dbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		String[] columns = { ReminderTable._ID, ReminderTable.COLUMN_NAME_TITLE,
				ReminderTable.COLUMN_NAME_BILLED_AMOUNT, ReminderTable.COLUMN_NAME_PAID_AMOUNT, 
				ReminderTable.COLUMN_NAME_DUE_DATE, ReminderTable.COLUMN_NAME_REMIND_DATE,
				ReminderTable.COLUMN_NAME_REMIND_AGAIN };

		String sortBy = ExpenseTable._ID + " ASC;";

		Cursor c = queryDatabase(ReminderTable.TABLE_NAME, columns, null, null,
				null, null, sortBy);
		List<Reminder> reminders = loadReminders(c);
		close();
		return reminders;
	}
	
	/**
	 * Creates a list of reminders from a cursor object
	 * 
	 * @param c cursor to sql table object
	 * @return list of reminders
	 */
	private List<Reminder> loadReminders(Cursor c) {
		List<Reminder> reminders = new ArrayList<Reminder>();
		
		c.moveToFirst();
		
		while(!c.isAfterLast()){
			Reminder r = new Reminder();
			r.setId(c.getInt(c.getColumnIndexOrThrow(ReminderTable._ID)));
			r.setTitle(c.getString(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_TITLE)));
			r.setBilledAmount(c.getDouble(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_BILLED_AMOUNT)));
			r.setPaidAmount(c.getDouble(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_PAID_AMOUNT)));
			r.setDueDate(c.getString(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_DUE_DATE)));
			r.setRemindDate(c.getString(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_REMIND_DATE)));
			r.setRemindAgain((c.getInt(c.getColumnIndexOrThrow(ReminderTable.COLUMN_NAME_REMIND_AGAIN))>0));
			
			reminders.add(r);
			c.moveToNext();
		}
		return reminders;
	}
	
	/**
	 * Adds a reminder to the database.
	 * 
	 * 
	 * @param title the title of the reminder
	 * @param billedAmount the original amount
	 * @param (kind of optional: input -1 if no value is to be inserted) paidAmount the amount that has been paid
	 * @param dueDate the due date
	 * @param (optional) remindDate when to remind the user
	 */
	public void addReminder(String title, double billedAmount, double paidAmount, String dueDate, String remindDate, boolean remindAgain){
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(ReminderTable.COLUMN_NAME_TITLE, title);
		values.put(ReminderTable.COLUMN_NAME_BILLED_AMOUNT, billedAmount);		
		values.put(ReminderTable.COLUMN_NAME_DUE_DATE, dueDate);
		values.put(ReminderTable.COLUMN_NAME_REMIND_AGAIN, remindAgain);
		
		if(paidAmount >= 0.0){
			values.put(ReminderTable.COLUMN_NAME_PAID_AMOUNT, paidAmount);
		}
		if(remindDate != null){
			values.put(ReminderTable.COLUMN_NAME_REMIND_DATE, remindDate);
		}
		
		insert(ReminderTable.TABLE_NAME, null, values);
		close();
	}
	
	/**
	 * Edits an existing reminder in the database.
	 * 
	 * @param id the id
	 * @param title the title
	 * @param billedAmount the original amount
	 * @param paidAmount the amount paid
	 * @param dueDate the due date
	 * @param remindDate the date to remind the user
	 */
	public void editReminder(int id, String title, double billedAmount, double paidAmount,
							 String dueDate, String remindDate, boolean remindAgain){
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		if(title != null){
			values.put(ReminderTable.COLUMN_NAME_TITLE, title);
		}
		if(billedAmount >= 0.0){
			values.put(ReminderTable.COLUMN_NAME_BILLED_AMOUNT, billedAmount);
		}
		if(paidAmount >= 0.0){
			values.put(ReminderTable.COLUMN_NAME_PAID_AMOUNT, paidAmount);
		}
		if(dueDate != null){
			values.put(ReminderTable.COLUMN_NAME_DUE_DATE, dueDate);
		}
		if(remindDate != null){
			values.put(ReminderTable.COLUMN_NAME_REMIND_DATE, remindDate);
		}
		values.put(ReminderTable.COLUMN_NAME_REMIND_AGAIN, remindAgain);
		
		String[] whereArgs = {Integer.toString(id), };
		
		db.update(ReminderTable.TABLE_NAME, values, "_id=?", whereArgs);
		close();
	}
	
	/**
	 * Removes a specific reminder from the database
	 *  
	 * @param id the id
	 */
	public void deleteReminder(int id){
		db = dbHelper.getWritableDatabase();
		
		String[] whereArgs = {Integer.toString(id), };
		
		db.delete(ReminderTable.TABLE_NAME, "_id=?", whereArgs);
		close();
	}
	
	/**
	 * Adds a new shared expense into the shared expense table.
	 * 
	 * @param expenseId the expense id associated with the new shared expense
	 * @param userId1 user1
	 * @param hasPaid1 haspaid1
	 * @param userId2 user2
	 * @param hasPaid2 haspaid2
	 * @param userId3 user3
	 * @param hasPaid3 haspaid3
	 */
	public void addSharedExpense(int expenseId, String userId1, boolean hasPaid1,
								                String userId2, boolean hasPaid2,
								                String userId3, boolean hasPaid3){
		db = dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		
		if(userId1 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID1, userId1);
			values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID1, hasPaid1);
		}
		if(userId2 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID2, userId2);
			values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID2, hasPaid2);
		}
		if(userId3 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID3, userId3);
			values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID3, hasPaid3);
		}

        db.insert(SharedExpenseTable.TABLE_NAME, null, values);
        // retrieve the newly created sharedexpense's id for the expense table
        // Define a projection that specifies which columns from the database
 		String[] columns = { SharedExpenseTable._ID, };

 		String sortBy = SharedExpenseTable._ID + " DESC";

 		Cursor c = queryDatabase(SharedExpenseTable.TABLE_NAME, columns, null, null,
 				null, null, sortBy);
 		
 		c.moveToFirst();
 		int sharedId = c.getInt(c.getColumnIndexOrThrow(SharedExpenseTable._ID));
        // update the expense with the new sharedid
 		editExpense(expenseId, sharedId, -1, null, null, null, null, null);
	}
	
	/**
	 * Updates the shared expense with new values.
	 * 
	 * @param sharedId id
	 * @param userId1 userId1
	 * @param hasPaid1 haspaid1
	 * @param userId2 userId2
	 * @param hasPaid2 haspaid2
	 * @param userId3 userId3
	 * @param hasPaid3 haspaid3
	 */
	public void editSharedExpense(int sharedId, String userId1, boolean hasPaid1,
								                String userId2, boolean hasPaid2,
								                String userId3, boolean hasPaid3){
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		if(userId1 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID1, userId1);
			if(hasPaid1 == true)
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID1, 1);
			else
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID1, 0);
		}
		if(userId2 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID2, userId2);
			if(hasPaid2 == true)
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID2, 1);
			else
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID2, 0);
		}
		if(userId3 != null){
			values.put(SharedExpenseTable.COLUMN_NAME_CONTACT_ID3, userId3);
			if(hasPaid3 == true)
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID3, 1);
			else
				values.put(SharedExpenseTable.COLUMN_NAME_HAS_PAID3, 0);
		}	
		
		String[] whereArgs = {Integer.toString(sharedId), };
		
		db.update(SharedExpenseTable.TABLE_NAME, values, "_id=?", whereArgs);
		close();
	}
	
	/**
	 * Gets a single shared expense from the database
	 * 
	 * @param expenseId the id of the expense the shared expense belongs to.
	 * @return SharedExpense
	 */
	public SharedExpense getSharedExpense(int expenseId){
		db = dbHelper.getReadableDatabase();
		
		// Define a projection that specifies which columns from the database
		String[] columns = { ExpenseTable._ID, ExpenseTable.COLUMN_NAME_SHARED_ID, };		

		String[] whereArgs = {Integer.toString(expenseId), };
		
		Cursor c = queryDatabase(ExpenseTable.TABLE_NAME, columns, "_id=?", whereArgs,
				null, null, null);		
		
		try{
			c.moveToFirst();
			int sharedId = c.getInt(c.getColumnIndexOrThrow(ExpenseTable.COLUMN_NAME_SHARED_ID));
			
			String[] columns2 = { SharedExpenseTable._ID, SharedExpenseTable.COLUMN_NAME_CONTACT_ID1, SharedExpenseTable.COLUMN_NAME_HAS_PAID1,
					SharedExpenseTable.COLUMN_NAME_CONTACT_ID2, SharedExpenseTable.COLUMN_NAME_HAS_PAID2,
					SharedExpenseTable.COLUMN_NAME_CONTACT_ID3, SharedExpenseTable.COLUMN_NAME_HAS_PAID3, };
			
			String[] whereArgs2 = {Integer.toString(sharedId), };
			
			c = queryDatabase(SharedExpenseTable.TABLE_NAME, columns2, "_id=?", whereArgs2,
					null, null, null);
			
			SharedExpense sharedExpense = loadSharedExpense(c);
			close();
			return sharedExpense;
		} catch (Exception exception){
			exception.printStackTrace();
			close();
			return null;
		}
		
	}
	
	/**
	 * Creates a SharedExpense object from a database cursor object.
	 * 
	 * @param c the cursor
	 * @return SharedExpense
	 */
	public SharedExpense loadSharedExpense(Cursor c){
		SharedExpense e = new SharedExpense();
		c.moveToFirst();
		e.setId(c.getInt(c.getColumnIndexOrThrow(SharedExpenseTable._ID)));
		e.setUserId1(c.getString(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_CONTACT_ID1)));		
		e.setHasPaid1((c.getInt(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_HAS_PAID1))>0));
		e.setUserId2(c.getString(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_CONTACT_ID2)));		
		e.setHasPaid2((c.getInt(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_HAS_PAID2))>0));
		e.setUserId3(c.getString(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_CONTACT_ID3)));		
		e.setHasPaid3((c.getInt(c.getColumnIndexOrThrow(SharedExpenseTable.COLUMN_NAME_HAS_PAID3))>0));
		return e;
	}
	
	/**
	 * Deletes a specific shared expense from the shared expense table.
	 * 
	 * @param id the id of the shared expense.
	 */
	public void deleteSharedExpense(int id){
		db = dbHelper.getWritableDatabase();
		
		String[] whereArgs = {Integer.toString(id), };
		
		db.delete(SharedExpenseTable.TABLE_NAME, "_id=?", whereArgs);
		close();
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
