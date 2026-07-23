class TicketETRCustomerReq {
  int? custId;
  String? customerEmailId;
  String? customerMobileNo;
  int? mvnoId;
  String? notificationDate;
  String? notificationTime;
  String? remark;
  SelectedNotificationType? selectedNotificationType;
  int? staffId;
  String? templateContent;
  int? ticketId;
  String? ticketNumber;
  bool? isTemplateDynamic;
  String? status;
  String? sender;

  TicketETRCustomerReq(
      {this.custId,
      this.customerEmailId,
      this.customerMobileNo,
      this.mvnoId,
      this.notificationDate,
      this.notificationTime,
      this.remark,
      this.selectedNotificationType,
      this.staffId,
      this.templateContent,
      this.ticketId,
      this.ticketNumber,
      this.isTemplateDynamic,
      this.status,
      this.sender});

  TicketETRCustomerReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    customerEmailId = json['customerEmailId'];
    customerMobileNo = json['customerMobileNo'];
    mvnoId = json['mvnoId'];
    notificationDate = json['notificationDate'];
    notificationTime = json['notificationTime'];
    remark = json['remark'];
    selectedNotificationType = json['selectedNotificationType'] != null
        ? new SelectedNotificationType.fromJson(
            json['selectedNotificationType'])
        : null;
    staffId = json['staffId'];
    templateContent = json['templateContent'];
    ticketId = json['ticketId'];
    ticketNumber = json['ticketNumber'];
    isTemplateDynamic = json['isTemplateDynamic'];
    status = json['status'];
    sender = json['sender'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    data['customerEmailId'] = this.customerEmailId;
    data['customerMobileNo'] = this.customerMobileNo;
    data['mvnoId'] = this.mvnoId;
    data['notificationDate'] = this.notificationDate;
    data['notificationTime'] = this.notificationTime;
    data['remark'] = this.remark;
    if (this.selectedNotificationType != null) {
      data['selectedNotificationType'] =
          this.selectedNotificationType!.toJson();
    }
    data['staffId'] = this.staffId;
    data['templateContent'] = this.templateContent;
    data['ticketId'] = this.ticketId;
    data['ticketNumber'] = this.ticketNumber;
    data['isTemplateDynamic'] = this.isTemplateDynamic;
    data['status'] = this.status;
    data['sender'] = this.sender;
    return data;
  }
}

class SelectedNotificationType {
  bool? sms;
  bool? email;

  SelectedNotificationType({this.sms, this.email});

  SelectedNotificationType.fromJson(Map<String, dynamic> json) {
    sms = json['sms'];
    email = json['email'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['sms'] = this.sms;
    data['email'] = this.email;
    return data;
  }
}
