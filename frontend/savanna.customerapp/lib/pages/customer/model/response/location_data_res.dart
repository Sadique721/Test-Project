import 'package:savbill/webservices/base_response.dart';

class LocationDataRes extends BaseResponse {
  List<LocationDetail>? locations;

  LocationDataRes({code, this.locations, timestamp, status});

  LocationDataRes.fromJson(Map<String, dynamic> json) {
    code = json['code'];
    if (json['locations'] != null) {
      locations = <LocationDetail>[];
      json['locations'].forEach((v) {
        locations!.add(new LocationDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['code'] = this.code;
    if (this.locations != null) {
      data['locations'] = this.locations!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class LocationDetail {
  String? placeId;
  String? name;
  String? address;

  LocationDetail({this.placeId, this.name, this.address});

  LocationDetail.fromJson(Map<String, dynamic> json) {
    placeId = json['placeId'];
    name = json['name'];
    address = json['address'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['placeId'] = this.placeId;
    data['name'] = this.name;
    data['address'] = this.address;
    return data;
  }
}
