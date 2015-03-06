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
import android.widget.ListView;
import android.widget.TextView;

public class FragmentOverview extends Fragment 
{

	public FragmentOverview() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_overview, container, false);
		
		// Display the remaining budget.
		displayRemainingBudget(v);
		
		// Create the list of expenses
		buildExpensesList(v);
		
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

		DBManager dbManager = new DBManager(getActivity());
		try {
			Budget budget = dbManager.getBudget();
			display = "" + budget;
		} catch (Exception e) {
			// An exception probably means there is no budget set.
			// So just display the default value.
		}
		textView.setText(display);
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
}
