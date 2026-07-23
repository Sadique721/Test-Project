
import 'dart:io';

import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/staffs_by_service_area_res.dart';
import 'package:savbill/pages/customer_caf/basic_caf_details/customer_caf_basic_details_controller.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:country_picker/country_picker.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

import '../../model/dropdown_detail.dart';
import '../response/get_building_management_res.dart';

class CustomerCAFBasicDetails extends StatefulWidget {
  @override
  _CustomerBasicState createState() => _CustomerBasicState();
}

class _CustomerBasicState extends State<CustomerCAFBasicDetails> implements LocationBtnAction  {
  final customerBasicController = Get.put(CustomerCAFBasicDetailController());
  final customerBasicFormKey = GlobalKey<FormState>();

  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (customerBasicController.checkBtnClickEvent) {
          // customerBasicController.setBtnClickEvent(false);
          locationPermissionStatus();
        }
        return;
      default:
        return;
    }
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
      child: GetBuilder<CustomerCAFBasicDetailController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
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
                        Row(children: [
                          CustomText(title: "${Strings.basic_details} :-",colors: AppTheme.colorBlack,fontSize: Constant.MEDIUM_PADDING,fontWeight: FontWeight.bold,),
                        ],),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        // Row(
                        //   crossAxisAlignment: CrossAxisAlignment.center,
                        //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        //   children: [
                        //     Flexible(
                        //       flex: 1,
                        //       child: titleWithRequireWidget(Strings.title, true),
                        //     ),
                        //     const SizedBox(
                        //       width: Constant.SMALL_PADDING,
                        //     ),
                        //     Flexible(
                        //       flex: 2,
                        //       child: DropdownButtonHideUnderline(
                        //         child: DropdownButtonFormField(
                        //           icon: SvgPicture.asset(
                        //             downArrowSvg,
                        //             height: Constant.DROP_DOWN_ARROW_W_H,
                        //             width: Constant.DROP_DOWN_ARROW_W_H,
                        //             color: AppTheme.colorBlack,
                        //             fit: BoxFit.fill,
                        //           ),
                        //           decoration: Utils.ddlDecoration(),
                        //           hint: Align(
                        //               alignment: Alignment.centerLeft,
                        //               child: Text(Strings.title,
                        //                   style: TextStyle(
                        //                     fontSize: AppTheme.medium,
                        //                     color: AppTheme.colorIconGrey,
                        //                     fontFamily: AppTheme.appFontName,
                        //                   ))),
                        //           style: AppTheme.dropdownTextStyle,
                        //           isExpanded: true,
                        //           isDense: true,
                        //           value: customerBasicController.selectedBDType,
                        //           items: customerBasicController.bdTypeList!
                        //               .map((CustomerTitle value) {
                        //             return DropdownMenuItem<CustomerTitle>(
                        //               value: value,
                        //               child: Align(
                        //                 alignment: Alignment.centerLeft,
                        //                 child: CustomText(
                        //                   title: value.text!,
                        //                   colors: AppTheme.colorBlack,
                        //                   textAlign: TextAlign.start,
                        //                   fontSize: AppTheme.small,
                        //                   fontWeight: FontWeight.w500,
                        //                 ), //Text(value.desig!),
                        //               ),
                        //             );
                        //           }).toList(),
                        //           onChanged: (value) {
                        //             customerBasicController.selectedBDType =
                        //             value as CustomerTitle?;
                        //             customerBasicController.update();
                        //           },
                        //           validator: (value) {
                        //             if (value == null ||
                        //                 customerBasicController.selectedBDType == null) {
                        //               return Strings.please_select_title;
                        //             }
                        //             return null;
                        //           },
                        //         ),
                        //       ),
                        //     ),
                        //   ],
                        // ),
                        // const SizedBox(
                        //   height: Constant.MEDIUM_PADDING,
                        // ),
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
                              child: titleWithRequireWidget(Strings.tin_no, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.tin_no,
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
                              child: titleWithRequireWidget(Strings.feasibility, false),
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
                                      child: Text(Strings.feasibility,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: customerBasicController.selectedFeasibilityData,
                                  items: customerBasicController.feasibilityList!
                                      .map((CustomerFeasibility value) {
                                    return DropdownMenuItem<CustomerFeasibility>(
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
                                    customerBasicController.selectedFeasibilityData =
                                    value as CustomerFeasibility?;
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
                              child: titleWithRequireWidget(Strings.email, false),
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
                                    // if (value!.isEmpty) {
                                    //   return Strings.enter_email;
                                    // } else {
                                    //   if (value.isNotEmpty) {
                                    //     if (!value.isValidEmail()) {
                                    //       return Strings.enter_valid_email;
                                    //     }
                                    //   }
                                    // }
                                    if (value!.isNotEmpty) {
                                      if (!value.isValidEmail()) {
                                        return Strings.enter_valid_email;
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
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(children: [
                          CustomText(title: "${Strings.location_details} :-",colors: AppTheme.colorBlack,fontSize: Constant.MEDIUM_PADDING,fontWeight: FontWeight.bold,),
                        ],),
                        const SizedBox(height: Constant.MEDIUM_PADDING),

                        /*----------------------Service Area ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.service_area, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.serviceAreaController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*----------------------unit no ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.unit_no, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.unitNoController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- branch partner ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.branch_partner, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.branchController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- Address ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.address, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.enter_address,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.addressController,
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
                        /*---------------------- Road Name ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.pincode, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.pincodeController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- Address/Building * ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.area, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.areaController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- sub area  ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.sub_area, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Expanded(
                              flex: 2,
                              child: DropdownSearch<SubAreaDataList>(
                                key: customerBasicController.subAreaDropDownKey,
                                mode: Mode.form,
                                selectedItem:
                                customerBasicController.selectedSubAreaData,
                                items: (filter, infiniteScrollProps) =>
                                customerBasicController.subAreaDataList!,
                                compareFn: (item1, item2) => item1.id == item2.id,
                                itemAsString: (item) => item.name!,
                                decoratorProps: DropDownDecoratorProps(
                                  baseStyle: TextStyle(
                                      color: AppTheme.title_dark, fontSize: AppTheme.small),
                                  // Change text color
                                  decoration: InputDecoration(
                                    hintText: Strings.sub_area,
                                    // ✅ Hint text for dropdown
                                    hintStyle: AppTheme.dropdownHintStyle,
                                    // labelStyle: TextStyle(color: Colors.black, fontSize: AppTheme.small),
                                    labelStyle: AppTheme.dropdownHintStyle,
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide:
                                      BorderSide(color: AppTheme.colorBlack, width: 0.8),
                                    ),
                                    focusColor: Colors.black,
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorPrimary, width: 0.8),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                        color: AppTheme.colorBlack,
                                        width: 1.0,
                                      ),
                                    ),
                                  ),
                                ),
                                popupProps: PopupProps.menu(
                                  showSearchBox: true,
                                  fit: FlexFit.loose,
                                  constraints: BoxConstraints(),
                                  menuProps: MenuProps(
                                    backgroundColor: Colors.white,
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                  ),
                                  searchFieldProps: TextFieldProps(
                                    decoration: InputDecoration(
                                      hintText: Strings.select_sub_area,
                                      hintStyle: AppTheme.dropdownHintStyle,
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorBlack, width: 0.8),
                                      ),
                                    ),
                                  ),
                                  listViewProps: ListViewProps(
                                    shrinkWrap: true,
                                  ),
                                ),
                                onChanged: (value) {
                                  customerBasicController.selectedSubAreaData = value;
                                  customerBasicController.getBuildingMgmtCall(value!.id);
                                  customerBasicController.selectedBuildingManagementData = null;
                                  customerBasicController.update();
                                  // customerBasicController.buildingManagementDataList!.clear();
                                  // customerBasicController.selectedBuildingManagementData = null;
                                  // customerBasicController.buildingNumberList!.clear();
                                  // customerBasicController.selectedBuildingNumber = null;
                                  // customerBasicController.idData = value!.id;
                                  // customerBasicController.selectedBuildingNumber = null;
                                  // customerBasicController.idData = value!.id;
                                  // customerBasicController.selectedMappingFrom = value.name;
                                  // customerBasicController.custServiceAreaId = value!.id;
                                },
                                validator: (value) {
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- Building Name  ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.building_name, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Expanded(
                              flex: 2,
                              child: DropdownSearch<BuildingManagementDataList>(
                                key: customerBasicController.buildingNameDropDownKey,
                                mode: Mode.form,
                                selectedItem:
                                customerBasicController.selectedBuildingManagementData,
                                items: (filter, infiniteScrollProps) =>
                                customerBasicController.buildingManagementDataList!,
                                compareFn: (item1, item2) => item1.buildingMgmtId == item2.buildingMgmtId,
                                itemAsString: (item) => item.buildingName!,
                                decoratorProps: DropDownDecoratorProps(
                                  baseStyle: TextStyle(
                                      color: AppTheme.title_dark, fontSize: AppTheme.small),
                                  // Change text color
                                  decoration: InputDecoration(
                                    hintText: Strings.building_name,
                                    // ✅ Hint text for dropdown
                                    hintStyle: AppTheme.dropdownHintStyle,
                                    // labelStyle: TextStyle(color: Colors.black, fontSize: AppTheme.small),
                                    labelStyle: AppTheme.dropdownHintStyle,
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide:
                                      BorderSide(color: AppTheme.colorBlack, width: 0.8),
                                    ),
                                    focusColor: Colors.black,
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorPrimary, width: 0.8),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                        color: AppTheme.colorBlack,
                                        width: 1.0,
                                      ),
                                    ),
                                  ),
                                ),
                                popupProps: PopupProps.menu(
                                  showSearchBox: true,
                                  fit: FlexFit.loose,
                                  constraints: BoxConstraints(),
                                  menuProps: MenuProps(
                                    backgroundColor: Colors.white,
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                  ),
                                  searchFieldProps: TextFieldProps(
                                    decoration: InputDecoration(
                                      hintText: Strings.select_sub_area,
                                      hintStyle: AppTheme.dropdownHintStyle,
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorBlack, width: 0.8),
                                      ),
                                    ),
                                  ),
                                  listViewProps: ListViewProps(
                                    shrinkWrap: true,
                                  ),
                                ),
                                onChanged: (value) {
                                  customerBasicController.selectedBuildingManagementData = value;
                                  customerBasicController.getBuildingMgmtNumbersCall(value!.buildingMgmtId);
                                  customerBasicController.selectedBuildingNumber = null;
                                  customerBasicController.update();
                                  // customerBasicController.buildingManagementDataList!.clear();
                                  // customerBasicController.selectedBuildingManagementData = null;
                                  // customerBasicController.buildingNumberList!.clear();
                                  // customerBasicController.selectedBuildingNumber = null;
                                  // customerBasicController.idData = value!.id;
                                  // customerBasicController.idData = value!.buildingMgmtId;
                                  // customerBasicController.selectedMappingFrom = value.name;

                                },
                                validator: (value) {
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- Building Number  ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.building_no, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Expanded(
                              flex: 2,
                              child: DropdownSearch<String>(
                                key: customerBasicController.buildingNumberDropDownKey,
                                mode: Mode.form,
                                selectedItem:
                                customerBasicController.selectedBuildingNumber,
                                items: (filter, infiniteScrollProps) =>
                                customerBasicController.buildingNumberList!,
                                compareFn: (item1, item2) => item1 == item2,
                                itemAsString: (item) => item,
                                decoratorProps: DropDownDecoratorProps(
                                  baseStyle: TextStyle(
                                      color: AppTheme.title_dark, fontSize: AppTheme.small),
                                  // Change text color
                                  decoration: InputDecoration(
                                    hintText: Strings.building_no,
                                    // ✅ Hint text for dropdown
                                    hintStyle: AppTheme.dropdownHintStyle,
                                    // labelStyle: TextStyle(color: Colors.black, fontSize: AppTheme.small),
                                    labelStyle: AppTheme.dropdownHintStyle,
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide:
                                      BorderSide(color: AppTheme.colorBlack, width: 0.8),
                                    ),
                                    focusColor: Colors.black,
                                    focusedBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorPrimary, width: 0.8),
                                    ),
                                    enabledBorder: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                        color: AppTheme.colorBlack,
                                        width: 1.0,
                                      ),
                                    ),
                                  ),
                                ),
                                popupProps: PopupProps.menu(
                                  showSearchBox: true,
                                  fit: FlexFit.loose,
                                  constraints: BoxConstraints(),
                                  menuProps: MenuProps(
                                    backgroundColor: Colors.white,
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                  ),
                                  searchFieldProps: TextFieldProps(
                                    decoration: InputDecoration(
                                      hintText: Strings.select_sub_area,
                                      hintStyle: AppTheme.dropdownHintStyle,
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorBlack, width: 0.8),
                                      ),
                                    ),
                                  ),
                                  listViewProps: ListViewProps(
                                    shrinkWrap: true,
                                  ),
                                ),
                                onChanged: (value) {
                                  customerBasicController.selectedBuildingNumber = value;
                                  customerBasicController.update();
                                  // customerBasicController.buildingManagementDataList!.clear();
                                  // customerBasicController.selectedBuildingManagementData = null;
                                  // customerBasicController.buildingNumberList!.clear();
                                  // customerBasicController.selectedBuildingNumber = null;
                                  // customerBasicController.idData = value!.id;
                                  // customerBasicController.selectedBuildingNumber = null;
                                  // customerBasicController.idData = value!.id;
                                  // customerBasicController.selectedMappingFrom = value.name;
                                },
                                validator: (value) {
                                  return null;
                                },
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- Ward * ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.city, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.cityController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- District * ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.state, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.stateController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        /*---------------------- City * ------------------------*/
                        Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.country, true),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                // labelText: Strings.,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.countryController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fontSize: AppTheme.small,
                                  fillColor: AppTheme.colorLightGrey,
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
                              child: titleWithRequireWidget(Strings.landmark, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.enter_landmark,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.landmarkController,
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
                              child: titleWithRequireWidget(Strings.house_no, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.enter_house_no,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.landmarkController,
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
                      /*  Row(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 1,
                              child: titleWithRequireWidget(Strings.latitude, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.latitude,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.latController,
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
                              child: titleWithRequireWidget(Strings.longitude, false),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              flex: 2,
                              child: CoustomTextField(
                                  labelText: Strings.longitude,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                  customerBasicController.longController,
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
                                  readOnly: true),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.MEDIUM_PADDING),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            InkWell(
                              onTap: () {
                                //get current location
                                locationPermissionStatus();
                              },
                              child: Material(
                                elevation: 1.5,
                                color: AppTheme.custNearLocationLight,
                                shape: RoundedRectangleBorder(
                                    borderRadius:
                                    BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                                child: Container(
                                  height: Constant.BTN_HEIGHT_M,
                                  width: Constant.BTN_HEIGHT_M,
                                  alignment: Alignment.center,
                                  padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
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
                                    borderRadius:
                                    BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
                                child: Container(
                                  height: Constant.BTN_HEIGHT_M,
                                  width: Constant.BTN_HEIGHT_M,
                                  alignment: Alignment.center,
                                  padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
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
                        ),*/
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
      customerBasicController.setBtnClickEvent(true);
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
        customerBasicController.setBtnClickEvent(true);
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
        customerBasicController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    customerBasicController.isLoading = true;
    customerBasicController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        customerBasicController.setBtnClickEvent(false);
        customerBasicController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        customerBasicController.latController.text =
            currentPosition.latitude.toString();
        customerBasicController.longController.text =
            currentPosition.longitude.toString();
        customerBasicController.update();
      } else {
        customerBasicController.isLoading = false;
        customerBasicController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      customerBasicController.isLoading = false;
      customerBasicController.update();
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

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        customerBasicController.selectedLocation = data;
        customerBasicController.update();
        customerBasicController.getLocationToLatLong();
      }
    }
  }
}