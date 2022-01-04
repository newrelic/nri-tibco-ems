package com.newrelic.nri.tibco.ems;

import java.util.ArrayList;
import java.util.List;

import com.tibco.tibjms.admin.BridgeInfo;
import com.tibco.tibjms.admin.ChannelInfo;
// import com.tibco.tibjms.admin.ConnectionInfo;
import com.tibco.tibjms.admin.DetailedDestStat;
import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.RouteInfo;
import com.tibco.tibjms.admin.ServerInfo;
import com.tibco.tibjms.admin.StatData;
import com.tibco.tibjms.admin.State;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.admin.TopicInfo;


import com.newrelic.nri.tibco.ems.metrics.*;

@SuppressWarnings("deprecation")
public class EMSMonitor  {
	
	private EMSServer emsServer;
//	private static final Logger logger = LoggerFactory.getLogger(EMSMonitor.class);
	private static final String TMPSTART = "$TMP";
	private static final String TEMP = "Temp";
	private static final String SYSSTART = "$SYS";
	private static final String SYS = "SYS";
	private boolean collectDetails = true;
	
	private TibjmsAdmin connect(EMSServer m)  {
		TibjmsAdmin tibcoInst = null;
		String user = m.getUsername();
		String host = m.getEMSURL();
		String password = m.getPassword();

		try {
			tibcoInst = new TibjmsAdmin(host, user, password);
		}
		catch (TibjmsAdminException e) {
			Utils.reportError("Failed to connect",e);
		}

		return tibcoInst;
	}

	public EMSMonitor(EMSServer ems) {
		emsServer = ems;
	}

	public void populateMetrics(JSONMetricReporter metricReporter) throws Exception {
		TibjmsAdmin tibjmsServer = connect(emsServer);
		if (tibjmsServer != null) {
				State serverState = tibjmsServer.getStateInfo().getState();
				if( serverState.get() == State.SERVER_STATE_ACTIVE || serverState.get() == State.SERVER_STATE_ACTIVE_REPLICATING  ) {
				getServerInfo(tibjmsServer, metricReporter);
				getChannelStats(tibjmsServer, metricReporter);
				getBridgeStats(tibjmsServer, metricReporter);
				getQueueStats(tibjmsServer, metricReporter);
				getTopicStats(tibjmsServer, metricReporter);
				getRouteStats(tibjmsServer, metricReporter);
			} else {
			    // Report server status only
                // getServerInfo(tibjmsServer, metricReporter);
                String emsServerName = emsServer.getName().trim();
                List<Metric> metricList = new ArrayList<Metric>();
                metricList.add(new AttributeMetric("EMS Server", emsServerName));
                metricList.add(new AttributeMetric("Server State", serverState.toString()));
                metricReporter.report("Server Metrics",StatType.Server, metricList);
			}
			tibjmsServer.close();
		} else {
			Utils.reportError("Not connected to "+emsServer.getName());
		}
	}

	private void addGaugeMetric(List<Metric> metricList,String name, Number n) {
		Number m = null;
		if(n instanceof Long) {
			m = new Float(n.floatValue());
		} else {
			m = n;
		}
		metricList.add(new GaugeMetric(name, m));
	}

