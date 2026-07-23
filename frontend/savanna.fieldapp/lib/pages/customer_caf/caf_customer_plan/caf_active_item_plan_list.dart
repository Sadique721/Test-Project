import 'dart:core';
import 'dart:developer';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/caf_customer_plan/customer_caf_plan_controller.dart';
import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:moment_dart/moment_dart.dart';

class CafActivePlanItemView extends StatelessWidget {
  CustPlanDataList item;
  List<CustPlanDataList>? futurePlanList;
  List<CustPlanDataList>? activePlanList;
  CustomerCafPlanController? controller;

  int index;

  // String status;
  CustomerDetail? customerDetail;
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


  Color? statusColor;
  String? displayStatus;

  CafActivePlanItemView(
      {Key? key,
      required this.index,
      required this.item,
      required this.activePlanList,
      required this.customerDetail,
      required this.futurePlanList,
      required this.controller})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    checkStatus(item.custPlanStatus, item.custServMappingStatus);

    log("custTypeStatus==>${controller!.customerDetail!.status!}");

    return Stack(
      children: [
        Card(
          margin: const EdgeInsets.only(
              top: Constant.MEDIUM_PADDING,
              left: 0,
              right: 0,
              bottom: Constant.MEDIUM_PADDING),
          elevation: 2,
          color: AppTheme.colorWhite,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // nameRow(),
              // cardDataRow(Strings.service_name, item.service!.toString()),
              // line(),
              // cardDataRow(
              //   Strings.serial_no,
              //   item.customerInventorySerialnumberDtos!.isNotEmpty
              //       ? getSerialNumber(item)
              //       : "N/A",
              // ),
              // line(),
              // cardDataRow(Strings.nick_name, item.nickname ?? ""),
              // line(),
              cardDataRow(Strings.plan_name, item.planName ?? ""),
              line(),

              cardDataRow(
                  Strings.validity,
                  controller!.customerDetail!.custtype!
                          .equalsIgnoreCase("Postpaid")
                      ? "N/A"
                      : item.validity!.toInt().toString()),
              line(),
              Padding(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomText(
                        title: Strings.plan_status,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small + 1,
                        height: 1,
                        fontWeight: FontWeight.w500),
                    const SizedBox(width: Constant.MEDIUM_PADDING),
                    Expanded(
                        child: Align(
                      alignment: Alignment.topRight,
                      child: Container(
                        padding: const EdgeInsets.only(
                            left: Constant.MEDIUM_PADDING,
                            right: Constant.MEDIUM_PADDING,
                            top: Constant.VERY_SMALL_PADDING,
                            bottom: Constant.VERY_SMALL_PADDING),
                        decoration: BoxDecoration(
                            borderRadius: BorderRadius.circular(10),
                            color: statusColor ?? AppTheme.lable_noramal),
                        child: CustomText(
                            title: displayStatus!.toUpperCase() ?? "-",
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.end,
                            fontSize: AppTheme.small,
                            height: 1,
                            fontWeight: FontWeight.w400),
                      ),
                    ))
                  ],
                ),
              ),
              line(),
              cardDataRow(Strings.start_date,
                  getDateTimeFormat(item.dbStartDate.toString())),
              line(),
              cardDataRow(
                  Strings.expiry_date,
                  controller!.customerDetail!.custtype!
                          .equalsIgnoreCase("Postpaid")
                      ? "N/A"
                      : getDateTimeFormat(item.dbEndDate.toString())),
              line(),
              cardDataRow(Strings.group, item.plangroup ?? ""),
            ],
          ),
        ),
        Positioned(
          top: Constant.MEDIUM_PADDING,
          left: Constant.SMALL_PADDING,
          child: Container(
            width: 80,
            height: Constant.VERY_SMALL_PADDING,
            color: textColorArr[index % textColorArr.length],
          ),
        ),
      ],
    );
  }

  nameRow() {
    return Padding(
        padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor:
                  backgroundColorArr[index % backgroundColorArr.length],
              radius: 15,
              child: Text(
                !item.planName!.isNullOrEmpty()
                    ? item.planName![0].toUpperCase()
                    : "",
                style: TextStyle(
                    color: textColorArr[index % textColorArr.length],
                    fontSize: AppTheme.large,
                    fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            Expanded(
                child: CustomText(
                    title: item.planName!,
                    colors: textColorArr[index % textColorArr.length],
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    maxLines: 2,
                    height: 1,
                    fontWeight: FontWeight.w500)),
          ],
        ));
  }

  cardDataRow(String label, String value) {
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
                title: value.isNotEmpty ? value : "-",
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
        color: AppTheme.colorGrayTxtBg,
        height: 0.5,
      ),
    );
  }

  checkStatus(String? custPlanStatus, String? custServMappingStatus) {
    String? status = custPlanStatus!.toLowerCase();
    String? statusWorkflow = custServMappingStatus != null
        ? custServMappingStatus.toLowerCase()
        : "";
    if (statusWorkflow == "new activation" || statusWorkflow == "rejected") {
      if (statusWorkflow == "new activation") {
        statusColor = AppTheme.statusApprove;
      } else {
        statusColor = AppTheme.colorRed;
      }
      displayStatus = custServMappingStatus!.toLowerCase();
    } else {
      displayStatus = custPlanStatus.toLowerCase();
      switch (status) {
        case "active":
        case "ingrace":
          return statusColor = AppTheme.statusApprove;
        case "terminate":
        case "stop":
        case "inactive":
        case "expired":
          return statusColor = AppTheme.colorRed;
        case "hold":
        case "disable":
          return statusColor = AppTheme.colorGrey;
        default:
          return;
      }
    }
    return true;
  }
}

