package org.nrg.xnat.services.resources.util;

import org.nrg.xdat.om.XnatProjectdata;
import org.nrg.xft.XFTItem;
import org.nrg.xft.presentation.FlattenedItemA;
import org.nrg.xft.presentation.ItemJSONBuilder;

public class RepresentItemUtil {

	public String representItem(XFTItem item, XnatProjectdata proj) {
        String representation = null;
        try {
        	FlattenedItemA.HistoryConfigI history = new FlattenedItemA.HistoryConfigI() {
        	    @Override
        	    public boolean getIncludeHistory() {
        	        return false;
        	    }
        	};
        	representation = new JSONObjectRepresentationUtil((new ItemJSONBuilder()).call(item, history, false)).getText();
        	} catch (Exception e) {
        	//	getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
        	  //  return null;
        	}
//        if (representation != null && proj != null && representation instanceof TurbineScreenRepresentationUtil && StringUtils.isNotBlank(proj.getId())) {
//            ((TurbineScreenRepresentationUtil) representation).setRunDataParameter("project", proj.getId());
//        }
        return representation;
    }
}
