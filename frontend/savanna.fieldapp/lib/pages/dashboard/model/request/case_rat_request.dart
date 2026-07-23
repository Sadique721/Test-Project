class CaseRatRequest {
  int? caseId;
  String? customerFeedback;
  int? rating;

  CaseRatRequest({this.caseId, this.customerFeedback, this.rating});

  CaseRatRequest.fromJson(Map<String, dynamic> json) {
    caseId = json['caseId'];
    customerFeedback = json['customerFeedback'];
    rating = json['rating'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['caseId'] = this.caseId;
    data['customerFeedback'] = this.customerFeedback;
    data['rating'] = this.rating;
    return data;
  }
}
