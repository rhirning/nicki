package org.mgnl.nicki.vaadin.base.components;

public interface NickiProgress {

	void progressed(int newCurrent, String newDetails);

	void finish();

	void init(ProgressRunner progressRunner);

}
