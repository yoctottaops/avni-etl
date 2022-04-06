package org.avniproject.etl.domain;

public final class NullObject {
    private NullObject() {
    }

    public static boolean isNullObject(Object obj) {
        return obj.getClass().isAssignableFrom(NullObject.class);
    }

    public static NullObject instance() {
        return new NullObject();
    }
}
