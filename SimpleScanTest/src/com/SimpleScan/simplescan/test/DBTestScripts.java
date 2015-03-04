package com.SimpleScan.simplescan.test;

import com.SimpleScan.simplescan.sqlite.SimpleScanContract;
import com.SimpleScan.simplescan.sqlite.SimpleScanContract.UserTable;;

/**
 * @author pearse1
 *
 * Contains test data and scripts for testing database functionality.
 */
public class DBTestScripts {

	public static abstract class UserScript {	    
	    private static final String USERNAME1 = "tonyp";
	    
	    public static final String INSERT_USER1 = "INSERT INTO " + 
	    		UserTable.TABLE_NAME +  "(" + UserTable.COLUMN_NAME_USERNAME + ") VALUES" + 
	    		  "(" + USERNAME1 + ");";
	}

}
