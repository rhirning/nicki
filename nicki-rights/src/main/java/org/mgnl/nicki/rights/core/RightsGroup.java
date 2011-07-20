package org.mgnl.nicki.rights.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.mgnl.nicki.rights.provider.Provider;

public class RightsGroup extends Right {

	private List<Right> rightsList = new ArrayList<Right>();
	private Provider provider = null;
	private String providerClass = null;
	
	public RightsGroup(Element rightsGroupElement) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(rightsGroupElement);
		providerClass = rightsGroupElement.getAttributeValue("provider");
		@SuppressWarnings("unchecked")
		List<Element> rights = rightsGroupElement.getChildren("right");
		if (rights != null && rights.size() > 0) {
			for (Iterator<Element> iterator = rights.iterator(); iterator.hasNext();) {
				Element rightElement = iterator.next();
				rightsList.add(new Right(rightElement, getAttributeList()));
			}
		}

	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[rightsGroup name='").append(getName());
		sb.append("' label='").append(getLabel());
		sb.append("' provider='").append(getProvider().getClass().getName());
		sb.append("']\n");
		for (Iterator<RightAttribute> iterator = getAttributeList().iterator(); iterator.hasNext();) {
			RightAttribute attribute = iterator.next();
			sb.append(attribute.toString()).append("\n");
		}
		return sb.toString();
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public Provider getProvider() {
		return provider;
	}

	public List<Right> getRightsList() {
		return rightsList;
	}

	public boolean hasRights() {
		return rightsList != null && rightsList.size() > 0;
	}


}
