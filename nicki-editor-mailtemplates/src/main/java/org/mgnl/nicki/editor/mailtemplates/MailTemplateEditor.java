package org.mgnl.nicki.editor.mailtemplates;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class MailTemplateEditor extends NickiApplication {
	@Override
	public Component getEditor() {
		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.mailtemplates.basedn"), new MailTemplateFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, Org.class, Template.class );
		editor.configureClass(Template.class, Icon.DOCUMENT, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.setClassEditor(Template.class, new I18nMailTemplateViewer(getI18nBase()));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test de", "de"));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test fr", "fr"));
		editor.addAction(new PreviewMail(getNickiContext(), Template.class, "Test it", "it"));
		editor.initActions();
		return editor;
	}
	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}
	
	@Override
	public String getI18nBase() {
		return "nicki.editor.mailtemplates";
	}

	
}
