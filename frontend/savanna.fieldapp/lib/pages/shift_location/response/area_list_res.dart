import 'package:savbill/pages/customer/model/response/service_area_detail_res.dart';
import 'package:savbill/webservices/base_response.dart';

class AreaListRes extends BaseResponse {
  List<ServiceAreaDetailData>? dataList;

  AreaListRes({responseCode, responseMessage, this.dataList});

  AreaListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ServiceAreaDetailData>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ServiceAreaDetailData.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
