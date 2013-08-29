package org.mgnl.nicki.core.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ChildFilter implements Serializable {
	private static final long serialVersionUID = -3256064092279768874L;
	private String filter;
	private List<Class<? extends DynamicObject>> objectFilters;
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public List<Class<? extends DynamicObject>> getObjectFilters() {
		return objectFilters;
	}
	public void setObjectFilters(List<Class<? extends DynamicObject>> objectFilters) {
		this.objectFilters = objectFilters;
	}
	public void addObjectFilter(Class<? extends DynamicObject> objectFilter) {
		if (objectFilters == null) {
			objectFilters = new ArrayList<Class<? extends DynamicObject>>();
		}
		objectFilters.add(objectFilter);
	}
	public boolean hasFilter() {
		return StringUtils.isNotBlank(filter);
	}
	public boolean hasObjectFilters() {
		return objectFilters != null && objectFilters.size() > 0;
	}
}
