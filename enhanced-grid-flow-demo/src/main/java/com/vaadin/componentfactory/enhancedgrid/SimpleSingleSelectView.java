package com.vaadin.componentfactory.enhancedgrid;

import java.util.Comparator;
import java.util.List;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFieldFilterDto;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedgrid.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

/**
 * Basic single selection grid example with setItems, editing, filtering and sorting.
 */
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "single-grid", layout = MainLayout.class)
public class SimpleSingleSelectView extends Div {

    public SimpleSingleSelectView() {
    	add(new Paragraph("Basic single selection grid example with editing, filtering and sorting"));    	
    	
        Div messageDiv = new Div();

        List<Person> personList = getItems();
        EnhancedGrid<Person> grid = new EnhancedGrid<>();
                
        // set selection predicate to indicate which items can be selected
        grid.setSelectionPredicate(p -> p.getAge() > 18);
        grid.setItems(personList);
     
        // add columns
        // first name column with filtering button on header
        EnhancedColumn<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name", new TextFilterField());
        // last name column with filtering button and pre-selected filter by last name = "Allen"
        grid.addColumn(Person::getLastName).setHeader("Last Name", new TextFilterField(new TextFieldFilterDto("Allen")));
        // age column with renderer
        NumberRenderer<Person> ageRenderer = new NumberRenderer<Person>(Person::getAge, "Age: %d");
        EnhancedColumn<Person> ageColumn = grid.addColumn(ageRenderer).setComparator(Comparator.comparing(Person::getAge)).setHeader("Age", new TextFilterField());
        ageColumn.setValueProvider(p -> String.valueOf(p.getAge()));
                      
        // add pre-selected descendent order for first name column
        List<GridSortOrder<Person>> sortByFirstName = new GridSortOrderBuilder<Person>()
    	      .thenDesc(firstNameColumn).build();
    	grid.sort(sortByFirstName);

        // set selection mode
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);

        grid.asSingleSelect().addValueChangeListener(event -> {
            String message = String.format("Selection changed from %s to %s",
                event.getOldValue(), event.getValue());
            messageDiv.setText(message);
        });
                
        // can pre-select items
        grid.select(personList.get(0));
               
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
       
        // add column with save button, to invoke when editing item
        grid.addComponentColumn(person -> {
            Button save = new Button("Save");
            
            save.addClickListener(e -> {
                editor.save();                
                firstNameField.focus();
            });
            save.setEnabled(editor.isOpen());
            return save;
        });
        
        // show save item message
        editor.addSaveListener(
                event -> messageDiv.setText("Item saved: " + event.getItem().getFirstName() + ", "
                		+ event.getItem().getLastName() + ", "
                        + event.getItem().getAge()));
        
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
       
        add( grid, messageDiv, horizontalLayout);
   
    }

    private List<Person> getItems() {
        PersonService personService = new PersonService();
        return personService.fetchAll();
    }
}
