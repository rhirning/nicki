package org.mgnl.nicki.shop.renderer;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class MulitCheckBoxChangeListener implements Property.ValueChangeListener {
	private static final long serialVersionUID = 5397184007544830202L;
	private Inventory inventory;
	private TableRenderer tableRenderer;
	public MulitCheckBoxChangeListener(Inventory inventory,
			InventoryArticle inventoryArticle, TableRenderer tableRenderer) {
		super();
		this.inventory = inventory;
		this.inventoryArticle = inventoryArticle;
		this.tableRenderer = tableRenderer;
	}


	private InventoryArticle inventoryArticle;


	@Override
	public void valueChange(ValueChangeEvent event) {
		String checkedString = String.valueOf(event.getProperty().getValue());
		boolean checked = DataHelper.booleanOf(checkedString);
		if (!checked) {
			inventory.removeArticle(inventoryArticle);
		}
		tableRenderer.render();
	}

}
