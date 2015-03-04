package com.SimpleScan.simplescan.test;

import com.SimpleScan.simplescan.sqlite.SimpleScanContract;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;;

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
	
	public static abstract class ExpenseScripts {
		/* Expenses */
		public static final String AMOUNT1 = "";
		public static final String DATE1 = "";
		public static final String TITLE1 = "";
		public static final String CATEGORY_ID1 = "";
		public static final String SHARED_ID1 = "";
		public static final String IMAGE_ID1 = "";		
		
		public static final String AMOUNT2 = "";
		public static final String DATE2 = "";
		public static final String TITLE2 = "";
		public static final String CATEGORY_ID2 = "";
		public static final String SHARED_ID2 = "";
		public static final String IMAGE_ID2 = "";
		
		public static final String AMOUNT3 = "";
		public static final String DATE3 = "";
		public static final String TITLE3 = "";
		public static final String CATEGORY_ID3 = "";
		public static final String SHARED_ID3 = "";
		public static final String IMAGE_ID3 = "";
		
		public static final String AMOUNT4 = "";
		public static final String DATE4 = "";
		public static final String TITLE4 = "";
		public static final String CATEGORY_ID4 = "";
		public static final String SHARED_ID4 = "";
		public static final String IMAGE_ID4 = "";
		
		public static final String AMOUNT5 = "";
		public static final String DATE5 = "";
		public static final String TITLE5 = "";
		public static final String CATEGORY_ID5 = "";
		public static final String SHARED_ID5 = "";
		public static final String IMAGE_ID5 = "";
		
		/* Expense Scripts */
		public static final String INSERT_EXPENSE1 = "";
		public static final String INSERT_EXPENSE2 = "";
		public static final String INSERT_EXPENSE3 = "";
		public static final String INSERT_EXPENSE4 = "";
		public static final String INSERT_EXPENSE5 = "";
	}

}
