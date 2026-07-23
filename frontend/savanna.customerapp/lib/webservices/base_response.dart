class BaseResponse {
  int? status;
  dynamic responseCode;
  int? code;
  String? responseMessage;
  String? message;
  String? msg;
  String? error;
  String? timestamp;
  String? ERROR;
  String? errorMessage;

  BaseResponse({
    this.timestamp,
    this.status,
    this.message,
    this.responseCode,
    this.responseMessage,
    this.msg,
    this.error,
    this.code,
    this.ERROR,
    this.errorMessage
  });

  BaseResponse.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    message = json['message'];
    timestamp = json['timestamp'];
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    msg = json['msg'];
    error = json['error'];
    code = json['code'];
    ERROR = json['ERROR'];
    errorMessage = json['errorMessage'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['msg'] = this.msg;
    data['error'] = this.error;
    data['code'] = this.code;
    data['ERROR'] = this.ERROR;
    data['errorMessage'] = this.errorMessage;
    return data;
  }
}
