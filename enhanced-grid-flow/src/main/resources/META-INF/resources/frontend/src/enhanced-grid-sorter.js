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
 
 /**
 * `<enhanced-grid-sorter>` is a helper element for the `<vaadin-grid>` that extends `<vaadin-grid-sorter>`
 * adding support to display a button for filtering
 */
import { PolymerElement, html } from '@polymer/polymer/polymer-element.js';
import { GridSorterElement  } from '@vaadin/vaadin-grid/vaadin-grid-sorter.js';

class EnhancedGridSorterElement extends GridSorterElement {
  static get template() {
    return html`
      <style>
      :host(:not([sortable])) [part='indicators']::before {
      	display: none;
      }

      :host(:not([sortable])) {
      	cursor: default;
      }
      
	  [part="filter-button"] {
		font-size: var(--lumo-font-size-s);
		background-color: transparent;
	  }
	
	  [part="filter-button"]:hover::before {
		opacity: 0;
	  }
	
	  :host([filtered]) [part="filter-button"] {
		color: var(--lumo-primary-text-color);
	  }
	
	  :host(:not([filtered])) [part="filter-button"] {
		color: var(--lumo-body-text-color);
		opacity: 0.2;
	  }

      </style>

      ${super.template}

      <slot name="direction"></slot> 
      <vaadin-button theme="icon" part="filter-button" role="button" path="[[path]]" on-click="_onFilterClick">
    	<iron-icon icon="vaadin:filter" slot="prefix" ></iron-icon>
      </vaadin-button>
    `;
  }
  
   static get properties() {
    return {
       filtered: {
        type: Boolean,
        reflectToAttribute: true,
        value: false
      },
      sortable: {
        type: Boolean,
        reflectToAttribute: true,
        value: false
      }
    };
  }

  static get is() {
    return 'enhanced-grid-sorter';
  }

    /** @private */
 _onFilterClick(e) {   
    e.stopPropagation();  
    this.dispatchEvent(new CustomEvent('filter-clicked', { bubbles: true, composed: true, detail: { id: e.target.path } }));
 }

 _onClick(e) {
   if(this.sortable) {
	   super._onClick(e);
     this._updateSorterDirection();
     } 
 }

 /** @private */
 _updateSorterDirection() {
    var sorter = this.querySelector("vaadin-grid-sorter");
    if(sorter){
      sorter.direction = this.direction;
    }
 }
    
 connectedCallback () {
	super.connectedCallback ();
	
	var slot = this.shadowRoot.querySelector("slot[name='direction']");
	var handler = this.__copyDirection.bind(this);
	slot.addEventListener('slotchange', handler);
	handler();
 }

 __copyDirection() {
	var sorter = this.querySelector("vaadin-grid-sorter");
	if(sorter){
		this.direction = sorter.direction;
		sorter.addEventListener('direction-changed', function(e) {
			this.direction = sorter.direction;	
			e.stopPropagation();	
		}.bind(this));
		sorter.addEventListener('sorter-changed', function(e) {
			e.stopPropagation();	
		});
	}
 }  

}

customElements.define(EnhancedGridSorterElement.is, EnhancedGridSorterElement);

export { EnhancedGridSorterElement };
