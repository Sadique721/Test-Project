import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer_caf/response/approve_reject_caf_customer_res.dart';
import 'package:savbill/pages/ticket_system/model/response/approve_reject_ticket_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CafCustomerAssignDialog extends StatefulWidget {
  final CafCustomerAssignAction cafCustomerAssignAction;
  final List<ApproveRejectCafDataList> itemsOrgLst;

  final String? staffStatus;
  final int? entityId;
  TextEditingController? controller;

  CafCustomerAssignDialog({
    Key? key,
    required this.cafCustomerAssignAction,
    required this.itemsOrgLst,
    required this.staffStatus,
    required this.entityId,
    this.controller,
  }) : super(key: key);

  @override
  _CafCustomerAssignState createState() => _CafCustomerAssignState();
}

class _CafCustomerAssignState extends State<CafCustomerAssignDialog> {
  List<ApproveRejectCafDataList> itemsLst = [];
  bool? selectedStaffStatus;

  @override
  void initState() {
    super.initState();
    setState(() {
      itemsLst.addAll(widget.itemsOrgLst);
    });
  }

  @override
  Widget build(BuildContext context) {
    // String title = "Select or Assign Staff";
    // String title = Strings.assign_credit_note;
    String title =
        "${widget.staffStatus.toString().capitalizeFirst} ${Strings.customer}";
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
                          vertical: Constant.MEDIUM_PADDING,
                          horizontal: Constant.SCREEN_PADDING),
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
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Container(
                        width: MediaQuery.of(context).size.width,
                        margin: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: Material(
                            color: AppTheme.colorWhite,
                            elevation: 1.5,
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(
                                    Constant.BTN_ROUNDED_CORNER - 2)),
                            child: Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Row(
                                  children: [
                                    Expanded(
                                      flex:3,
                                      child: Padding(
                                        padding: const EdgeInsets.symmetric(
                                            horizontal:
                                                Constant.SMALL_PADDING),
                                        child: CoustomTextField(
                                            labelText:
                                                Strings.search_global_filter,
                                            hintColor: AppTheme.colorIconGrey,
                                            textEditingController:
                                                widget.controller,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            borderFocusColors:
                                                AppTheme.colorBlack,
                                            textColor: AppTheme.colorBlack,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                            contentPadding:
                                                const EdgeInsets.symmetric(
                                                    horizontal: Constant
                                                        .MEDIUM_PADDING),
                                            borderCorner:
                                                Constant.BTN_ROUNDED_CORNER,
                                            keyboardType: TextInputType.text,
                                            maxLines: 1,
                                            onChanged: (value) {
                                              searchData(value);
                                            },
                                            onTextValidator: (value) {},
                                            onTextFiledOnTap: () {},
                                            readOnly: false),
                                      ),
                                    ),
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          clearData();
                                        },
                                        radius: Constant.BTN_HEIGHT_M,
                                        height: Constant.BTN_HEIGHT_M,
                                        bgColors: AppTheme.colorBlack,
                                        borderColors: AppTheme.colorBlack,
                                        child: CustomText(
                                          title: Strings.clear,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.normal,
                                        ),
                                      ),
                                    ),
                                  ],
                                )))),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.SMALL_PADDING,
                          horizontal: Constant.SCREEN_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: Strings.select_staff,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        CustomText(
                          title: Strings.name,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.username,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ],
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING - 5),
                      child: Divider(
                        height: 5,
                        color: AppTheme.dividerColor,
                        thickness: 1,
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Flexible(
                      child: ListView.builder(
                        shrinkWrap: true,
                        primary: false,
                        itemCount: itemsLst.length,
                        itemBuilder: (context, index) {
                          ApproveRejectCafDataList item = itemsLst[index];

                          return Column(
                            // mainAxisAlignment: MainAxisAlignment.spaceAround,
                            // crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              InkWell(
                                onTap: () {
                                  for (var f in itemsLst) {
                                    if (f.id == item.id!) {
                                      if (f.selected == null) {
                                        f.selected = true;
                                      } else {
                                        f.selected = !f.selected!;
                                      }
                                    } else {
                                      f.selected = false;
                                    }
                                  }
                                  setState(() {
                                    itemsLst = itemsLst;
                                  });
                                },
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(
                                      vertical: Constant.SMALL_PADDING + 1,
                                      horizontal: Constant.MEDIUM_PADDING),
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      item.selected == true
                                          ? Icon(
                                              Icons.check,
                                              color: AppTheme.colorPrimary,
                                              size: Constant.ICON_SIZE_M,
                                            )
                                          : const Icon(
                                              Icons.check,
                                              color: Colors.white,
                                              size: Constant.ICON_SIZE_M,
                                            ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        child: Row(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceEvenly,
                                          crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                          children: [
                                            Expanded(
                                              flex:1,
                                              child: CustomText(
                                                title:
                                                    "${item.firstname!} ${item.lastname!}",
                                                textAlign: TextAlign.start,
                                                colors: item.selected != null &&
                                                        item.selected == true
                                                    ? AppTheme.colorPrimary
                                                    : AppTheme.title_dark,
                                                fontSize: AppTheme.small + 1,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                            const SizedBox(
                                              width: Constant.MEDIUM_PADDING,
                                            ),
                                            Expanded(
                                              flex:1,
                                              child: CustomText(
                                                title: item.username!,
                                                textAlign: TextAlign.start,
                                                colors: item.selected != null &&
                                                        item.selected == true
                                                    ? AppTheme.colorPrimary
                                                    : AppTheme.title_dark,
                                                fontSize: AppTheme.small + 1,
                                                fontWeight: FontWeight.w500,
                                              ),
                                            ),
                                          ],
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                    ],
                                  ),
                                ),
                              ),
                              index == (itemsLst.length - 1)
                                  ? Container()
                                  : Padding(
                                      padding: const EdgeInsets.symmetric(
                                          horizontal:
                                              Constant.SCREEN_PADDING - 5),
                                      child: Divider(
                                        height: 5,
                                        color: AppTheme.dividerColor,
                                        thickness: 0.5,
                                      ),
                                    ),
                            ],
                          );
                        },
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              validateSelection();
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
                                Strings.assign,
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

  searchData(String value) {
    itemsLst.clear();
    log("searchData==>$value");
    if (value.isEmpty) {
      setState(() {
        itemsLst.addAll(widget.itemsOrgLst);
      });
    } else {
      // for (InwardMacMapDetail detail in inwardMacMapListOrg!) {
      for (ApproveRejectCafDataList detail in widget.itemsOrgLst) {
        if (detail.firstname!.containsIgnoreCase(value)) {
          setState(() {
            itemsLst.add(detail);
          });
        } else if (detail.lastname.toString().containsIgnoreCase(value)) {
          setState(() {
            itemsLst.add(detail);
          });
          // itemsLst.add(detail);
        } else if (detail.username!.containsIgnoreCase(value)) {
          setState(() {
            itemsLst.add(detail);
          });
          // itemsLst.add(detail);
        }
      }
      log("searchData==>${jsonEncode(itemsLst)}");
    }
  }

  clearData() {
    widget.controller!.clear();
    itemsLst.clear();
    setState(() {
      itemsLst.addAll(widget.itemsOrgLst);
    });

  }

  validateSelection() {
    ApproveRejectCafDataList? selectedItem;
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem = element;
        break;
      } else {
        selectedItem = element;
      }
    }

    if (selectedItem!.selected == true) {
      if (widget.staffStatus!
          .equalsIgnoreCase(Strings.approved.toLowerCase())) {
        widget.cafCustomerAssignAction.cafCustomerAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.approve,
            entityId: widget.entityId);
      } else if (widget.staffStatus!
          .equalsIgnoreCase(Strings.rejected.toLowerCase())) {
        widget.cafCustomerAssignAction.cafCustomerAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.reject,
            entityId: widget.entityId);
      }
    } else {
      log("Note selected Staff");
      widget.cafCustomerAssignAction.cafCustomerAssignBtnAction(
          selectedItem: selectedItem,
          isStaffSelected: false,
          approveRejectStatus: widget.staffStatus!,
          entityId: widget.entityId);
    }
  }
}

abstract class CafCustomerAssignAction {
  void cafCustomerAssignBtnAction(
      {ApproveRejectCafDataList selectedItem,
      bool isStaffSelected,
      String approveRejectStatus,
      int? entityId});
}
