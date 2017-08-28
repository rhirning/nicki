package org.mgnl.nicki.shop.renderer;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mgnl.nicki.shop.base.objects.CatalogArticle;
import org.mgnl.nicki.shop.base.objects.CatalogValueProvider;

public class FreeTextProvider implements CatalogValueProvider{

	@Override
	public Map<String, String> getEntries() {
		return null;
	}

	@Override
	public boolean checkEntry(String entry) {
		return StringUtils.isNotBlank(entry);
	}

	@Override
	public boolean isOnlyDefinedEntries() {
		return false;
	}

	@Override
	public void init(CatalogArticle article) {
	}

	@Override
	public TYPE getType() {
		return TYPE.TEXT_AREA;
	}

}
