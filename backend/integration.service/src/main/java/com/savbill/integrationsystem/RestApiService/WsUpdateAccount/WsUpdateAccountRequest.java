package com.savbill.integrationsystem.RestApiService.WsUpdateAccount;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WsUpdateAccountRequest {
    private Integer requestId;
    private String userName;
    private String serviceId;
    private List<Item> item;

  public static class Item {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
