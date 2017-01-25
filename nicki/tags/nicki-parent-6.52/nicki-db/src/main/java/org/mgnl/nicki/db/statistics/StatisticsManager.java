package org.mgnl.nicki.db.statistics;

import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.util.Classes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsManager {
	private static final Logger LOG = LoggerFactory.getLogger(StatisticsManager.class);
	private static StatisticsManager instance;
	private Map<String, Map<String, Statistics>> statisticsMap;

	public static boolean isAvailable(String businessCategory, String statisticName) {
		return getInstance().isStatisticsAvailable(businessCategory, statisticName);
	}

	public static Map<String, String> getStatistics(String businessCategory, String statisticName, Map<String, String> input)
			throws InvalidStatisticsException, MissingDataException, StatisticsException {
		return getInstance().executeStatistics(businessCategory, statisticName, input);
	}
	
	private Map<String, String> executeStatistics(String businessCategory, String statisticName, Map<String, String> input) throws InvalidStatisticsException, StatisticsException {
		if (!isAvailable(businessCategory, statisticName) ) {
			throw new InvalidStatisticsException(businessCategory + "/" + statisticName);
		}
		Statistics statistics = statisticsMap.get(businessCategory).get(statisticName);
		return statistics.execute(input);
	}

	private static synchronized StatisticsManager getInstance() {
		if (instance == null) {
			instance = new StatisticsManager();
			instance.init();
		}
		return instance;
	}

	private void init() {
		statisticsMap = new HashMap<>();
		String factoryClass = Config.getProperty("nicki.db.statistics.factory", "org.mgnl.nicki.db.statistics.ClasspathStatisticsFactory");
		try {
			StatisticsFactory factory = Classes.newInstance(factoryClass);
			for (Statistics statistics : factory.load()) {
				String category = statistics.getCategory();
				String name = statistics.getName();
				if (!statisticsMap.containsKey(category)) {
					statisticsMap.put(category, new HashMap<String, Statistics>());
				}
				Map<String, Statistics> bcMap = statisticsMap.get(category);
				bcMap.put(name, statistics);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOG.error("Error loading StatisticsFactory", e);
		}
	}

	private boolean isStatisticsAvailable(String businessCategory, String statisticName) {
		return statisticsMap.containsKey(businessCategory) && statisticsMap.get(businessCategory).containsKey(statisticName);
	}

}
