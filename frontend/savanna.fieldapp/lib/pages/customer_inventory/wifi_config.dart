import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/product_plan_service_inventory_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_inventory/other_inventory_controller.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/pages/customer_inventory/response/product_non_trackable_product_category_res.dart';
import 'package:savbill/pages/customer_inventory/show_mac_address_screen.dart';
import 'package:savbill/pages/customer_inventory/wifi_config_controller.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
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
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geocoding/geocoding.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

import '../customer/model/response/inventory_job_type.dart';
import '../customer/model/response/nature.dart';

class WifiConfig extends StatefulWidget {
  final CustomerInventoryDataList? item;

  const WifiConfig({super.key,required this.item});

  @override
  _WifiConfigState createState() => _WifiConfigState();
}

class _WifiConfigState extends State<WifiConfig> with WidgetsBindingObserver {
  late final WifiConfigController wifiConfigController;
  final otherInventoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  void initState() {
    wifiConfigController = Get.put(WifiConfigController(item: widget.item));
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    wifiConfigController.setBtnClickEvent(false);
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
      child: GetBuilder<WifiConfigController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: wifiConfigController.isLoading),
        ]);
      }), /**/
    );
  }

  _body(WifiConfigController controller) {
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
                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.SSID_Username, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.user_name,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                wifiConfigController.userController,
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
                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.SSID_Password, require: true),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.enter_password,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController:
                                wifiConfigController.passwordController,
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
                            readOnly: false),
                        const SizedBox(height: Constant.SCREEN_PADDING),
                        InputTitleRequire(
                            title: Strings.working_frequency, require: true),
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
                                child: Text(Strings.working_frequency,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ))),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: controller.selectedFrequency,
                            items: controller.frequencyList!
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
                              controller.selectedFrequency =
                                  value as DropdownDetail?;
                              controller.update();
                            },
                            validator: (value) {
                              if (value == null ||
                                  controller.selectedFrequency ==
                                      null) {
                                return Strings.enter_working_frequency;
                              }
                              return null;
                            },
                          ),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
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

  _appBar() {
    return DynamicAppBar(Strings.wifi_config, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (otherInventoryFormKey.currentState!.validate()) {
      wifiConfigController.saveNMSWifiConfig();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }
}
