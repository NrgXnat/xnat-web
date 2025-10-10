package org.nrg.xnat.ingest.services.components;

import org.nrg.xnat.ingest.device.handler.DeviceHandler;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface DeviceHandlerRegistryInterface {

    void registerHandler(DeviceHandler handler);
    void unregisterHandler(DeviceHandler handler);
    Optional<DeviceHandler> findHandler(Path deviceFolder);
    List<DeviceHandler> getAllHandlers();
    List<DeviceHandler> getHandlersByType(String deviceType);
}