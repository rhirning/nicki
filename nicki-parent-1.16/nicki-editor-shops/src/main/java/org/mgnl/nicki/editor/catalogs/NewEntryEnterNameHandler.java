package org.mgnl.nicki.editor.catalogs;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.vaadin.base.components.EnterNameHandler;

import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class NewEntryEnterNameHandler extends EnterNameHandler implements Serializable {
	private Table table;

	public NewEntryEnterNameHandler(Table table) {
		super();
		this.table = table;
	}

	public void setName(String name) throws Exception {
		if (StringUtils.isNotEmpty(name)) {
			table.addItem(new Object[]{name}, name);
		}
	}

}
