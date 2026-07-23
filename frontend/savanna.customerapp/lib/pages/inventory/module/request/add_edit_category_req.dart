class AddEditCategory {
  int? id;
  String? name;
  String? unit;
  String? type;
  String? status;
  List<SpecificationParametersDTOList>? specificationParametersDTOList;
  bool? hasMac;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  String? expiryTime;
  String? expiryTimeUnit;
  String? productId;
  String? dtvCategory;
  String? deviceType;

  AddEditCategory(
      {this.id,
        this.name,
        this.unit,
        this.type,
        this.status,
        this.specificationParametersDTOList,
        this.hasMac,
        this.hasSerial,
        this.hasTrackable,
        this.hasPort,
        this.hasCas,
        this.expiryTime,
        this.expiryTimeUnit,
        this.productId,
        this.dtvCategory,
        this.deviceType
      });

  AddEditCategory.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    type = json['type'];
    status = json['status'];
    if (json['specificationParametersDTOList'] != null) {
      specificationParametersDTOList = <SpecificationParametersDTOList>[];
      json['specificationParametersDTOList'].forEach((v) {
        specificationParametersDTOList!
            .add(new SpecificationParametersDTOList.fromJson(v));
      });
    }
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    productId = json['productId'];
    dtvCategory = json['dtvCategory'];
    deviceType = json['deviceType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['unit'] = this.unit;
    data['type'] = this.type;
    data['status'] = this.status;
    if (this.specificationParametersDTOList != null) {
      data['specificationParametersDTOList'] =
          this.specificationParametersDTOList!.map((v) => v.toJson()).toList();
    }
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['productId'] = this.productId;
    data['dtvCategory'] = this.dtvCategory;
    data['deviceType'] = this.deviceType;
    return data;
  }
}
class SpecificationParametersDTOList {
  String? paramName;
  bool? isMandatory;
  bool? isEditing;
  Null? paramMultiValues;
  bool? isMultiValueParam;

  SpecificationParametersDTOList(
      {this.paramName,
        this.isMandatory,
        this.isEditing,
        this.paramMultiValues,
        this.isMultiValueParam});

  SpecificationParametersDTOList.fromJson(Map<String, dynamic> json) {
    paramName = json['paramName'];
    isMandatory = json['isMandatory'];
    isEditing = json['isEditing'];
    paramMultiValues = json['paramMultiValues'];
    isMultiValueParam = json['isMultiValueParam'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paramName'] = this.paramName;
    data['isMandatory'] = this.isMandatory;
    data['isEditing'] = this.isEditing;
    data['paramMultiValues'] = this.paramMultiValues;
    data['isMultiValueParam'] = this.isMultiValueParam;
    return data;
  }
}
