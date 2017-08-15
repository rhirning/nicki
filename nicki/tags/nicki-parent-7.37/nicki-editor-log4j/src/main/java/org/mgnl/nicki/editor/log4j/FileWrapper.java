package org.mgnl.nicki.editor.log4j;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FileWrapper {
	private File file;

	public FileWrapper(File file) {
		super();
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	
	public String getPath() throws IOException {
		return file.getCanonicalPath();
	}
	
	public Date getLastModified() {
		return new Date(file.lastModified());
	}
	
	public String getName() {
		return file.getName();
	}
	
	public long getSize() {
		return file.length();
	}

	public String getMod() {
		StringBuilder sb = new StringBuilder();
		sb.append(file.canRead() ? "r" : "-");
		sb.append(file.canWrite() ? "w" : "-");
		sb.append(file.canExecute() ? "x" : "-");
		return sb.toString();
	}

}
