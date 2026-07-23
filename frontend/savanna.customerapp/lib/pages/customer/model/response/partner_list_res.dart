import 'package:savbill/webservices/base_response.dart';

class PartnerListRes extends BaseResponse {
  List<PartnerDetail>? partnerlist;

  PartnerListRes({this.partnerlist, timestamp, status, error});

  PartnerListRes.fromJson(Map<String, dynamic> json) {
    if (json['partnerlist'] != null) {
      partnerlist = <PartnerDetail>[];
      json['partnerlist'].forEach((v) {
        partnerlist!.add(new PartnerDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
    error = json['error'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.partnerlist != null) {
      data['partnerlist'] = this.partnerlist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['error'] = this.error;
    return data;
  }
}

class PartnerDetail {
  int? id;
  String? name;
  String? status;
  num? balance;
  String? nextbilldate;
  String? lastbilldate;
  String? cityName;
  String? countryName;
  String? stateName;
  String? taxName;
  String? parentPartnerName;
  num? outcomeBalance;
  int? mvnoId;

  PartnerDetail(
      {this.id,
      this.name,
      this.status,
      this.balance,
      this.nextbilldate,
      this.lastbilldate,
      this.cityName,
      this.countryName,
      this.stateName,
      this.taxName,
      this.parentPartnerName,
      this.outcomeBalance,
      this.mvnoId});

  PartnerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    balance = json['balance'];
    nextbilldate = json['nextbilldate'];
    lastbilldate = json['lastbilldate'];
    cityName = json['cityName'];
    countryName = json['countryName'];
    stateName = json['stateName'];
    taxName = json['taxName'];
    parentPartnerName = json['parentPartnerName'];
    outcomeBalance = json['outcomeBalance'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['balance'] = this.balance;
    data['nextbilldate'] = this.nextbilldate;
    data['lastbilldate'] = this.lastbilldate;
    data['cityName'] = this.cityName;
    data['countryName'] = this.countryName;
    data['stateName'] = this.stateName;
    data['taxName'] = this.taxName;
    data['parentPartnerName'] = this.parentPartnerName;
    data['outcomeBalance'] = this.outcomeBalance;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
