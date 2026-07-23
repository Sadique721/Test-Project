class CreditNoteRequest {
  String? type;
  int? page;
  int? pageSize;

  CreditNoteRequest({this.type,this.page, this.pageSize});

  CreditNoteRequest.fromJson(Map<String, dynamic> json) {
    type = json['type'];
    page = json['page'];
    pageSize = json['pageSize'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['type'] = this.type;
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    return data;
  }
}