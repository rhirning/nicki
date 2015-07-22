package org.mgnl.nicki.core.helper;

public interface AttributeMapper {

	public String toExternal(String internal);

	public String toInternal(String external);

	public boolean hasExternal(String external);

	public boolean hasInternal(String internal);
	
	public boolean isStrict();
}
