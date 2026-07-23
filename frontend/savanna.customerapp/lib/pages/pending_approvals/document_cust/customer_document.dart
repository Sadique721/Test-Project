import 'package:savbill/pages/pending_approvals/document_cust/cust_doc_approval_item.dart';
import 'package:savbill/pages/pending_approvals/document_cust/cust_doc_approve_reject_dialog.dart';
import 'package:savbill/pages/pending_approvals/document_cust/customer_doc_approval_controller.dart';
import 'package:savbill/pages/pending_approvals/model/response/customer_doc_approval_res.dart';
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

class CustomerDocumentApproval extends StatefulWidget {
  @override
  _CustomerDocumentApprovalState createState() => _CustomerDocumentApprovalState();
}

class _CustomerDocumentApprovalState extends State<CustomerDocumentApproval> implements CustDocApproveRejectBtnAction {
  final customerDocApprovalController = Get.put(CustomerDocApprovalController());

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
      child: GetBuilder<CustomerDocApprovalController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: customerDocApprovalController.isLoading),
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
                child: (customerDocApprovalController.customerDocList != null &&
                    customerDocApprovalController.customerDocList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: customerDocApprovalController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                            customerDocApprovalController.customerDocList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  customerDocApprovalController.customerDocList?.length) {
                                if (customerDocApprovalController.isShowLoadMore) {
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
                                CustDocApprovalDataList item =
                                customerDocApprovalController.customerDocList![index];
                                return CustDocApprovalItem(
                                  item: item,
                                  onTapApprove: () {
                                    customerDocApprovalController.entityId = item.docId;
                                    addRemarkDocumentDialog(context, Strings.approve, item);
                                  },
                                  onTapReject: () {
                                    customerDocApprovalController.entityId = item.docId;
                                    addRemarkDocumentDialog(context, Strings.reject, item);
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


  addRemarkDocumentDialog(
      BuildContext context, String? pageName, CustDocApprovalDataList item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustDocApproveRejectDialog(
              pageName: pageName,
              custDocApproveRejectBtnAction: this,
              custDocApprovalDataList:item);
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.customer_document,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void documentApproveRejectStatus({String? identifier,
    TextEditingController? remarkController,
    CustDocApprovalDataList? custDocApprovalDataList}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      customerDocApprovalController.getCustomerDocumentApproveRejectData(status: Strings.approve.toLowerCase(), isApprovedRequest : true, remark:remarkController!.text,custDocApprovalDataList :custDocApprovalDataList ,context: context);
    } else if (identifier != null && identifier.equalsIgnoreCase(Strings.reject)) {
      customerDocApprovalController.getCustomerDocumentApproveRejectData(status: Strings.reject.toLowerCase(), isApprovedRequest : false, remark:remarkController!.text,custDocApprovalDataList :custDocApprovalDataList ,context: context);
    }
  }
}
