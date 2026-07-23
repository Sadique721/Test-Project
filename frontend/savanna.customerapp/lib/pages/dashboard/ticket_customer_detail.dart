import 'package:savbill/pages/dashboard/ticket_customer_detail_controller.dart';
import 'package:savbill/pages/service_management/request/add_service_req.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TicketCustomerDetail extends StatefulWidget {
  @override
  _TicketDetailState createState() => _TicketDetailState();
}

class _TicketDetailState extends State<TicketCustomerDetail> {
  final ticketCustomerDetailController =
      Get.put(TicketCustomerDetailController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TicketCustomerDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: ticketCustomerDetailController.isLoading),
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
      child: ticketCustomerDetailController.customerDetail != null
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
                    kycDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    contactDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    subscriberLocationDetailsView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    additionalServiceDetailsView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    businessPartnerDetailsView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    paymentDetailsView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    presentAddressDetails(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    paymentAddressDetails(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    permanentAddressDetails(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    planDetails(),
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
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
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
                          "${ticketCustomerDetailController.customerDetail!.title} ${ticketCustomerDetailController.customerDetail!.firstname!.capitalizeFirst} ${ticketCustomerDetailController.customerDetail!.lastname!.capitalizeFirst ?? ""}",
                          Strings.contact_person,
                          ticketCustomerDetailController
                                  .customerDetail?.contactperson ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.caf_no,
                          ticketCustomerDetailController
                                  .customerDetail?.cafno ??
                              "-",
                          Strings.account_number,
                          ticketCustomerDetailController
                                  .customerDetail?.acctno ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.username,
                          ticketCustomerDetailController
                                  .customerDetail?.username ??
                              "-",
                          Strings.status,
                          ticketCustomerDetailController
                                  .customerDetail?.status ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.customer_type,
                          ticketCustomerDetailController
                                  .customerDetail?.custtype ??
                              "-",
                          Strings.calendar_type,
                          ticketCustomerDetailController
                                  .customerDetail?.calendarType ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.next_bill_date,
                          ticketCustomerDetailController.apiDateFormat.format(
                              DateTime.parse(ticketCustomerDetailController
                                  .customerDetail?.nextBillDate)),
                          "",
                          "-",
                          null,
                          false,
                          false),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  kycDetailView() {
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
            title: Strings.kyc_details,
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
                          Strings.gst,
                          ticketCustomerDetailController.customerDetail!.gst ??
                              "",
                          Strings.pan_no,
                          ticketCustomerDetailController.customerDetail?.pan ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.national_id,
                          ticketCustomerDetailController
                                  .customerDetail?.aadhar ??
                              "-",
                          Strings.passport_no,
                          ticketCustomerDetailController
                                  .customerDetail?.passportNo ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.vat, "-", "", "", null, false, false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  contactDetailView() {
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
            title: Strings.contact_details,
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
                          Strings.mobile,
                          "(${ticketCustomerDetailController.customerDetail!.countryCode}) ${ticketCustomerDetailController.customerDetail!.mobile}" ??
                              "",
                          Strings.tel_phone,
                          ticketCustomerDetailController
                                  .customerDetail?.phone ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.email,
                          ticketCustomerDetailController
                                  .customerDetail?.email ??
                              "-",
                          "",
                          "",
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
    );
  }

  subscriberLocationDetailsView() {
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
            title: Strings.subscriber_location_details,
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
                          Strings.latitude,
                          ticketCustomerDetailController
                                  .customerDetail!.latitude ??
                              "",
                          Strings.longitude,
                          ticketCustomerDetailController
                                  .customerDetail?.longitude ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.service_area,
                          ticketCustomerDetailController
                                  .customerDetail?.serviceareaName ??
                              "-",
                          "",
                          "",
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
    );
  }

  additionalServiceDetailsView() {
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
            title: Strings.additional_service_details,
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
                          Strings.voice_service_type,
                          ticketCustomerDetailController
                                  .customerDetail!.voicesrvtype ??
                              "",
                          Strings.did_no,
                          ticketCustomerDetailController
                                  .customerDetail?.didno ??
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
    );
  }

