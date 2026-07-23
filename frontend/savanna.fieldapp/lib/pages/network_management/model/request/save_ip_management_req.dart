class SaveIpManagmentReq {
  String? broadcastIp;
  bool? defaultPoolFlag;
  String? displayName;
  String? remark;
  String? firstHost;
  String? ipRange;
  bool? isStaticIpPool;
  String? lastHost;
  String? netMask;
  String? networkIp;
  String? poolCategory;
  String? poolName;
  String? poolType;
  String? status;
  String? totalHost;
  int? poolId;

  SaveIpManagmentReq(
      {this.broadcastIp,
        this.defaultPoolFlag,
        this.displayName,
        this.remark,
        this.firstHost,
        this.ipRange,
        this.isStaticIpPool,
        this.lastHost,
        this.netMask,
        this.networkIp,
        this.poolCategory,
        this.poolName,
        this.poolType,
        this.status,
        this.totalHost,
        this.poolId,
      });

  SaveIpManagmentReq.fromJson(Map<String, dynamic> json) {
    broadcastIp = json['broadcastIp'];
    defaultPoolFlag = json['defaultPoolFlag'];
    displayName = json['displayName'];
    remark = json['remark'];
    firstHost = json['firstHost'];
    ipRange = json['ipRange'];
    isStaticIpPool = json['isStaticIpPool'];
    lastHost = json['lastHost'];
    netMask = json['netMask'];
    networkIp = json['networkIp'];
    poolCategory = json['poolCategory'];
    poolName = json['poolName'];
    poolType = json['poolType'];
    status = json['status'];
    totalHost = json['totalHost'];
    poolId = json['poolId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['broadcastIp'] = this.broadcastIp;
    data['defaultPoolFlag'] = this.defaultPoolFlag;
    data['displayName'] = this.displayName;
    data['remark'] = this.remark;
    data['firstHost'] = this.firstHost;
    data['ipRange'] = this.ipRange;
    data['isStaticIpPool'] = this.isStaticIpPool;
    data['lastHost'] = this.lastHost;
    data['netMask'] = this.netMask;
    data['networkIp'] = this.networkIp;
    data['poolCategory'] = this.poolCategory;
    data['poolName'] = this.poolName;
    data['poolType'] = this.poolType;
    data['status'] = this.status;
    data['totalHost'] = this.totalHost;
    data['poolId'] = this.poolId;
    return data;
  }
}
