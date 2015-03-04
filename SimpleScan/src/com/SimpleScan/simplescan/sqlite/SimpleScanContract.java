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
	private static final String DATE_TYPE = " TEXT";  // SQLite doesn't have a Date type (Only TEXT, REAL, INTEGER)
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
		public static final String COLUMN_NAME_CATEGORY = "category";
		public static final String COLUMN_NAME_IMAGEID = "imageid";
		
		public static final String CREATE_TABLE = "CREATE TABLE " +
	            TABLE_NAME + " (" +
	            _ID + " INTEGER PRIMARY KEY," +
	            COLUMN_NAME_AMOUNT + TEXT_TYPE + ", " +	            
	            COLUMN_NAME_DATE + TEXT_TYPE + ", " +
	            COLUMN_NAME_TITLE + TEXT_TYPE + ", " +
	            COLUMN_NAME_CATEGORY + TEXT_TYPE + ", " +	            
	            COLUMN_NAME_IMAGEID + TEXT_TYPE + ");";
		public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
	}
}
