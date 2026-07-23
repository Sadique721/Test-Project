class PageRequest {
  int? page;
  int? pageSize;

  PageRequest({this.page, this.pageSize});

  PageRequest.fromJson(Map<String, dynamic> json) {
    page = json['page'];
    pageSize = json['pageSize'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    return data;
  }
}
