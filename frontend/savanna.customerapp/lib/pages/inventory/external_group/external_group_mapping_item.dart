import 'package:savbill/pages/inventory/module/response/view_external_lite_mac_mapping_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class ExternalGroupMapItem extends StatelessWidget {
  ExternalLiteMacMappingDetail item;
  bool showMacAddress;
  String from;

  ExternalGroupMapItem({
    Key? key,
    required this.item,
    required this.from,
    required this.showMacAddress,
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
                  height: Constant.LARGE_PADDING,
                ),
                showMacAddress
                    ? Expanded(
                        flex: 1,
                        child: CustomText(
                          title: item.macAddress,
                          colors: AppTheme.lable_noramal,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.verySmall,
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
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                ),
                const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                  height: Constant.LARGE_PADDING,
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
