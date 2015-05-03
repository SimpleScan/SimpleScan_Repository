package com.SimpleScan.simplescan;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class FragmentConnect extends Fragment implements OnClickListener {
	TextView userIdSet;
	TextView firstNameTV;
	TextView lastNameTV;
	Profile pf;

	public FragmentConnect() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_connect, container, false);
		Button b = (Button) v.findViewById(R.id.saveButton);
		b.setOnClickListener(this);
		return v;
	}

	public void onStart() {
		super.onStart();

		userIdSet = (TextView) getView().findViewById(R.id.userId);
		firstNameTV = (TextView) getView().findViewById(R.id.editFirstName);
		lastNameTV = (TextView) getView().findViewById(R.id.editLastName);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) { // not already logged in user
			ParseUser user = new ParseUser();
			user.setUsername(generateRandomId(8));
			user.setPassword("temp");
			user.signUpInBackground(new SignUpCallback() {
				public void done(ParseException e) {
					if (e == null) {
						ParseUser currentUser = ParseUser.getCurrentUser();
						userIdSet.setText("ID :" + currentUser.getUsername());
						loadInfo();
					} else {
						// Sign up didn't succeed. Look at the
						// ParseException
						// to figure out what went wrong
						Log.d("error", e.toString());
					}
				}
			});
		} else {
			userIdSet.setText("ID :" + currentUser.getUsername());
			loadInfo();
		}
	}

	private void loadInfo() {
		int currId = Integer.parseInt(ParseUser.getCurrentUser().getUsername());
		pf = new Profile(currId);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
		query.whereEqualTo("id", currId);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> profile, ParseException e) {
				if (e == null) {
					if (profile.size() == 0) { /* generate new empty profile if one doesn't exist */
						ParseObject newProfile = new ParseObject("Profile");
						newProfile.put("id", Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
						newProfile.put("firstName", "");
						newProfile.put("lastName", "");
						newProfile.saveInBackground();
						getView().findViewById(R.id.connect_frame).setVisibility(View.VISIBLE);
						getView().findViewById(R.id.loading_frame).setVisibility(View.GONE);
					} else {
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

	/* Generates a new ID for a user */
	private String generateRandomId(int length) {
		if (length == 0)
			return "";
		return ((int) (Math.random() * 10)) + "" + generateRandomId(length - 1);
	}

	@Override
	public void onClick(View v) {
		/* Hide the keyboard on input */
		InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		switch (v.getId()) {
		case R.id.saveButton:
			saveProfile();
			break;
		}
	}

	public void saveProfile() {
		int currId = Integer.parseInt(ParseUser.getCurrentUser().getUsername());
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
		query.whereEqualTo("id", currId);
		// Retrieve the object by id
		((TextView) getView().findViewById(R.id.loadingText)).setText("Saving ...");
		getView().findViewById(R.id.connect_frame).setVisibility(View.GONE);
		getView().findViewById(R.id.loading_frame).setVisibility(View.VISIBLE);
		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> profile, ParseException e) {
				if (e == null) {
					if (profile.size() > 0) {
						String firstName = ((TextView) getView().findViewById(R.id.editFirstName)).getText().toString();
						String lastName = ((TextView) getView().findViewById(R.id.editLastName)).getText().toString();
						profile.get(0).put("firstName", firstName);
						profile.get(0).put("lastName", lastName);
						profile.get(0).saveInBackground();
						ParseUser.getCurrentUser().put("name", firstName + " " + lastName);
						ParseUser.getCurrentUser().saveInBackground();
						getView().findViewById(R.id.connect_frame).setVisibility(View.VISIBLE);
						getView().findViewById(R.id.loading_frame).setVisibility(View.GONE);
					}
				} else {
					Log.d("profile", "Error: " + e.getMessage());
				}
			}
		});
	}
}
