import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/product_plan_service_inventory_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_inventory/other_inventory_controller.dart';
import 'package:savbill/pages/customer_inventory/response/product_non_trackable_product_category_res.dart';
import 'package:savbill/pages/customer_inventory/show_mac_address_screen.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
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
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';


class OtherInventory extends StatefulWidget {
  @override
  _OtherInventoryState createState() => _OtherInventoryState();
}

class _OtherInventoryState extends State<OtherInventory>
    with WidgetsBindingObserver
    implements SelectMacAddressAction, LocationBtnAction {
  final otherInventoryController = Get.put(OtherInventoryController());
  final otherInventoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    otherInventoryController.setBtnClickEvent(false);
    super.dispose();
  }

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
      child: GetBuilder<OtherInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: otherInventoryController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(OtherInventoryController controller) {
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
                    key: otherInventoryFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        /*__________________ Service ____________________*/

                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.service, require: true),
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
                                Strings.service,
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
                            value: controller.selectedPlanService,
                            items: controller.planServiceList
                                ?.map((CustomerPlanServiceDetail value) {
                              return DropdownMenuItem<
                                  CustomerPlanServiceDetail>(
                                value: value,
                                child: Text(value.service!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              controller.selectedPlanService =
                                  value as CustomerPlanServiceDetail?;
                              controller.selectServices = value.toString();
                              if (value!.connectionNo != null ||
                                  value.connectionNo!.isNotEmpty) {
                                controller.connectionNumberController.text =
                                    value.connectionNo!;
                              } else {
                                controller.connectionNumberController.text =
                                    "--";
                              }
                              controller.serviceVisible = true;
                              controller
                                  .getProductInventoryList(value.serviceId);
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectedPlanService == null) {
                                return Strings.select_service;
                              }
                              return null;
                            },
                          ),
                        ),

                        /*__________________ Connection No ____________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.connection_no, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? CoustomTextField(
                                labelText: Strings.connection_no,
                                textEditingController:
                                    controller.connectionNumberController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorGrey,
                                textInputAction: TextInputAction.next,
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),

                        /*__________________ Item Type_________________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.item_type, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.item_type,
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
                                  value: controller.selectedItemType,
                                  items: controller.itemTypeList
                                      ?.map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedItemType =
                                        value as DropdownDetail?;
                                    if (value!.text!.equalsIgnoreCase(
                                        Strings.serialized_item)) {
                                      controller.productTrackableDataList!
                                          .clear();
                                      controller.serializedItemVisible = true;
                                      controller.nonSerializedQtyVisible =
                                          false;
                                    } else {
                                      controller.serializedItemVisible = false;
                                      controller.nonSerializedQtyVisible = true;
                                      controller
                                          .getAllProductForNonTrackableProductCategory();
                                    }
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller.selectedItemType == null) {
                                      return Strings.select_item_type;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*____________ product List by non-serialized Item ________*/

                        controller.serializedItemVisible == false
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == false
                            ? InputTitleRequire(
                                title: Strings.product, require: true)
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == false
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == false
                            ? DropdownButtonHideUnderline(
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
                                  value: controller
                                      .selectedProductTrackableDataService,
                                  items: controller.productTrackableDataList!
                                      .map((ProductTrackableDataList value) {
                                    return DropdownMenuItem<
                                        ProductTrackableDataList>(
                                      value: value,
                                      child: Text(value.name),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.nonTrackableProductDataList
                                        .clear();

                                    controller
                                            .selectedProductTrackableDataService =
                                        value as ProductTrackableDataList?;
                                    controller.getNonTrackableProductQtyApi(
                                        value!.id!);

                                    controller.selectProductUnit =
                                        value.productCategory!.unit;
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        otherInventoryController
                                                .selectedProductTrackableDataService ==
                                            null) {
                                      return Strings.select_product;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                    child: InputTitleRequire(
                                        title: Strings.available_quantity,
                                        require: false),
                                  ),
                                  Expanded(
                                    flex: 2,
                                    child: InputTitleRequire(
                                        title: Strings.assign_qty,
                                        require: true),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: controller.qtyValue ?? 0,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.medium,
                                      fontWeight: FontWeight.w400,
                                      colors: AppTheme.colorBlack,
                                    ),
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CoustomTextField(
                                        labelText: Strings.enter_quantity,
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        // textEditingController: controller.qtyPicsController,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_quantity;
                                          }
                                          return null;
                                        },
                                        onChanged: (value) {
                                          if (int.parse(value) >
                                              controller.qtyValue!) {
                                            Utils.showSnackbar(
                                                Strings.ERROR,
                                                Strings
                                                    .assign_quantity_less_than_value_msg,
                                                AppTheme.colorWhite,
                                                AppTheme.colorRed);
                                          } else if (int.parse(value) <= 0) {
                                            Utils.showSnackbar(
                                                Strings.ERROR,
                                                Strings
                                                    .assign_quantity_greater_zero,
                                                AppTheme.colorWhite,
                                                AppTheme.colorRed);
                                          } else {
                                            controller.qtyPicsController.text =
                                                value;
                                          }
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: controller.qtyValue == 0
                                            ? true
                                            : false),
                                  ),
                                  Expanded(
                                    flex: 1,
                                    child: CustomText(
                                      title: controller.selectProductUnit,
                                      textAlign: TextAlign.center,
                                      fontSize: AppTheme.medium,
                                      fontWeight: FontWeight.w400,
                                      colors: AppTheme.colorBlack,
                                    ),
                                  )
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*____________ Bill to & Discount ______________________*/

                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.bill_to, require: false),
                                  ),
                                  const SizedBox(width: Constant.LARGE_PADDING),
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.discount_without,
                                        require: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                      flex: 1,
                                      child: DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.bill_to,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value:
                                              controller.selectedBillToDetail,
                                          items: controller.billToList
                                              ?.map((DropdownDetail value) {
                                            return DropdownMenuItem<
                                                DropdownDetail>(
                                              value: value,
                                              child: Text(
                                                  value.text!.toUpperCase()),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            controller.selectedBillToDetail =
                                                value as DropdownDetail?;
                                            controller.update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                controller
                                                        .selectedBillToDetail ==
                                                    null) {
                                              return Strings
                                                  .please_select_bill_to;
                                            }
                                            return null;
                                          },
                                        ),
                                      )),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: '0',
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            controller.discountController,
                                        onTextValidator: (String? value) {
                                          /*  if (value!.isEmpty) {
                                            return Strings.enter_quantity;
                                          }*/
                                          return null;
                                        },
                                        onChanged: (value) {},
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: true),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*____________ Old offer Price & New Offer Price _______________*/

                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.old_offer_price,
                                        require: false),
                                  ),
                                  const SizedBox(width: Constant.LARGE_PADDING),
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.new_offer_price,
                                        require: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == true
                            ? Row(
                                children: [
                                  Expanded(
                                      flex: 1,
                                      child: CoustomTextField(
                                          labelText: Strings.old_offer_price,
                                          keyboardType: TextInputType.phone,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController: controller
                                              .oldOfferPriceController!,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings.enter_old_price;
                                            }
                                            return null;
                                          },
                                          onChanged: (value) {},
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING),
                                          readOnly: true)),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.new_offer_price,
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            controller.newOfferPriceController,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_new_price;
                                          }
                                          return null;
                                        },
                                        onChanged: (value) {},
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: true),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

                        /*__________________Assembly Type_________________________*/

                        controller.serializedItemVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == true
                            ? InputTitleRequire(
                                title: Strings.assembly_type, require: true)
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serializedItemVisible == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.assembly_type,
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
                                  value: controller.selectedAssemblyType,
                                  items: controller.assemblyTypeList
                                      ?.map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedAssemblyType =
                                        value as DropdownDetail?;
                                    if (value!.text!.equalsIgnoreCase(
                                        Strings.single_item_type)) {
                                      controller.productItemVisible = true;
                                      controller.productConditionType = true;
                                      controller.assemblyOldNewOfferPriceFlag =
                                          true;
                                    } else {
                                      controller.productItemVisible = false;
                                      controller.productConditionType = false;
                                      controller.assemblyOldNewOfferPriceFlag =
                                          true;
                                    }
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller.selectedAssemblyType ==
                                            null) {
                                      return Strings.select_assembly_type;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Condition Type_________________________*/

                        controller.productConditionType == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productConditionType == true
                            ? InputTitleRequire(
                                title: Strings.condition_type, require: true)
                            : const SizedBox.shrink(),
                        controller.productConditionType == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productConditionType == true
                            ? DropdownButtonHideUnderline(
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
                                      Strings.condition_type,
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
                                  value: controller.selectedConditionType,
                                  items: controller.conditionTypeList
                                      ?.map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedConditionType =
                                        value as DropdownDetail?;
                                    if (value!.text!
                                        .equalsIgnoreCase(Strings.key_new)) {
                                      controller.productItemVisible = true;
                                    } else {
                                      controller.productItemVisible = false;
                                    }
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller.selectedAssemblyType ==
                                            null) {
                                      return Strings.select_condition_type;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Product _________________________*/

                        controller.productItemVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productItemVisible == true
                            ? InputTitleRequire(
                                title: Strings.product, require: true)
                            : const SizedBox.shrink(),
                        controller.productItemVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productItemVisible == true
                            ? DropdownButtonHideUnderline(
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
                                  value: controller.selectedProductPlanService,
                                  items: controller.productPlanServiceList?.map(
                                      (ProductInventoryServiceList value) {
                                    return DropdownMenuItem<
                                        ProductInventoryServiceList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedProductPlanService =
                                        value as ProductInventoryServiceList?;
                                    controller.oldOfferPriceController.clear();
                                    controller.newOfferPriceController.clear();
                                    controller.assignMacController.text = "";
                                    controller.oldOfferPriceController.text =
                                        value!.newProductAmount.toString();
                                    controller.newOfferPriceController.text =
                                        value.newProductAmount.toString();
                                    controller.oldOfferAndNewOfferPriceFlag =
                                        true;
                                    controller.productId = value.id;
                                    controller
                                        .getProductMacAddressData(value.id!);
                                    controller.update();
                                    // otherInventoryController.getInwardsData();
                                    // controller.getProductMacAddressData();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        otherInventoryController
                                                .selectedProductPlanService ==
                                            null) {
                                      return Strings.select_product;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*____________ Mac Address & Serial Number ______________*/
                        controller.productMacAddressList!.isNotEmpty
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productMacAddressList!.isNotEmpty
                            ? InputTitleRequire(
                                title: Strings.mac_address, require: true)
                            : const SizedBox.shrink(),
                        controller.productMacAddressList!.isNotEmpty
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.productMacAddressList!.isNotEmpty
                            ? CoustomTextField(
                                labelText: Strings.assign_mac_address,
                                textEditingController: otherInventoryController
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
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),

/*_________________billTo Serialized Item___________________*/

                        controller.nonSerializedQtyVisible == false
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == false
                            ? Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.bill_to, require: true),
                                  ),
                                  controller.selectedBillToDetail != null &&
                                          controller.selectedBillToDetail!.text!
                                              .equalsIgnoreCase(
                                                  Strings.subisu.toUpperCase())
                                      ? const SizedBox(
                                          width: Constant.MEDIUM_PADDING)
                                      : const SizedBox.shrink(),
                                  controller.selectedBillToDetail != null &&
                                          controller.selectedBillToDetail!.text!
                                              .equalsIgnoreCase(
                                                  Strings.subisu.toUpperCase())
                                      ? Expanded(
                                          flex: 1,
                                          child: InputTitleRequire(
                                              title: Strings.invoice_to_org,
                                              require: false),
                                        )
                                      : const SizedBox.shrink(),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == false
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.nonSerializedQtyVisible == false
                            ? Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: DropdownButtonHideUnderline(
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
                                            Strings.bill_to,
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
                                        value: controller.selectedBillToDetail,
                                        items: controller.billToList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child:
                                                Text(value.text!.toUpperCase()),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          controller.selectedBillToDetail =
                                              value as DropdownDetail?;
                                          controller.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              controller.selectedBillToDetail ==
                                                  null) {
                                            return Strings
                                                .please_select_bill_to;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                  controller.selectedBillToDetail != null &&
                                          controller.selectedBillToDetail!.text!
                                              .equalsIgnoreCase(
                                                  Strings.subisu.toUpperCase())
                                      ? const SizedBox(
                                          width: Constant.MEDIUM_PADDING,
                                        )
                                      : const SizedBox.shrink(),
                                  controller.selectedBillToDetail != null &&
                                          controller.selectedBillToDetail!.text!
                                              .equalsIgnoreCase(
                                                  Strings.subisu.toUpperCase())
                                      ? Expanded(
                                          flex: 1,
                                          child: DropdownButtonHideUnderline(
                                            child: DropdownButtonFormField(
                                              icon: SvgPicture.asset(
                                                downArrowSvg,
                                                height: Constant
                                                    .DROP_DOWN_ARROW_W_H,
                                                width: Constant
                                                    .DROP_DOWN_ARROW_W_H,
                                                color: AppTheme.colorBlack,
                                                fit: BoxFit.fill,
                                              ),
                                              decoration: Utils.ddlDecoration(),
                                              hint: Align(
                                                alignment: Alignment.centerLeft,
                                                child: Text(
                                                  Strings.invoice_to_org,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ),
                                                ),
                                              ),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: controller
                                                  .selectedInvoiceToOrg,
                                              items: controller.invoiceToOrgList
                                                  ?.map((DropdownDetail value) {
                                                return DropdownMenuItem<
                                                    DropdownDetail>(
                                                  value: value,
                                                  child: Text(value.text!
                                                      .toUpperCase()),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                controller
                                                        .selectedInvoiceToOrg =
                                                    value as DropdownDetail?;
                                                controller.update();
                                              },
                                              validator: (value) {
                                                return null;
                                              },
                                            ),
                                          ),
                                        )
                                      : const SizedBox.shrink(),
                                ],
                              )
                            : const SizedBox.shrink(),

/*_______________ Old  & New Offer Price ______________*/

                        controller.oldOfferAndNewOfferPriceFlag == true &&
                                controller.nonSerializedQtyVisible == false
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true &&
                                controller.nonSerializedQtyVisible == false
                            ? Row(
                                children: [
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.old_offer_price,
                                        require: false),
                                  ),
                                  const SizedBox(width: Constant.LARGE_PADDING),
                                  Expanded(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.new_offer_price,
                                        require: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true &&
                                controller.nonSerializedQtyVisible == false
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.oldOfferAndNewOfferPriceFlag == true &&
                                controller.nonSerializedQtyVisible == false
                            ? Row(
                                children: [
                                  Expanded(
                                      flex: 1,
                                      child: CoustomTextField(
                                          labelText: Strings.old_offer_price,
                                          keyboardType: TextInputType.phone,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          borderFocusColors:
                                              AppTheme.colorLightGrey,
                                          fillColor: AppTheme.colorLightGrey,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController: controller
                                              .oldOfferPriceController,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings.enter_old_price;
                                            }
                                            return null;
                                          },
                                          onChanged: (value) {},
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING),
                                          readOnly: true)),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.new_offer_price,
                                        keyboardType: TextInputType.phone,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            controller.newOfferPriceController,
                                        onTextValidator: (String? value) {
                                          log("onChanged==>>$value");
                                          if (value!.isEmpty) {
                                            return Strings.enter_new_price;
                                          }
                                          return null;
                                        },
                                        onChanged: (String? value) {
                                          if (controller
                                                  .selectedProductPlanService !=
                                              null) {
                                            double? oldPriceAmount =
                                                double.parse(controller
                                                    .selectedProductPlanService!
                                                    .newProductAmount
                                                    .toString());
                                            if (double.parse(value.toString()) >
                                                oldPriceAmount) {
                                              Utils.showSnackbar(
                                                  Strings.INFO,
                                                  Strings.amountValidationMsg,
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorBlueRView);
                                            }
                                          }
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        readOnly: false),
                                  ),
                                ],
                              )
                            : const SizedBox.shrink(),

