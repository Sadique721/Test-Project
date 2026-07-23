import 'package:savbill/pages/inventory/assigned_inventories/inventory_warranty_change_controller.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class InventoryWarrantyChangeItem extends StatelessWidget {
  InventoryListDetail item;
  int index;
  InventoryWarrantyChangeController inventoryWarrantyChangeController;

  InventoryWarrantyChangeItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.inventoryWarrantyChangeController})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.symmetric(
        vertical: index == 0 ? 0 : Constant.MEDIUM_PADDING,
        horizontal: Constant.SCREEN_PADDING,
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
          children: [
            detailItem(
              Strings.serial_no,
              item.serialNumber ?? "",
              Strings.condition,
              item.condition!.isNotEmpty || item.condition != null ? item.condition : "",
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
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
                      titleWidget(Strings.currant_warranty),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(item.warranty != null
                          ? item.warranty.toString()
                          : "-"),
                    ],
                  ),
                ),
                Row(
                  children: [
                    CustomText(
                      title: "${Strings.warranty} :",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w600,
                      maxLines: 1,
                    ),
                    const SizedBox(
                      width: Constant.VERY_SMALL_PADDING,
                    ),
                    SizedBox(
                        width: 120,
                        height: Constant.APPBAR_ITEM_H - 10,
                        child: DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            key: Key(item.id.toString()),
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.warranty_status,
                                style: TextStyle(
                                  fontSize: AppTheme.small,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: item.selectedWarranty,
                            items: inventoryWarrantyChangeController
                                .warrantyStatusList
                                ?.map((WarrantyStatusDetail value) {
                              return DropdownMenuItem<WarrantyStatusDetail>(
                                value: value,
                                child: Text(value.text!, style: TextStyle(
                                  fontSize: AppTheme.small,
                                  color: AppTheme.lable_noramal,
                                  fontFamily: AppTheme.appFontName,
                                ),),
                              );
                            }).toList(),
                            onChanged: (value) {
                              inventoryWarrantyChangeController
                                      .inventoryList![index].selectedWarranty =
                                  value as WarrantyStatusDetail?;
                              inventoryWarrantyChangeController.update();
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        )),
                  ],
                )
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
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

  titleWidget(String title) {
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
      title: value,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
