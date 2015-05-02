package com.SimpleScan.simplescan;

import java.util.ArrayList;
import java.util.List;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;

public class FragmentOverview extends Fragment {
	
	private static final String FRAGMENT_NAME = "Overview";

	public FragmentOverview() {
		// Required empty public constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {	
		View v = inflater.inflate(R.layout.fragment_overview, container, false);
		
		// Display the remaining budget.
		displayRemainingBudget(v);
		
		// Create the list of expenses
		buildExpensesList(v);

	    Button addButton = (Button) v.findViewById(R.id.O_addExpenseButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addExpenseFragment();
			}
		});
		
		return v;
	}
	
	/**
	 * Displays the remaining budget.
	 * 
	 * @param view
	 */
	private void displayRemainingBudget(View view) {
		String display = "$0.00";
		
		TextView textView = (TextView) view.findViewById(R.id.O_remainingBudget);
		Main context = (Main) getActivity();

		DBManager dbManager = new DBManager(context);
		try {
			Budget budget = dbManager.getBudget();
			display = "$" + budget.getCurrAmount();
		} catch (Exception e) {
			/*
			FragmentEditBudget newFragment = FragmentEditBudget.createNewBudget(context);
			context.changeFragment(newFragment, "Add Budget", false);
			*/
			Time today = new Time(Time.getCurrentTimezone());
			today.setToNow();
			String day = String.valueOf(today.monthDay);
			String month = String.valueOf(today.month);
			String monthNext = String.valueOf(today.month+1);
			String year = String.valueOf(today.year);
			Double iniBudget = 0.00; 
			String newStartDate = month+"/"+day+"/"+year;
			String newEndDate = monthNext+"/"+day+"/"+year;
			dbManager.createBudget(iniBudget, newStartDate, newEndDate);
			display = "$" + iniBudget;
		}
		textView.setText(display);
		textView.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				addBudgetFragment();
				return true;
			}
		});
	}
	
	/**
	 * Populates the list of expenses, up to a limit.
	 * If there are no expenses, gives a simple message stating so.
	 * 
	 * @param view
	 */
	private void buildExpensesList(View view) {
		int listLimit = 10;
		
		DBManager dbManager = new DBManager(getActivity());
		List<Expense> expensesList = dbManager.getExpenses(listLimit);
		ListView listView = (ListView) view.findViewById(R.id.O_partialExpensesListView);
		
		// If the list is empty, give a simple message stating so.
		// Otherwise, populate the ListView.
		if (expensesList.isEmpty()) {
			List<String> emptyMsg = new ArrayList<String>();
			emptyMsg.add("You have no expenses to list!");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, emptyMsg);
			listView.setAdapter(adapter);
		} else {
			ArrayAdapter<Expense> adapter = new ArrayAdapter<Expense>(getActivity(),
					android.R.layout.simple_list_item_1, expensesList);
			listView.setAdapter(adapter);
		}
	}
	
	public void addExpenseFragment() {
		getActivity().setTitle("Edit Expense");
		Fragment fragment = FragmentShareExpense.createNewExpense(getActivity());
		
		Toast.makeText(getActivity().getBaseContext(), "Create new expense",Toast.LENGTH_SHORT).show();
		((Main) getActivity()).changeFragment(fragment, "Edit Expense", true);
	}
	
	public void addBudgetFragment() {
		getActivity().setTitle("Edit Budget");
		Fragment fragment = FragmentEditBudget.createNewBudget(getActivity());
		
		Toast.makeText(getActivity().getBaseContext(), "Create new budget",Toast.LENGTH_SHORT).show();
		((Main) getActivity()).changeFragment(fragment, "Edit Budget", true);
	}
}
