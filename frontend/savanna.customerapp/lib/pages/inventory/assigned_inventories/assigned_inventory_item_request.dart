import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventory_approve_reject_dialog.dart';
import 'package:savbill/pages/inventory/assigned_inventories/forward_inventory_request.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_request_assigned.dart';
import 'package:savbill/pages/inventory/assigned_inventories/request_inventory_controller.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class MyAssignedInventoryRequestItem extends StatelessWidget  implements AssignedInventoryApproveRejectBtnAction{
  AssignedInventoryDataList item;
  RequestInventoryController requestInventoryController;

  MyAssignedInventoryRequestItem(
      {Key? key, required this.item, required this.requestInventoryController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "",
        assignedDate = "",
        outwardsNo = "";
    Color approvalStatus = AppTheme.colorGrey;

    if (item.requestInventoryName != null &&
        item.requestInventoryName!.isNotEmpty) {
      pName = item.requestInventoryName!;
    }

    if (item.status != null && item.status!.isNotEmpty) {
      if (item.status!.equalsIgnoreCase(Strings.pending)) {
        approvalStatus = AppTheme.colorGrey;
      } else if (item.status!.equalsIgnoreCase(Strings.approve)) {
        approvalStatus = AppTheme.statusClosedGreen;
      } else if (item.status!.equalsIgnoreCase(Strings.rejected)) {
        approvalStatus = AppTheme.statusReject;
      }
    }




    // if (item.assignedDateTime != null && item.assignedDateTime!.isNotEmpty) {
    //   DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
    //       .parse(item.assignedDateTime!);
    //   assignedDate =
    //       DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
    //           .format(date);
    // }

    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                InputTitleRequire(
                  title: "${Strings.requester_id} : $pName",
                  require: false,
                  colorValue: AppTheme.statusUnAssignGray,
                ),

                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal:
                        Constant.SMALL_PADDING,
                        vertical: Constant
                            .VERY_SMALL_PADDING),
                    decoration: BoxDecoration(
                      borderRadius:
                      BorderRadius.circular(
                          Constant.LARGE_PADDING),color: approvalStatus),
                    child: CustomText(
                        title:  (item.status != null && item.status!.isNotEmpty)
                            ? item.status!.toString()
                            : "-",
                        colors: AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),
              ],
            ),
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.on_behalf_of,
                item.onBehalfOf != null && item.onBehalfOf!.isNotEmpty
                    ? item.onBehalfOf!
                    : "-",
                Strings.requester,
                item.requesterName != null && item.requesterName!.isNotEmpty
                    ? item.requesterName!
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.request_to,
                item.requestToName != null
                    ? item.requestToName!.toString()
                    : "-",
                "",
                ""),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [

             !item.status!.equalsIgnoreCase(Strings.pending) ?
             buttonView(checkSvg, AppTheme.colorLightGrey, AppTheme.colorDisableGray, () => null) :
             buttonView(checkSvg, AppTheme.colorPrimary, AppTheme.colorWhite, () {
               addRemarkApproveRejectDialog(context,Strings.approve,item);
             }),

              const SizedBox(width: Constant.VERY_SMALL_PADDING,),

              !item.status!.equalsIgnoreCase(Strings.pending) ?
              buttonView(cancelSvg, AppTheme.colorLightGrey, AppTheme.colorDisableGray, () => null) :
              buttonView(cancelSvg, AppTheme.colorPrimary, AppTheme.colorWhite, () {
                addRemarkApproveRejectDialog(context,Strings.reject,item);
              }),

              const SizedBox(width: Constant.VERY_SMALL_PADDING,),

              item.status!.equalsIgnoreCase(Strings.rejected)||item.status!.equalsIgnoreCase(Strings.approve) ?
              buttonView(forwardWarehouseSvg, AppTheme.colorLightGrey, AppTheme.colorDisableGray, () => null) :
              buttonView(forwardWarehouseSvg, AppTheme.colorPrimary, AppTheme.colorWhite, () {
                Get.to(ForwardInventoryRequest(itemList: item,));
              }),

              const SizedBox(width: Constant.VERY_SMALL_PADDING,),

              item.status!.equalsIgnoreCase(Strings.pending) || item.status!.equalsIgnoreCase(Strings.rejected) ?
              buttonView(fulFillMentSvg, AppTheme.colorLightGrey, AppTheme.colorDisableGray, () => null) :
              buttonView(fulFillMentSvg, AppTheme.colorPrimary, AppTheme.colorWhite, (){
                // Get.to(InventoryRequestAssigned(itemList: item));
                openInventoryRequestAssignedScreen(item.id);
              }),

              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),

              /*GestureDetector(
                onTap: () {
                  if (item.status!.equalsIgnoreCase(Strings.pending)) {
                    requestInventoryController.assignedInvReqApproveStatus(
                        item.id!, "Pending");
                  } else {}
                },
                child: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 8, bottom: 5),
                  child: Material(
                    elevation: 1.5,
                    color: item.status!.equalsIgnoreCase(Strings.rejected)
                        ? item.status!.equalsIgnoreCase(Strings.pending)
                        ? AppTheme.custPaymentLinkDark
                        : AppTheme.custPaymentLinkLight
                        : AppTheme.custPaymentLinkLight,
                    shape: RoundedRectangleBorder(
                        borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                    child: Container(
                      height: Constant.BTN_HEIGHT_M - 10,
                      width: Constant.BTN_HEIGHT_M - 10,
                      alignment: Alignment.center,
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                      child: SvgPicture.asset(
                        checkSvg,
                        height: Constant.ICON_SIZE,
                        width: Constant.ICON_SIZE,
                        color: AppTheme.colorWhite,
                        fit: BoxFit.fill,
                      ),
                    ),
                  ),
                ),
              ),
              GestureDetector(
                onTap: () {
                  if (item.status!.equalsIgnoreCase(Strings.pending)) {
                    requestInventoryController.assignedInvReqApproveStatus(
                        item.id!, "Rejected");
                  } else {}
                },
                child: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 8, bottom: 5),
                  child: Material(
                    elevation: 1.5,
                    color: item.status!.equalsIgnoreCase(Strings.rejected)
                        ? item.status!.equalsIgnoreCase(Strings.pending)
                        ? AppTheme.custPaymentLinkDark
                        : AppTheme.custPaymentLinkLight
                        : AppTheme.custPaymentLinkLight,
                    shape: RoundedRectangleBorder(
                        borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                    child: Container(
                      height: Constant.BTN_HEIGHT_M - 10,
                      width: Constant.BTN_HEIGHT_M - 10,
                      alignment: Alignment.center,
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                      child: SvgPicture.asset(
                        cancelSvg,
                        height: Constant.ICON_SIZE,
                        width: Constant.ICON_SIZE,
                        color: AppTheme.colorWhite,
                        fit: BoxFit.fill,
                      ),
                    ),
                  ),
                ),
              ),
              GestureDetector(
                onTap: () {
                  if (item.status!.equalsIgnoreCase(Strings.pending) || item.status!.equalsIgnoreCase(Strings.approve)) {
                    Get.to(ForwardInventoryRequest(itemList: item,));
                  }
                },
                child: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 8, bottom: 5),
                  child: Material(
                    elevation: 1.5,
                    color: item.status!.equalsIgnoreCase(Strings.rejected)
                        ? AppTheme.custPaymentLinkLight
                        : AppTheme.custPaymentLinkDark,
                    shape: RoundedRectangleBorder(
                        borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                    child: Container(
                      height: Constant.BTN_HEIGHT_M - 10,
                      width: Constant.BTN_HEIGHT_M - 10,
                      alignment: Alignment.center,
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                      child: SvgPicture.asset(
                        forwardWarehouseSvg,
                        height: Constant.BIG_ICON_SIZE,
                        width: Constant.BIG_ICON_SIZE,
                        color: AppTheme.colorWhite,
                        fit: BoxFit.fill,
                      ),
                    ),
                  ),
                ),
              ),
              GestureDetector(
                onTap: () {
                  if (item.status!.equalsIgnoreCase(Strings.pending) || item.status!.equalsIgnoreCase(Strings.approve)) {
                    Get.to(InventoryRequestAssigned(itemList: item));
                  }

                  // openInventoryRequestAssignedScreen(item.id);
                },
                child: Container(
                  alignment: Alignment.centerRight,
                  padding: const EdgeInsets.only(right: 10, bottom: 5),
                  child: Material(
                    elevation: 1.5,
                    color: item.status!.equalsIgnoreCase(Strings.rejected)
                        ? AppTheme.custPaymentLinkLight
                        : AppTheme.custPaymentLinkDark,
                    shape: RoundedRectangleBorder(
                        borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                    child: Container(
                      height: Constant.BTN_HEIGHT_M - 10,
                      width: Constant.BTN_HEIGHT_M - 10,
                      alignment: Alignment.center,
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                      child: SvgPicture.asset(
                        fulFillMentSvg,
                        height: Constant.ACTION_ICON_SIZE,
                        width: Constant.ACTION_ICON_SIZE,
                        color: AppTheme.colorWhite,
                        fit: BoxFit.fill,
                      ),
                    ),
                  ),
                ),
              )*/


            ],
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }




  openInventoryRequestAssignedScreen(int? assignedInventoryId) async {
    Get.to(InventoryRequestAssigned(), arguments: {
      Constant.ID: assignedInventoryId,
    });
  }



  basicDetailItem(String title1, String? value1, String title2,
      String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
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
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
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
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
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
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
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


  addRemarkApproveRejectDialog(BuildContext context, String? pageName,AssignedInventoryDataList item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return AssignedInventoryApproveRejectDialog(
              pageName: pageName,
            assignedInventoryApproveRejectBtnAction: this,
          );
        });
  }

  @override
  void assignedInventoryApproveRejectStatus({String? identifier, TextEditingController? remarkController}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      requestInventoryController.assignedInvReqApproveStatus(item.id!, Strings.approve,remarkController!.text);
    } else if (identifier != null && identifier.equalsIgnoreCase(Strings.reject)) {
      requestInventoryController.assignedInvReqApproveStatus(item.id!, Strings.rejected,remarkController!.text);
    }
  }

}
