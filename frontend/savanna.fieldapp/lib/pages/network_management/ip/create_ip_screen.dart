import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/ip/create_ip_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
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

class CreateIpScreen extends StatefulWidget {
  @override

  _CreateIpState createState() => _CreateIpState();
}

class _CreateIpState extends State<CreateIpScreen> with WidgetsBindingObserver{
  final createIpController = Get.put(CreateIpController());
   var createIpFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: false);
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    // createNetworkController.getDeviceListData();
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        print("on pause method call");
        return;
      case AppLifecycleState.resumed:
        print("on resume method call");
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreateIpController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: createIpController.isLoading),
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
                      key: createIpFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          /*_______________ Network Ip ______________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.network_ip, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_network_ip,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .networkIpController,
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
                                  return Strings.please_select_network_ip;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          /*_______________ Display Name ____________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.display_name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_display_name,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .displayNameController,
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
                                  return Strings.please_select_display_name;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          /*_________________ Ip Range _____________________*/


                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.ip_range, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_ip_range,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .ipRangeController,
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
                                  return Strings.please_select_ip_range;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          /*_________________ Pool Name _____________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.pool_name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_pool_name,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .poolNameController,
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
                                  return Strings.please_select_pool_name;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          /*_________________ Pool Category ___________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.pool_category, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_pool_category,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .poolCategoryController,
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
                                  return Strings.please_select_pool_catergory;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),



                          /*_________________ Pool Type _____________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),

                          InputTitleRequire(
                              title: Strings.pool_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                                  Strings.select_pool_type,
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
                              createIpController.selectedPoolType,
                              items: createIpController.poolTypeList!
                                  .map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: CustomText(
                                    title: value.text!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createIpController.selectedPoolType =
                                value as DropdownDetail?;
                                createIpController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

                          /*_______________  Default Pool Flag___________________________*/


                          const SizedBox(height: Constant.SCREEN_PADDING),

                          InputTitleRequire(
                              title: Strings.default_pool_flag, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                                  Strings.select_defalut_pool_flag,
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
                              createIpController.selectedDefaultPoolFlag,
                              items: createIpController.defaultPoolFlagList!
                                  .map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: CustomText(
                                    title: value.text!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createIpController.selectedDefaultPoolFlag =
                                value as DropdownDetail?;
                                createIpController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),


                          /*_______________  Static Ip Pool ___________________________*/


                          const SizedBox(height: Constant.SCREEN_PADDING),

                          InputTitleRequire(
                              title: Strings.static_ip_pool, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                                  Strings.select_static_ip_pool,
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
                              createIpController.selectedStaticIpPool,
                              items: createIpController.staticIpPoolList!
                                  .map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: CustomText(
                                    title: value.text!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createIpController.selectedStaticIpPool =
                                value as DropdownDetail?;
                                createIpController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

                          /*_____________________ Broadcast Ip ___________________________ */

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.broadcast_ip, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_broadcast_ip,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .broadcastIpController,
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
                                  return Strings.please_select_broadcast_ip;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),


                      /*_____________________ First Host ___________________________ */

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.first_host, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_first_host,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .firstHostController,
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
                                  return Strings.please_select_first_host;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                     /*_____________________ Last Host ___________________________ */

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.last_host, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_last_host,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .lastHostController,
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
                                  return Strings.please_select_last_host;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                      /*_____________________ Total Host ___________________________ */

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.total_host, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_total_host,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .totalHostController,
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
                                  return Strings.please_select_total_host;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                      /*_____________________ Net Mask ___________________________ */

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.net_mask, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_net_mask,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .netMaskController,
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
                                  return Strings.select_select_net_mask;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),


                      /*_______________ status ____________________________*/


                          const SizedBox(height: Constant.SCREEN_PADDING),

                          InputTitleRequire(
                              title: Strings.status, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                              isExpanded: true,
                              isDense: true,
                              value:
                              createIpController.selectedStatus,
                              items: createIpController.statusList!
                                  .map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: CustomText(
                                    title: value.text!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createIpController.selectedStatus =
                                value as DropdownDetail?;
                                createIpController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),


                      /*_____________________ remarks ________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),

                          InputTitleRequire(
                              title: Strings.remarks, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),

                          CoustomTextField(
                              labelText: Strings.enter_remarks,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createIpController
                                  .remarkController,
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
                              maxLines: 3,
                              maxLength: 250,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_remark;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
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
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          createIpController.from.equalsIgnoreCase(Strings.update_text) ? const Padding(
                            padding: EdgeInsets.symmetric(horizontal: 8.0),
                            child: Icon(Icons.check_circle,color: Colors.white,),
                          ) : const SizedBox.shrink(),
                          CustomText(
                            title: createIpController.from.equalsIgnoreCase(Strings.add) ? Strings.add_ip : Strings.update_ip,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w400,
                          ),
                        ],
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
    return DynamicAppBar(
        createIpController.from.equalsIgnoreCase(Strings.add) ? Strings.create_ip : Strings.update_ip,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (createIpFormKey.currentState!.validate()) {
      if(createIpController.from.equalsIgnoreCase(Strings.add)) {
        createIpController.saveIpManagementApiCall();
      }else if(createIpController.from.equalsIgnoreCase(Strings.update_text)){
        createIpController.updateIpManagementApiCall();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

}