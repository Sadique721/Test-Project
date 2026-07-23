package com.diameter.service;


import java.io.Serializable;

public interface IBaseData<K extends Serializable> {
    K getPrimaryKey();
    void setDeleteFlag(boolean deleteFlag);
    boolean getDeleteFlag();
}

