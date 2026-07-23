import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../theme/app_theme.dart';
import '../../widgets/dynamic_appbar.dart';

class ImagePreviewScreen extends StatefulWidget {
  String? url, titleBarTitle;

  ImagePreviewScreen({this.url, this.titleBarTitle});

  @override
  _ImagePreviewScreenState createState() => _ImagePreviewScreenState();
}

class _ImagePreviewScreenState extends State<ImagePreviewScreen> {
  Uint8List convertBase64Image(String base64String) {
    return const Base64Decoder().convert(base64String.split(',').last);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: _appBar(),
      body: Container(
        padding: const EdgeInsets.all(8.0),
        child: Image.memory(
          convertBase64Image(widget.url.toString()),
          gaplessPlayback: true,
          // fit: BoxFit.cover,
          height: MediaQuery.of(context).size.height,
          frameBuilder: ((context, child, frame, wasSynchronouslyLoaded) {
            if (wasSynchronouslyLoaded) return child;
            return AnimatedSwitcher(
              duration: const Duration(milliseconds: 200),
              child: frame != null
                  ? child
                  : const Center(
                      child: SizedBox(
                        height: 40,
                        width: 40,
                        child: CircularProgressIndicator(strokeWidth: 3),
                      ),
                    ),
            );
          }),
        ),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(widget.titleBarTitle!, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  _backScreen() {
    Get.back();
  }
}
