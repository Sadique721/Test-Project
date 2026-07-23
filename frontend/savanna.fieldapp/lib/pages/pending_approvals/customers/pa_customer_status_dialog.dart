import 'dart:io';

import 'package:image_picker/image_picker.dart';
import 'package:path/path.dart';
import 'package:savbill/pages/pending_approvals/model/request/customer_approve_reject_req.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../widgets/file_grid_item.dart';
import '../../customer_caf/caf_customer_approve_reject_dialog.dart';
import '../../model/file_detail.dart';

class PACustomerStatusDialog extends StatefulWidget {
  final PACustomerStatusBtnAction paCustomerStatusBtnAction;
  final CustomerApproveRejectReq customerApproveRejectReq;
  final String from;
  List<FileDetail> selectedFiles = [];

  PACustomerStatusDialog(
      {Key? key,
      required this.paCustomerStatusBtnAction,
      required this.customerApproveRejectReq,
      required this.from})
      : super(key: key);

  @override
  _PACustomerStatusDialogtate createState() => _PACustomerStatusDialogtate();
}

class _PACustomerStatusDialogtate extends State<PACustomerStatusDialog> {
  TextEditingController remarkController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(BuildContext context) {
    String title = "";
    if (widget.from.equalsIgnoreCase(Strings.approve)) {
      title = "${Strings.approve} ${Strings.customer}";
    } else if (widget.from.equalsIgnoreCase(Strings.reject)) {
      title = "${Strings.reject} ${Strings.customer}";
    }
    return Stack(children: [
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
          child: SingleChildScrollView(
            child: Column(
                mainAxisSize: MainAxisSize.min,
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Padding(
                  //   padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  //   child: Align(
                  //     alignment: Alignment.centerLeft,
                  //     child: CustomText(
                  //       title: title,
                  //       colors: AppTheme.title_dark,
                  //       fontSize: AppTheme.large,
                  //       fontWeight: FontWeight.w600,
                  //     ),
                  //   ),
                  // ),

                  Container(
                    color: AppTheme.colorPrimary,
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING,
                        vertical: Constant.MEDIUM_PADDING),
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
                  const SizedBox(height: Constant.MEDIUM_PADDING),
                  Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: InputTitleRequire(
                        title: Strings.remarks, require: true),
                  ),
                  const SizedBox(
                    height: Constant.SMALL_PADDING,
                  ),
                  Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: TextFormField(
                        controller: remarkController,
                        maxLines: 3,
                        maxLength: 250,
                        style: const TextStyle(fontSize: AppTheme.medium),
                        decoration: InputDecoration(
                          hintText: Strings.remarks,
                          alignLabelWithHint: true,
                          contentPadding: const EdgeInsets.all(
                              Constant.TEXT_FIELD_CONTENT_PADDING),
                          focusColor: Colors.transparent,
                          focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER),
                            borderSide: BorderSide(
                                color: AppTheme.colorPrimary, width: 1.0),
                          ),
                          enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER),
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
                  ),
                  if (widget.from.equalsIgnoreCase(Strings.reject))
                    const SizedBox(height: Constant.MEDIUM_PADDING),

                  if (widget.from.equalsIgnoreCase(Strings.reject))
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

                  if (widget.from.equalsIgnoreCase(Strings.reject))
                    SingleChildScrollView(
                      scrollDirection: Axis.horizontal,
                      child: Row(
                        children:
                            widget.selectedFiles.asMap().entries.map((entry) {
                          final index = entry.key;
                          final fileDetail = entry.value;
                          return Padding(
                            padding: const EdgeInsets.all(
                                Constant.VERY_SMALL_PADDING),
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

                  const SizedBox(height: Constant.MEDIUM_PADDING),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: () {
                            if (remarkController.text.isNullOrEmpty()) {
                              Utils.showSnackbar(
                                  Strings.ERROR,
                                  Strings.please_enter_remarks,
                                  AppTheme.colorWhite,
                                  AppTheme.colorRed);
                              return;
                            }
                            widget.customerApproveRejectReq.remark =
                                remarkController.text;

                            widget.paCustomerStatusBtnAction
                                .paCustomerStatusBtnAction(
                              identifier: Strings.submit,
                              customerApproveRejectReq:
                                  widget.customerApproveRejectReq,
                              allFiles: widget.selectedFiles,
                            );
                          },
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.LARGE_PADDING),
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.colorLightGrey,
                                width: 1.0,
                              ),
                              borderRadius: const BorderRadius.only(
                                  bottomLeft: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.submit,
                              colors: AppTheme.colorPositive,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
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
                                vertical: Constant.LARGE_PADDING),
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.colorLightGrey,
                                width: 1.0,
                              ),
                              borderRadius: const BorderRadius.only(
                                  bottomRight: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.cancel,
                              colors: AppTheme.colorNagative,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ]),
          ),
        ),
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
    ]);
  }
}

abstract class PACustomerStatusBtnAction {
  void paCustomerStatusBtnAction(
      {String identifier, CustomerApproveRejectReq customerApproveRejectReq,List<FileDetail> allFiles,});
}
