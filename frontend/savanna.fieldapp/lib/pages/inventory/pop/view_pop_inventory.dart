import 'dart:developer';

import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/pages/inventory/pop/approval_progress_dialog.dart';
import 'package:savbill/pages/inventory/pop/approve_reject_pop_dialog.dart';
import 'package:savbill/pages/inventory/pop/pop_assign_inventory.dart';
import 'package:savbill/pages/inventory/pop/pop_inventory_item.dart';
import 'package:savbill/pages/inventory/pop/pop_inventory_mapping.dart';
import 'package:savbill/pages/inventory/pop/view_pop_inventory_controller.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ViewPopInventory extends StatefulWidget {
  @override
  _ViewPopInventoryState createState() => _ViewPopInventoryState();
}

class _ViewPopInventoryState extends State<ViewPopInventory>
    implements PopApproveRejectBtnAction,ApproveProgressAction {
  final viewPopInventoryController = Get.put(ViewPopInventoryController());

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
      child: GetBuilder<ViewPopInventoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: viewPopInventoryController.isLoading),
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
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.only(
                    left: Constant.LARGE_PADDING,
                    bottom: Constant.SMALL_PADDING),
                child: CustomText(
                    title:
                        "${viewPopInventoryController.custName} ${Strings.inventory_list}",
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    maxLines: 1,
                    height: 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewPopInventoryController.popInventoryList != null &&
                        viewPopInventoryController.popInventoryList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewPopInventoryController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewPopInventoryController
                                    .popInventoryList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index == viewPopInventoryController.popInventoryList?.length) {
                                if (viewPopInventoryController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                PopInventoryDetail item = viewPopInventoryController.popInventoryList![index];
                                viewPopInventoryController.ownerType = item.ownerType;
                                viewPopInventoryController.ownerID = item.ownerId;
                                return PopInventoryItem(
                                  item: item,
                                  onTapEdit: () {
                                    openMappingScreen(
                                        item.inOutWardMACMapping!);
                                  },
                                  onTapApprove: () {
                                    approveRejectPopDialog(context,Strings.approve,item.id);
                                  },
                                  onTapReject: () {
                                    approveRejectPopDialog(context,Strings.reject,item.id);
                                  },
                                  onTapAuditApproval: () {
                                    approvalProgressDialog(context,viewPopInventoryController.popInventoryList!);

                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                    onTap: () {
                      // viewPopController.addEditPopScreen(Strings.add, null);
                      openAssignInventoryScreen(
                          viewPopInventoryController.popId,
                          viewPopInventoryController.ownerType);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.assign_inventory,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ))
                ],
              )
            ]),
      ),
    );
  }

  openMappingScreen(List<InOutWardMACMapping>? inOutWardMACMapping) async {
    var result = await Get.to(PopInventoryMapping(),
        arguments: {Constant.IM_DETAIL: inOutWardMACMapping});
    if (result != null && result == true) {
      Get.back(result: true);
    }
  }

  openAssignInventoryScreen(int? ownerId, String? ownerType) async {
    var result = await Get.to(PopAssignInventory(), arguments: {
      Constant.OWNER_ID: ownerId,
      Constant.OWNER_TYPE: ownerType,
    });
    if (result != null) {
      ViewPopInventoryRes data = result;
      if (data != null) {
        viewPopInventoryController.viewPopInventoryRes = data;
        viewPopInventoryController.popInventoryList!.clear();
        viewPopInventoryController.viewPopInventoryData();
        viewPopInventoryController.update();
      }
    }
  }

  approveRejectPopDialog(
      BuildContext context, String? pageName, int? inventoryMappingId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PopApproveRejectDialog(
            pageName: pageName,
            creditApproveRejectBtnAction: this,
            inventoryMappingId: inventoryMappingId,
          );
        });
  }




  approvalProgressDialog(
      BuildContext context, List<PopInventoryDetail> item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ApproveProgressDialog(
            approveProgressAction: this,
            itemsOrgLst: item,
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.pop_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  @override
  void popApproveRejectStatus(
      {String? identifier, TextEditingController? remarkController,int? mappingId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      viewPopInventoryController.viewPopDetail(mappingId,remarkController!.text, true);
    } else if (identifier != null && identifier.equalsIgnoreCase(Strings.reject)) {
      viewPopInventoryController.viewPopDetail(mappingId,remarkController!.text, false);
    }
  }

  @override
  void approveProgressBtnAction({PopInventoryDetail? selectedItem}) {

  }
}
