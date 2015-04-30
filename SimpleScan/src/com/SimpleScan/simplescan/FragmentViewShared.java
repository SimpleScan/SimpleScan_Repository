package com.SimpleScan.simplescan;

import java.util.ArrayList;
import java.util.List;

import com.SimpleScan.listadapter.ExpenseListViewAdapter;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentViewShared extends Fragment {
	private int myID;
	protected View v;
	Activity a;
	ExpenseListViewAdapter expenseAdapter;
	public FragmentViewShared() { 
		this.myID = Integer.parseInt(ParseUser.getCurrentUser().getUsername());
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_view_shared, container, false);
		a = this.getActivity();
		
		
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		ParseQuery<ParseObject> pending_query = ParseQuery.getQuery("ShareRequest");
		pending_query.whereEqualTo("id_sender",myID);
		//v.findViewById(R.id.contact_frame).setVisibility(View.GONE);
		//v.findViewById(R.id.loading_frame).setVisibility(View.VISIBLE);
		pending_query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects,com.parse.ParseException e) {
				if (e == null) {
					
					ArrayList<Expense> expenseList = new ArrayList<Expense>();
					for (int i = 0; i < objects.size(); i++) {
						Expense exp = new Expense();
						exp.setTitle(objects.get(i).getString("title")+" (shared w/ "+objects.get(i).getInt("id_receiver")+")");
						exp.setAmount(objects.get(i).getDouble("amount"));
					
						expenseList.add(exp);
					}
					expenseAdapter = new ExpenseListViewAdapter(a, expenseList);
					ListView expensesLV = (ListView) v.findViewById(R.id.list_expenses);
					expensesLV.setAdapter(expenseAdapter);
					
				} else {
					Log.d("contact", "Error: " + e.getMessage());
					// Something went wrong.
				}
			//	v.findViewById(R.id.contact_frame).setVisibility(View.VISIBLE);
			//	v.findViewById(R.id.loading_frame).setVisibility(View.GONE);
			}
		});
	}
}
