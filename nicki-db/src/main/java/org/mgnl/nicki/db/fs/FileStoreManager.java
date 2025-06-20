package org.mgnl.nicki.db.fs;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.mgnl.nicki.core.config.Config;
import org.mgnl.nicki.db.context.DBContext;
import org.mgnl.nicki.db.context.DBContextManager;
import org.mgnl.nicki.db.context.NotInTransactionException;
import org.mgnl.nicki.db.context.NotSupportedException;
import org.mgnl.nicki.db.profile.InitProfileException;

import lombok.extern.slf4j.Slf4j;


/**
 * The Class FileStoreManager.
 */
@Slf4j
public class FileStoreManager {
	
	/** The context. */
	private static String context = Config.getString("pnw.db.context.fileStore");
	
	/**
	 * Persist to DB.
	 *
	 * @param fileNames the file names
	 * @throws FileStoreException the file store exception
	 */
	public static void persistToDB(List<String> fileNames) throws FileStoreException {

		log.info("persist files to DB");
		try (DBContext dbContext = DBContextManager.getContext(context)) {
			dbContext.beginTransaction();
			Date now = new Date();
			for (String fileName : fileNames) {
				InputStream inputStream = FileStoreManager.class.getResourceAsStream(fileName);
				if (inputStream != null) {
					String data = IOUtils.toString(inputStream, "UTF-8");
					persist(dbContext, fileName, data, now);
				} else {
					log.error("Missing file: " + fileName);
					return;
				}
			}
			dbContext.commit();
		} catch (SQLException | InitProfileException | NotSupportedException | InstantiationException | IllegalAccessException | NotInTransactionException | IOException e) {
			throw new FileStoreException(e);
		}
	}

	/**
	 * Persist.
	 *
	 * @param dbContext the db context
	 * @param pathInfo the path info
	 * @param data the data
	 * @param now the now
	 * @throws SQLException the SQL exception
	 * @throws InitProfileException the init profile exception
	 * @throws NotSupportedException the not supported exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	private static void persist(DBContext dbContext, String pathInfo, String data, Date now) throws SQLException, InitProfileException, NotSupportedException, InstantiationException, IllegalAccessException {
		FileStore fileStore = new FileStore();
		fileStore.setName(pathInfo);
		
		if (dbContext.exists(fileStore)) {
			log.debug("update " + pathInfo);
			fileStore = dbContext.loadObject(fileStore, false);
			fileStore.setData(data);
			fileStore.setModifyTime(now);
			dbContext.update(fileStore, "data", "modifyTime");
		} else {
			log.debug("create " + pathInfo);
			fileStore.setData(data);
			fileStore.setModifyTime(now);
			dbContext.create(fileStore);			
		}
	}

	/**
	 * Gets the as stream.
	 *
	 * @param path the path
	 * @return the as stream
	 * @throws FileStoreException the file store exception
	 */
	public static InputStream getAsStream(String path) throws FileStoreException {
		FileStore fileStore = new FileStore();
		fileStore.setName(path);
		try (DBContext dbContext = DBContextManager.getContext(context)) {
			fileStore = dbContext.loadObject(fileStore, false);
			return new ByteArrayInputStream(fileStore.getData().getBytes("UTF-8"));
		} catch (Exception e) {
			log.error("Error reading file from FIleStore", e);
			throw new FileStoreException(e);
		}

	}

}
