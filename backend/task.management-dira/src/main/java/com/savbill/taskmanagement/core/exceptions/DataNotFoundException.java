package com.savbill.taskmanagement.core.exceptions;

public class DataNotFoundException extends RuntimeException {
    public DataNotFoundException(){
        super("Data not found");
    }
    public DataNotFoundException(String msg){
        super(msg);
    }
}
