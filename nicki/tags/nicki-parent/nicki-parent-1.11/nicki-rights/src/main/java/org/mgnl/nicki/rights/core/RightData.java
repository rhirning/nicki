package org.mgnl.nicki.rights.core;

import java.io.Serializable;

import org.jdom.Element;

@SuppressWarnings("serial")
public class RightData extends Right implements Serializable{

	public RightData(Element dataElement) throws ClassNotFoundException {
		super(dataElement);
	}

}
