class AddUpdateManufactureReq {
  String? name;
  String? status;
  int? id;
  int? mvnoId;
  bool? delete;
  bool? isDelete;

  AddUpdateManufactureReq(
      {this.name,
        this.status,
        this.id,
        this.mvnoId,
        this.delete,
        this.isDelete});

  AddUpdateManufactureReq.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    status = json['status'];
    id = json['id'];
    mvnoId = json['mvnoId'];
    delete = json['delete'];
    isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['status'] = this.status;
    data['id'] = this.id;
    data['mvnoId'] = this.mvnoId;
    data['delete'] = this.delete;
    data['isDelete'] = this.isDelete;
    return data;
  }
}
