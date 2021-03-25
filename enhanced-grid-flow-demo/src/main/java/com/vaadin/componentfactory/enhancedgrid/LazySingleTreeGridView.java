package com.vaadin.componentfactory.enhancedgrid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.componentfactory.enhancedgrid.bean.Department;
import com.vaadin.componentfactory.enhancedgrid.bean.DepartmentSort;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedgrid.service.DepartmentService;
import com.vaadin.componentfactory.enhancedtreegrid.EnhancedTreeGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Filter;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.Route;

/**
 * Single selection treegrid example with lazy loading, editing, filtering and sorting.
 * 
 */
@Route(value = "lazy-single-tree", layout = MainLayout.class)
public class LazySingleTreeGridView extends Div {

	 public LazySingleTreeGridView() {
		add(new Paragraph("Single selection treegrid example with lazy loading, editing, filtering and sorting")); 
		 
		Div messageDiv = new Div(); 
		 
		EnhancedTreeGrid<Department> grid = new EnhancedTreeGrid<>();
		
		// add columns
		Column<Department> nameColumn = grid.addHierarchyColumn(Department::getName).setHeader("Department Name").setSortProperty(DepartmentSort.NAME);
		Column<Department> managerColumn = grid.addColumn(Department::getManager).setHeader("Manager", new TextFilterField()).setSortProperty(DepartmentSort.MANAGER);				
						
		// add ascending sorting for manager column
		grid.sort(Arrays.asList(new GridSortOrder<Department>(managerColumn, SortDirection.ASCENDING)));
		
		// get data
		DepartmentService departmentService = new DepartmentService();	
		
		HierarchicalConfigurableFilterDataProvider<Department, Void, Filter<Department>> dataProvider =
                new AbstractBackEndHierarchicalDataProvider<Department, Filter<Department>>() {

            @Override
            public int getChildCount(HierarchicalQuery<Department, Filter<Department>> query) {
                return (int) departmentService.getChildCount(query.getParent(), query.getFilter());
            }

            @Override
            public boolean hasChildren(Department item) {
                return departmentService.hasChildren(item);
            }

            @Override
            protected Stream<Department> fetchChildrenFromBackEnd(
                    HierarchicalQuery<Department, Filter<Department>> query) {
            		List<DepartmentSort> sortOrders = query.getSortOrders().stream()
            			.map(sortOrder -> new DepartmentSort(sortOrder.getSorted(), sortOrder.getDirection().equals(SortDirection.ASCENDING)))
            			.collect(Collectors.toList());
                return departmentService.fetchChildren(query.getParent(), query.getOffset(), query.getLimit(), query.getFilter(), sortOrders).stream();
            }
        }.withConfigurableFilter();

        grid.setDataProvider(dataProvider);
		    	
    	// set selection predicate to indicate which items can be selected
    	grid.setSelectionPredicate(d -> d.getName().startsWith("T"));
    	
    	// set selection mode
    	grid.setSelectionMode(SelectionMode.SINGLE);
    	
    	grid.asSingleSelect().addValueChangeListener(event -> {
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
