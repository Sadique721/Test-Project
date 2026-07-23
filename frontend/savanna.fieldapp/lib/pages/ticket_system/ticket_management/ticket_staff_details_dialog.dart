import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

TicketStaffDetailsDialog(BuildContext context, List<String>? staffList) {
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
                                child:  CustomText(
                                  title: formatString(staffList!),
                                  fontSize: AppTheme.medium,
                                  colors: AppTheme.lable_noramal,
                                  textAlign: TextAlign.start,
                                  fontWeight: FontWeight.normal,
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


String formatString(List x) {
  String formatted ='';
  for(var i in x) {
    formatted += '$i, ';
  }
  return formatted.replaceRange(formatted.length -2, formatted.length, '');
}