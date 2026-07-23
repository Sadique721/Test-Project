import 'package:savbill/pages/shift_location/request/approve_customer_address_req.dart';
import 'package:savbill/pages/shift_location/shift_location_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ApproveRejectShiftLocationDialog extends StatefulWidget {
  String? pageName;
  ShiftLocationController? controller;
  ApproveCustomerAddressReq? approveCustomerAddressReq;
  final ApproveRejectShiftBtnAction? approveRejectShiftBtnAction;

  ApproveRejectShiftLocationDialog(
      {Key? key, this.pageName,this.controller, this.approveCustomerAddressReq,this.approveRejectShiftBtnAction})
      : super(key: key);

  @override
  _AddRemarkInvoiceState createState() => _AddRemarkInvoiceState();
}

class _AddRemarkInvoiceState extends State<ApproveRejectShiftLocationDialog> {
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.pageName!.equalsIgnoreCase(Strings.approve)) {
      title = "${Strings.approve.toLowerCase().capitalizeFirst} ${Strings.shift_location}";
    } else if (widget.pageName!.equalsIgnoreCase(Strings.reject)) {
      title = "${Strings.reject.toLowerCase().capitalizeFirst} ${Strings.shift_location}";
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
            backgroundColor: AppTheme.colorAccent,
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
                      color: AppTheme.colorAccent,
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING,
                          vertical: Constant.SMALL_PADDING),
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

                    Divider(
                      height: 1,
                      color: AppTheme.dividerColor,
                      thickness: 1,
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    reviewEditor(),
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
                                widget.approveCustomerAddressReq!.remark = controller.text;
                                if(widget.pageName!.equalsIgnoreCase(Strings.approve)){
                                  widget.approveCustomerAddressReq!.flag = "approved";
                                }else if(widget.pageName!.equalsIgnoreCase(Strings.reject)){
                                  widget.approveCustomerAddressReq!.flag = "Rejected";
                                }
                                widget.approveCustomerAddressReq!.remark = controller.text;
                                widget.approveRejectShiftBtnAction!
                                    .approveRejectShiftDetails(
                                    identifier: widget.pageName!
                                        .equalsIgnoreCase(
                                        Strings.approve)
                                        ? Strings.approve
                                        : Strings.reject,
                                    remarkController: controller,
                                    approveCustomerAddressReq:
                                    widget.approveCustomerAddressReq);
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
                                // widget.pageName!.equalsIgnoreCase(Strings.approved.toLowerCase())
                                  widget.pageName!.equalsIgnoreCase(Strings.approve)
                                    ? Strings.approve
                                    : Strings.reject,
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

abstract class ApproveRejectShiftBtnAction {
  void approveRejectShiftDetails(
      {String identifier, TextEditingController remarkController,ApproveCustomerAddressReq? approveCustomerAddressReq});
}
