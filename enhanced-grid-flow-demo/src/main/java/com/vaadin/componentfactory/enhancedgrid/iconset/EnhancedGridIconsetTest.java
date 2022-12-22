package com.vaadin.componentfactory.enhancedgrid.iconset;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.IconFactory;
import java.util.Locale;

@JsModule("./icons/enhancedgridiconsettest.js")
public enum EnhancedGridIconsetTest implements IconFactory {
    
	FILTERICON;

    public Icon create() {
        return new Icon(this.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("^-", ""));
    }

    public static final class Icon extends com.vaadin.flow.component.icon.Icon {
        Icon(String icon) {
            super("enhancedgridiconsettest", icon);
        }
    }
}