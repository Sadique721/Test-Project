import 'package:savbill/pages/inventory/module/response/inventory_product_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class ProductItem extends StatelessWidget {
  ProductDetail item;
  int index;

  final Function()? onTapEdit;
  final Function()? onTapDelete;
  final Function()? onTapProductDetails;

  ProductItem({
    Key? key,
    required this.index,
    required this.item,
    this.onTapEdit,
    this.onTapDelete,
    this.onTapProductDetails,
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
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: InkWell(
                      onTap: onTapProductDetails,
                      child: CustomText(
                          title: (item.name != null && item.name!.isNotEmpty)
                              ? item.name!
                              : "-",
                          colors: AppTheme.colorPrimary,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.small,
                          maxLines: 2,
                          height: 1,
                          fontWeight: FontWeight.w500),
                    )),


                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING,vertical: Constant.VERY_SMALL_PADDING),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal:
                        Constant.SMALL_PADDING,
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
                Strings.warranty_time,
                (item.expiryTime != null) ? item.expiryTime!.toString() : "-",
                Strings.warranty_time_unit,
                (item.expiryTimeUnit != null && item.expiryTimeUnit!.isNotEmpty)
                    ? item.expiryTimeUnit
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.type,
                (item.productCategory != null &&
                        item.productCategory!.type != null &&
                        item.productCategory!.type!.isNotEmpty)
                    ? item.productCategory!.type
                    : "-",
                Strings.description,
                (item.description != null && item.description!.isNotEmpty)
                    ? item.description
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
