package com.SimpleScan.simplescan.test.sqlite;

import java.util.Date;
import java.util.List;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.test.DBManagerDummy;
import com.SimpleScan.simplescan.test.DBTestScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.BudgetScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.ExpenseScripts;

public class DBManagerTest extends AndroidTestCase {
	
	private DBManager db;
	private DBManagerDummy dbTest;

    public void setUp(){
    	// create a separate test database
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBManager(context);
        dbTest = new DBManagerDummy(context);
    }
    
    public void testGetUsername(){
        setUp();
        dbTest.createUserData1();
        User user = db.getUserInfo();
        assertNotNull(user);
        assertEquals(DBTestScripts.UserScripts.USERNAME1, user.getName());
    }
    
    public void testGetAllExpenses(){
    	setUp();
    	dbTest.createExpenseData1();
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	
    	assertEquals(5, expenses.size());    	
    	assertEquals(14.14, expenses.get(2).getAmount());
    	assertEquals("421 book", expenses.get(4).getTitle());
    }
    
    public void testGetNumExpenses(){
    	setUp();
    	dbTest.createExpenseData1();
    	List<Expense> expenses = db.getExpenses(2);  // grab the latest two expenses
    	assertNotNull(expenses);
    	
    	assertEquals(2, expenses.size());
    	assertEquals("Water Bill", expenses.get(0).getTitle());    	
    }
    
    public void testAddExpenseNoImage(){
    	setUp();
    	dbTest.createExpenseData1();
    	Expense newExpense = new Expense();
    	newExpense.setAmount(ExpenseScripts.AMOUNT6);
    	newExpense.setDate(ExpenseScripts.DATE6);
    	newExpense.setTitle(ExpenseScripts.TITLE6);
    	
    	db.addExpense(newExpense);
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	
    	assertEquals(ExpenseScripts.AMOUNT6, expenses.get(0).getAmount());
    	
    	Budget b = db.getBudget();
    	assertNotNull(b);
    	
    	assertEquals(BudgetScripts.CURRENT_AMOUNT1 - ExpenseScripts.AMOUNT6, b.getAmount());    	
    }    

    public void tearDown() throws Exception{
        db.close(); 
        dbTest.close();
        super.tearDown();
    }

}
