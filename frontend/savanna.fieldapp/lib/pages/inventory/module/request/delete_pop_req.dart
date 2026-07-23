class DeletePopReq {
  int? id;
  int? createdById;
  String? createdByName;
  String? createdate;
  int? lastModifiedById;
  String? lastModifiedByName;
  String? updatedate;
  String? latitude;
  String? longitude;
  String? name;
  String? status;
  int? mvnoId;

  DeletePopReq(
      {this.id,
      this.createdById,
      this.createdByName,
      this.createdate,
      this.lastModifiedById,
      this.lastModifiedByName,
      this.updatedate,
      this.latitude,
      this.longitude,
      this.name,
      this.status,
      this.mvnoId});

  DeletePopReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdById = json['createdById'];
    createdByName = json['createdByName'];
    createdate = json['createdate'];
    lastModifiedById = json['lastModifiedById'];
    lastModifiedByName = json['lastModifiedByName'];
    updatedate = json['updatedate'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    name = json['name'];
    status = json['status'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdById'] = this.createdById;
    data['createdByName'] = this.createdByName;
    data['createdate'] = this.createdate;
    data['lastModifiedById'] = this.lastModifiedById;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['updatedate'] = this.updatedate;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['name'] = this.name;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
