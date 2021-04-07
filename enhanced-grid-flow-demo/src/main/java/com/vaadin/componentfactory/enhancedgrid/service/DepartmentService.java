package com.vaadin.componentfactory.enhancedgrid.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.bean.DepartmentSort;
import com.vaadin.componentfactory.enhancedgrid.data.DepartmentData;
import com.vaadin.flow.component.grid.Filter;
import com.vaadin.flow.function.SerializablePredicate;

public class DepartmentService {
	
	private DepartmentData departmentData = new DepartmentData();
	
    private List<Department> departmentList = departmentData.getDepartments();

    public long getChildCount(Department parent) {
        return departmentList.stream()
                .filter(department -> Objects.equals(parent, department.getParent()))
                .count();
    }

    public Boolean hasChildren(Department parent) {
        return departmentList.stream()
                .anyMatch(department -> Objects.equals(parent, department.getParent()));
    }

    public List<Department> fetchChildren(Department parent) {
        return departmentList.stream()
                .filter(department -> Objects.equals(parent, department.getParent())).collect(Collectors.toList());
    }
	
    public int getChildCount(Department parent, Optional<Filter<Department>> filter) {
    	SerializablePredicate<Department> predicate = filter.map(Filter::getFilterPredicate).orElse(null);
    	
    	List<Department> result = new ArrayList<>();
    	
    	if(hasChildren(parent)) {
    		result = departmentList.stream()
	    		.filter(department -> predicate != null ? predicate.test(department) : true)
	    		.filter(department -> Objects.equals(parent, department.getParent()))
	    		.collect(Collectors.toList());
    	}
    	
        return result.size();
    }

    public List<Department> fetchChildren(Department parent, int offset, int limit, Optional<Filter<Department>> filter, List<DepartmentSort> sortOrders) {
    	// apply sorting
		Comparator<Department> comparator = (o1, o2) -> 0;
		for (DepartmentSort departmentSort : sortOrders) {
			switch (departmentSort.getPropertyName()) {
				case DepartmentSort.NAME:
					comparator = comparator.thenComparing(Department::getName);
					break;
				case DepartmentSort.MANAGER:
					comparator = comparator.thenComparing(Department::getManager);
					break;
			}
			if (!departmentSort.isDescending()) comparator = comparator.reversed();
		}
		List<Department> sortedList = new LinkedList<>(this.fetchChildren(parent));
		sortedList.sort(comparator);
    	
    	// get filter predicate
    	SerializablePredicate<Department> predicate = filter.map(Filter::getFilterPredicate).orElse(null);
    	
    	List<Department> result = new ArrayList<>();
    	
    	if(hasChildren(parent)) {
    		result = sortedList.stream()
	    		.filter(department -> predicate != null ? predicate.test(department) : true)
	    		.filter(department -> Objects.equals(parent, department.getParent()))
	    		.skip(offset)
				.limit(limit)
	    		.collect(Collectors.toList());
    	}
    	
    	return result;
    }

    
}
