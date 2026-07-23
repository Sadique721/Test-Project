import 'dart:io';

import 'package:savbill/pages/customer/location_list.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/inventory/pop/add_edit_pop_controller.dart';
import 'package:savbill/pages/inventory/pop/service_area_selection_dialog.dart';
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

class AddEditPop extends StatefulWidget {
  @override
  _AddEditPopState createState() => _AddEditPopState();
}

class _AddEditPopState extends State<AddEditPop>
    with WidgetsBindingObserver
    implements ServiceAreaSelectionAction, LocationBtnAction {
  final addEditPopController = Get.put(AddEditPopController());
  final addEditPopFormKey = GlobalKey<FormState>();
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
    addEditPopController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (addEditPopController.checkBtnClickEvent) {
          addEditPopController.setBtnClickEvent(false);
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
      child: GetBuilder<AddEditPopController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditPopController.isLoading),
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
                      key: addEditPopFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.pop_name, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.pop_name,
                              textEditingController:
                                  addEditPopController.popNameController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_pop_name;
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
                              title: Strings.pop_code, require: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.enter_pop_code,
                              textEditingController:
                              addEditPopController.popCodeController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                // if (value!.isEmpty) {
                                //   return Strings.please_enter_pop_code;
                                // }
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
                              title: Strings.latitude, require: true),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.latitude,
                              textEditingController:
                                  addEditPopController.latController,
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
                                  addEditPopController.longController,
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
                                  addEditPopController.servicesAreaController,
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
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.status, require: true),
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
                              value: addEditPopController.selectedStatus,
                              items: addEditPopController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditPopController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditPopController.selectedStatus ==
                                        null) {
                                  return Strings.please_select_status;
                                }
                                return null;
                              },
                            ),
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

    if (from.equalsIgnoreCase(Strings.service_area)) {
      if (addEditPopController.serviceAreaList != null &&
          addEditPopController.serviceAreaList!.isNotEmpty) {
        for (var element in addEditPopController.serviceAreaList!) {
          element.selected = false;
        }
        if (addEditPopController.selectedServiceArea.isNotEmpty) {
          for (var element in addEditPopController.serviceAreaList!) {
            for (int selElement in addEditPopController.selectedServiceAreaIds) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditPopController.serviceAreaList!);
      }
    }

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

  openLocationListScreen() async {
    var result = await Get.to(LocationList());
    if (result != null) {
      LocationDetail data = result;
      if (data != null) {
        addEditPopController.selectedLocation = data;
        addEditPopController.update();
        addEditPopController.getLocationToLatLong();
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
      addEditPopController.setBtnClickEvent(true);
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
        addEditPopController.setBtnClickEvent(true);
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
        addEditPopController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    addEditPopController.isLoading = true;
    addEditPopController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        addEditPopController.setBtnClickEvent(false);
        addEditPopController.isLoading = false;

        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        addEditPopController.latController.text =
            currentPosition.latitude.toString();
        addEditPopController.longController.text =
            currentPosition.longitude.toString();
        addEditPopController.update();
      } else {
        addEditPopController.isLoading = false;
        addEditPopController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      addEditPopController.isLoading = false;
      addEditPopController.update();
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
    if (addEditPopFormKey.currentState!.validate()) {
      addEditPopController.addEditPopApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditPopController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_pop
            : Strings.create_pop,
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
    if (identifier.toString().equalsIgnoreCase(Strings.service_area) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditPopController.selectedServiceArea.clear();
      addEditPopController.selectedServiceAreaIds.clear();
      for (StaffServiceAreaDetail element in selectedItem) {
        addEditPopController.selectedServiceArea.add(element.name!);
        addEditPopController.selectedServiceAreaIds.add(element.id!);
        serviceAreaName = "$serviceAreaName${element.name!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addEditPopController.servicesAreaController.text = serviceAreaName;
    }
    addEditPopController.update();
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
