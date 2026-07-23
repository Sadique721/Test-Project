package com.savbill.radius.dto;


import lombok.Data;

@Data
public class AccessResponseDto {


    private Long id;

    private String name;

    private String message;

    private String event;
}
