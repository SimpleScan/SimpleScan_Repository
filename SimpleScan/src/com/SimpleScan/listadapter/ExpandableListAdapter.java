package com.SimpleScan.listadapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.SimpleScan.simplescan.R;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.R.id;
import com.SimpleScan.simplescan.R.layout;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Context context;
	private ArrayList<String> headerMonthList;
	private HashMap<String, ArrayList<String>> listData;
	private HashMap<String, ArrayList<Expense>> expenseList;
 
    public ExpandableListAdapter(Context context, List<Expense> expenses) {
        this.context = context;
        this.headerMonthList = new ArrayList<String>();
        this.listData = new HashMap<String, ArrayList<String>>();
        this.expenseList = new HashMap<String, ArrayList<Expense>>();
        extractInfo(expenses);
    }
    
    private void extractInfo(List<Expense> expenses) {
    	SimpleDateFormat inFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    	SimpleDateFormat outFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
    	SimpleDateFormat shortFormat = new SimpleDateFormat("MMMM dd", Locale.US);
    	for (Expense expense : expenses) {
    		try {
        		String dateString = expense.getDate();
    			Date date = inFormat.parse(dateString);
    			String monthString = outFormat.format(date);
    			if (!listData.containsKey(monthString)) {
    				headerMonthList.add(monthString);
    				listData.put(monthString, new ArrayList<String>());
    				expenseList.put(monthString, new ArrayList<Expense>());
    			}
    			String today = shortFormat.format(date);
    			String dataString = today + ": $" + expense.getAmount()
    					+ " - " + expense.getTitle();
    			listData.get(monthString).add(dataString);
    			expenseList.get(monthString).add(expense);
    		} catch (Exception e) {
    			// Skip this one
    		}
    	}
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) {
        return this.listData.get(this.headerMonthList.get(groupPosition))
                .get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, 
			boolean isLastChild, View view, ViewGroup parent) {
		final String childText = (String) getChild(groupPosition, childPosition);
		
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater)
					this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expenses_list_item, null);
		}
		
		TextView txtListChild = (TextView) view.findViewById(R.id.E_lblListItem);
		txtListChild.setText(childText);
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
        return this.listData.get(this.headerMonthList.get(groupPosition))
                .size();
	}

	@Override
	public Object getGroup(int groupPosition) {
        return this.headerMonthList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
        return this.headerMonthList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
        return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View view, 
			ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		
		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expenses_list_group, null);
        }
 
        TextView lblListHeader = (TextView) view
                .findViewById(R.id.E_lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return view;
	}

	@Override
	public boolean hasStableIds() {
        return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
	}
	
	public Expense getExpense(int groupPosition, int childPosition) {
		return this.expenseList.get(this.headerMonthList.get(groupPosition))
				.get(childPosition);
	}
}
