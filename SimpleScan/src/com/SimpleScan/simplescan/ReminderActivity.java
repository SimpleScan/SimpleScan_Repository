package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.SimpleScan.simplescan.Entities.Reminder;
import com.SimpleScan.simplescan.Tools.AlarmManagerBroadcastReceiver;
import com.SimpleScan.simplescan.Tools.TimeUtils;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReminderActivity extends Activity implements FragmentManager.OnBackStackChangedListener {    
    
	private static final String REMINDER_KEY = "reminder_key";
	private static FragmentManager mFragmentManager;
	private static CardFrontFragment mCardFrontFragment;
	
    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private static boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_flip);
        
        mFragmentManager = getFragmentManager();
        
        if(mCardFrontFragment == null) mCardFrontFragment = new CardFrontFragment();
        
        if (savedInstanceState == null) {
            // If there is no saved instance state, add a fragment representing the
            // front of the card to this activity. If there is saved instance state,
            // this fragment will have already been added to the activity.
        	mFragmentManager
                    .beginTransaction()
                    .add(R.id.CardContainer, mCardFrontFragment)
                    .commit();
        } else {
            mShowingBack = (mFragmentManager.getBackStackEntryCount() > 0);
        }

        // Monitor back stack changes to ensure the action bar shows the appropriate
        // button (either "photo" or "info").
        mFragmentManager.addOnBackStackChangedListener(this);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_layout_changes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, Main.class));
                return true;
                
            case R.id.action_go_back:
            	NavUtils.navigateUpTo(this, new Intent(this, Main.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static void flipCard(Reminder existingReminder) {
    	if (mShowingBack) {
        	mFragmentManager.popBackStack();
        } else {
	        // Flip to the back.
	        mShowingBack = true;
	
	        CardBackFragment mCardBackFragment = new CardBackFragment();
	        Bundle bundle = new Bundle();
	        if(existingReminder != null) bundle.putSerializable(REMINDER_KEY, existingReminder);
	        else bundle.putSerializable(REMINDER_KEY, new Reminder());
			mCardBackFragment.setArguments(bundle);

	        // Create and commit a new fragment transaction that adds the fragment for the back of
	        // the card, uses custom animations, and is part of the fragment manager's back stack.
	        mFragmentManager
	                .beginTransaction()
	
	                // Replace the default fragment animations with animator resources representing
	                // rotations when switching to the back of the card, as well as animator
	                // resources representing rotations when flipping back to the front (e.g. when
	                // the system Back button is pressed).
	                .setCustomAnimations(
	                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
	                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
	
	                // Replace any fragments currently in the container view with a fragment
	                // representing the next page (indicated by the just-incremented currentPage
	                // variable).
	                .replace(R.id.CardContainer, mCardBackFragment)
	
	                // Add this transaction to the back stack, allowing users to press Back
	                // to get to the front of the card.
	                .addToBackStack(null)
	
	                // Commit the transaction.
	                .commit();
        }
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (mFragmentManager.getBackStackEntryCount() > 0);
        // When the back stack changes, invalidate the options menu (action bar).
        invalidateOptionsMenu();
    }

    /**
     * A fragment representing a list of reminders as the front of the card.
     */
    public static class CardFrontFragment extends Fragment {        
    	private View v;
    	private Context context;
    	private DBManager dbManager;
    	private List<Reminder> allReminders;
    	
    	//For fragment_card_front
        private Button	addButton;  
        private TextView emptyText;       
        //For layoutchange_list_item
        private ViewGroup mContainerView;
   
    	/**
    	 * An empty constructor
    	 */
    	public CardFrontFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	v = inflater.inflate(R.layout.fragment_card_front, container, false);
        	context = v.getContext();
        	dbManager = new DBManager(context);

        	mContainerView = (ViewGroup) v.findViewById(R.id.RowsContainer);
   		 	addButton = (Button) v.findViewById(R.id.add_button);
   		 	emptyText = (TextView) v.findViewById(android.R.id.empty);
   		 	
   		 	allReminders = dbManager.getReminders();	
   		 	
   		 	if(allReminders.size()>0){  		 	
   		 		emptyText.setVisibility(View.INVISIBLE);
	   		 	for(int i=0; i<allReminders.size(); i++) {
	   		 		addItem(allReminders.get(i));
	   		 	}
   		 	}
   		 	
   		 	addButton.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					flipCard(null);
				}
			});
            return v;
        }
        
        private void addItem(final Reminder reminder) {
        	final int reminderID = reminder.getId();
        	String title = reminder.getTitle();
        	String billedAmt = String.valueOf(reminder.getBilledAmount());
        	String paidAmt = String.valueOf(reminder.getPaidAmount());
        	String unpaidAmt = String.valueOf(Double.valueOf(billedAmt) - Double.valueOf(paidAmt));
        	String dueDate = reminder.getDueDate();
        	
        	int days = Integer.parseInt(TimeUtils.getDateDiffStr(dueDate));
        	
        	// Instantiate a new "row" view.
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(context).inflate(
                    R.layout.layoutchange_list_item, mContainerView, false);
            
            newView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					flipCard(reminder);
					return true;
				}
			});
            
            // Set the text in the new row to a random country.
            TextView reminderTitleView = (TextView) newView.findViewById(R.id.reminderTitle);
            reminderTitleView.setText(title);
            reminderTitleView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					flipCard(reminder);
					return true;
				}
			});
            
            TextView reminderAmtView = (TextView) newView.findViewById(R.id.reminderAmtInfo);
            reminderAmtView.setText("$" + unpaidAmt + " out of " + billedAmt + " unpaid");
            reminderAmtView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					flipCard(reminder);
					return true;
				}
			});
            
            TextView reminderDateView = (TextView) newView.findViewById(R.id.reminderDateInfo);
            if(days <= 1) reminderDateView.setText(days + " day unitl " + dueDate);
            else reminderDateView.setText(days + " days unitl " + dueDate);
            reminderDateView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					flipCard(reminder);
					return true;
				}
			});

            // Set a click listener for the "X" button in the row that will remove the row.
            newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                	AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
                	alarm.cancelAlarm(context, reminderID);
                	
                    dbManager.deleteReminder(reminderID);
                	
                	// Remove the row from its parent (the container view).
                    // Because mContainerView has android:animateLayoutChanges set to true,
                    // this removal is automatically animated.
                    mContainerView.removeView(newView);

                    // If there are no rows remaining, show the empty view.
                    if (mContainerView.getChildCount() == 0) emptyText.setVisibility(View.VISIBLE);
                }
            });

            // Because mContainerView has android:animateLayoutChanges set to true,
            // adding this view is automatically animated.
            mContainerView.addView(newView, 0);        
        }
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    	private View v;
    	private Context context;
    	
    	private Reminder reminder;
    	
    	private EditText editTitle;
    	private EditText editDueDate;
    	private EditText editAmount;
    	private EditText editPaid;
    	private Spinner remindDaysSpinner;
    	private CheckBox repeatAlarmBox;
    	
    	private SimpleDateFormat dateFormat;
		private String defaultCurDate;
		private String newRemindDate;
    	
    	public CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	v = inflater.inflate(R.layout.fragment_card_back, container, false);
        	context = v.getContext();
        	reminder = (Reminder) getArguments().getSerializable(REMINDER_KEY);
        	
        	dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        	defaultCurDate = dateFormat.format(new Date());
        	
        	setUpEditReminder(v);
    		
            return v;
        }
        
        private void setUpEditReminder(View v) {
        	setUpReminderTextFields();
        	setUpReminderDateFields(v);
		
    		// Set button listener
    		Button saveButton = (Button) v.findViewById(R.id.btnSave);
    	    saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(editTitle.getText().toString() == null || editTitle.getText().toString().matches("")) {
						Toast.makeText(context, "Invalid title", Toast.LENGTH_SHORT).show();
					} else if(editDueDate.getText().toString() == null || editDueDate.getText().toString().matches("")
					|| Integer.valueOf(TimeUtils.getDateDiffStr(editDueDate.getText().toString())) <= 0) {
						Toast.makeText(context, "Invalid due date", Toast.LENGTH_SHORT).show();
					} else if(Integer.valueOf(TimeUtils.getDateDiffStr(editDueDate.getText().toString())) < TimeUtils.remindDateStr2Int(newRemindDate)){
						Toast.makeText(context, "Invalid remind date", Toast.LENGTH_SHORT).show();
					} else {
						saveReminder();
						
						flipCard(null);
						
						editTitle.setText("");
						editDueDate.setText(defaultCurDate);
						editAmount.setText("0.00");
						editPaid.setText("0.00");
					}
				}
			});
    	}
        
        private void setUpReminderTextFields() {
        	// Set the default values for text fields
        	editTitle = (EditText) v.findViewById(R.id.editTextTitle);
        	editDueDate = (EditText) v.findViewById(R.id.editTextDueDate);
        	editAmount = (EditText) v.findViewById(R.id.editTextAmount);
        	editPaid = (EditText) v.findViewById(R.id.editTextPaid);
        	
        	editTitle.setText(reminder.getTitle());  		
    		if(reminder.getDueDate() == null || reminder.getDueDate() == "") editDueDate.setText(defaultCurDate);
    		else editDueDate.setText(reminder.getDueDate());  		
    		editAmount.setText("" + String.format("%.2f", reminder.getBilledAmount()));
    		editPaid.setText("" + String.format("%.2f", reminder.getPaidAmount()));
        }
        
        private void setUpReminderDateFields(View v) {
        	setUpRemindDaySpinner();
        	setUpDatePicker(v);
        	repeatAlarmBox = (CheckBox) v.findViewById(R.id.checkboxRepeatedAlarm);
    		repeatAlarmBox.setChecked(reminder.isRemindAgain());
        }
        
        private void setUpDatePicker(View v) {
    		final Calendar calendar = Calendar.getInstance();
    		Button datePickerBtn = (Button) v.findViewById(R.id.btnDatePicker);
    		
    		final DatePickerDialog.OnDateSetListener datePicker =
    				new DatePickerDialog.OnDateSetListener() {
    			@Override
    			public void onDateSet(DatePicker view, int year, int monthOfYear,
    					int dayOfMonth) {
    				calendar.set(Calendar.YEAR,  year);
    				calendar.set(Calendar.MONTH, monthOfYear);
    				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    				editDueDate.setText(sdf.format(calendar.getTime()));
    			}
    		};
    		
    		datePickerBtn.setOnClickListener(new View.OnClickListener() {
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
        
        private void setUpRemindDaySpinner() {
        	remindDaysSpinner = (Spinner) v.findViewById(R.id.spinnerRemindDate);

        	List<String> remindDayList = new ArrayList<String>();
    		remindDayList.add(TimeUtils.NONE);
    		remindDayList.add(TimeUtils.ONE_DAY);
    		remindDayList.add(TimeUtils.TWO_DAYS);
    		remindDayList.add(TimeUtils.THREE_DAYS);
    		remindDayList.add(TimeUtils.ONE_WEEK);
    		
    		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, remindDayList);
    		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		remindDaysSpinner.setAdapter(adapter);
    		remindDaysSpinner.setOnItemSelectedListener(this);
    		
			remindDaysSpinner.setSelection(adapter.getPosition(reminder.getRemindDate()));
        }
        
        @Override
		public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
			newRemindDate = parent.getItemAtPosition(pos).toString();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
        
        private void saveReminder() {
    		int id = reminder.getId();

    		String newTitle = editTitle.getText().toString();  		
    		String newDueDate = editDueDate.getText().toString();
    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    		try {
    			sdf.parse(newDueDate);
    		} catch (ParseException e) {
    			return;
    		}
    		double newBilledAmount = Double.parseDouble(editAmount.getText().toString());    		
    		double newPaidAmount = Double.parseDouble(editPaid.getText().toString());
    		boolean repeatAlarm = repeatAlarmBox.isChecked();
    		
    		DBManager dbManager = new DBManager(context);    	
    		AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();

    		int repeatedFrequency = TimeUtils.days2sec(TimeUtils.getDaysInMonth());
    		
    		// ID is -1 if expense was just created??
    		if (id <= 0) {
    			if(repeatAlarm) alarm.setRepeatedDateAlarm(context, id, newDueDate, repeatedFrequency, "Reminder: " + newTitle + " is due! ");
    			else alarm.setOneTimeDateAlarm(context, id, newDueDate, "Reminder: " + newTitle + " is due! ");    			
    			
    			if(newRemindDate!=TimeUtils.NONE) alarm.setOneTimeDateAlarm(context, id+42, TimeUtils.getRemindDate(newDueDate, newRemindDate), "Reminder: " + newTitle + " is due in " + String.valueOf(TimeUtils.remindDateStr2Int(newRemindDate))+" ");    			
    			
    			dbManager.addReminder(newTitle, newBilledAmount, newPaidAmount, newDueDate, newRemindDate, repeatAlarm);
    		}
    		else {
    			if(newDueDate != dbManager.getReminders().get(id-1).getDueDate()){
    				alarm.cancelAlarm(context, id);
    				if(repeatAlarm) alarm.setRepeatedDateAlarm(context, id, newDueDate, repeatedFrequency, "Reminder: " + newTitle + " is due! ");
        			else alarm.setOneTimeDateAlarm(context, id, newDueDate, "Reminder: " + newTitle + " is due! ");
    			}
    			if(newRemindDate != dbManager.getReminders().get(id-1).getRemindDate()){
    				alarm.cancelAlarm(context, id+42);
    				if(newRemindDate!=TimeUtils.NONE) alarm.setOneTimeDateAlarm(context, id+42, TimeUtils.getRemindDate(newDueDate, newRemindDate), "Reminder: " + newTitle + " is due in " + String.valueOf(TimeUtils.remindDateStr2Int(newRemindDate))+" ");
    			}
    			
    			dbManager.editReminder(id, newTitle, newBilledAmount, newPaidAmount, newDueDate, newRemindDate, repeatAlarm);
    		} 

    	}
    }
}
