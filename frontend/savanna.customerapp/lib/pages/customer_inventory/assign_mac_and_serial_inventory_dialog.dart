import 'dart:developer';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import '../inventory/pop/pop_assign_inventory_controller.dart';

class AssignMacAndSerialInventoryDialog extends StatefulWidget {
  final MacAndSerialAssignInventoryAction macSerialNumberAction;
  final List<ProductMacDataList> macAddressLst;
  final PopAssignInventoryController? controller;

  const AssignMacAndSerialInventoryDialog({
    Key? key,
    required this.macSerialNumberAction,
    required this.macAddressLst,
    required this.controller,
  }) : super(key: key);

  @override
  _AssignMacAndSerialInventoryDialogState createState() => _AssignMacAndSerialInventoryDialogState();
}

class _AssignMacAndSerialInventoryDialogState extends State<AssignMacAndSerialInventoryDialog> {
  List<ProductMacDataList> selectItemsLst = [];

  String? macAddressValue;
  String? serialNumberValue;


  @override
  void initState() {
    super.initState();
    setState(() {
      selectItemsLst.addAll(widget.macAddressLst);
    });
  }

  @override
  Widget build(BuildContext context) {
    String title = Strings.mac_address;
    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          AlertDialog(
            insetPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING * 2,
            ),
            contentPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING,
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            backgroundColor: AppTheme.colorWhite,
            shape: const RoundedRectangleBorder(
                borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
            content: Container(
              width: MediaQuery.of(context).size.width,
              color: AppTheme.colorWhite,
              child: Form(
                child: Column(
                    mainAxisSize: MainAxisSize.min,
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: title,
                            colors: AppTheme.title_dark,
                            fontSize: AppTheme.large,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING - 5),
                        child: Divider(
                          height: 5,
                          color: AppTheme.dividerColor,
                          thickness: 1,
                        ),
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Expanded(
                            child: CustomText(
                              title: Strings.items,
                              textAlign: TextAlign.center,
                              colors: AppTheme.lable_noramal,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          Container(
                            height: Constant.EXTRA_LARGE_PADDING,
                            width: 1,
                            color: AppTheme.lable_noramal,
                          ),
                          Expanded(
                            child: CustomText(
                              title: Strings.item_type,
                              textAlign: TextAlign.center,
                              colors: AppTheme.lable_noramal,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          Container(
                            height: Constant.EXTRA_LARGE_PADDING,
                            width: 1,
                            color: AppTheme.lable_noramal,
                          ),
                          Expanded(
                            child: CustomText(
                              title: Strings.mac_address,
                              textAlign: TextAlign.center,
                              colors: AppTheme.lable_noramal,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          Container(
                            height: Constant.EXTRA_LARGE_PADDING,
                            width: 1,
                            color: AppTheme.lable_noramal,
                          ),
                          Expanded(
                            child: CustomText(
                              title: Strings.serial_no,
                              textAlign: TextAlign.center,
                              colors: AppTheme.lable_noramal,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                          Container(
                            height: Constant.EXTRA_LARGE_PADDING,
                            width: 1,
                            color: AppTheme.lable_noramal,
                          ),
                          Expanded(
                            child: CustomText(
                              title: Strings.action,
                              textAlign: TextAlign.center,
                              colors: AppTheme.lable_noramal,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ],
                      ),
                      Flexible(
                          child: ListView.builder(
                            shrinkWrap: true,
                            primary: false,
                            itemCount: selectItemsLst.length,
                            itemBuilder: (context, index) {
                              ProductMacDataList item = selectItemsLst[index];
                              item.macAddressValue = item.macAddress;
                              item.serialNumberValue = item.serialNumber;
                              return Column(
                                children: [
                                  InkWell(
                                    onTap: () {
                                      selectItemsLst.forEach((f) {
                                        if (f.id == item.id) {
                                          f.selected = !f.selected!;
                                          item.macAddressValue = item.macAddress;
                                          item.serialNumberValue =
                                              item.serialNumber;
                                        } else {
                                          f.selected = false;
                                        }
                                      });
                                      setState(() {
                                        selectItemsLst = selectItemsLst;
                                      });
                                    },
                                    child: Padding(
                                      padding: const EdgeInsets.symmetric(
                                          vertical: Constant.SMALL_PADDING + 1,
                                          horizontal: Constant.MEDIUM_PADDING),
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
                                            child: CustomText(
                                              title: "${item.itemId!}",
                                              textAlign: TextAlign.start,
                                              colors: item.selected == true
                                                  ? AppTheme.colorPrimary
                                                  : AppTheme.lable_noramal,
                                              fontSize: AppTheme.small + 1,
                                              fontWeight: item.selected == true
                                                  ? FontWeight.w500
                                                  : FontWeight.w700,
                                            ),
                                          ),
                                          Expanded(
                                            flex: 1,
                                            child: CustomText(
                                              title: item.condition!,
                                              textAlign: TextAlign.start,
                                              colors: item.selected == true
                                                  ? AppTheme.colorPrimary
                                                  : AppTheme.lable_noramal,
                                              fontSize: AppTheme.small + 1,
                                              fontWeight: item.selected == true
                                                  ? FontWeight.w500
                                                  : FontWeight.w700,
                                            ),
                                          ),
                                          Expanded(
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
                                                textAlign: TextAlign.start,
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
                                                    Strings.mac_address,
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
                                                  widget.controller!.update();
                                                },
                                              ),
                                            ),
                                          ),
                                          Expanded(
                                            child: item.selected == false
                                                ? CustomText(
                                              title: item.serialNumber!,
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
                                                textAlign: TextAlign.start,
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
                                                    Strings.serial_no,
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
                                                  widget.controller!.update();
                                                },
                                              ),
                                            ),
                                          ),

                                          Expanded(
                                              child: InkWell(
                                                onTap: () {
                                                  if (item.selected == true) {
                                                    widget.controller!
                                                        .updateMacAndSerialNumber(
                                                        item.itemId,
                                                        item.serialNumber,
                                                        item.macAddress);
                                                    widget.controller!.update();
                                                  }
                                                },
                                                child: Container(
                                                  margin: const EdgeInsets.all(
                                                      Constant.SMALL_PADDING),
                                                  child: Material(
                                                    elevation: 1,
                                                    color: item.selected == false
                                                        ? AppTheme.custEditLight
                                                        : AppTheme.colorAccent,
                                                    shape: RoundedRectangleBorder(
                                                        borderRadius: BorderRadius
                                                            .circular(Constant
                                                            .BTN_ROUNDED_CORNER)),
                                                    child: Container(
                                                      height: Constant.BTN_HEIGHT_M - 5,
                                                      width: Constant.BTN_HEIGHT_M - 20,
                                                      alignment: Alignment.center,
                                                      padding: const EdgeInsets.all(
                                                          Constant.SMALL_PADDING),
                                                      child: SvgPicture.asset(
                                                        editSvg,
                                                        height: Constant.ICON_SIZE,
                                                        width: Constant.ICON_SIZE,
                                                        color: item.selected == false
                                                            ? AppTheme.colorIconGrey
                                                            : AppTheme.colorWhite,
                                                      ),
                                                    ),
                                                  ),
                                                ),
                                              )),
                                        ],
                                      ),
                                    ),
                                  ),
                                  index == (selectItemsLst.length - 1)
                                      ? Container()
                                      : Padding(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal:
                                        Constant.SCREEN_PADDING - 5),
                                    child: Divider(
                                      height: 5,
                                      color: AppTheme.lable_noramal,
                                      thickness: 0.1,
                                    ),
                                  ),
                                ],
                              );
                            },
                          )),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      Row(
                        children: [
                          Expanded(
                            child: InkWell(
                              onTap: () {
                                validateSelection();
                              },
                              child: Container(
                                padding: const EdgeInsets.only(
                                    top: Constant.SCREEN_PADDING,
                                    bottom: Constant.SCREEN_PADDING),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                    color: AppTheme.colorLightGrey,
                                    width: 1.0,
                                  ),
                                  borderRadius: const BorderRadius.only(
                                      bottomLeft: Radius.circular(
                                          Constant.MEDIUM_PADDING)),
                                ),
                                child: Text(
                                  Strings.select,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: AppTheme.medium + 1,
                                    color: AppTheme.colorPositive,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                          ),
                          Expanded(
                            child: InkWell(
                              onTap: () {
                                Get.back();
                              },
                              child: Container(
                                padding: const EdgeInsets.only(
                                    top: Constant.SCREEN_PADDING,
                                    bottom: Constant.SCREEN_PADDING),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                    color: AppTheme.colorLightGrey,
                                    width: 1.0,
                                  ),
                                  borderRadius: const BorderRadius.only(
                                      bottomRight: Radius.circular(
                                          Constant.MEDIUM_PADDING)),
                                ),
                                child: Text(
                                  Strings.cancel,
                                  style: TextStyle(
                                    fontWeight: FontWeight.bold,
                                    fontSize: AppTheme.medium + 1,
                                    color: AppTheme.colorNagative,
                                  ),
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ]),
              ),
            ),
          ),
          Positioned(
            child: GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Align(
                alignment: Alignment.topRight,
                child: Icon(Icons.close, color: AppTheme.colorWhite),
              ),
            ),
          ),
        ],
      ),
    );
  }

  validateSelection() {
    List<ProductMacDataList> selectedItem = [];
    for (var element in selectItemsLst) {
      if (element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.macSerialNumberAction
          .macSerialNumberBtnAction(selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, Strings.select_at_list_one_item,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  void setMacAddressValue(String value, String macAddress) {
    Map<String, int> macList = {};
    try {
      int number = int.parse(value);
      macList[macAddress] = number;
      widget.controller!.macAddressController.text = macList as String;
    } on FormatException {}
  }

  void setSerialNumberValue(String value, String serialNumber) {
    Map<String, int> serialList = {};
    try {
      int number = int.parse(value);
      serialList[serialNumber] = number;
      widget.controller!.serialNumberController.text = serialList as String;
    } on FormatException {}
    widget.controller!.update();
  }
}

abstract class MacAndSerialAssignInventoryAction {
  void macSerialNumberBtnAction({List<ProductMacDataList> selectedItem});
}
