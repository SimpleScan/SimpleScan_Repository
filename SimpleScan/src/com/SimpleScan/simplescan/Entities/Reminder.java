package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pearse1
 *
 * Stores information for a reminder.
 */
public class Reminder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date reminderDateTime;
	private Date dueDate;
	private String message;
	
	/**
	 * Public Constructor.
	 */
	public Reminder(){
		
	}

	public Date getReminderDateTime() {
		return reminderDateTime;
	}

	public void setReminderDateTime(Date reminderDateTime) {
		this.reminderDateTime = reminderDateTime;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}
