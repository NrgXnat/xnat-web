package org.nrg.xapi.rest.dicomweb.search;

public class SearchException extends Exception {
    private final Type type;

    public SearchException( Type type, String msg) {
        super(msg);
        this.type = type;
    }
    public SearchException( Type type, Exception e) {
        super(e);
        this.type = type;
    }
    public SearchException( Type type, String msg, Exception e) {
        super( msg, e);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        STUDY_INSTANCE_UID_CONFLICT,
        UNEXPECTED
    }
}
