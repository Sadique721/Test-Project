import 'package:savbill/pages/inventory/assigned_inventories/Inventory_request_assigned_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/fulfilment/inventory_fulfilment.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_req_product_item.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../widgets/dynamic_appbar.dart';
import '../../../widgets/progress_bar.dart';
import '../../../widgets/title_widge.dart';

class InventoryRequestAssigned extends StatefulWidget {
  @override
  _InventoryRequestAssignedState createState() =>
      _InventoryRequestAssignedState();
}

class _InventoryRequestAssignedState extends State<InventoryRequestAssigned> {
  final inventoryRequestFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final inventoryRequestAssignedController =
      Get.put(InventoryRequestAssignedController());

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
      child:
          GetBuilder<InventoryRequestAssignedController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: inventoryRequestAssignedController.isLoading),
        ]);
      }), /**/
    );
  }

  _body() {
    Color? statusColor;
    if (inventoryRequestAssignedController.fulfilmentData != null) {
      if (inventoryRequestAssignedController.fulfilmentData!.status!
          .equalsIgnoreCase(Strings.approve)) {
        statusColor = AppTheme.statusApprove;
      } else {
        statusColor = AppTheme.statusReject;
      }
    }
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
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
                      key: inventoryRequestFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Stack(
                            children: [
                              Container(
                                padding: const EdgeInsets.all(10),
                                margin:
                                    const EdgeInsets.only(top: 12, bottom: 12),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 1.0,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorIconGrey)),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      children: [
                                        Row(
                                          children: [
                                            CustomText(
                                              title:
                                                  "${Strings.requester_id} : ",
                                              colors: AppTheme.title_dark,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w600,
                                              maxLines: 1,
                                            ),
                                            CustomText(
                                              title: inventoryRequestAssignedController
                                                          .fulfilmentData !=
                                                      null
                                                  ? inventoryRequestAssignedController
                                                      .fulfilmentData!
                                                      .requestInventoryName
                                                  : "",
                                              colors: AppTheme.colorLightBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w400,
                                              maxLines: 1,
                                            ),
                                          ],
                                        ),
                                        Padding(
                                          padding: const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.VERY_SMALL_PADDING,
                                              vertical:
                                                  Constant.VERY_SMALL_PADDING),
                                          child: Container(
                                            padding: const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.SMALL_PADDING,
                                                vertical: Constant
                                                    .VERY_SMALL_PADDING),
                                            decoration: BoxDecoration(
                                                borderRadius:
                                                    BorderRadius.circular(
                                                        Constant.LARGE_PADDING),
                                                color: statusColor),
                                            child: CustomText(
                                                title: inventoryRequestAssignedController
                                                            .fulfilmentData !=
                                                        null
                                                    ? inventoryRequestAssignedController
                                                        .fulfilmentData!.status!
                                                    : "-",
                                                colors: AppTheme.colorWhite,
                                                textAlign: TextAlign.start,
                                                fontSize: AppTheme.small,
                                                maxLines: 2,
                                                height: 1,
                                                fontWeight: FontWeight.w500),
                                          ),
                                        ),
                                      ],
                                    ),
                                    Divider(
                                      color: AppTheme.title_dark,
                                      thickness: 0.5,
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.start,
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        children: [
                                          Expanded(
                                            flex: 1,
                                            child: basicDetailItem(
                                                Strings.request_to,
                                                inventoryRequestAssignedController
                                                            .fulfilmentData !=
                                                        null
                                                    ? inventoryRequestAssignedController
                                                        .fulfilmentData!
                                                        .requestToName
                                                    : "",
                                                Strings.on_behalf_of,
                                                inventoryRequestAssignedController
                                                            .fulfilmentData !=
                                                        null
                                                    ? inventoryRequestAssignedController
                                                        .fulfilmentData!
                                                        .onBehalfOf
                                                    : ""),
                                          ),
                                        ]),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.start,
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        children: [
                                          Expanded(
                                            flex: 1,
                                            child: basicDetailItem(
                                              Strings.reason,
                                              inventoryRequestAssignedController
                                                          .fulfilmentData !=
                                                      null
                                                  ? inventoryRequestAssignedController
                                                      .fulfilmentData!.reason
                                                  : "",
                                              Strings.remarks,
                                              inventoryRequestAssignedController
                                                          .fulfilmentData !=
                                                      null
                                                  ? inventoryRequestAssignedController
                                                      .fulfilmentData!.remarks
                                                  : "",
                                            ),
                                          ),
                                        ]),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(3),
                                      color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.basic_details,
                                      require: false),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),
                          Stack(
                            children: [
                              Container(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.MEDIUM_PADDING,
                                    horizontal: Constant.MEDIUM_PADDING),
                                margin: const EdgeInsets.only(
                                    top: Constant.EXPANTABLE_ITEM_MARGIN,
                                    bottom: Constant.SMALL_PADDING),
                                decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(5),
                                    border: Border.all(
                                        width: 0.8,
                                        style: BorderStyle.solid,
                                        color: AppTheme.colorGrey)),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    ListView.builder(
                                        scrollDirection: Axis.vertical,
                                        shrinkWrap: true,
                                        itemCount:
                                            inventoryRequestAssignedController
                                                .fulfilmentProductMapping!
                                                .length,
                                        itemBuilder: (context, index) {
                                          FulfilmentProductMappings item =
                                              inventoryRequestAssignedController
                                                      .fulfilmentProductMapping![
                                                  index];
                                          return InventoryReqProductItem(
                                            item: item,
                                            index: index,
                                          );
                                        }),
                                    // : noDataFound(),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Center(
                                      child: SimpleButton(
                                        onTap: () {
                                          openInventoryFulFilMentScreen(inventoryRequestAssignedController.fulfilmentData!);
                                          // validateForm();
                                        },
                                        radius: 10,
                                        height: Constant.BTN_HEIGHT_M,
                                        bgColors: AppTheme.colorPrimary,
                                        borderColors: AppTheme.colorPrimary,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        child: CustomText(
                                          title: Strings.click_to_fulfill,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w400,
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 30,
                                child: Container(
                                  padding: const EdgeInsets.all(4),
                                  decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(3),
                                      color: Colors.white),
                                  child: InputTitleRequire(
                                      title: Strings.product_details,
                                      require: false),
                                ),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.inventory_request, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
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
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  openInventoryFulFilMentScreen(FulfilmentData fulfilmentData) async {
    Get.to(InventoryFulFilMent(), arguments: {
      Constant.FUL_FILL_MENT: fulfilmentData,
    });
  }

}

noDataFound() {
  return const NoDataFound();
}
