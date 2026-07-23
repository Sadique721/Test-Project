package com.savbill.salescrmsbss.entity.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendLeadDocConvertPojo {

	private List<CustomerDocDetailsDTO> customerDocDetailsDTOList;
}
