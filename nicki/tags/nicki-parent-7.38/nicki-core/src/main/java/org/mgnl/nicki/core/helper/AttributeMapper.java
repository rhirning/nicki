package org.mgnl.nicki.core.helper;

public interface AttributeMapper {

	String toExternal(String internal);

	String toInternal(String external);

	boolean hasExternal(String external);

	boolean hasInternal(String internal);
	
	boolean isStrict();

	boolean isHiddenInternal(String internal);

	boolean isHiddenExternal(String external);
}
