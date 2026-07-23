import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_status_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_status_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_warranty_change_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_warranty_change_item.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InventoryChangeStatus extends StatefulWidget {
  @override
  _InventoryChangeStatusState createState() =>
      _InventoryChangeStatusState();
}

class _InventoryChangeStatusState extends State<InventoryChangeStatus> {
  final inventoryChangeStatusController =
      Get.put(InventoryChangeStatusController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back(result: true);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryChangeStatusController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: inventoryChangeStatusController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SCREEN_PADDING,
            ),
            Expanded(
              flex: 1,
              child: (inventoryChangeStatusController.inventoryList != null &&
                  inventoryChangeStatusController
                          .inventoryList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount: inventoryChangeStatusController
                          .inventoryList!.length,
                      itemBuilder: (context, index) {
                        InventoryListDetail item =
                        inventoryChangeStatusController
                                .inventoryList![index];
                        return InventoryChangeStatusItem(
                            item: item,
                            index: index,
                            inventoryChangeStatusController:
                            inventoryChangeStatusController);
                      })
                  : noDataFound(),
            ),
            inventoryChangeStatusController.inventoryList != null &&
                inventoryChangeStatusController.inventoryList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          bool valid = true;
                          for (var element in inventoryChangeStatusController
                              .inventoryList!) {
                            if (element.selectedItemStatus== null) {
                              valid = false;
                              break;
                            }
                          }
                          if (valid) {
                            inventoryChangeStatusController
                                .itemStatusRequest();
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                Strings.please_select_status,
                                AppTheme.colorWhite,
                                AppTheme.colorRed);
                          }
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.save,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w400,
                        ),
                      ),
                    ),
                  ])
                : Container(),
          ]),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.change_item_status_description,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
