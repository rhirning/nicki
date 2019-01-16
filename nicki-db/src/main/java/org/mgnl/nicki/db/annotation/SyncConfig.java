
package org.mgnl.nicki.db.annotation;

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
import org.mgnl.nicki.db.dynamic.objects.DefaultSyncEntry;
import org.mgnl.nicki.db.dynamic.objects.NullAttributeMapper;
import org.mgnl.nicki.db.dynamic.objects.SyncEntry;

import lombok.Data;

@Data
public class SyncConfig {
	private String context;
	private String entryType;
	private Class<? extends SyncEntry> entryClass = DefaultSyncEntry.class;
	private AttributeMapper attributeMapper = new NullAttributeMapper();
	private boolean history;

	public static Builder builder() {
		return new Builder();
	}
	

	public static final class Builder {

		private SyncConfig syncConfig;
		
		Builder() {
			this.syncConfig = new SyncConfig();
		}
		
		public SyncConfig build() {
			return this.syncConfig;
		}
		
		public Builder context(String context) {
			this.syncConfig.context = context;
			return this;
		}
		
		public Builder entryType(String entryType) {
			this.syncConfig.entryType = entryType;
			return this;
		}
		
		public Builder entryClass(Class<? extends SyncEntry> entryClass) {
			this.syncConfig.entryClass = entryClass;
			return this;
		}
		
		public Builder attributeMapper(AttributeMapper attributeMapper) {
			this.syncConfig.attributeMapper = attributeMapper;
			return this;
		}
		
		public Builder history(boolean history) {
			this.syncConfig.history = history;
			return this;
		}
		
		
	}
}
