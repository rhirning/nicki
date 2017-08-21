package org.mgnl.nicki.shop.base.objects;

import java.util.List;

import org.mgnl.nicki.core.objects.BaseDynamicObject;
import org.mgnl.nicki.core.visitor.Visitor;
import org.mgnl.nicki.shop.base.visitor.CatalogVisitor;


public abstract class CatalogObject extends BaseDynamicObject {
	private static final long serialVersionUID = 1244940179441748075L;

	public Visitor.ACTION accept(CatalogVisitor visitor) {
		if (visitor.visit(this) == Visitor.ACTION.FINISH) {
			return Visitor.ACTION.FINISH;
		} else {
			if (getChildList() != null) {
				for (CatalogObject child : getChildList()) {
					if (child.accept(visitor) == Visitor.ACTION.FINISH) {
						return Visitor.ACTION.FINISH;
					}
				}
			}
			return Visitor.ACTION.CONTINUE;
		}
		
	}

	public abstract List<? extends CatalogObject> getChildList();

}
