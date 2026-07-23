import 'package:savbill/pages/customer_caf_invoice/cust_caf_invoice_detail_controller.dart';
import 'package:savbill/pages/customer_invoice/cust_invoice_detail/cust_invoice_tax_detail_dialog.dart';
import 'package:savbill/pages/customer_invoice/cust_invoice_detail/model/cust_invoice_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerCAFInvoiceDetailScreen extends StatefulWidget {
  @override
  _CustomerInvoiceDetailState createState() => _CustomerInvoiceDetailState();
}

class _CustomerInvoiceDetailState extends State<CustomerCAFInvoiceDetailScreen> {
  final custCAFInvoiceDetailController =
  Get.put(CustomerCAFInvoiceDetailController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCAFInvoiceDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: custCAFInvoiceDetailController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
      margin: const EdgeInsets.only(
        top: Constant.SMALL_PADDING,
      ),
      color: AppTheme.colorBG,
      child: custCAFInvoiceDetailController.customerDetail != null
          ? SingleChildScrollView(
        physics: const ScrollPhysics(),
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              basicDetailView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              amountDetailView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              chargeDetailView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
            ]),
      )
          : noDataFound(),
    );
  }

  basicDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        clipBehavior: Clip.antiAlias,
        color: AppTheme.colorWhite,
        child: Theme(
          data: ThemeData(
            dividerColor: Colors.transparent,
          ),
          child: ExpansionTile(
            key: const Key(Strings.basic_details),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            initiallyExpanded: true,
            tilePadding:
            const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.basic_details,
              fontSize: AppTheme.medium,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontWeight: FontWeight.w600,
            ),
            onExpansionChanged: ((newState) {}),
            children: <Widget>[
              Container(
                width: Get.width,
                height: 1.5,
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(6),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.4),
                        blurRadius: 1.5,
                        spreadRadius: 1.5,
                      ),
                    ]),
              ),
              Padding(
                padding: const EdgeInsets.only(
                    top: Constant.EXPANTABLE_ITEM_MARGIN,
                    left: Constant.EXPANTABLE_ITEM_MARGIN,
                    right: Constant.EXPANTABLE_ITEM_MARGIN,
                    bottom: 0),
                child: Container(
                  alignment: Alignment.topLeft,
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        basicDetailItem(
                            Strings.name,
                            "${custCAFInvoiceDetailController.custInvoiceDetails != null ? custCAFInvoiceDetailController.custInvoiceDetails!.customerName.toString().capitalizeFirst : ""}",
                            Strings.document_no,
                            custCAFInvoiceDetailController
                                .custInvoiceDetails?.docnumber ??
                                "-",
                            null,
                            false,
                            false),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        basicDetailItem(
                            Strings.bill_run_Id,
                            custCAFInvoiceDetailController
                                .custInvoiceDetails?.billrunid
                                .toString() ??
                                "-",
                            Strings.bill_date,
                            custCAFInvoiceDetailController
                                .custInvoiceDetails?.billdate ??
                                "-",
                            null,
                            false,
                            false),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        basicDetailItem(
                            Strings.status,
                            custCAFInvoiceDetailController
                                .custInvoiceDetails?.billrunstatus ??
                                "-",
                            "-",
                            "-",
                            null,
                            false,
                            false),
                        const SizedBox(height: Constant.SMALL_PADDING),
                      ]),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  amountDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
        clipBehavior: Clip.antiAlias,
        color: AppTheme.colorWhite,
        child: Theme(
          data: ThemeData(
            dividerColor: Colors.transparent,
          ),
          child: ExpansionTile(
            key: const Key(Strings.basic_details),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            initiallyExpanded: true,
            tilePadding:
            const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.amount_details,
              fontSize: AppTheme.medium,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontWeight: FontWeight.w600,
            ),
            onExpansionChanged: ((newState) {}),
            children: <Widget>[
              Container(
                width: Get.width,
                height: 1.5,
                decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(6),
                    boxShadow: [
                      BoxShadow(
                        color: Colors.grey.withOpacity(0.3),
                        blurRadius: 1.5,
                        spreadRadius: 1.5,
                      ),
                    ]),
              ),
              Padding(
                padding: const EdgeInsets.only(
                    top: Constant.EXPANTABLE_ITEM_MARGIN,
                    left: Constant.EXPANTABLE_ITEM_MARGIN,
                    right: Constant.EXPANTABLE_ITEM_MARGIN,
                    bottom: 0),
                child: Container(
                  alignment: Alignment.topLeft,
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        basicDetailItem(
                            Strings.total_amount,
                            custCAFInvoiceDetailController.custInvoiceDetails !=
                                null
                                ? custCAFInvoiceDetailController
                                .custInvoiceDetails!.totalamount!
                                .toStringAsFixed(2)
                                : "",
                            Strings.discount_without,
                            custCAFInvoiceDetailController.custInvoiceDetails !=
                                null
                                ? custCAFInvoiceDetailController
                                .custInvoiceDetails!.discount
                                .toString()
                                : "",
                            null,
                            false,
                            false),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        Row(
                          mainAxisSize: MainAxisSize.max,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              flex: 3,
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  titleWidget(
                                    Strings.tax,
                                  ),
                                  const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING - 1),
                                  InkWell(
                                    child: valueWidget(
                                        custCAFInvoiceDetailController
                                            .custInvoiceDetails !=
                                            null
                                            ? custCAFInvoiceDetailController
                                            .custInvoiceDetails!.tax!
                                            .toStringAsFixed(2)
                                            : "",
                                        true),
                                    onTap: () {
                                      custCAFInvoiceDetailController
                                          .openTotalTaxModel();
                                      showTotalTaxInvoiceDialog(
                                          custCAFInvoiceDetailController
                                              .taxTotalData,
                                          Strings.amount);
                                    },
                                  ),
                                ],
                              ),
                            ),
                            Expanded(
                              flex: 2,
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  titleWidget(Strings.late_payment_date),
                                  const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING - 1),
                                  valueWidget(
                                      custCAFInvoiceDetailController
                                          .custInvoiceDetails !=
                                          null
                                          ? custCAFInvoiceDetailController
                                          .custInvoiceDetails!
                                          .latepaymentdate ??
                                          ""
                                          : "",
                                      false),
                                ],
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        Row(
                          mainAxisSize: MainAxisSize.max,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Flexible(
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  titleWidget(Strings.amount_in_words),
                                  const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING - 1),
                                  CustomText(
                                    title: custCAFInvoiceDetailController
                                        .custInvoiceDetails !=
                                        null
                                        ? custCAFInvoiceDetailController
                                        .custInvoiceDetails!.amountinwords!
                                        : "",
                                    colors: AppTheme.lable_noramal,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small + 1,
                                    fontWeight: FontWeight.w400,
                                    decoration: TextDecoration.none,
                                    maxLines: 2,
                                  )
                                ],
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        basicDetailItem(
                            Strings.start_date,
                            custCAFInvoiceDetailController.custInvoiceDetails !=
                                null
                                ? custCAFInvoiceDetailController
                                .custInvoiceDetails!.startdate!
                                : "",
                            Strings.end_date,
                            custCAFInvoiceDetailController.custInvoiceDetails !=
                                null
                                ? custCAFInvoiceDetailController
                                .custInvoiceDetails!.endate!
                                : "",
                            null,
                            false,
                            false),
                        const SizedBox(height: Constant.SMALL_PADDING),
                        basicDetailItem(
                            Strings.create_date,
                            custCAFInvoiceDetailController.custInvoiceDetails !=
                                null
                                ? custCAFInvoiceDetailController
                                .custInvoiceDetails!.createdate!
                                : "",
                            "",
                            "",
                            null,
                            false,
                            false),
                      ]),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  chargeDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.charge_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          children: <Widget>[
            Container(
              width: Get.width,
              height: 1.5,
              decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(6),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.grey.withOpacity(0.4),
                      blurRadius: 1.5,
                      spreadRadius: 1.5,
                    ),
                  ]),
            ),
            Padding(
              padding: const EdgeInsets.only(
                  top: Constant.EXPANTABLE_ITEM_MARGIN,
                  left: Constant.EXPANTABLE_ITEM_MARGIN,
                  right: Constant.EXPANTABLE_ITEM_MARGIN,
                  bottom: 0),
              child: Container(
                alignment: Alignment.topLeft,
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // basicDetailItem(
                      //     Strings.bill_to,
                      //     ticketCustomerDetailController.customerBill ?? "",
                      //     ticketCustomerDetailController.ifPlanGroup == true
                      //         ? Strings.plan_group
                      //         : "",
                      //     ticketCustomerDetailController.ifPlanGroup == true
                      //         ? ticketCustomerDetailController
                      //         .plansByPlanGroupIdList![0].name
                      //         : "-",
                      //     null,
                      //     false,
                      //     false),
                      // const SizedBox(height: Constant.SMALL_PADDING),
                      // ticketCustomerDetailController.ifPlanGroup == true
                      //     ? basicDetailItem(
                      //     Strings.discount,
                      //     ticketCustomerDetailController.customerDetail !=
                      //         null
                      //         ? ticketCustomerDetailController
                      //         .customerDetail!.discount
                      //         .toString()
                      //         : "-",
                      //     "",
                      //     "-",
                      //     null,
                      //     false,
                      //     false)
                      //     : const SizedBox.shrink(),
                      // const SizedBox(height: Constant.SMALL_PADDING),
                      custCAFInvoiceDetailController.debitDocDetails != null &&
                          custCAFInvoiceDetailController
                              .debitDocDetails!.isNotEmpty
                          ? Card(
                        color: AppTheme.colorWhite,
                        child: (custCAFInvoiceDetailController
                            .debitDocDetails!.isNotEmpty)
                            ? ListView.builder(
                            physics:
                            const NeverScrollableScrollPhysics(),
                            scrollDirection: Axis.vertical,
                            shrinkWrap: true,
                            itemCount: custCAFInvoiceDetailController
                                .debitDocDetails!.length,
                            itemBuilder: (context, ii) {
                              DebitDocDetail? items =
                              custCAFInvoiceDetailController
                                  .debitDocDetails![ii];
                              int? lstLength =
                                  custCAFInvoiceDetailController
                                      .debitDocDetails!.length;
                              return Padding(
                                padding: const EdgeInsets.only(
                                    top: Constant
                                        .EXPANTABLE_ITEM_MARGIN,
                                    left: Constant
                                        .EXPANTABLE_ITEM_MARGIN,
                                    right: Constant
                                        .EXPANTABLE_ITEM_MARGIN,
                                    bottom: Constant
                                        .EXPANTABLE_ITEM_MARGIN),
                                child: Container(
                                  decoration: BoxDecoration(
                                    color: AppTheme.expantableItemBg,
                                    border: Border.all(
                                        color: AppTheme
                                            .expantableItemBg),
                                    borderRadius:
                                    const BorderRadius.all(
                                      Radius.circular(3),
                                    ),
                                  ),
                                  child: Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Column(
                                      mainAxisAlignment:
                                      MainAxisAlignment.start,
                                      crossAxisAlignment:
                                      CrossAxisAlignment.start,
                                      children: [
                                        basicDetailItem(
                                            Strings.charge_name,
                                            items.chargename ?? "-",
                                            Strings.sub_total,
                                            items.subtotal!
                                                .toStringAsFixed(
                                                2) ??
                                                "-",
                                            null,
                                            false,
                                            false),
                                        const SizedBox(
                                            height: Constant
                                                .SMALL_PADDING),
                                        basicDetailItem(
                                            Strings.discount_without,
                                            "${items.discount}",
                                            Strings.discount,
                                            items.discount != null
                                                ? items.discount!
                                                .toStringAsFixed(
                                                2)
                                                : "-",
                                            null,
                                            false,
                                            false),
                                        const SizedBox(
                                            height: Constant
                                                .SMALL_PADDING),
                                        // basicDetailItem(
                                        //     Strings.tax,
                                        //     items.tax != null
                                        //         ? items.tax!.toStringAsFixed(2)
                                        //         : "-",
                                        //     Strings.total_amount,
                                        //     items.totalamount != null
                                        //         ? items.totalamount!.toStringAsFixed(2)
                                        //         : "-",
                                        //     null,
                                        //     false,
                                        //     false),

                                        Row(
                                          mainAxisSize:
                                          MainAxisSize.max,
                                          crossAxisAlignment:
                                          CrossAxisAlignment
                                              .center,
                                          mainAxisAlignment:
                                          MainAxisAlignment
                                              .spaceBetween,
                                          children: [
                                            Flexible(
                                              flex: 3,
                                              child: Column(
                                                mainAxisAlignment:
                                                MainAxisAlignment
                                                    .start,
                                                crossAxisAlignment:
                                                CrossAxisAlignment
                                                    .start,
                                                children: [
                                                  titleWidget(
                                                      Strings.tax),
                                                  const SizedBox(
                                                      height: Constant
                                                          .VERY_SMALL_PADDING -
                                                          1),
                                                  InkWell(
                                                    onTap: () {
                                                      custCAFInvoiceDetailController
                                                          .openTaxModel(
                                                          items
                                                              .debitdocdetailid,
                                                          "charge");
                                                      showTaxInvoiceDialog(
                                                          custCAFInvoiceDetailController
                                                              .taxData,
                                                          Strings
                                                              .charge);
                                                    },
                                                    child: valueWidget(
                                                        items.tax !=
                                                            null
                                                            ? items
                                                            .tax!
                                                            .toStringAsFixed(
                                                            2)
                                                            : "-",
                                                        true),
                                                  ),
                                                ],
                                              ),
                                            ),
                                            Expanded(
                                              flex: 2,
                                              child: Column(
                                                mainAxisAlignment:
                                                MainAxisAlignment
                                                    .start,
                                                crossAxisAlignment:
                                                CrossAxisAlignment
                                                    .start,
                                                children: [
                                                  titleWidget(Strings
                                                      .total_amount),
                                                  const SizedBox(
                                                      height: Constant
                                                          .VERY_SMALL_PADDING -
                                                          1),
                                                  valueWidget(
                                                      items.totalamount !=
                                                          null
                                                          ? items
                                                          .totalamount!
                                                          .toStringAsFixed(
                                                          2)
                                                          : "-",
                                                      false),
                                                ],
                                              ),
                                            ),
                                          ],
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                              );
                            })
                            : Container(),
                      )
                          : noDataFound(),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  basicDetailItem(String title1, String? value1, String title2, String? value2,
      Function()? onTap1, bool? isLink1, bool? isLink2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              InkWell(
                child: valueWidget(value1, isLink1!),
                onTap: onTap1,
              ),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2, isLink2!),
            ],
          ),
        ),
      ],
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.invoiceMasterDetail, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w700,
      maxLines: 2,
    );
  }

  valueWidget(String? value, bool? isLinkable) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors:
      isLinkable == true ? AppTheme.colorPrimary : AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable! ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }

  showTaxInvoiceDialog(List<DebitDocumentTAXReels>? item, String? type) {
    showDialog(
        context: Get.overlayContext!,
        barrierDismissible: true,
        builder: (_) {
          return CustInvoiceTaxDialog(
            debitDocumentTAXReelsList: item,
            type: type,
          );
        });
  }

  showTotalTaxInvoiceDialog(List<DebitDocumentTAXRelDtos>? item, String? type) {
    showDialog(
        context: Get.overlayContext!,
        barrierDismissible: true,
        builder: (_) {
          return CustInvoiceTaxDialog(
              debitDocumentTAXRelDtosList: item, type: type);
        });
  }
}
