package com.savbill.salescrmsbss.entity.pojo;


import lombok.Data;

import java.util.List;

@Data
public class CommonListDTO {
    private Long id;
    private String text;
    private String value;
    private String type;
    private String status;
    private List<CommonListDTO> subTypeList;
    private Integer displayId;
    private String displayName;

    private Integer mvnoId;
}