package org.mgnl.nicki.core.data;

import java.util.List;

import org.mgnl.nicki.core.objects.DynamicObjectException;

public interface TreeData {

	String getDisplayName();

	boolean childrenAllowed();

	String getName();

	void unLoadChildren();

	List<? extends TreeData> getAllChildren();

	String getSlashPath(TreeData root);
	
	String getSlashPath(String parentPath);

	String getPath();

	boolean isModified();

	void delete() throws DynamicObjectException, InvalidActionException;

	void renameObject(String name) throws DynamicObjectException;

	<T extends TreeData> T createChild(Class<T> classDefinition, String name) throws InvalidActionException;

	String getChildPath(TreeData parent, TreeData child);

	void moveTo(String newPath) throws DynamicObjectException;


}
