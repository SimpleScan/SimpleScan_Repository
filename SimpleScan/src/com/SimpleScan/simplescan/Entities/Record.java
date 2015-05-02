package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import android.media.Image;

/**
 * 
 * Stores information for a record.
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;

	private String recordName;
	private int amount;	
	private Date transactionDate;
	private Category category;
	private boolean hasImage;
	// TODO: We may need to create BillImage like we talked about (separate class to store scanned bills)
	private Image image;
	private Map<User, Expense> usersShared;
	
	/**
	 * Public Constructor.
	 */
	public Record(){
		
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public boolean isHasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Map<User, Expense> getUsersShared() {
		return usersShared;
	}

	public void setUsersShared(Map<User, Expense> usersShared) {
		this.usersShared = usersShared;
	}
}
