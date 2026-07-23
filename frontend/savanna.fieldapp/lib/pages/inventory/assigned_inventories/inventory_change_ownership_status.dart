import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_ownership_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_ownership_item.dart';
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

class InventoryChangeOwnershipStatus extends StatefulWidget {
  @override
  _ChangeOwnershipStatusState createState() => _ChangeOwnershipStatusState();
}

class _ChangeOwnershipStatusState
    extends State<InventoryChangeOwnershipStatus> {
  final inventoryChangeOwnershipController =
      Get.put(InventoryChangeOwnershipController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back(result: true);
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryChangeOwnershipController>(
        builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: inventoryChangeOwnershipController.isLoading),
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
              child:
                  (inventoryChangeOwnershipController.inventoryList != null &&
                          inventoryChangeOwnershipController
                              .inventoryList!.isNotEmpty)
                      ? ListView.builder(
                          scrollDirection: Axis.vertical,
                          itemCount: inventoryChangeOwnershipController
                              .inventoryList!.length,
                          itemBuilder: (context, index) {
                            InventoryListDetail item =
                                inventoryChangeOwnershipController
                                    .inventoryList![index];
                            return InventoryChangeOwnershipItem(
                                item: item,
                                index: index,
                                inventoryChangeOwnershipController:
                                    inventoryChangeOwnershipController);
                          })
                      : noDataFound(),
            ),
            inventoryChangeOwnershipController.inventoryList != null &&
                    inventoryChangeOwnershipController.inventoryList!.isNotEmpty
                ? Row(children: [
                    Expanded(
                      child: SimpleButton(
                        onTap: () {
                          bool valid = true;
                          for (var element in inventoryChangeOwnershipController
                              .inventoryList!) {
                            if (element.selectedOwnershipStatus == null ||
                                (element.ownerShipRemarks == null ||
                                    element.ownerShipRemarks!.isEmpty)) {
                              valid = false;
                              break;
                            }
                          }
                          if (valid) {
                            inventoryChangeOwnershipController
                                .changeOwnershipStatusRequest();
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                Strings.please_select_ownership_status,
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
        Strings.change_item_ownership_description,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
