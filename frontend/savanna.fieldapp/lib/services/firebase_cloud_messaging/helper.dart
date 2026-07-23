import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'dart:developer';

class DownloadUtil {
  static Future<String> downloadAndSaveFile(String url, String fileName) async {
    final Directory directory = await getApplicationDocumentsDirectory();
    final String filePath = '${directory.path}/$fileName';

    final http.Response response = await http.get(Uri.parse(url));
    final File file = File(filePath);
    await file
        .writeAsBytes(response.bodyBytes)
        .then((value) {})
        .onError((error, stackTrace) {
      log(error.toString());
    });

    return filePath;
  }

  static Future<void> readFile(String fileName) async {
    final Directory directory = await getApplicationDocumentsDirectory();
    final String filePath = '${directory.path}/$fileName';
    final File file = File(filePath);
    file.readAsBytes().then((value) {
      log(value.toString());
    }).onError((PathNotFoundException e, stackTrace) {
      if (e.osError?.errorCode == 2 &&
          e.osError?.message.compareTo('No such file or directory') == 0 &&
          e.path?.compareTo(filePath) == 0) {
        log("Image Deleted Successfully");
      } else {
        log(e.toString());
      }
    });
  }

  static Future<void> deleteFile(String fileName) async {
    final Directory directory = await getApplicationDocumentsDirectory();
    final String filePath = '${directory.path}/$fileName';
    final File file = File(filePath);
    file.delete().then((value) {
      readFile(fileName);
    }).onError((error, stackTrace) {
      log(error.toString());
    });
  }
}
