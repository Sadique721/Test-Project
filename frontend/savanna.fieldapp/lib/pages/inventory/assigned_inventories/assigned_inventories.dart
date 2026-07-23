import 'dart:developer';

import 'package:savbill/pages/inventory/assigned_inventories/all_inventories_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/all_inventory_filter.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_customer_inventories_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventories_controller.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_inventories_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_pop_inventories_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/assigned_service_area_inventory_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_ownership_status.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_status.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_change_type.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_return_item.dart';
import 'package:savbill/pages/inventory/assigned_inventories/inventory_warranty_change.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_detail_res.dart';
import 'package:savbill/pages/inventory/module/response/filter_data.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_customer_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_pop_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_service_area_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AssignedInventories extends StatefulWidget {
  @override
  _AssignedInventoriesState createState() => _AssignedInventoriesState();
}

class _AssignedInventoriesState extends State<AssignedInventories>
    with TickerProviderStateMixin {
  final assignedInventoriesController =
      Get.put(AssignedInventoriesController());
  TabController? _tabController;
  TabController? _subTabController;

  List<Tab> myTabs = <Tab>[
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.all_inventories,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.inventory_assigned_customer,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.inventory_assigned_pop,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.inventory_assigned_service_area,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
  ];

  List<Tab> mySubTabs = <Tab>[
    const Tab(
      child: Align(
          alignment: Alignment.center,
          child: Text(
            Strings.assigned_serialized_item,
            textAlign: TextAlign.center,
            style: TextStyle(
              fontSize: AppTheme.small,
              fontWeight: FontWeight.w500,
            ),
          )),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.assigned_non_serialized_item,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
  ];

  @override
  void initState() {
    super.initState();
    /* customerPlanController.isCallAllApi = true;
    customerPlanController.update();
    customerPlanController.getArgumentData();*/

    _tabController =
        TabController(vsync: this, length: myTabs.length, initialIndex: 0)
          ..addListener(() {
            assignedInventoriesController.tabIndex = _tabController!.index;
            assignedInventoriesController.update();
          });

    _subTabController =
        TabController(vsync: this, length: mySubTabs.length, initialIndex: 0)
          ..addListener(() {
            assignedInventoriesController.subTabIndex.value =
                _subTabController!.index;
            assignedInventoriesController.update();
          });
  }

  @override
  void didChangeDependencies() {}

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<AssignedInventoriesController>(builder: (controller) {
      return WillPopScope(
        onWillPop: () async{
          return true;
        },
        child: Scaffold(
          floatingActionButton: assignedInventoriesController.allInventoryList !=
                      null &&
                  assignedInventoriesController.allInventoryList!.isNotEmpty &&
                  assignedInventoriesController.tabIndex == 0
              ? SpeedDial(
                  icon: Icons.more_vert,
                  iconTheme: IconThemeData(color: AppTheme.colorWhite),
                  activeIcon: Icons.close,
                  curve: Curves.bounceIn,
                  overlayColor: AppTheme.colorBlack,
                  overlayOpacity: 0.0,
                  heroTag: 'float-btn',
                  backgroundColor: AppTheme.colorPrimary,
                  foregroundColor: AppTheme.colorPrimary,
                  elevation: 8.0,
                  shape: const CircleBorder(),
                  children: [
                      // SpeedDialChild(
                      //     child: SvgPicture.asset(
                      //       returnItemSvg,
                      //       height: Constant.ICON_SIZE_M,
                      //       width: Constant.ICON_SIZE_M,
                      //       color: AppTheme.colorWhite,
                      //       fit: BoxFit.fill,
                      //     ),
                      //     backgroundColor: AppTheme.colorBlack,
                      //     label: Strings.return_item,
                      //     labelBackgroundColor: AppTheme.statusClosedGreen,
                      //     labelStyle: TextStyle(
                      //         fontSize: AppTheme.small,
                      //         color: AppTheme.colorWhite),
                      //     onTap: () {
                      //       validateStatusChange(1);
                      //     }),
                      SpeedDialChild(
                          child: SvgPicture.asset(
                            changeTypeSvg,
                            height: Constant.ICON_SIZE_M,
                            width: Constant.ICON_SIZE_M,
                            color: AppTheme.colorWhite,
                            fit: BoxFit.fill,
                          ),
                          backgroundColor: AppTheme.colorBlack,
                          labelBackgroundColor: AppTheme.statusClosedGreen,
                          labelStyle: TextStyle(
                              fontSize: AppTheme.small,
                              color: AppTheme.colorWhite),
                          label: Strings.change_type,
                          onTap: () {
                            validateStatusChange(2);
                          }),
                      SpeedDialChild(
                          child: SvgPicture.asset(
                            warrantyStatusSvg,
                            height: Constant.ICON_SIZE_M,
                            width: Constant.ICON_SIZE_M,
                            color: AppTheme.colorWhite,
                            fit: BoxFit.fill,
                          ),
                          backgroundColor: AppTheme.colorBlack,
                          label: Strings.warranty,
                          labelBackgroundColor: AppTheme.statusClosedGreen,
                          labelStyle: TextStyle(
                              fontSize: AppTheme.small,
                              color: AppTheme.colorWhite),
                          onTap: () {
                            validateStatusChange(3);
                          }),
                      SpeedDialChild(
                          child: SvgPicture.asset(
                            statusItemSvg,
                            height: Constant.ICON_SIZE_M,
                            width: Constant.ICON_SIZE_M,
                            color: AppTheme.colorWhite,
                            fit: BoxFit.fill,
                          ),
                          backgroundColor: AppTheme.colorBlack,
                          label: Strings.status,
                          labelBackgroundColor: AppTheme.statusClosedGreen,
                          labelStyle: TextStyle(
                              fontSize: AppTheme.small,
                              color: AppTheme.colorWhite),
                          onTap: () {
                            validateStatusChange(4);
                          }),
                      SpeedDialChild(
                          child: SvgPicture.asset(
                            ownershipStatusSvg,
                            height: Constant.ICON_SIZE_M,
                            width: Constant.ICON_SIZE_M,
                            color: AppTheme.colorWhite,
                            fit: BoxFit.fill,
                          ),
                          backgroundColor: AppTheme.colorBlack,
                          label: Strings.ownership_status,
                          labelBackgroundColor: AppTheme.statusClosedGreen,
                          labelStyle: TextStyle(
                              fontSize: AppTheme.small,
                              color: AppTheme.colorWhite),
                          onTap: () {
                            validateStatusChange(5);
                          }),
                    ])
              : null,
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
      );
    });
  }

  _body() {
    return Stack(children: <Widget>[
      assignedInventoriesController.isLoading
          ? ProgressBar(isLoader: assignedInventoriesController.isLoading)
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
                            AppTheme.title_dark.withOpacity(0.6),
                        indicator: UnderlineTabIndicator(
                          borderSide: BorderSide(
                              width: Constant.TAB_INDICATOR_H,
                              color: AppTheme.title_dark),
                        ),
                        labelColor: AppTheme.title_dark,
                        labelStyle: const TextStyle(
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w600),
                        unselectedLabelStyle: const TextStyle(
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500),
                        tabs: myTabs,
                      ),
                    ),
                    assignedInventoriesController.tabIndex == 0
                        ? Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.VERY_SMALL_PADDING,
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
                                          color: assignedInventoriesController
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
                        : Padding(
                            padding: const EdgeInsets.symmetric(
                                // vertical: Constant.SMALL_PADDING,
                                horizontal: Constant.SCREEN_PADDING),
                            child: TabBar(
                              onTap: (value){
                               if(value == 0){
                                 assignedInventoriesController.getAssignedInventoryCustomerList(true);
                                 // assignedInventoriesController.getAssignedInventoryPopList(true);
                                 // assignedInventoriesController.getAssignedInventoryServiceAreaList(true);
                               }else{
                                 assignedInventoriesController.getAssignedInventoryCustomerList(false);
                                 // assignedInventoriesController.getAssignedInventoryPopList(false);
                                 // assignedInventoriesController.getAssignedInventoryServiceAreaList(false);
                               }
                              },
                              controller: _subTabController,
                              unselectedLabelColor:
                                  AppTheme.title_dark.withOpacity(0.6),
                              indicator: UnderlineTabIndicator(
                                borderSide: BorderSide(
                                    width: Constant.TAB_INDICATOR_H,
                                    color: AppTheme.title_dark),
                              ),
                              labelColor: AppTheme.title_dark,
                              labelStyle: const TextStyle(
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w600),
                              unselectedLabelStyle: const TextStyle(
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500),
                              tabs: mySubTabs,
                            )),
                    Flexible(
                      child: TabBarView(
                        controller: _tabController,
                        children: [
                          _allInventoriesList(),
                          _assignedInventoriesList(
                              Strings.inventory_assigned_customer),
                          _assignedInventoriesList(
                              Strings.inventory_assigned_pop),
                          _assignedInventoriesList(
                              Strings.inventory_assigned_service_area),
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
        assignedInventoriesController.isFilterApply = true;
        assignedInventoriesController.filterData = filterData;
        assignedInventoriesController.pageAllInventory = 1;
        assignedInventoriesController.update();
        assignedInventoriesController.applyFilter();
      } else if (filterData != null &&
          filterData.identify!.equalsIgnoreCase(Strings.reset)) {
        assignedInventoriesController.isFilterApply = false;
        assignedInventoriesController.update();
        assignedInventoriesController.clearFilter();
      }
    }
  }

  _allInventoriesList() {
    return (assignedInventoriesController.allInventoryList != null &&
            assignedInventoriesController.allInventoryList!.isNotEmpty)
        ? ListView.builder(
            controller: assignedInventoriesController.controllerAllInventory,
            padding: const EdgeInsets.symmetric(
              horizontal: Constant.EXTRA_LARGE_PADDING,
              //    vertical: Constant.SMALL_PADDING
            ),
            itemCount:
                assignedInventoriesController.allInventoryList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  assignedInventoriesController.allInventoryList?.length) {
                if (assignedInventoriesController.isShowLoadMore) {
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
                InventoryListDetail item =
                    assignedInventoriesController.allInventoryList![index];
                return AllInventoriesItem(
                  item: item,
                  onSelectChanged: (value) {
                    if (item.selected != null && item.selected!) {
                      item.selected = false;
                    } else {
                      item.selected = true;
                    }
                    assignedInventoriesController.update();
                  },
                );
              }
            })
        : noDataFound();
  }

  validateStatusChange(int from) {
    // 1 for return item, 2 for change type, 3 for warranty, 4 for status, 5 for ownership
    List<InventoryListDetail>? selectedInventoryList = [];
    if (assignedInventoriesController.allInventoryList != null &&
        assignedInventoriesController.allInventoryList!.isNotEmpty) {
      assignedInventoriesController.allInventoryList!.forEach((element) {
        if (element.selected != null && element.selected == true) {
          selectedInventoryList.add(element);
        }
      });
    }

    if (selectedInventoryList.isNotEmpty) {
      if (from == 1) {
        openReturnItemScreen(selectedInventoryList);
      } else if (from == 2) {
        openItemTypeScreen(selectedInventoryList);
      } else if (from == 3) {
        openWarrantyChangesScreen(selectedInventoryList);
      } else if (from == 4) {
        openChangeStatusScreen(selectedInventoryList);
      } else if (from == 5) {
        openChangeOwnershipStatusScreen(selectedInventoryList);
      }
    } else {
      Utils.showSnackbar(Strings.INFO, "Please select at-least one inventory",
          AppTheme.colorBlack, AppTheme.colorBlueRView);
    }
  }

  openReturnItemScreen(List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryReturnItem(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      assignedInventoriesController.isFilterApply = false;
      assignedInventoriesController.update();
      assignedInventoriesController.clearFilter();
    }
  }

  openItemTypeScreen(List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeType(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      assignedInventoriesController.isFilterApply = false;
      assignedInventoriesController.update();
      assignedInventoriesController.clearFilter();
    }
  }

  openWarrantyChangesScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryWarrantyChange(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      assignedInventoriesController.isFilterApply = false;
      assignedInventoriesController.update();
      assignedInventoriesController.clearFilter();
    }
  }

  openChangeStatusScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeStatus(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      assignedInventoriesController.isFilterApply = false;
      assignedInventoriesController.update();
      assignedInventoriesController.clearFilter();
    }
  }

  openChangeOwnershipStatusScreen(
      List<InventoryListDetail>? selectedInventoryList) async {
    var result = await Get.to(InventoryChangeOwnershipStatus(),
        arguments: {Constant.INVENTORY_ITEMS: selectedInventoryList});

    if (result != null && result == true) {
      assignedInventoriesController.isFilterApply = false;
      assignedInventoriesController.update();
      assignedInventoriesController.clearFilter();
    }
  }

  _assignedInventoriesList(String? typeInventory) {
    return TabBarView(
      controller: _subTabController,
      children: [
        typeInventory!.equalsIgnoreCase(Strings.inventory_assigned_customer)
            ? _assignedCustomerList()
            : (typeInventory.equalsIgnoreCase(Strings.inventory_assigned_pop)
                ? _assignedPopList()
                : (typeInventory.equalsIgnoreCase(
                        Strings.inventory_assigned_service_area)
                    ? _assignedServiceAreaList()
                    : _assignedCustomerList())),
        typeInventory.equalsIgnoreCase(Strings.inventory_assigned_customer)
            ? _assignedCustomerList()
            : (typeInventory.equalsIgnoreCase(Strings.inventory_assigned_pop)
                ? _assignedPopList()
                : (typeInventory.equalsIgnoreCase(
                        Strings.inventory_assigned_service_area)
                    ? _assignedServiceAreaList()
                    : _assignedCustomerList())),
      ], //_tabsContainer(),
    );
  }

  _assignedCustomerList() {
    return (assignedInventoriesController.assignedCustomerList != null &&
            assignedInventoriesController.assignedCustomerList!.isNotEmpty)
        ? ListView.builder(
            controller: assignedInventoriesController.controllerAssigned,
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            // itemCount: assignedInventoriesController.assignedCustomerList?.length + 1,
            itemCount:
                assignedInventoriesController.assignedCustomerList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  assignedInventoriesController.assignedCustomerList?.length) {
                if (assignedInventoriesController.isShowLoadMore) {
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
                InventoryAssignedCustomerDetail item =
                    assignedInventoriesController.assignedCustomerList![index];
                return AssignedCustomerInventoriesItem(item: item);
              }
            })
        : noDataFound();
  }

  _assignedPopList() {
    return (assignedInventoriesController.assignedPopList != null &&
            assignedInventoriesController.assignedPopList!.isNotEmpty)
        ? ListView.builder(
            controller: assignedInventoriesController.controllerPop,
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            // itemCount: assignedInventoriesController.assignedCustomerList?.length + 1,
            itemCount:
                assignedInventoriesController.assignedPopList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  assignedInventoriesController.assignedPopList?.length) {
                if (assignedInventoriesController.isShowLoadMore) {
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
                InventoryAssignedPopDataList item =
                    assignedInventoriesController.assignedPopList![index];
                return AssignedPopInventoriesItem(item: item);
              }
            })
        : noDataFound();
  }

  _assignedServiceAreaList() {
    return (assignedInventoriesController.assignedServiceList != null &&
            assignedInventoriesController.assignedServiceList!.isNotEmpty)
        ? ListView.builder(
            controller: assignedInventoriesController.controllerServiceArea,
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            // itemCount: assignedInventoriesController.assignedCustomerList?.length + 1,
            itemCount:
                assignedInventoriesController.assignedServiceList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  assignedInventoriesController.assignedServiceList?.length) {
                if (assignedInventoriesController.isShowLoadMore) {
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
                AssignedServiceAreaDataList item =
                    assignedInventoriesController.assignedServiceList![index];
                return AssignedServiceAreaInventoryItem(item: item);
              }
            })
        : noDataFound();
  }

  _assignedInventoriesCustomerList() {
    return (assignedInventoriesController.assignedInventoryList != null &&
            assignedInventoriesController.assignedInventoryList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount:
                assignedInventoriesController.assignedInventoryList!.length + 1,
            controller: assignedInventoriesController.controller,
            itemBuilder: (BuildContext context, int index) {
              if (index ==
                  assignedInventoriesController.assignedInventoryList?.length) {
                if (assignedInventoriesController.isShowLoadMore) {
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
                AssignedInventoryDetail item =
                    assignedInventoriesController.assignedInventoryList![index];
                return AssignedInventoriesItem(item: item);
              }
            })
        : noDataFound();
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.assigned_inventories,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
