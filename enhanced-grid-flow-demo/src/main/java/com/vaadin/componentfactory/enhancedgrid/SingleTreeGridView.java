package com.vaadin.componentfactory.enhancedgrid;

import java.util.List;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.data.DepartmentData;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedtreegrid.EnhancedTreeGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;

/**
 * Basic single selection treegrid example with editing, filtering and sorting.
 * 
 */
@Route(value = "single-tree", layout = MainLayout.class)
public class SingleTreeGridView extends Div {

	 public SingleTreeGridView() {
		add(new Paragraph("Basic single selection treegrid example with editing, filtering and sorting")); 
		 
		Div messageDiv = new Div(); 
		 
		DepartmentData departmentData = new DepartmentData();
    	EnhancedTreeGrid<Department> grid = new EnhancedTreeGrid<>();
    	
    	// set selection predicate to indicate which items can be selected
    	grid.setSelectionPredicate(d -> d.getName().startsWith("T"));
    	
    	// set selection mode
    	grid.setSelectionMode(SelectionMode.SINGLE);
    	
    	grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
    	
    	// set items
		grid.setItems(departmentData.getRootDepartments(),
		        departmentData::getChildDepartments);
		
		// add columns
		EnhancedColumn<Department> nameColumn = grid.addHierarchyColumn(Department::getName)
		        .setHeader("Department Name");
		EnhancedColumn<Department> managerColumn = grid.addColumn(Department::getManager).setHeader("Manager", new TextFilterField());				
		managerColumn.setSortable(true);		
		
		// add pre-selected ascendent order for department name column
        List<GridSortOrder<Department>> sortByName = new GridSortOrderBuilder<Department>()
    	      .thenAsc(nameColumn).build();
    	grid.sort(sortByName);
		
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
		
        // add layout for buttons
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidthFull();
        horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
        // add button to clear all selected filters
        Button clearFiltersButton = new Button("Clear Filters", e -> grid.clearAllFilters());
        horizontalLayout.add(clearFiltersButton);
        // add button to clear all sorting
        Button clearSortingButton = new Button("Clear Sorting", e -> grid.sort(null));
        horizontalLayout.add(clearSortingButton);  
        
		add(grid, messageDiv, horizontalLayout);
	 }
	
}
