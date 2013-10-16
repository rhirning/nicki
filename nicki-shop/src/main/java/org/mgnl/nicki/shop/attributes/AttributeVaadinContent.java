package org.mgnl.nicki.shop.attributes;

import org.mgnl.nicki.core.context.NickiContext;

public interface AttributeVaadinContent {

	VaadinComponent getVaadinContent();
	void setContext(NickiContext context);

}
