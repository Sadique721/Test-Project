import 'package:savbill/webservices/base_response.dart';

class CustomerLedgerRes  extends BaseResponse {
  CustomerLedgerDtls? customerLedgerDtls;

  CustomerLedgerRes({this.customerLedgerDtls, timestamp, status});

  CustomerLedgerRes.fromJson(Map<String, dynamic> json) {
    customerLedgerDtls = json['customerLedgerDtls'] != null
        ? new CustomerLedgerDtls.fromJson(json['customerLedgerDtls'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customerLedgerDtls != null) {
      data['customerLedgerDtls'] = this.customerLedgerDtls!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustomerLedgerDtls {
  int? custId;
  String? username;
  String? custname;
  String? plan;
  String? address;
  String? zonename;
  String? status;
  CustomerLedgerInfo? customerLedgerInfoPojo;

  CustomerLedgerDtls(
      {this.custId,
        this.username,
        this.custname,
        this.plan,
        this.address,
        this.zonename,
        this.status,
        this.customerLedgerInfoPojo});

  CustomerLedgerDtls.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    username = json['username'];
    custname = json['custname'];
    plan = json['plan'];
    address = json['address'];
    zonename = json['zonename'];
    status = json['status'];
    customerLedgerInfoPojo = json['customerLedgerInfoPojo'] != null
        ? new CustomerLedgerInfo.fromJson(json['customerLedgerInfoPojo'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    data['username'] = this.username;
    data['custname'] = this.custname;
    data['plan'] = this.plan;
    data['address'] = this.address;
    data['zonename'] = this.zonename;
    data['status'] = this.status;
    if (this.customerLedgerInfoPojo != null) {
      data['customerLedgerInfoPojo'] = this.customerLedgerInfoPojo!.toJson();
    }
    return data;
  }
}

class CustomerLedgerInfo {
  num? openingAmount;
  List<LedgerDebitCreditDetail>? debitCreditDetail;
  num? closingBalance;

  CustomerLedgerInfo(
      {this.openingAmount, this.debitCreditDetail, this.closingBalance});

  CustomerLedgerInfo.fromJson(Map<String, dynamic> json) {
    openingAmount = json['openingAmount'];
    if (json['debitCreditDetail'] != null) {
      debitCreditDetail = <LedgerDebitCreditDetail>[];
      json['debitCreditDetail'].forEach((v) {
        debitCreditDetail!.add(new LedgerDebitCreditDetail.fromJson(v));
      });
    }
    closingBalance = json['closingBalance'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['openingAmount'] = this.openingAmount;
    if (this.debitCreditDetail != null) {
      data['debitCreditDetail'] =
          this.debitCreditDetail!.map((v) => v.toJson()).toList();
    }
    data['closingBalance'] = this.closingBalance;
    return data;
  }
}

class LedgerDebitCreditDetail {
  int? id;
  int? custId;
  String? description;
  String? transtype;
  String? transcategory;
  String? cREATEDATE;
  String? eNDDATE;
  String? paymentMode;
  String? paymentRefNo;
  String? receiptNo;
  String? remarks;
  num? refNo;
  num? balAmount;
  num? amount;
  String? createDATE;
  String? endDATE;
  List<String?>? invoiceNo;

  LedgerDebitCreditDetail(
      {this.id,
        this.custId,
        this.description,
        this.transtype,
        this.transcategory,
        this.cREATEDATE,
        this.eNDDATE,
        this.paymentMode,
        this.paymentRefNo,
        this.receiptNo,
        this.remarks,
        this.refNo,
        this.balAmount,
        this.amount,
        this.createDATE,
        this.endDATE,
        this.invoiceNo,});

  LedgerDebitCreditDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    description = json['description'];
    transtype = json['transtype'];
    transcategory = json['transcategory'];
    cREATEDATE = json['CREATE_DATE'];
    eNDDATE = json['END_DATE'];
    paymentMode = json['paymentMode'];
    paymentRefNo = json['paymentRefNo'];
    receiptNo = json['receiptNo'];
    remarks = json['remarks'];
    refNo = json['refNo'];
    balAmount = json['balAmount'];
    amount = json['amount'];
    createDATE = json['create_DATE'];
    endDATE = json['end_DATE'];
    invoiceNo = json['invoiceNo'].cast<String?>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['description'] = this.description;
    data['transtype'] = this.transtype;
    data['transcategory'] = this.transcategory;
    data['CREATE_DATE'] = this.cREATEDATE;
    data['END_DATE'] = this.eNDDATE;
    data['paymentMode'] = this.paymentMode;
    data['paymentRefNo'] = this.paymentRefNo;
    data['receiptNo'] = this.receiptNo;
    data['remarks'] = this.remarks;
    data['refNo'] = this.refNo;
    data['balAmount'] = this.balAmount;
    data['amount'] = this.amount;
    data['create_DATE'] = this.createDATE;
    data['end_DATE'] = this.endDATE;
    data['invoiceNo'] = this.invoiceNo;
    return data;
  }
}