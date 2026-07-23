package com.savbill.radius.aaa.data;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CustomerDetails {

    private String userName;

    private String user_class;

    private String custId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getUser_class() {
        return user_class;
    }

    public void setUser_class(String user_class) {
        this.user_class = user_class;
    }

    public CustomerDetails(String userName, String custId, String user_class) {
        this.userName = userName;
        this.custId = custId;
        this.user_class = user_class;
    }

    @Override
    public String toString() {
        return "CustomerDetails{" +
                "userName='" + userName + '\'' +
                "class='" + user_class + '\'' +
                ", custId='" + custId + '\'' +
                '}';
    }
}
