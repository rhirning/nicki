package org.mgnl.nicki.core.data;

public class TreeObjectWrapper<T extends TreeObject> implements TreeObject {
	private T object;
	
	
	public TreeObjectWrapper(T object) {
		this.object = object;
	}


	@Override
	public String getDisplayName() {
		return object.getDisplayName();
	}
	
	public TreeObject getObject() {
		return object;
	}

}
