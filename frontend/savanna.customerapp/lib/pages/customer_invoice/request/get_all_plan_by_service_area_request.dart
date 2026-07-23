class GetAllPlansByServiceAreaReq {
  List<int>? sa;
  int? currentPlanId;
  bool? isQosUpgrade;
  bool? isQosDowngrade;

  GetAllPlansByServiceAreaReq({this.sa,this.currentPlanId,this.isQosDowngrade,this.isQosUpgrade});

  GetAllPlansByServiceAreaReq.fromJson(Map<String, dynamic> json) {
    sa = json['sa'].cast<int>();
    currentPlanId = json['currentPlanId'];
    isQosDowngrade = json['isQosDowngrade'];
    isQosUpgrade = json['isQosUpgrade'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['sa'] = this.sa;
    data['currentPlanId'] = this.currentPlanId;
    data['isQosDowngrade'] = this.isQosDowngrade;
    data['isQosUpgrade'] = this.isQosUpgrade;
    return data;
  }
}