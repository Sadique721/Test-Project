import 'package:savbill/pages/pending_approvals/model/response/inventory_approval_res.dart';
import 'package:savbill/pages/pending_approvals/pending_inventory/inventory_approval_reject_dialog.dart';
import 'package:savbill/pages/pending_approvals/pending_inventory/inventory_aprroval_item.dart';
import 'package:savbill/pages/pending_approvals/pending_inventory/inventory_pending_approval_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class InventoryPendingApproval extends StatefulWidget {
  const InventoryPendingApproval({super.key});

  @override
  State<InventoryPendingApproval> createState() =>
      _InventoryPendingApprovalState();
}

class _InventoryPendingApprovalState extends State<InventoryPendingApproval>
    implements InventoryApproveRejectBtnAction {
  final inventoryPendingApprovalController =
      Get.put(InventoryPendingApprovalController());

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
      child:
          GetBuilder<InventoryPendingApprovalController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: inventoryPendingApprovalController.isLoading),
        ]);
      }),
    );
  }

  _appBar() {
    return DynamicAppBar(
        Strings.inventory_pending_approval,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
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
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.customer_document,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (inventoryPendingApprovalController
                                .inventoryApprovalList !=
                            null &&
                        inventoryPendingApprovalController
                            .inventoryApprovalList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                inventoryPendingApprovalController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: inventoryPendingApprovalController
                                    .inventoryApprovalList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  inventoryPendingApprovalController
                                      .inventoryApprovalList?.length) {
                                if (inventoryPendingApprovalController
                                    .isShowLoadMore) {
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
                                InventoryApprovalDataList item =
                                    inventoryPendingApprovalController
                                        .inventoryApprovalList![index];
                                return InventoryApprovalItem(
                                  item: item,
                                  onTapApprove: () {
                                    // inventoryPendingApprovalController.entityId = item.docId;
                                    // addRemarkDocumentDialog(context, Strings.approve, item);
                                  },
                                  onTapReject: () {
                                    // inventoryPendingApprovalController.entityId = item.docId;
                                    // addRemarkDocumentDialog(context, Strings.reject, item);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  @override
  void inventoryApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      InventoryApprovalDataList? inventoryApprovalDataList}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      inventoryPendingApprovalController.getCustomerDocumentApproveRejectData(
          status: Strings.approve.toLowerCase(),
          isApprovedRequest: true,
          remark: remarkController!.text,
          inventoryApprovalDataList: inventoryApprovalDataList,
          context: context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      inventoryPendingApprovalController.getCustomerDocumentApproveRejectData(
          status: Strings.reject.toLowerCase(),
          isApprovedRequest: false,
          remark: remarkController!.text,
          inventoryApprovalDataList: inventoryApprovalDataList,
          context: context);
    }
  }
}
