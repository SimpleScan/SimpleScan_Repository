package com.SimpleScan.simplescan;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.Entities.Expense;

public class FragmentExpenses extends Fragment 
{

	public FragmentExpenses() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_expenses, container, false);
		
		// Create the categories spinner
		buildCategoriesSpinner(v);
		
		// Create the list of expenses
		buildExpensesList(v);
		
		return v;
	}
	
	private void buildCategoriesSpinner(View view) {
		Spinner spinner = (Spinner) view.findViewById(R.id.E_categoriesSpinner);
		
		// For now, there's only one category and the spinner does nothing.
		
		List<String> categoriesList = new ArrayList<String>();
		categoriesList.add("All");
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_spinner_item, categoriesList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	/**
	 * Populates the list of expenses.
	 * If there are no expenses, gives a simple message stating so.
	 * 
	 * @param view
	 */
	private void buildExpensesList(View view) {
		DBManager dbManager = new DBManager(getActivity());
		List<Expense> expensesList = dbManager.getExpenses();
		ListView listView = (ListView) view.findViewById(R.id.E_fullExpensesListView);
		
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
