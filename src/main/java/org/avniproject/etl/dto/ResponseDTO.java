package org.avniproject.etl.dto;

import org.avniproject.etl.repository.sql.Page;

public class ResponseDTO<T> {

    public int total;
    public int page;
    public T data;

    public ResponseDTO(int total, Page page, T data ){
        this.total = total;
        this.page = page.page();
        this.data = data;
    }
}