  paymentDetails() {
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
            title: Strings.payment_details,
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
                          Strings.amount,
                          ticketCustomerDetailController.customerDetail != null
                              ? ticketCustomerDetailController
                                  .customerDetail!.creditDocuments![0].amount
                                  .toString()
                              : "",
                          Strings.reference_no,
                          ticketCustomerDetailController.customerDetail != null
                              ? ticketCustomerDetailController.customerDetail!
                                  .creditDocuments![0].referenceno
                              : "",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.payment_date,
                          ticketCustomerDetailController.customerDetail != null
                              ? ticketCustomerDetailController.customerDetail!
                                  .creditDocuments![0].paymentdate
                              : "",
                          Strings.payment_mode,
                          ticketCustomerDetailController.customerDetail != null
                              ? ticketCustomerDetailController
                                  .customerDetail!.creditDocuments![0].paymode
                              : "",
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
    );
  }

  businessPartnerDetailsView() {
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
            title: Strings.business_partner_details,
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
                          Strings.partner,
                          ticketCustomerDetailController.partnerList != null
                              ? ticketCustomerDetailController.partnerList!.name
                              : "",
                          Strings.sales_mark,
                          ticketCustomerDetailController
                                  .customerDetail?.salesremark ??
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
    );
  }

  paymentDetailsView() {
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
            title: Strings.payment_details,
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
                          Strings.amount,
                          ticketCustomerDetailController
                                          .customerDetail!.creditDocuments !=
                                      null &&
                                  ticketCustomerDetailController.customerDetail!
                                      .creditDocuments!.isNotEmpty
                              ? ticketCustomerDetailController
                                  .customerDetail!.creditDocuments![0].amount
                                  .toString()
                              : ""
                                  "",
                          Strings.reference_no,
                          ticketCustomerDetailController
                                          .customerDetail!.creditDocuments !=
                                      null &&
                                  ticketCustomerDetailController.customerDetail!
                                      .creditDocuments!.isNotEmpty
                              ? ticketCustomerDetailController.customerDetail!
                                  .creditDocuments![0].referenceno
                                  .toString()
                              : ""
                                  "",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.payment_date,
                          ticketCustomerDetailController
                                          .customerDetail!.creditDocuments !=
                                      null &&
                                  ticketCustomerDetailController.customerDetail!
                                      .creditDocuments!.isNotEmpty
                              ? ticketCustomerDetailController.customerDetail!
                                  .creditDocuments![0].paymentdate
                                  .toString()
                              : ""
                                  "",
                          Strings.payment_mode,
                          ticketCustomerDetailController
                                          .customerDetail!.creditDocuments !=
                                      null &&
                                  ticketCustomerDetailController.customerDetail!
                                      .creditDocuments!.isNotEmpty
                              ? ticketCustomerDetailController
                                  .customerDetail!.creditDocuments![0].paymode
                                  .toString()
                              : ""
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
    );
  }

  presentAddressDetails() {
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
            title: Strings.present_address_details,
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
                          Strings.address,
                          ticketCustomerDetailController
                                  .customerDetail!.addressList![0].landmark ??
                              "",
                          Strings.pincode,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.code
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.name
                              : "-",
                          Strings.city,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.cityName
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.stateName
                              : "-",
                          Strings.country,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.countryName
                              : "-",
                          null,
                          false,
                          false),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  paymentAddressDetails() {
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
            title: Strings.payment_address_details,
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
                          Strings.address,
                          ticketCustomerDetailController
                                  .addressListData!.isNotEmpty
                              ? ticketCustomerDetailController
                                  .addressListData![0].landmark
                              : "",
                          Strings.pincode,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.code
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.name
                              : "-",
                          Strings.city,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.cityName
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.stateName
                              : "-",
                          Strings.country,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.countryName
                              : "-",
                          null,
                          false,
                          false),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  permanentAddressDetails() {
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
            title: Strings.permanent_address_details,
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
                          Strings.address,
                          ticketCustomerDetailController
                                  .addressListData!.isNotEmpty
                              ? ticketCustomerDetailController
                                  .addressListData![0].landmark
                              : "",
                          Strings.pincode,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.code
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController.addressData!.name
                              : "-",
                          Strings.city,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.cityName
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.stateName
                              : "-",
                          Strings.country,
                          ticketCustomerDetailController.addressData != null
                              ? ticketCustomerDetailController
                                  .addressData!.countryName
                              : "-",
                          null,
                          false,
                          false),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  planDetails() {
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
            title: Strings.plan_details,
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
                          Strings.bill_to,
                          ticketCustomerDetailController.customerBill ?? "",
                          ticketCustomerDetailController.ifPlanGroup == true
                              ? Strings.plan_group
                              : "",
                          ticketCustomerDetailController.ifPlanGroup == true
                              ? ticketCustomerDetailController
                                  .plansByPlanGroupIdList![0].name
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      ticketCustomerDetailController.ifPlanGroup == true
                          ? basicDetailItem(
                              Strings.discount,
                              ticketCustomerDetailController.customerDetail !=
                                      null
                                  ? ticketCustomerDetailController
                                      .customerDetail!.discount
                                      .toString()
                                  : "-",
                              "",
                              "-",
                              null,
                              false,
                              false)
                          : const SizedBox.shrink(),
                      // const SizedBox(height: Constant.SMALL_PADDING),
                      ticketCustomerDetailController.ifIndividualPlan == true
                          ? Card(
                              color: AppTheme.colorWhite,
                              child: (ticketCustomerDetailController
                                      .customerDetail!
                                      .planMappingList!
                                      .isNotEmpty)
                                  ? ListView.builder(
                                      physics:
                                          const NeverScrollableScrollPhysics(),
                                      scrollDirection: Axis.vertical,
                                      shrinkWrap: true,
                                      itemCount: ticketCustomerDetailController
                                          .customerDetail!
                                          .planMappingList
                                          ?.length,
                                      itemBuilder: (context, ii) {
                                        PlanMappingList? items =
                                            ticketCustomerDetailController
                                                .customerDetail!
                                                .planMappingList![ii];
                                        int? lstLength =
                                            ticketCustomerDetailController
                                                .customerDetail!
                                                .planMappingList
                                                ?.length;
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
                                                      Strings.service,
                                                      items.service ?? "-",
                                                      Strings.plan,
                                                      items.planName ?? "-",
                                                      null,
                                                      false,
                                                      false),
                                                  const SizedBox(
                                                      height: Constant
                                                          .SMALL_PADDING),
                                                  basicDetailItem(
                                                      Strings.validity,
                                                      "${items.validity} ${items.unitsOfValidity}",
                                                      Strings.discount,
                                                      items.discount != null
                                                          ? items.discount
                                                              .toString()
                                                          : "-",
                                                      null,
                                                      false,
                                                      false),
                                                  const SizedBox(
                                                      height: Constant
                                                          .SMALL_PADDING),
                                                  basicDetailItem(
                                                      Strings.final_offer_price,
                                                      items.offerPrice != null
                                                          ? items.offerPrice
                                                              .toString()
                                                          : "-",
                                                      "",
                                                      "",
                                                      null,
                                                      false,
                                                      false),
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
    return DynamicAppBar(Strings.customer_detail, '', AppTheme.colorPrimary,
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

  valueWidget(String? value, bool isLinkable) {
    return CustomText(
      title: value ?? "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }
}
