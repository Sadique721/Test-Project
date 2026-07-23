import 'package:savbill/webservices/base_response.dart';

class OnlinePaymentAuditRes extends BaseResponse{

  List<OnlineAuditData>? onlineAuditData;
  String? message;
  String? timestamp;

  OnlinePaymentAuditRes(
      {
        this.onlineAuditData,
        this.message,
        this.timestamp});

  OnlinePaymentAuditRes.fromJson(Map<String, dynamic> json) {
    // status = json['Status'];
    responseCode = json['ResponseCode'];
    if (json['onlineAuditData'] != null) {
      onlineAuditData = <OnlineAuditData>[];
      json['onlineAuditData'].forEach((v) {
        onlineAuditData!.add(new OnlineAuditData.fromJson(v));
      });
    }
    message = json['message'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    // data['Status'] = this.status;
    data['ResponseCode'] = this.responseCode;
    if (this.onlineAuditData != null) {
      data['onlineAuditData'] =
          this.onlineAuditData!.map((v) => v.toJson()).toList();
    }
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class OnlineAuditData {
  dynamic id;
  dynamic orderId;
  dynamic custId;
  double? payment;
  String? status;
  String? gatewayStatus;
  dynamic pgTransactionId;
  dynamic linkId;
  String? paymentDate;
  dynamic planId;
  bool? isFromCaptive;
  String? merchantName;
  String? accountNumber;
  String? failureDescription;
  String? transactionDate;
  String? payerMobileNumber;
  String? customerUsername;
  dynamic mvnoid;
  dynamic buid;
  dynamic creditDocumentId;
  String? paymentLink;
  String? autoPaymentInitiator;

  OnlineAuditData(
      {this.id,
        this.orderId,
        this.custId,
        this.payment,
        this.status,
        this.gatewayStatus,
        this.pgTransactionId,
        this.linkId,
        this.paymentDate,
        this.planId,
        this.isFromCaptive,
        this.merchantName,
        this.accountNumber,
        this.failureDescription,
        this.transactionDate,
        this.payerMobileNumber,
        this.customerUsername,
        this.mvnoid,
        this.buid,
        this.creditDocumentId,
        this.paymentLink,
        this.autoPaymentInitiator,
      });

  OnlineAuditData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    orderId = json['orderId'];
    custId = json['custId'];
    payment = json['payment'];
    status = json['status'];
    gatewayStatus = json['gatewayStatus'];
    pgTransactionId = json['pgTransactionId'];
    linkId = json['linkId'];
    paymentDate = json['paymentDate'];
    planId = json['planId'];
    isFromCaptive = json['isFromCaptive'];
    merchantName = json['merchantName'];
    accountNumber = json['accountNumber'];
    failureDescription = json['failureDescription'];
    transactionDate = json['transactionDate'];
    payerMobileNumber = json['payerMobileNumber'];
    customerUsername = json['customerUsername'];
    mvnoid = json['mvnoid'];
    buid = json['buid'];
    creditDocumentId = json['creditDocumentId'];
    paymentLink = json['paymentLink'];
    autoPaymentInitiator = json['autoPaymentInitiator'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['orderId'] = this.orderId;
    data['custId'] = this.custId;
    data['payment'] = this.payment;
    data['status'] = this.status;
    data['gatewayStatus'] = this.gatewayStatus;
    data['pgTransactionId'] = this.pgTransactionId;
    data['linkId'] = this.linkId;
    data['paymentDate'] = this.paymentDate;
    data['planId'] = this.planId;
    data['isFromCaptive'] = this.isFromCaptive;
    data['merchantName'] = this.merchantName;
    data['accountNumber'] = this.accountNumber;
    data['failureDescription'] = this.failureDescription;
    data['transactionDate'] = this.transactionDate;
    data['payerMobileNumber'] = this.payerMobileNumber;
    data['customerUsername'] = this.customerUsername;
    data['mvnoid'] = this.mvnoid;
    data['buid'] = this.buid;
    data['creditDocumentId'] = this.creditDocumentId;
    data['paymentLink'] = this.paymentLink;
    data['autoPaymentInitiator'] = this.autoPaymentInitiator;
    return data;
  }
}
