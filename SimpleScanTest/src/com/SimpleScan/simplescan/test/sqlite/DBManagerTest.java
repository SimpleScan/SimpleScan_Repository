package com.SimpleScan.simplescan.test.sqlite;

import java.util.List;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Category;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.Entities.Reminder;
import com.SimpleScan.simplescan.Entities.SharedExpense;
import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.test.DBManagerDummy;
import com.SimpleScan.simplescan.test.DBTestScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.BudgetScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.CategoryScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.ExpenseScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.ReminderScripts;
import com.SimpleScan.simplescan.test.DBTestScripts.SharedExpenseScripts;

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
    
    public void testGetUserInfo(){
    	setUp();
    	dbTest.createUserData2();
    	User user = db.getUserInfo();
    	assertNotNull(user);
    	assertEquals(DBTestScripts.UserScripts.USERNAME2, user.getName());
    	assertEquals(DBTestScripts.UserScripts.USERID2, user.getId());
    }
    
    public void testUpdateUserDoesntYetExist(){
    	setUp();
    	db.updateUser(DBTestScripts.UserScripts.USERNAME2, DBTestScripts.UserScripts.USERID2);
    	User user = db.getUserInfo();
    	assertNotNull(user);
    	assertEquals(DBTestScripts.UserScripts.USERNAME2, user.getName());
    	assertEquals(DBTestScripts.UserScripts.USERID2, user.getId());
    }
    
    public void testUpdateUserAlreadyExists(){
    	setUp();
    	dbTest.createUserData2();
    	db.updateUser("Tony the Tiger", DBTestScripts.UserScripts.USERID2);
    	User user = db.getUserInfo();
    	assertNotNull(user);
    	assertEquals("Tony the Tiger", user.getName());
    	assertEquals(DBTestScripts.UserScripts.USERID2, user.getId());
    }
    
    public void testUpdateUserAlreadyExistsDiffId(){
    	setUp();
    	dbTest.createUserData2();
    	db.updateUser("Tony the Tiger", "COMPLETELY DIFFERENT ID");
    	User user = db.getUserInfo();
    	assertNotNull(user);
    	assertEquals("Tony the Tiger", user.getName());
    	assertEquals(DBTestScripts.UserScripts.USERID2, user.getId());
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
    	dbTest.createBudgetData1();
    	
    	db.addExpense(ExpenseScripts.AMOUNT6, ExpenseScripts.DATE6, ExpenseScripts.TITLE6, null, null, null);
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	
    	assertEquals(ExpenseScripts.AMOUNT6, expenses.get(0).getAmount());
    	
    	Budget b = db.getBudget();
    	assertNotNull(b);
    	
    	assertEquals(BudgetScripts.CURRENT_AMOUNT1 - ExpenseScripts.AMOUNT6, b.getCurrAmount());    	
    }    
    
    public void testEditExpense(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.editExpense(1, -1, 30.00, null, "Electric Bill", null, null, null);
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	assertEquals(expenses.get(0).getAmount(), 30.00);
    	assertEquals(expenses.get(0).getTitle(), "Electric Bill");
    }
    
    public void testDeleteExpense(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.deleteExpense(1);
    	List<Expense> expenses = db.getExpenses();
    	
    	assertNotNull(expenses);
    	assertEquals(4, expenses.size());
    }
    
    public void testDeleteExpenseWithSharedExpense(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(5, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);    	
    	
    	db.deleteExpense(5);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	assertEquals(4, expenses.size());
    	assertNull(db.getSharedExpense(5));
    }
    
    public void testAddSharedExpense1(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(5, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	Expense e = expenses.get(4);
    	
    	//assertEquals(0, e.getSharedId());
    	
    	SharedExpense se = db.getSharedExpense(e.getId());
    	assertNotNull(se);
    	assertEquals(SharedExpenseScripts.USER_ID1_1, se.getUserId1());
    	assertEquals(SharedExpenseScripts.HAS_PAID1_1, se.isHasPaid1());
    	assertEquals(SharedExpenseScripts.USER_ID2_1, se.getUserId2());
    	assertEquals(SharedExpenseScripts.HAS_PAID2_1, se.isHasPaid2());
    	assertEquals(SharedExpenseScripts.USER_ID3_1, se.getUserId3());
    	assertEquals(SharedExpenseScripts.HAS_PAID3_1, se.isHasPaid3());
    }
    
    public void testAddSharedExpense2(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(1, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	Expense e = expenses.get(0);
    	
    	//assertEquals(0, e.getSharedId());
    	
    	SharedExpense se = db.getSharedExpense(e.getId());
    	assertNotNull(se);
    	assertEquals(SharedExpenseScripts.USER_ID1_1, se.getUserId1());
    	assertEquals(SharedExpenseScripts.HAS_PAID1_1, se.isHasPaid1());
    	assertEquals(SharedExpenseScripts.USER_ID2_1, se.getUserId2());
    	assertEquals(SharedExpenseScripts.HAS_PAID2_1, se.isHasPaid2());
    	assertEquals(SharedExpenseScripts.USER_ID3_1, se.getUserId3());
    	assertEquals(SharedExpenseScripts.HAS_PAID3_1, se.isHasPaid3());
    }
    
    public void testAddSharedExpense3(){
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(1, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	db.addSharedExpense(2, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	assertEquals(5, expenses.size());
    	Expense e1 = expenses.get(0);
    	Expense e2 = expenses.get(1);
    	
    	//assertEquals(0, e1.getSharedId());
    	//assertEquals(1, e2.getSharedId());
    	
    	SharedExpense se1 = db.getSharedExpense(e1.getId());
    	assertNotNull(se1);
    	assertEquals(SharedExpenseScripts.USER_ID1_1, se1.getUserId1());
    	
    	SharedExpense se2 = db.getSharedExpense(e2.getId());
    	assertNotNull(se2);
    	assertEquals(SharedExpenseScripts.HAS_PAID1_1, se2.isHasPaid1());
    }
    
    public void testEditSharedExpense1(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(5, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	Expense e = expenses.get(4);
    	
    	//assertEquals(0, e.getSharedId());
    	db.editSharedExpense(1, "12345", true, "12345", true, "12345", true);
    	SharedExpense se = db.getSharedExpense(e.getId());
    	assertNotNull(se);
    	assertEquals("12345", se.getUserId1());
    	assertEquals(true, se.isHasPaid1());
    	assertEquals("12345", se.getUserId2());
    	assertEquals(true, se.isHasPaid2());
    	assertEquals("12345", se.getUserId3());
    	assertEquals(true, se.isHasPaid3());
    }
    
    public void testEditSharedExpense2(){
    	setUp();
    	dbTest.createExpenseData1();
    	
    	db.addSharedExpense(5, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	db.addSharedExpense(4, SharedExpenseScripts.USER_ID1_1, SharedExpenseScripts.HAS_PAID1_1,
    			SharedExpenseScripts.USER_ID2_1, SharedExpenseScripts.HAS_PAID2_1,
    			SharedExpenseScripts.USER_ID3_1, SharedExpenseScripts.HAS_PAID3_1);
    	
    	List<Expense> expenses = db.getExpenses();
    	assertNotNull(expenses);
    	assertEquals(5, expenses.size());
    	Expense e1 = expenses.get(3);
    	Expense e2 = expenses.get(4);
    	
    	//assertEquals(0, e.getSharedId());
    	db.editSharedExpense(1, "12345", true, "12345", true, "12345", true);
    	db.editSharedExpense(2, "99999", true, "99999", true, "99999", false);
    	SharedExpense se = db.getSharedExpense(e1.getId());
    	assertNotNull(se);
    	assertEquals("99999", se.getUserId1());
    	assertEquals(true, se.isHasPaid1());
    	assertEquals("99999", se.getUserId2());
    	assertEquals(true, se.isHasPaid2());
    	assertEquals("99999", se.getUserId3());
    	assertEquals(false, se.isHasPaid3());
    }
    
    public void testGetBudget(){
    	setUp();
    	dbTest.createBudgetData1();
    	
    	Budget b = db.getBudget();
    	assertNotNull(b);
    	assertEquals(BudgetScripts.CURRENT_AMOUNT1, b.getCurrAmount());
    }
    
    public void testCreateBudget(){
    	setUp();
    	dbTest.createBudgetData1();
    	
    	db.createBudget(BudgetScripts.ORIGINAL_AMOUNT2, BudgetScripts.START_DATE2, BudgetScripts.END_DATE2);
    	Budget b = db.getBudget();
    	assertNotNull(b);
    	assertEquals(BudgetScripts.CURRENT_AMOUNT2, b.getCurrAmount());
    	assertEquals(BudgetScripts.START_DATE2, b.getStartDate());
    	assertEquals(BudgetScripts.END_DATE2, b.getEndDate());
    }
    
    /**
     * Tests that after creating a new budget, the last budget is ended (end date is set to start date of new budget)
     */
    public void testCreateBudgetUpdateOld(){
    	setUp();
    	dbTest.createBudgetData1();
    	
    	db.createBudget(BudgetScripts.ORIGINAL_AMOUNT2, BudgetScripts.START_DATE2, BudgetScripts.END_DATE2);
    	List<Budget> budgets = db.getBudgets();
    	assertNotNull(budgets);
    	assertEquals(2, budgets.size());
    	Budget b1 = budgets.get(0); // new budget
    	Budget b2 = budgets.get(1); // old budget
    	
    	assertEquals(b1.getStartDate(), b2.getEndDate());    	
    }

    public void testGetCategories(){
    	setUp();
    	dbTest.createCategoryData1();
    	
    	List<Category> categories = db.getCategories();
    	assertNotNull(categories);
    	assertEquals(categories.size(), 3);
    	assertEquals(categories.get(0).getTitle(), "Electric");
    	assertEquals(categories.get(2).getColor(), "BROWN");
    }
    
    public void testAddCategories(){
    	setUp();
    	dbTest.createCategoryData1();
    	
    	db.addCategory(CategoryScripts.TITLE4, CategoryScripts.COLOR4);
    	List<Category> categories = db.getCategories();
    	assertNotNull(categories);
    	assertEquals(categories.size(), 4);
    	assertEquals(categories.get(3).getTitle(), CategoryScripts.TITLE4);
    	assertEquals(categories.get(3).getColor(), CategoryScripts.COLOR4);
    }
    
    public void testGetReminders(){
    	setUp();
    	dbTest.createReminderData1();
    	
    	List<Reminder> reminders = db.getReminders();
    	assertNotNull(reminders);
    	assertEquals(3, reminders.size());
    	assertEquals("Utilities", reminders.get(0).getTitle());
    	assertEquals(80.00, reminders.get(1).getBilledAmount());
    	assertEquals(ReminderScripts.REMIND_DATE3, reminders.get(2).getRemindDate());
    }
    
    public void testAddReminder(){
    	setUp();
    	dbTest.createReminderData1();
    	
    	db.addReminder(ReminderScripts.TITLE4, ReminderScripts.BILLED_AMOUNT4, ReminderScripts.PAID_AMOUNT4,
    			ReminderScripts.DUE_DATE4, ReminderScripts.REMIND_DATE4, ReminderScripts.REMIND_AGAIN4);
    	
    	List<Reminder> reminders = db.getReminders();
    	assertNotNull(reminders);
    	assertEquals(4, reminders.size());
    	assertEquals(ReminderScripts.TITLE4, reminders.get(3).getTitle());
    	assertEquals(ReminderScripts.DUE_DATE4, reminders.get(3).getDueDate());
    }
    
    public void testEditReminder(){
    	setUp();
    	dbTest.createReminderData1();
    	
    	db.editReminder(1, ReminderScripts.TITLE4, ReminderScripts.BILLED_AMOUNT4, ReminderScripts.PAID_AMOUNT4,
    			ReminderScripts.DUE_DATE4, ReminderScripts.REMIND_DATE4, ReminderScripts.REMIND_AGAIN4);
    	
    	List<Reminder> reminders = db.getReminders();
    	assertNotNull(reminders);
    	assertEquals(3, reminders.size());
    	assertEquals(ReminderScripts.TITLE4, reminders.get(0).getTitle());
    	assertEquals(ReminderScripts.PAID_AMOUNT4, reminders.get(0).getPaidAmount());
    }
    
    public void testDeleteReminder(){
    	setUp();
    	dbTest.createReminderData1();
    	
    	db.deleteReminder(1);
    	
    	List<Reminder> reminders = db.getReminders();
    	assertNotNull(reminders);
    	assertEquals(2, reminders.size());
    	assertEquals(ReminderScripts.TITLE2, reminders.get(0).getTitle());
    	assertEquals(ReminderScripts.BILLED_AMOUNT3, reminders.get(1).getBilledAmount());
    }
    
    public void testAddDeleteReminders(){
    	setUp();
    	dbTest.createReminderData1();
    	
    	db.addReminder(ReminderScripts.TITLE4, ReminderScripts.BILLED_AMOUNT4, ReminderScripts.PAID_AMOUNT4,
    			ReminderScripts.DUE_DATE4, ReminderScripts.REMIND_DATE4, ReminderScripts.REMIND_AGAIN4);
    	
    	List<Reminder> reminders = db.getReminders();
    	assertNotNull(reminders);
    	assertEquals(4, reminders.size());
    	assertEquals(ReminderScripts.TITLE4, reminders.get(3).getTitle());
    	assertEquals(ReminderScripts.DUE_DATE4, reminders.get(3).getDueDate());
    	
    	db.deleteReminder(1);
    	List<Reminder> reminders2 = db.getReminders();
    	assertNotNull(reminders2);
    	assertEquals(3, reminders2.size());
    }
    
    public void tearDown() throws Exception{
        //db.close(); 
        dbTest.close();
        super.tearDown();
    }

}
