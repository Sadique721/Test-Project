import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer_inventory/document/document_view_screen.dart';
import 'package:savbill/pages/dashboard/model/response/get_ticket_tat_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_ticket_etr_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_follow_up_find_all_response.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_followup_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/savbill_caretab_controller.dart';
import 'package:savbill/pages/dashboard/ticket_detail_controller.dart';
import 'package:savbill/pages/dashboard/ticket_follow_up_item_list.dart';
import 'package:savbill/pages/dashboard/ticket_history_dialog.dart';
import 'package:savbill/pages/dashboard/ticket_tat_name_map_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/change_ticket_priority_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/document_upload_ticket/ticket_document_view_screen.dart';
import 'package:savbill/pages/ticket_system/ticket_management/follow_up/ticket_schedule_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/link_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_change_status.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/ticket_etr_screen.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/ticket_re_schedule_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/ticket_remark_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_remark/ticket_remark.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_status_approve_reject_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_workflow_audit.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket_controller.dart';
import 'package:savbill/pages/upload_document/upload_document.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:readmore/readmore.dart';
import '../../webservices/url_constants.dart';
import '../customer_invoice/pdf_viewer_page.dart';
import '../ticket_system/ticket_management/ticket_re_schedule/close_ticket_remark_follow_up_dialog.dart';

class TicketDetailScreen extends StatefulWidget {
  @override
  _TicketDetailState createState() => _TicketDetailState();
}

