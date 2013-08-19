package org.mgnl.nicki.shop.renderer;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;

public class CheckBoxChangeListener implements Property.ValueChangeListener {
	private static final long serialVersionUID = 5397184007544830202L;
	private Inventory inventory;
	private CatalogArticle catalogArticle;
	private TableRenderer tableRenderer;
	public CheckBoxChangeListener(Inventory inventory,
			CatalogArticle catalogArticle, TableRenderer tableRenderer) {
		super();
		this.inventory = inventory;
		this.catalogArticle = catalogArticle;
		this.tableRenderer = tableRenderer;
	}




	@Override
	public void valueChange(ValueChangeEvent event) {
		Item item = tableRenderer.getTable().getItem(catalogArticle);
		String checkedString = String.valueOf(event.getProperty().getValue());
		boolean checked = DataHelper.booleanOf(checkedString);
		  if (checked) {
			  InventoryArticle inventoryArticle = inventory.addArticle(catalogArticle);
			  tableRenderer.showEntry(item, catalogArticle, inventoryArticle);
		  } else {
			  inventory.removeArticle(catalogArticle);
			  tableRenderer.hideEntry(item);
		  }

	}
	
}
