package org.mgnl.nicki.lucene.search;

import org.apache.lucene.document.Document;
import org.mgnl.nicki.search.SearchDocument;

public class LuceneSearchDocument implements SearchDocument {

	private Document doc;
	public LuceneSearchDocument(Document doc) {
		this.doc = doc;
	}

	@Override
	public String get(String attributeKey) {
		return doc.get(attributeKey);
	}

}
