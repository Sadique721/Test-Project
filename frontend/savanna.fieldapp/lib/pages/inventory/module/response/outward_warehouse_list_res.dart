import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class OutwardWarehouseListRes extends BaseResponse {
  List<WareHouseDetail>? dataList;

  OutwardWarehouseListRes({responseCode, responseMessage, this.dataList});

  OutwardWarehouseListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <WareHouseDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new WareHouseDetail.fromJson(v));
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
