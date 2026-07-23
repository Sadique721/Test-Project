import 'dart:developer';

import 'package:savbill/pages/inventory/module/response/inventory_assigned_service_area_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class AssignedServiceAreaInventoryItem extends StatelessWidget {
  AssignedServiceAreaDataList item;

  AssignedServiceAreaInventoryItem({
    Key? key,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "", assignedDate = "", outwardsNo = "";
    if (item.productName != null && item.productName!.isNotEmpty) {
      pName = item.productName!;
    }
    if (item.assignedDateTime != null && item.assignedDateTime!.isNotEmpty) {
      DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
          .parse(item.assignedDateTime!);
      assignedDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }



    String serialNumberAdd = "";
    for (var element in item.inOutWardMACMapping!) {
      serialNumberAdd = "$serialNumberAdd${element.serialNumber!}, ";
    }
    if (!serialNumberAdd.isNullOrEmpty() &&
        serialNumberAdd.contains(",") &&
        serialNumberAdd.length >= 2) {
      serialNumberAdd =
          serialNumberAdd.substring(0, serialNumberAdd.length - 2);
    }



    String macAddressValue = "";
    if(item.inOutWardMACMapping!.isNotEmpty) {
      for (var element in item.inOutWardMACMapping!) {
        macAddressValue = "$macAddressValue${element.macAddress}, ";
      }
      if (!macAddressValue.isNullOrEmpty() &&
          macAddressValue.contains(",") &&
          macAddressValue.length >= 2) {
        macAddressValue =
            macAddressValue.substring(0, macAddressValue.length - 2);
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
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: CustomText(
                        title: pName,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
                CustomText(
                    title: assignedDate,
                    colors: AppTheme.colorPrimary,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small,
                    maxLines: 2,
                    height: 1,
                    fontWeight: FontWeight.w500),
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
                Strings.serial_no,
                serialNumberAdd,
                Strings.mac_address,macAddressValue)
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(Strings.assign_qty,
                item.qty != null ? item.qty!.toString() : "-",
                "${Strings.service_area} ${Strings.name}",
                item.productName != null && item.productName!.isNotEmpty
                    ? item.productName!
                    : "-"
            ),
          ),

          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                "${Strings.assign} ${Strings.name}",
                item.assigneeName != null ? item.assigneeName!.toString() : "-","", ""),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
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