import 'dart:ui';

import 'package:savbill/pages/pending_approvals/model/response/pa_open_lead_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PendingApprovalOpenLeadItem extends StatelessWidget {
  PAOpenLeadContent item;

  PendingApprovalOpenLeadItem({
    Key? key,
    required this.item,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String? leadSubSourceName;
    Color? statusColor;

    if (item.leadSubSourceId != null) {
      leadSubSourceName = item.leadSubSourceName;
    } else if (item.leadAgentId != null) {
      leadSubSourceName = item.leadAgentName;
    } else if (item.leadBranchId != null) {
      leadSubSourceName = item.leadBranchName;
    } else if (item.leadCustomerId != null) {
      leadSubSourceName = item.leadCustomerName;
    } else if (item.leadPartnerId != null) {
      leadSubSourceName = item.leadPartnerName;
    } else if (item.leadServiceAreaId != null) {
      leadSubSourceName = item.leadServiceAreaName;
    } else if (item.leadStaffId != null) {
      leadSubSourceName = item.leadStaffName;
    } else {
      leadSubSourceName = "-";
    }

    if (item.leadStatus!.equalsIgnoreCase("Inquiry")) {
      statusColor = AppTheme.colorGreen;
    } else if (item.leadStatus!.equalsIgnoreCase("Rejected")) {
      statusColor = AppTheme.statusReject;
    } else if (item.leadStatus!.equalsIgnoreCase("Re-Inquiry")) {
      statusColor = AppTheme.statusPending;
    } else if (item.leadStatus!.equalsIgnoreCase("Converted")) {
      statusColor = AppTheme.colorGreen;
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
            child: basicDetailItem(
                Strings.customer_name,
                (item.title != null ||
                        item.firstname != null ||
                        item.lastname != null)
                    ? "${item.title ?? ""} ${item.firstname ?? ""} ${item.lastname ?? ""}"
                    : "-",
                Strings.mobile_number,
                (item.mobile != null) ? item.mobile!.toString() : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.lead_source,
                (item.leadSourceName != null) ? item.leadSourceName! : "-",
                Strings.lead_sub_source,
                leadSubSourceName,
              )),
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
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(
                        Strings.assigneeName,
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(item.assigneeName ?? "-"),
                    ],
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.status),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: Constant.VERY_SMALL_PADDING,
                        ),
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
                              title: item.leadStatus ?? "-",
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
                "No. of FollowUp Counts",
                item.leadFollowUpCount !=  null ? item.leadFollowUpCount.toString() : "",
                "",
                "",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
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

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
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
