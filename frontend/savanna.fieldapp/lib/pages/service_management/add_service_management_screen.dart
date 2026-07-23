import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/service_management/add_service_managment_controller.dart';
import 'package:savbill/pages/service_management/response/get_plan_by_service_id_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import '../../widgets/simple_button.dart';

class AddServiceManagement extends StatefulWidget {
  @override
  _AddServiceManagementState createState() => _AddServiceManagementState();
}

class _AddServiceManagementState extends State<AddServiceManagement> {
  final addServiceController = Get.put(AddServiceManagementController());
  final addServiceManagementFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<AddServiceManagementController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: addServiceController.isLoading),
      ]);
    });
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
                    padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
                    child: Form(
                      key: addServiceManagementFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          CustomText(
                              title: Strings.plan_detail,
                              colors: AppTheme.colorBlack,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium + 1,
                              fontWeight: FontWeight.w500),
                          const SizedBox(
                            width: Constant.VERY_SMALL_PADDING,
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          InputTitleRequire(
                              title: Strings.plan_category, require: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.plan_category,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: addServiceController
                                  .planCategoryNameController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              fillColor: Colors.black12,
                              fontSize: AppTheme.small,
                              textInputAction: TextInputAction.next,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING,
                                  vertical: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.bill_to, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.select_bill_to,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  addServiceController.billToController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              keyboardType: TextInputType.number,
                              fillColor: Colors.black12,
                              fontSize: AppTheme.small,
                              textInputAction: TextInputAction.done,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING,
                                  vertical: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onChanged: (value) {},
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              readOnly: true),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          InputTitleRequire(
                              title: Strings.billableTo, require: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.select_billable_to,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  addServiceController.billableToController,
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
                                // if (addServiceController
                                //     .billableToController.text.isEmpty) {
                                //   return Strings.select_bill_to;
                                // }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                openParentCustomerScreen();
                              },
                              readOnly: true),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
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
                              value: addServiceController
                                  .selectServicesByServiceAreaData,
                              items: addServiceController
                                  .servicesByServiceAreaDataList!
                                  .map((ServicesByServiceAreaDataList value) {
                                return DropdownMenuItem<
                                    ServicesByServiceAreaDataList>(
                                  value: value,
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: CustomText(
                                      title: value.name!,
                                      colors: AppTheme.colorBlack,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                    ), //Text(value.desig!),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addServiceController
                                        .selectServicesByServiceAreaData =
                                    value as ServicesByServiceAreaDataList?;
                                addServiceController.serviceNameValue =
                                    value!.name;
                                addServiceController.planByServiceIdList!
                                    .clear();
                                addServiceController.selectedPlanByServiceId =
                                    null;
                                addServiceController.planList!.clear();
                                addServiceController.planId = null;
                                addServiceController.enterValidityController
                                    .clear();
                                addServiceController.offerPriceController
                                    .clear();
                                addServiceController.getPlanServicesDetail();
                                addServiceController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    addServiceController
                                            .selectServicesByServiceAreaData ==
                                        null) {
                                  return Strings.select_service;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          InputTitleRequire(title: Strings.plan, require: true),
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
                                  Strings.select_plan,
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
                                  addServiceController.selectedPlanByServiceId,
                              items: addServiceController.planByServiceIdList!
                                  .map((PlanByServiceId value) {
                                return DropdownMenuItem<PlanByServiceId>(
                                  value: value,
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: CustomText(
                                      title: value.name!,
                                      colors: AppTheme.colorBlack,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                    ), //Text(value.desig!),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addServiceController.selectedPlanByServiceId =
                                    value as PlanByServiceId?;
                                addServiceController.planList!.clear();
                                addServiceController.planId = value!.id;
                                addServiceController
                                    .getPlanDetailFromPlanId(value.id);
                                addServiceController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    addServiceController
                                            .selectedPlanByServiceId ==
                                        null) {
                                  return Strings.select_plan;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            children: [
                              Flexible(
                                child: CoustomTextField(
                                    labelText: Strings.enter_validity,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: addServiceController
                                        .enterValidityController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                child: CoustomTextField(
                                    labelText: Strings.old_offer_price,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: addServiceController
                                        .offerPriceController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              Expanded(
                                flex: 1,
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.discount_type,
                                        require: false),
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
                                            Strings.select_discount_type,
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
                                        value: addServiceController
                                            .selectedDiscountType,
                                        items: addServiceController
                                            .discountTypeList!
                                            .map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Align(
                                              alignment: Alignment.centerLeft,
                                              child: CustomText(
                                                title: value.text!,
                                                colors: AppTheme.colorBlack,
                                                textAlign: TextAlign.start,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                              ), //Text(value.desig!),
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addServiceController
                                                  .selectedDiscountType =
                                              value as DropdownDetail?;
                                          if (value!.text!.equalsIgnoreCase(
                                              Strings.onetime)) {
                                            addServiceController
                                                .expiryDateController
                                                .clear();
                                          }
                                          addServiceController.update();
                                        },
                                        validator: (value) {
                                          return null;
                                        },
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Expanded(
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.discount,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_discount,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            addServiceController
                                                .discountController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        keyboardType: TextInputType.number,
                                        textInputAction: TextInputAction.next,
                                        fontWeight: FontWeight.w500,
                                        inputFormatters: [
                                          FilteringTextInputFormatter.allow(
                                              RegExp("[0-9.]")),
                                        ],
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING,
                                                vertical:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        onTextFiledOnTap: () {},
                                        readOnly: false),
                                  ],
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            children: [
                              addServiceController.selectedDiscountType !=
                                          null &&
                                      addServiceController
                                          .selectedDiscountType!.text!
                                          .equalsIgnoreCase(Strings.recurring)
                                  ? Flexible(
                                      flex: 1,
                                      child: CoustomTextField(
                                          labelText: Strings.expiry_date,
                                          suffixIcon: Padding(
                                            padding:
                                                const EdgeInsetsDirectional.all(
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
                                              addServiceController
                                                  .expiryDateController,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .please_select_expiry_date;
                                            }
                                            return null;
                                          },
                                          onTextFiledOnTap: () {
                                            selectDate(
                                                Strings.expiry_date,
                                                DateTime(
                                                    DateTime.now().year - 10),
                                                DateTime(
                                                    DateTime.now().year + 10));
                                          },
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.LARGE_PADDING),
                                          readOnly: true),
                                    )
                                  : const SizedBox.shrink(),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Expanded(
                                flex: 1,
                                child: InkWell(
                                  onTap: () {
                                    addServiceController.isTrial =
                                        !(addServiceController.isTrial!);
                                    addServiceController.update();
                                  },
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    children: [
                                      Padding(
                                        padding: const EdgeInsets.only(
                                            top: Constant.SMALL_PADDING,
                                            left: Constant.SMALL_PADDING,
                                            bottom: Constant.SMALL_PADDING),
                                        child: SizedBox(
                                          width: 15,
                                          height: 10,
                                          child: Checkbox(
                                            value: addServiceController.isTrial,
                                            activeColor: AppTheme.colorPrimary,
                                            onChanged: (value) {
                                              addServiceController.isTrial =
                                                  !(addServiceController
                                                      .isTrial!);
                                              addServiceController.update();
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        child: CustomText(
                                          title: Strings.trial_plan,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.normal,
                                        ),
                                      )
                                    ],
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
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
                        title: Strings.add_service,
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

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }

  openParentCustomerScreen() async {
    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_DETAIL: addServiceController.customerDetail!,
      Constant.CUSTOMER_TYPE: addServiceController.customerType!,
      Constant.SHIFT_LOCATION: Strings.shift_location,
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        addServiceController.selectedParentCustomer = data;
        addServiceController.billableToController.text = data.name!;
        addServiceController.billableCustomerId = data.id;
        addServiceController.update();
      }
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.create_service, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.expiry_date) {
      if (addServiceController.selectedExpiryDateTime != null) {
        selectedDate = addServiceController.selectedExpiryDateTime;
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
      if (identity == Strings.expiry_date) {
        addServiceController.selectedExpiryDateTime = picked;
        addServiceController.update();
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
        addServiceController.selectedExpiryDateTime!.year,
        addServiceController.selectedExpiryDateTime!.month,
        addServiceController.selectedExpiryDateTime!.day,
        picked.hour,
        picked.minute,
      );
      addServiceController.expiryDateController.text =
          addServiceController.apiDateFormatChange.format(dt);
      addServiceController.expiryDiscountDateFormat =
          addServiceController.apiDateFormat.format(dt);
      addServiceController.update();
    }
  }

  validateForm() {
    if (addServiceManagementFormKey.currentState!.validate()) {
      addServiceController.addServiceApi();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
