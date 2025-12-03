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
 
/**
 * `<enhanced-grid-sorter>` is a helper element for the `<vaadin-grid>` that extends `<vaadin-grid-sorter>`
 * adding support to display a button for filtering
 */
import { html, css } from 'lit';
import { defineCustomElement } from '@vaadin/component-base/src/define.js';
import { GridSorter } from '@vaadin/grid/src/vaadin-grid-sorter.js';

class EnhancedGridSorter extends GridSorter {
  static get is() {
    return 'enhanced-grid-sorter';
  }

  static get styles() {
    return [
      super.styles,
      css`
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
      `
    ];
  }

  static get properties() {
    return {
      path: {
        type: String,
        reflect: true,
      },
      filtered: {
        type: Boolean,
        reflect: true,
      },
      sortable: {
        type: Boolean,
        reflect: true,
      },
      filtericon: {
        type: String,
        reflect: true,
      }
    };
  }

  constructor() {
    super();
    this.path = null;
    this.filtered = false;
    this.sortable = false;
    this.filtericon = "vaadin:filter";
  }

  /** @protected */
  render() {
    return html`
      <div part="content">
        <slot></slot>
      </div>
      <div part="indicators">
        <span part="order">${this._getDisplayOrder(this._order)}</span>
      </div>
      <slot name="direction"></slot>
      <vaadin-button 
        theme="icon" 
        part="filter-button" 
        role="button" 
        .path="${this.path}"
        @click="${this._onFilterClick}">
        <vaadin-icon icon="${this.filtericon}" slot="prefix"></vaadin-icon>
      </vaadin-button>
    `;
  }

  /** @private */
  _onFilterClick(e) {
    e.stopPropagation();

    const theButtonPath = e.target.tagName.toLowerCase() === 'vaadin-icon' ? e.target.parentElement.path : e.target.path;
    this.dispatchEvent(new CustomEvent('filter-clicked', { bubbles: true, composed: true, detail: { id: theButtonPath } }));
  }

  /** @protected */
  _onClick(e) {
    if(this.sortable) {
      super._onClick(e);
      this._updateSorterDirection();
    } 
  }

  /** @private */
  _updateSorterDirection() {
    const sorter = this.querySelector("vaadin-grid-sorter");
    if(sorter){
      sorter.direction = this.direction;
    }
  }
    
  /** @protected */
  connectedCallback() {
    super.connectedCallback();
    
    // Wait for the next frame to ensure shadow DOM is ready
    requestAnimationFrame(() => {
      const slot = this.shadowRoot.querySelector("slot[name='direction']");
      if (slot) {
        const handler = this.__copyDirection.bind(this);
        slot.addEventListener('slotchange', handler);
        handler();
      }
    });
  }

  /** @private */
  __copyDirection() {
    const sorter = this.querySelector("vaadin-grid-sorter");
    if(sorter){
      this.direction = sorter.direction;
      sorter.addEventListener('direction-changed', (e) => {
        this.direction = sorter.direction;	
        e.stopPropagation();	
      });
      sorter.addEventListener('sorter-changed', (e) => {
        e.stopPropagation();	
      });
    }
  }  

}

defineCustomElement(EnhancedGridSorter);

function _patch($connector) {
  const tryCatchWrapper = function (callback) {
    return window.Vaadin.Flow.tryCatchWrapper(callback, 'Enhanced Grid');
  };
  const singleTimeRenderer = (renderer) => {
    return (root) => {
      if (renderer) {
        renderer(root);
        renderer = null;
      }
    };
  };

  const originalMethod = $connector.setHeaderRenderer;

  $connector.setHeaderRenderer = tryCatchWrapper(function (column, options) {
    const columnData = $connector._USE_CUSTOM_VERSION_FOR_COLUMNS[column._flowId];

    if (columnData === undefined) {
      originalMethod.call($connector, column, options);
      return;
    }

    const { content, showSorter, sorterPath } = options;
    const showSortControls = showSorter;

    if (content === null) {
      column.headerRenderer = null;
      return;
    }

    column.headerRenderer = singleTimeRenderer((root) => {
      // Clear previous contents
      root.innerHTML = '';
      // Render sorter
      let contentRoot = root;

      const sorter = document.createElement('enhanced-grid-sorter');

      sorter.sortable = showSortControls;
      if (showSortControls) {
        sorter.path = sorterPath;
        const ariaLabel = content instanceof Node ? content.textContent : content;
        if (ariaLabel) {
            sorter.setAttribute('aria-label', `Sort by ${ariaLabel}`);
        }
        // all the server side updates will be done by querying for vaadin-grid-sorter
        // components, so we will add a vaadin-grid-sorter here and copy all changes
        // done to this element to the parent enhanced-grid-sorter
        const hack = document.createElement('vaadin-grid-sorter')
        hack.setAttribute('path', sorterPath);
        hack.slot = 'direction';
        hack.style.display = 'none';
        sorter.append(hack);
      }
      else {
        sorter.path = columnData.path;
      }

      root.appendChild(sorter);

      // Use sorter as content root
      contentRoot = sorter;

      // Add content
      if (content instanceof Node) {
        contentRoot.appendChild(content);
      } else {
        contentRoot.textContent = content;
      }
    });
  });
}

function monkeyPatchHeaderRenderer($connector, columnId) {
  if ($connector._USE_CUSTOM_VERSION_FOR_COLUMNS === undefined) {
    $connector._USE_CUSTOM_VERSION_FOR_COLUMNS = {};
    _patch($connector);
  }
  $connector._USE_CUSTOM_VERSION_FOR_COLUMNS[columnId] = {
    path: columnId,
  };
}

window.monkeyPatchHeaderRenderer = monkeyPatchHeaderRenderer;

export { EnhancedGridSorter };
