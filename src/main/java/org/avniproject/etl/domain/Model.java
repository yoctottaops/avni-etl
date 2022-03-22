package org.avniproject.etl.domain;

public class Model {
    private Integer id;

    public Model(Integer id) {
        this.id = id;
    }

    public Model() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    protected static boolean equalsIgnoreNulls(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null && b != null) return false;
        if (a != null && b == null) return false;
        return a.equals(b);
    }
}
