import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_type_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_type_item.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InventoryChangeType extends StatefulWidget {
  @override
  _InventoryChangeTypeState createState() => _InventoryChangeTypeState();
}

class _InventoryChangeTypeState extends State<InventoryChangeType> {
  final inventoryChangeTypeController =
      Get.put(InventoryChangeTypeController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back(result: true);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryChangeTypeController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: inventoryChangeTypeController.isLoading),
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
              child: (inventoryChangeTypeController.inventoryList != null &&
                      inventoryChangeTypeController.inventoryList!.isNotEmpty)
                  ? ListView.builder(
                      scrollDirection: Axis.vertical,
                      itemCount:
                          inventoryChangeTypeController.inventoryList!.length,
                      itemBuilder: (context, index) {
                        InventoryListDetail item = inventoryChangeTypeController.inventoryList![index];

                        return InventoryChangeTypeItem(
                            item: item,
                            index: index,
                            inventoryChangeTypeController:
                                inventoryChangeTypeController);
                      })
                  : noDataFound(),
            ),
            inventoryChangeTypeController.inventoryList != null &&
                    inventoryChangeTypeController.inventoryList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          bool valid = true;
                          for (var element
                              in inventoryChangeTypeController.inventoryList!) {
                            if (element.selectedItemType == null ||
                                element.selectedRemarkType == null ||
                                (element.selectedRemarkType != null &&
                                    element.selectedRemarkType!.id!
                                        .equalsIgnoreCase(Strings.other) &&
                                    (element.changeTypeRemarks == null ||
                                        element.changeTypeRemarks!.isEmpty))) {
                              valid = false;
                              break;
                            }
                          }
                          if (valid) {
                            inventoryChangeTypeController
                                .changeItemTypeRequest();
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                Strings.please_select_change_type_detail,
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
        Strings.change_item_type_description,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
