class CustomerPojo {
  final int? id;
  final String? name;

  CustomerPojo({required this.id, required this.name});

  factory CustomerPojo.fromJson(Map<String, dynamic> json) {
    return CustomerPojo(
      id: json['id'],
      name: '${json['title']} ${json['custname']}',
    );
  }
}