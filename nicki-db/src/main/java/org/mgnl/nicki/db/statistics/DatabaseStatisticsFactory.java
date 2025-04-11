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


import java.sql.SQLException;
import java.util.Collection;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating DatabaseStatistics objects.
 */
@Slf4j
public class DatabaseStatisticsFactory implements StatisticsFactory {
	
	/** The Constant context. */
	private static final String context = "nicki.db.statistics.db.context";

	/**
	 * Load.
	 *
	 * @return the collection
	 */
	@Override
	public Collection<Statistics> load() {
		String contextName = Config.getString(context);
		DBContext dbContext = DBContextManager.getContext(contextName);
		Statistics statistics = new Statistics();
		Collection<Statistics> collection = null;
		try {
			collection = dbContext.loadObjects(statistics, false);
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
			log.error("Could not load statistics from database", e);
		}
		return collection;
	}
}
