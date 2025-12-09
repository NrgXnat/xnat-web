package org.nrg.xnat.ingest.services.components;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.ingest.device.handler.DeviceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.nrg.xdat.XDAT;

@Service
@Slf4j
public class DeviceHandlerManager implements DeviceHandlerRegistryInterface {

    private final Map<String, DeviceHandler> handlers = new ConcurrentHashMap<>();

    @Autowired
    public DeviceHandlerManager() {}

    // Automatically discover and register all DeviceHandler beans after application startup
    public void initializeHandlers() {
        // Get all DeviceHandler beans from Spring context
        Collection<DeviceHandler> handlerBeans = XDAT.getContextService().getBeansOfType(DeviceHandler.class).values();

        for (DeviceHandler handler : handlerBeans) {
            registerHandler(handler);
        }

        log.info("Initialized " + handlers.size() + " device handlers");
    }

    @Override
    public void registerHandler(DeviceHandler handler) {
        if (handler != null) {
            String handlerName = handler.getClass().getSimpleName();
            handlers.put(handlerName, handler);
            log.info("Registered handler: " + handlerName);
        }
    }

    @Override
    public void unregisterHandler(DeviceHandler handler) {
        if (handler != null) {
            handlers.values().remove(handler);
        }
    }

    @Override
    public Optional<DeviceHandler> findHandler(Path deviceFolder) {
        if (deviceFolder == null ) {
            return Optional.empty();
        }

        File deviceFolderFile = deviceFolder.toFile();
        if (!deviceFolderFile.exists() || !deviceFolderFile.isDirectory()) {
            return Optional.empty();
        }

        // Sort handlers by priority if using @DeviceHandlerComponent annotation
        return handlers.values().stream()
                .sorted(this::compareHandlerPriority)
                .filter(handler -> handler.canHandleDevice(deviceFolder))
                .findFirst();
    }

    @Override
    public List<DeviceHandler> getAllHandlers() {
        return new ArrayList<>(handlers.values());
    }

    @Override
    public List<DeviceHandler> getHandlersByType(String deviceType) {
        return handlers.values().stream()
                .filter(handler -> supportsDeviceType(handler, deviceType))
                .sorted(this::compareHandlerPriority)
                .collect(Collectors.toList());
    }

    private int compareHandlerPriority(DeviceHandler h1, DeviceHandler h2) {
        int priority1 = getHandlerPriority(h1);
        int priority2 = getHandlerPriority(h2);
        return Integer.compare(priority2, priority1); // Higher priority first
    }

    private int getHandlerPriority(DeviceHandler handler) {
        DeviceHandlerComponent annotation = handler.getClass().getAnnotation(DeviceHandlerComponent.class);
        return annotation != null ? annotation.priority() : 0;
    }

    private boolean supportsDeviceType(DeviceHandler handler, String deviceType) {
        DeviceHandlerComponent annotation = handler.getClass().getAnnotation(DeviceHandlerComponent.class);
        if (annotation != null && annotation.supportedDeviceTypes().length > 0) {
            return Arrays.asList(annotation.supportedDeviceTypes()).contains(deviceType);
        }
        return true;
    }
}