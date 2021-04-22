package org.nrg.xapi.rest.dicomweb.mediator;

import org.nrg.xdat.om.XnatImagesessiondata;

import java.util.List;
import java.util.Optional;

public interface Mediator {

    Optional<List<Conflict>> getConflicts( List<XnatImagesessiondata> sessions);
}
