class CustAuditDetailReq {
  int? page;
  int? pageSize;
  String? sortBy;
  int? sortOrder;

  CustAuditDetailReq({this.page, this.pageSize, this.sortBy, this.sortOrder});

  CustAuditDetailReq.fromJson(Map<String, dynamic> json) {
    page = json['page'];
    pageSize = json['pageSize'];
    sortBy = json['sortBy'];
    sortOrder = json['sortOrder'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    data['sortBy'] = this.sortBy;
    data['sortOrder'] = this.sortOrder;
    return data;
  }
}
