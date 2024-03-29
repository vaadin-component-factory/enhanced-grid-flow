package com.vaadin.componentfactory.enhancedgrid;

import java.util.stream.Stream;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.service.DepartmentService;
import com.vaadin.componentfactory.enhancedtreegrid.EnhancedTreeGrid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.Route;

/**
 * Multiple selection treegrid example with lazy loading and editing.
 * 
 */
@Route(value = "lazy-multi-tree", layout = MainLayout.class)
public class LazyMultiTreeGridView extends Div {

	 public LazyMultiTreeGridView() {
		add(new Paragraph("Multiple selection treegrid example with lazy loading and editing")); 
		 
		Div messageDiv = new Div(); 
		 
		EnhancedTreeGrid<Department> grid = new EnhancedTreeGrid<>();
		
		// add columns
		EnhancedColumn<Department> nameColumn = grid.addHierarchyColumn(Department::getName)
		        .setHeader("Department Name");
		EnhancedColumn<Department> managerColumn = grid.addColumn(Department::getManager).setHeader("Manager");				
		managerColumn.setSortable(true);	
		
		// get data
		DepartmentService departmentService = new DepartmentService();	
	    HierarchicalDataProvider<Department, Void> dataProvider =
                new AbstractBackEndHierarchicalDataProvider<Department, Void>() {

            @Override
            public int getChildCount(HierarchicalQuery<Department, Void> query) {
                return (int) departmentService.getChildCount(query.getParent());
            }

            @Override
            public boolean hasChildren(Department item) {
                return departmentService.hasChildren(item);
            }

            @Override
            protected Stream<Department> fetchChildrenFromBackEnd(
                    HierarchicalQuery<Department, Void> query) {
                return departmentService.fetchChildren(query.getParent(), query.getLimit(), query.getOffset()).stream();
            }
        };

        grid.setDataProvider(dataProvider);
		    	
    	// set selection predicate to indicate which items can be selected
    	grid.setSelectionPredicate(d -> d.getName().startsWith("T"));
    	
    	// set selection mode
    	grid.setSelectionMode(SelectionMode.MULTI);
    	
    	grid.asMultiSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
    	
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
