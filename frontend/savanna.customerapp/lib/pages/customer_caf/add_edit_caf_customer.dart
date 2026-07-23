import 'dart:async';
import 'dart:convert';
import 'dart:developer';
import 'dart:io';
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
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/partner_list_res.dart';
import 'package:savbill/pages/customer/model/response/payment_mode_list_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer_caf/add_edit_caf_customer_controller.dart';
import 'package:savbill/pages/customer_caf/response/get_area_all_res.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_pincode_all_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
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

class AddEditCafCustomer extends StatefulWidget {
  @override
  _AddEditCafCustomerState createState() => _AddEditCafCustomerState();
}

class _AddEditCafCustomerState extends State<AddEditCafCustomer>
    with WidgetsBindingObserver
    implements LocationBtnAction {
  final addEditCafCustomerController = Get.put(AddEditCafCustomerController());

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
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    addEditCafCustomerController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (addEditCafCustomerController.checkBtnClickEvent) {
          // addEditCafCustomerController.setBtnClickEvent(false);
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
      child: GetBuilder<AddEditCafCustomerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            resizeToAvoidBottomInset: false,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditCafCustomerController.isLoading),
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
            numbers: addEditCafCustomerController.data,
            enableStepTapping: false,
            enableNextPreviousButtons: false,
            activeStep: addEditCafCustomerController.activeStep,
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
              addEditCafCustomerController.activeStep = index;
              addEditCafCustomerController.update();
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
            padding: EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
            child: SingleChildScrollView(
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
                      addEditCafCustomerController.activeStep == 0
                          ? basicDetailForm()
                          : Container(),
                      // addEditCafCustomerController.activeStep == 1
                      //     ? kycDetailForm()
                      //     : Container(),
                      // addEditCafCustomerController.activeStep == 2
                      //     ? contactDetailForm()
                      //     : Container(),
                      // addEditCafCustomerController.activeStep == 3
                      //     ? locationDetailForm()
                      //     : Container(),
                      // addEditCafCustomerController.activeStep == 1
                      //     ? businessPartnerForm()
                      //     : Container(),
                      // addEditCafCustomerController.activeStep == 2
                      //     ? paymentForm()
                      //     : Container(),
                      addEditCafCustomerController.activeStep == 1
                          ? presentAddressForm()
                          : Container(),
                      /*addEditCafCustomerController.activeStep == 2
                          ? paymentAddressForm()
                          : Container(),
                      addEditCafCustomerController.activeStep == 3
                          ? permanentAddressForm()
                          : Container(),*/
                      addEditCafCustomerController.activeStep == 2
                          ? planDetailForm()
                          : Container(),
                      /* addEditCafCustomerController.activeStep == 3
                          ? additionalServiceForm()
                          : Container(),*/
                      /* addEditCafCustomerController.activeStep == 4
                          ? radiusServiceForm()
                          : Container(),
                      addEditCafCustomerController.activeStep == 5
                          ? chargeDetailForm()
                          : Container(),
                      addEditCafCustomerController.activeStep == 6
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
                      if (addEditCafCustomerController.activeStep > 0) {
                        addEditCafCustomerController.activeStep--;
                        addEditCafCustomerController.update();
                        /*setState(() {
                          activeStep--;
                        });*/
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
                      if (addEditCafCustomerController.activeStep == 2) {
                        if (addEditCafCustomerController.individualPlanList ==
                                null ||
                            addEditCafCustomerController
                                .individualPlanList!.isEmpty) {
                          Utils.showSnackbar(
                              Strings.ERROR,
                              "Minimum one Plan Details need to add",
                              AppTheme.colorWhite,
                              AppTheme.colorRed);
                        } else {
                          validateMacMapppingDetail();
                        }
                      } else {
                        validateForm();
                      }
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: addEditCafCustomerController.activeStep == 2
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
    switch (addEditCafCustomerController.activeStep) {
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
        addEditCafCustomerController.countryCode = "+${country.phoneCode}";
        addEditCafCustomerController.update();
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                    value: addEditCafCustomerController.selectedBDType,
                    items: addEditCafCustomerController.bdTypeList!
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
                      addEditCafCustomerController.selectedBDType =
                          value as CustomerTitle?;
                      addEditCafCustomerController.update();
                      // addEditCafCustomerController.getCustomerSector();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController.selectedBDType == null) {
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
                        addEditCafCustomerController.fnameController,
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
                        addEditCafCustomerController.lnameController,
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
                        addEditCafCustomerController.contactPersonController,
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
          //========================== CAF No ===================================
          /*Row(
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
                        addEditCafCustomerController.cafNoController,
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/
          addEditCafCustomerController.isCredentialMatchWithAccountNo == false
              ? Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.aaa_username, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.aaa_username,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                    addEditCafCustomerController.aaaUserNameController,
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
                        return Strings.please_enter_aaa_username;
                      }
                      return null;
                    },
                    onTextFiledOnTap: () {},
                    readOnly: addEditCafCustomerController.action!
                        .equalsIgnoreCase(Strings.add)
                        ? false
                        : true),
              ),
            ],
          )
              : SizedBox.shrink(),
          addEditCafCustomerController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          addEditCafCustomerController.isCredentialMatchWithAccountNo == false
              ? Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.aaa_password, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.aaa_password,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                    addEditCafCustomerController.aaaPasswordController,
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
                        return Strings.please_enter_aaa_password;
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
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(
                width: Constant.SMALL_PADDING,
                child: Checkbox(
                  value: addEditCafCustomerController
                      .isCredentialMatchWithAccountNo,
                  activeColor: AppTheme.colorPrimary,
                  onChanged: (value) {
                    addEditCafCustomerController
                            .isCredentialMatchWithAccountNo =
                        !addEditCafCustomerController
                            .isCredentialMatchWithAccountNo;
                    addEditCafCustomerController.aaaUserNameController.clear();
                    addEditCafCustomerController.aaaPasswordController.clear();
                    addEditCafCustomerController.update();
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
                    addEditCafCustomerController.usernameController,
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
                    readOnly: addEditCafCustomerController.action!
                        .equalsIgnoreCase(Strings.add)
                        ? false
                        : true),
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
                    addEditCafCustomerController.passwordController,
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
          ),

          const SizedBox(height: Constant.MEDIUM_PADDING),
          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.calendar_type, true),
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
          //             alignment: Alignment.centerLeft,
          //             child: Text(
          //               Strings.calendar_type,
          //               style: TextStyle(
          //                 fontSize: AppTheme.medium,
          //                 color: AppTheme.colorIconGrey,
          //                 fontFamily: AppTheme.appFontName,
          //               ),
          //             ),
          //           ),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addEditCafCustomerController.selectedCalenderType,
          //           items: addEditCafCustomerController.calenderTypeList!
          //               .map((DropdownDetail value) {
          //             return DropdownMenuItem<DropdownDetail>(
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
          //             addEditCafCustomerController.selectedCalenderType =
          //             value as DropdownDetail?;
          //             // if (addEditCafCustomerController.custCategoryList == null ||
          //             //     addEditCafCustomerController.custCategoryList!.isEmpty) {
          //             //   addEditCafCustomerController.getCustomerCategory();
          //             // }
          //             addEditCafCustomerController.update();
          //           },
          //           validator: (value) {
          //             if (value == null ||
          //                 addEditCafCustomerController.selectedCalenderType ==
          //                     null) {
          //               return Strings.please_select_calendar_type;
          //             }
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
          (addEditCafCustomerController.type != null &&
              addEditCafCustomerController.type!
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
                    value: addEditCafCustomerController.selectedBillDay,
                    items: addEditCafCustomerController.billDayList!
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
                      addEditCafCustomerController.selectedBillDay =
                      value as int?;
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController.selectedBillDay ==
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
          (addEditCafCustomerController.type != null &&
              addEditCafCustomerController.type!
                  .equalsIgnoreCase(Strings.postpaid))
              ? const SizedBox(
            height: Constant.MEDIUM_PADDING,
          )
              : Container(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.tin_no, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_tin_no,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCafCustomerController.vatController,
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
                      if(value!.isEmpty){
                        return Strings.enter_pan_no;
                      }else if (value.length<10 ){
                        return Strings.enter_valid_pan_no;
                      }
                      return null;
                    },
                    onTextFiledOnTap: () {},
                    readOnly: false),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),

          //============================== Passport No  & VAT ============================
          /* Row(
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
                        addEditCafCustomerController.passportController,
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
                        addEditCafCustomerController.tinController,
                    maxLength: 9,
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
                      if (addEditCafCustomerController
                          .tinController.text.isNotEmpty) {
                        if (int.tryParse(value!.length.toString()) != 9) {
                          return Strings.enter_vat_number;
                        }
                      }
                      return null;
                    },
                    onTextFiledOnTap: () {},
                    readOnly: false),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          /*Row(
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
                    value: addEditCafCustomerController.selectedValleyType,
                    items: addEditCafCustomerController.valleyTypeList!
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
                      addEditCafCustomerController.selectedValleyType =
                          value as ValleyType?;
                      addEditCafCustomerController.update();
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
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id ==
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
                          value:
                              addEditCafCustomerController.selectedInsideValley,
                          items: addEditCafCustomerController.insideValleyList!
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
                            addEditCafCustomerController.selectedInsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCafCustomerController.update();
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

          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id ==
                      Constant.INSIDE_VALLEY)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id ==
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
                          value: addEditCafCustomerController
                              .selectedOutsideValley,
                          items: addEditCafCustomerController.outsideValleyList!
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
                            addEditCafCustomerController.selectedOutsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCafCustomerController.update();
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
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id ==
                      Constant.OUTSIDE_VALLEY)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),*/

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
                    value: addEditCafCustomerController.selectedFeasibilityData,
                    items: addEditCafCustomerController.feasibilityList!
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
                      addEditCafCustomerController.selectedFeasibilityData =
                          value as DropdownDetail?;
                      addEditCafCustomerController.update();
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

          addEditCafCustomerController.selectedFeasibilityData != null &&
                  addEditCafCustomerController.selectedFeasibilityData!.id!
                      .equalsIgnoreCase(Strings.na)
              ? Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Flexible(
                      flex: 1,
                      child: titleWithRequireWidget(
                          Strings.feasibilityRemark, false),
                    ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.feasibilityRemark,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController: addEditCafCustomerController
                              .feasibilityRemarkController,
                          maxLength: 250,
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
              : const SizedBox.shrink(),

          addEditCafCustomerController.selectedFeasibilityData != null && addEditCafCustomerController.selectedFeasibilityData!.id!.equalsIgnoreCase(Strings.na) ?
          const SizedBox(height: Constant.MEDIUM_PADDING) : const SizedBox.shrink(),

          //============================== Dedicated Staff ============================

          /*Row(
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
                    value: addEditCafCustomerController
                        .selectStaffsByServiceAreaData,
                    items: addEditCafCustomerController.staffsByServiceAreaList!
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
                      addEditCafCustomerController
                              .selectStaffsByServiceAreaData =
                          value as StaffsByServiceAreaData?;
                      addEditCafCustomerController.update();
                      // addEditCafCustomerController.manageCustomerSubType();
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

          //============================== Parent Customer & Parent Exp ============================

          /*Row(
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
                    labelText: Strings.parent_customer,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addEditCafCustomerController.parentCustomerController,
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
                    value:
                        addEditCafCustomerController.selectedParentExperience,
                    items: addEditCafCustomerController.parentExperienceList!
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
                      addEditCafCustomerController.selectedParentExperience =
                          value as DropdownDetail?;
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      // if (value == null ||
                      //     addEditCafCustomerController.selectedParentExperience ==
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.dedicated_staff, false),
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
          //               child: Text(Strings.dedicated_staff,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value:
          //               addEditCafCustomerController.selectStaffsByServiceAreaData,
          //           items: addEditCafCustomerController.staffsByServiceAreaList!
          //               .map((StaffsByServiceAreaData value) {
          //             return DropdownMenuItem<StaffsByServiceAreaData>(
          //               value: value,
          //               child: Align(
          //                 alignment: Alignment.centerLeft,
          //                 child: CustomText(
          //                   title: value.displayName!,
          //                   colors: AppTheme.colorBlack,
          //                   textAlign: TextAlign.start,
          //                   fontSize: AppTheme.small,
          //                   fontWeight: FontWeight.w500,
          //                 ), //Text(value.desig!),
          //               ),
          //             );
          //           }).toList(),
          //           onChanged: (value) {
          //             addEditCafCustomerController.selectStaffsByServiceAreaData =
          //                 value as StaffsByServiceAreaData?;
          //             addEditCafCustomerController.update();
          //             // addEditCafCustomerController.manageCustomerSubType();
          //           },
          //           validator: (value) {
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),

          //============================== Customer Type ============================
          /* Row(
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
                    value: addEditCafCustomerController.selectParentCustType,
                    items: addEditCafCustomerController.parentCustTypeList!
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
                      addEditCafCustomerController.selectParentCustType =
                          value as DropdownDetail?;
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController.selectParentCustType ==
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

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
                        addEditCafCustomerController.saleRemarkController,
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

          /*Row(
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
                    value: addEditCafCustomerController.selectedCustSector,
                    items: addEditCafCustomerController.custSectorList!
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
                      addEditCafCustomerController.selectedCustSector =
                          value as CustomerSectorData?;
                      addEditCafCustomerController.update();
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

          addEditCafCustomerController.selectedCustSector != null
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
                              addEditCafCustomerController.customerSectorType,
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
              : SizedBox.shrink(),
          addEditCafCustomerController.selectedCustSector != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              :SizedBox.shrink(),

          /*Row(
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
                    value: addEditCafCustomerController.selectAllDepartmentData,
                    items: addEditCafCustomerController.allDepartmentDataList!
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
                      addEditCafCustomerController.selectAllDepartmentData =
                          value as DepartmentListData?;
                      // if (addEditCafCustomerController.servicesAreaList == null ||
                      //     addEditCafCustomerController.servicesAreaList!.isEmpty) {
                      //   addEditCafCustomerController.getServiceArea();
                      // }
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      // if (value == null ||
                      //     addEditCafCustomerController.selectAllDepartmentData ==
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(
                    Strings.parent_quota_type_new, false),
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
                        child: Text(Strings.parent_quota_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addEditCafCustomerController.selectParentQuotaType,
                    items: addEditCafCustomerController.parentQuotaTypeList!
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
                      addEditCafCustomerController.selectParentQuotaType =
                          value as DropdownDetail?;
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      // if (value == null ||
                      //     addEditCafCustomerController.selectedParentExperience ==
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
                    addEditCafCustomerController.renewPlanLimitController,
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

          //============================== Is Parent Location ============================
          /*Container(
            margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
            child: Row(children: [
              SizedBox(
                width: 10,
                child: Checkbox(
                  value: addEditCafCustomerController.isParentLocation,
                  activeColor: AppTheme.colorPrimary,
                  onChanged: (value) {
                    addEditCafCustomerController.isParentLocation =
                        !addEditCafCustomerController.isParentLocation;
                    addEditCafCustomerController.update();
                  },
                ),
              ),
              const SizedBox(width: Constant.SMALL_PADDING),
              InkWell(
                  child: CustomText(
                    title: Strings.isParentLocation,
                    textAlign: TextAlign.start,
                    colors: AppTheme.colorBlack,
                    fontSize: AppTheme.medium,
                    fontWeight: FontWeight.w400,
                  ),
                  onTap: () {
                    addEditCafCustomerController.isParentLocation =
                        !addEditCafCustomerController.isParentLocation;
                    addEditCafCustomerController.update();
                  }),
            ]),
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.fax_number, false),
          //     ),
          //     const SizedBox(
          //       width: Constant.SMALL_PADDING,
          //     ),
          //     Flexible(
          //       flex: 2,
          //       child: CoustomTextField(
          //           labelText: Strings.fax_number,
          //           hintColor: AppTheme.colorIconGrey,
          //           textEditingController:
          //               addEditCafCustomerController.faxNumberController,
          //           maxLength: 50,
          //           borderEnableColors: AppTheme.colorIconGrey,
          //           borderFocusColors: AppTheme.colorIconGrey,
          //           textColor: AppTheme.colorBlack,
          //           keyboardType: TextInputType.phone,
          //           fontSize: AppTheme.small,
          //           textInputAction: TextInputAction.next,
          //           fontWeight: FontWeight.w500,
          //           contentPadding: const EdgeInsets.symmetric(
          //               horizontal: Constant.MEDIUM_PADDING,
          //               vertical: Constant.MEDIUM_PADDING),
          //           borderCorner: Constant.BTN_ROUNDED_CORNER,
          //           onTextValidator: (String? value) {
          //             return null;
          //           },
          //           onTextFiledOnTap: () {},
          //           readOnly: false),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),

          /*  const SizedBox(height: Constant.MEDIUM_PADDING),
          addEditCafCustomerController.selectedParentCustomer != null
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
                          value: addEditCafCustomerController.selectedInvoiceType,
                          items: addEditCafCustomerController.invoiceTypeList!
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
                            addEditCafCustomerController.selectedInvoiceType =
                                value as DropdownDetail?;
                            addEditCafCustomerController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                addEditCafCustomerController.selectedInvoiceType ==
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

          /* addEditCafCustomerController.selectedParentCustomer != null
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
                    value: addEditCafCustomerController.selectedPop,
                    items: addEditCafCustomerController.popList!
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
                      addEditCafCustomerController.selectedPop =
                          value as PopDetail?;
                      addEditCafCustomerController.update();
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
                    value: addEditCafCustomerController.selectedValleyType,
                    items: addEditCafCustomerController.valleyTypeList!
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
                      addEditCafCustomerController.selectedValleyType =
                          value as ValleyType?;
                      addEditCafCustomerController.update();
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
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id == 447)
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
                          value: addEditCafCustomerController.selectedInsideValley,
                          items: addEditCafCustomerController.insideValleyList!
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
                            addEditCafCustomerController.selectedInsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCafCustomerController.update();
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

          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id == 447)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id == 448)
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
                              addEditCafCustomerController.selectedOutsideValley,
                          items: addEditCafCustomerController.outsideValleyList!
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
                            addEditCafCustomerController.selectedOutsideValley =
                                value as InsideOutsideValleyData?;
                            addEditCafCustomerController.update();
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
          (addEditCafCustomerController.selectedValleyType != null &&
                  addEditCafCustomerController.selectedValleyType!.id == 448)
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),*/
          const SizedBox(height: Constant.MEDIUM_PADDING),

          CustomText(
            title: Strings.contact_details,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontSize: AppTheme.small,
            fontWeight: FontWeight.bold,
          ),

          const SizedBox(height: Constant.LARGE_PADDING),

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
                      FilteringTextInputFormatter.deny(
                        RegExp(r'^0+'),
                      ),
                    ],
                    textEditingController:
                        addEditCafCustomerController.mobileController,
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
                            title: addEditCafCustomerController.countryCode,
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

          //============================== Secondary Mobile Number  & Telephone ============================

          /*Row(
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
                      FilteringTextInputFormatter.deny(
                        RegExp(r'^0+'),
                      ),
                    ],
                    textEditingController:
                        addEditCafCustomerController.secondaryMobileController,
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
                            title: addEditCafCustomerController.countryCode,
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
                        addEditCafCustomerController.telephoneController,
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/
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
                        addEditCafCustomerController.emailController,
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
          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.customer_new_category, true),
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
          //               child: Text(Strings.customer_category,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addEditCafCustomerController.selectedCustCategory,
          //           items: addEditCafCustomerController.custCategoryList!
          //               .map((CustomerCategoryDetail value) {
          //             return DropdownMenuItem<CustomerCategoryDetail>(
          //               value: value,
          //               child: Align(
          //                 alignment: Alignment.centerLeft,
          //                 child: CustomText(
          //                   title: value.value!,
          //                   colors: AppTheme.colorBlack,
          //                   textAlign: TextAlign.start,
          //                   fontSize: AppTheme.small,
          //                   fontWeight: FontWeight.w500,
          //                 ), //Text(value.desig!),
          //               ),
          //             );
          //           }).toList(),
          //           onChanged: (value) {
          //             addEditCafCustomerController.selectedCustCategory =
          //                 value as CustomerCategoryDetail?;
          //             addEditCafCustomerController.update();
          //           },
          //           validator: (value) {
          //             if (value == null ||
          //                 addEditCafCustomerController.selectedCustCategory == null) {
          //               return Strings.select_customer_category;
          //             }
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
          /*Row(
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
                    value: addEditCafCustomerController.selectedCustType,
                    items: addEditCafCustomerController.custTypeList!
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
                      addEditCafCustomerController.selectedCustType =
                          value as CustomerTypeData?;
                      addEditCafCustomerController.update();
                      addEditCafCustomerController.manageCustomerSubType();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController.selectedCustType ==
                              null) {
                        return Strings.please_select_customer_type;
                      }
                      return null;
                    },
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addEditCafCustomerController.selectedCustType != null &&
                  addEditCafCustomerController.custSubTypeDDl
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
                          value: addEditCafCustomerController
                              .selectedCustomerSubType,
                          items: addEditCafCustomerController
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
                              : addEditCafCustomerController
                                  .customerSubTypeList!
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
                            addEditCafCustomerController
                                    .selectedCustomerSubType =
                                value as CustomerSubType?;
                            addEditCafCustomerController.update();
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
          addEditCafCustomerController.selectedCustType != null &&
                  addEditCafCustomerController.custSubTypeDDl == false
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
                              addEditCafCustomerController.customerSubType,
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
          addEditCafCustomerController.selectedCustType != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),*/
          //============================== DOB ============================
          /* Row(
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
                        addEditCafCustomerController.dobDateController,
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
          const SizedBox(height: Constant.MEDIUM_PADDING),*/

          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.status, true),
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
          //               child: Text(Strings.status,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addEditCafCustomerController.selectedStatus,
          //           items: addEditCafCustomerController.statusList!
          //               .map((CustomerStatusDetail value) {
          //             return DropdownMenuItem<CustomerStatusDetail>(
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
          //             addEditCafCustomerController.selectedStatus =
          //                 value as CustomerStatusDetail?;
          //             addEditCafCustomerController.update();
          //           },
          //           validator: (value) {
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
        ],
      ),
    );
  }

  kycDetailForm() {
    return Form(
      key: kycDetailFormKey,
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.gstController,
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
                        addEditCafCustomerController.vatController,
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
                        addEditCafCustomerController.nationalIdController,
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
        ],
      ),
    );
  }

  contactDetailForm() {
    return Form(
      key: contactDetailFormKey,
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.mobileController,
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
                            title: addEditCafCustomerController.countryCode,
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
                        addEditCafCustomerController.telephoneController,
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
                        addEditCafCustomerController.emailController,
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
                    value: addEditCafCustomerController.selectedCustCategory,
                    items: addEditCafCustomerController.custCategoryList!
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
                      addEditCafCustomerController.selectedCustCategory =
                          value as CustomerCategoryDetail?;
                      addEditCafCustomerController.update();
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
                    value: addEditCafCustomerController.selectedCustType,
                    items: addEditCafCustomerController.custTypeList!
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
                      addEditCafCustomerController.selectedCustType =
                          value as CustomerTypeData?;
                      addEditCafCustomerController.update();
                      addEditCafCustomerController.manageCustomerSubType();
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
          addEditCafCustomerController.selectedCustType != null &&
                  addEditCafCustomerController.custSubTypeDDl
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
                          value: addEditCafCustomerController
                              .selectedCustomerSubType,
                          items: addEditCafCustomerController
                              .customerSubTypeList!
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
                            addEditCafCustomerController
                                    .selectedCustomerSubType =
                                value as CustomerSubType?;
                            addEditCafCustomerController.update();
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
          addEditCafCustomerController.selectedCustType != null &&
                  addEditCafCustomerController.custSubTypeDDl == false
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
                              addEditCafCustomerController.customerSubType,
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
          addEditCafCustomerController.selectedCustType != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : Container(),
        ],
      ),
    );
  }

  /* locationDetailForm() {
    return Form(
      key: locationDetailFormKey,
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.latController,
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
                        addEditCafCustomerController.longController,
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                    value: addEditCafCustomerController.selectedPartner,
                    items: addEditCafCustomerController.partnerList!
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
                      addEditCafCustomerController.selectedPartner =
                          value as PartnerDetail?;
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController.selectedPartner ==
                              null) {
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.amountController,
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
                        addEditCafCustomerController.referenceNoController,
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
                        addEditCafCustomerController.paymentDateController,
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
                    value: addEditCafCustomerController.selectedPayMode,
                    items: addEditCafCustomerController.payModeList!
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
                      addEditCafCustomerController.selectedPayMode =
                          value as PaymentModeDetail?;
                      addEditCafCustomerController.update();
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                width: Constant.MEDIUM_PADDING,
              ),
              Flexible(
                flex: 2,
                child: DropdownSearch<ServicesAreaDetail>(
                  key: addEditCafCustomerController.serviceAreaDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addEditCafCustomerController.selPresentServiceArea,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.servicesAreaList!,
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
                    addEditCafCustomerController.selPresentServiceArea = value;
                    addEditCafCustomerController.selPresentPincode = null;
                    addEditCafCustomerController.selPresentCity = null;
                    addEditCafCustomerController.selPresentState = null;
                    addEditCafCustomerController.selPresentCountry = null;
                    addEditCafCustomerController.selectedNewPinCodeAll = null;
                    addEditCafCustomerController.selectedNewAreaAllData = null;
                    addEditCafCustomerController.selectedSubAreaData = null;
                    addEditCafCustomerController.selectedBuildingManagementData = null;
                    addEditCafCustomerController.selectedBuildingNumber = null;
                    addEditCafCustomerController.pincodeList!.clear();
                    addEditCafCustomerController.serviceAreaId = value!.id;
                    addEditCafCustomerController.update();
                    serviceAreaId.add(value.id!);
                    log("serviceAreaId===>>${value.id}");
                    addEditCafCustomerController.getStaffsByServiceAreaAPI(value.id,true);
                    addEditCafCustomerController.getAllBranchesByServiceAreaData(serviceAreaId, true);
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCafCustomerController.selPresentServiceArea ==
                            null) {
                      return Strings.select_service_area;
                    }
                    return null;
                  },
                ),
              ),
              // InkWell(
              //     onTap: () {
              //       addEditCafCustomerController.getServiceArea();
              //     },
              //     child: Icon(
              //       Icons.refresh_outlined,
              //       color: AppTheme.title_dark,
              //       size: 16,
              //     ))
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
                    value: addEditCafCustomerController
                        .selectBranchesByServiceAreaData,
                    items: addEditCafCustomerController
                            .branchesByServiceAreaList!.isEmpty
                        ? [
                            DropdownMenuItem<CustomerSubType>(
                              value: null,
                              enabled: false,
                              child: CustomText(
                                title: Strings.no_data_found,
                                colors: AppTheme.title_dark,
                                fontSize: AppTheme.small,
                              ), // Disable selection
                            ),
                          ]
                        : addEditCafCustomerController
                            .branchesByServiceAreaList!
                            .map((BranchesByServiceAreaDataList value) {
                            return DropdownMenuItem<
                                BranchesByServiceAreaDataList>(
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
                      addEditCafCustomerController
                              .selectBranchesByServiceAreaData =
                          value as BranchesByServiceAreaDataList?;
                      // addEditCafCustomerController.getServiceAreaDetail();
                      addEditCafCustomerController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addEditCafCustomerController
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

          addEditCafCustomerController.areaDetail != null && addEditCafCustomerController.areaDetail!.serviceAreaType != null &&
                  addEditCafCustomerController.areaDetail!.serviceAreaType!
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
                          value: addEditCafCustomerController.selectedBlockNo,
                          items: addEditCafCustomerController
                                  .blockNoOptions.isEmpty
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
                              : addEditCafCustomerController.blockNoOptions
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
                            addEditCafCustomerController.selectedBlockNo =
                                int.parse(value.toString());
                            addEditCafCustomerController.update();
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
          addEditCafCustomerController.areaDetail != null && addEditCafCustomerController.areaDetail!.serviceAreaType != null &&
                  addEditCafCustomerController.areaDetail!.serviceAreaType!
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
                        addEditCafCustomerController.presentAddController,
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
              Expanded(
                flex: 2,
                child: DropdownSearch<PinCodeAllDataList>(
                  key: addEditCafCustomerController.pincodeDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addEditCafCustomerController.selectedNewPinCodeAll,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.newPinCodeAllList!,
                  compareFn: (item1, item2) =>
                      item1.pincodeid == item2.pincodeid,
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
                    addEditCafCustomerController.selectedNewPinCodeAll = value;
                    addEditCafCustomerController.update();
                    addEditCafCustomerController
                        .getServicAreaIdByPincodeCAll(value!.pincodeid);
                    addEditCafCustomerController.getPinCodeToAreaData(
                        value.pincodeid!, "Present");
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCafCustomerController.selectedNewPinCodeAll ==
                            null) {
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
              Expanded(
                flex: 2,
                child: DropdownSearch<AreaAllDataList>(
                  key: addEditCafCustomerController.areaDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addEditCafCustomerController.selectedNewAreaAllData,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.newAreaAllList!,
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
                    addEditCafCustomerController.selectedNewAreaAllData = value;
                    addEditCafCustomerController.update();
                    addEditCafCustomerController.buildingManagementDataList!.clear();
                    addEditCafCustomerController
                        .selectedBuildingManagementData = null;
                    addEditCafCustomerController.buildingNumberList!.clear();
                    addEditCafCustomerController.selectedBuildingNumber = null;
                    addEditCafCustomerController.subAreaDataList!.clear();
                    addEditCafCustomerController.selectedSubAreaData = null;
                    addEditCafCustomerController.getAreaDetail(
                        subAreaId: null,
                        areaId: addEditCafCustomerController
                            .selectedNewAreaAllData!.id!,
                        type: "Present");

                    addEditCafCustomerController
                        .getSubAreaFromAreaCall(value!.id);
                  },
                  validator: (value) {
                    if (value == null ||
                        addEditCafCustomerController.selectedNewAreaAllData ==
                            null) {
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
                child: titleWithRequireWidget(Strings.sub_area, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 2,
                child: DropdownSearch<SubAreaDataList>(
                  key: addEditCafCustomerController.subAreaDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addEditCafCustomerController.selectedSubAreaData,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.subAreaDataList!,
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
                    addEditCafCustomerController.selectedSubAreaData = value;
                    addEditCafCustomerController.buildingManagementDataList!.clear();
                    addEditCafCustomerController.selectedBuildingManagementData = null;
                    addEditCafCustomerController.buildingNumberList!.clear();
                    addEditCafCustomerController.selectedBuildingNumber = null;
                    addEditCafCustomerController.idData = value!.id;
                    addEditCafCustomerController.getAreaIdFromSubAreaIdCall(
                        value.id, "Present");
                  },
                  validator: (value) {
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
                child: titleWithRequireWidget(Strings.building_name, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 2,
                child: DropdownSearch<BuildingManagementDataList>(
                  key: addEditCafCustomerController.buildingNameDropDownKey,
                  mode: Mode.form,
                  selectedItem: addEditCafCustomerController
                      .selectedBuildingManagementData,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.buildingManagementDataList!,
                  compareFn: (item1, item2) =>
                      item1 == item2,
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
                        hintText: Strings.select_building,
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
                  onChanged: (BuildingManagementDataList? value) {
                    addEditCafCustomerController.selectedBuildingManagementData = value!;
                    addEditCafCustomerController.getBuildingMgmtNumbersCall(value.buildingMgmtId);
                    addEditCafCustomerController.update();
                  },
                  validator: (value) {
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
                child: titleWithRequireWidget(Strings.building_no, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 2,
                child: DropdownSearch<String>(
                  key: addEditCafCustomerController.buildingNoDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addEditCafCustomerController.selectedBuildingNumber,
                  items: (filter, infiniteScrollProps) =>
                      addEditCafCustomerController.buildingNumberList!,
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
                        hintText: Strings.select_building_number,
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
                    addEditCafCustomerController.selectedBuildingNumber = value;
                    addEditCafCustomerController.update();
                  },
                  validator: (value) {
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
                        value: addEditCafCustomerController.selPresentCity,
                        items: addEditCafCustomerController.cityList!
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
                          addEditCafCustomerController.selPresentCity =
                              value as CityDetail?;
                          addEditCafCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCafCustomerController.selPresentCity ==
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
                        value: addEditCafCustomerController.selPresentState,
                        items: addEditCafCustomerController.stateList!
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
                          addEditCafCustomerController.selPresentState =
                              value as StateDetail?;
                          addEditCafCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCafCustomerController.selPresentState ==
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
                        value: addEditCafCustomerController.selPresentCountry,
                        items: addEditCafCustomerController.countryList!
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
                          addEditCafCustomerController.selPresentCountry =
                              value as CountryDetail?;
                          addEditCafCustomerController.update();
                        },
                        validator: (value) {
                          if (value == null ||
                              addEditCafCustomerController.selPresentCountry ==
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
                        addEditCafCustomerController.latController,
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
                        addEditCafCustomerController.longController,
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.paymentAddController,
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
                    value: addEditCafCustomerController.selPaymentPincode,
                    items: addEditCafCustomerController.paymentPincodeList!
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
                      addEditCafCustomerController.selPaymentPincode =
                          value as PincodeDetail?;
                      addEditCafCustomerController.update();
                      addEditCafCustomerController.getPinCodeToAreaData(
                          addEditCafCustomerController
                              .selPaymentPincode!.pincodeid!,
                          Strings.payment_address_details);

                      /*   addEditCafCustomerController.getPinCodeToAreaList(
                          addEditCafCustomerController
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
                    value: addEditCafCustomerController.selPaymentArea,
                    items: addEditCafCustomerController.paymentAreaList!
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
                      addEditCafCustomerController.selPaymentArea =
                          value as PincodeAreaDetail?;
                      addEditCafCustomerController.update();

                      addEditCafCustomerController.getAreaDetail(
                          subAreaId: null,
                          areaId:
                              addEditCafCustomerController.selPaymentArea!.id!,
                          type: Strings.payment_address_details);
                      /*   addEditCafCustomerController
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
                        value: addEditCafCustomerController.selPaymentCity,
                        items: addEditCafCustomerController.paymentCityList!
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
                          addEditCafCustomerController.selPaymentCity =
                              value as CityDetail?;
                          addEditCafCustomerController.update();
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
                          value: addEditCafCustomerController.selPaymentState,
                          items: addEditCafCustomerController.paymentStateList!
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
                            addEditCafCustomerController.selPaymentState =
                                value as StateDetail?;
                            addEditCafCustomerController.update();
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
                        value: addEditCafCustomerController.selPaymentCountry,
                        items: addEditCafCustomerController.paymentCountryList!
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
                          addEditCafCustomerController.selPaymentCountry =
                              value as CountryDetail?;
                          addEditCafCustomerController.update();
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        addEditCafCustomerController.permanentAddController,
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
                    value: addEditCafCustomerController.selPermanentPincode,
                    items: addEditCafCustomerController.permanentPincodeList!
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
                      addEditCafCustomerController.selPermanentPincode =
                          value as PincodeDetail?;
                      addEditCafCustomerController.update();
                      addEditCafCustomerController.getPinCodeToAreaData(
                          addEditCafCustomerController
                              .selPermanentPincode!.pincodeid!,
                          Strings.permanent_address_details);

                      /* addEditCafCustomerController.getPinCodeToAreaList(
                          addEditCafCustomerController
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
                    value: addEditCafCustomerController.selPermanentArea,
                    items: addEditCafCustomerController.permanentAreaList!
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
                      addEditCafCustomerController.selPermanentArea =
                          value as PincodeAreaDetail?;
                      addEditCafCustomerController.update();
                      addEditCafCustomerController.getAreaDetail(
                          subAreaId: null,
                          areaId: addEditCafCustomerController
                              .selPermanentArea!.id!,
                          type: Strings.permanent_address_details);
                      /*addEditCafCustomerController
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
                        value: addEditCafCustomerController.selPermanentCity,
                        items: addEditCafCustomerController.permanentCityList!
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
                          addEditCafCustomerController.selPermanentCity =
                              value as CityDetail?;
                          addEditCafCustomerController.update();
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
                        value: addEditCafCustomerController.selPermanentState,
                        items: addEditCafCustomerController.permanentStateList!
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
                          addEditCafCustomerController.selPermanentState =
                              value as StateDetail?;
                          addEditCafCustomerController.update();
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
                        value: addEditCafCustomerController.selPermanentCountry,
                        items: addEditCafCustomerController
                            .permanentCountryList!
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
                          addEditCafCustomerController.selPermanentCountry =
                              value as CountryDetail?;
                          addEditCafCustomerController.update();
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                            textEditingController: addEditCafCustomerController
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
                addEditCafCustomerController.showDiscountPrice
                    ? const SizedBox(
                        width: Constant.SMALL_PADDING,
                      )
                    : Container(),
                addEditCafCustomerController.showDiscountPrice
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
                                      addEditCafCustomerController
                                          .planNewPriceController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fillColor: addEditCafCustomerController
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
                                    if (addEditCafCustomerController
                                                .selPlanCategory !=
                                            null &&
                                        addEditCafCustomerController
                                            .selPlanCategory!.text!
                                            .equalsIgnoreCase(
                                                Strings.plan_group)) {
                                      addEditCafCustomerController
                                          .calculatePlanGroupDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    } else {
                                      addEditCafCustomerController
                                          .calculatePlanDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    }
                                  },
                                  onTextValidator: (String? value) {
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: addEditCafCustomerController
                                      .readOnlyDiscountPrice)
                            ]),
                      )
                    : Container(),
              ],
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.showInvoiceTag)
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
                            ignoring: addEditCafCustomerController
                                .businessPromotionFlag,
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
                                    fillColor: addEditCafCustomerController
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
                                value: addEditCafCustomerController
                                    .selectedInvoiceToOrg,
                                items: addEditCafCustomerController
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
                                  addEditCafCustomerController
                                          .selectedInvoiceToOrg =
                                      value as DropdownDetail?;
                                  addEditCafCustomerController.update();
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.showInvoiceTag)
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
                              value:
                                  addEditCafCustomerController.selPlanCategory,
                              items: addEditCafCustomerController
                                  .planCategoryList!
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
                                addEditCafCustomerController.selPlanCategory =
                                    value as DropdownDetail?;
                                addEditCafCustomerController.selPlanGroup =
                                    null;

                                addEditCafCustomerController.selPlanService =
                                    null;
                                addEditCafCustomerController.selPlan = null;
                                addEditCafCustomerController.individualPlanList!
                                    .clear();
                                addEditCafCustomerController
                                    .planValidityController
                                    .clear();
                                addEditCafCustomerController.discountController
                                    .clear();
                                addEditCafCustomerController
                                    .planGroupMappingList!
                                    .clear();
                                addEditCafCustomerController.offerPrice = 0;
                                addEditCafCustomerController
                                    .discountOfferPrice = 0;
                                addEditCafCustomerController
                                    .planOfferPriceController
                                    .clear();
                                addEditCafCustomerController
                                    .planNewPriceController
                                    .clear();
                                addEditCafCustomerController.discountController
                                    .clear();
                                addEditCafCustomerController.billToReadOnly =
                                    false;
                                addEditCafCustomerController.update();
                              },
                              validator: (value) {
                                // need to add validation
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
                                      // addEditCafCustomerController.billToReadOnly
                                      addEditCafCustomerController
                                              .businessPromotionFlag
                                          ? Colors.black12
                                          : AppTheme.colorWhite),
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
                              value:
                                  addEditCafCustomerController.selectedBillTo,
                              items: addEditCafCustomerController.billToList!
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
                                addEditCafCustomerController.selectedBillTo =
                                    value as BillToDetail?;
                                addEditCafCustomerController.update();
                                addEditCafCustomerController
                                    .manageDiscountVisibility();
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditCafCustomerController
                                            .selectedBillTo ==
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
            //============================== Billable To ============================

            /*const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCafCustomerController.selPlanCategory != null)
                ? InputTitleRequire(title: Strings.billableTo, require: false)
                : const SizedBox.shrink(),

            (addEditCafCustomerController.selPlanCategory != null)
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : const SizedBox.shrink(),

            (addEditCafCustomerController.selPlanCategory != null)
                ? Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Flexible(
                        child: CoustomTextField(
                            labelText: Strings.select_billable_to,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: addEditCafCustomerController
                                .billableToController,
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
                            addEditCafCustomerController.billableToController
                                .clear();
                            addEditCafCustomerController
                                .selectedParentCustomer = null;
                            addEditCafCustomerController.billableToCustomerId =
                                null;
                            addEditCafCustomerController.update();
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
                : const SizedBox.shrink(),*/

            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
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
                              value: addEditCafCustomerController.selPlanGroup,
                              items: addEditCafCustomerController.planGroupList!
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
                                addEditCafCustomerController.selPlanGroup =
                                    value as PlanGroupDetail?;
                                addEditCafCustomerController.update();
                                addEditCafCustomerController
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

            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addEditCafCustomerController.showDiscountPrice)
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
                                  addEditCafCustomerController
                                      .discountController,
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
                                addEditCafCustomerController
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addEditCafCustomerController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
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
                                  value: addEditCafCustomerController
                                      .selectServicesByServiceAreaData,
                                  items: addEditCafCustomerController
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
                                    addEditCafCustomerController
                                            .selectServicesByServiceAreaData =
                                        value as ServicesByServiceAreaDataList?;

                                    addEditCafCustomerController.update();
                                    addEditCafCustomerController
                                        .serviceAreaName = value!.name;
                                    addEditCafCustomerController
                                        .selectedServiceAreaPlanList!
                                        .clear();
                                    // addEditCafCustomerController.getServicePlanModeServiceAreaAPI();
                                    addEditCafCustomerController
                                        .getPlanServicesDetail();
                                    // addEditCafCustomerController.setPlanData();
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
                                  value: addEditCafCustomerController
                                      .serviceAreaPlanPostpaidData,
                                  // value: addEditCafCustomerController.selPlan,
                                  // items: addEditCafCustomerController.planList!
                                  items: addEditCafCustomerController
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
                                    addEditCafCustomerController
                                            .serviceAreaPlanPostpaidData =
                                        value
                                            as ServiceAreaPlanPostpaidplanList?;
                                    if (value!.category!.equalsIgnoreCase(
                                        Constant.BUSINESS_PROMOTION)) {
                                      addEditCafCustomerController
                                          .businessPromotionFlag = true;
                                      addEditCafCustomerController
                                          .showInvoiceTag = true;
                                      for (BillToDetail element
                                          in addEditCafCustomerController
                                              .billToList!) {
                                        if (element.text == "ORGANIZATION") {
                                          addEditCafCustomerController
                                              .selectedBillTo = element;
                                          for (DropdownDetail element
                                              in addEditCafCustomerController
                                                  .invoiceToOrgList!) {
                                            if (element.text == Strings.no) {
                                              addEditCafCustomerController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            } else if (element.text ==
                                                Strings.yes) {
                                              addEditCafCustomerController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            }
                                          }
                                        } else if (element.text == "CUSTOMER") {
                                          addEditCafCustomerController
                                              .selectedBillTo = element;
                                        }
                                      }
                                    } else {
                                      addEditCafCustomerController
                                          .businessPromotionFlag = false;
                                      addEditCafCustomerController
                                          .showInvoiceTag = false;
                                    }

                                    if (addEditCafCustomerController
                                            .serviceAreaPlanPostpaidData !=
                                        null) {
                                      addEditCafCustomerController
                                              .planValidityController.text =
                                          "${addEditCafCustomerController.serviceAreaPlanPostpaidData!.validity}-${addEditCafCustomerController.serviceAreaPlanPostpaidData!.unitsOfValidity!}";
                                      addEditCafCustomerController
                                              .newOfferPricePlanController
                                              .text =
                                          addEditCafCustomerController
                                              .serviceAreaPlanPostpaidData!
                                              .offerprice!
                                              .toString();
                                    }
                                    addEditCafCustomerController.update();
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addEditCafCustomerController.selPlan != null &&
                    addEditCafCustomerController.showDiscountPrice == false)
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title:
                          "Plan Old Price : ${addEditCafCustomerController.selPlan!.offerprice!}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                    ))
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addEditCafCustomerController.selPlan != null)
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
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
                                      addEditCafCustomerController
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
                      (addEditCafCustomerController.showDiscountPrice)
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
                                            addEditCafCustomerController
                                                .discountController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        keyboardType: TextInputType.number,
                                        fontSize: AppTheme.small,
                                        maxLength: 5,
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
                                            addEditCafCustomerController
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),

            // id == 224 (SUBISU)
            (addEditCafCustomerController.selectedBillTo != null &&
                    addEditCafCustomerController.selectedBillTo!.text!
                        .equalsIgnoreCase("ORGANIZATION"))
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
                                DropdownButtonHideUnderline(
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
                                      fillColor: addEditCafCustomerController
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
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addEditCafCustomerController
                                        .selDiscountType,
                                    items: addEditCafCustomerController
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
                                      addEditCafCustomerController
                                              .selDiscountType =
                                          value as DropdownDetail?;
                                      addEditCafCustomerController.update();
                                    },
                                    validator: (value) {
                                      // need to add validation
                                      return null;
                                    },
                                  ),
                                ),
                              ]),
                        ),
                        Expanded(flex: 1, child: Container())
                      ]),

            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.showDiscountPrice)
                ? trialPlanWidget()
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController
                        .planGroupMappingList!.isNotEmpty &&
                    addEditCafCustomerController.selPlanCategory!.text!
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController
                        .planGroupMappingList!.isNotEmpty &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: addEditCafCustomerController
                        .planGroupMappingList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      PlanGroupMappingDetail item = addEditCafCustomerController
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
                                          addEditCafCustomerController
                                              .individualPlanList!
                                              .remove(item);
                                          addEditCafCustomerController.update();
                                          addEditCafCustomerController
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? Align(
                    alignment: Alignment.centerRight,
                    child: InkWell(
                      onTap: () {
                        String discount = addEditCafCustomerController
                            .discountController.text;
                        // if (addEditCafCustomerController.selPlanService == null ||
                        //     addEditCafCustomerController.selPlan == null) {

                        if (addEditCafCustomerController
                                    .selectServicesByServiceAreaData ==
                                null ||
                            addEditCafCustomerController
                                    .serviceAreaPlanPostpaidData ==
                                null ||
                            addEditCafCustomerController.selDiscountType ==
                                null) {
                          Utils.showSnackbar(
                              Strings.INFO,
                              "Please fill-up mandatory data!",
                              AppTheme.colorWhite,
                              AppTheme.colorBlueRView);
                          return;
                        }

                        addEditCafCustomerController.individualPlanList!.add(
                            IndividualPlanData(
                                type: addEditCafCustomerController
                                        .showDiscountPrice
                                    ? 1
                                    : 2,
                                planService: addEditCafCustomerController
                                    .selectServicesByServiceAreaData!,
                                // planDetail: addEditCafCustomerController.selPlan,
                                planDetail: addEditCafCustomerController
                                    .serviceAreaPlanPostpaidData,
                                discount: discount.isEmpty ? "0" : discount,
                                discountType: addEditCafCustomerController
                                    .selDiscountType!.text,
                                newOfferPrice: addEditCafCustomerController
                                    .newOfferPricePlanController.text,
                                // planOfferPrice: addEditCafCustomerController.selPlan!.offerprice!
                                planOfferPrice: addEditCafCustomerController
                                    .serviceAreaPlanPostpaidData!.offerprice!
                                    .toString(),
                                trialPlan:
                                    addEditCafCustomerController.trialPlan));

                        addEditCafCustomerController.selPlanService = null;
                        addEditCafCustomerController.selPlan = null;
                        addEditCafCustomerController.planValidityController
                            .clear();
                        addEditCafCustomerController.discountController.clear();

                        // Service Area
                        addEditCafCustomerController
                            .selectServicesByServiceAreaData = null;
                        addEditCafCustomerController
                            .selectedServiceAreaPlanList!
                            .clear();

                        // Discount Type
                      //  addEditCafCustomerController.selDiscountType = null;

                        addEditCafCustomerController.newOfferPricePlanController
                            .clear();
                        addEditCafCustomerController.trialPlan = false;
                        if (addEditCafCustomerController.individualPlanList !=
                                null &&
                            addEditCafCustomerController
                                .individualPlanList!.isNotEmpty) {
                          addEditCafCustomerController.billToReadOnly = true;
                          addEditCafCustomerController.readOnlyDiscountPrice =
                              false;
                        } else {
                          addEditCafCustomerController.billToReadOnly = false;
                          addEditCafCustomerController.readOnlyDiscountPrice =
                              true;
                        }
                        addEditCafCustomerController.calculatePlanDiscountPrice(
                            Strings.add, "");
                        addEditCafCustomerController.update();
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
            (addEditCafCustomerController.selPlanCategory != null &&
                    addEditCafCustomerController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addEditCafCustomerController.individualPlanList != null &&
                    addEditCafCustomerController.individualPlanList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount:
                        addEditCafCustomerController.individualPlanList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      IndividualPlanData item = addEditCafCustomerController
                          .individualPlanList![index];
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
                                          addEditCafCustomerController
                                              .individualPlanList!
                                              .remove(item);
                                          addEditCafCustomerController.update();
                                          addEditCafCustomerController
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

  additionalServiceForm() {
    return Form(
      key: additionalServiceFormKey,
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                      value: addEditCafCustomerController.selectedPop,
                      items: addEditCafCustomerController.popList!
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
                        addEditCafCustomerController.selectedPop =
                            value as PopDetail?;
                        addEditCafCustomerController.update();
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
                      value: addEditCafCustomerController
                          .selectedOltNetworkDeviceList,
                      items: addEditCafCustomerController
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
                        addEditCafCustomerController
                                .selectedOltNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        if (addEditCafCustomerController
                            .masterDBNetworkDevicesByDeviceList!.isEmpty) {
                          addEditCafCustomerController
                              .getNetworkDevicesByDeviceTypeAPI(
                                  Strings.master_db);
                        }

                        addEditCafCustomerController.update();
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
                      value: addEditCafCustomerController
                          .selectedMasterDBNetworkDeviceList,
                      items: addEditCafCustomerController
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
                        addEditCafCustomerController
                                .selectedMasterDBNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        if (addEditCafCustomerController
                            .splitterDBNetworkDevicesByDeviceList!.isEmpty) {
                          addEditCafCustomerController
                              .getNetworkDevicesByDeviceTypeAPI(
                                  Strings.splitter_db);
                        }
                        addEditCafCustomerController.update();
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
                      value: addEditCafCustomerController
                          .selectedSplitterDBNetworkDeviceList,
                      items: addEditCafCustomerController
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
                        addEditCafCustomerController
                                .selectedSplitterDBNetworkDeviceList =
                            value as NetworkDevicesByDeviceDataList?;
                        addEditCafCustomerController.update();
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
                              addEditCafCustomerController.nasIpController,
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
                              addEditCafCustomerController.nasPort,
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
                    child: titleWithRequireWidget(Strings.vlan_id, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enterVlanId,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCafCustomerController.staticIPController,
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
                    child: titleWithRequireWidget(Strings.framedIp, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enterFramedIp,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCafCustomerController.frameIpController,
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
                    child: titleWithRequireWidget(Strings.framedIpv, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enterFramedIpv,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addEditCafCustomerController.ipPoolNameController,
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
                        Strings.maxConcurrentSession, false),
                  ),
                  const SizedBox(
                    width: Constant.SMALL_PADDING,
                  ),
                  Flexible(
                      flex: 2,
                      child: CoustomTextField(
                          labelText: Strings.enterMaxConcurrentSession,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController: addEditCafCustomerController
                              .maxConcurrentController,
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                              addEditCafCustomerController.nasPort,
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
                              addEditCafCustomerController.framedIP,
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
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                        value: addEditCafCustomerController.selCharge,
                        items: addEditCafCustomerController.chargeList!
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
                          addEditCafCustomerController.selCharge =
                              value as ChargeDetail?;
                          addEditCafCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ]),
            addEditCafCustomerController.selCharge != null
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : Container(),
            addEditCafCustomerController.selCharge != null
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title: Strings.amount +
                          " : " +
                          addEditCafCustomerController.selCharge!.price!
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
                        value: addEditCafCustomerController.selectedChargeType,
                        items: addEditCafCustomerController.chargeTypeLst
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
                          addEditCafCustomerController.selectedChargeType =
                              value as String?;
                          addEditCafCustomerController.update();
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
            addEditCafCustomerController.selectedChargeType != null &&
                    addEditCafCustomerController.selectedChargeType!
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
                              value: addEditCafCustomerController
                                  .selectedRecurringMonth,
                              items: addEditCafCustomerController
                                  .recurringMonthLst
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
                                addEditCafCustomerController
                                    .selectedRecurringMonth = value as int?;
                                addEditCafCustomerController.update();
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
            addEditCafCustomerController.selectedChargeType != null &&
                    addEditCafCustomerController.selectedChargeType!
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
                        value: addEditCafCustomerController.selectedChargePlan,
                        items: addEditCafCustomerController.chargePlanList!
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
                          addEditCafCustomerController.selectedChargePlan =
                              value as IndividualPlanData?;
                          addEditCafCustomerController.update();
                        },
                        validator: (value) {
                          // need to add validation
                          return null;
                        },
                      ),
                    ),
                  ),
                ]),
            addEditCafCustomerController.selectedChargePlan != null
                ? const SizedBox(height: Constant.SMALL_PADDING)
                : Container(),
            addEditCafCustomerController.selectedChargePlan != null
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title:
                          "${Strings.validity} : ${addEditCafCustomerController.selectedChargePlan!.planDetail!.validity} - ${addEditCafCustomerController.selectedChargePlan!.planDetail!.unitsOfValidity!}",
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
                              addEditCafCustomerController.newPriceController,
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
                      addEditCafCustomerController.newPriceController.text;
                  String recMonth =
                      (addEditCafCustomerController.selectedRecurringMonth !=
                              null)
                          ? addEditCafCustomerController.selectedRecurringMonth!
                              .toString()
                          : "";
                  /* String validity =
                      addEditCafCustomerController.validityController.text;
                  String price = addEditCafCustomerController.priceController.text;*/
                  /* String date =
                      addEditCafCustomerController.chargeDateController.text;*/

                  if (addEditCafCustomerController.selCharge == null ||
                      addEditCafCustomerController.selectedChargeType == null ||
                      (addEditCafCustomerController.selectedChargeType !=
                              null &&
                          addEditCafCustomerController.selectedChargeType!
                              .equalsIgnoreCase(Strings.recurring) &&
                          recMonth.isEmpty) ||
                      addEditCafCustomerController.selectedChargePlan == null ||
                      newPrice.isEmpty) {
                    Utils.showSnackbar(Strings.ERROR, "Please fill-up data!",
                        AppTheme.colorWhite, AppTheme.colorRed);
                    return;
                  }
                  double priceNew = double.parse(newPrice);
                  double price = double.parse(addEditCafCustomerController
                      .selCharge!.price!
                      .toString());
                  if (price > priceNew) {
                    Utils.showSnackbar(
                        Strings.ERROR,
                        "New Price must not be less than the actual charge price",
                        AppTheme.colorWhite,
                        AppTheme.colorRed);
                    return;
                  }

                  addEditCafCustomerController.chargeDataList!.add(ChargeData(
                      chargeDetail: addEditCafCustomerController.selCharge!,
                      chargeType:
                          addEditCafCustomerController.selectedChargeType!,
                      recMonth:
                          addEditCafCustomerController.selectedRecurringMonth,
                      chargePlan:
                          addEditCafCustomerController.selectedChargePlan,
                      price: newPrice));

                  addEditCafCustomerController.selCharge = null;
                  addEditCafCustomerController.selectedChargeType = null;
                  addEditCafCustomerController.selectedRecurringMonth = null;
                  addEditCafCustomerController.selectedChargePlan = null;
                  addEditCafCustomerController.newPriceController.clear();
                  //addEditCafCustomerController.validityController.clear();
                  //addEditCafCustomerController.priceController.clear();
                  // addEditCafCustomerController.chargeDateController.clear();
                  //  addEditCafCustomerController.selectedChargeDate = null;
                  addEditCafCustomerController.update();
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
            (addEditCafCustomerController.chargeDataList != null &&
                    addEditCafCustomerController.chargeDataList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount:
                        addEditCafCustomerController.chargeDataList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      ChargeData item =
                          addEditCafCustomerController.chargeDataList![index];
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
                                          addEditCafCustomerController
                                              .chargeDataList!
                                              .remove(item);
                                          addEditCafCustomerController.update();
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
      if (addEditCafCustomerController.selectedChargeDate != null) {
        selectedDate = addEditCafCustomerController.selectedChargeDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.payment_date) {
      if (addEditCafCustomerController.selectedPaymentDate != null) {
        selectedDate = addEditCafCustomerController.selectedPaymentDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.dob_date) {
      if (addEditCafCustomerController.selectedDOBDate != null) {
        selectedDate = addEditCafCustomerController.selectedDOBDate;
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
        addEditCafCustomerController.selectedChargeDate = picked;
        addEditCafCustomerController.chargeDateController.text =
            addEditCafCustomerController.apiDateFormat.format(picked);
      }*/
      if (identity == Strings.payment_date) {
        addEditCafCustomerController.selectedPaymentDate = picked;
        addEditCafCustomerController.paymentDateController.text =
            addEditCafCustomerController.apiDateFormat.format(picked);
      }
      if (identity == Strings.dob_date) {
        addEditCafCustomerController.selectedDOBDate = picked;
        addEditCafCustomerController.dobDateController.text =
            addEditCafCustomerController.apiDateFormat.format(picked);
      }
      addEditCafCustomerController.update();
    }
  }

  macMapppingDetailForm() {
    return Form(
      key: macMapppingFormKey,
      autovalidateMode: addEditCafCustomerController.autoValidateMode,
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
                              addEditCafCustomerController.macAddressController,
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
                      addEditCafCustomerController.macAddressController.text;

                  if (macAddress.isEmpty) {
                    Utils.showSnackbar(Strings.ERROR, "Please fill-up data!",
                        AppTheme.colorWhite, AppTheme.colorRed);
                    return;
                  }
                  addEditCafCustomerController.macAddressList!.add(macAddress);
                  addEditCafCustomerController.macAddressController.clear();
                  addEditCafCustomerController.update();
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
            (addEditCafCustomerController.macAddressList != null &&
                    addEditCafCustomerController.macAddressList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount:
                        addEditCafCustomerController.macAddressList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      String item =
                          addEditCafCustomerController.macAddressList![index];
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
                                          addEditCafCustomerController
                                              .macAddressList!
                                              .remove(item);
                                          addEditCafCustomerController.update();
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
            value: addEditCafCustomerController.trialPlan,
            activeColor: AppTheme.colorPrimary,
            onChanged: (value) {
              addEditCafCustomerController.trialPlan =
                  !addEditCafCustomerController.trialPlan;
              addEditCafCustomerController.update();
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
              addEditCafCustomerController.trialPlan =
                  !addEditCafCustomerController.trialPlan;
              addEditCafCustomerController.update();
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
                ? addEditCafCustomerController.paymentSameAs
                : addEditCafCustomerController.permanentSameAs,
            activeColor: AppTheme.colorPrimary,
            onChanged: (value) {
              if (type.equalsIgnoreCase(Strings.payment_address_details)) {
                addEditCafCustomerController.paymentSameAs =
                    !addEditCafCustomerController.paymentSameAs;
              } else {
                addEditCafCustomerController.permanentSameAs =
                    !addEditCafCustomerController.permanentSameAs;
              }
              addEditCafCustomerController.update();
              addEditCafCustomerController.sameAsPresentAddress(type);
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
                addEditCafCustomerController.paymentSameAs =
                    !addEditCafCustomerController.paymentSameAs;
              } else {
                addEditCafCustomerController.permanentSameAs =
                    !addEditCafCustomerController.permanentSameAs;
              }
              addEditCafCustomerController.update();
              addEditCafCustomerController.sameAsPresentAddress(type);
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
        (addEditCafCustomerController.type != null &&
                addEditCafCustomerController.type!
                    .equalsIgnoreCase(Strings.prepaid))
            ? Strings.create_prepaid_caf_customer
            : Strings.create_postpaid_caf_customer,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (addEditCafCustomerController.activeStep == 0) {
      validateBasicDetail();
    }
    /*else if (addEditCafCustomerController.activeStep == 1) {
      validateKycDetail();
    } else if (addEditCafCustomerController.activeStep == 2) {
      validateContactDetail();
    } else if (addEditCafCustomerController.activeStep == 3) {
      validateLocationDetail();
    } else if (addEditCafCustomerController.activeStep == 1) {
      validateBusinessDetail();
    } else if (addEditCafCustomerController.activeStep == 2) {
      validatePaymentDetail();
    } */
    else if (addEditCafCustomerController.activeStep == 1) {
      validatePresentAddDetail();
    }
    /*else if (addEditCafCustomerController.activeStep == 2) {
      validatePaymentAddDetail();
    } else if (addEditCafCustomerController.activeStep == 3) {
      validatePermanentAddDetail();
    }*/
    else if (addEditCafCustomerController.activeStep == 2) {
      validatePlanDetail();
    }
    // else if (addEditCafCustomerController.activeStep == 3) {
    //   validateAdditionalServiceDetail();
    // }
    /* else if (addEditCafCustomerController.activeStep == 4) {
      validateRadiusServiceDetail();
    } else if (addEditCafCustomerController.activeStep == 5) {
      validateChargeDetail();
    }*/
  }

  validateBasicDetail() {
    if (basicDetailFormKey.currentState!.validate()) {
      if(addEditCafCustomerController.isCredentialMatchWithAccountNo == false) {
        addEditCafCustomerController.checkCustomerExist();
      }else{
        addEditCafCustomerController.getSubAreaCall();
        if (addEditCafCustomerController.activeStep <
            addEditCafCustomerController.dotCount - 1) {
          addEditCafCustomerController.activeStep++;
          addEditCafCustomerController.autoValidateMode =
              AutovalidateMode.disabled;
          addEditCafCustomerController.update();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validateKycDetail() {
    if (kycDetailFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
        if (addEditCafCustomerController.custCategoryList == null ||
            addEditCafCustomerController.custCategoryList!.isEmpty) {
          addEditCafCustomerController.getCustomerCategory();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validateContactDetail() {
    if (contactDetailFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  // validateLocationDetail() {
  //   if (locationDetailFormKey.currentState!.validate()) {
  //     if (addEditCafCustomerController.activeStep <
  //         addEditCafCustomerController.dotCount - 1) {
  //       addEditCafCustomerController.activeStep++;
  //       addEditCafCustomerController.autoValidateMode = AutovalidateMode.disabled;
  //       addEditCafCustomerController.update();
  //
  //       if (addEditCafCustomerController.partnerList == null ||
  //           addEditCafCustomerController.partnerList!.isEmpty) {
  //         addEditCafCustomerController.setBtnClickEvent(false);
  //         addEditCafCustomerController.getActivePartner();
  //       }
  //     }
  //   } else {
  //     addEditCafCustomerController.autoValidateMode =
  //         AutovalidateMode.onUserInteraction;
  //     addEditCafCustomerController.update();
  //   }
  // }

  validateBusinessDetail() {
    if (businessPartnerFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
        if (addEditCafCustomerController.payModeList == null ||
            addEditCafCustomerController.payModeList!.isEmpty) {
          addEditCafCustomerController.getPaymentMode();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validatePaymentDetail() {
    if (paymentFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
        if (addEditCafCustomerController.servicesAreaList == null ||
            addEditCafCustomerController.servicesAreaList!.isEmpty) {
          addEditCafCustomerController.getServiceArea();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validatePresentAddDetail() {
    if (presentAddressFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();

        if ((addEditCafCustomerController.pincodeList != null &&
                addEditCafCustomerController.pincodeList!.isNotEmpty) ||
            (addEditCafCustomerController.newPinCodeAllList != null &&
                addEditCafCustomerController.newPinCodeAllList!.isNotEmpty)) {
          addEditCafCustomerController.paymentSameAs = false;
          addEditCafCustomerController.paymentAddController.clear();
          addEditCafCustomerController.selPaymentPincode = null;
          addEditCafCustomerController.selPaymentArea = null;
          addEditCafCustomerController.selPaymentCity = null;
          addEditCafCustomerController.selPaymentState = null;
          addEditCafCustomerController.selPaymentCountry = null;
          addEditCafCustomerController.paymentPincodeList!.clear();
          addEditCafCustomerController.paymentPincodeList!
              .addAll(addEditCafCustomerController.pincodeList!);
          if (addEditCafCustomerController.billToList == null ||
              addEditCafCustomerController.billToList!.isEmpty) {
            addEditCafCustomerController.getBillToDetail();
          } else {
            if (addEditCafCustomerController.planGroupList == null ||
                addEditCafCustomerController.planGroupList!.isEmpty) {
              addEditCafCustomerController.getPlanGroupDetail();
            }
          }

          addEditCafCustomerController.update();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validatePaymentAddDetail() {
    if (paymentAddressFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();

        if (addEditCafCustomerController.pincodeList != null &&
            addEditCafCustomerController.pincodeList!.isNotEmpty) {
          addEditCafCustomerController.permanentSameAs = false;
          addEditCafCustomerController.permanentAddController.clear();
          addEditCafCustomerController.selPermanentPincode = null;
          addEditCafCustomerController.selPermanentArea = null;
          addEditCafCustomerController.selPermanentCity = null;
          addEditCafCustomerController.selPermanentState = null;
          addEditCafCustomerController.selPermanentCountry = null;
          addEditCafCustomerController.permanentPincodeList!.clear();
          addEditCafCustomerController.permanentPincodeList!
              .addAll(addEditCafCustomerController.pincodeList!);
          addEditCafCustomerController.update();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validatePermanentAddDetail() {
    if (permanentAddressFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
        if (addEditCafCustomerController.billToList == null ||
            addEditCafCustomerController.billToList!.isEmpty) {
          addEditCafCustomerController.getBillToDetail();
        } else {
          if (addEditCafCustomerController.planGroupList == null ||
              addEditCafCustomerController.planGroupList!.isEmpty) {
            addEditCafCustomerController.getPlanGroupDetail();
          }
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validatePlanDetail() {
    if (planDetailFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.selPlanCategory == null) {
        Utils.showSnackbar(
            Strings.ERROR,
            "Minimum one Plan Details need to add",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }

      if (addEditCafCustomerController.selPlanCategory != null &&
          addEditCafCustomerController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.plan_group)) {
        if (addEditCafCustomerController.selPlanGroup == null) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }

      if (addEditCafCustomerController.selPlanCategory != null &&
          addEditCafCustomerController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.individual)) {
        if (addEditCafCustomerController.individualPlanList == null ||
            addEditCafCustomerController.individualPlanList!.isEmpty) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }
      //  addEditCafCustomerController.selectedBillTo=null;

      /*if ((addEditCafCustomerController.selPlanCategory!.text!
                  .equalsIgnoreCase(Strings.individual) &&
              addEditCafCustomerController.selectedBillTo != null &&
              addEditCafCustomerController.selectedBillTo!.id != 224) &&
          addEditCafCustomerController.selectedBillTo == null) {
        Utils.showSnackbar(Strings.ERROR, "Please select bill to detail",
            AppTheme.colorWhite, AppTheme.colorRed);
        return;
      }*/

      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        // if (addEditCafCustomerController.popList == null ||
        //     addEditCafCustomerController.popList!.isEmpty) {
        //   addEditCafCustomerController.getAllPop();
        // } else {
        //   addEditCafCustomerController.getAllPop();
        // }

        Utils.showSnackbar(Strings.INFO, "ApiCall", AppTheme.colorWhite,
            AppTheme.colorBlueRView);
        addEditCafCustomerController.update();
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validateRadiusServiceDetail() {
    if (radiusServiceFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.selCharge = null;
        addEditCafCustomerController.selectedChargeType = null;
        addEditCafCustomerController.selectedRecurringMonth = null;
        addEditCafCustomerController.selectedChargePlan = null;
        addEditCafCustomerController.newPriceController.clear();
        addEditCafCustomerController.update();

        if (addEditCafCustomerController.selPlanCategory != null &&
            addEditCafCustomerController.selPlanCategory!.text!
                .equalsIgnoreCase(Strings.plan_group) &&
            addEditCafCustomerController.selPlanGroup != null) {
          // List<PlanMappingDetail>? planMappingList = addEditCafCustomerController.selPlanGroup!.planMappingList;
          ServiceAreaPlanPostpaidplanList? planMappingList =
              addEditCafCustomerController.serviceAreaPlanPostpaidData;
          List<IndividualPlanData>? planList = [];
          // for (var element in planMappingList!) {
          //   if (element.plan != null) {
          planList.add(IndividualPlanData(
              // planService: PlanServiceDetail(name: element.service),
              planService: ServicesByServiceAreaDataList(
                  name: addEditCafCustomerController
                      .serviceAreaPlanPostpaidData!.name),
              planDetail: planMappingList,
              discount: addEditCafCustomerController.discountController.text));
          // }
          // }
          addEditCafCustomerController.chargePlanList!.clear();
          addEditCafCustomerController.chargePlanList!.addAll(planList);
        } else {
          addEditCafCustomerController.chargePlanList!.clear();
          addEditCafCustomerController.chargePlanList!
              .addAll(addEditCafCustomerController.individualPlanList!);
        }

        if (addEditCafCustomerController.chargeList == null ||
            addEditCafCustomerController.chargeList!.isEmpty) {
          addEditCafCustomerController.getChargeList();
        }
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validateAdditionalServiceDetail() {
    if (additionalServiceFormKey.currentState!.validate()) {
      if (addEditCafCustomerController.activeStep <
          addEditCafCustomerController.dotCount - 1) {
        addEditCafCustomerController.activeStep++;
        addEditCafCustomerController.autoValidateMode =
            AutovalidateMode.disabled;
        addEditCafCustomerController.update();
      }
    } else {
      addEditCafCustomerController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      addEditCafCustomerController.update();
    }
  }

  validateChargeDetail() {
    //if (chargeDetailsFormKey.currentState!.validate()) {
    if (addEditCafCustomerController.activeStep <
        addEditCafCustomerController.dotCount - 1) {
      addEditCafCustomerController.activeStep++;
      addEditCafCustomerController.autoValidateMode = AutovalidateMode.disabled;
      addEditCafCustomerController.update();
    }
    /* } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }*/
  }

  validateMacMapppingDetail() {
    // submit add-edit customer

    addEditCafCustomerController.createCustomerApiCall();
  }

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        addEditCafCustomerController.selectedLocation = data;
        addEditCafCustomerController.update();
        addEditCafCustomerController.getLocationToLatLong();
      }
    }
  }

  openParentCustomerScreen(String? type) async {
    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_TYPE: addEditCafCustomerController.type!
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.parent_customer)) {
          addEditCafCustomerController.selectedParentCustomer = data;
          addEditCafCustomerController.parentCustomerController.text =
              data.name!;
          // addEditCafCustomerController
          //     .getStaffsByServiceAreaAPI(data.networkDetails!.serviceareaid);
          log("serviceareaid>>> ${data.networkDetails!.serviceareaid}");
        } else if (type.equalsIgnoreCase(Strings.billableTo)) {
          addEditCafCustomerController.selectedParentCustomer = data;
          addEditCafCustomerController.billableToController.text = data.name!;
          addEditCafCustomerController.billableToCustomerId = data.id!;
        }
        addEditCafCustomerController.update();
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
      addEditCafCustomerController.setBtnClickEvent(true);
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
        addEditCafCustomerController.setBtnClickEvent(true);
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
        addEditCafCustomerController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    addEditCafCustomerController.isLoading = true;
    addEditCafCustomerController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        addEditCafCustomerController.setBtnClickEvent(false);
        addEditCafCustomerController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        addEditCafCustomerController.latController.text =
            currentPosition.latitude.toString();
        addEditCafCustomerController.longController.text =
            currentPosition.longitude.toString();
        addEditCafCustomerController.update();
      } else {
        addEditCafCustomerController.isLoading = false;
        addEditCafCustomerController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      addEditCafCustomerController.isLoading = false;
      addEditCafCustomerController.update();
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
