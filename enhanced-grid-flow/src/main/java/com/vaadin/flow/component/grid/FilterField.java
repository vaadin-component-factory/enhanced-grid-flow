package com.vaadin.flow.component.grid;

/*-
 * #%L
 * Enhanced Grid
 * %%
 * Copyright (C) 2020 - 2025 Vaadin Ltd
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

import java.util.Optional;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;

/**
 * Filter field component that can be used to filter grid columns.
 */
public class FilterField extends Popover {

	private static final String APPLY_BTN_KEY = "filter-field.apply.btn";

	private static final String RESET_BTN_KEY = "filter-field.reset.btn";

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
		getElement().getThemeList().add("enhanced-grid-filter-field");

		setOpenOnClick(false);
		setOpenOnFocus(true);
		setOpenOnHover(false);

		setCloseOnOutsideClick(true);
		setCloseOnEsc(true);
		setAutofocus(true);
	}

	private HorizontalLayout createButtonsLayout() {
		applyButton = new Button(getTranslation(APPLY_BTN_KEY), e -> applyFilter());
		applyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		resetButton = new Button(getTranslation(RESET_BTN_KEY), e -> resetFilter());
		resetButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidthFull();
		buttonsLayout.setJustifyContentMode(JustifyContentMode.END);
		buttonsLayout.add(applyButton, resetButton);
		return buttonsLayout;
	}

	/**
	 * Apply the filter.
	 */
	public void applyFilter() {
		applyFilterListener.onApplyFilter(((HasValue<?, ?>) filterComponent).getValue());
		this.close();
	}

	/**
	 * Reset the filter.
	 */
	public void resetFilter() {
		((HasValue<?, ?>) filterComponent).clear();
	}

	/**
	 * Add a filter component to the filter field.
	 * 
	 * @param filterComponent the filter component to add
	 */
	public void addFilterComponent(Component filterComponent) {
		this.filterComponent = filterComponent;
		filterComponentDiv.add(filterComponent);
		if (!isEmptyFilter()) {
			applyFilter();
		}
	}

	/**
	 * Get the filter component.
	 * 
	 * @return the filter component
	 */
	public Component getFilterComponent() {
		return filterComponent;
	}

	/**
	 * Check if the filter is empty.
	 * 
	 * @return true if the filter is empty, false otherwise
	 */
	public boolean isEmptyFilter() {
		return ((HasValue<?, ?>) filterComponent).isEmpty();
	}

	private ApplyFilterListener applyFilterListener;

	/**
	 * Add an apply filter listener.
	 * 
	 * @param applyFilterListener the apply filter listener to add
	 */
	public void addApplyFilterListener(ApplyFilterListener applyFilterListener) {
		this.applyFilterListener = applyFilterListener;
	}

	/**
	 * Returns the FilterField that contains the component, if any.
	 * 
	 * @param component the component to find the FilterField for
	 * @return the FilterField that contains the component, if any
	 */
	public static Optional<FilterField> findComponent(Component component) {
		while (component != null && !(component instanceof FilterField)) {
			component = component.getParent().orElse(null);
		}
		return Optional.ofNullable((FilterField) component);
	}
}
