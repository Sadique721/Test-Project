package com.savbill.commonGateway.core.data;


import java.io.Serializable;

public interface IBaseData<K extends Serializable> {
    K getPrimaryKey();
    void setDeleteFlag(boolean deleteFlag);
    boolean getDeleteFlag();
    void setBuId(Long buId);
}

