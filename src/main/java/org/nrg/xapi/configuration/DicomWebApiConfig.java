/*
 * web: org.nrg.xapi.configuration.RestApiConfig
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.configuration;

import org.nrg.xapi.model.dicomweb.DicomObjectFactory;
import org.nrg.xapi.model.dicomweb.FrameGrabber;
import org.nrg.xapi.model.dicomweb.TransCoder;
import org.nrg.xapi.model.dicomweb.dcm4che3.DicomObjectFactoryChe3;
import org.nrg.xapi.model.dicomweb.dcm4che3.TransCoderChe3;
import org.nrg.xapi.model.dicomweb.framegrabber.basic.BasicFrameGrabber;
import org.nrg.xapi.model.dicomweb.framegrabber.cache.CacheFrameGrabber;
import org.nrg.xapi.rest.dicomweb.mediator.BaseMediator;
import org.nrg.xapi.rest.dicomweb.mediator.Mediator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = {"org.nrg.xapi","org.nrg.xapi.model", "org.nrg.xapi.model.dicomweb"})
public class DicomWebApiConfig {

    @Bean
    @Autowired
    public FrameGrabber createFrameGrabber( TransCoder transCoder) { return new CacheFrameGrabber( transCoder); }

    @Bean
    @Autowired
    public DicomObjectFactory createDicomObjectFactory( FrameGrabber frameGrabber) { return new DicomObjectFactoryChe3( frameGrabber); }

    @Bean
    public TransCoder transCoder() {
        return new TransCoderChe3();
    }

    @Bean
    public Mediator getMediator() { return new BaseMediator(); }

}
