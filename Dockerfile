FROM hivemq/hivemq4:latest

# Copy the configuration file
COPY config.xml /opt/hivemq/conf/config.xml

# Copy rbac extensions to the extensions folder
COPY hivemq-file-rbac-extension/ /opt/hivemq/extensions/hivemq-file-rbac-extension
