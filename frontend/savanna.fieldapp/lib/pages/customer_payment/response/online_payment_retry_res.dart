import 'package:savbill/webservices/base_response.dart';

class OnlinePaymentRetryRes{
  String? status;
  int? statusCode;
  List<OnlineAuditRetryData>? onlineAuditData;
  String? message;

  OnlinePaymentRetryRes({this.status, this.statusCode, this.onlineAuditData, this.message});

  OnlinePaymentRetryRes.fromJson(Map<String, dynamic> json) {
    status = json['Status'];
    statusCode = json['statusCode'];
    if (json['onlineAuditData'] != null) {
      onlineAuditData = <OnlineAuditRetryData>[];
      json['onlineAuditData'].forEach((v) {
        onlineAuditData!.add(new OnlineAuditRetryData.fromJson(v));
      });
    }
    message = json['message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['Status'] = this.status;
    data['statusCode'] = this.statusCode;
    if (this.onlineAuditData != null) {
      data['onlineAuditData'] =
          this.onlineAuditData!.map((v) => v.toJson()).toList();
    }
    data['message'] = this.message;
    return data;
  }
}

class OnlineAuditRetryData {
  int? id;
  String? orderId;
  int? custId;
  dynamic payment;
  String? status;
  String? pgTransactionId;
  dynamic linkId;
  String? paymentDate;
  int? planId;
  bool? isFromCaptive;
  String? merchantName;
  String? transactionDate;
  String? customerUsername;
  int? mvnoid;
  dynamic buid;
  dynamic creditDocumentId;
  dynamic paymentLink;
  dynamic checksum;
  dynamic partnerId;
  dynamic partnerPaymentId;
  String? customerUUID;
  bool? isScheduled;
  int? createdById;
  String? createdByName;
  int? invoiceId;
  bool? isAdvancePayment;
  dynamic failureDescription;
  String? accountNumber;
  dynamic walletAmount;
  dynamic planPrice;
  String? gatewayStatus;
  String? payerMobileNumber;
  dynamic autoPaymentInitiator;

  OnlineAuditRetryData(
      {this.id,
        this.orderId,
        this.custId,
        this.payment,
        this.status,
        this.pgTransactionId,
        this.linkId,
        this.paymentDate,
        this.planId,
        this.isFromCaptive,
        this.merchantName,
        this.transactionDate,
        this.customerUsername,
        this.mvnoid,
        this.buid,
        this.creditDocumentId,
        this.paymentLink,
        this.checksum,
        this.partnerId,
        this.partnerPaymentId,
        this.customerUUID,
        this.isScheduled,
        this.createdById,
        this.createdByName,
        this.invoiceId,
        this.isAdvancePayment,
        this.failureDescription,
        this.accountNumber,
        this.walletAmount,
        this.planPrice,
        this.gatewayStatus,
        this.payerMobileNumber,
        this.autoPaymentInitiator});

  OnlineAuditRetryData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    orderId = json['orderId'];
    custId = json['custId'];
    payment = json['payment'];
    status = json['status'];
    pgTransactionId = json['pgTransactionId'];
    linkId = json['linkId'];
    paymentDate = json['paymentDate'];
    planId = json['planId'];
    isFromCaptive = json['isFromCaptive'];
    merchantName = json['merchantName'];
    transactionDate = json['transactionDate'];
    customerUsername = json['customerUsername'];
    mvnoid = json['mvnoid'];
    buid = json['buid'];
    creditDocumentId = json['creditDocumentId'];
    paymentLink = json['paymentLink'];
    checksum = json['checksum'];
    partnerId = json['partnerId'];
    partnerPaymentId = json['partnerPaymentId'];
    customerUUID = json['customerUUID'];
    isScheduled = json['isScheduled'];
    createdById = json['createdById'];
    createdByName = json['createdByName'];
    invoiceId = json['invoiceId'];
    isAdvancePayment = json['isAdvancePayment'];
    failureDescription = json['failureDescription'];
    accountNumber = json['accountNumber'];
    walletAmount = json['walletAmount'];
    planPrice = json['planPrice'];
    gatewayStatus = json['gatewayStatus'];
    payerMobileNumber = json['payerMobileNumber'];
    autoPaymentInitiator = json['autoPaymentInitiator'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['orderId'] = this.orderId;
    data['custId'] = this.custId;
    data['payment'] = this.payment;
    data['status'] = this.status;
    data['pgTransactionId'] = this.pgTransactionId;
    data['linkId'] = this.linkId;
    data['paymentDate'] = this.paymentDate;
    data['planId'] = this.planId;
    data['isFromCaptive'] = this.isFromCaptive;
    data['merchantName'] = this.merchantName;
    data['transactionDate'] = this.transactionDate;
    data['customerUsername'] = this.customerUsername;
    data['mvnoid'] = this.mvnoid;
    data['buid'] = this.buid;
    data['creditDocumentId'] = this.creditDocumentId;
    data['paymentLink'] = this.paymentLink;
    data['checksum'] = this.checksum;
    data['partnerId'] = this.partnerId;
    data['partnerPaymentId'] = this.partnerPaymentId;
    data['customerUUID'] = this.customerUUID;
    data['isScheduled'] = this.isScheduled;
    data['createdById'] = this.createdById;
    data['createdByName'] = this.createdByName;
    data['invoiceId'] = this.invoiceId;
    data['isAdvancePayment'] = this.isAdvancePayment;
    data['failureDescription'] = this.failureDescription;
    data['accountNumber'] = this.accountNumber;
    data['walletAmount'] = this.walletAmount;
    data['planPrice'] = this.planPrice;
    data['gatewayStatus'] = this.gatewayStatus;
    data['payerMobileNumber'] = this.payerMobileNumber;
    data['autoPaymentInitiator'] = this.autoPaymentInitiator;
    return data;
  }
}
