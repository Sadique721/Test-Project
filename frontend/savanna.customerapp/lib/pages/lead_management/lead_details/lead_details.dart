import 'dart:developer';

import 'package:savbill/pages/customer/customer_option_menu.dart';
import 'package:savbill/pages/customer/model/customer_detail_option.dart';
import 'package:savbill/pages/lead_management/audit_trial/lead_audit_trial.dart';
import 'package:savbill/pages/lead_management/lead_details/lead_detail_controller.dart';
import 'package:savbill/pages/lead_management/lead_follow_up/lead_follow_up_list.dart';
import 'package:savbill/pages/lead_management/lead_note/lead_notes_screen.dart';
import 'package:savbill/pages/lead_management/lead_status/lead_status_screen.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class LeadDetailScreen extends StatefulWidget {
  @override
  _LeadDetailState createState() => _LeadDetailState();
}

class _LeadDetailState extends State<LeadDetailScreen> {
  final leadDetailController = Get.put(LeadDetailController());

  _backScreen() {
    if(leadDetailController.isDashboardFlag == true){
      Get.offAllNamed(AppRoutes.LEAD_MANAGEMENT,
          arguments: {
            Constant.FROM: Strings.menu,
          });
    }else {
      Get.back();
    }
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<LeadDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: leadDetailController.isLoading),
      ]);
    });
  }

  _body() {
    if (leadDetailController.leadMaster != null &&
        leadDetailController.leadMaster!.outsideValley != null) {
      leadDetailController.valleyType = "Outside Valley";
    } else if (leadDetailController.leadMaster != null &&
        leadDetailController.leadMaster!.insideValley != null) {
      leadDetailController.valleyType = "Inside Valley";
    } else {
      leadDetailController.valleyType = "-";
    }
    return Container(
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
        margin: const EdgeInsets.only(
          top: Constant.SMALL_PADDING,
        ),
        color: AppTheme.colorBG,
        child: SingleChildScrollView(
          physics: const ScrollPhysics(),
          child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  margin: const EdgeInsets.only(
                      top: Constant.SMALL_PADDING,
                      left: Constant.SCREEN_PADDING),
                  child: CustomText(
                    title: Strings.lead_management,
                    fontSize: AppTheme.medium,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                Container(
                  margin: const EdgeInsets.only(
                    top: Constant.SMALL_PADDING,
                  ),
                  height: Constant.TOP_MENU_OPTION,
                  child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: leadDetailController.optionList.length,
                      itemBuilder: (BuildContext context, int index) {
                        CustomerDetailOption detail =
                        leadDetailController.optionList[index];
                        return InkWell(
                            onTap: () {
                              if (detail.id == 1) {
                                openLeadAuditTrailScreen(leadDetailController.eventId);
                              } else if (detail.id == 2) {
                                openLeadStatusWorkFlowScreen(leadDetailController.eventId);
                              } else if (detail.id == 3) {
                                openLeadNotesScreen(leadDetailController.eventId);
                              }else if (detail.id == 4) {
                                openLeadFollowUpScreen(leadDetailController.eventId);
                              }
                            },
                            child: Container(
                              margin: EdgeInsets.only(
                                  left: index == 0
                                      ? Constant.SCREEN_PADDING
                                      : Constant.SMALL_PADDING,
                                  right: index ==
                                      leadDetailController
                                                  .optionList.length -
                                              1
                                      ? Constant.SCREEN_PADDING
                                      : Constant.SMALL_PADDING,
                                  bottom: 2),
                              child: CustomerOptionItemView(
                                detail: detail,
                                index: index,
                              ),
                            ));
                      }),
                ),
                const SizedBox(
                  height: Constant.MEDIUM_PADDING,
                ),
                basicLeadDetailView(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                basicCustomerDetailView(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                presentAddressDetailView(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                competitorPackView(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
                basicCAFView(),
                const SizedBox(
                  height: Constant.VERY_SMALL_PADDING,
                ),
              ]),
        ));
  }

  basicLeadDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_lead_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_lead_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                          Strings.lead_no,
                          leadDetailController.leadMaster?.leadNo ?? "-",
                          "${Strings.lead} ${Strings.customer_type}",
                          leadDetailController.leadMaster?.leadCustomerType ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          "${Strings.lead} ${Strings.customer_sector}",
                          leadDetailController.leadMaster?.leadCustomerSector ??
                              "-",
                          Strings.requiredServiceType,
                          leadDetailController.leadMaster?.requireServiceType ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          "${Strings.lead} ${Strings.type}",
                          leadDetailController.leadMaster?.leadType ?? "-",
                          "${Strings.lead} ${Strings.category}",
                          leadDetailController.leadMaster?.leadCategory ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.leadOriginType,
                          leadDetailController.leadMaster?.leadOriginType ??
                              "-",
                          "${Strings.lead} ${Strings.source}",
                          leadDetailController.leadMaster?.leadSourceName ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.feasibility,
                          leadDetailController.leadMaster?.feasibility ?? "-",
                          "",
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

  basicCustomerDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basicCustomerDetails),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basicCustomerDetails,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                          "${leadDetailController.leadMaster?.title ?? ""} ${leadDetailController.leadMaster?.firstname ?? ""} ${leadDetailController.leadMaster?.lastname ?? ""}",
                          Strings.primary_mobile_number,
                          "(${leadDetailController.leadMaster?.countryCode ?? ""}) ${leadDetailController.leadMaster?.mobile}"
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.primary_email,
                          leadDetailController.leadMaster?.email ?? "-",
                          Strings.service_area,
                          leadDetailController.serviceAreaDATA ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.gender,
                          leadDetailController.leadMaster?.gender ?? "-",
                          Strings.bill_to,
                          leadDetailController.customerBill ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.pan_no,
                          leadDetailController.leadMaster?.pan ?? "-",
                          Strings.vat,
                          leadDetailController.leadMaster?.tinNo ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.unit_no,
                          leadDetailController.leadMaster?.blockNo ?? "-",
                          "",
                          "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      leadDetailController.leadMaster?.invoiceType == true
                          ? basicDetailItem(
                              Strings.invoice_type,
                              leadDetailController.leadMaster?.invoiceType ??
                                  "-",
                              "",
                              "-",
                              null,
                              false,
                              false)
                          : const SizedBox.shrink(),
                      leadDetailController.leadMaster?.invoiceType == true
                          ? const SizedBox(height: Constant.SMALL_PADDING)
                          : const SizedBox.shrink(),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  presentAddressDetailView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.present_address_details),
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
          shape: const Border(),
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
                          Strings.landmark,
                          (leadDetailController.leadMaster != null && leadDetailController.leadMaster!.addressList!.isNotEmpty && leadDetailController.leadMaster!.addressList![0].landmark != null) ? leadDetailController.leadMaster!.addressList![0].landmark :
                              "-",
                          Strings.pincode,
                          leadDetailController.pinCodeData?.code ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.area,
                          leadDetailController.pinCodeData?.name ?? "-",
                          Strings.city,
                          leadDetailController.pinCodeData?.cityName ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.state,
                          leadDetailController.pinCodeData?.stateName ?? "-",
                          Strings.country,
                          leadDetailController.pinCodeData?.countryName ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.street_name,
                          "${(leadDetailController.leadMaster != null && leadDetailController.leadMaster!.addressList!.isNotEmpty && leadDetailController.leadMaster!.addressList![0].streetName != null) ? leadDetailController.leadMaster!.addressList![0].streetName : "-"}",
                          Strings.house_no,
                          (leadDetailController.leadMaster != null &&
                                  leadDetailController
                                      .leadMaster!.addressList!.isNotEmpty && leadDetailController
                              .leadMaster!.addressList![0].houseNo != null)
                              ? leadDetailController
                                  .leadMaster!.addressList![0].houseNo
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.valley_type,
                          leadDetailController.valleyType,
                          leadDetailController.leadMaster != null &&
                                  leadDetailController
                                          .leadMaster!.outsideValley !=
                                      null
                              ? "Outside Valley"
                              : "Inside Valley",
                          leadDetailController.leadMaster != null &&
                                  leadDetailController
                                          .leadMaster!.outsideValley !=
                                      null
                              ? leadDetailController.leadMaster?.outsideValley
                              : leadDetailController.leadMaster?.insideValley ??
                                  "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.latitude,
                          leadDetailController.leadMaster?.latitude ?? "-",
                          Strings.longitude,
                          leadDetailController.leadMaster?.longitude ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING)
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  competitorPackView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.competitor_pack_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.competitor_pack_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          shape: const Border(),
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
                          Strings.competitor_pack_duration,
                          leadDetailController.leadMaster?.competitorDuration ??
                              "-",
                          Strings.expiry,
                          leadDetailController.leadMaster?.expiry ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.previous_month,
                          leadDetailController.leadMaster?.previousMonth ?? "-",
                          Strings.previous_amount,
                          leadDetailController.leadMaster?.previousAmount !=
                                  null
                              ? leadDetailController.leadMaster?.previousAmount
                                  .toString()
                              : "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.customer_feedback,
                          leadDetailController.leadMaster?.feedback ?? "-",
                          Strings.current_pay,
                          leadDetailController.leadMaster?.amount.toString() ?? "-",
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

  basicCAFView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.basic_caf_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          initiallyExpanded: true,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.basic_caf_details,
            fontSize: AppTheme.medium,
            colors: AppTheme.title_dark,
            textAlign: TextAlign.start,
            fontWeight: FontWeight.w600,
          ),
          onExpansionChanged: ((newState) {}),
          shape: const Border(),
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
                          Strings.title,
                          leadDetailController.leadMaster?.title ?? "-",
                          Strings.contact_person,
                          leadDetailController.leadMaster?.contactperson ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.caf_no,
                          leadDetailController.leadMaster?.cafno ?? "-",
                          Strings.username,
                          leadDetailController.leadMaster?.username ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.calendar_type,
                          leadDetailController.leadMaster?.calendarType ?? "-",
                          Strings.parent_customer,
                          leadDetailController.leadMaster?.parentCustomerName ??
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

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 1.5,
        color: bgColor,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          height: Constant.BTN_HEIGHT_M - 6,
          width: Constant.BTN_HEIGHT_M - 6,
          alignment: Alignment.center,
          padding: const EdgeInsets.all(Constant.SMALL_PADDING - 1),
          child: SvgPicture.asset(
            btnName,
            height: Constant.ICON_SIZE,
            width: Constant.ICON_SIZE,
            color: txtColor,
            fit: BoxFit.fill,
          ),
        ),
      ),
    );
  }



  openLeadAuditTrailScreen(int? leadMasterId) async {
    var result = await Get.to(LeadAuditScreen(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
    });
    if (result != null && result == true) {
      // customerCafDetailController.getCustomerDetail();
    }
  }

  openLeadNotesScreen(int? leadMasterId) async {
    var result = await Get.to(LeadNotesScreen(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
    });
    if (result != null && result == true) {
      // customerCafDetailController.getCustomerDetail();
    }
  }

  openLeadFollowUpScreen(int? leadMasterId) async {
    var result = await Get.to(LeadFollowUpList(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
    });
    if (result != null && result == true) {
      // customerCafDetailController.getCustomerDetail();
    }
  }

  openLeadStatusWorkFlowScreen(int? leadMasterId) async {
    var result = await Get.to(LeadStatusWorkFlow(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
    });
    if (result != null && result == true) {
      // customerCafDetailController.getCustomerDetail();
    }
  }


  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.leadDetails, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }
}
