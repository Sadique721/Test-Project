import 'package:savbill/pages/customer/model/request/filters.dart';

class CategorySearchReq {
  List<Filters>? filter;

  CategorySearchReq({this.filter});

  CategorySearchReq.fromJson(Map<String, dynamic> json) {
    if (json['filter'] != null) {
      filter = <Filters>[];
      json['filter'].forEach((v) {
        filter!.add(new Filters.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.filter != null) {
      data['filter'] = this.filter!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
