import 'dart:developer';
import 'package:savbill/pages/inventory/bulk_consumption/add_edit_bulk_consumption_controller.dart';
import 'package:savbill/pages/inventory/bulk_consumption/serial_mapping_item_bulk_consumption.dart';
import 'package:savbill/pages/inventory/module/response/active_staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_product_based_item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_serialized_item_base_on_product_res.dart';
import 'package:savbill/pages/inventory/module/response/partiner_list_new_res.dart';
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
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AddEditBulkConsumption extends StatefulWidget {
  @override
  _AddEditBulkConsumptionState createState() => _AddEditBulkConsumptionState();
}

class _AddEditBulkConsumptionState extends State<AddEditBulkConsumption> {
  final addEditBulkConsumptionController =
      Get.put(AddEditBulkConsumptionController());
  final addEditBulkConsumptionFormKey = GlobalKey<FormState>();
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
      child:
          GetBuilder<AddEditBulkConsumptionController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditBulkConsumptionController.isLoading),
        ]);
      }), /**/
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
                      key: addEditBulkConsumptionFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(title: Strings.name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.name,
                              textEditingController:
                                  addEditBulkConsumptionController
                                      .nameController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.text,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_name;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          /*_____________________ itemType _____________________________*/
                          InputTitleRequire(
                              title: Strings.item_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: false,
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
                                    Strings.select_item_type,
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
                                value: addEditBulkConsumptionController
                                    .selectedItemType,
                                items: addEditBulkConsumptionController
                                    .itemTypeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditBulkConsumptionController
                                          .selectedItemType =
                                      value as DropdownDetail?;
                                  addEditBulkConsumptionController
                                      .getAllProductBasedOnItemType(
                                          value!.text);
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditBulkConsumptionController
                                              .selectedItemType ==
                                          null) {
                                    return Strings.select_item_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          /*_____________________ Product _____________________________*/
                          addEditBulkConsumptionController
                                  .serializedNonProductDataList!.isNotEmpty
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.product, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    IgnorePointer(
                                      ignoring: false,
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
                                              Strings.product,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: false,
                                          isDense: true,
                                          value:
                                              addEditBulkConsumptionController
                                                  .selectSerializedProductData,
                                          items: addEditBulkConsumptionController
                                              .serializedNonProductDataList
                                              ?.map(
                                                  (ProductBasedItemTypeDataList
                                                      value) {
                                            return DropdownMenuItem<
                                                ProductBasedItemTypeDataList>(
                                              value: value,
                                              child: Text(value.name!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addEditBulkConsumptionController
                                                    .selectSerializedProductData =
                                                value
                                                    as ProductBasedItemTypeDataList?;
                                            addEditBulkConsumptionController
                                                .update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                addEditBulkConsumptionController
                                                        .selectSerializedProductData ==
                                                    null) {
                                              return Strings
                                                  .please_select_product;
                                            }
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          /*_____________________ OwnerType _____________________________*/
                          addEditBulkConsumptionController
                                      .selectSerializedProductData !=
                                  null
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.owner_type,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    IgnorePointer(
                                      ignoring: false,
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
                                              Strings.owner_type,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: false,
                                          isDense: true,
                                          value:
                                              addEditBulkConsumptionController
                                                  .selectedOwnerType,
                                          items:
                                              addEditBulkConsumptionController
                                                  .ownerTypeList
                                                  ?.map((DropdownDetail value) {
                                            return DropdownMenuItem<
                                                DropdownDetail>(
                                              value: value,
                                              child: Text(value.text!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addEditBulkConsumptionController
                                                    .selectedOwnerType =
                                                value as DropdownDetail?;
                                            addEditBulkConsumptionController
                                                .partnerList!
                                                .clear();
                                            addEditBulkConsumptionController
                                                .wareHouseList!
                                                .clear();
                                            addEditBulkConsumptionController
                                                .staffUserList!
                                                .clear();
                                            addEditBulkConsumptionController
                                                .selectedWarehouse = null;
                                            addEditBulkConsumptionController
                                                .selectedStaffUserDetail = null;
                                            addEditBulkConsumptionController
                                                .selectedPartnerData = null;
                                            if (value!.text!.equalsIgnoreCase(
                                                Strings.ware_house)) {
                                              addEditBulkConsumptionController
                                                  .getAllActiveWareHouse();
                                            } else if (value.text!
                                                .equalsIgnoreCase(
                                                    Strings.staff)) {
                                              addEditBulkConsumptionController
                                                  .getAllStaffUser();
                                            } else if (value.text!
                                                .equalsIgnoreCase(
                                                    Strings.partner)) {
                                              addEditBulkConsumptionController
                                                  .getAllPartnerUser();
                                            }
                                            addEditBulkConsumptionController
                                                .update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                addEditBulkConsumptionController
                                                        .selectedOwnerType ==
                                                    null) {
                                              return Strings.select_item_type;
                                            }
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          /*_____________________ Owner _____________________________*/

                          addEditBulkConsumptionController.selectedOwnerType !=
                                  null
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.owner, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    (addEditBulkConsumptionController
                                            .wareHouseList!.isNotEmpty)
                                        ? IgnorePointer(
                                            ignoring: false,
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
                                                decoration:
                                                    Utils.ddlDecoration(),
                                                hint: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                    Strings.owner,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ),
                                                  ),
                                                ),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: false,
                                                isDense: true,
                                                value:
                                                    addEditBulkConsumptionController
                                                        .selectedWarehouse,
                                                items:
                                                    addEditBulkConsumptionController
                                                        .wareHouseList
                                                        ?.map((WareHouseDetail
                                                            value) {
                                                  return DropdownMenuItem<
                                                      WareHouseDetail>(
                                                    value: value,
                                                    child: Text(value.name!),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditBulkConsumptionController
                                                          .selectedWarehouse =
                                                      value as WareHouseDetail?;
                                                  addEditBulkConsumptionController
                                                      .ownerId = value!.id;
                                                  addEditBulkConsumptionController
                                                      .update();
                                                  if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .non_serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .availableQtyProductDestination();
                                                  } else if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .getAllSerializedItemBaseOnProduct();
                                                  }
                                                },
                                                validator: (value) {
                                                  if (value == null ||
                                                      addEditBulkConsumptionController
                                                              .selectedWarehouse ==
                                                          null) {
                                                    return Strings
                                                        .please_select_owner;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                          )
                                        : const SizedBox.shrink(),
                                    (addEditBulkConsumptionController
                                            .staffUserList!.isNotEmpty)
                                        ? IgnorePointer(
                                            ignoring: false,
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
                                                decoration:
                                                    Utils.ddlDecoration(),
                                                hint: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                    Strings.select_owner,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ),
                                                  ),
                                                ),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: false,
                                                isDense: true,
                                                value:
                                                    addEditBulkConsumptionController
                                                        .selectedStaffUserDetail,
                                                items:
                                                    addEditBulkConsumptionController
                                                        .staffUserList
                                                        ?.map((StaffUserDataList
                                                            value) {
                                                  return DropdownMenuItem<
                                                      StaffUserDataList>(
                                                    value: value,
                                                    child: Text(
                                                        value.displayName!),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditBulkConsumptionController
                                                          .selectedStaffUserDetail =
                                                      value
                                                          as StaffUserDataList?;
                                                  addEditBulkConsumptionController
                                                      .update();
                                                  if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .non_serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .availableQtyProductDestination();
                                                  } else if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .getAllSerializedItemBaseOnProduct();
                                                  }
                                                },
                                                validator: (value) {
                                                  if (value == null ||
                                                      addEditBulkConsumptionController
                                                              .selectedStaffUserDetail ==
                                                          null) {
                                                    return Strings
                                                        .please_select_owner;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                          )
                                        : const SizedBox.shrink(),
                                    (addEditBulkConsumptionController
                                            .partnerList!.isNotEmpty)
                                        ? IgnorePointer(
                                            ignoring: false,
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
                                                decoration:
                                                    Utils.ddlDecoration(),
                                                hint: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                    Strings.select_owner,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ),
                                                  ),
                                                ),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: false,
                                                isDense: true,
                                                value:
                                                    addEditBulkConsumptionController
                                                        .selectedPartnerData,
                                                items:
                                                    addEditBulkConsumptionController
                                                        .partnerList
                                                        ?.map((Partnerlist
                                                            value) {
                                                  return DropdownMenuItem<
                                                      Partnerlist>(
                                                    value: value,
                                                    child: Text(
                                                        value.displayName!),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditBulkConsumptionController
                                                          .selectedPartnerData =
                                                      value as Partnerlist?;
                                                  addEditBulkConsumptionController
                                                      .update();
                                                  if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .non_serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .availableQtyProductDestination();
                                                  } else if (addEditBulkConsumptionController
                                                      .selectedItemType!.text!
                                                      .equalsIgnoreCase(Strings
                                                          .serialized_item)) {
                                                    addEditBulkConsumptionController
                                                        .getAllSerializedItemBaseOnProduct();
                                                  }
                                                },
                                                validator: (value) {
                                                  if (value == null ||
                                                      addEditBulkConsumptionController
                                                              .selectedPartnerData ==
                                                          null) {
                                                    return Strings
                                                        .please_select_owner;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                          )
                                        : const SizedBox.shrink(),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          /*_____________________ Non-Serialization _____________________________*/

                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.non_serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedWarehouse !=
                                      null)
                              ? Stack(
                                  children: [
                                    Container(
                                        width: double.infinity,
                                        margin: const EdgeInsets.fromLTRB(
                                            0, 20, 0, 10),
                                        padding: const EdgeInsets.only(
                                            bottom: 5, left: 15, right: 15),
                                        decoration: BoxDecoration(
                                          border: Border.all(
                                              color: AppTheme.colorBlackEnd,
                                              width: 1),
                                          borderRadius:
                                              BorderRadius.circular(5),
                                          shape: BoxShape.rectangle,
                                        ),
                                        child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            mainAxisAlignment:
                                                MainAxisAlignment.start,
                                            children: [
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING +
                                                          5),
                                              Row(
                                                children: [
                                                  Flexible(
                                                    flex: 1,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .available_quantity,
                                                            textEditingController:
                                                                TextEditingController(
                                                                    text: addEditBulkConsumptionController
                                                                        .availableQty
                                                                        .toString()),
                                                            keyboardType:
                                                                TextInputType
                                                                    .text,
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_product_name;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: true),
                                                      ],
                                                    ),
                                                  ),
                                                  const SizedBox(
                                                      width: Constant
                                                          .SCREEN_PADDING),
                                                  Flexible(
                                                    flex: 2,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .enter_assign_qty,
                                                            textEditingController:
                                                                addEditBulkConsumptionController
                                                                    .assignQuantityController,
                                                            keyboardType:
                                                                TextInputType
                                                                    .number,
                                                            inputFormatters: [
                                                              FilteringTextInputFormatter
                                                                  .allow(RegExp(
                                                                      r"[0-9.]")),
                                                              TextInputFormatter
                                                                  .withFunction(
                                                                      (oldValue,
                                                                          newValue) {
                                                                final text =
                                                                    newValue
                                                                        .text;
                                                                return text
                                                                        .isEmpty
                                                                    ? newValue
                                                                    : double.tryParse(text) ==
                                                                            null
                                                                        ? oldValue
                                                                        : newValue;
                                                              }),
                                                            ],
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onChanged: (value) {
                                                              if (value
                                                                  .isNotEmpty) {
                                                                if (int.parse(
                                                                        value) >
                                                                    addEditBulkConsumptionController
                                                                        .availableQty!) {
                                                                  Utils.showSnackbar(
                                                                      Strings
                                                                          .ERROR,
                                                                      Strings
                                                                          .enter_valid_quantity,
                                                                      AppTheme
                                                                          .colorWhite,
                                                                      AppTheme
                                                                          .colorRed);
                                                                }
                                                              }
                                                            },
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_valid_quantity;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: false),
                                                      ],
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING -
                                                          5),
                                            ])),
                                    Positioned(
                                      left: 50,
                                      top: 10,
                                      child: Container(
                                        padding: const EdgeInsets.only(
                                            bottom: 3,
                                            left: 3,
                                            right: 3,
                                            top: 3),
                                        color: Colors.white,
                                        child: CustomText(
                                          title: Strings
                                              .non_serialized_product_details,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.non_serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedStaffUserDetail !=
                                      null)
                              ? Stack(
                                  children: [
                                    Container(
                                        width: double.infinity,
                                        margin: const EdgeInsets.fromLTRB(
                                            0, 20, 0, 10),
                                        padding: const EdgeInsets.only(
                                            bottom: 5, left: 15, right: 15),
                                        decoration: BoxDecoration(
                                          border: Border.all(
                                              color: AppTheme.colorBlackEnd,
                                              width: 1),
                                          borderRadius:
                                              BorderRadius.circular(5),
                                          shape: BoxShape.rectangle,
                                        ),
                                        child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            mainAxisAlignment:
                                                MainAxisAlignment.start,
                                            children: [
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING +
                                                          5),
                                              Row(
                                                children: [
                                                  Flexible(
                                                    flex: 1,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .available_quantity,
                                                            textEditingController:
                                                                TextEditingController(
                                                                    text: addEditBulkConsumptionController
                                                                        .availableQty
                                                                        .toString()),
                                                            keyboardType:
                                                                TextInputType
                                                                    .text,
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_product_name;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: true),
                                                      ],
                                                    ),
                                                  ),
                                                  const SizedBox(
                                                      width: Constant
                                                          .SCREEN_PADDING),
                                                  Flexible(
                                                    flex: 2,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .enter_assign_qty,
                                                            textEditingController:
                                                                addEditBulkConsumptionController
                                                                    .assignQuantityController,
                                                            keyboardType:
                                                                TextInputType
                                                                    .number,
                                                            inputFormatters: [
                                                              FilteringTextInputFormatter
                                                                  .allow(RegExp(
                                                                      r"[0-9.]")),
                                                              TextInputFormatter
                                                                  .withFunction(
                                                                      (oldValue,
                                                                          newValue) {
                                                                final text =
                                                                    newValue
                                                                        .text;
                                                                return text
                                                                        .isEmpty
                                                                    ? newValue
                                                                    : double.tryParse(text) ==
                                                                            null
                                                                        ? oldValue
                                                                        : newValue;
                                                              }),
                                                            ],
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onChanged: (value) {
                                                              if (value
                                                                  .isNotEmpty) {
                                                                if (int.parse(
                                                                        value) >
                                                                    addEditBulkConsumptionController
                                                                        .availableQty!) {
                                                                  Utils.showSnackbar(
                                                                      Strings
                                                                          .ERROR,
                                                                      Strings
                                                                          .enter_valid_quantity,
                                                                      AppTheme
                                                                          .colorWhite,
                                                                      AppTheme
                                                                          .colorRed);
                                                                }
                                                              }
                                                            },
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_valid_quantity;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: false),
                                                      ],
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING -
                                                          5),
                                            ])),
                                    Positioned(
                                      left: 50,
                                      top: 10,
                                      child: Container(
                                        padding: const EdgeInsets.only(
                                            bottom: 3,
                                            left: 3,
                                            right: 3,
                                            top: 3),
                                        color: Colors.white,
                                        child: CustomText(
                                          title: Strings
                                              .non_serialized_product_details,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),
                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.non_serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedPartnerData !=
                                      null)
                              ? Stack(
                                  children: [
                                    Container(
                                        width: double.infinity,
                                        margin: const EdgeInsets.fromLTRB(
                                            0, 20, 0, 10),
                                        padding: const EdgeInsets.only(
                                            bottom: 5, left: 15, right: 15),
                                        decoration: BoxDecoration(
                                          border: Border.all(
                                              color: AppTheme.colorBlackEnd,
                                              width: 1),
                                          borderRadius:
                                              BorderRadius.circular(5),
                                          shape: BoxShape.rectangle,
                                        ),
                                        child: Column(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.start,
                                            mainAxisAlignment:
                                                MainAxisAlignment.start,
                                            children: [
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING +
                                                          5),
                                              Row(
                                                children: [
                                                  Flexible(
                                                    flex: 1,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .available_quantity,
                                                            textEditingController:
                                                                TextEditingController(
                                                                    text: addEditBulkConsumptionController
                                                                        .availableQty
                                                                        .toString()),
                                                            keyboardType:
                                                                TextInputType
                                                                    .text,
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_product_name;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: true),
                                                      ],
                                                    ),
                                                  ),
                                                  const SizedBox(
                                                      width: Constant
                                                          .SCREEN_PADDING),
                                                  Flexible(
                                                    flex: 2,
                                                    child: Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      children: [
                                                        InputTitleRequire(
                                                            title: Strings
                                                                .available_quantity,
                                                            require: true),
                                                        const SizedBox(
                                                          height: Constant
                                                              .VERY_SMALL_PADDING,
                                                        ),
                                                        CoustomTextField(
                                                            labelText: Strings
                                                                .enter_assign_qty,
                                                            textEditingController:
                                                                addEditBulkConsumptionController
                                                                    .assignQuantityController,
                                                            keyboardType:
                                                                TextInputType
                                                                    .number,
                                                            inputFormatters: [
                                                              FilteringTextInputFormatter
                                                                  .allow(RegExp(
                                                                      r"[0-9.]")),
                                                              TextInputFormatter
                                                                  .withFunction(
                                                                      (oldValue,
                                                                          newValue) {
                                                                final text =
                                                                    newValue
                                                                        .text;
                                                                return text
                                                                        .isEmpty
                                                                    ? newValue
                                                                    : double.tryParse(text) ==
                                                                            null
                                                                        ? oldValue
                                                                        : newValue;
                                                              }),
                                                            ],
                                                            borderEnableColors:
                                                                AppTheme
                                                                    .colorBlack,
                                                            textInputAction:
                                                                TextInputAction
                                                                    .next,
                                                            hintColor: AppTheme
                                                                .colorIconGrey,
                                                            onChanged: (value) {
                                                              if (value
                                                                  .isNotEmpty) {
                                                                if (int.parse(
                                                                        value) >
                                                                    addEditBulkConsumptionController
                                                                        .availableQty!) {
                                                                  Utils.showSnackbar(
                                                                      Strings
                                                                          .ERROR,
                                                                      Strings
                                                                          .enter_valid_quantity,
                                                                      AppTheme
                                                                          .colorWhite,
                                                                      AppTheme
                                                                          .colorRed);
                                                                }
                                                              }
                                                            },
                                                            onTextValidator:
                                                                (String?
                                                                    value) {
                                                              if (value!
                                                                  .isEmpty) {
                                                                return Strings
                                                                    .enter_valid_quantity;
                                                              }
                                                              return null;
                                                            },
                                                            borderCorner: Constant
                                                                .INPUT_ROUNDED_CORNER,
                                                            contentPadding:
                                                                const EdgeInsets
                                                                        .symmetric(
                                                                    horizontal:
                                                                        Constant
                                                                            .LARGE_PADDING),
                                                            readOnly: false),
                                                      ],
                                                    ),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(
                                                  height:
                                                      Constant.SCREEN_PADDING -
                                                          5),
                                            ])),
                                    Positioned(
                                      left: 50,
                                      top: 10,
                                      child: Container(
                                        padding: const EdgeInsets.only(
                                            bottom: 3,
                                            left: 3,
                                            right: 3,
                                            top: 3),
                                        color: Colors.white,
                                        child: CustomText(
                                          title: Strings
                                              .non_serialized_product_details,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          /*_____________________ Serialization _____________________________*/

                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedWarehouse !=
                                      null)
                              ? serializedItemSelection()
                              : const SizedBox.shrink(),
                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedStaffUserDetail !=
                                      null)
                              ? serializedItemSelection()
                              : const SizedBox.shrink(),
                          ((addEditBulkConsumptionController.selectedItemType !=
                                          null &&
                                      addEditBulkConsumptionController
                                          .selectedItemType!.text!
                                          .equalsIgnoreCase(
                                              Strings.serialized_item)) &&
                                  addEditBulkConsumptionController
                                          .selectedPartnerData !=
                                      null)
                              ? serializedItemSelection()
                              : const SizedBox.shrink(),
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
    if (addEditBulkConsumptionFormKey.currentState!.validate()) {
      addEditBulkConsumptionController.searchItem("");
      addEditBulkConsumptionController.addEditBulkConsumptionApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditBulkConsumptionController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_bulk_consumption
            : Strings.create_bulk_consumption,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  serializedItemSelection() {
    return ((addEditBulkConsumptionController.serializedItemBaseDataList !=
                null &&
            addEditBulkConsumptionController
                .serializedItemBaseDataList!.isNotEmpty))
        ? Stack(
            children: [
              Container(
                width: double.infinity,
                margin: const EdgeInsets.fromLTRB(0, 20, 0, 10),
                padding: const EdgeInsets.only(bottom: 5, left: 15, right: 15),
                decoration: BoxDecoration(
                  border: Border.all(color: AppTheme.colorBlackEnd, width: 1),
                  borderRadius: BorderRadius.circular(5),
                  shape: BoxShape.rectangle,
                ),
                child: Column(
                  children: [
                    const SizedBox(height: Constant.SCREEN_PADDING + 5),
                    ListView.builder(
                        itemCount: addEditBulkConsumptionController
                            .serializedItemBaseDataList!.length,
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        scrollDirection: Axis.vertical,
                        itemBuilder: (BuildContext context, int index) {
                          SerializedItemBaseDataList item =
                              addEditBulkConsumptionController
                                  .serializedItemBaseDataList![index];
                          item.macAddressValue = item.macAddress;
                          item.serialNumberValue = item.serialNumber;
                          return Container(
                            margin: const EdgeInsets.only(
                                top: Constant.VERY_SMALL_PADDING - 2),
                            child: InkWell(
                              onTap: () {
                                if (item.selected != null && item.selected!) {
                                  item.selected = false;
                                } else {
                                  item.selected = true;
                                }
                                addEditBulkConsumptionController.update();
                                // }
                              },
                              child: SerialMappingBulkConsumptionItem(
                                item: item,
                                controller: addEditBulkConsumptionController,
                                onSelectChanged: (value) {
                                  if (item.selected != null && item.selected!) {
                                    item.selected = false;
                                  } else {
                                    item.selected = true;
                                  }
                                  addEditBulkConsumptionController.update();
                                },
                              ),
                            ),
                          );
                        }),
                    const SizedBox(height: Constant.SMALL_PADDING),
                  ],
                ),
              ),
              Positioned(
                left: 50,
                top: 10,
                child: Container(
                  padding: const EdgeInsets.only(
                      bottom: 3, left: 3, right: 3, top: 3),
                  color: Colors.white,
                  child: CustomText(
                    title: Strings.non_serialized_product_details,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          )
        : const SizedBox.shrink();
  }
}
