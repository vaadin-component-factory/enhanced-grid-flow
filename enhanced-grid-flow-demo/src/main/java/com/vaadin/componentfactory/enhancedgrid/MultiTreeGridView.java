package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.data.DepartmentData;
import com.vaadin.componentfactory.enhancedtreegrid.EnhancedTreeGrid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

/**
 * Basic multiple selection treegrid example with editing.
 * 
 */
@Route(value = "multi-tree", layout = MainLayout.class)
public class MultiTreeGridView extends Div {

	 public MultiTreeGridView() {
		add(new Paragraph("Basic multiple selection treegrid example with editing")); 
		 
		Div messageDiv = new Div(); 
		 
		DepartmentData departmentData = new DepartmentData();
    	EnhancedTreeGrid<Department> grid = new EnhancedTreeGrid<>();
    	
    	// set selection predicate to indicate which items can be selected
    	grid.setSelectionPredicate(d -> d.getName().startsWith("T"));
    	
    	// set selection mode
    	grid.setSelectionMode(SelectionMode.MULTI);
    	
    	grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
    	
    	// set items
		grid.setItems(departmentData.getRootDepartments(),
		        departmentData::getChildDepartments);
		
		// add columns
		Column<Department> nameColumn = grid.addHierarchyColumn(Department::getName)
		        .setHeader("Department Name");
		Column<Department> managerColumn = grid.addColumn(Department::getManager).setHeader("Manager");				
		managerColumn.setSortable(true);		
		
		// set editable predicate to indicate which items can be edited
        grid.setEditablePredicate(d -> d.getName().startsWith("T"));        
        
        Binder<Department> binder = new Binder<>(Department.class);
		Editor<Department> editor = grid.getEditor();
		editor.setBinder(binder);
		editor.setBuffered(true);
	     
		// define editor components for columns
        TextField nameField = new TextField();
        binder.bind(nameField, Department::getName, Department::setName);
        nameColumn.setEditorComponent(nameField);
               
        // call edit
        grid.addItemDoubleClickListener(event -> {
            grid.editItem(event.getItem());
            nameField.focus();
        });                
        
        // cancel edit
        grid.getElement().addEventListener("keyup",
                event -> editor.cancel())
        .setFilter("event.key === 'Escape' || event.key === 'Esc'");
		
		add(grid, messageDiv);
	 }
	
}
