package com.vaadin.componentfactory.enhancedgrid;

/*-
 * #%L
 * Enhanced Grid
 * %%
 * Copyright (C) 2020 - 2021 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.ColumnPathRenderer;
import com.vaadin.flow.component.grid.FilterFieldDto;
import com.vaadin.flow.component.grid.FilterField;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * 
 * {@link Grid.Column Column} that extends setHeader methods to add a filter button 
 * and a {@link FilterField filter component} to perform column's filtering.
 *  
 */
@CssImport(value = "./styles/enhanced-column.css")
public class EnhancedColumn<T> extends Grid.Column<T> {

	private HasValueAndElement<?, ? extends FilterFieldDto> filter;
	
	private ValueProvider<T, ?> valueProvider;
	
	private EnhancedGrid<T> grid;
	
	private Button filterButton;
	
	private FilterField filterField;
	
	/**
	 * @see Column#Column(Grid, String, Renderer)
	 * 
	 * @param grid
	 * @param columnId
	 * @param renderer
	 */
	public EnhancedColumn(EnhancedGrid<T> grid, String columnId, Renderer<T> renderer) {
		super(grid, columnId, renderer);
		this.grid = grid;
	}
	
	public Column<T> setHeader(String labelText, HasValueAndElement<?, ? extends FilterFieldDto> filter) {	
		if(filter != null) {
			Component headerComponent = new Div();
	        headerComponent.getElement().setText(labelText);
	        addFilterButtonToHeader(headerComponent, filter);			
			return super.setHeader(headerComponent); 
		}		
		return super.setHeader(labelText);
    }
	
	public Column<T> setHeader(Component headerComponent, HasValueAndElement<?, ? extends FilterFieldDto> filter) {	
		if(filter != null) {
			addFilterButtonToHeader(headerComponent, filter);
		}				
		return super.setHeader(headerComponent);		
	}
	
	private void addFilterButtonToHeader(Component headerComponent, HasValueAndElement<?, ? extends FilterFieldDto> filter) {
		this.filter = filter;
		
		// add filter button
		filterButton = new Button(new Icon(VaadinIcon.FILTER));
        filterButton.setId("filter-button");
        filterButton.addClassName("filter-not-selected");
        
        // add filter field popup and set filter as it's filter component
        filterField = new FilterField();
        filterField.setFor(filterButton.getId().get());
        filterField.addApplyFilterListener(grid);
        filterField.addFilterComponent(filter.getElement().getComponent().get());
                      
        // this is needed to avoid issues when adding popup
        headerComponent.getElement().appendChild(filterButton.getElement());
        headerComponent.getElement().executeJs("return").then(ignore -> {
        	headerComponent.getElement().appendChild(filterField.getElement());
        	if(this.isSortable()) {
        		headerComponent.getElement().executeJs("this.parentElement.parentElement.root.querySelector('[part=indicators]').style.marginLeft='-50px'");
        	}
        });
	}

	HasValueAndElement<?, ? extends FilterFieldDto> getFilter() {
		return filter; 
	}	
	
	void updateFilterButtonStyle(){
		if(filter.isEmpty()) {
			filterButton.getClassNames().remove("filter-selected");
			filterButton.getClassNames().add("filter-not-selected");			
		} else {
			filterButton.getClassNames().remove("filter-not-selected");
			filterButton.getClassNames().add("filter-selected");
		}
	}

	ValueProvider<T, ?> getValueProvider(){
		if (this.getRenderer() instanceof ColumnPathRenderer) { 
			valueProvider = ((ColumnPathRenderer<T>)this.getRenderer()).getValueProviders().values().iterator().next();
		} else {
			throw new UnsupportedOperationException("Column doesn't have a ColumnPathRenderer. "
					+ "Value Provider for column is unknown, please set one calling setValueProvider method.");
		}
		return valueProvider;
	}

	public void setValueProvider(ValueProvider<T, ?> valueProvider) {
		this.valueProvider = valueProvider;
	}
	
	/**
	 * Clear selected filter.
	 * 
	 */
	public void clearFilter(){
		filterField.resetFilter();
	}
	
}
