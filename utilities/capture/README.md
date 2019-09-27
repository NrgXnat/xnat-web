# Capture tools #

Copy these files to the system where your XNAT installation is running. You can put them in a path where they'll be accessible automatically,
e.g. **/usr/local/bin**.

Note that, if you're running in a virtual machine created using the [XNAT Vagrant project](https://bitbucket.org/xnatdev/xnat-vagrant),
you'll already have the **xnat-capture** and **xnat-restore** scripts installed.

## Configuring

You'll need to modify **vars.sh** to set environment variables properly, as described below:

| Variable | Default | Description |
| -------- | ------- | ----------- |
| DATA_ROOT | **/data/xnat** | Set to the folder where the archive, prearchive, and other system folders are located. |
| PROJECT | **xnat** | Set to the database name. |
| SERVER | **xnatdev.xnat.org** | Set to the server address, NOT the full URL! |
| TOMCAT | **tomcat7** | Set to the folder or service name for Tomcat. For example, look under **/var/lib** for **tomcat**, **tomcat7**, or **tomcat8**. The important thing is that you should be able to run a command like **systemctl status ${TOMCAT}.service**. |
| XNAT_HOME | **/data/xnat/home** | Set to the XNAT home folder. This is the same value that's set for **xnat.home** in Tomcat. |
| XNAT_USER | **xnat** | Set to the XNAT username. |

## Running

There are two scripts:

* **xnat-capture** captures the current state of your XNAT system
* **xnat-restore** restores a previously captured XNAT system state 

Note that both capturing and restoring system state requires a Tomcat shutdown.

### Capturing

To capture the current state of your XNAT system, run **xnat-capture**:

```bash
$ xnat-capture
```

This will capture the system state into a folder named **/resources/captures/capture-_timestamp_**.

You can also name a particular capture operation:

```bash
$ xnat-capture initialized
```

This will capture the system state into a folder named **/resources/captures/initialized**. This is useful for creating benchmark snapshots that
you can easily restore later.

### Restoring

To restore a previously captured XNAT system state, run **xnat-restore**:

```bash
$ xnat-restore <capture>
```

This will restore the system state stored in the folder named **/resources/restores/<capture>**.

Note that, unlike **xnat-capture**, you _must_ provide a capture name to restore system state.

