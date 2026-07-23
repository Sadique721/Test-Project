import 'dart:developer';

import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/caf_service_managment_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

import '../../../util/resources.dart';
import '../../customer/model/response/customer_status_list_res.dart';

class CafServiceTerminationDialog extends StatefulWidget {
  final CafServiceTerminationStatusBtnAction serviceTerminationStatusBtnAction;
  final String from;
  List<CustomerStatusDetail>? deActiveReasonList;
  CustomerStatusDetail? selectedDeactivateReason;
  CafServiceManagementController? controller;
  CustomerPlanServiceDetail? customerPlanServiceDetail;
  final String? subTypeService;

  CafServiceTerminationDialog({
    Key? key,
    required this.serviceTerminationStatusBtnAction,
    required this.deActiveReasonList,
    required this.controller,
    required this.customerPlanServiceDetail,
    required this.from,
    required this.subTypeService,
  }) : super(key: key);

  @override
  _ServiceTerminationDialogState createState() =>
      _ServiceTerminationDialogState();
}

class _ServiceTerminationDialogState extends State<CafServiceTerminationDialog> {
  TextEditingController remarkController = TextEditingController();

  @override
  void initState() {
    super.initState();

    log("deActiveReasonList==>${widget.deActiveReasonList}");
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
    if (widget.from.equalsIgnoreCase(Strings.cust_service_termination)) {
      title = "${Strings.delete} ${Strings.reason}";
    } else if (widget.from.equalsIgnoreCase(Strings.cust_service_hold)) {
      if(widget.subTypeService!.equalsIgnoreCase("Start")){
        title = "${Strings.start} ${Strings.reason}";
      }else{
        title = "${Strings.pause} ${Strings.reason}";
      }
    } else if (widget.from.equalsIgnoreCase(Strings.cust_service_stop)) {
      title = "${Strings.stop} ${Strings.reason}";
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
        backgroundColor: AppTheme.colorWhite,
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
                  Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: CustomText(
                        title: title,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.large,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(height: Constant.MEDIUM_PADDING),
                  widget.subTypeService!.equalsIgnoreCase("Start")
                      ? const SizedBox.shrink()
                      : Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                  right: Constant.SMALL_PADDING),
                              child: InputTitleRequire(
                                  title: Strings.deactiveReason, require: true),
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            Padding(
                              padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                  right: Constant.SMALL_PADDING),
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
                                    child: Text(Strings.reason,
                                        style: TextStyle(
                                          fontSize: AppTheme.medium,
                                          color: AppTheme.colorIconGrey,
                                          fontFamily: AppTheme.appFontName,
                                        ))),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: true,
                                isDense: true,
                                value: widget.selectedDeactivateReason,
                                items: widget.deActiveReasonList!
                                    .map((CustomerStatusDetail value) {
                                  return DropdownMenuItem<CustomerStatusDetail>(
                                    value: value,
                                    child: Align(
                                      alignment: Alignment.centerLeft,
                                      child: CustomText(
                                        title: value.text!,
                                        colors: AppTheme.colorBlack,
                                        textAlign: TextAlign.start,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                      ), //Text(value.desig!),
                                    ),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  widget.selectedDeactivateReason =
                                      value as CustomerStatusDetail?;
                                  widget.controller!.update();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      widget.selectedDeactivateReason == null) {
                                    return Strings
                                        .please_select_deactivate_reason;
                                  }
                                  return null;
                                },
                              ),
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                          ],
                        ),
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
                          if (value!.isEmpty) {
                            return Strings.please_select_remark;
                          } else {
                            return null;
                          }
                        },
                      ),
                    ),
                  ),
                  const SizedBox(height: Constant.MEDIUM_PADDING * 2),
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
                              widget.serviceTerminationStatusBtnAction
                                  .cafServiceTerminationBtnAction(
                                      identifier: widget.from,
                                      customerStatusDetail:
                                          widget.selectedDeactivateReason,
                                      customerPlanServiceDetail:
                                          widget.customerPlanServiceDetail,
                                      remark: remarkController.text,
                                  subServiceType: widget.subTypeService);
                            }
                          },
                          child: Container(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.LARGE_PADDING),
                            decoration: BoxDecoration(
                              border: Border.all(
                                color: AppTheme.colorLightGrey,
                                width: 0.0,
                              ),
                              color: AppTheme.colorPrimary,
                              borderRadius: const BorderRadius.only(
                                  bottomLeft: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.submit,
                              colors: AppTheme.colorWhite,
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
                                color: AppTheme.colorPrimary,
                                width: 0.0,
                              ),
                              borderRadius: const BorderRadius.only(
                                  bottomRight: Radius.circular(6.0)),
                            ),
                            child: CustomText(
                              title: Strings.cancel,
                              colors: AppTheme.colorNagative,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w600,
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

abstract class CafServiceTerminationStatusBtnAction {
  void cafServiceTerminationBtnAction(
      {String? identifier,
      CustomerStatusDetail? customerStatusDetail,
      String? remark,
      CustomerPlanServiceDetail? customerPlanServiceDetail,String? subServiceType});
}
