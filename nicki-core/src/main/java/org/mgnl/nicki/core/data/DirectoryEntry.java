
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

public class DirectoryEntry extends FileEntry implements TreeData {
	private static final long serialVersionUID = 501515157067795051L;

	public DirectoryEntry(String path) {
		super(path);
	}

	public DirectoryEntry(File file) {
		super(file);
	}

	@Override
	public List<TreeData> getAllChildren() {
		List<TreeData> files = new ArrayList<>();
		for(File file : getFile().listFiles()) {
			files.add(new DirectoryEntry(file));
		}
		return files;
	}


}
