import 'package:savbill/pages/inventory/module/request/save_manual_mac_serial_req.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class InwardMappingItemNew extends StatelessWidget {

  MacSerialListDTOList? item;
  final Function()? onTapDelete;
  bool showMacAddress;
  String from;

  InwardMappingItemNew({
    Key? key,
    this.item,
    required this.from,
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
            height: Constant.VERY_SMALL_PADDING,
          ),
          IntrinsicHeight(
            child: Row(
              children: [


                showMacAddress
                    ? Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item?.macAddress,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                    maxLines: 2,
                  ),
                )
                    : const SizedBox.shrink(),
                showMacAddress
                    ? const SizedBox(
                  width: Constant.VERY_SMALL_PADDING,
                ): const SizedBox.shrink(),
                showMacAddress
                    ? VerticalDivider(
                  color: AppTheme.title_dark,
                  thickness: 0.5,
                ): const SizedBox.shrink(),
                Expanded(
                  flex: 1,
                  child: CustomText(
                    title: item?.serialNumber ?? "-",
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.verySmall + 1,
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
                          padding: const EdgeInsets.all(
                              Constant.SMALL_PADDING - 2),
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
