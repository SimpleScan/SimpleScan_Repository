package com.SimpleScan.simplescan;

import com.SimpleScan.simplescan.Entities.User;
import com.SimpleScan.simplescan.sqlite.DBManager;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentProfile extends Fragment 
{
	private static final String FRAGMENT_NAME = "View Profile";
	private Button btnSubmit;
	private TextView txtName;
	private EditText editName;
	private ImageView imgIcon;
	private DBManager dbManager;
	// the unique android ID, used for the parse platform 
	private String android_id;
	public FragmentProfile() 
	{
		// Required empty public constructor
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		getActivity().setTitle(FRAGMENT_NAME);
		View v = inflater.inflate(R.layout.fragment_profile, container, false);
		
		btnSubmit = (Button)v.findViewById(R.id.PRO_btnSubmit);
		txtName = (TextView)v.findViewById(R.id.PRO_txtName);
		editName = (EditText)v.findViewById(R.id.PRO_editName);
		imgIcon = (ImageView)v.findViewById(R.id.PRO_edit_icon);
		android_id = Secure.getString(v.getContext().getContentResolver(),
                Secure.ANDROID_ID); 
		//retrieve the user name from database
		dbManager = new DBManager(getActivity());
		User userInfo = dbManager.getUserInfo();
		// check if the user set up the user name before
		if(!userInfo.getName().equals("-1"))
		{
			Log.i("Fragement Profile -->"," userInfo exist");
			if(!userInfo.getName().isEmpty())
			{
				Log.i("Fragement Profile -->"," Unser name exist");
				txtName.setText(userInfo.getName());
				//TODO, should remove this later, now just wanna make sure the DB has the user ID
				Toast.makeText(getActivity(),userInfo.getId().toString(), Toast.LENGTH_SHORT).show();
			}
		}
		imgIcon.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				editName.setVisibility(v.VISIBLE);
				btnSubmit.setVisibility(v.VISIBLE);
			}
		});
		
		btnSubmit.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				if(editName.getText().toString().isEmpty())
				{
					Toast.makeText(getActivity(),"User Name cannot be empty", Toast.LENGTH_SHORT).show();
				}
				else
				{
					txtName.setText(editName.getText().toString());
					// insert the android device id, the DBmanager will check if the android device existence,
					//if it exists, only update the user name
					dbManager.updateUser(editName.getText().toString(), android_id);
					editName.setVisibility(v.GONE);
					btnSubmit.setVisibility(v.GONE);
				}
			}
			
		});
		
		return v;
	}
}
