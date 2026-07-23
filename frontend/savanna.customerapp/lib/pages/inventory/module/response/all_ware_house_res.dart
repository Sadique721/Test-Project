class GetAllWareHousesRes {
  int? id;
  String? name;
  dynamic mvnoId;
  int? identityKey;

  GetAllWareHousesRes({this.id, this.name, this.mvnoId, this.identityKey});

  GetAllWareHousesRes.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
