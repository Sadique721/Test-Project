class ChangePlanGroupScreen {
  String? planGroupName;
  int? groupId;
  String? planGroupValue;

  ChangePlanGroupScreen({this.groupId, this.planGroupName,this.planGroupValue});

  ChangePlanGroupScreen.fromJson(Map<String, dynamic> json) {
    groupId = json['plan_group_id'];
    planGroupName = json['plan_group_name'];
    planGroupValue = json['plan_group_value'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['plan_group_id'] = groupId;
    data['plan_group_name'] = planGroupName;
    data['plan_group_value'] = planGroupValue;
    return data;
  }
}