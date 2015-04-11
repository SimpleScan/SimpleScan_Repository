package com.SimpleScan.simplescan;

import java.util.List;

import com.SimpleScan.simplescan.sqlite.DBManager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.SimpleScan.simplescan.Entities.Category;

public class FragmentCategories extends Fragment 
{
	private static final String FRAGMENT_NAME = "View Categories";
	EditText txtCateNmae;
	Spinner spinner ;
	Button btnSave;
	TextView txtSaveCate;
	TextView txtSaveMessage;
	DBManager dbManager;
	List<Category> cateList;
	public FragmentCategories() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		getActivity().setTitle(FRAGMENT_NAME);
		View v = inflater.inflate(R.layout.fragment_categories, container, false);		
		addComponents(v);
		
		return v;
	}
	

	private void addComponents(View view) 
	{
		spinner = (Spinner)view.findViewById(R.id.cate_spinner);
		txtCateNmae = (EditText)view.findViewById(R.id.cate_editName);
		btnSave = (Button)view.findViewById(R.id.cate_btnsave);
		//txtDBInfo = (TextView)view.findViewById(R.id.cate_txtDBInfo);
		dbManager = new DBManager(getActivity());
		txtSaveCate =(TextView)view.findViewById(R.id.cate_txtSaveCate);
		txtSaveMessage =(TextView)view.findViewById(R.id.cate_txtSaveMessage);

		cateList = dbManager.getCategories();
		
		btnSave.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				if(validateUserInput(cateList)) 
				{
					String color = colorPhraser( spinner.getSelectedItem().toString());
					dbManager.addCategory(txtCateNmae.getText().toString(),color);
					txtSaveCate.setText(txtCateNmae.getText().toString());
					txtSaveCate.setTextColor(Color.parseColor(color));
					txtSaveMessage.setText(" has been saved");
				}
			}
		});
	}
	
	private Boolean validateUserInput(List<Category> cateList)
	{
		boolean exist = false;
		if(txtCateNmae.getText().toString().trim().isEmpty())
		{
			Toast.makeText(getActivity(),"Please enter the category name", Toast.LENGTH_SHORT).show();
			return false;
		}
		else if(spinner.getSelectedItemPosition()== 0)
		{
			Toast.makeText(getActivity(),"Please select a color for the category", Toast.LENGTH_SHORT).show();
			return false;
		}
		else
		{		
			for( Category c :cateList)
			{
				//Log.d("DB category name -->", c.getName().trim());
				//Log.d("UserInput Name   -->", txtCateNmae.getText().toString().trim());
				if (c.getTitle().trim().equals(txtCateNmae.getText().toString().trim()))
				{
					Toast.makeText(getActivity(),"Category name exists already", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
			return true	;
		}
			
	}
	
	
	private String colorPhraser(String color)
	{
		String result = "";
	
		if(color.equalsIgnoreCase("Red")) result ="#DF0101";
		else if(color.equalsIgnoreCase("Yellow")) result = "#FFFF00";
		else if(color.equalsIgnoreCase("Green")) result = "#00FF00";
		else if(color.equalsIgnoreCase("Blue")) result = "#2E9AFE";
		else if(color.equalsIgnoreCase("Purple")) result = "#CC2EFA";
		
		return result;
	}
	
}
