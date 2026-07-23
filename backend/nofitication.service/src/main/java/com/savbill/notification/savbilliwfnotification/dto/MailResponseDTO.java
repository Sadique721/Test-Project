package com.savbill.notification.savbilliwfnotification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailResponseDTO {

    private String header;
    private String content;
}
