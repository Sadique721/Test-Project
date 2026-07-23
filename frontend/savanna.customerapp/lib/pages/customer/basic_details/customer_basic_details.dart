import 'dart:developer';

import 'package:savbill/pages/customer/basic_details/customer_basic_details_controller.dart';
import 'package:savbill/pages/customer/model/response/customer_category_res.dart';
import 'package:savbill/pages/customer/model/response/customer_department_list.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/staffs_by_service_area_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:country_picker/country_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class CustomerBasicDetails extends StatefulWidget {
  @override
  _CustomerBasicState createState() => _CustomerBasicState();
}

class _CustomerBasicState extends State<CustomerBasicDetails> {
  final customerBasicController = Get.put(CustomerBasicDetailController());
  final customerBasicFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<CustomerBasicDetailController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: customerBasicController.isLoading),
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
                  padding:  const EdgeInsets.only(
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING),
                  child:Form(
                    key: customerBasicFormKey,
                    autovalidateMode: customerBasicController.autoValidateMode,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.title, true),
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
                                      child: Text(Strings.title,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedBDType,
                                  items: customerBasicController.bdTypeList!
                                      .map((CustomerTitle value) {
                                    return DropdownMenuItem<CustomerTitle>(
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
                                    customerBasicController.selectedBDType =
                                    value as CustomerTitle?;
                                    customerBasicController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        customerBasicController.selectedBDType == null) {
                                      return Strings.please_select_title;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.firstname, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.firstname,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.fNameController,
                                  maxLength: 30,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_enter_first_name;
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.lastname, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.lastname,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.lastNameController,
                                  maxLength: 30,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_enter_last_name;
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.username, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.username,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.userNameController,
                                  maxLength: 30,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_enter_username;
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.fax_number, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.fax_number,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.faxController,
                                  maxLength: 50,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.phone,
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
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.pan_no, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.pan_no,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.panNumberController,
                                  maxLength: 50,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
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
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.contact_person, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.contact_person,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.contactPersonController,
                                  maxLength: 30,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_enter_contact_person;
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child:
                              titleWithRequireWidget(Strings.primary_mobile_number_new, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.mobile,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.primaryMobileNumberController,
                                  maxLength: 14,
                                  prefixIcon: InkWell(
                                    onTap: _showCountryCodeDialog,
                                    child: Container(
                                      decoration: BoxDecoration(
                                        color: AppTheme.colorWhite,
                                        border: Border(
                                          right: BorderSide(
                                              width: 0.5, color: AppTheme.colorIconGrey),
                                        ),
                                      ),
                                      margin: const EdgeInsets.only(
                                          left: 2.0, right: 8, top: 2, bottom: 2),
                                      width: 50.0,
                                      child: Center(
                                        child: CustomText(
                                          title: customerBasicController.countryCode,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ),
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.enter_mobile_no;
                                    }
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(
                                  Strings.secondary_mobile_number, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.mobile,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.secondMobileNumberController,
                                  maxLength: 14,
                                  prefixIcon: InkWell(
                                    onTap: _showCountryCodeDialog,
                                    child: Container(
                                      decoration: BoxDecoration(
                                        color: AppTheme.colorWhite,
                                        border: Border(
                                          right: BorderSide(
                                              width: 0.5, color: AppTheme.colorIconGrey),
                                        ),
                                      ),
                                      margin: const EdgeInsets.only(
                                          left: 2.0, right: 8, top: 2, bottom: 2),
                                      width: 50.0,
                                      child: Center(
                                        child: CustomText(
                                          title: customerBasicController.countryCode,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ),
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    // if (value!.isEmpty) {
                                    //   return Strings.enter_mobile_no;
                                    // }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.telephone, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.telephone,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.telephoneController,
                                  maxLength: 10,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
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
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.email, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.email,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.emailController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.emailAddress,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.enter_email;
                                    } else {
                                      if (value.isNotEmpty) {
                                        if (!value.isValidEmail()) {
                                          return Strings.enter_valid_email;
                                        }
                                      }
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.category, true),
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
                                      child: Text(Strings.customer_category,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedCustCategory,
                                  items: customerBasicController.custCategoryList!
                                      .map((CustomerCategoryDetail value) {
                                    return DropdownMenuItem<CustomerCategoryDetail>(
                                      value: value,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: CustomText(
                                          title: value.value!,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ), //Text(value.desig!),
                                      ),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    customerBasicController.selectedCustCategory =
                                    value as CustomerCategoryDetail?;
                                    customerBasicController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        customerBasicController.selectedCustCategory ==
                                            null) {
                                      return Strings.please_select_category;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.customer_type, false),
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
                                      child: Text(Strings.customer_type,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedCustType,
                                  items: customerBasicController.custTypeList!
                                      .map((CustomerTypeData value) {
                                    return DropdownMenuItem<CustomerTypeData>(
                                      value: value,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: CustomText(
                                          title: value.value!,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ), //Text(value.desig!),
                                      ),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    customerBasicController.selectedCustType =
                                    value as CustomerTypeData?;
                                    customerBasicController.update();
                                    customerBasicController.manageCustomerSubType();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        customerBasicController.selectedCustType != null &&
                            customerBasicController.custSubTypeDDl
                            ? Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(
                                  Strings.customer_sub_type, false),
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
                                      child: Text(Strings.customer_sub_type,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedCustomerSubType,
                                  items: customerBasicController
                                      .customerSubTypeList!.isEmpty
                                      ? [
                                    DropdownMenuItem<CustomerSubType>(
                                      value: null,
                                      enabled: false,
                                      child: CustomText(
                                        title: Strings.no_data_found,
                                        colors: AppTheme.title_dark,
                                      ), // Disable selection
                                    ),
                                  ] :customerBasicController.customerSubTypeList!
                                      .map((CustomerSubType value) {
                                    return DropdownMenuItem<CustomerSubType>(
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
                                    customerBasicController.selectedCustomerSubType =
                                    value as CustomerSubType?;
                                    customerBasicController.update();
                                  },
                                  validator: (value) {
                                    // if (value == null ||
                                    //     customerBasicController.selectedCustomerSubType ==
                                    //         null) {
                                    //   return Strings.please_select_cust_sub_type;
                                    // }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        )
                            : Container(),
                        customerBasicController.selectedCustType != null &&
                            customerBasicController.custSubTypeDDl == false
                            ? Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(
                                  Strings.customer_sub_type, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.customer_sub_type,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.customerSubType,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
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
                                  readOnly: false),
                            ),
                          ],
                        )
                            : Container(),
                        customerBasicController.selectedCustType != null
                            ? const SizedBox(height: Constant.MEDIUM_PADDING)
                            : Container(),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.customer_sector, false),
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
                                      child: Text(Strings.customer_sector,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedCustSector,
                                  items: customerBasicController.custSectorList!
                                      .map((CustomerSectorData value) {
                                    return DropdownMenuItem<CustomerSectorData>(
                                      value: value,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: CustomText(
                                          title: value.value!,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ), //Text(value.desig!),
                                      ),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    customerBasicController.selectedCustSector =
                                    value as CustomerSectorData?;
                                    customerBasicController.update();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        customerBasicController.selectedCustSector != null
                            ? Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(
                                  Strings.customer_sector_type, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.customer_sector_type,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.customerSectorType,
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
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        )
                            : Container(),
                        customerBasicController.selectedCustSector != null
                            ? const SizedBox(height: Constant.MEDIUM_PADDING)
                            : Container(),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.caf_no, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.enter_caf_no,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.cafNumberController,
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
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.dob, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.date_format,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.dobDateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  maxLength: 6,
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
                                  onTextFiledOnTap: () {
                                    selectDate(context, Strings.dob_date,
                                        DateTime(DateTime.now().year - 100), DateTime.now());
                                  },
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.dedicated_staff, false),
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
                                      child: Text(Strings.dedicated_staff,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value:
                                  customerBasicController.selectStaffsByServiceAreaData,
                                  items: customerBasicController
                                      .staffsByServiceAreaList!.isEmpty
                                      ? [
                                    DropdownMenuItem<StaffsByServiceAreaData>(
                                      value: null,
                                      enabled: false,
                                      child: CustomText(
                                        title: Strings.no_data_found,
                                        colors: AppTheme.title_dark,
                                      ), // Disable selection
                                    ),
                                  ]
                                      : customerBasicController.staffsByServiceAreaList!
                                      .map((StaffsByServiceAreaData value) {
                                    return DropdownMenuItem<StaffsByServiceAreaData>(
                                      value: value,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: CustomText(
                                          title: value.firstname!,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ), //Text(value.desig!),
                                      ),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    customerBasicController.selectStaffsByServiceAreaData =
                                    value as StaffsByServiceAreaData?;
                                    customerBasicController.update();
                                    // customerBasicController.manageCustomerSubType();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.sales_mark, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.sales_mark,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.salesMarkController,
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
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.renew_plan_limit, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.enter_renew_plan_limit,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.renewPlanLimitController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.done,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.department, false),
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
                                      child: Text(Strings.department,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectAllDepartmentData,
                                  items: customerBasicController.allDepartmentDataList!
                                      .map((DepartmentListData value) {
                                    return DropdownMenuItem<DepartmentListData>(
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
                                    customerBasicController.selectAllDepartmentData =
                                    value as DepartmentListData?;
                                    customerBasicController.update();
                                  },
                                  validator: (value) {
                                    // if (value == null ||
                                    //     customerBasicController.selectAllDepartmentData ==
                                    //         null) {
                                    //   return Strings.please_select_department;
                                    // }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                      ],
                    ),
                  )
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
                      title: Strings.update_customer,
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
      BuildContext context,
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;

    if (identity == Strings.dob_date) {
      if (customerBasicController.selectedDOBDate != null) {
        selectedDate = customerBasicController.selectedDOBDate;
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
      if (identity == Strings.dob_date) {
        // customerBasicController.selectedDOBDate = picked;
        customerBasicController.selectedDOBDate = picked;

        customerBasicController.customerDob = customerBasicController.apiDateFormat.format(picked);
        customerBasicController.dobDateController.text = customerBasicController.apiCustDateFormat.format(picked);
      }
      customerBasicController.update();
    }
  }


  _appBar() {
    return DynamicAppBar(Strings.update_customer, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (customerBasicFormKey.currentState!.validate()) {
      customerBasicController.updateCustomerDetailsApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
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

  void _showCountryCodeDialog() async {
    showCountryPicker(
      context: context,
      showPhoneCode: true,
      onSelect: (Country country) {
        customerBasicController.countryCode = "+${country.phoneCode}";
        customerBasicController.update();
      },
      countryListTheme: CountryListThemeData(
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(20.0),
          topRight: Radius.circular(20.0),
        ),
        inputDecoration: InputDecoration(
          hintText: Strings.search,
          prefixIcon: Icon(
            Icons.search,
            color: AppTheme.colorIconGrey,
          ),
          border: OutlineInputBorder(
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
            ),
          ),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(
              color: AppTheme.colorIconGrey,
              width: 1.0,
            ),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
            borderSide: BorderSide(color: AppTheme.colorIconGrey, width: 1.0),
          ),
        ),
      ),
    );
  }
}