package com.vaadin.componentfactory.enhancedgrid;


/*
 * #%L
 * enhanced-grid-flow
 * %%
 * Copyright (C) 2020 Vaadin Ltd
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

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.grid.CustomAbstractGridMultiSelectionModel;
import com.vaadin.flow.component.grid.CustomAbstractGridSingleSelectionModel;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridArrayUpdater;
import com.vaadin.flow.component.grid.GridArrayUpdater.UpdateQueueData;
import com.vaadin.flow.component.grid.GridSelectionModel;
import com.vaadin.flow.data.provider.DataGenerator;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.function.SerializableBiFunction;
import com.vaadin.flow.function.SerializablePredicate;

import elemental.json.JsonObject;

import java.util.Objects;

/**
 * Add a selectionFilter to forbid the grid selection for specific rows
 *
 * @param <T>
 */
public class EnhancedGrid<T> extends Grid<T> {

    /**
     * @see Grid#Grid()
     */
    public EnhancedGrid() {
        super();
    }

    /**
     *
     * @see Grid#Grid(int)
     *
     * @param pageSize - the page size. Must be greater than zero.
     */
    public EnhancedGrid(int pageSize) {
        super(pageSize);
    }

    /**
     *
     * @see Grid#Grid(Class, boolean)
     *
     * @param beanType - the bean type to use, not null
     * @param autoCreateColumns – when true, columns are created automatically for the properties of the beanType
     */
    public EnhancedGrid(Class<T> beanType, boolean autoCreateColumns) {
        super(beanType, autoCreateColumns);
    }

    /**
     *
     * @see Grid#Grid(Class)
     *
     * @param beanType - the bean type to use, not null
     */
    public EnhancedGrid(Class<T> beanType) {
        super(beanType);
    }
    
    /**
     * 
     * @see Grid#Grid(Class, SerializableBiFunction, DataCommunicatorBuilder)
     * 
     * @param <U>
     * @param <B>
     * @param beanType
     * @param updateQueueBuilder
     * @param dataCommunicatorBuilder
     */
    protected <U extends GridArrayUpdater, B extends DataCommunicatorBuilder<T, U>> EnhancedGrid(
            Class<T> beanType,
            SerializableBiFunction<UpdateQueueData, Integer, UpdateQueue> updateQueueBuilder,
            B dataCommunicatorBuilder){
    	super(beanType, updateQueueBuilder, dataCommunicatorBuilder);
    }
    
    /**
     * 
     * @see Grid#Grid(int, SerializableBiFunction, DataCommunicatorBuilder)
     * 
     * @param <U>
     * @param <B>
     * @param pageSize
     * @param updateQueueBuilder
     * @param dataCommunicatorBuilder
     */
    protected <U extends GridArrayUpdater, B extends DataCommunicatorBuilder<T, U>> EnhancedGrid(
            int pageSize,
            SerializableBiFunction<UpdateQueueData, Integer, UpdateQueue> updateQueueBuilder,
            B dataCommunicatorBuilder) {
    	super(pageSize, updateQueueBuilder, dataCommunicatorBuilder);
    }
    
    
    private SerializablePredicate<T> selectionFilter = item -> true;
    private DataGenerator<T> generateSelectionGenerator;

    public SerializablePredicate<T> getSelectionFilter() {
        return selectionFilter;
    }

    /**
     * Disable selection/deselection of the item that doesn't match the selectionFilter
     *
     * @param selectionFilter selectionFilter
     */
    public void setSelectionFilter(SerializablePredicate<T> selectionFilter) {
        this.selectionFilter = selectionFilter;
        if (generateSelectionGenerator != null) {
            removeDataGenerator(generateSelectionGenerator);
        }
        generateSelectionGenerator = this::generateSelectionAccess;
        addDataGenerator(generateSelectionGenerator);
    }

    /**
     * Add a selectionDisabled value on the client side
     *
     * @param item item
     * @param jsonObject jsonObject
     */
    private void generateSelectionAccess(T item, JsonObject jsonObject) {
        if (!selectionFilter.test(item)) {
            jsonObject.put("selectionDisabled", true);
        }
    }

    @Override
    public GridSelectionModel<T> setSelectionMode(SelectionMode selectionMode) {
        if (selectionMode == SelectionMode.MULTI) {

            Objects.requireNonNull(selectionMode, "Selection mode cannot be null.");
            GridSelectionModel<T> model = new CustomAbstractGridMultiSelectionModel<T>(this) {

                @Override
                protected void fireSelectionEvent(SelectionEvent<Grid<T>, T> event) {
                    ComponentUtil.fireEvent(getGrid(), (ComponentEvent<Grid<?>>) event);
                }
            };
            setSelectionModel(model, selectionMode);
            return model;
        } else  if (selectionMode == SelectionMode.SINGLE) {
            GridSelectionModel<T> model = new CustomAbstractGridSingleSelectionModel<T>(this) {

                @Override
                protected void fireSelectionEvent(SelectionEvent<Grid<T>, T> event) {
                    ComponentUtil.fireEvent(getGrid(), (ComponentEvent<Grid<?>>) event);
                }

                @Override
                public void setDeselectAllowed(boolean deselectAllowed) {
                    super.setDeselectAllowed(deselectAllowed);
                    getGrid().getElement().executeJs(
                        "this.$connector.deselectAllowed = $0",
                        deselectAllowed);
                }
            };
            setSelectionModel(model, selectionMode);
            return model;
        } else {
            return super.setSelectionMode(selectionMode);
        }
    }
}
