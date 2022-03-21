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
}
