package org.mgnl.nicki.dynamic.objects.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.objects.ContextSearchResult;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Member extends DynamicObject implements Serializable{

	@Override
	public void initDataModel() {
		addObjectClass("nickiProjectMember");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute("member", "nickiProjectPerson", String.class,
				"nicki.users.basedn", "(objectClass=Person)");
		dynAttribute.setForeignKey();
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryRead", "nickiProjectDirectoryRead", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryWrite", "nickiProjectDirectoryWrite", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

	}
	
	@SuppressWarnings("unchecked")
	public List<String> getReadRights() {
		return get("directoryRead") != null?(List<String>) get("directoryRead"): new ArrayList<String>();
	}

	public void setReadRights(List<String> readRights) {
		put("directoryRead", readRights);
	}

	@SuppressWarnings("unchecked")
	public List<String> getWriteRights() {
		return get("directoryWrite") != null?(List<String>) get("directoryWrite"): new ArrayList<String>();
	}

	public void setWriteRights(List<String> writeRights) {
		put("directoryWrite", writeRights);
	}


	public void init(NickiContext context, ContextSearchResult rs) {
		super.init(context, rs);
	}


	public void setReadRight(Directory directory, boolean right) {
		if (right == true) {
			addReadRight(directory);
		} else {
			removeReadRight(directory);
		}
	}
	
	public void setWriteRight(Directory directory, boolean right) {
		if (right == true) {
			addWriteRight(directory);
		} else {
			removeWriteRight(directory);
		}
	}
	
	public boolean hasReadRight(Directory directory) {
		return getReadRights().contains(directory.getPath());
	}

	public boolean hasWriteRight(Directory directory) {
		return getWriteRights().contains(directory.getPath());
	}
	
	public void addReadRight(Directory directory) {
		List<String> list = getReadRights();
		if (!list.contains(directory.getPath())) {
			list.add(directory.getPath());
			setReadRights(list);
		}
	}

	public void removeReadRight(Directory directory) {
		List<String> list = getReadRights();
		if (list.contains(directory.getPath())) {
			list.remove(directory.getPath());
			setReadRights(list);
		}
	}

	public void addWriteRight(Directory directory) {
		List<String> list = getWriteRights();
		if (!list.contains(directory.getPath())) {
			list.add(directory.getPath());
			setWriteRights(list);
		}
	}

	public void removeWriteRight(Directory directory) {
		List<String> list = getWriteRights();
		if (list.contains(directory.getPath())) {
			list.remove(directory.getPath());
			setWriteRights(list);
		}
	}
	
}
