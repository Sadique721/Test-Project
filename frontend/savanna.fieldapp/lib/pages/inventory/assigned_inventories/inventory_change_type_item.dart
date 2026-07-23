import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_type_controller.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class InventoryChangeTypeItem extends StatelessWidget {
  InventoryListDetail item;
  int index;
  InventoryChangeTypeController inventoryChangeTypeController;

  InventoryChangeTypeItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.inventoryChangeTypeController})
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
              item.serialNumber.toString().isNotEmpty  || item.serialNumber != null ? item.serialNumber : "",
              Strings.condition,
              item.condition.toString().isNotEmpty || item.condition != null ? item.condition : "",
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
                      titleWidget(Strings.type),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      SizedBox(
                          width: 120,
                          height: Constant.APPBAR_ITEM_H - 10,
                          child: DropdownButtonHideUnderline(
                            child: DropdownButtonFormField(
                              key: Key("type${item.id}"),
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
                                  Strings.type,
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
                              value: item.selectedItemType,
                              items: inventoryChangeTypeController.typeList
                                  ?.map((ItemTypeDetail value) {
                                return DropdownMenuItem<ItemTypeDetail>(
                                  value: value,
                                  child: Text(
                                    value.text!,
                                    style: TextStyle(
                                      fontSize: AppTheme.small,
                                      color: AppTheme.lable_noramal,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                inventoryChangeTypeController
                                        .inventoryList![index]
                                        .selectedItemType =
                                    value as ItemTypeDetail?;
                                inventoryChangeTypeController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          )),
                    ],
                  ),
                ),
                Flexible(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.remarks),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      SizedBox(
                          width: 120,
                          height: Constant.APPBAR_ITEM_H - 10,
                          child: DropdownButtonHideUnderline(
                            child: DropdownButtonFormField(
                              key: Key("remark${item.id}"),
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
                                  Strings.remarks,
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
                              value: item.selectedRemarkType,
                              items: inventoryChangeTypeController
                                  .remarkTypeList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(
                                    value.text!,
                                    style: TextStyle(
                                      fontSize: AppTheme.small,
                                      color: AppTheme.lable_noramal,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                inventoryChangeTypeController
                                        .inventoryList![index]
                                        .selectedRemarkType =
                                    value as DropdownDetail?;
                                inventoryChangeTypeController
                                    .inventoryList![index].changeTypeRemarks="";
                                if (inventoryChangeTypeController
                                        .inventoryList![index]
                                        .selectedRemarkType!
                                        .id ==
                                    Strings.other) {
                                  inventoryChangeTypeController
                                      .inventoryList![index].readOnly = false;
                                } else {
                                  inventoryChangeTypeController
                                      .inventoryList![index].readOnly = true;
                                }
                                inventoryChangeTypeController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          )),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Row(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(flex: 1, child: Container()),
                  Flexible(
                    flex: 1,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        titleWidget(Strings.remarks),
                        const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                        SizedBox(
                          width: 120,
                          height: Constant.APPBAR_ITEM_H - 10,
                          child: TextFormField(
                            readOnly:
                                item.readOnly != null ? item.readOnly! : true,
                            key: Key("edt${item.id}"),
                            initialValue: item.changeTypeRemarks != null &&
                                    item.changeTypeRemarks!.isNotEmpty
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
                                      color: AppTheme.colorIconGrey,
                                      width: 1.0),
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
                              inventoryChangeTypeController
                                  .inventoryList![index]
                                  .changeTypeRemarks = value;
                              inventoryChangeTypeController.update();
                            },
                          ),
                        ),
                      ],
                    ),
                  ),
                ]),
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
