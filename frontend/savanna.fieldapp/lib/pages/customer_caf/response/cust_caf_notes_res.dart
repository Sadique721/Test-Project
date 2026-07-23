import '../../../webservices/base_response.dart';

class CustCAFAllNotesRes extends BaseResponse{
  // CustomerNotesList? customerNotesList;
  List<CafNoteContent>? customerNotesList;
  String? timestamp;
  int? status;
  CustCAFAllNotesRes({this.customerNotesList, this.timestamp, this.status});
  CustCAFAllNotesRes.fromJson(Map<String, dynamic> json) {
    // customerNotesList = json['customerNotesList'] != null
    //     ? CustomerNotesList.fromJson(json['customerNotesList'])
    //     : null;
    customerNotesList = (json['customerNotesList'] as List?)
        ?.map((v) => CafNoteContent.fromJson(v))
        .toList();
    timestamp = json['timestamp'];
    status = json['status'];
  }
  Map<String, dynamic> toJson() {
    return {
      'customerNotesList': customerNotesList?.map((v) => v.toJson()).toList(),
      'timestamp': timestamp,
      'status': status,
    };
  }
}



class CustCAFNotesRes extends BaseResponse{
  CustomerNotesList? customerNotesList;
  String? timestamp;
  int? status;
  CustCAFNotesRes({this.customerNotesList, this.timestamp, this.status});
  CustCAFNotesRes.fromJson(Map<String, dynamic> json) {
    customerNotesList = json['customerNotesList'] != null
        ? CustomerNotesList.fromJson(json['customerNotesList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }
  Map<String, dynamic> toJson() {
    return {
      'customerNotesList': customerNotesList?.toJson(),
      'timestamp': timestamp,
      'status': status,
    };
  }
}
class CustomerNotesList {
  List<CafNoteContent>? content;
  Pageable? pageable;
  bool last;
  dynamic totalElements;
  dynamic totalPages;
  Sort? sort;
  dynamic numberOfElements;
  bool first;
  dynamic size;
  dynamic number;
  bool empty;
  CustomerNotesList({
    this.content,
    this.pageable,
    this.last = false,
    this.totalElements = 0,
    this.totalPages = 0,
    this.sort,
    this.numberOfElements = 0,
    this.first = false,
    this.size = 0,
    this.number = 0,
    this.empty = false,
  });
  CustomerNotesList.fromJson(Map<String, dynamic> json)
      : last = json['last'] ?? false,
        totalElements = json['totalElements'] ?? 0,
        totalPages = json['totalPages'] ?? 0,
        numberOfElements = json['numberOfElements'] ?? 0,
        first = json['first'] ?? false,
        size = json['size'] ?? 0,
        number = json['number'] ?? 0,
        empty = json['empty'] ?? false {
    content = (json['content'] as List?)
        ?.map((v) => CafNoteContent.fromJson(v))
        .toList();
    pageable = json['pageable'] != null
        ? Pageable.fromJson(json['pageable'])
        : null;
    sort = json['sort'] != null ? Sort.fromJson(json['sort']) : null;
  }
  Map<String, dynamic> toJson() {
    return {
      'content': content?.map((v) => v.toJson()).toList(),
      'pageable': pageable?.toJson(),
      'last': last,
      'totalElements': totalElements,
      'totalPages': totalPages,
      'sort': sort?.toJson(),
      'numberOfElements': numberOfElements,
      'first': first,
      'size': size,
      'number': number,
      'empty': empty,
    };
  }
}
class CafNoteContent {
  dynamic id;
  String notes;
  dynamic custId;
  String createdOn;
  String createdBy;
  String createdByName;
  CafNoteContent({
    required this.id,
    required this.notes,
    required this.custId,
    required this.createdOn,
    required this.createdBy,
    required this.createdByName,
  });
  CafNoteContent.fromJson(Map<String, dynamic> json)
      : id = json['id'] ?? 0,
        notes = json['notes'] ?? '',
        custId = json['custId'] ?? 0,
        createdOn = json['createdOn'] ?? '',
        createdBy = json['createdBy'] ?? '',
        createdByName = json['createdByName'] ?? '';
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'notes': notes,
      'custId': custId,
      'createdOn': createdOn,
      'createdBy': createdBy,
      'createdByName': createdByName,
    };
  }
}
class Pageable {
  Sort? sort;
  dynamic pageNumber;
  dynamic pageSize;
  dynamic offset;
  bool unpaged;
  bool paged;
  Pageable({
    this.sort,
    this.pageNumber = 0,
    this.pageSize = 0,
    this.offset = 0,
    this.unpaged = false,
    this.paged = true,
  });
  Pageable.fromJson(Map<String, dynamic> json)
      : sort = json['sort'] != null ? Sort.fromJson(json['sort']) : null,
        pageNumber = json['pageNumber'] ?? 0,
        pageSize = json['pageSize'] ?? 0,
        offset = json['offset'] ?? 0,
        unpaged = json['unpaged'] ?? false,
        paged = json['paged'] ?? true;
  Map<String, dynamic> toJson() {
    return {
      'sort': sort?.toJson(),
      'pageNumber': pageNumber,
      'pageSize': pageSize,
      'offset': offset,
      'unpaged': unpaged,
      'paged': paged,
    };
  }
}
class Sort {
  bool sorted;
  bool unsorted;
  bool empty;
  Sort({this.sorted = false, this.unsorted = false, this.empty = false});
  Sort.fromJson(Map<String, dynamic> json)
      : sorted = json['sorted'] ?? false,
        unsorted = json['unsorted'] ?? false,
        empty = json['empty'] ?? false;
  Map<String, dynamic> toJson() {
    return {
      'sorted': sorted,
      'unsorted': unsorted,
      'empty': empty,
    };
  }
}