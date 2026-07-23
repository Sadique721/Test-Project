import 'package:savbill/pages/customer/model/request/filters.dart';

class CustomerListRequest {
  int? page;
  int? pageSize;
  String? sortBy;
  int? sortOrder; // 1 for ascending, 0 for descending
  List<Filters>? filters;
  String? status;

  CustomerListRequest(
      {this.page, this.pageSize, this.sortBy, this.sortOrder, this.filters,this.status});

  CustomerListRequest.fromJson(Map<String, dynamic> json) {
    page = json['page'];
    pageSize = json['pageSize'];
    sortBy = json['sortBy'];
    sortOrder = json['sortOrder'];
    if (json['filters'] != null) {
      filters = <Filters>[];
      json['filters'].forEach((v) {
        filters!.add(new Filters.fromJson(v));
      });
    }
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    data['sortBy'] = this.sortBy;
    data['sortOrder'] = this.sortOrder;
    if (this.filters != null) {
      data['filters'] = this.filters!.map((v) => v.toJson()).toList();
    }
    data['status'] = this.status;
    return data;
  }
}
