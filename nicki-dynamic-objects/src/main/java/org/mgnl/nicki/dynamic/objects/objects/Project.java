package org.mgnl.nicki.dynamic.objects.objects;

import java.io.Serializable;
import java.util.List;

import org.mgnl.nicki.dynamic.objects.reference.ChildReferenceDynamicAttribute;
import org.mgnl.nicki.dynamic.objects.reference.ReferenceDynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicAttribute;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Project extends DynamicObject implements Serializable {
	
	@Override
	public void initDataModel() {
		addObjectClass("nickiProject");
		DynamicAttribute dynAttribute = new DynamicAttribute("name", "cn", String.class);
		dynAttribute.setNaming();
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("projectdirectory", "nickiProjectDirectory", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("email", "nickiProjectEmail", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("emailDomain", "nickiProjectEmailDomain", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("emailVisible", "nickiProjectEmailVisible", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new DynamicAttribute("description", "nickiDescription", String.class);
		addAttribute(dynAttribute);

		dynAttribute = new ReferenceDynamicAttribute("manager", "nickiOwner", String.class,
				"nicki.users.basedn", "(objectClass=Person)");
		dynAttribute.setMandatory();
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		dynAttribute = new ChildReferenceDynamicAttribute("deputy", "nickiDeputy", String.class, "(objectClass=nickiProjectMember)");
		dynAttribute.setForeignKey();
		addAttribute(dynAttribute);

		// TODO
		addChild("directory", "(objectClass=nickiProjectDirectory)");
		addChild("member", "(objectClass=nickiProjectMember)");
	}

	public String getDescription() {
		String description = getAttribute("description");
		return description!=null?description:"";
	}

	public void setDescription(String description) {
		put("description", description);
	}

	public Person getProjectLeader() {
		return (Person) getForeignKeyObject("manager");
	}

	public void setProjectLeader(Person projectLeader) {
		this.put("manager", projectLeader.getPath());
	}

	public Member getDeputyProjectLeader() {
		return (Member) getForeignKeyObject("deputy");
	}

	public void setDeputyProjectLeader(Member deputyProjectLeader) {
		this.put("deputy", deputyProjectLeader.getPath());
	}

	public List<DynamicObject> getMembers() {
		return getChildren("member");
	}

	public List<DynamicObject> getDirectories() {
		return getChildren("directory");
	}

	public void removeMember(Member target) {
		this.getMembers().remove(target);
	}

	public void addDirectory(String name) {
		Directory direcory = new Directory();
		direcory.init(getPath(), name);
		this.getDirectories().add(direcory);
	}
}
