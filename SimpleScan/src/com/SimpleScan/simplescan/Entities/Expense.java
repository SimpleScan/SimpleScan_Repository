package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;

/**
 * @author pearse1
 *
 * Public Constructor.
 */
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;

	private int amount;
	private User user;
	private boolean hasPaid;
	
	/**
	 * Public Constructor.
	 */
	public Expense(){
		
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isHasPaid() {
		return hasPaid;
	}

	public void setHasPaid(boolean hasPaid) {
		this.hasPaid = hasPaid;
	}
}
