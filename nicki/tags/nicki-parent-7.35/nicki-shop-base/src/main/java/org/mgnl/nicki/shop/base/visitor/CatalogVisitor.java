package org.mgnl.nicki.shop.base.visitor;

import org.mgnl.nicki.core.visitor.Visitor;
import org.mgnl.nicki.shop.base.objects.Catalog;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogObject;
import org.mgnl.nicki.shop.base.objects.CatalogPage;

public interface CatalogVisitor extends Visitor {

	Visitor.ACTION visit(CatalogObject catalogObject); 
	Visitor.ACTION visit(Catalog catalog); 
	Visitor.ACTION visit(CatalogPage catalogPage);
	Visitor.ACTION visit(CatalogArticle catalogArticle); 
}
