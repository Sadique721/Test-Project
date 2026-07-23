class CustUploadDocumentReq {
  String? custId;
  num? docId;
  String? docType;
  String? docSubType;
  String? docStatus;
  String? remark;
  String? startDate;
  String? mode;
  String? documentNumber;
  String? endDate;
  String? filename;

  CustUploadDocumentReq(
      {this.custId,
     this.docId,
        this.docType,
        this.docSubType,
        this.docStatus,
        this.remark,
        this.startDate,
        this.mode,
        this.documentNumber,
        this.endDate,
        this.filename});

  CustUploadDocumentReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    docId = json['docId'];
    docType = json['docType'];
    docSubType = json['docSubType'];
    docStatus = json['docStatus'];
    remark = json['remark'];
    startDate = json['startDate'];
    mode = json['mode'];
    documentNumber = json['documentNumber'];
    endDate = json['endDate'];
    filename = json['filename'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    data['docId'] = this.docId;
    data['docType'] = this.docType;
    data['docSubType'] = this.docSubType;
    data['docStatus'] = this.docStatus;
    data['remark'] = this.remark;
    data['startDate'] = this.startDate;
    data['mode'] = this.mode;
    data['documentNumber'] = this.documentNumber;
    data['endDate'] = this.endDate;
    data['filename'] = this.filename;
    return data;
  }
}



class UploadDocRequest {
  List<CustUploadDocumentReq>? docDetailsList;

  UploadDocRequest({this.docDetailsList});

  UploadDocRequest.fromJson(Map<String, dynamic> json) {
    if (json['docDetailsList'] != null) {
      docDetailsList = <CustUploadDocumentReq>[];
      json['docDetailsList'].forEach((v) {
        docDetailsList!.add(CustUploadDocumentReq.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.docDetailsList != null) {
      data['docDetailsList'] =
          this.docDetailsList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

