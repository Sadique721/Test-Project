import 'package:savbill/pages/inventory/assigned_inventories/add_inventory_request.dart';
import 'package:savbill/pages/inventory/assigned_inventories/all_inventory_filter.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventory_item_request.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_ownership_status.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_status.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_type.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_return_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_warranty_change.dart';
import 'package:savbill/pages/inventory/assigned_inventories/request_inventory_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/request_inventory_item.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/pages/inventory/module/response/filter_data.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../module/response/inventory_request_list_res.dart';

class RequestInventories extends StatefulWidget {
  @override
  _AssignedInventoriesState createState() => _AssignedInventoriesState();
}

class _AssignedInventoriesState extends State<RequestInventories>
    with TickerProviderStateMixin {
  final requestInventoriesController = Get.put(RequestInventoryController());
  TabController? _tabController;

  List<Tab> myTabs = <Tab>[
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.inventory_request,
          textAlign: TextAlign.center,
        ),
      ),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.assigned_inventories_request,
          textAlign: TextAlign.center,
        ),
      ),
    ),
  ];

  @override
  void initState() {
    super.initState();
    _tabController =
        TabController(vsync: this, length: myTabs.length, initialIndex: 0)
          ..addListener(() {
            requestInventoriesController.tabIndex = _tabController!.index;
            requestInventoriesController.update();
          });
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<RequestInventoryController>(builder: (controller) {
      return Scaffold(
        floatingActionButton: requestInventoriesController.tabIndex == 0
            ? FloatingActionButton(
                onPressed: () {
                  openAddInventoryRequest();
                },
                backgroundColor: AppTheme.colorPrimary,
                hoverColor: AppTheme.colorPrimary,
                tooltip: 'Raised Inventory Request',
                elevation: 5,
                splashColor: AppTheme.colorBG,
                child: Icon(
                  Icons.add,
                  color: AppTheme.colorWhite,
                  size: 25,
                ),
              )
            : null,
        floatingActionButtonLocation: FloatingActionButtonLocation.endFloat,
        backgroundColor: AppTheme.colorBG,
        appBar: _appBar(),
        body: _body(),
      );
    });
  }

  _body() {
    return Stack(children: <Widget>[
      requestInventoriesController.isLoading
          ? ProgressBar(isLoader: requestInventoriesController.isLoading)
          : Container(
              color: AppTheme.colorBG,
              width: MediaQuery.of(context).size.width,
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    Container(
                      margin: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING,
                      ),
                      height: Constant.TABBAR_HEIGHT,
                      decoration: BoxDecoration(
                        color: AppTheme.colorTransparent,
                        border: Border(
                            bottom: BorderSide(
                                color: AppTheme.title_dark.withOpacity(0.9),
                                width: Constant.TABBAR_BOTTOM_LINE_H)),
                      ),
                      child: TabBar(
                        controller: _tabController,
                        unselectedLabelColor:
                            AppTheme.title_dark.withOpacity(0.8),
                        indicator: UnderlineTabIndicator(
                          borderSide: BorderSide(
                              width: Constant.TAB_INDICATOR_H,
                              color: AppTheme.title_dark),
                        ),
                        labelColor: AppTheme.title_dark,
                        labelStyle: const TextStyle(
                            fontSize: AppTheme.large,
                            fontWeight: FontWeight.w600),
                        unselectedLabelStyle: const TextStyle(
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w500),
                        tabs: myTabs,
                      ),
                    ),

                    /*requestInventoriesController.tabIndex == 0
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                    vertical: Constant.SMALL_PADDING,
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: "",
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    Row(children: [
                      InkWell(
                        onTap: () {
                          openOrderFilterScreen();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(
                                right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: requestInventoriesController
                                  .isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ])
                  ],
                ),
              )
                  : const SizedBox(
                height: Constant.SMALL_PADDING,
              ),*/

                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    Flexible(
                      child: TabBarView(
                        controller: _tabController,
                        children: [
                          _allInventoriesList(),

                          _assignedInventoriesRequestList(),
                          // _assignedInventoriesList(),
                          // _assignedCustomerList(),
                        ], //_tabsContainer(),
                      ),
                    ),
                  ]),
            ),
    ]);
  }

  openOrderFilterScreen() async {
    var result = await Get.to(
      AllInventoryFilter(),
    );
    if (result != null) {
      FilterData filterData = result;
      if (filterData != null &&
          filterData.identify!.equalsIgnoreCase(Strings.apply)) {
        requestInventoriesController.isFilterApply = true;
        requestInventoriesController.filterData = filterData;
        // requestInventoriesController.pageAllInventory = 1;
        requestInventoriesController.update();
        // requestInventoriesController.applyFilter();
      } else if (filterData != null &&
          filterData.identify!.equalsIgnoreCase(Strings.reset)) {
        requestInventoriesController.isFilterApply = false;
        requestInventoriesController.update();
        // requestInventoriesController.clearFilter();
      }
    }
  }

  _allInventoriesList() {
    return (requestInventoriesController.requestInventoryList != null &&
            requestInventoriesController.requestInventoryList!.isNotEmpty)
        ? ListView.builder(
            controller: requestInventoriesController.controller,
            padding: const EdgeInsets.symmetric(
              horizontal: Constant.EXTRA_LARGE_PADDING,
              //    vertical: Constant.SMALL_PADDING
            ),
            itemCount:
                requestInventoriesController.requestInventoryList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  requestInventoriesController.requestInventoryList?.length) {
                if (requestInventoriesController.isShowLoadMore) {
                  return Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
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
                InventroyRequestDataList item =
                    requestInventoriesController.requestInventoryList![index];
                return MyInventoryRequestItem(item: item, requestInventoryController: requestInventoriesController);
              }
            })
        : noDataFound();
  }

  _assignedInventoriesRequestList() {
    return (requestInventoriesController.assignedInventoryReqList != null &&
            requestInventoriesController.assignedInventoryReqList!.isNotEmpty)
        ? ListView.builder(
            controller: requestInventoriesController.assignedInvController,
            padding: const EdgeInsets.symmetric(
              horizontal: Constant.EXTRA_LARGE_PADDING,
            ),
            itemCount:
                requestInventoriesController.assignedInventoryReqList!.length +
                    1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  requestInventoriesController
                      .assignedInventoryReqList?.length) {
                if (requestInventoriesController.isShowLoadMore) {
                  return Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
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
                AssignedInventoryDataList item = requestInventoriesController
                    .assignedInventoryReqList![index];
                return MyAssignedInventoryRequestItem(
                    item: item,
                    requestInventoryController: requestInventoriesController);
              }
            })
        : noDataFound();
  }

  // validateStatusChange(int from) {
  //   // 1 for return item, 2 for change type, 3 for warranty, 4 for status, 5 for ownership
  //   List<InventoryListDetail>? selectedInventoryList = [];
  //   if (requestInventoriesController.allInventoryList != null &&
  //       requestInventoriesController.allInventoryList!.isNotEmpty) {
  //     requestInventoriesController.allInventoryList!.forEach((element) {
  //       if (element.selected != null && element.selected == true) {
  //         selectedInventoryList.add(element);
  //       }
  //     });
  //   }
  //
  //   if (selectedInventoryList.isNotEmpty) {
  //     if (from == 1) {
  //       openReturnItemScreen(selectedInventoryList);
  //     } else if (from == 2) {
  //       openItemTypeScreen(selectedInventoryList);
  //     } else if (from == 3) {
  //       openWarrantyChangesScreen(selectedInventoryList);
  //     } else if (from == 4) {
  //       openChangeStatusScreen(selectedInventoryList);
  //     } else if (from == 5) {
  //       openChangeOwnershipStatusScreen(selectedInventoryList);
  //     }
  //   } else {
  //     Utils.showSnackbar(Strings.INFO, "Please select at-least one inventory",
  //         AppTheme.colorBlack, AppTheme.colorBlueRView);
  //   }
  // }

  openReturnItemScreen(List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryReturnItem(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.update();
      // requestInventoriesController.clearFilter();
    }
  }



  openAddInventoryRequest() async {
    var result = await Get.to(AddInventoryRequest(), arguments: {});
    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.viewRequestInventroyList();
      requestInventoriesController.update();
    }
  }



  openItemTypeScreen(List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeType(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.update();
      // requestInventoriesController.clearFilter();
    }
  }

  openWarrantyChangesScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryWarrantyChange(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.update();
      // requestInventoriesController.clearFilter();
    }
  }

  openChangeStatusScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeStatus(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.update();
      // requestInventoriesController.clearFilter();
    }
  }

  openChangeOwnershipStatusScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeOwnershipStatus(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      requestInventoriesController.isFilterApply = false;
      requestInventoriesController.update();
      // requestInventoriesController.clearFilter();
    }
  }

  // _assignedInventoriesList() {
  //   return (requestInventoriesController.assignedInventoryList != null &&
  //       requestInventoriesController.assignedInventoryList!.isNotEmpty)
  //       ? ListView.builder(
  //       padding: const EdgeInsets.symmetric(
  //           horizontal: Constant.EXTRA_LARGE_PADDING,
  //           vertical: Constant.SMALL_PADDING),
  //       itemCount:
  //       requestInventoriesController.assignedInventoryList!.length + 1,
  //       controller: requestInventoriesController.controller,
  //       itemBuilder: (BuildContext context, int index) {
  //         if (index ==
  //             requestInventoriesController.assignedInventoryList?.length) {
  //           if (requestInventoriesController.isShowLoadMore) {
  //             return Padding(
  //               padding: const EdgeInsets.all(Constant.SMALL_PADDING),
  //               child: Center(
  //                 child: SizedBox(
  //                   width: Constant.SCREEN_PADDING,
  //                   height: Constant.SCREEN_PADDING,
  //                   child: CircularProgressIndicator(
  //                     strokeWidth: 2.5,
  //                     valueColor: AlwaysStoppedAnimation<Color>(
  //                         AppTheme.colorProgress),
  //                     backgroundColor: AppTheme.colorProgressBg,
  //                   ),
  //                 ),
  //               ),
  //             );
  //           } else {
  //             return Container();
  //           }
  //         } else {
  //           AssignedInventoryDetail item =
  //           requestInventoriesController.assignedInventoryList![index];
  //           return AssignedInventoriesItem(item: item);
  //         }
  //       })
  //       : noDataFound();
  // }
  //
  // _assignedCustomerList() {
  //   return (requestInventoriesController.assignedCustomerList != null &&
  //       requestInventoriesController.assignedCustomerList!.isNotEmpty)
  //       ? ListView.builder(
  //       controller: requestInventoriesController.controllerAssigned,
  //       padding: const EdgeInsets.symmetric(
  //           horizontal: Constant.EXTRA_LARGE_PADDING,
  //           vertical: Constant.SMALL_PADDING),
  //       // itemCount: requestInventoriesController.assignedCustomerList?.length + 1,
  //       itemCount: requestInventoriesController.assignedCustomerList!.length + 1,
  //       itemBuilder: (BuildContext context, int index) {
  //         if (index == requestInventoriesController.assignedCustomerList?.length) {
  //           if (requestInventoriesController.isShowLoadMore) {
  //             return Padding(
  //               padding: const EdgeInsets.all(Constant.SMALL_PADDING),
  //               child: Center(
  //                 child: SizedBox(
  //                   width: Constant.SCREEN_PADDING,
  //                   height: Constant.SCREEN_PADDING,
  //                   child: CircularProgressIndicator(
  //                     strokeWidth: 2.5,
  //                     valueColor: AlwaysStoppedAnimation<Color>(
  //                         AppTheme.colorProgress),
  //                     backgroundColor: AppTheme.colorProgressBg,
  //                   ),
  //                 ),
  //               ),
  //             );
  //           } else {
  //             return Container();
  //           }
  //         }else {
  //           InventoryAssignedCustomerDetail item = requestInventoriesController
  //               .assignedCustomerList![index];
  //           return AssignedCustomerInventoriesItem(item: item);
  //         }
  //       })
  //       : noDataFound();
  // }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.inventory_request_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
