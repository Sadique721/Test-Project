import 'package:savbill/pages/customer/customer_document_controller.dart';
import 'package:savbill/pages/customer/customer_document_list_item.dart';
import 'package:savbill/pages/customer/doc_create/create_doc_cust.dart';
import 'package:savbill/pages/customer/document_approve_reject_dialog.dart';
import 'package:savbill/pages/customer/model/response/customer_document_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
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

class CustomerDocumentList extends StatefulWidget {
  CustomerDocumentList({Key? key}) : super(key: key);

  @override
  _CustomerDocumentListState createState() => _CustomerDocumentListState();
}

class _CustomerDocumentListState extends State<CustomerDocumentList>
    with WidgetsBindingObserver
    implements DocumentApproveRejectBtnAction {
  final customerDocumentController = Get.put(CustomerDocumentController());

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    customerDocumentController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        return;
      case AppLifecycleState.resumed:
        if (customerDocumentController.checkBtnClickEvent) {
          customerDocumentController.setBtnClickEvent(false);
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
      child: GetBuilder<CustomerDocumentController>(builder: (controller) {
        return Stack(children: <Widget>[
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: customerDocumentController.isLoading),
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
          child: (customerDocumentController.documentList!.isNotEmpty)
              ? ListView.builder(
                  itemCount: customerDocumentController.documentList!.length,
                  itemBuilder: (context, index) {
                    DocumentDetail item =
                        customerDocumentController.documentList![index];
                    return CustomerDocumentListView(
                      item: item,
                      userDetail: customerDocumentController.userDetail,
                      onTapEdit: () {
                        openCustomerCreateDocumentScreen(Strings.edit,
                            customerDocumentController.customerId, item);
                      },
                      onTapDelete: () async {
                        // customerDeleteDocument(item);
                        customerDocumentController.setBtnClickEvent(false);
                        showDialog(
                          context: context,
                          builder: (BuildContext context) {
                            return AlertDialogHelper(
                                title: Strings.app_name,
                                message: Strings.msg_delete,
                                positiveBtnText: Strings.yes,
                                negativeBtnText: Strings.no,
                                positiveBtnClick: () {
                                  Get.back();
                                  customerDocumentController
                                      .customerUploadDocumentDelete(item);
                                },
                                negativeBtnClick: () {
                                  Get.back();
                                });
                          },
                        );
                      },
                      onTapVerify: () {
                        customerDocumentController.verifyCustomerDocument(item);
                      },
                      onTapPick: () {
                        customerDocumentController.pickUpDocument(item.docId);
                      },
                      onTapApprove: () {
                        customerDocumentController.entityId = item.docId;
                        addRemarkDocumentDialog(context, Strings.approve, item);
                      },
                      onTapReject: () {
                        customerDocumentController.entityId = item.docId;
                        addRemarkDocumentDialog(context, Strings.reject, item);
                      },
                      onTapDocumentDetial: () {
                        openCreditNoteStatus(int.parse(item.docId.toString()));
                      },
                      onTapReassign: () {
                        customerDocumentController.entityId = item.docId;
                        customerDocumentController.reassignWorkflowGetStaff(item.docId, "DOCUMENT_VERIFICATION");

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
                    Strings.add, customerDocumentController.customerId, null);
              },
              radius: 0,
              height: Constant.BOTTOM_BTN_HEIGHT,
              bgColors: AppTheme.colorPrimary,
              borderColors: AppTheme.colorPrimary,
              child: CustomText(
                title: Strings.create_cust_document,
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

  openCustomerCreateDocumentScreen(
      String? from, int? customerId, DocumentDetail? item) async {
    var result = Get.to(CreateDocumentCustomer(), arguments: {
      Constant.FROM: from,
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_DOCUMENT_DETAIL: item,
    });

    if (result != null && result == true) {
      customerDocumentController.getCustomerDocumentData();
    }
  }

  openCreditNoteStatus(int? eventId) async {
    var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "DOCUMENT_VERIFICATION"
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.customer_document, '', AppTheme.colorPrimary,
        false, backScreen, [], AppBar().preferredSize.height);
  }

  addRemarkDocumentDialog(
      BuildContext context, String? pageName, DocumentDetail item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return DocumentApproveRejectDialog(
              pageName: pageName,
              documentApproveRejectBtnAction: this,
              documentDetail:item);
          // paymentApproveRejectReq: PaymentApproveRejectReq(
          //     idlist:item.id,
          //     customerid: item.custId,
          //     paymode: item.paymode,
          //     paystatus: item.status,
          //     paytodate: item.paymentdate,
          //     referenceno: item.referenceno));
        });
  }

  @override
  void documentApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      DocumentDetail? documentDetail}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      customerDocumentController.getCustomerDocumentApproveRejectData(status: Strings.approve.toLowerCase(), isApprovedRequest : true, remark:remarkController!.text,documentDetail :documentDetail ,context: context);
    } else if (identifier != null && identifier.equalsIgnoreCase(Strings.reject)) {
      customerDocumentController.getCustomerDocumentApproveRejectData(status: Strings.reject.toLowerCase(), isApprovedRequest : false, remark:remarkController!.text,documentDetail :documentDetail ,context: context);
    }
  }
}
