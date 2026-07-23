import 'package:savbill/pages/pending_approvals/model/response/inventory_approval_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class InventoryApprovalItem extends StatelessWidget {
  InventoryApprovalDataList item;

  final Function()? onTapApprove;
  final Function()? onTapReject;

  InventoryApprovalItem({
    Key? key,
    required this.item,
    this.onTapApprove,
    this.onTapReject,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
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
                (item.customerName != null && item.customerName!.isNotEmpty)
                    ? item.customerName
                    : "-",
                Strings.product_name,
                (item.productName != null && item.productName!.isNotEmpty)
                    ? item.productName
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.current_plan,
                (item.currentPlan != null && item.currentPlan!.isNotEmpty)
                    ? item.currentPlan
                    : "-",
                Strings.item_type,
                (item.itemType != null && item.itemType!.isNotEmpty)
                    ? item.itemType
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.status,
              (item.status != null && item.status!.isNotEmpty)
                  ? item.status
                  : "-",
              Strings.next_approval,
              (item.assigneeName != null && item.assigneeName!.isNotEmpty)
                  ? item.assigneeName
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.status,
              (item.status != null && item.status!.isNotEmpty)
                  ? item.status
                  : "-",
              Strings.next_approval,
              (item.assigneeName != null && item.assigneeName!.isNotEmpty)
                  ? item.assigneeName
                  : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          // Padding(
          //   padding:
          //       const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
          //   child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
          //     buttonView(checkSvg, AppTheme.custEditLight,
          //         AppTheme.custEditDark, onTapApprove!),
          //     const SizedBox(
          //       width: Constant.SMALL_PADDING,
          //     ),
          //     buttonView(cancelSvg, AppTheme.custDeleteLight,
          //         AppTheme.custDeleteDark, onTapReject!),
          //   ]),
          // ),
          // const SizedBox(
          //   height: Constant.SMALL_PADDING,
          // ),
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
