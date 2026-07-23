import 'package:savbill/pages/inventory/module/response/view_bulk_consumption_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class BulkConsumptionItem extends StatelessWidget {
  BulkConsumptionDetail item;
  int index;
  final Function()? onTapMacMapView;
  final Function()? onTapDelete;
  final Function()? onTapApprove;
  final Function()? onTapReject;

  BulkConsumptionItem({
    Key? key,
    required this.index,
    required this.item,
    this.onTapMacMapView,
    this.onTapDelete,
    this.onTapApprove,
    this.onTapReject,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String pName = "";
    Color approvalStatus = AppTheme.colorGrey;
    bool? showButton = true;

    if (item.productName != null && item.productName!.isNotEmpty) {
      pName = item.productName!;
    }

    if (item.approvalStatus != null && item.approvalStatus!.isNotEmpty) {
      if (item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
        approvalStatus = AppTheme.colorGrey;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.approve)) {
        approvalStatus = AppTheme.statusClosedGreen;
      } else if (item.approvalStatus!.equalsIgnoreCase(Strings.rejected)) {
        approvalStatus = AppTheme.statusReject;
      }
    }

    if (!item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
      showButton = false;
    } else {
      showButton = true;
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
                        title: item.bulkConsumptionName!,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),

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
                      color: approvalStatus,
                    ),
                    child: CustomText(
                        title: (item.approvalStatus != null &&
                                item.approvalStatus!.isNotEmpty)
                            ? item.approvalStatus
                            : "",
                        colors: AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500),
                  ),
                ),

                // (item.approvalStatus != null && item.approvalStatus!.isNotEmpty)
                //     ? CustomText(
                //         title: (item.approvalStatus != null &&
                //                 item.approvalStatus!.isNotEmpty)
                //             ? item.approvalStatus
                //             : "",
                //         colors: approvalStatus,
                //         textAlign: TextAlign.start,
                //         fontSize: AppTheme.small,
                //         maxLines: 2,
                //         height: 1,
                //         fontWeight: FontWeight.w500)
                //     : Container(),
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
              Strings.product,
              pName.isNotEmpty ? pName : "-",
              Strings.qty,
              item.qty != null ? item.qty.toString() : "-",
              Strings.item_type,
              item.itemType != null && item.itemType!.isNotEmpty
                  ? item.itemType
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              showButton == true
                  ? buttonView(deleteSvg, AppTheme.colorPrimaryTheme,
                      AppTheme.colorWhite, onTapDelete!)
                  : buttonView(deleteSvg, AppTheme.colorGrayTxtBg,
                      AppTheme.colorGrey, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              showButton == true
                  ? buttonView(checkSvg, AppTheme.colorPrimaryTheme,
                      AppTheme.colorWhite, onTapApprove!)
                  : buttonView(checkSvg, AppTheme.colorGrayTxtBg,
                      AppTheme.colorGrey, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              showButton == true
                  ? buttonView(cancelSvg, AppTheme.colorPrimaryTheme,
                      AppTheme.colorWhite, onTapReject!)
                  : buttonView(cancelSvg, AppTheme.colorGrayTxtBg,
                      AppTheme.colorGrey, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(statusSvg, AppTheme.colorPrimaryTheme,
                  AppTheme.colorWhite, onTapMacMapView!),
            ]),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
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

  basicDetailItem(String title1, String? value1, String title2, String? value2,
      String title3, String? value3) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
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
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title3),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value3),
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
