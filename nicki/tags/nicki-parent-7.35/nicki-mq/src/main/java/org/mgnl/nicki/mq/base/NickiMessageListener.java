package org.mgnl.nicki.mq.base;

import javax.jms.MessageListener;

import org.mgnl.nicki.mq.model.Consumer;

public interface NickiMessageListener extends MessageListener {

	void setConsumer(Consumer consumer);
}
