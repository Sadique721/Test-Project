import 'dart:developer';
import 'package:savbill/pages/change_plan/remark_dialog.dart';
import 'package:savbill/pages/customer/assign_inventory.dart';
import 'package:savbill/pages/customer_inventory/assign_inventory_plan.dart';
import 'package:savbill/pages/customer_inventory/customer_edit_inventory_approve_reject_dialog.dart';
import 'package:savbill/pages/customer_inventory/customer_invent_approve_reject_dialg.dart';
import 'package:savbill/pages/customer_inventory/customer_inventory_team_work_flow.dart';
import 'package:savbill/pages/customer_inventory/document/document_upload.dart';
import 'package:savbill/pages/customer_inventory/document/document_view_screen.dart';
import 'package:savbill/pages/customer_inventory/external_inventory/external_inventory.dart';
import 'package:savbill/pages/customer_inventory/history_inventory/cust_inventory_history.dart';
import 'package:savbill/pages/customer_inventory/inventory_detail_controller.dart';
import 'package:savbill/pages/customer_inventory/inventory_item_view.dart';
import 'package:savbill/pages/customer_inventory/inventory_replace.dart';
import 'package:savbill/pages/customer_inventory/other_inventory.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/pages/customer_inventory/wifi_config.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../util/permission_service.dart';
import '../customer/customer_detail_controller.dart';
import 'inventory_remove_approve_reject_dialog.dart';

class CustomerInventoryDetail extends StatefulWidget {
  @override
  _CustomerInventoryState createState() => _CustomerInventoryState();
}

