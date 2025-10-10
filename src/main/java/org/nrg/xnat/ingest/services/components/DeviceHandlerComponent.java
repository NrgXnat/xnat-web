package org.nrg.xnat.ingest.services.components;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Custom annotation to mark DeviceHandler implementations
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface DeviceHandlerComponent {
    String value() default "";
    int priority() default 0;
    String[] supportedDeviceTypes() default {};
}