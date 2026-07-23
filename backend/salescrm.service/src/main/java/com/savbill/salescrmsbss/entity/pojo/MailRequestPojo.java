package com.savbill.salescrmsbss.entity.pojo;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestPojo {
	
	private String emailAuditingDTO;
	private MultipartFile file;

}