class _TicketDetailState extends State<TicketDetailScreen>
    implements
        TicketApproveRejectBtnAction,
        TicketPriorityBtnAction,
        CloseFollowUpRemarkBtnAction {
  final ticketDetailController = Get.put(TicketDetailController());
  final viewTicketController = Get.put(ViewTicketController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TicketDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: ticketDetailController.isLoading),
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
      child: ticketDetailController.ticketDetail != null
          ? SingleChildScrollView(
              physics: const ScrollPhysics(),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    actionButtonView(ticketDetailController),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    basicDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ratingDetailView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketHistoryView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketFollowUpView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketInternalRemarksView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    InkWell(
                      onTap: () {
                        Get.to(TicketWorkflowAudit(), arguments: {
                          Constant.TICKET_ID:
                              ticketDetailController.ticketDetail!.caseId,
                        });
                      },
                      child: Padding(
                        padding: const EdgeInsets.only(
                            left: Constant.SCREEN_PADDING,
                            right: Constant.SCREEN_PADDING,
                            top: Constant.SMALL_PADDING - 2),
                        child: Container(
                          margin: const EdgeInsets.symmetric(
                            vertical: Constant.VERY_SMALL_PADDING,
                          ),
                          decoration: BoxDecoration(
                            color: AppTheme.expantableItemBg,
                            border: Border.all(color: AppTheme.colorLightGrey),
                            borderRadius: const BorderRadius.all(
                              Radius.circular(4),
                            ),
                          ),
                          child: Material(
                            color: AppTheme.colorWhite,
                            elevation: 1,
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(4),
                            ),
                            child: Padding(
                              padding: const EdgeInsets.symmetric(
                                  vertical: Constant.LARGE_PADDING,
                                  horizontal: Constant.MEDIUM_PADDING),
                              child: Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  CustomText(
                                    title: Strings.ticket_workflow_audit,
                                    fontSize: AppTheme.medium,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontWeight: FontWeight.w600,
                                  ),
                                  Icon(
                                    Icons.arrow_forward_ios_rounded,
                                    size: 16,
                                    color: AppTheme.colorIconGrey,
                                  )
                                ],
                              ),
                            ),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketAttachmentView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketETRView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    ticketTATReportView(),
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                  ]),
            )
          : noDataFound(),
    );
  }

  ticketFollowUpView() {
    if (ticketDetailController.followUpFindAllTicketList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.followup_detail),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.followup_detail,
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
              (ticketDetailController.followUpFindAllTicketList!.isNotEmpty)
                  ? ListView.builder(
                      shrinkWrap: true,
                      scrollDirection: Axis.vertical,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: ticketDetailController
                          .followUpFindAllTicketList!.length,
                      itemBuilder: (context, index) {
                        if (index ==
                            ticketDetailController
                                .followUpFindAllTicketList!.length) {
                          if (ticketDetailController.isShowLoadMore) {
                            return Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
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
                          TicketFollowUpFindAllDataList item =
                              ticketDetailController
                                  .followUpFindAllTicketList![index];
                          return TicketFollowUpItemList(
                            item: item,
                            index: index,
                            controller: ticketDetailController,
                            ticketMasterId: ticketDetailController.ticketId,
                            onTapRescheduleFollowUp: () {
                              openRescheduleRemarkFollowUp(
                                  ticketDetailController.ticketDetail!.caseId,
                                  item,
                                  Strings.reschedule);
                            },
                            onTapRemarkFollowUp: () {
                              openRemarkFollowUp(item.id, item);
                            },
                            onTapCloseFollowUp: () {
                              showDialog(
                                  context: context,
                                  barrierDismissible: true,
                                  builder: (BuildContext context) {
                                    return CloseTicketRemarkFollowUpDialog(
                                      pageName: 'ticketCloseFollowUp',
                                      closeFollowUpRemarkBtnAction: this,
                                      itemList: item,
                                    );
                                  });
                            },
                            onTapCallFollowUp: () {
                              Utils.showSnackbar(
                                  "Call configure",
                                  "Sorry! Please configure call client first..",
                                  AppTheme.colorWhite,
                                  AppTheme.colorBlueRView);
                            },
                          );
                        }
                      })
                  : SizedBox(
                      child: noDataFound(),
                      height: Get.height * 0.1,
                    ),
            ],
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  ticketInternalRemarksView() {
    if (ticketDetailController.followUpDetailList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.internalRemark),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.internalRemark,
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
              ListView.builder(
                  physics: const NeverScrollableScrollPhysics(),
                  scrollDirection: Axis.vertical,
                  shrinkWrap: true,
                  itemCount: ticketDetailController.followUpDetailList?.length,
                  itemBuilder: (context, ii) {
                    FollowUpDetail? items =
                        ticketDetailController.followUpDetailList![ii];
                    int? lstLength =
                        ticketDetailController.followUpDetailList?.length;
                    return Padding(
                      padding: EdgeInsets.only(
                          top: (ii == 0)
                              ? Constant.SMALL_PADDING
                              : Constant.EXPANTABLE_ITEM_MARGIN,
                          left: Constant.EXPANTABLE_ITEM_MARGIN,
                          right: Constant.EXPANTABLE_ITEM_MARGIN,
                          bottom: (ii == (lstLength! - 1))
                              ? Constant.EXPANTABLE_ITEM_MARGIN
                              : 0),
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.expantableItemBg,
                          border: Border.all(color: AppTheme.expantableItemBg),
                          borderRadius: const BorderRadius.all(
                            Radius.circular(3),
                          ),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              basicDetailItem(
                                  Strings.ticket_name,
                                  items.caseTitle ?? "-",
                                  Strings.customer_name,
                                  items.customersName ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.staff_name,
                                  items.staffUserName ?? "-",
                                  Strings.remark_date,
                                  items.remarkDate ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.remarks,
                                  items.remark ?? "-",
                                  "-",
                                  "-",
                                  null,
                                  false,
                                  false),
                            ],
                          ),
                        ),
                      ),
                    );
                  }),
            ],
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  ticketETRView() {
    if (ticketDetailController.showTicketETRReportList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.etr_report),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.etr_report,
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
              ListView.builder(
                  physics: const NeverScrollableScrollPhysics(),
                  scrollDirection: Axis.vertical,
                  shrinkWrap: true,
                  itemCount:
                      ticketDetailController.showTicketETRReportList?.length,
                  itemBuilder: (context, ii) {
                    ShowTicketETRReportDataList? items =
                        ticketDetailController.showTicketETRReportList![ii];
                    int? lstLength =
                        ticketDetailController.showTicketETRReportList?.length;
                    return Padding(
                      padding: EdgeInsets.only(
                          top: (ii == 0)
                              ? Constant.SMALL_PADDING
                              : Constant.EXPANTABLE_ITEM_MARGIN,
                          left: Constant.EXPANTABLE_ITEM_MARGIN,
                          right: Constant.EXPANTABLE_ITEM_MARGIN,
                          bottom: (ii == (lstLength! - 1))
                              ? Constant.EXPANTABLE_ITEM_MARGIN
                              : 0),
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.expantableItemBg,
                          border: Border.all(color: AppTheme.expantableItemBg),
                          borderRadius: const BorderRadius.all(
                            Radius.circular(3),
                          ),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              basicDetailItem(
                                  Strings.cust_user_name,
                                  items.custUserName ?? "-",
                                  Strings.staff_person_name,
                                  items.staffPersonName ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.case_number,
                                  items.caseNumber ?? "-",
                                  Strings.messageMode,
                                  items.messageMode ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.notification_mode,
                                  items.notificationMode ?? "-",
                                  Strings.notification_time,
                                  items.notificationSentTime ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.notification_date,
                                  items.notificationSentDate ?? "-",
                                  Strings.notification_status,
                                  items.notificationStatus ?? "-",
                                  null,
                                  false,
                                  false),
                            ],
                          ),
                        ),
                      ),
                    );
                  }),
            ],
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  ticketTATReportView() {
    if (ticketDetailController.showTicketTATReportList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.etr_report),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.tat_report,
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
              ListView.builder(
                  physics: const NeverScrollableScrollPhysics(),
                  scrollDirection: Axis.vertical,
                  shrinkWrap: true,
                  itemCount:
                      ticketDetailController.showTicketTATReportList?.length,
                  itemBuilder: (context, ii) {
                    GetTicketTATReportDataList? items =
                        ticketDetailController.showTicketTATReportList![ii];
                    int? lstLength =
                        ticketDetailController.showTicketTATReportList?.length;
                    return Padding(
                      padding: EdgeInsets.only(
                          top: (ii == 0)
                              ? Constant.SMALL_PADDING
                              : Constant.EXPANTABLE_ITEM_MARGIN,
                          left: Constant.EXPANTABLE_ITEM_MARGIN,
                          right: Constant.EXPANTABLE_ITEM_MARGIN,
                          bottom: (ii == (lstLength! - 1))
                              ? Constant.EXPANTABLE_ITEM_MARGIN
                              : 0),
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppTheme.expantableItemBg,
                          border: Border.all(color: AppTheme.expantableItemBg),
                          borderRadius: const BorderRadius.all(
                            Radius.circular(3),
                          ),
                        ),
                        child: Padding(
                          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              basicDetailItem(
                                  Strings.tat_action,
                                  items.tatAction ?? "-",
                                  Strings.tat_time,
                                  "${items.tatTime} ${items.tatUnit}" ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.sla_time,
                                  "${items.slaTime} ${items.slaUnit}" ?? "-",
                                  Strings.case_level,
                                  items.caseLevel ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.notification_for,
                                  items.notificationFor ?? "-",
                                  Strings.isTATBreached,
                                  items.isTatBreached ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              basicDetailItem(
                                  Strings.messageMode,
                                  items.messageMode ?? "-",
                                  Strings.message_status,
                                  items.messageStatus ?? "-",
                                  null,
                                  false,
                                  false),
                              const SizedBox(height: Constant.SMALL_PADDING),
                              Row(
                                mainAxisSize: MainAxisSize.max,
                                crossAxisAlignment: CrossAxisAlignment.center,
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Flexible(
                                    flex: 3,
                                    child: Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        titleWidget(Strings.tat_message),
                                        const SizedBox(
                                            height:
                                                Constant.VERY_SMALL_PADDING -
                                                    1),
                                        ReadMoreText(
                                          items.tatMessage ?? "-",
                                          // items.tatMessage!.body!.text,
                                          trimLines: 1,
                                          lessStyle: const TextStyle(
                                            color: AppTheme.colorAccentTheme,
                                            fontSize: AppTheme.small + 1,
                                            fontWeight: FontWeight.w400,
                                            decoration: TextDecoration.none,
                                          ),
                                          // colorClickableText: AppTheme.colorAccentTheme,
                                          style: TextStyle(
                                            color: AppTheme.lable_noramal,
                                            fontSize: AppTheme.small + 1,
                                            fontWeight: FontWeight.w400,
                                            decoration: TextDecoration.none,
                                          ),
                                          semanticsLabel: "Hemendra",
                                          trimMode: TrimMode.Line,
                                          trimCollapsedText: 'Show more',
                                          trimExpandedText: 'Show less',
                                          moreStyle: const TextStyle(
                                            color: AppTheme.colorAccentTheme,
                                            fontSize: AppTheme.small + 1,
                                            fontWeight: FontWeight.w400,
                                            decoration: TextDecoration.none,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: Constant.SMALL_PADDING),
                            ],
                          ),
                        ),
                      ),
                    );
                  }),
            ],
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  ticketAttachmentView() {
    if (ticketDetailController.attachmentList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.ticket_attachments),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
                const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.ticket_attachments,
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
              ticketDetailController.attachmentList != null &&
                      ticketDetailController.attachmentList!.isNotEmpty
                  ? ListView.builder(
                      physics: const NeverScrollableScrollPhysics(),
                      scrollDirection: Axis.vertical,
                      shrinkWrap: true,
                      itemCount: ticketDetailController.attachmentList?.length,
                      itemBuilder: (context, ii) {
                        TicketAttachments? items =
                            ticketDetailController.attachmentList![ii];
                        int? lstLength =
                            ticketDetailController.attachmentList?.length;
                        return Padding(
                          padding: EdgeInsets.only(
                              top: (ii == 0)
                                  ? Constant.SMALL_PADDING
                                  : Constant.EXPANTABLE_ITEM_MARGIN,
                              left: Constant.EXPANTABLE_ITEM_MARGIN,
                              right: Constant.EXPANTABLE_ITEM_MARGIN,
                              bottom: (ii == (lstLength! - 1))
                                  ? Constant.EXPANTABLE_ITEM_MARGIN
                                  : 0),
                          child: Container(
                            decoration: BoxDecoration(
                              color: AppTheme.expantableItemBg,
                              border:
                                  Border.all(color: AppTheme.expantableItemBg),
                              borderRadius: const BorderRadius.all(
                                Radius.circular(3),
                              ),
                            ),
                            child: Padding(
                              padding:
                                  const EdgeInsets.all(Constant.SMALL_PADDING),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  basicDetailItem(
                                      Strings.filename,
                                      (items.filename != null &&
                                              items.filename!.isNotEmpty)
                                          ? items.filename
                                          : "-",
                                      Strings.status,
                                      (items.docStatus != null &&
                                              items.docStatus!.isNotEmpty)
                                          ? items.docStatus
                                          : "-", () {
                                    ticketDetailController.download(
                                        "${UrlConstants.ticket_download_document}/${items.ticketId}/${items.docId}",
                                        "${items.docId}ticket",
                                        context);
                                  }, true, false),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  basicDetailItem(
                                      Strings.update_by,
                                      (items.createdByName != null &&
                                              items.createdByName!.isNotEmpty)
                                          ? items.createdByName
                                          : "-",
                                      "${Strings.action} ${Strings.date}",
                                      (items.createdate != null &&
                                              items.createdate!.isNotEmpty)
                                          ? items.createdate
                                          : "-",
                                      null,
                                      false,
                                      false),
                                  const SizedBox(
                                      height: Constant.SMALL_PADDING),
                                  Row(
                                    mainAxisSize: MainAxisSize.max,
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    mainAxisAlignment: MainAxisAlignment.end,
                                    children: [
                                      Flexible(
                                        child: Column(
                                          mainAxisAlignment:
                                              MainAxisAlignment.start,
                                          crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                          children: [
                                            titleWidget("Action"),
                                            const SizedBox(
                                                height: Constant
                                                        .VERY_SMALL_PADDING -
                                                    1),
                                            InkWell(
                                                onTap: () {
                                                  ticketDetailController
                                                      .downloadFile(
                                                          "${UrlConstants.ticket_download_document}/${items.ticketId}/${items.docId}",
                                                          items);
                                                  // openPdfViewScreen(
                                                  //     Strings.ticket,
                                                  //     "${UrlConstants.ticket_download_document}/${items.ticketId}/${items.docId}");
                                                },
                                                child: const Icon(
                                                  Icons.remove_red_eye,
                                                )),
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
            ],
          ),
        ),
      );
    } else {
      return Container();
    }
  }

  ticketHistoryView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.ticket_history),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.ticket_history,
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
            (ticketDetailController.caseUpdateList!.isNotEmpty)
                ? ListView.builder(
                    physics: const NeverScrollableScrollPhysics(),
                    scrollDirection: Axis.vertical,
                    shrinkWrap: true,
                    itemCount: ticketDetailController.caseUpdateList?.length,
                    itemBuilder: (context, ii) {
                      CaseUpdateList? items =
                          ticketDetailController.caseUpdateList![ii];
                      int? lstLength =
                          ticketDetailController.caseUpdateList?.length;
                      return Padding(
                        padding: EdgeInsets.only(
                            top: (ii == 0)
                                ? Constant.SMALL_PADDING
                                : Constant.EXPANTABLE_ITEM_MARGIN,
                            left: Constant.EXPANTABLE_ITEM_MARGIN,
                            right: Constant.EXPANTABLE_ITEM_MARGIN,
                            bottom: (ii == (lstLength! - 1))
                                ? Constant.EXPANTABLE_ITEM_MARGIN
                                : 0),
                        child: Container(
                          decoration: BoxDecoration(
                            color: AppTheme.expantableItemBg,
                            border:
                                Border.all(color: AppTheme.expantableItemBg),
                            borderRadius: const BorderRadius.all(
                              Radius.circular(3),
                            ),
                          ),
                          child: Padding(
                            padding:
                                const EdgeInsets.all(Constant.SMALL_PADDING),
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                basicDetailItem(
                                    Strings.create_by,
                                    items.createdByName ?? "-",
                                    Strings.create_date,
                                    items.createdate ?? "-",
                                    null,
                                    false,
                                    false),
                                const SizedBox(height: Constant.SMALL_PADDING),
                                basicDetailItem(
                                    Strings.update_by,
                                    items.lastModifiedByName ?? "-",
                                    Strings.update_date,
                                    items.updatedate ?? "-",
                                    null,
                                    false,
                                    false),
                                const SizedBox(height: Constant.SMALL_PADDING),
                                Row(
                                  mainAxisSize: MainAxisSize.max,
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  mainAxisAlignment: MainAxisAlignment.end,
                                  children: [
                                    Column(
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.end,
                                      children: [
                                        titleWidget(
                                            Strings.view_progress_detail),
                                        const SizedBox(
                                            height:
                                                Constant.VERY_SMALL_PADDING -
                                                    1),
                                        InkWell(
                                          onTap: () async {
                                            int? lstLength =
                                                items.updateDetails!.length;
                                            if (items.updateDetails != null &&
                                                lstLength > 0) {
                                              ticketHistoryDialog(context,
                                                  items.updateDetails!);
                                            }
                                          },
                                          child: Container(
                                            padding: const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.VERY_SMALL_PADDING,
                                                vertical: Constant
                                                        .VERY_SMALL_PADDING -
                                                    3),
                                            decoration: BoxDecoration(
                                                color: AppTheme.colorPrimary,
                                                borderRadius: BorderRadius
                                                    .circular(Constant
                                                        .VERY_SMALL_PADDING)),
                                            child: SvgPicture.asset(
                                              openTicketInvoice,
                                              height:
                                                  Constant.MENU_ICON_SIZE + 2,
                                              width:
                                                  Constant.MENU_ICON_SIZE + 2,
                                              color: AppTheme.colorWhite,
                                              fit: BoxFit.fill,
                                            ),
                                          ),
                                        ),
                                      ],
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
          ],
        ),
      ),
    );
  }

  ratingDetailView() {
    String? txtFeedback = "-", txtRat = "-";
    if (ticketDetailController.ticketDetail?.customerFeedback != null) {
      txtFeedback = ticketDetailController.ticketDetail?.customerFeedback;
    }
    if (ticketDetailController.ticketDetail?.rating != null) {
      txtRat = "${ticketDetailController.ticketDetail?.rating} Star";
    }

    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.rating_details),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.rating_details,
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
                      basicDetailItem(Strings.rating, txtRat, Strings.feedback,
                          txtFeedback, null, false, false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }

  actionButtonView(TicketDetailController ticketDetailController) {
    bool? showAssignTicket,
        showApprove = false,
        showReject = false,
        showChangeStatus = false,
        showChangePriority = false,
        showLink = true,
        showFollowup = false,
        showUploadDoc = true,
        showChangeProblemDomain = true,
        showTicketRemark = true,
        showETRTicket = true;
    // if (ticketDetailController.ticketDetail!.currentAssigneeId != null ||
    //     ticketDetailController.ticketDetail!.currentAssigneeId ==
    //         ticketDetailController.userDetail!.userId) {
    //   showAssignTicket = true;
    // } else {
    //   showAssignTicket = false;
    // }

    if (ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Follow Up") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close")) {
      showAssignTicket = true;
    } else if (ticketDetailController.ticketDetail!.caseStatus != "Closed" &&
        ticketDetailController.ticketDetail!.caseStatus != "Raise and Close") {
      showAssignTicket = false;
    }

    if (ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("approved") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        (ticketDetailController.ticketDetail!.currentAssigneeId !=
            ticketDetailController.userDetail!.userId)) {
      showETRTicket = true;
      showApprove = true;
    } else {
      showETRTicket = false;
      showApprove = false;
    }

    if (ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("approved") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        ticketDetailController.ticketDetail!.caseOrder == 1 ||
        ticketDetailController.ticketDetail!.currentAssigneeId !=
            ticketDetailController.userDetail!.userId) {
      showReject = true;
    } else {
      showReject = false;
    }

    if (ticketDetailController.changeStatusAccess == true &&
            ticketDetailController.ticketDetail!.currentAssigneeId ==
                ticketDetailController.userDetail?.userId ||
        (ticketDetailController.ticketDetail!.caseStatus!
                .equalsIgnoreCase("Resolved") &&
            ticketDetailController.ticketDetail!.finalClosedById != "null")) {
      if (ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("rejected") ||
          (ticketDetailController.ticketDetail!.status != null &&
              ticketDetailController.ticketDetail!.status
                  .equalsIgnoreCase("Closed")) ||
          ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("Closed") ||
          ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("Raise and Close") ||
          ((!ticketDetailController.ticketDetail!.caseStatus!
                  .equalsIgnoreCase("Resolved")) &&
              ticketDetailController.ticketDetail!.currentAssigneeId !=
                  ticketDetailController.userDetail?.userId) ||
          (ticketDetailController.ticketDetail!.caseStatus!
                  .equalsIgnoreCase("Resolved") &&
              ticketDetailController.ticketDetail!.finalResolvedById !=
                  ticketDetailController.userDetail?.userId)) {
        showChangeStatus = true;
      } else {
        showChangeStatus = false;
      }
    } else {
      showChangeStatus = null;
    }

    if (ticketDetailController.ticketDetail!.currentAssigneeId == null ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Resolved") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        !(ticketDetailController.ticketDetail!.caseStatus!
                .equalsIgnoreCase("In Progress") &&
            ticketDetailController.userDetail!.userId ==
                ticketDetailController.assignStaffParentId)) {
      showChangePriority = true;
    } else {
      showChangePriority = false;
    }
    if (ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected")) {
      showLink = true;
      showUploadDoc = true;
    } else {
      showLink = false;
      showUploadDoc = false;
    }
    if (ticketDetailController.ticketDetail!.teamHierarchyMappingId == null ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Resolved") ||
        (ticketDetailController.ticketDetail!.caseStatus!
                .equalsIgnoreCase("In Progress") &&
            ticketDetailController.ticketDetail!.currentAssigneeId !=
                ticketDetailController!.assignStaffParentId)) {
      showChangeProblemDomain = true;
    } else {
      showChangeProblemDomain = false;
    }

    if (ticketDetailController.ticketDetail!.currentAssigneeId != null) {
      if ((ticketDetailController.ticketDetail!.status != null &&
              ticketDetailController.ticketDetail!.status!
                  .equalsIgnoreCase("approved")) ||
          ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("rejected") ||
          (ticketDetailController.ticketDetail!.status != null &&
              ticketDetailController.ticketDetail!.status!
                  .equalsIgnoreCase("Closed")) ||
          ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("Closed") ||
          ticketDetailController.ticketDetail!.caseStatus!
              .equalsIgnoreCase("Raise and Close")) {
        showFollowup = true;
      } else {
        showFollowup = true;
      }
    }

    if (ticketDetailController.ticketDetail!.caseStatus != null &&
            ticketDetailController.ticketDetail!.caseStatus!
                .equalsIgnoreCase("Open") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("approved") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        ticketDetailController.ticketDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        ticketDetailController.ticketDetail!.currentAssigneeId == null) {
      showTicketRemark = true;
    } else {
      showTicketRemark = false;
    }

    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Stack(
            children: <Widget>[
              Container(
                width: double.infinity,
                margin: const EdgeInsets.fromLTRB(0, 20, 0, 0),
                padding: const EdgeInsets.only(
                    bottom: 5,
                    left: 15,
                    right: 15,
                    top: Constant.SMALL_PADDING),
                decoration: BoxDecoration(
                    border: Border.all(color: AppTheme.colorGrey, width: 0.8),
                    borderRadius: BorderRadius.circular(5),
                    shape: BoxShape.rectangle,
                    color: AppTheme.colorWhite),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    const SizedBox(
                      height: Constant.VERY_SMALL_PADDING,
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.SMALL_PADDING,
                          horizontal: Constant.SMALL_PADDING),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [
                            showAssignTicket == false
                                ? buttonView(
                                    assignSvg,
                                    AppTheme.custChangeStatusLight,
                                    AppTheme.custChangeStatusDark, () {
                                    ticketDetailController.getStaffListData(
                                        ticketDetailController.ticketId);
                                  })
                                : buttonView(
                                    assignSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            showApprove == false
                                ? buttonView(checkSvg, AppTheme.custEditLight,
                                    AppTheme.custEditDark, () {
                                    addRemarkTicketDialog(
                                        context,
                                        Strings.approve,
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    checkSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            showReject == false
                                ? buttonView(
                                    cancelSvg,
                                    AppTheme.custDeleteLight,
                                    AppTheme.custDeleteDark, () {
                                    addRemarkTicketDialog(
                                        context,
                                        Strings.reject,
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    cancelSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            showFollowup
                                ? buttonView(
                                    followUpSvg,
                                    AppTheme.custPaymentLinkLight,
                                    AppTheme.custPaymentLinkDark, () {
                                    openScheduleFollowUpScreen(
                                        ticketDetailController.ticketDetail,
                                        ticketDetailController.ticketId);
                                  })
                                : Container(),
                            showFollowup == true
                                ? const SizedBox(
                                    width: Constant.SMALL_PADDING,
                                  )
                                : const SizedBox.shrink(),
                            showChangeStatus == false
                                ? buttonView(
                                    ticketChangeStatusSvg,
                                    AppTheme.custEditLight,
                                    AppTheme.custEditDark, () {
                                    if (ticketDetailController
                                        .ticketDetail!.caseStatus!
                                        .equalsIgnoreCase("Closed")) {
                                      Utils.showSnackbar(
                                          Strings.INFO,
                                          "Can not change status as ticket is closed.",
                                          AppTheme.colorWhite,
                                          AppTheme.colorBlueRView);
                                    } else {
                                      openTicketChangeStatusScreen(
                                          ticketDetailController, "pTicket");
                                    }
                                  })
                                : buttonView(
                                    ticketChangeStatusSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            // buttonView(
                            //     changePrioritySvg,
                            //     AppTheme.custDeleteLight,
                            //     AppTheme.custDeleteDark,
                            //     null),

                            showChangePriority == false
                                ? buttonView(
                                    changePrioritySvg,
                                    AppTheme.custChangeStatusLight,
                                    AppTheme.custChangeStatusDark, () {
                                    showTicketPriorityDialog(
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    changePrioritySvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                          ]),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.SMALL_PADDING,
                          horizontal: Constant.SMALL_PADDING),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            // buttonView(linkSvg, AppTheme.custEditLight,
                            //     AppTheme.custUploadFileDark, null),

                            showLink == false
                                ? buttonView(
                                    linkSvg,
                                    AppTheme.custAssignInventoryLight,
                                    AppTheme.custAssignInventoryDark, () {
                                    openLinkTicketScreen(
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    linkSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            // buttonView(
                            //     documentUploadSvg,
                            //     AppTheme.custUploadFileLight,
                            //     AppTheme.custUploadFileDark,
                            //     null),

                            showUploadDoc == false
                                ? buttonView(
                                    documentUploadSvg,
                                    AppTheme.custUploadFileLight,
                                    AppTheme.custUploadFileDark, () {
                                    openTicketDocumentUploadScreen(
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    documentUploadSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            // buttonView(linkSvg, AppTheme.custEditLight,
                            //     AppTheme.custUploadFileDark, null),

                            showChangeProblemDomain == true
                                ? buttonView(
                                    linkSvg,
                                    AppTheme.custNearLocationLight,
                                    AppTheme.custNearLocationDark, () {
                                    ticketDetailController.checkTicketReAssign(
                                        ticketDetailController.ticketDetail,
                                        viewTicketController);
                                  })
                                : buttonView(
                                    linkSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            // buttonView(
                            //     ticketPromiseToPaySvg,
                            //     AppTheme.custDeleteLight,
                            //     AppTheme.custDeleteDark,
                            //     null),

                            showETRTicket == false
                                ? buttonView(
                                    ticketPromiseToPaySvg,
                                    AppTheme.custAssignInventoryLight,
                                    AppTheme.custAssignInventoryDark, () {
                                    onTicketETRScreen(
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    ticketPromiseToPaySvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            // buttonView(
                            //     followUpSvg,
                            //     AppTheme.custUploadFileLight,
                            //     AppTheme.custUploadFileDark,
                            //     null),

                            // showFollowup
                            //     ? buttonView(
                            //     followUpSvg,
                            //     AppTheme.custPaymentLinkLight,
                            //     AppTheme.custPaymentLinkDark,
                            //     (){
                            //       showFollowUpPopup(ticketDetailController.ticketDetail);
                            //     })
                            //     : Container(),

                            showTicketRemark == false
                                ? buttonView(
                                    msgRemarkSvg,
                                    AppTheme.custNearLocationLight,
                                    AppTheme.custNearLocationDark, () {
                                    onTicketRemarkScreen(
                                        ticketDetailController.ticketDetail);
                                  })
                                : buttonView(
                                    msgRemarkSvg,
                                    AppTheme.colorTransparent
                                        .withOpacity(0.005),
                                    AppTheme.colorWhite,
                                    null),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            buttonView(pdfSvg, AppTheme.custPaymentLinkLight,
                                AppTheme.custPaymentLinkDark, () {
                              openDocumentViewTicketScreen(
                                  ticketDetailController.ticketDetail);
                            })
                          ]),
                    ),
                  ],
                ),
              ),
              Positioned(
                left: 50,
                top: 10,
                child: Container(
                  padding: const EdgeInsets.only(
                      bottom: 3, left: 5, right: 5, top: 3),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(5),
                    color: AppTheme.totalCardBg,
                  ),
                  child: CustomText(
                    title: Strings.action_button,
                    fontSize: AppTheme.medium,
                    colors: AppTheme.title_dark,
                    textAlign: TextAlign.start,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ],
      ),
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
                          ticketDetailController.ticketDetail?.caseTitle ?? "-",
                          Strings.number,
                          ticketDetailController.ticketDetail?.caseNumber ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.customer_name,
                          ticketDetailController.ticketDetail?.userName ?? "-",
                          Strings.user_name,
                          ticketDetailController.ticketDetail?.userName ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.service_area,
                          ticketDetailController
                                  .ticketDetail?.serviceAreaName ??
                              "-",
                          Strings.case_type,
                          ticketDetailController.ticketDetail?.caseType ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.case_reason,
                          ticketDetailController.ticketDetail?.caseReasonName ??
                              "-",
                          Strings.priority,
                          ticketDetailController.ticketDetail?.priority ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.followup_date_time,
                          ticketDetailController
                                  .ticketDetail?.nextFollowupDate ??
                              "-",
                          Strings.case_status,
                          ticketDetailController.ticketDetail?.caseStatus ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.problem_domain,
                          ticketDetailController
                                  .ticketDetail?.caseReasonCategory ??
                              "-",
                          Strings.sub_problem_domain,
                          ticketDetailController
                                  .ticketDetail?.caseReasonSubCategory ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.create_date,
                        " ${ticketDetailController.ticketDetail?.createdate ?? "-"}",
                        Strings.last_modified_date,
                        " ${ticketDetailController.ticketDetail?.updatedate ?? "-"}",
                        null,
                        false,
                        false,
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      /*basicDetailItem(
                          Strings.helper_name,
                          ticketDetailController.ticketDetail?.helperName ??
                              "-",
                          Strings.tat_name,
                          ticketDetailController.showTATDetailsData?.name ??
                              "-",
                              () {

                          },
                          false,
                          true),*/

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
                                titleWidget(Strings.helper_name),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                valueWidget(
                                    ticketDetailController
                                            .ticketDetail?.helperName ??
                                        "-",
                                    false),
                              ],
                            ),
                          ),
                          Expanded(
                            flex: 2,
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                titleWidget(Strings.tat_name),
                                const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING - 1),
                                ticketDetailController.showTATDetailsData !=
                                            null &&
                                        ticketDetailController
                                            .showTATDetailsData!
                                            .name!
                                            .isNotEmpty
                                    ? InkWell(
                                        onTap: () {
                                          log("showTATDetailsData===>${jsonEncode(ticketDetailController.showTATDetailsData)}");

                                          openTatNameWithMappingDetail(
                                              ticketDetailController
                                                  .showTATDetailsData);
                                        },
                                        child: CustomText(
                                          title: (ticketDetailController
                                                  .showTATDetailsData?.name ??
                                              "-"),
                                          colors: AppTheme.colorPrimary,
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.small + 1,
                                          fontWeight: FontWeight.w400,
                                          decoration: TextDecoration.underline,
                                          maxLines: 2,
                                        ),
                                      )
                                    : CustomText(
                                        title: "",
                                      ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.source,
                          " ${ticketDetailController.ticketDetail?.source ?? "-"}",
                          // Strings.sub_source,
                          // " ${ticketDetailController.ticketDetail?.subSource ?? "-"}",
                          "",
                          "",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.helper_name,
                          " ${ticketDetailController.ticketDetail?.helperName ?? "-"}",
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

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.ticket_detail, '', AppTheme.colorPrimary,
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      decoration: isLinkable ? TextDecoration.underline : TextDecoration.none,
      maxLines: 2,
    );
  }

  openPdfViewScreen(
      String? pageTitle, String? networkPathUrl, String? customerName) async {
    var result = await Get.to(PdfViewerPage(
        title: pageTitle ?? " ",
        filePathUrl: networkPathUrl ?? "",
        customerName: customerName));
    if (result != null) {}
  }

  addRemarkTicketDialog(
      BuildContext context, String? pageName, TicketDetail? item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketApproveRejectDialog(
            pageName: pageName,
            ticketApproveRejectBtnAction: this,
            caseId: item?.caseId,
          );
        });
  }

  @override
  void ticketApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      int? caseId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      ticketDetailController.approveRejectTicket(
          viewTicketController: viewTicketController,
          status: Strings.approve.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      ticketDetailController.approveRejectTicket(
          viewTicketController: viewTicketController,
          status: Strings.reject.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    }
  }

  openTicketChangeStatusScreen(
      TicketDetailController ticketDetailController, String? tickT) async {
    var result = await Get.to(TicketChangeStatusScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetailController.ticketDetail,
      Constant.TICKET_TYPE: tickT,
    });

    if (result != null && result == true) {
      ticketDetailController
          .getTicketListData(ticketDetailController.ticketDetail?.caseId ?? 0);
      viewTicketController.clearFilter();
      if (Get.isRegistered<SavbillCareTabController>()) {
        final savbillCareTabController = Get.find<SavbillCareTabController>();
        savbillCareTabController.page = 1;
        savbillCareTabController.update();
        savbillCareTabController.initPlatformState();
      }
    }
  }

  showTicketPriorityDialog(TicketDetail? ticketDetail) {
    if (viewTicketController.ticketPriorityList != null &&
        viewTicketController.ticketPriorityList!.isNotEmpty) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return ChangeTicketPriorityDialog(
                ticketPriorityBtnAction: this,
                ticketDetail: ticketDetail,
                priorityList: viewTicketController.ticketPriorityList!);
          });
    }
  }

  @override
  void ticketPriorityBtnAction(
      {TicketPriority? priority, TicketDetail? ticketDetail}) {
    Get.back();

    if (priority != null && ticketDetail != null) {
      // call update api call
      viewTicketController.changePriorityTicket(priority, ticketDetail);
    }
  }

  openTicketDocumentUploadScreen(TicketDetail? ticketDetail) async {
    Get.to(UploadDocumentScreen(), arguments: {
      Constant.TICKET_ID: ticketDetail?.caseId,
    });
  }

  openLinkTicketScreen(TicketDetail? ticketDetail) async {
    var result = await Get.to(LinkTicket(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      ticketDetailController.getTicketListData(ticketDetail?.caseId ?? 0);
      viewTicketController.clearFilter();
    }
  }

  openScheduleFollowUpScreen(TicketDetail? ticketDetail, int? ticketId) async {
    var result = await Get.to(TicketScheduleFollowUpScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
      Constant.TICKET_ID: ticketId,
    });
    if (result != null && result == true) {
      ticketDetailController.getTicketListData(ticketDetail?.caseId ?? 0);
      viewTicketController.clearFilter();
    }
  }

  onTicketETRScreen(TicketDetail? ticketDetail) async {
    var result = await Get.to(TicketETRScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      // viewTicketController.getAllProblemDomain();
      ticketDetailController.getTicketListData(ticketDetail?.caseId ?? 0);
    }
  }

  onTicketRemarkScreen(TicketDetail? ticketDetail) async {
    var result = await Get.to(TicketRemarkScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      ticketDetailController.getTicketListData(ticketDetail?.caseId ?? 0);
    }
  }

  openDocumentViewTicketScreen(TicketDetail? ticketDetail) async {
    var result = await Get.to(TicketViewDocumentScreen(), arguments: {
      Constant.FROM: Strings.ticket,
      Constant.INVENTORY_ID: ticketDetail!.caseId,
      Constant.CUSTOMER_NAME: ticketDetail.customerName,
    });
    if (result != null && result == true) {
      ticketDetailController.getTicketListData(ticketDetail.caseId ?? 0);
    }
  }

  openRescheduleRemarkFollowUp(
      int? followUPId,
      TicketFollowUpFindAllDataList? followUpListData,
      String? scheduleType) async {
    var result = await Get.to(TicketReScheduleFollowUpScreen(), arguments: {
      Constant.FOLLOW_UP_ID: followUPId,
      Constant.FOLLOW_UP_DATA: followUpListData,
      Constant.SCHEDULE_TYPE: scheduleType
    });

    if (result != null && result == true) {
      ticketDetailController.getTicketListData(followUPId ?? 0);
    }
  }

  openRemarkFollowUp(
      int? followUpId, TicketFollowUpFindAllDataList? followUpListData) async {
    var result = await Get.to(TicketRemarkFollowUp(), arguments: {
      Constant.FOLLOW_UP_ID: followUpId,
      Constant.FOLLOW_UP_DATA: followUpListData,
    });
    if (result != null) {
      ticketDetailController.getTicketListData(followUpListData?.caseId ?? 0);
    }
  }

  openTatNameWithMappingDetail(ShowTATDetailsData? item) {
    Get.to(() => TatNameTicketMapDetail(),
        arguments: {Constant.TAT_NAME_DETAIL: item});
  }

  @override
  void closeFollowUpRemarkBtnAction(
      {String? identifier,
      TextEditingController? remarkController,
      int? followUpId}) {
    Get.back();
    if (identifier!.equalsIgnoreCase("ticketCloseFollowUp")) {
      ticketDetailController.closeRemarkFollowUp(
        followUpId: followUpId!,
        remark: remarkController!.text,
      );
    }
  }
}
