
# New Relic Infrastructure Integration for Tibco EMS

Reports status and metrics for Tibco EMS server

## Disclaimer

New Relic has open-sourced this integration to enable monitoring of this technology. This integration is provided AS-IS WITHOUT WARRANTY OR SUPPORT, although you can report issues and contribute to this integration via GitHub.
    
## Requirements

 - New Relic Infrastructure Agent

## Installation

1. Extract the Release archive to the local disk
2. Either run the install script or follow the instructions below for manual installation.

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

## Configuration



Edit *tibco-ems-server-config.json* file to edit the tibco server(s) connection information. 
   
    
| Attribute | Description |
| --- | --- |
| name | Name describing the EMS Server |
| host | DNS name of IP of the EMS Server |
| port | port number of EMS Server, typically 7222 |
| username | username for connecting |
| password | (optional) provide password for user if needed |
| includeDynamicQueues | flag (true, false) to indicate whether to collect temporary queues |
| queueIgnores | JSON Array of JSON Object pair "qIgnoreRegEx" and a regular expression to match against queue names |
| includeDynamicTopics | flag (true, false) to indicate whether to collect temporary topics |
| topicIgnores | JSON Array of JSON Object pair "tIgnoreRegEx" and a regular expression to match against topic names |



Finally verify that the path to *tibco-ems.jar* in the *tibco-ems-definition.yml* file.

Restart the infrastructure agent

```sh
sudo systemctl stop newrelic-infra

sudo systemctl start newrelic-infra
```

## Usage

You can view your data in Insights by creating your own custom NRQL queries. To
do so use **EMS**.. event types.

## Compatibility

* Supported OS: Linux
