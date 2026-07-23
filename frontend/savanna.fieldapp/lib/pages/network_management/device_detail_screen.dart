import 'package:savbill/pages/network_management/device_detail_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class DeviceDetailScreen extends StatefulWidget {
  @override
  _DeviceDetailState createState() => _DeviceDetailState();
}

class _DeviceDetailState extends State<DeviceDetailScreen> {
  final deviceDetailController = Get.put(DeviceDetailController());

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
      child: GetBuilder<DeviceDetailController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: deviceDetailController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return Container(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
      ),
      color: AppTheme.colorBG,
      child: deviceDetailController.deviceDetail != null
          ? SingleChildScrollView(
              physics: const ScrollPhysics(),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  basicDetailView(),
                ],
              ),
            )
          : noDataFound(),
    );
  }

  basicDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: deviceDetailController.title,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      basicDetailItem(
                          Strings.name,
                          deviceDetailController.deviceDetail!.name != null &&
                                  deviceDetailController
                                      .deviceDetail!.name!.isNotEmpty
                              ? deviceDetailController.deviceDetail!.name!
                              : "-",
                          Strings.status,
                          deviceDetailController.deviceDetail!.status != null &&
                                  deviceDetailController
                                      .deviceDetail!.status!.isNotEmpty
                              ? deviceDetailController.deviceDetail!.status!
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.parent_network_device,
                          "-",
                          Strings.device_type,
                          deviceDetailController.deviceDetail!.devicetype !=
                                      null &&
                                  deviceDetailController
                                      .deviceDetail!.devicetype!.isNotEmpty
                              ? deviceDetailController.deviceDetail!.devicetype!
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.total_in_port,
                          deviceDetailController.deviceDetail!.totalInPorts !=
                                  null
                              ? deviceDetailController
                                  .deviceDetail!.totalInPorts!
                                  .toString()
                              : "-",
                          Strings.total_out_port,
                          deviceDetailController.deviceDetail!.totalOutPorts !=
                                  null
                              ? deviceDetailController
                                  .deviceDetail!.totalOutPorts!
                                  .toString()
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.available_in_ports,
                          deviceDetailController
                                      .deviceDetail!.availableInPorts !=
                                  null
                              ? deviceDetailController
                                  .deviceDetail!.availableInPorts!
                                  .toString()
                              : "-",
                          Strings.available_out_ports,
                          deviceDetailController
                                      .deviceDetail!.availableOutPorts !=
                                  null
                              ? deviceDetailController
                                  .deviceDetail!.availableOutPorts!
                                  .toString()
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.latitude,
                          deviceDetailController.deviceDetail!.latitude !=
                                      null &&
                                  deviceDetailController
                                      .deviceDetail!.latitude!.isNotEmpty
                              ? deviceDetailController.deviceDetail!.latitude!
                              : "-",
                          Strings.longitude,
                          deviceDetailController.deviceDetail!.longitude !=
                                      null &&
                                  deviceDetailController
                                      .deviceDetail!.longitude!.isNotEmpty
                              ? deviceDetailController.deviceDetail!.longitude!
                              : "-"),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.service_area,
                          deviceDetailController.serviceAreaName.isNotEmpty
                              ? deviceDetailController.serviceAreaName
                              : "-",
                          "",
                          ""),
                      // Strings.hierarchy, "Click here"
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              InkWell(
                onTap: title2.equalsIgnoreCase(Strings.hierarchy)
                    ? () {
                        Utils.showSnackbar(
                            Strings.SUCCESS,
                            Strings.under_development,
                            AppTheme.colorWhite,
                            AppTheme.colorGreen);
                      }
                    : null,
                child: valueWidget(value2),
              ),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.device_detail,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
