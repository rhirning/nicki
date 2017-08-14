package org.mgnl.nicki.core.context;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;

public interface ContextFactory {
	
	NickiContext getGuestContext(Target target);
	
	NickiContext getNamedUserContext(Target target, DynamicObject user, String password) throws InvalidPrincipalException;

	NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException;
	
	DynamicObjectAdapter getAdapter();

}
