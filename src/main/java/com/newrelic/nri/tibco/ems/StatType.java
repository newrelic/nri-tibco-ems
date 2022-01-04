package com.newrelic.nri.tibco.ems;

public enum StatType {
	Channel {
        @Override
        public String getEventType() {
            return "EMSChannel";
        }
	}, 
	Bridge {
        @Override
        public String getEventType() {
            return "EMSBridge";
        }
	}, 
	Queue {
        @Override
        public String getEventType() {
            return "EMSQueue";
        }
	}, 
	Route {
        @Override
        public String getEventType() {
            return "EMSRoute";
        }
	}, 
	Topic {
        @Override
        public String getEventType() {
            return "EMSTopic";
        }
	}, 
    Server {
        @Override
        public String getEventType() {
            return "EMSServer";
        }
	}, 
    ChannelDetails {
        @Override
        public String getEventType() {
            return "EMSChannelDetails";
        }
	}, 
	QueueTotals {
        @Override
        public String getEventType() {
            return "EMSQueueTotals";
        }
	}, 
	TopicTotals {
        @Override
        public String getEventType() {
            return "EMSTopicTotals";
        }
	}, 
	RouteTotals {
        @Override
        public String getEventType() {
            return "EMSRouteTotals";
        }
	};
	
	public abstract String getEventType();
}
