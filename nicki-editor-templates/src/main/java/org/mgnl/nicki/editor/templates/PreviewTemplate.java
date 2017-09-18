
package org.mgnl.nicki.editor.templates;

/*-
 * #%L
 * nicki-editor-templates
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.vaadin.base.editor.BaseTreeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PreviewTemplate extends BaseTreeAction {
	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private TextArea result;



	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */



	private static final Logger LOG = LoggerFactory.getLogger(PreviewTemplate.class);

	private Window previewWindow;
	private NickiContext context;
	private String i18nBase;
	private Map<String, Object> params;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param localeString 
	 */
	public PreviewTemplate(NickiContext context, Class<? extends TreeData> classDefinition,
			String name, String i18nBase) {
		super(classDefinition, name);
		this.context = context;
		this.i18nBase = i18nBase;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		result.setWordwrap(false);
	}

	public PreviewTemplate(NickiContext context, String i18nBase, Map<String, Object> params) {
		super(null, null);
		this.context = context;
		this.i18nBase = i18nBase;
		this.params = params;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		result.setWordwrap(false);
	}
	
	public void close() {
		UI.getCurrent().removeWindow(previewWindow);
	}

	public void execute(TreeData dynamicObject) {
		Template template = (Template) dynamicObject;
		showResultDialog(template, params);
	}

	private void showResultDialog(Template template, Map<String, Object> params) {
		try {
			StringStreamSource streamSource = new StringStreamSource(template, context, params);
			this.result.setValue(IOUtils.toString(streamSource.getStream()));
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
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// result
		result = new TextArea();
		result.setImmediate(false);
		result.setWidth("100.0%");
		result.setHeight("100.0%");
		mainLayout.addComponent(result);
		
		return mainLayout;
	}

}
