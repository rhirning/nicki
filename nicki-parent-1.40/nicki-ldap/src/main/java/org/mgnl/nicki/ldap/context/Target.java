package org.mgnl.nicki.ldap.context;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.ldap.auth.InvalidPrincipalException;
import org.mgnl.nicki.ldap.auth.NickiPrincipal;
import org.mgnl.nicki.ldap.context.NickiContext;
import org.mgnl.nicki.ldap.context.NickiContext.READONLY;
import org.mgnl.nicki.ldap.objects.DynamicObject;

@SuppressWarnings("serial")
public class Target implements Serializable {

	private String targetName;
	private String propertyBase;
	private List<DynamicObject> dynamicObjects = null;

	public Target(String targetName, String propertyBase) {
		this.targetName = targetName;
		this.propertyBase = propertyBase;
	}

	public void setDynamicObjects(List<DynamicObject> initDynamicObjects) {
		dynamicObjects = initDynamicObjects;
		
	}

	public List<DynamicObject> getDynamicObjects() {
		return dynamicObjects;
	}
	
	public String getProperty(String appendix) {
		return Config.getProperty(propertyBase + "." + appendix);
	}

	public ObjectFactory getObjectFactory(NickiContext context) {
		return new TargetObjectFactory(context, this);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Target [").append(targetName).append("]:");
		for (Iterator<DynamicObject> iterator = dynamicObjects.iterator(); iterator.hasNext();) {
			sb.append(" ");
			sb.append(iterator.next().getClass().getSimpleName());
		}
		return sb.toString();
	}

	public DynamicObject login(NickiPrincipal principal) {
		try {
			return new SystemContext(this, null, READONLY.TRUE).login(principal.getName(), principal.getPassword());
		} catch (InvalidPrincipalException e) {
			return null;
		}
	}

	public NickiContext getNamedUserContext(DynamicObject user, String password) throws InvalidPrincipalException {
		return new NamedUserContext(this, user, password);
	}

	public NickiContext getGuestContext() {
		return new GuestContext(this, READONLY.TRUE);
	}

	public String getName() {
		return targetName;
	}
}
