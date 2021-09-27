package org.nrg.xnat.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.SessionFactory;
import org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.nrg.framework.beans.XnatPluginBeanManager;
import org.nrg.framework.orm.DatabaseHelper;
import org.nrg.framework.orm.hibernate.AggregatedAnnotationSessionFactoryBean;
import org.nrg.framework.orm.hibernate.HibernateEntityPackageList;
import org.nrg.framework.orm.hibernate.PrefixedTableNamingStrategy;
import org.nrg.framework.services.ContextService;
import org.nrg.framework.services.NrgEventService;
import org.nrg.framework.services.NrgEventServiceI;
import org.nrg.prefs.resolvers.PreferenceEntityResolver;
import org.nrg.prefs.resolvers.SimplePrefsEntityResolver;
import org.nrg.prefs.services.NrgPreferenceService;
import org.nrg.prefs.services.PreferenceService;
import org.nrg.test.utils.TestBeans;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xnat.test.config.XftDataTestConfig;
import org.postgresql.Driver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.bus.EventBus;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Provides a re-usable test configuration for setting up in-memory ORM environment. This shouldn't be used in
 * production code and should eventually be refactored into an NRG test tools library.
 */
@Configuration
@Import({TestSerializationConfig.class, XftDataTestConfig.class})
@EnableTransactionManagement
public class TestXftDataSerializationConfig {
    public static String POSTGRES_URL = "";

    @Bean
    public ContextService contextService() {
        return new ContextService();
    }

    @Bean
    public XnatPluginBeanManager xnatPluginBeanManager() {
        return new XnatPluginBeanManager();
    }

    @Bean
    public JsonNode siteMap() throws IOException {
        return TestBeans.getDefaultTestSiteMap();
    }

    @Bean
    public PreferenceEntityResolver defaultResolver(final PreferenceService service, final JsonNode siteMap) throws IOException {
        return new SimplePrefsEntityResolver(service, siteMap);
    }

    @Bean
    public EventBus eventBus() {
        return EventBus.create();
    }

    @Bean
    public NrgEventServiceI eventService(final EventBus eventBus) {
        return new NrgEventService(eventBus);
    }

    @Bean
    public SiteConfigPreferences siteConfigPreferences(final NrgPreferenceService preferenceService, final NrgEventServiceI eventService) {
        return new SiteConfigPreferences(preferenceService, eventService, null, null);
    }

    @Bean
    public DataSource dataSource() {
        final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(Driver.class);
        dataSource.setUrl(POSTGRES_URL);
        dataSource.setUsername("xnat");
        dataSource.setPassword("xnat");
        return dataSource;
    }

    @Bean
    public ImprovedNamingStrategy namingStrategy() {
        return new PrefixedTableNamingStrategy("xhbm");
    }

    @Bean
    public PropertiesFactoryBean hibernateProperties() {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.show_sql", "false");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");

        final PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setProperties(properties);
        return bean;
    }

    @Bean
    public RegionFactory regionFactory(@Qualifier("hibernateProperties") final Properties properties) {
        return new SingletonEhCacheRegionFactory(properties);
    }

    @Bean
    public FactoryBean<SessionFactory> sessionFactory(final RegionFactory factory,
                                                      final DataSource dataSource,
                                                      @Qualifier("hibernateProperties") final Properties properties,
                                                      final ImprovedNamingStrategy namingStrategy,
                                                      @Autowired(required = false) final List<HibernateEntityPackageList> packageLists) {
        final AggregatedAnnotationSessionFactoryBean bean = new AggregatedAnnotationSessionFactoryBean();
        bean.setEntityPackageLists(packageLists);
        bean.setCacheRegionFactory(factory);
        bean.setDataSource(dataSource);
        bean.setHibernateProperties(properties);
        bean.setNamingStrategy(namingStrategy);
        return bean;
    }

    @Bean
    public ResourceTransactionManager transactionManager(final FactoryBean<SessionFactory> sessionFactory) throws Exception {
        return new HibernateTransactionManager(sessionFactory.getObject());
    }

    @Bean
    public TransactionTemplate transactionTemplate(final PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }

    @Bean
    public DatabaseHelper databaseHelper(final NamedParameterJdbcTemplate template, final TransactionTemplate transactionTemplate) {
        return new DatabaseHelper(template, transactionTemplate);
    }
}
