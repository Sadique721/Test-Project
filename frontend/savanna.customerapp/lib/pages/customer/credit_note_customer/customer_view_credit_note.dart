import 'package:savbill/pages/credit_note/create_credit_note.dart';
import 'package:savbill/pages/credit_note/credit_status_approve_reject_dialog.dart';
import 'package:savbill/pages/credit_note/response/credit_note_res.dart';
import 'package:savbill/pages/customer/credit_note_customer/customer_credit_veiw_item_list.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/pending_approvals/model/request/payment_approve_reject_req.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import 'customer_view_credit_note_controller.dart';

class CustomerViewCreditNote extends StatefulWidget {
  @override
  _CustomerViewCreditNoteState createState() => _CustomerViewCreditNoteState();
}

class _CustomerViewCreditNoteState extends State<CustomerViewCreditNote>
    with WidgetsBindingObserver
    implements LogoutClickEvent, CreditApproveRejectBtnAction {
  final creditNoteController = Get.put(CustomerCreditNoteController());
  final GlobalKey<ScaffoldState> _creditNoteListKey = GlobalKey();

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    creditNoteController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        //customerListController.setBtnClickEvent(false);
        return;
      case AppLifecycleState.resumed:
        if (creditNoteController.checkBtnClickEvent) {
          creditNoteController.setBtnClickEvent(false);
          // check permission
        }
        return;
      default:
        return;
    }
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCreditNoteController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          appBar: _appBar(),
          body: _body(),
          backgroundColor: AppTheme.colorBG,
        ),
        ProgressBar(isLoader: creditNoteController.isLoading),
      ]);
    });
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
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title:
                            "${creditNoteController.customerDetail!.title.toString().capitalizeFirst} ${creditNoteController.customerDetail!.custname} ${Strings.credit_note} ${Strings.details}",
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    PermissionService().hasAclPermission([
                      creditNoteController.customerType != null &&  creditNoteController.customerType!
                                      .equalsIgnoreCase('Prepaid')
                                  ? AclPreCustConstants.PRE_CUST_CREDIT_NOTE
                                  : AclPostCustConstants.POST_CUST_CREDIT_NOTE
                            ]) ==
                            true
                        ? Row(children: [
                            Material(
                              color: AppTheme.colorWhite,
                              elevation: 2,
                              shape: RoundedRectangleBorder(
                                  borderRadius: BorderRadius.circular(20)),
                              child: InkWell(
                                onTap: () {
                                  openCreateCreditScreen();
                                },
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: AppTheme.colorPrimary,
                                    borderRadius: const BorderRadius.all(
                                        Radius.circular(20)),
                                  ),
                                  padding: const EdgeInsets.all(6),
                                  child: Icon(
                                    Icons.add,
                                    color: AppTheme.colorWhite,
                                    size: 22,
                                  ),
                                ),
                              ),
                            ),
                          ])
                        : SizedBox.shrink()
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (creditNoteController.creditNoteList != null &&
                        creditNoteController.creditNoteList!.isNotEmpty)
                    ? ListView.builder(
                        controller: creditNoteController.controller,
                        scrollDirection: Axis.vertical,
                        itemCount:
                            creditNoteController.creditNoteList!.length + 1,
                        itemBuilder: (context, index) {
                          // CreditNoteDetailsList item = creditNoteController.creditNoteList![index];
                          if (index ==
                              creditNoteController.creditNoteList?.length) {
                            if (creditNoteController.isShowLoadMore) {
                              return Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
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
                            return CustomerCreditViewListItem(
                              index: index,
                              item: creditNoteController.creditNoteList![index],
                              userId: creditNoteController
                                  .creditNoteList![index].custId!,
                              isShowBtn: true,
                              currency: creditNoteController.currencySymbol,
                              controller: creditNoteController,
                            );
                          }
                        })
                    : noDataFound(),
              ),
              /*Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                    onTap: () {
                      openCreateCreditScreen();
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.generate_credit_note,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ))
                ],
              ),*/
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.credit_note, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      Get.offAllNamed(AppRoutes.DASHBOARD,
          arguments: {Constant.FROM: Strings.payment_system});
    }
  }

  @override
  void logoutClick() {
    creditNoteController.getStorage.remove(Constant.USER_DATA);
    creditNoteController.getStorage.remove(Constant.USER_TOKEN);
    creditNoteController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  // openParentCustomerScreen() async {
  //   var result = await Get.to(CreditCustomerList(), arguments: {});
  //   if (result != null) {
  //     CustomerCreditList data = result;
  //     if (data != null) {
  //       creditNoteController.createCreditController.selectedCustomer = data;
  //       creditNoteController.createCreditController.selectedInvoice = null;
  //       creditNoteController.createCreditController.invoiceList!.clear();
  //       creditNoteController.createCreditController
  //           .getCreditInvoiceListData(data.id!);
  //       creditNoteController
  //           .createCreditController.createCustomerController.text = data.name!;
  //       creditNoteController.createCreditController.update();
  //     }
  //   }
  // }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.credit_from_date) {
      if (creditNoteController.selectedPayFromDate != null) {
        selectedDate = creditNoteController.selectedPayFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.credit_to_date) {
      if (creditNoteController.selectedPayToDate != null) {
        selectedDate = creditNoteController.selectedPayToDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != selectedDate) {
      if (identity == Strings.credit_from_date) {
        creditNoteController.selectedPayFromDate = picked;
        creditNoteController.creditFormDateController.text =
            creditNoteController.dateFormat.format(picked);
        creditNoteController.selectedPayFromDateApi =
            creditNoteController.apiDateFormat.format(picked);
      }
      if (identity == Strings.credit_to_date) {
        creditNoteController.selectedPayToDate = picked;
        creditNoteController.creditToDateController.text =
            creditNoteController.dateFormat.format(picked);
        creditNoteController.selectedPayToDateApi =
            creditNoteController.apiDateFormat.format(picked);
      }
      creditNoteController.update();
    }
  }

  openCreateCreditScreen() async {
    var result = await Get.to(CreateCreditNote(), arguments: {
      Constant.CUSTOMER_DETAIL: creditNoteController.customerDetail,
    });
    if (result != null) {
      // CustomerCreditList data = result;
      // if (data != null) {
      creditNoteController.getCreditNoteListData();
      creditNoteController.update();
      // }
    }
  }

  openCreditNoteStatus(int? eventId) async {
    var result = await Get.to(const InventoryTeamWorkFlow(), arguments: {
      Constant.ID: eventId,
      Constant.EVENT_TYPE: "CREDIT_NOTE"
      // Constant.
    });
    if (result != null && result == true) {
      // inventoryDetailController.getTeamHierarchyApprovalFlow(eventId);
    }
  }

  addRemarkInvoiceDialog(
      BuildContext context, String? pageName, CreditNoteDetailsList item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CreditApproveRejectDialog(
              pageName: pageName,
              creditApproveRejectBtnAction: this,
              paymentApproveRejectReq: PaymentApproveRejectReq(
                  idlist: item.id,
                  customerid: item.custId,
                  paymode: item.paymode,
                  paystatus: item.status,
                  paytodate: item.paymentdate,
                  referenceno: item.referenceno));
        });
  }

  @override
  void creditApproveRejectStatus({
    String? identifier,
    TextEditingController? remarkController,
    PaymentApproveRejectReq? paymentApproveRejectReq,
  }) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      creditNoteController.approveRejectCreditPayment(
          Strings.approve.toLowerCase(), paymentApproveRejectReq!, context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      creditNoteController.approveRejectCreditPayment(
          Strings.reject.toLowerCase(), paymentApproveRejectReq!, context);
    }
  }
}
