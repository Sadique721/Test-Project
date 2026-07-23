import 'dart:io';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/device_location_update_controller.dart';
import 'package:savbill/pages/network_management/service_area_dialog.dart';
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

class DeviceLocationUpdate extends StatefulWidget {
  @override
  _DeviceLocationUpdateState createState() => _DeviceLocationUpdateState();
}

class _DeviceLocationUpdateState extends State<DeviceLocationUpdate>
    with WidgetsBindingObserver
    implements LocationBtnAction, ServiceAreaAction {
  final deviceLocationUpdateController =
      Get.put(DeviceLocationUpdateController());

  final locationUpdateFormKey = GlobalKey<FormState>();

  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    deviceLocationUpdateController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (deviceLocationUpdateController.checkBtnClickEvent) {
          deviceLocationUpdateController.setBtnClickEvent(false);
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
      child: GetBuilder<DeviceLocationUpdateController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: deviceLocationUpdateController.isLoading),
        ]);
      }),
    );
  }

  _body(DeviceLocationUpdateController controller) {
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
                    padding: const EdgeInsets.all(
                      Constant.SCREEN_PADDING,
                    ),
                    child: Form(
                      key: locationUpdateFormKey,
                      autovalidateMode:
                          deviceLocationUpdateController.autoValidateMode,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          /*_______________ name ______________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(title: Strings.name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          deviceLocationUpdateController.deviceDetail != null
                              ? CoustomTextField(
                                  labelText: Strings.name,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: deviceLocationUpdateController
                                          .nameController,
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
                                      return Strings.please_enter_name;
                                    } else {}
                                    return null;
                                  },
                                  onTextFiledOnTap: () {
                                    // openParentCustomerScreen();
                                  },
                                  readOnly: false)
                              : const SizedBox.shrink(),

                          /*_______________ product ____________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.product, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          deviceLocationUpdateController.deviceDetail != null
                              ? CoustomTextField(
                                  labelText: Strings.parent_customer,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: TextEditingController(
                                      text: deviceLocationUpdateController
                                          .product_Name),
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
                                    if (value!.isEmpty) {
                                      return Strings.please_select_product;
                                    } else {}
                                    return null;
                                  },
                                  onTextFiledOnTap: () {
                                    // openParentCustomerScreen();
                                  },
                                  readOnly: true)
                              : const SizedBox.shrink(),

                      /*_______________ Select Inward ____________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.select_inward, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                           CoustomTextField(
                              labelText: Strings.parent_customer,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: TextEditingController(
                                  text: ""),
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
                                // openParentCustomerScreen();
                              },
                              readOnly: true),

                          /*__________________ Device Type_________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.device_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.parent_customer,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: TextEditingController(
                                  text: deviceLocationUpdateController
                                      .deviceDetail!.devicetype!),
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
                                if (value!.isEmpty) {
                                  return Strings.please_select_customer;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // openParentCustomerScreen();
                              },
                              readOnly: true),


                        /*__________________ service Area _________________*/


                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.service_area, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.service_area,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                              deviceLocationUpdateController
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


                          /*___________ Lat & Long _______________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.latitude, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.latitude,
                              textEditingController:
                                  deviceLocationUpdateController
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
                                  deviceLocationUpdateController
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
                                  // openLocationListScreen();
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

                          /*_________________ status __________________________*/

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
                              value: deviceLocationUpdateController.selectedStatus,
                              items: deviceLocationUpdateController.statusList!
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
                                deviceLocationUpdateController.selectedStatus =
                                value as DropdownDetail?;
                                deviceLocationUpdateController.update();
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
                        title: Strings.update_text,
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

  validateForm() {
    if (locationUpdateFormKey.currentState!.validate()) {
      deviceLocationUpdateController.updateDeviceDetail();
    } else {
      deviceLocationUpdateController.autoValidateMode =
          AutovalidateMode.onUserInteraction;
      deviceLocationUpdateController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.update_network,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
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
      deviceLocationUpdateController.setBtnClickEvent(true);
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
        deviceLocationUpdateController.setBtnClickEvent(true);
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
        deviceLocationUpdateController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    deviceLocationUpdateController.isLoading = true;
    deviceLocationUpdateController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        deviceLocationUpdateController.setBtnClickEvent(false);
        deviceLocationUpdateController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        deviceLocationUpdateController.latitudeController.text =
            currentPosition.latitude.toString();
        deviceLocationUpdateController.longitudeController.text =
            currentPosition.longitude.toString();
        deviceLocationUpdateController.update();
      } else {
        deviceLocationUpdateController.isLoading = false;
        deviceLocationUpdateController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      deviceLocationUpdateController.isLoading = false;
      deviceLocationUpdateController.update();
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



  showServicesAreaSelectionDialog(String from) {
    List<ServicesAreaDetail> item = [];

    if (from.equalsIgnoreCase(Strings.service_area)) {
      if (deviceLocationUpdateController.servicesAreaList != null &&
          deviceLocationUpdateController.servicesAreaList!.isNotEmpty) {
        for (var element in deviceLocationUpdateController.servicesAreaList!) {
          element.selected = false;
        }
        if (deviceLocationUpdateController.selectedServicesArea!.isNotEmpty) {
          for (var element
              in deviceLocationUpdateController.servicesAreaList!) {
            for (ServicesAreaDetail selElement
                in deviceLocationUpdateController.selectedServicesArea!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(deviceLocationUpdateController.servicesAreaList!);
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
      deviceLocationUpdateController.selectedServicesArea!.clear();
      for (ServicesAreaDetail element in selectedItem) {
        deviceLocationUpdateController.selectedServicesArea!.add(element);
        serviceAreaName = "$serviceAreaName${element.name!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      deviceLocationUpdateController.servicesAreaController.text =
          serviceAreaName;
    }
    deviceLocationUpdateController.update();
  }
}
