/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * Author: Dr. Ralf Hirning
 * All rights reserved.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.vaadin.base.fields;

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
