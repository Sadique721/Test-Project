class AllPlanInventoryIdOnPlan {
  int? _id;
  int? _planId;
  int? _productCategoryId;
  String? _productType;
  String? _createdate;
  String? _updatedate;
  String? _createdByName;
  String? _lastModifiedByName;
  int? _createdById;
  int? _lastModifiedById;
  int? _productId;
  String? _revisedCharge;
  String? _ownershipType;
  String? _name;
  Null? _productCategoryName;
  Null? _productName;
  Null? _planName;
  Null? _mvnoId;
  int? _identityKey;

  AllPlanInventoryIdOnPlan(
      {int? id,
        int? planId,
        int? productCategoryId,
        String? productType,
        String? createdate,
        String? updatedate,
        String? createdByName,
        String? lastModifiedByName,
        int? createdById,
        int? lastModifiedById,
        int? productId,
        String? revisedCharge,
        String? ownershipType,
        String? name,
        Null? productCategoryName,
        Null? productName,
        Null? planName,
        Null? mvnoId,
        int? identityKey}) {
    if (id != null) {
      this._id = id;
    }
    if (planId != null) {
      this._planId = planId;
    }
    if (productCategoryId != null) {
      this._productCategoryId = productCategoryId;
    }
    if (productType != null) {
      this._productType = productType;
    }
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
    if (productId != null) {
      this._productId = productId;
    }
    if (revisedCharge != null) {
      this._revisedCharge = revisedCharge;
    }
    if (ownershipType != null) {
      this._ownershipType = ownershipType;
    }
    if (name != null) {
      this._name = name;
    }
    if (productCategoryName != null) {
      this._productCategoryName = productCategoryName;
    }
    if (productName != null) {
      this._productName = productName;
    }
    if (planName != null) {
      this._planName = planName;
    }
    if (mvnoId != null) {
      this._mvnoId = mvnoId;
    }
    if (identityKey != null) {
      this._identityKey = identityKey;
    }
  }

  int? get id => _id;
  set id(int? id) => _id = id;
  int? get planId => _planId;
  set planId(int? planId) => _planId = planId;
  int? get productCategoryId => _productCategoryId;
  set productCategoryId(int? productCategoryId) =>
      _productCategoryId = productCategoryId;
  String? get productType => _productType;
  set productType(String? productType) => _productType = productType;
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
  int? get productId => _productId;
  set productId(int? productId) => _productId = productId;
  String? get revisedCharge => _revisedCharge;
  set revisedCharge(String? revisedCharge) => _revisedCharge = revisedCharge;
  String? get ownershipType => _ownershipType;
  set ownershipType(String? ownershipType) => _ownershipType = ownershipType;
  String? get name => _name;
  set name(String? name) => _name = name;
  Null? get productCategoryName => _productCategoryName;
  set productCategoryName(Null? productCategoryName) =>
      _productCategoryName = productCategoryName;
  Null? get productName => _productName;
  set productName(Null? productName) => _productName = productName;
  Null? get planName => _planName;
  set planName(Null? planName) => _planName = planName;
  Null? get mvnoId => _mvnoId;
  set mvnoId(Null? mvnoId) => _mvnoId = mvnoId;
  int? get identityKey => _identityKey;
  set identityKey(int? identityKey) => _identityKey = identityKey;

  AllPlanInventoryIdOnPlan.fromJson(Map<String, dynamic> json) {
    _id = json['id'];
    _planId = json['planId'];
    _productCategoryId = json['productCategoryId'];
    _productType = json['product_type'];
    _createdate = json['createdate'];
    _updatedate = json['updatedate'];
    _createdByName = json['createdByName'];
    _lastModifiedByName = json['lastModifiedByName'];
    _createdById = json['createdById'];
    _lastModifiedById = json['lastModifiedById'];
    _productId = json['productId'];
    _revisedCharge = json['revisedCharge'];
    _ownershipType = json['ownershipType'];
    _name = json['name'];
    _productCategoryName = json['productCategoryName'];
    _productName = json['productName'];
    _planName = json['planName'];
    _mvnoId = json['mvnoId'];
    _identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this._id;
    data['planId'] = this._planId;
    data['productCategoryId'] = this._productCategoryId;
    data['product_type'] = this._productType;
    data['createdate'] = this._createdate;
    data['updatedate'] = this._updatedate;
    data['createdByName'] = this._createdByName;
    data['lastModifiedByName'] = this._lastModifiedByName;
    data['createdById'] = this._createdById;
    data['lastModifiedById'] = this._lastModifiedById;
    data['productId'] = this._productId;
    data['revisedCharge'] = this._revisedCharge;
    data['ownershipType'] = this._ownershipType;
    data['name'] = this._name;
    data['productCategoryName'] = this._productCategoryName;
    data['productName'] = this._productName;
    data['planName'] = this._planName;
    data['mvnoId'] = this._mvnoId;
    data['identityKey'] = this._identityKey;
    return data;
  }
}
