package org.mgnl.nicki.jcr.context;

import org.mgnl.nicki.core.auth.InvalidPrincipalException;
import org.mgnl.nicki.core.context.ContextFactory;
import org.mgnl.nicki.core.context.NickiContext;
import org.mgnl.nicki.core.context.Target;
import org.mgnl.nicki.core.objects.DynamicObject;
import org.mgnl.nicki.core.objects.DynamicObjectAdapter;

public class JcrContextFactory implements ContextFactory {

	@Override
	public NickiContext getGuestContext(Target target) {
		return new JcrGuestContext(getAdapter(), target);
	}

	@Override
	public NickiContext getNamedUserContext(Target target, DynamicObject user, String password)
			throws InvalidPrincipalException {
		return new JcrNamedUserContext(getAdapter(), target, user, password);
	}

	@Override
	public NickiContext getSystemContext(Target target, DynamicObject user) throws InvalidPrincipalException {
		return new JcrSystemContext(getAdapter(), target, user);
	}

	@Override
	public DynamicObjectAdapter getAdapter() {
		return null;
	}

}
