package com.vaadin.componentfactory.enhancedgrid;

import java.util.Objects;

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
import com.vaadin.flow.component.grid.CancelEditConfirmDialog;
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
import com.vaadin.flow.function.SerializableRunnable;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;

import elemental.json.JsonObject;

/**
 * Add a selectionPredicate to forbid the grid selection for specific rows
 *
 * @param <T>
 */
public class EnhancedGrid<T> extends Grid<T> implements BeforeLeaveObserver {

	private static final String CANCEL_EDIT_MSG_KEY = "cancel-edit-dialog.text";
	    
    private static final String CANCEL_EDIT_CONFIRM_BTN_KEY = "cancel-edit-dialog.confirm-btn";
    
    private static final String CANCEL_EDIT_CANCEL_BTN_KEY = "cancel-edit-dialog.cancel-btn";
    
    private SerializablePredicate<T> selectionPredicate = item -> true;
    
    private DataGenerator<T> generateSelectionGenerator;
    
    private SerializablePredicate<T> editablePredicate = item -> true;
        
    private boolean showCancelEditDialog = true;	    
	
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
    
    /**
     * Define if an item can be selected.
     * 
     * @return
     */
    public SerializablePredicate<T> getSelectionPredicate() {
        return selectionPredicate;
    }

    /**
     * Disable selection/deselection of the item that doesn't match the selectionPredicate
     *
     * @param selectionPredicate selectionPredicate
     */
    public void setSelectionPredicate(SerializablePredicate<T> selectionPredicate) {
        this.selectionPredicate = selectionPredicate;
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
        if (!selectionPredicate.test(item)) {
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
    
   
    /**
     * Define if an item can be edited.
     * 
     * @param editablePredicate
     */
    public void setEditablePredicate(SerializablePredicate<T> editablePredicate) {
        this.editablePredicate = editablePredicate;
    }
    
    /**
     * Return whether an item is editable or not.
     * 
     * @param item
     * @return
     */
    public boolean isEditable(T item) {
    	return this.editablePredicate.test(item);
    }
        
    /**
     * Edit the selected item.
     * 
     * @param item
     */
    public void editItem(T item) {
        if(!isEditable(item)) {
    		return;
    	}
        
        T onEditItem = this.getEditor().getItem();    	    	
    	if(onEditItem != null && item.equals(onEditItem)) {
    		return;
    	}
    	
		if(onEditItem != null && !item.equals(onEditItem) && allowCancelEditDialogDisplay()) {
			cancelEditItem(item, null);
		} else {
			this.getEditor().editItem(item);
		}        
    }
    
    /**
     * Cancel the current item edition.
     * 
     */
    public void cancelEdit() {
    	cancelEditItem(null, null);
    }
    
    private void cancelEditItem(T newEditItem, ContinueNavigationAction action) {
    	String text = getTranslation(CANCEL_EDIT_MSG_KEY);
    	String confirmText = getTranslation(CANCEL_EDIT_CONFIRM_BTN_KEY); 
    	String cancelText = getTranslation(CANCEL_EDIT_CANCEL_BTN_KEY);
       	SerializableRunnable onConfirmCallback = action != null ? () -> this.onConfirmEditItem(newEditItem, action) : () -> this.onConfirmEditItem(newEditItem);
       	new CancelEditConfirmDialog<T>(text, confirmText, cancelText, onConfirmCallback).open();
     }
   
    private void onConfirmEditItem(T newEditItem) {
    	this.getEditor().cancel();
		if(newEditItem != null) {
			this.getEditor().editItem(newEditItem);
		}
    }
    
    private void onConfirmEditItem(T newEditItem, ContinueNavigationAction action) {
    	this.onConfirmEditItem(null);
    	action.proceed();
    }
   
	/**
	 * Set showCancelEditDialog value to know if {@link CancelEditConfirmDialog} should be displayed.
	 * 
	 * @param showCancelEditDialog
	 */
	public void setShowCancelEditDialog(boolean showCancelEditDialog) {
		this.showCancelEditDialog = showCancelEditDialog;
	}
    
	/**
	 * {@link CancelEditConfirmDialog} will be displayed if showCancelEditDialog is true
	 * && editor is in buffered mode.
	 * 
	 * @return
	 */
	private boolean allowCancelEditDialogDisplay() {
		return showCancelEditDialog && this.getEditor().isBuffered();
	}	
    
	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		T onEditItem = this.getEditor().getItem();
		if(onEditItem != null && allowCancelEditDialogDisplay()) {
			ContinueNavigationAction action = event.postpone();
			cancelEditItem(null, action);
		}		
	}
}
