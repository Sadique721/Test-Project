import 'package:savbill/webservices/base_response.dart';

class GetProductCategoryByPlanIdRes{
  int? _responseCode;
  String? _responseMessage;
  dynamic _data;
  List<ProductCategoryDataList>? _dataList;
  dynamic _excelDataList;
  int? _totalRecords;
  int? _pageRecords;
  int? _currentPageNumber;
  int? _totalPages;

  GetProductCategoryByPlanIdRes(
      {int? responseCode,
        String? responseMessage,
        Null? data,
        List<ProductCategoryDataList>? dataList,
        Null? excelDataList,
        int? totalRecords,
        int? pageRecords,
        int? currentPageNumber,
        int? totalPages}) {
    if (responseCode != null) {
      this._responseCode = responseCode;
    }
    if (responseMessage != null) {
      this._responseMessage = responseMessage;
    }
    if (data != null) {
      this._data = data;
    }
    if (dataList != null) {
      this._dataList = dataList;
    }
    if (excelDataList != null) {
      this._excelDataList = excelDataList;
    }
    if (totalRecords != null) {
      this._totalRecords = totalRecords;
    }
    if (pageRecords != null) {
      this._pageRecords = pageRecords;
    }
    if (currentPageNumber != null) {
      this._currentPageNumber = currentPageNumber;
    }
    if (totalPages != null) {
      this._totalPages = totalPages;
    }
  }

  int? get responseCode => _responseCode;
  set responseCode(int? responseCode) => _responseCode = responseCode;
  String? get responseMessage => _responseMessage;
  set responseMessage(String? responseMessage) =>
      _responseMessage = responseMessage;
  Null? get data => _data;
  set data(Null? data) => _data = data;
  List<ProductCategoryDataList>? get dataList => _dataList;
  set dataList(List<ProductCategoryDataList>? dataList) => _dataList = dataList;
  Null? get excelDataList => _excelDataList;
  set excelDataList(Null? excelDataList) => _excelDataList = excelDataList;
  int? get totalRecords => _totalRecords;
  set totalRecords(int? totalRecords) => _totalRecords = totalRecords;
  int? get pageRecords => _pageRecords;
  set pageRecords(int? pageRecords) => _pageRecords = pageRecords;
  int? get currentPageNumber => _currentPageNumber;
  set currentPageNumber(int? currentPageNumber) =>
      _currentPageNumber = currentPageNumber;
  int? get totalPages => _totalPages;
  set totalPages(int? totalPages) => _totalPages = totalPages;

  GetProductCategoryByPlanIdRes.fromJson(Map<String, dynamic> json) {
    _responseCode = json['responseCode'];
    _responseMessage = json['responseMessage'];
    _data = json['data'];
    if (json['dataList'] != null) {
      _dataList = <ProductCategoryDataList>[];
      json['dataList'].forEach((v) {
        _dataList!.add(new ProductCategoryDataList.fromJson(v));
      });
    }
    _excelDataList = json['excelDataList'];
    _totalRecords = json['totalRecords'];
    _pageRecords = json['pageRecords'];
    _currentPageNumber = json['currentPageNumber'];
    _totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this._responseCode;
    data['responseMessage'] = this._responseMessage;
    data['data'] = this._data;
    if (this._dataList != null) {
      data['dataList'] = this._dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this._excelDataList;
    data['totalRecords'] = this._totalRecords;
    data['pageRecords'] = this._pageRecords;
    data['currentPageNumber'] = this._currentPageNumber;
    data['totalPages'] = this._totalPages;
    return data;
  }
}

class ProductCategoryDataList {
  String? _createdate;
  String? _updatedate;
  String? _createdByName;
  String? _lastModifiedByName;
  int? _createdById;
  int? _lastModifiedById;
  int? _id;
  String? _name;
  String? _unit;
  int? _mvnoId;
  bool? _hasMac;
  String? _type;
  String? _status;
  String? _productId;
  bool? _isDeleted;
  bool? _hasSerial;
  bool? _hasTrackable;
  bool? _hasPort;
  bool? _hasCas;
  String? _dtvCategory;
  bool? _deleteFlag;
  int? _primaryKey;

  DataList(
      {String? createdate,
        String? updatedate,
        String? createdByName,
        String? lastModifiedByName,
        int? createdById,
        int? lastModifiedById,
        int? id,
        String? name,
        String? unit,
        int? mvnoId,
        bool? hasMac,
        String? type,
        String? status,
        String? productId,
        bool? isDeleted,
        bool? hasSerial,
        bool? hasTrackable,
        bool? hasPort,
        bool? hasCas,
        String? dtvCategory,
        bool? deleteFlag,
        int? primaryKey}) {
    if (createdate != null) {
      this._createdate = createdate;
    }
    if (updatedate != null) {
      this._updatedate = updatedate;
    }
    if (createdByName != null) {
      this._createdByName = createdByName;
    }
    if (lastModifiedByName != null) {
      this._lastModifiedByName = lastModifiedByName;
    }
    if (createdById != null) {
      this._createdById = createdById;
    }
    if (lastModifiedById != null) {
      this._lastModifiedById = lastModifiedById;
    }
    if (id != null) {
      this._id = id;
    }
    if (name != null) {
      this._name = name;
    }
    if (unit != null) {
      this._unit = unit;
    }
    if (mvnoId != null) {
      this._mvnoId = mvnoId;
    }
    if (hasMac != null) {
      this._hasMac = hasMac;
    }
    if (type != null) {
      this._type = type;
    }
    if (status != null) {
      this._status = status;
    }
    if (productId != null) {
      this._productId = productId;
    }
    if (isDeleted != null) {
      this._isDeleted = isDeleted;
    }
    if (hasSerial != null) {
      this._hasSerial = hasSerial;
    }
    if (hasTrackable != null) {
      this._hasTrackable = hasTrackable;
    }
    if (hasPort != null) {
      this._hasPort = hasPort;
    }
    if (hasCas != null) {
      this._hasCas = hasCas;
    }
    if (dtvCategory != null) {
      this._dtvCategory = dtvCategory;
    }
    if (deleteFlag != null) {
      this._deleteFlag = deleteFlag;
    }
    if (primaryKey != null) {
      this._primaryKey = primaryKey;
    }
  }

