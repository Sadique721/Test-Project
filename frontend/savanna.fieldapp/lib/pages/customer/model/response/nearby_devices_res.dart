import 'package:savbill/webservices/base_response.dart';

class NearbyDevicesRes extends BaseResponse {
  List<NearByDeviceDetail>? locations;

  NearbyDevicesRes({code, error, timestamp, status, this.locations});

  NearbyDevicesRes.fromJson(Map<String, dynamic> json) {
    error = json['error'];
    timestamp = json['timestamp'];
    status = json['status'];
    code = json['code'];
    if (json['locations'] != null) {
      locations = <NearByDeviceDetail>[];
      json['locations'].forEach((v) {
        locations!.add(new NearByDeviceDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['error'] = this.error;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['code'] = this.code;
    if (this.locations != null) {
      data['locations'] = this.locations!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class NearByDeviceDetail {
  String? address;
  String? latitude;
  String? longitude;
  String? name;
  num? distance;
  num? networkDeviceId;

  NearByDeviceDetail(
      {this.address,
      this.latitude,
      this.longitude,
      this.name,
      this.distance,
      this.networkDeviceId});

  NearByDeviceDetail.fromJson(Map<String, dynamic> json) {
    address = json['address'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    name = json['name'];
    distance = json['distance'];
    networkDeviceId = json['networkDeviceId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['address'] = this.address;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['name'] = this.name;
    data['distance'] = this.distance;
    data['networkDeviceId'] = this.networkDeviceId;
    return data;
  }
}
