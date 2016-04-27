package org.mgnl.nicki.editor.templates;


import javax.naming.NamingException;

import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.components.SimpleEditor;
import org.mgnl.nicki.vaadin.base.components.TestDataView;
import org.mgnl.nicki.vaadin.base.data.AttributeDataContainer;
import org.mgnl.nicki.vaadin.base.data.ListPartDataContainer;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class TemplateViewer extends CustomComponent implements ClassEditor {

	private AbsoluteLayout mainLayout;
	
	private TabSheet tab;
	private Template template;
	private Button saveButton;
	private Button previewButton;
	private Button htmlPreviewButton;
	private Link csvLink;
	private Link pdfLink;
	private NickiTreeEditor editor;
	
	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public TemplateViewer() {
	}
	
	public void setDynamicObject(NickiTreeEditor nickiEditor, DynamicObject dynamicObject) {
		this.editor = nickiEditor;
		this.template = (Template) dynamicObject;
		buildEditor();
		setCompositionRoot(mainLayout);
		
		createSheets();
		
		saveButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		previewButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					preview();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		htmlPreviewButton.addListener(new Button.ClickListener() {
			
			public void buttonClick(ClickEvent event) {
				try {
					htmlPreview();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		pdfLink.setCaption("PDF");
		pdfLink.setTargetName("_blank");
		PdfStreamSource pdfStreamSource = new PdfStreamSource(template, template.getContext());
		pdfLink.setResource(new LinkResource(pdfStreamSource, template.getName() + ".pdf",
				nickiEditor.getApplication(), "application/pdf"));

		csvLink.setCaption("CSV");
		csvLink.setTargetName("_blank");
		CsvStreamSource csvStreamSource = new CsvStreamSource(template, template.getContext());
		csvLink.setResource(new LinkResource(csvStreamSource, template.getName() + ".csv",
				nickiEditor.getApplication(), "text/comma-separated-values"));
	}

	protected void preview() throws DynamicObjectException, NamingException {
		save();
		PreviewTemplate preview = new PreviewTemplate(editor.getNickiContext(), editor.getMessageKeyBase());
		preview.execute(getWindow(), template);
	}

	protected void htmlPreview() throws DynamicObjectException, NamingException {
		save();
		HtmlPreviewTemplate preview = new HtmlPreviewTemplate(editor.getNickiContext(), editor.getMessageKeyBase());
		preview.execute(getWindow(), template);
	}

	private void createSheets() {
		tab.addTab(new SimpleEditor(new AttributeDataContainer(template, "data")), I18n.getText(editor.getMessageKeyBase() +".tab.data"), null);
		tab.addTab(new TestDataView(new ListPartDataContainer(template, "testData", "="), editor.getMessageKeyBase()),
				I18n.getText(editor.getMessageKeyBase() +".tab.testdata"), null);

	}
	
	private AbsoluteLayout buildEditor() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setHeight("100%");
		mainLayout.addComponent(verticalLayout, "top:20.0px;left:20.0px;");

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		tab = new TabSheet();
		tab.setWidth("640px");
		tab.setHeight("640px");
		tab.setImmediate(false);
		verticalLayout.addComponent(tab);

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setHeight(40, UNITS_PIXELS);
		verticalLayout.addComponent(horizontalLayout);
		saveButton = new Button();
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		saveButton.setCaption("Speichern");
		saveButton.setImmediate(true);
		horizontalLayout.addComponent(saveButton);
		
		previewButton = new Button();
		previewButton.setWidth("-1px");
		previewButton.setHeight("-1px");
		previewButton.setCaption("Vorschau");
		previewButton.setImmediate(true);
		horizontalLayout.addComponent(previewButton);
		
		htmlPreviewButton = new Button();
		htmlPreviewButton.setWidth("-1px");
		htmlPreviewButton.setHeight("-1px");
		htmlPreviewButton.setCaption("HTML Vorschau");
		htmlPreviewButton.setImmediate(true);
		horizontalLayout.addComponent(htmlPreviewButton);
		
		pdfLink = new Link();
		horizontalLayout.addComponent(pdfLink);
		
		csvLink = new Link();
		horizontalLayout.addComponent(csvLink);
		
		return mainLayout;
	}

	public TabSheet getTab() {
		return tab;
	}


	public void save() throws DynamicObjectException, NamingException {
		if (template.isComplete()) {
			if (!template.isNew()) {
				template.update();
			}
		}
	}
	
}