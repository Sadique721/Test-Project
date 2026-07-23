package com.savbill.integrationsystem.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Packages {

    private String package_name;
    List<Price> prices = new ArrayList<>();
}
