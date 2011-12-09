/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.shop.renderer;

import java.io.Serializable;

import org.mgnl.nicki.vaadin.base.shop.core.ShopViewerComponent;
import org.mgnl.nicki.vaadin.base.shop.inventory.Inventory;

import com.vaadin.ui.Component;

public interface ShopRenderer extends Serializable{

	Component render(ShopViewerComponent shopViewerComponent, Inventory inventory);

}
