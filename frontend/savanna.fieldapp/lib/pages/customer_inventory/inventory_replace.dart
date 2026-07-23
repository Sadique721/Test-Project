import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer_inventory/inventory_mac_address_list_screen.dart';
import 'package:savbill/pages/customer_inventory/inventory_mac_address_list_screen.dart';
import 'package:savbill/pages/customer_inventory/inventory_replace_controller.dart';
import 'package:savbill/pages/customer_inventory/response/product_mac_serial_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'response/replacement_mac_address_list_res.dart';

class InventoryReplace extends StatefulWidget {
  @override
  _InventoryReplaceState createState() => _InventoryReplaceState();
}

class _InventoryReplaceState extends State<InventoryReplace> implements SelectInventoryMacAddressAction{
  final inventoryReplaceController = Get.put(InventoryReplaceController());
  final inventoryReplaceFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();

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
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: inventoryReplaceController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(InventoryReplaceController controller) {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Expanded(
              child: SingleChildScrollView(
                child: Padding(
                  padding: const EdgeInsets.only(
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING),
                  child: Form(
                    key: inventoryReplaceFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        /*__________________ Replacement ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.replacement, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
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
                                Strings.replacement,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: false,
                            isDense: true,
                            value: controller.selectReplacementItem,
                            items: controller.replacementList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectReplacementItem =
                                  value as DropdownDetail?;
                              controller.selectReplacementValue =
                                  value.toString();
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectReplacementItem == null) {
                                return Strings.select_replacement;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Replacement reason ____________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.replacement_reason, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
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
                                Strings.replacement_reason,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: controller.selectReplacementReason,
                            items: controller.replacementReasonList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectReplacementReason =
                                  value as DropdownDetail?;
                              controller.selectReplacementReasonValue =
                                  value.toString();
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectReplacementReason == null) {
                                return Strings.select_replacement_reason;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Remark _________________________*/
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.remarks, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.enter_remarks,
                            textEditingController:
                                inventoryReplaceController.remarksController,
                            keyboardType: TextInputType.text,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.please_enter_remarks;
                              }
                              return null;
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING),
                            readOnly: false),

                        /*________________ Product List _______________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.product, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        controller.otherInventoryReplaceFlag == true ? DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
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
                                Strings.product,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: controller.selectProductMacSerialData,
                            items: controller.productMacSerialDataList
                                ?.map((ProductMacSerialDataList value) {
                              return DropdownMenuItem<ProductMacSerialDataList>(
                                value: value,
                                child: Text(value.name!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectProductMacSerialData =
                                  value as ProductMacSerialDataList?;
                              controller.getReplacementMacAddressListApi(value!.id);
                              // controller.getItemBasedOnProductTypeApiCall(
                              //   ownerId: value!.createdById.toString(),
                              //   productId: value.id.toString(),
                              //   productCategoryId: "",
                              // );
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectProductMacSerialData ==
                                      null) {
                                return Strings.please_select_product;
                              }
                              return null;
                            },
                          ),
                        ): const SizedBox.shrink(),

                        controller.replacementMacAddressList.isNotEmpty
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.replacementMacAddressList!.isNotEmpty
                            ? InputTitleRequire(
                                title: Strings.mac_address, require: true)
                            : const SizedBox.shrink(),
                        controller.replacementMacAddressList!.isNotEmpty
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.replacementMacAddressList!.isNotEmpty
                            ? Container(
                                width: MediaQuery.of(context).size.width,
                                color: AppTheme.colorWhite,
                                child: Form(
                                  child: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                    CoustomTextField(
                                        labelText: Strings.assign_mac_address,
                                        textEditingController: inventoryReplaceController
                                            .assignMacController,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.select_mac_address;
                                          }
                                          return null;
                                        },
                                        onTextFiledOnTap: () {
                                          showMacAddressDialog();
                                        },
                                        borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding: const EdgeInsets.symmetric(
                                            horizontal: Constant.SMALL_PADDING),
                                        readOnly: true)

                                        // Row(
                                        //   crossAxisAlignment: CrossAxisAlignment.center,
                                        //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                        //   children: [
                                        //     Expanded(
                                        //       child: CustomText(
                                        //         title: Strings.items,
                                        //         textAlign: TextAlign.center,
                                        //         colors: AppTheme.lable_noramal,
                                        //         fontSize: AppTheme.small,
                                        //         fontWeight: FontWeight.w500,
                                        //       ),
                                        //     ),
                                        //     Container(
                                        //       height: Constant.EXTRA_LARGE_PADDING,
                                        //       width: 1,
                                        //       color: AppTheme.lable_noramal,
                                        //     ),
                                        //     Expanded(
                                        //       child: CustomText(
                                        //         title: Strings.item_type,
                                        //         textAlign: TextAlign.center,
                                        //         colors: AppTheme.lable_noramal,
                                        //         fontSize: AppTheme.small,
                                        //         fontWeight: FontWeight.w500,
                                        //       ),
                                        //     ),
                                        //     Container(
                                        //       height: Constant.EXTRA_LARGE_PADDING,
                                        //       width: 1,
                                        //       color: AppTheme.lable_noramal,
                                        //     ),
                                        //     Expanded(
                                        //       child: CustomText(
                                        //         title: Strings.mac_address,
                                        //         textAlign: TextAlign.center,
                                        //         colors: AppTheme.lable_noramal,
                                        //         fontSize: AppTheme.small,
                                        //         fontWeight: FontWeight.w500,
                                        //       ),
                                        //     ),
                                        //     Container(
                                        //       height: Constant.EXTRA_LARGE_PADDING,
                                        //       width: 1,
                                        //       color: AppTheme.lable_noramal,
                                        //     ),
                                        //     Expanded(
                                        //       child: CustomText(
                                        //         title: Strings.serial_no,
                                        //         textAlign: TextAlign.center,
                                        //         colors: AppTheme.lable_noramal,
                                        //         fontSize: AppTheme.small,
                                        //         fontWeight: FontWeight.w500,
                                        //       ),
                                        //     ),
                                        //     Container(
                                        //       height: Constant.EXTRA_LARGE_PADDING,
                                        //       width: 1,
                                        //       color: AppTheme.lable_noramal,
                                        //     ),
                                        //     Expanded(
                                        //       child: CustomText(
                                        //         title: Strings.action,
                                        //         textAlign: TextAlign.center,
                                        //         colors: AppTheme.lable_noramal,
                                        //         fontSize: AppTheme.small,
                                        //         fontWeight: FontWeight.w500,
                                        //       ),
                                        //     ),
                                        //   ],
                                        // ),
                                        // Flexible(
                                        //     child: ListView.builder(
                                        //   shrinkWrap: true,
                                        //   primary: false,
                                        //   itemCount: inventoryReplaceController
                                        //       .replacementMacAddressList.length,
                                        //   itemBuilder: (context, index) {
                                        //     ReplacementMacAddressList item =
                                        //         inventoryReplaceController
                                        //                 .replacementMacAddressList[
                                        //             index];
                                        //     item.macAddressValue = item.macAddress;
                                        //     item.serialNumberValue = item.serialNumber;
                                        //     return Column(
                                        //       children: [
                                        //         InkWell(
                                        //           onTap: () {
                                        //             for (var f
                                        //                 in inventoryReplaceController
                                        //                     .replacementMacAddressList) {
                                        //               if (f.id == item.id) {
                                        //                 f.selected =
                                        //                     !f.selected!;
                                        //                 item.macAddressValue =
                                        //                     item.macAddress;
                                        //                 item.serialNumberValue =
                                        //                     item.serialNumber;
                                        //                 inventoryReplaceController
                                        //                         .newMacMappingId =
                                        //                     item.id;
                                        //               } else {
                                        //                 f.selected = false;
                                        //               }
                                        //             }
                                        //             inventoryReplaceController
                                        //                 .update();
                                        //           },
                                        //           child: Padding(
                                        //             padding: const EdgeInsets
                                        //                     .symmetric(
                                        //                 vertical: Constant
                                        //                         .SMALL_PADDING +
                                        //                     1,
                                        //                 horizontal: Constant
                                        //                     .MEDIUM_PADDING),
                                        //             child: Row(
                                        //               children: [
                                        //                 item.selected == true
                                        //                     ? Icon(
                                        //                         Icons
                                        //                             .check_circle,
                                        //                         color: AppTheme
                                        //                             .colorPrimary,
                                        //                         size: Constant
                                        //                             .ICON_SIZE,
                                        //                       )
                                        //                     : Icon(
                                        //                         Icons
                                        //                             .radio_button_off,
                                        //                         color: AppTheme
                                        //                             .lable_noramal,
                                        //                         size: Constant
                                        //                             .ICON_SIZE,
                                        //                       ),
                                        //                 const SizedBox(
                                        //                   width: Constant
                                        //                       .SMALL_PADDING,
                                        //                 ),
                                        //                 Expanded(
                                        //                   child: CustomText(
                                        //                     title:
                                        //                         "${item.itemId}",
                                        //                     textAlign:
                                        //                         TextAlign.start,
                                        //                     colors: item.selected ==
                                        //                             true
                                        //                         ? AppTheme
                                        //                             .colorPrimary
                                        //                         : AppTheme
                                        //                             .lable_noramal,
                                        //                     fontSize:
                                        //                         AppTheme.small +
                                        //                             1,
                                        //                     fontWeight: item
                                        //                                 .selected ==
                                        //                             true
                                        //                         ? FontWeight
                                        //                             .w500
                                        //                         : FontWeight
                                        //                             .w700,
                                        //                   ),
                                        //                 ),
                                        //                 Expanded(
                                        //                   flex: 1,
                                        //                   child: CustomText(
                                        //                     title:
                                        //                         item.condition!,
                                        //                     textAlign:
                                        //                         TextAlign.start,
                                        //                     colors: item.selected ==
                                        //                             true
                                        //                         ? AppTheme
                                        //                             .colorPrimary
                                        //                         : AppTheme
                                        //                             .lable_noramal,
                                        //                     fontSize:
                                        //                         AppTheme.small +
                                        //                             1,
                                        //                     fontWeight: item
                                        //                                 .selected ==
                                        //                             true
                                        //                         ? FontWeight
                                        //                             .w500
                                        //                         : FontWeight
                                        //                             .w700,
                                        //                   ),
                                        //                 ),
                                        //                 Expanded(
                                        //                   child:
                                        //                       item.selected ==
                                        //                               false
                                        //                           ? CustomText(
                                        //                               title: item
                                        //                                       .macAddress ??
                                        //                                   "-",
                                        //                               textAlign:
                                        //                                   TextAlign
                                        //                                       .center,
                                        //                               colors: item.selected ==
                                        //                                       true
                                        //                                   ? AppTheme
                                        //                                       .colorPrimary
                                        //                                   : AppTheme
                                        //                                       .lable_noramal,
                                        //                               fontSize:
                                        //                                   AppTheme.small +
                                        //                                       1,
                                        //                               fontWeight: item.selected ==
                                        //                                       true
                                        //                                   ? FontWeight
                                        //                                       .w300
                                        //                                   : FontWeight
                                        //                                       .w500,
                                        //                             )
                                        //                           : Container(
                                        //                               margin: const EdgeInsets
                                        //                                       .only(
                                        //                                   right:
                                        //                                       Constant.VERY_SMALL_PADDING),
                                        //                               child:
                                        //                                   TextFormField(
                                        //                                 // key: Key(item.id.toString()),
                                        //                                 initialValue: item.macAddress != null &&
                                        //                                         item.macAddress!.isNotEmpty
                                        //                                     ? item.macAddress
                                        //                                     : "",
                                        //                                 textAlign:
                                        //                                     TextAlign.start,
                                        //                                 textAlignVertical:
                                        //                                     TextAlignVertical.center,
                                        //                                 style:
                                        //                                     TextStyle(
                                        //                                   color:
                                        //                                       AppTheme.title_dark,
                                        //                                   fontSize:
                                        //                                       AppTheme.small,
                                        //                                   fontWeight:
                                        //                                       FontWeight.w500,
                                        //                                   height:
                                        //                                       1,
                                        //                                   fontFamily:
                                        //                                       AppTheme.appFontName,
                                        //                                   decoration:
                                        //                                       TextDecoration.none,
                                        //                                 ),
                                        //                                 decoration: InputDecoration(
                                        //                                     counterText: "",
                                        //                                     border: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                                        //                                     ),
                                        //                                     focusColor: Colors.amberAccent,
                                        //                                     focusedBorder: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
                                        //                                     ),
                                        //                                     enabledBorder: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(
                                        //                                         color: AppTheme.colorIconGrey,
                                        //                                         width: 0.8,
                                        //                                       ),
                                        //                                     ),
                                        //                                     contentPadding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING),
                                        //                                     hintText: Strings.mac_address,
                                        //                                     alignLabelWithHint: true,
                                        //                                     fillColor: AppTheme.colorWhite,
                                        //                                     hoverColor: AppTheme.colorWhite),
                                        //                                 textInputAction:
                                        //                                     TextInputAction.next,
                                        //                                 keyboardType:
                                        //                                     TextInputType.text,
                                        //                                 maxLines:
                                        //                                     1,
                                        //                                 onChanged:
                                        //                                     (value) {
                                        //                                   // log("macAddress >>${value}");
                                        //                                   item.macAddressValue =
                                        //                                       value;
                                        //                                   item.macAddress =
                                        //                                       value;
                                        //                                   inventoryReplaceController
                                        //                                       .update();
                                        //                                 },
                                        //                               ),
                                        //                             ),
                                        //                 ),
                                        //                 Expanded(
                                        //                   child:
                                        //                       item.selected ==
                                        //                               false
                                        //                           ? CustomText(
                                        //                               title: item
                                        //                                   .serialNumber!,
                                        //                               textAlign:
                                        //                                   TextAlign
                                        //                                       .center,
                                        //                               colors: item.selected ==
                                        //                                       true
                                        //                                   ? AppTheme
                                        //                                       .colorPrimary
                                        //                                   : AppTheme
                                        //                                       .lable_noramal,
                                        //                               fontSize:
                                        //                                   AppTheme.small +
                                        //                                       1,
                                        //                               fontWeight: item.selected ==
                                        //                                       true
                                        //                                   ? FontWeight
                                        //                                       .w300
                                        //                                   : FontWeight
                                        //                                       .w500,
                                        //                             )
                                        //                           : Container(
                                        //                               margin: const EdgeInsets
                                        //                                       .only(
                                        //                                   left:
                                        //                                       Constant.VERY_SMALL_PADDING),
                                        //                               child:
                                        //                                   TextFormField(
                                        //                                 // key: Key(item.id.toString()),
                                        //                                 initialValue: item.serialNumber != null &&
                                        //                                         item.serialNumber!.isNotEmpty
                                        //                                     ? item.serialNumber
                                        //                                     : "-",
                                        //                                 textAlign:
                                        //                                     TextAlign.start,
                                        //                                 textAlignVertical:
                                        //                                     TextAlignVertical.center,
                                        //                                 style:
                                        //                                     TextStyle(
                                        //                                   color:
                                        //                                       AppTheme.title_dark,
                                        //                                   fontSize:
                                        //                                       AppTheme.small,
                                        //                                   fontWeight:
                                        //                                       FontWeight.w500,
                                        //                                   height:
                                        //                                       1,
                                        //                                   fontFamily:
                                        //                                       AppTheme.appFontName,
                                        //                                   decoration:
                                        //                                       TextDecoration.none,
                                        //                                 ),
                                        //                                 decoration: InputDecoration(
                                        //                                     counterText: "",
                                        //                                     border: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(color: AppTheme.colorPrimary, width: 1.0),
                                        //                                     ),
                                        //                                     focusColor: Colors.amberAccent,
                                        //                                     focusedBorder: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
                                        //                                     ),
                                        //                                     enabledBorder: OutlineInputBorder(
                                        //                                       borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                                        //                                       borderSide: BorderSide(
                                        //                                         color: AppTheme.colorIconGrey,
                                        //                                         width: 0.8,
                                        //                                       ),
                                        //                                     ),
                                        //                                     contentPadding: const EdgeInsets.symmetric(horizontal: Constant.VERY_SMALL_PADDING),
                                        //                                     hintText: Strings.serial_no,
                                        //                                     alignLabelWithHint: true,
                                        //                                     fillColor: AppTheme.colorWhite,
                                        //                                     hoverColor: AppTheme.colorWhite),
                                        //                                 textInputAction:
                                        //                                     TextInputAction.done,
                                        //                                 keyboardType:
                                        //                                     TextInputType.text,
                                        //                                 maxLines:
                                        //                                     1,
                                        //                                 onChanged:
                                        //                                     (value) {
                                        //                                   item.serialNumberValue =
                                        //                                       value;
                                        //                                   item.serialNumber =
                                        //                                       value;
                                        //                                   inventoryReplaceController
                                        //                                       .update();
                                        //                                 },
                                        //                               ),
                                        //                             ),
                                        //                 ),
                                        //                 Expanded(
                                        //                     child: InkWell(
                                        //                   onTap: () {
                                        //                     if (item.selected ==
                                        //                         true) {
                                        //                       inventoryReplaceController
                                        //                           .updateMacAndSerialNumber(
                                        //                               item.itemId,
                                        //                               item.serialNumber,
                                        //                               item.macAddress);
                                        //                       inventoryReplaceController
                                        //                           .update();
                                        //                     }
                                        //                   },
                                        //                   child: Container(
                                        //                     margin: const EdgeInsets
                                        //                             .all(
                                        //                         Constant
                                        //                             .SMALL_PADDING),
                                        //                     child: Material(
                                        //                       elevation: 1,
                                        //                       color: item
                                        //                                   .selected ==
                                        //                               false
                                        //                           ? AppTheme
                                        //                               .custEditLight
                                        //                           : AppTheme
                                        //                               .colorAccent,
                                        //                       shape: RoundedRectangleBorder(
                                        //                           borderRadius:
                                        //                               BorderRadius.circular(
                                        //                                   Constant
                                        //                                       .BTN_ROUNDED_CORNER)),
                                        //                       child: Container(
                                        //                         height: Constant
                                        //                                 .BTN_HEIGHT_M -
                                        //                             5,
                                        //                         width: Constant
                                        //                                 .BTN_HEIGHT_M -
                                        //                             20,
                                        //                         alignment:
                                        //                             Alignment
                                        //                                 .center,
                                        //                         padding: const EdgeInsets
                                        //                                 .all(
                                        //                             Constant
                                        //                                 .SMALL_PADDING),
                                        //                         child:
                                        //                             SvgPicture
                                        //                                 .asset(
                                        //                           editSvg,
                                        //                           height: Constant
                                        //                               .ICON_SIZE,
                                        //                           width: Constant
                                        //                               .ICON_SIZE,
                                        //                           color: item
                                        //                                       .selected ==
                                        //                                   false
                                        //                               ? AppTheme
                                        //                                   .colorIconGrey
                                        //                               : AppTheme
                                        //                                   .colorWhite,
                                        //                         ),
                                        //                       ),
                                        //                     ),
                                        //                   ),
                                        //                 )),
                                        //               ],
                                        //             ),
                                        //           ),
                                        //         ),
                                        //         index ==
                                        //                 (inventoryReplaceController
                                        //                         .productTypeDataList!
                                        //                         .length -
                                        //                     1)
                                        //             ? Container()
                                        //             : Padding(
                                        //                 padding: const EdgeInsets
                                        //                         .symmetric(
                                        //                     horizontal: Constant
                                        //                             .SCREEN_PADDING -
                                        //                         5),
                                        //                 child: Divider(
                                        //                   height: 5,
                                        //                   color: AppTheme
                                        //                       .lable_noramal,
                                        //                   thickness: 0.1,
                                        //                 ),
                                        //               ),
                                        //       ],
                                        //     );
                                        //   },
                                        // )),



                                      ]),
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            CustomText(
                              title: Strings.mac_mpappping_list,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.normal,
                            ),
                            InkWell(
                              onTap: () {
                                showMacAddressDialog();
                              },
                              child: CustomText(
                                title: Strings.add_mac_address,
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.normal,
                              ),
                            ),
                          ],
                        ),*/

/*___________________ Replacement Date_______________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.replacement_date, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.replacement_date,
                            suffixIcon: Padding(
                              padding: const EdgeInsetsDirectional.all(
                                  Constant.MEDIUM_PADDING),
                              child: SvgPicture.asset(
                                calendarSvg,
                                color: AppTheme.colorBlack,
                                width: Constant.ICON_SIZE_S,
                                height: Constant.ICON_SIZE_S,
                                // myIcon is a 48px-wide widget.
                              ),
                            ),
                            textEditingController: inventoryReplaceController
                                .outwardDateController,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.please_select_inward_date;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              selectDate(
                                  Strings.inward_date,
                                  DateTime(DateTime.now().year - 10),
                                  DateTime(DateTime.now().year + 10));
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: true),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            Row(
              children: [
                inventoryReplaceController.replacementMacAddressList.isNotEmpty? Expanded(
                  child: SimpleButton(
                    onTap: () {
                      validateForm();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.submit,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ) : Expanded(
                  child: SimpleButton(
                    onTap: (){},
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorDisableGray,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.submit,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.inward_date) {
      if (inventoryReplaceController.selectedInwordDateTime != null) {
        selectedDate = inventoryReplaceController.selectedInwordDateTime;
      } else {
        selectedDate = DateTime.now();
      }
    }

    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null) {
      if (identity == Strings.inward_date) {
        inventoryReplaceController.selectedInwordDateTime = picked;
        inventoryReplaceController.update();
        _selectDateTime();
      }
    }
  }

  Future<void> _selectDateTime() async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );

    if (picked != null) {
      DateTime dt = DateTime(
        inventoryReplaceController.selectedInwordDateTime!.year,
        inventoryReplaceController.selectedInwordDateTime!.month,
        inventoryReplaceController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      inventoryReplaceController.outwardDateController.text =
          inventoryReplaceController.dateFormat.format(dt);
      inventoryReplaceController.replacementDateTime =
          inventoryReplaceController.apiDateTimeFormat.format(dt);
      inventoryReplaceController.update();
    }
  }



  showMacAddressDialog() {
    Get.to(() => InventoryMacAddressListScreen(
      macAddressAction: this,
      macAddressLst: inventoryReplaceController.replacementMacAddressList!,
    ));
  }

  _appBar() {
    return DynamicAppBar(
        "${Strings.replacement} ${Strings.inventory}",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (inventoryReplaceFormKey.currentState!.validate()) {
      inventoryReplaceController.replaceInventoryCustomer();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

// showMacAddressDialog() {
//   showDialog(
//       context: scaffoldKey.currentContext!,
//       barrierDismissible: true,
//       builder: (BuildContext context) {
//         return SelectMacAddressDialog(
//             macAddressAction: this,
//             macAddressLst: inventoryReplaceController.productMacAddressList!,
//             controller: inventoryReplaceController);
//       });
// }
//
// @override
// void selectMacAddressBtnAction({List<ProductMacDataList>? selectedItem}) {
//   Get.back();
//   if (selectedItem != null) {
//     inventoryReplaceController.selectedMacAddressList!.clear();
//     inventoryReplaceController.selectedMacAddressList!.addAll(selectedItem);
//     inventoryReplaceController.availableQtyPics =
//         inventoryReplaceController.selectedMacAddressList!.length;
//     String macAdd = "";
//     for (int i = 0; i < selectedItem.length; i++) {
//       ProductMacDataList element = selectedItem[i];
//       inventoryReplaceController.productMacAddressData = element;
//       if (i == selectedItem.length - 1) {
//         if (element.macAddress != null) {
//           macAdd =
//               "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!} - ${element.macAddress!}";
//         } else {
//           macAdd =
//               "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!}";
//         }
//       } else {
//         macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}, ";
//       }
//     }
//     inventoryReplaceController.assignMacController.text = macAdd;
//     inventoryReplaceController.update();
//   }
// }
//
  @override
  void selectInventoryMacAddressBtnAction({List<ReplacementMacAddressList>? selectedItem}) {
    Get.back();
    if (selectedItem != null) {
      inventoryReplaceController.selectedMacAddressList!.clear();
      inventoryReplaceController.selectedMacAddressList!.addAll(selectedItem);
      inventoryReplaceController.availableQtyPics =
          inventoryReplaceController.selectedMacAddressList!.length;
      String macAdd = "";
      for (int i = 0; i < selectedItem.length; i++) {
        ReplacementMacAddressList element = selectedItem[i];
        inventoryReplaceController.productMacAddressData = element;
        inventoryReplaceController.macItemId = element.itemId;
        if (i == selectedItem.length - 1) {
          if (element.macAddress != null) {
            macAdd =
            "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!} - ${element.macAddress!}";
          } else {
            macAdd =
            "$macAdd${element.itemId!} - ${element.condition!} - ${element.serialNumber!}";
          }
        } else {
          macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}, ";
        }
        inventoryReplaceController.newMacMappingId = element.id;
      }

      inventoryReplaceController.assignMacController.text = macAdd;
      inventoryReplaceController.update();
    }
  }

  // @override
  // void btnClickAction({String? btnIdentifier}) {
  //   Get.back();
  //   if (btnIdentifier!.equalsIgnoreCase(Strings.try_again)) {
  //     getCurrentPosition(false);
  //   } else if (btnIdentifier.equalsIgnoreCase(Strings.location_settings)) {
  //     geolocatorPlatform.openLocationSettings();
  //   } else if (btnIdentifier
  //       .equalsIgnoreCase(Strings.app_permission_settings)) {
  //     geolocatorPlatform.openAppSettings();
  //   }
  // }


}
