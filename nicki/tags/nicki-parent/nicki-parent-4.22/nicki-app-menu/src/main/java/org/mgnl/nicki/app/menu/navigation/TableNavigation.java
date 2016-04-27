package org.mgnl.nicki.app.menu.navigation;

import java.util.List;

import org.mgnl.nicki.app.menu.application.MainView;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.VerticalLayout;

public class TableNavigation extends NavigationBase implements Navigation {
	private static final long serialVersionUID = -4231539383235849692L;
	private static final Object VISIBLE_COLUMNS[] = {"navigationCaption"};
	private VerticalLayout layout;
	private Table table;
	private NavigationEntry selected = null;
	
	public TableNavigation(MainView mainView) {
		super(mainView);
		buildMainLayout();
		setCompositionRoot(layout);
		setSizeFull();
	}

	private VerticalLayout buildMainLayout() {
		layout = new VerticalLayout();
		layout.setHeight("100%");
		Panel panel = new Panel();
		panel.setHeight("100px");
		panel.setStyleName("logo");
		layout.addComponent(panel);
		table = new Table();
		table.setImmediate(true);
		table.setSelectable(true);
		table.setNullSelectionAllowed(false);
		table.addContainerProperty("navigationCaption", String.class, null);
		table.setVisibleColumns(VISIBLE_COLUMNS);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setWidth("100%");
		table.setHeight("100%");
		table.setPageLength(0);

		table.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -3655302097682416518L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (table.getValue() instanceof NavigationEntry) {
					NavigationEntry entry = (NavigationEntry) table.getValue();
					if (entry != selected) {
						if (select(entry)) {
							selected = entry;
						} else {
							table.setValue(selected);
						}
					}
				} else {
					if (selected != null) {
						table.setValue(selected);
					}
				}
			}
		});
		
		table.setCellStyleGenerator(new CellStyleGenerator() {
			private static final long serialVersionUID = -6749256680831726555L;

			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				if (itemId instanceof NavigationFolder) {
					return "folder";
				}
				else if (itemId instanceof NavigationEntry) {
					return "entry";
				} else if (itemId instanceof NavigationSeparator) {
					return "separator";
				}

				return null;
			}
		});

		layout.addComponent(table);
		layout.setExpandRatio(table, 1.0f);
		//layout.setExpandRatio(panel, 0.0f);
		return layout;
	}
	

	@Override
	public void init(List<NavigationFolder> navigationFolders) {
		initContainer();
		for (NavigationFolder folder : navigationFolders) {
			if (folder.isSeparator()) {
				add(new NavigationSeparator());
			} else {
				add(folder);
				for (NavigationEntry entry : folder.getEntries()) {
					add(entry);
				}
			}
		}
		table.setContainerDataSource(getContainer());
	}
}