package org.avniproject.etl.dto;

public class ResponseDTO<T> {

    public int total;
    public int page;
    public T data;

    public ResponseDTO(int total, int page, T data ){
        this.total = total;
        this.page = page;
        this.data = data;
    }
}
