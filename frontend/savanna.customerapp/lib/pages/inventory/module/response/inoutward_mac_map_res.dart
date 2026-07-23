import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/webservices/base_response.dart';

class InOutwardMacMapRes extends BaseResponse {
  List<InOutWardMACMapping>? dataList;

  InOutwardMacMapRes({responseCode, responseMessage, this.dataList});

  InOutwardMacMapRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];

    if (json['dataList'] != null) {
      dataList = <InOutWardMACMapping>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InOutWardMACMapping.fromJson(v));
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
