package com.SimpleScan.simplescan.Entities;

import java.io.Serializable;

import android.graphics.Color;

/**
 * @author pearse1
 *
 * Stores information for a category.
 */
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private Color color;
	
	/**
	 * Public Constructor.
	 */
	private Category(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
