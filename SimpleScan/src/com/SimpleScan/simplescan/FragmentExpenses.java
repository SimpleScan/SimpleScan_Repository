package com.SimpleScan.simplescan;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.SimpleScan.simplescan.sqlite.DBManager;
import com.SimpleScan.simplescan.Entities.Expense;

public class FragmentExpenses extends Fragment 
{
	private ExpandableListAdapter expandableListAdapter;

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

	    Button addButton = (Button) v.findViewById(R.id.E_addExpenseButton);
		addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addExpenseFragment();
			}
		});
		
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
		ExpandableListView listView = (ExpandableListView) 
				view.findViewById(R.id.E_fullExpensesListView);
		
		// If the list is empty, give a simple message stating so.
		// Otherwise, populate the ListView.
		if (expensesList.isEmpty()) {
			List<String> emptyMsg = new ArrayList<String>();
			emptyMsg.add("You have no expenses to list!");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, emptyMsg);
			listView.setAdapter(adapter);
		} else {
			expandableListAdapter = new ExpandableListAdapter(getActivity(), 
					expensesList);
			listView.setAdapter(expandableListAdapter);
			
			listView.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					Expense expense = expandableListAdapter.getExpense(groupPosition, childPosition);
					getActivity().setTitle("Edit Expense");
					Fragment fragment = FragmentShareExpense.createNewInstance(expense);
					((Main) getActivity()).changeFragment(fragment, "Edit Expense", true);
					return false;
				}
			});
		}
	}
	
	public void addExpenseFragment() {
		getActivity().setTitle("Edit Expense");
		Fragment fragment = FragmentShareExpense.createNewExpense(getActivity());
		
		Toast.makeText(getActivity().getBaseContext(), "Expense Created",Toast.LENGTH_SHORT).show();
		((Main) getActivity()).changeFragment(fragment, "Edit Expense", true);
	}
}
