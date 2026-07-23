class AssignNonSerializedItemReq {
  int? id;
  int? qty;
  int? productId;
  int? staffId;
  String? inwardId;
  String? assignedDateTime;
  String? status;
  String? mvnoId;
  int? ownerId;
  String? ownerType;
  String? itemTypeFlag;
  int? nonSerializedQty;
  int? itemId;
  String? itemAssemblyStatus;

  AssignNonSerializedItemReq(
      {this.id,
        this.qty,
        this.productId,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.status,
        this.mvnoId,
        this.ownerId,
        this.ownerType,
        this.itemTypeFlag,
        this.nonSerializedQty,
        this.itemId,
        this.itemAssemblyStatus});

  AssignNonSerializedItemReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    itemTypeFlag = json['itemTypeFlag'];
    nonSerializedQty = json['nonSerializedQty'];
    itemId = json['itemId'];
    itemAssemblyStatus = json['itemAssemblyStatus'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['itemTypeFlag'] = this.itemTypeFlag;
    data['nonSerializedQty'] = this.nonSerializedQty;
    data['itemId'] = this.itemId;
    data['itemAssemblyStatus'] = this.itemAssemblyStatus;
    return data;
  }
}
