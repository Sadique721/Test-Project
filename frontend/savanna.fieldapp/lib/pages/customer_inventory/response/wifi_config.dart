class WifiConfigResponse {
    Data? data;
    int? responseCode;

    WifiConfigResponse({this.data, this.responseCode});

    WifiConfigResponse.fromJson(Map<String, dynamic> json) {
        responseCode = json['responseCode'];
        data = json['data'] != null ? Data.fromJson(json['data']) : null;
    }

    Map<String, dynamic> toJson() {
        final Map<String, dynamic> map = {};
        map['responseCode'] = responseCode;
        if (data != null) {
            map['data'] = data!.toJson();
        }
        return map;
    }
}

class Data {
    int? itemId;
    int? customerId;
    int? custInvenId;
    String? serialNumber;
    String? ssidUsername;
    String? ssidPassword;
    String? workingFrequency;

    Data({
        this.itemId,
        this.customerId,
        this.custInvenId,
        this.serialNumber,
        this.ssidUsername,
        this.ssidPassword,
        this.workingFrequency,
    });

    Data.fromJson(Map<String, dynamic> json) {
        itemId = json['itemId'];
        customerId = json['customerId'];
        custInvenId = json['custInvenId'];
        serialNumber = json['serialNumber'];
        ssidUsername = json['ssidUsername'];
        ssidPassword = json['ssidPassword'];
        workingFrequency = json['workingFrequency'];
    }

    Map<String, dynamic> toJson() {
        final Map<String, dynamic> map = {};
        map['itemId'] = itemId;
        map['customerId'] = customerId;
        map['custInvenId'] = custInvenId;
        map['serialNumber'] = serialNumber;
        map['ssidUsername'] = ssidUsername;
        map['ssidPassword'] = ssidPassword;
        map['workingFrequency'] = workingFrequency;
        return map;
    }
}
