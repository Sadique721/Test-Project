import 'dart:developer';

import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TicketAssignDialog extends StatefulWidget {
  final TicketAssignAction ticketAssignAction;
  final List<TicketAssignStaff> itemsOrgLst;
  final String? staffStatus;

  const TicketAssignDialog({
    Key? key,
    required this.ticketAssignAction,
    required this.itemsOrgLst,
    this.staffStatus
  }) : super(key: key);

  @override
  _TicketAssignState createState() => _TicketAssignState();
}

class _TicketAssignState extends State<TicketAssignDialog> {
  List<TicketAssignStaff> itemsLst = [];

  @override
  void initState() {
    super.initState();
    setState(() {
      itemsLst.addAll(widget.itemsOrgLst);
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = "${Strings.select} ${Strings.assign_staff}";
    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          // AlertDialog(
          //   insetPadding: const EdgeInsets.only(
          //     top: Constant.SCREEN_PADDING * 2,
          //   ),
          //   contentPadding: const EdgeInsets.only(
          //     top: Constant.SCREEN_PADDING,
          //   ),
          //   clipBehavior: Clip.antiAliasWithSaveLayer,
          //   backgroundColor: AppTheme.colorWhite,
          //   shape: const RoundedRectangleBorder(
          //       borderRadius:
          //           BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
          //   content: Container(
          //     width: MediaQuery.of(context).size.width,
          //     color: AppTheme.colorWhite,
          //     child: Column(
          //         mainAxisSize: MainAxisSize.min,
          //         mainAxisAlignment: MainAxisAlignment.start,
          //         crossAxisAlignment: CrossAxisAlignment.start,
          //         children: [
          //           Padding(
          //             padding: const EdgeInsets.symmetric(
          //                 horizontal: Constant.SCREEN_PADDING),
          //             child: Align(
          //               alignment: Alignment.centerLeft,
          //               child: CustomText(
          //                 title: title,
          //                 colors: AppTheme.title_dark,
          //                 fontSize: AppTheme.large,
          //                 fontWeight: FontWeight.w600,
          //               ),
          //             ),
          //           ),
          //           const SizedBox(height: Constant.SMALL_PADDING),
          //           Padding(
          //             padding: const EdgeInsets.symmetric(
          //                 horizontal: Constant.SCREEN_PADDING - 5),
          //             child: Divider(
          //               height: 5,
          //               color: AppTheme.dividerColor,
          //               thickness: 1,
          //             ),
          //           ),
          //           const SizedBox(height: Constant.SMALL_PADDING),
          //           Flexible(
          //               child: ListView.builder(
          //             shrinkWrap: true,
          //             primary: false,
          //             itemCount: itemsLst.length,
          //             itemBuilder: (context, index) {
          //               TicketAssignStaff item = itemsLst[index];
          //               return Column(
          //                 mainAxisAlignment: MainAxisAlignment.start,
          //                 crossAxisAlignment: CrossAxisAlignment.start,
          //                 children: [
          //                   InkWell(
          //                     onTap: () {
          //                       for (var f in itemsLst) {
          //                         if (f.id == item.id!) {
          //                           if (f.selected == null) {
          //                             f.selected = true;
          //                           } else {
          //                             f.selected = !f.selected!;
          //                           }
          //                         } else {
          //                           f.selected = false;
          //                         }
          //                       }
          //                       setState(() {
          //                         itemsLst = itemsLst;
          //                       });
          //                     },
          //                     child: Padding(
          //                       padding: const EdgeInsets.symmetric(
          //                           vertical: Constant.SMALL_PADDING + 1,
          //                           horizontal: Constant.MEDIUM_PADDING),
          //                       child: Row(
          //                         children: [
          //                           item.selected == true
          //                               ? Icon(
          //                                   Icons.check,
          //                                   color: AppTheme.colorPrimary,
          //                                   size: Constant.ICON_SIZE_M,
          //                                 )
          //                               : const Icon(
          //                                   Icons.check,
          //                                   color: Colors.white,
          //                                   size: Constant.ICON_SIZE_M,
          //                                 ),
          //                           const SizedBox(
          //                             width: Constant.SMALL_PADDING,
          //                           ),
          //                           CustomText(
          //                             title:
          //                                 "Name : ${item.firstname!} ${item.lastname!}\nUsername : ${item.username!} ",
          //                             textAlign: TextAlign.start,
          //                             colors: item.selected != null &&
          //                                     item.selected == true
          //                                 ? AppTheme.colorPrimary
          //                                 : AppTheme.lable_noramal,
          //                             fontSize: AppTheme.small + 1,
          //                             fontWeight: FontWeight.w500,
          //                           ),
          //                         ],
          //                       ),
          //                     ),
          //                   ),
          //                   index == (itemsLst.length - 1)
          //                       ? Container()
          //                       : Padding(
          //                           padding: const EdgeInsets.symmetric(
          //                               horizontal:
          //                                   Constant.SCREEN_PADDING - 5),
          //                           child: Divider(
          //                             height: 5,
          //                             color: AppTheme.dividerColor,
          //                             thickness: 0.5,
          //                           ),
          //                         ),
          //                 ],
          //               );
          //             },
          //           )),
          //           const SizedBox(height: Constant.SMALL_PADDING),
          //           Row(
          //             children: [
          //               Expanded(
          //                 child: InkWell(
          //                   onTap: () {
          //                     validateSelection();
          //                   },
          //                   child: Container(
          //                     padding: const EdgeInsets.only(
          //                         top: Constant.SCREEN_PADDING,
          //                         bottom: Constant.SCREEN_PADDING),
          //                     decoration: BoxDecoration(
          //                       border: Border.all(
          //                         color: AppTheme.colorIconGrey,
          //                         width: 1.0,
          //                       ),
          //                       borderRadius: const BorderRadius.only(
          //                           bottomLeft: Radius.circular(
          //                               Constant.SMALL_PADDING)),
          //                     ),
          //                     child: Text(
          //                       Strings.select,
          //                       style: TextStyle(
          //                         fontWeight: FontWeight.bold,
          //                         fontSize: AppTheme.medium + 1,
          //                         color: AppTheme.colorPositive,
          //                       ),
          //                       textAlign: TextAlign.center,
          //                     ),
          //                   ),
          //                 ),
          //               ),
          //               Expanded(
          //                 child: InkWell(
          //                   onTap: () {
          //                     Get.back();
          //                   },
          //                   child: Container(
          //                     padding: const EdgeInsets.only(
          //                         top: Constant.SCREEN_PADDING,
          //                         bottom: Constant.SCREEN_PADDING),
          //                     decoration: BoxDecoration(
          //                       border: Border.all(
          //                         color: AppTheme.colorIconGrey,
          //                         width: 1.0,
          //                       ),
          //                       borderRadius: const BorderRadius.only(
          //                           bottomRight: Radius.circular(
          //                               Constant.SMALL_PADDING)),
          //                     ),
          //                     child: Text(
          //                       Strings.cancel,
          //                       style: TextStyle(
          //                         fontWeight: FontWeight.bold,
          //                         fontSize: AppTheme.medium + 1,
          //                         color: AppTheme.colorNagative,
          //                       ),
          //                       textAlign: TextAlign.center,
          //                     ),
          //                   ),
          //                 ),
          //               ),
          //             ],
          //           ),
          //         ]),
          //   ),
          // ),
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
                          TicketAssignStaff item = itemsLst[index];

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
                                                fontSize: AppTheme.small,
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
                                                overflow: TextOverflow.ellipsis,
                                                textAlign: TextAlign.start,
                                                colors: item.selected != null &&
                                                    item.selected == true
                                                    ? AppTheme.colorPrimary
                                                    : AppTheme.title_dark,
                                                fontSize: AppTheme.small,
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

  validateSelection() {
    // TicketAssignStaff? selectedItem;
    // for (var element in itemsLst) {
    //   if (element.selected != null && element.selected == true) {
    //     selectedItem = element;
    //   }
    // }
    // if (selectedItem != null) {
    //   widget.ticketAssignAction
    //       .ticketAssignBtnAction(selectedItem: selectedItem);
    // } else {
    //   Utils.showSnackbar(Strings.ERROR, "Please select at-lease one item",
    //       AppTheme.colorWhite, AppTheme.colorRed);
    // }

    TicketAssignStaff? selectedItem;
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem = element;
        break;
      } else {
        selectedItem = element;
      }
    }
    if (selectedItem!.selected == true) {
      if (widget.staffStatus!.equalsIgnoreCase(Strings.approve.toLowerCase())) {
        log("staffStatus===>>> ${Strings.approve}");
        widget.ticketAssignAction.ticketAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.approve);
      } else if (widget.staffStatus!
          .equalsIgnoreCase(Strings.reject.toLowerCase())) {
        log("staffStatus===>>> ${Strings.reject}");
        widget.ticketAssignAction.ticketAssignBtnAction(
            selectedItem: selectedItem,
            isStaffSelected: true,
            approveRejectStatus: Strings.reject);
      }
    } else {
      log("Note selected Staff");
      widget.ticketAssignAction.ticketAssignBtnAction(
          selectedItem: selectedItem, isStaffSelected: false,approveRejectStatus:widget.staffStatus!);
    }
  }
}

abstract class TicketAssignAction {
  void ticketAssignBtnAction(
  {TicketAssignStaff selectedItem,
  bool isStaffSelected,
  String approveRejectStatus}
      // {TicketAssignStaff selectedItem}
      );
}
