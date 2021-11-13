package org.nrg.xnat.configuration;

import org.nrg.anonscriptprovider.auth.impl.BaseScriptResourceAuthority;
import org.nrg.anonscriptprovider.configuration.ConfigAwareScriptResourceAuthority;
import org.nrg.dicom.dicomedit.DE6ScriptFactory;
import org.nrg.xapi.rest.dicom.AnonymizeRestControllerAdvice;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.nrg.anonscriptprovider"})
public class AnonScriptProviderConfig {

    @Bean
    public BaseScriptResourceAuthority scriptResourceAuthority(SiteConfigPreferences prefs) {
        return new ConfigAwareScriptResourceAuthority( prefs);
    }

    @Bean
    public DE6ScriptFactory scriptFactory( BaseScriptResourceAuthority resourceAuthority) {
        return new DE6ScriptFactory( resourceAuthority);
    }

    @Bean
    public AnonymizeRestControllerAdvice createAdvice( SiteConfigPreferences prefs) {
        return new AnonymizeRestControllerAdvice( prefs);
    }
}
