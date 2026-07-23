import 'package:savbill/webservices/base_response.dart';

class CustRevenueReportRes extends BaseResponse {
  List<CustomerDBRPojos>? customerDBRPojos;
  double? outstandingPending;
  double? outstandingDbr;
  double? outstandingRevenue;

  CustRevenueReportRes(
      {this.customerDBRPojos,
        this.outstandingPending,
        this.outstandingDbr,
        this.outstandingRevenue});

  CustRevenueReportRes.fromJson(Map<String, dynamic> json) {
    if (json['customerDBRPojos'] != null) {
      customerDBRPojos = <CustomerDBRPojos>[];
      json['customerDBRPojos'].forEach((v) {
        customerDBRPojos!.add(new CustomerDBRPojos.fromJson(v));
      });
    }
    outstandingPending = json['outstandingPending'];
    outstandingDbr = json['outstandingDbr'];
    outstandingRevenue = json['outstandingRevenue'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customerDBRPojos != null) {
      data['customerDBRPojos'] =
          this.customerDBRPojos!.map((v) => v.toJson()).toList();
    }
    data['outstandingPending'] = this.outstandingPending;
    data['outstandingDbr'] = this.outstandingDbr;
    data['outstandingRevenue'] = this.outstandingRevenue;
    return data;
  }
}

class CustomerDBRPojos {
  double? dbr;
  double? pendingamt;
  Null? startdate;
  String? date;
  double? cummRevenue;
  String? remark;
  bool? isContainsMultipleService;
  Null? serviceName;
  String? month;

  CustomerDBRPojos(
      {this.dbr,
        this.pendingamt,
        this.startdate,
        this.date,
        this.cummRevenue,
        this.remark,
        this.isContainsMultipleService,
        this.serviceName,
        this.month});

  CustomerDBRPojos.fromJson(Map<String, dynamic> json) {
    dbr = json['dbr'];
    pendingamt = json['pendingamt'];
    startdate = json['startdate'];
    date = json['date'];
    cummRevenue = json['cumm_revenue'];
    remark = json['remark'];
    isContainsMultipleService = json['isContainsMultipleService'];
    serviceName = json['serviceName'];
    month = json['month'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['dbr'] = this.dbr;
    data['pendingamt'] = this.pendingamt;
    data['startdate'] = this.startdate;
    data['date'] = this.date;
    data['cumm_revenue'] = this.cummRevenue;
    data['remark'] = this.remark;
    data['isContainsMultipleService'] = this.isContainsMultipleService;
    data['serviceName'] = this.serviceName;
    data['month'] = this.month;
    return data;
  }
}
