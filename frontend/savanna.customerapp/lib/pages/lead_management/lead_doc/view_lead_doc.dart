
import 'dart:developer';

import 'package:savbill/pages/lead_management/lead_doc/create_lead_document/create_lead_document.dart';
import 'package:savbill/pages/lead_management/lead_doc/lead_doc_item_list.dart';
import 'package:savbill/pages/lead_management/lead_doc/view_lead_doc_controller.dart';
import 'package:savbill/pages/lead_management/model/view_lead_doc_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ViewLeadDoc extends StatefulWidget {
  ViewLeadDoc({Key? key}) : super(key: key);

  @override
  _ViewLeadDocState createState() => _ViewLeadDocState();
}

class _ViewLeadDocState extends State<ViewLeadDoc>
    with WidgetsBindingObserver /*implements DocApproveRejectBtnAction*/ {
  final viewLeadDocController = Get.put(ViewLeadDocController());

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    viewLeadDocController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (viewLeadDocController.checkBtnClickEvent) {
          viewLeadDocController.setBtnClickEvent(false);
        }
        return;
      default:
        return;
    }
  }

  Future<bool> _onWillPop() async {
    return (await backScreen()) ?? false;
  }

  backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewLeadDocController>(builder: (controller) {
        return Stack(children: <Widget>[
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewLeadDocController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const SizedBox(
          height: Constant.MEDIUM_PADDING,
        ),
        Expanded(
          child: (viewLeadDocController.documentList!.isNotEmpty)
              ? ListView.builder(
                  itemCount: viewLeadDocController.documentList!.length,
                  itemBuilder: (context, index) {
                    LeadDocContent item = viewLeadDocController.documentList![index];
                    return LeadDocItemListView(
                      item: item,
                      userDetail: viewLeadDocController.userDetail,
                      onTapEdit: () {
                        openCustomerCreateDocumentScreen(Strings.edit,
                            viewLeadDocController.customerId, item);
                      },
                      onTapDelete: () async {
                        // customerDeleteDocument(item);
                        viewLeadDocController.setBtnClickEvent(false);
                        showDialog(
                          context: context,
                          builder: (BuildContext context) {
                            return AlertDialogHelper(
                                title: Strings.app_name,
                                message: Strings.lead_customer_doct_delete_msg,
                                positiveBtnText: Strings.yes,
                                negativeBtnText: Strings.no,
                                positiveBtnClick: () {
                                  Get.back();
                                  viewLeadDocController
                                      .customerUploadDocumentDelete(item.docId);
                                },
                                negativeBtnClick: () {
                                  Get.back();
                                });
                          },
                        );
                      },

                    );
                  },
                )
              : noDataFound(),
        ),
        Row(
          children: [
            Expanded(
                child: SimpleButton(
              onTap: () {
                openCustomerCreateDocumentScreen(
                    Strings.add, viewLeadDocController.customerId, null);
              },
              radius: 0,
              height: Constant.BOTTOM_BTN_HEIGHT,
              bgColors: AppTheme.colorPrimary,
              borderColors: AppTheme.colorPrimary,
              child: CustomText(
                title: Strings.create_lead_document,
                fontSize: AppTheme.medium,
                fontWeight: FontWeight.w400,
              ),
            ))
          ],
        )
      ],
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  // openCustomerCreateDocumentScreen(
  //     String? from, int? customerId, DocumentDetail? item) async {
  //   var result = Get.to(CreateDocumentCustomer(), arguments: {
  //     Constant.FROM: from,
  //     Constant.CUSTOMER_ID: customerId,
  //     Constant.CUSTOMER_DOCUMENT_DETAIL: item,
  //   });
  //
  //   if (result != null && result == true) {
  //     viewLeadDocController.getCustomerDocumentData();
  //   }
  // }

  // openCreditNoteStatus(int? eventId) async {
  //   var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
  //     Constant.ID: eventId,
  //     Constant.EVENT_TYPE: "DOCUMENT_VERIFICATION"
  //     // Constant.
  //   });
  //   if (result != null && result == true) {
  //     // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
  //   }
  // }

  _appBar() {
    return DynamicAppBar(Strings.lead_doc_management, '', AppTheme.colorPrimary,
        false, backScreen, [], AppBar().preferredSize.height);
  }

  openCustomerCreateDocumentScreen(
      String? from, int? customerId, LeadDocContent? item) async {
    var result = Get.to(()=>CreateDocumentLead(), arguments: {
      Constant.FROM: from,
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_DOCUMENT_DETAIL: item,
    });

    if (result != null && result == true) {
      viewLeadDocController.getCustomerDocumentData();
    }
  }

// addRemarkDocumentDialog(
//     BuildContext context, String? pageName, DocumentDetail item) {
//   showDialog(
//       context: context,
//       barrierDismissible: true,
//       builder: (BuildContext context) {
//         return DocumentApproveRejectDialog(
//             pageName: pageName,
//             documentApproveRejectBtnAction: this,
//             documentDetail:item);
//         // paymentApproveRejectReq: PaymentApproveRejectReq(
//         //     idlist:item.id,
//         //     customerid: item.custId,
//         //     paymode: item.paymode,
//         //     paystatus: item.status,
//         //     paytodate: item.paymentdate,
//         //     referenceno: item.referenceno));
//       });
// }

// @override
// void documentApproveRejectStatus(
//     {String? identifier,
//       TextEditingController? remarkController,
//       DocumentDetail? documentDetail}) {
//   Get.back();
//   if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
//     viewLeadDocController.getCustomerDocumentApproveRejectData(status: Strings.approve.toLowerCase(), isApprovedRequest : true, remark:remarkController!.text,documentDetail :documentDetail ,context: context);
//   } else if (identifier != null && identifier.equalsIgnoreCase(Strings.reject)) {
//     viewLeadDocController.getCustomerDocumentApproveRejectData(status: Strings.reject.toLowerCase(), isApprovedRequest : false, remark:remarkController!.text,documentDetail :documentDetail ,context: context);
//   }
// }
}
