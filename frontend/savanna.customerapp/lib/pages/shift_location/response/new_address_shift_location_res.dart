import 'package:savbill/webservices/base_response.dart';

// class NewAddressShiftLocationRes extends BaseResponse{
//   NewcustomerAddress? newcustomerAddress;
//   String? timestamp;
//   int? status;
//
//   NewAddressShiftLocationRes(
//       {this.newcustomerAddress, this.timestamp, this.status});
//
//   NewAddressShiftLocationRes.fromJson(Map<String, dynamic> json) {
//     newcustomerAddress = json['newcustomerAddress'] != null
//         ? new NewcustomerAddress.fromJson(json['newcustomerAddress'])
//         : null;
//     timestamp = json['timestamp'];
//     status = json['status'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     if (this.newcustomerAddress != null) {
//       data['newcustomerAddress'] = this.newcustomerAddress!.toJson();
//     }
//     data['timestamp'] = this.timestamp;
//     data['status'] = this.status;
//     return data;
//   }
// }
//
// class NewcustomerAddress {
//   dynamic createdate;
//   dynamic updatedate;
//   dynamic createdByName;
//   dynamic lastModifiedByName;
//   dynamic createdById;
//   dynamic lastModifiedById;
//   int? id;
//   String? addressType;
//   dynamic address1;
//   dynamic address2;
//   String? landmark;
//   dynamic landmark1;
//   int? areaId;
//   int? pincodeId;
//   int? cityId;
//   int? stateId;
//   int? countryId;
//   int? customerId;
//   String? fullAddress;
//   bool? isDelete;
//   dynamic nextTeamHierarchyMappingId;
//   int? nextStaff;
//   String? status;
//   String? version;
//   int? shiftId;
//   dynamic shiftedPartnerId;
//   dynamic shiftedServiceAreaId;
//   String? requestedByName;
//   String? requestedDate;
//   bool? delete;
//
//   NewcustomerAddress(
//       {this.createdate,
//         this.updatedate,
//         this.createdByName,
//         this.lastModifiedByName,
//         this.createdById,
//         this.lastModifiedById,
//         this.id,
//         this.addressType,
//         this.address1,
//         this.address2,
//         this.landmark,
//         this.landmark1,
//         this.areaId,
//         this.pincodeId,
//         this.cityId,
//         this.stateId,
//         this.countryId,
//         this.customerId,
//         this.fullAddress,
//         this.isDelete,
//         this.nextTeamHierarchyMappingId,
//         this.nextStaff,
//         this.status,
//         this.version,
//         this.shiftId,
//         this.shiftedPartnerId,
//         this.shiftedServiceAreaId,
//         this.requestedByName,
//         this.requestedDate,
//         this.delete});
//
//   NewcustomerAddress.fromJson(Map<String, dynamic> json) {
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     id = json['id'];
//     addressType = json['addressType'];
//     address1 = json['address1'];
//     address2 = json['address2'];
//     landmark = json['landmark'];
//     landmark1 = json['landmark1'];
//     areaId = json['areaId'];
//     pincodeId = json['pincodeId'];
//     cityId = json['cityId'];
//     stateId = json['stateId'];
//     countryId = json['countryId'];
//     customerId = json['customerId'];
//     fullAddress = json['fullAddress'];
//     isDelete = json['isDelete'];
//     nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
//     nextStaff = json['nextStaff'];
//     status = json['status'];
//     version = json['version'];
//     shiftId = json['shiftId'];
//     shiftedPartnerId = json['shiftedPartnerId'];
//     shiftedServiceAreaId = json['shiftedServiceAreaId'];
//     requestedByName = json['requestedByName'];
//     requestedDate = json['requestedDate'];
//     delete = json['delete'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['id'] = this.id;
//     data['addressType'] = this.addressType;
//     data['address1'] = this.address1;
//     data['address2'] = this.address2;
//     data['landmark'] = this.landmark;
//     data['landmark1'] = this.landmark1;
//     data['areaId'] = this.areaId;
//     data['pincodeId'] = this.pincodeId;
//     data['cityId'] = this.cityId;
//     data['stateId'] = this.stateId;
//     data['countryId'] = this.countryId;
//     data['customerId'] = this.customerId;
//     data['fullAddress'] = this.fullAddress;
//     data['isDelete'] = this.isDelete;
//     data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
//     data['nextStaff'] = this.nextStaff;
//     data['status'] = this.status;
//     data['version'] = this.version;
//     data['shiftId'] = this.shiftId;
//     data['shiftedPartnerId'] = this.shiftedPartnerId;
//     data['shiftedServiceAreaId'] = this.shiftedServiceAreaId;
//     data['requestedByName'] = this.requestedByName;
//     data['requestedDate'] = this.requestedDate;
//     data['delete'] = this.delete;
//     return data;
//   }
// }
class NewAddressShiftLocationRes extends BaseResponse{
  List<NewcustomerAddress>? newcustomerAddress;
  String? timestamp;
  int? status;

