import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/change_plan/change_plan_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../widgets/no_data_found.dart';

class ShowDirectChargeDialog extends StatefulWidget {
  final bool showChargeDetails;
  final List<dynamic> addedChargeList;
  ChangePlanController controller;
  // final Function()? onDeleteTap;

  ShowDirectChargeDialog({
    required this.showChargeDetails,
    required this.addedChargeList,
    required this.controller,
  });

  @override
  State<ShowDirectChargeDialog> createState() => _ShowDirectChargeDialogState();
}

class _ShowDirectChargeDialogState extends State<ShowDirectChargeDialog> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    // DateFormat dateFormat =
    // DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
    return contentBox(context);
  }

  contentBox(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: widget.showChargeDetails!
          ? Stack(
              children: [
                AlertDialog(
                  // title: CustomText(
                  //   title: "Added Charges",
                  //   colors: AppTheme.title_dark,
                  //   fontSize: AppTheme.large,
                  //   fontWeight: FontWeight.w600,
                  // ),
                  insetPadding: const EdgeInsets.only(
                    top: Constant.SCREEN_PADDING * 1,
                  ),
                  contentPadding: const EdgeInsets.all(0),
                  clipBehavior: Clip.antiAliasWithSaveLayer,
                  backgroundColor: AppTheme.colorPrimary,
                  shape: const RoundedRectangleBorder(
                      borderRadius: BorderRadius.all(
                          Radius.circular(Constant.SMALL_PADDING))),
                  content: Container(
                    width: MediaQuery.of(context).size.width,
                    color: AppTheme.colorWhite,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          color: AppTheme.colorPrimary,
                          padding: const EdgeInsets.symmetric(
                              vertical: Constant.MEDIUM_PADDING,
                              horizontal: Constant.SCREEN_PADDING),
                          child: Align(
                            alignment: Alignment.centerLeft,
                            child: CustomText(
                              title: "Added Charges",
                              colors: AppTheme.title_dark,
                              fontSize: AppTheme.large,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        if (widget.addedChargeList.isNotEmpty) ...[
                          (widget.addedChargeList.isNotEmpty)
                              ? ListView.builder(
                                  shrinkWrap: true,
                                  physics: const NeverScrollableScrollPhysics(),
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: Constant.VERY_SMALL_PADDING - 5,
                                      vertical: Constant.SMALL_PADDING),
                                  itemCount: widget.addedChargeList.length,
                                  itemBuilder:
                                      (BuildContext context, int index) {
                                    return directChargeList(context, index,
                                        widget.addedChargeList[index]);
                                  })
                              : SizedBox(
                                  height: Constant.VERY_EXTRA_LARGE_PADDING,
                                  child: noDataFound(),
                                ),
                        ],
                        const SizedBox(height: Constant.SMALL_PADDING),
                        Align(
                          alignment: Alignment.center,
                          child: InkWell(
                            onTap: () {
                              Get.back();
                            },
                            child: Container(
                              alignment: Alignment.center,
                              width: Constant.REMARKS_VIEW_HEIGHT,
                              height: Constant.APPBAR_ITEM_H - 5,
                              padding: const EdgeInsets.only(
                                  top: Constant.VERY_SMALL_PADDING + 2,
                                  bottom: Constant.VERY_SMALL_PADDING + 2),
                              decoration: BoxDecoration(
                                color: AppTheme.colorRed,
                                borderRadius: const BorderRadius.all(
                                    Radius.circular(Constant.MEDIUM_PADDING)),
                              ),
                              child: CustomText(
                                title: Strings.close,
                                fontSize: AppTheme.small,
                                fontWeight: FontWeight.bold,
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(height: Constant.SMALL_PADDING),
                      ],
                    ),
                  ),
                ),
                Positioned(
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
            )
          : const SizedBox.shrink(),
    );
  }

  directChargeList(BuildContext context, int index, dynamic addedChargeItem) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: Constant.VERY_SMALL_PADDING,horizontal: Constant.VERY_SMALL_PADDING),
      child: Card(
        elevation: 2,
        color: AppTheme.colorWhite,
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: cardDataRow(
                      Strings.charge_name,
                      addedChargeItem.chargeName.toString() ?? "-",
                      AppTheme.lable_noramal),
                  flex: 2,
                ),
                Expanded(
                  child: cardDataRow(
                      Strings.charge_amount,
                      addedChargeItem.actualprice.toString() ?? "-",
                      AppTheme.lable_noramal),
                  flex: 2,
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  child: cardDataRow(Strings.charge_type,
                      addedChargeItem.type ?? "-", AppTheme.lable_noramal),
                  flex: 2,
                ),
                Expanded(
                  child: cardDataRow(
                      Strings.plan_name,
                      addedChargeItem.planName ?? "-",
                      AppTheme.lable_noramal),
                  flex: 2,
                ),
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 2,
                  child: cardDataRow(
                      Strings.validity,
                      "${addedChargeItem.validity.toString() ?? ""} ${addedChargeItem.unitsOfValidity ?? "-"}",
                      AppTheme.lable_noramal),
                ),
                Expanded(
                  flex: 2,
                  child: cardDataRow(Strings.new_price,
                      addedChargeItem.price.toString() ?? "", AppTheme.lable_noramal),
                )
              ],
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Expanded(
                  flex: 2,
                  child: cardDataRow(
                      Strings.discount, addedChargeItem.discount != null ? addedChargeItem.discount.toString() : "", AppTheme.lable_noramal),
                ),
                Expanded(
                  flex: 2,
                  child: Container(
                    height: Constant.CARD_BOTTOM_BUTTON_H - 10,
                    alignment: Alignment.center,
                    decoration: const BoxDecoration(
                        borderRadius: BorderRadius.only(
                            bottomLeft:
                                Radius.circular(Constant.BTN_ROUNDED_CORNER),
                            bottomRight: Radius.circular(
                                Constant.BTN_ROUNDED_CORNER))),
                    child:IconButton(
                      constraints: const BoxConstraints(maxHeight: 36),
                      padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
                      icon: const Icon(
                        Icons.delete,
                        color: Colors.redAccent,
                      ),
                      onPressed: (){
                        showDialog(
                          context: context,
                          builder:
                              (BuildContext context) {
                            return AlertDialogHelper(
                                title: Strings.app_name,
                                message:
                                Strings.msg_delete,
                                positiveBtnText:
                                Strings.ok,
                                negativeBtnText:
                                Strings.cancel,
                                positiveBtnClick: () {
                                  Get.back();
                                  widget.addedChargeList.remove(addedChargeItem);
                                  widget.controller.update();
                                  Get.back();
                                },
                                negativeBtnClick: () {
                                  Get.back();
                                });
                          },
                        );
                      },
                    ),
                  ),
                )
              ],
            ),
          ],
        ),
      ),
    );
  }

  String formatDate(DateTime date) {
    return date != null
        ? '${date.year}-${date.month}-${date.day} ${date.hour}:${date.minute}:${date.second}'
        : '-';
  }

  noDataFound() {
    return const NoDataFound();
  }

  cardDataRow(String label, String value, Color? textColor) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(height: Constant.VERY_SMALL_PADDING),
          CustomText(
              title: value.isNotEmpty ? value : "-",
              colors: textColor ?? AppTheme.lable_noramal,
              textAlign: TextAlign.end,
              fontSize: AppTheme.small,
              maxLines: 2,
              height: 1,
              fontWeight: FontWeight.w400)
        ],
      ),
    );
  }
}