getDateTimeFormat(String? startEndExpiryDate) {
  DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API).parse(startEndExpiryDate!);
  return DateFormat(Constant.API_DATE_TIME_FORMAT_AM_PM).format(date);

  log("startEndExpiryDate122345>> ${DateFormat(Constant.API_DATE_TIME_FORMAT).format(date)}");
  // DateFormat(Constant.API_DATE_TIME_FORMAT).format(date);
}

String remainingDuration(String endDate) {
  final now = Moment.now();
  final endDates = DateTime.parse(endDate).toMoment();
  var remainingDays = endDates.differenceInDays(now);
  return remainingDays.toString();
}

getSerialNumber(CustPlanDataList item) {
  String? serialNumber;
  for (var element in item.customerInventorySerialnumberDtos!) {
    if (element.primary == true) {
      serialNumber = element.serialNumber;
    } else {
      serialNumber = element.serialNumber;
    }
  }
  return serialNumber;
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
        // padding: const EdgeInsets.all(Constant.SMALL_PADDING - 5),
        child: SvgPicture.asset(
          btnName,
          height: Constant.MENU_ICON_SIZE,
          width: Constant.MENU_ICON_SIZE,
          color: txtColor,
          fit: BoxFit.fill,
        ),
      ),
    ),
  );
}

isLatestFuturePlan(
    int? custPlanMappingId,
    String? expiryDate,
    List<CustPlanDataList>? futurePlanList,
    List<CustPlanDataList>? activePlanList) {
  if (futurePlanList != null && futurePlanList.isNotEmpty) {
    if (activePlanList!.length == 1) {
      return (futurePlanList
          .where((element) => element.custPlanMapppingId == custPlanMappingId)
          .isEmpty);
    }
    return (futurePlanList
            .where((element) => element.custPlanMapppingId == custPlanMappingId)
            .isEmpty &&
        activePlanList
            .where((element) =>
                element.custPlanMapppingId == custPlanMappingId &&
                DateFormat(Constant.DATE_NEW_TIME_FORMAT)
                        .parse(element.expiryDate.toString())
                        .millisecondsSinceEpoch
                        .toInt() >
                    DateFormat(Constant.DATE_NEW_TIME_FORMAT)
                        .parse(expiryDate.toString())
                        .millisecondsSinceEpoch
                        .toInt())
            .isEmpty);
  }
  return (activePlanList!
      .where((element) =>
          element.custPlanMapppingId == custPlanMappingId &&
          DateFormat(Constant.DATE_NEW_TIME_FORMAT)
                  .parse(element.expiryDate.toString())
                  .millisecondsSinceEpoch
                  .toInt() >
              DateFormat(Constant.DATE_NEW_TIME_FORMAT)
                  .parse(expiryDate.toString())
                  .millisecondsSinceEpoch
                  .toInt())
      .isEmpty);
}

checkPlanGroup(CustPlanDataList plan, List<CustPlanDataList>? futurePlanList) {
  if (plan.plangroupid != null) {
    // futurePlanList.forEach((element) {});
    // Iterable<CustPlanDataList> groupPlanList = futurePlanList
    //     .where((element) => element.plangroupid == plan.plangroupid);
    // return groupPlanList == plan;
    return (futurePlanList!
            .where((element) => element.plangroupid == plan.plangroupid)
            .toList() ==
        plan);
  }
  return true;
}

checkIfChildCustomer(CustPlanDataList plan, CustomerDetail customerDetail) {
  return customerDetail.parentCustomerId != null && plan.invoiceType == "Group";
}
