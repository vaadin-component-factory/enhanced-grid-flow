package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.bean.DepartmentData;
import com.vaadin.componentfactory.enhancedtreegrid.EnhancedTreeGrid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;

@Route(value = "single-tree", layout = MainLayout.class)
public class SingleTreeGridView extends Div {

	 public SingleTreeGridView() {
		Div messageDiv = new Div(); 
		 
		DepartmentData departmentData = new DepartmentData();
    	EnhancedTreeGrid<Department> grid = new EnhancedTreeGrid<>();
    	grid.setSelectionPredicate(d -> d.getName().startsWith("T"));
    	
    	grid.setSelectionMode(SelectionMode.SINGLE);
    	
    	grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
    	
		grid.setItems(departmentData.getRootDepartments(),
		        departmentData::getChildDepartments);
		grid.addHierarchyColumn(Department::getName)
		        .setHeader("Department Name");
		Column<Department> managerColumn = grid.addColumn(Department::getManager).setHeader("Manager");				
		managerColumn.setSortable(true);		
		
		add(grid, messageDiv);
	 }
	
}
