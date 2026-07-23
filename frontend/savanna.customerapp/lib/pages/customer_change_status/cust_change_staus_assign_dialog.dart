import 'dart:developer';
import 'package:savbill/pages/customer_change_status/response/customer_terminate_approve_reject_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ChangeStatusAssignDialog extends StatefulWidget {
  final ChangeStatusAssignAction changeStatusAssignAction;
  final List<CustomerTerminateApproveRejectDataList> itemsOrgLst;
  final String? staffStatus;

  const ChangeStatusAssignDialog({
    Key? key,
    required this.changeStatusAssignAction,
    required this.itemsOrgLst,
    required this.staffStatus,
  }) : super(key: key);

  @override
  _ChangeStatusAssignState createState() => _ChangeStatusAssignState();
}

class _ChangeStatusAssignState extends State<ChangeStatusAssignDialog> {
  List<CustomerTerminateApproveRejectDataList> itemsLst = [];
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
    String title = Strings.assign_credit_note;
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
                          CustomerTerminateApproveRejectDataList item = itemsLst[index];

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
                                            CustomText(
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
                                            const SizedBox(
                                              width: Constant.MEDIUM_PADDING,
                                            ),
                                            CustomText(
                                              title: item.username!,
                                              textAlign: TextAlign.start,
                                              colors: item.selected != null &&
                                                  item.selected == true
                                                  ? AppTheme.colorPrimary
                                                  : AppTheme.title_dark,
                                              fontSize: AppTheme.small + 1,
                                              fontWeight: FontWeight.w500,
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
                              log("validiationCall");
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

  validateSelection() {
    CustomerTerminateApproveRejectDataList? selectedItem;
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem = element;
        break;
      } else {
        selectedItem = element;
      }
    }

    log("staffStatusstaffStatusstaffStatusstaffStatus===>>> ${widget.staffStatus!}");
    if (selectedItem!.selected == true) {
      if (widget.staffStatus!.equalsIgnoreCase(Strings.approved)) {
        // log("staffStatus===>>> ${Strings.approve}");
        widget.changeStatusAssignAction.changeStatusAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.approve);
      } else if (widget.staffStatus!
          .equalsIgnoreCase(Strings.rejected.toLowerCase())) {
        // log("staffStatus===>>> ${Strings.rejected}");
        widget.changeStatusAssignAction.changeStatusAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.reject);
      }
    } else {
      log("Note selected Staff");
      widget.changeStatusAssignAction.changeStatusAssignBtnAction(
          selectedItem: selectedItem, isStaffSelected: false,approveRejectStatus:widget.staffStatus!);
    }
  }

  // validateSelection() {
  //   CustomerTerminateApproveRejectDataList? selectedItem;
  //   for (var element in itemsLst) {
  //     if (element.selected != null && element.selected == true) {
  //       selectedItem = element;
  //       break;
  //     } else {
  //       selectedItem = element;
  //     }
  //   }
  //
  //
  //   log("StaffStatus==>${widget.staffStatus!}");
  //
  //
  //   if (selectedItem!.selected == true) {
  //     if (widget.staffStatus!.equalsIgnoreCase(Strings.approved)) {
  //       log("staffStatus===>>> ${Strings.approve}");
  //       widget.changeStatusAssignAction.changeStatusAssignBtnAction(
  //           selectedItem: selectedItem,
  //           isStaffSelected: true,
  //           approveRejectStatus: Strings.approve);
  //     } else if (widget.staffStatus!
  //         .equalsIgnoreCase(Strings.reject.toLowerCase())) {
  //       log("staffStatus===>>> ${Strings.reject}");
  //       widget.changeStatusAssignAction.changeStatusAssignBtnAction(
  //           selectedItem: selectedItem,
  //           isStaffSelected: true,
  //           approveRejectStatus: Strings.reject);
  //     }
  //   } else {
  //     log("Note selected Staff");
  //     widget.changeStatusAssignAction.changeStatusAssignBtnAction(
  //         selectedItem: selectedItem, isStaffSelected: false,approveRejectStatus:widget.staffStatus!);
  //   }
  // }

}

abstract class ChangeStatusAssignAction {
  void changeStatusAssignBtnAction(
      {CustomerTerminateApproveRejectDataList selectedItem,
        bool isStaffSelected,
        String approveRejectStatus});
}
