package org.mgnl.nicki.report;



import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Template;
import org.mgnl.nicki.editor.templates.TemplateConfig;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.EntryFilter;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

public class ReportApplication extends NickiApplication {

	private static final long serialVersionUID = -8245147689512577915L;
    

	@SuppressWarnings("unchecked")
	@Override
	public Component getEditor() {

		DataProvider dataProvider = new DynamicObjectRoot(getTemplatesRoot(), getEntryFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), dataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY, Org.class, Template.class );
		editor.configureClass(Template.class, Icon.DOCUMENT, TreeEditor.CREATE.DENY, TreeEditor.DELETE.DENY, TreeEditor.RENAME.DENY);
		editor.setClassEditor(Template.class, new TemplateConfig());
		editor.initActions();
		
		return editor;
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.templates";
	}
	
	public EntryFilter getEntryFilter() {
		return new ShowAllFilter();
	}
	
	public String getTemplatesRoot() {
		return Config.getProperty("nicki.templates.basedn");
	}


}
