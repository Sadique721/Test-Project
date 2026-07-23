import 'dart:developer';
import 'package:savbill/pages/customer_inventory/inventory_replace_controller.dart';
import 'package:savbill/pages/customer_inventory/response/replacement_mac_address_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';


class InventoryMacAddressListScreen extends StatefulWidget {
  final SelectInventoryMacAddressAction macAddressAction;
  final List<ReplacementMacAddressList> macAddressLst;
  // final OtherInventoryController? controller;

  const InventoryMacAddressListScreen({
    Key? key,
    required this.macAddressAction,
    required this.macAddressLst,
    // this.controller,
  }) : super(key: key);

  @override
  _SelectMacAddressState createState() => _SelectMacAddressState();
}

class _SelectMacAddressState extends State<InventoryMacAddressListScreen> {
  final controller = Get.find<InventoryReplaceController>();

  List<ReplacementMacAddressList> selectItemsLst = [];

  String? macAddressValue;
  String? serialNumberValue;

  // @override
  // void initState() {
  //   super.initState();
  //   widget.macAddressLst.addAll(widget.macAddressLst);
  // }

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<InventoryReplaceController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: controller.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    return GestureDetector(
        onTap: () {
          FocusScope.of(context).requestFocus(FocusNode());
        },
        child: Column(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: Constant.LARGE_PADDING),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  // Row(
                  //   children: [
                  //     // Expanded(
                  //     //   flex: 2,
                  //     //   child: Padding(
                  //     //     padding: const EdgeInsets.symmetric(
                  //     //         horizontal: Constant.SCREEN_PADDING),
                  //     //     child: CoustomTextField(
                  //     //         labelText: Strings.search_your_text_here,
                  //     //         hintColor: AppTheme.colorIconGrey,
                  //     //         textEditingController:
                  //     //         controller
                  //     //             .searchController,
                  //     //         borderEnableColors: AppTheme.colorIconGrey,
                  //     //         borderFocusColors: AppTheme.colorIconGrey,
                  //     //         textColor: AppTheme.colorBlack,
                  //     //         keyboardType: TextInputType.text,
                  //     //         fontSize: AppTheme.small,
                  //     //         textInputAction: TextInputAction.next,
                  //     //         fontWeight: FontWeight.w500,
                  //     //         contentPadding: const EdgeInsets.symmetric(
                  //     //             horizontal: Constant.MEDIUM_PADDING,
                  //     //             vertical: Constant.SMALL_PADDING),
                  //     //         borderCorner: Constant.BTN_ROUNDED_CORNER,
                  //     //         onChanged: (value) {
                  //     //           controller
                  //     //               .searchData(value);
                  //     //         },
                  //     //         onTextValidator: (String? value) {
                  //     //           return null;
                  //     //         },
                  //     //         onTextFiledOnTap: () {},
                  //     //         readOnly: false),
                  //     //   ),
                  //     // ),
                  //     // Expanded(
                  //     //     flex: 1,
                  //     //     child: Padding(
                  //     //       padding: const EdgeInsets.symmetric(
                  //     //           horizontal: Constant.SCREEN_PADDING),
                  //     //       child: SimpleButton(
                  //     //         onTap: () {
                  //     //           controller.clearData();
                  //     //         },
                  //     //         radius: 8,
                  //     //         height: Constant.APPBAR_ITEM_H,
                  //     //         bgColors: AppTheme.colorBlack,
                  //     //         borderColors: AppTheme.colorPrimary,
                  //     //         child: CustomText(
                  //     //           title: Strings.clear,
                  //     //           colors: AppTheme.colorWhite,
                  //     //           fontSize: AppTheme.medium,
                  //     //           fontWeight: FontWeight.w400,
                  //     //         ),
                  //     //       ),
                  //     //     ))
                  //   ],
                  // ),
                  // const SizedBox(height: Constant.MEDIUM_PADDING),
                ],
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
              // const SizedBox(height: Constant.SMALL_PADDING),
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
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING - 5),
                child: Divider(
                  height: 5,
                  color: AppTheme.dividerColor,
                  thickness: 1,
                ),
              ),
              Flexible(
                  child: ListView.builder(
                    controller: controller.controller,
                    scrollDirection: Axis.vertical,
                    itemCount: widget.macAddressLst.length,
                    itemBuilder: (context, index) {
                      if (index ==
                          widget.macAddressLst.length) {
                        if (controller.isShowLoadMore) {
                          return Padding(
                            padding: const EdgeInsets.all(
                                Constant.SMALL_PADDING),
                            child: Center(
                              child: SizedBox(
                                width: Constant.SCREEN_PADDING,
                                height: Constant.SCREEN_PADDING,
                                child: CircularProgressIndicator(
                                  strokeWidth: 2.5,
                                  valueColor: AlwaysStoppedAnimation<Color>(
                                      AppTheme.colorProgress),
                                  backgroundColor: AppTheme.colorProgressBg,
                                ),
                              ),
                            ),
                          );
                        } else {
                          return Container();
                        }
                      }
                      else {
                        ReplacementMacAddressList item = widget.macAddressLst[index];
                        item.macAddressValue = item.macAddress;
                        item.serialNumberValue = item.serialNumber;
                        return Column(
                          children: [
                            InkWell(
                              onTap: () {
                                widget.macAddressLst.forEach((f) {
                                  if (f.id == item.id) {
                                    f.selected = !f.selected!;
                                    item.macAddressValue = item.macAddress;
                                    item.serialNumberValue =
                                        item.serialNumber;
                                  } else {
                                    f.selected = false;
                                  }
                                });
                                controller.update();

                                // setState(() {
                                //   selectItemsLst = selectItemsLst;
                                // });
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
                                            controller!.update();
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
                                            controller.update();
                                          },
                                        ),
                                      ),
                                    ),
                                    Expanded(
                                        child: InkWell(
                                          onTap: () {
                                            if (item.selected == true) {
                                              controller
                                                  .updateMacAndSerialNumber(
                                                  item.itemId,
                                                  item.serialNumber,
                                                  item.macAddress);
                                              controller.update();
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
                            index == (widget.macAddressLst.length - 1)
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
                      }
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
                        color: AppTheme.colorPrimary,
                        padding: const EdgeInsets.only(
                            top: Constant.SCREEN_PADDING,
                            bottom: Constant.SCREEN_PADDING),
                        // decoration: BoxDecoration(
                        //   border: Border.all(
                        //     color: AppTheme.colorLightGrey,
                        //     width: 1.0,
                        //   ),
                        //   borderRadius: const BorderRadius.only(
                        //       bottomLeft: Radius.circular(
                        //           Constant.MEDIUM_PADDING)),
                        // ),
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
            ])
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.inward_mac_mapping, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateSelection() {
    List<ReplacementMacAddressList> selectedItem = [];
    for (var element in widget.macAddressLst) {
      if (element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.macAddressAction
          .selectInventoryMacAddressBtnAction(selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, Strings.select_at_list_one_item,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

}

abstract class SelectInventoryMacAddressAction {
  void selectInventoryMacAddressBtnAction({List<ReplacementMacAddressList> selectedItem});
}
