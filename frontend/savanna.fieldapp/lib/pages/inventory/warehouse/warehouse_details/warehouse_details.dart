import 'package:savbill/pages/inventory/warehouse/warehouse_details/warehouse_details_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'teams_service_area_list_dialog.dart';

class WarehouseDetails extends StatefulWidget {
  @override
  _WarehouseDetailsState createState() => _WarehouseDetailsState();
}

class _WarehouseDetailsState extends State<WarehouseDetails> {
  final warehouseDetailsController = Get.put(WarehouseDetailsController());
  final inwardsDetailsFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<WarehouseDetailsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: warehouseDetailsController.isLoading),
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
            width: MediaQuery.of(context).size.width,
            height: MediaQuery.of(context).size.height,
            margin: const EdgeInsets.only(
              top: Constant.SMALL_PADDING,
            ),
            color: AppTheme.colorBG,
            child: SingleChildScrollView(
              physics: const ScrollPhysics(),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Container(
                      margin: const EdgeInsets.only(
                          top: Constant.SMALL_PADDING,
                          left: Constant.SCREEN_PADDING),
                      child: CustomText(
                        title:
                            "${warehouseDetailsController.warehouseName!.capitalizeFirst} ${Strings.ware_house.capitalizeFirst}",
                        fontSize: AppTheme.medium,
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    basicDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    locationDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    addressDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                  ]),
            )));
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
            title: Strings.basic_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                          Strings.ware_house_name,
                          warehouseDetailsController
                                  .warehouseDetailData?.name ??
                              "-",
                          Strings.ware_house_type,
                          warehouseDetailsController
                                  .warehouseDetailData?.warehouseType ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.status,
                          warehouseDetailsController
                                  .warehouseDetailData?.status ??
                              "-",
                          Strings.ware_house_code,
                          warehouseDetailsController
                                  .warehouseDetailData?.warehouseCode ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      // basicDetailItem(
                      //     Strings.teams,
                      //     Strings.click,
                      //     Strings.parent_service_area,
                      //     Strings.click,
                      //     null,
                      //     true,
                      //     false),
                      Row(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Flexible(
                            flex: 3,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.teams),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                InkWell(
                                  child: CustomText(
                                    title: Strings.click,
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small + 1,
                                    fontWeight: FontWeight.w400,
                                    decoration: TextDecoration.underline,
                                    maxLines: 1,
                                  ),
                                  onTap: () {
                                    showServicesAreaSelectionDialog(warehouseDetailsController.warehouseDetailData!.teamsList!,Strings.teams);
                                  },
                                ),
                              ],
                            ),
                          ),
                          Expanded(
                            flex: 2,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.parent_service_area),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),

                                InkWell(
                                  onTap: () {
                                    showServicesAreaSelectionDialog(warehouseDetailsController.warehouseDetailData!.parenetServiceAreaNameList!,Strings.parent_service_area);
                                  },
                                  child: CustomText(
                                    title: Strings.click,
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small + 1,
                                    fontWeight: FontWeight.w400,
                                    decoration: TextDecoration.underline,
                                    maxLines: 1,
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.description,
                          warehouseDetailsController
                                  .warehouseDetailData?.description ??
                              "-",
                          "",
                          "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  locationDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.location_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.location_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                          Strings.latitude,
                          warehouseDetailsController
                                  .warehouseDetailData?.latitude ??
                              "-",
                          Strings.longitude,
                          warehouseDetailsController
                                  .warehouseDetailData?.longitude ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  addressDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.address_detail),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.address_detail,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                      // basicDetailItem(
                      //     Strings.service_area_name,
                      //     Strings.click,
                      //     Strings.branch,
                      //     warehouseDetailsController
                      //             .warehouseDetailData?.barnchName ??
                      //         "-",
                      //     null,
                      //     false,
                      //     false),
                      Row(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Flexible(
                            flex: 3,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.service_area_name),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                InkWell(
                                  child: CustomText(
                                    title: Strings.click,
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small + 1,
                                    fontWeight: FontWeight.w400,
                                    decoration: TextDecoration.underline,
                                    maxLines: 1,
                                  ),
                                  onTap: () {
                                    showServicesAreaSelectionDialog(warehouseDetailsController.warehouseDetailData!.serviceAreaNameList!,Strings.service_area_name);
                                  },
                                ),
                              ],
                            ),
                          ),
                          Expanded(
                            flex: 2,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.branch,),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                valueWidget(warehouseDetailsController.warehouseDetailData?.barnchName ?? "-", false),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.address1,
                          warehouseDetailsController
                                  .warehouseDetailData?.address1 ??
                              "-",
                          Strings.address2,
                          warehouseDetailsController
                                  .warehouseDetailData?.address2 ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.pincode,
                          warehouseDetailsController
                                  .warehouseDetailData?.pincode ??
                              "-",
                          Strings.city,
                          warehouseDetailsController
                                  .warehouseDetailData?.city ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          warehouseDetailsController
                                  .warehouseDetailData?.state ??
                              "-",
                          Strings.country,
                          warehouseDetailsController
                                  .warehouseDetailData?.country ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  showServicesAreaSelectionDialog(List<dynamic> itemList, String from) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TeamServiceAreaListDialog(
            fromFor: from,
            itemList: itemList,
          );
        });
  }

  basicDetailItem(String title1, String? value1, String title2, String? value2,
      Function()? onTap1, bool? isLink1, bool? isLink2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              InkWell(
                child: valueWidget(value1, isLink1!),
                onTap: onTap1,
              ),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2, isLink2!),
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

  valueWidget(String? value, bool isLinkable) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }

  _appBar() {
    return DynamicAppBar(
        Strings.warehouse_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
