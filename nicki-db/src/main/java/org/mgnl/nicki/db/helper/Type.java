package org.mgnl.nicki.db.helper;

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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mgnl.nicki.db.context.DBContext;


public enum Type {
	STRING(String.class),
	TIMESTAMP(Date.class),
	DATE(Date.class),
	LONG(Long.class, long.class),
	INT(Integer.class, int.class),
	UNKONWN();
		
	private List<Class<?>> classes = new ArrayList<>();
	
	private Type(Class<?> ...classes){
		if (classes != null) {
			for (Class<?> clazz : classes) {
				this.classes.add(clazz);
			}
		}
		
	}

	public boolean match(Class<?> clazz) {
		for (Class<?> c : this.classes) {
			if (c.isAssignableFrom(clazz)) {
				return true;
			}
		}
		return false;
	}
	
	public Type getType(Class<?> clazz) {
		for (Type type : values()) {
			if (match(clazz)) {
				return type;
			}
		}
		return UNKONWN;
	}
	
	public static String getDbString(DBContext dbContext, Type type, Object value) {
		if (type == STRING) {
			return dbContext.getStringAsDbString((String) value);
		} else if (type == DATE) {
			return dbContext.getDateAsDbString((Date) value);
		} else if (type == Type.TIMESTAMP) {
			return dbContext.getTimestampAsDbString((Date) value);
		} else if (type == Type.LONG) {
			return dbContext.getLongAsDbString((Long) value);
		} else if (type == Type.INT) {
			return dbContext.getIntAsDbString( (Integer) value);
		} else {
			return null;
		}
	}


}
