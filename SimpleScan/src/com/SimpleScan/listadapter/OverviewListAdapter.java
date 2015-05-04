package com.SimpleScan.listadapter;

import java.util.List;

import com.SimpleScan.simplescan.FragmentShareExpense;
import com.SimpleScan.simplescan.Main;
import com.SimpleScan.simplescan.R;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.R.id;
import com.SimpleScan.simplescan.R.layout;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OverviewListAdapter extends BaseAdapter {
		
	private Context context;
	private static List<Expense> exp;
	
	/**
	 * OverviewListAdapter constructor
	 * @param context
	 * @param expenses: expenses represented by the list
	 */
	public OverviewListAdapter(Context context, List<Expense> expenses) {
		super();
		this.exp = expenses;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return exp.size();
	}

	@Override
	public Object getItem(int position) {
		return exp.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = LayoutInflater.from(context).
				inflate(R.layout.expenses_list_overview, parent, false);
		
		final Expense curExp = exp.get(position);
		String date = curExp.getDate();
		String amt = String.valueOf(curExp.getAmount());
		String title = curExp.getTitle();
		
		TextView txtExp = ((TextView) rowView.findViewById(R.id.txtExpense));
		txtExp.setText(date+": $"+amt+" - "+title);
		
		txtExp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Main) context).setTitle("Edit Expense");
				Fragment fragment = FragmentShareExpense.createNewInstance(curExp);
				((Main) context).changeFragment(fragment, "Edit Expense", true);
			}
		});
		
		return rowView;
	}
}
