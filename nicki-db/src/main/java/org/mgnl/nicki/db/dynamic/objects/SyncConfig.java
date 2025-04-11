
package org.mgnl.nicki.db.dynamic.objects;

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


import org.mgnl.nicki.core.helper.AttributeMapper;

import lombok.Data;

// TODO: Auto-generated Javadoc
/**
 * The Class SyncConfig.
 */
@Data
public class SyncConfig {
	
	/** The context. */
	private String context;
	
	/** The entry type. */
	private String entryType;
	
	/** The entry class. */
	private Class<? extends SyncEntry> entryClass = DefaultSyncEntry.class;
	
	/** The id generator. */
	private SyncIdGenerator idGenerator;
	
	/** The attribute mapper. */
	private AttributeMapper attributeMapper = new NullAttributeMapper();
	
	/** The history. */
	private boolean history;

	/**
	 * Builder.
	 *
	 * @return the builder
	 */
	public static Builder builder() {
		return new Builder();
	}
	

	/**
	 * The Class Builder.
	 */
	public static final class Builder {

		/** The sync config. */
		private SyncConfig syncConfig;
		
		/**
		 * Instantiates a new builder.
		 */
		Builder() {
			this.syncConfig = new SyncConfig();
		}
		
		/**
		 * Builds the.
		 *
		 * @return the sync config
		 */
		public SyncConfig build() {
			if (this.syncConfig.idGenerator == null) {
				this.syncConfig.idGenerator = new DefaultSyncEntry();
			}
			return this.syncConfig;
		}
		
		/**
		 * Context.
		 *
		 * @param context the context
		 * @return the builder
		 */
		public Builder context(String context) {
			this.syncConfig.context = context;
			return this;
		}
		
		/**
		 * Entry type.
		 *
		 * @param entryType the entry type
		 * @return the builder
		 */
		public Builder entryType(String entryType) {
			this.syncConfig.entryType = entryType;
			return this;
		}
		
		/**
		 * Entry class.
		 *
		 * @param entryClass the entry class
		 * @return the builder
		 */
		public Builder entryClass(Class<? extends SyncEntry> entryClass) {
			this.syncConfig.entryClass = entryClass;
			return this;
		}
		
		/**
		 * Id generator.
		 *
		 * @param idGenerator the id generator
		 * @return the builder
		 */
		public Builder idGenerator(SyncIdGenerator idGenerator) {
			this.syncConfig.idGenerator = idGenerator;
			return this;
		}
		
		/**
		 * Attribute mapper.
		 *
		 * @param attributeMapper the attribute mapper
		 * @return the builder
		 */
		public Builder attributeMapper(AttributeMapper attributeMapper) {
			this.syncConfig.attributeMapper = attributeMapper;
			return this;
		}
		
		/**
		 * History.
		 *
		 * @param history the history
		 * @return the builder
		 */
		public Builder history(boolean history) {
			this.syncConfig.history = history;
			return this;
		}
		
		
	}
}
