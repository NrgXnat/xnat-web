package org.nrg.xnat.export.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author mohan_000
 *
 */
public class ExportExclusionStrategy  implements ExclusionStrategy {

    private final Class<?> typeToSkip;

    public ExportExclusionStrategy(Class<?> typeToSkip) {
        this.typeToSkip = typeToSkip;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {

        // if field name 'pKey`, skip
        if ("pKey".equals(f.getName())) {
            return true;
        }

        // if found @ExcludeField, skip
        if (f.getAnnotation(ExcludeField.class) != null) {
            return true;
        }

        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz == typeToSkip);
    }

}