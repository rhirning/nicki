package org.mgnl.nicki.dynamic.objects.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;
import org.mgnl.nicki.ldap.objects.DynamicObjectException;

@SuppressWarnings("serial")
public class Member extends DynamicObject implements Serializable{
	public enum RIGHT {NONE, READ, WRITE};

	@Override
	public void initDataModel() {
		addObjectClass("nickiProjectMember");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute(Person.class, "member", "nickiProjectPerson", String.class,
				Config.getProperty("nicki.users.basedn"));
		dynAttribute.setForeignKey(Person.class);
		dynAttribute.setMandatory();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryRead", "nickiProjectDirectoryRead", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Directory.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("directoryWrite", "nickiProjectDirectoryWrite", String.class);
		dynAttribute.setMultiple();
		dynAttribute.setForeignKey(Directory.class);
		addAttribute(dynAttribute);

	}

	@Override
	public String getDisplayName() {
		return getForeignKeyObject(Person.class, "member").getDisplayName();
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
	
	public void setRight(Directory directory, RIGHT right) {
		if (right == RIGHT.READ) {
			addReadRight(directory);
		} else if (right == RIGHT.WRITE) {
				addWriteRight(directory);
		} else {
			removeRights(directory);
		}
	}
	
	public void setReadRight(Directory directory) {
		addReadRight(directory);
		removeWriteRight(directory);
	}

	public void removeRights(Directory directory) {
		removeReadRight(directory);
		removeWriteRight(directory);
	}

	public void setWriteRight(Directory directory) {
		addWriteRight(directory);
		removeReadRight(directory);
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
	
	@Override
	public void delete() throws DynamicObjectException {
		setReadRights(null);
		setWriteRights(null);

		update();

		super.delete();
	}


	
}
