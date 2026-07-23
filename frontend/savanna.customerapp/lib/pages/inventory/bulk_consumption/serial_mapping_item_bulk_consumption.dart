import 'dart:developer';
import 'package:savbill/pages/inventory/bulk_consumption/add_edit_bulk_consumption_controller.dart';
import 'package:savbill/pages/inventory/module/response/get_all_serialized_item_base_on_product_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class SerialMappingBulkConsumptionItem extends StatelessWidget {
  SerializedItemBaseDataList item;
  bool showCheckBox = false;
  ValueChanged<bool?>? onSelectChanged;
  AddEditBulkConsumptionController? controller;

  SerialMappingBulkConsumptionItem({
    Key? key,
    required this.controller,
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
        child:
        Column(
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.SMALL_PADDING,
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                children: [
                  item.selected == true
                      ? Icon(
                    Icons.check_circle,
                    color: AppTheme.colorPrimary,
                    size: Constant.ICON_SIZE,
                  )
                      : Icon(
                    Icons.radio_button_off,
                    color: AppTheme.lable_noramal,
                    size: Constant.ICON_SIZE,
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Expanded(
                    flex: 1,
                    child: CustomText(
                      title: "${item.itemId!}",
                      textAlign: TextAlign.center,
                      colors: item.selected == true
                          ? AppTheme.colorPrimary
                          : AppTheme.lable_noramal,
                      fontSize: AppTheme.small,
                      fontWeight: item.selected == true
                          ? FontWeight.w500
                          : FontWeight.w700,
                    ),
                  ),
                  Expanded(
                    flex: 2,
                    child: item.selected == false
                        ? CustomText(
                      title: item.macAddress ?? "-",
                      textAlign: TextAlign.center,
                      colors: item.selected == true
                          ? AppTheme.colorPrimary
                          : AppTheme.lable_noramal,
                      fontSize: AppTheme.small + 1,
                      fontWeight:
                      item.selected == true
                          ? FontWeight.w300
                          : FontWeight.w500,
                    )
                        : Container(
                      margin: const EdgeInsets.only(
                          right: Constant
                              .VERY_SMALL_PADDING),
                      child: TextFormField(
                        // key: Key(item.id.toString()),
                        initialValue:
                        item.macAddress != null &&
                            item.macAddress!
                                .isNotEmpty
                            ? item.macAddress
                            : "",
                        textAlign: TextAlign.center,
                        textAlignVertical:
                        TextAlignVertical.center,
                        style: TextStyle(
                          color: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                          height: 1,
                          fontFamily:
                          AppTheme.appFontName,
                          decoration:
                          TextDecoration.none,
                        ),
                        decoration: InputDecoration(
                            counterText: "",
                            border:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme
                                      .colorPrimary,
                                  width: 1.0),
                            ),
                            focusColor:
                            Colors.amberAccent,
                            focusedBorder:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme
                                      .colorIconGrey,
                                  width: 1.0),
                            ),
                            enabledBorder:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                color: AppTheme
                                    .colorIconGrey,
                                width: 0.8,
                              ),
                            ),
                            contentPadding:
                            const EdgeInsets
                                .symmetric(
                                horizontal: Constant
                                    .VERY_SMALL_PADDING),
                            hintText:
                            Strings.address,
                            alignLabelWithHint: true,
                            fillColor:
                            AppTheme.colorWhite,
                            hoverColor:
                            AppTheme.colorWhite),
                        textInputAction:
                        TextInputAction.next,
                        keyboardType:
                        TextInputType.text,
                        maxLines: 1,
                        onChanged: (value) {
                          log("macAddress >>${value}");
                          item.macAddressValue =
                              value;
                          item.macAddress = value;
                          controller!.update();
                        },
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 2,
                    child: item.selected == false
                        ? CustomText(
                      title: item.macAddress ?? "-",
                      textAlign: TextAlign.center,
                      colors: item.selected == true
                          ? AppTheme.colorPrimary
                          : AppTheme.lable_noramal,
                      fontSize: AppTheme.small + 1,
                      fontWeight:
                      item.selected == true
                          ? FontWeight.w300
                          : FontWeight.w500,
                    )
                        : Container(
                      margin: const EdgeInsets.only(
                          left: Constant
                              .VERY_SMALL_PADDING),
                      child: TextFormField(
                        // key: Key(item.id.toString()),
                        initialValue:
                        item.serialNumber !=
                            null &&
                            item.serialNumber!
                                .isNotEmpty
                            ? item.serialNumber
                            : "-",
                        textAlign: TextAlign.center,
                        textAlignVertical:
                        TextAlignVertical.center,
                        style: TextStyle(
                          color: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                          height: 1,
                          fontFamily:
                          AppTheme.appFontName,
                          decoration:
                          TextDecoration.none,
                        ),
                        decoration: InputDecoration(
                            counterText: "",
                            border:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme
                                      .colorPrimary,
                                  width: 1.0),
                            ),
                            focusColor:
                            Colors.amberAccent,
                            focusedBorder:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                  color: AppTheme
                                      .colorIconGrey,
                                  width: 1.0),
                            ),
                            enabledBorder:
                            OutlineInputBorder(
                              borderRadius: BorderRadius
                                  .circular(Constant
                                  .BTN_ROUNDED_CORNER),
                              borderSide: BorderSide(
                                color: AppTheme
                                    .colorIconGrey,
                                width: 0.8,
                              ),
                            ),
                            contentPadding: const EdgeInsets
                                .symmetric(
                                horizontal: Constant
                                    .VERY_SMALL_PADDING),
                            hintText:
                            Strings.number,
                            alignLabelWithHint: true,
                            fillColor:
                            AppTheme.colorWhite,
                            hoverColor:
                            AppTheme.colorWhite),
                        textInputAction:
                        TextInputAction.done,
                        keyboardType:
                        TextInputType.text,
                        maxLines: 1,
                        onChanged: (value) {
                          log("serailNumber >>${value}");
                          item.serialNumberValue =
                              value;
                          item.serialNumber = value;
                          controller!.update();
                        },
                      ),
                    ),
                  ),
                  Expanded(
                    flex: 1,
                    child: CustomText(
                      title: item.condition!,
                      textAlign: TextAlign.center,
                      colors: item.selected == true
                          ? AppTheme.colorPrimary
                          : AppTheme.lable_noramal,
                      fontSize: AppTheme.small + 1,
                      fontWeight: item.selected == true
                          ? FontWeight.w500
                          : FontWeight.w700,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
