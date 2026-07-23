class NearbyDevicesReq {
  String? latitude;
  String? longitude;

  NearbyDevicesReq({this.latitude, this.longitude});

  NearbyDevicesReq.fromJson(Map<String, dynamic> json) {
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
