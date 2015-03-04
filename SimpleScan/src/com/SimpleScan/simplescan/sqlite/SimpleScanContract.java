package com.SimpleScan.simplescan.sqlite;

import android.provider.BaseColumns;

/**
 * @author pearse1
 *
 * Represents a database "contract."  In other words, a schema for our database.
 */
public class SimpleScanContract {

	public static final  int    DATABASE_VERSION = 1;
	public static final  String DATABASE_NAME = "simplescan.db";
	private static final String TEXT_TYPE = " TEXT";
	private static final String INTEGER_TYPE = " INTEGER";	
	private static final String COMMA_SEP = ",";
	
	/**
	 * Empty default constructor so a user cannot create an instance.
	 */
	public SimpleScanContract(){}
	
	/** Represents a User table */
	public static abstract class UserTable implements BaseColumns {
	    public static final String TABLE_NAME = "user";
	    public static final String COLUMN_NAME_USERNAME = "username";
	    //public static final String COLUMN_NAME_PICTUREID = "pictureid";
	
	    public static final String CREATE_TABLE = "CREATE TABLE " +
	            TABLE_NAME + " (" +
	            _ID + " INTEGER PRIMARY KEY," +
	            COLUMN_NAME_USERNAME + TEXT_TYPE + ");";
	            
	    public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
	
	/** Represents a expense table */
	public static abstract class ExpenseTable implements BaseColumns {
		public static final String TABLE_NAME = "expense";
		public static final String COLUMN_NAME_AMOUNT = "amount";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_CATEGORY_ID = "categoryid";
		public static final String COLUMN_NAME_SHARED_ID = "sharedid";
		public static final String COLUMN_NAME_IMAGE_ID = "imageid";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
	            TABLE_NAME + " (" +
	            _ID + " INTEGER PRIMARY KEY," +
	            COLUMN_NAME_AMOUNT + TEXT_TYPE + COMMA_SEP +	            
	            COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
	            COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	            COLUMN_NAME_CATEGORY_ID + TEXT_TYPE + COMMA_SEP +	
	            COLUMN_NAME_SHARED_ID + TEXT_TYPE + COMMA_SEP +	
	            COLUMN_NAME_IMAGE_ID + TEXT_TYPE + ");";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	public static abstract class SharedExpenseTable implements BaseColumns {
		public static final String TABLE_NAME = "sharedexpense";
		public static final String COLUMN_NAME_EXPENSE_ID = "expenseid";
		public static final String COLUMN_NAME_CONTACT_ID = "contactid";
		public static final String COLUMN_NAME_HAS_PAID = "haspaid";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
	            TABLE_NAME + " (" +
	            _ID + " INTEGER PRIMARY KEY," +
	            COLUMN_NAME_EXPENSE_ID + TEXT_TYPE + COMMA_SEP +	            
	            COLUMN_NAME_CONTACT_ID + TEXT_TYPE + COMMA_SEP +
	            COLUMN_NAME_HAS_PAID + TEXT_TYPE + ");";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
	
	/** Represents a category table */
	public static abstract class CategoryTable implements BaseColumns {
		public static final String TABLE_NAME = "category";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_COLOR = "color";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
	            TABLE_NAME + " (" +
	            _ID + INTEGER_TYPE + COMMA_SEP +	            
	            COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +	            
	            COLUMN_NAME_COLOR + TEXT_TYPE + COMMA_SEP + 
	            "PRIMARY KEY(_ID, " + COLUMN_NAME_TITLE + "));"; // We have two primary keys for this table
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
