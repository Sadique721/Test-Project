import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

ticketDetailDialog(BuildContext context, TicketDetail item) {
  return showDialog(
      barrierDismissible: true,
      context: context,
      builder: (context) {
        return Stack(
          children: [
            Container(
              padding: const EdgeInsets.only(
                top: Constant.VERY_EXTRA_LARGE_PADDING,
              ),
              child: AlertDialog(
                insetPadding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING,
                    vertical: Constant.SCREEN_PADDING),
                contentPadding: const EdgeInsets.all(
                  Constant.SCREEN_PADDING,
                ),
                shape: const RoundedRectangleBorder(
                    borderRadius: BorderRadius.all(
                        Radius.circular(Constant.SMALL_PADDING))),
                content: SingleChildScrollView(
                    child: ListBody(
                  children: [
                    SizedBox(
                      width: Get.width,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // const SizedBox(height: Constant.SMALL_PADDING),
                          CustomText(
                            title: Strings.ticket_detail,
                            fontSize: AppTheme.large,
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontWeight: FontWeight.w600,
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          titleWidget(Strings.customer_name),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.customerName),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.user_name),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.userName),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.case_title),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.caseTitle),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.service_area),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.serviceAreaName),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.case_status),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.caseStatus),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.case_type),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.caseType),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.case_reason),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.caseReasonName),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.priority),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.priority),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.followup_date),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.nextFollowupDate),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          titleWidget(Strings.followup_time),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          valueWidget(item.nextFollowupTime),
                          // const SizedBox(height: Constant.SMALL_PADDING),
                        ],
                      ),
                    ),
                  ],
                )),
              ),
            ),
            Positioned(
              top: Constant.SMALL_PADDING,
              right: Constant.SCREEN_PADDING,
              child: GestureDetector(
                onTap: () {
                  Get.back();
                },
                child: Align(
                  alignment: Alignment.topRight,
                  child: Icon(Icons.close, color: AppTheme.colorWhite),
                ),
              ),
            ),
          ],
        );
      });
}

titleWidget(String title) {
  return CustomText(
    title: title,
    colors: AppTheme.title_dark,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w700,
  );
}

valueWidget(String? value) {
  return CustomText(
    title: value!.isNotEmpty ? value : "-",
    colors: AppTheme.lable_noramal,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w400,
  );
}
