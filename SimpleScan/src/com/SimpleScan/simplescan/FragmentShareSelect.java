package com.SimpleScan.simplescan;

import java.util.ArrayList;
import com.SimpleScan.listadapter.ExpenseListViewAdapter;
import com.SimpleScan.simplescan.Entities.Expense;
import com.SimpleScan.simplescan.sqlite.DBManager;
import com.parse.ParseObject;
import com.parse.ParseUser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentShareSelect extends Fragment {

	private int contactID;
	private int myID;
	View v;
	DBManager dbManager;
	protected ExpenseListViewAdapter expenseAdapter;

	public FragmentShareSelect() {
		// Required empty public constructor
	}

	public FragmentShareSelect(int contactID) {
		this.contactID = contactID;
		this.myID = Integer.parseInt(ParseUser.getCurrentUser().getUsername());
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dbManager = new DBManager(getActivity());
		v = inflater.inflate(R.layout.fragment_share_select, container, false);
		ArrayList<Expense> expensesList = (ArrayList<Expense>) dbManager.getExpenses();
		for (int i = 0; i < expensesList.size(); i++) {
			if (expensesList.get(i).getSharedId() > -1) {
				expensesList.remove(i);
				i--;
			}
		}
		expenseAdapter = new ExpenseListViewAdapter(this.getActivity(), expensesList);
		ListView expensesLV = (ListView) v.findViewById(R.id.list_expenses);
		expensesLV.setAdapter(expenseAdapter);
		expensesLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Expense e = expenseAdapter.getItem(arg2);
				dbManager.addSharedExpense(e.getId(), contactID + "", false, (String) null, false, (String) null, false);
				e.setAmount(e.getAmount() / 2);
				dbManager.editExpense(e.getId(), 1, e.getAmount(), e.getDate(), e.getTitle(), e.getCategory(), e.getImageTitle(), e.getImagePath());
				System.out.println(e.getAmount() + "\n");
				ParseObject newSE = new ParseObject("ShareRequest");
				newSE.put("id_sender", myID);
				newSE.put("id_receiver", contactID);
				newSE.put("status", "pending");
				newSE.put("title", e.getTitle());
				newSE.put("amount", e.getAmount());
				newSE.saveInBackground();
				Fragment fragment = new FragmentViewShared();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.mainContent, fragment);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		});
		return v;
	}

}
