package com.SimpleScan.simplescan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

public class FragmentShareExpense extends Fragment implements View.OnClickListener
{
	private static final String EXPENSE_KEY = "expense_key";
	private Expense expense;
	private Calendar calendar;
	private DatePickerDialog.OnDateSetListener datePicker;
	private EditText editDate;
	
	public FragmentShareExpense() 
	{
		// Required empty public constructor
	}
	
	/**
	 * Creates an Expense and creates a FragmentShareExpense with it.
	 * 
	 * @param context The Main activity
	 * @return A FragmentEditExpense that can edit the new expense
	 */
	public static FragmentShareExpense createNewExpense(Context context) {
		// Get today's date to set as default
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		
		// Create the expense and save it
		Expense newExpense = new Expense();
		newExpense.setAmount(0.);
		newExpense.setDate(sdf.format(date));
		newExpense.setTitle("expense");
		DBManager dbManager = new DBManager(context);
		dbManager.addExpense(
				newExpense.getAmount(), 
				newExpense.getDate(), 
				newExpense.getTitle(), 
				newExpense.getCategory(), 
				newExpense.getImageTitle(), 
				newExpense.getImagePath()
				);
		
		// Create new instance
		return createNewInstance(newExpense);
	}
	
	/**
	 * Creates a new FragmentShareExpense, passing in an Expense object.
	 * 
	 * @param expense The Expense that should be edited
	 * @return A FragmentEditExpense that can edit that Expense
	 */
	public static FragmentShareExpense createNewInstance(Expense expense) {
		FragmentShareExpense fragment = new FragmentShareExpense();
		Bundle bundle = new Bundle();
		bundle.putSerializable(EXPENSE_KEY, expense);
		fragment.setArguments(bundle);
		
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_share_expense, container, false);
		expense = (Expense) getArguments().getSerializable(EXPENSE_KEY);
		
		setUpEditExpense(v);
		setUpDatePicker(v);
		
		return v;
	}

	private void setUpEditExpense(View v) {
		// Set the default values for text fields
		EditText edit = (EditText) v.findViewById(R.id.SE_editName);
		edit.setText(expense.getTitle());
		editDate = (EditText) v.findViewById(R.id.SE_editDate);
		editDate.setText(expense.getDate());
		edit = (EditText) v.findViewById(R.id.SE_editAmount);
		edit.setText("" + expense.getAmount());
		
		// Set button listeners
	    Button saveButton = (Button) v.findViewById(R.id.SE_btnSave);
	    saveButton.setOnClickListener(this);
	    Button deleteButton = (Button) v.findViewById(R.id.SE_btnDel);
	    deleteButton.setOnClickListener(this);
		ImageView ExpenseImg = (ImageView)v.findViewById(R.id.SE_im);
		ExpenseImg.setOnClickListener(this);
	}
	
	private void setUpDatePicker(View v) {
		calendar = Calendar.getInstance();
		EditText edit = (EditText) v.findViewById(R.id.SE_editDate);
		
		datePicker = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR,  year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel();
			}
		};
		
		edit.setOnClickListener(this);
	}
	
	private void updateLabel() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		editDate.setText(sdf.format(calendar.getTime()));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case (R.id.SE_btnSave):
			saveExpense(view);
			break;
		case (R.id.SE_btnDel):
			deleteExpense();
			break;
		case (R.id.SE_editDate):
			createDatePickerDialog();
			break;
		case (R.id.SE_im):
			// To Tai: u can just change the class name here to navigate to your scan bill class 
			startActivity(new Intent(getActivity(), CameraActivity.class));
		default:
			break;
		}
	}
	
	private void saveExpense(View view) {
		Expense newExpense = new Expense();
		EditText edit = (EditText) view.findViewById(R.id.SE_editName);
		newExpense.setTitle(edit.getText().toString());
		edit = (EditText) view.findViewById(R.id.SE_editDate);
		newExpense.setAmount(Double.parseDouble(edit.getText().toString()));
		edit = (EditText) view.findViewById(R.id.SE_editAmount);
		newExpense.setDate(edit.getText().toString());
		
		DBManager dbManager = new DBManager(getActivity());
		
		// Does nothing until updateExpense is implemented in DBManager
		
	}
	
	private void deleteExpense() {
		// Does nothing
	}
	
	private void createDatePickerDialog() {
		new DatePickerDialog(getActivity(), 
				datePicker, 
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH)
				).show();
	}
	
}
