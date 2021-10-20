package org.nrg.xnat.archive;

import org.nrg.xdat.model.XnatAbstractresourceI;
import org.nrg.xdat.om.XnatAbstractresource;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NotAResourceCatalogException extends CatalogOperationException {
    public NotAResourceCatalogException(final XnatAbstractresourceI resource, final String rootPath) {
        super(Paths.get(((XnatAbstractresource) resource).getFullPath(rootPath)), "Resource " + resource.getXnatAbstractresourceId() + " is not a catalog");
    }
}
