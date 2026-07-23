import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_inventory/external_inventory/external_inventory_controller.dart';
import 'package:savbill/pages/customer_inventory/history_inventory/cust_inventory_history_controller.dart';
import 'package:savbill/pages/customer_inventory/response/external_inv_product_customer_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_externalItem_product_staff_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_customer_inventory_details_history_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_mac_mapping_external_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CustInventoryHistory extends StatefulWidget {
  @override
  _CustInventoryHistoryState createState() => _CustInventoryHistoryState();
}

class _CustInventoryHistoryState extends State<CustInventoryHistory> {
  final custInventoryHistoryController =
      Get.put(CustInventoryHistoryController());
  final inventoryHistoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final GlobalKey<ScaffoldState> scaffoldKey = GlobalKey<ScaffoldState>();

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
      child: GetBuilder<CustInventoryHistoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            key: scaffoldKey,
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(controller),
          ),
          ProgressBar(isLoader: controller.isLoading),
        ]);
      }),
    );
  }

  _body(CustInventoryHistoryController controller) {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            /* Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                          title: "${controller.customerName} ${Strings.inventory_list}",
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                ],
              ),
            ),*/
            Expanded(
              flex: 1,
              child: (controller.inventoryHistoryDataList != null &&
                      controller.inventoryHistoryDataList!.isNotEmpty)
                  ? ListView.builder(
                      controller: controller.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      controller.inventoryHistoryDataList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            controller.inventoryHistoryDataList?.length) {
                          if (controller.isShowLoadMore) {
                            return Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Center(
                                child: SizedBox(
                                  width: Constant.SCREEN_PADDING,
                                  height: Constant.SCREEN_PADDING,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.5,
                                    valueColor: AlwaysStoppedAnimation<Color>(
                                        AppTheme.colorProgress),
                                    backgroundColor: AppTheme.colorProgressBg,
                                  ),
                                ),
                              ),
                            );
                          } else {
                            return Container();
                          }
                        } else {
                          InventoryHistoryDataList item = controller.inventoryHistoryDataList![index];
                          return InventoryHistoryViewItem(
                            item: item,
                            index: index,
                          );
                        }
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.cust_inventory_history,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}

InventoryHistoryViewItem({required InventoryHistoryDataList item, required int index}) {

  return Container(
    margin: const EdgeInsets.only(
      left: Constant.SPACE_BW_FIELD,
      right: Constant.SPACE_BW_FIELD,
      bottom: Constant.MEDIUM_PADDING,
      top: Constant.MEDIUM_PADDING,
    ),
    child: Material(
      color: AppTheme.colorWhite,
      elevation: 0.5,
      shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
      child: Padding(
        padding: const EdgeInsets.only(left:Constant.MEDIUM_PADDING,right:Constant.MEDIUM_PADDING,top: Constant.SMALL_PADDING,bottom: Constant.SMALL_PADDING),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            basicDetailItem(
                Strings.inventory_type,
                item.condition ?? "-",
                Strings.service_name,
                item.serviceName ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            basicDetailItem(
                Strings.connection_no,
                item.connectionNo ?? "-",
                Strings.mac_address,
                item.macAddress ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            basicDetailItem(
                Strings.event_date,
                item.startDate ?? "-",
                Strings.serial_no,
                item.serialNumber ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            basicDetailItem(
                Strings.plan_name,
                item.postPaidPlanName ?? "-",
                Strings.external_item_groups,
                item.externalItemGroupNumber ?? "-"),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            basicDetailItem(
                Strings.event_type,
                item.event ?? "-",
                Strings.remarks,
                item.approvalRemark ?? "-"),
          ],
        ),
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
    colors: AppTheme.title_dark,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w700,
    maxLines: 2,
  );
}

valueWidget(String? value) {
  return CustomText(
    title: value!.isNotEmpty ? value : "-",
    colors: AppTheme.lable_noramal,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small + 1,
    fontWeight: FontWeight.w400,
    maxLines: 2,
  );
}

