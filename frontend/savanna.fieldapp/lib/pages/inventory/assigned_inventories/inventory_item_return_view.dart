import 'package:savbill/pages/inventory/assigned_inventories/inventory_return_item_controller.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class InventoryReturnItemView extends StatelessWidget {
  InventoryListDetail item;
  int index;
  InventoryReturnItemController inventoryReturnItemController;

  InventoryReturnItemView(
      {Key? key,
      required this.index,
      required this.item,
      required this.inventoryReturnItemController})
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
              item.serialNumber,
              Strings.condition,
              item.condition,
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

                    ],
                  ),
                ),
                Row(
                  children: [
                    CustomText(
                      title: "${Strings.remarks} :",
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
                      child: TextFormField(
                        key: Key(item.id.toString()),
                        initialValue:
                            item.remarks != null && item.remarks!.isNotEmpty
                                ? item.remarks
                                : "",
                        textAlign: TextAlign.start,
                        textAlignVertical: TextAlignVertical.center,
                        style: TextStyle(
                          color: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                          height: 1,
                          fontFamily: AppTheme.appFontName,
                          decoration: TextDecoration.none,
                        ),
                        decoration: InputDecoration(
                            counterText: "",
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme.colorPrimary, width: 1.0),
                            ),
                            focusColor: Colors.amberAccent,
                            focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme.colorIconGrey, width: 1.0),
                            ),
                            enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                color: AppTheme.colorIconGrey,
                                width: 1.0,
                              ),
                            ),
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING),
                            hintText: Strings.enter_remarks,
                            alignLabelWithHint: true,
                            fillColor: AppTheme.colorWhite,
                            hoverColor: AppTheme.colorWhite),
                        textInputAction: TextInputAction.done,
                        keyboardType: TextInputType.text,
                        maxLines: 1,
                        onChanged: (value) {
                          inventoryReturnItemController
                              .inventoryList![index].remarks = value;
                          inventoryReturnItemController.update();
                        },
                      ),
                    ),
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
