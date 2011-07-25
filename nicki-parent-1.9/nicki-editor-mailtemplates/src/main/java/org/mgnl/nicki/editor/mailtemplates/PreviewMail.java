package org.mgnl.nicki.editor.mailtemplates;


import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.i18n.I18n;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.mailtemplate.engine.MailTemplateEngine;
import org.mgnl.nicki.mailtemplate.model.MailWrapper;
import org.mgnl.nicki.vaadin.base.editor.BaseTreeAction;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PreviewMail extends BaseTreeAction {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private VerticalLayout verticalLayout_1;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_3;
	@AutoGenerated
	private Label footer;
	@AutoGenerated
	private Label label_footer;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_2;
	@AutoGenerated
	private Label header;
	@AutoGenerated
	private Label label_header;
	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;
	@AutoGenerated
	private Label subject;
	@AutoGenerated
	private Label label_subject;
	
	private Window parentWindow;
	private Window previewWindow;
	private String localeString;
	private NickiContext context;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 * @param localeString 
	 */
	public PreviewMail(NickiContext context, Class<?> classDefinition,
			String name, String localeString) {
		super(classDefinition, name);
		this.context = context;
		this.localeString = localeString;
		buildMainLayout();
		setCompositionRoot(mainLayout);

		// TODO add user code here
	}

	public void close() {
		this.parentWindow.removeWindow(previewWindow);
	}

	@Override
	public void execute(Window parentWindow, DynamicObject dynamicObject) {
		this.parentWindow = parentWindow;
		String parentPath = Config.getProperty("nicki.mailtemplates.basedn");
		String templatePath = dynamicObject.getSlashPath(parentPath);
		if (StringUtils.contains(templatePath, "_")) {
			templatePath = StringUtils.substringBefore(templatePath, "_");
		}
		
		try {
			MailWrapper wrapper = 
			MailTemplateEngine.executeMailTemplate(templatePath, new TestData(dynamicObject).getDataModel(context));
			subject.setValue(wrapper.getMailPart(this.localeString, MailTemplateEngine.MAILPART_SUBJECT));
			header.setValue(wrapper.getMailPart(this.localeString, MailTemplateEngine.MAILPART_HEADER));
			footer.setValue(wrapper.getMailPart(this.localeString, MailTemplateEngine.MAILPART_FOOTER));
			if (null != this.getParent()) {
				this.setParent(null);
			}
			previewWindow = new Window(I18n.getText("nicki.editor.shop.create.window.title"), this);
			previewWindow.setModal(true);
			previewWindow.setWidth(1024, Sizeable.UNITS_PIXELS);
			previewWindow.setHeight(520, Sizeable.UNITS_PIXELS);
			this.parentWindow.addWindow(previewWindow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// verticalLayout_1
		verticalLayout_1 = buildVerticalLayout_1();
		mainLayout.addComponent(verticalLayout_1, "top:20.0px;left:20.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private VerticalLayout buildVerticalLayout_1() {
		// common part: create layout
		verticalLayout_1 = new VerticalLayout();
		verticalLayout_1.setWidth("100.0%");
		verticalLayout_1.setHeight("100.0%");
		verticalLayout_1.setImmediate(false);
		verticalLayout_1.setMargin(false);
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		verticalLayout_1.addComponent(horizontalLayout_1);
		
		// horizontalLayout_2
		horizontalLayout_2 = buildHorizontalLayout_2();
		verticalLayout_1.addComponent(horizontalLayout_2);
		
		// horizontalLayout_3
		horizontalLayout_3 = buildHorizontalLayout_3();
		verticalLayout_1.addComponent(horizontalLayout_3);
		
		return verticalLayout_1;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setMargin(false);
		
		// label_subject
		label_subject = new Label();
		label_subject.setWidth("100px");
		label_subject.setHeight("-1px");
		label_subject.setValue("Subject");
		label_subject.setImmediate(false);
		horizontalLayout_1.addComponent(label_subject);
		
		// subject
		subject = new Label();
		subject.setWidth("400px");
		subject.setHeight("-1px");
		subject.setValue("Subject");
		subject.setImmediate(false);
		horizontalLayout_1.addComponent(subject);
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_2() {
		// common part: create layout
		horizontalLayout_2 = new HorizontalLayout();
		horizontalLayout_2.setWidth("-1px");
		horizontalLayout_2.setHeight("-1px");
		horizontalLayout_2.setImmediate(false);
		horizontalLayout_2.setMargin(false);
		
		// label_header
		label_header = new Label();
		label_header.setWidth("100px");
		label_header.setHeight("-1px");
		label_header.setValue("Header");
		label_header.setImmediate(false);
		horizontalLayout_2.addComponent(label_header);
		
		// header
		header = new Label();
		header.setWidth("-1px");
		header.setHeight("-1px");
		header.setValue("Header");
		header.setImmediate(false);
		horizontalLayout_2.addComponent(header);
		
		return horizontalLayout_2;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_3() {
		// common part: create layout
		horizontalLayout_3 = new HorizontalLayout();
		horizontalLayout_3.setWidth("-1px");
		horizontalLayout_3.setHeight("-1px");
		horizontalLayout_3.setImmediate(false);
		horizontalLayout_3.setMargin(false);
		
		// label_footer
		label_footer = new Label();
		label_footer.setWidth("100px");
		label_footer.setHeight("-1px");
		label_footer.setValue("Footer");
		label_footer.setImmediate(false);
		horizontalLayout_3.addComponent(label_footer);
		
		// footer
		footer = new Label();
		footer.setWidth("-1px");
		footer.setHeight("-1px");
		footer.setValue("Footer");
		footer.setImmediate(false);
		horizontalLayout_3.addComponent(footer);
		
		return horizontalLayout_3;
	}

}
