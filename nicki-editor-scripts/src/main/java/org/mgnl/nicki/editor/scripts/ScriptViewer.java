
package org.mgnl.nicki.editor.scripts;

/*-
 * #%L
 * nicki-editor-scripts
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



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.core.data.TreeData;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.dynamic.objects.objects.Script;
import org.mgnl.nicki.vaadin.base.editor.ClassEditor;
import org.mgnl.nicki.vaadin.base.editor.NickiTreeEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import bsh.EvalError;
import bsh.Interpreter;

@SuppressWarnings("serial")
public class ScriptViewer extends CustomComponent implements ClassEditor {
	@AutoGenerated
	private VerticalLayout mainLayout;

	@AutoGenerated
	private Panel result;

	@AutoGenerated
	private VerticalLayout resultLayout;

	@AutoGenerated
	private HorizontalLayout horizontalLayout_1;

	@AutoGenerated
	private Label resultObject;

	@AutoGenerated
	private Button saveButton;

	@AutoGenerated
	private Button executeButton;

	@AutoGenerated
	private TextArea editor;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	private static final Logger LOG = LoggerFactory.getLogger(ScriptViewer.class);

	private Script script;
	
	private Object request;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 * @param request 
	 */
	public ScriptViewer(Object request) {
		this.request = request;
	}

	public void setDynamicObject(NickiTreeEditor nickiEditor,
			TreeData dynamicObject) {
		this.script = (Script) dynamicObject;
		buildMainLayout();
		setCompositionRoot(mainLayout);
		resultObject.setContentMode(ContentMode.PREFORMATTED);
		editor.setValue(StringUtils.trimToEmpty(script.getData()));

		executeButton.addClickListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					evaluate();
				} catch (IOException e) {
					LOG.error("Error", e);
				}
			}
		});

		saveButton.addClickListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				try {
					save();
				} catch (Exception e) {
					LOG.error("Error", e);
				}
			}
		});

	}

	public void save() throws DynamicObjectException, NamingException {
		script.setData((String) editor.getValue());
		script.update("data");
	}

	protected void evaluate() throws IOException {
		resultLayout.removeAllComponents();
		resultObject.setValue("");
		String script = (String) editor.getValue();
		try {
			StringBuilder scriptOutput = new StringBuilder();
			Object resultObj = evalScript(script, scriptOutput, false);
			setResult(scriptOutput.toString());
			if (resultObj != null) {
				resultObject.setValue(resultObj.toString());
			} else {
				resultObject.setValue(null);
			}
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
					LOG.error("Error", e1);
				}
				setResult(errString);
			} else {
				LOG.error("Error", e);
			}
		}
	}

	String showScriptContext(String s, int lineNo, int context) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new StringReader(s));

		int beginLine = Math.max(1, lineNo - context);
		int endLine = lineNo + context;
		for (int i = 1; i <= lineNo + context + 1; i++) {
			if (i < beginLine) {
				br.readLine();
				continue;
			}
			if (i > endLine)
				break;

			String line;
			line = br.readLine();

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
			resultLine.setContentMode(ContentMode.PREFORMATTED);
			resultLayout.addComponent(resultLine);
		}

	}

	private Object evalScript(String script, StringBuilder scriptOutput,
			boolean captureOutErr) throws EvalError {
		// Create a PrintStream to capture output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream pout = new PrintStream(baos);

		// Create an interpreter instance with a null inputstream,
		// the capture out/err stream, non-interactive
		Interpreter bsh = new Interpreter(null, pout, pout, false);
		bsh.set("bsh.httpServletRequest", this.request);

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

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		
		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");
		
		// editor
		editor = new TextArea();
		editor.setImmediate(false);
		editor.setWidth("100.0%");
		editor.setHeight("300px");
		mainLayout.addComponent(editor);
		
		// horizontalLayout_1
		horizontalLayout_1 = buildHorizontalLayout_1();
		mainLayout.addComponent(horizontalLayout_1);
		
		// result
		result = buildResult();
		mainLayout.addComponent(result);
		mainLayout.setExpandRatio(result, 1.0f);
		
		return mainLayout;
	}

	@AutoGenerated
	private HorizontalLayout buildHorizontalLayout_1() {
		// common part: create layout
		horizontalLayout_1 = new HorizontalLayout();
		horizontalLayout_1.setImmediate(false);
		horizontalLayout_1.setWidth("-1px");
		horizontalLayout_1.setHeight("-1px");
		horizontalLayout_1.setMargin(false);
		horizontalLayout_1.setSpacing(true);
		
		// executeButton
		executeButton = new Button();
		executeButton.setCaption("Execute");
		executeButton.setImmediate(true);
		executeButton.setWidth("-1px");
		executeButton.setHeight("-1px");
		horizontalLayout_1.addComponent(executeButton);
		
		// saveButton
		saveButton = new Button();
		saveButton.setCaption("Save");
		saveButton.setImmediate(true);
		saveButton.setWidth("-1px");
		saveButton.setHeight("-1px");
		horizontalLayout_1.addComponent(saveButton);
		
		// resultObject
		resultObject = new Label();
		resultObject.setImmediate(false);
		resultObject.setWidth("100.0%");
		resultObject.setHeight("-1px");
		resultObject.setValue("");
		horizontalLayout_1.addComponent(resultObject);
		
		return horizontalLayout_1;
	}

	@AutoGenerated
	private Panel buildResult() {
		// common part: create layout
		result = new Panel();
		result.setCaption("Result");
		result.setImmediate(false);
		result.setWidth("100.0%");
		result.setHeight("100.0%");
		
		// resultLayout
		resultLayout = new VerticalLayout();
		resultLayout.setImmediate(false);
		resultLayout.setWidth("100.0%");
		resultLayout.setHeight("-1px");
		resultLayout.setMargin(true);
		result.setContent(resultLayout);
		
		return result;
	}

}
