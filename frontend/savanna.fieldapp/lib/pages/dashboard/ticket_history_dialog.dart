import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

ticketHistoryDialog(BuildContext context, List<CaseHistoryDetails>? item) {
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
                            title: Strings.ticket_progress_details,
                            fontSize: AppTheme.large,
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontWeight: FontWeight.w600,
                          ),
                          const SizedBox(height: Constant.SMALL_PADDING),
                          ListView.builder(
                              physics: const NeverScrollableScrollPhysics(),
                              scrollDirection: Axis.vertical,
                              shrinkWrap: true,
                              itemCount: item?.length,
                              itemBuilder: (BuildContext context, int index) {
                                CaseHistoryDetails? itemDetail = item?[index];
                                int? lstLength = item?.length;
                                return Padding(
                                  padding: EdgeInsets.only(
                                      top: (index == 0)
                                          ? Constant.SMALL_PADDING
                                          : Constant.EXPANTABLE_ITEM_MARGIN,
                                      bottom: (index == (lstLength! - 1))
                                          ? Constant.EXPANTABLE_ITEM_MARGIN
                                          : 0),
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
                                          mainAxisAlignment:
                                              MainAxisAlignment.start,
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                            detailItem(
                                                Strings.entry_type,
                                                itemDetail?.entitytype ?? "-",
                                                Strings.operation,
                                                itemDetail?.operation ?? "-"),
                                            const SizedBox(
                                                height: Constant.SMALL_PADDING),
                                            detailItem(
                                                Strings.new_value,
                                                itemDetail?.newvalue ?? "-",
                                                Strings.old_value,
                                                itemDetail?.oldvalue ?? "-"),
                                          ]),
                                    ),
                                  ),
                                );
                              }),
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


taskHistoryDialog(BuildContext context, List<CaseHistoryDetails>? item) {
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
                                title: Strings.task_progress_details,
                                fontSize: AppTheme.large,
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontWeight: FontWeight.w600,
                              ),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              ListView.builder(
                                  physics: const NeverScrollableScrollPhysics(),
                                  scrollDirection: Axis.vertical,
                                  shrinkWrap: true,
                                  itemCount: item?.length,
                                  itemBuilder: (BuildContext context, int index) {
                                    CaseHistoryDetails? itemDetail = item?[index];
                                    int? lstLength = item?.length;
                                    return Padding(
                                      padding: EdgeInsets.only(
                                          top: (index == 0)
                                              ? Constant.SMALL_PADDING
                                              : Constant.EXPANTABLE_ITEM_MARGIN,
                                          bottom: (index == (lstLength! - 1))
                                              ? Constant.EXPANTABLE_ITEM_MARGIN
                                              : 0),
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
                                              mainAxisAlignment:
                                              MainAxisAlignment.start,
                                              crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                              children: [
                                                detailItem(
                                                    Strings.entry_type,
                                                    itemDetail?.entitytype ?? "-",
                                                    Strings.operation,
                                                    itemDetail?.operation ?? "-"),
                                                const SizedBox(
                                                    height: Constant.SMALL_PADDING),
                                                detailItem(
                                                    Strings.new_value,
                                                    itemDetail?.newvalue ?? "-",
                                                    Strings.old_value,
                                                    itemDetail?.oldvalue ?? "-"),
                                              ]),
                                        ),
                                      ),
                                    );
                                  }),
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
      Flexible(
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
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.end,
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
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w400,
  );
}
