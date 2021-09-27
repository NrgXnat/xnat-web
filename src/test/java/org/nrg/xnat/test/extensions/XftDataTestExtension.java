package org.nrg.xnat.test.extensions;

import com.google.common.collect.ImmutableMap;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.nrg.framework.utilities.BasicXnatResourceLocator;
import org.nrg.xdat.XDAT;
import org.nrg.xft.db.DatabaseUpdater;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class XftDataTestExtension extends SpringExtension {
    private static final List<String> INIT_DATABASE = Arrays.asList("CREATE ROLE xnat PASSWORD 'xnat'",
                                                                    "ALTER ROLE xnat WITH LOGIN",
                                                                    "CREATE DATABASE xnat",
                                                                    "ALTER DATABASE xnat OWNER TO xnat");
    private static final List<String> FOLDERS       = Arrays.asList("archive", "build", "cache", "fileStore", "ftp", "inbox", "pipeline", "prearchive");

    private final EmbeddedPostgres postgres;

    public XftDataTestExtension() throws IOException {
        postgres = EmbeddedPostgres.start();
    }

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        super.beforeAll(context);

        final DataSource dataSource = postgres.getPostgresDatabase();
        try (final Connection connection = dataSource.getConnection()) {
            final Statement statement = connection.createStatement();
            for (final String sql : INIT_DATABASE) {
                statement.execute(sql);
            }
        }

        final List<Class<?>> testClasses = new ArrayList<>();
        context.getTestInstances().ifPresent(instances -> testClasses.addAll(instances.getAllInstances().stream().map(Object::getClass).collect(Collectors.toList())));
        context.getTestClass().ifPresent(testClasses::add);

        final String jdbcUrl = postgres.getJdbcUrl("xnat", "xnat");
        testClasses.stream()
                   .filter(testClass -> testClass.isAnnotationPresent(ContextConfiguration.class))
                   .map(testClass -> testClass.getAnnotation(ContextConfiguration.class).classes())
                   .flatMap(Stream::of).distinct().map(config -> {
                       try {
                           final Field field = config.getField("POSTGRES_URL");
                           return Modifier.isStatic(field.getModifiers()) ? field : null;
                       } catch (NoSuchFieldException e) {
                           // This is okay, it just doesn't have the field.
                       }
                       return null;
                   })
                   .filter(Objects::nonNull)
                   .forEach(field -> {
                       try {
                           field.set(null, jdbcUrl);
                       } catch (IllegalAccessException e) {
                           log.warn("An error occurred trying to set the JDBC URL on the field {} from the class {}", field.getName(), field.getType());
                       }
                   });

        XDAT.init(".", true);

        final File folder = Files.createTempDirectory("xft-").toFile();
        folder.deleteOnExit();
        FOLDERS.stream().map(subfolder -> folder.toPath().resolve(subfolder).toFile()).forEach(subfolder -> {
            subfolder.mkdirs();
            subfolder.deleteOnExit();
        });

        final StringSubstitutor substitutor = new StringSubstitutor(ImmutableMap.of("FOLDER", folder.getAbsolutePath()), "${", "}", '\\');
        final List<String>      config;
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(BasicXnatResourceLocator.getResource("classpath:sql/init-site-config.sql").getInputStream()))) {
            config = reader.lines().map(substitutor::replace).collect(Collectors.toList());
        }

        final DatabaseUpdater updater = new DatabaseUpdater(new JdbcTemplate(postgres.getDatabase("xnat", "xnat")));
        updater.addAdditionalStatements(config);
        updater.populateOrUpdateDatabase();
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        super.afterAll(context);
        postgres.close();
    }
}
