package org.mgnl.nicki.editor.templates;

import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.vaadin.base.components.SimpleEditor;
import org.mgnl.nicki.vaadin.base.components.SimpleUploadEditor;
import org.mgnl.nicki.vaadin.base.data.PartDataContainer;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

public class ExcelEditor extends CustomComponent {
	private static final long serialVersionUID = -4999034916650372313L;

	public ExcelEditor(String messageBase, Template template) {
		// common part: create layout
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		SimpleUploadEditor simpleUploadEditor = new SimpleUploadEditor(messageBase,
				template, Template.ATTRIBUTE_FILE,
			"ExcelMaster.xls", "application/msexcel");
//		SimpleUploadEditor simpleUploadEditor = new SimpleUploadEditor(
//				new AttributeDataContainer<byte[]>(template, Template.ATTRIBUTE_FILE),
//			"ExcelMaster.xls", "application/msexcel");
		mainLayout.addComponent(simpleUploadEditor);

		SimpleEditor simpleEditor = new SimpleEditor(new PartDataContainer(
				template, Template.ATTRIBUTE_PARTS, "xls", "="));
		mainLayout.addComponent(simpleEditor);
		mainLayout.setExpandRatio(simpleEditor, 1);
		setCompositionRoot(mainLayout);
	}

}
