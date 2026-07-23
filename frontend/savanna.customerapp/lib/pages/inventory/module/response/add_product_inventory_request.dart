class AddProductInventoryRequest {
  String? onBehalfOf;
  int? requestNameId;
  int? requestToWarehouseId;
  String? reason;
  String? status;
  List<RequestInvenotryProductMappings>? requestInvenotryProductMappings;

  AddProductInventoryRequest(
      {this.onBehalfOf,
        this.requestNameId,
        this.requestToWarehouseId,
        this.reason,
        this.status,
        this.requestInvenotryProductMappings});

  AddProductInventoryRequest.fromJson(Map<String, dynamic> json) {
    onBehalfOf = json['onBehalfOf'];
    requestNameId = json['requestNameId'];
    requestToWarehouseId = json['requestToWarehouseId'];
    reason = json['reason'];
    status = json['status'];
    if (json['requestInvenotryProductMappings'] != null) {
      requestInvenotryProductMappings = <RequestInvenotryProductMappings>[];
      json['requestInvenotryProductMappings'].forEach((v) {
        requestInvenotryProductMappings!
            .add(new RequestInvenotryProductMappings.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['onBehalfOf'] = this.onBehalfOf;
    data['requestNameId'] = this.requestNameId;
    data['requestToWarehouseId'] = this.requestToWarehouseId;
    data['reason'] = this.reason;
    data['status'] = this.status;
    if (this.requestInvenotryProductMappings != null) {
      data['requestInvenotryProductMappings'] =
          this.requestInvenotryProductMappings!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class RequestInvenotryProductMappings {
  int? productCategoryId;
  String? productCategoryName;
  int? productId;
  String? productName;
  String? itemType;
  int? quantity;
  String? id;
  bool? isDeleted;

  RequestInvenotryProductMappings(
      {this.productCategoryId,
        this.productCategoryName,
        this.productId,
        this.productName,
        this.itemType,
        this.quantity,
        this.id,
        this.isDeleted
      });

  RequestInvenotryProductMappings.fromJson(Map<String, dynamic> json) {
    productCategoryId = json['productCategoryId'];
    productCategoryId = json['productCategoryName'];
    productId = json['productId'];
    productId = json['productName'];
    itemType = json['itemType'];
    quantity = json['quantity'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['productCategoryId'] = this.productCategoryId;
    data['productCategoryName'] = this.productCategoryName;
    data['productId'] = this.productId;
    data['productName'] = this.productName;
    data['itemType'] = this.itemType;
    data['quantity'] = this.quantity;
    data['id'] = this.id;
    return data;
  }
}
