import 'package:savbill/webservices/base_response.dart';

class LeadSourceCustomerCRMRes extends BaseResponse {
  List<CustomersList>? customersList;
  String? timestamp;
  int? status;

  LeadSourceCustomerCRMRes({this.customersList, this.timestamp, this.status});

  LeadSourceCustomerCRMRes.fromJson(Map<String, dynamic> json) {
    if (json['customersList'] != null) {
      customersList = <CustomersList>[];
      json['customersList'].forEach((v) {
        customersList!.add(new CustomersList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customersList != null) {
      data['customersList'] =
          this.customersList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustomersList {
  int? id;
  dynamic title;
  dynamic username;
  dynamic password;
  dynamic firstname;
  dynamic lastname;
  dynamic status;
  dynamic mvnoId;
  dynamic buId;
  bool? isDeleted;

  CustomersList(
      {this.id,
        this.title,
        this.username,
        this.password,
        this.firstname,
        this.lastname,
        this.status,
        this.mvnoId,
        this.buId,
        this.isDeleted});

  CustomersList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    title = json['title'];
    username = json['username'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['title'] = this.title;
    data['username'] = this.username;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}
