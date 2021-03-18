package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        
        final RouterLink simpleMultiGrid = new RouterLink("Multiple Selection Grid", SimpleMultiSelectView.class);
        final RouterLink simpleSingleGrid = new RouterLink("Single Selection Grid", SimpleSingleSelectView.class);           
        final RouterLink multiTree = new RouterLink("Multiple Selection Tree Grid", MultiTreeGridView.class);
        final RouterLink singleTree = new RouterLink("Single Selection Tree Grid", SingleTreeGridView.class);
        
        final VerticalLayout menuLayout = new VerticalLayout(simpleMultiGrid, simpleSingleGrid, multiTree, singleTree);
        addToDrawer(menuLayout);
        addToNavbar(drawerToggle);
    }

}