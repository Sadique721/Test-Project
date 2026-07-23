class CMSLeadUploadDocumentReq {
  String? docStatus;
  String? docSubType;
  String? docType;
  String? endDate;
  String? filename;
  String? documentNumber;
  String? leadMasterId;
  String? remark;
  String? startDate;

  CMSLeadUploadDocumentReq(
      {this.docStatus,
        this.docSubType,
        this.docType,
        this.endDate,
        this.filename,
        this.documentNumber,
        this.leadMasterId,
        this.remark,
        this.startDate});

  CMSLeadUploadDocumentReq.fromJson(Map<String, dynamic> json) {
    docStatus = json['docStatus'];
    docSubType = json['docSubType'];
    docType = json['docType'];
    endDate = json['endDate'];
    filename = json['filename'];
    documentNumber = json['documentNumber'];
    leadMasterId = json['leadMasterId'];
    remark = json['remark'];
    startDate = json['startDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['docStatus'] = this.docStatus;
    data['docSubType'] = this.docSubType;
    data['docType'] = this.docType;
    data['endDate'] = this.endDate;
    data['filename'] = this.filename;
    data['documentNumber'] = this.documentNumber;
    data['leadMasterId'] = this.leadMasterId;
    data['remark'] = this.remark;
    data['startDate'] = this.startDate;
    return data;
  }
}
