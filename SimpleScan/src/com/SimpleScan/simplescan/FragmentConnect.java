package com.SimpleScan.simplescan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class FragmentConnect extends Fragment {
	TextView userIdSet;
	public FragmentConnect() {
		// Required empty public constructor
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_connect, container, false);
		return v;
	}
	
	public void onStart() {
		super.onStart();
		userIdSet = (TextView) getView().findViewById(R.id.userId);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) { // not already logged in user
				ParseUser user = new ParseUser();
				user.setUsername(generateRandomId(8));
				user.setPassword("temp");
				user.signUpInBackground(new SignUpCallback() {
					public void done(ParseException e) {
						if (e == null) {
							ParseUser currentUser = ParseUser.getCurrentUser();
							userIdSet.setText(currentUser.getUsername());
						} else {
							// Sign up didn't succeed. Look at the
							// ParseException
							// to figure out what went wrong
							Log.d("error", e.toString());
						}
					}
				});
		}
		else{
			userIdSet.setText(currentUser.getUsername());
		}
	}

	/* Generates a new ID for a user */
	private String generateRandomId(int length) {
		if (length == 0)
			return "";
		return ((int) (Math.random() * 10)) + "" + generateRandomId(length - 1);
	}
}
