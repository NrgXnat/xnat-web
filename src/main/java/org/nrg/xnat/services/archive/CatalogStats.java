package org.nrg.xnat.services.archive;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.nrg.xdat.model.CatCatalogI;
import org.nrg.xnat.utils.CatalogUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Accessors(prefix = "_")
public class CatalogStats {
    public CatalogStats(final CatCatalogI catalog, final String parentPath) {
        final AtomicInteger count = new AtomicInteger();
        final AtomicLong    size  = new AtomicLong();
        CatalogUtils.getFiles(catalog, parentPath).stream().filter(file -> file != null && file.exists() && !file.getName().endsWith("catalog.xml")).forEach(file -> {
            count.incrementAndGet();
            size.getAndAdd(file.length());
        });
        _count = count.get();
        _size = size.get();
    }

    private final int  _count;
    private final long _size;
}
