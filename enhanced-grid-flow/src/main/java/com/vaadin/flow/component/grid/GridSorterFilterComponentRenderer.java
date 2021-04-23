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

import java.util.Optional;

import com.vaadin.componentfactory.enhancedgrid.EnhancedColumn;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.provider.ComponentDataGenerator;
import com.vaadin.flow.data.provider.DataGenerator;
import com.vaadin.flow.data.provider.DataKeyMapper;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Rendering;
import com.vaadin.flow.dom.Element;

/**
 * Internal component renderer for sortable headers inside Grid.
 * 
 */
public class GridSorterFilterComponentRenderer <SOURCE>
	extends ComponentRenderer<Component, SOURCE>{

	private final EnhancedColumn<?> column;
    private final Component component;

    /**
     * Creates a new renderer for a specific column, using the defined
     * component.
     * 
     * @param column
     *            The column which header should be rendered
     * @param component
     *            The component to be used by the renderer
     */
    public GridSorterFilterComponentRenderer(EnhancedColumn<?> column,
            Component component) {
        this.column = column;
        this.component = component;
    }

    @Override
    public Rendering<SOURCE> render(Element container,
            DataKeyMapper<SOURCE> keyMapper, Element contentTemplate) {

        GridSorterFilterComponentRendering rendering = new GridSorterFilterComponentRendering(
                contentTemplate);

        container.getNode()
                .runWhenAttached(ui -> ui.getInternals().getStateTree()
                        .beforeClientResponse(container.getNode(),
                                context -> setupTemplateWhenAttached(
                                        context.getUI(), container, rendering,
                                        keyMapper)));
        return rendering;
    }

    private void setupTemplateWhenAttached(UI ui, Element owner,
            GridSorterFilterComponentRendering rendering,
            DataKeyMapper<SOURCE> keyMapper) {
        String appId = ui.getInternals().getAppId();
        Element templateElement = rendering.getTemplateElement();
        owner.appendChild(templateElement);

        Element container = new Element("div");
        owner.appendVirtualChild(container);
        rendering.setContainer(container);
        String templateInnerHtml;

        if (component != null) {
            container.appendChild(component.getElement());

            templateInnerHtml = String.format(
                    "<flow-component-renderer appid=\"%s\" nodeid=\"%s\"></flow-component-renderer>",
                    appId, component.getElement().getNode().getId());
        } else {
            templateInnerHtml = "";
        }
        
        /*
         * The renderer must set the base header template back to the column, so 
         * if/when the sortable state is changed by the developer, the column
         * knows how to add or remove the grid sorter.
         */
        column.setBaseHeaderTemplate(templateInnerHtml);
        if (column.hasSortingIndicators() && !column.isFilterable()) {
            templateInnerHtml = column.addGridSorter(templateInnerHtml);
        }

        if(column.isFilterable()) {
        	String gridSorter = column.isSortable() ? column.addGridSorter("")
        			.replaceFirst(">", "style='display:none' slot='direction'>") : "";
        	templateInnerHtml = column.addEnhancedGridSorter(templateInnerHtml);
        	templateInnerHtml = templateInnerHtml.replaceFirst(">", ">" + gridSorter);
        }
        
        templateElement.setProperty("innerHTML", templateInnerHtml);
    }

    private class GridSorterFilterComponentRendering extends
            ComponentDataGenerator<SOURCE> implements Rendering<SOURCE> {

        private Element templateElement;

        public GridSorterFilterComponentRendering(Element templateElement) {
            super(GridSorterFilterComponentRenderer.this, null);
            this.templateElement = templateElement;
        }

        @Override
        public Element getTemplateElement() {
            return templateElement;
        }

        @Override
        public Optional<DataGenerator<SOURCE>> getDataGenerator() {
            return Optional.of(this);
        }
    }

}
