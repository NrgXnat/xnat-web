package org.nrg.xnat.ingest.device.handler;


import java.nio.file.Path;

public  abstract class DeviceHandler {
        protected String deviceId;
        protected String deviceType;
        protected Path deviceRootPath;

        public DeviceHandler(String deviceId, String deviceType, Path deviceRootPath) {
            this.deviceId = deviceId;
            this.deviceType = deviceType;
            this.deviceRootPath = deviceRootPath;
        }

        public abstract boolean canHandleDevice(Path devicePath);
        public abstract ManifestEntry createManifestEntry();

        public String getDeviceId() { return deviceId; }
        public String getDeviceType() { return deviceType; }
        public Path getDeviceRootPath() { return deviceRootPath; }
    }

