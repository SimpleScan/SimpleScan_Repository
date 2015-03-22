package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.SimpleScan.simplescan.Entities.Category;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

public class FragmentShareExpense extends Fragment implements View.OnClickListener
{
	private static final String EXPENSE_KEY = "expense_key";
	private Expense expense;
	private EditText editName;
	private EditText editDate;
	private EditText editAmount;
	private Spinner spinner ;
	private DBManager dbManager;
	
	//For setDataFromCam
	private static boolean cameFlag = false;
	private static Expense camExpense = new Expense();
	
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
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Calendar calendar = Calendar.getInstance();
		
		// Create the expense and save it
		Expense newExpense = new Expense();
		newExpense.setAmount(0.);
		newExpense.setDate(sdf.format(calendar.getTime()));
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
		setUpCategory(v);
		
		/*
		// check if camera data has been passed
		if(cameFlag)
		{
			// grab the UI Component 
			editName = (EditText)v.findViewById(R.id.SE_editName);
			editDate  = (EditText)v.findViewById(R.id.SE_editDate);
			editAmount = (EditText)v.findViewById(R.id.SE_editAmount);
			// set the values for UI
			editName.setText(camExpense.getTitle());
			editDate.setText(camExpense.getDate());
			editAmount.setText(String.valueOf(camExpense.getAmount()));
			//set the flag back to false
			cameFlag = false;
		}
		*/
		
		return v;
	}

	private void setUpCategory(View v)
	{
		spinner = (Spinner)v.findViewById(R.id.SE_spinner);
		List<String> cateNameList = new ArrayList<String>();
		List<Category> categoriesList = new ArrayList<Category>();
		dbManager = new DBManager(getActivity());
		categoriesList = dbManager.getCategories();
		for(Category c : categoriesList)
		{
			cateNameList.add(c.getTitle());
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_spinner_item, cateNameList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	private void setUpEditExpense(View v) {
		// Set the default values for text fields
		editName = (EditText) v.findViewById(R.id.SE_editName);
		editName.setText(expense.getTitle());
		editDate = (EditText) v.findViewById(R.id.SE_editDate);
		editDate.setText(expense.getDate());
		editAmount = (EditText) v.findViewById(R.id.SE_editAmount);
		editAmount.setText("" + expense.getAmount());
		
		// Set button listeners
	    Button saveButton = (Button) v.findViewById(R.id.SE_btnSave);
	    saveButton.setOnClickListener(this);
	    Button deleteButton = (Button) v.findViewById(R.id.SE_btnDel);
	    deleteButton.setOnClickListener(this);
		ImageView ExpenseImg = (ImageView)v.findViewById(R.id.SE_im);
		ExpenseImg.setOnClickListener(this);
	}
	
	private void setUpDatePicker(View v) {
		final Calendar calendar = Calendar.getInstance();
		
		final DatePickerDialog.OnDateSetListener datePicker =
				new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(Calendar.YEAR,  year);
				calendar.set(Calendar.MONTH, monthOfYear);
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
				editDate.setText(sdf.format(calendar.getTime()));
			}
		};
		editDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new DatePickerDialog(getActivity(), 
						datePicker, 
						calendar.get(Calendar.YEAR), 
						calendar.get(Calendar.MONTH), 
						calendar.get(Calendar.DAY_OF_MONTH)
						).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case (R.id.SE_btnSave):
			saveExpense();
			break;
		case (R.id.SE_btnDel):
			deleteExpense();
			break;
		case (R.id.SE_im):
			// To Tai: u can just change the class name here to navigate to your scan bill class 
			startActivity(new Intent(getActivity(), CameraActivity.class));
		default:
			break;
		}
	}
	
	private void saveExpense() {
		int id = expense.getId();
		Main context = (Main) getActivity();
		
		String newTitle = editName.getText().toString();
		
		String newDate = editDate.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		try {
			sdf.parse(newDate);
		} catch (ParseException e) {
			context.makeToast(newDate + " is not a valid date! (Format: MM/dd/yyyy)");
			return;
		}
		double newAmount = Double.parseDouble(editAmount.getText().toString());
		
		DBManager dbManager = new DBManager(context);
		dbManager.editExpense(id, newAmount, newDate, newTitle, null, null);
		
		context.makeToast("Changes saved");
	}
	
	private void deleteExpense() {
		// Does nothing
	}
	
	public static void setDataFromCam(String title, String date, double amount)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
		Calendar calendar = Calendar.getInstance();
		
		if(title!="") camExpense.setTitle(title);
		else camExpense.setTitle("expense");
		
		if(date!="") camExpense.setDate(date);
		else camExpense.setDate(sdf.format(calendar.getTime()));
		
		camExpense.setAmount(amount);
		
		cameFlag = true;
		
	}
	
}
