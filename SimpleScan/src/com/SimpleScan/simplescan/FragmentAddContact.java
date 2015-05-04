package com.SimpleScan.simplescan;

import java.util.List;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author David
 * Fragment to add a contact. Checks if the contact you have tried adding is actually a user
 * then sends a contact request over. The other user can then choose to accept or reject the request.
 *
 */
public class FragmentAddContact extends Fragment implements OnClickListener {

	public FragmentAddContact() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_add_contacts, container, false);
		Button b = (Button) v.findViewById(R.id.insertContactButton);
		b.setOnClickListener(this);
		return v;
	}

	/**
	 *  Send a contact request to another existing user 
	 */
	public void addContact() {
		EditText editText = (EditText) getView().findViewById(R.id.editContactText);
		if (!editText.getText().toString().isEmpty() && editText.getText().toString() != "") {
			int newContactID = Integer.parseInt(editText.getText().toString());
			ParseQuery<ParseUser> query = ParseUser.getQuery();
			query.whereEqualTo("username", newContactID + ""); 
			//finds whether the input ID is an actual user, then find the user
																 
			query.findInBackground(new FindCallback<ParseUser>() {
				@Override
				public void done(List<ParseUser> objects, com.parse.ParseException e) {
					EditText editText = (EditText) getView().findViewById(R.id.editContactText);
					TextView contactMessage = (TextView) getView().findViewById(R.id.contactMessage);
					int newContactID = Integer.parseInt(editText.getText().toString());
					if (e == null) {
						if (objects.size() == 1) {
							ParseObject newContact = new ParseObject("ContactRequest");
							newContact.put("id_receiver", newContactID);
							newContact.put("id_sender", Integer.parseInt(ParseUser.getCurrentUser().getUsername()));
							newContact.put("accepted", false);
							newContact.put("rejected", false);
							newContact.saveInBackground();
							reloadContactFragment();
						} else {
							editText.setText("");
							contactMessage.setText("Invalid ID. Please try again");
							contactMessage.setTextColor(Color.RED);
						}
					} else {
						contactMessage.setText("Error. Something went wrong.");
					}

				}
			});
		} else {
			TextView contactMessage = (TextView) getView().findViewById(R.id.contactMessage);
			contactMessage.setText("Invalid ID. Please try again");
			contactMessage.setTextColor(Color.RED);
		}
	}

	@Override
	public void onClick(View v) {
		/* Hide the keyboard on input */
		InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		switch (v.getId()) {
		case R.id.insertContactButton:
			addContact();
			break;
		}
	}

	/** 
	 * Send it back to the contact fragment 
	 */
	public void reloadContactFragment() {
		Fragment fragment = new FragmentContact();
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.mainContent, fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

}
