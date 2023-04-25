package org.avniproject.etl.dto;

import org.avniproject.etl.repository.sql.Page;

public class ResponseDTO<T> {
    public int page;
    public T data;

    public ResponseDTO(Page page, T data ){
        this.page = page.page();
        this.data = data;
    }
}
