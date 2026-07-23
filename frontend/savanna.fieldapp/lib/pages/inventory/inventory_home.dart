import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventories.dart';
import 'package:savbill/pages/inventory/assigned_inventories/request_inventories.dart';
import 'package:savbill/pages/inventory/bulk_consumption/view_bulk_consumption.dart';
import 'package:savbill/pages/inventory/category/view_category.dart';
import 'package:savbill/pages/inventory/external_group/view_external_group.dart';
import 'package:savbill/pages/inventory/inventory_home_controller.dart';
import 'package:savbill/pages/inventory/inwards/view_inwards.dart';
import 'package:savbill/pages/inventory/manufacturer/view_manufacturer.dart';
import 'package:savbill/pages/inventory/outwards/view_outwards.dart';
import 'package:savbill/pages/inventory/pop/view_pop.dart';
import 'package:savbill/pages/inventory/product/view_product.dart';
import 'package:savbill/pages/inventory/warehouse/view_warehouse.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InventoryHome extends StatefulWidget {
  @override
  _InventoryHomeState createState() => _InventoryHomeState();
}

class _InventoryHomeState extends State<InventoryHome>
    implements LogoutClickEvent {
  final inventoryHomeController = Get.put(InventoryHomeController());
  final GlobalKey<ScaffoldState> inventoryHomeKey = GlobalKey();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    inventoryHomeController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<InventoryHomeController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: inventoryHomeKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: SafeArea(
                child: _body(),
              ),
            ),
          ),
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
        width: MediaQuery.of(context).size.width,
        padding:
            const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
        child: Column(children: <Widget>[
          const SizedBox(height: Constant.SCREEN_PADDING),
          Expanded(
            child: inventoryHomeController.dataList.isNotEmpty
                ? ListView.builder(
                    itemCount: inventoryHomeController.dataList.length,
                    itemBuilder: (BuildContext context, int index) {
                      ItemList data = inventoryHomeController.dataList[index];
                      String? icon = data.icon;
                      return Padding(
                        padding: EdgeInsets.only(
                          left: Constant.VERY_SMALL_PADDING,
                          right: Constant.VERY_SMALL_PADDING,
                          top: (index == 0) ? 0 : Constant.LARGE_PADDING,
                        ),
                        child: InkWell(
                          onTap: () {
                            if (data.id == 1) {
                              openInventoryManufacturerList();
                            } else if (data.id == 2) {
                              openInventoryCategoryList();
                            } else if (data.id == 3) {
                              openInventoryProductList();
                            } else if (data.id == 4) {
                              openInventoryPopList();
                            } else if (data.id == 5) {
                              openInventoryWareHouseList();
                            } else if (data.id == 6) {
                              openInventoryInwardsList();
                            } else if (data.id == 7) {
                              openInventoryOutwardsList();
                            } else if (data.id == 8) {
                              // Get.defaultDialog();
                              requestInventories();
                              // Get.defaultDialog();
                            } else if (data.id == 9) {
                              openExternalGroupList();
                            } else if (data.id == 10) {
                              openBulkConsumptionList();
                            } else if (data.id == 11) {
                              openInventoryRequestList();
                            }
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                  color:
                                      AppTheme.colorPrimary, // Set border color
                                  width: 1.0), // Set border width
                              borderRadius: const BorderRadius.all(
                                  Radius.circular(
                                      6.0)), // Set rounded corner radius
                            ),
                            child: IntrinsicHeight(
                              child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Container(
                                      width: Constant.BTN_HEIGHT,
                                      height: Constant.BTN_HEIGHT,
                                      padding: const EdgeInsets.all(
                                          Constant.MEDIUM_PADDING),
                                      decoration: BoxDecoration(
                                        color: AppTheme.colorPrimary,
                                        borderRadius: const BorderRadius.only(
                                          topLeft: Radius.circular(5.0),
                                          bottomLeft: Radius.circular(5.0),
                                        ),
                                      ),
                                      child: Image.asset(
                                        icon!,
                                        height: Constant.ICON_SIZE,
                                        width: Constant.ICON_SIZE,
                                      ),
                                    ),
                                    const SizedBox(
                                        width: Constant.SCREEN_PADDING),
                                    Align(
                                      child: CustomText(
                                          title: data.title,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w400,
                                          colors: AppTheme.colorBlack),
                                    )
                                  ]),
                            ),
                          ),
                        ),
                      );
                    },
                  )
                : noDataFound(),
          ),
          const SizedBox(height: Constant.SMALL_PADDING,),
        ]),
      ),
    );
  }
  openInventoryManufacturerList() async {
    var result = await Get.to(ViewManufacturer());
  }
  openInventoryCategoryList() async {
    var result = await Get.to(ViewCategory());
  }

  openInventoryProductList() async {
    var result = await Get.to(ViewProduct());
  }

  openInventoryPopList() async {
    var result = await Get.to(ViewPopList());
  }

  openInventoryWareHouseList() async {
    var result = await Get.to(ViewWareHouse());
  }

  openInventoryInwardsList() async {
    var result = await Get.to(ViewInwards());
  }

  openInventoryOutwardsList() async {
    var result = await Get.to(ViewOutwards());
  }

  openInventoryRequestList() async {
    var result = await Get.to(()=> AssignedInventories());
  }

  requestInventories() async {
    var result = await Get.to(RequestInventories());
  }

  openExternalGroupList() async {
    var result = await Get.to(ViewExternalGroup());
  }

  openBulkConsumptionList() async {
    var result = await Get.to(ViewBulkConsumption());
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (inventoryHomeKey.currentState!.isDrawerOpen) {
      inventoryHomeKey.currentState?.closeDrawer();
    } else {
      inventoryHomeKey.currentState?.openDrawer();
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.inventory_management,
        '',
        AppTheme.colorPrimary,
        true,
        _onMenuClick,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void logoutClick() {
    inventoryHomeController.getStorage.remove(Constant.USER_DATA);
    inventoryHomeController.getStorage.remove(Constant.USER_TOKEN);
    inventoryHomeController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      Get.offAllNamed(AppRoutes.DASHBOARD,
          arguments: {Constant.FROM: Strings.payment_system});
    }
  }
}
