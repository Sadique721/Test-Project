import 'package:savbill/pages/inventory/assigned_inventories/request_inventory_controller.dart';
import 'package:savbill/pages/inventory/module/response/inventory_request_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class MyInventoryRequestItem extends StatelessWidget {
  InventroyRequestDataList item;
  RequestInventoryController requestInventoryController;


  MyInventoryRequestItem({
    Key? key,
    required this.item,
    required this.requestInventoryController,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "", assignedDate = "", outwardsNo = "";
    Color? statusColor,inventoryRequestStatus;

    bool? isDelete = false;

    if (item.requestInventoryName != null &&
        item.requestInventoryName!.isNotEmpty) {
      pName = item.requestInventoryName!;
    }
    // if (item.assignedDateTime != null && item.assignedDateTime!.isNotEmpty) {
    //   DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
    //       .parse(item.assignedDateTime!);
    //   assignedDate =
    //       DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
    //           .format(date);
    // }


    if(item.status!.equalsIgnoreCase(Strings.pending)){
      statusColor = AppTheme.colorGrey;
    }else if(item.status!.equalsIgnoreCase(Strings.rejected)){
      statusColor = AppTheme.statusReject;
    }else if(item.status!.equalsIgnoreCase(Strings.approve)){
      statusColor = AppTheme.statusApprove;
    }
    
    
    if(item.inventoryRequestStatus!.equalsIgnoreCase("In-Progress")){
      inventoryRequestStatus = AppTheme.statusPending;
    }else if(item.inventoryRequestStatus!.equalsIgnoreCase("Waiting for Approval")){
      inventoryRequestStatus = AppTheme.colorGrey;
    }else if(item.inventoryRequestStatus!.equalsIgnoreCase("Complted")){
      inventoryRequestStatus = AppTheme.statusApprove;
    }else if(item.inventoryRequestStatus!.equalsIgnoreCase("Rejected")){
      inventoryRequestStatus = AppTheme.statusReject;
    }else if(item.inventoryRequestStatus!.equalsIgnoreCase("Partially Completed")){
      inventoryRequestStatus = AppTheme.colorBlueRView;
    }


    if(item.status!.equalsIgnoreCase(Strings.approve)){
      isDelete = true;
    }else{
      isDelete = false;
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
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex:3,
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      InputTitleRequire(
                        title: "${Strings.requester_id} : ",
                        require: false,
                        colorValue: AppTheme.statusUnAssignGray,
                      ),
                      CustomText(
                          title: pName,
                          colors: AppTheme.custChangeStatusDark,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          maxLines: 2,
                          fontWeight: FontWeight.w500),
                    ],
                  ),
                ),
                Expanded(
                  flex:1,
                  child: Align(
                    alignment: Alignment.centerRight,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal:
                            Constant.SMALL_PADDING,
                            vertical: Constant
                                .VERY_SMALL_PADDING),
                        decoration: BoxDecoration(
                          borderRadius:
                          BorderRadius.circular(Constant.LARGE_PADDING),
                          color: statusColor,),
                        child: CustomText(
                            title: item.status,
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.center,
                            fontSize: AppTheme.small,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    ),
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
            child: Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.request_to),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(item.requestToName != null ? item.requestToName!.toString() : "-"),
                    ],
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget("${Strings.inventory_request}\n${Strings.status}"),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal:
                            Constant.SMALL_PADDING,
                            vertical: Constant
                                .VERY_SMALL_PADDING),
                        decoration: BoxDecoration(
                          borderRadius:
                          BorderRadius.circular(Constant.LARGE_PADDING),
                          color: inventoryRequestStatus,),
                        child: CustomText(
                            title: item.inventoryRequestStatus,
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.reason,
              item.reason != null ? item.reason!.toString() : "-",
              "", "",),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          InkWell(
            onTap: () {
             isDelete == false ? showDialog(
                context: context,
                builder: (BuildContext context) {
                  return AlertDialogHelper(
                      title: Strings.delete_confirmation,
                      message: Strings.msg_delete,
                      positiveBtnText: Strings.ok,
                      negativeBtnText: Strings.cancel,
                      positiveBtnClick: () {
                        requestInventoryController
                            .deleteRequestInventroyItem(item.id);
                        Get.back();
                      },
                      negativeBtnClick: () {
                        Get.back();
                      });
                },
              ) :{};
            },
            child: Container(
              alignment: Alignment.centerRight,
              padding: const EdgeInsets.only(right: 10, bottom: 5),
              child: Material(
                elevation: 1.5,
                color: isDelete == false ? AppTheme.custPaymentLinkDark : AppTheme.colorDisableGray,
                shape: RoundedRectangleBorder(
                    borderRadius:
                        BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                child: Container(
                  height: Constant.BTN_HEIGHT_M - 10,
                  width: Constant.BTN_HEIGHT_M - 10,
                  alignment: Alignment.center,
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
                  child: SvgPicture.asset(
                    deleteSvg,
                    height: Constant.ICON_SIZE,
                    width: Constant.ICON_SIZE,
                    color: AppTheme.colorWhite,
                    fit: BoxFit.fill,
                  ),
                ),
              ),
            ),
          )
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
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
}
