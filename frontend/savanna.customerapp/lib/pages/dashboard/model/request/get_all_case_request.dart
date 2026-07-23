class GetAllCaseRequest {
  int? page;
  int? pageSize;
  String? sortBy; //nextFollowupDate
  int? sortOrder; // 1 for ascending, 0 for descending

  GetAllCaseRequest({this.page, this.pageSize, this.sortBy, this.sortOrder});

  GetAllCaseRequest.fromJson(Map<String, dynamic> json) {
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
