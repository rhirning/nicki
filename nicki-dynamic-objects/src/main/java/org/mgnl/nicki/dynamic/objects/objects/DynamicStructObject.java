package org.mgnl.nicki.dynamic.objects.objects;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.mgnl.nicki.core.helper.DataHelper;
import org.mgnl.nicki.ldap.methods.StructuredData;


@SuppressWarnings("serial")
public abstract class DynamicStructObject extends DynamicTemplateObject {

	public static final String SEPARATOR = "/";
	/*
	 * Beispiel /ref/src
	 */
	public String getInfo(String infoPath) {
		String parts[] = StringUtils.split(infoPath, SEPARATOR);
		if (parts.length < 2) {
			return null;
		}
		try {
			Element element = getDocument().getRootElement();
			int i = 1;
			while (i < parts.length - 1) {
				element = element.getChild(parts[i]);
				i++;
			}
			
			return element.getChildTextTrim(parts[i]);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Date getDateInfo(String infoPath) {
		try {
			String info = StringUtils.chomp(getInfo(infoPath), "Z");
			return DataHelper.formatTime.parse(info);
		} catch (Exception e) {
			return null;
		}
	}

	public String getFlag() {
		return getAttribute("struct:flag");
	}

	public String getXml() {
		return getAttribute("struct:xml");
	}
	
	public Document getDocument() {
		return (Document) ((StructuredData) get("struct")).getDocument();
	}
	
}
