package org.nrg.xnat.export;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQQueue;
import org.nrg.mail.services.MailService;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xnat.export.jms.errors.ExportJmsErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Mohana Ramaratnam
 *
 */
@Configuration
@Slf4j
@EnableJms
public class ExportConfiguration {
    public static final String QUEUE_MIN_CONCURRENCY_DFLT = "10";
    public static final String QUEUE_MAX_CONCURRENCY_DFLT = "20";
	

    @Bean(name = "exportThreadPoolExecutorFactoryBean")
    public ThreadPoolExecutorFactoryBean exportThreadPoolExecutorFactoryBean() {
        ThreadPoolExecutorFactoryBean tBean = new ThreadPoolExecutorFactoryBean();
        tBean.setCorePoolSize(5);
        tBean.setThreadNamePrefix("export-");
        return tBean;
    }
    
    @Bean(name = "exportQueueListenerFactory")
    public DefaultJmsListenerContainerFactory exportQueueListenerFactory(final SiteConfigPreferences siteConfigPreferences,
                                                                          final MailService mailService,
                                                                          @Qualifier("springConnectionFactory")
                                                                                       ConnectionFactory connectionFactory) {
        return defaultFactory(connectionFactory, siteConfigPreferences, mailService);
    }

    @Bean(name = "exportRequest")
    public Destination containerStagingRequest(@Value("exportRequest") String exportRequest)
            throws JMSException {
        return new ActiveMQQueue(exportRequest);
    }


    private DefaultJmsListenerContainerFactory defaultFactory(ConnectionFactory connectionFactory,
                                                              final SiteConfigPreferences siteConfigPreferences,
                                                              final MailService mailService) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency(QUEUE_MIN_CONCURRENCY_DFLT + "-" + QUEUE_MAX_CONCURRENCY_DFLT);
        factory.setErrorHandler(new ExportJmsErrorHandler(siteConfigPreferences, mailService));
        return factory;
    }



}
