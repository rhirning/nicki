/**
 * Copyright © 2017 Ralf Hirning (ralf@hirning.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mgnl.nicki.lucene.search;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.mgnl.nicki.search.Extractor;
import org.mgnl.nicki.search.NickiSearch;
import org.mgnl.nicki.search.NickiSearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearch<T extends Object> implements NickiSearch<T> {
	private static final Logger LOG = LoggerFactory.getLogger(LuceneSearch.class);


	public static enum MODE {
		CREATE, UPDATE
	}

	private String indexPath = "index";
	private MODE mode = MODE.CREATE;

	public LuceneSearch() {
	}

	public List<NickiSearchResult> search(String searchString, int maxResults) throws IOException {
		List<NickiSearchResult> list = new ArrayList<>();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = getAnalyzer();
		QueryParser parser = new QueryParser(ATTRIBUTE_CONTENT, analyzer);
		parser.setAllowLeadingWildcard(true);
		Query query;
		try {
			query = parser.parse(searchString);
		} catch (ParseException e) {
			throw new IOException(e.getMessage());
		}
		TopDocs results = searcher.search(query, maxResults);

		ScoreDoc[] hits = results.scoreDocs;
		for (ScoreDoc scoreDoc : hits) {
			Document doc = searcher.doc(scoreDoc.doc);
			float score = scoreDoc.score;
			list.add(new NickiSearchResult(new LuceneSearchDocument(doc), score));
		}
		return list;
	}

	private void indexObjects(IndexWriter writer, Collection<T> list, Extractor<T> extractor) {
		if (list != null) {
			for (T t : list) {
				try {
					indexObject(writer, t, extractor);
				} catch (IOException e) {
					LOG.error("Error indexing", e);
				}
			}
		}

	}

	private void indexObject(IndexWriter writer, T object, Extractor<T> extractor) throws IOException {
		if (!extractor.accept(object)) {
			return;
		}
		String category = extractor.getCategory(object);
		String key = extractor.getKey(object);
		String description = extractor.getDescription(object);
		String title = extractor.getTitle(object);
		
		//int hash = title.hashCode();

		// make a new, empty document
		Document doc = new Document();

		Field keyField = new StringField(ATTRIBUTE_KEY, key, Field.Store.YES);
		doc.add(keyField);
		//doc.add(new LongField(ATTRIBUTE_HASH, hash, Field.Store.YES));
		if (StringUtils.isNotBlank(category)) {
			doc.add(new StringField(ATTRIBUTE_CATEGORY, category, Field.Store.YES));
		}
		if (StringUtils.isNotBlank(title)) {
			doc.add(new StringField(ATTRIBUTE_TITLE, title, Field.Store.YES));
		}
		if (StringUtils.isNotBlank(description)) {
			doc.add(new StringField(ATTRIBUTE_DESCRIPTION, description, Field.Store.YES));
		}
		StringBuilder sb = new StringBuilder(key);
		if (StringUtils.isNotBlank(title)) {
			sb.append(" ").append(title);
		}
		if (StringUtils.isNotBlank(description)) {
			sb.append(" ").append(description);
		}

		doc.add(new StringField(ATTRIBUTE_CONTENT, sb.toString(), Field.Store.YES));
		
		if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
			LOG.debug("adding " + key);
			writer.addDocument(doc);
		} else {
			LOG.debug("updating " + key);
			writer.updateDocument(new Term(ATTRIBUTE_KEY, key), doc);
		}

	}

	public void index(Collection<T> list, Extractor<T> extractor) {
		Date start = new Date();
		try {
			LOG.debug("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = getAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (mode == MODE.CREATE) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			try (IndexWriter writer = new IndexWriter(dir, iwc)) {
				indexObjects(writer, list, extractor);

				// NOTE: if you want to maximize search performance,
				// you can optionally call forceMerge here. This can be
				// a terribly costly operation, so generally it's only
				// worth it when your index is relatively static (ie
				// you're done adding documents to it):
				//
				// writer.forceMerge(1);

				Date end = new Date();
				LOG.info(end.getTime() - start.getTime() + " total milliseconds");
			}

		} catch (IOException e) {
			LOG.error("Error indexing", e);
		}
	}

	private Analyzer getAnalyzer() throws IOException {
		return new StandardAnalyzer();
	}

	public MODE getMode() {
		return mode;
	}

	public void setMode(MODE mode) {
		this.mode = mode;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

}
