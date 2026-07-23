import 'package:savbill/pages/network_management/bind_port_item.dart';
import 'package:savbill/pages/network_management/device_port_bind_controller.dart';
import 'package:savbill/pages/network_management/model/request/device_port_bind_req.dart';
import 'package:savbill/pages/network_management/model/response/bind_port_device_res.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class DevicePortBind extends StatefulWidget {
  @override
  _DevicePortBindState createState() => _DevicePortBindState();
}

class _DevicePortBindState extends State<DevicePortBind> {
  final devicePortBindController = Get.put(DevicePortBindController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: devicePortBindController.isDataChange);
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<DevicePortBindController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: devicePortBindController.isLoading),
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
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: SingleChildScrollView(
          child: Padding(
              padding: const EdgeInsets.all(
                Constant.SCREEN_PADDING,
              ),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Form(
                      key: devicePortBindController.inPortFormKey,
                      autovalidateMode:
                          devicePortBindController.autoValidateModeIn,
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            InputTitleRequire(
                                title: devicePortBindController.deviceDetail !=
                                        null
                                    ? "Device Name :- ${devicePortBindController.deviceDetail!.name!}"
                                    : "",
                                require: false),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            CustomText(
                              title: "${Strings.parent_connection} :",
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w600,
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            IgnorePointer(
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
                                      child: Text(Strings.select_device_port,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value:
                                      devicePortBindController.selectedInPort,
                                  items: devicePortBindController.inPortList!
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
                                    devicePortBindController.selectedInPort =
                                        value as String?;
                                    devicePortBindController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        devicePortBindController
                                                .selectedInPort ==
                                            null) {
                                      return Strings.select_in_port;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            IgnorePointer(
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
                                      child: Text(Strings.select_parent_device,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: devicePortBindController
                                      .selectedInParentDevice,
                                  items: devicePortBindController
                                      .inParentDeviceList!
                                      .map((DeviceDetail value) {
                                    return DropdownMenuItem<DeviceDetail>(
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
                                    devicePortBindController
                                            .selectedInParentDevice =
                                        value as DeviceDetail?;
                                    devicePortBindController.update();
                                    devicePortBindController
                                        .checkInPortAvailability(2, "OUT");
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            IgnorePointer(
                              ignoring: devicePortBindController
                                          .selectedInParentDevice !=
                                      null
                                  ? false
                                  : true,
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
                                      fillColor: devicePortBindController
                                                  .selectedInParentDevice !=
                                              null
                                          ? AppTheme.colorWhite
                                          : Colors.black12),
                                  hint: Align(
                                      alignment: Alignment.centerLeft,
                                      child: Text(Strings.select_parent_device,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: devicePortBindController
                                      .selectedInPortForBind,
                                  items: devicePortBindController
                                      .inPortForBindList!
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
                                    devicePortBindController
                                            .selectedInPortForBind =
                                        value as String?;
                                    devicePortBindController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        devicePortBindController
                                                .selectedInPortForBind ==
                                            null) {
                                      return Strings.select_parent_device;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            Align(
                                alignment: Alignment.centerRight,
                                child: InkWell(
                                  onTap: () {
                                    validateForm("in");
                                  },
                                  child: CustomText(
                                    title:
                                        "+ Add (${devicePortBindController.inPortList!.length})",
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w600,
                                  ),
                                )),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            (devicePortBindController.inPortBindData != null &&
                                    devicePortBindController
                                        .inPortBindData!.isNotEmpty)
                                ? ListView.builder(
                                    physics:
                                        const NeverScrollableScrollPhysics(),
                                    shrinkWrap: true,
                                    itemCount: devicePortBindController
                                        .inPortBindData!.length,
                                    itemBuilder:
                                        (BuildContext context, int index) {
                                      BindPortDeviceDetail item =
                                          devicePortBindController
                                              .inPortBindData![index];
                                      return Container(
                                          margin: EdgeInsets.only(
                                              top: index == 0
                                                  ? 0
                                                  : Constant
                                                      .VERY_SMALL_PADDING),
                                          child: BindPortItem(
                                            item: item,
                                          ));
                                    })
                                : Container(),
                          ]),
                    ),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    Form(
                      key: devicePortBindController.outPortFormKey,
                      autovalidateMode:
                          devicePortBindController.autoValidateModeOut,
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            CustomText(
                              title: "${Strings.child_connection} :",
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w600,
                            ),
                            const SizedBox(
                              height: Constant.SMALL_PADDING,
                            ),
                            IgnorePointer(
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
                                      child: Text(Strings.select_device_port,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value:
                                      devicePortBindController.selectedOutPort,
                                  items: devicePortBindController.outPortList!
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
                                    devicePortBindController.selectedOutPort =
                                        value as String?;
                                    devicePortBindController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        devicePortBindController
                                                .selectedOutPort ==
                                            null) {
                                      return Strings.select_device_port;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            IgnorePointer(
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
                                      child: Text(Strings.select_child_device,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: devicePortBindController
                                      .selectedOutParentDevice,
                                  items: devicePortBindController
                                      .outParentDeviceList!
                                      .map((DeviceDetail value) {
                                    return DropdownMenuItem<DeviceDetail>(
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
                                    devicePortBindController
                                            .selectedOutParentDevice =
                                        value as DeviceDetail?;
                                    devicePortBindController.update();
                                    devicePortBindController
                                        .checkInPortAvailability(4, "IN");
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            IgnorePointer(
                              ignoring: devicePortBindController
                                          .selectedOutParentDevice !=
                                      null
                                  ? false
                                  : true,
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
                                      fillColor: devicePortBindController
                                                  .selectedOutParentDevice !=
                                              null
                                          ? AppTheme.colorWhite
                                          : Colors.black12),
                                  hint: Align(
                                      alignment: Alignment.centerLeft,
                                      child: Text(Strings.select_child_device,
                                          style: TextStyle(
                                            fontSize: AppTheme.medium,
                                            color: AppTheme.colorIconGrey,
                                            fontFamily: AppTheme.appFontName,
                                          ))),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: devicePortBindController
                                      .selectedOutPortForBind,
                                  items: devicePortBindController
                                      .outPortForBindList!
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
                                    devicePortBindController
                                            .selectedOutPortForBind =
                                        value as String?;
                                    devicePortBindController.update();
                                  },
                                  validator: (value) {
                                    if (value == null ||
                                        devicePortBindController
                                                .selectedOutPortForBind ==
                                            null) {
                                      return Strings.select_in_port;
                                    }
                                    return null;
                                  },
                                ),
                              ),
                            ),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            Align(
                                alignment: Alignment.centerRight,
                                child: InkWell(
                                  onTap: () {
                                    validateForm("out");
                                  },
                                  child: CustomText(
                                    title:
                                        "+ Add (${devicePortBindController.outPortList!.length})",
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w600,
                                  ),
                                )),
                            const SizedBox(
                              height: Constant.MEDIUM_PADDING,
                            ),
                            (devicePortBindController.outPortBindData != null &&
                                    devicePortBindController
                                        .outPortBindData!.isNotEmpty)
                                ? ListView.builder(
                                    physics:
                                        const NeverScrollableScrollPhysics(),
                                    shrinkWrap: true,
                                    itemCount: devicePortBindController
                                        .outPortBindData!.length,
                                    itemBuilder:
                                        (BuildContext context, int index) {
                                      BindPortDeviceDetail item =
                                          devicePortBindController
                                              .outPortBindData![index];
                                      return Container(
                                          margin: EdgeInsets.only(
                                              top: index == 0
                                                  ? 0
                                                  : Constant
                                                      .VERY_SMALL_PADDING),
                                          child: BindPortItem(
                                            item: item,
                                          ));
                                    })
                                : Container(),
                          ]),
                    ),
                  ])),
        ),
      ),
    );
  }

  validateForm(String type) {
    if (type.equalsIgnoreCase("IN")) {
      if (devicePortBindController.inPortFormKey.currentState!.validate()) {
        setState(() {
          devicePortBindController.autoValidateModeOut =
              AutovalidateMode.disabled;
          devicePortBindController.update();
        });
        InOutPortDevices item = InOutPortDevices(
            inBind: (devicePortBindController.selectedInPort != null &&
                    devicePortBindController.selectedInPort!.isNotEmpty)
                ? devicePortBindController.selectedInPort
                : "",
            outBind: (devicePortBindController.selectedInPortForBind != null &&
                    devicePortBindController.selectedInPortForBind!.isNotEmpty)
                ? devicePortBindController.selectedInPortForBind
                : "",
            parentDeviceId:
                (devicePortBindController.selectedInParentDevice != null)
                    ? devicePortBindController.selectedInParentDevice!.id
                    : null,
            flag: false);
        devicePortBindController.updateDevicePortBind(type, item);
      } else {
        devicePortBindController.autoValidateModeIn =
            AutovalidateMode.onUserInteraction;
        devicePortBindController.update();
      }
    }

    if (type.equalsIgnoreCase("OUT")) {
      if (devicePortBindController.outPortFormKey.currentState!.validate()) {
        setState(() {
          devicePortBindController.autoValidateModeIn =
              AutovalidateMode.disabled;
          devicePortBindController.update();
        });

        InOutPortDevices item = InOutPortDevices(
            inBind: (devicePortBindController.selectedOutPort != null &&
                    devicePortBindController.selectedOutPort!.isNotEmpty)
                ? devicePortBindController.selectedOutPort
                : "",
            outBind: (devicePortBindController.selectedOutPortForBind != null &&
                    devicePortBindController.selectedOutPortForBind!.isNotEmpty)
                ? devicePortBindController.selectedOutPortForBind
                : "",
            parentDeviceId:
                (devicePortBindController.selectedOutParentDevice != null)
                    ? devicePortBindController.selectedOutParentDevice!.id
                    : null,
            flag: false);
        devicePortBindController.updateDevicePortBind(type, item);
      } else {
        devicePortBindController.autoValidateModeOut =
            AutovalidateMode.onUserInteraction;
        devicePortBindController.update();
      }
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.parent_device_mapping,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
