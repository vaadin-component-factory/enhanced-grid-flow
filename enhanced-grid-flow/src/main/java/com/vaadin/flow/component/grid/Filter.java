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

import com.vaadin.componentfactory.enhancedgrid.EnhancedGrid;
import com.vaadin.flow.function.SerializablePredicate;

/**
 * Filter class to set a filter predicate to a {@link EnhancedGrid grid} in lazy
 * loading mode.
 * 
 * @param <T>
 *            type of the underlying grid this filter is compatible with
 */
public class Filter<T> {

	private SerializablePredicate<T> filterPredicate;

	/**
	 * Default constructor.
	 */
	public Filter() {
	}

	/**
	 * Constructor with filter predicate.
	 * 
	 * @param filterPredicate the filter predicate to set
	 */
	public Filter(SerializablePredicate<T> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}

	/**
	 * Get the filter predicate.
	 * 
	 * @return the filter predicate
	 */
	public SerializablePredicate<T> getFilterPredicate() {
		return filterPredicate;
	}

	/**
	 * Set the filter predicate.
	 * 
	 * @param filterPredicate the filter predicate to set
	 */
	public void setFilterPredicate(SerializablePredicate<T> filterPredicate) {
		this.filterPredicate = filterPredicate;
	}

}
