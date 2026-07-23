import 'package:savbill/webservices/base_response.dart';

class LocationLatLongRes extends BaseResponse {
  LocationLatLong? location;

  LocationLatLongRes({code, this.location, timestamp, status, error});

  LocationLatLongRes.fromJson(Map<String, dynamic> json) {
    code = json['code'];
    location = json['location'] != null
        ? new LocationLatLong.fromJson(json['location'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
    error = json['error'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['code'] = this.code;
    if (this.location != null) {
      data['location'] = this.location!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['error'] = this.error;
    return data;
  }
}

class LocationLatLong {
  String? latitude;
  String? longitude;

  LocationLatLong({this.latitude, this.longitude});

  LocationLatLong.fromJson(Map<String, dynamic> json) {
    latitude = json['latitude'];
    longitude = json['longitude'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    return data;
  }
}
