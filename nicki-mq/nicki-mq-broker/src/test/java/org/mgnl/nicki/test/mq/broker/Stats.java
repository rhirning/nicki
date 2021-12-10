package org.mgnl.nicki.test.mq.broker;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.mgnl.nicki.core.config.Config;

public class Stats {
	public static void main(String[] args) throws Exception {
		int port = Config.getInteger("nicki.mq.broker.jmx.port", 1099);
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
		JMXConnector connector = JMXConnectorFactory.connect(url, null);
		connector.connect();
		MBeanServerConnection connection = connector.getMBeanServerConnection();
		String domain = Config.getString("nicki.mq.broker.jmx.domain", "DAM-Broker");
		ObjectName name = new ObjectName(domain + ":BrokerName=localhost,Type=Broker");
		BrokerViewMBean mbean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name,
				BrokerViewMBean.class, true);
		System.out.println("Statistics for broker " + mbean.getBrokerId() + " - " + mbean.getBrokerName());
		System.out.println("\n-----------------\n");
		System.out.println("Total message count: " + mbean.getTotalMessageCount() + "\n");
		System.out.println("Total number of consumers: " + mbean.getTotalConsumerCount());
		System.out.println("Total number of Queues: " + mbean.getQueues().length);
		for (ObjectName queueName : mbean.getQueues()) {
			QueueViewMBean queueMbean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection,
					queueName, QueueViewMBean.class, true);
			System.out.println("\n-----------------\n");
			System.out.println("Statistics for queue " + queueMbean.getName());
			System.out.println("Size: " + queueMbean.getQueueSize());
			System.out.println("Number of consumers: " + queueMbean.getConsumerCount());
		}
	}
}
