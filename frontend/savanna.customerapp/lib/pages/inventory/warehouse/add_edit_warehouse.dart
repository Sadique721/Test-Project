import 'dart:io';

import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/inventory/module/response/branch_service_area_list_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_team_based_inventory_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/inventory/module/response/warehouse_type_res.dart';
import 'package:savbill/pages/inventory/pop/service_area_selection_dialog.dart';
import 'package:savbill/pages/inventory/warehouse/_team_select_dialog.dart';
import 'package:savbill/pages/inventory/warehouse/add_edit_warehouse_controller.dart';
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
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

class AddEditWareHouse extends StatefulWidget {
  @override
  _AddEditWareHouseState createState() => _AddEditWareHouseState();
}

class _AddEditWareHouseState extends State<AddEditWareHouse>
    with WidgetsBindingObserver
    implements
        ServiceAreaSelectionAction,
        TeamSelectionAction,
        LocationBtnAction {
  final addEditWareHouseController = Get.put(AddEditWareHouseController());
  final addEditWareHouseFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    addEditWareHouseController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (addEditWareHouseController.checkBtnClickEvent) {
          addEditWareHouseController.setBtnClickEvent(false);
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
      child: GetBuilder<AddEditWareHouseController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditWareHouseController.isLoading),
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
                      key: addEditWareHouseFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ware_house_name,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.ware_house_name,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .warehouseNameController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return Strings
                                              .please_enter_ware_house_name;
                                        }
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ware_house_type,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: IgnorePointer(
                                    ignoring: addEditWareHouseController
                                                .wareHouseData !=
                                            null
                                        ? true
                                        : false,
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
                                            Strings.ware_house_type,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: addEditWareHouseController
                                            .selectedType,
                                        items: addEditWareHouseController
                                            .typeList
                                            ?.map((WareHouseTypeDetail value) {
                                          return DropdownMenuItem<
                                              WareHouseTypeDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController
                                                  .selectedType =
                                              value as WareHouseTypeDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditWareHouseController
                                                      .selectedType ==
                                                  null) {
                                            return Strings
                                                .please_select_ware_house_type;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.parent_service_area,
                                      require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.parent_service_area,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .parentServicesAreaController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        /* if (value!.isEmpty) {
                                          return Strings
                                              .please_select_parent_service_area;
                                        }*/
                                      },
                                      onTextFiledOnTap: () {
                                        if (addEditWareHouseController
                                                .wareHouseData !=
                                            null) {
                                          print("not editable");
                                        } else {
                                          showServicesAreaSelectionDialog(
                                              Strings.parent_service_area);
                                        }
                                      },
                                      readOnly: true),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),

                          //new warehouse code

                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.ware_house_code,
                                      require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.ware_house_code,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .warehouseCodeController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        /* if (value!.isEmpty) {
                                          return Strings
                                              .please_enter_ware_house_name;
                                        }*/
                                        return null;
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.team, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.select_a_team,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .teamController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
                                      textColor: AppTheme.colorBlack,
                                      keyboardType: TextInputType.text,
                                      fontSize: AppTheme.small,
                                      textInputAction: TextInputAction.next,
                                      fontWeight: FontWeight.w500,
                                      suffixIcon: Icon(Icons.keyboard_arrow_down),
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.MEDIUM_PADDING,
                                              vertical:
                                                  Constant.MEDIUM_PADDING),
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return Strings.please_select_team;
                                        }
                                      },
                                      onTextFiledOnTap: () {
                                        // if (addEditWareHouseController
                                        //         .wareHouseData !=
                                        //     null) {
                                        //   print("not editable");
                                        // } else {
                                          showServicesAreaSelectionDialog(
                                              Strings.team);
                                        // }
                                      },
                                      readOnly: false),

                                  /*DropdownButtonHideUnderline(
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
                                            Strings.select_a_team,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: addEditWareHouseController
                                            .selectedTeamInventoryData,
                                        items: addEditWareHouseController
                                            .allTeamBasedInventoryList
                                            ?.map((AllTeamDataList value) {
                                          return DropdownMenuItem<
                                              AllTeamDataList>(
                                            value: value,
                                            child: Text(value.displayName!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController
                                              .selectedTeamInventoryData =
                                          value as AllTeamDataList?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditWareHouseController
                                                  .selectedTeamInventoryData ==
                                                  null) {
                                            return Strings.please_select_team;
                                          }
                                          return null;
                                        },
                                      ),
                                    )*/
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.status, require: true),
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
                                            Strings.status,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: addEditWareHouseController
                                            .selectedStatus,
                                        items: addEditWareHouseController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController
                                                  .selectedStatus =
                                              value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditWareHouseController
                                                      .selectedStatus ==
                                                  null) {
                                            return Strings.please_select_status;
                                          }
                                          return null;
                                        },
                                      ),
                                    )),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.description,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: TextFormField(
                                    controller: addEditWareHouseController
                                        .descriptionController,
                                    maxLines: 3,
                                    maxLength: 250,
                                    style: const TextStyle(
                                        fontSize: AppTheme.medium),
                                    decoration: InputDecoration(
                                      hintText: Strings.description,
                                      fillColor: Colors.white,
                                      filled: true,
                                      alignLabelWithHint: true,
                                      contentPadding: const EdgeInsets.all(
                                          Constant.TEXT_FIELD_CONTENT_PADDING),
                                      focusColor: Colors.transparent,
                                      focusedBorder: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.BTN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorPrimary,
                                            width: 1.0),
                                      ),
                                      enabledBorder: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.BTN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                          color: AppTheme.colorIconGrey,
                                          width: 1.0,
                                        ),
                                      ),
                                      border: OutlineInputBorder(
                                          borderRadius: BorderRadius.circular(
                                              Constant
                                                  .TEXT_FIELD_CONTENT_PADDING)),
                                      isDense: true,
                                      labelStyle: TextStyle(
                                        color: AppTheme.colorGrey,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.normal,
                                        height: 1,
                                        fontFamily: AppTheme.appFontName,
                                        decoration: TextDecoration.none,
                                      ),
                                      counterText: "",
                                    ),
                                    keyboardType: TextInputType.multiline,
                                    validator: (value) {
                                      if (value!.isEmpty) {
                                        return Strings.please_enter_description;
                                      }
                                    },
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CustomText(
                            title: Strings.location_details,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small + 1,
                            fontWeight: FontWeight.w500,
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.latitude, require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.latitude,
                                      textEditingController:
                                          addEditWareHouseController
                                              .latController,
                                      keyboardType: TextInputType.number,
                                      borderEnableColors: AppTheme.colorBlack,
                                      textInputAction: TextInputAction.next,
                                      hintColor: AppTheme.colorIconGrey,
                                      onTextValidator: (String? value) {
                                        // if (value!.isEmpty) {
                                        //   return Strings.enter_latitude;
                                        // }
                                        return null;
                                      },
                                      borderCorner:
                                          Constant.INPUT_ROUNDED_CORNER,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.LARGE_PADDING),
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.longitude, require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.longitude,
                                      textEditingController:
                                          addEditWareHouseController
                                              .longController,
                                      keyboardType: TextInputType.number,
                                      borderEnableColors: AppTheme.colorBlack,
                                      textInputAction: TextInputAction.next,
                                      hintColor: AppTheme.colorIconGrey,
                                      onTextValidator: (String? value) {
                                        // if (value!.isEmpty) {
                                        //   return Strings.enter_longitude;
                                        // }
                                        return null;
                                      },
                                      borderCorner:
                                          Constant.INPUT_ROUNDED_CORNER,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.LARGE_PADDING),
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.end,
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              InkWell(
                                onTap: () {
                                  locationPermissionStatus();
                                },
                                child: Material(
                                  elevation: 1.5,
                                  color: AppTheme.custNearLocationLight,
                                  shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.BTN_ROUNDED_CORNER)),
                                  child: Container(
                                    height: Constant.BTN_HEIGHT_M,
                                    width: Constant.BTN_HEIGHT_M,
                                    alignment: Alignment.center,
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING - 1),
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
                                      borderRadius: BorderRadius.circular(
                                          Constant.BTN_ROUNDED_CORNER)),
                                  child: Container(
                                    height: Constant.BTN_HEIGHT_M,
                                    width: Constant.BTN_HEIGHT_M,
                                    alignment: Alignment.center,
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING - 1),
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
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CustomText(
                            title: Strings.address_detail,
                            colors: AppTheme.title_dark,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small + 1,
                            fontWeight: FontWeight.w500,
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),

                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.service_area,
                                      require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.service_area,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .servicesAreaController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return Strings.select_service_area;
                                        }
                                      },
                                      onTextFiledOnTap: () {
                                        showServicesAreaSelectionDialog(
                                            Strings.service_area);
                                      },
                                      readOnly: true),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.branch, require: false),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: IgnorePointer(
                                    ignoring: addEditWareHouseController
                                                .wareHouseData !=
                                            null
                                        ? false
                                        : false,
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
                                            Strings.branch,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: addEditWareHouseController
                                            .selBranch,
                                        items: addEditWareHouseController
                                            .branchList
                                            ?.map((BranchServiceAreaDetail
                                                value) {
                                          return DropdownMenuItem<
                                              BranchServiceAreaDetail>(
                                            value: value,
                                            child: Text(value.name!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController.selBranch =
                                              value as BranchServiceAreaDetail?;
                                        },
                                        validator: (value) {
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.address1, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.address1,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .address1Controller,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return "${Strings.enter_address} 1";
                                        } else {
                                          return null;
                                        }
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Flexible(
                                  flex: 1,
                                  child: InputTitleRequire(
                                      title: Strings.address2, require: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Flexible(
                                  flex: 2,
                                  child: CoustomTextField(
                                      labelText: Strings.address2,
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          addEditWareHouseController
                                              .address2Controller,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
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
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {
                                        if (value!.isEmpty) {
                                          return "${Strings.enter_address} 2";
                                        } else {
                                          return null;
                                        }
                                      },
                                      onTextFiledOnTap: () {},
                                      readOnly: false),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
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
                                        child: Text(
                                          Strings.pincode,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ),
                                        ),
                                      ),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: false,
                                      isDense: true,
                                      value:
                                          addEditWareHouseController.selPincode,
                                      items: addEditWareHouseController
                                          .pincodeList
                                          ?.map((PincodeDetail value) {
                                        return DropdownMenuItem<PincodeDetail>(
                                          value: value,
                                          child: Text(value.pincode!),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        addEditWareHouseController.selPincode =
                                            value as PincodeDetail?;
                                        // addEditWareHouseController.update();
                                        addEditWareHouseController
                                            .getPinCodeToAreaList(
                                                addEditWareHouseController
                                                    .selPincode!.id!);
                                        addEditWareHouseController.update();
                                        // setState(() {
                                        //   autoValidateMode =
                                        //       AutovalidateMode.disabled;
                                        // });
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            addEditWareHouseController
                                                    .selPincode ==
                                                null) {
                                          return Strings.select_pincode;
                                        } else {
                                          return null;
                                        }
                                      },
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
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
                                            Strings.city,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value:
                                            addEditWareHouseController.selCity,
                                        items: addEditWareHouseController
                                            .cityList
                                            ?.map((CityDetail value) {
                                          return DropdownMenuItem<CityDetail>(
                                            value: value,
                                            child: CustomText(title: value.name!,colors: AppTheme.title_dark,),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController.selCity =
                                              value as CityDetail?;
                                          addEditWareHouseController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null ||
                                          //      addEditWareHouseController
                                          //          .selCity ==
                                          //          null) {
                                          //    return Strings.select_city;
                                          //  }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
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
                                            Strings.state,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value:
                                            addEditWareHouseController.selState,
                                        items: addEditWareHouseController
                                            .stateList
                                            ?.map((StateDetail value) {
                                          return DropdownMenuItem<StateDetail>(
                                            value: value,
                                            child: Text(value.name!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController.selState =
                                              value as StateDetail?;
                                          addEditWareHouseController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null ||
                                          //     addEditWareHouseController
                                          //             .selState ==
                                          //         null) {
                                          //   return Strings.select_state;
                                          // }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
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
                                            Strings.country,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: true,
                                        value: addEditWareHouseController
                                            .selCountry,
                                        items: addEditWareHouseController
                                            .countryList
                                            ?.map((CountryDetail value) {
                                          return DropdownMenuItem<
                                              CountryDetail>(
                                            value: value,
                                            child: Text(value.name!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditWareHouseController
                                                  .selCountry =
                                              value as CountryDetail?;
                                          addEditWareHouseController.update();
                                        },
                                        validator: (value) {
                                          // if (value == null ||
                                          //     addEditWareHouseController
                                          //             .selCountry ==
                                          //         null) {
                                          //   return Strings.select_country;
                                          // }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                ),
                              ]),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
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
            ]),
      ),
    );
  }

  showServicesAreaSelectionDialog(String from) {
    List<StaffServiceAreaDetail> item = [];
    List<AllTeamDataList> itemTeam = [];
    if (from.equalsIgnoreCase(Strings.parent_service_area)) {
      if (addEditWareHouseController.parentServiceAreaList != null &&
          addEditWareHouseController.parentServiceAreaList!.isNotEmpty) {
        for (var element in addEditWareHouseController.parentServiceAreaList!) {
          element.selected = false;
        }
        if (addEditWareHouseController.selectedParentServiceArea.isNotEmpty) {
          for (var element
              in addEditWareHouseController.parentServiceAreaList!) {
            for (int selElement
                in addEditWareHouseController.selectedParentServiceArea) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditWareHouseController.parentServiceAreaList!);
      }
    }
    else if (from.equalsIgnoreCase(Strings.service_area)) {
      if (addEditWareHouseController.serviceAreaList != null &&
          addEditWareHouseController.serviceAreaList!.isNotEmpty) {
        for (var element in addEditWareHouseController.serviceAreaList!) {
          element.selected = false;
        }
        if (addEditWareHouseController.selectedServiceArea.isNotEmpty) {
          for (var element in addEditWareHouseController.serviceAreaList!) {
            for (int selElement
                in addEditWareHouseController.selectedServiceArea) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditWareHouseController.serviceAreaList!);
      }
    }
    else if (from.equalsIgnoreCase(Strings.team)) {
      if (addEditWareHouseController.allTeamBasedInventoryList != null &&
          addEditWareHouseController.allTeamBasedInventoryList!.isNotEmpty) {
        for (var element
            in addEditWareHouseController.allTeamBasedInventoryList!) {
          element.selected = false;
        }
        if (addEditWareHouseController
            .selectedAllTeamInventoryList!.isNotEmpty) {
          for (var element
              in addEditWareHouseController.allTeamBasedInventoryList!) {
            for (int selElement
                in addEditWareHouseController.selectedAllTeamInventoryList!) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        itemTeam.addAll(addEditWareHouseController.allTeamBasedInventoryList!);
      }
    }
    if (from.equalsIgnoreCase(Strings.team)) {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return TeamSelectionDialog(
                teamSelectionAction: this, fromFor: from, itemTeam: itemTeam);
          });
    } else {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return ServiceAreaSelectionDialog(
                serviceAreaSelectionAction: this,
                fromFor: from,
                itemsOrgLst: item);
          });
    }
  }

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      addEditWareHouseController.selectedLocation = data;
      addEditWareHouseController.update();
      addEditWareHouseController.getLocationToLatLong();
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
      addEditWareHouseController.setBtnClickEvent(true);
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
        addEditWareHouseController.setBtnClickEvent(true);
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
        addEditWareHouseController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    addEditWareHouseController.isLoading = true;
    addEditWareHouseController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        addEditWareHouseController.setBtnClickEvent(false);
        addEditWareHouseController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        addEditWareHouseController.latController.text =
            currentPosition.latitude.toString();
        addEditWareHouseController.longController.text =
            currentPosition.longitude.toString();
        addEditWareHouseController.update();
      } else {
        addEditWareHouseController.isLoading = false;
        addEditWareHouseController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      addEditWareHouseController.isLoading = false;
      addEditWareHouseController.update();
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

  validateForm() {
    if (addEditWareHouseFormKey.currentState!.validate()) {
      addEditWareHouseController.addEditWareHouseApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditWareHouseController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_ware_house
            : Strings.add_ware_house,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void serviceAreaSelectionBtnAction(
      {String? identifier, List<StaffServiceAreaDetail>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.parent_service_area) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditWareHouseController.selectedParentServiceArea.clear();
      for (StaffServiceAreaDetail element in selectedItem) {
        addEditWareHouseController.selectedParentServiceArea.add(element.id!);
        serviceAreaName = "$serviceAreaName${element.name!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addEditWareHouseController.parentServicesAreaController.text =
          serviceAreaName;
    } else if (identifier.toString().equalsIgnoreCase(Strings.service_area) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditWareHouseController.selectedServiceArea.clear();
      for (StaffServiceAreaDetail element in selectedItem) {
        addEditWareHouseController.selectedServiceArea.add(element.id!);
        serviceAreaName = "$serviceAreaName${element.name!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addEditWareHouseController.servicesAreaController.text = serviceAreaName;
      addEditWareHouseController.update();
      addEditWareHouseController.branchList!.clear();
      addEditWareHouseController.selBranch = null;
      if (addEditWareHouseController.wareHouseData != null) {
        addEditWareHouseController.getPinCodeFromArea();
      } else {
        addEditWareHouseController.getBranchServiceArea(
            addEditWareHouseController.selectedServiceArea);
      }
      // addEditWareHouseController.getPinCodeFromArea();
    }
    addEditWareHouseController.update();
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

  @override
  void teamSelectionBtnAction(
      {String? identifier, List<AllTeamDataList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.team) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String teamName = "";
      addEditWareHouseController.selectedAllTeamInventoryList!.clear();
      for (AllTeamDataList element in selectedItem) {
        addEditWareHouseController.selectedAllTeamInventoryList!
            .add(element.id!);
        teamName = "$teamName${element.name!}, ";
      }

      if (!teamName.isNullOrEmpty() &&
          teamName.contains(",") &&
          teamName.length >= 2) {
        teamName = teamName.substring(0, teamName.length - 2);
      }
      addEditWareHouseController.teamController.text = teamName;
    }
    addEditWareHouseController.update();
  }
}