class _CustomerInventoryState extends State<CustomerInventoryDetail>
    implements
        InventoryRemoveRemarkBtnAction,
        CustomerInventoryApproveRejectBtnAction,
        CustomerEditInventoryApproveRejectBtnAction {
  final inventoryDetailController = Get.put(InventoryDetailController());

  @override
  void initState() {
    super.initState();
    Get.delete<CustomerDetailController>();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<InventoryDetailController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: inventoryDetailController.isLoading),
      ]);
    });
  }

  _body() {
    final isPrepaid =
        (inventoryDetailController.type?.toLowerCase() ?? '') == 'prepaid';
    final isCustCaf = (inventoryDetailController.isCustCaf);

    final hasPlanInventory = PermissionService().hasAclPermission(isPrepaid
            ? (isCustCaf
                ? [AclPreCustConstants.PRE_CUST_CAF_INVEN_PLAN]
                : [AclPreCustConstants.PRE_CUST_INVEN_PLAN])
            : (isCustCaf
                ? [AclPostCustConstants.POST_CUST_CAF_INVEN_PLAN]
                : [AclPostCustConstants.POST_CUST_INVEN_PLAN])) ==
        true;

    final hasOtherInventory = PermissionService().hasAclPermission(isPrepaid
            ? (isCustCaf
                ? [AclPreCustConstants.PRE_CUST_CAF_INVEN_OTHER]
                : [AclPreCustConstants.PRE_CUST_INVEN_OTHER])
            : (isCustCaf
                ? [AclPostCustConstants.POST_CUST_CAF_INVEN_OTHER]
                : [AclPostCustConstants.POST_CUST_INVEN_OTHER])) ==
        true;

    final hasExternalInventory = PermissionService().hasAclPermission(isPrepaid
            ? (isCustCaf
                ? [AclPreCustConstants.PRE_CUST_CAF_INVEN_EXTERNAL]
                : [AclPreCustConstants.PRE_CUST_INVEN_EXTERNAL])
            : (isCustCaf
                ? [AclPostCustConstants.POST_CUST_CAF_INVEN_EXTERNAL]
                : [AclPostCustConstants.POST_CUST_INVEN_EXTERNAL])) ==
        true;

    final hasHistoryInventory = PermissionService().hasAclPermission(isPrepaid
            ? (isCustCaf
                ? [AclPreCustConstants.PRE_CUST_CAF_INVEN_HISTORY]
                : [AclPreCustConstants.PRE_CUST_INVEN_HISTORY])
            : (isCustCaf
                ? [AclPostCustConstants.POST_CUST_CAF_INVEN_HISTORY]
                : [AclPostCustConstants.POST_CUST_INVEN_HISTORY])) ==
        true;

    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            SingleChildScrollView(
              scrollDirection: Axis.horizontal,
              padding: const EdgeInsets.all(Constant.SMALL_PADDING),
              child: Row(
                children: [
                  if (hasPlanInventory)
                    InkWell(
                      onTap: () {
                        openAssignInventoryPlanScreen();
                      },
                      child: tabButton(Strings.plan_inventory),
                    ),
                  if (hasOtherInventory)
                    InkWell(
                      onTap: () {
                        openOtherInventoryScreen();
                      },
                      child: tabButton(Strings.other_inventroy),
                    ),
                  if (hasExternalInventory)
                    InkWell(
                      onTap: () {
                        openExternalInventoryScreen();
                      },
                      child: tabButton(Strings.external_inventory),
                    ),
                  if (hasHistoryInventory)
                    InkWell(
                        onTap: () {
                          openCustomerInventoryHistoryScreen();
                        },
                        child: tabButton(Strings.inventory_history)),
                ],
              ),
            ),
            Container(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                      child: CustomText(
                          title:
                              "${inventoryDetailController.customerName} ${Strings.inventory_list}",
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500)),
                  const SizedBox(
                    width: Constant.VERY_SMALL_PADDING,
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 1,
              child: (inventoryDetailController.inventoryDataList != null &&
                      inventoryDetailController.inventoryDataList!.isNotEmpty)
                  ? ListView.builder(
                      controller: inventoryDetailController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                          inventoryDetailController.inventoryDataList!.length +
                              1,
                      itemBuilder: (context, index) {
                        if (index ==
                            inventoryDetailController
                                .inventoryDataList?.length) {
                          if (inventoryDetailController.isShowLoadMore) {
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
                          CustomerInventoryDataList item =
                              inventoryDetailController
                                  .inventoryDataList![index];
                          return InventoryViewItem(
                              item: item,
                              controller: inventoryDetailController,
                              serviceList:
                                  inventoryDetailController.planServiceList,
                              userId:
                                  inventoryDetailController.userDetail!.userId,
                              index: index,
                              onTapDownload: () {
                                inventoryDetailController
                                    .downloadDocument(item);
                              },
                              onTapUpload: () {
                                openCustInventoryDocumentUploadScreen(
                                    Strings.add, item.customerId, item);
                              },
                              onTapView: () {
                                openCustInventoryDocumentViewScreen(
                                    Strings.add, item.customerId, item);
                              },
                              onTapWifiConfig: () {
                                Get.to(WifiConfig(item: item));
                                // WifiConfig
                              },
                              onTapApprove: () {
                                // if (item.status!.equalsIgnoreCase(
                                //     Constant.PENDING.toUpperCase())) {
                                inventoryDetailController.approveIdList!
                                    .clear();
                                inventoryDetailController
                                    .setBtnClickEvent(false);
                                inventoryDetailController
                                    .getInventoryDocumentList(item.id);
                                inventoryDetailController.approveIdList!
                                    .add(item.id);
                                addRemarkApproveRejectDialog(
                                    context,
                                    Strings.approve,
                                    item,
                                    inventoryDetailController);
                                // } else {
                                //
                                // }
                              },
                              onTapReject: () {
                                if (item.status!.equalsIgnoreCase(
                                    Constant.ACTIVE.toUpperCase())) {
                                } else {
                                  inventoryDetailController.approveIdList!
                                      .clear();
                                  inventoryDetailController
                                      .setBtnClickEvent(false);
                                  inventoryDetailController
                                      .getInventoryDocumentList(item.id);
                                  inventoryDetailController.approveIdList!
                                      .add(item.id);
                                  // inventoryDetailController.getApproveReqInventoryApi(false);
                                  addRemarkApproveRejectDialog(
                                      context,
                                      Strings.reject,
                                      item,
                                      inventoryDetailController);
                                }
                              },
                              onTapReplace: () {
                                inventoryDetailController
                                    .setBtnClickEvent(false);
                                if (item.inOutWardMACMapping!.isNotEmpty) {
                                  openInventoryReplaceScreen(
                                      item.inOutWardMACMapping![0].id,
                                      item.planId,
                                      item.inOutWardMACMapping![0].itemId);
                                }
                              },
                              onTapEdit: () {
                                inventoryDetailController
                                    .setBtnClickEvent(false);
                                if (item.inOutWardMACMapping!.length > 1) {
                                  editApproveRejectDialog(
                                      context,
                                      Strings.inventory_detail,
                                      item,
                                      inventoryDetailController
                                          .userDetail!.userId);
                                }
                                // Utils.showSnackbar(
                                //     Strings.SUCCESS,
                                //     Strings.under_development,
                                //     AppTheme.colorWhite,
                                //     AppTheme.colorGreen);
                              },
                              onTapReactiveBox: () {
                                if (item.status!.equalsIgnoreCase(
                                    Constant.ACTIVE.toUpperCase())) {
                                  inventoryDetailController.approveIdList!
                                      .clear();
                                  inventoryDetailController
                                      .setBtnClickEvent(false);
                                  inventoryDetailController.approveIdList!
                                      .add(item.id);
                                  inventoryDetailController
                                      .inventoryReactiveBoxApi();
                                } else {}
                              },
                              onTapDeleteInventory: () {
                                inventoryDetailController
                                    .setBtnClickEvent(false);
                                if(!isCustCaf && isDisableRemove(
                                    item,
                                    inventoryDetailController
                                        .planServiceList)){
                                  Utils.showSnackbar(
                                      Strings.INFO,
                                      "Please terminate service, before remove inventory",
                                      AppTheme.colorWhite,
                                      AppTheme.colorBlueRView);
                                  return;
                                }
                                // if (isDisableRemove(
                                //     item,
                                //     inventoryDetailController
                                //         .planServiceList)) {
                                //   Utils.showSnackbar(
                                //       Strings.INFO,
                                //       "Please terminate service, before remove inventory",
                                //       AppTheme.colorWhite,
                                //       AppTheme.colorBlueRView);
                                // }
                                // else {
                                if (item.inOutWardMACMapping!.isNotEmpty) {
                                  inventoryDetailController.removeId =
                                      item.inOutWardMACMapping![0].id;
                                  inventoryDetailController
                                          .removeCustInventoryId =
                                      item.inOutWardMACMapping![0]
                                          .custInventoryMappingId;
                                  inventoryDetailController.removeItemId =
                                      item.inOutWardMACMapping![0].itemId;
                                  inventoryDetailController.editInventory =
                                      false;
                                  inventoryDetailController
                                      .editSTBCradInventory = false;

                                  showExitDialog(item);
                                  // showDialog(
                                  //     context: context,
                                  //     barrierDismissible: true,
                                  //     builder: (BuildContext context) {
                                  //       return RemarkDialog(
                                  //         pageName: 'inventoryDetails',
                                  //         inventoryRemarkBtnAction: this,
                                  //         itemList: item,
                                  //       );
                                  //     });
                                } else {
                                  Utils.showSnackbar(
                                      Strings.ERROR,
                                      "Not Getting macMapping Id",
                                      AppTheme.colorWhite,
                                      AppTheme.colorRed);
                                }
                                // }
                              },
                              onTapApproveProgress: () {
                                inventoryDetailController
                                    .setBtnClickEvent(false);
                                openInventoryProgressScreen(item.id);
                              },
                              onTapApproveRemoveInventory: () {
                                showDialog(
                                    context: context,
                                    barrierDismissible: true,
                                    builder: (BuildContext context) {
                                      return RemoveInventoryApproveRejectDialog(
                                        pageName:
                                            Strings.approveRemoveInventory,
                                        inventoryRemoveRemarkBtnAction: this,
                                        itemList: item,
                                      );
                                    });
                              },
                              onTapRejectRemoveInventory: () {
                                showDialog(
                                    context: context,
                                    barrierDismissible: true,
                                    builder: (BuildContext context) {
                                      return RemoveInventoryApproveRejectDialog(
                                        pageName: Strings.rejectRemoveInventory,
                                        inventoryRemoveRemarkBtnAction: this,
                                        itemList: item,
                                      );
                                    });
                              });
                        }
                      })
                  : noDataFound(),
            ),
          ]),
    );
  }

  showExitDialog(CustomerInventoryDataList? item) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.confirmation,
            message: Strings.remove_inventory_msg,
            positiveBtnText: Strings.yes,
            negativeBtnText: Strings.no,
            positiveBtnClick: () {
              Navigator.pop(context);
              inventoryDetailController.inventoryRemoveById(
                itemId: item!.itemId,
                customerInventoryId:
                    item.inOutWardMACMapping![0].custInventoryMappingId!,
              );
            },
            negativeBtnClick: () {
              Navigator.pop(context);
            });
      },
    );
  }

  openCustInventoryDocumentUploadScreen(
      String? from, int? customerId, CustomerInventoryDataList? item) async {
    var result = Get.to(DocumentUploadScreen(), arguments: {
      Constant.FROM: from,
      Constant.CUSTOMER_ID: customerId,
      Constant.INVENTORY_ITEMS: item,
    });

    if (result != null && result == true) {
      inventoryDetailController.getAllCustomerInventoryListApi();
    }
  }

  openCustInventoryDocumentViewScreen(
      String? from, int? customerId, CustomerInventoryDataList? item) async {
    var result = Get.to(ViewDocumentScreen(), arguments: {
      Constant.FROM: from,
      Constant.INVENTORY_ID: item!.id,
      Constant.CUSTOMER_NAME: item.customerName,
    });
    if (result != null && result == true) {
      inventoryDetailController.getAllCustomerInventoryListApi();
    }
  }

  openAssignInventoryScreen(int? customerId) async {
    var result = await Get.to(AssignInventory(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });

    if (result != null && result == true) {
      inventoryDetailController.page = 1;
      // inventoryDetailController.getCustomerInventoryDetail();
      inventoryDetailController.getAllCustomerInventoryListApi();
    }
  }

  openInventoryProgressScreen(int? eventId) async {
    var result = await Get.to(const CustomerTeamWorkInventoryFlow(),
        arguments: {
          Constant.ID: eventId,
          Constant.EVENT_TYPE: "CUSTOMER_INVENTORY_ASSIGN"
        });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  openInventoryReplaceScreen(
      int? macMappingId, int? planId, int? itemId) async {
    var result = await Get.to(InventoryReplace(), arguments: {
      Constant.CUSTOMER_ID: inventoryDetailController.customerId,
      Constant.MAC_MAPPING_ID: macMappingId,
      Constant.ITEM_ID: itemId,
      Constant.PLAN_ID: planId,
    });
    if (result != null && result == true) {
      inventoryDetailController.getAllCustomerInventoryListApi();
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  addRemarkApproveRejectDialog(BuildContext context, String? pageName,
      CustomerInventoryDataList item, InventoryDetailController controller) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustomerInventoryApproveRejectDialog(
            pageName: pageName,
            inventoryApproveRejectBtnAction: this,
            itemId: item.id,
            item: item,
            controller: controller,
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.inventory_detail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  openOtherInventoryScreen() async {
    String name = inventoryDetailController.customerName;
    var result = await Get.to(OtherInventory(), arguments: {
      Constant.CUSTOMER_ID: inventoryDetailController.customerId,
      Constant.CUSTOMER_NAME: name,
      Constant.CUSTOMER_FIRST_NAME: inventoryDetailController.customerFirstName,
      Constant.SERVICE_AREA_ID: inventoryDetailController.serviceAreaId,
      Constant.CUSTOMER_TYPE: inventoryDetailController.type,
    });
    if (result != null) {
      inventoryDetailController.getAllCustomerInventoryListApi();
    }
  }

  openExternalInventoryScreen() async {
    String name = inventoryDetailController.customerName;
    var result = await Get.to(ExternalInventory(), arguments: {
      Constant.CUSTOMER_ID: inventoryDetailController.customerId,
      Constant.CUSTOMER_NAME: name,
      Constant.SERVICE_AREA_ID: inventoryDetailController.serviceAreaId,
    });
  }

  openCustomerInventoryHistoryScreen() async {
    String name = inventoryDetailController.customerName;
    var result = await Get.to(CustInventoryHistory(), arguments: {
      Constant.CUSTOMER_ID: inventoryDetailController.customerId,
      Constant.CUSTOMER_NAME: name,
      Constant.SERVICE_AREA_ID: inventoryDetailController.serviceAreaId,
    });
  }

  editApproveRejectDialog(
    BuildContext context,
    String? pageName,
    CustomerInventoryDataList item,
    int? userId,
  ) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustomerEditInventoryApproveRejectDialog(
            pageName: pageName,
            editInventoryApproveRejectBtnAction: this,
            item: item,
            staffUserId: userId,
          );
        });
  }

  openAssignInventoryPlanScreen() async {
    String name = inventoryDetailController.customerName;
    var result = await Get.to(AssignInventoryPlan(), arguments: {
      Constant.CUSTOMER_ID: inventoryDetailController.customerId,
      Constant.CUSTOMER_NAME: name,
      Constant.SERVICE_AREA_ID: inventoryDetailController.serviceAreaId,
      Constant.CUSTOMER_TYPE: inventoryDetailController.type,
    });
  }

  tabButton(String buttonName) {
    return GestureDetector(
      child: Container(
        margin: const EdgeInsets.only(
            top: Constant.LARGE_PADDING,
            left: Constant.VERY_SMALL_PADDING,
            right: Constant.VERY_SMALL_PADDING),
        constraints: const BoxConstraints(
          minWidth: Constant.TOP_MENU_OPTION + Constant.LARGE_PADDING,
        ),
        child: Material(
          color: AppTheme.colorAccentTheme,
          elevation: 1.0,
          shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(Constant.ROUNDED_CORNER_BTN)),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                FittedBox(
                  child: CustomText(
                    title: buttonName,
                    colors: AppTheme.colorWhite,
                    textAlign: TextAlign.center,
                    fontSize: AppTheme.small,
                    fontWeight: FontWeight.w400,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  @override
  void customerInventoryApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      int? itemId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      inventoryDetailController.getApproveReqInventoryApi(
          approveRequest: true,
          remark: remarkController!.text,
          status: Strings.approve,
          context: context,
          itemId: itemId);
      // requestInventoryController.assignedInvReqApproveStatus(item.id!, Strings.approve,remarkController!.text);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      inventoryDetailController.getApproveReqInventoryApi(
          approveRequest: false,
          remark: remarkController!.text,
          status: Strings.reject,
          context: context,
          itemId: itemId);
      // requestInventoryController.assignedInvReqApproveStatus(item.id!, Strings.rejected,remarkController!.text);
    }
  }

  @override
  void customerEditInventoryApproveRejectStatus(
      {String? identifier, CustomerInventoryDataList? item}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      inventoryDetailController.approveReplaceInventoryApi(true, item!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      inventoryDetailController.approveReplaceInventoryApi(false, item!);
    }
  }

  @override
  void inventoryRemoveRemarkBtnAction(
      {String? identifier,
      TextEditingController? remarkController,
      int? nextStaffId,
      int? macMappingId,
      int? customerInventoryId,
      int? customerId,
      bool? isApprove}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      log("inventoryRemoveRemarkBtnAction>>> ${remarkController!.text.toUpperCase()}");
      inventoryDetailController.inventoryItemDeleteCall(
          remark: remarkController.text,
          macMappingId: macMappingId!,
          customerId: customerId!,
          customerInventoryId: customerInventoryId!,
          nextStaffId: nextStaffId!,
          isApprove: true);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      inventoryDetailController.inventoryItemDeleteCall(
          remark: remarkController!.text,
          macMappingId: macMappingId!,
          customerId: customerId!,
          customerInventoryId: customerInventoryId!,
          nextStaffId: nextStaffId!,
          isApprove: false);
    }
  }
}
