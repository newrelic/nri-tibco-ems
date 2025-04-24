package com.newrelic.nri.tibco.ems;

public enum StatType {
	EventMapping {
        @Override
        public String getEventType() {
            return "EventMapping";
        }
	},
	Channel {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Channel");
        }
	}, 
	Bridge {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Bridge");
        }
	}, 
	Queue {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Queue");
        }
	}, 
	Route {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Route");
        }
	}, 
	Topic {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Topic");
        }
	}, 
    Server {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("Server");
        }
	}, 
    ChannelDetails {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("ChannelDetails");
        }
	}, 
	QueueTotals {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("QueueTotals");
        }
	}, 
	TopicTotals {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("TopicTotals");
        }
	}, 
	RouteTotals {
        @Override
        public String getEventType() {
            return EventMappings.getEventMapping("RouteTotals");
        }
	};
	
	public abstract String getEventType();
}
