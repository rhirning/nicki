package org.mgnl.nicki.vaadin.base.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticle;
import org.mgnl.nicki.dynamic.objects.objects.CatalogArticleAttribute;
import org.mgnl.nicki.vaadin.base.shop.AttributeComponent;
import org.mgnl.nicki.vaadin.base.shop.CheckboxComponent;
import org.mgnl.nicki.vaadin.base.shop.CostCenterComponent;
import org.mgnl.nicki.vaadin.base.shop.DateComponent;
import org.mgnl.nicki.vaadin.base.shop.LabelComponent;
import org.mgnl.nicki.vaadin.base.shop.ShopPage;
import org.mgnl.nicki.vaadin.base.shop.ShopViewerComponent;
import org.mgnl.nicki.vaadin.base.shop.TextComponent;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class BaseShopRenderer {
	
	protected static Map<String, Class<? extends AttributeComponent>> attributeComponents
		= new HashMap<String, Class<? extends AttributeComponent>>();
	static {
		attributeComponents.put("date", DateComponent.class);
		attributeComponents.put("text", TextComponent.class);
		attributeComponents.put("checkbox", CheckboxComponent.class);
		attributeComponents.put("costCenter", CostCenterComponent.class);
		attributeComponents.put("default", LabelComponent.class);
	}

	protected Component getAttributeComponent(CatalogArticleAttribute pageAttribute) {
		try {
			return attributeComponents.get(pageAttribute.getType()).newInstance().getInstance(pageAttribute);
		} catch (Exception e) {
			return new LabelComponent().getInstance(pageAttribute);
		}
		
	}

	protected Component getAttributeComponent(CatalogArticleAttribute pageAttribute, Object value) {
		try {
			AttributeComponent attributeComponent = attributeComponents.get(pageAttribute.getType()).newInstance();
			attributeComponent.setValue(value);
			return attributeComponent.getInstance(pageAttribute);
		} catch (Exception e) {
			return new LabelComponent().getInstance(pageAttribute);
		}
	}

	protected void removeExcept(Layout parent, Button button) {
		List<Component> toBeRemoved = new ArrayList<Component>();
		for (Iterator<Component> iterator = parent.getComponentIterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component != button) {
				toBeRemoved.add(component);
			}
		}
		for (Iterator<Component> iterator = toBeRemoved.iterator(); iterator.hasNext();) {
			Component component = iterator.next();
			parent.removeComponent(component);
		}
	}

	@SuppressWarnings("serial")
	protected Component getArticleComponent(CatalogArticle article) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setData(article);
		layout.setHeight("40px");
		CheckBox checkBox = new CheckBox(I18n.getText(article.getDisplayName()));
		checkBox.setImmediate(true);
		checkBox.setWidth("200px");
		layout.addComponent(checkBox);
		checkBox.addListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				  boolean enabled = event.getButton().booleanValue();
				  HorizontalLayout parent = (HorizontalLayout) event.getButton().getParent();
				  if (enabled) {
					  showArticleAttributes(parent);
				  } else {
//					  removeExcept(parent, event.getButton());
				  }
			}
		});

		return layout;
	}
	
	protected void showArticleAttributes(HorizontalLayout layout) {
		CatalogArticle article = (CatalogArticle) layout.getData(); 
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				layout.addComponent(getAttributeComponent(pageAttribute));
			}
		}
	}
	

	public Component getXMLComponent(ShopViewerComponent shopViewerComponent) {
		AbsoluteLayout layout = new AbsoluteLayout();
		layout.setHeight("420px");
		// textArea_1
		TextArea xml = new TextArea();
		xml.setWidth("100%");
		xml.setHeight("100%");
		xml.setImmediate(false);
		xml.setValue(shopViewerComponent.toString());
		layout.addComponent(xml, "top:20.0px;left:20.0px;right:20.0px;");

		return layout;
	}

	protected AbstractOrderedLayout getHorizontalArticleAttributes(CatalogArticle article) {
		AbstractOrderedLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setHeight("40px");
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute attribute = iterator.next();
				layout.addComponent(getAttributeComponent(attribute));
			}
		}
		return layout;
	}

	protected AbstractOrderedLayout getVerticalArticleAttributes(CatalogArticle article) {
		AbstractOrderedLayout attrLayout = new VerticalLayout();
		
		if (article.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = article.getAllAttributes().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				attrLayout.addComponent(getAttributeComponent(pageAttribute));
			}
		}
		return attrLayout;
	}
	
	protected Component getPageAttributes(ShopPage page) {
		VerticalLayout attrLayout = new VerticalLayout();
		
		if (page.hasAttributes()) {
			for (Iterator<CatalogArticleAttribute> iterator = page.getAttributeList().iterator(); iterator.hasNext();) {
				CatalogArticleAttribute pageAttribute = iterator.next();
				attrLayout.addComponent(getAttributeComponent(pageAttribute));
			}
		}
		return attrLayout;
	}






}
