class Filters {
  String? filterColumn;
  String? filterCondition;
  String? filterDataType;
  String? filterOperator;
  dynamic filterValue;

  Filters(
      {this.filterColumn,
      this.filterCondition,
      this.filterDataType,
      this.filterOperator,
      this.filterValue});

  Filters.fromJson(Map<String, dynamic> json) {
    filterColumn = json['filterColumn'];
    filterCondition = json['filterCondition'];
    filterDataType = json['filterDataType'];
    filterOperator = json['filterOperator'];
    filterValue = json['filterValue'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['filterColumn'] = this.filterColumn;
    data['filterCondition'] = this.filterCondition;
    data['filterDataType'] = this.filterDataType;
    data['filterOperator'] = this.filterOperator;
    data['filterValue'] = this.filterValue;
    return data;
  }
}
