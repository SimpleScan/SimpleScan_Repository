package com.SimpleScan.simplescan.test.sqlite;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.test.DBManagerDummy;

public class DBManagerTest extends AndroidTestCase {
	
	private DBManager db;
	private DBManagerDummy dbTest;

    public void setUp(){
    	// create a separate test database
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBManager(context);
    }
    
    public void testAddExpense(){
        
    }

/*    public void tearDown() throws Exception{
        db.close(); 
        super.tearDown();
    }
*/
}
