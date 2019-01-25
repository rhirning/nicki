package org.mgnl.nicki.db.statistics;

import java.util.List;
import java.util.Map;

public interface StatisticsResult {
	
	Map<String, String> getResult();
	List<Map<String, String>> getResultList();
}
