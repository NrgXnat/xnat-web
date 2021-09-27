package org.nrg.xnat.test.extensions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.nrg.framework.orm.DatabaseHelper;
import org.nrg.xdat.preferences.SiteConfigPreferences;
import org.nrg.xnat.test.config.XftDataTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(XftDataTestExtension.class)
@ContextConfiguration(classes = XftDataTestConfig.class)
public class TestXftDataTestExtension {
    private static final String[] DATABASE_TABLES = new String[]{"xdat_user", "xdat_meta_element", "xnat_projectdata"};

    private SiteConfigPreferences _siteConfigPreferences;
    private DatabaseHelper        _databaseHelper;

    @Autowired
    public void setSiteConfigPreferences(final SiteConfigPreferences siteConfigPreferences) {
        _siteConfigPreferences = siteConfigPreferences;
    }

    @Autowired
    public void setDatabaseHelper(final DatabaseHelper databaseHelper) {
        _databaseHelper = databaseHelper;
    }

    @Test
    public void sanityTest() {
        assertThat(_siteConfigPreferences).isNotNull();
        assertThat(_databaseHelper).isNotNull();
    }

    @Test
    public void testTablesExist() throws SQLException {
        assertThat(_databaseHelper.tablesExist(DATABASE_TABLES)).isTrue();
    }
}
