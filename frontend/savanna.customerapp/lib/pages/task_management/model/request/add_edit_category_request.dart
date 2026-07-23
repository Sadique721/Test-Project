class AddEditCategoryReq {
  String? categoryName;
  List<CategoryTatMappingList>? caseCategoryTatMappingList;
  String? status;
  dynamic categoryId;
  bool? isDefaultCaseCategory;
  bool? isDeleted;
  int? mvnoId;

  AddEditCategoryReq(
      {this.categoryName,
        this.caseCategoryTatMappingList,
        this.status,
        this.categoryId,
        this.isDefaultCaseCategory,
        this.isDeleted,
        this.mvnoId});

  AddEditCategoryReq.fromJson(Map<String, dynamic> json) {
    categoryName = json['categoryName'];
    if (json['caseCategoryTatMappingList'] != null) {
      caseCategoryTatMappingList = <CategoryTatMappingList>[];
      json['caseCategoryTatMappingList'].forEach((v) {
        caseCategoryTatMappingList!
            .add(new CategoryTatMappingList.fromJson(v));
      });
    }
    status = json['status'];
    categoryId = json['categoryId'];
    isDefaultCaseCategory = json['isDefaultCaseCategory'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['categoryName'] = this.categoryName;
    if (this.caseCategoryTatMappingList != null) {
      data['caseCategoryTatMappingList'] =
          this.caseCategoryTatMappingList!.map((v) => v.toJson()).toList();
    }
    data['status'] = this.status;
    data['categoryId'] = this.categoryId;
    data['isDefaultCaseCategory'] = this.isDefaultCaseCategory;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class CategoryTatMappingList {
  dynamic id;
  int? orderid;
  TaskTicketTatMatrix? ticketTatMatrix;

  CategoryTatMappingList({this.id, this.orderid, this.ticketTatMatrix});

  CategoryTatMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    orderid = json['orderid'];
    ticketTatMatrix = json['ticketTatMatrix'] != null
        ? new TaskTicketTatMatrix.fromJson(json['ticketTatMatrix'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['orderid'] = this.orderid;
    if (this.ticketTatMatrix != null) {
      data['ticketTatMatrix'] = this.ticketTatMatrix!.toJson();
    }
    return data;
  }
}

class TaskTicketTatMatrix {
  int? id;

  TaskTicketTatMatrix({this.id});

  TaskTicketTatMatrix.fromJson(Map<String, dynamic> json) {
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    return data;
  }
}
