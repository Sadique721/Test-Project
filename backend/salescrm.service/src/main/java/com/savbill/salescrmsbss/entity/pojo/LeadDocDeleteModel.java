package com.savbill.salescrmsbss.entity.pojo;

import java.util.List;

import lombok.Data;

@Data
public class LeadDocDeleteModel {
	
	 private List<Long> docIdList;
	 
	 private Integer custId;

}
