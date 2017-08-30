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
package org.mgnl.nicki.db.statistics;

import java.sql.SQLException;
import java.util.Collection;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.profile.InitProfileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatabaseStatisticsFactory implements StatisticsFactory {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseStatisticsFactory.class);
	private static final String context = "nicki.db.statistics.db.context";

	@Override
	public Collection<Statistics> load() {
		String contextName = Config.getProperty(context);
		DBContext dbContext = DBContextManager.getContext(contextName);
		Statistics statistics = new Statistics();
		Collection<Statistics> collection = null;
		try {
			collection = dbContext.loadObjects(statistics, false);
		} catch (InstantiationException | IllegalAccessException | SQLException | InitProfileException e) {
			LOG.error("Could not load statistics from database", e);
		}
		return collection;
	}
}
