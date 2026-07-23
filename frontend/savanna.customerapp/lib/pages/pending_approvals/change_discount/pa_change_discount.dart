import 'package:savbill/pages/pending_approvals/change_discount/pa_change_discount_controller.dart';
import 'package:savbill/pages/pending_approvals/change_discount/pa_change_discount_item.dart';
import 'package:savbill/pages/pending_approvals/change_discount/pa_change_discount_status_dialog.dart';
import 'package:savbill/pages/pending_approvals/model/request/change_discount_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/ap_change_discount_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalChangeDiscount extends StatefulWidget {
  @override
  _PAChangeDiscountState createState() => _PAChangeDiscountState();
}

class _PAChangeDiscountState extends State<PendingApprovalChangeDiscount>
    implements ChangeDiscountStatusBtnAction {
  final paChangeDiscountController = Get.put(PAChangeDiscountController());

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
      child: GetBuilder<PAChangeDiscountController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paChangeDiscountController.isLoading),
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
        width: MediaQuery
            .of(context)
            .size
            .width,
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
                    title: Strings.change_discount_detail,
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
                child: (paChangeDiscountController.changeDiscountList != null &&
                    paChangeDiscountController
                        .changeDiscountList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: paChangeDiscountController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount: paChangeDiscountController
                          .changeDiscountList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            paChangeDiscountController
                                .changeDiscountList?.length) {
                          if (paChangeDiscountController.isShowLoadMore) {
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
                          APChangeDiscount item =
                          paChangeDiscountController
                              .changeDiscountList![index];
                          return PAChangeDiscountItem(
                            item: item,
                            onTapApprove: () {
                              showChangeStatusDialog(
                                  item, Strings.approve);
                            },
                            onTapReject: () {
                              showChangeStatusDialog(
                                  item, Strings.reject);
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

  showChangeStatusDialog(APChangeDiscount detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approved.toLowerCase();
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
    }

    ChangeDiscountApproveRejectReq request = ChangeDiscountApproveRejectReq(
        custPackageId: detail.id,
        planId: detail.planId,
        flag: status,
        nextStaffId: 0,
        remark: "",
        staffId: paChangeDiscountController.userDetail!.userId.toString());

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PAChangeDiscountStatusDialog(
              changeDiscountStatusBtnAction: this,
              changeDiscountApproveRejectReq: request,
              from: from,
             );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.change_discount_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void changeDiscountBtnAction(
      {String? identifier, ChangeDiscountApproveRejectReq? request}) {
    Get.back();
    paChangeDiscountController.approveRejectChangeDiscount(request!);
  }
}
