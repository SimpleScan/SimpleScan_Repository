package com.SimpleScan.simplescan.test;

import com.SimpleScan.simplescan.sqlite.SimpleScanContract;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.ExpenseTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.BudgetTable;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.CategoryTable;

/**
 * @author pearse1
 *
 * Contains test data and scripts for testing database functionality.
 */
public class DBTestScripts {

	public static abstract class UserScripts {	    
	    public static final String USERNAME1 = "tonyp";
	    
	    public static final String INSERT_USER1 = "INSERT INTO " + 
	    		UserTable.TABLE_NAME +  "('" + UserTable.COLUMN_NAME_USERNAME + "') VALUES" + 
	    		  "('" + USERNAME1 + "');";
	}
	
	public static abstract class BudgetScripts {
		public static final double ORIGINAL_AMOUNT1 = 500.00;
		public static final double CURRENT_AMOUNT1 = 218.01;
		public static final String START_DATE = "2015-02-14 12:00:00";
		public static final String END_DATE = "2015-03-14 12:00:00";
		
		public static final String INSERT_BUDGET1 = "INSERT INTO " + 
				BudgetTable.TABLE_NAME + "('" + BudgetTable.COLUMN_NAME_ORIGINAL_AMOUNT + "', '" + 
				BudgetTable.COLUMN_NAME_CURRENT_AMOUNT + "', '" + BudgetTable.COLUMN_NAME_START_DATE + "', '" +
				BudgetTable.COLUMN_NAME_END_DATE + "') VALUES" + 
				"('" + ORIGINAL_AMOUNT1 + "', '" + CURRENT_AMOUNT1 + "', '" + 
				START_DATE + "', '" + END_DATE + "');";
	}
	
	public static abstract class ExpenseScripts {
		// Date Format: yyyy-MM-dd HH:mm:ss
		/* Expenses */
		public static final double AMOUNT1 = 24.38;
		public static final String DATE1 = "2015-03-01 01:25:25";
		public static final String TITLE1 = "Water Bill";
		public static final String CATEGORY_ID1 = "";
		public static final String SHARED_ID1 = "";				
		
		public static final double AMOUNT2 = 136.25;
		public static final String DATE2 = "2015-03-01 00:50:14";
		public static final String TITLE2 = "Groceries";
		public static final String CATEGORY_ID2 = "";
		public static final String SHARED_ID2 = "";		
		
		public static final double AMOUNT3 = 14.14;
		public static final String DATE3 = "2015-02-28 14:15:16";
		public static final String TITLE3 = "Football";
		public static final String CATEGORY_ID3 = "";
		public static final String SHARED_ID3 = "";		
		
		public static final double AMOUNT4 = 31.11;
		public static final String DATE4 = "2015-02-27 12:30:42";
		public static final String TITLE4 = "Chipotle Lunch";
		public static final String CATEGORY_ID4 = "";
		public static final String SHARED_ID4 = "";		
		
		public static final double AMOUNT5 = 76.11;
		public static final String DATE5 = "2015-02-25 20:05:11";
		public static final String TITLE5 = "421 book";
		public static final String CATEGORY_ID5 = "";
		public static final String SHARED_ID5 = "";	
		
		public static final double AMOUNT6 = 100.00;
		public static final String DATE6 = "2015-03-04 10:08:39";
		public static final String TITLE6 = "breakfast";
		public static final String CATEGORY_ID6 = "";
		public static final String SHARED_ID6 = "";
		
		/* Expense Scripts No Category/SharedID*/
		public static final String INSERT_EXPENSE1_5 = "INSERT INTO " + 
				ExpenseTable.TABLE_NAME + " ('" + ExpenseTable.COLUMN_NAME_AMOUNT + "', '" + 
				ExpenseTable.COLUMN_NAME_DATE + "', '" + ExpenseTable.COLUMN_NAME_TITLE + "') VALUES " + 
				"('" + AMOUNT1 + "', '" + DATE1 + "', '" + TITLE1 + "')," + 
				"('" + AMOUNT2 + "', '" + DATE2 + "', '" + TITLE2 + "')," + 
				"('" + AMOUNT3 + "', '" + DATE3 + "', '" + TITLE3 + "')," + 
				"('" + AMOUNT4 + "', '" + DATE4 + "', '" + TITLE4 + "')," + 
				"('" + AMOUNT5 + "', '" + DATE5 + "', '" + TITLE5 + "');"; 		
	}

}
