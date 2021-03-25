package com.vaadin.componentfactory.enhancedgrid.bean;

/**
 * This class represents the sorting orders that can be applied to a collection of Persons, to sort them out.
 * 
 */
public class PersonSort {

	public static final String FIRST_NAME = "firstName";
	
	public static final String LAST_NAME= "lastName";
	
	public static final String AGE = "age";

	private String propertyName;
	
	private boolean descending;
	
	public PersonSort(String propertyName, boolean descending) {
		this.propertyName = propertyName;
		this.descending = descending;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public boolean isDescending() {
		return descending;
	}
	public void setDescending(boolean descending) {
		this.descending = descending;
	}
}
