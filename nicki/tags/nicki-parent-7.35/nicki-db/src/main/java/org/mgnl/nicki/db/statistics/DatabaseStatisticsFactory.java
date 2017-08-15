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
