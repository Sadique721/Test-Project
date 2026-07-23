import 'package:savbill/webservices/base_response.dart';

class BalanceCommissionForShiftLocationRes extends BaseResponse{
  BalanceAndCommissionInfo? balanceAndCommissionInfo;
  String? timestamp;
  int? status;

  BalanceCommissionForShiftLocationRes(
      {this.balanceAndCommissionInfo, this.timestamp, this.status});

  BalanceCommissionForShiftLocationRes.fromJson(Map<String, dynamic> json) {
    balanceAndCommissionInfo = json['balanceAndCommissionInfo'] != null
        ? new BalanceAndCommissionInfo.fromJson(
        json['balanceAndCommissionInfo'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.balanceAndCommissionInfo != null) {
      data['balanceAndCommissionInfo'] =
          this.balanceAndCommissionInfo!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class BalanceAndCommissionInfo {
  bool? isInvoiceClear;
  double? transferBalance;
  double? transferCommission;

  BalanceAndCommissionInfo(
      {this.isInvoiceClear, this.transferBalance, this.transferCommission});

  BalanceAndCommissionInfo.fromJson(Map<String, dynamic> json) {
    isInvoiceClear = json['isInvoiceClear'];
    transferBalance = json['transferBalance'];
    transferCommission = json['transferCommission'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['isInvoiceClear'] = this.isInvoiceClear;
    data['transferBalance'] = this.transferBalance;
    data['transferCommission'] = this.transferCommission;
    return data;
  }
}
