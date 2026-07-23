import 'package:savbill/webservices/base_response.dart';

class ConnectionHistoryRes extends BaseResponse {
  AcctCdr? acctCdr;

  ConnectionHistoryRes({this.acctCdr, timestamp, status});

  ConnectionHistoryRes.fromJson(Map<String, dynamic> json) {
    acctCdr =
        json['acctCdr'] != null ? new AcctCdr.fromJson(json['acctCdr']) : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.acctCdr != null) {
      data['acctCdr'] = this.acctCdr!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class AcctCdr {
  List<Content>? content;
  Pageable? pageable;
  int? totalPages;
  int? totalElements;
  bool? last;
  bool? first;
  int? numberOfElements;
  int? size;
  int? number;
  bool? empty;

  AcctCdr(
      {this.content,
      this.pageable,
      this.totalPages,
      this.totalElements,
      this.last,
      this.first,
      this.numberOfElements,
      this.size,
      this.number,
      this.empty});

  AcctCdr.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <Content>[];
      json['content'].forEach((v) {
        content!.add(new Content.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    last = json['last'];
    first = json['first'];
    numberOfElements = json['numberOfElements'];
    size = json['size'];
    number = json['number'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.content != null) {
      data['content'] = this.content!.map((v) => v.toJson()).toList();
    }
    if (this.pageable != null) {
      data['pageable'] = this.pageable!.toJson();
    }
    data['totalPages'] = this.totalPages;
    data['totalElements'] = this.totalElements;
    data['last'] = this.last;
    data['first'] = this.first;
    data['numberOfElements'] = this.numberOfElements;
    data['size'] = this.size;
    data['number'] = this.number;
    data['empty'] = this.empty;
    return data;
  }
}

class Content {
  int? cdrId;
  String? userName;
  String? nasIpAddress;
  String? nasPort;
  String? framedIpAddress;
  String? framedIpNetmask;
  String? framedRouting;
  int? filterId;
  String? replyMessage;
  String? vendorSpecific;
  String? calledStationId;
  String? callingStationId;
  String? nasIdentifier;
  String? acctStatusType;
  String? acctDelayTime;
  String? acctInputOctets;
  String? acctOutputOctets;
  String? acctSessionId;
  String? acctSessionTime;
  String? acctInputPackets;
  String? acctOutputPackets;
  String? acctTerminateCause;
  String? acctInputGigawords;
  String? acctOutputGigawords;
  String? eventTimestamp;
  String? nasPortType;
  String? connectInfo;
  String? nasPortId;
  int? mvnoId;
  String? createDate;
  String? lastModificationDate;

  Content(
      {this.cdrId,
      this.userName,
      this.nasIpAddress,
      this.nasPort,
      this.framedIpAddress,
      this.framedIpNetmask,
      this.framedRouting,
      this.filterId,
      this.replyMessage,
      this.vendorSpecific,
      this.calledStationId,
      this.callingStationId,
      this.nasIdentifier,
      this.acctStatusType,
      this.acctDelayTime,
      this.acctInputOctets,
      this.acctOutputOctets,
      this.acctSessionId,
      this.acctSessionTime,
      this.acctInputPackets,
      this.acctOutputPackets,
      this.acctTerminateCause,
      this.acctInputGigawords,
      this.acctOutputGigawords,
      this.eventTimestamp,
      this.nasPortType,
      this.connectInfo,
      this.nasPortId,
      this.mvnoId,
      this.createDate,
      this.lastModificationDate});

  Content.fromJson(Map<String, dynamic> json) {
    cdrId = json['cdrId'];
    userName = json['userName'];
    nasIpAddress = json['nasIpAddress'];
    nasPort = json['nasPort'];
    framedIpAddress = json['framedIpAddress'];
    framedIpNetmask = json['framedIpNetmask'];
    framedRouting = json['framedRouting'];
    filterId = json['filterId'];
    replyMessage = json['replyMessage'];
    vendorSpecific = json['vendorSpecific'];
    calledStationId = json['calledStationId'];
    callingStationId = json['callingStationId'];
    nasIdentifier = json['nasIdentifier'];
    acctStatusType = json['acctStatusType'];
    acctDelayTime = json['acctDelayTime'];
    acctInputOctets = json['acctInputOctets'];
    acctOutputOctets = json['acctOutputOctets'];
    acctSessionId = json['acctSessionId'];
    acctSessionTime = json['acctSessionTime'];
    acctInputPackets = json['acctInputPackets'];
    acctOutputPackets = json['acctOutputPackets'];
    acctTerminateCause = json['acctTerminateCause'];
    acctInputGigawords = json['acctInputGigawords'];
    acctOutputGigawords = json['acctOutputGigawords'];
    eventTimestamp = json['eventTimestamp'];
    nasPortType = json['nasPortType'];
    connectInfo = json['connectInfo'];
    nasPortId = json['nasPortId'];
    mvnoId = json['mvnoId'];
    createDate = json['createDate'];
    lastModificationDate = json['lastModificationDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['cdrId'] = this.cdrId;
    data['userName'] = this.userName;
    data['nasIpAddress'] = this.nasIpAddress;
    data['nasPort'] = this.nasPort;
    data['framedIpAddress'] = this.framedIpAddress;
    data['framedIpNetmask'] = this.framedIpNetmask;
    data['framedRouting'] = this.framedRouting;
    data['filterId'] = this.filterId;
    data['replyMessage'] = this.replyMessage;
    data['vendorSpecific'] = this.vendorSpecific;
    data['calledStationId'] = this.calledStationId;
    data['callingStationId'] = this.callingStationId;
    data['nasIdentifier'] = this.nasIdentifier;
    data['acctStatusType'] = this.acctStatusType;
    data['acctDelayTime'] = this.acctDelayTime;
    data['acctInputOctets'] = this.acctInputOctets;
    data['acctOutputOctets'] = this.acctOutputOctets;
    data['acctSessionId'] = this.acctSessionId;
    data['acctSessionTime'] = this.acctSessionTime;
    data['acctInputPackets'] = this.acctInputPackets;
    data['acctOutputPackets'] = this.acctOutputPackets;
    data['acctTerminateCause'] = this.acctTerminateCause;
    data['acctInputGigawords'] = this.acctInputGigawords;
    data['acctOutputGigawords'] = this.acctOutputGigawords;
    data['eventTimestamp'] = this.eventTimestamp;
    data['nasPortType'] = this.nasPortType;
    data['connectInfo'] = this.connectInfo;
    data['nasPortId'] = this.nasPortId;
    data['mvnoId'] = this.mvnoId;
    data['createDate'] = this.createDate;
    data['lastModificationDate'] = this.lastModificationDate;
    return data;
  }
}

class Pageable {
  int? pageNumber;
  int? pageSize;
  int? offset;
  bool? unpaged;
  bool? paged;

  Pageable(
      {this.pageNumber, this.pageSize, this.offset, this.unpaged, this.paged});

  Pageable.fromJson(Map<String, dynamic> json) {
    pageNumber = json['pageNumber'];
    pageSize = json['pageSize'];
    offset = json['offset'];
    unpaged = json['unpaged'];
    paged = json['paged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['pageNumber'] = this.pageNumber;
    data['pageSize'] = this.pageSize;
    data['offset'] = this.offset;
    data['unpaged'] = this.unpaged;
    data['paged'] = this.paged;
    return data;
  }
}