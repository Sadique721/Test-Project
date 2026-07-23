import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/network_devices_by_device_type_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer/parent_customer.dart';
import 'package:savbill/pages/customer/parent_staff_list.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/shift_location/create_shift_location_controller.dart';
import 'package:savbill/pages/shift_location/response/charge_by_type_res.dart';
import 'package:savbill/pages/shift_location/response/partner_service_res.dart';
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
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

import '../inventory/module/response/view_pop_list_res.dart';

class CreateShiftLocation extends StatefulWidget {
  @override
  _CreateShiftLocationState createState() => _CreateShiftLocationState();
}

class _CreateShiftLocationState extends State<CreateShiftLocation> {
  final createShiftLocationController =
      Get.put(CreateShiftLocationController());

  final shiftLocationFormKey = GlobalKey<FormState>();
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
    return GetBuilder<CreateShiftLocationController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: createShiftLocationController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                children: [
                  Expanded(
                      child: CustomText(
                          title: Strings.present_address_details,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500))
                ],
              ),
            ),
            //  const SizedBox(height: Constant.MEDIUM_PADDING),
            Expanded(
              child: SingleChildScrollView(
                child: Padding(
                  padding: const EdgeInsets.only(
                      left: Constant.SCREEN_PADDING,
                      right: Constant.SCREEN_PADDING),
                  child: Form(
                    key: shiftLocationFormKey,
                    autovalidateMode: autoValidateMode,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.service_area, require: true),
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
                                    value: createShiftLocationController
                                        .selPresentServiceArea,
                                    items: createShiftLocationController
                                        .servicesAreaList!
                                        .map((ServicesAreaDetail value) {
                                      return DropdownMenuItem<
                                          ServicesAreaDetail>(
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
                                      createShiftLocationController
                                              .selPresentServiceArea =
                                          value as ServicesAreaDetail?;
                                      createShiftLocationController
                                          .selPresentArea = null;
                                      createShiftLocationController
                                          .selPresentCity = null;
                                      createShiftLocationController
                                          .selPresentState = null;
                                      createShiftLocationController
                                          .selPresentCountry = null;
                                      // createShiftLocationController.areaList!.clear();
                                      createShiftLocationController.cityList!
                                          .clear();
                                      createShiftLocationController.stateList!
                                          .clear();
                                      createShiftLocationController.countryList!
                                          .clear();
                                      createShiftLocationController
                                          .buildingManagementDataList!
                                          .clear();
                                      createShiftLocationController
                                          .buildingNumberList!
                                          .clear();
                                      createShiftLocationController
                                          .selectedBuildingNumber = null;
                                      createShiftLocationController
                                              .selectedBuildingManagementData =
                                          null;
                                      createShiftLocationController.update();
                                      createShiftLocationController
                                          .getPincodeData();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          createShiftLocationController
                                                  .selPresentServiceArea ==
                                              null) {
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
                                child: InputTitleRequire(
                                    title: Strings.pop, require: false),
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
                                        Strings.select_pop,
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
                                    value: createShiftLocationController
                                        .selectedPop,
                                    items:
                                        Utils.popList!.map((PopDetail value) {
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
                                      createShiftLocationController
                                          .selectedPop = value as PopDetail?;
                                      createShiftLocationController.update();
                                    },
                                    validator: (value) {
                                      // if (value == null ||
                                      //     createShiftLocationController
                                      //         .selectedPop ==
                                      //         null) {
                                      //   return Strings.please_select_pop;
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
                                child: InputTitleRequire(
                                    title: Strings.olt, require: false),
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
                                        Strings.select_olt,
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
                                    value: createShiftLocationController
                                        .selectedOLTDevice,
                                    items: createShiftLocationController
                                            .oltNetworkDevicesByDeviceList!
                                            .isEmpty
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
                                        : createShiftLocationController
                                            .oltNetworkDevicesByDeviceList!
                                            .map((NetworkDevicesByDeviceDataList
                                                value) {
                                            return DropdownMenuItem<
                                                NetworkDevicesByDeviceDataList>(
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
                                      createShiftLocationController
                                              .selectedOLTDevice =
                                          value
                                              as NetworkDevicesByDeviceDataList?;
                                      createShiftLocationController.update();
                                    },
                                    validator: (value) {
                                      // if (value == null ||
                                      //     createShiftLocationController
                                      //         .selectedOLTDevice ==
                                      //         null) {
                                      //   return Strings.please_select_olt;
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
                                child: InputTitleRequire(
                                    title: Strings.address, require: true),
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
                                        createShiftLocationController
                                            .presentAddController,
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
                                child: InputTitleRequire(
                                    title: Strings.pincode, require: true),
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
                                    value: createShiftLocationController
                                        .selPresentPincode,
                                    items: createShiftLocationController
                                        .pincodeList!
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
                                      createShiftLocationController
                                              .selPresentPincode =
                                          value as PincodeDetail?;
                                      createShiftLocationController.update();
                                      createShiftLocationController
                                          .getPinCodeToAreaData(
                                              createShiftLocationController
                                                  .selPresentPincode!
                                                  .pincodeid!);
                                      //createShiftLocationController.getAreaDetail();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          createShiftLocationController
                                                  .selPresentPincode ==
                                              null) {
                                        return Strings.select_pincode;
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
                                child: InputTitleRequire(
                                    title: Strings.area, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: false,
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
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createShiftLocationController
                                          .selPresentArea,
                                      items: createShiftLocationController
                                          .areaList!
                                          .map((PincodeAreaDetail value) {
                                        return DropdownMenuItem<
                                            PincodeAreaDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.name!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createShiftLocationController
                                                .selPresentArea =
                                            value as PincodeAreaDetail?;
                                        createShiftLocationController.update();
                                        // createShiftLocationController
                                        //     .getAreaDetail(value!.id);
                                        createShiftLocationController
                                                .selectedBuildingManagementData =
                                            null;
                                        createShiftLocationController
                                            .getSubAreaFromAreaCall(value!.id);
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            createShiftLocationController
                                                    .selPresentArea ==
                                                null) {
                                          return Strings.select_area;
                                        }
                                        return null;
                                      },
                                    ),
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
                                child: InputTitleRequire(
                                    title: Strings.sub_area, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: false,
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
                                          child: Text(Strings.select_sub_area,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createShiftLocationController
                                          .selectedSubAreaData,
                                      items: createShiftLocationController
                                          .subAreaDataList!
                                          .isEmpty
                                          ? [
                                        DropdownMenuItem<SubAreaDataList>(
                                          value: null,
                                          enabled: false,
                                          child: CustomText(
                                            title: Strings.no_data_found,
                                            colors: AppTheme.title_dark,
                                          ), // Disable selection
                                        ),
                                      ] :
                                      createShiftLocationController
                                          .subAreaDataList!
                                          .map((SubAreaDataList value) {
                                        return DropdownMenuItem<
                                            SubAreaDataList>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.name!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createShiftLocationController
                                                .selectedSubAreaData =
                                            value as SubAreaDataList?;
                                        createShiftLocationController.update();
                                        createShiftLocationController
                                            .buildingManagementDataList!
                                            .clear();
                                        createShiftLocationController
                                            .buildingNumberList!
                                            .clear();
                                        createShiftLocationController
                                            .selectedBuildingNumber = null;
                                        createShiftLocationController
                                                .selectedBuildingManagementData =
                                            null;
                                        createShiftLocationController
                                            .getBuildingMgmtCall(
                                                entityId:
                                                    createShiftLocationController
                                                        .selectedSubAreaData!
                                                        .id,
                                                entryName: "Sub Area");
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
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
                                child: InputTitleRequire(
                                    title: Strings.building_name,
                                    require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: false,
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
                                          child: Text(Strings.select_building,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createShiftLocationController
                                          .selectedBuildingManagementData,
                                      items: createShiftLocationController
                                              .buildingManagementDataList!
                                              .isEmpty
                                          ? [
                                              DropdownMenuItem<BuildingManagementDataList>(
                                                value: null,
                                                enabled: false,
                                                child: CustomText(
                                                  title: Strings.no_data_found,
                                                  colors: AppTheme.title_dark,
                                                ), // Disable selection
                                              ),
                                            ]
                                          : createShiftLocationController
                                              .buildingManagementDataList!
                                              .map((BuildingManagementDataList
                                                  value) {
                                              return DropdownMenuItem<
                                                  BuildingManagementDataList>(
                                                value: value,
                                                child: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: CustomText(
                                                    title: value.buildingName!,
                                                    colors: AppTheme.colorBlack,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight: FontWeight.w500,
                                                  ),
                                                ),
                                              );
                                            }).toList(),
                                      onChanged: (value) {
                                        createShiftLocationController
                                                .selectedBuildingManagementData =
                                            value
                                                as BuildingManagementDataList?;
                                        createShiftLocationController.update();
                                        createShiftLocationController
                                            .getBuildingMgmtNumbersCall(
                                                createShiftLocationController
                                                    .selectedBuildingManagementData!
                                                    .buildingMgmtId!);
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
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
                                child: InputTitleRequire(
                                    title: Strings.building_no, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: false,
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
                                              Strings.select_building_number,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: createShiftLocationController
                                          .selectedBuildingNumber,
                                      items: createShiftLocationController
                                          .buildingNumberList!
                                          .isEmpty
                                          ? [
                                        DropdownMenuItem<String>(
                                          value: null,
                                          enabled: false,
                                          child: CustomText(
                                            title: Strings.no_data_found,
                                            colors: AppTheme.title_dark,
                                          ), // Disable selection
                                        ),
                                      ]
                                          : createShiftLocationController
                                          .buildingNumberList!
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
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        createShiftLocationController
                                                .selectedBuildingNumber =
                                            value as String?;
                                        createShiftLocationController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
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
                                  child: InputTitleRequire(
                                      title: Strings.city, require: true),
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
                                        decoration: Utils.ddlDecoration(
                                            fillColor: AppTheme.colorLightGrey),
                                        hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(Strings.city,
                                                style: TextStyle(
                                                  fontSize: AppTheme.medium,
                                                  color: AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ))),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: createShiftLocationController
                                            .selPresentCity,
                                        items: createShiftLocationController
                                            .cityList!
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
                                          createShiftLocationController
                                                  .selPresentCity =
                                              value as CityDetail?;
                                          createShiftLocationController
                                              .update();
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
                                  child: InputTitleRequire(
                                      title: Strings.state, require: true),
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
                                        decoration: Utils.ddlDecoration(
                                            fillColor: AppTheme.colorLightGrey),
                                        hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(Strings.state,
                                                style: TextStyle(
                                                  fontSize: AppTheme.medium,
                                                  color: AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ))),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: createShiftLocationController
                                            .selPresentState,
                                        items: createShiftLocationController
                                            .stateList!
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
                                          createShiftLocationController
                                                  .selPresentState =
                                              value as StateDetail?;
                                          createShiftLocationController
                                              .update();
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
                                  child: InputTitleRequire(
                                      title: Strings.country, require: true),
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
                                        decoration: Utils.ddlDecoration(
                                            fillColor: AppTheme.colorLightGrey),
                                        hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(Strings.country,
                                                style: TextStyle(
                                                  fontSize: AppTheme.medium,
                                                  color: AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ))),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: createShiftLocationController
                                            .selPresentCountry,
                                        items: createShiftLocationController
                                            .countryList!
                                            .map((CountryDetail value) {
                                          return DropdownMenuItem<
                                              CountryDetail>(
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
                                          createShiftLocationController
                                                  .selPresentCountry =
                                              value as CountryDetail?;
                                          createShiftLocationController
                                              .update();
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
                                  child: InputTitleRequire(
                                      title: Strings.requester_by,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.select_staff,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          createShiftLocationController
                                              .requesterByController,
                                      suffixIcon: Padding(
                                        padding:
                                            const EdgeInsetsDirectional.all(
                                                Constant.LARGE_PADDING - 2),
                                        child: SvgPicture.asset(
                                          downArrowSvg,
                                          color: AppTheme.colorBlack,
                                          width: Constant.ICON_SIZE_S,
                                          height: Constant.ICON_SIZE_S,
                                        ),
                                      ),
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
                                      textColor: AppTheme.colorBlack,
                                      keyboardType: TextInputType.text,
                                      fontSize: AppTheme.small,
                                      textInputAction: TextInputAction.done,
                                      fontWeight: FontWeight.w500,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.MEDIUM_PADDING,
                                              vertical:
                                                  Constant.MEDIUM_PADDING),
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (createShiftLocationController
                                            .requesterByController
                                            .text
                                            .isEmpty) {
                                          return Strings.select_requester_by;
                                        }
                                        return null;
                                      },
                                      onTextFiledOnTap: () {
                                        openParentStaffScreen(
                                            Strings.requester);
                                      },
                                      readOnly: true),
                                ),
                              ]),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title:
                                        "${Strings.branch}/${Strings.partner}",
                                    require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              createShiftLocationController
                                          .isBranchShiftLocation ==
                                      true
                                  ? Flexible(
                                      flex: 2,
                                      child: DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.branch,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: createShiftLocationController
                                              .selectBranchesByServiceAreaData,
                                          items: createShiftLocationController
                                              .branchesByServiceAreaList!
                                              .map(
                                                  (BranchesByServiceAreaDataList
                                                      value) {
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
                                            createShiftLocationController
                                                    .selectBranchesByServiceAreaData =
                                                value
                                                    as BranchesByServiceAreaDataList?;
                                            createShiftLocationController
                                                .update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                createShiftLocationController
                                                        .selectBranchesByServiceAreaData ==
                                                    null) {
                                              return Strings
                                                  .select_branch_partner;
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
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.partner,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: createShiftLocationController
                                              .selectedPartner,
                                          items: createShiftLocationController
                                              .partnerList!
                                              .map(
                                                  (PartnerServiceDetail value) {
                                            return DropdownMenuItem<
                                                PartnerServiceDetail>(
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
                                            createShiftLocationController
                                                    .selectedPartner =
                                                value as PartnerServiceDetail?;
                                            createShiftLocationController
                                                .update();
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                createShiftLocationController
                                                        .selectedPartner ==
                                                    null) {
                                              return Strings
                                                  .select_branch_partner;
                                            }
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),

                          /// Wallet Amount
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title:
                                        "${Strings.wallet} ${Strings.amount}",
                                    require: false),
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
                                        createShiftLocationController
                                            .walletAmountController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    fillColor: AppTheme.colorLightGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {},
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),

                          /// prepaid Value
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title:
                                        "${Strings.prepaid} ${Strings.value}",
                                    require: false),
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
                                        createShiftLocationController
                                            .prepaidAmountController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    fillColor: AppTheme.colorLightGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {},
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),

                          ///Due Value
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: "${Strings.due} ${Strings.value}",
                                    require: false),
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
                                        createShiftLocationController
                                            .dueAmountController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    fillColor: AppTheme.colorLightGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {},
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.EXTRA_LARGE_PADDING),

                          Row(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Expanded(
                                child: InkWell(
                                  onTap: () {
                                    createShiftLocationController
                                            .samePaymentAdd =
                                        !createShiftLocationController
                                            .samePaymentAdd;
                                    createShiftLocationController.update();
                                  },
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Padding(
                                        padding: const EdgeInsets.only(
                                            top: Constant.VERY_SMALL_PADDING),
                                        child: SizedBox(
                                          width: 15,
                                          height: 10,
                                          child: Checkbox(
                                            value: createShiftLocationController
                                                .samePaymentAdd,
                                            activeColor: AppTheme.colorPrimary,
                                            onChanged: (value) {
                                              createShiftLocationController
                                                      .samePaymentAdd =
                                                  !createShiftLocationController
                                                      .samePaymentAdd;
                                              createShiftLocationController
                                                  .update();
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        child: CustomText(
                                          title:
                                              "Set Payment Address\n${Strings.same_as_parent_address}",
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
                              const SizedBox(width: Constant.MEDIUM_PADDING),
                              Expanded(
                                child: InkWell(
                                  onTap: () {
                                    createShiftLocationController
                                            .samePermanentAdd =
                                        !createShiftLocationController
                                            .samePermanentAdd;
                                    createShiftLocationController.update();
                                  },
                                  child: Row(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Padding(
                                        padding: const EdgeInsets.only(
                                            top: Constant.VERY_SMALL_PADDING),
                                        child: SizedBox(
                                          width: 15,
                                          height: 10,
                                          child: Checkbox(
                                            value: createShiftLocationController
                                                .samePermanentAdd,
                                            activeColor: AppTheme.colorPrimary,
                                            onChanged: (value) {
                                              createShiftLocationController
                                                      .samePermanentAdd =
                                                  !createShiftLocationController
                                                      .samePermanentAdd;
                                              createShiftLocationController
                                                  .update();
                                            },
                                          ),
                                        ),
                                      ),
                                      const SizedBox(
                                        width: Constant.SMALL_PADDING,
                                      ),
                                      Expanded(
                                        child: CustomText(
                                          title:
                                              "Set Permanent Address\n${Strings.same_as_parent_address}",
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.normal,
                                        ),
                                      ),
                                    ],
                                  ),
                                ),
                              )
                            ],
                          ),
                          const SizedBox(height: Constant.EXTRA_LARGE_PADDING),
                          createShiftLocationController.customerTypeData!
                                  .equalsIgnoreCase("CUSTOMER")
                              ? Stack(
                                  children: <Widget>[
                                    Container(
                                      width: double.infinity,
                                      margin: const EdgeInsets.fromLTRB(
                                          0, 20, 0, 10),
                                      padding: const EdgeInsets.only(
                                          bottom: 5, left: 15, right: 15),
                                      decoration: BoxDecoration(
                                        border: Border.all(
                                            color: AppTheme.colorBlackEnd,
                                            width: 1),
                                        borderRadius: BorderRadius.circular(5),
                                        shape: BoxShape.rectangle,
                                      ),
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        mainAxisAlignment:
                                            MainAxisAlignment.start,
                                        children: [
                                          const SizedBox(
                                            height: Constant.SCREEN_PADDING +
                                                Constant.SMALL_PADDING,
                                          ),
                                          InputTitleRequire(
                                              title: Strings.billableTo,
                                              require: false),
                                          const SizedBox(
                                            height: Constant.VERY_SMALL_PADDING,
                                          ),
                                          CoustomTextField(
                                              labelText:
                                                  Strings.select_billable_to,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  createShiftLocationController
                                                      .billableToController,
                                              suffixIcon: Padding(
                                                padding:
                                                    const EdgeInsetsDirectional
                                                        .all(
                                                        Constant.LARGE_PADDING -
                                                            2),
                                                child: SvgPicture.asset(
                                                  downArrowSvg,
                                                  color: AppTheme.colorBlack,
                                                  width: Constant.ICON_SIZE_S,
                                                  height: Constant.ICON_SIZE_S,
                                                ),
                                              ),
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType: TextInputType.text,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.done,
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
                                                // if (createShiftLocationController
                                                //     .billableToController
                                                //     .text
                                                //     .isEmpty) {
                                                //   return Strings.select_bill_to;
                                                // }
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
                                              title: Strings.payment_owner,
                                              require: true),
                                          const SizedBox(
                                            height: Constant.VERY_SMALL_PADDING,
                                          ),
                                          CoustomTextField(
                                              labelText: Strings.select_staff,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  createShiftLocationController
                                                      .paymentOwnerController,
                                              suffixIcon: Padding(
                                                padding:
                                                    const EdgeInsetsDirectional
                                                        .all(
                                                        Constant.LARGE_PADDING -
                                                            2),
                                                child: SvgPicture.asset(
                                                  downArrowSvg,
                                                  color: AppTheme.colorBlack,
                                                  width: Constant.ICON_SIZE_S,
                                                  height: Constant.ICON_SIZE_S,
                                                ),
                                              ),
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              keyboardType: TextInputType.text,
                                              fontSize: AppTheme.small,
                                              textInputAction:
                                                  TextInputAction.done,
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
                                                if (createShiftLocationController
                                                    .paymentOwnerController
                                                    .text
                                                    .isEmpty) {
                                                  return Strings
                                                      .select_requester_by;
                                                }
                                                return null;
                                              },
                                              onTextFiledOnTap: () {
                                                openParentStaffScreen(
                                                    Strings.payment_owner);
                                              },
                                              readOnly: true),
                                          const SizedBox(
                                            height: Constant.MEDIUM_PADDING,
                                          ),
                                          InputTitleRequire(
                                              title: Strings.charge,
                                              require: true),
                                          const SizedBox(
                                            height: Constant.VERY_SMALL_PADDING,
                                          ),
                                          DropdownButtonHideUnderline(
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
                                                alignment: Alignment.centerLeft,
                                                child: Text(
                                                  Strings.select_charge,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ),
                                                ),
                                              ),
                                              style: AppTheme.dropdownTextStyle,
                                              // isExpanded: false,
                                              // isDense: true,
                                              value:
                                                  createShiftLocationController
                                                      .selectedChargeList,
                                              items:
                                                  createShiftLocationController
                                                      .chargeList!
                                                      .map((Chargelist value) {
                                                return DropdownMenuItem<
                                                    Chargelist>(
                                                  value: value,
                                                  child:
                                                      Text(value.displayName!),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                createShiftLocationController
                                                    .actualPriceController
                                                    .clear();
                                                createShiftLocationController
                                                        .selectedChargeList =
                                                    value as Chargelist?;
                                                createShiftLocationController
                                                        .actualPrice =
                                                    value!.actualprice;
                                                createShiftLocationController
                                                        .actualPriceController
                                                        .text =
                                                    value.actualprice
                                                        .toString();
                                                createShiftLocationController
                                                    .getBalanceCommissionForShiftLocation();
                                                createShiftLocationController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    createShiftLocationController
                                                            .selectedChargeList ==
                                                        null) {
                                                  return Strings
                                                      .please_select_charge;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                          const SizedBox(
                                            height: Constant.MEDIUM_PADDING,
                                          ),
                                          InputTitleRequire(
                                              title: Strings.actual_price,
                                              require: true),
                                          const SizedBox(
                                            height: Constant.VERY_SMALL_PADDING,
                                          ),
                                          CoustomTextField(
                                              labelText: Strings.amount,
                                              hintColor: AppTheme.colorIconGrey,
                                              textEditingController:
                                                  createShiftLocationController
                                                      .actualPriceController,
                                              borderEnableColors:
                                                  AppTheme.colorIconGrey,
                                              fillColor:
                                                  AppTheme.colorLightGrey,
                                              borderFocusColors:
                                                  AppTheme.colorIconGrey,
                                              textColor: AppTheme.colorBlack,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .MEDIUM_PADDING),
                                              borderCorner:
                                                  Constant.BTN_ROUNDED_CORNER,
                                              onTextValidator:
                                                  (String? value) {},
                                              onTextFiledOnTap: () {},
                                              readOnly: true),
                                          const SizedBox(
                                            height: Constant.MEDIUM_PADDING,
                                          ),
                                          Column(
                                            children: [
                                              Row(
                                                mainAxisSize: MainAxisSize.max,
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.start,
                                                children: [
                                                  Expanded(
                                                    flex: 2,
                                                    child: InputTitleRequire(
                                                        title:
                                                            Strings.charge_type,
                                                        require: true),
                                                  ),
                                                  const SizedBox(
                                                    width:
                                                        Constant.SMALL_PADDING,
                                                  ),
                                                  Expanded(
                                                    flex: 1,
                                                    child: InputTitleRequire(
                                                        title:
                                                            Strings.new_price,
                                                        require: true),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(
                                                height:
                                                    Constant.VERY_SMALL_PADDING,
                                              ),
                                              Row(
                                                children: [
                                                  Flexible(
                                                    flex: 2,
                                                    child:
                                                        DropdownButtonHideUnderline(
                                                      child:
                                                          DropdownButtonFormField(
                                                        icon: SvgPicture.asset(
                                                          downArrowSvg,
                                                          height: Constant
                                                              .DROP_DOWN_ARROW_W_H,
                                                          width: Constant
                                                              .DROP_DOWN_ARROW_W_H,
                                                          color: AppTheme
                                                              .colorBlack,
                                                          fit: BoxFit.fill,
                                                        ),
                                                        decoration: Utils
                                                            .ddlDecoration(),
                                                        hint: Align(
                                                          alignment: Alignment
                                                              .centerLeft,
                                                          child: Text(
                                                            Strings
                                                                .select_charge_type,
                                                            style: TextStyle(
                                                              fontSize: AppTheme
                                                                  .medium,
                                                              color: AppTheme
                                                                  .colorIconGrey,
                                                              fontFamily: AppTheme
                                                                  .appFontName,
                                                            ),
                                                          ),
                                                        ),
                                                        style: AppTheme
                                                            .dropdownTextStyle,
                                                        isExpanded: false,
                                                        isDense: true,
                                                        value: createShiftLocationController
                                                            .selectedChargeTypeList,
                                                        items: createShiftLocationController
                                                            .chargeTypeList
                                                            .map((DropdownDetail
                                                                value) {
                                                          return DropdownMenuItem<
                                                              DropdownDetail>(
                                                            value: value,
                                                            child: Text(
                                                                value.text!),
                                                          );
                                                        }).toList(),
                                                        onChanged: (value) {
                                                          createShiftLocationController
                                                                  .selectedChargeTypeList =
                                                              value
                                                                  as DropdownDetail?;
                                                        },
                                                        validator: (value) {
                                                          if (value == null ||
                                                              createShiftLocationController
                                                                      .selectedChargeTypeList ==
                                                                  null) {
                                                            return Strings
                                                                .please_select_charge_type;
                                                          }
                                                          return null;
                                                        },
                                                      ),
                                                    ),
                                                  ),
                                                  const SizedBox(
                                                    width:
                                                        Constant.SMALL_PADDING,
                                                  ),
                                                  Flexible(
                                                    flex: 1,
                                                    child: CoustomTextField(
                                                        labelText:
                                                            Strings.amount,
                                                        hintColor: AppTheme
                                                            .colorIconGrey,
                                                        textEditingController:
                                                            createShiftLocationController
                                                                .newPriceController,
                                                        keyboardType:
                                                            TextInputType
                                                                .number,
                                                        borderEnableColors:
                                                            AppTheme
                                                                .colorIconGrey,
                                                        borderFocusColors:
                                                            AppTheme
                                                                .colorIconGrey,
                                                        textColor:
                                                            AppTheme.colorBlack,
                                                        fontSize:
                                                            AppTheme.small,
                                                        fontWeight:
                                                            FontWeight.w500,
                                                        contentPadding:
                                                            const EdgeInsets
                                                                .symmetric(
                                                                horizontal: Constant
                                                                    .MEDIUM_PADDING),
                                                        borderCorner: Constant
                                                            .BTN_ROUNDED_CORNER,
                                                        onTextValidator:
                                                            (value) {
                                                          if (value == null ||
                                                              value.isEmpty) {
                                                            return Strings
                                                                .please_enter_new_price;
                                                          }
                                                          return null;
                                                        },
                                                        onChanged:
                                                            (String value) {
                                                          // if (createShiftLocationController
                                                          //     .newPriceController
                                                          //     .text
                                                          //     .isNotEmpty) {
                                                          //   if (double.parse(
                                                          //           createShiftLocationController
                                                          //               .newPriceController
                                                          //               .text
                                                          //               .toString()) <
                                                          //       createShiftLocationController
                                                          //           .actualPrice!) {
                                                          //     return Utils.showSnackbar(
                                                          //         Strings.INFO,
                                                          //         Strings
                                                          //             .new_price_must_not_actual_charge_price,
                                                          //         AppTheme.colorWhite,
                                                          //         AppTheme
                                                          //             .colorBlueRView);
                                                          //   }
                                                          // }
                                                          // return null;
                                                        },
                                                        onTextFiledOnTap: () {},
                                                        readOnly: false),
                                                  ),
                                                ],
                                              ),
                                            ],
                                          ),
                                          const SizedBox(
                                            height: Constant.MEDIUM_PADDING,
                                          ),
                                          Column(
                                            children: [
                                              Row(
                                                mainAxisSize: MainAxisSize.max,
                                                crossAxisAlignment:
                                                    CrossAxisAlignment.start,
                                                children: [
                                                  Expanded(
                                                    flex: 1,
                                                    child: InputTitleRequire(
                                                        title: Strings.discount,
                                                        require: false),
                                                  ),
                                                ],
                                              ),
                                              const SizedBox(
                                                height:
                                                    Constant.VERY_SMALL_PADDING,
                                              ),
                                              Row(
                                                children: [
                                                  Flexible(
                                                    flex: 1,
                                                    child: CoustomTextField(
                                                        labelText: Strings
                                                            .enter_discount,
                                                        hintColor: AppTheme
                                                            .colorIconGrey,
                                                        textEditingController:
                                                            createShiftLocationController
                                                                .discountController,
                                                        keyboardType:
                                                            TextInputType
                                                                .number,
                                                        borderEnableColors:
                                                            AppTheme
                                                                .colorIconGrey,
                                                        borderFocusColors:
                                                            AppTheme
                                                                .colorIconGrey,
                                                        textColor:
                                                            AppTheme.colorBlack,
                                                        fontSize:
                                                            AppTheme.small,
                                                        fontWeight:
                                                            FontWeight.w500,
                                                        contentPadding:
                                                            const EdgeInsets
                                                                .symmetric(
                                                                horizontal: Constant
                                                                    .MEDIUM_PADDING),
                                                        borderCorner: Constant
                                                            .BTN_ROUNDED_CORNER,
                                                        onTextValidator:
                                                            (value) {},
                                                        onTextFiledOnTap: () {},
                                                        readOnly: false),
                                                  ),
                                                ],
                                              ),
                                            ],
                                          ),
                                          const SizedBox(
                                            height: Constant.MEDIUM_PADDING,
                                          ),
                                        ],
                                      ),
                                    ),
                                    Positioned(
                                      left: 50,
                                      top: 10,
                                      child: Container(
                                        padding: const EdgeInsets.only(
                                            bottom: 3,
                                            left: 3,
                                            right: 3,
                                            top: 3),
                                        color: Colors.white,
                                        child: CustomText(
                                          title: Strings.charge_details,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                )
                              : SizedBox.shrink(),

                          const SizedBox(height: Constant.MEDIUM_PADDING),
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
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.shift_location, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (shiftLocationFormKey.currentState!.validate()) {
      if (createShiftLocationController.customerTypeData!
          .equalsIgnoreCase("CUSTOMER_CAF")) {
        createShiftLocationController.updateShiftLocation();
      } else {
        if (double.parse(createShiftLocationController.newPriceController.text
                .toString()) <
            createShiftLocationController.actualPrice!) {
          return Utils.showSnackbar(
              Strings.INFO,
              Strings.new_price_must_not_actual_charge_price,
              AppTheme.colorWhite,
              AppTheme.colorBlueRView);
        } else {
          createShiftLocationController.updateShiftLocation();
        }
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  openParentCustomerScreen() async {
    log("customerType==>${createShiftLocationController.customerType!}");
    log("customerDetails==>${jsonEncode(createShiftLocationController.customerDetail!)}");
    log("shift_location==>${jsonEncode(Strings.shift_location)}");

    var result = await Get.to(ParentCustomerList(), arguments: {
      Constant.CUSTOMER_DETAIL: createShiftLocationController.customerDetail!,
      Constant.CUSTOMER_TYPE: createShiftLocationController.customerType!,
      Constant.SHIFT_LOCATION: Strings.shift_location,
    });
    if (result != null) {
      ParentCustomerDetail data = result;
      if (data != null) {
        createShiftLocationController.selectedParentCustomer = data;
        createShiftLocationController.billableToController.text = data.name!;
        createShiftLocationController.billableCustomerId = data.id;
        createShiftLocationController.update();
      }
    }
  }

  openParentStaffScreen(String? type) async {
    var result = await Get.to(ParentStaffList(), arguments: {});
    if (result != null) {
      ParentStaffUserlist data = result;
      if (data != null) {
        if (type!.equalsIgnoreCase(Strings.requester)) {
          createShiftLocationController.selectedParentStaff = data;
          createShiftLocationController.requesterByController.text =
              data.firstname!;
          createShiftLocationController.requesterId = data.id;
        } else if (type.equalsIgnoreCase(Strings.payment_owner)) {
          createShiftLocationController.selectedPaymentOwner = data;
          createShiftLocationController.paymentOwnerController.text =
              data.firstname!;
          createShiftLocationController.paymentOwnerId = data.id;
        }
        createShiftLocationController.update();
      }
    }
  }
}
