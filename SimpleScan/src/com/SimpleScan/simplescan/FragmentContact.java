package com.SimpleScan.simplescan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentContact extends Fragment
{

	public FragmentContact() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{		
		View v = inflater.inflate(R.layout.fragment_contacts, container, false);
		TextView addContact = (TextView) getView().findViewById(R.id.addContact);
	
		addContact.setOnClickListener(new OnClickListener() {

	        @Override
	        public void onClick(View v) {
	        	Fragment fragment = new FragmentAddContact();
	            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
	            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	            fragmentTransaction.replace(R.id.contact_frame, fragment);
	            fragmentTransaction.addToBackStack(null);
	            fragmentTransaction.commit();
	        }
	    });
		return v;
	}
	
}
