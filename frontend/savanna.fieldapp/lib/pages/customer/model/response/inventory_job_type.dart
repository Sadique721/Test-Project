
import 'dart:convert';

InventoryJobType inventoryJobTypeFromJson(String str) => InventoryJobType.fromJson(json.decode(str));

String inventoryJobTypeToJson(InventoryJobType data) => json.encode(data.toJson());

class InventoryJobType {
    InventoryJobType({
        required this.totalRecords,
        required this.pageRecords,
        required this.dataList,
        required this.currentPageNumber,
        required this.totalPages,
        required this.responseMessage,
        required this.responseCode,
    });

    int totalRecords;
    int pageRecords;
    List<InventoryTypeDataList> dataList;
    int currentPageNumber;
    int totalPages;
    String responseMessage;
    int responseCode;

    factory InventoryJobType.fromJson(Map<dynamic, dynamic> json) => InventoryJobType(
        totalRecords: json["totalRecords"],
        pageRecords: json["pageRecords"],
        dataList: List<InventoryTypeDataList>.from(json["dataList"].map((x) => InventoryTypeDataList.fromJson(x))),
        currentPageNumber: json["currentPageNumber"],
        totalPages: json["totalPages"],
        responseMessage: json["responseMessage"],
        responseCode: json["responseCode"],
    );

    Map<dynamic, dynamic> toJson() => {
        "totalRecords": totalRecords,
        "pageRecords": pageRecords,
        "dataList": List<dynamic>.from(dataList.map((x) => x.toJson())),
        "currentPageNumber": currentPageNumber,
        "totalPages": totalPages,
        "responseMessage": responseMessage,
        "responseCode": responseCode,
    };
}

class InventoryTypeDataList {
    InventoryTypeDataList({
        required this.displayName,
        required this.mvnoId,
        required this.hasMandatory,
        required this.id,
        required this.text,
        required this.type,
        required this.displayId,
        required this.value,
        required this.status,
    });

    String displayName;
    int mvnoId;
    bool hasMandatory;
    int id;
    String text;
    String type;
    int displayId;
    String value;
    String status;

    factory InventoryTypeDataList.fromJson(Map<dynamic, dynamic> json) => InventoryTypeDataList(
        displayName: json["displayName"],
        mvnoId: json["mvnoId"],
        hasMandatory: json["hasMandatory"],
        id: json["id"],
        text: json["text"],
        type: json["type"],
        displayId: json["displayId"],
        value: json["value"],
        status: json["status"],
    );

    Map<dynamic, dynamic> toJson() => {
        "displayName": displayName,
        "mvnoId": mvnoId,
        "hasMandatory": hasMandatory,
        "id": id,
        "text": text,
        "type": type,
        "displayId": displayId,
        "value": value,
        "status": status,
    };
}
