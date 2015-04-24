package com.SimpleScan.listadapter;

import java.util.ArrayList;

import com.SimpleScan.simplescan.R;
import com.SimpleScan.simplescan.Entities.Expense;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExpenseListViewAdapter extends BaseAdapter {
	Activity activity;
	ArrayList<Expense> expenseList;

	public ExpenseListViewAdapter(Activity activity, ArrayList<Expense> data) {
		expenseList = data;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return expenseList.size();
	}

	@Override
	public Expense getItem(int arg0) {
		return expenseList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		if (arg1 == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater.inflate(R.layout.list_single, arg2, false);
		}

		TextView stopName = (TextView) arg1.findViewById(R.id.textView1);
		TextView stopDist = (TextView) arg1.findViewById(R.id.textView2);

		Expense ex = expenseList.get(arg0);

		stopName.setText(ex.getTitle());
		stopDist.setText("$"+ex.getAmount());

		return arg1;
	}

}
