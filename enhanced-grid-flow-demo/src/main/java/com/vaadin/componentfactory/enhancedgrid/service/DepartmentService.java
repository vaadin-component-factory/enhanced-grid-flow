package com.vaadin.componentfactory.enhancedgrid.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.data.DepartmentData;

public class DepartmentService {

	private DepartmentData departmentData = new DepartmentData();
    private List<Department> departmentList = departmentData.getDepartments();

    public long getChildCount(Department parent) {
        return departmentList.stream()
                .filter(account -> Objects.equals(parent, account.getParent()))
                .count();
    }

    public Boolean hasChildren(Department parent) {

        return departmentList.stream()
                .anyMatch(account -> Objects.equals(parent, account.getParent()));
    }

    public List<Department> fetchChildren(Department parent) {

        return departmentList.stream()
                .filter(account -> Objects.equals(parent, account.getParent())).collect(Collectors.toList());
    }
	
}
