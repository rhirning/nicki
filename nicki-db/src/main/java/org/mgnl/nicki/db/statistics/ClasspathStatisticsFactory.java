
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


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.core.helper.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClasspathStatisticsFactory implements StatisticsFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ClasspathStatisticsFactory.class);
	private static final String propertyKey = "nicki.db.statistics.classpath";

	@Override
	public Collection<Statistics> load() {
		Collection<Statistics> collection = new ArrayList<>();
		String path = Config.getProperty(propertyKey, "/META-INF/nicki/statistics.json");
		JsonArray array = null;
		try {
			array = buildFromResource(path);
		} catch (IOException e) {
			LOG.error("Could not load statistics from classpath", e);
		}
		if (array != null) {
			for (JsonValue jsonValue : array) {
				JsonObject jsonObject = (JsonObject) jsonValue;
				try {
					collection.add(JsonHelper.toBean(Statistics.class, jsonObject));
				} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
					LOG.error("Could not load statistics from classpath", e);
				}
			}
		}
		return collection;
	}

	
	private JsonArray buildFromResource(String path) throws IOException {
		InputStream inputStream = getClass().getResourceAsStream(path);
		JsonReader reader = Json.createReader(new InputStreamReader(inputStream));
		return reader.readArray();
	}
}
