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
package org.mgnl.nicki.editor.scripts;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.dynamic.objects.objects.Script;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;

import bsh.EvalError;
import bsh.Interpreter;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ScriptViewer extends CustomComponent implements ClassEditor {

	@AutoGenerated
	private AbsoluteLayout mainLayout;
	@AutoGenerated
	private Button saveButton;
	@AutoGenerated
	private Label resultObject;
	@AutoGenerated
	private Panel result;
	@AutoGenerated
	private VerticalLayout vLayout;
	@AutoGenerated
	private Button executeButton;
	@AutoGenerated
	private TextArea editor;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Script script;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public ScriptViewer() {
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor,
			DynamicObject dynamicObject) {
		this.script = (Script) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		resultObject.setContentMode(Label.CONTENT_PREFORMATTED);
		editor.setValue(StringUtils.trimToEmpty(script.getData()));

		executeButton.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					evaluate();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		saveButton.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					save();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void save() throws DynamicObjectException, NamingException {
		script.setData((String) editor.getValue());
		script.update();
	}

	protected void evaluate() throws IOException {
		result.removeAllComponents();
		resultObject.setValue("");
		String script = (String) editor.getValue();
		try {
			StringBuffer scriptOutput = new StringBuffer();
			Object resultObj = evalScript(script, scriptOutput, false);
			setResult(scriptOutput.toString());
			resultObject.setValue(resultObj);
		} catch (Exception e) {
			if (e instanceof EvalError) {
				String errString = "ERROR:\n";
				errString += e.getMessage();
				try {
					int lineNo = ((EvalError) e).getErrorLineNumber();
					int contextLines = 4;
					if (lineNo > -1) {
						errString += "\n------------------------------------------------------\n"
								+ showScriptContext(script, lineNo,
										contextLines);
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				setResult(errString);
			} else {
				e.printStackTrace();
			}
		}
	}

	String showScriptContext(String s, int lineNo, int context) {
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new StringReader(s));

		int beginLine = Math.max(1, lineNo - context);
		int endLine = lineNo + context;
		for (int i = 1; i <= lineNo + context + 1; i++) {
			if (i < beginLine) {
				try {
					br.readLine();
				} catch (IOException e) {
					throw new RuntimeException(e.toString());
				}
				continue;
			}
			if (i > endLine)
				break;

			String line;
			try {
				line = br.readLine();
			} catch (IOException e) {
				throw new RuntimeException(e.toString());
			}

			if (line == null)
				break;
			if (i == lineNo)
				sb.append(i + ": --> " + line + "\n");
			else
				sb.append(i + ":     " + line + "\n");
		}

		return sb.toString();
	}

	private void setResult(String scriptOutput) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(
				scriptOutput));
		String line;
		while ((line = reader.readLine()) != null) {
			Label resultLine = new Label(line);
			resultLine.setContentMode(Label.CONTENT_PREFORMATTED);
			result.addComponent(resultLine);
		}

	}

	private Object evalScript(String script, StringBuffer scriptOutput,
			boolean captureOutErr) throws EvalError {
		// Create a PrintStream to capture output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(baos);

		// Create an interpreter instance with a null inputstream,
		// the capture out/err stream, non-interactive
		Interpreter bsh = new Interpreter(null, pout, pout, false);

		// set up interpreter
		bsh.set("bsh.httpServletRequest", request);
		bsh.set("bsh.httpServletResponse", response);

		// Eval the text, gathering the return value or any error.
		Object result = null;
		PrintStream sout = System.out;
		PrintStream serr = System.err;
		if (captureOutErr) {
			System.setOut(pout);
			System.setErr(pout);
		}
		try {
			// Eval the user text
			result = bsh.eval(script);
		} finally {
			if (captureOutErr) {
				System.setOut(sout);
				System.setErr(serr);
			}
		}
		pout.flush();
		scriptOutput.append(baos.toString());
		return result;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// editor
		editor = new TextArea();
		editor.setWidth("100.0%");
		editor.setHeight("300px");
		editor.setImmediate(false);
		mainLayout.addComponent(editor, "top:20.0px;right:20.0px;left:20.0px;");
		
		// executeButton
		executeButton = new Button();
		executeButton.setWidth("-1px");
		executeButton.setHeight("-1px");
		executeButton.setCaption("Execute");
		executeButton.setImmediate(true);
		mainLayout.addComponent(executeButton, "top:330.0px;left:20.0px;");
		
		// result
		result = buildResult();
		mainLayout.addComponent(result,
				"top:360.0px;right:20.0px;bottom:20.0px;left:20.0px;");
		
		// resultObject
		resultObject = new Label();
		resultObject.setWidth("100.0%");
		resultObject.setHeight("-1px");
		resultObject.setImmediate(false);
		mainLayout.addComponent(resultObject,
				"top:330.0px;right:20.0px;left:200.0px;");
		
		// saveButton
		saveButton = new Button();
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		saveButton.setCaption("Save");
		saveButton.setImmediate(true);
		mainLayout.addComponent(saveButton, "top:330.0px;left:100.0px;");
		
		return mainLayout;
	}

	@AutoGenerated
	private Panel buildResult() {
		// common part: create layout
		result = new Panel();
		result.setWidth("100.0%");
		result.setHeight("300px");
		result.setCaption("Result");
		result.setImmediate(false);
		
		// vLayout
		vLayout = new VerticalLayout();
		vLayout.setWidth("100.0%");
		vLayout.setHeight("-1px");
		vLayout.setImmediate(false);
		vLayout.setMargin(true);
		result.setContent(vLayout);
		
		return result;
	}

}
