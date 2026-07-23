class FileDetail {
  String? fileName;
  String? filePath;
  String? filePathLocal;
  String? fileType;
  bool? isFileLocal = false;
  bool? isFileUpload = false;

  FileDetail(
      {this.fileName,
      this.filePath,
      this.filePathLocal,
      this.isFileLocal,
      this.isFileUpload,
      this.fileType});
}