	protected void getChannelStats(TibjmsAdmin tibjmsServer,JSONMetricReporter metricReporter) {
		
		try {
			String emsServerName = emsServer.getName().trim();
			ChannelInfo[] channelInfos = tibjmsServer.getChannels();
			List<Metric> metricList = new ArrayList<Metric>();
			
			if(channelInfos != null) {
				for(ChannelInfo c : channelInfos) {
					metricList.clear();
					String channelName = c.getName().trim();
					metricList.add(new AttributeMetric("EMS Server", emsServerName));
					metricList.add(new AttributeMetric("Channel Name", channelName));
					
					addGaugeMetric(metricList,"Backlog Count", c.getBacklogCount());
					addGaugeMetric(metricList,"Buffered Bytes", c.getBufferedBytes());
					metricList.add(new RateMetric("Max Rate",c.getMaxRate()));
					addGaugeMetric(metricList,"Max Rate",c.getMaxTime());
					addGaugeMetric(metricList,"Retransmitted", c.getRetransmittedBytes());
					addGaugeMetric(metricList,"Transmitted", c.getTransmittedBytes());			
					metricList.add(new AttributeMetric("Address", c.getAddress()));
					metricList.add(new AttributeMetric("Interface", c.getInterface()));
					StatData statData = c.getStatistics();
					metricList.add(new RateMetric("Byte Rate", statData.getByteRate()));
					metricList.add(new RateMetric("Message Rate", statData.getMessageRate()));
					addGaugeMetric(metricList,"Total Bytes", statData.getTotalBytes());
					addGaugeMetric(metricList,"Total Messages", statData.getTotalMessages());
					metricReporter.report("Channel Metrics",StatType.Channel, metricList);
					if (collectDetails) {
						DetailedDestStat[] details = c.getDetailedStatistics();
						if (details != null && details.length > 0) {
							for (DetailedDestStat detail : details) {
								metricList.clear();
								String destType = detail.getDestinationType() == 2 ? "Topic" : "Queue";
								metricList.add(new AttributeMetric("EMS Server", emsServerName));
								metricList.add(new AttributeMetric("Destination Type", destType));
								metricList.add(new AttributeMetric("Channel Name", channelName));
								metricList.add(new AttributeMetric("Destination Name", detail.getDestinationName()));

								statData = detail.getStatData();
								if (statData != null) {
									metricList.add(new RateMetric("Byte Rate", statData.getByteRate()));
									metricList.add(new RateMetric("Message Rate", statData.getMessageRate()));
									addGaugeMetric(metricList, "Total Bytes", statData.getTotalBytes());
									addGaugeMetric(metricList, "Total Messages", statData.getTotalMessages());
								}
								StatData inboundStat = detail.getInboundStatData();
								if (inboundStat != null) {
									metricList.add(new RateMetric("Inbound-Byte Rate", inboundStat.getByteRate()));
									metricList.add(new RateMetric("Inbound-Message Rate", inboundStat.getMessageRate()));
									addGaugeMetric(metricList, "Inbound-Total Bytes", inboundStat.getTotalBytes());
									addGaugeMetric(metricList, "Inbound-Total Messages", inboundStat.getTotalMessages());
								}
								StatData outboundStat = detail.getOutboundStatData();
								if (outboundStat != null) {
									metricList.add(new RateMetric("Outbound-Byte Rate", outboundStat.getByteRate()));
									metricList.add(new RateMetric("Outbound-Message Rate", outboundStat.getMessageRate()));
									addGaugeMetric(metricList, "Outbound-Total Bytes", outboundStat.getTotalBytes());
									addGaugeMetric(metricList, "Outbound-Total Messages",outboundStat.getTotalMessages());
								}
								metricList.add(new AttributeMetric("EMS Server", emsServerName));
								metricReporter.report("Channel Detailed Metrics",StatType.ChannelDetails, metricList);
							}
						}
					}
				}
			}
		} catch(Exception e) {
			Utils.reportError("Exception occurred",e);
		}
	}

	protected void getBridgeStats(TibjmsAdmin tibjmsServer,JSONMetricReporter metricReporter) {
		String prefix = "EMS Bridges";
		try {

			String emsServerName = emsServer.getName().trim();
			BridgeInfo[] bridgeList = tibjmsServer.getBridges();
			List<Metric> metricList = new ArrayList<Metric>();

			if (bridgeList != null && bridgeList.length > 0) {
				for (BridgeInfo t : bridgeList) {

					Metric m = new AttributeMetric(t.getName(), t.toString());
					metricList.add(m);						
				}
				metricList.add(new AttributeMetric("EMS Server", emsServerName));
			}
			if(!metricList.isEmpty()) {
				metricReporter.report(prefix,StatType.Bridge, metricList);
			}

		} catch (TibjmsAdminException e) {
			Utils.reportError("TibJMSAdminException occurred",e);
		} 
		
		
	}

