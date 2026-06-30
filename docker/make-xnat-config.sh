#!/bin/sh
set -eu

# Generate xnat-conf.properties from the environment, unless one already exists
# (a conf mounted by the chart/operator wins).
if [ ! -f "$XNAT_HOME/config/xnat-conf.properties" ]; then
  cat > "$XNAT_HOME/config/xnat-conf.properties" << EOF
datasource.driver=$XNAT_DATASOURCE_DRIVER
datasource.url=$XNAT_DATASOURCE_URL
datasource.username=$XNAT_DATASOURCE_USERNAME
datasource.password=$XNAT_DATASOURCE_PASSWORD

hibernate.dialect=org.hibernate.dialect.PostgreSQL9Dialect
hibernate.hbm2ddl.auto=update
hibernate.show_sql=false
hibernate.cache.use_second_level_cache=true
hibernate.cache.use_query_cache=true

spring.activemq.broker-url=tcp://$XNAT_ACTIVEMQ:61616?wireFormat.maxInactivityDuration=0
spring.activemq.user=admin
spring.activemq.password=admin
xnat.is_primary_node=true

spring.http.multipart.max-file-size=1073741824
spring.http.multipart.max-request-size=1073741824
EOF
fi
