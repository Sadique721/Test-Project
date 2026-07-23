
import 'package:savbill/pages/customer_invoice/pdf_viewer_page.dart';
import 'package:savbill/pages/dashboard/model/response/get_ticket_tat_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/pages/dashboard/model/response/show_ticket_etr_report_res.dart';
import 'package:savbill/pages/dashboard/model/response/ticket_follow_up_find_all_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/task_management/model/response/task_followuo_list_response.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/link_task/link_task.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_change_status/task_change_status_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_detail/change_task_priority_dialog.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_detail/task_detail_controller.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_detail/tat_name_task_map_detail.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_etr/task_etr_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_remark/task_remark_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_workflow_audit.dart';
import 'package:savbill/pages/task_management/task_mgmt/upload_doc/task_upload_doc.dart';
import 'package:savbill/pages/task_management/task_mgmt/upload_doc/task_view_document_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/view_task_mgmt_controller.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/follow_up/ticket_schedule_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/close_ticket_remark_follow_up_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/ticket_re_schedule_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_re_schedule/ticket_remark_follow_up.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_status_approve_reject_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import 'package:readmore/readmore.dart';


class TaskDetailScreen extends StatefulWidget {
  @override
  _TaskDetailState createState() => _TaskDetailState();
}

class _TaskDetailState extends State<TaskDetailScreen>
    implements
        TicketApproveRejectBtnAction,
        TaskPriorityBtnAction,
        CloseFollowUpRemarkBtnAction {
  final taskDetailController = Get.put(TaskDetailController());
  final viewTaskController = Get.put(ViewTaskMgmtController());

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<TaskDetailController>(builder: (controller) {
      return Stack(children: [
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: taskDetailController.isLoading),
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
      child: taskDetailController.taskDetail != null
          ? SingleChildScrollView(
        physics: const ScrollPhysics(),
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              actionButtonView(taskDetailController),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              basicDetailView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              taskHistoryView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              ticketInternalRemarksView(),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              InkWell(
                onTap: () {
                  Get.to(TaskWorkflowAudit(), arguments: {
                    Constant.TASK_ID:
                    taskDetailController.taskDetail!.caseId,
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
                              title: Strings.task_assign_audit,
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
              taskAttachmentView(),
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


  ticketInternalRemarksView() {
    if (taskDetailController.followUpDetailList != null) {
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
                  itemCount: taskDetailController.followUpDetailList?.length,
                  itemBuilder: (context, ii) {
                    TaskFollowUpDetail? items =
                    taskDetailController.followUpDetailList![ii];
                    int? lstLength =
                        taskDetailController.followUpDetailList?.length;
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
    if (taskDetailController.showTicketETRReportList != null) {
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
                  taskDetailController.showTicketETRReportList?.length,
                  itemBuilder: (context, ii) {
                    ShowTicketETRReportDataList? items =
                    taskDetailController.showTicketETRReportList![ii];
                    int? lstLength =
                        taskDetailController.showTicketETRReportList?.length;
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
    if (taskDetailController.showTicketTATReportList != null) {
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
                  taskDetailController.showTicketTATReportList?.length,
                  itemBuilder: (context, ii) {
                    GetTicketTATReportDataList? items =
                    taskDetailController.showTicketTATReportList![ii];
                    int? lstLength =
                        taskDetailController.showTicketTATReportList?.length;
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

  taskAttachmentView() {
    if (taskDetailController.attachmentList != null) {
      return Padding(
        padding: const EdgeInsets.only(
            left: Constant.SCREEN_PADDING,
            right: Constant.SCREEN_PADDING,
            top: Constant.SMALL_PADDING - 2),
        child: Card(
          color: AppTheme.colorWhite,
          child: ExpansionTile(
            key: const Key(Strings.task_attachments),
            maintainState: true,
            backgroundColor: AppTheme.colorWhite,
            iconColor: AppTheme.title_dark,
            tilePadding:
            const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
            title: CustomText(
              title: Strings.task_attachments,
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
              taskDetailController.taskDetail!.caseDocDetails != null &&
                  taskDetailController.taskDetail!.caseDocDetails!.isNotEmpty
                  ? ListView.builder(
                  physics: const NeverScrollableScrollPhysics(),
                  scrollDirection: Axis.vertical,
                  shrinkWrap: true,
                  itemCount: taskDetailController.taskDetail!.caseDocDetails?.length,
                  itemBuilder: (context, ii) {
                    CaseDocDetails? items =
                    taskDetailController.taskDetail!.caseDocDetails?[ii];
                    int? lstLength =
                        taskDetailController.taskDetail!.caseDocDetails!.length;
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
                                  (items!.filename != null &&
                                      items.filename!.isNotEmpty)
                                      ? items.filename
                                      : "-",
                                  Strings.status,
                                  (items.docStatus != null &&
                                      items.docStatus!.isNotEmpty)
                                      ? items.docStatus
                                      : "-", () {
                                taskDetailController.download(
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
                                              taskDetailController
                                                  .downloadFile(
                                                  "${UrlConstants.task_document_download}/${items.ticketId}/${items.docId}",
                                                  items);
                                              // openPdfViewScreen(
                                              //     Strings.ticket,
                                              //     "${UrlConstants.ticket_download_document}/${items.ticketId}/${items.docId}");
                                            },
                                            child: const Icon(
                                              Icons.remove_red_eye,
                                              color: AppTheme.colorPrimaryTheme,
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

  taskHistoryView() {
    return Padding(
      padding: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          top: Constant.SMALL_PADDING - 2),
      child: Card(
        color: AppTheme.colorWhite,
        child: ExpansionTile(
          key: const Key(Strings.task_history),
          maintainState: true,
          backgroundColor: AppTheme.colorWhite,
          iconColor: AppTheme.title_dark,
          tilePadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 0),
          title: CustomText(
            title: Strings.task_history,
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
            Container(),Container(),Container(),Container()
            // (taskDetailController.caseUpdateList!.isNotEmpty)
            //     ? ListView.builder(
            //     physics: const NeverScrollableScrollPhysics(),
            //     scrollDirection: Axis.vertical,
            //     shrinkWrap: true,
            //     itemCount: taskDetailController.caseUpdateList?.length,
            //     itemBuilder: (context, ii) {
            //       CaseUpdateList? items =
            //       taskDetailController.caseUpdateList![ii];
            //       int? lstLength =
            //           taskDetailController.caseUpdateList?.length;
            //       return Padding(
            //         padding: EdgeInsets.only(
            //             top: (ii == 0)
            //                 ? Constant.SMALL_PADDING
            //                 : Constant.EXPANTABLE_ITEM_MARGIN,
            //             left: Constant.EXPANTABLE_ITEM_MARGIN,
            //             right: Constant.EXPANTABLE_ITEM_MARGIN,
            //             bottom: (ii == (lstLength! - 1))
            //                 ? Constant.EXPANTABLE_ITEM_MARGIN
            //                 : 0),
            //         child: Container(
            //           decoration: BoxDecoration(
            //             color: AppTheme.expantableItemBg,
            //             border:
            //             Border.all(color: AppTheme.expantableItemBg),
            //             borderRadius: const BorderRadius.all(
            //               Radius.circular(3),
            //             ),
            //           ),
            //           child: Padding(
            //             padding:
            //             const EdgeInsets.all(Constant.SMALL_PADDING),
            //             child: Column(
            //               mainAxisAlignment: MainAxisAlignment.start,
            //               crossAxisAlignment: CrossAxisAlignment.start,
            //               children: [
            //                 basicDetailItem(
            //                     Strings.create_by,
            //                     items?.createdByName ?? "-",
            //                     Strings.create_date,
            //                     items?.createdate ?? "-",
            //                     null,
            //                     false,
            //                     false),
            //                 const SizedBox(height: Constant.SMALL_PADDING),
            //                 basicDetailItem(
            //                     Strings.update_by,
            //                     items?.lastModifiedByName ?? "-",
            //                     Strings.update_date,
            //                     items?.updatedate ?? "-",
            //                     null,
            //                     false,
            //                     false),
            //                 const SizedBox(height: Constant.SMALL_PADDING),
            //                 Row(
            //                   mainAxisSize: MainAxisSize.max,
            //                   crossAxisAlignment: CrossAxisAlignment.center,
            //                   mainAxisAlignment: MainAxisAlignment.end,
            //                   children: [
            //                     Column(
            //                       mainAxisAlignment:
            //                       MainAxisAlignment.center,
            //                       crossAxisAlignment:
            //                       CrossAxisAlignment.end,
            //                       children: [
            //                         titleWidget(
            //                             Strings.view_progress_detail),
            //                         const SizedBox(
            //                             height:
            //                             Constant.VERY_SMALL_PADDING -
            //                                 1),
            //                         InkWell(
            //                           onTap: () async {
            //                             int? lstLength =
            //                                 items?.updateDetails!.length;
            //                             if (items?.updateDetails != null &&
            //                                 lstLength != null &&
            //                                 lstLength > 0) {
            //                               taskHistoryDialog(context,
            //                                   items?.updateDetails!);
            //                             }
            //                           },
            //                           child: Container(
            //                             padding: const EdgeInsets.symmetric(
            //                                 horizontal:
            //                                 Constant.VERY_SMALL_PADDING,
            //                                 vertical: Constant
            //                                     .VERY_SMALL_PADDING -
            //                                     3),
            //                             decoration: BoxDecoration(
            //                                 color: AppTheme.colorPrimary,
            //                                 borderRadius: BorderRadius
            //                                     .circular(Constant
            //                                     .VERY_SMALL_PADDING)),
            //                             child: SvgPicture.asset(
            //                               openTicketInvoice,
            //                               height:
            //                               Constant.MENU_ICON_SIZE + 2,
            //                               width:
            //                               Constant.MENU_ICON_SIZE + 2,
            //                               color: AppTheme.colorWhite,
            //                               fit: BoxFit.fill,
            //                             ),
            //                           ),
            //                         ),
            //                       ],
            //                     ),
            //                   ],
            //                 ),
            //               ],
            //             ),
            //           ),
            //         ),
            //       );
            //     })
            //     : Container(),
          ],
        ),
      ),
    );
  }


  actionButtonView(TaskDetailController taskDetailController) {
    bool? showAssignTask,
        showApprove = false,
        showReject = false,
        showChangeStatus = false,
        showChangePriority = false,
        showLink = true,
        showFollowup = false,
        showUploadDoc = true,
        showChangeProblemDomain = true,
        showTicketRemark = true,
        showETRTask = true,
        showDocumentDownload = true;
    if (taskDetailController.taskDetail!.currentAssigneeId != null ||
        taskDetailController.taskDetail!.currentAssigneeId ==
            taskDetailController.userDetail!.userId) {
      showAssignTask = true;
    } else {
      showAssignTask = false;
    }

    if (taskDetailController.taskDetail!.caseStatus!
        .equalsIgnoreCase("Follow Up") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close")) {
      showAssignTask = true;
    } else if (taskDetailController.taskDetail!.caseStatus != "Closed" &&
        taskDetailController.taskDetail!.caseStatus != "Raise and Close") {
      showAssignTask = false;
    }
    if((taskDetailController.userDetail!.userId == taskDetailController.assignStaffParentId) ||(taskDetailController.userDetail!.userId == taskDetailController.taskDetail!.currentAssigneeId)) {
      if (taskDetailController.taskDetail!.currentAssigneeId == null ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Done") ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Discarded")) {
        showAssignTask = true;
      } else {
        showAssignTask = false;
      }
    }else{
      showAssignTask = true;
    }

    if (taskDetailController.taskDetail!.caseStatus!
        .equalsIgnoreCase("Done") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Discarded") ||
        (taskDetailController.taskDetail!.currentAssigneeId !=
            taskDetailController.userDetail!.userId)) {
      showETRTask = true;
      showApprove = true;
    } else {
      showETRTask = false;
      showApprove = false;
    }

    if (taskDetailController.taskDetail!.caseStatus!
        .equalsIgnoreCase("approved") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("rejected") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Closed") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        taskDetailController.taskDetail!.caseOrder == 1 ||
        taskDetailController.taskDetail!.currentAssigneeId !=
            taskDetailController.userDetail!.userId) {
      showReject = true;
    } else {
      showReject = false;
    }

    if (taskDetailController.changeStatusAccess == true &&
        taskDetailController.taskDetail!.currentAssigneeId ==
            taskDetailController.userDetail?.userId ||
        (taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Resolved") &&
            taskDetailController.taskDetail!.finalClosedById != "null")) {
      if (taskDetailController.taskDetail!.caseStatus!
          .equalsIgnoreCase("rejected") ||
          (taskDetailController.taskDetail!.caseStatus != null &&
              taskDetailController.taskDetail!.caseStatus
              !.equalsIgnoreCase("Closed")) ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Closed") ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Raise and Close") ||
          ((!taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Resolved")) &&
              taskDetailController.taskDetail!.currentAssigneeId !=
                  taskDetailController.userDetail?.userId) ||
          (taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Resolved") &&
              taskDetailController.taskDetail!.finalResolvedById !=
                  taskDetailController.userDetail?.userId)) {
        showChangeStatus = true;
      } else {
        showChangeStatus = false;
      }
    } else {
      showChangeStatus = null;
    }

    if (taskDetailController.taskDetail!.currentAssigneeId == null ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Done") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Discarded") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Resolved") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Rejected") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Cancelled") ||
        (taskDetailController.userDetail!.userId !=
            taskDetailController.assignStaffParentId)) {
      showChangePriority = true;
    } else {
      showChangePriority = false;
    }

    if (taskDetailController.taskDetail!.caseStatus!
        .equalsIgnoreCase("Closed") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Raise and Close") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("rejected")) {
      showLink = true;
      showUploadDoc = true;
    } else {
      showLink = false;
      showUploadDoc = false;
    }


    if (taskDetailController != null) {
      if (taskDetailController.taskDetail!.caseStatus!
          .equalsIgnoreCase("Closed") ||
          taskDetailController.taskDetail!.teamHierarchyMappingId == null ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Raise and Close") ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("rejected") ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Resolved") ||
          ((taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("In Progress"))
              &&
              (taskDetailController.userDetail!.userId !=
                  taskDetailController.assignStaffParentId))

      ) {
        showChangeProblemDomain = true;
      } else {
        showChangeProblemDomain = false;
      }
    }

    if (taskDetailController.taskDetail!.currentAssigneeId != null) {
      if ((taskDetailController.taskDetail!.caseStatus != null &&
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("approved")) ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("rejected") ||
          (taskDetailController.taskDetail!.caseStatus != null &&
              taskDetailController.taskDetail!.caseStatus!
                  .equalsIgnoreCase("Closed")) ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Closed") ||
          taskDetailController.taskDetail!.caseStatus!
              .equalsIgnoreCase("Raise and Close")) {
        showFollowup = true;
      } else {
        showFollowup = true;
      }
    }

    if (taskDetailController.taskDetail!.caseStatus!
        .equalsIgnoreCase("Done") ||
        taskDetailController.taskDetail!.caseStatus!
            .equalsIgnoreCase("Discarded") ||
        taskDetailController.taskDetail!.currentAssigneeId == null) {
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
                            showAssignTask == false
                                ? buttonView(
                                assignSvg,
                                AppTheme.custChangeStatusLight,
                                AppTheme.custChangeStatusDark, () {
                              viewTaskController.getAllTeamList(
                                  taskDetailController.taskId,
                                  taskDetailController.taskDetail
                              );
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
                            showChangeStatus == false
                                ? buttonView(
                                ticketChangeStatusSvg,
                                AppTheme.custEditLight,
                                AppTheme.custEditDark, () {
                              if (taskDetailController
                                  .taskDetail!.caseStatus!
                                  .equalsIgnoreCase("Closed")) {
                                Utils.showSnackbar(
                                    Strings.INFO,
                                    "Can not change status as ticket is closed.",
                                    AppTheme.colorWhite,
                                    AppTheme.colorBlueRView);
                              } else {
                                openTaskChangeStatusScreen(
                                    taskDetailController.taskDetail,
                                    "pTicket");
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
                            showChangePriority == false
                                ? buttonView(
                                changePrioritySvg,
                                AppTheme.custChangeStatusLight,
                                AppTheme.custChangeStatusDark, () {
                              showTicketPriorityDialog(
                                  taskDetailController.taskDetail);
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

                            showUploadDoc == false
                                ? buttonView(
                                documentUploadSvg,
                                AppTheme.custUploadFileLight,
                                AppTheme.custUploadFileDark, () {
                              openTaskDocumentUploadScreen(
                                  taskDetailController.taskDetail);
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

                            // showChangeProblemDomain == true
                            //     ? buttonView(
                            //     linkSvg,
                            //     AppTheme.custNearLocationLight,
                            //     AppTheme.custNearLocationDark, () {
                            //   viewTaskController.checkTaskReAssign(
                            //       taskDetailController.taskDetail);
                            // })
                            //     : buttonView(
                            //     linkSvg,
                            //     AppTheme.colorTransparent
                            //         .withOpacity(0.005),
                            //     AppTheme.colorWhite,
                            //     null),
                            showChangeProblemDomain == true
                                ? buttonView(
                                linkSvg,
                                AppTheme.custNearLocationLight,
                                AppTheme.custNearLocationDark, () {
                              openLinkTicketScreen(
                                  taskDetailController.taskDetail);
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

                          ]),
                    ),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.SMALL_PADDING,
                          horizontal: Constant.SMALL_PADDING),
                      child: Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.end,
                          children: [

                            showETRTask == false
                                ? buttonView(
                                ticketPromiseToPaySvg,
                                AppTheme.custAssignInventoryLight,
                                AppTheme.custAssignInventoryDark, () {
                              onTaskETRScreen(
                                  taskDetailController.taskDetail);
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
                            showTicketRemark == false
                                ? buttonView(
                                msgRemarkSvg,
                                AppTheme.custNearLocationLight,
                                AppTheme.custNearLocationDark, () {
                              onTaskRemarkScreen(
                                  taskDetailController.taskDetail);
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

                            buttonView(
                                pdfSvg,
                                AppTheme.custAssignInventoryLight,
                                AppTheme.custAssignInventoryDark,
                                    () {
                                      onTaskDocumentViewScreen(
                                  taskDetailController.taskDetail);
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
                          taskDetailController.taskDetail?.caseTitle ?? "-",
                          Strings.number,
                          taskDetailController.taskDetail?.caseNumber ??
                              "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.category,
                          taskDetailController
                              .taskDetail?.caseCategoryName ??
                              "-",
                          Strings.task_type,
                          taskDetailController.taskDetail?.caseType ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                          Strings.task_status,
                          taskDetailController.taskDetail?.caseStatus ??
                              "-",
                          Strings.priority,
                          taskDetailController.taskDetail?.priority ?? "-",
                          null,
                          false,
                          false),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      basicDetailItem(
                        Strings.create_date,
                        " ${taskDetailController.taskDetail?.createdate ?? "-"}",
                        Strings.last_modified_date,
                        " ${taskDetailController.taskDetail?.updatedate ?? "-"}",
                        null,
                        false,
                        false,
                      ),
                      const SizedBox(height: Constant.SMALL_PADDING),
                      /*basicDetailItem(
                          Strings.helper_name,
                          taskDetailController.taskDetail?.helperName ??
                              "-",
                          Strings.tat_name,
                          taskDetailController.showTATDetailsData?.name ??
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
                                    taskDetailController.taskDetail?.helperName ??
                                        "-", false),
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
                                taskDetailController.showTATDetailsData != null && taskDetailController.showTATDetailsData!.name!.isNotEmpty ? InkWell(
                                  onTap: (){
                                    openTatNameWithMappingDetail(taskDetailController.showTATDetailsData);
                                  },
                                  child: CustomText(
                                    title: (taskDetailController.showTATDetailsData?.name ?? "-"),
                                    colors: AppTheme.colorPrimary,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.small + 1,
                                    fontWeight: FontWeight.w400,
                                    decoration: TextDecoration.underline,
                                    maxLines: 2,
                                  ),
                                ) : CustomText(title: "",),
                              ],
                            ),
                          ),
                        ],
                      ),
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
    return DynamicAppBar(Strings.task_detail, '', AppTheme.colorPrimary,
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
      viewTaskController.approveRejectTicket(
          status: Strings.approve.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      viewTaskController.approveRejectTicket(
          status: Strings.reject.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    }
  }

  openTaskChangeStatusScreen(
      TaskDetail? taskDetail, String? tickT) async {
    var result = await Get.to(TaskChangeStatusScreen(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
      Constant.TASK_TYPE: tickT,
    });

    if (result != null && result == true) {
      viewTaskController.clearFilter();
    }
  }

  showTicketPriorityDialog(TaskDetail? taskDetail) {
    if (viewTaskController.ticketPriorityList != null &&
        viewTaskController.ticketPriorityList!.isNotEmpty) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return ChangeTaskPriorityDialog(
                ticketPriorityBtnAction: this,
                taskDetail: taskDetail,
                priorityList: viewTaskController.ticketPriorityList!);
          });
    }
  }

  @override
  void taskPriorityBtnAction(
      {TicketPriority? priority, TaskDetail? taskDetail}) {
    Get.back();

    if (priority != null && taskDetail != null) {
      // call update api call
      viewTaskController.changePriorityTicket(priority, taskDetail);
    }
  }

  openTaskDocumentUploadScreen(TaskDetail? taskDetail) async {
    Get.to(TaskUploadDocumentScreen(), arguments: {
      Constant.TASK_ID: taskDetail?.caseId,
    });
  }

  openLinkTicketScreen(TaskDetail? taskDetail) async {
    var result = Get.to(LinkTask(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
    });
    if (result != null && result == true) {
      viewTaskController.clearFilter();
    }
  }

  openScheduleFollowUpScreen(TaskDetail? taskDetail, int? taskId) async {
    var result = Get.to(TicketScheduleFollowUpScreen(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
      Constant.TASK_ID: taskId,
    });
    if (result != null && result == true) {
      viewTaskController.clearFilter();
    }
  }

  onTaskETRScreen(TaskDetail? taskDetail) async {
    var result = Get.to(TaskETRScreen(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
    });
    if (result != null && result == true) {
      viewTaskController.getAllProblemDomain();
    }
  }


  onTaskDocumentViewScreen(TaskDetail? taskDetail) async {
    var result = Get.to(TaskViewDocumentScreen(), arguments: {
      Constant.FROM: Strings.ticket,
      Constant.INVENTORY_ID: taskDetail!.caseId,
      Constant.CUSTOMER_NAME: taskDetail.customerName,
    });
    if (result != null && result == true) {
      viewTaskController.getAllProblemDomain();
    }
  }


  onTaskRemarkScreen(TaskDetail? taskDetail) async {
    var result = Get.to(TaskRemarkScreen(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
    });
    if (result != null && result == true) {
      viewTaskController.getAllProblemDomain();
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
      taskDetailController.getTicketListData();
    }
  }

  openRemarkFollowUp(
      int? followUpId, TicketFollowUpFindAllDataList? followUpListData) async {
    var result = await Get.to(TicketRemarkFollowUp(), arguments: {
      Constant.FOLLOW_UP_ID: followUpId,
      Constant.FOLLOW_UP_DATA: followUpListData,
    });
    if (result != null) {
      taskDetailController.getTicketListData();
    }
  }

  openTatNameWithMappingDetail(ShowTATDetailsData? item) {
    Get.to(()=>TatNameTaskMapDetail(), arguments: {Constant.TAT_NAME_DETAIL: item});
  }


  @override
  void closeFollowUpRemarkBtnAction(
      {String? identifier,
        TextEditingController? remarkController,
        int? followUpId}) {
    Get.back();
    if (identifier!.equalsIgnoreCase("ticketCloseFollowUp")) {
      taskDetailController.closeRemarkFollowUp(
        followUpId: followUpId!,
        remark: remarkController!.text,
      );
    }
  }
}