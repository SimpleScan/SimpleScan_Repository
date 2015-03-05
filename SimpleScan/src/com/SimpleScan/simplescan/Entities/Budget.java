package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;
import java.util.Date;

// TEST TEST TEST ///

/**
 * @author pearse1
 * 
 * Stores information for a budget.
 */
public class Budget implements Serializable {

	private static final long serialVersionUID = 1L;

	private double amount;
	private String startDate;
	private String endDate;
	
	/**
	 * Public Constructor.
	 */
	public Budget(){
		
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


}
