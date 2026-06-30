# Non-root xnat-web image. Wraps the PUBLISHED war (pulled from JFrog at build)
# on a Tomcat base, running as a non-root uid. The container ships the same
# application bytes as the published WAR, with file logging swapped to console.
# Adapted from build_scripts_v2/xnat-docker-build; the only functional deltas are
# non-root (USER + ownership) and TZ-via-env instead of the /etc/localtime symlink.
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
# Optional sha256 of the WAR; when set, verified before unzip (provenance).
ARG XNAT_WAR_SHA256=
# Non-root runtime uid:gid. 1000:1000 = the chart's `tomcat` user/group -- matches
# its securityContext `runAsUser: 1000`, `fsGroup: 1000`, and the mounted
# /etc/passwd entry, so the image is a drop-in for the chart with no runAsUser /
# runAsGroup override needed.
ARG XNAT_UID=1000
ARG XNAT_GID=1000

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
      ${XNAT_ROOT}/archive ${XNAT_ROOT}/build ${XNAT_ROOT}/cache ${XNAT_ROOT}/ftp ${XNAT_ROOT}/pipeline ${XNAT_ROOT}/prearchive

# Wrap the PUBLISHED war -- same application bytes, file logging swapped to
# console; no source build. Own + group-write (g=u) the exploded webapp in THIS
# layer (same RUN as the unzip) so the recursive chown doesn't force a second
# copy-up of the whole webapp into a later layer.
RUN curl -fSL --retry 5 -o /tmp/ROOT.war "${XNAT_WAR_URL}" \
 && if [ -n "${XNAT_WAR_SHA256}" ]; then echo "${XNAT_WAR_SHA256}  /tmp/ROOT.war" | sha256sum -c -; fi \
 && unzip -oq -d ${CATALINA_HOME}/webapps/ROOT /tmp/ROOT.war && rm /tmp/ROOT.war \
 && sed -i 's#rolling.RollingFileAppender#ConsoleAppender#' \
      ${CATALINA_HOME}/webapps/ROOT/WEB-INF/classes/logback.xml \
 && chown -R ${XNAT_UID}:${XNAT_GID} ${CATALINA_HOME}/webapps \
 && chmod -R g=u ${CATALINA_HOME}/webapps

# Non-root: own the remaining writable paths as <uid>:<gid> (1000:1000),
# group-writable (g=u) to match the chart's `runAsUser: 1000` + `fsGroup: 1000`.
# ${CATALINA_HOME}/conf is included so Tomcat can create conf/Catalina/localhost
# on a standalone (non-chart) run. Small dirs -> cheap layer.
RUN chown -R ${XNAT_UID}:${XNAT_GID} ${XNAT_ROOT} ${CATALINA_HOME}/conf ${CATALINA_HOME}/work ${CATALINA_HOME}/temp ${CATALINA_HOME}/logs \
 && chmod -R g=u ${XNAT_ROOT} ${CATALINA_HOME}/conf ${CATALINA_HOME}/work ${CATALINA_HOME}/temp ${CATALINA_HOME}/logs

USER ${XNAT_UID}:${XNAT_GID}
ENTRYPOINT ["/usr/local/bin/entrypoint.sh", "/usr/local/tomcat/bin/catalina.sh", "run"]
