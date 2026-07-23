import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/bulk_consumption/add_edit_bulk_consumption.dart';
import 'package:savbill/pages/inventory/bulk_consumption/bulk_cons_approve_reject_dialog.dart';
import 'package:savbill/pages/inventory/bulk_consumption/bulk_consumption_item.dart';
import 'package:savbill/pages/inventory/bulk_consumption/bulk_consumption_mapping.dart';
import 'package:savbill/pages/inventory/bulk_consumption/view_bulk_consumption_controller.dart';
import 'package:savbill/pages/inventory/module/response/bulk_cons_approve_reject_req.dart';
import 'package:savbill/pages/inventory/module/response/view_bulk_consumption_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ViewBulkConsumption extends StatefulWidget {
  @override
  _ViewBulkConsumptionState createState() => _ViewBulkConsumptionState();
}

class _ViewBulkConsumptionState extends State<ViewBulkConsumption> implements BulkConsApproveRejectBtnAction {
  final viewBulkConsumptionController =
      Get.put(ViewBulkConsumptionController());

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
      child: GetBuilder<ViewBulkConsumptionController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewBulkConsumptionController.isLoading),
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
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.bulk_consumption,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Flexible(
                        child: Card(
                          margin: const EdgeInsets.all(0),
                          elevation: 0.5,
                          child: Container(
                            height: 50,
                            padding: const EdgeInsets.symmetric(
                                horizontal:
                                    Constant.SEARCH_BAR_CARD_PADDING - 2,
                                vertical: Constant.SEARCH_BAR_CARD_PADDING - 4),
                            child: CoustomTextField(
                                labelText: Strings.search_your_text_here,
                                textEditingController:
                                    viewBulkConsumptionController
                                        .searchController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorPrimary,
                                textInputAction: TextInputAction.done,
                                onChanged: (value) {},
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                prefixIcon: Icon(
                                  Icons.search,
                                  color: AppTheme.colorPrimary,
                                ),
                                borderCorner: Constant.BTN_ROUNDED_CORNER_M,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: false),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Row(children: [
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewBulkConsumptionController.applyFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.statusClosedGreen,
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.check,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewBulkConsumptionController.clearFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.colorRed,
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.close,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                      ]),
                    ]),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewBulkConsumptionController.bulkConsumptionList !=
                            null &&
                        viewBulkConsumptionController
                            .bulkConsumptionList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                viewBulkConsumptionController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewBulkConsumptionController
                                    .bulkConsumptionList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewBulkConsumptionController
                                      .bulkConsumptionList?.length) {
                                if (viewBulkConsumptionController
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
                                BulkConsumptionDetail item =
                                    viewBulkConsumptionController
                                        .bulkConsumptionList![index];
                                return BulkConsumptionItem(
                                  index: index,
                                  item: item,
                                    onTapMacMapView: () {
                                      openBulkConsumptionScreen(
                                          Strings.view, item);
                                    },
                                    onTapDelete :(){
                                      showDialog(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return AlertDialogHelper(
                                              title: Strings.delete_confirmation,
                                              message: Strings.msg_delete,
                                              positiveBtnText: Strings.ok,
                                              negativeBtnText: Strings.cancel,
                                              positiveBtnClick: () {
                                                Get.back();
                                                viewBulkConsumptionController
                                                    .deleteBulkConsumptionItemData(
                                                    item);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                    onTapApprove :(){
                                      addRemarkInvoiceDialog(context, Strings.approve, viewBulkConsumptionController
                                          .bulkConsumptionList![index]);
                                    },
                                    onTapReject :(){
                                      addRemarkInvoiceDialog(context, Strings.rejected, viewBulkConsumptionController
                                          .bulkConsumptionList![index]);
                                    }
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
                      addEditBulkConsumptionScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_bulk_consumption,
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



  openBulkConsumptionScreen(String from, BulkConsumptionDetail? item) async {
    var result = await Get.to(BulkConsumptionMapping(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewBulkConsumptionController.clearFilter();
    }
  }

  addEditBulkConsumptionScreen(String from, BulkConsumptionDetail? item) async {
    var result = await Get.to(AddEditBulkConsumption(),
        arguments: {Constant.FROM: from, Constant.IM_DETAIL: item});

    if (result != null && result == true) {
      viewBulkConsumptionController.clearFilter();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.bulk_consumption, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  addRemarkInvoiceDialog(BuildContext context, String? pageName,BulkConsumptionDetail item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return BulkConsuApproveRejectDialog(
              pageName: pageName,
              bulkConsApproveRejectBtnAction: this,
              bulkConsApproveRejectReq: BulkConsApproveRejectReq(
                  id:item.id,
                  approvalStatus: pageName,
              ));
        });
  }

  @override
  void bulkConsApproveRejectStatus({String? identifier, TextEditingController? remarkController, BulkConsApproveRejectReq? bulkConsApproveRejectReq}) {
    Get.back();
    viewBulkConsumptionController.bulkConsumptionApproveReject(bulkConsApproveRejectReq!);

  }

}
