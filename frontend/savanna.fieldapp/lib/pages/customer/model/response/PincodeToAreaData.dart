import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PinCodeToAreaData extends BaseResponse{

  List<PincodeAreaDetail>? areaList;

  PinCodeToAreaData({timestamp, status, this.areaList});

  PinCodeToAreaData.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    if (json['areaList'] != null) {
      areaList = <PincodeAreaDetail>[];
      json['areaList'].forEach((v) {
        areaList!.add(new PincodeAreaDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}