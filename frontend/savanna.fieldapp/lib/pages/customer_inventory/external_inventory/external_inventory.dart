import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_inventory/external_inventory/external_inventory_controller.dart';
import 'package:savbill/pages/customer_inventory/response/external_inv_product_customer_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_externalItem_product_staff_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_mac_mapping_external_res.dart';
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

class ExternalInventory extends StatefulWidget {
  @override
  _ExternalInventoryState createState() => _ExternalInventoryState();
}

class _ExternalInventoryState
    extends State<ExternalInventory>{
  final externalInventoryController = Get.put(ExternalInventoryController());
  final externalInventoryFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<ExternalInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: externalInventoryController.isLoading),
        ]);
      }),
    );
  }

  _body(ExternalInventoryController controller) {
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
                    key: externalInventoryFormKey,
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
                            isExpanded: true,
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
                              if (value.custPlanCategory != null ||
                                  value.custPlanCategory!.isNotEmpty) {
                                controller.planCategoryController.text =
                                    value.custPlanCategory!;
                              } else {
                                controller.planCategoryController.text = "--";
                              }
                              controller.serviceVisible = true;
                              controller.getProductByCustomerOwnerData();
                              // controller.getActivePlanListData(value.serviceId);
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

                        /*__________________ Plan Category ____________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.plan_category, require: true)
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? CoustomTextField(
                                labelText: Strings.plan_category,
                                textEditingController:
                                    controller.planCategoryController,
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

                        /*__________________ Product List _________________________*/

                        controller.serviceVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.serviceVisible == true
                            ? InputTitleRequire(
                                title: Strings.product, require: true)
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
                                      Strings.select_product,
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
                                  value: controller.selectedPlanDetail,
                                  items: controller.activePlanList?.map(
                                      (ExternalInvProductsDataList value) {
                                    return DropdownMenuItem<
                                        ExternalInvProductsDataList>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller.selectedPlanDetail =
                                        value as ExternalInvProductsDataList?;
                                    controller.externalItemGroupVisible = true;
                                    if (value!.id != null ||
                                        value.id.toString().isNotEmpty) {
                                      controller.productId = value.id;
                                      controller
                                          .getAllExternalItemProductStaffCall(
                                              value.id);
                                    }
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller.selectedPlanDetail == null) {
                                      return Strings.please_select_plan;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                        /*__________________ Select External Item Group _________________________*/

                        controller.externalItemGroupVisible == true
                            ? const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.externalItemGroupVisible == true
                            ? InputTitleRequire(
                                title: Strings.external_item_group,
                                require: true)
                            : const SizedBox.shrink(),
                        controller.externalItemGroupVisible == true
                            ? const SizedBox(
                                height: Constant.SMALL_PADDING,
                              )
                            : const SizedBox.shrink(),
                        controller.externalItemGroupVisible == true
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
                                      Strings.external_item_group,
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
                                      .selectedExternalItemProductDetails,
                                  items: controller.externalItemProductList!
                                      .map((GetAllExternalItemProductDataList
                                          value) {
                                    return DropdownMenuItem<
                                        GetAllExternalItemProductDataList>(
                                      value: value,
                                      child:
                                          Text(value.externalItemGroupNumber!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    controller
                                            .selectedExternalItemProductDetails =
                                        value
                                            as GetAllExternalItemProductDataList?;
                                    if (value!.id != null ||
                                        value.id.toString().isNotEmpty) {
                                      controller.externalItemId = value.id;
                                      controller
                                          .getAllMACMappingByExternalIdCall(
                                              value.id);
                                    }
                                    controller.macNoAndSerialNoFlag = true;
                                    controller.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        controller
                                                .selectedExternalItemProductDetails ==
                                            null) {
                                      return Strings
                                          .select_external_itemm_number;
                                    }
                                    return null;
                                  },
                                ),
                              )
                            : const SizedBox.shrink(),

                      /*_______________ Serial Number ______________________*/

                        controller.macNoAndSerialNoFlag == true
                            ? Container(
                                width: MediaQuery.of(context).size.width,
                                color: AppTheme.colorWhite,
                                margin: const EdgeInsets.only(
                                    top: Constant.MEDIUM_PADDING),
                                child: Form(
                                  child: Container(
                                      color: AppTheme.colorGrayTxtBg,
                                    child: Column(
                                        mainAxisSize: MainAxisSize.min,
                                        mainAxisAlignment:
                                            MainAxisAlignment.start,
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: [
                                          const SizedBox(
                                              height: Constant.LARGE_PADDING),
                                          Row(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.center,
                                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                            children: [
                                              Expanded(
                                                flex: 1,
                                                child: CustomText(
                                                  title: Strings.item_id,
                                                  textAlign: TextAlign.center,
                                                  colors: AppTheme.lable_noramal,
                                                  fontSize: AppTheme.small,
                                                  fontWeight: FontWeight.w500,
                                                ),
                                              ),
                                              Container(
                                                height:
                                                    Constant.EXTRA_LARGE_PADDING,
                                                width: 1,
                                                color: AppTheme.lable_noramal,
                                              ),
                                              Expanded(
                                                flex: 1,
                                                child: CustomText(
                                                  title: Strings.serial_no,
                                                  textAlign: TextAlign.center,
                                                  colors: AppTheme.lable_noramal,
                                                  fontSize: AppTheme.small+1,
                                                  fontWeight: FontWeight.w500,
                                                ),
                                              ),
                                            ],
                                          ),
                                          Container(
                                            height: 1,
                                            color: AppTheme.lable_noramal,
                                          ),
                                          Flexible(
                                              child: ListView.builder(
                                              shrinkWrap: true,
                                              primary: false,
                                              itemCount: controller
                                                .externalMacMappingExternalList!
                                                .length,
                                              itemBuilder: (context, index) {
                                              MACMappingExternalData item = controller
                                                      .externalMacMappingExternalList![
                                                  index];
                                              item.serialNumberValue = item.serialNumber;
                                              return Column(
                                                children: [
                                                  InkWell(
                                                    onTap: () {
                                                      for (var f in controller.externalMacMappingExternalList!) {
                                                        if (f.id == item.id) {
                                                          f.selected = !f.selected!;
                                                          item.serialNumberValue = item.serialNumber;
                                                          controller.productItemId = item.itemId;
                                                        } else {
                                                          f.selected = false;
                                                        }
                                                      }
                                                      setState(() {
                                                        controller
                                                            .productTypeDataList =
                                                            controller.productTypeDataList;
                                                        validateSelection();
                                                        controller.update();
                                                      });
                                                    },
                                                    child: Padding(
                                                      padding: const EdgeInsets
                                                              .symmetric(
                                                          vertical: Constant
                                                                  .SMALL_PADDING +
                                                              1,
                                                          horizontal: Constant
                                                              .MEDIUM_PADDING),
                                                      child: Row(
                                                        children: [
                                                          item.selected == true
                                                              ? Icon(
                                                                  Icons
                                                                      .check_circle,
                                                                  color: AppTheme
                                                                      .colorPrimary,
                                                                  size: Constant
                                                                      .ICON_SIZE,
                                                                )
                                                              : Icon(
                                                                  Icons
                                                                      .radio_button_off,
                                                                  color: AppTheme
                                                                      .lable_noramal,
                                                                  size: Constant
                                                                      .ICON_SIZE,
                                                                ),
                                                          const SizedBox(
                                                            width: Constant
                                                                .SMALL_PADDING,
                                                          ),
                                                          Expanded(
                                                            child: CustomText(
                                                              title:
                                                                  "${item.id!}",
                                                              textAlign:
                                                                  TextAlign.center,
                                                              colors: item.selected ==
                                                                      true
                                                                  ? AppTheme
                                                                      .colorPrimary
                                                                  : AppTheme
                                                                      .lable_noramal,
                                                              fontSize:
                                                                  AppTheme.small+1,
                                                              fontWeight: item
                                                                          .selected ==
                                                                      true
                                                                  ? FontWeight
                                                                      .w400
                                                                  : FontWeight
                                                                      .w400,
                                                            ),
                                                          ),
                                                          Expanded(
                                                              child: CustomText(
                                                            title: item
                                                                .serialNumber!,
                                                            textAlign:
                                                                TextAlign.center,
                                                            colors: item
                                                                        .selected ==
                                                                    true
                                                                ? AppTheme
                                                                    .colorPrimary
                                                                : AppTheme
                                                                    .lable_noramal,
                                                            fontSize:
                                                                AppTheme.small+1,
                                                            fontWeight: item
                                                                        .selected ==
                                                                    true
                                                                ? FontWeight.w400
                                                                : FontWeight.w400,
                                                          )),
                                                        ],
                                                      ),
                                                    ),
                                                  ),
                                                  index ==
                                                          (controller
                                                                  .externalMacMappingExternalList!
                                                                  .length -
                                                              1)
                                                      ? Container()
                                                      : Padding(
                                                          padding: const EdgeInsets
                                                                  .symmetric(
                                                              horizontal: Constant
                                                                      .SCREEN_PADDING -
                                                                  5),
                                                          child: Divider(
                                                            height: 5,
                                                            color: AppTheme
                                                                .lable_noramal,
                                                            thickness: 0.1,
                                                          ),
                                                        ),
                                                ],
                                              );
                                            },
                                          )),
                                          const SizedBox(
                                              height: Constant.SMALL_PADDING),
                                        ]),
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),

                      /*_______________ Assign Date ______________________*/

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
                            textEditingController: externalInventoryController
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

                        /*_______________ status ___________________________*/

                      /*  const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        InputTitleRequire(title: Strings.status, require: true),
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
                            value: externalInventoryController.selectedStatus,
                            items: externalInventoryController.statusList
                                ?.map((DropdownDetail value) {
                              return DropdownMenuItem<DropdownDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              externalInventoryController.selectedStatus =
                                  value as DropdownDetail?;
                            },
                            validator: (value) {
                              if (value == null ||
                                  externalInventoryController.selectedStatus ==
                                      null) {
                                return Strings.select_status;
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
      if (externalInventoryController.selectedInwordDateTime != null) {
        selectedDate = externalInventoryController.selectedInwordDateTime;
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
        externalInventoryController.selectedInwordDateTime = picked;
        externalInventoryController.update();
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
        externalInventoryController.selectedInwordDateTime!.year,
        externalInventoryController.selectedInwordDateTime!.month,
        externalInventoryController.selectedInwordDateTime!.day,
        picked.hour,
        picked.minute,
      );
      externalInventoryController.outwardDateController.text =
          externalInventoryController.dateFormat.format(dt);
      externalInventoryController.inwardDateTime =
          externalInventoryController.apiDateTimeFormat.format(dt);
      externalInventoryController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.assign_inventory_external_item,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (externalInventoryFormKey.currentState!.validate()) {
      externalInventoryController.assignPlanInventoryByPlanCallApi();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  validateSelection() {
    List<MACMappingExternalData> selectedItem = [];
    externalInventoryController.selectExternalMacExternalList!.clear();
    for (var element in externalInventoryController.externalMacMappingExternalList!) {
      if (element.selected == true) {
        // selectedItem.add(element);
        externalInventoryController.selectExternalMacExternalList!.add(element);
      }
    }
  }
}

