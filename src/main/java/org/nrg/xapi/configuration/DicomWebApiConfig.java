/*
 * web: org.nrg.xapi.configuration.RestApiConfig
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.configuration;

import org.nrg.xapi.model.dicomweb.FrameGrabber;
import org.nrg.xapi.model.dicomweb.framegrabber.basic.BasicFrameGrabber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"org.nrg.xapi","org.nrg.xapi.model", "org.nrg.xapi.model.dicomweb"})
public class DicomWebApiConfig {
    @Bean
    public FrameGrabber getFrameGrabber() {
        return new BasicFrameGrabber();
    }

}
