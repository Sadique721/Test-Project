import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:get/get.dart';

ticketStaffDetailDialog(BuildContext context, TicketStaffDetail staffDetail) {
  String roleName = "";
  if (staffDetail.roleName != null && staffDetail.roleName!.isNotEmpty) {
    roleName = staffDetail.roleName![0];
  }
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
                          CustomText(
                            title: Strings.staff_details,
                            fontSize: AppTheme.large,
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontWeight: FontWeight.w600,
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          Padding(
                            padding: const EdgeInsets.only(
                                top: Constant.SMALL_PADDING,
                                bottom: Constant.EXPANTABLE_ITEM_MARGIN),
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.expantableItemBg,
                                border: Border.all(
                                    color: AppTheme.expantableItemBg),
                                borderRadius: const BorderRadius.all(
                                  Radius.circular(3),
                                ),
                              ),
                              child: Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      detailItem(
                                        Strings.name,
                                        (staffDetail.fullName != null &&
                                                staffDetail
                                                    .fullName!.isNotEmpty)
                                            ? staffDetail.fullName!
                                            : "-",
                                        Strings.username,
                                        (staffDetail.username != null &&
                                                staffDetail
                                                    .username!.isNotEmpty)
                                            ? staffDetail.username!
                                            : "-",
                                      ),
                                      const SizedBox(
                                          height: Constant.SMALL_PADDING),
                                      detailItem(
                                        Strings.mobile,
                                        (staffDetail.phone != null &&
                                                staffDetail.phone!.isNotEmpty)
                                            ? staffDetail.phone!
                                            : "-",
                                        Strings.email,
                                        (staffDetail.email != null &&
                                                staffDetail.email!.isNotEmpty)
                                            ? staffDetail.email!
                                            : "-",
                                      ),
                                      const SizedBox(
                                          height: Constant.SMALL_PADDING),
                                      detailItem(
                                        Strings.role_name,
                                        (roleName.isNotEmpty) ? roleName : "-",
                                        Strings.service_area,
                                          staffDetail.serviceAreasNameList!.isNotEmpty ? formatString(staffDetail.serviceAreasNameList!) : "-",
                                      ),
                                    ]),
                              ),
                            ),
                          ),
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

detailItem(String title1, String? value1, String title2, String? value2) {
  return Row(
    mainAxisSize: MainAxisSize.max,
    crossAxisAlignment: CrossAxisAlignment.center,
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
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
    maxLines: 2,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w400,
  );
}

String formatString(List listData) {
  String formatted ='';
  for(var i in listData) {
    formatted += '$i, ';
  }
  return formatted.replaceRange(formatted.length -2, formatted.length, '');
}
