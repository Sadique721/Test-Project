package com.savbill.commonGateway.moules.userUiPreferences.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import javax.persistence.Column;
import java.util.HashMap;
import java.util.Iterator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUiPreferencesDTO {

    private Long id;

    private Integer mvnoId;

    private String mvnoName;

    private HashMap<String, Object> styleObj;

    private String style;

    private String logoImage;

    private String bgImage;

    private String pageName;

    private boolean isDelete = false;

    private String status;

//    public HashMap<String, Object> setStyleObj(String style) {
//        HashMap<String, Object> map = new HashMap<String, Object>();
//        JSONObject jObject = new JSONObject(style);
//        Iterator<?> keys = jObject.keys();
//
//        while( keys.hasNext() ){
//            String key = (String)keys.next();
//            String value = jObject.getString(key);
//            map.put(key, value);
//        }
//        return map;
//    }
}
