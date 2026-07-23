import 'package:savbill/pages/customer_change_status/request/cust_terminate_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/customers/pa_customer_item.dart';
import 'package:savbill/pages/pending_approvals/model/request/termination_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_customer_res.dart';
import 'package:savbill/pages/pending_approvals/termination/pa_approve_reject_customer_termination_dialog.dart';
import 'package:savbill/pages/pending_approvals/termination/pa_customer_termination_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PACustomerTermination extends StatefulWidget {
  @override
  _PACustomerTerminationState createState() => _PACustomerTerminationState();
}

class _PACustomerTerminationState extends State<PACustomerTermination> implements ApproveRejectCustomerStatusBtnAction {
  final paCustomerTerminationController =
      Get.put(PACustomerTerminationController());

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
      child: GetBuilder<PACustomerTerminationController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paCustomerTerminationController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
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
                    title: Strings.termination_customer_detail,
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
                child: (paCustomerTerminationController.customersList != null &&
                        paCustomerTerminationController
                            .customersList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                paCustomerTerminationController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: paCustomerTerminationController
                                    .customersList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paCustomerTerminationController
                                      .customersList?.length) {
                                if (paCustomerTerminationController
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
                                ApprovalPendingCustomer item =
                                    paCustomerTerminationController
                                        .customersList![index];
                                return PACustomerItem(
                                  item: item,
                                  onTapApprove: () {
                                    // showChangeStatusDialog(
                                    //     item, Strings.approve);

                                    paCustomerTerminationController.entityId = item.id;
                                    addRemarkStatusDialog(context, Strings.approve,
                                        paCustomerTerminationController, item.id);
                                  },
                                  onTapReject: () {
                                    // showChangeStatusDialog(
                                    //     item, Strings.reject);
                                    paCustomerTerminationController.entityId = item.id;
                                    addRemarkStatusDialog(context, Strings.reject,
                                        paCustomerTerminationController, item.id);
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

  showChangeStatusDialog(ApprovalPendingCustomer detail, String from) {
    String status = "", title = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approved;
      title =
          "Are you sure, want to ${Strings.approve} ${Strings.termination_customer} ?";
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
      title =
          "Are you sure, want to ${Strings.reject} ${Strings.termination_customer} ?";
    }

    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: title,
            positiveBtnText: Strings.submit,
            negativeBtnText: Strings.cancel,
            positiveBtnClick: () {
              Get.back();
              paCustomerTerminationController.approveRejectTermination(TerminationApproveRejectReq(
                id: detail.id,
                status: status,
              ));
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }


  addRemarkStatusDialog(BuildContext context, String? pageName,
      PACustomerTerminationController? controller, int? terminateProductId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ApproveRejectCustomerStatusDialog(
            pageName: pageName,
            controller: controller,
            approveRejectChangeStatusBtnAction: this,
            customerTerminateApproveReq: CustomerTerminateApproveRejectReq(),
            terminateProductId: terminateProductId,
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.customer_termination_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void approveRejectCustomerStatusDetails({String? identifier, TextEditingController? remarkController, CustomerTerminateApproveRejectReq? approveCustomerAddressReq, BuildContext? context}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      paCustomerTerminationController.getApproveCustomerChangeStatusApproveReject(Strings.approved,
          remarkController!.text, approveCustomerAddressReq!, context!);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      paCustomerTerminationController.getApproveCustomerChangeStatusApproveReject(Strings.rejected,
          remarkController!.text, approveCustomerAddressReq, context!);
    }
  }

}
