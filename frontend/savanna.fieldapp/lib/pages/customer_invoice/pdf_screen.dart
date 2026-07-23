import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_pdfview/flutter_pdfview.dart';
import 'package:get/get.dart';

import '../../theme/app_theme.dart';
import '../../widgets/dynamic_appbar.dart';

class PDFScreen extends StatelessWidget {
  final File? pFile;
  final String? titleBarText;

  PDFScreen({super.key, this.pFile, this.titleBarText});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(titleBarText),
      body: PDFView(
        autoSpacing: true,
        enableSwipe: true,
        pageSnap: true,
        swipeHorizontal: true,
        nightMode: false,
        pageFling: false,
        filePath: pFile!.path ?? "",
      ),
    );
  }
}

_appBar(String? titleBarText) {
  return DynamicAppBar(titleBarText!, '', AppTheme.colorPrimary, false,
      _backScreen, [], AppBar().preferredSize.height);
}

_backScreen() {
  Get.back();
}