  NewAddressShiftLocationRes(
      {this.newcustomerAddress, this.timestamp, this.status});

  NewAddressShiftLocationRes.fromJson(Map<String, dynamic> json) {
    if (json['newcustomerAddress'] != null) {
      newcustomerAddress = <NewcustomerAddress>[];
      json['newcustomerAddress'].forEach((v) {
        newcustomerAddress!.add(new NewcustomerAddress.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.newcustomerAddress != null) {
      data['newcustomerAddress'] =
          this.newcustomerAddress!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class NewcustomerAddress {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? addressType;
  dynamic address1;
  dynamic address2;
  String? landmark;
  dynamic landmark1;
  int? areaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;
  int? customerId;
  String? fullAddress;
  bool? isDelete;
  dynamic nextTeamHierarchyMappingId;
  int? nextStaff;
  String? status;
  String? version;
  int? shiftId;
  dynamic shiftedPartnerId;
  dynamic shiftedServiceAreaId;
  dynamic requestedByName;
  String? requestedDate;
  int? subareaId;
  int? buildingMgmtId;
  String? buildingNumber;
  bool? delete;

  NewcustomerAddress(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.addressType,
        this.address1,
        this.address2,
        this.landmark,
        this.landmark1,
        this.areaId,
        this.pincodeId,
        this.cityId,
        this.stateId,
        this.countryId,
        this.customerId,
        this.fullAddress,
        this.isDelete,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.status,
        this.version,
        this.shiftId,
        this.shiftedPartnerId,
        this.shiftedServiceAreaId,
        this.requestedByName,
        this.requestedDate,
        this.subareaId,
        this.buildingMgmtId,
        this.buildingNumber,
        this.delete});

  NewcustomerAddress.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    addressType = json['addressType'];
    address1 = json['address1'];
    address2 = json['address2'];
    landmark = json['landmark'];
    landmark1 = json['landmark1'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    customerId = json['customerId'];
    fullAddress = json['fullAddress'];
    isDelete = json['isDelete'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    status = json['status'];
    version = json['version'];
    shiftId = json['shiftId'];
    shiftedPartnerId = json['shiftedPartnerId'];
    shiftedServiceAreaId = json['shiftedServiceAreaId'];
    requestedByName = json['requestedByName'];
    requestedDate = json['requestedDate'];
    subareaId = json['subareaId'];
    buildingMgmtId = json['building_mgmt_id'];
    buildingNumber = json['buildingNumber'];
    delete = json['delete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['addressType'] = this.addressType;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['landmark'] = this.landmark;
    data['landmark1'] = this.landmark1;
    data['areaId'] = this.areaId;
    data['pincodeId'] = this.pincodeId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;
    data['customerId'] = this.customerId;
    data['fullAddress'] = this.fullAddress;
    data['isDelete'] = this.isDelete;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['status'] = this.status;
    data['version'] = this.version;
    data['shiftId'] = this.shiftId;
    data['shiftedPartnerId'] = this.shiftedPartnerId;
    data['shiftedServiceAreaId'] = this.shiftedServiceAreaId;
    data['requestedByName'] = this.requestedByName;
    data['requestedDate'] = this.requestedDate;
    data['subareaId'] = this.subareaId;
    data['building_mgmt_id'] = this.buildingMgmtId;
    data['buildingNumber'] = this.buildingNumber;
    data['delete'] = this.delete;
    return data;
  }
}
