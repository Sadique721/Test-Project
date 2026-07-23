import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class BCMapItem extends StatelessWidget {
  InOutWardMACMapping item;

  ValueChanged<bool?>? onSelectChanged;

  BCMapItem({
    Key? key,
    required this.item,
    this.onSelectChanged,
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
                Expanded(
                  flex: 3,
                  child: CustomText(
                    title: item.serialNumber,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 3,
                  child: CustomText(
                    title: item.macAddress,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING + 2,
                ),
                Center(
                  child: SizedBox(
                    width: 15,
                    height: 15,
                    child: Checkbox(
                      value: item.selected ?? false,
                      activeColor: AppTheme.colorPrimary,
                      onChanged: onSelectChanged,
                    ),
                  ),
                ),
                const SizedBox(
                  width: Constant.MEDIUM_PADDING + 2,
                ),
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