  String? get createdate => _createdate;
  set createdate(String? createdate) => _createdate = createdate;
  String? get updatedate => _updatedate;
  set updatedate(String? updatedate) => _updatedate = updatedate;
  String? get createdByName => _createdByName;
  set createdByName(String? createdByName) => _createdByName = createdByName;
  String? get lastModifiedByName => _lastModifiedByName;
  set lastModifiedByName(String? lastModifiedByName) =>
      _lastModifiedByName = lastModifiedByName;
  int? get createdById => _createdById;
  set createdById(int? createdById) => _createdById = createdById;
  int? get lastModifiedById => _lastModifiedById;
  set lastModifiedById(int? lastModifiedById) =>
      _lastModifiedById = lastModifiedById;
  int? get id => _id;
  set id(int? id) => _id = id;
  String? get name => _name;
  set name(String? name) => _name = name;
  String? get unit => _unit;
  set unit(String? unit) => _unit = unit;
  int? get mvnoId => _mvnoId;
  set mvnoId(int? mvnoId) => _mvnoId = mvnoId;
  bool? get hasMac => _hasMac;
  set hasMac(bool? hasMac) => _hasMac = hasMac;
  String? get type => _type;
  set type(String? type) => _type = type;
  String? get status => _status;
  set status(String? status) => _status = status;
  String? get productId => _productId;
  set productId(String? productId) => _productId = productId;
  bool? get isDeleted => _isDeleted;
  set isDeleted(bool? isDeleted) => _isDeleted = isDeleted;
  bool? get hasSerial => _hasSerial;
  set hasSerial(bool? hasSerial) => _hasSerial = hasSerial;
  bool? get hasTrackable => _hasTrackable;
  set hasTrackable(bool? hasTrackable) => _hasTrackable = hasTrackable;
  bool? get hasPort => _hasPort;
  set hasPort(bool? hasPort) => _hasPort = hasPort;
  bool? get hasCas => _hasCas;
  set hasCas(bool? hasCas) => _hasCas = hasCas;
  String? get dtvCategory => _dtvCategory;
  set dtvCategory(String? dtvCategory) => _dtvCategory = dtvCategory;
  bool? get deleteFlag => _deleteFlag;
  set deleteFlag(bool? deleteFlag) => _deleteFlag = deleteFlag;
  int? get primaryKey => _primaryKey;
  set primaryKey(int? primaryKey) => _primaryKey = primaryKey;

  ProductCategoryDataList.fromJson(Map<String, dynamic> json) {
    _createdate = json['createdate'];
    _updatedate = json['updatedate'];
    _createdByName = json['createdByName'];
    _lastModifiedByName = json['lastModifiedByName'];
    _createdById = json['createdById'];
    _lastModifiedById = json['lastModifiedById'];
    _id = json['id'];
    _name = json['name'];
    _unit = json['unit'];
    _mvnoId = json['mvnoId'];
    _hasMac = json['hasMac'];
    _type = json['type'];
    _status = json['status'];
    _productId = json['productId'];
    _isDeleted = json['isDeleted'];
    _hasSerial = json['hasSerial'];
    _hasTrackable = json['hasTrackable'];
    _hasPort = json['hasPort'];
    _hasCas = json['hasCas'];
    _dtvCategory = json['dtvCategory'];
    _deleteFlag = json['deleteFlag'];
    _primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this._createdate;
    data['updatedate'] = this._updatedate;
    data['createdByName'] = this._createdByName;
    data['lastModifiedByName'] = this._lastModifiedByName;
    data['createdById'] = this._createdById;
    data['lastModifiedById'] = this._lastModifiedById;
    data['id'] = this._id;
    data['name'] = this._name;
    data['unit'] = this._unit;
    data['mvnoId'] = this._mvnoId;
    data['hasMac'] = this._hasMac;
    data['type'] = this._type;
    data['status'] = this._status;
    data['productId'] = this._productId;
    data['isDeleted'] = this._isDeleted;
    data['hasSerial'] = this._hasSerial;
    data['hasTrackable'] = this._hasTrackable;
    data['hasPort'] = this._hasPort;
    data['hasCas'] = this._hasCas;
    data['dtvCategory'] = this._dtvCategory;
    data['deleteFlag'] = this._deleteFlag;
    data['primaryKey'] = this._primaryKey;
    return data;
  }
}
