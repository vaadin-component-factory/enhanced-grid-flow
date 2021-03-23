package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedgrid.service.PersonService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Filter;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.Route;

/**
 * Single selection grid example with lazy loading
 * 
 */
@Route(value = "lazy-single-grid", layout = MainLayout.class)
public class LazySingleSelectView extends Div {
	
	 public LazySingleSelectView() {
        Div messageDiv = new Div();
        
        // get lazy data provider
        Filter gridFilter = new Filter();
        PersonService personService = new PersonService();
                
        ConfigurableFilterDataProvider<Person,Void,Filter> dataProvider =
        		DataProvider.<Person, Filter>fromFilteringCallbacks(
    	                query -> personService.fetchPersons(query.getOffset(), query.getLimit(), query.getFilter()),
    	                query -> (int) personService.getPersonCount(query.getFilter())).withConfigurableFilter();
        
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
        grid.setDataProvider(dataProvider);
        dataProvider.setFilter(gridFilter);  
        
        // set selection predicate to indicate which items can be selected
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        
        // add columns
        Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name", new TextFilterField());
        grid.addColumn(Person::getAge).setHeader("Age");

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
                event -> grid.getEditor().cancel())
        .setFilter("event.key === 'Escape' || event.key === 'Esc'");
       
        add(grid, messageDiv);
    }	 

}
