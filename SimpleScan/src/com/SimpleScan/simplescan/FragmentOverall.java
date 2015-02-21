package com.SimpleScan.simplescan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentOverall extends Fragment 
{

	public FragmentOverall() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_overall, container, false);
		return v;
	}
}
