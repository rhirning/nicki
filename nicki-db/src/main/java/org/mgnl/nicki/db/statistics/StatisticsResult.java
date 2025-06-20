package org.mgnl.nicki.db.statistics;

/*-
 * #%L
 * nicki-db
 * %%
 * Copyright (C) 2017 - 2023 Ralf Hirning
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

import java.util.List;
import java.util.Map;


/**
 * The Interface StatisticsResult.
 */
public interface StatisticsResult {
	
	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	Map<String, String> getResult();
	
	/**
	 * Gets the result list.
	 *
	 * @return the result list
	 */
	List<Map<String, String>> getResultList();
}
