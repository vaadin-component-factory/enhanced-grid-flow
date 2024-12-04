package com.vaadin.componentfactory.enhancedgrid;

/*-
 * #%L
 * Enhanced Grid
 * %%
 * Copyright (C) 2020 - 2024 Vaadin Ltd
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

import java.util.Comparator;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.FilterField;
import com.vaadin.flow.component.grid.FilterFieldDto;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.SortOrderProvider;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

/**
 * 
 * {@link Grid.Column Column} that extends setHeader methods to add a filter button 
 * and a {@link FilterField filter component} to perform column's filtering.
 *  
 */
@Uses(Icon.class)
@JsModule(value = "./src/enhanced-grid-sorter.js")
public class EnhancedColumn<T> extends Grid.Column<T> implements BeforeEnterObserver {

	private HasValueAndElement<?, ? extends FilterFieldDto> filter;
	
	private ValueProvider<T, ?> valueProvider;
	
	private EnhancedGrid<T> grid;
	
	private FilterField filterField;
	
	private Component headerComponent;
	
	private String filterIcon;
			
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
	
	public EnhancedColumn<T> setHeader(String labelText, HasValueAndElement<?, ? extends FilterFieldDto> filter) {	
		if(filter != null) {
			Component headerComponent = new Span();
	        headerComponent.getElement().setText(labelText);
	        addFilterButtonToHeader(headerComponent, filter);
	        return setHeader(headerComponent);
		}		
		return setHeader(labelText);
    }
	
	public EnhancedColumn<T> setHeader(Component headerComponent, HasValueAndElement<?, ? extends FilterFieldDto> filter) {	
		if(filter != null) {
			addFilterButtonToHeader(headerComponent, filter);
		}				
		return setHeader(headerComponent);		
	}

	/** @see Column#setHeader(Component) */
	@Override
	public EnhancedColumn<T> setHeader(Component headerComponent) {
		if (this.isFilterable()) {
			this.grid.getElement().executeJs("monkeyPatchHeaderRenderer(this.$connector, $0)", getInternalId());
		}
		return (EnhancedColumn<T>) super.setHeader(headerComponent);
	}

	/**
	 * @see Column#setHeader(String)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setHeader(String labelText) {
		return (EnhancedColumn<T>) super.setHeader(labelText);
	}
	
	protected void addFilterButtonToHeader(Component headerComponent, HasValueAndElement<?, ? extends FilterFieldDto> filter) {
		this.filter = filter;
		this.headerComponent = headerComponent;
                
        // add filter field (popup component) and set filter as it's filter component
        filterField = new FilterField();
        filterField.addApplyFilterListener(grid);
		filter.getElement().getComponent()
		      .ifPresent(filterComponent -> filterField.addFilterComponent(filterComponent));

        filterField.addOpenedChangeListener(e -> {
           	if(grid.getEditor().getItem() != null) {
           		if(grid.allowCancelEditDialogDisplay()) {
           			grid.cancelEditWithCancelCallback(() -> filterField.close());
           		} else {
           			grid.getEditor().cancel();
           		}
           	}  	
        });
        
		// add filter field to header
       	headerComponent.getElement().appendChild(filterField.getElement());
		filterField.setTarget(headerComponent);

        grid.addFilterClickedEventListener(e -> {
           	if(e.buttonId.equals(getInternalId())) {
        		if(filterField.isOpened()) {
        			filterField.close();
        		} else {
        			filterField.open();
        		}
        	}
		});
	}
	
	public HasValueAndElement<?, ? extends FilterFieldDto> getFilter() {
		return filter; 
	}	
	
	protected void updateFilterButtonStyle(){
		if(headerComponent != null) {
			headerComponent.getElement().executeJs("return").then(ignore -> {
				if(hasFilterSelected()) {
					headerComponent.getElement().executeJs("this.parentElement._setProperty('filtered', true);");
				} else {
					headerComponent.getElement().executeJs("this.parentElement._setProperty('filtered', false);");
				}
	        });
		}		
	}

	private void updateFilterIcon() {
		if (headerComponent != null) {
			headerComponent.getElement().executeJs("return").then(ignore -> {
				headerComponent.getElement().executeJs("this.parentElement._setProperty('filtericon', $0);",
						filterIcon);
			});
		}
	}
	
	public ValueProvider<T, ?> getValueProvider(){
		if (this.valueProvider != null) {
			 return this.valueProvider;
		} else {
			throw new UnsupportedOperationException("Value provider for column is unknown. "
					+ "Please set one calling setValueProvider method.");
		}
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

	/**
	 * @see Column#setSortProperty(String...)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setSortProperty(String... properties) {
		return (EnhancedColumn<T>) super.setSortProperty(properties);
	}
	
	/**
	 * @see Column#setSortOrderProvider(SortOrderProvider)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setSortOrderProvider(SortOrderProvider provider) {
		return (EnhancedColumn<T>) super.setSortOrderProvider(provider);
	}
	
	/**
	 * @see Column#setSortable(boolean)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setSortable(boolean sortable) {
		return (EnhancedColumn<T>) super.setSortable(sortable);
	}

  /**
	 * @see Column#setResizable(boolean)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setResizable(boolean resizable) {
		return  (EnhancedColumn<T>)super.setResizable(resizable);
	}
	
	/**
	 * @see Column#setComparator(ValueProvider)
	 * 
	 */
	@Override
	public <V extends Comparable<? super V>> EnhancedColumn<T> setComparator(ValueProvider<T, V> keyExtractor) {
		return (EnhancedColumn<T>)super.setComparator(keyExtractor);
	}
	
	/**
	 * @see Column#setComparator(Comparator)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setComparator(Comparator<T> comparator) {
		return (EnhancedColumn<T>)super.setComparator(comparator);
	}
		
	@Override
	protected void setHeaderComponent(Component component) {		
	    super.setHeaderComponent(component);
	}
	
	/**
	 * Return if column shows filter field
	 * 
	 * @return
	 */
	public boolean isFilterable() {
		return filterField != null;
	}
	
	/**
	 * Returns if column is filtered
	 * 
	 * @return
	 */
	public boolean hasFilterSelected() {
		return filter != null && !filter.isEmpty();
	}
	
	protected void setFilterIcon(Icon icon) {
		filterIcon = icon.getElement().getAttribute("icon");
		updateFilterIcon();
	}
	
	/**
	 * @see Column#setKey(String)
	 * 
	 */
	@Override
	public EnhancedColumn<T> setKey(String key) {
		return (EnhancedColumn<T>) super.setKey(key);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if (beforeEnterEvent.isRefreshEvent()) {
			Optional.ofNullable(this.getHeaderComponent())
					.ifPresent(headerComponent -> {
						this.setHeader(headerComponent);
						this.updateFilterButtonStyle();
					});
		}
	}
}
