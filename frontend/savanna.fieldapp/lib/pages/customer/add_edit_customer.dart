import 'dart:async';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer/add_edit_customer_controller.dart';
import 'package:savbill/pages/customer/charge_list_item.dart';
import 'package:savbill/pages/customer/model/response/network_devices_by_device_type_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/plan_group_mapping_list_item.dart';
import 'package:savbill/pages/customer/individual_plan_item.dart';
import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/mac_address_item.dart';
import 'package:savbill/pages/customer/model/charge_data.dart';
import 'package:savbill/pages/customer/model/individual_plan_data.dart';
import 'package:savbill/pages/customer/model/response/bill_to_res.dart';
import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/charge_list_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/customer_category_res.dart';
import 'package:savbill/pages/customer/model/response/customer_department_list.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/customer_status_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/inside_outside_valley_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/partner_list_res.dart';
import 'package:savbill/pages/customer/model/response/payment_mode_list_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/customer/model/response/staffs_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer/model/response/valley_type_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
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
import 'package:country_picker/country_picker.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:im_stepper/stepper.dart';

class AddEditCustomer extends StatefulWidget {
  @override
  _AddEditCustomerState createState() => _AddEditCustomerState();
}

class _AddEditCustomerState extends State<AddEditCustomer>
    with WidgetsBindingObserver
    implements LocationBtnAction {
  final ScrollController _scrollController = ScrollController();
  final addEditCustomerController = Get.put(AddEditCustomerController());

  final basicDetailFormKey = GlobalKey<FormState>();
  final kycDetailFormKey = GlobalKey<FormState>();
  final contactDetailFormKey = GlobalKey<FormState>();

  // final locationDetailFormKey = GlobalKey<FormState>();
  final businessPartnerFormKey = GlobalKey<FormState>();
  final paymentFormKey = GlobalKey<FormState>();
  final presentAddressFormKey = GlobalKey<FormState>();
  final paymentAddressFormKey = GlobalKey<FormState>();
  final permanentAddressFormKey = GlobalKey<FormState>();
  final planDetailFormKey = GlobalKey<FormState>();

  final additionalServiceFormKey = GlobalKey<FormState>();
  final radiusServiceFormKey = GlobalKey<FormState>();
  final chargeDetailsFormKey = GlobalKey<FormState>();
  final macMapppingFormKey = GlobalKey<FormState>();

  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    log("selectedInvoiceType>>>${addEditCustomerController.selectedInvoiceType}");
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    addEditCustomerController.setBtnClickEvent(false);
    super.dispose();
  }
  void scrollToTop() {
    _scrollController.animateTo(
      0, // scroll offset
      duration: const Duration(milliseconds: 300),
      curve: Curves.easeInOut,
    );
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (addEditCustomerController.checkBtnClickEvent) {
          addEditCustomerController.setBtnClickEvent(false);
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
    showBackDialog();
  }

  showBackDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: Strings.msg_back,
            positiveBtnText: Strings.yes,
            negativeBtnText: Strings.no,
            positiveBtnClick: () {
              Get.back();
              Get.back();
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<AddEditCustomerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            resizeToAvoidBottomInset: false,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addEditCustomerController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return Stack(
      children: [
        Container(
          height: 70,
          alignment: Alignment.topCenter,
          child: NumberStepper(
            numbers: addEditCustomerController.data,
            enableStepTapping: false,
            enableNextPreviousButtons: false,
            activeStep: addEditCustomerController.activeStep,
            stepRadius: 14,
            lineLength: 80,
            lineColor: AppTheme.colorPrimary,
            activeStepColor: AppTheme.colorPrimary,
            activeStepBorderColor: AppTheme.colorPrimary,
            activeStepBorderWidth: 2,
            activeStepBorderPadding: 3,
            stepColor: AppTheme.colorDisableGray,
            numberStyle: TextStyle(
              color: AppTheme.colorBlack,
              fontSize: AppTheme.large,
              fontWeight: FontWeight.bold,
              height: 1,
              fontFamily: AppTheme.appFontName,
              decoration: TextDecoration.none,
            ),
            stepReachedAnimationEffect: Curves.decelerate,
            onStepReached: (index) {
              addEditCustomerController.activeStep = index;
              addEditCustomerController.update();
            },
          ),
        ),
        Container(
          margin: const EdgeInsets.only(
            top: 70,
          ),
          alignment: Alignment.topCenter,
          child: AnimatedPadding(
            duration: Duration(milliseconds: 100),
            padding: EdgeInsets.only(
                bottom: MediaQuery.of(context).viewInsets.bottom),
            child: SingleChildScrollView(
              controller: _scrollController,
              physics: const ScrollPhysics(),
              child: Padding(
                padding: const EdgeInsets.only(
                    top: Constant.VERY_SMALL_PADDING,
                    left: Constant.SCREEN_PADDING,
                    right: Constant.SCREEN_PADDING,
                    bottom: Constant.SCREEN_PADDING * 3),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      CustomText(
                        title: getTitle(),
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w500,
                      ),
                      addEditCustomerController.activeStep == 0
                          ? basicDetailForm()
                          : Container(),
                      // addEditCustomerController.activeStep == 1
                      //     ? kycDetailForm()
                      //     : Container(),
                      // addEditCustomerController.activeStep == 2
                      //     ? contactDetailForm()
                      //     : Container(),
                      // addEditCustomerController.activeStep == 3
                      //     ? locationDetailForm()
                      //     : Container(),
                      // addEditCustomerController.activeStep == 1
                      //     ? businessPartnerForm()
                      //     : Container(),
                      // addEditCustomerController.activeStep == 2
                      //     ? paymentForm()
                      //     : Container(),
                      addEditCustomerController.activeStep == 1
                          ? presentAddressForm()
                          : Container(),
                      /*addEditCustomerController.activeStep == 2
                          ? paymentAddressForm()
                          : Container(),
                      addEditCustomerController.activeStep == 3
                          ? permanentAddressForm()
                          : Container(),*/
                      addEditCustomerController.activeStep == 2
                          ? planDetailForm()
                          : Container(),
                      addEditCustomerController.activeStep == 3
                          ? additionalServiceForm()
                          : Container(),
                      /* addEditCustomerController.activeStep == 4
                          ? radiusServiceForm()
                          : Container(),
                      addEditCustomerController.activeStep == 5
                          ? chargeDetailForm()
                          : Container(),
                      addEditCustomerController.activeStep == 6
                          ? macMapppingDetailForm()
                          : Container(),*/
                    ]),
              ),
            ),
          ),
        ),
        Positioned(
          child: Align(
            alignment: FractionalOffset.bottomCenter,
            child: Row(
              children: [
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      if (addEditCustomerController.activeStep > 0) {
                        addEditCustomerController.activeStep--;
                        addEditCustomerController.update();
                        // setState(() {
                        //   activeStep--;
                        // });
                      } else {
                        Get.back();
                      }
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorBlack,
                    borderColors: AppTheme.colorBlack,
                    child: CustomText(
                      title: Strings.back,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
                Expanded(
                  child: SimpleButton(
                    onTap: () {
                      if (addEditCustomerController.activeStep == 3) {
                        validateMacMapppingDetail();
                      } else {
                        validateForm();
                        scrollToTop();
                      }
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: addEditCustomerController.activeStep == 3
                          ? Strings.submit
                          : Strings.next,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }

  String getTitle() {
    String strTitle = "";
    switch (addEditCustomerController.activeStep) {
      case 0:
        strTitle = Strings.basic_details;
        break;
      /* case 1:
        strTitle = Strings.kyc_details;
        break;
      case 2:
        strTitle = Strings.contact_details;
        break;
      case 3:
        strTitle = Strings.subscriber_location_details;
        break;
      case 1:
        strTitle = Strings.business_partner_details;
        break;
      case 2:
        strTitle = Strings.payment_details;
        break;*/
      case 1:
        strTitle = Strings.present_address_details;
        break;
      /*case 2:
        strTitle = Strings.payment_address_details;
        break;
      case 3:
        strTitle = Strings.permanent_address_details;
        break;*/
      case 2:
        strTitle = Strings.plan_details;
        break;
      case 3:
        strTitle = Strings.network_location_details;
        break;
      /*case 4:
        strTitle = Strings.radius_service_Details;
        break;
      case 5:
        strTitle = Strings.charge_details;
        break;
      case 6:
        strTitle = Strings.mac_mpappping_list;
        break;*/
    }
    return strTitle;
  }

  void _showCountryCodeDialog() async {
    showCountryPicker(
      context: context,
      showPhoneCode: true,
      onSelect: (Country country) {
        addEditCustomerController.countryCode = "+${country.phoneCode}";
        addEditCustomerController.update();
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

  basicDetailForm() {
    return Form(
      key: basicDetailFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
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
          //           value: addEditCustomerController.selectedBDType,
          //           items: addEditCustomerController.bdTypeList!
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
          //             addEditCustomerController.selectedBDType =
          //                 value as CustomerTitle?;
          //             addEditCustomerController.update();
          //           },
          //           validator: (value) {
          //             if (value == null ||
          //                 addEditCustomerController.selectedBDType == null) {
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
                        addEditCustomerController.fnameController,
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
                        addEditCustomerController.lnameController,
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
          (addEditCustomerController.type != null &&
                  addEditCustomerController.type!
                      .equalsIgnoreCase(Strings.postpaid))
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(Strings.bill_day, true),
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
                              child: Text(Strings.bill_day,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selectedBillDay,
                          items: addEditCustomerController.billDayList!
                              .map((int value) {
                            return DropdownMenuItem<int>(
                              value: value,
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: CustomText(
                                  title: value.toString(),
                                  colors: AppTheme.colorBlack,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                ), //Text(value.desig!),
                              ),
                            );
                          }).toList(),
                          onChanged: (value) {
                            addEditCustomerController.selectedBillDay =
                                value as int?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                addEditCustomerController.selectedBillDay ==
                                    null) {
                              return Strings.please_select_bill_day;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),
          (addEditCustomerController.type != null &&
                  addEditCustomerController.type!
                      .equalsIgnoreCase(Strings.postpaid))
              ? const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                )
              : Container(),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              SizedBox(
                width: Constant.SMALL_PADDING,
                child: Checkbox(
                  value:
                      addEditCustomerController.isCredentialMatchWithAccountNo,
                  activeColor: AppTheme.colorPrimary,
                  onChanged: (value) {
                    addEditCustomerController.isCredentialMatchWithAccountNo =
                        !addEditCustomerController
                            .isCredentialMatchWithAccountNo;
                    addEditCustomerController.usernameController.clear();
                    addEditCustomerController.passwordController.clear();
                    addEditCustomerController.update();
                  },
                ),
              ),
              const SizedBox(width: Constant.MEDIUM_PADDING),
              CustomText(
                title: Strings.is_credential_match_with_account_no,
                colors: AppTheme.lable_noramal,
                fontSize: AppTheme.medium,
                fontWeight: FontWeight.normal,
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addEditCustomerController.isCredentialMatchWithAccountNo == false
              ? Row(
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
                              addEditCustomerController.usernameController,
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
                          readOnly: addEditCustomerController.action!
                                  .equalsIgnoreCase(Strings.add)
                              ? false
                              : true),
                    ),
                  ],
                )
              : SizedBox.shrink(),
          addEditCustomerController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          addEditCustomerController.isCredentialMatchWithAccountNo == false
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(Strings.password, true),
                    ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.password,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.passwordController,
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
                              return Strings.please_enter_password;
                            }
                            return null;
                          },
                          onTextFiledOnTap: () {},
                          readOnly: false,
                          obscureText: true),
                    ),
                  ],
                )
              : SizedBox.shrink(),
          addEditCustomerController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
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
                        addEditCustomerController.faxNumberController,
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
                    labelText: Strings.enter_pan_no,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.vatController,
                    maxLength: 11,
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
                      // if(value!.isEmpty){
                      //   return Strings.enter_pan_no;
                      // }else if (value.length<11 ){
                      //   return Strings.enter_valid_pan_no;
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
                child: titleWithRequireWidget(Strings.calendar_type, true),
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
                      child: Text(
                        Strings.calendar_type,
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
                    value: addEditCustomerController.selectedCalenderType,
                    items: addEditCustomerController.calenderTypeList!
                        .map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
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
                      addEditCustomerController.selectedCalenderType =
                          value as DropdownDetail?;
                      if (addEditCustomerController.custCategoryList == null ||
                          addEditCustomerController.custCategoryList!.isEmpty) {
                        addEditCustomerController.getCustomerCategory();
                      }
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController.selectedCalenderType ==
                              null) {
                        return Strings.please_select_calendar_type;
                      }
                      return null;
                    },
                  ),
                ),
              ),
            ],
          ),

          /*  const SizedBox(height: Constant.MEDIUM_PADDING),
          addEditCustomerController.selectedParentCustomer != null
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(Strings.invoice_type, true),
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
                              child: Text(Strings.invoice_type,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selectedInvoiceType,
                          items: addEditCustomerController.invoiceTypeList!
                              .map((DropdownDetail value) {
                            return DropdownMenuItem<DropdownDetail>(
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
                            addEditCustomerController.selectedInvoiceType =
                                value as DropdownDetail?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                addEditCustomerController.selectedInvoiceType ==
                                    null) {
                              return Strings.please_select_invoice_type;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),*/

          /* addEditCustomerController.selectedParentCustomer != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.pop, false),
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
                        child: Text(Strings.pop,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedPop,
                    items: addEditCustomerController.popList!
                        .map((PopDetail value) {
                      return DropdownMenuItem<PopDetail>(
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
                      addEditCustomerController.selectedPop =
                          value as PopDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      return null;
                    },
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          /* Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.valley_type, false),
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
                        child: Text(Strings.valley_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedValleyType,
                    items: addEditCustomerController.valleyTypeList!
                        .map((ValleyType value) {
                      return DropdownMenuItem<ValleyType>(
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
                      addEditCustomerController.selectedValleyType =
                          value as ValleyType?;
                      addEditCustomerController.update();
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
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id == 447)
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child:
                          titleWithRequireWidget(Strings.inside_valley, false),
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
                              child: Text(Strings.inside_valley,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selectedInsideValley,
                          items: addEditCustomerController.insideValleyList!
                              .map((InsideOutsideValleyData value) {
                            return DropdownMenuItem<InsideOutsideValleyData>(
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
                            addEditCustomerController.selectedInsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),

          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id == 447)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id == 448)
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child:
                          titleWithRequireWidget(Strings.outside_valley, false),
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
                              child: Text(Strings.outside_valley,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value:
                              addEditCustomerController.selectedOutsideValley,
                          items: addEditCustomerController.outsideValleyList!
                              .map((InsideOutsideValleyData value) {
                            return DropdownMenuItem<InsideOutsideValleyData>(
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
                            addEditCustomerController.selectedOutsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id == 448)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),*/

          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(
                    Strings.primary_mobile_number_new, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.mobile,
                    hintColor: AppTheme.colorIconGrey,
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly, // allows only 0-9
                    ],
                    textEditingController:
                        addEditCustomerController.mobileController,
                    maxLength: 9,
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
                            title: addEditCustomerController.countryCode,
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
                      } else if (value.length != 9) {
                        return Strings.mobile_number_must_be_ten_digit;
                      } else {
                        return null;
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
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly, // allows only 0-9
                    ],
                    textEditingController:
                        addEditCustomerController.secondaryMobileController,
                    maxLength: 9,
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
                            title: addEditCustomerController.countryCode,
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
                    onTextValidator: (value) {
                      if (value!.isEmpty) {
                        return null;
                      } else if (value.isNotEmpty && value.length != 9) {
                        return Strings.mobile_number_must_be_ten_digit;
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
                        addEditCustomerController.telephoneController,
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
                        addEditCustomerController.emailController,
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
                child: titleWithRequireWidget(Strings.contact_person, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_contact_number,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.contactPersonController,
                    maxLength: 10,
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
                        return Strings.please_contact_number;
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
                    titleWithRequireWidget(Strings.customer_new_category, true),
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
                    value: addEditCustomerController.selectedCustCategory,
                    items: addEditCustomerController.custCategoryList!
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
                      addEditCustomerController.selectedCustCategory =
                          value as CustomerCategoryDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController.selectedCustCategory ==
                              null) {
                        return Strings.select_customer_category;
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
                    value: addEditCustomerController.selectedCustType,
                    items: addEditCustomerController.custTypeList!
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
                      addEditCustomerController.selectedCustType =
                          value as CustomerTypeData?;
                      addEditCustomerController.update();
                      addEditCustomerController.manageCustomerSubType();
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
          addEditCustomerController.selectedCustType != null &&
                  addEditCustomerController.custSubTypeDDl
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(
                          Strings.customer_sub_type_new, false),
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
                          value:
                              addEditCustomerController.selectedCustomerSubType,
                          items: addEditCustomerController
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
                                ]
                              : addEditCustomerController.customerSubTypeList!
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
                            addEditCustomerController.selectedCustomerSubType =
                                value as CustomerSubType?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),
          addEditCustomerController.selectedCustType != null &&
                  addEditCustomerController.custSubTypeDDl == false
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(
                          Strings.customer_sub_type_new, false),
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
                              addEditCustomerController.customerSubType,
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
          addEditCustomerController.selectedCustType != null
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
                    value: addEditCustomerController.selectedCustSector,
                    items: addEditCustomerController.custSectorList!
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
                      addEditCustomerController.selectedCustSector =
                          value as CustomerSectorData?;
                      addEditCustomerController.update();
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
          addEditCustomerController.selectedCustSector != null
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
                              addEditCustomerController.customerSectorType,
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
          addEditCustomerController.selectedCustSector != null
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
                        addEditCustomerController.cafNoController,
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
                        addEditCustomerController.dobDateController,
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
                child: titleWithRequireWidget(Strings.status, true),
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
                        child: Text(Strings.status,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedStatus,
                    items: addEditCustomerController.statusList!
                        .map((CustomerStatusDetail value) {
                      return DropdownMenuItem<CustomerStatusDetail>(
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
                      addEditCustomerController.selectedStatus =
                          value as CustomerStatusDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController.selectedStatus == null) {
                        return Strings.please_select_status;
                      } else {
                        return null;
                      }
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
                child: titleWithRequireWidget(Strings.parent_customer, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.select_a_customer,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.parentCustomerController,
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
                      return null;
                    },
                    onTextFiledOnTap: () {
                      openParentCustomerScreen(Strings.parent_customer);
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
                        addEditCustomerController.selectStaffsByServiceAreaData,
                    items: addEditCustomerController.staffsByServiceAreaList!
                        .map((StaffsByServiceAreaData value) {
                      return DropdownMenuItem<StaffsByServiceAreaData>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value.displayName!,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addEditCustomerController.selectStaffsByServiceAreaData =
                          value as StaffsByServiceAreaData?;
                      addEditCustomerController.update();
                      // addEditCustomerController.manageCustomerSubType();
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
                child: titleWithRequireWidget(Strings.customer_type, true),
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
                    value: addEditCustomerController.selectParentCustType,
                    items: addEditCustomerController.parentCustTypeList!
                        .map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
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
                      addEditCustomerController.selectParentCustType =
                          value as DropdownDetail?;
                      if (addEditCustomerController.allDepartmentDataList ==
                              null ||
                          addEditCustomerController
                              .allDepartmentDataList!.isEmpty) {
                        addEditCustomerController.getDepartmentListAPI();
                      }
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController.selectParentCustType ==
                              null) {
                        return Strings.please_select_customer;
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
                        addEditCustomerController.saleRemarkController,
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
                        addEditCustomerController.renewPlanLimitController,
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
                child: titleWithRequireWidget(Strings.parent_experience, false),
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
                        child: Text(Strings.parent_experience,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedParentExperience,
                    items: addEditCustomerController.parentExperienceList!
                        .map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
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
                      addEditCustomerController.selectedParentExperience =
                          value as DropdownDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      // if (value == null ||
                      //     addEditCustomerController.selectedParentExperience ==
                      //         null) {
                      //   return Strings.please_select_parent_experience;
                      // }
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
                    value: addEditCustomerController.selectAllDepartmentData,
                    items: addEditCustomerController.allDepartmentDataList!
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
                      addEditCustomerController.selectAllDepartmentData =
                          value as DepartmentListData?;
                      // if (addEditCustomerController.servicesAreaList == null ||
                      //     addEditCustomerController.servicesAreaList!.isEmpty) {
                      //   addEditCustomerController.getServiceArea();
                      // }
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      // if (value == null ||
                      //     addEditCustomerController.selectAllDepartmentData ==
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
    );
  }

  kycDetailForm() {
    return Form(
      key: kycDetailFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                child: titleWithRequireWidget(Strings.gst, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.gst,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.gstController,
                    maxLength: 50,
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
                        addEditCustomerController.vatController,
                    maxLength: 50,
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
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.national_id, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.national_id,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.nationalIdController,
                    maxLength: 30,
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
                child: titleWithRequireWidget(Strings.passport_no, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.passport_no,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.passportController,
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
                child: titleWithRequireWidget(Strings.vat, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.vat,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.tinController,
                    maxLength: 50,
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
        ],
      ),
    );
  }

  contactDetailForm() {
    return Form(
      key: contactDetailFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                child: titleWithRequireWidget(Strings.mobile, true),
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
                        addEditCustomerController.mobileController,
                    maxLength: 9,
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
                            title: addEditCustomerController.countryCode,
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
                        addEditCustomerController.telephoneController,
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
                        addEditCustomerController.emailController,
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
                child: titleWithRequireWidget(Strings.category, false),
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
                        child: Text(Strings.customer_new_category,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedCustCategory,
                    items: addEditCustomerController.custCategoryList!
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
                      addEditCustomerController.selectedCustCategory =
                          value as CustomerCategoryDetail?;
                      addEditCustomerController.update();
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
                    value: addEditCustomerController.selectedCustType,
                    items: addEditCustomerController.custTypeList!
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
                      addEditCustomerController.selectedCustType =
                          value as CustomerTypeData?;
                      addEditCustomerController.update();
                      addEditCustomerController.manageCustomerSubType();
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
          addEditCustomerController.selectedCustType != null &&
                  addEditCustomerController.custSubTypeDDl
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(
                          Strings.customer_sub_type_new, false),
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
                          value:
                              addEditCustomerController.selectedCustomerSubType,
                          items: addEditCustomerController.customerSubTypeList!
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
                            addEditCustomerController.selectedCustomerSubType =
                                value as CustomerSubType?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),
          addEditCustomerController.selectedCustType != null &&
                  addEditCustomerController.custSubTypeDDl == false
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(
                          Strings.customer_sub_type_new, false),
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
                              addEditCustomerController.customerSubType,
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
          addEditCustomerController.selectedCustType != null
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
                    value: addEditCustomerController.selectedCustSector,
                    items: addEditCustomerController.custSectorList!
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
                      addEditCustomerController.selectedCustSector =
                          value as CustomerSectorData?;
                      addEditCustomerController.update();
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
          addEditCustomerController.selectedCustSector != null
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
                              addEditCustomerController.customerSectorType,
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
          addEditCustomerController.selectedCustSector != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
        ],
      ),
    );
  }

  /* locationDetailForm() {
    return Form(
      key: locationDetailFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                        addEditCustomerController.latController,
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
                        addEditCustomerController.longController,
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
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }*/

  businessPartnerForm() {
    return Form(
      key: businessPartnerFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                child: titleWithRequireWidget(Strings.partner, true),
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
                        child: Text(Strings.partner,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedPartner,
                    items: addEditCustomerController.partnerList!
                        .map((PartnerDetail value) {
                      return DropdownMenuItem<PartnerDetail>(
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
                      addEditCustomerController.selectedPartner =
                          value as PartnerDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController.selectedPartner == null) {
                        return Strings.select_partner;
                      }
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
    );
  }

  paymentForm() {
    return Form(
      key: paymentFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                child: titleWithRequireWidget(Strings.amount, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.amount,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.amountController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.number,
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
                child: titleWithRequireWidget(Strings.reference_no, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.reference_no,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.referenceNoController,
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
                child: titleWithRequireWidget(Strings.payment_date, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.payment_date,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.paymentDateController,
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
                      selectDate(context, Strings.payment_date,
                          DateTime(DateTime.now().year - 10), DateTime.now());
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
                child: titleWithRequireWidget(Strings.payment_mode, false),
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
                        child: Text(Strings.payment_mode,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedPayMode,
                    items: addEditCustomerController.payModeList!
                        .map((PaymentModeDetail value) {
                      return DropdownMenuItem<PaymentModeDetail>(
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
                      addEditCustomerController.selectedPayMode =
                          value as PaymentModeDetail?;
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      // need to add validation
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
    );
  }

  presentAddressForm() {
    return Form(
      key: presentAddressFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                child: titleWithRequireWidget(Strings.service_area, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: DropdownSearch<ServicesAreaDetail>(
                  key: addEditCustomerController.serviceAreaDropDownKey,
                  mode: Mode.form,
                  selectedItem: addEditCustomerController.selPresentServiceArea,
                  items: (filter, infiniteScrollProps) =>
                      addEditCustomerController.servicesAreaList!,
                  compareFn: (item1, item2) => item1.id == item2.id,
                  itemAsString: (item) => item.name!,
                  decoratorProps: DropDownDecoratorProps(
                    baseStyle: TextStyle(
                        color: AppTheme.title_dark, fontSize: AppTheme.small),
                    // Change text color
                    decoration: InputDecoration(
                      hintText: Strings.service_area,
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
                        hintText: Strings.service_area,
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
                    List<int>? serviceAreaId = [];
                    addEditCustomerController.selPresentServiceArea =
                        value;
                    addEditCustomerController.selPresentPincode = null;
                    addEditCustomerController.pincodeList!.clear();
                    addEditCustomerController.serviceAreaId = value!.id;
                    addEditCustomerController.update();
                    serviceAreaId.add(value.id!);
                    addEditCustomerController
                        .getAllBranchesByServiceAreaData(serviceAreaId);
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCustomerController.selPresentServiceArea ==
                            null) {
                      return Strings.select_service_area;
                    }
                    return null;
                  },
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
                child: titleWithRequireWidget(Strings.branch_partner, true),
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
                      child: Text(
                        Strings.branch_partner,
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
                    value: addEditCustomerController
                        .selectBranchesByServiceAreaData,
                    items: addEditCustomerController.branchesByServiceAreaList!
                        .map((BranchesByServiceAreaDataList value) {
                      return DropdownMenuItem<BranchesByServiceAreaDataList>(
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
                      addEditCustomerController
                              .selectBranchesByServiceAreaData =
                          value as BranchesByServiceAreaDataList?;
                      addEditCustomerController.getServiceAreaDetail();
                      addEditCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCustomerController
                                  .selectBranchesByServiceAreaData ==
                              null) {
                        return Strings.select_branch_partner;
                      }
                      return null;
                    },
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addEditCustomerController.areaDetail != null &&
                  addEditCustomerController.areaDetail!.serviceAreaType !=
                      null &&
                  addEditCustomerController.areaDetail!.serviceAreaType!
                      .equalsIgnoreCase("private")
              ? Row(
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
                              child: Text(Strings.select_block_no,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selectedBlockNo,
                          items:
                              addEditCustomerController.blockNoOptions.isEmpty
                                  ? [
                                      DropdownMenuItem<int>(
                                        value: null,
                                        enabled: false,
                                        child: CustomText(
                                          title: Strings.no_data_found,
                                          colors: AppTheme.title_dark,
                                        ), // Disable selection
                                      ),
                                    ]
                                  : addEditCustomerController.blockNoOptions
                                      .map((int value) {
                                      return DropdownMenuItem<int>(
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
                            addEditCustomerController.selectedBlockNo =
                                int.parse(value.toString());
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            if (value == null) {
                              return Strings.please_select_block_no;
                            }
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : SizedBox.shrink(),
          addEditCustomerController.areaDetail != null &&
                  addEditCustomerController.areaDetail!.serviceAreaType !=
                      null &&
                  addEditCustomerController.areaDetail!.serviceAreaType!
                      .equalsIgnoreCase("private")
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
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
                    labelText: Strings.address,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.presentAddController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.text,
                    maxLength: 250,
                    fontSize: AppTheme.small,
                    textInputAction: TextInputAction.next,
                    fontWeight: FontWeight.w500,
                    contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                        vertical: Constant.MEDIUM_PADDING),
                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                    onTextValidator: (String? value) {
                      if (value!.isEmpty) {
                        return Strings.enter_address;
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
                child: titleWithRequireWidget(Strings.pincode, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              // Flexible(
              //   flex: 2,
              //   child: DropdownButtonHideUnderline(
              //     child: DropdownButtonFormField(
              //       icon: SvgPicture.asset(
              //         downArrowSvg,
              //         height: Constant.DROP_DOWN_ARROW_W_H,
              //         width: Constant.DROP_DOWN_ARROW_W_H,
              //         color: AppTheme.colorBlack,
              //         fit: BoxFit.fill,
              //       ),
              //       decoration: Utils.ddlDecoration(),
              //       hint: Align(
              //           alignment: Alignment.centerLeft,
              //           child: Text(Strings.pincode,
              //               style: TextStyle(
              //                 fontSize: AppTheme.medium,
              //                 color: AppTheme.colorIconGrey,
              //                 fontFamily: AppTheme.appFontName,
              //               ))),
              //       style: AppTheme.dropdownTextStyle,
              //       isExpanded: true,
              //       isDense: true,
              //       value: addEditCustomerController.selPresentPincode,
              //       items: addEditCustomerController.pincodeList!
              //           .map((PincodeDetail value) {
              //         return DropdownMenuItem<PincodeDetail>(
              //           value: value,
              //           child: Align(
              //             alignment: Alignment.centerLeft,
              //             child: CustomText(
              //               title: value.pincode!,
              //               colors: AppTheme.colorBlack,
              //               textAlign: TextAlign.start,
              //               fontSize: AppTheme.small,
              //               fontWeight: FontWeight.w500,
              //             ), //Text(value.desig!),
              //           ),
              //         );
              //       }).toList(),
              //       onChanged: (value) {
              //         addEditCustomerController.selPresentPincode =
              //             value as PincodeDetail?;
              //         addEditCustomerController.update();
              //         addEditCustomerController.getPinCodeToAreaData(
              //             addEditCustomerController
              //                 .selPresentPincode!.pincodeid!,
              //             "Present");
              //       },
              //       validator: (value) {
              //         if (value == null ||
              //             addEditCustomerController.selPresentPincode == null) {
              //           return Strings.select_pincode;
              //         }
              //         return null;
              //       },
              //     ),
              //   ),
              // ),

              Expanded(
                flex: 2,
                child: DropdownSearch<PincodeDetail>(
                  key: addEditCustomerController.pinCodeDropDownKey,
                  mode: Mode.form,
                  selectedItem: addEditCustomerController.selPresentPincode,
                  items: (filter, infiniteScrollProps) =>
                      addEditCustomerController.pincodeList!,
                  compareFn: (item1, item2) => item1.id == item2.id,
                  itemAsString: (item) => item.pincode!,
                  decoratorProps: DropDownDecoratorProps(
                    baseStyle: TextStyle(
                        color: AppTheme.title_dark, fontSize: AppTheme.small),
                    // Change text color
                    decoration: InputDecoration(
                      hintText: Strings.pincode,
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
                        hintText: Strings.pincode,
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
                    addEditCustomerController.selPresentPincode = value;
                    addEditCustomerController.update();
                    addEditCustomerController.getPinCodeToAreaData(
                        addEditCustomerController.selPresentPincode!.pincodeid!,
                        "Present");
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCustomerController.selPresentPincode == null) {
                      return Strings.select_pincode;
                    }
                    return null;
                  },
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
                child: titleWithRequireWidget(Strings.area, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              // Flexible(
              //   flex: 2,
              //   child: IgnorePointer(
              //     ignoring: false,
              //     child: DropdownButtonHideUnderline(
              //       child: DropdownButtonFormField(
              //         icon: SvgPicture.asset(
              //           downArrowSvg,
              //           height: Constant.DROP_DOWN_ARROW_W_H,
              //           width: Constant.DROP_DOWN_ARROW_W_H,
              //           color: AppTheme.colorBlack,
              //           fit: BoxFit.fill,
              //         ),
              //         decoration: Utils.ddlDecoration(),
              //         hint: Align(
              //             alignment: Alignment.centerLeft,
              //             child: Text(Strings.area,
              //                 style: TextStyle(
              //                   fontSize: AppTheme.medium,
              //                   color: AppTheme.colorIconGrey,
              //                   fontFamily: AppTheme.appFontName,
              //                 ))),
              //         style: AppTheme.dropdownTextStyle,
              //         isExpanded: true,
              //         isDense: true,
              //         value: addEditCustomerController.selPresentArea,
              //         items: addEditCustomerController.areaList!
              //             .map((PincodeAreaDetail value) {
              //           return DropdownMenuItem<PincodeAreaDetail>(
              //             value: value,
              //             child: Align(
              //               alignment: Alignment.centerLeft,
              //               child: CustomText(
              //                 title: value.name!,
              //                 colors: AppTheme.colorBlack,
              //                 textAlign: TextAlign.start,
              //                 fontSize: AppTheme.small,
              //                 fontWeight: FontWeight.w500,
              //               ),
              //             ),
              //           );
              //         }).toList(),
              //         onChanged: (value) {
              //           addEditCustomerController.selPresentArea =
              //               value as PincodeAreaDetail?;
              //           addEditCustomerController.update();
              //           addEditCustomerController.getAreaDetail(
              //               addEditCustomerController.selPresentArea!.id!,
              //               "Present");
              //         },
              //         validator: (value) {
              //           if (value == null ||
              //               addEditCustomerController.selPresentArea == null) {
              //             return Strings.select_area;
              //           }
              //           return null;
              //         },
              //       ),
              //     ),
              //   ),
              // ),

              Expanded(
                flex: 2,
                child: DropdownSearch<PincodeAreaDetail>(
                  key: addEditCustomerController.areaDropDownKey,
                  mode: Mode.form,
                  selectedItem: addEditCustomerController.selPresentArea,
                  items: (filter, infiniteScrollProps) =>
                      addEditCustomerController.areaList!,
                  compareFn: (item1, item2) => item1.id == item2.id,
                  itemAsString: (item) => item.name!,
                  decoratorProps: DropDownDecoratorProps(
                    baseStyle: TextStyle(
                        color: AppTheme.title_dark, fontSize: AppTheme.small),
                    // Change text color
                    decoration: InputDecoration(
                      hintText: Strings.area,
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
                        hintText: Strings.area,
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
                    addEditCustomerController.selPresentArea =
                        value as PincodeAreaDetail?;
                    addEditCustomerController.update();
                    addEditCustomerController.getAreaDetail(
                        addEditCustomerController.selPresentArea!.id!,
                        "Present");
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCustomerController.selPresentArea == null) {
                      return Strings.select_area;
                    }
                    return null;
                  },
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
                  child: titleWithRequireWidget(Strings.city, true),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.city,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPresentCity,
                        items: addEditCustomerController.cityList!
                            .map((CityDetail value) {
                          return DropdownMenuItem<CityDetail>(
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
                          addEditCustomerController.selPresentCity =
                              value as CityDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCustomerController.selPresentCity ==
                                  null) {
                            return Strings.select_city;
                          }
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
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
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.state,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPresentState,
                        items: addEditCustomerController.stateList!
                            .map((StateDetail value) {
                          return DropdownMenuItem<StateDetail>(
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
                          addEditCustomerController.selPresentState =
                              value as StateDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCustomerController.selPresentState ==
                                  null) {
                            return Strings.select_state;
                          }
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
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
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.country,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPresentCountry,
                        items: addEditCustomerController.countryList!
                            .map((CountryDetail value) {
                          return DropdownMenuItem<CountryDetail>(
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
                          addEditCustomerController.selPresentCountry =
                              value as CountryDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCustomerController.selPresentCountry ==
                                  null) {
                            return Strings.select_country;
                          }
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
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
                        addEditCustomerController.landmarkController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.text,
                    maxLength: 250,
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
                child: titleWithRequireWidget(Strings.valley_type, false),
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
                        child: Text(Strings.valley_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selectedValleyType,
                    items: addEditCustomerController.valleyTypeList!
                        .map((ValleyType value) {
                      return DropdownMenuItem<ValleyType>(
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
                      addEditCustomerController.selectedValleyType =
                          value as ValleyType?;
                      addEditCustomerController.update();
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
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id ==
                      Constant.INSIDE_VALLEY)
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child:
                          titleWithRequireWidget(Strings.inside_valley, false),
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
                              child: Text(Strings.inside_valley,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selectedInsideValley,
                          items: addEditCustomerController.insideValleyList!
                              .map((InsideOutsideValleyData value) {
                            return DropdownMenuItem<InsideOutsideValleyData>(
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
                            addEditCustomerController.selectedInsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),

          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id ==
                      Constant.INSIDE_VALLEY)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id ==
                      Constant.OUTSIDE_VALLEY)
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child:
                          titleWithRequireWidget(Strings.outside_valley, false),
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
                              child: Text(Strings.outside_valley,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value:
                              addEditCustomerController.selectedOutsideValley,
                          items: addEditCustomerController.outsideValleyList!
                              .map((InsideOutsideValleyData value) {
                            return DropdownMenuItem<InsideOutsideValleyData>(
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
                            addEditCustomerController.selectedOutsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                    ),
                  ],
                )
              : Container(),
          (addEditCustomerController.selectedValleyType != null &&
                  addEditCustomerController.selectedValleyType!.id ==
                      Constant.OUTSIDE_VALLEY)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),

          // const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
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
                        addEditCustomerController.latController,
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
                        addEditCustomerController.longController,
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
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }

  paymentAddressForm() {
    return Form(
      key: paymentAddressFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.MEDIUM_PADDING),
          sameAsPresentAddWidget(Strings.payment_address_details),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.address, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.address,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.paymentAddController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.text,
                    maxLength: 250,
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
                child: titleWithRequireWidget(Strings.pincode, false),
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
                        child: Text(Strings.pincode,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPaymentPincode,
                    items: addEditCustomerController.paymentPincodeList!
                        .map((PincodeDetail value) {
                      return DropdownMenuItem<PincodeDetail>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value.pincode!,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addEditCustomerController.selPaymentPincode =
                          value as PincodeDetail?;
                      addEditCustomerController.update();
                      addEditCustomerController.getPinCodeToAreaData(
                          addEditCustomerController
                              .selPaymentPincode!.pincodeid!,
                          Strings.payment_address_details);

                      /*   addEditCustomerController.getPinCodeToAreaList(
                          addEditCustomerController
                              .selPaymentPincode!.pincodeid!,
                          Strings.payment_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                child: titleWithRequireWidget(Strings.area, false),
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
                        child: Text(Strings.area,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPaymentArea,
                    items: addEditCustomerController.paymentAreaList!
                        .map((PincodeAreaDetail value) {
                      return DropdownMenuItem<PincodeAreaDetail>(
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
                      addEditCustomerController.selPaymentArea =
                          value as PincodeAreaDetail?;
                      addEditCustomerController.update();

                      addEditCustomerController.getAreaDetail(
                          addEditCustomerController.selPaymentArea!.id!,
                          Strings.payment_address_details);
                      /*   addEditCustomerController
                          .setCityData(Strings.payment_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                  child: titleWithRequireWidget(Strings.city, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.city,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPaymentCity,
                        items: addEditCustomerController.paymentCityList!
                            .map((CityDetail value) {
                          return DropdownMenuItem<CityDetail>(
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
                          addEditCustomerController.selPaymentCity =
                              value as CityDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.state, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                    flex: 2,
                    child: IgnorePointer(
                      ignoring: true,
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
                              child: Text(Strings.state,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addEditCustomerController.selPaymentState,
                          items: addEditCustomerController.paymentStateList!
                              .map((StateDetail value) {
                            return DropdownMenuItem<StateDetail>(
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
                            addEditCustomerController.selPaymentState =
                                value as StateDetail?;
                            addEditCustomerController.update();
                          },
                          validator: (value) {
                            // need to add validation
                            return null;
                          },
                        ),
                      ),
                    )),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.country, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.country,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPaymentCountry,
                        items: addEditCustomerController.paymentCountryList!
                            .map((CountryDetail value) {
                          return DropdownMenuItem<CountryDetail>(
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
                          addEditCustomerController.selPaymentCountry =
                              value as CountryDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }

  permanentAddressForm() {
    return Form(
      key: permanentAddressFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.MEDIUM_PADDING),
          sameAsPresentAddWidget(Strings.permanent_address_details),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.address, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.address,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.permanentAddController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.text,
                    maxLength: 250,
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
                child: titleWithRequireWidget(Strings.pincode, false),
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
                        child: Text(Strings.pincode,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPermanentPincode,
                    items: addEditCustomerController.permanentPincodeList!
                        .map((PincodeDetail value) {
                      return DropdownMenuItem<PincodeDetail>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value.pincode!,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addEditCustomerController.selPermanentPincode =
                          value as PincodeDetail?;
                      addEditCustomerController.update();
                      addEditCustomerController.getPinCodeToAreaData(
                          addEditCustomerController
                              .selPermanentPincode!.pincodeid!,
                          Strings.permanent_address_details);

                      /* addEditCustomerController.getPinCodeToAreaList(
                          addEditCustomerController
                              .selPermanentPincode!.pincodeid!,
                          Strings.permanent_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                child: titleWithRequireWidget(Strings.area, false),
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
                        child: Text(Strings.area,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPermanentArea,
                    items: addEditCustomerController.permanentAreaList!
                        .map((PincodeAreaDetail value) {
                      return DropdownMenuItem<PincodeAreaDetail>(
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
                      addEditCustomerController.selPermanentArea =
                          value as PincodeAreaDetail?;
                      addEditCustomerController.update();
                      addEditCustomerController.getAreaDetail(
                          addEditCustomerController.selPermanentArea!.id!,
                          Strings.permanent_address_details);
                      /*addEditCustomerController
                          .setCityData(Strings.permanent_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                  child: titleWithRequireWidget(Strings.city, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.city,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentCity,
                        items: addEditCustomerController.permanentCityList!
                            .map((CityDetail value) {
                          return DropdownMenuItem<CityDetail>(
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
                          addEditCustomerController.selPermanentCity =
                              value as CityDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.state, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.state,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentState,
                        items: addEditCustomerController.permanentStateList!
                            .map((StateDetail value) {
                          return DropdownMenuItem<StateDetail>(
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
                          addEditCustomerController.selPermanentState =
                              value as StateDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.country, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.country,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentCountry,
                        items: addEditCustomerController.permanentCountryList!
                            .map((CountryDetail value) {
                          return DropdownMenuItem<CountryDetail>(
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
                          addEditCustomerController.selPermanentCountry =
                              value as CountryDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }

  planDetailForm() {
    return Form(
      key: planDetailFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      mainAxisAlignment: MainAxisAlignment.start,
                      children: [
                        InputTitleRequire(
                            title: Strings.plan_offer_price, require: false),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.plan_offer_price,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: addEditCustomerController
                                .planOfferPriceController,
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
                            readOnly: true)
                      ]),
                ),
                addEditCustomerController.showDiscountPrice
                    ? const SizedBox(
                        width: Constant.SMALL_PADDING,
                      )
                    : Container(),
                addEditCustomerController.showDiscountPrice
                    ? Flexible(
                        flex: 1,
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              InputTitleRequire(
                                  title: Strings.new_price_with_discount,
                                  require: true),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              CoustomTextField(
                                  labelText: Strings.new_price_with_discount,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      addEditCustomerController
                                          .planNewPriceController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fillColor: addEditCustomerController
                                          .readOnlyDiscountPrice
                                      ? Colors.black12
                                      : AppTheme.colorWhite,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.done,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onChanged: (value) {
                                    // add calculation logic
                                    if (addEditCustomerController
                                                .selPlanCategory !=
                                            null &&
                                        addEditCustomerController
                                            .selPlanCategory!.text!
                                            .equalsIgnoreCase(
                                                Strings.plan_group)) {
                                      addEditCustomerController
                                          .calculatePlanGroupDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    } else {
                                      addEditCustomerController
                                          .calculatePlanDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    }
                                  },
                                  onTextValidator: (String? value) {
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: addEditCustomerController
                                      .readOnlyDiscountPrice)
                            ]),
                      )
                    : Container(),
              ],
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.showInvoiceTag)
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                        Flexible(
                          flex: 1,
                          child: titleWithRequireWidget(
                              Strings.invoice_to_org, false),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Flexible(
                          flex: 2,
                          child: IgnorePointer(
                            ignoring:
                                addEditCustomerController.businessPromotionFlag,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                icon: SvgPicture.asset(
                                  downArrowSvg,
                                  height: Constant.DROP_DOWN_ARROW_W_H,
                                  width: Constant.DROP_DOWN_ARROW_W_H,
                                  color: AppTheme.colorBlack,
                                  fit: BoxFit.fill,
                                ),
                                decoration: Utils.ddlDecoration(
                                    fillColor: addEditCustomerController
                                            .businessPromotionFlag
                                        ? Colors.black12
                                        : AppTheme.colorWhite),
                                hint: Align(
                                    alignment: Alignment.centerLeft,
                                    child: Text(Strings.invoice_to_org,
                                        style: TextStyle(
                                          fontSize: AppTheme.medium,
                                          color: AppTheme.colorIconGrey,
                                          fontFamily: AppTheme.appFontName,
                                        ))),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: true,
                                isDense: true,
                                value: addEditCustomerController
                                    .selectedInvoiceToOrg,
                                items: addEditCustomerController
                                    .invoiceToOrgList!
                                    .map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
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
                                  addEditCustomerController
                                          .selectedInvoiceToOrg =
                                      value as DropdownDetail?;
                                  addEditCustomerController.update();
                                },
                                validator: (value) {
                                  // need to add validation
                                  return null;
                                },
                              ),
                            ),
                          ),
                        ),
                      ])
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.showInvoiceTag)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          InputTitleRequire(
                              title: Strings.plan_category, require: true),
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
                                  child: Text(Strings.plan_category,
                                      style: TextStyle(
                                        fontSize: AppTheme.medium,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ))),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: addEditCustomerController.selPlanCategory,
                              items: addEditCustomerController.planCategoryList!
                                  .map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
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
                                addEditCustomerController.selPlanCategory =
                                    value as DropdownDetail?;
                                addEditCustomerController.selPlanGroup = null;

                                addEditCustomerController.selPlanService = null;
                                addEditCustomerController.selPlan = null;
                                addEditCustomerController.individualPlanList!
                                    .clear();
                                addEditCustomerController.planValidityController
                                    .clear();
                                addEditCustomerController.discountController
                                    .clear();
                                addEditCustomerController.planGroupMappingList!
                                    .clear();
                                addEditCustomerController.offerPrice = 0;
                                addEditCustomerController.discountOfferPrice =
                                    0;
                                addEditCustomerController
                                    .planOfferPriceController
                                    .clear();
                                addEditCustomerController.planNewPriceController
                                    .clear();
                                addEditCustomerController.discountController
                                    .clear();
                                addEditCustomerController.billToReadOnly =
                                    false;
                                addEditCustomerController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditCustomerController.selPlanCategory ==
                                        null) {
                                  return Strings.please_select_plan_category;
                                }
                                return null;
                              },
                            ),
                          ),
                        ]),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                    flex: 1,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          InputTitleRequire(
                              title: Strings.bill_to, require: true),
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
                              decoration: Utils.ddlDecoration(
                                fillColor:
                                    // addEditCustomerController.billToReadOnly
                                    addEditCustomerController.selectedBillTo ==
                                            null
                                        ? Colors.black12
                                        : AppTheme.colorWhite,
                              ),
                              autofocus: false,
                              hint: Align(
                                  alignment: Alignment.centerLeft,
                                  child: Text(Strings.bill_to,
                                      style: TextStyle(
                                        fontSize: AppTheme.medium,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ))),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: addEditCustomerController.selectedBillTo,
                              items: addEditCustomerController.billToList!
                                  .map((BillToDetail value) {
                                return DropdownMenuItem<BillToDetail>(
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
                                addEditCustomerController.selectedBillTo =
                                    value as BillToDetail?;
                                addEditCustomerController.update();
                                addEditCustomerController
                                    .manageDiscountVisibility();
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditCustomerController.selectedBillTo ==
                                        null) {
                                  return Strings.select_bill_to;
                                }
                                return null;
                              },
                            ),
                          ),
                        ]),
                  ),
                ]),

            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.selPlanCategory != null)
                ? InputTitleRequire(title: Strings.billableTo, require: false)
                : const SizedBox.shrink(),

            (addEditCustomerController.selPlanCategory != null)
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : const SizedBox.shrink(),

            (addEditCustomerController.selPlanCategory != null)
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Flexible(
                        child: CoustomTextField(
                            labelText: Strings.select_billable_to,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                addEditCustomerController.billableToController,
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
                              // if(controller.billableToController.text.isEmpty){
                              //   return Strings.select_bill_to;
                              // }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              openParentCustomerScreen(Strings.billableTo);
                            },
                            readOnly: true),
                      ),
                      SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Material(
                        elevation: 1.5,
                        color: AppTheme.colorPrimary,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER)),
                        child: InkWell(
                          onTap: () {
                            addEditCustomerController.billableToController
                                .clear();
                            addEditCustomerController.selectedParentCustomer =
                                null;
                            addEditCustomerController.billableToCustomerId =
                                null;
                            addEditCustomerController.update();
                          },
                          child: Container(
                            height: Constant.BTN_HEIGHT_M - 5,
                            width: Constant.BTN_HEIGHT_M - 5,
                            alignment: Alignment.center,
                            padding: const EdgeInsets.all(
                                Constant.SMALL_PADDING - 1),
                            child: SvgPicture.asset(
                              deleteSvg,
                              height: Constant.ICON_SIZE,
                              width: Constant.ICON_SIZE,
                              color: AppTheme.colorWhite,
                              fit: BoxFit.fill,
                            ),
                          ),
                        ),
                      ),
                    ],
                  )
                : const SizedBox.shrink(),

            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                        Flexible(
                          flex: 1,
                          child:
                              titleWithRequireWidget(Strings.plan_group, false),
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
                                  child: Text(Strings.plan_group,
                                      style: TextStyle(
                                        fontSize: AppTheme.medium,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ))),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: addEditCustomerController.selPlanGroup,
                              items: addEditCustomerController.planGroupList!
                                  .map((PlanGroupDetail value) {
                                return DropdownMenuItem<PlanGroupDetail>(
                                  value: value,
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: CustomText(
                                      title: value.planGroupName!,
                                      colors: AppTheme.colorBlack,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                    ), //Text(value.desig!),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditCustomerController.selPlanGroup =
                                    value as PlanGroupDetail?;
                                addEditCustomerController.update();
                                addEditCustomerController
                                    .manageThePlanGroupSelection();
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

            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addEditCustomerController.showDiscountPrice)
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                        Flexible(
                          flex: 1,
                          child:
                              titleWithRequireWidget(Strings.discount, false),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Flexible(
                          flex: 2,
                          child: CoustomTextField(
                              labelText: Strings.discount,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  addEditCustomerController.discountController,
                              maxLength: 10,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              keyboardType: TextInputType.number,
                              fontSize: AppTheme.small,
                              textInputAction: TextInputAction.next,
                              fontWeight: FontWeight.w500,
                              fillColor: AppTheme.colorLightGrey.withOpacity(1),
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING,
                                  vertical: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onChanged: (value) {
                                // add calculation logic]
                                addEditCustomerController
                                    .calculatePlanGroupDiscountPrice(
                                        Strings.discount, value);
                              },
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              readOnly: true),
                        ),
                      ])
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addEditCustomerController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Flexible(
                        flex: 1,
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
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
                                      child: Text(Strings.service,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: addEditCustomerController
                                      .selectServicesByServiceAreaData,
                                  items: addEditCustomerController
                                      .servicesByServiceAreaDataList!
                                      .map((ServicesByServiceAreaDataList
                                          value) {
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
                                    addEditCustomerController
                                            .selectServicesByServiceAreaData =
                                        value as ServicesByServiceAreaDataList?;

                                    addEditCustomerController.update();
                                    addEditCustomerController.serviceAreaName =
                                        value!.name;
                                    addEditCustomerController
                                        .selectedServiceAreaPlanList!
                                        .clear();
                                    // addEditCustomerController.getServicePlanModeServiceAreaAPI();
                                    addEditCustomerController
                                        .getPlanServicesDetail();
                                    // addEditCustomerController.setPlanData();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ]),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Flexible(
                        flex: 1,
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              InputTitleRequire(
                                  title: Strings.plan, require: true),
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
                                      child: Text(Strings.plan,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: addEditCustomerController
                                      .serviceAreaPlanPostpaidData,
                                  // value: addEditCustomerController.selPlan,
                                  // items: addEditCustomerController.planList!
                                  items: addEditCustomerController
                                      .selectedServiceAreaPlanList!
                                      .map((ServiceAreaPlanPostpaidplanList
                                          value) {
                                    return DropdownMenuItem<
                                        ServiceAreaPlanPostpaidplanList>(
                                      value: value,
                                      child: Align(
                                        alignment: Alignment.centerLeft,
                                        child: CustomText(
                                          title: value.category!
                                                  .equalsIgnoreCase(Constant
                                                      .BUSINESS_PROMOTION)
                                              ? "${value.displayName!} (${value.category})"
                                              : value.displayName!,
                                          colors: AppTheme.colorBlack,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.w500,
                                        ), //Text(value.desig!),
                                      ),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    addEditCustomerController
                                            .serviceAreaPlanPostpaidData =
                                        value
                                            as ServiceAreaPlanPostpaidplanList?;
                                    if (value!.category!.equalsIgnoreCase(
                                        Constant.BUSINESS_PROMOTION)) {
                                      addEditCustomerController
                                          .businessPromotionFlag = true;
                                      addEditCustomerController.showInvoiceTag =
                                          true;
                                      for (BillToDetail element
                                          in addEditCustomerController
                                              .billToList!) {
                                        if (element.text == "SUBISU") {
                                          addEditCustomerController
                                              .selectedBillTo = element;
                                          for (DropdownDetail element
                                              in addEditCustomerController
                                                  .invoiceToOrgList!) {
                                            if (element.text == Strings.no) {
                                              addEditCustomerController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            } else if (element.text ==
                                                Strings.yes) {
                                              addEditCustomerController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            }
                                          }
                                        } else if (element.text == "CUSTOMER") {
                                          addEditCustomerController
                                              .selectedBillTo = element;
                                        }
                                      }
                                    } else {
                                      addEditCustomerController
                                          .businessPromotionFlag = false;
                                      addEditCustomerController.showInvoiceTag =
                                          false;
                                    }

                                    if (addEditCustomerController
                                            .serviceAreaPlanPostpaidData !=
                                        null) {
                                      addEditCustomerController
                                              .planValidityController.text =
                                          "${addEditCustomerController.serviceAreaPlanPostpaidData!.validity}-${addEditCustomerController.serviceAreaPlanPostpaidData!.unitsOfValidity!}";
                                      addEditCustomerController
                                              .newOfferPricePlanController
                                              .text =
                                          addEditCustomerController
                                              .serviceAreaPlanPostpaidData!
                                              .offerprice!
                                              .toString();
                                    }
                                    addEditCustomerController.update();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ]),
                      ),
                    ],
                  )
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addEditCustomerController.selPlan != null &&
                    addEditCustomerController.showDiscountPrice == false)
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title:
                          "Plan Old Price : ${addEditCustomerController.selPlan!.offerprice!}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                    ))
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addEditCustomerController.selPlan != null)
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Flexible(
                        flex: 1,
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              InputTitleRequire(
                                  title: Strings.validity, require: true),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              CoustomTextField(
                                  labelText: Strings.validity,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      addEditCustomerController
                                          .planValidityController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
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
                                  readOnly: true)
                            ]),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      (addEditCustomerController.showDiscountPrice)
                          ? Flexible(
                              flex: 1,
                              child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.discount,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.discount,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            addEditCustomerController
                                                .discountController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        keyboardType: TextInputType.number,
                                        fontSize: AppTheme.small,
                                        maxLength: 5,
                                        fillColor: AppTheme.colorLightGrey
                                            .withOpacity(1),
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
                                          return null;
                                        },
                                        onTextFiledOnTap: () {},
                                        readOnly: true)
                                  ]),
                            )
                          : Flexible(
                              flex: 1,
                              child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    InputTitleRequire(
                                        title: Strings.new_offer_price_plan,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.new_offer_price_plan,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            addEditCustomerController
                                                .newOfferPricePlanController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        keyboardType: TextInputType.number,
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
                                          return null;
                                        },
                                        onTextFiledOnTap: () {},
                                        readOnly: false)
                                  ]),
                            ),
                    ],
                  )
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),

            // id == 224 (SUBISU)
            (addEditCustomerController.selectedBillTo != null &&
                    addEditCustomerController.selectedBillTo!.id == 224)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                        Expanded(
                          flex: 1,
                          child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                InputTitleRequire(
                                    title: Strings.discount_type,
                                    require: true),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                IgnorePointer(
                                  ignoring: true,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      // decoration: Utils.ddlDecoration(),
                                      decoration: Utils.ddlDecoration(
                                        fillColor: addEditCustomerController
                                                    .selDiscountType ==
                                                null
                                            ? Colors.grey.shade300
                                            : AppTheme.colorLightGrey
                                                .withOpacity(1),
                                      ),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.plan_category,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: addEditCustomerController
                                          .selDiscountType,
                                      items: addEditCustomerController
                                          .discountTypeList!
                                          .map((DropdownDetail value) {
                                        return DropdownMenuItem<DropdownDetail>(
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
                                        addEditCustomerController
                                                .selDiscountType =
                                            value as DropdownDetail?;
                                        addEditCustomerController.update();
                                      },
                                      validator: (value) {
                                        // need to add validation
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ]),
                        ),
                        Expanded(flex: 1, child: Container())
                      ]),

            // (addEditCustomerController.selPlanCategory != null &&
            //         addEditCustomerController.showDiscountPrice)
            //     ? trialPlanWidget()
            //     : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController
                        .planGroupMappingList!.isNotEmpty &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? CustomText(
                    title: Strings.plan_mapping_list,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w500,
                  )
                : const SizedBox.shrink(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController
                        .planGroupMappingList!.isNotEmpty &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount:
                        addEditCustomerController.planGroupMappingList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      PlanGroupMappingDetail item = addEditCustomerController
                          .planGroupMappingList![index];
                      return Container(
                          margin: EdgeInsets.only(
                              top:
                                  index == 0 ? 0 : Constant.VERY_SMALL_PADDING),
                          child: PlanGroupMappingList(
                              item: item,
                              index: index,
                              onDeleteTap: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialogHelper(
                                        title: Strings.app_name,
                                        message: Strings.msg_delete,
                                        positiveBtnText: Strings.ok,
                                        negativeBtnText: Strings.cancel,
                                        positiveBtnClick: () {
                                          Get.back();
                                          addEditCustomerController
                                              .individualPlanList!
                                              .remove(item);
                                          addEditCustomerController.update();
                                          addEditCustomerController
                                              .calculatePlanDiscountPrice(
                                                  Strings.delete, "");
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              }));
                    })
                : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                addEditCustomerController.selPlanCategory!.text!
                    .equalsIgnoreCase(Strings.individual))
                ? Align(
                alignment: Alignment.centerRight,
                child: InkWell(
                  onTap: () {
                    String discount = addEditCustomerController
                        .discountController.text;
                    // if (addEditCafCustomerController.selPlanService == null ||
                    //     addEditCafCustomerController.selPlan == null) {

                    if (addEditCustomerController
                        .selectServicesByServiceAreaData ==
                        null ||
                        addEditCustomerController
                            .serviceAreaPlanPostpaidData ==
                            null ||
                        addEditCustomerController.selDiscountType ==
                            null) {
                      Utils.showSnackbar(
                          Strings.INFO,
                          "Please fill-up mandatory data!",
                          AppTheme.colorWhite,
                          AppTheme.colorBlueRView);
                      return;
                    }

                    addEditCustomerController.individualPlanList!.add(
                        IndividualPlanData(
                            type: addEditCustomerController
                                .showDiscountPrice
                                ? 1
                                : 2,
                            planService: addEditCustomerController
                                .selectServicesByServiceAreaData!,
                            // planDetail: addEditCafCustomerController.selPlan,
                            planDetail: addEditCustomerController
                                .serviceAreaPlanPostpaidData,
                            discount: discount.isEmpty ? "0" : discount,
                            discountType: addEditCustomerController
                                .selDiscountType!.text,
                            newOfferPrice: addEditCustomerController
                                .newOfferPricePlanController.text,
                            // planOfferPrice: addEditCafCustomerController.selPlan!.offerprice!
                            planOfferPrice: addEditCustomerController
                                .serviceAreaPlanPostpaidData!.offerprice!
                                .toString(),
                            trialPlan:
                            addEditCustomerController.trialPlan));

                    addEditCustomerController.selPlanService = null;
                    addEditCustomerController.selPlan = null;
                    addEditCustomerController.planValidityController
                        .clear();
                    addEditCustomerController.discountController.clear();

                    // Service Area
                    addEditCustomerController
                        .selectServicesByServiceAreaData = null;
                    addEditCustomerController
                        .selectedServiceAreaPlanList!
                        .clear();

                    // Discount Type
                    //  addEditCafCustomerController.selDiscountType = null;

                    addEditCustomerController.newOfferPricePlanController
                        .clear();
                    addEditCustomerController.trialPlan = false;
                    if (addEditCustomerController.individualPlanList !=
                        null &&
                        addEditCustomerController
                            .individualPlanList!.isNotEmpty) {
                      addEditCustomerController.billToReadOnly = true;
                      addEditCustomerController.readOnlyDiscountPrice =
                      false;
                    } else {
                      addEditCustomerController.billToReadOnly = false;
                      addEditCustomerController.readOnlyDiscountPrice =
                      true;
                    }
                    addEditCustomerController.calculatePlanDiscountPrice(
                        Strings.add, "");
                    addEditCustomerController.update();
                  },
                  child: CustomText(
                    title: "+ Add Plan",
                    colors: AppTheme.colorPrimary,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w600,
                  ),
                ))
                : Container(),
            // (addEditCustomerController.selPlanCategory != null &&
            //         addEditCustomerController.selPlanCategory!.text!
            //             .equalsIgnoreCase(Strings.individual))
            //     ? Align(
            //         alignment: Alignment.centerRight,
            //         child: InkWell(
            //           onTap: () {
            //             String discount =
            //                 addEditCustomerController.discountController.text;
            //             // if (addEditCustomerController.selPlanService == null ||
            //             //     addEditCustomerController.selPlan == null) {
            //             if (addEditCustomerController
            //                         .selectServicesByServiceAreaData ==
            //                     null ||
            //                 addEditCustomerController
            //                         .serviceAreaPlanPostpaidData ==
            //                     null ||
            //                 addEditCustomerController.selDiscountType == null) {
            //               Utils.showSnackbar(
            //                   Strings.ERROR,
            //                   "Please fill-up data!",
            //                   AppTheme.colorWhite,
            //                   AppTheme.colorRed);
            //               return;
            //             }
            //
            //             addEditCustomerController.individualPlanList!.add(
            //                 IndividualPlanData(
            //                     type:
            //                         addEditCustomerController.showDiscountPrice
            //                             ? 1
            //                             : 2,
            //                     planService: addEditCustomerController
            //                         .selectServicesByServiceAreaData!,
            //                     // planDetail: addEditCustomerController.selPlan,
            //                     planDetail: addEditCustomerController
            //                         .serviceAreaPlanPostpaidData,
            //                     discount: discount.isEmpty ? "0" : discount,
            //                     discountType: addEditCustomerController
            //                         .selDiscountType!.text,
            //                     newOfferPrice: addEditCustomerController
            //                         .newOfferPricePlanController.text,
            //                     // planOfferPrice: addEditCustomerController.selPlan!.offerprice!
            //                     planOfferPrice: addEditCustomerController
            //                         .serviceAreaPlanPostpaidData!.offerprice!
            //                         .toString(),
            //                     trialPlan:
            //                         addEditCustomerController.trialPlan));
            //
            //             addEditCustomerController.selPlanService = null;
            //             addEditCustomerController.selPlan = null;
            //             addEditCustomerController.planValidityController
            //                 .clear();
            //             addEditCustomerController.discountController.clear();
            //
            //             // Service Area
            //             addEditCustomerController
            //                 .selectServicesByServiceAreaData = null;
            //             addEditCustomerController.selectedServiceAreaPlanList!
            //                 .clear();
            //
            //             // Discount Type
            //             addEditCustomerController.selDiscountType = null;
            //
            //             addEditCustomerController.newOfferPricePlanController
            //                 .clear();
            //             addEditCustomerController.trialPlan = false;
            //             if (addEditCustomerController.individualPlanList !=
            //                     null &&
            //                 addEditCustomerController
            //                     .individualPlanList!.isNotEmpty) {
            //               addEditCustomerController.billToReadOnly = true;
            //               addEditCustomerController.readOnlyDiscountPrice =
            //                   false;
            //             } else {
            //               addEditCustomerController.billToReadOnly = false;
            //               addEditCustomerController.readOnlyDiscountPrice =
            //                   true;
            //             }
            //             addEditCustomerController.calculatePlanDiscountPrice(
            //                 Strings.add, "");
            //             addEditCustomerController.update();
            //           },
            //           child: CustomText(
            //             title: "+ Add Plan",
            //             colors: AppTheme.colorPrimary,
            //             textAlign: TextAlign.start,
            //             fontSize: AppTheme.medium,
            //             fontWeight: FontWeight.w600,
            //           ),
            //         ))
            //     : Container(),
            (addEditCustomerController.selPlanCategory != null &&
                    addEditCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.individualPlanList != null &&
                    addEditCustomerController.individualPlanList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount:
                        addEditCustomerController.individualPlanList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      IndividualPlanData item =
                          addEditCustomerController.individualPlanList![index];
                      return Container(
                          margin: EdgeInsets.only(
                              top:
                                  index == 0 ? 0 : Constant.VERY_SMALL_PADDING),
                          child: IndividualPlanItem(
                              item: item,
                              index: index,
                              onDeleteTap: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialogHelper(
                                        title: Strings.app_name,
                                        message: Strings.msg_delete,
                                        positiveBtnText: Strings.ok,
                                        negativeBtnText: Strings.cancel,
                                        positiveBtnClick: () {
                                          Get.back();
                                          addEditCustomerController
                                              .individualPlanList!
                                              .remove(item);
                                          addEditCustomerController.update();
                                          addEditCustomerController
                                              .calculatePlanDiscountPrice(
                                              Strings.delete, "");
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              }));
                    })
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
          ]),
    );
  }

  competitorPackDetails() {
    return Form(
      key: permanentAddressFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.MEDIUM_PADDING),
          sameAsPresentAddWidget(Strings.permanent_address_details),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.address, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.address,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCustomerController.permanentAddController,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    keyboardType: TextInputType.text,
                    maxLength: 250,
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
                child: titleWithRequireWidget(Strings.pincode, false),
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
                        child: Text(Strings.pincode,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPermanentPincode,
                    items: addEditCustomerController.permanentPincodeList!
                        .map((PincodeDetail value) {
                      return DropdownMenuItem<PincodeDetail>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value.pincode!,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addEditCustomerController.selPermanentPincode =
                          value as PincodeDetail?;
                      addEditCustomerController.update();
                      addEditCustomerController.getPinCodeToAreaData(
                          addEditCustomerController
                              .selPermanentPincode!.pincodeid!,
                          Strings.permanent_address_details);

                      /* addEditCustomerController.getPinCodeToAreaList(
                          addEditCustomerController
                              .selPermanentPincode!.pincodeid!,
                          Strings.permanent_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                child: titleWithRequireWidget(Strings.area, false),
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
                        child: Text(Strings.area,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCustomerController.selPermanentArea,
                    items: addEditCustomerController.permanentAreaList!
                        .map((PincodeAreaDetail value) {
                      return DropdownMenuItem<PincodeAreaDetail>(
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
                      addEditCustomerController.selPermanentArea =
                          value as PincodeAreaDetail?;
                      addEditCustomerController.update();
                      addEditCustomerController.getAreaDetail(
                          addEditCustomerController.selPermanentArea!.id!,
                          Strings.permanent_address_details);
                      /*addEditCustomerController
                          .setCityData(Strings.permanent_address_details);*/
                    },
                    validator: (value) {
                      // need to add validation
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
                  child: titleWithRequireWidget(Strings.city, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.city,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentCity,
                        items: addEditCustomerController.permanentCityList!
                            .map((CityDetail value) {
                          return DropdownMenuItem<CityDetail>(
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
                          addEditCustomerController.selPermanentCity =
                              value as CityDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.state, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.state,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentState,
                        items: addEditCustomerController.permanentStateList!
                            .map((StateDetail value) {
                          return DropdownMenuItem<StateDetail>(
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
                          addEditCustomerController.selPermanentState =
                              value as StateDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Flexible(
                  flex: 1,
                  child: titleWithRequireWidget(Strings.country, false),
                ),
                const SizedBox(
                  width: Constant.SMALL_PADDING,
                ),
                Flexible(
                  flex: 2,
                  child: IgnorePointer(
                    ignoring: true,
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
                            child: Text(Strings.country,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selPermanentCountry,
                        items: addEditCustomerController.permanentCountryList!
                            .map((CountryDetail value) {
                          return DropdownMenuItem<CountryDetail>(
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
                          addEditCustomerController.selPermanentCountry =
                              value as CountryDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ),
              ]),
          const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }

  additionalServiceForm() {
    return Form(
      key: additionalServiceFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                  child: titleWithRequireWidget(Strings.pop, false),
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
                          child: Text(Strings.pop,
                              style: TextStyle(
                                fontSize: AppTheme.medium,
                                color: AppTheme.colorIconGrey,
                                fontFamily: AppTheme.appFontName,
                              ))),
                      style: AppTheme.dropdownTextStyle,
                      isExpanded: true,
                      isDense: true,
                      value: addEditCustomerController.selectedPop,
                      items: addEditCustomerController.popList!
                          .map((PopDetail value) {
                        return DropdownMenuItem<PopDetail>(
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
                        addEditCustomerController.selectedPop =
                            value as PopDetail?;
                        addEditCustomerController.update();
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
                  child: titleWithRequireWidget(Strings.olt, false),
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
                          child: Text(Strings.olt,
                              style: TextStyle(
                                fontSize: AppTheme.medium,
                                color: AppTheme.colorIconGrey,
                                fontFamily: AppTheme.appFontName,
                              ))),
                      style: AppTheme.dropdownTextStyle,
                      isExpanded: true,
                      isDense: true,
                      value: addEditCustomerController
                          .selectedOltNetworkDeviceList,
                      items: addEditCustomerController
                          .oltNetworkDevicesByDeviceList!
                          .map((NetworkDevicesByDeviceDataList value) {
                        return DropdownMenuItem<NetworkDevicesByDeviceDataList>(
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
                        addEditCustomerController.selectedOltNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        if (addEditCustomerController
                            .masterDBNetworkDevicesByDeviceList!.isEmpty) {
                          addEditCustomerController
                              .getNetworkDevicesByDeviceTypeAPI(
                                  Strings.master_db);
                        }

                        addEditCustomerController.update();
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
                  child: titleWithRequireWidget(Strings.master_db, false),
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
                          child: Text(Strings.master_db,
                              style: TextStyle(
                                fontSize: AppTheme.medium,
                                color: AppTheme.colorIconGrey,
                                fontFamily: AppTheme.appFontName,
                              ))),
                      style: AppTheme.dropdownTextStyle,
                      isExpanded: true,
                      isDense: true,
                      value: addEditCustomerController
                          .selectedMasterDBNetworkDeviceList,
                      items: addEditCustomerController
                          .masterDBNetworkDevicesByDeviceList!
                          .map((NetworkDevicesByDeviceDataList value) {
                        return DropdownMenuItem<NetworkDevicesByDeviceDataList>(
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
                        addEditCustomerController
                                .selectedMasterDBNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        if (addEditCustomerController
                            .splitterDBNetworkDevicesByDeviceList!.isEmpty) {
                          addEditCustomerController
                              .getNetworkDevicesByDeviceTypeAPI(
                                  Strings.splitter_db);
                        }
                        addEditCustomerController.update();
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
                  child: titleWithRequireWidget(Strings.splitter_db, false),
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
                          child: Text(Strings.splitter_db,
                              style: TextStyle(
                                fontSize: AppTheme.medium,
                                color: AppTheme.colorIconGrey,
                                fontFamily: AppTheme.appFontName,
                              ))),
                      style: AppTheme.dropdownTextStyle,
                      isExpanded: true,
                      isDense: true,
                      value: addEditCustomerController
                          .selectedSplitterDBNetworkDeviceList,
                      items: addEditCustomerController
                          .splitterDBNetworkDevicesByDeviceList!
                          .map((NetworkDevicesByDeviceDataList value) {
                        return DropdownMenuItem<NetworkDevicesByDeviceDataList>(
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
                        addEditCustomerController
                                .selectedSplitterDBNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        addEditCustomerController.update();
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
                    child: titleWithRequireWidget(Strings.static_ip, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enter_static_ip,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.staticIPController,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(Strings.nas_ip, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enter_nas_ip,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.nasIpController,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(
                        '${Strings.nas_port}\n(${Strings.validate})', false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText:
                              "${Strings.enter_nas_port} (${Strings.validate})",
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.nasPort,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(
                        '${Strings.ip_pool_name}\n(${Strings.bind})', false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText:
                              '${Strings.enter_ip_pool_name} (${Strings.bind})',
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.ipPoolNameController,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
          ]),
    );
  }

  radiusServiceForm() {
    return Form(
      key: radiusServiceFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                    child: titleWithRequireWidget(Strings.nas_port, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.nas_port,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.nasPort,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(Strings.framed_ip, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.framed_ip,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.framedIP,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
          ]),
    );
  }

  chargeDetailForm() {
    return Form(
      key: chargeDetailsFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                    child: titleWithRequireWidget(Strings.charge, false),
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
                            child: Text(Strings.charge,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selCharge,
                        items: addEditCustomerController.chargeList!
                            .map((ChargeDetail value) {
                          return DropdownMenuItem<ChargeDetail>(
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
                          addEditCustomerController.selCharge =
                              value as ChargeDetail?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ]),
            addEditCustomerController.selCharge != null
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : Container(),
            addEditCustomerController.selCharge != null
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title: Strings.amount +
                          " : " +
                          addEditCustomerController.selCharge!.price!
                              .toString(),
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.normal,
                    ),
                  )
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(Strings.charge_type, false),
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
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selectedChargeType,
                        items: addEditCustomerController.chargeTypeLst
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
                          addEditCustomerController.selectedChargeType =
                              value as String?;
                          addEditCustomerController.update();
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
            addEditCustomerController.selectedChargeType != null &&
                    addEditCustomerController.selectedChargeType!
                        .equalsIgnoreCase(Strings.recurring)
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                        Flexible(
                          flex: 1,
                          child: titleWithRequireWidget(
                              Strings.recurring_month, false),
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
                                  child: Text(Strings.recurring_month,
                                      style: TextStyle(
                                        fontSize: AppTheme.medium,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ))),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: addEditCustomerController
                                  .selectedRecurringMonth,
                              items: addEditCustomerController.recurringMonthLst
                                  .map((int value) {
                                return DropdownMenuItem<int>(
                                  value: value,
                                  child: Align(
                                    alignment: Alignment.centerLeft,
                                    child: CustomText(
                                      title: value.toString(),
                                      colors: AppTheme.colorBlack,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                    ), //Text(value.desig!),
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditCustomerController
                                    .selectedRecurringMonth = value as int?;
                                addEditCustomerController.update();
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
            addEditCustomerController.selectedChargeType != null &&
                    addEditCustomerController.selectedChargeType!
                        .equalsIgnoreCase(Strings.recurring)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(Strings.plan, false),
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
                            child: Text(Strings.plan,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ))),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: addEditCustomerController.selectedChargePlan,
                        items: addEditCustomerController.chargePlanList!
                            .map((IndividualPlanData value) {
                          return DropdownMenuItem<IndividualPlanData>(
                            value: value,
                            child: Align(
                              alignment: Alignment.centerLeft,
                              child: CustomText(
                                title: value.planDetail!.name,
                                colors: AppTheme.colorBlack,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.small,
                                fontWeight: FontWeight.w500,
                              ), //Text(value.desig!),
                            ),
                          );
                        }).toList(),
                        onChanged: (value) {
                          addEditCustomerController.selectedChargePlan =
                              value as IndividualPlanData?;
                          addEditCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ]),
            addEditCustomerController.selectedChargePlan != null
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : Container(),
            addEditCustomerController.selectedChargePlan != null
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title:
                          "${Strings.validity} : ${addEditCustomerController.selectedChargePlan!.planDetail!.validity} - ${addEditCustomerController.selectedChargePlan!.planDetail!.unitsOfValidity!}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.normal,
                    ),
                  )
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Flexible(
                    flex: 1,
                    child: titleWithRequireWidget(Strings.new_price, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.new_price,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.newPriceController,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Align(
              alignment: Alignment.centerRight,
              child: InkWell(
                onTap: () {
                  String newPrice =
                      addEditCustomerController.newPriceController.text;
                  String recMonth =
                      (addEditCustomerController.selectedRecurringMonth != null)
                          ? addEditCustomerController.selectedRecurringMonth!
                              .toString()
                          : "";
                  /* String validity =
                      addEditCustomerController.validityController.text;
                  String price = addEditCustomerController.priceController.text;*/
                  /* String date =
                      addEditCustomerController.chargeDateController.text;*/

                  if (addEditCustomerController.selCharge == null ||
                      addEditCustomerController.selectedChargeType == null ||
                      (addEditCustomerController.selectedChargeType != null &&
                          addEditCustomerController.selectedChargeType!
                              .equalsIgnoreCase(Strings.recurring) &&
                          recMonth.isEmpty) ||
                      addEditCustomerController.selectedChargePlan == null ||
                      newPrice.isEmpty) {
                    Utils.showSnackbar(Strings.ERROR, "Please fill-up data!",
                        AppTheme.colorWhite, AppTheme.colorRed);
                    return;
                  }
                  double priceNew = double.parse(newPrice);
                  double price = double.parse(
                      addEditCustomerController.selCharge!.price!.toString());
                  if (price > priceNew) {
                    Utils.showSnackbar(
                        Strings.ERROR,
                        "New Price must not be less than the actual charge price",
                        AppTheme.colorWhite,
                        AppTheme.colorRed);
                    return;
                  }

                  addEditCustomerController.chargeDataList!.add(ChargeData(
                      chargeDetail: addEditCustomerController.selCharge!,
                      chargeType: addEditCustomerController.selectedChargeType!,
                      recMonth:
                          addEditCustomerController.selectedRecurringMonth,
                      chargePlan: addEditCustomerController.selectedChargePlan,
                      price: newPrice));

                  addEditCustomerController.selCharge = null;
                  addEditCustomerController.selectedChargeType = null;
                  addEditCustomerController.selectedRecurringMonth = null;
                  addEditCustomerController.selectedChargePlan = null;
                  addEditCustomerController.newPriceController.clear();
                  //addEditCustomerController.validityController.clear();
                  //addEditCustomerController.priceController.clear();
                  // addEditCustomerController.chargeDateController.clear();
                  //  addEditCustomerController.selectedChargeDate = null;
                  addEditCustomerController.update();
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
            (addEditCustomerController.chargeDataList != null &&
                    addEditCustomerController.chargeDataList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: addEditCustomerController.chargeDataList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      ChargeData item =
                          addEditCustomerController.chargeDataList![index];
                      return Container(
                          margin: const EdgeInsets.only(
                              top: Constant.VERY_SMALL_PADDING),
                          child: ChargeListItem(
                              item: item,
                              index: index,
                              onDeleteTap: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialogHelper(
                                        title: Strings.app_name,
                                        message: Strings.msg_delete,
                                        positiveBtnText: Strings.ok,
                                        negativeBtnText: Strings.cancel,
                                        positiveBtnClick: () {
                                          Get.back();
                                          addEditCustomerController
                                              .chargeDataList!
                                              .remove(item);
                                          addEditCustomerController.update();
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              }));
                    })
                : Container(),
          ]),
    );
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.charge_date) {
      if (addEditCustomerController.selectedChargeDate != null) {
        selectedDate = addEditCustomerController.selectedChargeDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.payment_date) {
      if (addEditCustomerController.selectedPaymentDate != null) {
        selectedDate = addEditCustomerController.selectedPaymentDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.dob_date) {
      if (addEditCustomerController.selectedDOBDate != null) {
        selectedDate = addEditCustomerController.selectedDOBDate;
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
      /*if (identity == Strings.charge_date) {
        addEditCustomerController.selectedChargeDate = picked;
        addEditCustomerController.chargeDateController.text =
            addEditCustomerController.apiDateFormat.format(picked);
      }*/
      if (identity == Strings.payment_date) {
        addEditCustomerController.selectedPaymentDate = picked;
        addEditCustomerController.paymentDateController.text =
            addEditCustomerController.apiDateFormat.format(picked);
      }
      if (identity == Strings.dob_date) {
        addEditCustomerController.selectedDOBDate = picked;
        addEditCustomerController.dobDateController.text =
            addEditCustomerController.apiDateFormat.format(picked);
      }
      addEditCustomerController.update();
    }
  }

  macMapppingDetailForm() {
    return Form(
      key: macMapppingFormKey,
      autovalidateMode: addEditCustomerController.autoValidateMode,
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
                    child: titleWithRequireWidget(Strings.mac_address, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.mac_address,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCustomerController.macAddressController,
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
                          readOnly: false)),
                ]),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            Align(
              alignment: Alignment.centerRight,
              child: InkWell(
                onTap: () {
                  String macAddress =
                      addEditCustomerController.macAddressController.text;

                  if (macAddress.isEmpty) {
                    Utils.showSnackbar(Strings.ERROR, "Please fill-up data!",
                        AppTheme.colorWhite, AppTheme.colorRed);
                    return;
                  }
                  addEditCustomerController.macAddressList!.add(macAddress);
                  addEditCustomerController.macAddressController.clear();
                  addEditCustomerController.update();
                },
                child: CustomText(
                  title: "+ Add Mac Address",
                  colors: AppTheme.colorPrimary,
                  textAlign: TextAlign.start,
                  fontSize: AppTheme.medium,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCustomerController.macAddressList != null &&
                    addEditCustomerController.macAddressList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: addEditCustomerController.macAddressList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      String item =
                          addEditCustomerController.macAddressList![index];
                      return Container(
                          margin: EdgeInsets.only(
                              top:
                                  index == 0 ? 0 : Constant.VERY_SMALL_PADDING),
                          child: MacAddressItem(
                              item: item,
                              index: index,
                              onDeleteTap: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialogHelper(
                                        title: Strings.app_name,
                                        message: Strings.msg_delete,
                                        positiveBtnText: Strings.ok,
                                        negativeBtnText: Strings.cancel,
                                        positiveBtnClick: () {
                                          Get.back();
                                          addEditCustomerController
                                              .macAddressList!
                                              .remove(item);
                                          addEditCustomerController.update();
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              }));
                    })
                : Container(),
          ]),
    );
  }

  trialPlanWidget() {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(children: [
        SizedBox(
          width: 10,
          child: Checkbox(
            value: addEditCustomerController.trialPlan,
            activeColor: AppTheme.colorPrimary,
            onChanged: (value) {
              addEditCustomerController.trialPlan =
                  !addEditCustomerController.trialPlan;
              addEditCustomerController.update();
            },
          ),
        ),
        const SizedBox(width: Constant.SMALL_PADDING),
        InkWell(
            child: CustomText(
              title: Strings.trial_plan,
              textAlign: TextAlign.start,
              colors: AppTheme.colorBlack,
              fontSize: AppTheme.medium,
              fontWeight: FontWeight.w400,
            ),
            onTap: () {
              addEditCustomerController.trialPlan =
                  !addEditCustomerController.trialPlan;
              addEditCustomerController.update();
            }),
      ]),
    );
  }

  sameAsPresentAddWidget(String type) {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(children: [
        SizedBox(
          width: 10,
          child: Checkbox(
            value: type.equalsIgnoreCase(Strings.payment_address_details)
                ? addEditCustomerController.paymentSameAs
                : addEditCustomerController.permanentSameAs,
            activeColor: AppTheme.colorPrimary,
            onChanged: (value) {
              if (type.equalsIgnoreCase(Strings.payment_address_details)) {
                addEditCustomerController.paymentSameAs =
                    !addEditCustomerController.paymentSameAs;
              } else {
                addEditCustomerController.permanentSameAs =
                    !addEditCustomerController.permanentSameAs;
              }
              addEditCustomerController.update();
              addEditCustomerController.sameAsPresentAddress(type);
            },
          ),
        ),
        const SizedBox(width: Constant.SMALL_PADDING),
        InkWell(
            child: CustomText(
              title: Strings.same_as_parent_address,
              textAlign: TextAlign.start,
              colors: AppTheme.colorBlack,
              fontSize: AppTheme.medium,
              fontWeight: FontWeight.w400,
            ),
            onTap: () {
              if (type.equalsIgnoreCase(Strings.payment_address_details)) {
                addEditCustomerController.paymentSameAs =
                    !addEditCustomerController.paymentSameAs;
              } else {
                addEditCustomerController.permanentSameAs =
                    !addEditCustomerController.permanentSameAs;
              }
              addEditCustomerController.update();
              addEditCustomerController.sameAsPresentAddress(type);
            }),
      ]),
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

  _appBar() {
    return DynamicAppBar(
        (addEditCustomerController.type != null &&
                addEditCustomerController.type!
                    .equalsIgnoreCase(Strings.prepaid))
            ? Strings.create_prepaid_customer
            : Strings.create_postpaid_customer,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (addEditCustomerController.activeStep == 0) {
      validateBasicDetail();
    }
    /*else if (addEditCustomerController.activeStep == 1) {
      validateKycDetail();
    } else if (addEditCustomerController.activeStep == 2) {
      validateContactDetail();
    } else if (addEditCustomerController.activeStep == 3) {
      validateLocationDetail();
    } else if (addEditCustomerController.activeStep == 1) {
      validateBusinessDetail();
    } else if (addEditCustomerController.activeStep == 2) {
      validatePaymentDetail();
    } */
    else if (addEditCustomerController.activeStep == 1) {
      validatePresentAddDetail();
    }
    /*else if (addEditCustomerController.activeStep == 2) {
      validatePaymentAddDetail();
    } else if (addEditCustomerController.activeStep == 3) {
      validatePermanentAddDetail();
    }*/
    else if (addEditCustomerController.activeStep == 2) {
      validatePlanDetail();
    } else if (addEditCustomerController.activeStep == 3) {
      validateAdditionalServiceDetail();
    } /* else if (addEditCustomerController.activeStep == 4) {
      validateRadiusServiceDetail();
    } else if (addEditCustomerController.activeStep == 5) {
      validateChargeDetail();
    }*/
  }

  validateBasicDetail() {
    if (basicDetailFormKey.currentState!.validate()) {
      if (addEditCustomerController.isCredentialMatchWithAccountNo == false) {
        addEditCustomerController.checkCustomerExist();
      } else {
        // addEditCustomerController.getSubAreaCall();
        if (addEditCustomerController.activeStep <
            addEditCustomerController.dotCount - 1) {
          addEditCustomerController.activeStep++;
          addEditCustomerController.autoValidateMode =
              AutovalidateMode.disabled;
          addEditCustomerController.update();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validateKycDetail() {
    if (kycDetailFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
        if (addEditCustomerController.custCategoryList == null ||
            addEditCustomerController.custCategoryList!.isEmpty) {
          addEditCustomerController.getCustomerCategory();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validateContactDetail() {
    if (contactDetailFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  // validateLocationDetail() {
  //   if (locationDetailFormKey.currentState!.validate()) {
  //     if (addEditCustomerController.activeStep <
  //         addEditCustomerController.dotCount - 1) {
  //       addEditCustomerController.activeStep++;
  //       addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
  //       addEditCustomerController.update();
  //
  //       if (addEditCustomerController.partnerList == null ||
  //           addEditCustomerController.partnerList!.isEmpty) {
  //         addEditCustomerController.setBtnClickEvent(false);
  //         addEditCustomerController.getActivePartner();
  //       }
  //     }
  //   } else {
  //     addEditCustomerController.autoValidateMode =
  //         AutovalidateMode.onUserInteraction;
  //     addEditCustomerController.update();
  //   }
  // }

  validateBusinessDetail() {
    if (businessPartnerFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
        if (addEditCustomerController.payModeList == null ||
            addEditCustomerController.payModeList!.isEmpty) {
          addEditCustomerController.getPaymentMode();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validatePaymentDetail() {
    if (paymentFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
        if (addEditCustomerController.servicesAreaList == null ||
            addEditCustomerController.servicesAreaList!.isEmpty) {
          addEditCustomerController.getServiceArea();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validatePresentAddDetail() {
    if (presentAddressFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();

        if (addEditCustomerController.pincodeList != null &&
            addEditCustomerController.pincodeList!.isNotEmpty) {
          addEditCustomerController.paymentSameAs = false;
          addEditCustomerController.paymentAddController.clear();
          addEditCustomerController.selPaymentPincode = null;
          addEditCustomerController.selPaymentArea = null;
          addEditCustomerController.selPaymentCity = null;
          addEditCustomerController.selPaymentState = null;
          addEditCustomerController.selPaymentCountry = null;
          addEditCustomerController.paymentPincodeList!.clear();
          addEditCustomerController.paymentPincodeList!
              .addAll(addEditCustomerController.pincodeList!);
          if (addEditCustomerController.billToList == null ||
              addEditCustomerController.billToList!.isEmpty) {
            addEditCustomerController.getBillToDetail();
          } else {
            if (addEditCustomerController.planGroupList == null ||
                addEditCustomerController.planGroupList!.isEmpty) {
              addEditCustomerController.getPlanGroupDetail();
            }
          }

          addEditCustomerController.update();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validatePaymentAddDetail() {
    if (paymentAddressFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();

        if (addEditCustomerController.pincodeList != null &&
            addEditCustomerController.pincodeList!.isNotEmpty) {
          addEditCustomerController.permanentSameAs = false;
          addEditCustomerController.permanentAddController.clear();
          addEditCustomerController.selPermanentPincode = null;
          addEditCustomerController.selPermanentArea = null;
          addEditCustomerController.selPermanentCity = null;
          addEditCustomerController.selPermanentState = null;
          addEditCustomerController.selPermanentCountry = null;
          addEditCustomerController.permanentPincodeList!.clear();
          addEditCustomerController.permanentPincodeList!
              .addAll(addEditCustomerController.pincodeList!);
          addEditCustomerController.update();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validatePermanentAddDetail() {
    if (permanentAddressFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
        if (addEditCustomerController.billToList == null ||
            addEditCustomerController.billToList!.isEmpty) {
          addEditCustomerController.getBillToDetail();
        } else {
          if (addEditCustomerController.planGroupList == null ||
              addEditCustomerController.planGroupList!.isEmpty) {
            addEditCustomerController.getPlanGroupDetail();
          }
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validatePlanDetail() {
    if (planDetailFormKey.currentState!.validate()) {
      if (addEditCustomerController.selPlanCategory == null) {
        Utils.showSnackbar(
            Strings.ERROR,
            "Minimum one Plan Details need to add",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }

      if (addEditCustomerController.selPlanCategory != null &&
          addEditCustomerController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.plan_group)) {
        if (addEditCustomerController.selPlanGroup == null) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }

      if (addEditCustomerController.selPlanCategory != null &&
          addEditCustomerController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.individual)) {
        if (addEditCustomerController.individualPlanList == null ||
            addEditCustomerController.individualPlanList!.isEmpty) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }
      //  addEditCustomerController.selectedBillTo=null;

      /*if ((addEditCustomerController.selPlanCategory!.text!
                  .equalsIgnoreCase(Strings.individual) &&
              addEditCustomerController.selectedBillTo != null &&
              addEditCustomerController.selectedBillTo!.id != 224) &&
          addEditCustomerController.selectedBillTo == null) {
        Utils.showSnackbar(Strings.ERROR, "Please select bill to detail",
            AppTheme.colorWhite, AppTheme.colorRed);
        return;
      }*/

      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        if (addEditCustomerController.popList == null ||
            addEditCustomerController.popList!.isEmpty) {
          addEditCustomerController.getAllPop();
        } else {
          addEditCustomerController.getAllPop();
        }
        addEditCustomerController.update();
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validateRadiusServiceDetail() {
    if (radiusServiceFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.selCharge = null;
        addEditCustomerController.selectedChargeType = null;
        addEditCustomerController.selectedRecurringMonth = null;
        addEditCustomerController.selectedChargePlan = null;
        addEditCustomerController.newPriceController.clear();
        addEditCustomerController.update();

        if (addEditCustomerController.selPlanCategory != null &&
            addEditCustomerController.selPlanCategory!.text!
                .equalsIgnoreCase(Strings.plan_group) &&
            addEditCustomerController.selPlanGroup != null) {
          // List<PlanMappingDetail>? planMappingList = addEditCustomerController.selPlanGroup!.planMappingList;
          ServiceAreaPlanPostpaidplanList? planMappingList =
              addEditCustomerController.serviceAreaPlanPostpaidData;
          List<IndividualPlanData>? planList = [];
          // for (var element in planMappingList!) {
          //   if (element.plan != null) {
          planList.add(IndividualPlanData(
              // planService: PlanServiceDetail(name: element.service),
              planService: ServicesByServiceAreaDataList(
                  name: addEditCustomerController
                      .serviceAreaPlanPostpaidData!.name),
              planDetail: planMappingList,
              discount: addEditCustomerController.discountController.text));
          // }
          // }
          addEditCustomerController.chargePlanList!.clear();
          addEditCustomerController.chargePlanList!.addAll(planList);
        } else {
          addEditCustomerController.chargePlanList!.clear();
          addEditCustomerController.chargePlanList!
              .addAll(addEditCustomerController.individualPlanList!);
        }

        if (addEditCustomerController.chargeList == null ||
            addEditCustomerController.chargeList!.isEmpty) {
          addEditCustomerController.getChargeList();
        }
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validateAdditionalServiceDetail() {
    if (additionalServiceFormKey.currentState!.validate()) {
      if (addEditCustomerController.activeStep <
          addEditCustomerController.dotCount - 1) {
        addEditCustomerController.activeStep++;
        addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
        addEditCustomerController.update();
      }
    } else {
      addEditCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCustomerController.update();
    }
  }

  validateChargeDetail() {
    //if (chargeDetailsFormKey.currentState!.validate()) {
    if (addEditCustomerController.activeStep <
        addEditCustomerController.dotCount - 1) {
      addEditCustomerController.activeStep++;
      addEditCustomerController.autoValidateMode = AutovalidateMode.disabled;
      addEditCustomerController.update();
    }
    /* } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }*/
  }

  validateMacMapppingDetail() {
    // submit add-edit customer
    addEditCustomerController.createCustomerApiCall();
  }

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        addEditCustomerController.selectedLocation = data;
        addEditCustomerController.update();
        addEditCustomerController.getLocationToLatLong();
      }
    }
  }

  openParentCustomerScreen(String? type) async {
    var result = await Get.to(ParentCustomerList(),
        arguments: {Constant.CUSTOMER_TYPE: addEditCustomerController.type!});
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.parent_customer)) {
          addEditCustomerController.selectedParentCustomer = data;
          addEditCustomerController.parentCustomerController.text = data.name!;
          addEditCustomerController.selectStaffsByServiceAreaData = null;
          addEditCustomerController
              .getStaffsByServiceAreaAPI(data.networkDetails!.serviceareaid);
          log("serviceareaid>>> ${data.networkDetails!.serviceareaid}");
        } else if (type.equalsIgnoreCase(Strings.billableTo)) {
          addEditCustomerController.selectedParentCustomer = data;
          addEditCustomerController.billableToController.text = data.name!;
          addEditCustomerController.billableToCustomerId = data.id!;
        }
        addEditCustomerController.update();
      }
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
      addEditCustomerController.setBtnClickEvent(true);
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
        addEditCustomerController.setBtnClickEvent(true);
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
        addEditCustomerController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    addEditCustomerController.isLoading = true;
    addEditCustomerController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        addEditCustomerController.setBtnClickEvent(false);
        addEditCustomerController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        addEditCustomerController.latController.text =
            currentPosition.latitude.toString();
        addEditCustomerController.longController.text =
            currentPosition.longitude.toString();
        addEditCustomerController.update();
      } else {
        addEditCustomerController.isLoading = false;
        addEditCustomerController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      addEditCustomerController.isLoading = false;
      addEditCustomerController.update();
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
