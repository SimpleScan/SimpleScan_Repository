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

	private int amount;
	private Date startDate;
	private Date endDate;
	
	/**
	 * Public Constructor.
	 */
	public Budget(){
		
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}	
}
