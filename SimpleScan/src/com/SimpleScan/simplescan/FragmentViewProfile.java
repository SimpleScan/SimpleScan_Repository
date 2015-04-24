package com.SimpleScan.simplescan;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.SimpleScan.simplescan.Entities.Profile;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class FragmentViewProfile extends Fragment implements OnClickListener{
	TextView userIdSet;
	TextView firstNameTV;
	TextView lastNameTV;
	Profile pf;
	int contactID = 0;
	public FragmentViewProfile() {
		// Required empty public constructor
	}
	
	public FragmentViewProfile(int contactID) {
		this.contactID = contactID;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_view_profile, container, false);
		Button b = (Button) v.findViewById(R.id.removeButton);
		Button b2 = (Button) v.findViewById(R.id.shareButton);
		b.setOnClickListener(this);
		b2.setOnClickListener(this);
		return v;
	}
	
	public void onStart() {
		super.onStart();
		
		userIdSet = (TextView) getView().findViewById(R.id.userId);
		userIdSet.setText("ID:"+contactID);
		firstNameTV = (TextView) getView().findViewById(R.id.firstName);
		lastNameTV =(TextView) getView().findViewById(R.id.lastName);
		loadInfo();
		
	}

	private void loadInfo(){
		int currId = contactID;
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
		query.whereEqualTo("id", contactID);
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> profile, ParseException e) {
		        if (e == null) {
		        	if (profile.size() == 0) { /* generate new empty profile if one doesn't exist */
		        		ParseObject newProfile = new ParseObject("Profile");
		        		firstNameTV.setText("Not set");
		        		lastNameTV.setText("Not set");
		        		getView().findViewById(R.id.connect_frame).setVisibility(View.VISIBLE);
						getView().findViewById(R.id.loading_frame).setVisibility(View.GONE);
		        	}
		        	else{
		        		String firstName = profile.get(0).getString("firstName");
		        		String lastName = profile.get(0).getString("lastName");
		        		firstNameTV.setText(firstName);
		        		lastNameTV.setText(lastName);
		        		getView().findViewById(R.id.connect_frame).setVisibility(View.VISIBLE);
						getView().findViewById(R.id.loading_frame).setVisibility(View.GONE);
		        	}
		        } else {
		            Log.d("profile", "Error: " + e.getMessage());
		        }
		    }
		});
	}

	@Override
	public void onClick(View v) {
		/* Hide the keyboard on input */
		InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		switch (v.getId()) {
			case R.id.removeButton:
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Contact");
				query.whereEqualTo("id_friend", contactID);
				query.whereEqualTo("id_user", Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
				query.findInBackground(new FindCallback<ParseObject>() {
				    public void done(List<ParseObject> profile, ParseException e) {
				        if (e == null) {
				        	if (profile.size() == 0) { /* generate new empty profile if one doesn't exist */
				       
				        	}
				        	else{
				        		profile.get(0).deleteInBackground();
				        		ParseQuery<ParseObject> query = ParseQuery.getQuery("Contact");
				    			query.whereEqualTo("id_user", contactID);
				    			query.whereEqualTo("id_friend", Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
				    			query.findInBackground(new FindCallback<ParseObject>() {
				    			    public void done(List<ParseObject> profile, ParseException e) {
				    			        if (e == null) {
				    			        	if (profile.size() == 0) { /* generate new empty profile if one doesn't exist */
				    			       
				    			        	}
				    			        	else{
				    			        		profile.get(0).deleteInBackground();
				    			        	}
				    			        	Fragment fragment = new FragmentContact();
				    						FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				    						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				    						fragmentTransaction.replace(R.id.mainContent, fragment);
				    						fragmentTransaction.addToBackStack(null);
				    						fragmentTransaction.commit();
				    			        } else {
				    			            Log.d("profile", "Error: " + e.getMessage());
				    			        }
				    			    }
				    			});
				        	}
				        } else {
				            Log.d("profile", "Error: " + e.getMessage());
				        }
				    }
				});
	
				break;
			case R.id.shareButton:
				Fragment fragment = new FragmentShareSelect(contactID);
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.mainContent, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
	
				break;
		}
	}
	
}
