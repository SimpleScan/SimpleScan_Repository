package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.SimpleScan.simplescan.Entities.Budget;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentEditBudget extends Fragment implements OnClickListener {
	
	private static final String FRAGMENT_NAME = "Edit Budget";
	private static final String BUDGET_KEY = "budget_key";
	private Budget budget;
	private EditText editBudget;
	private EditText editStartDate;
	private EditText editEndDate;
	
	public FragmentEditBudget() 
	{
		// Required empty public constructor
	}
	
	/**
	 * Creates an Expense and creates a FragmentShareExpense with it.
	 * 
	 * @param context The Main activity
	 * @return A FragmentEditExpense that can edit the new expense
	 */
	public static FragmentEditBudget createNewBudget(Context context) {
		// Get today's date to set as default
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		
		// Create the expense and save it
		Budget newBudget = new Budget();
		newBudget.setCurrAmount(0.0);
		newBudget.setOrigAmount(0.0);
		newBudget.setStartDate(sdf.format(calendar.getTime()));
		calendar.add(Calendar.MONTH, 1);
		newBudget.setEndDate(sdf.format(calendar.getTime()));
		
		// Create new instance
		return createNewInstance(newBudget);
	}
	
	public static FragmentEditBudget createNewInstance(Budget budget) {
		FragmentEditBudget fragment = new FragmentEditBudget();
		Bundle bundle = new Bundle();
		bundle.putSerializable(BUDGET_KEY, budget);
		fragment.setArguments(bundle);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_edit_budget, container, false);
		budget = (Budget) getArguments().getSerializable(BUDGET_KEY);
		
		setUpBudget(v);
		setUpDatePicker(v);
		
		return v;
	}
	
	private void setUpBudget(View v) {
		editBudget = (EditText) v.findViewById(R.id.EB_editBudget);
		editBudget.setText(""+budget.getCurrAmount());
		editStartDate = (EditText) v.findViewById(R.id.EB_editStartDate);
		editStartDate.setText(budget.getStartDate());
		editEndDate = (EditText) v.findViewById(R.id.EB_editEndDate);
		editEndDate.setText(budget.getEndDate());

		// Set button listeners
	    Button saveButton = (Button) v.findViewById(R.id.EB_btnSave);
	    saveButton.setOnClickListener(this);
	    Button cancelButton = (Button) v.findViewById(R.id.EB_btnCancel);
	    cancelButton.setOnClickListener(this);
	}
	
	private void setUpDatePicker(View v) {
		// Lots of redundant code. Wonder if there's a way to fix that...
		final Calendar calendar = Calendar.getInstance();
		
		final DatePickerDialog.OnDateSetListener startDatePicker =
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR,  year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
				editStartDate.setText(sdf.format(calendar.getTime()));
			}
		};
		editStartDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new DatePickerDialog(getActivity(), 
						startDatePicker, 
						calendar.get(Calendar.YEAR), 
						calendar.get(Calendar.MONTH), 
						calendar.get(Calendar.DAY_OF_MONTH)
						).show();
			}
		});
		
		final DatePickerDialog.OnDateSetListener endDatePicker =
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR,  year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
				editEndDate.setText(sdf.format(calendar.getTime()));
			}
		};
		editEndDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new DatePickerDialog(getActivity(), 
						endDatePicker, 
						calendar.get(Calendar.YEAR), 
						calendar.get(Calendar.MONTH), 
						calendar.get(Calendar.DAY_OF_MONTH)
						).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case(R.id.EB_btnSave):
				saveBudget();
				addOverviewFragment();
				break;
			case(R.id.EB_btnCancel):
				addOverviewFragment();
				break;
			default:
				break;
		}
	}
	
	private void saveBudget() {
		Main context = (Main) getActivity();

		double newBudget = Double.parseDouble(editBudget.getText().toString());
		
		String newStartDate = editStartDate.getText().toString();
		String newEndDate = editEndDate.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		try {
			sdf.parse(newStartDate);
			sdf.parse(newEndDate);
		} catch (ParseException e) {
			context.makeToast("Invalid date! (Format: MM/dd/yyyy)");
			return;
		}
		
		DBManager dbManager = new DBManager(context);
		dbManager.createBudget(newBudget, newStartDate, newEndDate);
		
		context.makeToast("New budget of " + newBudget);
	}
	
	public void addOverviewFragment() {
		getActivity().setTitle("Overview");
		Fragment fragment = new FragmentOverview();
		
		Toast.makeText(getActivity().getBaseContext(), "Overall", Toast.LENGTH_SHORT).show();
		((Main) getActivity()).changeFragment(fragment, "Overall", true);
	}

}
