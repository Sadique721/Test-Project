import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class PopInventoryMapItem extends StatelessWidget {
  InOutWardMACMapping item;
  final Function()? onTapDelete;

  PopInventoryMapItem({
    Key? key,
    required this.item,
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
            height: Constant.VERY_SMALL_PADDING,
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
                    title: item.macAddress,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 3,
                  child: CustomText(
                    title: item.serialNumber,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 2,
                  child: CustomText(
                    title: item.status,
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
                VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ),
                Expanded(
                  flex: 1,
                  child: Center(
                    child: InkWell(
                      onTap: onTapDelete,
                      child: Material(
                        elevation: 1.5,
                        color: AppTheme.custDeleteLight,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER)),
                        child: Container(
                          height: Constant.BTN_HEIGHT_M - 10,
                          width: Constant.BTN_HEIGHT_M - 10,
                          alignment: Alignment.center,
                          padding:
                              const EdgeInsets.all(Constant.SMALL_PADDING - 2),
                          child: SvgPicture.asset(
                            deleteSvg,
                            height: Constant.ICON_SIZE,
                            width: Constant.ICON_SIZE,
                            color: AppTheme.custDeleteDark,
                            fit: BoxFit.fill,
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
        ]),
      ),
    );
  }
}
