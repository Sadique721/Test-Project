import 'package:savbill/pages/model/page_request.dart';

class CustAssigninwardNewReq {
  int? productId;
  String? ownerId;
  String? ownerType;
  PageRequest? paginationRequestDTO;

  CustAssigninwardNewReq(
      {this.productId,
        this.ownerId,
        this.ownerType,
        this.paginationRequestDTO});

  CustAssigninwardNewReq.fromJson(Map<String, dynamic> json) {
    productId = json['productId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    paginationRequestDTO = json['paginationRequestDTO'] != null
        ? new PageRequest.fromJson(json['paginationRequestDTO'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['productId'] = this.productId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    if (this.paginationRequestDTO != null) {
      data['paginationRequestDTO'] = this.paginationRequestDTO!.toJson();
    }
    return data;
  }
}

