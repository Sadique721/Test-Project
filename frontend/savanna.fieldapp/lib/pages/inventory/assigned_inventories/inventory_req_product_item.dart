import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class InventoryReqProductItem extends StatelessWidget {
  FulfilmentProductMappings? item;
  int index;

  InventoryReqProductItem({Key? key, required this.index, required this.item})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(
        vertical: index == 0 ? 0 : Constant.MEDIUM_PADDING,
        // horizontal: Constant.SCREEN_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.VERY_SMALL_PADDING),
              child: detailItem(
                Strings.product_category,
                (item!.productCategoryName != null &&
                        item!.productCategoryName!.isNotEmpty)
                    ? item!.productCategoryName!
                    : "-",
                Strings.product,
                (item!.productName != null && item!.productName!.isNotEmpty)
                    ? "${item!.productName}"
                    : "-",
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: detailItem(
                Strings.item_type,
                (item!.itemType != null && item!.itemType!.isNotEmpty)
                    ? item!.itemType!
                    : "-",
                Strings.qty,
                (item!.quantity != null && item!.quantity.toString().isNotEmpty)
                    ? "${item!.quantity}"
                    : "-",
              ),
            ),
            Align(
              alignment: Alignment.centerRight,
              child: Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.VERY_SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SMALL_PADDING,
                      vertical: Constant.VERY_SMALL_PADDING),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(Constant.LARGE_PADDING),
                    color: (item!.requestStatus != null &&
                            item!.requestStatus!.isNotEmpty &&
                            item!.requestStatus!.equalsIgnoreCase("open"))
                        ? AppTheme.colorBlueRView
                        : AppTheme.colorGreen,
                  ),
                  child: CustomText(
                      title: item!.requestStatus,
                      colors: AppTheme.colorWhite,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      maxLines: 2,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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

  titleWidget(String? title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small,
      fontWeight: FontWeight.w600,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
