class SelcomPayRequest {
  CustomerPaymentDTO? customerPaymentDTO;
  SelcomPayPayment? selcomPayPayment;

  SelcomPayRequest({this.customerPaymentDTO, this.selcomPayPayment});

  SelcomPayRequest.fromJson(Map<String, dynamic> json) {
    customerPaymentDTO = json['customerPaymentDTO'] != null
        ? new CustomerPaymentDTO.fromJson(json['customerPaymentDTO'])
        : null;
    selcomPayPayment = json['selcomPayPayment'] != null
        ? new SelcomPayPayment.fromJson(json['selcomPayPayment'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customerPaymentDTO != null) {
      data['customerPaymentDTO'] = this.customerPaymentDTO!.toJson();
    }
    if (this.selcomPayPayment != null) {
      data['selcomPayPayment'] = this.selcomPayPayment!.toJson();
    }
    return data;
  }
}

class CustomerPaymentDTO {
  int? customerId;
  dynamic buid;
  int? planId;
  String? amount;
  bool? isBuyPlan;
  bool? isFromCaptive;
  String? merchantName;
  String? customerUserName;
  String? customerUUID;
  int? mvnoId;
  int? custServiceMappingId;
  String? mobileNumber;
  dynamic orderId;
  dynamic invoiceId;
  int? partnerId;
  dynamic partnerPaymentId;
  String? status;
  String? requestFor;

  CustomerPaymentDTO(
      {this.customerId,
        this.buid,
        this.planId,
        this.amount,
        this.isBuyPlan,
        this.isFromCaptive,
        this.merchantName,
        this.customerUserName,
        this.customerUUID,
        this.mvnoId,
        this.custServiceMappingId,
        this.mobileNumber,
        this.orderId,
        this.invoiceId,
        this.partnerId,
        this.partnerPaymentId,
        this.status,
      this.requestFor});

  CustomerPaymentDTO.fromJson(Map<String, dynamic> json) {
    customerId = json['customerId'];
    buid = json['buid'];
    planId = json['planId'];
    amount = json['amount'];
    isBuyPlan = json['isBuyPlan'];
    isFromCaptive = json['isFromCaptive'];
    merchantName = json['merchantName'];
    customerUserName = json['customerUserName'];
    customerUUID = json['customerUUID'];
    mvnoId = json['mvnoId'];
    custServiceMappingId = json['custServiceMappingId'];
    mobileNumber = json['mobileNumber'];
    orderId = json['orderId'];
    invoiceId = json['invoiceId'];
    partnerId = json['partnerId'];
    partnerPaymentId = json['partnerPaymentId'];
    status = json['status'];
    requestFor = json['requestFor'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerId'] = this.customerId;
    data['buid'] = this.buid;
    data['planId'] = this.planId;
    data['amount'] = this.amount;
    data['isBuyPlan'] = this.isBuyPlan;
    data['isFromCaptive'] = this.isFromCaptive;
    data['merchantName'] = this.merchantName;
    data['customerUserName'] = this.customerUserName;
    data['customerUUID'] = this.customerUUID;
    data['mvnoId'] = this.mvnoId;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['mobileNumber'] = this.mobileNumber;
    data['orderId'] = this.orderId;
    data['invoiceId'] = this.invoiceId;
    data['partnerId'] = this.partnerId;
    data['partnerPaymentId'] = this.partnerPaymentId;
    data['status'] = this.status;
    data['requestFor'] = this.requestFor;
    return data;
  }
}

class SelcomPayPayment {
  String? vendor;
  Null? orderId;
  String? buyerEmail;
  String? buyerName;
  String? buyerPhone;
  String? gatewayBuyerUuid;
  String? amount;
  String? currency;
  String? paymentMethods;
  String? billingFirstname;
  String? billingLastname;
  String? billingAddress1;
  String? billingCity;
  String? billingStateOrRegion;
  String? billingCountry;
  String? billingPhone;
  int? noOfItems;
  String? webhook;

  SelcomPayPayment(
      {this.vendor,
        this.orderId,
        this.buyerEmail,
        this.buyerName,
        this.buyerPhone,
        this.gatewayBuyerUuid,
        this.amount,
        this.currency,
        this.paymentMethods,
        this.billingFirstname,
        this.billingLastname,
        this.billingAddress1,
        this.billingCity,
        this.billingStateOrRegion,
        this.billingCountry,
        this.billingPhone,
        this.noOfItems,
        this.webhook});

  SelcomPayPayment.fromJson(Map<String, dynamic> json) {
    vendor = json['vendor'];
    orderId = json['order_id'];
    buyerEmail = json['buyer_email'];
    buyerName = json['buyer_name'];
    buyerPhone = json['buyer_phone'];
    gatewayBuyerUuid = json['gateway_buyer_uuid'];
    amount = json['amount'];
    currency = json['currency'];
    paymentMethods = json['payment_methods'];
    billingFirstname = json['billing.firstname'];
    billingLastname = json['billing.lastname'];
    billingAddress1 = json['billing.address_1'];
    billingCity = json['billing.city'];
    billingStateOrRegion = json['billing.state_or_region'];
    billingCountry = json['billing.country'];
    billingPhone = json['billing.phone'];
    noOfItems = json['no_of_items'];
    webhook = json['webhook'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['vendor'] = this.vendor;
    data['order_id'] = this.orderId;
    data['buyer_email'] = this.buyerEmail;
    data['buyer_name'] = this.buyerName;
    data['buyer_phone'] = this.buyerPhone;
    data['gateway_buyer_uuid'] = this.gatewayBuyerUuid;
    data['amount'] = this.amount;
    data['currency'] = this.currency;
    data['payment_methods'] = this.paymentMethods;
    data['billing.firstname'] = this.billingFirstname;
    data['billing.lastname'] = this.billingLastname;
    data['billing.address_1'] = this.billingAddress1;
    data['billing.city'] = this.billingCity;
    data['billing.state_or_region'] = this.billingStateOrRegion;
    data['billing.country'] = this.billingCountry;
    data['billing.phone'] = this.billingPhone;
    data['no_of_items'] = this.noOfItems;
    data['webhook'] = this.webhook;
    return data;
  }
}
