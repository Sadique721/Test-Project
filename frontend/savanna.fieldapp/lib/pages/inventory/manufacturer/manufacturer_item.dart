import 'package:savbill/pages/inventory/module/response/manufacture_list_res.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';

class ManufacturerItem extends StatelessWidget {
  ManufacturerDetail item;
  int index;

  final Function()? onTapEdit;
  final Function()? onTapDelete;

  ManufacturerItem({
    Key? key,
    required this.index,
    required this.item,
    this.onTapEdit,
    this.onTapDelete,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin:  const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  flex: 4,
                  child: CustomText(
                      title: item.name!,
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      maxLines: 2,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ),

                Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal:
                      Constant.SMALL_PADDING-2,
                      vertical: Constant
                          .VERY_SMALL_PADDING),
                  decoration: BoxDecoration(
                    borderRadius:
                    BorderRadius.circular(
                        Constant.LARGE_PADDING),color: (item.status != null &&
                      item.status!.isNotEmpty &&
                      item.status!.equalsIgnoreCase(Strings.active))
                      ? AppTheme.statusClosedGreen
                      : AppTheme.statusReject,),
                  child: CustomText(
                      title: (item.status != null &&
                          item.status!.isNotEmpty &&
                          item.status!.equalsIgnoreCase(Strings.active))
                          ? Strings.active
                          : Strings.in_active,
                      colors: AppTheme.colorWhite,
                      textAlign: TextAlign.center,
                      fontSize: AppTheme.small-1,
                      maxLines: 2,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ),
                Expanded(
                  flex: 2,
                  child: Padding(
                    padding:
                    const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING),
                    child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
                      buttonView(editSvg, AppTheme.custEditLight, AppTheme.custEditDark,
                          onTapEdit!),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      buttonView(deleteSvg, AppTheme.custDeleteLight,
                          AppTheme.custDeleteDark, onTapDelete!),
                    ]),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),


          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
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