package org.mgnl.nicki.core.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryEntry extends FileEntry implements TreeData {

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
