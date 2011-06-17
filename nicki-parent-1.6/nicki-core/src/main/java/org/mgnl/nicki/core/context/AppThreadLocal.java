package org.mgnl.nicki.core.context;

public class AppThreadLocal extends ThreadLocal<Context> {

	@Override
	protected Context initialValue() {
		return new Context();
	}
	
}
