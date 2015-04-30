package com.SimpleScan.simplescan;

import java.util.List;

import com.SimpleScan.contactui.ContactUILoader;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentContact extends Fragment implements OnClickListener {
	ContactUILoader ui;
	String contactID;
	public FragmentContact() {
		ui = new ContactUILoader();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_contacts, container, false);
		loadContacts();
		return v;
	}

	public void onStart() {
		super.onStart();
		TextView addContact = (TextView) getView().findViewById(R.id.addContact);
		addContact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragment fragment = new FragmentAddContact();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.mainContent, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
	}
	/* Loads all currently accepted contacts */
	private void loadContacts(){
		ParseQuery<ParseObject> received_query = ParseQuery.getQuery("Contact");
		received_query.whereEqualTo("id_user",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		received_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, com.parse.ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {
						loadAcceptedContact(objects.get(i).getInt("id_friend")+ "");
					}
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
				/* Load next set of contact requests */
				loadReceivedRequests();
			}
		});
	}
	/* Loads all the current user's received requests from the Parse DB */
	private void loadReceivedRequests() {
		ParseQuery<ParseObject> received_query = ParseQuery.getQuery("ContactRequest");
		received_query.whereEqualTo("id_receiver",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		received_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, com.parse.ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {
						loadReceivedContact(objects.get(i).getInt("id_sender")+ "");
					}
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
				/* Load next set of contact requests */
				loadPendingRequests();
			}
		});
	}

	/* Loads all the current user's pending requests from the Parse DB */

	private void loadPendingRequests() {
		ParseQuery<ParseObject> pending_query = ParseQuery.getQuery("ContactRequest");
		pending_query.whereEqualTo("id_sender",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		pending_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects,com.parse.ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {
						loadPendingContact(objects.get(i).getInt("id_receiver")+ "");
					}
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
				getView().findViewById(R.id.contact_frame).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.loading_frame).setVisibility(View.GONE);
			}
		});
	}
	/*
	 * Loads a contact that you have accepted 
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void loadAcceptedContact(String acceptedContact) {
		contactID = acceptedContact;
		/* Retrieve the parent layout */
		LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.acontacts_layout);
		/* Create a new contact linear layout to add all contact widgets in */
		LinearLayout contactLL = new LinearLayout(this.getActivity());
		contactLL = ui.loadContactLayout(contactLL);
	    
		/* Create the TextView containing contact name */
		TextView contactTV = new TextView(this.getActivity());
		contactTV = ui.loadTextView(contactTV, acceptedContact);
		/* Create the TextView containing show pending request */
		TextView acceptedTV = new TextView(this.getActivity());
		acceptedTV = ui.loadStatusTV(acceptedTV,"View Details");
		acceptedTV.setId(Integer.parseInt(acceptedContact));
		acceptedTV.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int id = v.getId();
				Fragment fragment = new FragmentViewProfile(id);
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.mainContent, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
		contactLL.addView(contactTV);
		contactLL.addView(acceptedTV);
		linearLayout.addView(contactLL);
	}
	
	/*
	 * Loads a contact that you have previously sent a contact request to, but
	 * has not yet accepted or rejected it.
	 */
	private void loadPendingContact(String pendingContact) {
		/* Retrieve the parent layout */
		LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.rcontacts_layout);
		/* Create a new contact linear layout to add all contact widgets in */
		LinearLayout contactLL = new LinearLayout(this.getActivity());
		contactLL = ui.loadContactLayout(contactLL);
		/* Create the TextView containing contact name */
		TextView contactTV = new TextView(this.getActivity());
		contactTV = ui.loadTextView(contactTV, pendingContact);
		/* Create the TextView containing show pending request */
		TextView pendingTV = new TextView(this.getActivity());
		pendingTV = ui.loadStatusTV(pendingTV,"Request Pending");
		contactLL.addView(contactTV);
		contactLL.addView(pendingTV);
		linearLayout.addView(contactLL);
	}
	
	/*
	 * Loads a contact that you have received a request from
	 */
	private void loadReceivedContact(String newContact) {
		/* Retrieve the parent layout */
		LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.rcontacts_layout);
		/* Create a new contact linear layout to add all contact widgets in */
		LinearLayout contactLL = new LinearLayout(this.getActivity());
		contactLL = ui.loadContactLayout(contactLL);
		/* Create the TextView containing contact name */
		TextView contactTV = new TextView(this.getActivity());
		contactTV = ui.loadTextView(contactTV, newContact);
		contactLL.addView(contactTV);
		/* Create the accept and reject buttons and add to layout */
		Button acceptBTN = new Button(this.getActivity());
		acceptBTN = ui.loadRequestButton(acceptBTN,Color.GREEN,"Accept", newContact);
		acceptBTN.setOnClickListener(this);
		Button rejectBTN = new Button(this.getActivity());
		rejectBTN = ui.loadRequestButton(rejectBTN,Color.RED,"Reject", newContact);
		rejectBTN.setOnClickListener(this);
		contactLL.addView(acceptBTN);
		contactLL.addView(rejectBTN);
		linearLayout.addView(contactLL);
	}
	
	/* Called to accept friend request of a specified id */
	private void acceptRequest(int id){
		ParseQuery<ParseObject> pending_query = ParseQuery.getQuery("ContactRequest");
		pending_query.whereEqualTo("id_receiver",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		pending_query.whereEqualTo("id_sender",id);
		pending_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects,com.parse.ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {
						objects.get(i).deleteInBackground();
					}
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
			}
		});
		/* Add contact under both user and friend */
		ParseObject newContact = new ParseObject("Contact");
		newContact.put("id_friend",  id);
		newContact.put("id_user",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		newContact.saveInBackground();
		ParseObject newContactFriend = new ParseObject("Contact");
		newContactFriend.put("id_user",  id);
		newContactFriend.put("id_friend",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		newContactFriend.saveInBackground();
	}
	/* Called to reject friend request of a specified id */
	private void rejectRequest(int id){
		ParseQuery<ParseObject> pending_query = ParseQuery.getQuery("ContactRequest");
		pending_query.whereEqualTo("id_receiver",Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
		pending_query.whereEqualTo("id_sender",id);
		pending_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects,com.parse.ParseException e) {
				if (e == null) {
					for (int i = 0; i < objects.size(); i++) {
						objects.get(i).deleteInBackground();
					}
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		Button b = (Button)v;
		int bID = b.getId();
	    String buttonText = b.getText().toString();
	    String statusTxt = "";
		if(buttonText.equals("Accept")){
			acceptRequest(bID);
			statusTxt = "Request Accepted";
		}
		else if(buttonText.equals("Reject")){
			rejectRequest(bID);
			statusTxt = "Request Rejected";
		}
		LinearLayout parent = (LinearLayout)v.getParent();
		parent.removeView(v);
		parent.removeView((TextView) getView().findViewById(v.getId()));
		/* Response Text View */
		TextView acceptTV = new TextView(this.getActivity());
		acceptTV = ui.loadStatusTV(acceptTV,statusTxt);
		/* Set parameters of new TextView */
		/* Insert into layouts */
		parent.addView(acceptTV);
	}
}
