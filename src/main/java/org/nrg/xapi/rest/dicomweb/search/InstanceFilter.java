package org.nrg.xapi.rest.dicomweb.search;

import org.nrg.xapi.rest.dicomweb.QueryParameters;
import org.nrg.xdat.bean.CatDcmentryBean;

import java.util.List;

/**
 * Filter catalog entries against Query parameters. We can't use ItemSearch to filter because instances aren't in
 * the DB.
 */
public class InstanceFilter {

    public boolean match(CatDcmentryBean entry, QueryParameters qp) {
        boolean match = true;
        List<String> s = qp.getParams(QueryParameters.SOP_INSTANCE_UID_NAME);
        if( s != null) {
            match = s.stream().filter(entry.getUid()::equals).findFirst().isPresent();
        }
        s = qp.getParams(QueryParameters.INSTANCE_NUMBER_NAME);
        if( match == true && s != null) {
            match = s.stream().filter(entry.getInstancenumber().toString()::equals).findFirst().isPresent();
        }

        return match;
    }

}
