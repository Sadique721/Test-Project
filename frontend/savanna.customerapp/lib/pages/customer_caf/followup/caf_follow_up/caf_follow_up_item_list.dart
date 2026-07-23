import 'dart:core';
import 'dart:developer';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_controller.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/customer_caf_follow_up_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';
import 'package:moment_dart/moment_dart.dart';

class CafFollowUpItemList extends StatelessWidget {
  CafFollowUpDataList? item;
  CafFollowUpController? controller;
  final Function()? onTapRescheduleFollowUp;
  final Function()? onTapCloseFollowUp;
  final Function()? onTapRemarkFollowUp;
  final Function()? onTapCallFollowUp;

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
  Color? cardColor;
  String? displayStatus;
  dynamic currentDateTime;


  CafFollowUpItemList(
      {Key? key,
      required this.index,
      required this.item,
      required this.customerDetail,
      required this.controller,
      required this.onTapRescheduleFollowUp,
      required this.onTapRemarkFollowUp,
      required this.onTapCloseFollowUp,
      required this.onTapCallFollowUp})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    bool? isDisable = false;
    if (item!.status!.equalsIgnoreCase("Closed") ||
        item!.status!.equalsIgnoreCase("closed")) {
      statusColor = AppTheme.colorBlueRView;
    } else if (item!.status!.equalsIgnoreCase("Pending") ||
        item!.status!.equalsIgnoreCase("pending")) {
      statusColor = AppTheme.colorError;
    } else if (item!.status!.equalsIgnoreCase("ReSchedule") ||
        item!.status!.equalsIgnoreCase("r{eSchedule")) {
      statusColor = AppTheme.colorBlueRView;
    }

    if (item!.status!.equalsIgnoreCase("Closed") ||
        item!.status!.equalsIgnoreCase("closed")) {
      isDisable = true;
    }



    currentDateTime = DateTime.now();
    if(DateTime.now().isAfter(DateTime.parse(item!.followUpDatetime!))){
      cardColor = AppTheme.colorYellow;
    } else {
      cardColor = AppTheme.colorWhite;
    }

    return Stack(
      children: [
        Card(
          margin: const EdgeInsets.only(
              top: Constant.MEDIUM_PADDING,
              left: 0,
              right: 0,
              bottom: Constant.MEDIUM_PADDING),
          elevation: 2,
          color: cardColor,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              cardDataRow(Strings.customer_name, item!.customersName ?? ""),
              line(),
              cardDataRow(Strings.followup_name, item!.followUpName ?? ""),
              line(),
              cardDataRow(
                  Strings.followup_date_time, item!.followUpDatetime ?? ""),
              line(),
              Padding(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomText(
                        title: Strings.status,
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
                            title: item!.status ?? "",
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
              Padding(
                padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    CustomText(
                        title: Strings.action,
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
                        padding: const EdgeInsets.symmetric(
                            vertical: Constant.SMALL_PADDING,
                            horizontal: Constant.SMALL_PADDING),
                        child: Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            // crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              isDisable == true
                                  ? buttonView(
                                      rescheduleFollowUpSvg,
                                      AppTheme.colorDisableGray,
                                      AppTheme.colorWhite,
                                      null,
                                    )
                                  : buttonView(
                                      rescheduleFollowUpSvg,
                                      AppTheme.colorPrimary,
                                      AppTheme.colorWhite,
                                onTapRescheduleFollowUp,
                                    ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING + 3,
                              ),
                              isDisable == true
                                  ? buttonView(
                                      rejectRemoveSvg,
                                      AppTheme.colorDisableGray,
                                      AppTheme.colorWhite,
                                      null,
                                    )
                                  : buttonView(
                                      rejectRemoveSvg,
                                      AppTheme.colorPrimary,
                                      AppTheme.colorWhite,
                                      onTapCloseFollowUp,
                                    ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING + 3,
                              ),
                              buttonView(
                                openTicketInvoice,
                                AppTheme.colorPrimary,
                                AppTheme.colorWhite,
                                onTapRemarkFollowUp,
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING + 3,
                              ),
                              buttonView(
                                phoneIconSvg,
                                AppTheme.colorPrimary,
                                AppTheme.colorWhite,
                                onTapCallFollowUp,
                              ),
                            ]),
                      ),
                    ))
                  ],
                ),
              ),
            ],
          ),
        ),
        Positioned(
          top: Constant.MEDIUM_PADDING,
          left: Constant.SMALL_PADDING,
          child: Container(
            width: 80,
            height: Constant.VERY_SMALL_PADDING,
            // color: textColorArr[index % textColorArr.length],
            color: textColorArr[index % textColorArr.length],
          ),
        ),
      ],
    );
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
}

getDateTimeFormat(String? startEndExpiryDate) {
  DateTime date =
      DateFormat(Constant.DATE_TIME_FORMAT_API).parse(startEndExpiryDate!);
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
