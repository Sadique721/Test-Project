import 'package:savbill/pages/customer_caf/customer_caf_detail/customer_caf_detail.dart';
import 'package:savbill/pages/pending_approvals/customers/pa_customer_item.dart';
import 'package:savbill/pages/pending_approvals/customers/pa_customer_status_dialog.dart';
import 'package:savbill/pages/pending_approvals/customers/pending_approval_customer_controller.dart';
import 'package:savbill/pages/pending_approvals/model/request/customer_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_customer_res.dart';
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

import '../../model/file_detail.dart';

class PendingApprovalCustomer extends StatefulWidget {
  @override
  _PendingApprovalCustomerState createState() =>
      _PendingApprovalCustomerState();
}

class _PendingApprovalCustomerState extends State<PendingApprovalCustomer>
    implements PACustomerStatusBtnAction {
  final customerPendingApprovalController =
      Get.put(CustomerPendingApprovalController());

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
          GetBuilder<CustomerPendingApprovalController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: customerPendingApprovalController.isLoading),
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
                    title: Strings.customer_detail,
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
                child: (customerPendingApprovalController.customersList !=
                            null &&
                        customerPendingApprovalController
                            .customersList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                customerPendingApprovalController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: customerPendingApprovalController
                                    .customersList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  customerPendingApprovalController
                                      .customersList?.length) {
                                if (customerPendingApprovalController
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
                                    customerPendingApprovalController
                                        .customersList![index];
                                return PACustomerItem(
                                  item: item,
                                  onTapName: () {
                                    openCustomerDetailScreen(
                                        item.id, Strings.prepaid);
                                  },
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

  openCustomerDetailScreen(int? customerId, String? custType) async {
    Get.to(CustomerCafDetailScreen(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_TYPE: custType,
      Constant.CUST_APPROVAL: true,
    });
  }

  showChangeStatusDialog(ApprovalPendingCustomer detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approved;
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected.toLowerCase();
    }

    CustomerApproveRejectReq request = CustomerApproveRejectReq(
        nextStaffId: "",
        custcafId: detail.id,
        flag: status,
        remark: "",
        staffId:
            customerPendingApprovalController.userDetail!.userId.toString());

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PACustomerStatusDialog(
            paCustomerStatusBtnAction: this,
            customerApproveRejectReq: request,
            from: from,
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.customer_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void paCustomerStatusBtnAction(
      {String? identifier,
      CustomerApproveRejectReq? customerApproveRejectReq,
      List<FileDetail>? allFiles}) {
    Get.back();
    customerPendingApprovalController.approveRejectCustomer(
        customerApproveRejectReq!, context,allFiles);
  }
}
