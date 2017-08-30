/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.jcr.objects;

import java.io.Serializable;

import javax.jcr.Node;


public class GenericNodeDynamicObject extends NodeDynamicTemplateObject implements JcrDynamicObject, Serializable {
	private static final long serialVersionUID = -2438906061486993342L;
	public static final String ATTRIBUTE_SURNAME = "surname";
	public static final String ATTRIBUTE_GIVENNAME = "givenname";
	public static final String ATTRIBUTE_FULLNAME = "fullname";
	public static final String ATTRIBUTE_PASSWORD = "password";
	public static final String ATTRIBUTE_LANGUAGE = "language";
	
	@Override
	public boolean accept(Node node) {
		return true;
	}

}
