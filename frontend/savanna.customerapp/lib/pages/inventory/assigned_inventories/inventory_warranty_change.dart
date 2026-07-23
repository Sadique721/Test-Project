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

class InventoryWarrantyChange extends StatefulWidget {
  @override
  _InventoryWarrantyChangeState createState() =>
      _InventoryWarrantyChangeState();
}

class _InventoryWarrantyChangeState extends State<InventoryWarrantyChange> {
  final inventoryWarrantyChangeController =
      Get.put(InventoryWarrantyChangeController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back(result: true);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryWarrantyChangeController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: inventoryWarrantyChangeController.isLoading),
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
              child: (inventoryWarrantyChangeController.inventoryList != null &&
                      inventoryWarrantyChangeController
                          .inventoryList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount: inventoryWarrantyChangeController
                          .inventoryList!.length,
                      itemBuilder: (context, index) {
                        InventoryListDetail item =
                            inventoryWarrantyChangeController
                                .inventoryList![index];
                        return InventoryWarrantyChangeItem(
                            item: item,
                            index: index,
                            inventoryWarrantyChangeController:
                                inventoryWarrantyChangeController);
                      })
                  : noDataFound(),
            ),
            inventoryWarrantyChangeController.inventoryList != null &&
                    inventoryWarrantyChangeController.inventoryList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          bool valid = true;
                          for (var element in inventoryWarrantyChangeController
                              .inventoryList!) {
                            if (element.selectedWarranty == null) {
                              valid = false;
                              break;
                            }
                          }
                          if (valid) {
                            inventoryWarrantyChangeController
                                .warrantyItemRequest();
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                Strings.please_select_warranty,
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
        Strings.change_item_warranty,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
