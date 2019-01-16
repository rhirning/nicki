package org.mgnl.nicki.db.dynamic.objects;

import org.mgnl.nicki.core.helper.AttributeMapper;

public class NullAttributeMapper implements AttributeMapper {

	@Override
	public String toExternal(String internal) {
		return internal;
	}

	@Override
	public String toInternal(String external) {
		return external;
	}

	@Override
	public boolean hasExternal(String external) {
		return true;
	}

	@Override
	public boolean hasInternal(String internal) {
		return true;
	}

	@Override
	public boolean isStrict() {
		return true;
	}

	@Override
	public boolean isHiddenInternal(String internal) {
		return false;
	}

	@Override
	public boolean isHiddenExternal(String external) {
		return false;
	}

	@Override
	public String correctValue(String key, String value) {
		return value;
	}

}
