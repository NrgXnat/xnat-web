package org.nrg.xnat.ingest.device.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
public abstract class DeviceHandler {
        protected String deviceId;
        protected String deviceType;
        protected Path deviceRootPath;

        public abstract boolean canHandleDevice(Path devicePath);
        public abstract ManifestEntry createManifestEntry();
}