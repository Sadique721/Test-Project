import 'package:savbill/webservices/base_response.dart';

class CustomerChangeStatusRes extends BaseResponse {
  List<ChangeStatusDetail>? customer;

  CustomerChangeStatusRes({this.customer, timestamp, status});

  CustomerChangeStatusRes.fromJson(Map<String, dynamic> json) {
    if (json['customer'] != null) {
      customer = <ChangeStatusDetail>[];
      json['customer'].forEach((v) {
        customer!.add(new ChangeStatusDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customer != null) {
      data['customer'] = this.customer!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ChangeStatusDetail {
  int? id;
  String? custName;
  String? currentStatus;
  String? activeStatus;
  String? currentStaff;
  String? parentStaff;
  String? status;
  int? customerID;
  String? firstName;
  String? lastName;

  ChangeStatusDetail(
      {this.id,
      this.custName,
      this.currentStatus,
      this.activeStatus,
      this.currentStaff,
      this.parentStaff,
      this.status,
      this.customerID,
      this.firstName,
      this.lastName});

  ChangeStatusDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custName = json['custName'];
    currentStatus = json['currentStatus'];
    activeStatus = json['activeStatus'];
    currentStaff = json['currentStaff'];
    parentStaff = json['parentStaff'];
    status = json['status'];
    customerID = json['customerID'];
    firstName = json['firstName'];
    lastName = json['lastName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custName'] = this.custName;
    data['currentStatus'] = this.currentStatus;
    data['activeStatus'] = this.activeStatus;
    data['currentStaff'] = this.currentStaff;
    data['parentStaff'] = this.parentStaff;
    data['status'] = this.status;
    data['customerID'] = this.customerID;
    data['firstName'] = this.firstName;
    data['lastName'] = this.lastName;
    return data;
  }
}
