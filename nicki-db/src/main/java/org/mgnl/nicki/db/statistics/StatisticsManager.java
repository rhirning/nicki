package org.mgnl.nicki.db.statistics;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 Ralf Hirning
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.HashMap;
import java.util.Map;

import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.util.Classes;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatisticsManager {
	private static StatisticsManager instance;
	private Map<String, Map<String, Statistics>> statisticsMap;

	public static boolean isAvailable(String businessCategory, String statisticName) {
		return getInstance().isStatisticsAvailable(businessCategory, statisticName);
	}

	public static StatisticsResult getStatistics(String businessCategory, String statisticName, Map<String, String> input)
			throws InvalidStatisticsException, MissingDataException, StatisticsException {
		return getInstance().executeStatistics(businessCategory, statisticName, input);
	}
	
	private StatisticsResult executeStatistics(String businessCategory, String statisticName, Map<String, String> input) throws InvalidStatisticsException, StatisticsException, MissingDataException {
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
		String factoryClass = Config.getString("nicki.db.statistics.factory", "org.mgnl.nicki.db.statistics.ClasspathStatisticsFactory");
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
			log.error("Error loading StatisticsFactory", e);
		}
	}

	private boolean isStatisticsAvailable(String businessCategory, String statisticName) {
		return statisticsMap.containsKey(businessCategory) && statisticsMap.get(businessCategory).containsKey(statisticName);
	}

}
