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

import java.util.function.Predicate;

/**
 * Interface for a filter field dto.
 * 
 * @param <T> type of the filter field dto
 */
public interface FilterFieldDto<T> {

	/**
	 * Returns the filter predicate.
	 * 
	 * @return the filter predicate
	 */
	Predicate<T> getFilterPredicate();

	/**
	 * Returns true if the filter field is empty.
	 * 
	 * @return true if the filter field is empty
	 */
	boolean isEmpty();

}
