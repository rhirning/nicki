package org.mgnl.nicki.core.i18n;

public class TopicCache {
	TopicCacheConfiguration topicCacheConfiguration;

	public TopicCache(TopicCacheConfiguration topicCacheConfiguration) {
		this.topicCacheConfiguration = topicCacheConfiguration;
	}

	public String getTopic() {
		return topicCacheConfiguration.getTopic();
	}

	public String getTranslatedText(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
