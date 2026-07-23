package com.savbill.integrationsystem.RestApiService.logOnSubSession;

import com.savbill.integrationsystem.generated.logonsubsession.LogonSubSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LogOnSubSessionDTO {
    private String string1;
    private String string2;
    private String string3;

    public LogOnSubSessionDTO(LogonSubSession request) {
        this.string1 = request.getString1();
        this.string2 = request.getString2();
        this.string3 = request.getString3();
    }

    public LogOnSubSessionDTO() {
    }
}
