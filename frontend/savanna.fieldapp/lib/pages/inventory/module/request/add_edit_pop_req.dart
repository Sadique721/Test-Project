class AddEditPopReq {
  int? id;
  String? name;
  String? popCode;
  int? createdById;
  int? lastModifiedById;
  List<int>? serviceAreaIdsList;
  String? status;
  bool? isDeleted;
  String? latitude;
  String? longitude;
  int? mvnoId;

  AddEditPopReq(
      {this.id,
      this.name,
        this.popCode,
      this.createdById,
      this.lastModifiedById,
      this.serviceAreaIdsList,
      this.status,
      this.isDeleted,
      this.latitude,
      this.longitude,
      this.mvnoId});

  AddEditPopReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    popCode = json['popCode'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    status = json['status'];
    isDeleted = json['isDeleted'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['popCode'] = this.popCode;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
