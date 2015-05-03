package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * Stores information for an expense.
 */
public class Expense implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id = -1;
	private int sharedId = -1;
	private double amount;
	private String title;
	private String date;
	private boolean paid;
	private Category category;

	private String imageTitle;
	private String imagePath;
	
	/**
	 * Public Constructor.
	 */
	public Expense(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSharedId() {
		return sharedId;
	}

	public void setSharedId(int sharedId) {
		this.sharedId = sharedId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getImageTitle() {
		return imageTitle;
	}

	public void setImageTitle(String imageTitle) {
		this.imageTitle = imageTitle;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagepath) {
		this.imagePath = imagepath;
	}
	
	public String toString() {
		return date + ": $" + amount + " - " + title;
	}

}