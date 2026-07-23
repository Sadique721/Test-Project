import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/lead_management/view_lead_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import 'model/lead_all_rejected_reason_res.dart';

class LeadApproveRejectDialog extends StatefulWidget {
  String? pageName;
  final LeadApproveRejectBtnAction? leadApproveRejectBtnAction;
  LeadMasterListData? item;
  ViewLeadController? controller;

  LeadApproveRejectDialog(
      {Key? key,
      this.pageName,
      this.leadApproveRejectBtnAction,
      this.item,
      this.controller})
      : super(key: key);

  @override
  _LeadApproveRejectState createState() => _LeadApproveRejectState();
}

class _LeadApproveRejectState extends State<LeadApproveRejectDialog> {
  TextEditingController remarkController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.pageName!.equalsIgnoreCase(Strings.approve)) {
      title = "${Strings.approve} ${Strings.remarks}";
    } else if (widget.pageName!.equalsIgnoreCase(Strings.reject)) {
      title = "${Strings.reject} ${Strings.remarks}";
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
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
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
                    const SizedBox(height: Constant.SMALL_PADDING),
                    // Divider(
                    //   height: 5,
                    //   color: AppTheme.dividerColor,
                    //   thickness: 1,
                    // ),
                    reviewEditor(),
                    widget.pageName!.equalsIgnoreCase(Strings.reject)
                        ? rejectedView()
                        : const SizedBox.shrink(),
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
                              } else {
                                widget.leadApproveRejectBtnAction!
                                    .leadApproveRejectStatus(
                                        identifier: widget.pageName!
                                                .equalsIgnoreCase(
                                                    Strings.approve)
                                            ? Strings.approve
                                            : Strings.reject,
                                        remarkController: remarkController,
                                        caseId: widget.item!.id,context: context,item: widget.item);
                              }
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(
                                        Constant.SMALL_PADDING)),
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
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(
                                        Constant.SMALL_PADDING)),
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
                  ]),
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
              controller: remarkController,
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

  rejectedView() {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          InputTitleRequire(title: Strings.rejectedReasonList, require: true),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          DropdownButtonHideUnderline(
            child: DropdownButtonFormField(
              icon: SvgPicture.asset(
                downArrowSvg,
                height: Constant.DROP_DOWN_ARROW_W_H,
                width: Constant.DROP_DOWN_ARROW_W_H,
                color: AppTheme.colorBlack,
                fit: BoxFit.fill,
              ),
              decoration: Utils.ddlDecoration(),
              hint: Align(
                  alignment: Alignment.centerLeft,
                  child: Text(Strings.ticket_type,
                      style: TextStyle(
                        fontSize: AppTheme.medium,
                        color: AppTheme.colorIconGrey,
                        fontFamily: AppTheme.appFontName,
                      ))),
              style: AppTheme.dropdownTextStyle,
              isExpanded: true,
              isDense: true,
              value: widget.controller!.selectedRejectedReason,
              items: widget.controller!.rejectedReasonList!
                  .map((RejectReasonList value) {
                return DropdownMenuItem<RejectReasonList>(
                  value: value,
                  child: Align(
                    alignment: Alignment.centerLeft,
                    child: CustomText(
                      title: value.name,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                    ), //Text(value.desig!),
                  ),
                );
              }).toList(),
              onChanged: (value) {
                widget.controller!.selectedRejectedReason =
                    value as RejectReasonList?;
                widget.controller!.update();
              },
              validator: (value) {
                if (value == null ||
                    widget.controller!.selectedRejectedReason == null) {
                  return Strings.pleaseRejectedReasonRequired;
                }
                return null;
              },
            ),
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }
}

abstract class LeadApproveRejectBtnAction {
  void leadApproveRejectStatus(
      {String identifier, TextEditingController remarkController, int? caseId,BuildContext context,LeadMasterListData? item});
}
