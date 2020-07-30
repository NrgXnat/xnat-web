package org.nrg.xapi.rest.dicomweb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nrg.xnat.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class PopulateInstances {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    public void populate() {
        System.out.println( jdbcTemplate);

//        jdbcTemplate.query(
//                "select * from xnat_experimentdata where project = ?", new Object[] { "drmtest2" },
//                (rs, rowNum) -> new Experiment( rs.getString("id"), rs.getString("label"))
//        ).forEach(experiment -> System.out.println(experiment.toString()));

    }
}
