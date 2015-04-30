package com.SimpleScan.simplescan.Entities;

/**
 * Represents a shared expense.
 * 
 * @author pearse1
 *
 */
public class SharedExpense {

	public int id;
	
	public String userId1;
	public String userId2;
	public String userId3;
	
	public boolean hasPaid1;
	public boolean hasPaid2;
	public boolean hasPaid3;
	
	/**
	 * Default constructor
	 */
	public SharedExpense(){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserId1() {
		return userId1;
	}

	public void setUserId1(String userId1) {
		this.userId1 = userId1;
	}

	public String getUserId2() {
		return userId2;
	}

	public void setUserId2(String userId2) {
		this.userId2 = userId2;
	}

	public String getUserId3() {
		return userId3;
	}

	public void setUserId3(String userId3) {
		this.userId3 = userId3;
	}

	public boolean isHasPaid1() {
		return hasPaid1;
	}

	public void setHasPaid1(boolean hasPaid1) {
		this.hasPaid1 = hasPaid1;
	}

	public boolean isHasPaid2() {
		return hasPaid2;
	}

	public void setHasPaid2(boolean hasPaid2) {
		this.hasPaid2 = hasPaid2;
	}

	public boolean isHasPaid3() {
		return hasPaid3;
	}

	public void setHasPaid3(boolean hasPaid3) {
		this.hasPaid3 = hasPaid3;
	}
	
	
}
