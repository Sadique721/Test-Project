import 'package:savbill/webservices/base_response.dart';

class PartnerListRes extends BaseResponse {
  List<PartnerDetail>? partnerlist;

  PartnerListRes({timestamp, status, this.partnerlist});

  PartnerListRes.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    if (json['partnerlist'] != null) {
      partnerlist = <PartnerDetail>[];
      json['partnerlist'].forEach((v) {
        partnerlist!.add(new PartnerDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.partnerlist != null) {
      data['partnerlist'] = this.partnerlist!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PartnerDetail {
  int? id;
  String? name;
  String? status;

  PartnerDetail({this.id, this.name, this.status});

  PartnerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    return data;
  }
}
