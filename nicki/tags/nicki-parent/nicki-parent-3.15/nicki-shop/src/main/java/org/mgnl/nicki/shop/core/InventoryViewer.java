package org.mgnl.nicki.shop.core;

import java.util.Map;

import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.inventory.Inventory;
import org.mgnl.nicki.shop.base.inventory.InventoryArticle;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class InventoryViewer extends CustomComponent {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Table inventoryEntries;
	@AutoGenerated
	private Label recipient;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */
	
	private static final long serialVersionUID = 8441664095916322794L;
	private Inventory inventory;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public InventoryViewer(Inventory inventory) {
		this.inventory = inventory;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		this.recipient.setValue(this.inventory.getPerson().getDisplayName());
		fillInventoryEntries();
	}

	@SuppressWarnings("unchecked")
	private void fillInventoryEntries() {

		inventoryEntries.addContainerProperty("status", String.class, "");
		inventoryEntries.setColumnWidth("status", 100);
		inventoryEntries.setColumnHeader("status", "Status");
		inventoryEntries.addContainerProperty("article", String.class, "");
		inventoryEntries.setColumnWidth("article", 250);
		inventoryEntries.setColumnHeader("article", "Artikel");
		inventoryEntries.addContainerProperty("start", String.class, "");
		inventoryEntries.setColumnWidth("start", 70);
		inventoryEntries.setColumnHeader("start", I18n.getText(CatalogArticle.CAPTION_START));
		inventoryEntries.addContainerProperty("end", String.class, "");
		inventoryEntries.setColumnWidth("end", 70);
		inventoryEntries.setColumnHeader("end", I18n.getText(CatalogArticle.CAPTION_END));
		inventoryEntries.addContainerProperty("attributes", String.class, "");
		inventoryEntries.setColumnWidth("attributes", 420);
		inventoryEntries.setColumnHeader("attributes", "Attribute");
		for (String key : inventory.getMulitArticles().keySet()) {
			Map<String, InventoryArticle> list = inventory.getMulitArticles().get(key);
			for (String specifier : list.keySet()) {
				InventoryArticle iArticle = list.get(specifier);
				Item item = inventoryEntries.addItem(iArticle);
				item.getItemProperty("status").setValue(iArticle.getStatus().toString());
				item.getItemProperty("article").setValue(iArticle.getArticle().getDisplayName()
						+ ": " + iArticle.getSpecifier());
				if (iArticle.getStart() != null) {
					item.getItemProperty("start").setValue(DataHelper.formatDisplayDay.format(iArticle.getStart()));
				}
				if (iArticle.getEnd() != null) {
					item.getItemProperty("end").setValue(DataHelper.formatDisplayDay.format(iArticle.getEnd()));
				}
				item.getItemProperty("attributes").setValue(iArticle.getAttributes().toString());
			}
		}
		for (String key : inventory.getArticles().keySet()) {
			InventoryArticle iArticle = inventory.getArticles().get(key);
			Item item = inventoryEntries.addItem(iArticle);
			item.getItemProperty("status").setValue(iArticle.getStatus().toString());
			item.getItemProperty("article").setValue(iArticle.getArticle().getDisplayName());
			if (iArticle.getStart() != null) {
				item.getItemProperty("start").setValue(DataHelper.formatDisplayDay.format(iArticle.getStart()));
			}
			if (iArticle.getEnd() != null) {
				item.getItemProperty("end").setValue(DataHelper.formatDisplayDay.format(iArticle.getEnd()));
			}
			item.getItemProperty("attributes").setValue(iArticle.getAttributes().toString());
		}
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// recipient
		recipient = new Label();
		recipient.setCaption("Empf�nger");
		recipient.setImmediate(false);
		recipient.setWidth("-1px");
		recipient.setHeight("-1px");
		recipient.setValue("recipient");
		mainLayout.addComponent(recipient, "top:40.0px;left:21.0px;");
		
		// inventoryEntries
		inventoryEntries = new Table();
		inventoryEntries.setImmediate(false);
		inventoryEntries.setWidth("100.0%");
		inventoryEntries.setHeight("100.0%");
		mainLayout.addComponent(inventoryEntries,
				"top:80.0px;right:20.0px;bottom:20.0px;left:20.0px;");
		
		return mainLayout;
	}

}
