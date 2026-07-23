class AddEditWareHouseReq {
  String? id;
  String? name;
  String? description;
  String? status;
  String? address1;
  String? address2;
  int? pincode;
  int? city;
  int? state;
  int? country;
  String? longitude;
  String? latitude;
  int? mvnoId;
  List<int>? parentServiceAreaIdsList;
  List<int>? serviceAreaIdsList;
  String? warehouseType;
  int? branchId;
  String? warehouseCode;
  List<int>? teamsIdsList;

  AddEditWareHouseReq(
      {this.id,
      this.name,
      this.description,
      this.status,
      this.address1,
      this.address2,
      this.pincode,
      this.city,
      this.state,
      this.country,
      this.longitude,
      this.latitude,
      this.mvnoId,
      this.parentServiceAreaIdsList,
      this.serviceAreaIdsList,
      this.warehouseType,
      this.branchId,this.warehouseCode,
        this.teamsIdsList});

  AddEditWareHouseReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    address1 = json['address1'];
    address2 = json['address2'];
    pincode = json['pincode'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    longitude = json['longitude'];
    latitude = json['latitude'];
    mvnoId = json['mvnoId'];
    parentServiceAreaIdsList = json['parentServiceAreaIdsList'].cast<int>();
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    warehouseType = json['warehouseType'];
    branchId = json['branchId'];
    warehouseCode = json['warehouseCode'];
    teamsIdsList = json['teamsIdsList'].cast<int>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['pincode'] = this.pincode;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['longitude'] = this.longitude;
    data['latitude'] = this.latitude;
    data['mvnoId'] = this.mvnoId;
    data['parentServiceAreaIdsList'] = this.parentServiceAreaIdsList;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['warehouseType'] = this.warehouseType;
    data['branchId'] = this.branchId;
    data['warehouseCode'] = this.warehouseCode;
    data['teamsIdsList'] = this.teamsIdsList;
    return data;
  }
}
