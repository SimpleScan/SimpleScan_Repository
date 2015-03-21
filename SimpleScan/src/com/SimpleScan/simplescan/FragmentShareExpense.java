package com.SimpleScan.simplescan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentShareExpense extends Fragment
{
	protected String imgPath;
	protected String billAmt;
	
	ImageView ExpenseImg;
	
	public FragmentShareExpense() 
	{
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{	
		View v = inflater.inflate(R.layout.fragment_share_expense, container, false);
		ExpenseImg = (ImageView)v.findViewById(R.id.SE_im);
		ExpenseImg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), CameraActivity.class));
			}
		});
		return v;
	}
	
	public void setImage() {
		Bitmap imageBitmap = BitmapFactory.decodeFile(imgPath);
		ExpenseImg.setImageBitmap(imageBitmap);
	}
	
}