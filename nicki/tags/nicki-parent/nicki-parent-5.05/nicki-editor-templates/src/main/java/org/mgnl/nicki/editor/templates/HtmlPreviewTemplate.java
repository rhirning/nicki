/**
 * This file Copyright (c) 2003-2011 Dr. Ralf Hirning
 * All rights reserved.
 *
 *
 * This file is dual-licensed under both the GNU General
 * Public License and an individual license with Dr. Ralf
 * Hirning.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or the individual license, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the individual license, this file and the accompanying
 * materials are made available under the terms of the
 * individual license.
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package org.mgnl.nicki.editor.templates;


import java.util.Map;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.vaadin.base.editor.BaseTreeAction;
import org.mgnl.nicki.vaadin.base.editor.LinkResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class HtmlPreviewTemplate extends BaseTreeAction {
	private static final Logger LOG = LoggerFactory.getLogger(HtmlPreviewTemplate.class);

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private BrowserFrame result;
	private Window previewWindow;
	private NickiContext context;
	private String i18nBase;
	private Map<String, Object> params;

	public HtmlPreviewTemplate(NickiContext context, String i18nBase, Map<String, Object> params) {
		super(null, null);
		this.context = context;
		this.i18nBase = i18nBase;
		this.params = params;
		buildMainLayout();
		setCompositionRoot(mainLayout);
	}

	public void close() {
		UI.getCurrent().removeWindow(previewWindow);
	}

	public void execute(DynamicObject dynamicObject) {
		Template template = (Template) dynamicObject;
		showResultDialog(template, params);
	}

	private void showResultDialog(Template template, Map<String, Object> params) {
		try {
			StringStreamSource streamSource = new StringStreamSource(template, context, params);

			this.result.setSource(new LinkResource(streamSource, template.getName() + ".html?a=b", "text/html"));
			if (null != this.getParent()) {
				this.setParent(null);
			}
			previewWindow = new Window(I18n.getText(i18nBase + ".preview.window.title"), this);
			previewWindow.setModal(true);
			previewWindow.setWidth(1024, Unit.PIXELS);
			previewWindow.setHeight(520, Unit.PIXELS);
			UI.getCurrent().addWindow(previewWindow);
		} catch (Exception e) {
			LOG.error("Error", e);
		}
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// result
		result = new BrowserFrame();
		result.setWidth("100.0%");
		result.setHeight("100.0%");
		result.setImmediate(false);
		mainLayout.addComponent(result,
				"top:20.0px;right:20.0px;bottom:20.0px;left:20.0px;");
		
		return mainLayout;
	}

}