
package org.mgnl.nicki.template.loader;

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


import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mgnl.nicki.core.objects.ContextSearchResult;
import org.mgnl.nicki.core.objects.DynamicObjectException;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.data.QueryHandler;
import org.mgnl.nicki.ldap.context.LdapContext;
import org.mgnl.nicki.ldap.query.BasicLdapHandler;
import org.mgnl.nicki.template.engine.Template;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class TemplateLoaderLdapQueryHandler.
 */
@Slf4j
public class TemplateLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	/** The Constant TEMPLATE_NAME. */
	public static final String TEMPLATE_NAME = "ou";
	
	/** The Constant ATTRIBUTE_DATA. */
	public static final String ATTRIBUTE_DATA = "nickiTemplateData";
	
	/** The Constant ATTRIBUTE_PART. */
	public static final String ATTRIBUTE_PART = "nickiTemplatePart";
	
	/** The Constant PART_SEPARATOR. */
	public static final String PART_SEPARATOR = "=";
	
	/** The name. */
	private String name;
	
	/** The dn. */
	private String dn;
	
	/** The template. */
	private Template template;

	/**
	 * Instantiates a new template loader ldap query handler.
	 *
	 * @param context the context
	 * @param name the name
	 * @param dn the dn
	 */
	public TemplateLoaderLdapQueryHandler(NickiContext context, String name, String dn) {
		super((LdapContext) context);
		this.name = name;
		this.dn = dn;
		this.setFilter("objectClass=nickiTemplate");
	}


	/**
	 * Gets the base DN.
	 *
	 * @return the base DN
	 */
	public String getBaseDN() {
		return this.dn;
	}

	/**
	 * Handle.
	 *
	 * @param results the results
	 */
	public void handle(List<ContextSearchResult> results) {
		try {
			template = new Template(this.name);
			if (results != null && results.size() > 0) {
				handle(results.get(0));
			}
		} catch (Exception e) {
			template = null;
		}
	}


	/**
	 * Handle.
	 *
	 * @param rs the rs
	 * @throws DynamicObjectException the dynamic object exception
	 */
	public void handle(ContextSearchResult rs) throws DynamicObjectException {
		template = new Template(this.name);

		try {
			template.setData(getAttribute(rs, ATTRIBUTE_DATA));
		} catch (Exception e) {
			log.debug("Error", e);
		}

		for (Object value : rs.getValues(ATTRIBUTE_PART)) {
			String partString = (String) value;
			String partName = StringUtils.substringBefore(partString, PART_SEPARATOR);
			String partValue = StringUtils.substringAfter(partString, PART_SEPARATOR);
			template.putPart(partName, partValue);
		}
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}


	/**
	 * Gets the attribute.
	 *
	 * @param rs the rs
	 * @param attributeName the attribute name
	 * @return the attribute
	 * @throws DynamicObjectException the dynamic object exception
	 */
	private String getAttribute(ContextSearchResult rs, String attributeName) throws DynamicObjectException {
		String result = (String) rs.getValue(String.class, attributeName);
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}

		return null;
	}


	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	@Override
	public SCOPE getScope() {
		return SCOPE.OBJECT;
	}
}
