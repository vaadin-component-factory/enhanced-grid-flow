package com.vaadin.componentfactory.enhancedgrid;

import java.util.List;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.service.PersonService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;

/**
 * Multiple selection grid example with lazy loading
 * 
 */
@Route(value = "lazy-multi-grid", layout = MainLayout.class)
public class LazyMultiSelectView extends Div {
	
	 public LazyMultiSelectView() {
        Div messageDiv = new Div();
        
        // get data 
        PersonService personService = new PersonService();
        DataProvider<Person, Void> dataProvider =
    	    DataProvider.fromCallbacks(
    	        query -> {
    	            int offset = query.getOffset();
    	            int limit = query.getLimit();
    	            List<Person> persons = personService
    	                    .fetch(offset, limit);
    	            return persons.stream();
    	        },
    	        query -> personService.count()
    	);
    	
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
        grid.setDataProvider(dataProvider);
        
        // set selection predicate to indicate which items can be selected
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        
        // add columns
        Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        grid.asMultiSelect().addValueChangeListener(event -> {
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
       
        add(grid, messageDiv);
    }

}
