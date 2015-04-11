package com.SimpleScan.simplescan.Entities;

public class Profile {
	
	private int id;
	private String firstName;
	private String lastName;
	
	public Profile(int id){
		this.id = id;
	}
	
	/* Called when user presses the save button on the profile page, updates existing profile */
	public void update(String firstName, String lastName){
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
