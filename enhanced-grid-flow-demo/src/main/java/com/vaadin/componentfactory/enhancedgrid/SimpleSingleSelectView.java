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
import com.vaadin.flow.router.Route;

/**
 * Basic single selection grid example with setItems
 */
@Route(value = "single", layout = MainLayout.class)
public class SimpleSingleSelectView extends Div {

    public SimpleSingleSelectView() {
        Div messageDiv = new Div();

        List<Person> personList = getItems();
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
        
        // set selection predicate to indicate which items can be selected
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        grid.setItems(personList);
     
        // add columns
        Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");
        firstNameColumn.setSortable(true);

        // set selection mode
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
                
        // can pre-select items
        grid.select(personList.get(1));
               
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

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
