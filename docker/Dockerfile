# Non-root xnat-web image. Wraps the PUBLISHED war (pulled from JFrog at build,
# so the container ships the same bytes as the metal/file install) on a Tomcat
# base, running as a non-root uid. Adapted from build_scripts_v2/xnat-docker-build;
# the only functional deltas are non-root (USER + ownership) and TZ-via-env
# instead of the /etc/localtime symlink.
#
# JDK8 base = the 1.9.x line; pass tomcat:9.0-jdk21-temurin-noble for 1.10.
ARG TOMCAT_IMAGE=tomcat:9.0-jdk8-temurin-noble
FROM ${TOMCAT_IMAGE}

ARG XNAT_ROOT=/data/xnat
ARG XNAT_HOME=/data/xnat/home
ARG XNAT_DATASOURCE_DRIVER=org.postgresql.Driver
ARG XNAT_DATASOURCE_URL=jdbc:postgresql://xnat-postgresql/xnat
ARG XNAT_DATASOURCE_USERNAME=xnat
ARG XNAT_DATASOURCE_PASSWORD=xnat
ARG XNAT_ACTIVEMQ=xnat-activemq
# Published WAR to wrap (JFrog) -- supplied by build.sh / the workflow.
ARG XNAT_WAR_URL
# Non-root runtime uid: numeric (so K8s runAsNonRoot can verify it is non-zero)
# and high (avoids host/system uid collisions).
ARG XNAT_UID=10001

ENV XNAT_HOME=${XNAT_HOME} TZ=Etc/UTC \
    XNAT_DATASOURCE_DRIVER=${XNAT_DATASOURCE_DRIVER} XNAT_DATASOURCE_URL=${XNAT_DATASOURCE_URL} \
    XNAT_DATASOURCE_USERNAME=${XNAT_DATASOURCE_USERNAME} XNAT_DATASOURCE_PASSWORD=${XNAT_DATASOURCE_PASSWORD} \
    XNAT_ACTIVEMQ=${XNAT_ACTIVEMQ} PGPASSWORD=${XNAT_DATASOURCE_PASSWORD} \
    CATALINA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=66.0 -Dxnat.home=${XNAT_HOME}"

COPY entrypoint.sh make-xnat-config.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh /usr/local/bin/make-xnat-config.sh \
 && apt-get update && apt-get install -y --no-install-recommends unzip curl ca-certificates dcmtk \
 && rm -rf /var/lib/apt/lists/* ${CATALINA_HOME}/webapps/* \
 && mkdir -p ${CATALINA_HOME}/webapps/ROOT \
      ${XNAT_HOME}/config ${XNAT_HOME}/logs ${XNAT_HOME}/plugins ${XNAT_HOME}/work \
      ${XNAT_ROOT}/archive ${XNAT_ROOT}/build ${XNAT_ROOT}/cache ${XNAT_ROOT}/ftp ${XNAT_ROOT}/pipeline ${XNAT_ROOT}/prearchive \
 && /usr/local/bin/make-xnat-config.sh

# Wrap the PUBLISHED war (same bytes as metal) -- no source build.
RUN curl -fSL --retry 5 -o /tmp/ROOT.war "${XNAT_WAR_URL}" \
 && unzip -oq -d ${CATALINA_HOME}/webapps/ROOT /tmp/ROOT.war && rm /tmp/ROOT.war \
 && sed -i 's#rolling.RollingFileAppender#ConsoleAppender#' \
      ${CATALINA_HOME}/webapps/ROOT/WEB-INF/classes/logback.xml

# Non-root: own writable paths as <uid>:0, group-writable (g=u) so it runs as
# ${XNAT_UID} -- and as any uid with gid 0 (arbitrary-uid platforms) / a K8s fsGroup.
RUN chown -R ${XNAT_UID}:0 ${XNAT_ROOT} ${CATALINA_HOME}/webapps ${CATALINA_HOME}/work ${CATALINA_HOME}/temp ${CATALINA_HOME}/logs \
 && chmod -R g=u ${XNAT_ROOT} ${CATALINA_HOME}/webapps ${CATALINA_HOME}/work ${CATALINA_HOME}/temp ${CATALINA_HOME}/logs

USER ${XNAT_UID}
ENTRYPOINT ["/usr/local/bin/entrypoint.sh", "/usr/local/tomcat/bin/catalina.sh", "run"]
