package com.vaadin.componentfactory.enhancedgrid.i18n;

import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;

@WebServlet(urlPatterns = "/*",
        initParams = {@WebInitParam(name = InitParameters.I18N_PROVIDER, value = "com.vaadin.componentfactory.enhancedgrid.i18n.TranslationProvider")})
public class ApplicationServlet extends VaadinServlet {
}
