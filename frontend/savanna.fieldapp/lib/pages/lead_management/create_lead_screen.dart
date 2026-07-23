import 'dart:io';
import 'package:savbill/pages/customer/individual_plan_item.dart';
import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/individual_plan_data.dart';
import 'package:savbill/pages/customer/model/response/bill_to_res.dart';
import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/customer_category_res.dart';
import 'package:savbill/pages/customer/model/response/customer_department_list.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/new_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/plan_group_mapping_list_item.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/pages/lead_management/create_lead_controller.dart';
import 'package:savbill/pages/lead_management/existing_customer_list.dart';
import 'package:savbill/pages/lead_management/model/lead_source_branch_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_customer_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_partner_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_service_area_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_staff_user_crms_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/permisstion_deny_dialog.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:country_picker/country_picker.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'package:im_stepper/stepper.dart';
import 'package:image_picker/image_picker.dart';

class CreateLeadScreen extends StatefulWidget {
  @override
  _CreateLeadScreenState createState() => _CreateLeadScreenState();
}

class _CreateLeadScreenState extends State<CreateLeadScreen>
    with WidgetsBindingObserver
    implements PermissionDenyBtnAction, LocationBtnAction {
  final addLeadController = Get.put(CreateLeadController());
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();

  final basicLeadFormKey = GlobalKey<FormState>();
  final basicCustomerDetailsFormKey = GlobalKey<FormState>();
  final presentAddressFormKey = GlobalKey<FormState>();
  final planDetailsFormKey = GlobalKey<FormState>();

  // final competitorPackFormKey = GlobalKey<FormState>();
  final basicCAFFormKey = GlobalKey<FormState>();

  // final secondaryContactFormKey = GlobalKey<FormState>();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    addLeadController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (addLeadController.checkBtnClickEvent) {
          addLeadController.setBtnClickEvent(false);
          locationPermissionStatus();
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreateLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: SafeArea(
                child: _body(),
              ),
            ),
          ),
          ProgressBar(isLoader: addLeadController.isLoading),
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
            numbers: addLeadController.data,
            enableStepTapping: false,
            enableNextPreviousButtons: false,
            activeStep: addLeadController.activeStep,
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
              addLeadController.activeStep = index;
              addLeadController.update();
            },
          ),
        ),
        Container(
          margin: const EdgeInsets.only(
            top: 70,
          ),
          alignment: Alignment.topCenter,
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
                    addLeadController.activeStep == 0
                        ? basicDetailForm()
                        : Container(),
                    // addLeadController.activeStep == 1
                    //     ? basicCustomerDetailForm()
                    //     : Container(),
                    addLeadController.activeStep == 1
                        ? Column(
                      children: [
                        basicCustomerDetailForm(),
                        // basicCAFDetailsForm(),
                      ],
                    ) : Container(),
                    addLeadController.activeStep == 2
                        ? presentAddressDetailForm()
                        : Container(),
                    addLeadController.activeStep == 3
                        ? planDetailForm()
                        : Container(),
                    // addLeadController.activeStep == 4
                    //     ? basicCAFDetailsForm()
                    //     : Container(),
                  ]),
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
                      if (addLeadController.activeStep > 0) {
                        addLeadController.activeStep--;
                        addLeadController.update();
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
                      if (addLeadController.activeStep == 3) {
                        if (addLeadController.selPlanCategory == null) {
                          Utils.showSnackbar(
                              Strings.ERROR,
                              "Minimum one Plan Details need to add",
                              AppTheme.colorWhite,
                              AppTheme.colorRed);
                          return;
                        }

                        if (addLeadController.selPlanCategory != null &&
                            addLeadController.selPlanCategory!.text!
                                .equalsIgnoreCase(Strings.plan_group)) {
                          if (addLeadController.selPlanGroup == null) {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                "Minimum one Plan Details need to add",
                                AppTheme.colorWhite,
                                AppTheme.colorRed);
                            return;
                          }
                        }

                        if (addLeadController.selPlanCategory != null &&
                            addLeadController.selPlanCategory!.text!
                                .equalsIgnoreCase(Strings.individual)) {
                          if (addLeadController.individualPlanList == null ||
                              addLeadController.individualPlanList!.isEmpty) {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                "Minimum one Plan Details need to add",
                                AppTheme.colorWhite,
                                AppTheme.colorRed);
                            return;
                          }
                        }
                        if (planDetailsFormKey.currentState!.validate()) {
                          if (addLeadController.activeStep <
                              addLeadController.dotCount - 1) {
                            if (addLeadController.from
                                .equalsIgnoreCase(Strings.add)) {
                              addLeadController.createLead();
                            } else if (addLeadController.from
                                .equalsIgnoreCase(Strings.edit)) {
                              addLeadController.updateLead(
                                  addLeadController.leadViewContentData!.id!,
                                  null,
                                  false);
                            } else if (addLeadController.from
                                .equalsIgnoreCase(Strings.lead_caf)) {
                              addLeadController.leadToCAFConvertCustomer();
                            }
                          }
                        } else {
                          addLeadController.autoValidateMode =
                              AutovalidateMode.onUserInteraction;
                          addLeadController.update();
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
                      title: addLeadController.activeStep == 3
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

  titleWithRequireWidget(String title, bool require) {
    return RichText(
      text: TextSpan(
        style: const TextStyle(
          fontFamily: AppTheme.appFontName,
        ),
        children: [
          TextSpan(
            text: title,
            style: TextStyle(
              color: AppTheme.title_dark,
              fontSize: AppTheme.small,
              fontWeight: FontWeight.normal,
            ),
          ),
          if (require)
            TextSpan(
              text: " *",
              style: TextStyle(
                color: AppTheme.colorRed,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              ),
            ),
        ],
      ),
    );
  }

  _appBar() {
    String? title = Strings.create_lead;
    if (addLeadController.from.equalsIgnoreCase(Strings.add)) {
      title = Strings.create_lead;
    } else if (addLeadController.from.equalsIgnoreCase(Strings.edit)) {
      title = Strings.update_lead;
    } else if (addLeadController.from.equalsIgnoreCase(Strings.lead_caf)) {
      title =
          "${addLeadController.approveRejectStatus} & ${Strings.convert_lead_caf}";
    }
    return DynamicAppBar(title, '', AppTheme.colorPrimary, false, _backScreen,
        [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (addLeadController.activeStep == 0) {
      validateBasicDetail();
    } else if (addLeadController.activeStep == 1) {
      validateCustomerDetail();
      // validateBasicCAFDetail();
    } else if (addLeadController.activeStep == 2) {
      validatePresentAddressDetail();
    } else if (addLeadController.activeStep == 3) {
      validatePlanDetail();
    }
    // else if (addLeadController.activeStep == 4) {
    //   validateCompetitorPackDetail();
    // }
    // else if (addLeadController.activeStep == 4) {
    //   validateBasicCAFDetail();
    // }
    // else if (addLeadController.activeStep == 6) {
    //   validateSecondaryContactDetail();
    // }
  }

  validateBasicDetail() {
    if (basicLeadFormKey.currentState!.validate()) {
      if (addLeadController.activeStep < addLeadController.dotCount - 1) {
        addLeadController.activeStep++;
        addLeadController.autoValidateMode = AutovalidateMode.disabled;
        addLeadController.update();
      }
    } else {
      addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
      addLeadController.update();
    }
  }

  validateCustomerDetail() {
    if (basicCustomerDetailsFormKey.currentState!.validate()) {
      if (addLeadController.activeStep < addLeadController.dotCount - 1) {
        addLeadController.activeStep++;
        addLeadController.autoValidateMode = AutovalidateMode.disabled;
        addLeadController.update();
      }
    } else {
      addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
      addLeadController.update();
    }
  }

  validatePresentAddressDetail() {
    if (presentAddressFormKey.currentState!.validate()) {
      if (addLeadController.activeStep < addLeadController.dotCount - 1) {
        addLeadController.activeStep++;
        addLeadController.autoValidateMode = AutovalidateMode.disabled;
        addLeadController.update();

        if (addLeadController.pincodeList != null &&
            addLeadController.pincodeList!.isNotEmpty) {
          if (addLeadController.billToList == null ||
              addLeadController.billToList!.isEmpty) {
            addLeadController.getBillToDetail();
            addLeadController.update();
          } //else {
          // if (addLeadController.planGroupList == null ||
          //     addLeadController.planGroupList!.isEmpty) {
          //   addLeadController.getPlanGroupDetail();
          // }
          //}
          addLeadController.update();
        }
      }
    } else {
      addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
      addLeadController.update();
    }
  }

  validatePlanDetail() {
    if (planDetailsFormKey.currentState!.validate()) {
      if (addLeadController.selPlanCategory == null) {
        Utils.showSnackbar(
            Strings.ERROR,
            "Minimum one Plan Details need to add",
            AppTheme.colorWhite,
            AppTheme.colorRed);
        return;
      }

      if (addLeadController.selPlanCategory != null &&
          addLeadController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.plan_group)) {
        if (addLeadController.selPlanGroup == null) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }

      if (addLeadController.selPlanCategory != null &&
          addLeadController.selPlanCategory!.text!
              .equalsIgnoreCase(Strings.individual)) {
        if (addLeadController.individualPlanList == null ||
            addLeadController.individualPlanList!.isEmpty) {
          Utils.showSnackbar(
              Strings.ERROR,
              "Minimum one Plan Details need to add",
              AppTheme.colorWhite,
              AppTheme.colorRed);
          return;
        }
      }

      if (addLeadController.activeStep < addLeadController.dotCount - 1) {
        addLeadController.activeStep++;
        addLeadController.autoValidateMode = AutovalidateMode.disabled;
        addLeadController.update();
      }
    } else {
      addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
      addLeadController.update();
    }
  }

  // validateCompetitorPackDetail() {
  //   if (competitorPackFormKey.currentState!.validate()) {
  //     if (addLeadController.activeStep < addLeadController.dotCount - 1) {
  //       addLeadController.activeStep++;
  //       addLeadController.autoValidateMode = AutovalidateMode.disabled;
  //       addLeadController.update();
  //     }
  //   } else {
  //     addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
  //     addLeadController.update();
  //   }
  // }

  validateBasicCAFDetail() {
    if (basicCAFFormKey.currentState!.validate()) {
      if (addLeadController.activeStep < addLeadController.dotCount - 1) {
        addLeadController.activeStep++;
        addLeadController.autoValidateMode = AutovalidateMode.disabled;
        addLeadController.update();
      }
    } else {
      addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
      addLeadController.update();
    }
  }

  // validateSecondaryContactDetail() {
  //   if (secondaryContactFormKey.currentState!.validate()) {
  //     if (addLeadController.activeStep < addLeadController.dotCount - 1) {
  //       addLeadController.activeStep++;
  //       addLeadController.autoValidateMode = AutovalidateMode.disabled;
  //       addLeadController.update();
  //     }
  //   } else {
  //     addLeadController.autoValidateMode = AutovalidateMode.onUserInteraction;
  //     addLeadController.update();
  //   }
  // }

  String getTitle() {
    String strTitle = "";
    switch (addLeadController.activeStep) {
      case 0:
        strTitle = Strings.basic_lead_details;
        break;
      case 1:
        strTitle = Strings.basicCustomerDetails;
        break;
      case 2:
        strTitle = Strings.present_address_details;
        break;
      case 3:
        strTitle = Strings.plan_detail;
        break;
      case 4:
        strTitle = Strings.basic_caf_details;
        break;
      // case 5:
      //   strTitle = Strings.basic_caf_details;
      //   break;
      // case 6:
      //   strTitle = Strings.secondary_contact_details;
      //   break;
    }
    return strTitle;
  }

  basicDetailForm() {
    return Form(
      key: basicLeadFormKey,
      autovalidateMode: autoValidateMode,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SCREEN_PADDING),
          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: InputTitleRequire(title: Strings.lead_no, require: true),
          //     ),
          //     const SizedBox(
          //       width: Constant.SMALL_PADDING,
          //     ),
          //     Flexible(
          //       flex: 2,
          //       child: CoustomTextField(
          //           labelText: Strings.lead_no,
          //           hintColor: AppTheme.colorIconGrey,
          //           textEditingController: addLeadController.leadNoController,
          //           borderEnableColors: AppTheme.colorIconGrey,
          //           borderFocusColors: AppTheme.colorIconGrey,
          //           textColor: AppTheme.colorBlack,
          //           keyboardType: TextInputType.text,
          //           fontSize: AppTheme.small,
          //           textInputAction: TextInputAction.next,
          //           fontWeight: FontWeight.w500,
          //           contentPadding: const EdgeInsets.symmetric(
          //               horizontal: Constant.MEDIUM_PADDING,
          //               vertical: Constant.MEDIUM_PADDING),
          //           borderCorner: Constant.BTN_ROUNDED_CORNER,
          //           onTextValidator: (String? value) {
          //             if (value!.isEmpty) {
          //               return Strings.enter_lead_no_required;
          //             }
          //             return null;
          //           },
          //           onTextFiledOnTap: () {},
          //           readOnly: true),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
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
                    value: addLeadController.selectedCustCategory,
                    items: addLeadController.custCategoryList!
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
                      addLeadController.selectedCustCategory =
                          value as CustomerCategoryDetail?;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedCustCategory == null) {
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
                    value: addLeadController.selectedCustomerLeadType,
                    items: addLeadController.leadCustomerTypeList!
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
                      addLeadController.selectedCustomerLeadType =
                          value as DropdownDetail?;
                      addLeadController.type = value!.text;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedCustomerLeadType == null) {
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
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.customer_sector, true),
              ),
              const SizedBox(
                width: Constant.VERY_SMALL_PADDING,
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
                    value: addLeadController.selectedCustSector,
                    items: addLeadController.custSectorList!
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
                      addLeadController.selectedCustSector =
                          value as CustomerSectorData?;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedCustSector == null) {
                        return Strings.please_select_customer_sector;
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
                child:
                    titleWithRequireWidget(Strings.require_service_type, false),
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
                        child: Text(Strings.select_require_service_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedRequireServiceType,
                    items: addLeadController.requireServiceTypeList!
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
                      addLeadController.selectedRequireServiceType =
                          value as String?;
                      addLeadController.update();
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
                child: titleWithRequireWidget(Strings.lead_type, false),
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
                        child: Text(Strings.select_lead_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedLeadType,
                    items: addLeadController.leadTypeList!.map((String value) {
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
                      addLeadController.selectedLeadType = value as String?;
                      addLeadController.update();
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
                child: titleWithRequireWidget(Strings.lead_category, true),
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
                        child: Text(Strings.select_lead_category,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedLeadCategory,
                    items: addLeadController.leadCategoryList!
                        .map((DropdownDetail value) {
                      return DropdownMenuItem<DropdownDetail>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value.text,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addLeadController.selectedLeadCategory =
                          value as DropdownDetail?;
                      if (addLeadController.selectedLeadCategory!.text!
                          .equalsIgnoreCase(Strings.existing_customer)) {
                        openExistingCustomerScreen();
                      }
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedLeadCategory == null) {
                        return Strings.please_select_lead_category;
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
                child: titleWithRequireWidget(Strings.lead_origin_type, false),
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
                        child: Text(Strings.select_lead_origin_type,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedLeadOriginType,
                    items: addLeadController.leadOriginTypeList!
                        .map((String? value) {
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
                      addLeadController.selectedLeadOriginType =
                          value as String?;
                      addLeadController.update();
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
                child: titleWithRequireWidget(Strings.lead_source, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              addLeadController.from.equalsIgnoreCase(Strings.lead_caf)
                  ? Flexible(
                      flex: 2,
                      child: DropdownButtonHideUnderline(
                        child: DropdownButtonFormField<LeadSourceList>(
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
                              Strings.select_lead_source,
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
                          value: addLeadController.selectedLeadSource,
                          items: addLeadController.leadSourceList!
                              .map((LeadSourceList? value) {
                            return DropdownMenuItem<LeadSourceList>(
                              value: value,
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: CustomText(
                                  title: value!.leadSourceName,
                                  colors: AppTheme.colorBlack,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            );
                          }).toList(),
                          onChanged: null,
                          // Disables the dropdown
                          validator: (value) {
                            if (value == null ||
                                addLeadController.selectedLeadSource == null) {
                              return Strings.please_select_lead_source;
                            }
                            return null;
                          },
                        ),
                      ),
                    )
                  : Flexible(
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
                              child: Text(Strings.select_lead_source,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ))),
                          style: AppTheme.dropdownTextStyle,
                          isExpanded: true,
                          isDense: true,
                          value: addLeadController.selectedLeadSource,
                          items: addLeadController.leadSourceList!
                              .map((LeadSourceList? value) {
                            return DropdownMenuItem<LeadSourceList>(
                              value: value,
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: CustomText(
                                  title: value!.leadSourceName,
                                  colors: AppTheme.colorBlack,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                ), //Text(value.desig!),
                              ),
                            );
                          }).toList(),
                          onChanged: (value) {
                            addLeadController.selectedLeadSource =
                                value as LeadSourceList?;
                            addLeadController.leadSubSourceArr!.clear();
                            addLeadController.selectedLeadSubSourceArr = null;
                            addLeadController.selectLeadSource(
                                addLeadController.selectedLeadSource!.id);
                            addLeadController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                addLeadController.selectedLeadSource == null) {
                              return Strings.please_select_lead_source;
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
                child: titleWithRequireWidget(Strings.feasibility, true),
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
                        child: Text(Strings.select_feasibility_check,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedLeadFeasibility,
                    items: addLeadController.leadFeasibilityList!
                        .map((String? value) {
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
                      addLeadController.selectedLeadFeasibility =
                          value as String?;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedLeadFeasibility == null) {
                        return Strings.please_select_feasibility;
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
                        child: Text(Strings.select_department,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectAllDepartmentData,
                    items: addLeadController.allDepartmentDataList!
                        .map((DepartmentListData? value) {
                      return DropdownMenuItem<DepartmentListData>(
                        value: value,
                        child: Align(
                          alignment: Alignment.centerLeft,
                          child: CustomText(
                            title: value!.name,
                            colors: AppTheme.colorBlack,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                          ), //Text(value.desig!),
                        ),
                      );
                    }).toList(),
                    onChanged: (value) {
                      addLeadController.selectAllDepartmentData =
                          value as DepartmentListData?;
                      addLeadController.update();
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
          addLeadController.from.equalsIgnoreCase(Strings.lead_caf)
              ? addLeadController.selectedLeadSource != null
                  ? Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Flexible(
                          flex: 1,
                          child: titleWithRequireWidget(
                              Strings.lead_sub_source, false),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Flexible(
                          flex: 2,
                          child: CoustomTextField(
                              labelText: Strings.lead_sub_source,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  addLeadController.leadSubSourceController,
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
                              readOnly: true),
                        ),
                      ],
                    )
                  : const SizedBox.shrink()
              : addLeadController.selectedLeadSource != null
                  ? Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Flexible(
                          flex: 1,
                          child: titleWithRequireWidget(
                              Strings.lead_sub_source, false),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Customer") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSourceCustomer,
                                    items: addLeadController
                                        .leadSourceCustomerList!
                                        .map((CustomersList? value) {
                                      return DropdownMenuItem<CustomersList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.firstname,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSourceCustomer =
                                          value as CustomersList?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Branch") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSourceBranch,
                                    items: addLeadController
                                        .leadSourceBranchList!
                                        .map((BranchList? value) {
                                      return DropdownMenuItem<BranchList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.name,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSourceBranch =
                                          value as BranchList?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Partner") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSourcePartner,
                                    items: addLeadController
                                        .leadSourcePartnerList!
                                        .map((PartnerList? value) {
                                      return DropdownMenuItem<PartnerList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.name,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSourcePartner =
                                          value as PartnerList?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Staff") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSourceStaffUser,
                                    items: addLeadController
                                        .leadSourceStaffUserList!
                                        .map((StaffUserList? value) {
                                      return DropdownMenuItem<StaffUserList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.firstname,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSourceStaffUser =
                                          value as StaffUserList?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Outlet/ SA") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSourceServiceArea,
                                    items: addLeadController
                                        .leadSourceServiceAreaList!
                                        .map((ServiceAreaList? value) {
                                      return DropdownMenuItem<ServiceAreaList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.name,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSourceServiceArea =
                                          value as ServiceAreaList?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        addLeadController.leadSourceTitle!
                                    .equalsIgnoreCase("Agent") &&
                                !(addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController.selectAgentArr,
                                    items: addLeadController.agentArr!
                                        .map((String? value) {
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
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController.selectAgentArr =
                                          value as String?;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                        (addLeadController.myViewFlag!)
                            ? Flexible(
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
                                        child: Text(Strings.lead_sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: addLeadController
                                        .selectedLeadSubSourceArr,
                                    items: addLeadController.leadSubSourceArr!
                                        .map((LeadSubSourceDtoList? value) {
                                      return DropdownMenuItem<
                                          LeadSubSourceDtoList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value!.name,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      addLeadController
                                              .selectedLeadSubSourceArr =
                                          value as LeadSubSourceDtoList;
                                      addLeadController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              )
                            : const SizedBox.shrink(),
                      ],
                    )
                  : const SizedBox.shrink(),
          addLeadController.selectedLeadSource != null
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : const SizedBox.shrink(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child:
                    InputTitleRequire(title: Strings.remarks, require: false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                  labelText: Strings.organization_text,
                  hintColor: AppTheme.colorIconGrey,
                  textEditingController: addLeadController.remarksController,
                  borderEnableColors: AppTheme.colorIconGrey,
                  borderFocusColors: AppTheme.colorIconGrey,
                  textColor: AppTheme.colorBlack,
                  keyboardType: TextInputType.multiline,
                  fontSize: AppTheme.small,
                  minLines: 3,
                  maxLength: 250,
                  maxLines: 5,
                  textInputAction: TextInputAction.newline,
                  fontWeight: FontWeight.w500,
                  contentPadding: const EdgeInsets.symmetric(
                      horizontal: Constant.MEDIUM_PADDING,
                      vertical: Constant.MEDIUM_PADDING),
                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                  onTextValidator: (String? value) {
                    return null;
                  },
                  onTextFiledOnTap: () {},
                  readOnly: false,
                ),
              ),
            ],
          ),
          const SizedBox(height: Constant.EXTRA_LARGE_PADDING),
        ],
      ),
    );
  }

  basicCustomerDetailForm() {
    return Form(
      key: basicCustomerDetailsFormKey,
      autovalidateMode: autoValidateMode,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.SCREEN_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child:
                    InputTitleRequire(title: Strings.firstname, require: true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_first_name,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addLeadController.firstNameController,
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
                child:
                    InputTitleRequire(title: Strings.lastname, require: true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_last_name,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.lastNameController,
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
                child: titleWithRequireWidget(Strings.mobile_number, true),
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
                    textEditingController: addLeadController.mobileController,
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
                            title: addLeadController.countryCode,
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
                child: titleWithRequireWidget(Strings.primary_email, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.email,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.emailController,
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
                        addLeadController.parentCustomerController,
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
                child: titleWithRequireWidget(Strings.service_area, true),
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
                        Strings.service_area,
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
                    value: addLeadController.selectNewServiceArea,
                    items: addLeadController.newServicesAreaList!
                        .map((NewServiceDataList value) {
                      return DropdownMenuItem<NewServiceDataList>(
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
                      List<int>? serviceAreaId = [];
                      addLeadController.selectNewServiceArea =
                          value as NewServiceDataList?;
                      addLeadController.selPresentPincode = null;
                      // addLeadController.pincodeList!.clear();
                      addLeadController.serviceAreaId = value!.id;
                      addLeadController.update();
                      serviceAreaId.add(value.id!);
                      addLeadController
                          .getAllServicesByServiceAreaIdData(serviceAreaId);
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectNewServiceArea == null) {
                        return Strings.select_service_area;
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
                    value: addLeadController.selectBranchesByServiceAreaData,
                    items: addLeadController.branchesByServiceAreaList!.isEmpty
                        ? [
                            DropdownMenuItem<BranchesByServiceAreaDataList>(
                              value: null,
                              enabled: false,
                              child: CustomText(
                                title: Strings.no_data_found,
                                colors: AppTheme.title_dark,
                                fontSize: AppTheme.small,
                              ), // Disable selection
                            ),
                          ]
                        : addLeadController.branchesByServiceAreaList!
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
                      addLeadController.selectBranchesByServiceAreaData =
                          value as BranchesByServiceAreaDataList?;
                      // addLeadController.getServiceAreaDetail(
                      //     addLeadController.selPresentServiceArea!.id);

                      addLeadController.getServiceAreaDetail(
                          addLeadController.serviceAreaId);
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectBranchesByServiceAreaData ==
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
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(
                    "${Strings.customer}\n${Strings.gender}", true),
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
                        child: Text(Strings.gender,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ))),
                    style: AppTheme.dropdownTextStyle,
                    isExpanded: true,
                    isDense: true,
                    value: addLeadController.selectedLeadCustomerGender,
                    items: addLeadController.leadCustomerGenderList!
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
                      addLeadController.selectedLeadType = value as String?;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null) {
                        return Strings.please_select_gender;
                      }
                      return null;
                    },
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addLeadController.areaDetail!.serviceAreaType != null &&
                  addLeadController.areaDetail!.serviceAreaType!
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
                          value: addLeadController.selectedBlockNo,
                          items: addLeadController.blockNoOptions.isEmpty
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
                              : addLeadController.blockNoOptions
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
                            addLeadController.selectedBlockNo =
                                int.parse(value.toString());
                            addLeadController.update();
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
          addLeadController.areaDetail!.serviceAreaType != null &&
                  addLeadController.areaDetail!.serviceAreaType!
                      .equalsIgnoreCase("private")
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
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
                    textEditingController: addLeadController.panController,
                    maxLength: 11,
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
                    labelText: Strings.enter_vat_number,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.vatController,
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
          //               child: Text(Strings.select_title,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addLeadController.selectCustomerTitleCAF,
          //           items:
          //               Utils.getTitle().map<DropdownMenuItem<String>>((value) {
          //             return DropdownMenuItem<String>(
          //               value: value,
          //               child: Align(
          //                 alignment: Alignment.centerLeft,
          //                 child: CustomText(
          //                   title: value,
          //                   colors: AppTheme.colorBlack,
          //                   textAlign: TextAlign.start,
          //                   fontSize: AppTheme.small,
          //                   fontWeight: FontWeight.w500,
          //                 ), //Text(value.desig!),
          //               ),
          //             );
          //           }).toList(),
          //           onChanged: (value) {
          //             addLeadController.selectCustomerTitleCAF =
          //                 value as String?;
          //             addLeadController.update();
          //           },
          //           validator: (value) {
          //             if (value == null) {
          //               return Strings.please_select_title;
          //             }
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
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
                    labelText: Strings.enter_contact_person,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                    addLeadController.contactPersonPayController,
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
                        return Strings.enter_contact_person;
                      }
                      return null;
                    },
                    onTextFiledOnTap: () {},
                    readOnly: false),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addLeadController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              SizedBox(
                width: Constant.SMALL_PADDING,
                child: Checkbox(
                  value: addLeadController.isCredentialMatchWithAccountNo,
                  activeColor: AppTheme.colorPrimary,
                  onChanged: (value) {
                    addLeadController.isCredentialMatchWithAccountNo =
                    !addLeadController.isCredentialMatchWithAccountNo;
                    addLeadController.aaaUserNameController.clear();
                    addLeadController.aaaPasswordController.clear();
                    addLeadController.update();
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
          addLeadController.isCredentialMatchWithAccountNo == false
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
                    labelText: Strings.enter_aaa_username,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                    addLeadController.aaaUserNameController,
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
                    readOnly: false),
              ),
            ],
          )
              : SizedBox.shrink(),
          addLeadController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          addLeadController.isCredentialMatchWithAccountNo == false
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
                    labelText: Strings.enter_aaa_password,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                    addLeadController.aaaPasswordController,
                    keyboardType: TextInputType.text,
                    // borderEnableColors: AppTheme.colorPrimary,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    fontSize: AppTheme.small,
                    textInputAction: TextInputAction.done,
                    fontWeight: FontWeight.w500,
                    onTextValidator: (String? value) {
                      if (value!.isEmpty) {
                        return Strings.please_enter_aaa_password;
                      }
                      return null;
                    },
                    suffixIcon: IconButton(
                      onPressed: () {
                        addLeadController.isVisibleAAAPassword =
                        !addLeadController.isVisibleAAAPassword!;
                        addLeadController.update();
                      },
                      icon: Icon(
                        addLeadController.isVisibleAAAPassword!
                            ? Icons.visibility
                            : Icons.visibility_off,
                        color: AppTheme.colorGrey,
                      ),
                    ),
                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: Constant.LARGE_PADDING - 2,
                        horizontal: Constant.LARGE_PADDING),
                    readOnly: false,
                    obscureText: addLeadController.isVisibleAAAPassword!),
              ),
            ],
          )
              : SizedBox.shrink(),
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
                    labelText: Strings.enter_username,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.userNameController,
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
                child: titleWithRequireWidget(Strings.password, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_password,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.passwordController,
                    keyboardType: TextInputType.text,
                    // borderEnableColors: AppTheme.colorPrimary,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    fontSize: AppTheme.small,
                    textInputAction: TextInputAction.done,
                    fontWeight: FontWeight.w500,
                    onTextValidator: (String? value) {
                      if (value!.isEmpty) {
                        return Strings.please_enter_password;
                      }
                      return null;
                    },
                    suffixIcon: IconButton(
                      onPressed: () {
                        addLeadController.isVisiblePassword =
                        !addLeadController.isVisiblePassword!;
                        addLeadController.update();
                      },
                      icon: Icon(
                        addLeadController.isVisiblePassword!
                            ? Icons.visibility
                            : Icons.visibility_off,
                        color: AppTheme.colorGrey,
                      ),
                    ),
                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: Constant.LARGE_PADDING - 2,
                        horizontal: Constant.LARGE_PADDING),
                    readOnly: false,
                    obscureText: addLeadController.isVisiblePassword!),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          (addLeadController.selectedCustomerLeadType != null &&
              addLeadController.selectedCustomerLeadType!.text!
                  .equalsIgnoreCase(Strings.postpaid) &&
              addLeadController.from.equalsIgnoreCase(Strings.lead_caf))
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
                    value: addLeadController.selectedBillDay,
                    items:
                    addLeadController.billDayList!.map((int value) {
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
                      addLeadController.selectedBillDay = value as int?;
                      addLeadController.update();
                    },
                    validator: (value) {
                      if (value == null ||
                          addLeadController.selectedBillDay == null) {
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
          (addLeadController.selectedCustomerLeadType != null &&
              addLeadController.selectedCustomerLeadType!.text!
                  .equalsIgnoreCase(Strings.postpaid))
              ? const SizedBox(
            height: Constant.MEDIUM_PADDING,
          )
              : Container(),
        ],
      ),
    );
  }

  presentAddressDetailForm() {
    return Form(
      key: presentAddressFormKey,
      autovalidateMode: autoValidateMode,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          const SizedBox(height: Constant.MEDIUM_PADDING),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                flex: 1,
                child: titleWithRequireWidget(Strings.landmark, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_landmark,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.landmarkController,
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
                        return Strings.please_enter_landmark;
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
              //       value: addLeadController.selPresentPincode,
              //       items: addLeadController.pincodeList!
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
              //         addLeadController.selPresentPincode =
              //             value as PincodeDetail?;
              //         addLeadController.update();
              //         addLeadController.areaList!.clear();
              //         addLeadController.cityList!.clear();
              //         addLeadController.stateList!.clear();
              //         addLeadController.countryList!.clear();
              //         addLeadController.selPresentArea = null;
              //         addLeadController.selPresentCity = null;
              //         addLeadController.selPresentState = null;
              //         addLeadController.selPresentCountry = null;
              //         addLeadController.getPinCodeToAreaData(
              //             addLeadController.selPresentPincode!.pincodeid!,
              //             "Present");
              //       },
              //       validator: (value) {
              //         if (value == null ||
              //             addLeadController.selPresentPincode == null) {
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
                  key: addLeadController.pinCodeDropDownKey,
                  mode: Mode.form,
                  selectedItem: addLeadController.selPresentPincode,
                  items: (filter, infiniteScrollProps) =>
                      addLeadController.pincodeList!,
                  compareFn: (item1, item2) => item1.id == item2.id,
                  itemAsString: (item) => item.pincode!,
                  decoratorProps: DropDownDecoratorProps(
                    baseStyle: TextStyle(
                        color: AppTheme.title_dark, fontSize: AppTheme.small),
                    // Change text color
                    decoration: InputDecoration(
                      hintText: Strings.select_pincode,
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
                    addLeadController.selPresentPincode = value;
                    addLeadController.update();
                    addLeadController.areaList!.clear();
                    addLeadController.cityList!.clear();
                    addLeadController.stateList!.clear();
                    addLeadController.countryList!.clear();
                    addLeadController.selPresentArea = null;
                    addLeadController.selPresentCity = null;
                    addLeadController.selPresentState = null;
                    addLeadController.selPresentCountry = null;
                    addLeadController.getPinCodeToAreaData(
                        addLeadController.selPresentPincode!.pincodeid!,
                        "Present");
                  },
                  validator: (value) {
                    if (value == null ||
                        addLeadController.selPresentPincode == null) {
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
              //         value: addLeadController.selPresentArea,
              //         items: addLeadController.areaList!
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
              //           addLeadController.selPresentArea =
              //               value as PincodeAreaDetail?;
              //           addLeadController.update();
              //           addLeadController.getAreaDetail(
              //               addLeadController.selPresentArea!.id!, "Present");
              //         },
              //         validator: (value) {
              //           if (value == null ||
              //               addLeadController.selPresentArea == null) {
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
                  key: addLeadController.areaDropDownKey,
                  mode: Mode.form,
                  selectedItem: addLeadController.selPresentArea,
                  items: (filter, infiniteScrollProps) =>
                      addLeadController.areaList!,
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
                    addLeadController.selPresentArea = value;
                    addLeadController.update();
                    addLeadController.getAreaDetail(
                        addLeadController.selPresentArea!.id!, "Present");
                    addLeadController.getNewSubAreaFromAreaCall(value!.id);
                  },
                  validator: (value) {
                    if (value == null ||
                        addLeadController.selPresentArea == null) {
                      return Strings.select_area;
                    }
                    return null;
                  },
                ),
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
                  key: addLeadController.subAreaDropDownKey,
                  mode: Mode.form,
                  selectedItem: addLeadController.selectedSubAreaData,
                  items: (filter, infiniteScrollProps) =>
                      addLeadController.subAreaDataList!,
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
                    addLeadController.selectedSubAreaData = value;
                    addLeadController.getBuildingMgmtCall(value!.id);
                    addLeadController.getAreaDetail(
                        addLeadController.selPresentArea!.id!, "Present");
                    // addLeadController.selectedBuildingManagementData = null;
                    addLeadController.update();
                    // addLeadController.buildingManagementDataList!.clear();
                    // addLeadController.selectedBuildingManagementData = null;
                    // addLeadController.buildingNumberList!.clear();
                    // addLeadController.selectedBuildingNumber = null;
                    // addLeadController.idData = value!.id;
                    // addLeadController.selectedBuildingNumber = null;
                    // addLeadController.idData = value!.id;
                    // addLeadController.selectedMappingFrom = value.name;
                    // addLeadController.custServiceAreaId = value!.id;
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
                  key: addLeadController.buildingNameDropDownKey,
                  mode: Mode.form,
                  selectedItem:
                      addLeadController.selectedBuildingManagementData,
                  items: (filter, infiniteScrollProps) =>
                      addLeadController.buildingManagementDataList!,
                  compareFn: (item1, item2) =>
                      item1.buildingMgmtId == item2.buildingMgmtId,
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
                    addLeadController.selectedBuildingManagementData = value;
                    // addLeadController.getBuildingMgmtNumbersCall(value!.buildingMgmtId);
                    addLeadController
                        .getBuildingMgmtNumbersCall(value!.buildingMgmtId);
                    // addLeadController.selectedBuildingNumber = null;
                    addLeadController.update();
                    // addLeadController.buildingManagementDataList!.clear();
                    // addLeadController.selectedBuildingManagementData = null;
                    // addLeadController.buildingNumberList!.clear();
                    // addLeadController.selectedBuildingNumber = null;
                    // addLeadController.idData = value!.id;
                    // addLeadController.idData = value!.buildingMgmtId;
                    // addLeadController.selectedMappingFrom = value.name;
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
                  key: addLeadController.buildingNumberDropDownKey,
                  mode: Mode.form,
                  selectedItem: addLeadController.selectedBuildingNumber,
                  items: (filter, infiniteScrollProps) =>
                      addLeadController.buildingNumberList!,
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
                    addLeadController.selectedBuildingNumber = value;
                    addLeadController.update();
                    // addLeadController.buildingManagementDataList!.clear();
                    // addLeadController.selectedBuildingManagementData = null;
                    // addLeadController.buildingNumberList!.clear();
                    // addLeadController.selectedBuildingNumber = null;
                    // addLeadController.idData = value!.id;
                    // addLeadController.selectedBuildingNumber = null;
                    // addLeadController.idData = value!.id;
                    // addLeadController.selectedMappingFrom = value.name;
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
                        value: addLeadController.selPresentCity,
                        items:
                            addLeadController.cityList!.map((CityDetail value) {
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
                          addLeadController.selPresentCity =
                              value as CityDetail?;
                          addLeadController.update();
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
                        value: addLeadController.selPresentState,
                        items: addLeadController.stateList!
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
                          addLeadController.selPresentState =
                              value as StateDetail?;
                          addLeadController.update();
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
                        value: addLeadController.selPresentCountry,
                        items: addLeadController.countryList!
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
                          addLeadController.selPresentCountry =
                              value as CountryDetail?;
                          addLeadController.update();
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
                child: titleWithRequireWidget(Strings.street_name, false),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_street_name,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addLeadController.streetNameController,
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
                        addLeadController.houseNumberController,
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

          // Row(
          //   crossAxisAlignment: CrossAxisAlignment.center,
          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //   children: [
          //     Flexible(
          //       flex: 1,
          //       child: titleWithRequireWidget(Strings.valley_type, false),
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
          //               child: Text(Strings.valley_type,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addLeadController.selectedValleyType,
          //           items: addLeadController.valleyTypeList!
          //               .map((ValleyType value) {
          //             return DropdownMenuItem<ValleyType>(
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
          //             addLeadController.selectedValleyType =
          //                 value as ValleyType?;
          //             addLeadController.update();
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
          // (addLeadController.selectedValleyType != null &&
          //         addLeadController.selectedValleyType!.id ==
          //             Constant.INSIDE_VALLEY)
          //     ? Row(
          //         crossAxisAlignment: CrossAxisAlignment.center,
          //         mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //         children: [
          //           Flexible(
          //             flex: 1,
          //             child:
          //                 titleWithRequireWidget(Strings.inside_valley, false),
          //           ),
          //           const SizedBox(
          //             width: Constant.SMALL_PADDING,
          //           ),
          //           Flexible(
          //             flex: 2,
          //             child: DropdownButtonHideUnderline(
          //               child: DropdownButtonFormField(
          //                 icon: SvgPicture.asset(
          //                   downArrowSvg,
          //                   height: Constant.DROP_DOWN_ARROW_W_H,
          //                   width: Constant.DROP_DOWN_ARROW_W_H,
          //                   color: AppTheme.colorBlack,
          //                   fit: BoxFit.fill,
          //                 ),
          //                 decoration: Utils.ddlDecoration(),
          //                 hint: Align(
          //                     alignment: Alignment.centerLeft,
          //                     child: Text(Strings.inside_valley,
          //                         style: TextStyle(
          //                           fontSize: AppTheme.medium,
          //                           color: AppTheme.colorIconGrey,
          //                           fontFamily: AppTheme.appFontName,
          //                         ))),
          //                 style: AppTheme.dropdownTextStyle,
          //                 isExpanded: true,
          //                 isDense: true,
          //                 value: addLeadController.selectedInsideValley,
          //                 items: addLeadController.insideValleyList!
          //                     .map((InsideOutsideValleyData value) {
          //                   return DropdownMenuItem<InsideOutsideValleyData>(
          //                     value: value,
          //                     child: Align(
          //                       alignment: Alignment.centerLeft,
          //                       child: CustomText(
          //                         title: value.text!,
          //                         colors: AppTheme.colorBlack,
          //                         textAlign: TextAlign.start,
          //                         fontSize: AppTheme.small,
          //                         fontWeight: FontWeight.w500,
          //                       ), //Text(value.desig!),
          //                     ),
          //                   );
          //                 }).toList(),
          //                 onChanged: (value) {
          //                   addLeadController.selectedInsideValley =
          //                       value as InsideOutsideValleyData?;
          //                   addLeadController.update();
          //                 },
          //                 validator: (value) {
          //                   return null;
          //                 },
          //               ),
          //             ),
          //           ),
          //         ],
          //       )
          //     : Container(),
          //
          // (addLeadController.selectedValleyType != null &&
          //         addLeadController.selectedValleyType!.id ==
          //             Constant.INSIDE_VALLEY)
          //     ? const SizedBox(height: Constant.MEDIUM_PADDING)
          //     : Container(),
          // (addLeadController.selectedValleyType != null &&
          //         addLeadController.selectedValleyType!.id ==
          //             Constant.OUTSIDE_VALLEY)
          //     ? Row(
          //         crossAxisAlignment: CrossAxisAlignment.center,
          //         mainAxisAlignment: MainAxisAlignment.spaceBetween,
          //         children: [
          //           Flexible(
          //             flex: 1,
          //             child:
          //                 titleWithRequireWidget(Strings.outside_valley, false),
          //           ),
          //           const SizedBox(
          //             width: Constant.SMALL_PADDING,
          //           ),
          //           Flexible(
          //             flex: 2,
          //             child: DropdownButtonHideUnderline(
          //               child: DropdownButtonFormField(
          //                 icon: SvgPicture.asset(
          //                   downArrowSvg,
          //                   height: Constant.DROP_DOWN_ARROW_W_H,
          //                   width: Constant.DROP_DOWN_ARROW_W_H,
          //                   color: AppTheme.colorBlack,
          //                   fit: BoxFit.fill,
          //                 ),
          //                 decoration: Utils.ddlDecoration(),
          //                 hint: Align(
          //                     alignment: Alignment.centerLeft,
          //                     child: Text(Strings.outside_valley,
          //                         style: TextStyle(
          //                           fontSize: AppTheme.medium,
          //                           color: AppTheme.colorIconGrey,
          //                           fontFamily: AppTheme.appFontName,
          //                         ))),
          //                 style: AppTheme.dropdownTextStyle,
          //                 isExpanded: true,
          //                 isDense: true,
          //                 value: addLeadController.selectedOutsideValley,
          //                 items: addLeadController.outsideValleyList!
          //                     .map((InsideOutsideValleyData value) {
          //                   return DropdownMenuItem<InsideOutsideValleyData>(
          //                     value: value,
          //                     child: Align(
          //                       alignment: Alignment.centerLeft,
          //                       child: CustomText(
          //                         title: value.text!,
          //                         colors: AppTheme.colorBlack,
          //                         textAlign: TextAlign.start,
          //                         fontSize: AppTheme.small,
          //                         fontWeight: FontWeight.w500,
          //                       ), //Text(value.desig!),
          //                     ),
          //                   );
          //                 }).toList(),
          //                 onChanged: (value) {
          //                   addLeadController.selectedOutsideValley =
          //                       value as InsideOutsideValleyData?;
          //                   addLeadController.update();
          //                 },
          //                 validator: (value) {
          //                   return null;
          //                 },
          //               ),
          //             ),
          //           ),
          //         ],
          //       )
          //     : Container(),
          // (addLeadController.selectedValleyType != null &&
          //         addLeadController.selectedValleyType!.id ==
          //             Constant.OUTSIDE_VALLEY)
          //     ? const SizedBox(height: Constant.MEDIUM_PADDING)
          //     : Container(),

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
                    textEditingController: addLeadController.latController,
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
                    textEditingController: addLeadController.longController,
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

  planDetailForm() {
    return Form(
      key: planDetailsFormKey,
      autovalidateMode: addLeadController.autoValidateMode,
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
                            textEditingController:
                                addLeadController.planOfferPriceController,
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
                addLeadController.showDiscountPrice
                    ? const SizedBox(
                        width: Constant.SMALL_PADDING,
                      )
                    : Container(),
                addLeadController.showDiscountPrice
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
                                      addLeadController.planNewPriceController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.number,
                                  fillColor:
                                      addLeadController.readOnlyDiscountPrice
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
                                    if (addLeadController.selPlanCategory !=
                                            null &&
                                        addLeadController.selPlanCategory!.text!
                                            .equalsIgnoreCase(
                                                Strings.plan_group)) {
                                      addLeadController
                                          .calculatePlanGroupDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    } else {
                                      addLeadController
                                          .calculatePlanDiscountPrice(
                                              Strings.new_price_with_discount,
                                              value);
                                    }
                                  },
                                  onTextValidator: (String? value) {
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly:
                                      addLeadController.readOnlyDiscountPrice)
                            ]),
                      )
                    : Container(),
              ],
            ),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.showInvoiceTag)
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
                            ignoring: addLeadController.businessPromotionFlag,
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
                                    fillColor:
                                        addLeadController.businessPromotionFlag
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
                                value: addLeadController.selectedInvoiceToOrg,
                                items: addLeadController.invoiceToOrgList!
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
                                  addLeadController.selectedInvoiceToOrg =
                                      value as DropdownDetail?;
                                  addLeadController.update();
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
            (addLeadController.selPlanCategory != null &&
                    addLeadController.showInvoiceTag)
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
                              value: addLeadController.selPlanCategory,
                              items: addLeadController.planCategoryList!
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
                                addLeadController.selPlanCategory =
                                    value as DropdownDetail?;
                                addLeadController.selPlanGroup = null;

                                addLeadController.selPlanService = null;
                                addLeadController.selPlan = null;
                                addLeadController.individualPlanList!.clear();
                                addLeadController.planValidityController
                                    .clear();
                                addLeadController.discountController.clear();
                                addLeadController.planGroupMappingList!.clear();
                                addLeadController.offerPrice = 0;
                                addLeadController.discountOfferPrice = 0;
                                addLeadController.planOfferPriceController
                                    .clear();
                                addLeadController.planNewPriceController
                                    .clear();
                                addLeadController.discountController.clear();
                                addLeadController.billToReadOnly = false;
                                addLeadController.update();
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
                          IgnorePointer(
                            // ignoring:addLeadController.billToReadOnly,
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
                                decoration: Utils.ddlDecoration(
                                  fillColor:
                                      AppTheme.colorLightGrey.withOpacity(1),
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
                                value: addLeadController.selectedBillTo,
                                items: addLeadController.billToList!
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
                                  addLeadController.selectedBillTo =
                                      value as BillToDetail?;
                                  addLeadController.update();
                                  addLeadController.manageDiscountVisibility();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addLeadController.selectedBillTo ==
                                          null) {
                                    return Strings.select_bill_to;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                        ]),
                  ),
                ]),

            // const SizedBox(height: Constant.MEDIUM_PADDING),
            // (addLeadController.selPlanCategory != null)
            //     ? InputTitleRequire(title: Strings.billableTo, require: false)
            //     : const SizedBox.shrink(),
            //
            // (addLeadController.selPlanCategory != null)
            //     ? const SizedBox(height: Constant.SMALL_PADDING)
            //     : const SizedBox.shrink(),
            //
            // (addLeadController.selPlanCategory != null)
            //     ? CoustomTextField(
            //         labelText: Strings.select_billable_to,
            //         hintColor: AppTheme.colorIconGrey,
            //         textEditingController:
            //             addLeadController.billableToController,
            //         suffixIcon: Padding(
            //           padding: const EdgeInsetsDirectional.all(
            //               Constant.LARGE_PADDING - 2),
            //           child: SvgPicture.asset(
            //             downArrowSvg,
            //             color: AppTheme.colorBlack,
            //             width: Constant.ICON_SIZE_S,
            //             height: Constant.ICON_SIZE_S,
            //           ),
            //         ),
            //         borderEnableColors: AppTheme.colorIconGrey,
            //         borderFocusColors: AppTheme.colorIconGrey,
            //         textColor: AppTheme.colorBlack,
            //         keyboardType: TextInputType.text,
            //         fontSize: AppTheme.small,
            //         textInputAction: TextInputAction.done,
            //         fontWeight: FontWeight.w500,
            //         contentPadding: const EdgeInsets.symmetric(
            //             horizontal: Constant.MEDIUM_PADDING,
            //             vertical: Constant.MEDIUM_PADDING),
            //         borderCorner: Constant.BTN_ROUNDED_CORNER,
            //         onTextValidator: (String? value) {
            //           // if(controller.billableToController.text.isEmpty){
            //           //   return Strings.select_bill_to;
            //           // }
            //           return null;
            //         },
            //         onTextFiledOnTap: () {
            //           openParentCustomerScreen(Strings.billableTo);
            //         },
            //         readOnly: true)
            //     : const SizedBox.shrink(),

            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
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
                              value: addLeadController.selPlanGroup,
                              items: addLeadController.planGroupList!
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
                                addLeadController.selPlanGroup =
                                    value as PlanGroupDetail?;
                                addLeadController.update();
                                addLeadController.manageThePlanGroupSelection();
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

            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addLeadController.showDiscountPrice)
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
                                  addLeadController.discountController,
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
                              onChanged: (value) {
                                // add calculation logic]
                                addLeadController
                                    .calculatePlanGroupDiscountPrice(
                                        Strings.discount, value);
                              },
                              onTextValidator: (String? value) {
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              readOnly: false),
                        ),
                      ])
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group) &&
                    addLeadController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
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
                                  value: addLeadController
                                      .selectServicesByServiceAreaData,
                                  items: addLeadController
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
                                    addLeadController
                                            .selectServicesByServiceAreaData =
                                        value as ServicesByServiceAreaDataList?;

                                    addLeadController.update();
                                    addLeadController.serviceAreaName =
                                        value!.name;
                                    addLeadController
                                        .selectedServiceAreaPlanList!
                                        .clear();

                                    addLeadController
                                        .getServicePlanModeServiceAreaAPI();
                                    // addLeadController.getPlanServicesDetail();
                                    // addLeadController.setPlanData();
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
                                  value: addLeadController
                                      .serviceAreaPlanPostpaidData,
                                  items: addLeadController
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
                                  onChanged: (ServiceAreaPlanPostpaidplanList?
                                      value) async {
                                    addLeadController
                                        .serviceAreaPlanPostpaidData = value;
                                    if (value!.category!.equalsIgnoreCase(
                                        Constant.BUSINESS_PROMOTION)) {
                                      addLeadController.businessPromotionFlag =
                                          true;
                                      addLeadController.showInvoiceTag = true;
                                      for (BillToDetail element
                                          in addLeadController.billToList!) {
                                        if (element.text!
                                            .equalsIgnoreCase("ORGANIZATION")) {
                                          addLeadController.selectedBillTo =
                                              element;
                                          for (DropdownDetail element
                                              in addLeadController
                                                  .invoiceToOrgList!) {
                                            if (element.text == Strings.no) {
                                              addLeadController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            } else if (element.text ==
                                                Strings.yes) {
                                              addLeadController
                                                      .selectedInvoiceToOrg =
                                                  element;
                                              break;
                                            }
                                          }
                                        } else if (element.text!
                                            .equalsIgnoreCase("CUSTOMER")) {
                                          addLeadController.selectedBillTo =
                                              element;
                                        }
                                      }
                                    } else {
                                      addLeadController.businessPromotionFlag =
                                          false;
                                      addLeadController.showInvoiceTag = false;
                                    }
                                    if (addLeadController
                                            .serviceAreaPlanPostpaidData !=
                                        null) {
                                      addLeadController
                                              .planValidityController.text =
                                          "${addLeadController.serviceAreaPlanPostpaidData!.validity}-${addLeadController.serviceAreaPlanPostpaidData!.unitsOfValidity!}";
                                      addLeadController
                                              .newOfferPricePlanController
                                              .text =
                                          addLeadController
                                              .serviceAreaPlanPostpaidData!
                                              .offerprice!
                                              .toString();
                                    }
                                    addLeadController.update();
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
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addLeadController.selPlan != null &&
                    addLeadController.showDiscountPrice == false)
                ? Align(
                    alignment: Alignment.topRight,
                    child: CustomText(
                      title:
                          "Plan Old Price : ${addLeadController.selPlan!.offerprice!}",
                      colors: AppTheme.title_dark,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                    ))
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual) &&
                    addLeadController.selPlan != null)
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
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
                                      addLeadController.planValidityController,
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
                      (addLeadController.showDiscountPrice)
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
                                        textEditingController: addLeadController
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
                                        textEditingController: addLeadController
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
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.SMALL_PADDING,
                  )
                : Container(),

            // id == 224 (SUBISU)
            (addLeadController.selectedBillTo != null &&
                    addLeadController.selectedBillTo!.id == 224)
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
                                      decoration: Utils.ddlDecoration(
                                        fillColor: addLeadController
                                                        .serviceAreaPlanPostpaidData !=
                                                    null &&
                                                addLeadController
                                                        .serviceAreaPlanPostpaidData!
                                                        .allowdiscount ==
                                                    false
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
                                      value: addLeadController.selDiscountType,
                                      items: addLeadController.discountTypeList!
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
                                      onChanged: addLeadController
                                                      .serviceAreaPlanPostpaidData !=
                                                  null &&
                                              addLeadController
                                                      .serviceAreaPlanPostpaidData!
                                                      .allowdiscount ==
                                                  false
                                          ? null
                                          : (value) {
                                              addLeadController
                                                      .selDiscountType =
                                                  value as DropdownDetail?;
                                              addLeadController.update();
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

            // (addLeadController.selPlanCategory != null &&
            //         addLeadController.showDiscountPrice)
            //     ? trialPlanWidget()
            //     : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.showDiscountPrice)
                ? const SizedBox(height: Constant.MEDIUM_PADDING)
                : Container(),
            (addLeadController.selPlanCategory != null &&
                    addLeadController.planGroupMappingList!.isNotEmpty &&
                    addLeadController.selPlanCategory!.text!
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
            (addLeadController.selPlanCategory != null &&
                    addLeadController.planGroupMappingList!.isNotEmpty &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.plan_group))
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: addLeadController.planGroupMappingList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      PlanGroupMappingDetail item =
                          addLeadController.planGroupMappingList![index];
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
                                          addLeadController.individualPlanList!
                                              .remove(item);
                                          addLeadController.update();
                                          addLeadController
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
            (addLeadController.selPlanCategory != null &&
                addLeadController.selPlanCategory!.text!
                    .equalsIgnoreCase(Strings.individual))
                ? Align(
                alignment: Alignment.centerRight,
                child: InkWell(
                  onTap: () {
                    String discount = addLeadController
                        .discountController.text;
                    // if (addEditCafCustomerController.selPlanService == null ||
                    //     addEditCafCustomerController.selPlan == null) {

                    if (addLeadController
                        .selectServicesByServiceAreaData ==
                        null ||
                        addLeadController
                            .serviceAreaPlanPostpaidData ==
                            null ||
                        addLeadController.selDiscountType ==
                            null) {
                      Utils.showSnackbar(
                          Strings.INFO,
                          "Please fill-up mandatory data!",
                          AppTheme.colorWhite,
                          AppTheme.colorBlueRView);
                      return;
                    }

                    addLeadController.individualPlanList!.add(
                        IndividualPlanData(
                            type: addLeadController
                                .showDiscountPrice
                                ? 1
                                : 2,
                            planService: addLeadController
                                .selectServicesByServiceAreaData!,
                            // planDetail: addEditCafCustomerController.selPlan,
                            planDetail: addLeadController
                                .serviceAreaPlanPostpaidData,
                            discount: discount.isEmpty ? "0" : discount,
                            discountType: addLeadController
                                .selDiscountType!.text,
                            newOfferPrice: addLeadController
                                .newOfferPricePlanController.text,
                            // planOfferPrice: addEditCafCustomerController.selPlan!.offerprice!
                            planOfferPrice: addLeadController
                                .serviceAreaPlanPostpaidData!.offerprice!
                                .toString(),
                            trialPlan:
                            addLeadController.trialPlan));

                    addLeadController.selPlanService = null;
                    addLeadController.selPlan = null;
                    addLeadController.planValidityController
                        .clear();
                    addLeadController.discountController.clear();

                    // Service Area
                    addLeadController
                        .selectServicesByServiceAreaData = null;
                    addLeadController
                        .selectedServiceAreaPlanList!
                        .clear();

                    // Discount Type
                    //  addEditCafCustomerController.selDiscountType = null;

                    addLeadController.newOfferPricePlanController
                        .clear();
                    addLeadController.trialPlan = false;
                    if (addLeadController.individualPlanList !=
                        null &&
                        addLeadController
                            .individualPlanList!.isNotEmpty) {
                      addLeadController.billToReadOnly = true;
                      addLeadController.readOnlyDiscountPrice =
                      false;
                    } else {
                      addLeadController.billToReadOnly = false;
                      addLeadController.readOnlyDiscountPrice =
                      true;
                    }
                    addLeadController.calculatePlanDiscountPrice(
                        Strings.add, "");
                    addLeadController.update();
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
            // (addLeadController.selPlanCategory != null &&
            //         addLeadController.selPlanCategory!.text!
            //             .equalsIgnoreCase(Strings.individual))
            //     ? Align(
            //         alignment: Alignment.centerRight,
            //         child: InkWell(
            //           onTap: () {
            //             String discount =
            //                 addLeadController.discountController.text;
            //             // if (addLeadController.selPlanService == null ||
            //             //     addLeadController.selPlan == null) {
            //             if (addLeadController.selectServicesByServiceAreaData ==
            //                     null ||
            //                 addLeadController.serviceAreaPlanPostpaidData ==
            //                     null ||
            //                 addLeadController.selDiscountType == null) {
            //               Utils.showSnackbar(
            //                   Strings.ERROR,
            //                   "Please fill-up data!",
            //                   AppTheme.colorWhite,
            //                   AppTheme.colorRed);
            //               return;
            //             }
            //
            //             addLeadController.individualPlanList!.add(
            //                 IndividualPlanData(
            //                     type:
            //                         addLeadController.showDiscountPrice ? 1 : 2,
            //                     planService: addLeadController
            //                         .selectServicesByServiceAreaData!,
            //                     // planDetail: addLeadController.selPlan,
            //                     planDetail: addLeadController
            //                         .serviceAreaPlanPostpaidData,
            //                     discount: discount.isEmpty ? "0" : discount,
            //                     discountType:
            //                         addLeadController.selDiscountType!.text,
            //                     newOfferPrice: addLeadController
            //                         .newOfferPricePlanController.text,
            //                     // planOfferPrice: addLeadController.selPlan!.offerprice!
            //                     planOfferPrice: addLeadController
            //                         .serviceAreaPlanPostpaidData!.offerprice!
            //                         .toString(),
            //                     trialPlan: addLeadController.trialPlan));
            //
            //             addLeadController.selPlanService = null;
            //             addLeadController.selPlan = null;
            //             addLeadController.planValidityController.clear();
            //             addLeadController.discountController.clear();
            //
            //             // Service Area
            //             addLeadController.selectServicesByServiceAreaData =
            //                 null;
            //             addLeadController.selectedServiceAreaPlanList!.clear();
            //
            //             // Discount Type
            //             addLeadController.selDiscountType = null;
            //
            //             addLeadController.newOfferPricePlanController.clear();
            //             addLeadController.trialPlan = false;
            //             if (addLeadController.individualPlanList != null &&
            //                 addLeadController.individualPlanList!.isNotEmpty) {
            //               addLeadController.billToReadOnly = true;
            //               addLeadController.readOnlyDiscountPrice = false;
            //             } else {
            //               addLeadController.billToReadOnly = false;
            //               addLeadController.readOnlyDiscountPrice = true;
            //             }
            //             addLeadController.calculatePlanDiscountPrice(
            //                 Strings.add, "");
            //             addLeadController.update();
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
            (addLeadController.selPlanCategory != null &&
                    addLeadController.selPlanCategory!.text!
                        .equalsIgnoreCase(Strings.individual))
                ? const SizedBox(
                    height: Constant.MEDIUM_PADDING,
                  )
                : Container(),
            const SizedBox(height: Constant.MEDIUM_PADDING),
            (addLeadController.individualPlanList != null &&
                    addLeadController.individualPlanList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    shrinkWrap: true,
                    itemCount: addLeadController.individualPlanList!.length,
                    itemBuilder: (BuildContext context, int index) {
                      IndividualPlanData item =
                          addLeadController.individualPlanList![index];
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
                                          addLeadController
                                              .individualPlanList!
                                              .remove(item);
                                          addLeadController.update();
                                          addLeadController
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

  // competitorPackDetails() {
  //   return Form(
  //     key: competitorPackFormKey,
  //     autovalidateMode: autoValidateMode,
  //     child: Column(
  //       crossAxisAlignment: CrossAxisAlignment.start,
  //       mainAxisAlignment: MainAxisAlignment.start,
  //       children: [
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   Strings.previous_service_type, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: DropdownButtonHideUnderline(
  //                 child: DropdownButtonFormField(
  //                   icon: SvgPicture.asset(
  //                     downArrowSvg,
  //                     height: Constant.DROP_DOWN_ARROW_W_H,
  //                     width: Constant.DROP_DOWN_ARROW_W_H,
  //                     color: AppTheme.colorBlack,
  //                     fit: BoxFit.fill,
  //                   ),
  //                   decoration: Utils.ddlDecoration(),
  //                   hint: Align(
  //                       alignment: Alignment.centerLeft,
  //                       child: Text(Strings.select_previous_service_type,
  //                           style: TextStyle(
  //                             fontSize: AppTheme.medium,
  //                             color: AppTheme.colorIconGrey,
  //                             fontFamily: AppTheme.appFontName,
  //                           ))),
  //                   style: AppTheme.dropdownTextStyle,
  //                   isExpanded: true,
  //                   isDense: true,
  //                   value: addLeadController.selectedLeadServiceType,
  //                   items: addLeadController.leadServiceTypeList!
  //                       .map((String? value) {
  //                     return DropdownMenuItem<String>(
  //                       value: value,
  //                       child: Align(
  //                         alignment: Alignment.centerLeft,
  //                         child: CustomText(
  //                           title: value,
  //                           colors: AppTheme.colorBlack,
  //                           textAlign: TextAlign.start,
  //                           fontSize: AppTheme.small,
  //                           fontWeight: FontWeight.w500,
  //                         ), //Text(value.desig!),
  //                       ),
  //                     );
  //                   }).toList(),
  //                   onChanged: (value) {
  //                     addLeadController.selectedLeadServiceType =
  //                         value as String?;
  //                     addLeadController.update();
  //                   },
  //                   validator: (value) {
  //                     return null;
  //                   },
  //                 ),
  //               ),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(Strings.previous_amount, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_previous_amount,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.previousAmountController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.number,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //             crossAxisAlignment: CrossAxisAlignment.center,
  //             mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //             children: [
  //               Flexible(
  //                 flex: 1,
  //                 child: titleWithRequireWidget(Strings.previous_month, false),
  //               ),
  //               const SizedBox(
  //                 width: Constant.SMALL_PADDING,
  //               ),
  //               Flexible(
  //                 flex: 2,
  //                 child: DropdownButtonHideUnderline(
  //                   child: DropdownButtonFormField(
  //                     icon: SvgPicture.asset(
  //                       downArrowSvg,
  //                       height: Constant.DROP_DOWN_ARROW_W_H,
  //                       width: Constant.DROP_DOWN_ARROW_W_H,
  //                       color: AppTheme.colorBlack,
  //                       fit: BoxFit.fill,
  //                     ),
  //                     decoration: Utils.ddlDecoration(),
  //                     hint: Align(
  //                         alignment: Alignment.centerLeft,
  //                         child: Text(Strings.select_month,
  //                             style: TextStyle(
  //                               fontSize: AppTheme.medium,
  //                               color: AppTheme.colorIconGrey,
  //                               fontFamily: AppTheme.appFontName,
  //                             ))),
  //                     style: AppTheme.dropdownTextStyle,
  //                     isExpanded: true,
  //                     isDense: true,
  //                     value: addLeadController.selectMonth,
  //                     items: addLeadController.monthList!
  //                         .map((DropdownDetail? value) {
  //                       return DropdownMenuItem<DropdownDetail>(
  //                         value: value,
  //                         child: Align(
  //                           alignment: Alignment.centerLeft,
  //                           child: CustomText(
  //                             title: value!.text,
  //                             colors: AppTheme.colorBlack,
  //                             textAlign: TextAlign.start,
  //                             fontSize: AppTheme.small,
  //                             fontWeight: FontWeight.w500,
  //                           ),
  //                         ),
  //                       );
  //                     }).toList(),
  //                     onChanged: (value) {
  //                       addLeadController.selectMonth =
  //                           value as DropdownDetail?;
  //                       addLeadController.update();
  //                     },
  //                     validator: (value) {
  //                       // need to add validation
  //                       return null;
  //                     },
  //                   ),
  //                 ),
  //               ),
  //             ]),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //             crossAxisAlignment: CrossAxisAlignment.center,
  //             mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //             children: [
  //               Flexible(
  //                 flex: 1,
  //                 child: titleWithRequireWidget(
  //                     Strings.competitor_pack_duration_new, false),
  //               ),
  //               const SizedBox(
  //                 width: Constant.SMALL_PADDING,
  //               ),
  //               Flexible(
  //                 flex: 2,
  //                 child: Row(
  //                   crossAxisAlignment: CrossAxisAlignment.start,
  //                   mainAxisAlignment: MainAxisAlignment.start,
  //                   children: [
  //                     Expanded(
  //                       flex: 1,
  //                       child: CoustomTextField(
  //                           labelText: Strings.enter_pack_duration,
  //                           hintColor: AppTheme.colorIconGrey,
  //                           textEditingController:
  //                               addLeadController.packDurationController,
  //                           borderEnableColors: AppTheme.colorIconGrey,
  //                           borderFocusColors: AppTheme.colorIconGrey,
  //                           textColor: AppTheme.colorBlack,
  //                           keyboardType: TextInputType.number,
  //                           fontSize: AppTheme.small,
  //                           textInputAction: TextInputAction.next,
  //                           fontWeight: FontWeight.w500,
  //                           contentPadding: const EdgeInsets.symmetric(
  //                               horizontal: Constant.MEDIUM_PADDING,
  //                               vertical: Constant.MEDIUM_PADDING),
  //                           borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                           onTextValidator: (String? value) {
  //                             return null;
  //                           },
  //                           onTextFiledOnTap: () {},
  //                           readOnly: false),
  //                     ),
  //                     const SizedBox(
  //                       width: Constant.SMALL_PADDING,
  //                     ),
  //                     Expanded(
  //                       flex: 1,
  //                       child: DropdownButtonHideUnderline(
  //                         child: DropdownButtonFormField(
  //                           icon: SvgPicture.asset(
  //                             downArrowSvg,
  //                             height: Constant.DROP_DOWN_ARROW_W_H,
  //                             width: Constant.DROP_DOWN_ARROW_W_H,
  //                             color: AppTheme.colorBlack,
  //                             fit: BoxFit.fill,
  //                           ),
  //                           decoration: Utils.ddlDecoration(),
  //                           hint: Align(
  //                               alignment: Alignment.centerLeft,
  //                               child: Text(Strings.state,
  //                                   style: TextStyle(
  //                                     fontSize: AppTheme.medium,
  //                                     color: AppTheme.colorIconGrey,
  //                                     fontFamily: AppTheme.appFontName,
  //                                   ))),
  //                           style: AppTheme.dropdownTextStyle,
  //                           isExpanded: true,
  //                           isDense: true,
  //                           value: addLeadController.selectDurationUnit,
  //                           items: Utils.getDurationUnits()!
  //                               .map<DropdownMenuItem<String>>((value) {
  //                             return DropdownMenuItem<String>(
  //                               value: value,
  //                               child: Align(
  //                                 alignment: Alignment.centerLeft,
  //                                 child: CustomText(
  //                                   title: value,
  //                                   colors: AppTheme.colorBlack,
  //                                   textAlign: TextAlign.start,
  //                                   fontSize: AppTheme.small,
  //                                   fontWeight: FontWeight.w500,
  //                                 ), //Text(value.desig!),
  //                               ),
  //                             );
  //                           }).toList(),
  //                           onChanged: (value) {
  //                             addLeadController.selectDurationUnit =
  //                                 value as String?;
  //                             addLeadController.update();
  //                           },
  //                           validator: (value) {
  //                             if (value == null ||
  //                                 addLeadController.selectDurationUnit ==
  //                                     null) {
  //                               return Strings.select_unit;
  //                             }
  //                             return null;
  //                           },
  //                         ),
  //                       ),
  //                     ),
  //                   ],
  //                 ),
  //               ),
  //             ]),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(Strings.expiry, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.date_format,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController: addLeadController.expiryController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   maxLength: 6,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {
  //                     selectExpiryDate(context, Strings.expiry_date,
  //                         DateTime(DateTime.now().year - 100), DateTime.now());
  //                   },
  //                   readOnly: true),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(Strings.current_pay, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_amount,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.currentPayController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.number,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   Strings.customer_feedback_new, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_customer_feedback,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.customerFeedbackPayController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   maxLength: 250,
  //                   minLines: 3,
  //                   maxLines: 5,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //       ],
  //     ),
  //   );
  // }

  basicCAFDetailsForm() {
    return Form(
      key: basicCAFFormKey,
      autovalidateMode: autoValidateMode,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
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
          //               child: Text(Strings.select_title,
          //                   style: TextStyle(
          //                     fontSize: AppTheme.medium,
          //                     color: AppTheme.colorIconGrey,
          //                     fontFamily: AppTheme.appFontName,
          //                   ))),
          //           style: AppTheme.dropdownTextStyle,
          //           isExpanded: true,
          //           isDense: true,
          //           value: addLeadController.selectCustomerTitleCAF,
          //           items:
          //               Utils.getTitle().map<DropdownMenuItem<String>>((value) {
          //             return DropdownMenuItem<String>(
          //               value: value,
          //               child: Align(
          //                 alignment: Alignment.centerLeft,
          //                 child: CustomText(
          //                   title: value,
          //                   colors: AppTheme.colorBlack,
          //                   textAlign: TextAlign.start,
          //                   fontSize: AppTheme.small,
          //                   fontWeight: FontWeight.w500,
          //                 ), //Text(value.desig!),
          //               ),
          //             );
          //           }).toList(),
          //           onChanged: (value) {
          //             addLeadController.selectCustomerTitleCAF =
          //                 value as String?;
          //             addLeadController.update();
          //           },
          //           validator: (value) {
          //             if (value == null) {
          //               return Strings.please_select_title;
          //             }
          //             return null;
          //           },
          //         ),
          //       ),
          //     ),
          //   ],
          // ),
          // const SizedBox(height: Constant.MEDIUM_PADDING),
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
                    labelText: Strings.enter_contact_person,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController:
                        addLeadController.contactPersonPayController,
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
                        return Strings.enter_contact_person;
                      }
                      return null;
                    },
                    onTextFiledOnTap: () {},
                    readOnly: false),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          addLeadController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              SizedBox(
                width: Constant.SMALL_PADDING,
                child: Checkbox(
                  value: addLeadController.isCredentialMatchWithAccountNo,
                  activeColor: AppTheme.colorPrimary,
                  onChanged: (value) {
                    addLeadController.isCredentialMatchWithAccountNo =
                        !addLeadController.isCredentialMatchWithAccountNo;
                    addLeadController.aaaUserNameController.clear();
                    addLeadController.aaaPasswordController.clear();
                    addLeadController.update();
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
          addLeadController.isCredentialMatchWithAccountNo == false
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
                          labelText: Strings.enter_aaa_username,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addLeadController.aaaUserNameController,
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
                          readOnly: false),
                    ),
                  ],
                )
              : SizedBox.shrink(),
          addLeadController.isCredentialMatchWithAccountNo == false
              ? const SizedBox(height: Constant.MEDIUM_PADDING)
              : SizedBox.shrink(),
          addLeadController.isCredentialMatchWithAccountNo == false
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
                          labelText: Strings.enter_aaa_password,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                              addLeadController.aaaPasswordController,
                          keyboardType: TextInputType.text,
                          // borderEnableColors: AppTheme.colorPrimary,
                          borderEnableColors: AppTheme.colorIconGrey,
                          borderFocusColors: AppTheme.colorIconGrey,
                          textColor: AppTheme.colorBlack,
                          fontSize: AppTheme.small,
                          textInputAction: TextInputAction.done,
                          fontWeight: FontWeight.w500,
                          onTextValidator: (String? value) {
                            if (value!.isEmpty) {
                              return Strings.please_enter_aaa_password;
                            }
                            return null;
                          },
                          suffixIcon: IconButton(
                            onPressed: () {
                              addLeadController.isVisibleAAAPassword =
                                  !addLeadController.isVisibleAAAPassword!;
                              addLeadController.update();
                            },
                            icon: Icon(
                              addLeadController.isVisibleAAAPassword!
                                  ? Icons.visibility
                                  : Icons.visibility_off,
                              color: AppTheme.colorGrey,
                            ),
                          ),
                          borderCorner: Constant.INPUT_ROUNDED_CORNER,
                          contentPadding: const EdgeInsets.symmetric(
                              vertical: Constant.LARGE_PADDING - 2,
                              horizontal: Constant.LARGE_PADDING),
                          readOnly: false,
                          obscureText: addLeadController.isVisibleAAAPassword!),
                    ),
                  ],
                )
              : SizedBox.shrink(),
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
                    labelText: Strings.enter_username,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.userNameController,
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
                child: titleWithRequireWidget(Strings.password, true),
              ),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              Flexible(
                flex: 2,
                child: CoustomTextField(
                    labelText: Strings.enter_password,
                    hintColor: AppTheme.colorIconGrey,
                    textEditingController: addLeadController.passwordController,
                    keyboardType: TextInputType.text,
                    // borderEnableColors: AppTheme.colorPrimary,
                    borderEnableColors: AppTheme.colorIconGrey,
                    borderFocusColors: AppTheme.colorIconGrey,
                    textColor: AppTheme.colorBlack,
                    fontSize: AppTheme.small,
                    textInputAction: TextInputAction.done,
                    fontWeight: FontWeight.w500,
                    onTextValidator: (String? value) {
                      if (value!.isEmpty) {
                        return Strings.please_enter_password;
                      }
                      return null;
                    },
                    suffixIcon: IconButton(
                      onPressed: () {
                        addLeadController.isVisiblePassword =
                            !addLeadController.isVisiblePassword!;
                        addLeadController.update();
                      },
                      icon: Icon(
                        addLeadController.isVisiblePassword!
                            ? Icons.visibility
                            : Icons.visibility_off,
                        color: AppTheme.colorGrey,
                      ),
                    ),
                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                    contentPadding: const EdgeInsets.symmetric(
                        vertical: Constant.LARGE_PADDING - 2,
                        horizontal: Constant.LARGE_PADDING),
                    readOnly: false,
                    obscureText: addLeadController.isVisiblePassword!),
              ),
            ],
          ),
          const SizedBox(height: Constant.MEDIUM_PADDING),
          (addLeadController.selectedCustomerLeadType != null &&
                  addLeadController.selectedCustomerLeadType!.text!
                      .equalsIgnoreCase(Strings.postpaid) &&
                  addLeadController.from.equalsIgnoreCase(Strings.lead_caf))
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
                          value: addLeadController.selectedBillDay,
                          items:
                              addLeadController.billDayList!.map((int value) {
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
                            addLeadController.selectedBillDay = value as int?;
                            addLeadController.update();
                          },
                          validator: (value) {
                            if (value == null ||
                                addLeadController.selectedBillDay == null) {
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
          (addLeadController.selectedCustomerLeadType != null &&
                  addLeadController.selectedCustomerLeadType!.text!
                      .equalsIgnoreCase(Strings.postpaid))
              ? const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                )
              : Container(),
        ],
      ),
    );
  }

  // basicSecondaryContactDetailsForm() {
  //   return Form(
  //     key: secondaryContactFormKey,
  //     autovalidateMode: autoValidateMode,
  //     child: Column(
  //       crossAxisAlignment: CrossAxisAlignment.start,
  //       mainAxisAlignment: MainAxisAlignment.start,
  //       children: [
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(Strings.land_line_number, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_land_line_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.landlineNumberController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(Strings.secondary_email, false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_email,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryEmailController,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   "${Strings.secondary_phone} 1", false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_phone_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryPhoneController1,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   "${Strings.secondary_phone} 2", false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_phone_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryPhoneController2,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   "${Strings.secondary_phone} 3", false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_phone_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryPhoneController3,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   "${Strings.secondary_phone} 4", false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_phone_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryPhoneController4,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //         Row(
  //           crossAxisAlignment: CrossAxisAlignment.center,
  //           mainAxisAlignment: MainAxisAlignment.spaceBetween,
  //           children: [
  //             Flexible(
  //               flex: 1,
  //               child: titleWithRequireWidget(
  //                   "${Strings.secondary_phone} 5", false),
  //             ),
  //             const SizedBox(
  //               width: Constant.SMALL_PADDING,
  //             ),
  //             Flexible(
  //               flex: 2,
  //               child: CoustomTextField(
  //                   labelText: Strings.enter_secondary_phone_number,
  //                   hintColor: AppTheme.colorIconGrey,
  //                   textEditingController:
  //                       addLeadController.secondaryPhoneController5,
  //                   borderEnableColors: AppTheme.colorIconGrey,
  //                   borderFocusColors: AppTheme.colorIconGrey,
  //                   textColor: AppTheme.colorBlack,
  //                   keyboardType: TextInputType.text,
  //                   fontSize: AppTheme.small,
  //                   textInputAction: TextInputAction.next,
  //                   fontWeight: FontWeight.w500,
  //                   contentPadding: const EdgeInsets.symmetric(
  //                       horizontal: Constant.MEDIUM_PADDING,
  //                       vertical: Constant.MEDIUM_PADDING),
  //                   borderCorner: Constant.BTN_ROUNDED_CORNER,
  //                   onTextValidator: (String? value) {
  //                     return null;
  //                   },
  //                   onTextFiledOnTap: () {},
  //                   readOnly: false),
  //             ),
  //           ],
  //         ),
  //         const SizedBox(height: Constant.MEDIUM_PADDING),
  //       ],
  //     ),
  //   );
  // }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.followup_date_time) {
      if (addLeadController.selectedFollowUpDate != null) {
        selectedDate = addLeadController.selectedFollowUpDate;
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
      if (identity == Strings.followup_date_time) {
        addLeadController.selectedFollowUpDate = picked;
        addLeadController.update();
        _selectDateTime();
      }
    }
  }

  Future<void> selectExpiryDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.expiry_date) {
      if (addLeadController.selectedExpiryDate != null) {
        selectedDate = addLeadController.selectedExpiryDate;
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
        addLeadController.selectedExpiryDate = picked;
        addLeadController.expiryController.text =
            addLeadController.apiDateFormat.format(picked);
      }
      addLeadController.update();
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
        addLeadController.selectedFollowUpDate!.year,
        addLeadController.selectedFollowUpDate!.month,
        addLeadController.selectedFollowUpDate!.day,
        picked.hour,
        picked.minute,
      );
      addLeadController.followupDateTimeController.text =
          addLeadController.dateFormat.format(dt);
      addLeadController.followUpScheduleDate =
          addLeadController.apiDateFormat.format(dt);
      addLeadController.followUpScheduleTime =
          addLeadController.apiTimeFormat.format(dt);
      addLeadController.update();
    }
  }

  void _showCountryCodeDialog() async {
    showCountryPicker(
      context: context,
      showPhoneCode: true,
      onSelect: (Country country) {
        addLeadController.countryCode = "+${country.phoneCode}";
        addLeadController.update();
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

  openParentCustomerScreen(String? type) async {
    var result = await Get.to(ParentCustomerList(),
        arguments: {Constant.CUSTOMER_TYPE: addLeadController.type!});
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.parent_customer)) {
          addLeadController.selectedParentCustomer = data;
          addLeadController.parentCustomerController.text = data.name!;
        } else if (type.equalsIgnoreCase(Strings.billableTo)) {
          addLeadController.selectedParentCustomer = data;
          addLeadController.billableToController.text = data.name!;
          addLeadController.billableToCustomerId = data.id!;
        }
        addLeadController.update();
      }
    }
  }

  openExistingCustomerScreen() async {
    var result = await Get.to(() => ExistingCustomerList());
    if (result != null) {
      CustomerDetail data = result;
      if (data != null) {
        addLeadController.getCustomerDetailById(data.id!);
        addLeadController.update();
      }
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

  trialPlanWidget() {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(children: [
        SizedBox(
          width: 10,
          child: Checkbox(
            value: addLeadController.trialPlan,
            activeColor: AppTheme.colorPrimary,
            onChanged: (value) {
              addLeadController.trialPlan = !addLeadController.trialPlan;
              addLeadController.update();
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
              addLeadController.trialPlan = !addLeadController.trialPlan;
              addLeadController.update();
            }),
      ]),
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
      addLeadController.setBtnClickEvent(true);
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
        addLeadController.setBtnClickEvent(true);
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
        addLeadController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    addLeadController.isLoading = true;
    addLeadController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        addLeadController.setBtnClickEvent(false);
        addLeadController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        addLeadController.latController.text =
            currentPosition.latitude.toString();
        addLeadController.longController.text =
            currentPosition.longitude.toString();
        addLeadController.update();
      } else {
        addLeadController.isLoading = false;
        addLeadController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      addLeadController.isLoading = false;
      addLeadController.update();
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

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        addLeadController.selectedLocation = data;
        addLeadController.update();
        addLeadController.getLocationToLatLong();
      }
    }
  }
}
