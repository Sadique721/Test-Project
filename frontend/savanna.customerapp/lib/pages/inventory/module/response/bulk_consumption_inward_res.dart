import 'package:savbill/webservices/base_response.dart';

class BulkConsumptionInwardRes extends BaseResponse {
  List<BulkConsumptionInward>? dataList;

  BulkConsumptionInwardRes({responseCode, responseMessage, this.dataList});

  BulkConsumptionInwardRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BulkConsumptionInward>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BulkConsumptionInward.fromJson(v));
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

class BulkConsumptionInward {
  int? id;
  String? inwardNumber;

  BulkConsumptionInward({this.id, this.inwardNumber});

  BulkConsumptionInward.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardNumber = json['inwardNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardNumber'] = this.inwardNumber;
    return data;
  }
}
