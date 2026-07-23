class InventoryListReq {
  List<InventoryFilters>? filters;
  int? page;
  int? pageSize;
  String? sortBy;
  int? sortOrder;

  InventoryListReq(
      {this.filters, this.page, this.pageSize, this.sortBy, this.sortOrder});

  InventoryListReq.fromJson(Map<String, dynamic> json) {
    if (json['filters'] != null) {
      filters = <InventoryFilters>[];
      json['filters'].forEach((v) {
        filters!.add(InventoryFilters.fromJson(v));
      });
    }
    page = json['page'];
    pageSize = json['pageSize'];
    sortBy = json['sortBy'];
    sortOrder = json['sortOrder'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (filters != null) {
      data['filters'] = filters!.map((v) => v.toJson()).toList();
    }
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    data['sortBy'] = this.sortBy;
    data['sortOrder'] = this.sortOrder;
    return data;
  }
}

class InventoryFilters {
  int? filterValue;
  String? filterColumn;

  InventoryFilters({this.filterValue, this.filterColumn});

  InventoryFilters.fromJson(Map<String, dynamic> json) {
    filterValue = json['filterValue'];
    filterColumn = json['filterColumn'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['filterValue'] = filterValue;
    data['filterColumn'] = filterColumn;
    return data;
  }
}
