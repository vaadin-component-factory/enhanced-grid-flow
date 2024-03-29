package com.vaadin.componentfactory.enhancedgrid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.bean.PersonSort;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFieldFilterDto;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedgrid.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Filter;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.Route;

/**
 * Single selection grid example with lazy loading, editing, filtering and sorting.
 * 
 */
@Route(value = "lazy-single-grid", layout = MainLayout.class)
public class LazySingleSelectView extends Div {
	
	 public LazySingleSelectView() {
		add(new Paragraph("Single selection grid example with lazy loading, editing, filtering and sorting")); 
		 
        Div messageDiv = new Div();
        
        // get lazy data provider
        Filter<Person> gridFilter = new Filter<Person>();
        PersonService personService = new PersonService();
                
        ConfigurableFilterDataProvider<Person,Void,Filter<Person>> dataProvider =
    		DataProvider.<Person, Filter<Person>>fromFilteringCallbacks(
                query -> {
                	List<PersonSort> sortOrders = query.getSortOrders().stream()
                			.map(sortOrder -> new PersonSort(sortOrder.getSorted(), sortOrder.getDirection().equals(SortDirection.ASCENDING)))
                			.collect(Collectors.toList());
                	return personService.fetchPersons(query.getOffset(), query.getLimit(), query.getFilter(), sortOrders);
                },
                query -> (int) personService.getPersonCount(query.getFilter())).withConfigurableFilter();
        
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
        grid.setDataProvider(dataProvider);
        dataProvider.setFilter(gridFilter);  
        
        // set selection predicate to indicate which items can be selected
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        
        // add columns
        // first name column with filtering button on header
        EnhancedColumn<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name", new TextFilterField()).setSortProperty(PersonSort.FIRST_NAME);
        // last name column with filtering button and pre-selected filter by last name = "Allen"
        grid.addColumn(Person::getLastName).setHeader("Last Name", new TextFilterField(new TextFieldFilterDto("Allen"))).setSortProperty(PersonSort.LAST_NAME);
        // age column 
        grid.addColumn(Person::getAge).setHeader("Age").setSortProperty(PersonSort.AGE);
                 
        // add ascending sorting for first name column
        grid.sort(Arrays.asList(new GridSortOrder<Person>(firstNameColumn, SortDirection.ASCENDING)));
        
		// set multisort
		grid.setMultiSort(true);
        
        // select selection mode
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
                
        // set editable predicate to indicate which items can be edited
        grid.setEditablePredicate(p -> p.getAge() > 18);        
        
        Binder<Person> binder = new Binder<>(Person.class);
		Editor<Person> editor = grid.getEditor();
		editor.setBinder(binder);
		editor.setBuffered(true);
	    
		// define editor components for columns
        TextField firstNameField = new TextField();
        binder.bind(firstNameField, Person::getFirstName, Person::setFirstName);
        firstNameColumn.setEditorComponent(firstNameField);
               
        // call edit
        grid.addItemDoubleClickListener(event -> {
            grid.editItem(event.getItem());
            firstNameField.focus();
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
