
package org.mgnl.nicki.idm.novell.objects;

/*-
 * #%L
 * nicki-idm-novell
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

import org.mgnl.nicki.core.annotation.DynamicAttribute;
import org.mgnl.nicki.core.annotation.ObjectClass;
import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.objects.DynamicObject;

@ObjectClass("nickiAction")
public class Action extends BaseDynamicObject {
	private static final long serialVersionUID = 650085730365865573L;

	@DynamicAttribute(externalName="cn", naming=true)
	private String name;

	@DynamicAttribute(externalName="nickiParameter")
	private String parameter;

	@DynamicAttribute(externalName="nickiActionName", mandatory=true)
	private String action;

	@DynamicAttribute(externalName="nickiTarget", foreignKey=DynamicObject.class)
	private String target;

	public void setParameter(String parameter) {
		put("parameter", parameter);
	}

	public String getParameter() {
		return (String) get("parameter");
	}

	public void setTarget(DynamicObject target) {
		setTarget(target.getPath());
	}

	public void setTarget(String target) {
		put("target", target);
	}

	public String getTarget() {
		return (String) get("target");
	}

	public void setActionName(String name) {
		put("action", name);
	}

	public String getActionName() {
		return (String) get("action");
	}

}
