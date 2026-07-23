import 'package:savbill/pages/customer/customer_list_controller.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ChangeCustomerStatusDialog extends StatefulWidget {
  final ChangeCustomerStatusBtnAction changeCustomerStatusBtnAction;
  final List<CustomerStatusDetail> statusList;
  final CustomerDetail custDetail;
 final CustomerListController? controller;

  const ChangeCustomerStatusDialog(
      {Key? key,
      required this.changeCustomerStatusBtnAction,
      required this.statusList,
      required this.custDetail,
      required this.controller,
      })
      : super(key: key);

  @override
  _ChangeCustomerStatusDialogState createState() =>
      _ChangeCustomerStatusDialogState();
}

class _ChangeCustomerStatusDialogState
    extends State<ChangeCustomerStatusDialog> {
  TextEditingController remarksController = TextEditingController();
  List<CustomerStatusDetail>? statusList = [];
  CustomerStatusDetail? selectedStatus;

  @override
  void initState() {
    super.initState();
    statusList?.clear();
    statusList?.addAll(widget.statusList);
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
                        title: Strings.change_status,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.large,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  const SizedBox(height: Constant.SMALL_PADDING),
                  Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: Align(
                      alignment: Alignment.centerLeft,
                      child: CustomText(
                        title:
                            "${Strings.current_status} : ${widget.custDetail.status}",
                        colors: AppTheme.lable_noramal,
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.normal,
                      ),
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.only(
                        top: Constant.SMALL_PADDING,
                        left: Constant.SMALL_PADDING,
                        right: Constant.SMALL_PADDING),
                    child: Container(
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(7.0),
                        color: AppTheme.colorWhite,
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField(
                          icon: SvgPicture.asset(
                            downArrowSvg,
                            height: Constant.DROP_DOWN_ARROW_W_H,
                            width: Constant.DROP_DOWN_ARROW_W_H,
                            color: AppTheme.colorBlack,
                            fit: BoxFit.fill,
                          ),
                          decoration: InputDecoration(
                              filled: true,
                              contentPadding: const EdgeInsets.fromLTRB(
                                  Constant.LARGE_PADDING,
                                  0,
                                  Constant.LARGE_PADDING,
                                  0),
                              fillColor: AppTheme.colorWhite,
                              hintText: Strings.select_new_status,
                              hintStyle: AppTheme.dropdownHintStyle,
                              labelStyle: AppTheme.dropdownLabelStyle,
                              errorStyle: AppTheme.dropdownErrorStyle,
                              alignLabelWithHint: true,
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.DROP_DOWN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorBlack, width: 0.8),
                              ),
                              focusColor: Colors.transparent,
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.DROP_DOWN_ROUNDED_CORNER),
                                borderSide: BorderSide(
                                    color: AppTheme.colorBlack, width: 0.8),
                              ),
                              errorMaxLines: 3),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: false,
                          isDense: true,
                          value: selectedStatus,
                          items: statusList?.map((CustomerStatusDetail value) {
                            return DropdownMenuItem<CustomerStatusDetail>(
                              value: value,
                              child: Text(value.text!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            setState(() {
                              selectedStatus = value as CustomerStatusDetail?;
                            });
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ),
                  // const SizedBox(height: Constant.SMALL_PADDING),
                  selectedStatus != null && selectedStatus!.text!.equalsIgnoreCase("Terminate") ? reviewEditor() : const SizedBox.shrink(),
                  const SizedBox(height: Constant.MEDIUM_PADDING * 2),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: () {
                            if (selectedStatus == null) {
                              Utils.showSnackbar(
                                  Strings.ERROR,
                                  Strings.please_select_status,
                                  AppTheme.colorWhite,
                                  AppTheme.colorRed);
                              return;
                            }else if(selectedStatus != null && selectedStatus!.text!.equalsIgnoreCase("Terminate")  && remarksController.text.isEmpty){
                              Utils.showSnackbar(
                                  Strings.ERROR,
                                  Strings.please_select_remark,
                                  AppTheme.colorWhite,
                                  AppTheme.colorRed);
                              return;
                            }
                            widget.changeCustomerStatusBtnAction
                                .changeCustomerStatusBtnAction(
                                    identifier: Strings.submit,
                                    customerStatus: selectedStatus!,
                                    custDetail: widget.custDetail,
                                remark: remarksController.text.isNotEmpty ? remarksController.text : null);
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


  reviewEditor() {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SMALL_PADDING),
          InputTitleRequire(title: Strings.remarks, require: false),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(7.0),
              color: AppTheme.colorWhite,
            ),
            child: TextFormField(
              controller: remarksController,
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

abstract class ChangeCustomerStatusBtnAction {
  void changeCustomerStatusBtnAction(
      {String identifier,
      CustomerStatusDetail customerStatus,
      CustomerDetail custDetail,String? remark});
}
