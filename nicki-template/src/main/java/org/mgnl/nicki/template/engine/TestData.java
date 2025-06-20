
package org.mgnl.nicki.template.engine;

/*-
 * #%L
 * nicki-template
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



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.context.TargetFactory;
import org.mgnl.nicki.core.objects.DynamicObject;


/**
 * The Class TestData.
 */
public class TestData {

	/** The Constant SEPARATOR. */
	public static final String SEPARATOR = "=";
	
	/** The dynamic object. */
	private DynamicObject dynamicObject;
	
	/**
	 * Instantiates a new test data.
	 *
	 * @param dynamicObject the dynamic object
	 */
	public TestData(DynamicObject dynamicObject) {
		super();
		this.dynamicObject = dynamicObject;
	}

	/**
	 * Gets the target.
	 *
	 * @return the target
	 */
	public Target getTarget() {
		@SuppressWarnings("unchecked")
		List<String> testData = (List<String>) dynamicObject.get("testData");
		if (testData != null) {
			for (String value : testData) {
				String name = StringUtils.substringBefore(value, SEPARATOR);
				String data = StringUtils.substringAfter(value, SEPARATOR);
				if (StringUtils.equals(name, "target") && StringUtils.isNotEmpty(data)) {
					Target t = TargetFactory.getTarget(data);
					if (t != null) {
						return t;
					}
				}
			}
		}
		return TargetFactory.getDefaultTarget();
	}

	/**
	 * Gets the data.
	 *
	 * @param context the context
	 * @return the data
	 */
	public Map<String, Object> getData(NickiContext context) {
		// Create the root hash
		Map<String, Object> root = new HashMap<String, Object>();
		return root;
	}
	
	/**
	 * Gets the objects.
	 *
	 * @return the objects
	 */
	public  Map<String, String> getObjects() {
		Map<String, String> objects = new HashMap<String, String>();
		objects.put("person", "cn=ablake,ou=users,o=utopia");
		return objects;
	}
	
	/**
	 * Gets the data model.
	 *
	 * @param context the context
	 * @return the data model
	 */
	public Map<String, Object> getDataModel(NickiContext context) {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		@SuppressWarnings("unchecked")
		List<String> testData = (List<String>) dynamicObject.get("testData");
		if (testData != null) {
			for (String value : testData) {
				String name = StringUtils.substringBefore(value, SEPARATOR);
				String data = StringUtils.substringAfter(value, SEPARATOR);
				if (StringUtils.isNotEmpty(data)) {
					if (context.isExist(data)) {
						dataModel.put(name, context.loadObject(data));
					} else {
						dataModel.put(name, data);
					}
				}
			}
		}
		return dataModel;
	}


}
	
