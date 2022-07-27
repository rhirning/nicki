package org.mgnl.nicki.db.verify;

public interface BeanVerifier {
	void verify(Object bean) throws BeanVerifyError;
}
