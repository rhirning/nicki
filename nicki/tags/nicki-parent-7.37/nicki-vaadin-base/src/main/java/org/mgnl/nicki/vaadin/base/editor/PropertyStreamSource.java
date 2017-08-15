package org.mgnl.nicki.vaadin.base.editor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.mgnl.nicki.core.objects.DynamicObject;

import com.vaadin.server.StreamResource.StreamSource;

public class PropertyStreamSource implements StreamSource {
	private static final long serialVersionUID = -1066014939890504768L;
	private DynamicObject dynamicObject;
	private String attributeName;
	public PropertyStreamSource(DynamicObject dynamicObject, String attributeName) {
		this.dynamicObject = dynamicObject;
		this.attributeName = attributeName;
	}

	@Override
	public InputStream getStream() {
		Object o = dynamicObject.get(attributeName);
		//String oc = o.getClass().getName();
		return new ByteArrayInputStream((byte[]) o);
	}

}
