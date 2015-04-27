package com.SimpleScan.simplescan.test.espresso;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.SimpleScan.simplescan.FragmentCategories;
import com.SimpleScan.simplescan.FragmentExpenses;
import com.SimpleScan.simplescan.FragmentShareExpense;
import com.SimpleScan.simplescan.R;
import com.SimpleScan.simplescan.TestFragmentActivity;

public class FragmentGUITest extends ActivityInstrumentationTestCase2<TestFragmentActivity> {

	private TestFragmentActivity mActivity;
	
	public FragmentGUITest() {
		super(TestFragmentActivity.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();         
    }

	private Fragment startFragment(Fragment fragment) {
      FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
      transaction.add(R.id.activity_test_fragment_linearlayout, fragment, "tag");
      transaction.commit();
      getInstrumentation().waitForIdleSync();
      Fragment frag = mActivity.getSupportFragmentManager().findFragmentByTag("tag");
      return frag;
    }
	
	public void testCategoryFragmentGUI(){
		FragmentCategories categoryFrag = (FragmentCategories) startFragment(new FragmentCategories());
		
		EditText txt = (EditText) mActivity.findViewById(R.id.cate_editName);
		assertNotNull(txt);
	}
	
	public void testAddExpenseFragmentGUI() {
		
		FragmentShareExpense newExpenseFragment = (FragmentShareExpense) startFragment(new FragmentShareExpense());
		
		EditText editName = (EditText) mActivity.findViewById(R.id.SE_editName);
		EditText editDate = (EditText) mActivity.findViewById(R.id.SE_editDate);
		EditText editAmount = (EditText) mActivity.findViewById(R.id.SE_editAmount);
		
		assertNotNull(editName);
		assertNotNull(editDate);
		assertNotNull(editAmount);
	}
}
