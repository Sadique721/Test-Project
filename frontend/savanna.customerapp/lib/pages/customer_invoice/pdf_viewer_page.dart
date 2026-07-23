import 'dart:developer';

import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_pdfview/flutter_pdfview.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';
import '../../util/constant.dart';

class PdfViewerPage extends StatefulWidget {
  String? title, filePathUrl,customerName;


  PdfViewerPage({required this.title, required this.filePathUrl,required this.customerName});

  @override
  _PdfViewerPageState createState() => _PdfViewerPageState();
}

class _PdfViewerPageState extends State<PdfViewerPage> {
  File? pFile;
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  Future<File?> _downloadFile() async {
    setState(() {
      isLoading = true;
    });
    var url = "${widget.filePathUrl}";
    final filename = basename(url);
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };

    log("Url===>>>${url}");

    final response = await http.get(Uri.parse(url), headers: headers);
    final bytes = response.bodyBytes;
    var dir = await getApplicationDocumentsDirectory();
    var file = File('${dir.path}/${widget.customerName}$filename.pdf');
    await file.writeAsBytes(bytes, flush: true);
    setState(() {
      // loadDocument(file);
      pFile = file;

    });
    setState(() {
      isLoading = false;
    });
    // return pFile;
  }

  _backScreen() {
    Get.back();
  }

  @override
  initState() {
    super.initState();
      _downloadFile();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(),
      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : Center(
              child: PDFView(
                autoSpacing: true,
                enableSwipe: true,
                pageSnap: true,
                swipeHorizontal: true,
                nightMode: false,
                pageFling: false,
                filePath: pFile!.path ?? "",
              ),
            ),
    );
  }

  _appBar() {
    return DynamicAppBar(widget.title!, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
