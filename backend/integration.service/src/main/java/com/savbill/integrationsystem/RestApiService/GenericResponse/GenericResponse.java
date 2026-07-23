package com.savbill.integrationsystem.RestApiService.GenericResponse;

public class GenericResponse<T> {
    private T data;


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
