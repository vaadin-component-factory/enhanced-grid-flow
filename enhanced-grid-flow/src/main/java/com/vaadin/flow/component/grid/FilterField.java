package com.vaadin.flow.component.grid;

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

import com.vaadin.componentfactory.Popup;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class FilterField extends Popup {
	
	private Button applyButton;
	
	private Button resetButton;
	
	private VerticalLayout rootLayout;
	
	private Div filterComponentDiv;

	private Component filterComponent;
	
	public FilterField() {
		rootLayout = new VerticalLayout();
		rootLayout.setSpacing(false);
		filterComponentDiv = new Div();
		rootLayout.add(filterComponentDiv, createButtonsLayout());
		add(rootLayout);
	}
		
	private HorizontalLayout createButtonsLayout() {
		applyButton = new Button("Apply", e -> onApply());
		applyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		resetButton = new Button("Reset", e -> onReset());    	
		resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
    	HorizontalLayout buttonsLayout = new HorizontalLayout();
    	buttonsLayout.setWidthFull();
    	buttonsLayout.setJustifyContentMode(JustifyContentMode.END); 
    	buttonsLayout.add(applyButton, resetButton);
    	return buttonsLayout;
	}

	public void onApply() {
		applyFilterListener.onApplyFilter(((HasValue<?,?>)filterComponent).getValue());		
		this.hide();
	}

	public void onReset() {
		((HasValue<?,?>)filterComponent).clear();
	}

	public void addFilterComponent(Component filterComponent) {
		this.filterComponent = filterComponent;
		filterComponentDiv.add(filterComponent);
		if(!isEmptyFilter()) {
			onApply();
		}
	}	
	
	public Component getFilterComponent() {
		return filterComponent;
	}
	
	public boolean isEmptyFilter() {
		return ((HasValue<?,?>)filterComponent).isEmpty();
	}
		
	private ApplyFilterListener applyFilterListener;
	
	public void addApplyFilterListener(ApplyFilterListener applyFilterListener) {
        this.applyFilterListener = applyFilterListener;
    }
		
}
