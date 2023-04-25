package org.avniproject.etl.repository.sql;

public class Page {
    private final int page;
    private final int size;

    public Page(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int offset() {
        return page*size;
    }

    public int limit() {
        return size;
    }

    public int page() {
        return page;
    }
}
