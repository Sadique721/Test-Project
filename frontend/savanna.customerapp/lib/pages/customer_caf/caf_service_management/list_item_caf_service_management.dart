import 'dart:developer';

import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_caf/caf_service_management/caf_service_managment_controller.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/pages/service_management/service_audit/cust_service_audit_status.dart';
import 'package:savbill/pages/service_management/service_managment_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class ItemListCafServiceManagement extends StatelessWidget {
  CustomerPlanServiceDetail item;
  CafServiceManagementController controller;
  int index;

  ItemListCafServiceManagement(
      {Key? key,
      required this.index,
      required this.item,
      required this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    final GlobalKey<TooltipState> tooltipkey = GlobalKey<TooltipState>();

    Color? statusColor, statusTextColor = AppTheme.colorLightBlack;
    if (item.custPlanStatus != null && item.custPlanStatus!.isNotEmpty) {
      if (item.custPlanStatus!.equalsIgnoreCase("Active")) {
        statusColor = AppTheme.statusClosedGreen;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.custPlanStatus!.equalsIgnoreCase("Rejected")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.custPlanStatus!.equalsIgnoreCase("Approve")) {
        statusColor = AppTheme.statusApprove;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.custPlanStatus!.equalsIgnoreCase("InActive")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.custPlanStatus!.equalsIgnoreCase("Pending")) {
        statusColor = AppTheme.statusPending;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.custPlanStatus!.equalsIgnoreCase("Hold")) {
        statusColor = AppTheme.colorDisableGray;
        statusTextColor = AppTheme.colorWhite;
      }
    } else {
      statusColor = AppTheme.statusClosedGreen;
    }

    if (!(item.nextStaff == null && item.nextTeamHierarchyMappingId == null) ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.terminate.toLowerCase()) ||
        controller.customerDetail!.status!
            .toLowerCase()
            .equalsIgnoreCase("newactivation") ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.stop.toLowerCase())) {
      controller.isServiceTermination = true;
    } else {
      controller.isServiceTermination = false;
    }

    if ((item.nextStaff != null && item.nextTeamHierarchyMappingId == null) ||
        (item.nextStaff == null && item.nextTeamHierarchyMappingId == null) ||
        item.custPlanStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.stop.toLowerCase()) ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.stop.toLowerCase()) ||
        (item.nextStaff != null && item.nextTeamHierarchyMappingId != null) ||
        controller.customerDetail!.status!
            .toLowerCase()
            .equalsIgnoreCase("newnctivation")) {
      controller.isPickService = true;
    } else {
      controller.isPickService = false;
    }

    if (item.nextStaff != controller.userDetail!.userId ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        controller.customerDetail!.status!
            .toLowerCase()
            .equalsIgnoreCase("customerDetail")) {
      controller.isApproveRejectService = true;
    } else {
      controller.isApproveRejectService = false;
    }

    if (item.nextStaff != controller.userDetail!.userId ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.stop.toLowerCase()) ||
        item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        controller.customerDetail!.status!
            .toLowerCase()
            .equalsIgnoreCase("newactivation")) {
      controller.isReassignService = true;
    } else {
      controller.isReassignService = false;
    }

    if (item.invoiceType != null) {
      if (item.invoiceType.toString().equalsIgnoreCase("Group") ||
          item.custPlanStatus!.toLowerCase().equalsIgnoreCase("stop")) {
        controller.isPauseStartService = true;
      } else {
        controller.isPauseStartService = false;
      }
    }

    if (item.custPlanStatus!.toLowerCase().equalsIgnoreCase("stop") ||
        item.custServMappingStatus!.toLowerCase().equalsIgnoreCase("stop")) {
      controller.isServiceAudit = true;
    } else {
      controller.isServiceAudit = false;
    }

    if (item.custServMappingStatus!
            .toLowerCase()
            .equalsIgnoreCase("terminate") ||
        item.custPlanStatus!.toLowerCase().equalsIgnoreCase("stop") ||
        item.custPlanStatus!.toLowerCase().equalsIgnoreCase("disable") ||
        item.custServMappingStatus!.toLowerCase().equalsIgnoreCase("stop")) {
      controller.isStopService = true;
    } else {
      controller.isStopService = false;
    }

    if (item.custPlanStatus!.toLowerCase().equalsIgnoreCase("stop") ||
        item.custServMappingStatus!.toLowerCase().equalsIgnoreCase("stop")) {
      controller.isWorkFlowStatusDetail = true;
    } else {
      controller.isWorkFlowStatusDetail = false;
    }
    if(item.nickname != null ){
      controller.nickNameController.text = item.nickname!;
    }else{
      controller.nickNameController.text = "";
    }

    // plan.custPlanStatus.toLowerCase() === 'stop' ||
    // plan.custServMappingStatus.toLowerCase() === 'stop'

    return Card(
      margin: EdgeInsets.symmetric(
        vertical: index == 0 ? 0 : Constant.MEDIUM_PADDING,
        horizontal: Constant.SMALL_PADDING,
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
                    title: item.service != null
                        ? item.service!.toString()
                        : item.service!,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small + 1,
                    fontWeight: FontWeight.w500),
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
                      color: statusColor,
                    ),
                    child: CustomText(
                        title: (item.custServMappingStatus != null &&
                                item.custServMappingStatus!.isNotEmpty)
                            ? item.custServMappingStatus
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
              Strings.serial_no,
              item.customerInventorySerialnumberDtos!.isNotEmpty
                  ? getSerialNumber(item)
                  : "N/A",
              Strings.invoice_type,
              item.invoiceType != null ? item.invoiceType.toString() : "-",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(
              Strings.current_plan,
              item.planName != null ? item.planName.toString() : "-",
              Strings.expiry_date,
              item.serviceEndDate != null
                  ? controller.dateFormat
                      .format(DateTime.parse(item.serviceEndDate.toString()))
                  : "-",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(
              Strings.serviceHoldDate,
              item.serviceHoldDate != null
                  ? controller.dateFormat
                      .format(DateTime.parse(item.serviceHoldDate.toString()))
                  : "-",
              Strings.serviceResumeDate,
              item.serviceResumeDate != null
                  ? controller.dateFormat
                      .format(DateTime.parse(item.serviceResumeDate.toString()))
                  : "-",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            detailItem(
              Strings.holdBy,
              item.serviceHoldBy != null ? item.serviceHoldBy.toString() : "-",
              Strings.resumeBy,
              item.serviceResumeBy != null
                  ? item.serviceResumeBy.toString()
                  : "-",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Expanded(
                  flex: 3,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.holdBy),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                        item.serviceHoldBy != null
                            ? item.serviceHoldBy.toString()
                            : "-",
                      ),
                    ],
                  ),
                ),
                Flexible(
                  flex: 1,
                  child: InkWell(
                    onTap: () {
                      controller.getServiceNickNameUpdate(
                          item.custPlanMapppingId, item.nickname.toString());
                    },
                    child: titleWidget("             "),
                  ),
                ),
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.nick_name),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),

                      CoustomTextField(
                          labelText: "Enter Nick Name",
                          hintColor: AppTheme.colorIconGrey,
                          // textEditingController: TextEditingController()
                          //   ..text = item.nickname.toString()
                          //   ..selection = TextSelection.collapsed(
                          //       offset: item.nickname.toString().length),
                          textEditingController: controller.nickNameController,
                          borderEnableColors: AppTheme.colorIconGrey,
                          borderFocusColors: AppTheme.colorIconGrey,
                          textColor: AppTheme.colorBlack,
                          keyboardType: TextInputType.text,
                          fontSize: AppTheme.small + 1,
                          textInputAction: TextInputAction.done,
                          fontWeight: FontWeight.w400,
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: Constant.MEDIUM_PADDING,
                              vertical: Constant.MEDIUM_PADDING),
                          borderCorner: Constant.BTN_ROUNDED_CORNER,
                          onTextValidator: (String? value) {
                            if (value!.isEmpty) {
                              return Strings.amount_required;
                            }
                            return null;
                          },
                          onChanged: (value) {
                            item.nickname = value;
                            controller.update();
                          },
                          readOnly: false),
                      // nickNameEditor(),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),

          ],
        ),
      ),
    );
  }

  openDiscountStatus(int? eventId) async {
    var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "CUSTOMER_SERVICE_TERMINATION"
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  openServiceAuditStatus(int? serviceId) async {
    var result = await Get.to(CustomerAuditDetail(), arguments: {
      Constant.SERVICE_ID: serviceId,
      Constant.CUSTOMER_DETAIL: controller.customerDetail,
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
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
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 3,
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
          flex: 2,
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

  nickNameEditor() {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(7.0),
        color: AppTheme.colorWhite,
      ),
      child: TextFormField(
        controller: TextEditingController()
          ..text = item.nickname.toString()
          ..selection =
              TextSelection.collapsed(offset: item.nickname.toString().length),
        maxLines: 1,
        maxLength: 250,
        style: const TextStyle(fontSize: AppTheme.medium),
        decoration: InputDecoration(
          hintText: Strings.remarks,
          alignLabelWithHint: true,
          contentPadding:
              const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING - 5),
          focusColor: Colors.transparent,
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
              width: 1.0,
            ),
          ),
          border: OutlineInputBorder(
              borderRadius:
                  BorderRadius.circular(Constant.TEXT_FIELD_CONTENT_PADDING)),
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
        onChanged: (value) {
          // controller.nickNameController.text = value.toString();
          // controller.update();
        },
      ),
    );
  }

  String? getSerialNumber(CustomerPlanServiceDetail item) {
    return item.customerInventorySerialnumberDtos!
        .firstWhere(
          (element) => element.primary == true,
        )
        .serialNumber;
  }
}
