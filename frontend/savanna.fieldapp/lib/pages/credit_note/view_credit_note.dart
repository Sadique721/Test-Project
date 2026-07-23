import 'dart:developer';

import 'package:savbill/pages/credit_note/create_credit_note.dart';
import 'package:savbill/pages/credit_note/credit_status_approve_reject_dialog.dart';
import 'package:savbill/pages/credit_note/response/credit_note_res.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/credit_note/view_credit_note_controller.dart';
import 'package:savbill/pages/customer_inventory/inventory_team_work_flow.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import '../../util/resources.dart';
import '../../widgets/input_textfield.dart';
import '../../widgets/simple_button.dart';
import '../dashboard/model/response/payment_status_data.dart';
import '../pending_approvals/model/request/payment_approve_reject_req.dart';
import 'create_credit_controller.dart';
import 'credit_customer_list.dart';
import 'credit_list_item.dart';

class ViewCreditNote extends StatefulWidget {
  @override
  _ViewCreditNoteState createState() => _ViewCreditNoteState();
}

class _ViewCreditNoteState extends State<ViewCreditNote>
    with WidgetsBindingObserver
    implements LogoutClickEvent, CreditApproveRejectBtnAction {
  final creditNoteController = Get.put(CreditNoteController());
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

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    creditNoteController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreditNoteController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: _creditNoteListKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: SafeArea(
                child: _body(),
              ),
            ),
          ),
          ProgressBar(isLoader: creditNoteController.isLoading),
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
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: Strings.credit_note,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    Row(children: [
                      PermissionService().hasAclPermission(
                                  [AclCreditNotes.GENERATE_CREDIT_NOTE]) ==
                              true
                          ? Material(
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
                            )
                          : SizedBox.shrink(),
                      const SizedBox(
                        width: Constant.MEDIUM_PADDING,
                      ),
                      InkWell(
                        onTap: () {
                          if (creditNoteController.filterViewOpen) {
                            creditNoteController.filterViewOpen = false;
                          } else {
                            creditNoteController.filterViewOpen = true;
                          }
                          creditNoteController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: creditNoteController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ])
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              creditNoteController.filterViewOpen
                  ? Container(
                      width: MediaQuery.of(context).size.width,
                      margin: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: Material(
                        color: AppTheme.colorWhite,
                        elevation: 1.5,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER - 2)),
                        child: Padding(
                          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              CoustomTextField(
                                  labelText: Strings.select_a_customer,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: creditNoteController
                                      .createCreditController
                                      .createCustomerController,
                                  suffixIcon: Padding(
                                    padding: const EdgeInsetsDirectional.all(
                                        Constant.LARGE_PADDING - 2),
                                    child: SvgPicture.asset(
                                      downArrowSvg,
                                      color: AppTheme.colorBlack,
                                      width: Constant.ICON_SIZE_S,
                                      height: Constant.ICON_SIZE_S,
                                    ),
                                  ),
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.done,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_select_customer;
                                    } else {}
                                    return null;
                                  },
                                  onTextFiledOnTap: () {
                                    openParentCustomerScreen();
                                  },
                                  readOnly: true),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              Row(children: [
                                Expanded(
                                  child: CoustomTextField(
                                      labelText: Strings.credit_from_date,
                                      suffixIcon: Padding(
                                        padding:
                                            const EdgeInsetsDirectional.all(
                                                Constant.MEDIUM_PADDING),
                                        child: SvgPicture.asset(
                                          calendarSvg,
                                          color: AppTheme.colorBlack,
                                          width: Constant.ICON_SIZE_S,
                                          height: Constant.ICON_SIZE_S,
                                          // myIcon is a 48px-wide widget.
                                        ),
                                      ),
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          creditNoteController
                                              .creditFormDateController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
                                      textColor: AppTheme.colorBlack,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.MEDIUM_PADDING),
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {},
                                      onTextFiledOnTap: () {
                                        selectDate(
                                            context,
                                            Strings.credit_from_date,
                                            DateTime(DateTime.now().year - 10),
                                            DateTime(DateTime.now().year + 10));
                                      },
                                      readOnly: true),
                                ),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                Expanded(
                                  child: CoustomTextField(
                                      labelText: Strings.credit_to_date,
                                      suffixIcon: Padding(
                                        padding:
                                            const EdgeInsetsDirectional.all(
                                                Constant.MEDIUM_PADDING),
                                        child: SvgPicture.asset(
                                          calendarSvg,
                                          color: AppTheme.colorBlack,
                                          width: Constant.ICON_SIZE_S,
                                          height: Constant.ICON_SIZE_S,
                                          // myIcon is a 48px-wide widget.
                                        ),
                                      ),
                                      hintColor: AppTheme.colorIconGrey,
                                      textEditingController:
                                          creditNoteController
                                              .creditToDateController,
                                      borderEnableColors:
                                          AppTheme.colorIconGrey,
                                      borderFocusColors: AppTheme.colorIconGrey,
                                      textColor: AppTheme.colorBlack,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.w500,
                                      contentPadding:
                                          const EdgeInsets.symmetric(
                                              horizontal:
                                                  Constant.MEDIUM_PADDING),
                                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                                      onTextValidator: (String? value) {},
                                      onTextFiledOnTap: () {
                                        selectDate(
                                            context,
                                            Strings.credit_to_date,
                                            DateTime(DateTime.now().year - 10),
                                            DateTime(DateTime.now().year + 10));
                                      },
                                      readOnly: true),
                                ),
                              ]),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              DropdownButtonHideUnderline(
                                child: DropdownButtonFormField(
                                  icon: SvgPicture.asset(
                                    downArrowSvg,
                                    height: Constant.DROP_DOWN_ARROW_W_H,
                                    width: Constant.DROP_DOWN_ARROW_W_H,
                                    color: AppTheme.colorBlack,
                                    fit: BoxFit.fill,
                                  ),
                                  decoration: InputDecoration(
                                      filled: true,
                                      contentPadding: const EdgeInsets.fromLTRB(
                                          Constant.LARGE_PADDING,
                                          0,
                                          Constant.LARGE_PADDING,
                                          0),
                                      fillColor: AppTheme.colorWhite,
                                      hintText: Strings.credit_status,
                                      hintStyle: AppTheme.dropdownHintStyle,
                                      labelStyle: AppTheme.dropdownLabelStyle,
                                      errorStyle: AppTheme.dropdownErrorStyle,
                                      alignLabelWithHint: true,
                                      border: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorIconGrey,
                                            width: 0.8),
                                      ),
                                      focusColor: Colors.transparent,
                                      focusedBorder: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                            color: AppTheme.colorIconGrey,
                                            width: 0.8),
                                      ),
                                      enabledBorder: OutlineInputBorder(
                                        borderRadius: BorderRadius.circular(
                                            Constant.DROP_DOWN_ROUNDED_CORNER),
                                        borderSide: BorderSide(
                                          color: AppTheme.colorIconGrey,
                                          width: 1.0,
                                        ),
                                      ),
                                      errorMaxLines: 3),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: creditNoteController
                                      .selectedPaymentStatus,
                                  items: creditNoteController.creditStatusList!
                                      .map((PaymentStatus value) {
                                    return DropdownMenuItem<PaymentStatus>(
                                      value: value,
                                      child: Text(value.status!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    creditNoteController.selectedPaymentStatus =
                                        value as PaymentStatus?;
                                    creditNoteController.update();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              Row(
                                children: [
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.reference_no,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            creditNoteController
                                                .referenceController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        keyboardType: TextInputType.text,
                                        maxLines: 1,
                                        maxLength: 6,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {},
                                        readOnly: false),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.invoice_no,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            creditNoteController
                                                .invoiceNumberController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        keyboardType: TextInputType.text,
                                        maxLines: 1,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {},
                                        readOnly: false),
                                  ),
                                ],
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              Row(
                                children: [
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.mobile_number,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            creditNoteController
                                                .mobileNumberController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        keyboardType: TextInputType.number,
                                        maxLength: 10,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {},
                                        readOnly: false),
                                  ),
                                  const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  ),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.document_no,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            creditNoteController
                                                .documentNumberController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        keyboardType: TextInputType.text,
                                        maxLines: 1,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {},
                                        readOnly: false),
                                  ),
                                ],
                              ),
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Expanded(
                                    child: SimpleButton(
                                      onTap: () {
                                        creditNoteController.applyFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
                                      bgColors: AppTheme.colorPrimary,
                                      child: CustomText(
                                        title: Strings.apply,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.w500,
                                      ),
                                    ),
                                  ),
                                  const SizedBox(
                                    width: Constant.LARGE_PADDING,
                                  ),
                                  Expanded(
                                    child: SimpleButton(
                                      onTap: () {
                                        creditNoteController
                                            .createCreditController
                                            .createCustomerController
                                            .clear();
                                        creditNoteController.clearFilter();
                                      },
                                      radius: Constant.BTN_HEIGHT_M,
                                      height: Constant.BTN_HEIGHT_M,
                                      bgColors: AppTheme.colorBlack,
                                      borderColors: AppTheme.colorBlack,
                                      child: CustomText(
                                        title: Strings.clear,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.w500,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    )
                  : Container(),
              creditNoteController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : Container(),
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
                            return CreditViewListItem(
                              index: index,
                              item: creditNoteController.creditNoteList![index],
                              userId: creditNoteController
                                  .creditNoteList![index].custId!,
                              isShowBtn: true,
                              currency: creditNoteController.currencySymbol,
                              controller: creditNoteController,
                              onApproveTap: () {
                                creditNoteController.entityId =
                                    creditNoteController
                                        .creditNoteList![index].id;
                                addRemarkInvoiceDialog(
                                    context,
                                    Strings.approve,
                                    creditNoteController
                                        .creditNoteList![index]);
                              },
                              onRejectTap: () {
                                creditNoteController.entityId =
                                    creditNoteController
                                        .creditNoteList![index].id;
                                addRemarkInvoiceDialog(
                                    context,
                                    Strings.reject,
                                    creditNoteController
                                        .creditNoteList![index]);
                              },
                              onDownloadTap: () {
                                creditNoteController.entityId =
                                    creditNoteController
                                        .creditNoteList![index].id;
                                creditNoteController.fileDownloading(
                                    Strings.credit_note,
                                    "${UrlConstants.payment_receipt_url}${creditNoteController.entityId}",
                                    creditNoteController
                                        .creditNoteList![index].customerName);
                                // creditNoteController.update();
                                // creditNoteController.checkPermissionAndDownload();
                              },
                              onAuditStatusTap: () {
                                openCreditNoteStatus(creditNoteController
                                    .creditNoteList![index].id);
                              },
                              onInvoiceTap: () {
                                creditNoteController.entityId =
                                    creditNoteController
                                        .creditNoteList![index].id;
                                creditNoteController.fileDownloading(
                                    Strings.credit_note,
                                    "${UrlConstants.payment_receipt_url}${creditNoteController.entityId}",
                                    creditNoteController
                                        .creditNoteList![index].customerName);
                              },
                              onCustomerTap: () {
                                // openCustomerDetailScreen(item.custId);
                              },
                              onPickTab: () {
                                creditNoteController.pickUpCreditNote(
                                    creditNoteController
                                        .creditNoteList![index].id);
                              },
                              onReassignTab: () {
                                creditNoteController.entityId =
                                    creditNoteController
                                        .creditNoteList![index].id;
                                creditNoteController.reassignWorkflowGetStaff(
                                    creditNoteController
                                        .creditNoteList![index].id,
                                    "CREDIT_NOTE");
                              },
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
    return DynamicAppBar(Strings.credit_note, '', AppTheme.colorPrimary, true,
        _onMenuClick, [], AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (_creditNoteListKey.currentState!.isDrawerOpen) {
      _creditNoteListKey.currentState?.closeDrawer();
    } else {
      _creditNoteListKey.currentState?.openDrawer();
    }
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

  openParentCustomerScreen() async {
    var result = await Get.to(CreditCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      if (data != null) {
        creditNoteController.createCreditController.selectedCustomer = data;
        creditNoteController.createCreditController.selectedInvoice = null;
        creditNoteController.createCreditController.invoiceList!.clear();
        creditNoteController.createCreditController
            .getCreditInvoiceListData(data.id!);
        creditNoteController
            .createCreditController.createCustomerController.text = data.name!;
        creditNoteController.createCreditController.update();
      }
    }
  }

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
    if (Get.isRegistered<CreateCreditController>()) {
      final createCreditController = Get.find<CreateCreditController>();
      createCreditController.selectedCustomer = null;
      createCreditController.selectedInvoice = null;
      createCreditController.creditAmount = "".obs;
      createCreditController.createCustomerController.clear();
      createCreditController.invoiceController.clear();
      createCreditController.referenceNumberController.clear();
      createCreditController.remarksController.clear();
      createCreditController.update();
    }
    var result = await Get.to(() => CreateCreditNote(), arguments: {});
    if (result != null && result == true) {
      // CustomerCreditList data = result;
      // if (data != null) {
      // creditNoteController.getCreditNoteListData();
      creditNoteController.clearFilter();
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
