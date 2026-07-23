import 'dart:developer';

import 'package:savbill/pages/customer_change_status/approve_reject_change_status_dialog.dart';
import 'package:savbill/pages/customer_change_status/change_status_list_controller.dart';
import 'package:savbill/pages/customer_change_status/request/cust_terminate_approve_reject_req.dart';
import 'package:savbill/pages/customer_change_status/response/customer_change_status_res.dart';
import 'package:savbill/pages/shift_location/customer_shift_locaiton_work_flow.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_countdown_timer/countdown_timer_controller.dart';
import 'package:flutter_countdown_timer/current_remaining_time.dart';
import 'package:flutter_countdown_timer/flutter_countdown_timer.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class ChangeStatusItemView extends StatelessWidget
    implements ApproveRejectChangetStatusBtnAction {
  CountdownTimerController? timeController;
  ChangeStatusDetail item;
  ChangeStatusListController controller;
  int index;

  ChangeStatusItemView(
      {Key? key,
      required this.index,
      required this.item,
      required this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color? statusColor, statusTextColor = AppTheme.colorLightBlack;
    if (item.status != null && item.status!.isNotEmpty) {
      if (item.status!.equalsIgnoreCase("Active")) {
        statusColor = AppTheme.statusClosedGreen;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.status!.equalsIgnoreCase("Rejected")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.status!.equalsIgnoreCase("Approve")) {
        statusColor = AppTheme.statusApprove;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.status!.equalsIgnoreCase("InActive")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.status!.equalsIgnoreCase("Pending")) {
        statusColor = AppTheme.statusPending;
        statusTextColor = AppTheme.colorWhite;
      }
    } else {
      statusColor = AppTheme.statusClosedGreen;
    }

    int? endTime;
    String followUpTimeFormated = "";
    if (item.status!.equalsIgnoreCase("pending")) {
      if (controller.customerDetail!.nextfollowupdate != null &&
          controller.customerDetail!.nextfollowupdate!.isNotEmpty) {
        followUpTimeFormated =
        "${controller.customerDetail!.nextfollowupdate} ${controller.customerDetail!.nextfollowuptime}";
        DateTime followTime = DateTime.parse(followUpTimeFormated);
        endTime = followTime
            .toLocal()
            .millisecondsSinceEpoch;
        timeController =
            CountdownTimerController(endTime: endTime, onEnd: onEnd);
      }
    }


    return Card(
      margin: EdgeInsets.symmetric(
        vertical: index == 0 ? 0 : Constant.MEDIUM_PADDING,
        horizontal: Constant.SCREEN_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                CustomText(
                    title: item.custName != null
                        ? item.custName!.toString()
                        : "${item.firstName!} ${item.lastName!}",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small + 1,
                    fontWeight: FontWeight.w500),
                // CustomText(
                //     title: item.status != null ? item.status!.toString() : "",
                //     colors: AppTheme.colorPrimary,
                //     textAlign: TextAlign.start,
                //     fontSize: AppTheme.small + 1,
                //     fontWeight: FontWeight.w500),
                Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.VERY_SMALL_PADDING,
                      vertical: Constant.VERY_SMALL_PADDING),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SMALL_PADDING,
                        vertical: Constant.VERY_SMALL_PADDING),
                    decoration: BoxDecoration(
                      borderRadius:
                          BorderRadius.circular(Constant.LARGE_PADDING),
                      color: /*(item.approvalStatus != null &&
                              item.approvalStatus!.isNotEmpty &&
                              item.approvalStatus!
                                  .equalsIgnoreCase(Strings.active))
                          ? AppTheme.statusClosedGreen
                          : AppTheme.statusReject*/
                          statusColor,
                    ),
                    child: CustomText(
                        title: (item.status != null && item.status!.isNotEmpty)
                            ? item.status
                            : "",
                        colors: statusTextColor,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Divider(
              color: AppTheme.title_dark,
              height: 1,
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(
              Strings.current_status,
              item.currentStatus != null ? item.currentStatus.toString() : "-",
              Strings.new_status,
              item.activeStatus != null ? item.activeStatus.toString() : "-",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(
              Strings.req_raised_staff,
              item.currentStaff != null ? item.currentStaff.toString() : "-",
              Strings.parent_staff,
              item.parentStaff != null ? item.parentStaff.toString() : "-",
            ),

            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),

            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                CustomText(
                    title: Strings.remaining_time,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small + 1,
                    height: 1,
                    fontWeight: FontWeight.w500),
                const SizedBox(width: Constant.MEDIUM_PADDING),
                Expanded(
                    child: Align(
                      alignment: Alignment.topRight,
                      child: Padding(
                        padding: const EdgeInsets.only(
                            top: Constant.VERY_SMALL_PADDING,
                            bottom: Constant.VERY_SMALL_PADDING,
                            left: Constant.VERY_SMALL_PADDING,
                            right: Constant.VERY_SMALL_PADDING),
                        child: CountdownTimer(
                          controller: timeController,
                          endTime: endTime ?? 00,
                          textStyle: TextStyle(fontSize: AppTheme.small,fontWeight:FontWeight.normal,color: AppTheme.colorBlack,decoration: null,),
                          onEnd: onEnd,

                          widgetBuilder: (_, CurrentRemainingTime? time) {
                            if (time == null || time.isNullOrEmpty()) {
                              return valueWidget("00:00:00:00");
                            }
                            return valueWidget(
                                "${time.days ?? 00}:${time.hours ?? 00}:${time.min ?? 00}:${time.sec ?? 00}");
                          },
                        ), /*CustomText(
                        title: "00:00:00:00",
                        textAlign: TextAlign.center,
                        colors: AppTheme.title_dark,
                        fontSize: AppTheme.extraLarge,
                        fontWeight: FontWeight.w700,
                      ),*/
                      ),
                    ))
              ],
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                item.status!.equalsIgnoreCase("Approved") ||
                        item.status!.equalsIgnoreCase("Rejected") ||
                        (item.currentStaff != null &&
                            item.currentStaff != "") ||
                        item.currentStatus!.equalsIgnoreCase("Terminate")
                    ? buttonView(pickTicketSvg, AppTheme.colorLightGrey,
                        AppTheme.colorWhite, null)
                    : buttonView(pickTicketSvg, AppTheme.colorAccent,
                        AppTheme.colorWhite, () {
                        controller.pickModelOpen(item.customerID);
                      }),
                controller.assignStaffByName == item.currentStaff
                    ? Row(mainAxisAlignment: MainAxisAlignment.end, children: [
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        item.status!.equalsIgnoreCase("Approved") ||
                                item.status!.equalsIgnoreCase("Rejected") ||
                                item.currentStaff == null ||
                                item.currentStaff == "" ||
                                item.currentStatus!
                                    .equalsIgnoreCase("Terminate")
                            ? buttonView(checkSvg, AppTheme.colorLightGrey,
                                AppTheme.colorWhite, () {})
                            : buttonView(checkSvg, AppTheme.colorPrimary,
                                AppTheme.colorWhite, () {
                                addRemarkInvoiceDialog(context, Strings.approve,
                                    controller, item.id);
                              }),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        item.status!.equalsIgnoreCase(Strings.approved) ||
                                item.status!
                                    .equalsIgnoreCase(Strings.rejected) ||
                                (item.currentStaff == null ||
                                    item.currentStaff == "") ||
                                item.currentStatus!
                                    .equalsIgnoreCase("Terminate")
                            ? buttonView(cancelSvg, AppTheme.colorLightGrey,
                                AppTheme.colorWhite, () {})
                            : buttonView(cancelSvg, AppTheme.colorPrimary,
                                AppTheme.colorWhite, () {
                                addRemarkInvoiceDialog(context, Strings.reject,
                                    controller, item.id);
                              }),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        buttonView(assignInventorySvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, () {
                          openTerminationWorkFlow(controller.customerId);
                        }),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        item.currentStatus!.equalsIgnoreCase("Terminate") || item.status!.equalsIgnoreCase("Rejected")
                            ? buttonView(assignSvg, AppTheme.colorLightGrey,
                                AppTheme.colorWhite, null)
                            : buttonView(assignSvg, AppTheme.colorPrimary,
                                AppTheme.colorWhite, () {
                                controller.reassignWorkflowGetStaff(
                                    controller.customerId, "TERMINATION");
                              })
                      ])
                    : const SizedBox.shrink(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 5,
          width: Constant.BTN_HEIGHT_M - 5,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
          ),
        ),
      ),
    );
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  openTerminationWorkFlow(int? eventId) async {
    var result =
        await Get.to(const CustomerShiftLocationWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "TERMINATION"
      // Constant.
    });
    if (result != null && result == true) {
      // controll.getNewAddressShiftLocation();
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  addRemarkInvoiceDialog(BuildContext context, String? pageName,
      ChangeStatusListController? controller, int? terminateProductId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ApproveRejectChangeStatusDialog(
            pageName: pageName,
            controller: controller,
            approveRejectChangeStatusBtnAction: this,
            customerTerminateApproveReq: CustomerTerminateApproveRejectReq(),
            terminateProductId: terminateProductId,
          );
        });
  }

  @override
  void approveRejectChangeStatusDetails(
      {String? identifier,
      TextEditingController? remarkController,
      CustomerTerminateApproveRejectReq? approveCustomerAddressReq,
      BuildContext? context}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      controller.getApproveCustomerChangeStatusApproveReject(Strings.approved,
          remarkController!.text, approveCustomerAddressReq!, context!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      controller.getApproveCustomerChangeStatusApproveReject(Strings.rejected,
          remarkController!.text, approveCustomerAddressReq, context!);
    }
  }


  void onEnd() {
    if (timeController != null) {
      timeController!.disposeTimer();
    }
  }
}
