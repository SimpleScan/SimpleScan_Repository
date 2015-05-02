package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;

/**
 *
 * Stores information for a reminder.
 */
public class Reminder implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	private String title;
	private String dueDate;
	private String remindDate;
	
	private double billedAmount;
	private double paidAmount;
	
	private boolean remindAgain;
	
	/**
	 * Public Constructor.
	 */
	public Reminder(){
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getRemindDate() {
		return remindDate;
	}

	public void setRemindDate(String remindDate) {
		this.remindDate = remindDate;
	}

	public double getBilledAmount() {
		return billedAmount;
	}

	public void setBilledAmount(double billedAmount) {
		this.billedAmount = billedAmount;
	}

	public double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRemindAgain() {
		return remindAgain;
	}

	public void setRemindAgain(boolean remindAgain) {
		this.remindAgain = remindAgain;
	}

	
}