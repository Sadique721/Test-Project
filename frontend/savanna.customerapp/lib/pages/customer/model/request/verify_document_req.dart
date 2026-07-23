class VerifyDocumentRequest {
  num? docId;
  String? documentNumber;
  String? documentType;

  VerifyDocumentRequest({this.docId, this.documentNumber, this.documentType});

  VerifyDocumentRequest.fromJson(Map<String, dynamic> json) {
    docId = json['docId'];
    documentNumber = json['documentNumber'];
    documentType = json['documentType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['docId'] = this.docId;
    data['documentNumber'] = this.documentNumber;
    data['documentType'] = this.documentType;
    return data;
  }
}
