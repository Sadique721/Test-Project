import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_charge/add_charge_controller.dart';
import 'package:savbill/pages/customer_charge/add_charge_list_item.dart';
import 'package:savbill/pages/customer_charge/request/create_cust_charge_req.dart';
import 'package:savbill/pages/customer_charge/request/serial_number_req.dart';
import 'package:savbill/pages/customer_charge/response/active_plan_list_res.dart';
import 'package:savbill/pages/shift_location/response/charge_by_type_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../customer/model/response/cust_plan_detail.dart';

class AddCharge extends StatefulWidget {
  @override
  _AddChargeState createState() => _AddChargeState();
}

class _AddChargeState extends State<AddCharge> {
  final addChargeController = Get.put(AddChargeController());
  final addChargeFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddChargeController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addChargeController.isLoading),
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
                      key: addChargeFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            const SizedBox(
                              height: Constant.SCREEN_PADDING +
                                  Constant.SMALL_PADDING,
                            ),
                            InputTitleRequire(
                                title: Strings.billableTo, require: false),
                            const SizedBox(
                              height: Constant.VERY_SMALL_PADDING,
                            ),
                            CoustomTextField(
                                labelText: Strings.select_billable_to,
                                hintColor: AppTheme.colorIconGrey,
                                textEditingController:
                                    addChargeController.billableToController,
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
                                  if (addChargeController
                                      .billableToController.text.isEmpty) {
                                    return Strings.select_bill_to;
                                  }
                                  return null;
                                },
                                onTextFiledOnTap: () {
                                  openParentCustomerScreen();
                                },
                                readOnly: true),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            InputTitleRequire(
                                title: Strings.payment_owner, require: true),
                            const SizedBox(
                              height: Constant.VERY_SMALL_PADDING,
                            ),
                            CoustomTextField(
                                labelText: Strings.select_staff,
                                hintColor: AppTheme.colorIconGrey,
                                textEditingController:
                                    addChargeController.paymentOwnerController,
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
                                  if (value!.isEmpty) {
                                    return Strings.select_payment_owner;
                                  }
                                  return null;
                                },
                                onTextFiledOnTap: () {
                                  openParentStaffScreen(Strings.payment_owner);
                                },
                                readOnly: true),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            InputTitleRequire(
                                title:
                                    addChargeController.isShowConnection == true
                                        ? Strings.connection_no
                                        : Strings.serial_no,
                                require: true),
                            const SizedBox(
                              height: Constant.VERY_SMALL_PADDING,
                            ),
                            addChargeController.isShowConnection == true
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
                                              Strings.select_connection_no,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: addChargeController
                                          .selectedConnectionNumber,
                                      items: addChargeController.activePlanList!
                                          .map((ActivePlanListDataList value) {
                                        return DropdownMenuItem<
                                            ActivePlanListDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.connectionNo!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {

                                        addChargeController
                                                .selectedConnectionNumber =
                                            value as ActivePlanListDataList?;

                                        addChargeController.serviceId =
                                            value!.serviceId;

                                        addChargeController
                                            .getChargeByType(value.serviceId);

                                        addChargeController.update();
                                      },
                                      validator: (value) {
                                        // need to add validation
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
                                          child: Text(Strings.select_serial_no,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: addChargeController
                                          .selectSerialNumber,
                                      items: addChargeController
                                          .serviceSerialNumbers
                                          .map((SerialNumberReq value) {
                                        return DropdownMenuItem<
                                            SerialNumberReq>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.serialNumber ?? "-",
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ), //Text(value.desig!),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        addChargeController.selectSerialNumber =
                                            value as SerialNumberReq?;
                                        // addChargeController.serviceId = value!.serialNumber;
                                        addChargeController.getChargeByType(
                                            addChargeController.serviceId);
                                        addChargeController.update();
                                      },
                                      validator: (value) {
                                        // need to add validation
                                        return null;
                                      },
                                    ),
                                  ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.charge,
                                            require: true),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.actual_price,
                                            require: true),
                                      ),
                                    ]),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: DropdownButtonHideUnderline(
                                          child: DropdownButtonFormField(
                                            icon: SvgPicture.asset(
                                              downArrowSvg,
                                              height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              color: AppTheme.colorBlack,
                                              fit: BoxFit.fill,
                                            ),
                                            decoration: Utils.ddlDecoration(),
                                            hint: Align(
                                                alignment: Alignment.centerLeft,
                                                child: Text(Strings.charge,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ))),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: true,
                                            isDense: true,
                                            value: addChargeController
                                                .selectedChargeList,
                                            items: addChargeController
                                                .chargeList!
                                                .map((Chargelist value) {
                                              return DropdownMenuItem<
                                                  Chargelist>(
                                                value: value,
                                                child: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
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
                                              addChargeController
                                                      .selectedChargeList =
                                                  value as Chargelist?;
                                              addChargeController
                                                      .actualPriceController
                                                      .text =
                                                  addChargeController
                                                      .selectedChargeList!.price
                                                      .toString();
                                              addChargeController.getChargeList(
                                                  addChargeController
                                                      .selectedChargeList!.id);

                                              addChargeController.update();
                                            },
                                            validator: (value) {
                                              // need to add validation
                                              return null;
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.actual_price,
                                              hintColor: AppTheme.colorIconGrey,
                                              fillColor:
                                                  AppTheme.colorLightGrey,
                                              textEditingController:
                                                  addChargeController
                                                      .actualPriceController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: true)),
                                    ]),
                              ],
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            /* addChargeController.selectedChargeList != null
                                ? const SizedBox(height: Constant.SMALL_PADDING)
                                : Container(),
                            addChargeController.selectedChargeList != null
                                ? Align(
                                    alignment: Alignment.topRight,
                                    child: CustomText(
                                      title:
                                          "${Strings.amount} : ${addChargeController.selectedChargeList!.price!}",
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    ),
                                  )
                                : Container(),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Flexible(
                                    flex: 1,
                                    child: InputTitleRequire(
                                        title: Strings.charge_type,
                                        require: false),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Flexible(
                                    flex: 2,
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
                                            child: Text(Strings.charge_type,
                                                style: TextStyle(
                                                  fontSize: AppTheme.medium,
                                                  color: AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ))),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: addChargeController
                                            .selectedChargeType,
                                        items: addChargeController.chargeTypeLst
                                            .map((String value) {
                                          return DropdownMenuItem<String>(
                                            value: value,
                                            child: Align(
                                              alignment: Alignment.centerLeft,
                                              child: CustomText(
                                                title: value,
                                                colors: AppTheme.colorBlack,
                                                textAlign: TextAlign.start,
                                                fontSize: AppTheme.small,
                                                fontWeight: FontWeight.w500,
                                              ), //Text(value.desig!),
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addChargeController
                                                  .selectedChargeType =
                                              value as String?;
                                          addChargeController.update();
                                        },
                                        validator: (value) {
                                          // need to add validation
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ]),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            addChargeController.selectedChargeType != null &&
                                    addChargeController.selectedChargeType!
                                        .equalsIgnoreCase(Strings.recurring)
                                ? Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                        Flexible(
                                          flex: 1,
                                          child: InputTitleRequire(
                                              title: Strings.recurring_month,
                                              require: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
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
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                      Strings.recurring_month,
                                                      style: TextStyle(
                                                        fontSize:
                                                            AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: addChargeController
                                                  .selectedRecurringMonth,
                                              items: addChargeController
                                                  .recurringMonthLst
                                                  .map((int value) {
                                                return DropdownMenuItem<int>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                        Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value.toString(),
                                                      colors:
                                                          AppTheme.colorBlack,
                                                      textAlign:
                                                          TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                          FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addChargeController
                                                        .selectedRecurringMonth =
                                                    value as int?;
                                                addChargeController.update();
                                              },
                                              validator: (value) {
                                                // need to add validation
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                      ])
                                : Container(),
                            addChargeController.selectedChargeType != null &&
                                    addChargeController.selectedChargeType!
                                        .equalsIgnoreCase(Strings.recurring)
                                ? const SizedBox(
                                    height: Constant.MEDIUM_PADDING)
                                : Container(),*/

                            addChargeController.staticIPAddress == true
                                ? Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      InputTitleRequire(
                                          title: Strings.static_ip,
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings.enter_static_ip,
                                          hintColor: AppTheme.colorIconGrey,
                                          textEditingController:
                                              addChargeController
                                                  .staticIpController,
                                          borderEnableColors:
                                              AppTheme.colorIconGrey,
                                          borderFocusColors:
                                              AppTheme.colorIconGrey,
                                          textColor: AppTheme.colorBlack,
                                          keyboardType: TextInputType.text,
                                          fontSize: AppTheme.small,
                                          textInputAction: TextInputAction.next,
                                          fontWeight: FontWeight.w500,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.MEDIUM_PADDING,
                                                  vertical:
                                                      Constant.MEDIUM_PADDING),
                                          borderCorner:
                                              Constant.BTN_ROUNDED_CORNER,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings.enter_static_ip;
                                            }
                                            return null;
                                          },
                                          onTextFiledOnTap: () {},
                                          readOnly: false),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                    ],
                                  )
                                : const SizedBox.shrink(),
                            Column(
                              children: [
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.plan, require: true),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.expiry_date,
                                            require: true),
                                      ),
                                    ]),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Flexible(
                                        flex: 1,
                                        child: DropdownButtonHideUnderline(
                                          child: DropdownButtonFormField(
                                            icon: SvgPicture.asset(
                                              downArrowSvg,
                                              height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              color: AppTheme.colorBlack,
                                              fit: BoxFit.fill,
                                            ),
                                            decoration: Utils.ddlDecoration(),
                                            hint: Align(
                                                alignment: Alignment.centerLeft,
                                                child: Text(Strings.plan,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ))),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: true,
                                            isDense: true,
                                            value: addChargeController
                                                .selectPlanMappingList,
                                            items: addChargeController
                                                .planMappingList!
                                                .map((PlanMappingDetail value) {
                                              return DropdownMenuItem<
                                                  PlanMappingDetail>(
                                                value: value,
                                                child: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: CustomText(
                                                    title:
                                                        "${value.planName} (${addChargeController.apiDateFormatChange.format(DateTime.parse(value.expiryDate.toString()))})",
                                                    colors: AppTheme.colorBlack,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight: FontWeight.w500,
                                                  ), //Text(value.desig!),
                                                ),
                                              );
                                            }).toList(),
                                            onChanged: (value) {
                                              addChargeController
                                                      .selectPlanMappingList =
                                                  value as PlanMappingDetail?;
                                              addChargeController
                                                      .expiryDateController
                                                      .text =
                                                  addChargeController
                                                      .apiDateFormatChange
                                                      .format(DateTime.parse(
                                                          addChargeController
                                                              .selectPlanMappingList!
                                                              .expiryDate
                                                              .toString()));
                                              addChargeController.update();
                                            },
                                            validator: (value) {
                                              // need to add validation
                                              return null;
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Flexible(
                                        flex: 1,
                                        child: CoustomTextField(
                                            labelText: Strings.expiry_date,
                                            suffixIcon: Padding(
                                              padding:
                                                  const EdgeInsetsDirectional
                                                          .all(
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
                                                addChargeController
                                                    .expiryDateController,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
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
                                                  DateTime(DateTime.now().year +
                                                      10));
                                            },
                                            borderCorner:
                                                Constant.INPUT_ROUNDED_CORNER,
                                            contentPadding:
                                                const EdgeInsets.symmetric(
                                                    horizontal:
                                                        Constant.LARGE_PADDING),
                                            readOnly: true),
                                      ),
                                    ]),
                              ],
                            ),

                            /*addChargeController.selectedPlan != null
                                ? const SizedBox(height: Constant.SMALL_PADDING)
                                : Container(),
                            addChargeController.selectedPlan != null
                                ? Align(
                                    alignment: Alignment.topRight,
                                    child: CustomText(
                                      title:
                                          "${Strings.validity} : ${addChargeController.selectedPlan!.validity}-${addChargeController.selectedPlan!.unitsOfValidity} ",
                                      //- ${addChargeController.selectedPlan!.unitsOfValidity!}
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    ),
                                  )
                                : Container(),*/
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Column(
                              children: [
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.new_price,
                                            require: false),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        flex: 1,
                                        child: InputTitleRequire(
                                            title: Strings.discount,
                                            require: false),
                                      ),
                                    ]),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                    children: [
                                      Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText: Strings.new_price,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  addChargeController
                                                      .newPriceController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings
                                                      .please_enter_new_price;
                                                }
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: false)),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText: Strings.enter_discount,
                                              hintColor: AppTheme.colorIconGrey,
                                              fillColor:
                                                  AppTheme.colorLightGrey,
                                              textEditingController:
                                                  addChargeController
                                                      .discountController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType:
                                                  TextInputType.number,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.next,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING,
                                                      vertical: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator: (String? value) {
                                                return null;
                                              },
                                              onTextFiledOnTap: () {},
                                              readOnly: true)),
                                    ]),
                              ],
                            ),
                            const SizedBox(height: Constant.MEDIUM_PADDING),
                            Align(
                              alignment: Alignment.centerRight,
                              child: InkWell(
                                onTap: () {
                                  String newPrice = addChargeController
                                      .newPriceController.text;
                                  if (addChargeController.selectedChargeList ==
                                          null ||
                                      addChargeController
                                              .selectPlanMappingList ==
                                          null ||
                                      newPrice.isEmpty) {
                                    Utils.showSnackbar(
                                        Strings.ERROR,
                                        "Please fill-up data!",
                                        AppTheme.colorWhite,
                                        AppTheme.colorRed);
                                    return;
                                  }
                                  double priceNew = double.parse(newPrice);
                                  double price = double.parse(
                                      addChargeController
                                          .selectedChargeList!.price!
                                          .toString());
                                  if (price > priceNew) {
                                    Utils.showSnackbar(
                                        Strings.ERROR,
                                        Strings
                                            .new_price_must_not_actual_charge_price,
                                        AppTheme.colorWhite,
                                        AppTheme.colorRed);
                                    return;
                                  }

                                  if (addChargeController.isShowConnection ==
                                      true) {
                                    addChargeController.connectionSerialNumber =
                                        addChargeController
                                            .selectedConnectionNumber!
                                            .connectionNo!;
                                  } else {
                                    addChargeController.connectionSerialNumber =
                                        addChargeController
                                            .selectSerialNumber!.serialNumber;
                                  }

                                  /*if (addChargeController.selectSerialNumber !=
                                      null) {
                                    connectionNumber = addChargeController
                                        .selectSerialNumber!.connectionNo;
                                  } else if (addChargeController
                                          .selectedConnectionNumber !=
                                      null) {
                                    connectionNumber = addChargeController
                                        .selectSerialNumber!.connectionNo;
                                  }*/

                                  addChargeController.expiryDateFormat =
                                      addChargeController.apiDateFormat.format(
                                          DateTime.parse(addChargeController
                                              .selectPlanMappingList!.expiryDate
                                              .toString()));

                                  addChargeController.dateExpiry =
                                      addChargeController.apiDateTimeFormat
                                          .format(DateTime.parse(
                                              addChargeController
                                                  .selectPlanMappingList!
                                                  .expiryDate
                                                  .toString()));

                                  addChargeController.custChargeDetails!
                                      .add(CustChargeDetailsPojoList(
                                    type: Strings.recurring,
                                    chargeid: addChargeController
                                        .selectedChargeList!.id,
                                    validity: addChargeController
                                        .selectPlanMappingList!.validity,
                                    price: double.parse(addChargeController
                                        .newPriceController.text
                                        .toString()),
                                    // chargeName: ,
                                    actualprice: double.parse(
                                        addChargeController
                                            .actualPriceController.text
                                            .toString()),
                                    chargeDate: addChargeController
                                        .apiDateFormat
                                        .format(DateTime.now()),
                                    planid: addChargeController
                                        .selectPlanMappingList!.planId,
                                    planName: addChargeController
                                        .selectPlanMappingList!.planName,
                                    unitsOfValidity: addChargeController
                                        .selectPlanMappingList!.unitsOfValidity,
                                    billingCycle: 1,
                                    paymentOwnerId:
                                        addChargeController.paymentOwnerId,
                                    discount: null,
                                    staticIPAdrress:
                                        addChargeController.staticIPAddress ==
                                                true
                                            ? addChargeController
                                                .staticIpController.text
                                                .toString()
                                            : null,
                                    expiry:
                                        addChargeController.expiryDateFormat,
                                    expiryDate: addChargeController.dateExpiry,
                                    connectionNo: addChargeController
                                        .connectionSerialNumber,
                                    // chargeName: addChargeController
                                    //     .selectedChargeList!.name,
                                  ));

                                  log("custChargeDetails===>>${jsonEncode(addChargeController.custChargeDetails!)}");

                                  addChargeController.selectedChargeList = null;
                                  addChargeController.selectPlanMappingList =
                                      null;
                                  addChargeController.selectedConnectionNumber =
                                      null;
                                  addChargeController.selectSerialNumber = null;

                                  addChargeController.billableToController
                                      .clear();
                                  addChargeController.paymentOwnerController
                                      .clear();
                                  addChargeController.actualPriceController
                                      .clear();
                                  addChargeController.staticIpController
                                      .clear();
                                  addChargeController.discountController
                                      .clear();
                                  addChargeController.expiryDateController
                                      .clear();
                                  addChargeController.newPriceController
                                      .clear();
                                  addChargeController.update();
                                },
                                child: CustomText(
                                  title: "+ Add Charge",
                                  colors: AppTheme.colorPrimary,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w600,
                                ),
                              ),
                            ),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            (addChargeController.custChargeDetails != null &&
                                    addChargeController
                                        .custChargeDetails!.isNotEmpty)
                                ? ListView.builder(
                                    physics:
                                        const NeverScrollableScrollPhysics(),
                                    shrinkWrap: true,
                                    itemCount: addChargeController
                                        .custChargeDetails!.length,
                                    itemBuilder:
                                        (BuildContext context, int index) {
                                      CustChargeDetailsPojoList item =
                                          addChargeController
                                              .custChargeDetails![index];
                                      return Container(
                                          margin: const EdgeInsets.only(
                                              top: Constant.VERY_SMALL_PADDING),
                                          child: AddChargeListItem(
                                              item: item,
                                              index: index,
                                              onDeleteTap: () {
                                                showDialog(
                                                  context: context,
                                                  builder:
                                                      (BuildContext context) {
                                                    return AlertDialogHelper(
                                                        title: Strings.app_name,
                                                        message:
                                                            Strings.msg_delete,
                                                        positiveBtnText:
                                                            Strings.ok,
                                                        negativeBtnText:
                                                            Strings.cancel,
                                                        positiveBtnClick: () {
                                                          Get.back();
                                                          addChargeController
                                                              .custChargeDetails!
                                                              .remove(item);
                                                          addChargeController
                                                              .update();
                                                        },
                                                        negativeBtnClick: () {
                                                          Get.back();
                                                        });
                                                  },
                                                );
                                              }));
                                    })
                                : Container(),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                          ]),
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
                        title: Strings.save,
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

  _appBar() {
    return DynamicAppBar(addChargeController.title, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (addChargeController.custChargeDetails != null &&
        addChargeController.custChargeDetails!.isNotEmpty) {
      if (addChargeController.from.equalsIgnoreCase(Strings.create_charge)) {
        addChargeController.changeOverRideReq();
      } else if (addChargeController.from
          .equalsIgnoreCase(Strings.change_plan)) {
        Get.back(result: addChargeController.custChargeDetails);
      }
    } else {
      Utils.showSnackbar(
          Strings.INFO,
          "Please add at-lease one charge detail item!",
          AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
  }

  openParentCustomerScreen() async {
    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_DETAIL: addChargeController.customerDetail!,
      Constant.CUSTOMER_TYPE: addChargeController.customerType!,
      Constant.SHIFT_LOCATION: Strings.shift_location,
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        addChargeController.selectedParentCustomer = data;
        addChargeController.billableToController.text = data.name!;
        addChargeController.billableCustomerId = data.id;
        addChargeController.update();
      }
    }
  }

  openParentStaffScreen(String? type) async {
    var result = await Get.to(ParentStaffList(), arguments: {});
    if (result != null) {
      ParentStaffUserlist data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.payment_owner)) {
          addChargeController.selectedPaymentOwner = data;
          addChargeController.paymentOwnerController.text = data.firstname!;
          addChargeController.paymentOwnerId = data.id;
        }
        addChargeController.update();
      }
    }
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.expiry_date) {
      if (addChargeController.selectedExpiryDateTime != null) {
        selectedDate = addChargeController.selectedExpiryDateTime;
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
        addChargeController.selectedExpiryDateTime = picked;
        addChargeController.update();
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
        addChargeController.selectedExpiryDateTime!.year,
        addChargeController.selectedExpiryDateTime!.month,
        addChargeController.selectedExpiryDateTime!.day,
        picked.hour,
        picked.minute,
      );
      addChargeController.expiryDateController.text =
          addChargeController.apiDateFormatChange.format(dt);
      addChargeController.expiryDateFormat =
          addChargeController.apiDateFormat.format(dt);
      addChargeController.update();
    }
  }
}
