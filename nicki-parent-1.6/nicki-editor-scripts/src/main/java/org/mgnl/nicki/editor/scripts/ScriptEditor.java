package org.mgnl.nicki.editor.scripts;


import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.objects.Org;
import org.mgnl.nicki.dynamic.objects.objects.Script;
import org.mgnl.nicki.ldap.context.Target;
import org.mgnl.nicki.ldap.context.TargetFactory;
import org.mgnl.nicki.vaadin.base.application.NickiApplication;
import org.mgnl.nicki.vaadin.base.editor.DynamicObjectRoot;
import org.mgnl.nicki.vaadin.base.editor.Icon;
import org.mgnl.nicki.vaadin.base.editor.ShowAllFilter;
import org.mgnl.nicki.vaadin.base.editor.DataProvider;
import org.mgnl.nicki.vaadin.base.editor.TreeEditor;

import com.vaadin.ui.Component;

@SuppressWarnings("serial")
public class ScriptEditor extends NickiApplication {
	
	@Override
	public Component getEditor() {
		ScriptViewer scriptViewer = new ScriptViewer();
		scriptViewer.setRequest(getRequest());
		scriptViewer.setResponse(getResponse());

		DataProvider treeDataProvider = new DynamicObjectRoot(Config.getProperty("nicki.scripts.basedn"), new ShowAllFilter());
		TreeEditor editor = new TreeEditor(this, getNickiContext(), treeDataProvider, getI18nBase());
		editor.configureClass(Org.class, Icon.FOLDER, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW, Org.class, Script.class );
		editor.configureClass(Script.class, Icon.DOCUMENT, TreeEditor.DELETE.ALLOW, TreeEditor.RENAME.ALLOW);
		editor.setClassEditor(Script.class, scriptViewer);
		editor.initActions();
		
		return editor;
	}

	@Override
	public Target getTarget() {
		return TargetFactory.getDefaultTarget();
	}

	@Override
	public String getI18nBase() {
		return "nicki.editor.script";
	}
	
}
