
class InventoryFileUploadRequest {
  int? customerInventoryId;
  String? opticalPowerRange;
  List<SectionUploadRequest>? sections;

  InventoryFileUploadRequest({
    this.customerInventoryId,
    this.opticalPowerRange,
    this.sections,
  });

  factory InventoryFileUploadRequest.fromJson(Map<String, dynamic> json) {
    return InventoryFileUploadRequest(
      customerInventoryId: json['customerInventoryId'],
      opticalPowerRange: json['opticalPowerRange'],
      sections: (json['sections'] as List<dynamic>?)
          ?.map((e) => SectionUploadRequest.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'customerInventoryId': customerInventoryId,
      'opticalPowerRange': opticalPowerRange,
      'sections': sections?.map((e) => e.toJson()).toList(),
    };
  }
}

class SectionUploadRequest {
  String? name;
  List<dynamic>? files; // Use appropriate type instead of dynamic if known, e.g. List<MultipartFile>
  String? latitude;
  String? longitude;
  String? opticalRange;

  SectionUploadRequest({
    this.name,
    this.files,
    this.latitude,
    this.longitude,
    this.opticalRange,
  });

  factory SectionUploadRequest.fromJson(Map<String, dynamic> json) {
    return SectionUploadRequest(
      name: json['name'],
      files: json['files'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      opticalRange: json['opticalRange'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'files': files,
      'latitude': latitude,
      'longitude': longitude,
      'opticalRange': opticalRange,
    };
  }
}

