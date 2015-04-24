package com.SimpleScan.simplescan.Entities;

import android.media.Image;

/**
 * @author pearse1
 * 
 * Represents an image and its location.
 */
public class SimpleScanImage {

	private Image image;
	private String name;
	private String path;
	
	/**
	 * Default constructor
	 */
	public SimpleScanImage(){
		
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
