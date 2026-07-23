import 'dart:io';
import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer_inventory/assign_mac_and_serial_inventory_dialog.dart';
import 'package:savbill/pages/inventory/module/response/get_serialize_product_item_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/inventory/pop/pop_assign_inventory_controller.dart';
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
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

class PopAssignInventory extends StatefulWidget {
  @override
  _PopAssignInventoryState createState() => _PopAssignInventoryState();
}

class _PopAssignInventoryState extends State<PopAssignInventory>
    with WidgetsBindingObserver
    implements MacAndSerialAssignInventoryAction, LocationBtnAction {
  final popAssignInventoryController = Get.put(PopAssignInventoryController());
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
    popAssignInventoryController.setBtnClickEvent(false);
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
      child: GetBuilder<PopAssignInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: popAssignInventoryController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(PopAssignInventoryController controller) {
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
                        /*__________________ Item Type_________________________*/

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.item_type, require: true),
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
                              if (value!.text!
                                  .equalsIgnoreCase(Strings.serialized_item)) {
                                controller.productTrackableDataList!.clear();
                                controller.serializedItemVisible = true;
                                controller.nonSerializedQtyVisible = false;
                                controller.productItemVisible = true;
                                controller.getSerializedProductDataList();
                              } else {
                                controller.serializedItemVisible = false;
                                controller.nonSerializedQtyVisible = true;

                                controller.getNonSerializedProductDataList();
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
                        ),

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
                                      .selectedNonSerializeProductData,
                                  items: controller.nonSerializeProductList!
                                      .map((SerializedDataList value) {
                                    return DropdownMenuItem<SerializedDataList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.nonTrackableProductDataList
                                        .clear();
                                    controller.selectedNonSerializeProductData =
                                        value as SerializedDataList?;
                                    controller.getNonTrackableProductQtyApi(
                                        value!.id!);
                                    controller.productId = value.id!;
                                    // controller.selectProductUnit = value.productCategory!.unit;
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        popAssignInventoryController
                                                .selectedNonSerializeProductData ==
                                            null) {
                                      return Strings.please_select_product;
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
                                  value:
                                      controller.selectedSerializeProductData,
                                  items: controller.serializeProductList
                                      ?.map((SerializedDataList value) {
                                    return DropdownMenuItem<SerializedDataList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedSerializeProductData =
                                        value as SerializedDataList?;
                                    controller.assignMacController.text = "";
                                    popAssignInventoryController.productId =
                                        popAssignInventoryController
                                            .selectedSerializeProductData!.id;
                                    controller.getProductMacAddressData(
                                        popAssignInventoryController
                                            .selectedSerializeProductData!.id);
                                    controller.update();
                                    // popAssignInventoryController.getInwardsData();
                                    // controller.getProductMacAddressData();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        popAssignInventoryController
                                                .selectedSerializeProductData ==
                                            null) {
                                      return Strings.please_select_product;
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
                                textEditingController:
                                    popAssignInventoryController
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
                            textEditingController: popAssignInventoryController
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
                          height: Constant.SMALL_PADDING,
                        ),

                        InputTitleRequire(
                            title: Strings.latitude, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.latitude,
                            textEditingController:
                            popAssignInventoryController.latController,
                            keyboardType: TextInputType.number,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.enter_latitude;
                              }
                              return null;
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: false),
                        const SizedBox(
                          height: Constant.LARGE_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.longitude, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.longitude,
                            textEditingController:
                            popAssignInventoryController.longController,
                            keyboardType: TextInputType.number,
                            borderEnableColors: AppTheme.colorBlack,
                            textInputAction: TextInputAction.next,
                            hintColor: AppTheme.colorIconGrey,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.enter_longitude;
                              }
                              return null;
                            },
                            borderCorner: Constant.INPUT_ROUNDED_CORNER,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.LARGE_PADDING),
                            readOnly: false),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            InkWell(
                              onTap: () {
                                locationPermissionStatus();
                              },
                              child: Material(
                                elevation: 1.5,
                                color: AppTheme.custNearLocationLight,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.BTN_ROUNDED_CORNER)),
                                child: Container(
                                  height: Constant.BTN_HEIGHT_M,
                                  width: Constant.BTN_HEIGHT_M,
                                  alignment: Alignment.center,
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING - 1),
                                  child: SvgPicture.asset(
                                    currentLocationSvg,
                                    height: Constant.ICON_SIZE,
                                    width: Constant.ICON_SIZE,
                                    color: AppTheme.custNearLocationDark,
                                    fit: BoxFit.fill,
                                  ),
                                ),
                              ),
                            ),
                            const SizedBox(width: Constant.MEDIUM_PADDING),
                            InkWell(
                              onTap: () {
                                openLocationListScreen();
                              },
                              child: Material(
                                elevation: 1.5,
                                color: AppTheme.custChangeStatusLight,
                                shape: RoundedRectangleBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.BTN_ROUNDED_CORNER)),
                                child: Container(
                                  height: Constant.BTN_HEIGHT_M,
                                  width: Constant.BTN_HEIGHT_M,
                                  alignment: Alignment.center,
                                  padding: const EdgeInsets.all(
                                      Constant.SMALL_PADDING - 1),
                                  child: SvgPicture.asset(
                                    searchLocationSvg,
                                    height: Constant.ICON_SIZE,
                                    width: Constant.ICON_SIZE,
                                    color: AppTheme.custChangeStatusDark,
                                    fit: BoxFit.fill,
                                  ),
                                ),
                              ),
                            )
                          ],
                        ),
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
      if (popAssignInventoryController.selectedInwordDateTime != null) {
        selectedDate = popAssignInventoryController.selectedInwordDateTime;
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
        popAssignInventoryController.selectedInwordDateTime = picked;
        popAssignInventoryController.update();
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
        popAssignInventoryController.selectedInwordDateTime!.year,
        popAssignInventoryController.selectedInwordDateTime!.month,
        popAssignInventoryController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      popAssignInventoryController.outwardDateController.text =
          popAssignInventoryController.dateFormat.format(dt);
      popAssignInventoryController.inwardDateTime =
          popAssignInventoryController.apiDateTimeFormat.format(dt);
      popAssignInventoryController.update();
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
      if (popAssignInventoryController.selectedItemType!.text!
          .equalsIgnoreCase(Strings.serialized_item)) {
        if(popAssignInventoryController.productMacAddressData!= null && popAssignInventoryController.productMacAddressData!.itemId != null) {
          popAssignInventoryController.assignInventory();
        }else{
        Utils.showSnackbar(
            Strings.INFO,
            Strings.please_select_mac_address,
            AppTheme.colorWhite,
            AppTheme.colorBlueRView);
        }
      } else if (popAssignInventoryController.selectedItemType!.text!
          .equalsIgnoreCase(Strings.non_serialized_item)) {
        popAssignInventoryController.assignNonSerializeInventoryPop();

      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  showMacAddressDialog() {
    showDialog(
        context: scaffoldKey.currentContext!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return AssignMacAndSerialInventoryDialog(
            // macAddressAction: this,
            macAddressLst: popAssignInventoryController.productMacAddressList!,
            macSerialNumberAction: this,
            controller: popAssignInventoryController,
          );
        });
  }

  @override
  void macSerialNumberBtnAction({List<ProductMacDataList>? selectedItem}) {
    Get.back();
    if (selectedItem != null) {
      popAssignInventoryController.selectedMacAddressList!.clear();
      popAssignInventoryController.selectedMacAddressList!.addAll(selectedItem);
      popAssignInventoryController.availableQtyPics =
          popAssignInventoryController.selectedMacAddressList!.length;
      String macAdd = "";
      for (int i = 0; i < selectedItem.length; i++) {
        ProductMacDataList element = selectedItem[i];
        popAssignInventoryController.productMacAddressData = element;
        if (i == selectedItem.length - 1) {
          if (element.macAddress != null) {
            macAdd =
                "$macAdd${element.itemId!} -  ${element.serialNumber!} - ${element.macAddress!}";
          } else {
            macAdd = "$macAdd${element.itemId!} - ${element.serialNumber!}";
          }
        } else {
          macAdd = "$macAdd${element.serialNumber!}-${element.macAddress!}, ";
        }
      }
      popAssignInventoryController.assignMacController.text = macAdd;
      popAssignInventoryController.update();
    }
  }

  // locationPermissionStatus() async {
  //   if (Platform.isIOS) {
  //     getCurrentPosition(false);
  //   } else {
  //     PermissionService().requestLocationPermission(onPermissionSuccess: () {
  //       print("Location Service Permission approved");
  //       getCurrentPosition(false);
  //     }, onPermissionDenied: () async {
  //       print("Location Service Permission denied");
  //       getCurrentPosition(false);
  //     });
  //   }
  // }

  // getCurrentPosition(bool fromTryAgain) async {
  //   bool serviceEnabled = await checkLocationService();
  //   if (!serviceEnabled) {
  //     popAssignInventoryController.setBtnClickEvent(true);
  //     locationSettingsDialog(false, fromTryAgain);
  //     return false;
  //   }
  //   LocationPermission permission = await geolocatorPlatform.checkPermission();
  //   if (permission == LocationPermission.denied) {
  //     if (Platform.isIOS) {
  //       permission = await Geolocator.requestPermission();
  //       if (permission == LocationPermission.denied) {
  //         locationSettingsDialog(true, fromTryAgain);
  //         return false;
  //       }
  //     } else {
  //       popAssignInventoryController.setBtnClickEvent(true);
  //       locationSettingsDialog(true, fromTryAgain);
  //       return false;
  //     }
  //   }
  //   if (permission == LocationPermission.deniedForever) {
  //     // for app settings
  //     if (Platform.isIOS) {
  //       permission = await Geolocator.requestPermission();
  //       if (permission == LocationPermission.deniedForever) {
  //         locationSettingsDialog(true, fromTryAgain);
  //         return false;
  //       }
  //     } else {
  //       popAssignInventoryController.setBtnClickEvent(true);
  //       locationSettingsDialog(true, fromTryAgain);
  //       return false;
  //     }
  //   }
  //
  //   popAssignInventoryController.isLoading = true;
  //   popAssignInventoryController.update();
  //
  //   LocationSettings settings = const LocationSettings(
  //       accuracy: LocationAccuracy.bestForNavigation,
  //       timeLimit: Duration(seconds: 20));
  //   geolocatorPlatform
  //       .getCurrentPosition(
  //     locationSettings: settings,
  //   )
  //       .then((position) async {
  //     if (position != null) {
  //       popAssignInventoryController.setBtnClickEvent(false);
  //       popAssignInventoryController.isLoading = false;
  //
  //       Position currentPosition = position;
  //       print(
  //           "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
  //       List<Placemark> placemarks = await placemarkFromCoordinates(
  //           currentPosition.latitude, currentPosition.longitude);
  //       Placemark place = placemarks[0];
  //
  //       String? getFullAddress = "";
  //
  //       print("PlacemarkPlace>>> ${place}");
  //
  //       if (place.street!.isNotEmpty) {
  //         getFullAddress = place.street;
  //       }
  //       if (place.subLocality!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.subLocality}";
  //       }
  //       if (place.thoroughfare!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.thoroughfare}";
  //       }
  //
  //       if (place.subThoroughfare!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.subThoroughfare}";
  //       }
  //
  //       if (place.locality!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.locality}";
  //       }
  //
  //       if (place.administrativeArea!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.administrativeArea}";
  //       }
  //
  //       if (place.subAdministrativeArea!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.subAdministrativeArea}";
  //       }
  //
  //       if (place.country!.isNotEmpty) {
  //         getFullAddress = "$getFullAddress, ${place.country}";
  //       }
  //
  //       getFullAddress = "$getFullAddress, ${place.postalCode}";
  //
  //       popAssignInventoryController.latLonController.text =
  //           getFullAddress.toString();
  //
  //       // popAssignInventoryController.latController.text = currentPosition.latitude.toString();
  //       // popAssignInventoryController.longController.text = currentPosition.longitude.toString();
  //
  //       popAssignInventoryController.update();
  //     } else {
  //       popAssignInventoryController.isLoading = false;
  //       popAssignInventoryController.update();
  //       getCurrentPosition(false);
  //     }
  //   }).catchError((error) {
  //     popAssignInventoryController.isLoading = false;
  //     popAssignInventoryController.update();
  //     getCurrentPosition(false);
  //   });
  // }

  // Future<bool> checkLocationService() async {
  //   bool serviceEnabled;
  //   serviceEnabled = await geolocatorPlatform.isLocationServiceEnabled();
  //   if (!serviceEnabled) {
  //     return false;
  //   } else {
  //     return true;
  //   }
  // }

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        popAssignInventoryController.selectedLocation = data;
        popAssignInventoryController.update();
        popAssignInventoryController.getLocationToLatLong();
      }
    }
  }

  // locationSettingsDialog(bool isAppPermission, bool fromTryAgain) {
  //   if (!isAppPermission || fromTryAgain) {
  //     showDialog(
  //         context: context,
  //         barrierDismissible: false,
  //         builder: (BuildContext context) {
  //           return LocationSettingsDialog(
  //               locationBtnAction: this,
  //               isAppPermission: isAppPermission,
  //               from: Constant.NEAR_BY_DEVICE);
  //         });
  //   } else if (isAppPermission && fromTryAgain) {
  //     showDialog(
  //         context: context,
  //         barrierDismissible: false,
  //         builder: (BuildContext context) {
  //           return LocationSettingsDialog(
  //               locationBtnAction: this,
  //               isAppPermission: isAppPermission,
  //               from: Constant.NEAR_BY_DEVICE);
  //         });
  //   }
  // }



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
      popAssignInventoryController.setBtnClickEvent(true);
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
        popAssignInventoryController.setBtnClickEvent(true);
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
        popAssignInventoryController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    popAssignInventoryController.isLoading = true;
    popAssignInventoryController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        popAssignInventoryController.setBtnClickEvent(false);
        popAssignInventoryController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        popAssignInventoryController.latController.text =
            currentPosition.latitude.toString();
        popAssignInventoryController.longController.text =
            currentPosition.longitude.toString();
        popAssignInventoryController.update();
      } else {
        popAssignInventoryController.isLoading = false;
        popAssignInventoryController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      popAssignInventoryController.isLoading = false;
      popAssignInventoryController.update();
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
}
