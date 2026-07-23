import 'package:savbill/webservices/base_response.dart';

class FinalAmountTaxRes extends BaseResponse{
  Result? result;

  FinalAmountTaxRes({this.result});

  FinalAmountTaxRes.fromJson(Map<String, dynamic> json) {
    result =
    json['result'] != null ? new Result.fromJson(json['result']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.result != null) {
      data['result'] = this.result!.toJson();
    }
    return data;
  }
}

class Result {
  double? finalAmount;

  Result({this.finalAmount});

  Result.fromJson(Map<String, dynamic> json) {
    finalAmount = json['finalAmount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['finalAmount'] = this.finalAmount;
    return data;
  }
}
