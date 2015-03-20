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

	private double origAmount;
	private double currAmount;
	private String startDate;
	private String endDate;
	
	/**
	 * Public Constructor.
	 */
	public Budget(){
		
	}

	public double getCurrAmount() {
		return currAmount;
	}

	public void setCurrAmount(double amount) {
		this.currAmount = amount;
	}

	public double getOrigAmount() {
		return origAmount;
	}

	public void setOrigAmount(double origAmount) {
		this.origAmount = origAmount;
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