	protected void getQueueStats(TibjmsAdmin tibjmsServer,JSONMetricReporter metricReporter) {
		try {

			String emsServerName = emsServer.getName().trim();
			QueueInfo[] queueList = tibjmsServer.getQueues();

			List<String> ignores = emsServer.getQueueIgnores();

			long totalDelivered = 0;
			int totalConsumers = 0;
			int totalReceivers = 0;
			long totalInTransit = 0;
			long totalPendingCount = 0;
			long totalPendingSize = 0;
			long totalPersistentCount = 0;
			long totalPersistendSize = 0;
			int totalPrefetch = 0;
			int queues = 0;
			List<Metric> metricList = new ArrayList<Metric>();

			if (queueList != null) 
				for (QueueInfo q : queueList) {
					boolean skip = false;
					String qName = q.getName();
					if (q.isTemporary() && !emsServer.getFlagIncludeDynamicQueues()) skip = true;
					
					for(int i=0;i<ignores.size() && !skip;i++) {
						String ignore = ignores.get(i);
						if(qName.matches(ignore)) {
							skip = true;
							break;
						}

					}

					if(qName.toUpperCase().startsWith(TMPSTART)) qName = TEMP;
					if(qName.toUpperCase().startsWith(SYSSTART)) qName = SYS;
					
					if (!skip) {
						queues++;
						metricList.clear();
						String queueName = qName.trim();
						metricList.add(new AttributeMetric("Queue Name", qName));
						if(q.getStore() != null) {
							metricList.add(new AttributeMetric(queueName+"/Store", q.getStore()));
						}
						long deliveredCount = q.getDeliveredMessageCount();
						addGaugeMetric(metricList,"Delivered Count", deliveredCount);
						totalDelivered += deliveredCount;
						int consumerCount = q.getConsumerCount();
						addGaugeMetric(metricList,"Consumer Count", consumerCount);
						totalConsumers += consumerCount;
						int receiverCount = q.getReceiverCount();
						addGaugeMetric(metricList,"Receiver Count", receiverCount);
						totalReceivers += receiverCount;
						long inTransitCount = q.getInTransitMessageCount();
						addGaugeMetric(metricList,"InTransit Count", inTransitCount);
						totalInTransit += inTransitCount;

						addGaugeMetric(metricList,"Configuration/Max Redelivery Count", q.getMaxRedelivery());
						addGaugeMetric(metricList,"Configuration/Max Bytes", q.getMaxBytes());
						addGaugeMetric(metricList,"Configuration/Max Messages", q.getMaxMsgs());
						long pendingMessageCount = q.getPendingMessageCount();
						addGaugeMetric(metricList,"Pending Message Count", pendingMessageCount);
						totalPendingCount += pendingMessageCount;
						long pendingMessageSize = q.getPendingMessageSize();
						addGaugeMetric(metricList,"Pending Message Total", pendingMessageSize);
						totalPendingSize += pendingMessageSize;
						long persisentMessageCount = q.getPendingPersistentMessageCount();
						addGaugeMetric(metricList,"Pending Persistent Count", persisentMessageCount);
						totalPersistentCount += persisentMessageCount;
						long persistentMessageSize = q.getPendingPersistentMessageSize();
						addGaugeMetric(metricList,"Pending Persistent Total", persistentMessageSize);
						totalPersistendSize += persistentMessageSize;
						int prefetch = q.getPrefetch();
						addGaugeMetric(metricList,"Configuration/Prefetch Total", prefetch);
						totalPrefetch += prefetch;
						
						if(q.getInboundStatistics() != null) {

							metricList.add(new RateMetric("Inbound-Byte Rate", q.getInboundStatistics().getByteRate()));
							metricList.add(new RateMetric("Inbound-Message Rate", q.getInboundStatistics().getMessageRate()));
							metricList.add(new RateMetric("Inbound-Total Bytes", q.getInboundStatistics().getTotalBytes()));
							metricList.add(new RateMetric("Inbound-Total Messages", q.getInboundStatistics().getTotalMessages()));
						}

						if(q.getOutboundStatistics() != null) {
	 						metricList.add(new RateMetric("Outbound-Byte Rate", q.getOutboundStatistics().getByteRate()));
							metricList.add(new RateMetric("Outbound-Message Rate", q.getOutboundStatistics().getMessageRate()));
							addGaugeMetric(metricList,"Outbound-Total Bytes",  q.getOutboundStatistics().getTotalBytes());
							addGaugeMetric(metricList,"Outbound-Total Messages",q.getOutboundStatistics().getTotalMessages());
						}
						
						if(q.isRouted()) {
							String routeName = q.getRouteName();
							if(routeName != null && !routeName.isEmpty()) {
								metricList.add(new AttributeMetric("Route Name", routeName));
							}
						}

						String statString = q.statString();
						if(statString != null && !statString.isEmpty()) {
							metricList.add(new AttributeMetric("Stat String", statString));
						}
						if(!metricList.isEmpty()) {
							metricList.add(new AttributeMetric("EMS Server", emsServerName));
							metricReporter.report("Queue Metrics",StatType.Queue, metricList);
						}

					}

				}
			//			reportMetric(prefix + "/Critical Capacity", "queues", number_critical);
			//			reportMetric(prefix + "/Danger Capacity", "queues", number_warning);
			if(queues > 0) {
				metricList.clear();
				addGaugeMetric(metricList,"Total Consumers", totalConsumers);
				addGaugeMetric(metricList,"Total Pending Count", totalPendingCount);
				addGaugeMetric(metricList,"Total Pending Size", totalPendingSize);
				addGaugeMetric(metricList,"Total Pending Persistent Count", totalPersistentCount);
				addGaugeMetric(metricList,"Total Pending Persistent Size", totalPersistendSize);
				addGaugeMetric(metricList,"Total Prefetch", totalPrefetch);
				addGaugeMetric(metricList,"Total Delieverd", totalDelivered);
				addGaugeMetric(metricList,"Total Receivers", totalReceivers);
				addGaugeMetric(metricList,"Total In Transit", totalInTransit);
				metricList.add(new AttributeMetric("EMS Server", emsServerName));
				metricReporter.report("Queue Totals",StatType.QueueTotals, metricList);
			}

		} catch (TibjmsAdminException e) {
			Utils.reportError("TibJMSAdminException occurred",e);
		} 

	}

