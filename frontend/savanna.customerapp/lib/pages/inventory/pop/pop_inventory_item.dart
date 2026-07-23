import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';

class PopInventoryItem extends StatelessWidget {
  PopInventoryDetail item;
  final Function()? onTapEdit;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapAuditApproval;
  bool showApproveBtn = true,showEditBtn= true;

  PopInventoryItem({
    Key? key,
    required this.item,
    this.onTapEdit,
    this.onTapApprove,
    this.onTapReject,
    this.onTapAuditApproval,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String assignedDate = "", expiryDate = "";

    Color? statusColor, statusTextColor = AppTheme.colorLightBlack;
    if (item.approvalStatus != null && item.approvalStatus!.isNotEmpty) {
      if (item.approvalStatus!.equalsIgnoreCase("Active")) {
        statusColor = AppTheme.statusClosedGreen;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.approvalStatus!.equalsIgnoreCase("Rejected")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.approvalStatus!.equalsIgnoreCase("Approve")) {
        statusColor = AppTheme.statusApprove;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.approvalStatus!.equalsIgnoreCase("InActive")) {
        statusColor = AppTheme.statusReject;
        statusTextColor = AppTheme.colorWhite;
      } else if (item.approvalStatus!.equalsIgnoreCase("Pending")) {
        statusColor = AppTheme.statusPending;
        statusTextColor = AppTheme.colorWhite;
      }
    } else {
      statusColor = AppTheme.statusClosedGreen;
    }

    if (item.assignedDateTime != null && item.assignedDateTime!.isNotEmpty) {
      DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
          .parse(item.assignedDateTime!);
      assignedDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }
    if (item.expiryDateTime != null && item.expiryDateTime!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.expiryDateTime!);
      expiryDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }

    if (!item.approvalStatus!.equalsIgnoreCase(Strings.pending)) {
      showApproveBtn = false;
    } else {
      showApproveBtn = true;
    }



    if(item.approvalStatus!.equalsIgnoreCase(Strings.rejected)){
      showEditBtn = false;
    }else{
      showEditBtn = true;
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
                        title: (item.inwardNumber != null &&
                                item.inwardNumber!.isNotEmpty)
                            ? item.inwardNumber!
                            : "-",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
                /*CustomText(
                    title: (item.approvalStatus != null &&
                            item.approvalStatus!.isNotEmpty)
                        ? item.approvalStatus
                        : "",
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.small,
                    maxLines: 2,
                    height: 1,
                    fontWeight: FontWeight.w500)*/

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
                      color: /*(item.approvalStatus != null &&
                              item.approvalStatus!.isNotEmpty &&
                              item.approvalStatus!
                                  .equalsIgnoreCase(Strings.active))
                          ? AppTheme.statusClosedGreen
                          : AppTheme.statusReject*/
                          statusColor,
                    ),
                    child: CustomText(
                        title: (item.approvalStatus != null &&
                                item.approvalStatus!.isNotEmpty)
                            ? item.approvalStatus
                            : "",
                        colors: statusTextColor!,
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
                (item.productName != null && item.productName!.isNotEmpty)
                    ? item.productName
                    : "-",
                Strings.assign_qty,
                (item.qty != null) ? item.qty.toString() : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.assigned_date,
                (assignedDate.isNotEmpty) ? assignedDate : "-",
                Strings.expiry_date,
                (expiryDate.isNotEmpty) ? expiryDate : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              showEditBtn ? buttonView(editSvg, AppTheme.colorPrimary, AppTheme.colorWhite,
                  onTapEdit!) : buttonView(editSvg, AppTheme.colorLightGrey,
              AppTheme.colorDisableGray, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              /* buttonView(checkSvg, AppTheme.colorPrimary, AppTheme.colorWhite,
                  onTapApprove!),*/

              showApproveBtn
                  ? buttonView(checkSvg, AppTheme.colorPrimary,
                      AppTheme.colorWhite, onTapApprove!)
                  : buttonView(checkSvg, AppTheme.colorLightGrey,
                      AppTheme.colorDisableGray, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              showApproveBtn
                  ? buttonView(cancelSvg, AppTheme.colorPrimary,
                      AppTheme.colorWhite, onTapReject!)
                  : buttonView(cancelSvg, AppTheme.colorLightGrey,
                      AppTheme.colorDisableGray, null),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(auditStatusSvg, AppTheme.colorPrimary,
                  AppTheme.colorWhite, onTapAuditApproval!),
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
            height: Constant.ICON_SIZE + 10,
            width: Constant.ICON_SIZE + 10,
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
