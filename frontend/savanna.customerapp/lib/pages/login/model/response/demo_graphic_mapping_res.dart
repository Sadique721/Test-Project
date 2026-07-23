import 'package:savbill/webservices/base_response.dart';

class GetDemoGraphicMappingRes extends BaseResponse {
  List<Demographicmappingtable>? demographicmappingtable;
  String? timestamp;
  int? status;

  GetDemoGraphicMappingRes(
      {this.demographicmappingtable, this.timestamp, this.status});

  GetDemoGraphicMappingRes.fromJson(Map<String, dynamic> json) {
    if (json['demographicmappingtable'] != null) {
      demographicmappingtable = <Demographicmappingtable>[];
      json['demographicmappingtable'].forEach((v) {
        demographicmappingtable!.add(new Demographicmappingtable.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.demographicmappingtable != null) {
      data['demographicmappingtable'] =
          this.demographicmappingtable!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Demographicmappingtable {
  int? id;
  String? currentName;
  String? newName;
  String? validationRegex;
  int? primaryKey;
  bool? deleteFlag;

  Demographicmappingtable(
      {this.id,
        this.currentName,
        this.newName,
        this.validationRegex,
        this.primaryKey,
        this.deleteFlag});

  Demographicmappingtable.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    currentName = json['currentName'];
    newName = json['newName'];
    validationRegex = json['validationRegex'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['currentName'] = this.currentName;
    data['newName'] = this.newName;
    data['validationRegex'] = this.validationRegex;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}
