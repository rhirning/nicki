package org.mgnl.nicki.template.loader;

import java.util.Enumeration;
import java.util.List;

import javax.naming.directory.SearchControls;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.QueryHandler;
import org.mgnl.nicki.ldap.core.BasicLdapHandler;
import org.mgnl.nicki.ldap.objects.ContextAttribute;
import org.mgnl.nicki.ldap.objects.ContextAttributes;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;
import org.mgnl.nicki.template.engine.Template;

public class TemplateLoaderLdapQueryHandler extends BasicLdapHandler implements QueryHandler {
	
	public static final String TEMPLATE_NAME = "ou";
	public static final String ATTRIBUTE_DATA = "nickiTemplateData";
	public static final String ATTRIBUTE_PART = "nickiTemplatePart";
	public static final String PART_SEPARATOR = "=";
	private String name = null;
	private String dn = null;
	private Template template = null;

	public TemplateLoaderLdapQueryHandler(NickiContext context, String name, String dn) {
		super(context);
		this.name = name;
		this.dn = dn;
	}


	@Override
	public String getBaseDN() {
		return this.dn;
	}

	@Override
	public String getFilter() {
		return "(objectClass=nickiTemplate)";
	}

	@Override
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


	public void handle(ContextSearchResult rs) throws DynamicObjectException {
		template = new Template(this.name);
			
		ContextAttributes attrs = rs.getAttributes();
		try {
			template.setData(getAttribute(rs, ATTRIBUTE_DATA));
		} catch (Exception e) {
		}
		ContextAttribute attr = attrs.get(ATTRIBUTE_PART);
		if (attr != null) {
			for ( Enumeration<Object> vals = (Enumeration<Object>) attr.getAll(); vals.hasMoreElements();) {
				String partString = (String) vals.nextElement();
				String partName = StringUtils.substringBefore(partString, PART_SEPARATOR);
				String partValue = StringUtils.substringAfter(partString, PART_SEPARATOR);
				template.putPart(partName, partValue);
			}
		}
	}

	public Template getTemplate() {
		return template;
	}


	private String getAttribute(ContextSearchResult rs, String attributeName) throws DynamicObjectException {
		ContextAttributes attributes = rs.getAttributes();
		String result = attributes.get(attributeName).get().toString();
		if (StringUtils.isNotEmpty(result)) {
			return result;
		}

		return null;
	}


	public SearchControls getConstraints() {
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.OBJECT_SCOPE);
		return constraints;
	}
}
