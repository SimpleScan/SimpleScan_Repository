package com.SimpleScan.simplescan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import com.SimpleScan.simplescan.Entities.Reminder;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class ReminderActivity extends Activity implements FragmentManager.OnBackStackChangedListener {    
    
	private static final String REMINDER_KEY = "reminder_key";
	private static FragmentManager mFragmentManager;
	private static CardFrontFragment mCardFrontFragment;
	private static CardBackFragment mCardBackFragment;
	
    /**
     * Whether or not we're showing the back of the card (otherwise showing the front).
     */
    private static boolean mShowingBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_flip);
        
        mFragmentManager = getFragmentManager();
        
        if(mCardFrontFragment == null) {
        	mCardFrontFragment = new CardFrontFragment();
        	Log.i("onCreate", "new Front fragment");
        }
        if(mCardBackFragment == null) mCardBackFragment = new CardBackFragment();
        
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

    private static void flipCard() {
        if (mShowingBack) {
        	mFragmentManager.popBackStack();
        } else {
        
	        // Flip to the back.
	
	        mShowingBack = true;
	
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
     * A fragment representing the front of the card.
     */
    public static class CardFrontFragment extends Fragment {
        
    	private View v;
    	private Context context;
    	private DBManager dbManager;
    	private ViewGroup mContainerView;
        private Button	addButton;  
        private TextView emptyText;
        
        private List<Reminder> allReminders;
        private int numReminders;
    	
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
   		 	numReminders = allReminders.size(); 	
   		 	
   		 	if(numReminders>0){
   		 		emptyText.setVisibility(View.INVISIBLE);
	   		 	for(int i=0; i<numReminders; i++) { 	
	   		 		addItem(allReminders.get(i).getTitle());
	   		 	}
   		 	}
   		 	
   		 	addButton.setOnClickListener(new View.OnClickListener() {			
				@Override
				public void onClick(View v) {
					flipCard();
				}
			});

            return v;
        }
        
        private void addItem(final String title) {       	
        	// Instantiate a new "row" view.
            final ViewGroup newView = (ViewGroup) LayoutInflater.from(context).inflate(
                    R.layout.layoutchange_list_item, mContainerView, false);
            
            // Set the text in the new row to a random country.
            TextView reminderMessageView = (TextView) newView.findViewById(R.id.reminderMessage);
            reminderMessageView.setText(title);
            reminderMessageView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					flipCard();
					return true;
				}
			});

            // Set a click listener for the "X" button in the row that will remove the row.
            newView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove the row from its parent (the container view).
                    // Because mContainerView has android:animateLayoutChanges set to true,
                    // this removal is automatically animated.
                    mContainerView.removeView(newView);

                    // If there are no rows remaining, show the empty view.
                    if (mContainerView.getChildCount() == 0) {
                    	emptyText.setVisibility(View.VISIBLE);
                    }
                }
            });
            
            newView.findViewById(R.id.notify_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Notification countryNotification = new Notification(context, title, "SimpleScan: from " + title, false);
                	countryNotification.sendNotification(context, 001);
                }
            });

            // Because mContainerView has android:animateLayoutChanges set to true,
            // adding this view is automatically animated.
            mContainerView.addView(newView, 0);
            
        }
    	
        private static final String[] BILLS = new String[]{
                "Phone Bill", "Utilities", "Cable/Internet", "Subscriptions", "Rent",
                "Medical"
        };
    }

    /**
     * A fragment representing the back of the card.
     */
    public static class CardBackFragment extends Fragment {
    	
    	private View v;
    	private Context context;
    	
    	private Reminder reminder;
    	
    	private EditText editTitle;
    	private EditText editDueDate;
    	private EditText editAmount;
    	
    	public CardBackFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	
        	v = inflater.inflate(R.layout.fragment_card_back, container, false);
        	context = v.getContext();
        	//reminder = (Reminder) getArguments().getSerializable(REMINDER_KEY);
        	reminder = new Reminder();
        	
        	setUpEditExpense(v);
        	setUpDatePicker(v);
    		
            return v;
        }
        
        private void setUpEditExpense(View v) {
    		// Set the default values for text fields
        	editTitle = (EditText) v.findViewById(R.id.editTextTitle);
        	editTitle.setText(reminder.getTitle());
    		editDueDate = (EditText) v.findViewById(R.id.editTextDueDate);
    		editDueDate.setText(reminder.getDueDate());
    		editAmount = (EditText) v.findViewById(R.id.editTextAmount);
    		editAmount.setText("" + reminder.getBilledAmount());
    		
    		// Set button listener
    		Button saveButton = (Button) v.findViewById(R.id.btnSave);
    	    saveButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					saveReminder();
					flipCard();
				}
			});
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
    		
    		DBManager dbManager = new DBManager(context);
    		// ID is -1 if expense was just created??
    		if (id <= 0) {
    			dbManager.addReminder(newTitle, newBilledAmount, 0, newDueDate, "");
    		} else {
    			dbManager.editReminder(id, newTitle, newBilledAmount, 0, newDueDate, "");
    			//dbManager.editReminder(id, newTitle, newBilledAmount, newPaidAmount, newDueDate, "");
    		}
    	}
        
        private void deleteExpense() {
    		// Does nothing
    	}
    }

}
