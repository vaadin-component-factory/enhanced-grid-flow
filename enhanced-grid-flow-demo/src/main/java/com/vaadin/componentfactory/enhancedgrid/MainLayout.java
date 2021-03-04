package com.vaadin.componentfactory.enhancedgrid;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        final DrawerToggle drawerToggle = new DrawerToggle();
        final RouterLink simple = new RouterLink("Multi Selection Grid", SimpleView.class);
        final RouterLink singleSimple = new RouterLink("SingleSelection Grid", SimpleSingleSelectView.class);
        final VerticalLayout menuLayout = new VerticalLayout(simple, singleSimple);
        addToDrawer(menuLayout);
        addToNavbar(drawerToggle);
    }

}