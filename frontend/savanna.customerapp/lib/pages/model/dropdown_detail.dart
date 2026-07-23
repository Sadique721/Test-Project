class DropdownDetail {
  String? id;
  String? text;
  String? type;

  DropdownDetail({this.id, this.text, this.type});

  DropdownDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    type = json['type'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['type'] = this.type;
    return data;
  }

  @override
  bool operator ==(dynamic other) =>
      other != null &&
      other is DropdownDetail &&
      this.id == other.id &&
      this.text == other.text &&
      this.type == other.type;

  @override
  int get hashCode => super.hashCode;
}
