import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class OutwardMapItem extends StatelessWidget {
  InwardMacSerialDataList item;
  final Function()? onTapDelete;
  bool showMacAddress;

  OutwardMapItem({
    Key? key,
    required this.item,
    required this.showMacAddress,
    this.onTapDelete,
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
          IntrinsicHeight(
            child: Row(
              children: [
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                showMacAddress
                    ? Expanded(
                  child: CustomText(
                    title: item.id,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                )
                    : Container(),
                showMacAddress
                    ? const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                )
                    : Container(),
                showMacAddress
                    ? VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                )
                    : Container(),
                showMacAddress
                    ? Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item.macAddress,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                )
                    : Container(),
                showMacAddress
                    ? const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                )
                    : Container(),
                showMacAddress
                    ? VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                )
                    : Container(),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item.serialNumber,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                showMacAddress
                    ? VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                )
                    : Container(),
                showMacAddress
                    ? Expanded(
                  child: CustomText(
                    title: item.condition,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                )
                    : Container(),
                showMacAddress
                    ? const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                )
                    : Container(),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }
}