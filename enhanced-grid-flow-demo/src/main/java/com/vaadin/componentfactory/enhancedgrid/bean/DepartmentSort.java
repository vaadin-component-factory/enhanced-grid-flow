package com.vaadin.componentfactory.enhancedgrid.bean;

public class DepartmentSort {

	public static final String NAME = "name";
	
	public static final String MANAGER = "manager";

	private String propertyName;
	
	private boolean descending;
	
	public DepartmentSort(String propertyName, boolean descending) {
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
