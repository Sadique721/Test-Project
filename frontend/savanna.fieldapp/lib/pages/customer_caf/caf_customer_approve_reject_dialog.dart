import 'dart:io';

import 'package:device_info_plus/device_info_plus.dart';
import 'package:file_picker/file_picker.dart';
import 'package:image_picker/image_picker.dart';
import 'package:path/path.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../widgets/file_grid_item.dart';
import '../model/file_detail.dart';

class CafCustomerApproveRejectDialog extends StatefulWidget {
  String? pageName;
  final CafCustomerApproveRejectBtnAction? cafCustomerApproveRejectBtnAction;
  CustomerDetail? customerDetail;
  List<FileDetail> selectedFiles = [];

  CafCustomerApproveRejectDialog(
      {Key? key,
      this.pageName,
      this.cafCustomerApproveRejectBtnAction,
      this.customerDetail
      // this.caseId
      })
      : super(key: key);

  @override
  _CafCustomerApproveRejectState createState() =>
      _CafCustomerApproveRejectState();
}

class _CafCustomerApproveRejectState
    extends State<CafCustomerApproveRejectDialog> {
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.pageName!.equalsIgnoreCase(Strings.approve)) {
      title = Strings.approveCustomer;
    } else if (widget.pageName!.equalsIgnoreCase(Strings.reject)) {
      title = Strings.rejectCustomer;
    }

    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          AlertDialog(
            insetPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING * 2,
            ),
            contentPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING,
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            backgroundColor: AppTheme.colorPrimary,
            shape: const RoundedRectangleBorder(
                borderRadius:
                    BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
              content: Container(
                width: MediaQuery.of(context).size.width,
                color: AppTheme.colorWhite,
                child: SingleChildScrollView( // ✅ Wrap entire content in scroll view
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        color: AppTheme.colorPrimary,
                        padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING,
                          vertical: Constant.MEDIUM_PADDING,
                        ),
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: title,
                            colors: AppTheme.title_dark,
                            fontSize: AppTheme.large,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      reviewEditor(),

                      if (widget.pageName!.equalsIgnoreCase(Strings.reject))
                        Padding(
                          padding: const EdgeInsets.symmetric(
                              horizontal: Constant.SMALL_PADDING),
                          child: GestureDetector(
                            onTap: () async {
                              bool granted = await hasCameraPermission();
                              if (granted) {
                                final ImagePicker picker = ImagePicker();
                                final XFile? image = await picker.pickImage(
                                    source: ImageSource.gallery, imageQuality: 20);

                                if (image != null) {
                                  File file = File(image.path);
                                  FileDetail platformFile = FileDetail(
                                      fileName: basename(file.path),
                                      filePath: "",
                                      filePathLocal: file.path,
                                      isFileLocal: true,
                                      fileType: Strings.image);

                                  double fileSizeInMB =
                                      (await file.length()) / (1024 * 1024);
                                  debugPrint(
                                      "📸 File size: ${fileSizeInMB.toStringAsFixed(2)} MB");
                                  setState(() {
                                    widget.selectedFiles.add(platformFile);
                                  });
                                }
                              } else {
                                await requestCameraPermissionAndCapture();
                              }
                            },
                            child: Row(
                              children: [
                                Icon(Icons.add_circle_outline_rounded,
                                    color: AppTheme.title_dark, size: 18),
                                CustomText(
                                  title: " ${Strings.select_file} :",
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.small + 1,
                                  fontWeight: FontWeight.w500,
                                ),
                              ],
                            ),
                          ),
                        ),

                      if (widget.pageName!.equalsIgnoreCase(Strings.reject))
                        SingleChildScrollView(
                          scrollDirection: Axis.horizontal,
                          child: Row(
                            children: widget.selectedFiles.asMap().entries.map((entry) {
                              final index = entry.key;
                              final fileDetail = entry.value;
                              return Padding(
                                padding: const EdgeInsets.all(Constant.VERY_SMALL_PADDING),
                                child: FileGridItem(
                                  fileDetail: fileDetail,
                                  onTapItem: () {},
                                  bottomAction: Align(
                                    alignment: Alignment.topRight,
                                    child: InkWell(
                                      onTap: () {
                                        setState(() {
                                          widget.selectedFiles.removeAt(index);
                                        });
                                      },
                                      child: Container(
                                        height: 22,
                                        width: 22,
                                        decoration: BoxDecoration(
                                          color: AppTheme.colorRed,
                                          border: Border.all(
                                            color: AppTheme.colorWhite,
                                          ),
                                          borderRadius: BorderRadius.circular(30.0),
                                        ),
                                        child: Center(
                                          child: Icon(
                                            Icons.close,
                                            color: AppTheme.colorWhite,
                                            size: 14,
                                          ),
                                        ),
                                      ),
                                    ),
                                  ),
                                ),
                              );
                            }).toList(),
                          ),
                        ),

                      if (widget.pageName!.equalsIgnoreCase(Strings.reject))
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                      Row(
                        children: [
                          Expanded(
                            child: InkWell(
                              onTap: () {
                                if (controller.text.isNullOrEmpty()) {
                                  Utils.showSnackbar(
                                      Strings.ERROR,
                                      Strings.please_enter_remarks,
                                      AppTheme.colorWhite,
                                      AppTheme.colorRed);
                                  return;
                                } else {
                                  widget.cafCustomerApproveRejectBtnAction!
                                      .cafCustomerApproveRejectStatus(
                                    identifier: widget.pageName!
                                        .equalsIgnoreCase(Strings.approve)
                                        ? Strings.approve
                                        : Strings.reject,
                                    remarkController: controller,
                                    context: context,
                                    customerDetail: widget.customerDetail,
                                    allFiles: widget.selectedFiles,
                                  );
                                }
                              },
                              child: Container(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.SCREEN_PADDING),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                      color: AppTheme.colorIconGrey, width: 1.0),
                                  borderRadius: const BorderRadius.only(
                                      bottomLeft: Radius.circular(Constant.SMALL_PADDING)),
                                ),
                                child: Text(
                                  Strings.submit,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: AppTheme.medium + 1,
                                    color: AppTheme.colorPositive,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                          ),
                          Expanded(
                            child: InkWell(
                              onTap: () {
                                Get.back();
                              },
                              child: Container(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.SCREEN_PADDING),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                      color: AppTheme.colorIconGrey, width: 1.0),
                                  borderRadius: const BorderRadius.only(
                                      bottomRight:
                                      Radius.circular(Constant.SMALL_PADDING)),
                                ),
                                child: Text(
                                  Strings.cancel,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: AppTheme.medium + 1,
                                    color: AppTheme.colorNagative,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              )

          ),
          Positioned(
            child: GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Align(
                alignment: Alignment.topRight,
                child: Icon(Icons.close, color: AppTheme.colorWhite),
              ),
            ),
          ),
        ],
      ),
    );
  }

  reviewEditor() {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SMALL_PADDING),
          InputTitleRequire(title: Strings.remarks, require: true),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(7.0),
              color: AppTheme.colorWhite,
            ),
            child: TextFormField(
              controller: controller,
              maxLines: 3,
              maxLength: 250,
              style: const TextStyle(fontSize: AppTheme.medium),
              decoration: InputDecoration(
                hintText: Strings.remarks,
                alignLabelWithHint: true,
                contentPadding:
                    const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
                focusColor: Colors.transparent,
                focusedBorder: OutlineInputBorder(
                  borderRadius:
                      BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide:
                      BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius:
                      BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                  borderSide: BorderSide(
                    color: AppTheme.colorIconGrey,
                    width: 1.0,
                  ),
                ),
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(
                        Constant.TEXT_FIELD_CONTENT_PADDING)),
                isDense: true,
                labelStyle: TextStyle(
                  color: AppTheme.colorGrey,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.normal,
                  height: 1,
                  fontFamily: AppTheme.appFontName,
                  decoration: TextDecoration.none,
                ),
                counterText: "",
              ),
              keyboardType: TextInputType.multiline,
              validator: (value) {
                return null;
              },
            ),
          ),
          const SizedBox(height: Constant.SMALL_PADDING),
        ],
      ),
    );
  }
}

