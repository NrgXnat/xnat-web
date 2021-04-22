package org.nrg.xapi.rest.dicomweb.mediator;

import org.nrg.xapi.rest.dicomweb.search.SearchException;

import java.util.List;

public class BaseConflict implements Conflict {
    private SearchException.Type type;
    private List<String> description;

    public BaseConflict( SearchException.Type type, List<String> description) {
        this.type = type;
        this.description = description;
    }

    @Override
    public SearchException.Type getType() {
        return type;
    }

    @Override
    public List<String> getDescription() {
        return null;
    }
}
