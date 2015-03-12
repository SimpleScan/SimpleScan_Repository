package com.SimpleScan.simplescan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentShareExpense extends Fragment
{
	public FragmentShareExpense() 
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_share_expense, container, false);
		ImageView ExpenseImg = (ImageView)v.findViewById(R.id.SE_im);
		ExpenseImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// To Tai: u can just change the class name here to navigate to your scan bill class 
				startActivity(new Intent(getActivity(), CameraActivity.class));
			}
		});
		return v;
	}
	
}
