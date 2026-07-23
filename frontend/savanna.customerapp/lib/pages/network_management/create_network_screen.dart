import 'dart:io';

import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/create_network_controller.dart';
import 'package:savbill/pages/network_management/model/network_inward_product_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_product_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_type_res.dart';
import 'package:savbill/pages/network_management/service_area_dialog.dart';
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
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';

class CreateNetworkScreen extends StatefulWidget {
  @override

  _CreateNetworkState createState() => _CreateNetworkState();
}

class _CreateNetworkState extends State<CreateNetworkScreen>
    with WidgetsBindingObserver implements LocationBtnAction,ServiceAreaAction{
  final createNetworkController = Get.put(CreateNetworkController());
  final createNetworkFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

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
        // if (createCreditController.checkBtnClickEvent) {
        //   createCreditController.setBtnClickEvent(false);
        // }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreateNetworkController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: createNetworkController.isLoading),
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
                      key: createNetworkFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          /*_______________ name ______________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.name,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createNetworkController
                                  .nameNetworkController,
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
                                  return Strings.please_select_customer;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: false),

                          /*_______________ product ____________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.product, require: true),
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
                                  Strings.product,
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
                                  createNetworkController.selectedDeviceProduct,
                              items: createNetworkController.deviceProductList!
                                  .map((NetworkDeviceProduct value) {
                                return DropdownMenuItem<NetworkDeviceProduct>(
                                  value: value,
                                  child: CustomText(
                                    title: value.name!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createNetworkController.selectedDeviceProduct =
                                    value as NetworkDeviceProduct?;
                              createNetworkController.productId = value!.id;
                                createNetworkController
                                    .getNetworkInwardProductList(value.id);
                                createNetworkController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

                          /*_________________ select Inward _____________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.select_inward, require: true),
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
                                  Strings.select_inward,
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
                                  createNetworkController.selectedInwardProduct,
                              items: createNetworkController.inwardProductList!
                                  .map((NetworkInwardProudctDataList value) {
                                return DropdownMenuItem<
                                    NetworkInwardProudctDataList>(
                                  value: value,
                                  child: CustomText(
                                    title: value.inwardNumber!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createNetworkController.selectedInwardProduct =
                                    value as NetworkInwardProudctDataList?;
                                createNetworkController.inwardProductId = value!.id;
                                createNetworkController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

                          /*_________________ select device Type _____________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.device_type, require: true),
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
                                  Strings.device_type,
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
                              value: createNetworkController.selectedDeviceType,
                              items: createNetworkController.deviceTypeList!
                                  .map((NetworkDeviceType value) {
                                return DropdownMenuItem<NetworkDeviceType>(
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
                                createNetworkController.selectedDeviceType =
                                    value as NetworkDeviceType?;
                                createNetworkController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

                          /*_________________ total ports ___________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.total_ports, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          Row(
                            children: [
                              Expanded(
                                child: CoustomTextField(
                                    labelText: Strings.enter_total_ports,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: createNetworkController.totalPortsController,
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
                                        return Strings.please_enter_ports;
                                      } else {}
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      // openParentCustomerScreen();
                                    },
                                    readOnly: false),
                              ),
                              const SizedBox(width: Constant.SMALL_PADDING,),
                              Expanded(
                                child: CoustomTextField(
                                    labelText: Strings.enter_available_ports,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: createNetworkController
                                        .totalPortsController,
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
                                      // openParentCustomerScreen();
                                    },
                                    readOnly: true),
                              ),
                            ],
                          ),



                          /*_________________ Select service Area _____________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.service_area, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                         /* DropdownButtonHideUnderline(
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
                              value: createNetworkController
                                  .selectedServiceArea,
                              items: createNetworkController
                                  .servicesAreaList!
                                  .map((ServicesAreaDetail value) {
                                return DropdownMenuItem<
                                    ServicesAreaDetail>(
                                  value: value,
                                  child: CustomText(
                                    title: value.name!,
                                    colors: AppTheme.colorBlack,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                  ),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createNetworkController
                                    .selectedServiceArea =
                                value as ServicesAreaDetail?;
                                createNetworkController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),*/
                          CoustomTextField(
                              labelText: Strings.service_area,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                              createNetworkController
                                  .servicesAreaController,
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
                                  return Strings.select_service_area;
                                }
                              },
                              onTextFiledOnTap: () {
                                showServicesAreaSelectionDialog(
                                    Strings.service_area);
                              },
                              readOnly: true),

                      /*_______________ lat & long___________________________*/


                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.latitude, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.latitude,
                              textEditingController:
                              createNetworkController
                                  .latitudeController,
                              keyboardType: TextInputType.number,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_latitude;
                                }
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.longitude, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.longitude,
                              textEditingController:
                              createNetworkController
                                  .longitudeController,
                              keyboardType: TextInputType.number,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_longitude;
                                }
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
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
                              /* const SizedBox(width: Constant.MEDIUM_PADDING),
                              InkWell(
                                onTap: () {
                                  //openLocationListScreen();
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
                              )*/
                            ],
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),


                          /*_______________ status ____________________________*/

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
                              createNetworkController.selectedStatus,
                              items: createNetworkController.statusList!
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
                                createNetworkController.selectedStatus =
                                value as DropdownDetail?;
                                createNetworkController.update();
                              },
                              validator: (value) {
                                return null;
                              },
                            ),
                          ),

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
                      child: CustomText(
                        title: Strings.add_network,
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
    return DynamicAppBar(
        Strings.create_network,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (createNetworkFormKey.currentState!.validate()) {
      createNetworkController.addNetworkDeviceDetail();
      // createNetworkController.callRecordPaymentApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
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
        /* if (Platform.isIOS) {
          getCurrentPosition(false);
        } else {
          permissionDenyDialog();
        }*/
      });
    }
  }

  getCurrentPosition(bool fromTryAgain) async {
    bool serviceEnabled = await checkLocationService();
    if (!serviceEnabled) {
      createNetworkController.setBtnClickEvent(true);
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
        createNetworkController.setBtnClickEvent(true);
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
        createNetworkController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    createNetworkController.isLoading = true;
    createNetworkController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        createNetworkController.setBtnClickEvent(false);
        createNetworkController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        createNetworkController.latitudeController.text =
            currentPosition.latitude.toString();
        createNetworkController.longitudeController.text =
            currentPosition.longitude.toString();
        createNetworkController.update();
      } else {
        createNetworkController.isLoading = false;
        createNetworkController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      createNetworkController.isLoading = false;
      createNetworkController.update();
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
                from: Constant.DEVICE_LOCATION);
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


  showServicesAreaSelectionDialog(String from) {
    List<ServicesAreaDetail> item = [];

    if (from.equalsIgnoreCase(Strings.service_area)) {
      if (createNetworkController.servicesAreaList != null &&
          createNetworkController.servicesAreaList!.isNotEmpty) {
        for (var element in createNetworkController.servicesAreaList!) {
          element.selected = false;
        }
        if (createNetworkController.selectedServicesArea!.isNotEmpty) {
          for (var element in createNetworkController.servicesAreaList!) {
            for (ServicesAreaDetail selElement in createNetworkController.selectedServicesArea!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(createNetworkController.servicesAreaList!);
      }
    }

    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ServiceAreaDialog(
              serviceAreaAction: this, fromFor: from, itemsOrgLst: item);
        });
  }

  @override
  void serviceAreaBtnAction(
      {String? identifier, List<ServicesAreaDetail>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.service_area) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      createNetworkController.selectedServicesArea!.clear();
      for (ServicesAreaDetail element in selectedItem) {
        createNetworkController.selectedServicesArea!.add(element);
        serviceAreaName = "$serviceAreaName${element.name!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      createNetworkController.servicesAreaController.text =
          serviceAreaName;
    }
    createNetworkController.update();
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
