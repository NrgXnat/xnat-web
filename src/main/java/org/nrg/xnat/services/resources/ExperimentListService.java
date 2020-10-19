package org.nrg.xnat.services.resources;

import java.util.List;

import org.nrg.xapi.model.subjects.XnatExperiment;
import org.nrg.xft.security.UserI;

public interface ExperimentListService {

	public XnatExperiment create(UserI user, XnatExperiment item);

	public List<XnatExperiment> getAll(UserI user);

	public XnatExperiment get(UserI user, int itemId);

	public XnatExperiment findById(UserI user, String itemId);
}