abstract class CafCustomerApproveRejectBtnAction {
  void cafCustomerApproveRejectStatus({
    String identifier,
    TextEditingController remarkController,
    CustomerDetail? customerDetail,
    BuildContext context,
    List<FileDetail> allFiles,
  });
}

Future<bool> hasCameraPermission() async {
  bool isAndroid13OrAbove = false;

  if (Platform.isAndroid) {
    var androidInfo = await DeviceInfoPlugin().androidInfo;
    isAndroid13OrAbove = androidInfo.version.sdkInt >= 33;
  }

  var storagePermission =
      isAndroid13OrAbove ? Permission.photos : Permission.storage;

  var storageStatus = await storagePermission.status;

  // ✅ Return true only if both are already granted
  return storageStatus.isGranted;
}

Future<bool> requestCameraPermissionAndCapture() async {
  bool isAndroid13OrAbove = false;

  if (Platform.isAndroid) {
    var androidInfo = await DeviceInfoPlugin().androidInfo;
    isAndroid13OrAbove = androidInfo.version.sdkInt >= 33;
  }

  var storagePermission =
      isAndroid13OrAbove ? Permission.photos : Permission.storage;

  Map<Permission, PermissionStatus> statuses = await [
    storagePermission,
  ].request();

  return statuses[storagePermission]!.isGranted;
}
