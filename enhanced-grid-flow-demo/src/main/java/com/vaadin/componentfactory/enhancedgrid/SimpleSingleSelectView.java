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
 * Basic example with setItems
 */
@Route(value = "single", layout = MainLayout.class)
public class SimpleSingleSelectView extends Div {


    public SimpleSingleSelectView() {
        Div messageDiv = new Div();

        List<Person> personList = getItems();
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        grid.setItems(personList);

        Column<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name");
        grid.addColumn(Person::getAge).setHeader("Age");

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
                
        // You can pre-select items
        grid.select(personList.get(1));
               
        // edit items
        grid.setEditablePredicate(p -> p.getAge() > 18);        
        
        Binder<Person> binder = new Binder<>(Person.class);
		Editor<Person> editor = grid.getEditor();
		editor.setBinder(binder);
		editor.setBuffered(true);
	      
        TextField firstNameField = new TextField();
        binder.bind(firstNameField, Person::getFirstName, Person::setFirstName);
        firstNameColumn.setEditorComponent(firstNameField);
               
        grid.addItemDoubleClickListener(event -> {
            grid.editItem(event.getItem());
            firstNameField.focus();
        });                
       
        add(grid, messageDiv);
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
