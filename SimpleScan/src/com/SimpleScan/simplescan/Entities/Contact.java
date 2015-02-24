package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;

import android.media.Image;

/**
 * @author pearse1
 *
 * Stores information for a user contact.
 */
public class Contact implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Image contactPicture;
		
	/**
	 * Public Constructor.
	 */
	public Contact(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getContactPicture() {
		return contactPicture;
	}

	public void setContactPicture(Image contactPicture) {
		this.contactPicture = contactPicture;
	}
}
