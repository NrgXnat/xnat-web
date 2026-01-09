package org.nrg.xnat.ingest.services.config;

import org.nrg.xnat.ingest.services.components.DeviceHandlerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeviceHandlerConfiguration {

    @Bean
    public DeviceHandlerManager deviceHandlerManager() {
        return new DeviceHandlerManager();
    }
}