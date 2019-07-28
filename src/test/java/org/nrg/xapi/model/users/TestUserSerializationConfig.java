/*
 * web: org.nrg.xapi.model.users.TestUserSerializationConfig
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xapi.model.users;

import org.nrg.framework.configuration.SerializerConfig;
import org.nrg.framework.services.SerializerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

@Configuration
@Import(SerializerConfig.class)
public class TestUserSerializationConfig {
    @Bean
    public SerializerService serializerService(final Jackson2ObjectMapperBuilder builder, final DocumentBuilderFactory documentBuilderFactory, final SAXParserFactory saxParserFactory, final TransformerFactory transformerFactory) throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        return new SerializerService(builder, documentBuilderFactory, saxParserFactory, transformerFactory);
    }
}
