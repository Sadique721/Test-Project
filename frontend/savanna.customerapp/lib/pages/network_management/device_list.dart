import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/create_network_screen.dart';
import 'package:savbill/pages/network_management/device_item.dart';
import 'package:savbill/pages/network_management/device_list_controller.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_product_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_type_res.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class DeviceList extends StatefulWidget {
  @override
  _DeviceListState createState() => _DeviceListState();
}

class _DeviceListState extends State<DeviceList> implements LogoutClickEvent {
  final deviceListController = Get.put(DeviceListController());
  final GlobalKey<ScaffoldState> _deviceListKey = GlobalKey();

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    deviceListController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<DeviceListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: _deviceListKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: _body(),
            ),
          ),
          ProgressBar(isLoader: deviceListController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: Strings.network_device,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    Row(children: [

                      Material(
                        color: AppTheme.colorWhite,
                        elevation: 2,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(20)),
                        child: InkWell(
                          onTap: () {
                            openCreateNetworkScreen();
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.colorPrimary,
                              borderRadius:
                              const BorderRadius.all(Radius.circular(20)),
                            ),
                            padding: const EdgeInsets.all(6),
                            child: Icon(
                              Icons.add,
                              color: AppTheme.colorWhite,
                              size: 22,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: Constant.MEDIUM_PADDING,
                      ),
                      InkWell(
                        onTap: () {
                          if (deviceListController.filterViewOpen) {
                            deviceListController.filterViewOpen = false;
                          } else {
                            if (deviceListController.isLoadFilterData ==
                                false) {
                              deviceListController.getDeviceProductList();
                            } else {
                              deviceListController.filterViewOpen = true;
                            }
                          }
                          deviceListController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: deviceListController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ])
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              deviceListController.filterViewOpen
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: Container(
                        width: MediaQuery.of(context).size.width,
                        child: Material(
                          color: AppTheme.colorWhite, //AppTheme.colorFilterBg
                          elevation: 1.5,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER - 2)),
                          child: Padding(
                            padding:
                                const EdgeInsets.all(Constant.SMALL_PADDING),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                /*CoustomTextField(
                                    labelText: Strings.device_name,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: deviceListController
                                        .deviceNameController,
                                    borderEnableColors: AppTheme.colorBlack,
                                    borderFocusColors: AppTheme.colorPrimary,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {},
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),*/
                                Row(children: [
                                  Expanded(
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
                                      value: deviceListController
                                          .selectedDeviceType,
                                      items: deviceListController
                                          .deviceTypeList!
                                          .map((NetworkDeviceType value) {
                                        return DropdownMenuItem<
                                            NetworkDeviceType>(
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
                                        deviceListController
                                                .selectedDeviceType =
                                            value as NetworkDeviceType?;
                                        deviceListController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  )),
                                 /* const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING,
                                  ),
                                  Expanded(
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
                                      value: deviceListController
                                          .selectedDeviceProduct,
                                      items: deviceListController
                                          .deviceProductList!
                                          .map((NetworkDeviceProduct value) {
                                        return DropdownMenuItem<
                                            NetworkDeviceProduct>(
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
                                        deviceListController
                                                .selectedDeviceProduct =
                                            value as NetworkDeviceProduct?;
                                        deviceListController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  )),*/
                                ]),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                /*Row(children: [
                                  Expanded(
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
                                      value: deviceListController
                                          .selectedServiceArea,
                                      items: deviceListController
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
                                        deviceListController
                                                .selectedServiceArea =
                                            value as ServicesAreaDetail?;
                                        deviceListController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  )),
                                  const SizedBox(
                                    width: Constant.VERY_SMALL_PADDING,
                                  ),
                                  Expanded(
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
                                      isExpanded: true,
                                      isDense: true,
                                      value:
                                          deviceListController.selectedStatus,
                                      items: deviceListController.statusList!
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
                                        deviceListController.selectedStatus =
                                            value as DropdownDetail?;
                                        deviceListController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  )),
                                ]),*/
                                // const SizedBox(
                                //   height: Constant.SMALL_PADDING,
                                // ),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          deviceListController.applyFilter();
                                        },
                                        radius: Constant.BTN_HEIGHT_M,
                                        height: Constant.BTN_HEIGHT_M,
                                        bgColors: AppTheme.colorPrimary,
                                        child: CustomText(
                                          title: Strings.apply,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                    const SizedBox(
                                      width: Constant.LARGE_PADDING,
                                    ),
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          deviceListController.clearFilter();
                                        },
                                        radius: Constant.BTN_HEIGHT_M,
                                        height: Constant.BTN_HEIGHT_M,
                                        bgColors: AppTheme.colorBlack,
                                        borderColors: AppTheme.colorBlack,
                                        child: CustomText(
                                          title: Strings.clear,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w500,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    )
                  : Container(),
              deviceListController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
              Expanded(
                flex: 1,
                child: (deviceListController.deviceList != null &&
                        deviceListController.deviceList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: deviceListController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                deviceListController.deviceList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  deviceListController.deviceList?.length) {
                                if (deviceListController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                DeviceDetail item =
                                    deviceListController.deviceList![index];
                                return InkWell(
                                  onTap: () {},
                                  child: DeviceItem(
                                    item: item,
                                    onTapDetail: () {
                                      deviceListController.getDeviceDetail(
                                          item.id!, Strings.device_detail);
                                    },
                                    onTapUpdateLocation: () {
                                      deviceListController.getDeviceDetail(
                                          item.id!,
                                          Strings.update_device_location);
                                    },
                                    onTapPortBind: () {
                                      deviceListController.getDeviceDetail(
                                          item.id!,
                                          Strings.parent_device_mapping);
                                    },
                                    onTapDelete: () {
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
                                                deviceListController
                                                    .deleteDevice(item, index);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                  ),
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.network_management, '', AppTheme.colorPrimary,
        true, _onMenuClick, [], AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (_deviceListKey.currentState!.isDrawerOpen) {
      _deviceListKey.currentState?.closeDrawer();
    } else {
      _deviceListKey.currentState?.openDrawer();
    }
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      Get.offAllNamed(AppRoutes.DASHBOARD,
          arguments: {Constant.FROM: Strings.payment_system});
    }
  }

  @override
  void logoutClick() {
    deviceListController.getStorage.remove(Constant.USER_DATA);
    deviceListController.getStorage.remove(Constant.USER_TOKEN);
    deviceListController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }


  openCreateNetworkScreen() async {
    Get.to(CreateNetworkScreen(), arguments: {});
  }
}