/*____________________ Billable To ____________________*/
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.billableTo, require: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.select_billable_to,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                otherInventoryController.billableToController,
                            suffixIcon: Padding(
                              padding: const EdgeInsetsDirectional.all(
                                  Constant.LARGE_PADDING - 2),
                              child: SvgPicture.asset(
                                downArrowSvg,
                                color: AppTheme.colorBlack,
                                width: Constant.ICON_SIZE_S,
                                height: Constant.ICON_SIZE_S,
                              ),
                            ),
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            keyboardType: TextInputType.text,
                            fontSize: AppTheme.small,
                            textInputAction: TextInputAction.done,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              // if (controller
                              //     .billableToController.text.isEmpty) {
                              //   return Strings.select_bill_to;
                              // }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              openParentCustomerScreen();
                            },
                            readOnly: true),

                        /*_______________ Assign Date ___________________________*/
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.assigned_date, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.assigned_date,
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
                            textEditingController:
                                otherInventoryController.outwardDateController,
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

                        /*______________ payment owner _______________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.payment_owner, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.select_staff,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                controller.paymentOwnerStaffController,
                            suffixIcon: Padding(
                              padding: const EdgeInsetsDirectional.all(
                                  Constant.LARGE_PADDING - 2),
                              child: SvgPicture.asset(
                                downArrowSvg,
                                color: AppTheme.colorBlack,
                                width: Constant.ICON_SIZE_S,
                                height: Constant.ICON_SIZE_S,
                              ),
                            ),
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            textInputAction: TextInputAction.done,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              if (controller
                                  .paymentOwnerStaffController.text.isEmpty) {
                                return Strings.select_payment_owner;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              // openParentCustomerScreen();
                              openParentStaffScreen();
                            },
                            readOnly: true),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),

                        /*__________________ Get Location _______________________*/

                        controller.latLongFlag == true
                            ? const SizedBox(
                                height: Constant.VERY_SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.latLongFlag == true
                            ? InputTitleRequire(
                                title: Strings.location, require: true)
                            : const SizedBox.shrink(),
                        controller.latLongFlag == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.latLongFlag == true
                            ? CoustomTextField(
                                labelText: Strings.get_location,
                                textEditingController:
                                    controller.latLonController,
                                keyboardType: TextInputType.text,
                                fontSize: AppTheme.small,
                                maxLines: 3,
                                textColor: AppTheme.colorBlack,
                                borderEnableColors: AppTheme.colorGrey,
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: true)
                            : const SizedBox.shrink(),
                        const SizedBox(
                          height: Constant.LARGE_PADDING,
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
            Row(
              children: [
                Expanded(
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
      if (otherInventoryController.selectedInwordDateTime != null) {
        selectedDate = otherInventoryController.selectedInwordDateTime;
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
        otherInventoryController.selectedInwordDateTime = picked;
        otherInventoryController.update();
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
        otherInventoryController.selectedInwordDateTime!.year,
        otherInventoryController.selectedInwordDateTime!.month,
        otherInventoryController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      otherInventoryController.outwardDateController.text =
          otherInventoryController.dateFormat.format(dt);
      otherInventoryController.inwardDateTime =
          otherInventoryController.apiDateTimeFormat.format(dt);
      otherInventoryController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.assign_other_inventory,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (otherInventoryFormKey.currentState!.validate()) {
      otherInventoryController.assignInventory();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  showMacAddressDialog() {
    // showDialog(
    //     context: scaffoldKey.currentContext!,
    //     barrierDismissible: true,
    //     builder: (BuildContext context) {
    //       return SelectMacAddressDialog(
    //           macAddressAction: this,
    //           macAddressLst: otherInventoryController.productMacAddressList!,
    //           controller: otherInventoryController);
    //     });
    Get.to(() => ShowMacAddressScreen(
          macAddressAction: this,
          macAddressLst: otherInventoryController.productMacAddressList!,
        ));
  }

  @override
  void selectMacAddressBtnAction({List<ProductMacDataList>? selectedItem}) {
    Get.back();
    if (selectedItem != null) {
      otherInventoryController.selectedMacAddressList!.clear();
      otherInventoryController.selectedMacAddressList!.addAll(selectedItem);
      otherInventoryController.availableQtyPics =
          otherInventoryController.selectedMacAddressList!.length;
      String macAdd = "";
      for (int i = 0; i < selectedItem.length; i++) {
        ProductMacDataList element = selectedItem[i];
        otherInventoryController.productMacAddressData = element;
        otherInventoryController.macItemId = element.itemId;
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
        otherInventoryController.selectedSerialNumber!.add(element.serialNumber!);
      }
      otherInventoryController.getAllInventorySpecByItemIdApiCall(otherInventoryController.macItemId);
      otherInventoryController.assignMacController.text = macAdd;
      otherInventoryController.update();
    }
  }

  locationPermissionStatus() async {
    if (Platform.isIOS) {
      getCurrentPosition(false);
    } else {
      PermissionService().requestLocationPermission(onPermissionSuccess: () {
        print("Location Service Permission approved");
        getCurrentPosition(false);
      }, onPermissionDenied: () async {
        print("Location Service Permission denied");
        getCurrentPosition(false);
      });
    }
  }

  getCurrentPosition(bool fromTryAgain) async {
    bool serviceEnabled = await checkLocationService();
    if (!serviceEnabled) {
      otherInventoryController.setBtnClickEvent(true);
      locationSettingsDialog(false, fromTryAgain);
      return false;
    }
    LocationPermission permission = await geolocatorPlatform.checkPermission();
    if (permission == LocationPermission.denied) {
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        otherInventoryController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }
    if (permission == LocationPermission.deniedForever) {
      // for app settings
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.deniedForever) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        otherInventoryController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    otherInventoryController.isLoading = true;
    otherInventoryController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) async {
      if (position != null) {
        otherInventoryController.setBtnClickEvent(false);
        otherInventoryController.isLoading = false;
        Position currentPosition = position;
        List<Placemark> placemarks = await placemarkFromCoordinates(
            currentPosition.latitude, currentPosition.longitude);
        Placemark place = placemarks[0];

        String? getFullAddress = "";

        if (place.street!.isNotEmpty) {
          getFullAddress = place.street;
        }
        if (place.subLocality!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.subLocality}";
        }
        if (place.thoroughfare!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.thoroughfare}";
        }

        if (place.subThoroughfare!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.subThoroughfare}";
        }

        if (place.locality!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.locality}";
        }

        if (place.administrativeArea!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.administrativeArea}";
        }

        if (place.subAdministrativeArea!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.subAdministrativeArea}";
        }

        if (place.country!.isNotEmpty) {
          getFullAddress = "$getFullAddress, ${place.country}";
        }

        getFullAddress = "$getFullAddress, ${place.postalCode}";

        otherInventoryController.latLonController.text =
            getFullAddress.toString();

        // otherInventoryController.latController.text = currentPosition.latitude.toString();
        // otherInventoryController.longController.text = currentPosition.longitude.toString();

        otherInventoryController.update();
      } else {
        otherInventoryController.isLoading = false;
        otherInventoryController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      otherInventoryController.isLoading = false;
      otherInventoryController.update();
      getCurrentPosition(false);
    });
  }

  Future<bool> checkLocationService() async {
    bool serviceEnabled;
    serviceEnabled = await geolocatorPlatform.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return false;
    } else {
      return true;
    }
  }

  locationSettingsDialog(bool isAppPermission, bool fromTryAgain) {
    if (!isAppPermission || fromTryAgain) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    } else if (isAppPermission && fromTryAgain) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    }
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.try_again)) {
      getCurrentPosition(false);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.location_settings)) {
      geolocatorPlatform.openLocationSettings();
    } else if (btnIdentifier
        .equalsIgnoreCase(Strings.app_permission_settings)) {
      geolocatorPlatform.openAppSettings();
    }
  }

  openParentCustomerScreen() async {
    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_TYPE: otherInventoryController.customerType!,
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        otherInventoryController.selectedParentCustomer = data;
        otherInventoryController.billableToController.text = data.name!;
        otherInventoryController.billableCustomerId = data.id;
        otherInventoryController.update();
      }
    }
  }

  openParentStaffScreen() async {
    var result = await Get.to(ParentStaffList(), arguments: {});
    if (result != null) {
      ParentStaffUserlist data = result;
      if (data != null) {
        otherInventoryController.selectedParentStaff = data;
        otherInventoryController.paymentOwnerStaffController.text =
            data.firstname!;
        otherInventoryController.paymentOwnerId = data.id;
        otherInventoryController.latLongFlag = false;
        // locationPermissionStatus();
        otherInventoryController.update();
      }
    }
  }
}
