<a href="https://opensource.newrelic.com/oss-category/#new-relic-experimental"><picture><source media="(prefers-color-scheme: dark)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/dark/Experimental.png"><source media="(prefers-color-scheme: light)" srcset="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"><img alt="New Relic Open Source experimental project banner." src="https://github.com/newrelic/opensource-website/raw/main/src/images/categories/Experimental.png"></picture></a>

![GitHub forks](https://img.shields.io/github/forks/newrelic-experimental/nri-tibco-ems?style=social)
![GitHub stars](https://img.shields.io/github/stars/newrelic-experimental/nri-tibco-ems?style=social)
![GitHub watchers](https://img.shields.io/github/watchers/newrelic-experimental/nri-tibco-ems?style=social)

![GitHub all releases](https://img.shields.io/github/downloads/newrelic-experimental/nri-tibco-ems/total)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/newrelic-experimental/nri-tibco-ems)
![GitHub last commit](https://img.shields.io/github/last-commit/newrelic-experimental/nri-tibco-ems)
![GitHub Release Date](https://img.shields.io/github/release-date/newrelic-experimental/nri-tibco-ems)


![GitHub issues](https://img.shields.io/github/issues/newrelic-experimental/nri-tibco-ems)
![GitHub issues closed](https://img.shields.io/github/issues-closed/newrelic-experimental/nri-tibco-ems)
![GitHub pull requests](https://img.shields.io/github/issues-pr/newrelic-experimental/nri-tibco-ems)
![GitHub pull requests closed](https://img.shields.io/github/issues-pr-closed/newrelic-experimental/nri-tibco-ems)   
# New Relic Infrastructure Integration for Tibco EMS

Reports status and metrics for Tibco EMS server

## Disclaimer

New Relic has open-sourced this integration to enable monitoring of this technology. This integration is provided AS-IS WITHOUT WARRANTY OR SUPPORT, although you can report issues and contribute to this integration via GitHub.
    
## Requirements

 - New Relic Infrastructure Agent

## Using Tibco Mangled Passwords
  
In order to encrypt the password of the user connecting to EMS, you need to run the included utility to mangle the password.  Then place the encrypted password in the password field and include the encryptPassword field with a value of true.   
  
In the extracted release directory run the passwordSetup.sh script with the password to use.  It will output the mangled password to use in tibco-ems-server-config.json.  
   
## Configuration

Edit *tibco-ems-server-config.json* file to edit the tibco server(s) connection information. 
   
    
| Attribute | Description |
| --- | --- |
| name | Name describing the EMS Server |
| host | DNS name of IP of the EMS Server |
| port | port number of EMS Server, typically 7222 |
| username | username for connecting |
| password | (optional) provide password for user if needed |
| encryptPassword | (optional) set to true if password is mangled (encrypted), default is false |
| includeDynamicQueues | flag (true, false) to indicate whether to collect temporary queues |
| queueIgnores | JSON Array of JSON Object pair "qIgnoreRegEx" and a regular expression to match against queue names |
| includeDynamicTopics | flag (true, false) to indicate whether to collect temporary topics |
| topicIgnores | JSON Array of JSON Object pair "tIgnoreRegEx" and a regular expression to match against topic names |
| channeldetails | whether to collect channel details or not.  default is true |


### Ignoring Queues and/or Topics
The queueIgnores and topicIgnores configuration arrays can be used to ignore queues or topics that are either not of interest or are overloading the metrics resulting in lost metrics.  They are populated with Java regular expressions that are used to match against the actual queue name to see whether it should be ignored or not.   Note that the actual queue name is a regular expression that only matches that name.

*Example*
Suppose we would like to ignore all queues that start with either TMP or SYS.  Then we would enter the following for queueIgnores:    
![image](https://user-images.githubusercontent.com/8822859/134726305-aadeb327-f12e-4e85-89c5-ede56a5961f6.png)

## Installation

1. Extract the Release archive to the local disk
2. Edit *tibco-ems-server-config.json* according to the Confguration section above.   
3. Either run the install script or follow the instructions below for manual installation.
4. Restart the infrastructure agent

```sh
sudo systemctl stop newrelic-infra

sudo systemctl start newrelic-infra
```


### By Script

As the root user, run the following command:

```sh

./install.sh
```

### Manual

Install the Tibco EMS monitoring plugin

```sh

cp -R bin /var/db/newrelic-infra/custom-integrations/

cp tibco-ems-definition.yml /var/db/newrelic-infra/custom-integrations/

cp tibco-ems-config.yml.sample /etc/newrelic-infra/integrations.d/

cp tibco-ems-server-config.json /etc/newrelic-infra/integrations.d/

```

## Usage

The data collected is reported to New Relic as Custom Events.  The following is a list of events that can be reported.   An event is only recorded if data present for that item so not all events may be reported.
   
### Event Types
   
| Event Type | Description |
| ---- | ---- |
| **EMSQueue** | Metrics related to an EMS Queue.  Attribute "Queue Name" is the name of the queue |
| **EMSQueueTotals** | Contains the sums of certain metrics across all recorded queues |
| **EMSTopic** | Metrics related to an EMS Topic.  Attribute "Topic Name" is the name of the topic |
| **EMSTopicTotals** | Contains the sums of certain metrics across all recorded topics |
| **EMSRoute** | Metrics related to an EMS Route.  Attribute "Route Name" is the name of the route |
| **EMSRouteTotals** | Contains the sums of certain metrics across all recorded routes |
| **EMSChannel** | Metrics related to an EMS Channel.  Attribute "Channel Name" is the name of the channel |
| **EMSChannelDetails** | Contains more detailed information about a channel |
| **EMSBridge** | Metrics related to an EMS Bridge.  Attribute "Bridge Name" is the name of the bridge |

## Compatibility

* Supported OS: Linux
