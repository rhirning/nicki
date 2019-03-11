package org.mgnl.nicki.db.fs;

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

@Slf4j
public class FileStoreManager {
	private static String context = Config.getString("pnw.db.context.fileStore");
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
