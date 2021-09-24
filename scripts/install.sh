#!/bin/sh

cp tibco-ems.jar /var/db/newrelic-infra/custom-integrations

cp tibco-ems-definition.yml /var/db/newrelic-infra/custom-integrations/

cp tibco-ems-config.yml /etc/newrelic-infra/integrations.d/

cp tibco-ems-server-config.json /etc/newrelic-infra/integrations.d/
