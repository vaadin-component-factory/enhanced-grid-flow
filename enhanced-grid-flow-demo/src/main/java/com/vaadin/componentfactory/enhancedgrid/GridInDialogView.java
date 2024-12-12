package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.componentfactory.enhancedgrid.bean.Person;
import com.vaadin.componentfactory.enhancedgrid.filtering.TextFilterField;
import com.vaadin.componentfactory.enhancedgrid.service.PersonService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import java.util.List;

/**
 * Opening grid in a dialog.
 */
@Route(value = "in-dialog", layout = MainLayout.class)
public class GridInDialogView extends Div {

  public GridInDialogView() {
    add(new Paragraph("Opening grid in a dialog"));

    EnhancedGrid<Person> grid = new EnhancedGrid<>();
    grid.setItems(getItems());
    grid.addColumn(Person::getFirstName).setHeader("First Name", new TextFilterField());
    grid.addColumn(Person::getLastName).setHeader("Last Name").setSortable(true);    
    grid.setFilterIcon(VaadinIcon.FILTER.create());

    Dialog dialog = new Dialog();
    dialog.add(grid);
    dialog.setWidth("800px");
    dialog.setWidth("600px");

    Button button = new Button("Open dialog to see grid", e -> dialog.open());
    add(button);
  }

  private List<Person> getItems() {
    PersonService personService = new PersonService();
    return personService.fetchAll();
  }
}
