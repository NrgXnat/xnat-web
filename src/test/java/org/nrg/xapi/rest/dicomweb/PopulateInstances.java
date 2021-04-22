package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.xdat.security.XDATUser;
import org.nrg.xft.collections.ItemCollection;
import org.nrg.xft.search.CriteriaCollection;
import org.nrg.xft.search.ItemSearch;
import org.nrg.xft.security.UserI;
import org.nrg.xnat.config.TestDicomWebConfig;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestDicomWebConfig.class})
public class PopulateInstances {
//    @Autowired
//    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void populate() {
        try {
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(getDataSource()));
            System.out.println(jdbcTemplate);

//            UserI user = userManagementService.getUser("admin");
            UserI user = new XDATUser("admin");

            ItemCollection ic = getImageSessionsForProject( "drmtest2", user);

            SqlParameterSource namedParameters = new MapSqlParameterSource().addValue("project", "drmtest2");

            jdbcTemplate.query(
                    "select * from xnat_experimentdata where project = :project", namedParameters,
                    (rs, rowNum) -> new Experiment(rs.getString("id"), rs.getString("label"))
            ).forEach(experiment -> System.out.println(experiment.toString()));
        }
        catch (Exception e) {
            fail();
        }

    }

    private ItemCollection getImageSessionsForProject(String project, UserI user) throws Exception {
        CriteriaCollection cc = new CriteriaCollection("AND");

        cc.addClause("xnat:imageSessionData/project", project);
        ItemCollection ic = ItemSearch.GetItems( "xnat:imageSessionData", cc, user, false);
        return ic;
    }

    private class Experiment {
        String id;
        String label;
        public Experiment( String id, String label) {
            this.id = id;
            this.label = label;
        }
        public String toString() {
            return id + ", " + label;
        }
    }

    public DataSource getDataSource()
    {
//        DataSourceFactory dataSourceFactory = new DataSourceFactory();
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.h2.Driver");
//        dataSourceBuilder.url("jdbc:h2:file:C:/temp/test");
//        dataSourceBuilder.username("sa");
//        dataSourceBuilder.password("");
//        return dataSourceBuilder.build();

        PGPoolingDataSource source = new PGPoolingDataSource();
        source.setDataSourceName("XNAT DB Data Source");
        source.setServerName("10.1.1.17");
        source.setDatabaseName("xnat");
        source.setUser("xnat");
        source.setPassword("xnat");
        source.setMaxConnections(10);
        return source;
    }
}
