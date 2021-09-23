
# New Relic Infrastructure Integration for Tibco EMS

Reports status and metrics for Tibco EMS server

## Disclaimer

New Relic has open-sourced this integration to enable monitoring of this technology. This integration is provided AS-IS WITHOUT WARRANTY OR SUPPORT, although you can report issues and contribute to this integration via GitHub. Support for this integration is available with an [Expert Services subscription](https://newrelic.com/expertservices).

## Requirements

 - New Relic Infrastructure Agent

## Installation

Install the Tibco EMS monitoring plugin

```sh

cp -R bin /var/db/newrelic-infra/custom-integrations/

cp tibco-ems-definition.yml /var/db/newrelic-infra/custom-integrations/

cp tibco-ems-config.yml.sample /etc/newrelic-infra/integrations.d/

cp plugin.json /etc/newrelic-infra/integrations.d/

```

## Configuration

In order to use the `tibco-ems` integration it is required to configure tibco-ems-config.yml.sample file. Firstly, rename the file to tibco-ems-config.yml (that is, drop the .sample extension to enable this integration).

Edit the *tibco-ems-config.yml* configuration file to ensure that *config_file* argument points to the correct path of *plugin.json* file.

Then edit *plugin.json* file to edit the tibco server connection information. 

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