	protected void getTopicStats(TibjmsAdmin tibjmsServer,JSONMetricReporter metricReporter) {
		try {

			String emsServerName = emsServer.getName().trim();
			TopicInfo[] topicList = tibjmsServer.getTopics();

			List<String> ignores = emsServer.getTopicIgnores();
			List<Metric> metricList = new ArrayList<Metric>();
			metricList.add(new AttributeMetric("EMS Server", emsServerName));

			int totalConsumers = 0;
			long totalPendingCount = 0;
			long totalPendingSize = 0;
			long totalPersistentCount = 0;
			long totalPersistendSize = 0;
			int totalPrefetch = 0;
			int totalActiveDurable = 0;
//			int totalDurableSubscriptions = 0;
			int totalSubscriberCount = 0;
			int totalSubscriptionCount = 0;
			int topics = 0;

			if (topicList != null)  {
				for (TopicInfo t : topicList) {
					boolean skip = false;
					String tName = t.getName();
					if (t.isTemporary() && !emsServer.getFlagIncludeDynamicTopics()) skip = true;
					for(int i=0;i<ignores.size();i++) {
						String ignore = ignores.get(i);
						if(tName.matches(ignore)) {
							skip = true;
							break;
						}

					}
					if(tName.toUpperCase().startsWith(TMPSTART)) tName = TEMP;
					if(tName.toUpperCase().startsWith(SYSSTART)) tName = SYS;

					if (!skip) {
						metricList.clear();
						topics++;
						String topicName = tName.trim();
						metricList.add(new AttributeMetric("Topic", topicName));
						
						if(t.getChannel() != null) {
							metricList.add(new AttributeMetric("Channel", t.getChannel()));
						}

						int consumerCount = t.getConsumerCount();
						addGaugeMetric(metricList,"Consumer Count",  consumerCount);
						totalConsumers += consumerCount;
						addGaugeMetric(metricList,"Configuration/Max Bytes", t.getMaxBytes());
						addGaugeMetric(metricList,"Configuration/Max Messages", t.getMaxMsgs());
						long pendingMessageCount = t.getPendingMessageCount();
						addGaugeMetric(metricList,"Pending Message Count", pendingMessageCount);
						totalPendingCount += pendingMessageCount;
						long pendingMessageSize = t.getPendingMessageSize();
						addGaugeMetric(metricList,"Pending Message Total", pendingMessageSize);
						totalPendingSize += pendingMessageSize;
						long persisentMessageCount = t.getPendingPersistentMessageCount();
						addGaugeMetric(metricList,"Pending Persistent Count", persisentMessageCount);
						totalPersistentCount += persisentMessageCount;
						long persistentMessageSize = t.getPendingPersistentMessageSize();
						addGaugeMetric(metricList,"Pending Persistent Total", persistentMessageSize);
						totalPersistendSize += persistentMessageSize;
						int prefetch = t.getPrefetch();
						addGaugeMetric(metricList,"Configuration/Prefetch Total",  prefetch);
						totalPrefetch += prefetch;

						int activeDurableCount = t.getActiveDurableCount();
						addGaugeMetric(metricList,"Active Durable Count", activeDurableCount);
						totalActiveDurable += activeDurableCount;

//						int durableSubscriptionCount = t.getDurableSubscriptionCount();
//						addGaugeMetric(metricList,"Durable Subscription Count", durableSubscriptionCount));
//						totalDurableSubscriptions += durableSubscriptionCount;
						int subscriberCount = t.getSubscriberCount();
						addGaugeMetric(metricList,"Subscriber Count", subscriberCount);
						totalSubscriberCount += subscriberCount;
//						int subscriptionCount = t.getSubscriptionCount();
//						addGaugeMetric(metricList,"Subscription Count", subscriptionCount));
//						totalSubscriptionCount += subscriptionCount;
						if(t.getInboundStatistics() != null) {
							metricList.add(new RateMetric("Inbound-Byte Rate", t.getInboundStatistics().getByteRate()));
							metricList.add(new RateMetric("Inbound-Message Rate", t.getInboundStatistics().getMessageRate()));
							addGaugeMetric(metricList,"Inbound-Total Bytes", t.getInboundStatistics().getTotalBytes());
							addGaugeMetric(metricList,"Inbound-Total Messages", t.getInboundStatistics().getTotalMessages());
						}

						if(t.getOutboundStatistics() != null) {
							metricList.add(new RateMetric("Outbound-Byte Rate", t.getOutboundStatistics().getByteRate()));
							metricList.add(new RateMetric("Outbound-Message Rate", t.getOutboundStatistics().getMessageRate()));
							addGaugeMetric(metricList,"Outbound-Total Bytes", t.getOutboundStatistics().getTotalBytes());
							addGaugeMetric(metricList,"Outbound-Total Messages", t.getOutboundStatistics().getTotalMessages());
						}
						metricList.add(new AttributeMetric("EMS Server", emsServerName));
						metricReporter.report("Topic Metrics",StatType.Topic, metricList);
					}

				}
			}
			if(topics > 0) {
				metricList.clear();
				addGaugeMetric(metricList,"Total Consumers", totalConsumers);
				addGaugeMetric(metricList,"Total Pending Count", totalPendingCount);
				addGaugeMetric(metricList,"Total Pending Size", totalPendingSize);
				addGaugeMetric(metricList,"Total Pending Persistent Count", totalPersistentCount);
				addGaugeMetric(metricList,"Total Pending Persistent Size", totalPersistendSize);
				addGaugeMetric(metricList,"Total Prefetch", totalPrefetch);
				addGaugeMetric(metricList,"Total Active Durables", totalActiveDurable);
//				addGaugeMetric(metricList,"Total Durable Subscriptions", totalDurableSubscriptions));
				addGaugeMetric(metricList,"Total Subscribers", totalSubscriberCount);
				addGaugeMetric(metricList,"Total Subscriptions", totalSubscriptionCount);
				metricList.add(new AttributeMetric("EMS Server", emsServerName));
				metricReporter.report("Topic Totals",StatType.TopicTotals, metricList);
			}


		} catch (TibjmsAdminException e) {
			Utils.reportError("TibJMSAdminException occurred",e);
		} 

	}
	
