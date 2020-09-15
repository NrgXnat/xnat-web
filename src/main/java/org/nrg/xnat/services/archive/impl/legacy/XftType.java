package org.nrg.xnat.services.archive.impl.legacy;

public enum XftType {
    Project,
    Subject,
    Experiment,
    Unknown;

    // Cache the XftType values because each reference causes a new array to be allocated.
    public static final XftType[] values = values();
}
