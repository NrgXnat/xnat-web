package org.nrg.xapi.rest.dicomweb.mediator;

import org.nrg.xapi.rest.dicomweb.search.SearchException;

import java.util.List;

public interface Conflict {
    SearchException.Type getType();
    List<String> getDescription();
}
