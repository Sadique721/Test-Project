class ItemList {
  int? id;
  String? title;
  String? icon;

  ItemList({
    this.id,
    this.title,
    this.icon,
  });

  ItemList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    title = json['title'];
    icon = json['icon'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['title'] = this.title;
    data['icon'] = this.icon;
    return data;
  }
}
