import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ApproveProgressDialog extends StatefulWidget {
  final ApproveProgressAction approveProgressAction;
  final List<PopInventoryDetail> itemsOrgLst;

  const ApproveProgressDialog({
    Key? key,
    required this.approveProgressAction,
    required this.itemsOrgLst,
  }) : super(key: key);

  @override
  _ApproveProgressState createState() => _ApproveProgressState();
}

class _ApproveProgressState extends State<ApproveProgressDialog> {
  List<PopInventoryDetail> itemsLst = [];

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
    String title = Strings.inventory_progress;
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
                          title: Strings.workflow_audit,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.spaceAround,
                      children: [
                        CustomText(
                          title: Strings.partner_name_split,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.action,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small - 1,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.staff_name_split,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small - 1,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.remarks,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small - 1,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.action_date,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small - 1,
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
                    // itemsLst.isEmpty
                    //     ? Flexible(
                    //         child: ListView.builder(
                    //           shrinkWrap: true,
                    //           primary: false,
                    //           itemCount: itemsLst.length,
                    //           itemBuilder: (context, index) {
                    //             PopInventoryDetail item = itemsLst[index];
                    //             return Column(
                    //               // mainAxisAlignment: MainAxisAlignment.spaceAround,
                    //               // crossAxisAlignment: CrossAxisAlignment.center,
                    //               children: [
                    //                 InkWell(
                    //                   // onTap: () {
                    //                   //   for (var f in itemsLst) {
                    //                   //     if (f.id == item.id!) {
                    //                   //       if (f.selected == null) {
                    //                   //         f.selected = true;
                    //                   //       } else {
                    //                   //         f.selected = !f.selected!;
                    //                   //       }
                    //                   //     } else {
                    //                   //       f.selected = false;
                    //                   //     }
                    //                   //   }
                    //                   //   setState(() {
                    //                   //     itemsLst = itemsLst;
                    //                   //   });
                    //                   // },
                    //                   child: Padding(
                    //                     padding: const EdgeInsets.symmetric(
                    //                         vertical:
                    //                             Constant.SMALL_PADDING + 1,
                    //                         horizontal:
                    //                             Constant.MEDIUM_PADDING),
                    //                     child: Row(
                    //                       crossAxisAlignment:
                    //                           CrossAxisAlignment.center,
                    //                       mainAxisAlignment:
                    //                           MainAxisAlignment.spaceBetween,
                    //                       children: [
                    //                         /*item.selected == true
                    //                       ? Icon(
                    //                     Icons.check,
                    //                     color: AppTheme.colorPrimary,
                    //                     size: Constant.ICON_SIZE_M,
                    //                   )
                    //                       : const Icon(
                    //                     Icons.check,
                    //                     color: Colors.white,
                    //                     size: Constant.ICON_SIZE_M,
                    //                   ),*/
                    //                         const SizedBox(
                    //                           width: Constant.SMALL_PADDING,
                    //                         ),
                    //                         Expanded(
                    //                           child: Row(
                    //                             mainAxisAlignment:
                    //                                 MainAxisAlignment
                    //                                     .spaceEvenly,
                    //                             crossAxisAlignment:
                    //                                 CrossAxisAlignment.center,
                    //                             children: [
                    //                               CustomText(
                    //                                 title:
                    //                                     "${item.productName!} ",
                    //                                 textAlign: TextAlign.start,
                    //                                 colors: item.selected !=
                    //                                             null &&
                    //                                         item.selected ==
                    //                                             true
                    //                                     ? AppTheme.colorPrimary
                    //                                     : AppTheme.title_dark,
                    //                                 fontSize:
                    //                                     AppTheme.small + 1,
                    //                                 fontWeight: FontWeight.w500,
                    //                               ),
                    //                               const SizedBox(
                    //                                 width:
                    //                                     Constant.MEDIUM_PADDING,
                    //                               ),
                    //                               CustomText(
                    //                                 title: item.productId!,
                    //                                 textAlign: TextAlign.start,
                    //                                 colors: item.selected !=
                    //                                             null &&
                    //                                         item.selected ==
                    //                                             true
                    //                                     ? AppTheme.colorPrimary
                    //                                     : AppTheme.title_dark,
                    //                                 fontSize:
                    //                                     AppTheme.small + 1,
                    //                                 fontWeight: FontWeight.w500,
                    //                               ),
                    //                               const SizedBox(
                    //                                 width:
                    //                                     Constant.MEDIUM_PADDING,
                    //                               ),
                    //                               CustomText(
                    //                                 title: item.productId!,
                    //                                 textAlign: TextAlign.start,
                    //                                 colors: item.selected !=
                    //                                             null &&
                    //                                         item.selected ==
                    //                                             true
                    //                                     ? AppTheme.colorPrimary
                    //                                     : AppTheme.title_dark,
                    //                                 fontSize:
                    //                                     AppTheme.small + 1,
                    //                                 fontWeight: FontWeight.w500,
                    //                               ),
                    //                               const SizedBox(
                    //                                 width:
                    //                                     Constant.MEDIUM_PADDING,
                    //                               ),
                    //                               CustomText(
                    //                                 title: item.productId!,
                    //                                 textAlign: TextAlign.start,
                    //                                 colors: item.selected !=
                    //                                             null &&
                    //                                         item.selected ==
                    //                                             true
                    //                                     ? AppTheme.colorPrimary
                    //                                     : AppTheme.title_dark,
                    //                                 fontSize:
                    //                                     AppTheme.small + 1,
                    //                                 fontWeight: FontWeight.w500,
                    //                               ),
                    //                               const SizedBox(
                    //                                 width:
                    //                                     Constant.MEDIUM_PADDING,
                    //                               ),
                    //                               CustomText(
                    //                                 title: item.productId!,
                    //                                 textAlign: TextAlign.start,
                    //                                 colors: item.selected !=
                    //                                             null &&
                    //                                         item.selected ==
                    //                                             true
                    //                                     ? AppTheme.colorPrimary
                    //                                     : AppTheme.title_dark,
                    //                                 fontSize:
                    //                                     AppTheme.small + 1,
                    //                                 fontWeight: FontWeight.w500,
                    //                               ),
                    //                             ],
                    //                           ),
                    //                         ),
                    //                         const SizedBox(
                    //                           height: Constant.SMALL_PADDING,
                    //                         ),
                    //                       ],
                    //                     ),
                    //                   ),
                    //                 ),
                    //                 index == (itemsLst.length - 1)
                    //                     ? Container()
                    //                     : Padding(
                    //                         padding: const EdgeInsets.symmetric(
                    //                             horizontal:
                    //                                 Constant.SCREEN_PADDING -
                    //                                     5),
                    //                         child: Divider(
                    //                           height: 5,
                    //                           color: AppTheme.dividerColor,
                    //                           thickness: 0.5,
                    //                         ),
                    //                       ),
                    //               ],
                    //             );
                    //           },
                    //         ),
                    //       ) :
                    Container(
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.SMALL_PADDING),
                      alignment: Alignment.center,
                      child: CustomText(
                        title: Strings.no_data_found,
                        textAlign: TextAlign.center,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w500,
                      ),
                    ),

                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        /* Expanded(
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
                        ),*/
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
    PopInventoryDetail? selectedItem;
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem = element;
      } else {
        selectedItem = element;
      }
    }
    if (selectedItem != null) {
      // widget.approveProgressAction.ticketAssignBtnAction(selectedItem: selectedItem);
    } else {
      // widget.approveProgressAction.ticketAssignBtnAction(selectedItem:selectedItem!);
    }
  }
}

abstract class ApproveProgressAction {
  void approveProgressBtnAction({PopInventoryDetail selectedItem});
}
