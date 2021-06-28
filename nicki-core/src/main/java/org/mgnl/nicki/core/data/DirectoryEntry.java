
package org.mgnl.nicki.core.data;

/*-
 * #%L
 * nicki-core
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


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class DirectoryEntry extends FileEntry implements TreeData {
	private static final long serialVersionUID = 501515157067795051L;
	private List<TreeData> children;

	public DirectoryEntry(String path) {
		super(path);
	}

	public DirectoryEntry(File file) {
		super(file);
	}

	@Override
	public List<TreeData> getAllChildren() {
		if (children == null) {
			log.debug("getAllChildren of " + this);
			children = new ArrayList<>();
			if (this.getFile().listFiles() != null) {
				for (File child : this.getFile().listFiles()) {
					if (child.isDirectory()) {
						FileEntry dir = new DirectoryEntry(child);
						children.add(dir);
						log.debug("child directory found: " + dir);
					} else {
						log.debug("non directory child found: " + child.getName());
					}
				}
			}
		}
		return children.size() > 0 ? children : null;
	}


}
