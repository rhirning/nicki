package org.mgnl.nicki.vaadin.base.shop;

import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;

import com.vaadin.ui.Component;

public interface AttributeComponent {

	Component getInstance(CatalogArticleAttribute pageAttribute);

}
