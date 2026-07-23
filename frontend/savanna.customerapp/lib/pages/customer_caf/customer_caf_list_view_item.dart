import 'dart:developer';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/customer_caf_detail/customer_caf_detail.dart';
import 'package:savbill/pages/customer_caf/customer_caf_list_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_countdown_timer/countdown_timer_controller.dart';
import 'package:flutter_countdown_timer/current_remaining_time.dart';
import 'package:flutter_countdown_timer/flutter_countdown_timer.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CustomerCafListViewItem extends StatelessWidget {
  CountdownTimerController? timeController;
  List backgroundColorArr = [
    AppTheme.colorGreenRoundView,
    AppTheme.colorRedRoundView,
    AppTheme.colorBlueRoundView,
    AppTheme.colorYellowRoundView
  ];
  List textColorArr = [
    AppTheme.colorGreenRView,
    AppTheme.colorRedRView,
    AppTheme.colorBlueRView,
    AppTheme.colorYellowRView
  ];
  CustomerDetail item;
  int index;
  String? custType;

  final Function()? onTapEdit;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapPick;
  final Function()? onTapCloseCaf;
  final Function()? onTapDocumentUpload;
  final Function()? onTapNearByDevice;

  // final Function()? onTapSendPaymentLink;
  final Function()? onTapAssignInventory;
  final Function()? onTapReActivate;
  final Function()? onTapCustomerInvoicePayment;
  final Function()? onTapNotes;
  CustomerCafListController? controller;

  CustomerCafListViewItem({
    Key? key,
    required this.index,
    this.custType,
    required this.item,
    this.onTapEdit,
    this.onTapApprove,
    this.onTapReject,
    this.onTapPick,
    this.onTapCloseCaf,
    this.onTapDocumentUpload,
    this.onTapNearByDevice,
    // this.onTapSendPaymentLink,
    this.onTapAssignInventory,
    this.onTapReActivate,
    this.onTapCustomerInvoicePayment,
    this.onTapNotes,
    this.controller,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color? connectionModeBgColor = AppTheme.custUploadFileLight;

    DateTime? newYearsDate;
    DateTime? currentDate;

    if (item.status != null && item.status!.equalsIgnoreCase(Strings.active) ||
        item.status!.equalsIgnoreCase("NewActivation")) {
      connectionModeBgColor = AppTheme.onlineStatusBg;
    } else if (item.status != null &&
        item.status!.equalsIgnoreCase("ActivationPending")) {
      connectionModeBgColor = AppTheme.colorBlueRView;
    } else if (item.status!.equalsIgnoreCase("Rejected")) {
      connectionModeBgColor = AppTheme.colorRed;
    }

    /// Close CAF

    bool isCloseCafBtn = false,
        isApprovedRejectedBtn = false,
        isPickBtn = false,
        isStatusRejectedDisableBtn = false,
        isReassignCAF = false,
        isEditBtn = false;
    if (!(item.currentStaff == controller!.userDetail!.userId) ||
        item.status!.equalsIgnoreCase("Suspend") ||
        item.status!.equalsIgnoreCase("Rejected")) {
      isCloseCafBtn = true;
    } else {
      isCloseCafBtn = false;
    }

    /// Approve and Reject Button Disable

    if ((item.currentStaff != controller!.userDetail!.userId &&
            (!controller!.userDetail!.fullName!
                .equalsIgnoreCase("admin admin"))) ||
        item.status!.equalsIgnoreCase("Suspend") ||
        item.status!.equalsIgnoreCase("Rejected") ||
        item.status!.equalsIgnoreCase("Active") ||
        item.status!.equalsIgnoreCase("ActivationPending")) {
      isApprovedRejectedBtn = true;
    } else {
      isApprovedRejectedBtn = false;
    }

    /// Pick Button Disable

    if ((item.currentStaff != null &&
            item.status!.equalsIgnoreCase("NewActivation")) ||
        item.status!.equalsIgnoreCase("Rejected") ||
        item.status!.equalsIgnoreCase("Active") ||
        item.status!.equalsIgnoreCase("ActivationPending")) {
      isPickBtn = true;
    } else {
      isPickBtn = false;
    }

    /// Status -> Rejected then disable button
    if (item.status!.equalsIgnoreCase("Rejected")) {
      isStatusRejectedDisableBtn = true;
    } else {
      isStatusRejectedDisableBtn = false;
    }

    /// Reassign CAF

    if (!(item.currentStaff == controller!.userDetail!.userId ||
            (!controller!.userDetail!.fullName!
                .equalsIgnoreCase("admin admin"))) ||
        item.status!.equalsIgnoreCase("Suspend") ||
        item.status!.equalsIgnoreCase("Rejected") ||
        item.status!.equalsIgnoreCase("Active")) {
      isReassignCAF = true;
    } else {
      isReassignCAF = false;
    }

    if (item.currentStaff != controller!.userDetail!.userId &&
            (!controller!.userDetail!.fullName!
                .equalsIgnoreCase("admin admin")) ||
        item.status!.equalsIgnoreCase("Suspend") ||
        item.status!.equalsIgnoreCase("Rejected") ||
        item.status!.equalsIgnoreCase("Active")) {
      isEditBtn = true;
    } else {
      isEditBtn = false;
    }

    int? endTime;
    String followUpTimeFormated = "";
    if (!item.status!.equalsIgnoreCase("Active")) {
      if ((item.nextfollowupdate != null) && (item.nextfollowuptime != null)) {
        followUpTimeFormated =
            "${item.nextfollowupdate} ${item.nextfollowuptime}";
        DateTime followTime = DateTime.parse(followUpTimeFormated);
        endTime = followTime.toLocal().millisecondsSinceEpoch;
        timeController =
            CountdownTimerController(endTime: endTime, onEnd: onEnd);
      } else {
        timeController = CountdownTimerController(endTime: 0, onEnd: onEnd);
      }
    }


    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    flex: 1,
                    child: Row(
                      children: [
                        CircleAvatar(
                          backgroundColor: backgroundColorArr[
                              index % backgroundColorArr.length],
                          radius: 18,
                          child: Text(
                            (item.name?.isNotEmpty ?? false) ? item.name![0].toUpperCase() : item.username![0].toUpperCase(),
                            style: TextStyle(
                                color:
                                    textColorArr[index % textColorArr.length],
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.bold),
                          ),
                        ),
                        const SizedBox(width: Constant.SMALL_PADDING),
                        Expanded(
                            child: InkWell(
                          onTap: () {
                            openCustomerDetailScreen(
                                controller!.customerList![index].id
                                // customerCafListController.customerList![index].networkDetails!.serviceareaid
                                );
                          },
                          child: CustomText(
                              title: item.name ?? "",
                              colors: textColorArr[index % textColorArr.length],
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium + 1,
                              maxLines: 2,
                              height: 1,
                              fontWeight: FontWeight.w500),
                        )),
                      ],
                    ),
                  ),
                  statusBgView(
                    status: item.status ?? "",
                    bgColor: connectionModeBgColor,
                    textColor: (item.status != null &&
                                item.status!
                                    .equalsIgnoreCase("NewActivation") ||
                            item.status!.equalsIgnoreCase("Active") ||
                            item.status!.equalsIgnoreCase("Rejected"))
                        ? AppTheme.colorWhite
                        : AppTheme.title_dark,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.normal,
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            cardDataRow(Strings.username, item.username ?? "-"),
            line(),
            cardDataRow(Strings.service_area, item.serviceArea ?? "-"),
            line(),
            cardDataRow(Strings.mobile_number, item.mobile ?? "-"),
            line(),
            cardDataRow(Strings.account_number, item.acctno ?? "-"),
            line(),
            // cardDataRow(Strings.remaining_time, item.remainTime ?? "00:00:00:00"),

            Padding(
              padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
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
                        textStyle: TextStyle(
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.normal,
                          color: AppTheme.colorBlack,
                          decoration: null,
                        ),
                        onEnd: onEnd,
                        widgetBuilder: (_, CurrentRemainingTime? time) {
                          if (time == null || time.isNullOrEmpty()) {
                            return valueWidget("00:00:00:00");
                          }
                          return valueWidget(
                            "${time.days ?? 00}:${time.hours ?? 00}:${time.min ?? 00}:${time.sec ?? 00}",
                          );
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
            ),
            line(),
            cardDataRow(Strings.isp_name, item.mvnoName ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),line(),
            cardDataRow(Strings.current_assignee_name, item.currentAssigneeName ?? ""),
            const SizedBox(
              height: Constant.VERY_SMALL_PADDING,
            ),line(),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
                mainAxisAlignment: MainAxisAlignment.end,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  PermissionService().hasAclPermission([
                            custType!.equalsIgnoreCase('Prepaid')
                                ? AclPreCustConstants.ADD_NOTES_PRE_CUST_CAF
                                : AclPostCustConstants.POST_CUST_CHARGE_CREATE
                          ]) ==
                          true
                      ? buttonView(openTicketSVG, AppTheme.custEditLight,
                          AppTheme.custEditDark, onTapNotes!)
                      : SizedBox.shrink(),
                 const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                  PermissionService().hasAclPermission([
                            custType!.equalsIgnoreCase('Prepaid')
                                ? AclPreCustConstants.EDIT_PRE_CUST_CAF_LIST
                                : AclPostCustConstants.EDIT_POST_CUST_CAF
                          ]) ==
                          true
                      ? isEditBtn == false
                          ? buttonView(editSvg, AppTheme.custEditLight,
                              AppTheme.custEditDark, onTapEdit!)
                          : buttonView(editSvg, AppTheme.colorGrayTxtBg,
                              AppTheme.colorWhite, null)
                      : SizedBox.shrink(),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),


                  isApprovedRejectedBtn == false
                      ? buttonView(checkSvg, AppTheme.custPaymentLinkLight,
                          AppTheme.custPaymentLinkDark, onTapApprove)
                      : buttonView(checkSvg, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),
                  isApprovedRejectedBtn == false
                      ? buttonView(
                          rejectRemoveSvg,
                          AppTheme.custUploadFileLight,
                          AppTheme.custUploadFileDark,
                          onTapReject)
                      : buttonView(rejectRemoveSvg, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),
                  isPickBtn == false
                      ? buttonView(
                          pickTicketSvg,
                          AppTheme.custChangeStatusLight,
                          AppTheme.custChangeStatusDark,
                          onTapPick)
                      : buttonView(pickTicketSvg, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),

                  PermissionService().hasAclPermission([
                    custType!.equalsIgnoreCase('Prepaid')
                        ? AclPreCustConstants.CLOSE_PRE_CUST_CAF_LIST
                        : AclPostCustConstants.CLOSE_POST_CUST_CAF
                  ]) == true ?
                  isCloseCafBtn == false
                      ? buttonView(closeCafSVG, AppTheme.custChangeStatusLight,
                          AppTheme.custChangeStatusDark, onTapCloseCaf!)
                      : buttonView(closeCafSVG, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null) : SizedBox.shrink(),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),

                  PermissionService().hasAclPermission([
                    custType!.equalsIgnoreCase('Prepaid')
                        ? AclPreCustConstants.UPLOAD_PRE_CUST_CAF_LIST
                        : AclPostCustConstants.UPLOAD_DOCUMENTS_POST_CUST_CAF
                  ]) == true ?
                  isStatusRejectedDisableBtn == false
                      ? buttonView(
                          documentUploadSvg,
                          AppTheme.custUploadFileLight,
                          AppTheme.custUploadFileDark,
                          onTapDocumentUpload!)
                      : buttonView(documentUploadSvg, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null) : SizedBox.shrink(),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),
                ]),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
                mainAxisAlignment: MainAxisAlignment.end,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),
                  PermissionService().hasAclPermission([
                    custType!.equalsIgnoreCase('Prepaid')
                        ? AclPreCustConstants.PRE_CUST_CAF_NEAR_BY_DEVICE
                        : AclPostCustConstants.POST_CUST_CAF_NEARBY_DEVICE
                  ]) == true ?
                  isStatusRejectedDisableBtn == false
                      ? buttonView(nearByNewDevice, AppTheme.custEditLight,
                          AppTheme.custEditDark, onTapNearByDevice!)
                      : buttonView(nearByNewDevice, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null) : SizedBox.shrink(),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),

                  // PermissionService().hasAclPermission([
                  //   custType!.equalsIgnoreCase('Prepaid')
                  //       ? AclPreCustConstants.PAYMENT_LINK_PRE_CUST_CAF
                  //       : AclPostCustConstants.PAYMENT_LINK_POST_CUST_CAF
                  // ]) == true ?
                  // isStatusRejectedDisableBtn == false
                  //     ? buttonView(rupeeSvg, AppTheme.custPaymentLinkLight,
                  //         AppTheme.custPaymentLinkDark, onTapSendPaymentLink!)
                  //     : buttonView(rupeeSvg, AppTheme.colorGrayTxtBg,
                  //         AppTheme.colorWhite, null): SizedBox.shrink(),
                  // const SizedBox(
                  //   width: Constant.MEDIUM_PADDING,
                  // ),

                  PermissionService().hasAclPermission([
                    custType!.equalsIgnoreCase('Prepaid')
                        ? AclPreCustConstants.REASSIGN_PRE_CUST_CAF
                        : AclPostCustConstants.POST_CUST_CHARGE_CREATE
                  ]) == true ?
                  isReassignCAF == false
                      ? buttonView(
                          assignSvg,
                          AppTheme.custAssignInventoryLight,
                          AppTheme.custAssignInventoryDark,
                          onTapAssignInventory!)
                      : buttonView(assignSvg, AppTheme.colorGrayTxtBg,
                          AppTheme.colorWhite, null) : SizedBox.shrink(),

                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),
                  PermissionService().hasAclPermission([
                    custType!.equalsIgnoreCase('Prepaid')
                        ? AclPreCustConstants.PRE_CUST_CAF_INVOICE_PAYMENT
                        : AclPostCustConstants.POST_CUST_CHARGE_CREATE
                  ]) == true ?
                  buttonView(ticketPromiseToPaySvg, AppTheme.custDeleteLight,
                      AppTheme.custDeleteDark, onTapCustomerInvoicePayment!) : SizedBox.shrink(),
                  const SizedBox(
                    width: Constant.MEDIUM_PADDING,
                  ),

                  /// Re activate
                  item.status!.equalsIgnoreCase("ActivationPending")
                      ? buttonView(
                          reactivateSvg,
                          AppTheme.custChangeStatusLight,
                          AppTheme.custChangeStatusDark,
                          onTapReActivate!)
                      : const SizedBox.shrink(),
                  item.status!.equalsIgnoreCase("ActivationPending")
                      ? const SizedBox(
                          width: Constant.MEDIUM_PADDING,
                        )
                      : const SizedBox.shrink(),
                ]),
            const SizedBox(
              height: Constant.SMALL_PADDING,
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
        elevation: 1.5,
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
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }

  cardDataRow(String? label, String? value) {
    return Padding(
      padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(width: Constant.MEDIUM_PADDING),
          Expanded(
              child: Align(
            alignment: Alignment.topRight,
            child: CustomText(
                title: value!.isNotEmpty ? value : "-",
                colors: AppTheme.lable_noramal,
                textAlign: TextAlign.end,
                fontSize: AppTheme.small,
                height: 1,
                fontWeight: FontWeight.w400),
          ))
        ],
      ),
    );
  }

  line() {
    return SizedBox(
      width: double.infinity,
      child: Divider(
        color: Colors.grey[300],
        height: 0.5,
      ),
    );
  }

  openCustomerDetailScreen(int? customerId) async {
    Get.to(CustomerCafDetailScreen(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_TYPE: controller!.type
    });
  }

  void onEnd() {
    if (timeController != null) {
      timeController!.disposeTimer();
    }
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.medium,
      fontWeight: FontWeight.w500,
      maxLines: 1,
    );
  }
}
