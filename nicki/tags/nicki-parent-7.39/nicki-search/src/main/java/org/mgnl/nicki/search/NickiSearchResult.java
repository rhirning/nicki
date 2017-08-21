package org.mgnl.nicki.search;

public class NickiSearchResult {
	private String category;
	private String key;
	private String title;
	private String description;
	private float score;


	public NickiSearchResult(SearchDocument doc, float score) {
		this.category = doc.get(NickiSearch.ATTRIBUTE_CATEGORY);
		this.key = doc.get(NickiSearch.ATTRIBUTE_KEY);
		this.description = doc.get(NickiSearch.ATTRIBUTE_DESCRIPTION);
		this.title = doc.get(NickiSearch.ATTRIBUTE_TITLE);
		this.score = score;
	}


	public NickiSearchResult(String category, String key, float score) {
		this.category = category;
		this.key = key;
		this.score = score;
	}


	public String getKey() {
		return key;
	}


	public String getDescription() {
		return description;
	}


	public float getScore() {
		return score;
	}


	public String getTitle() {
		return title;
	}


	public String getCategory() {
		return category;
	}

}
