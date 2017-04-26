package org.mgnl.nicki.core.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mgnl.nicki.core.helper.PathHelper;
import org.mgnl.nicki.core.objects.DynamicObjectException;

public class FileEntry implements TreeData {
	private String path;
	private File file;

	public FileEntry() {
	}

	public FileEntry(String path) {
		this.path = path;
		this.file = new File(path);
	}

	public FileEntry(File file) {
		this.file = file;
		this.path = file.getPath();
	}

	@Override
	public String getDisplayName() {
		return file.getName();
	}

	@Override
	public boolean childrenAllowed() {
		return file.isDirectory();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public void unLoadChildren() {
	}

	@Override
	public List<TreeData> getAllChildren() {
		List<TreeData> children = new ArrayList<>();
		if (this.file.listFiles() != null) {
			for (File child : this.file.listFiles()) {
				if (child.isDirectory()) {
					children.add(new FileEntry(child));
				}
			}
		}
		return children;
	}

	@Override
	public String getSlashPath(TreeData root) {
		return getSlashPath(root.getPath());
	}

	@Override
	public String getSlashPath(String parentPath) {
		return PathHelper.getSlashPath(parentPath, getPath());
	}

	@Override
	public String getPath() {
		return this.path;
	}

	public File getFile() {
		return file;
	}

	@Override
	public boolean isModified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void delete() throws InvalidActionException {
		if (file.listFiles() != null && file.listFiles().length > 0) {
			throw new InvalidActionException("Directory is not empty");
		} else {
			file.delete();
		}
	}

	@Override
	public void renameObject(String name) {
		File destFile = new File(file.getParent() + "/" + name);
		file.renameTo(destFile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends TreeData> T createChild(Class<T> classDefinition, String name) throws InvalidActionException {
		File destFile = new File(path + "/" + name);
		destFile.mkdir();
		return (T) new FileEntry(destFile);
	}

	@Override
	public String getChildPath(TreeData parent, TreeData child) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void moveTo(String newPath) throws DynamicObjectException {
		File destFile = new File(newPath);
		file.renameTo(destFile);
	}

}
