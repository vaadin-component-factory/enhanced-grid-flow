package com.vaadin.componentfactory.enhancedgrid.service;

import com.vaadin.componentfactory.enhancedgrid.bean.DummyFile;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.stream.Stream;

public class TreeGridUtil {

    public static TreeGrid<DummyFile> getDummyFileTreeGrid(TreeGrid<DummyFile> grid) {
        HierarchicalDataProvider<DummyFile, Void> dataProvider =
                new AbstractBackEndHierarchicalDataProvider<DummyFile, Void>() {

                    @Override
                    public int getChildCount(HierarchicalQuery<DummyFile, Void> query) {
                        return DummyFileService.getChildCount(query.getParent());
                    }

                    @Override
                    public boolean hasChildren(DummyFile item) {
                        return DummyFileService.hasChildren(item);
                    }

                    @Override
                    protected Stream<DummyFile> fetchChildrenFromBackEnd(
                            HierarchicalQuery<DummyFile, Void> query) {
                        return DummyFileService.fetchChildren(query.getParent(), query.getOffset()).stream();
                    }
                };

        grid.setDataProvider(dataProvider);
        grid.setSizeFull();
        return grid;
    }
}
