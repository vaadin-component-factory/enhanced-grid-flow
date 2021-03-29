# 


## Development instructions

Build the project and install the add-on locally:
```
mvn clean install
```
Starting the demo server:

Go to enhanced-grid-flow-demo and run:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080

## Description 

The grid component provides support to Vaadin Grid and TreeGrid to:



## How to use it

Create a new component EnhancedGrid/EnhancedTreeGrid and use it like a Grid.


## Examples

Here is a simple example on how to try out the EnhancedGrid component:

* Creating the EnhancedGrid
```java
EnhancedGrid<Person> grid = new EnhancedGrid<>();
```
* Adding a selection predicate to indicate which items can be selected
```java
grid.setSelectionPredicate(p -> p.getAge() > 18);
```
* Adding a editable predicate to indicate which items can be edited
```java
grid.setEditablePredicate(p -> p.getAge() > 18); 
```
* Adding columns 
```java
// first name column with filtering button on header
EnhancedColumn<Person> firstNameColumn = grid.addColumn(Person::getFirstName).setHeader("First Name", new TextFilterField());
// last name column with filtering button and pre-selected filter by last name = "Allen"
grid.addColumn(Person::getLastName).setHeader("Last Name", new TextFilterField(new TextFieldFilterDto("Allen")));
// simple age column 
grid.addColumn(Person::getAge).setHeader("Age");

// add pre-selected ascendent order for firstNameColumn
List<GridSortOrder<Person>> sortByFirstName = new GridSortOrderBuilder<Person>()
	  .thenAsc(firstNameColumn).build();
grid.sort(sortByFirstName);    
```
* Adding editor and binder to edit items
```java
Binder<Person> binder = new Binder<>(Person.class);
Editor<Person> editor = grid.getEditor();
editor.setBinder(binder);
editor.setBuffered(true);
 
// define editor components for editable column firstNameColumn
TextField firstNameField = new TextField();
binder.bind(firstNameField, Person::getFirstName, Person::setFirstName);
firstNameColumn.setEditorComponent(firstNameField);
```
* Editing an item
```java
grid.addItemDoubleClickListener(event -> {
  grid.editItem(event.getItem());
});             
```

For more complete examples on <b>EnhancedGrid</b>, see:

com.vaadin.componentfactory.enhancedgrid.SimpleSingleSelectView
com.vaadin.componentfactory.enhancedgrid.SimpleMultiSelectView   
com.vaadin.componentfactory.enhancedgrid.LazySingleSelectView  
com.vaadin.componentfactory.enhancedgrid.LazyMultiSelectView

For more complete examples on <b>EnhancedTreeGrid</b>, see:

com.vaadin.componentfactory.enhancedgrid.SingleTreeGridView   
com.vaadin.componentfactory.enhancedgrid.MultiTreeGridView  
com.vaadin.componentfactory.enhancedgrid.LazySingleTreeGridView
com.vaadin.componentfactory.enhancedgrid.LazyMultiTreeGridView

## Demo

You can check the demo here: https://incubator.app.fi/enhanced-grid-flow-demo/

## Missing features or bugs

You can report any issue or missing feature on github: https://github.com/vaadin-component-factory/enhanced-grid-flow
