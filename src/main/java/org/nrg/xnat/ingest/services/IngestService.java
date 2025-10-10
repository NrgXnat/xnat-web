package org.nrg.xnat.ingest.services;

import lombok.extern.slf4j.Slf4j;
import org.nrg.xnat.ingest.device.handler.DeviceHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
public class IngestService {

        private final DeviceHandlerRegistry handlerRegistry;
        private final DynamicDeviceHandlerRegistrar dynamicRegistrar;

        @Autowired
        public IngestService(DeviceHandlerRegistry handlerRegistry,
                                       DynamicDeviceHandlerRegistrar dynamicRegistrar) {
            this.handlerRegistry = handlerRegistry;
            this.dynamicRegistrar = dynamicRegistrar;
        }

        public void processDevice(Path deviceFolder) {
            Optional<DeviceHandler> handler = handlerRegistry.findHandler(deviceFolder);

            if (handler.isPresent()) {
                log.info("Processing device with: " + handler.get().getClass().getSimpleName());
                handler.get().processDevice(deviceFolder);
            } else {
                System.out.println("No suitable handler found for device folder: " + deviceFolder.getPath());
            }
        }

        public void loadAdditionalHandlers() {
            // Dynamically scan for new handlers
            dynamicRegistrar.scanAndRegisterHandlers("com.yourcompany.plugins");

            // Load from external JAR
            dynamicRegistrar.loadHandlersFromJar("/path/to/external/handlers.jar");
        }

        public void listAllHandlers() {
            List<DeviceHandler> handlers = handlerRegistry.getAllHandlers();
            System.out.println("Available handlers:");
            for (DeviceHandler handler : handlers) {
                System.out.println("- " + handler.getClass().getSimpleName());
            }
        }

        public void listHandlersByType(String deviceType) {
            List<DeviceHandler> handlers = handlerRegistry.getHandlersByType(deviceType);
            System.out.println("Handlers for device type '" + deviceType + "':");
            for (DeviceHandler handler : handlers) {
                System.out.println("- " + handler.getClass().getSimpleName());
            }
        }

    private DeviceHandler findHandlerForDevice(Path devicePath) {
        for (DeviceHandler handler : deviceHandlers.values()) {
            if (handler.canHandleDevice(devicePath)) {
                // Create a new instance of the handler for this specific device
                try {
                    return handler.getClass()
                            .getConstructor(String.class, String.class, Path.class)
                            .newInstance(devicePath.getFileName().toString(),
                                    handler.getDeviceType(),
                                    devicePath);
                } catch (Exception e) {
                    System.err.println("Error creating handler instance: " + e.getMessage());
                }
            }
        }
        return null;
    }

}
