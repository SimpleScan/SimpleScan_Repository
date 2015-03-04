package com.SimpleScan.simplescan.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author pearse1
 *
 * SQLite helper class that creates/updates our database.  This class also provides us access to the database
 * by invoking parent functions to retrieve a writable or readable database.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, SimpleScanContract.DATABASE_NAME, null, SimpleScanContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SimpleScanContract.BudgetTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.CategoryTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.ContactTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.ExpenseTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.ReminderTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.SharedExpenseTable.CREATE_TABLE);
        db.execSQL(SimpleScanContract.UserTable.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.execSQL(SimpleScanContract.BudgetTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.CategoryTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.ContactTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.ExpenseTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.ReminderTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.SharedExpenseTable.DELETE_TABLE);
        db.execSQL(SimpleScanContract.UserTable.DELETE_TABLE);
        onCreate(db);
    }
}