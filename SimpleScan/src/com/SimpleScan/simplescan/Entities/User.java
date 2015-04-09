package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;
import java.util.List;

import android.app.Notification;
import android.media.Image;

/**
 * @author pearse1
 * 
 * Stores information for a user.
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//private long id; -- Kevin
	private String id;
	private String name;
	private Image userPicture;
	private List<Contact> contacts;
	private Budget budget;
	private List<Record> records;
	private List<Reminder> reminders;
	// TODO: Notification is an android class that we may be able to utilize instead here.
	private List<Notification> notifications;
	
	/**
	 * Public Constructor.
	 */
	public User(){
		name = "-1"; // indicator for the first initial 
	}

	//public long getId() {
	public String getId() {
		return id;
	}

	//public void setId(long id) {
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(Image userPicture) {
		this.userPicture = userPicture;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public Budget getBudget() {
		return budget;
	}

	public void setBudget(Budget budget) {
		this.budget = budget;
	}

	public List<Record> getRecords() {
		return records;
	}

	public void setRecords(List<Record> records) {
		this.records = records;
	}

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}
}
