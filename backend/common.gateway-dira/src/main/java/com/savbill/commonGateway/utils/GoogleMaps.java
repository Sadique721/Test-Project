package com.savbill.commonGateway.utils;


import com.savbill.commonGateway.common.domain.ClientService;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.DocumentConstants;
import com.savbill.commonGateway.constants.MapConstants;
import com.savbill.commonGateway.core.dto.LocationPlace;
import com.savbill.commonGateway.core.dto.LocationVo;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class GoogleMaps {

    private String googleMapKey1;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

//    @Autowired
//    GoogleMaps(ClientServiceSrv clientServiceServ){
//        this.googleMapKey1 = clientServiceServ.getByName("google_maps_key").getValue();
//    }

    public HashMap getPlaces(String query) {
        HashMap<String, Object> resp = new HashMap<>();
        List<LocationPlace> locationPlaceList = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();
            ClientService clientService = clientServiceSrv.getByNameMVNO("google_maps_key");
            if(clientService != null) {
                String googleMapKey = clientService.getValue();
                Request request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/place/queryautocomplete/json?input="+query+"&key="+googleMapKey)
                        .method("GET", null)
                        .build();
                Response response = client.newCall(request).execute();
                JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);

//            StaffUser staffUser = new Gson().fromJson(response.body().string(), StaffUser.class);
                if(jobj.has(MapConstants.STATUS) && jobj.get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                    if (jobj.has(MapConstants.PREDICTIONS)) {
                        for (JsonElement jsonElement : jobj.get(MapConstants.PREDICTIONS).getAsJsonArray()) {
                            LocationPlace locationPlace = new LocationPlace();
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            if (jsonObject.has(MapConstants.DESCRIPTION))
                                locationPlace.setAddress(jsonObject.get(MapConstants.DESCRIPTION).getAsString());
                            if (jsonObject.has(MapConstants.PLACE_ID))
                                locationPlace.setPlaceId(jsonObject.get(MapConstants.PLACE_ID).getAsString());
                            if (jsonObject.has(MapConstants.STRUCTURED_FORMATTING))
                                locationPlace.setName(jsonObject.get(MapConstants.STRUCTURED_FORMATTING).getAsJsonObject().get(MapConstants.MAIN_TEXT).getAsString());
                            locationPlaceList.add(locationPlace);
                        }
                    }
                    resp.put(MapConstants.LOCATIONS, locationPlaceList);
                    resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
                } else {
                    resp.put("error", jobj.has("error_message") ? jobj.get("error_message").getAsString() : "Location not found '" + query + "', please try with another location");
                    resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
                }
                return resp;
            } else {
                throw new CustomValidationException(APIConstants.NOT_FOUND,"google_maps_key Not Available!",null);
            }
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(ex.getErrCode(), ex.getMessage(), null);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public HashMap getLatitudeAndLongitude(String placeId) {
        HashMap<String, Object> resp = new HashMap<>();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();
            String googleMapKey = clientServiceSrv.getByNameMVNO("google_maps_key").getValue();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/details/json?fields=name,geometry,formatted_address&place_id="+placeId+"&key="+googleMapKey)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);

            if(jobj.has(MapConstants.STATUS) && jobj.get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                if (jobj.has(MapConstants.RESULT)) {
                    LocationVo locationVo = new LocationVo();
                    JsonObject jsonObject = jobj.get(MapConstants.RESULT).getAsJsonObject();
                    if (jsonObject.has(MapConstants.GEOMETRY) && jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().has(MapConstants.LOCATION))
                        locationVo.setLatitude(jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LAT).getAsString());
                    if (jsonObject.has(MapConstants.GEOMETRY) && jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().has(MapConstants.LOCATION))
                        locationVo.setLongitude(jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LNG).getAsString());
                    resp.put(MapConstants.LOCATION, locationVo);
                    resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
                }
            } else {
                resp.put("error", jobj.has("error_message") ? jobj.get("error_message").getAsString() : "Issue occurred while fetching location by placeId : "+placeId);
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
            }
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }


    private Long meterToKm(Long meter){
        Long rem = meter % 1000;
        Long result = meter / 1000;
        if(rem >= 500)
            result+=1;
        return result;
    }
}
