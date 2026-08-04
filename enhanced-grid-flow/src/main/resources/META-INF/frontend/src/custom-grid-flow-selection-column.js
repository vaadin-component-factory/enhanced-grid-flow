/*-
 * #%L
 * Selection Grid
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

import '@vaadin/grid/vaadin-grid-column.js';
import { GridColumn } from '@vaadin/grid/src/vaadin-grid-column.js';
import { defineCustomElement } from '@vaadin/component-base/src/define.js';

{
  class CustomGridFlowSelectionColumn extends GridColumn {
    
    static get is() {
      return 'custom-grid-flow-selection-column';
    }

    static get properties() {
      return {

        /**
         * Automatically sets the width of the column based on the column contents when this is set to `true`.
         */
        autoWidth: {
          type: Boolean,
        },

        /**
         * Width of the cells for this column.
         */
        width: {
          type: String,
        },

        /**
         * Flex grow ratio for the cell widths. When set to 0, cell width is fixed.
         */
        flexGrow: {
          type: Number,
        },

        /**
         * When true, all the items are selected.
         */
        selectAll: {
          type: Boolean,
          notify: true
        },

        selectAllHidden: {
          type: Boolean,
        }
      };
    }

    constructor() {
      super();
      this.autoWidth = true;
      this.width = '56px';
      this.flexGrow = 0;
      this.selectAll = false;
      this.selectAllHidden = false;
      
      this._boundOnSelectEvent = this._onSelectEvent.bind(this);
      this._boundOnDeselectEvent = this._onDeselectEvent.bind(this);
    }
  
    /** @protected */
    updated(changedProperties) {
      super.updated(changedProperties);
      
      // Handle property changes that affect header rendering
      if (changedProperties.has('_headerRenderer') || 
          changedProperties.has('path') || 
          changedProperties.has('header') || 
          changedProperties.has('selectAll') || 
          changedProperties.has('selectAllHidden')) {
        this._onHeaderRendererOrBindingChanged();
      }
    }

    /** @private */
    connectedCallback() {
      super.connectedCallback();
      if (this._grid) {
        this._grid.addEventListener('select', this._boundOnSelectEvent);
        this._grid.addEventListener('deselect', this._boundOnDeselectEvent);
      }
    }

    /** @private */
    disconnectedCallback() {
      super.disconnectedCallback();
      if (this._grid) {
        this._grid.removeEventListener('select', this._boundOnSelectEvent);
        this._grid.removeEventListener('deselect', this._boundOnDeselectEvent);
      }
    }

    /**
     * Renders the Select All checkbox to the header cell.
    *
    * @override
    */
    _defaultHeaderRenderer(root, _column) {
      let checkbox = root.firstElementChild;
      if (!checkbox) {
        checkbox = document.createElement('vaadin-checkbox');
        checkbox.id = 'selectAllCheckbox';
        checkbox.setAttribute('aria-label', 'Select All');
        checkbox.classList.add('vaadin-grid-select-all-checkbox');
        checkbox.addEventListener('click', this._onSelectAllClick.bind(this));
        root.appendChild(checkbox);
      }

      const checked = this.selectAll;
      checkbox.hidden = this.selectAllHidden;
      checkbox.checked = checked;
    }

    /**
     * Renders the Select Row checkbox to the body cell.
     *
     * @override
     */
    _defaultRenderer(root, _column, { item, selected }) {
      let checkbox = root.firstElementChild;
      if (!checkbox) {
        checkbox = document.createElement('vaadin-checkbox');
        checkbox.setAttribute('aria-label', 'Select Row');
        checkbox.addEventListener('click', this._onSelectClick.bind(this));
        root.appendChild(checkbox);
      }

      checkbox.__item = item;
      checkbox.checked = selected;
      checkbox.disabled = item.selectionDisabled;
    }

    _onSelectClick(e) {
        if (!e.currentTarget.__item.selectionDisabled) {
            e.currentTarget.checked ? this._grid.$connector.doDeselection([e.currentTarget.__item], true) : this._grid.$connector.doSelection([e.currentTarget.__item], true);
        }
    }

    _onSelectAllClick(e) {
      e.preventDefault();
      if (this._grid.hasAttribute('disabled')) {
        e.currentTarget.checked = !e.currentTarget.checked;
        return;
      }
      this.selectAll ? this.$server.deselectAll() : this.$server.selectAll();
    }

    _onSelectEvent(e) {
    }

    _onDeselectEvent(e) {
      if (e.detail.userOriginated) {
        this.selectAll = false;
      }
    }
  }

  defineCustomElement(CustomGridFlowSelectionColumn);

  Vaadin.CustomGridFlowSelectionColumn = CustomGridFlowSelectionColumn;
  
}