	protected void getRouteStats(TibjmsAdmin tibjmsServer,JSONMetricReporter metricReporter) {
		try {

			String emsServerName = emsServer.getName().trim();
			RouteInfo[] routeList = tibjmsServer.getRoutes();
			long totalBackLogCount = 0;
			long totalBackLogSize = 0;
			int routes = 0;
			List<Metric> metricList = new ArrayList<Metric>();

			if (routeList != null) 
				for (RouteInfo t : routeList) {
					routes++;
					metricList.clear();
					String routeName = t.getName().trim();
					metricList.add(new AttributeMetric("Route Name", routeName));
				
					if(t.getURL() != null) {
						metricList.add(new AttributeMetric("URL", t.getURL()));
					}
					
					if(t.getZoneName() != null) {
						metricList.add(new AttributeMetric("Zone Name", t.getZoneName()));
					}
					
					metricList.add(new AttributeMetric("Stalled", t.isStalled()));
					metricList.add(new AttributeMetric("Connected", t.isConnected()));

					addGaugeMetric(metricList,"Backlog Count", t.getBacklogCount());
					totalBackLogCount += t.getBacklogCount();
					addGaugeMetric(metricList,"Backlog Size", t.getBacklogSize());
					totalBackLogSize += t.getBacklogSize();

					if (t.getInboundStatistics() != null) {
						metricList.add(new RateMetric("Inbound-Byte Rate", t.getInboundStatistics().getByteRate()));
						metricList.add(new RateMetric("Inbound-Message Rate", t.getInboundStatistics().getMessageRate()));
						metricList.add(new RateMetric("Inbound-Total Bytes", t.getInboundStatistics().getTotalBytes()));
						addGaugeMetric(metricList,"Inbound-Total Messages", t.getInboundStatistics().getTotalMessages());
					}
					metricList.add(new RateMetric("Outbound-Byte Rate", t.getOutboundStatistics().getByteRate()));
					metricList.add(new RateMetric("Outbound-Message Rate", t.getOutboundStatistics().getMessageRate()));
					addGaugeMetric(metricList,"Outbound-Total Bytes", t.getOutboundStatistics().getTotalBytes());
					addGaugeMetric(metricList,"Outbound-Total Messages", t.getOutboundStatistics().getTotalMessages());
					metricList.add(new AttributeMetric("EMS Server", emsServerName));

					metricReporter.report("Route Metrics",StatType.Route, metricList);
				}
			
			if(routes > 0) {
				metricList.clear();
				addGaugeMetric(metricList,"Total Route Backlog Count", totalBackLogCount);
				addGaugeMetric(metricList,"Total Route Backlog Size", totalBackLogSize);
				addGaugeMetric(metricList,"Total Monitored Routes", routes);
				metricList.add(new AttributeMetric("EMS Server", emsServerName));
				metricReporter.report("Route Totals",StatType.RouteTotals, metricList);
			}

		} catch (TibjmsAdminException e) {
			Utils.reportError("TibJMSAdminException occurred",e);
		} 

	}
	protected void getServerInfo(TibjmsAdmin tibjmsServer, JSONMetricReporter metricReporter) {
		try {

			String emsServerName = emsServer.getName().trim();
			ServerInfo getInfo = tibjmsServer.getInfo();

			if (getInfo != null) 
				{
					List<Metric> metricList = new ArrayList<Metric>();
    				State serverState = tibjmsServer.getStateInfo().getState();

					metricList.add(new AttributeMetric("Server State", serverState.toString()));

					int pid = getInfo.getProcessId();
					metricList.add(new AttributeMetric("Process ID", pid));

					int connCount = getInfo.getConnectionCount();
					metricList.add(new AttributeMetric("Connection Count", connCount));

					long totalMsgMemory = getInfo.getMsgMem();
					metricList.add(new AttributeMetric("Total Message Memory", totalMsgMemory));

					long maxMsgMemory = getInfo.getMaxMsgMemory();
					metricList.add(new AttributeMetric("Max Message Memory", maxMsgMemory));

					metricList.add(new AttributeMetric("EMS Server", emsServerName));

					metricReporter.report("Route Metrics",StatType.Server, metricList);
				}

		} catch (TibjmsAdminException e) {
			Utils.reportError("TibJMSAdminException occurred",e);
		} 

	}

}
