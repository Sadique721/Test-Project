import 'package:savbill/pages/inventory/assigned_inventories/inventory_item_return_view.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_return_item_controller.dart';
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

class InventoryReturnItem extends StatefulWidget {
  @override
  _InventoryReturnItemState createState() => _InventoryReturnItemState();
}

class _InventoryReturnItemState extends State<InventoryReturnItem> {
  final inventoryReturnItemController =
      Get.put(InventoryReturnItemController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back(result: true);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryReturnItemController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: inventoryReturnItemController.isLoading),
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
            /*Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  CustomText(
                      title: customerDiscountController.customerName,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                ],
              ),
            ),*/
            const SizedBox(
              height: Constant.SCREEN_PADDING,
            ),
            Expanded(
              flex: 1,
              child: (inventoryReturnItemController.inventoryList != null &&
                      inventoryReturnItemController.inventoryList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          inventoryReturnItemController.inventoryList!.length,
                      itemBuilder: (context, index) {
                        InventoryListDetail item =
                            inventoryReturnItemController.inventoryList![index];
                        return InventoryReturnItemView(
                            item: item,
                            index: index,
                            inventoryReturnItemController:
                                inventoryReturnItemController);
                      })
                  : noDataFound(),
            ),
            inventoryReturnItemController.inventoryList != null &&
                    inventoryReturnItemController.inventoryList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          bool valid = true;
                          for (var element
                              in inventoryReturnItemController.inventoryList!) {
                            if (element.remarks == null ||
                                element.remarks!.isEmpty) {
                              valid = false;
                              break;
                            }
                          }
                          if (valid) {
                            inventoryReturnItemController.returnItemRequest();
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                Strings.please_enter_remarks,
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
        Strings.return_item_description,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
