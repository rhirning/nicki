package org.mgnl.nicki.vaadin.base.renderer;

import java.io.Serializable;

import org.mgnl.nicki.vaadin.base.shop.ShopViewerComponent;

import com.vaadin.ui.Component;

public interface ShopRenderer extends Serializable{

	Component render(ShopViewerComponent shopViewerComponent);

}
