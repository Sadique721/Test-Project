import '../../../../webservices/base_response.dart';

class RefreshTokenRes extends BaseResponse{
  String? timestamp;
  int? status;
  String? accessToken;

  RefreshTokenRes({this.accessToken, this.timestamp, this.status});

  RefreshTokenRes.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    accessToken = json['accessToken'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['accessToken'] = this.accessToken;
    return data;
  }
}