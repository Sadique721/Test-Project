import 'package:savbill/pages/inventory/assigned_inventories/fulfilment/fulfilment_inventory_product_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/fulfilment/inventory_fulfilment_controller.dart';
import 'package:savbill/pages/inventory/module/response/active_staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
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

class InventoryFulFilMent extends StatefulWidget {
  @override
  _InventoryFulFilMentState createState() => _InventoryFulFilMentState();
}

class _InventoryFulFilMentState extends State<InventoryFulFilMent> {
  final inventoryFulFilMentController =
      Get.put(InventoryFulFilMentController());
  final inventoryFulFilMentFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<InventoryFulFilMentController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: inventoryFulFilMentController.isLoading),
        ]);
      }),
    );
  }

  _body() {
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
                      key: inventoryFulFilMentFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.inventory_request_name,
                              require: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              autovalidateMode: AutovalidateMode.disabled,
                              fillColor: AppTheme.colorGrayTxtBg,
                              labelText: Strings.select_inventory_request_name,
                              textEditingController:
                                  inventoryFulFilMentController
                                      .inventoryRequestNameController,
                              borderEnableColors: AppTheme.colorDisableGray,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.text,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              onChanged: (String? value) {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.source_type, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              autovalidateMode: AutovalidateMode.disabled,
                              fillColor: AppTheme.colorGrayTxtBg,
                              labelText: Strings.please_select_source_type,
                              textEditingController:
                                  inventoryFulFilMentController
                                      .inventorySourceTypeController,
                              borderEnableColors: AppTheme.colorDisableGray,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.text,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              onChanged: (String? value) {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.select_source, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              autovalidateMode: AutovalidateMode.disabled,
                              fillColor: AppTheme.colorGrayTxtBg,
                              labelText: Strings.select_source,
                              textEditingController:
                                  inventoryFulFilMentController
                                      .inventorySelectSourceController,
                              borderEnableColors: AppTheme.colorDisableGray,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.text,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              onChanged: (String? value) {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.destination_type, require: true),
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
                                  Strings.please_select_destination_type,
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
                              value: inventoryFulFilMentController
                                  .selectedDestinationType,
                              items: inventoryFulFilMentController
                                  .destinationTypeList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                inventoryFulFilMentController
                                        .selectedDestinationType =
                                    value as DropdownDetail?;
                                if (inventoryFulFilMentController
                                    .selectedDestinationType!.text!
                                    .equalsIgnoreCase(Strings.staff)) {
                                  inventoryFulFilMentController
                                      .getAllStaffUser();
                                } else {
                                  inventoryFulFilMentController
                                      .getAllActiveWareHouse();
                                }
                                inventoryFulFilMentController.update();

                                // inventoryFulFilMentController.getInwardsDetail();
                              },
                              validator: (value) {
                                if (value == null ||
                                    inventoryFulFilMentController
                                            .selectedDestinationType ==
                                        null) {
                                  return Strings.please_select_destination_type;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.select_destination, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          inventoryFulFilMentController
                                          .selectedDestinationType !=
                                      null &&
                                  inventoryFulFilMentController
                                      .selectedDestinationType!.text!
                                      .equalsIgnoreCase(Strings.staff)
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
                                        Strings.select_destination,
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
                                    value: inventoryFulFilMentController
                                        .selectedStaffUserData,
                                    items: inventoryFulFilMentController
                                        .staffUserList
                                        ?.map((StaffUserDataList value) {
                                      return DropdownMenuItem<
                                          StaffUserDataList>(
                                        value: value,
                                        child: Text(value.username!),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      inventoryFulFilMentController
                                              .selectedStaffUserData =
                                          value as StaffUserDataList?;
                                      inventoryFulFilMentController.update();
                                      // inventoryFulFilMentController.getInwardsDetail();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          inventoryFulFilMentController
                                                  .selectedStaffUserData ==
                                              null) {
                                        return Strings
                                            .please_select_destination;
                                      }
                                      return null;
                                    },
                                  ),
                                )
                              : DropdownButtonHideUnderline(
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
                                        Strings.select_destination,
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
                                    value: inventoryFulFilMentController
                                        .selectedWareHouseData,
                                    items: inventoryFulFilMentController
                                        .wareHouseList
                                        ?.map((WareHouseDetail value) {
                                      return DropdownMenuItem<WareHouseDetail>(
                                        value: value,
                                        child: Text(value.name!),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      inventoryFulFilMentController
                                              .selectedWareHouseData =
                                          value as WareHouseDetail?;
                                      inventoryFulFilMentController.update();
                                      // inventoryFulFilMentController.getInwardsDetail();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          inventoryFulFilMentController
                                                  .selectedWareHouseData ==
                                              null) {
                                        return Strings
                                            .please_select_destination;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                          Stack(
                            children: [
                              Container(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.MEDIUM_PADDING,
                                    horizontal: Constant.MEDIUM_PADDING),
                                margin: const EdgeInsets.only(
                                    top: Constant.EXPANTABLE_ITEM_MARGIN,
                                    bottom: Constant.SMALL_PADDING),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 0.8,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorGrey)),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    ListView.builder(
                                        scrollDirection: Axis.vertical,
                                        shrinkWrap: true,
                                        itemCount: inventoryFulFilMentController
                                            .fulfilmentProductMapping!.length,
                                        itemBuilder: (context, index) {
                                          FulfilmentProductMappings item =
                                              inventoryFulFilMentController
                                                      .fulfilmentProductMapping![
                                                  index];
                                          return FulfillmentInventoryReqProductItem(
                                            item: item,
                                            index: index,
                                            controller:
                                                inventoryFulFilMentController,
                                          );
                                        }),
                                    // : noDataFound(),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(3),
                                      color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.product_details,
                                      require: false),
                                ),
                              ),
                            ],
                          ),
                          /* IgnorePointer(
                            ignoring: inventoryFulFilMentController.isReadOnly,
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
                                    Strings.product,
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
                                value: inventoryFulFilMentController.selectedProduct,
                                items: inventoryFulFilMentController.fulfillmentData
                                    ?.map((FulfilmentData value) {
                                  return DropdownMenuItem<ProductDetail>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  inventoryFulFilMentController.selectedProduct =
                                  value as ProductDetail?;
                                  inventoryFulFilMentController.update();
                                  // inventoryFulFilMentController.getInwardsDetail();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      inventoryFulFilMentController
                                          .selectedProduct ==
                                          null) {
                                    return Strings.select_product;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.source_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: inventoryFulFilMentController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key: inventoryFulFilMentController.keySourceType,
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
                                    Strings.source_type,
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
                                value:
                                inventoryFulFilMentController.selectedSourceType,
                                items: inventoryFulFilMentController.sourceTypeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  inventoryFulFilMentController.selectedSourceType =
                                  value as DropdownDetail?;
                                  inventoryFulFilMentController.selectedSource =
                                  null;
                                  inventoryFulFilMentController.sourceList!.clear();
                                  inventoryFulFilMentController.update();
                                  inventoryFulFilMentController.manageSourceType();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      inventoryFulFilMentController
                                          .selectedSourceType ==
                                          null) {
                                    return Strings.please_select_source_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.source, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: inventoryFulFilMentController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key: inventoryFulFilMentController.keySource,
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
                                    Strings.source,
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
                                value: inventoryFulFilMentController.selectedSource,
                                items: inventoryFulFilMentController.sourceList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  inventoryFulFilMentController.selectedSource =
                                  value as DropdownDetail?;
                                  inventoryFulFilMentController.update();
                                  inventoryFulFilMentController
                                      .availableQtyProductDestination();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      inventoryFulFilMentController.selectedSource ==
                                          null) {
                                    return Strings.please_select_source;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          Row(
                            children: [
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              inventoryFulFilMentController
                                  .availableQtyProductList !=
                                  null
                                  ? CustomText(
                                title:
                                "${Strings.available_quantity} :- ${inventoryFulFilMentController.availableQty.toString()}",
                                colors: AppTheme.title_dark,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.small,
                                fontWeight: FontWeight.normal,
                              )
                                  : const SizedBox.shrink(),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                            ],
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),


                          InputTitleRequire(
                              title: Strings.destination_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key:
                                addEditOutwardController.keyDestinationType,
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
                                    Strings.destination_type,
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
                                value: addEditOutwardController
                                    .selectedDestinationType,
                                items: addEditOutwardController
                                    .destinationTypeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController
                                      .selectedDestinationType =
                                  value as DropdownDetail?;
                                  addEditOutwardController.update();
                                  addEditOutwardController.selectedDestination =
                                  null;
                                  addEditOutwardController.destinationList!
                                      .clear();
                                  addEditOutwardController
                                      .manageDestinationType();
                                  addEditOutwardController.update();
                                },
                                validator: (value) {
                                  if (addEditOutwardController.outwardDetail !=
                                      null &&
                                      (value == null ||
                                          addEditOutwardController
                                              .selectedDestinationType ==
                                              null)) {
                                    return Strings
                                        .please_select_destination_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.destination, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key: addEditOutwardController.keyDestination,
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
                                    Strings.destination,
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
                                value: addEditOutwardController
                                    .selectedDestination,
                                items: addEditOutwardController.destinationList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedDestination =
                                  value as DropdownDetail?;
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditOutwardController
                                          .selectedDestination ==
                                          null) {
                                    return Strings.please_select_destination;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          */ /*const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.quantity_detail, require: false),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.available_qty_inward} :- ${addEditOutwardController.selectedInward!.unusedQty} "
                                : "${Strings.available_qty_inward} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.available_qty_outward} :- ${addEditOutwardController.selectedInward!.inTransitQty} "
                                : "${Strings.available_qty_outward} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.used_qty} :- ${addEditOutwardController.selectedInward!.usedQty} "
                                : "${Strings.used_qty} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),*/ /*
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.quantity_in, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                            // autovalidateMode : addEditOutwardController.from.equalsIgnoreCase(Strings.add) ?
                            // AutovalidateMode.onUserInteraction :
                            // AutovalidateMode.disabled ,
                              labelText: Strings.quantity_in,
                              textEditingController:
                              addEditOutwardController.qtyController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.number,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_quantity_in;
                                } else {
                                  int enterQty;
                                  if(addEditOutwardController.from.equalsIgnoreCase(Strings.edit)){
                                    enterQty = int.parse(addEditOutwardController.qtyController.text);
                                  }else {
                                    enterQty = int.parse(value);
                                    if (enterQty <= 0 ||
                                        (enterQty >
                                            addEditOutwardController
                                                .availableQty!)) {
                                      return Strings.enter_valid_quantity;
                                    }
                                  }
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              onChanged: (String? value) {
                                if(value!.isNotEmpty) {
                                  int enterQty = int.parse(value.toString());
                                  if (enterQty <= 0 ||
                                      (enterQty >
                                          addEditOutwardController
                                              .availableQty!)) {
                                    return Strings.enter_valid_quantity;
                                  } else {
                                    return null;
                                  }
                                }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: addEditOutwardController.from.equalsIgnoreCase(Strings.edit) ? true : false),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.outward_date, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.outward_date,
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
                              textEditingController: addEditOutwardController
                                  .outwardDateController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_outward_date;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                if (addEditOutwardController.isReadOnly) {
                                  print("not editable");
                                } else {
                                  selectDate(
                                      Strings.outward_date,
                                      DateTime(DateTime.now().year - 10),
                                      DateTime(DateTime.now().year + 10));
                                }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.status, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                                  Strings.status,
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
                              value: addEditOutwardController.selectedStatus,
                              items: addEditOutwardController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditOutwardController.selectedStatus =
                                value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditOutwardController.selectedStatus ==
                                        null) {
                                  return Strings.please_select_status;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
                          ),*/
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
            ]),
      ),
    );
  }

  validateForm() {
    if (inventoryFulFilMentFormKey.currentState!.validate()) {
      inventoryFulFilMentController.addSaveAllInventoryOutwardApiCall();
    } else {
      setState(() {
      autoValidateMode = AutovalidateMode.onUserInteraction;
      inventoryFulFilMentController.update();
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.inventory_fulfilment,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

/*  Future<void> selectDate(
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;
    if (identity == Strings.outward_date) {
      if (addEditOutwardController.selectedDateTime != null) {
        selectedDate = addEditOutwardController.selectedDateTime;
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
      if (identity == Strings.outward_date) {
        addEditOutwardController.selectedDateTime = picked;
        addEditOutwardController.update();
        _selectDateTime();
      }
    }
  }*/

/* Future<void> _selectDateTime() async {
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
        addEditOutwardController.selectedDateTime!.year,
        addEditOutwardController.selectedDateTime!.month,
        addEditOutwardController.selectedDateTime!.day,
        picked.hour,
        picked.minute,
      );
      addEditOutwardController.outwardDateController.text =
          addEditOutwardController.dateFormat.format(dt);
      addEditOutwardController.outwardDateTime =
          addEditOutwardController.apiDateTimeFormat.format(dt);
      addEditOutwardController.update();
    }
  }*/
}
