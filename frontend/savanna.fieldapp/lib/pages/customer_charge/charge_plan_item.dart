import 'package:savbill/pages/customer_charge/charge_management_controller.dart';
import 'package:savbill/pages/customer_charge/response/customer_charge_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class ChargePlanItem extends StatelessWidget {
  CustChargeOverrideDetail item;
  ChargeManagementController? controller;
  // PostpaidPlanDetail? planItem;
  int index;

  ChargePlanItem({
    Key? key,
    required this.index,
    required this.item,
    required this.controller,
    // required this.planItem,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String? validateValue = "", startDateValue = "", endDateValue = "",planName = "";
    //
    // if(planItem != null){
    //   planName = planItem!.name.toString();
    // }else{
    //   planName ="-";
    // }

    if (controller!.planList != null && controller!.planList!.length > index) {
     planName = controller!.planList![index].name; // You can safely access the element here.
    }else{
      planName ="-";
    }

    if (item.validity != null || item.unitsOfValidity != null) {
      validateValue = "${item.validity}${item.unitsOfValidity}";
    }
    if (item.startdate!.isNotEmpty && item.startdate != null) {
      startDateValue =
          controller!.apiDateFormat.format(DateTime.parse(item.startdate!));
    } else {
      startDateValue = "-";
    }
    if (item.startdate!.isNotEmpty && item.startdate != null) {
      endDateValue =
          controller!.apiDateFormat.format(DateTime.parse(item.startdate!));
    } else {
      endDateValue = "";
    }
    return Container(
      margin: const EdgeInsets.only(
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
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
                      child: CustomText(
                          title: item.chargeName ?? "",
                          colors: AppTheme.title_dark,
                          textAlign: TextAlign.start,
                          maxLines: 2,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
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
                          color: (item.isDeleted != null &&
                                  item.isDeleted == false)
                              ? AppTheme.statusClosedGreen
                              : AppTheme.statusReject,
                        ),
                        child: CustomText(
                            title: (item.isDeleted != null &&
                                    item.isDeleted == false)
                                ? Strings.active
                                : Strings.in_active,
                            colors: AppTheme.colorWhite,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 2,
                            height: 1,
                            fontWeight: FontWeight.w500),
                      ),
                    ),
                  ]),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.charge_amount,
                  "${controller!.currencySymbol}${item.actualprice.toString() ?? " "}",
                  Strings.static_ip,
                  (item.staticIPAdrress != null &&
                          item.staticIPAdrress!.isNotEmpty)
                      ? item.staticIPAdrress!
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.plan_name,
                  planName,
                  Strings.validity,
                  validateValue),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.new_price,
                  "${controller!.currencySymbol}${item.price.toString() ?? " "}",
                  Strings.start_date,
                  startDateValue),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(Strings.end_date, endDateValue, "", ""),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
          ],
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          flex: 2,
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
