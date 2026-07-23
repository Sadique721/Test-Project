class AddNotesReq {
  int? id;
  int? custId;
  String? notes;

  AddNotesReq({this.id, this.custId, this.notes});

  AddNotesReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    notes = json['notes'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['notes'] = this.notes;
    return data;
  }
}