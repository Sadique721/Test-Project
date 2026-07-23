import 'dart:developer';

import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_countdown_timer/countdown_timer_controller.dart';
import 'package:flutter_countdown_timer/current_remaining_time.dart';
import 'package:flutter_countdown_timer/flutter_countdown_timer.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';

class ViewTicketItem extends StatefulWidget {
  TicketDetail item;
  int userid;
  ViewTicketController? controller;
  final Function()? onTapStaffDetail;
  final Function()? onTapCustomerDetail;
  final Function()? onTapTicketDetail;
  final Function()? onTapEdit;
  final Function()? onTapAssignTicket;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapChangePriority;
  final Function()? onTapPick;
  final Function()? onTapLink;
  final Function()? onTapFollowup;
  final Function()? onTapUploadDoc;
  final Function()? onTapChangeProblemDomain;
  final Function()? onTapSelectItem;
  final Function()? onTapTicketChangeStatus;
  final Function()? onTapSLATimeCounter;
  final Function()? onTapETRTicket;
  final Function()? onTapTicketRemark;

  bool? showActionBtn = false, forSelection = false;

  ViewTicketItem(
      {Key? key,
      required this.item,
      required this.userid,
      this.controller,
      this.onTapTicketDetail,
      this.onTapStaffDetail,
      this.onTapCustomerDetail,
      this.onTapEdit,
      this.onTapAssignTicket,
      this.onTapApprove,
      this.onTapReject,
      this.onTapChangePriority,
      this.onTapPick,
      this.onTapLink,
      this.onTapUploadDoc,
      this.onTapChangeProblemDomain,
      this.onTapFollowup,
      this.showActionBtn,
      this.forSelection,
      this.onTapSelectItem,
      this.onTapTicketChangeStatus,
      this.onTapSLATimeCounter,
      this.onTapETRTicket,
      this.onTapTicketRemark})
      : super(key: key);

  @override
  State<ViewTicketItem> createState() => _ViewTicketItemState();
}

class _ViewTicketItemState extends State<ViewTicketItem> {
  bool showEdit = false,
      showApprove = false,
      showReject = false,
      showChangePriority = false,
      showPick = false,
      showFollowup = false,
      showLink = true,
      showUploadDoc = true,
      showChangeProblemDomain = true,
      showETRTicket = true,
      showTicketRemark = true;

  bool? showAssignTicket, showSLACounter = false;

  CountdownTimerController? timeController;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
    if (timeController != null) {
      timeController!.dispose();
    }
  }

  @override
  Widget build(BuildContext context) {

    final String? resolutionValue = widget.item.caseUpdateList
        ?.expand((u) => u.updateDetails ?? [])
        .firstWhere(
          (h) => h.operation == "Change Root Cause" && h.newvalue != null,
      orElse: () => CaseHistoryDetails(),
    ).newvalue;

    var followFormatDate;
    int? endTime;
    Color? statusTxtColor, typeBgColor = AppTheme.colorGreen;
    String followUpTimeFormated = "";
    if (widget.item.nextFollowupDate != null &&
        widget.item.nextFollowupDate!.isNotEmpty) {
      followUpTimeFormated =
          "${widget.item.nextFollowupDate} ${widget.item.nextFollowupTime}";
      DateTime followTime = DateTime.parse(followUpTimeFormated);
      followFormatDate = DateFormat(Constant.API_DATE_TIME_FORMAT_AM_PM).format(followTime);
      endTime = followTime.toLocal().millisecondsSinceEpoch;
      timeController = CountdownTimerController(endTime: endTime, onEnd: onEnd);
    } else {
      followFormatDate = null;
    }

    if (widget.item.caseStatus != null && widget.item.caseStatus!.isNotEmpty) {
      if (widget.item.caseStatus!.equalsIgnoreCase("Resolved")) {
        statusTxtColor = AppTheme.statusClosedGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("In Progress") ||
          widget.item.caseStatus!.equalsIgnoreCase("open")) {
        statusTxtColor = AppTheme.statusAssignOrange;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("On Hold")) {
        statusTxtColor = AppTheme.statusOnHold;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Completed")) {
        statusTxtColor = AppTheme.statusClosedGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
          widget.item.caseStatus!.equalsIgnoreCase("Raise and Close")) {
        statusTxtColor = AppTheme.statusClosedGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Assigned")) {
        statusTxtColor = AppTheme.statusAssignOrange;
      } else {
        statusTxtColor = AppTheme.statusUnAssignGray;
      }
    } else {
      statusTxtColor = AppTheme.statusUnAssignGray;
    }

    if (widget.item.caseStatus != null && widget.item.caseStatus!.isNotEmpty) {
      if (widget.item.caseStatus!.equalsIgnoreCase("In Progress")) {
        typeBgColor = AppTheme.colorGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Resolved")) {
        typeBgColor = AppTheme.colorGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Completed")) {
        typeBgColor = AppTheme.colorGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Open")) {
        typeBgColor = AppTheme.colorGreen;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
          widget.item.caseStatus!.equalsIgnoreCase("Follow Up")) {
        typeBgColor = AppTheme.colorBlueRView;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Closed")) {
        typeBgColor = AppTheme.colorError;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("rejected")) {
        typeBgColor = AppTheme.colorError;
      } else if (widget.item.caseStatus!.equalsIgnoreCase("Pending") ||
          widget.item.caseStatus!.equalsIgnoreCase("Re-open") ||
          widget.item.caseStatus!.equalsIgnoreCase("On Hold") ||
          widget.item.caseStatus!.equalsIgnoreCase("Out of domain")) {
        typeBgColor = AppTheme.colorBlueRView;
      }
    }

    // if (widget.item.caseStatus != null && widget.item.caseStatus!.isNotEmpty) {
    //   if (widget.item.caseStatus!.equalsIgnoreCase("approved") ||
    //       widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
    //       widget.item.caseStatus!.equalsIgnoreCase("closed") ||
    //       widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
    //       (widget.item.currentAssigneeId != null &&
    //           widget.item.currentAssigneeId != widget.userid)) {
    //     showApprove = false;
    //     showReject = false;
    //   } else {
    //     if ((widget.item.currentAssigneeId != null &&
    //         widget.item.currentAssigneeId == widget.userid)) {
    //       showApprove = true;
    //       showReject = true;
    //     } else {
    //       showApprove = false;
    //       showReject = false;
    //     }
    //   }
    //   if (widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
    //       widget.item.caseStatus!.equalsIgnoreCase("closed")) {
    //     showEdit = false;
    //     showPick = false;
    //     if (widget.item.currentAssigneeId != null) {
    //       showFollowup = true;
    //     } else {
    //       showFollowup = false;
    //     }
    //   } else {
    //     showEdit = true;
    //     showPick = true;
    //     showFollowup = false;
    //   }
    //
    //   if (widget.item.caseStatus!.equalsIgnoreCase("closed")) {
    //     showAssignTicket = false;
    //   } else {
    //     showAssignTicket = true;
    //   }
    //   if ((widget.item.currentAssigneeId != null &&
    //       widget.item.currentAssigneeId == widget.userid)) {
    //     showChangePriority = true;
    //   } else {
    //     showChangePriority = false;
    //   }
    // }

    if (widget.item.caseStatus != null &&
            widget.item.caseStatus!.equalsIgnoreCase("Open") ||
        widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.currentAssigneeId != widget.userid) {
      showEdit = true;
    } else {
      showEdit = false;
    }

    if (widget.item.caseStatus != null &&
            widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
        widget.item.caseStatus!.equalsIgnoreCase("Resolved") ||
        widget.item.currentAssigneeId != null) {
      showPick = true;
    } else {
      showPick = false;
    }

    if (widget.item.caseStatus != null &&
            widget.item.caseStatus!.equalsIgnoreCase("Open") ||
        widget.item.caseStatus!.equalsIgnoreCase("approved") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
        widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.currentAssigneeId == null) {
      showTicketRemark = true;
    } else {
      showTicketRemark = false;
    }

    if (widget.item.currentAssigneeId != null ||
        widget.item.currentAssigneeId == widget.userid) {
      showAssignTicket = true;
    } else {
      showAssignTicket = false;
    }

    if (widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
        widget.item.caseStatus!.equalsIgnoreCase("Follow Up") ||
        widget.item.caseStatus!.equalsIgnoreCase("On Hold") ||
        widget.item.caseStatus!.equalsIgnoreCase("Pending") ||
        widget.item.caseStatus!.equalsIgnoreCase("Out of domain")) {
      showSLACounter = true;
    } else {
      showSLACounter = false;
    }
    if (widget.item.currentAssigneeId != null && widget.controller != null) {
      if (widget.item.caseStatus!.equalsIgnoreCase("Follow Up") ||
          widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
          widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
          // widget.item.caseStatus!.equalsIgnoreCase("In Progress") ||
          (widget.item.currentAssigneeId !=
              widget.controller!.assignStaffParentId)) {
        showAssignTicket = true;
      } else {
        if (widget.item.caseStatus != "Closed" &&
            widget.item.caseStatus != "Raise and Close") {
          showAssignTicket = false;
        }
      }
    }

    if (widget.item.caseStatus!.equalsIgnoreCase("approved") ||
            widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
            widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
            widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
            (widget.item.currentAssigneeId != widget.userid)
        /*widget.item.currentAssigneeId !=
            widget.controller!.userDetail!.userId*/
        ) {
      showETRTicket = true;
      showApprove = true;
    } else {
      showETRTicket = false;
      showApprove = false;
    }

    if (widget.controller != null) {
      if (widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
          widget.item.teamHierarchyMappingId == null ||
          widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
          widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
          widget.item.caseStatus!.equalsIgnoreCase("Resolved") ||
          (widget.item.caseStatus!.equalsIgnoreCase("In Progress") &&
              widget.item.currentAssigneeId !=
                  widget.controller!.assignStaffParentId)) {
        showChangeProblemDomain = true;
      } else {
        showChangeProblemDomain = false;
      }
    }

    if (widget.item.caseStatus!.equalsIgnoreCase("approved") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
        widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.caseOrder == 1 ||
        widget.item.currentAssigneeId != widget.userid) {
      showReject = true;
    } else {
      showReject = false;
    }

    if (widget.item.currentAssigneeId == null ||
        widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.caseStatus!.equalsIgnoreCase("Resolved") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected") ||
        !(widget.item.caseStatus!.equalsIgnoreCase(
            "In Progress") /*&&
            widget.controller!.userDetail!.userId == widget.controller!.assignStaffParentId*/
        )) {
      showChangePriority = true;
    } else {
      showChangePriority = false;
    }

    if (widget.item.caseStatus!.equalsIgnoreCase("Closed") ||
        widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ||
        widget.item.caseStatus!.equalsIgnoreCase("rejected")) {
      showLink = true;
      showUploadDoc = true;
    } else {
      showLink = false;
      showUploadDoc = false;
    }

    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          InkWell(
            onTap: (widget.forSelection != null && widget.forSelection == true)
                ? widget.onTapSelectItem
                : widget.onTapTicketDetail,
            child: Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      (widget.forSelection != null &&
                              widget.forSelection == true)
                          ? widget.item.selected == true
                              ? Icon(
                                  Icons.check_circle,
                                  color: AppTheme.statusClosedGreen,
                                  size: Constant.ICON_SIZE,
                                )
                              : const Icon(
                                  Icons.check_circle,
                                  color: Colors.black38,
                                  size: Constant.ICON_SIZE,
                                )
                          : Container(),
                      (widget.forSelection != null &&
                              widget.forSelection == true)
                          ? const SizedBox(
                              width: Constant.VERY_SMALL_PADDING,
                            )
                          : Container(),
                      RichText(
                        maxLines: 2,
                        softWrap: false,
                        text: TextSpan(
                          text: "#${widget.item.caseNumber.toString()!} ",
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: AppTheme.small + 1,
                            color: AppTheme.colorPrimary,
                          ),
                          children: [
                            TextSpan(
                              text: widget.item.caseTitle,
                              style: TextStyle(
                                fontSize: AppTheme.small + 1,
                                fontWeight: FontWeight.normal,
                                color: AppTheme.title_dark,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      /*statusBgView(
                        status: item.caseType!,
                        bgColor: typeBgColor,
                        textColor: AppTheme.colorWhite,
                        fontSize: AppTheme.verySmall,
                        fontWeight: FontWeight.normal,
                      ),
                      const SizedBox(
                        height: 5,
                      ),*/
                      /*  CustomText(
                          title: item.caseStatus,
                          colors: statusTxtColor,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.verySmall,
                          fontWeight: FontWeight.w600),*/
                      statusBgView(
                        status: widget.item.caseStatus!,
                        bgColor: typeBgColor,
                        textColor: AppTheme.colorWhite,
                        fontSize: AppTheme.verySmall,
                        fontWeight: FontWeight.normal,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            // child: basicDetailItem(
            //     Strings.customer,
            //     (widget.item.userName != null &&
            //             widget.item.userName!.isNotEmpty)
            //         ? "${widget.item.userName}"
            //         : "-",
            //     Strings.assignee,
            //     (widget.item.currentAssigneeName != null &&
            //             widget.item.currentAssigneeName!.isNotEmpty)
            //         ? "${widget.item.currentAssigneeName}"
            //         : "-"),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.customer),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(
                        onTap: widget.onTapCustomerDetail,
                        child: CustomText(
                          title: (widget.item.userName != null &&
                                  widget.item.userName!.isNotEmpty)
                              ? "${widget.item.userName}"
                              : "-",
                          colors: AppTheme.colorPrimary,
                          textAlign: TextAlign.start,
                          decoration: TextDecoration.underline,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.normal,
                          maxLines: 2,
                        ),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(
                        Strings.assignee,
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      InkWell(
                        onTap:
                      (Strings.assignee.isNotEmpty &&
                              Strings.assignee.equalsIgnoreCase(Strings.assignee))
                              ? widget.onTapStaffDetail
                              : null,
                        child: CustomText(
                          title: (widget.item.currentAssigneeName != null &&
                                  widget.item.currentAssigneeName!.isNotEmpty)
                              ? "${widget.item.currentAssigneeName}"
                              : "-",
                          colors: AppTheme.colorPrimary,
                          textAlign: TextAlign.start,
                          decoration: TextDecoration.underline,
                          fontSize: AppTheme.small + 1,
                          fontWeight: FontWeight.normal,

                          maxLines: 1,
                        ),
                      )
                    ],
                  ),
                )
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.followup_date,
                followFormatDate ?? "-",
                Strings.type,
                (widget.item.caseType != null &&
                        widget.item.caseType!.isNotEmpty)
                    ? "${widget.item.caseType}"
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                "${Strings.create_date} & ${Strings.api_time}",
                (widget.item.createdate ?? "-"),
                "${Strings.last_modified_date} & ${Strings.api_time}",
                (widget.item.updatedate ?? "-")),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    titleWidget(Strings.remaining_time),
                    const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                    widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") ? valueWidget("00:00:00:00") : CountdownTimer(
                      controller: timeController,
                      endTime: endTime ?? 00,
                      onEnd: onEnd,
                      widgetBuilder: (_, CurrentRemainingTime? time) {
                        if (time == null) {
                          return valueWidget(
                            "00:00:00:00",
                          );
                        }
                        return valueWidget(
                          "${time.days ?? 00}:${time.hours ?? 00}:${time.min ?? 00}:${time.sec ?? 00}",
                        );
                      },
                    )
                  ],
                ),
              ],
            ), /*basicDetailItem(
                "${Strings.followup_date} & ${Strings.api_time}",
                (followFormatDate.isNotEmpty) ? followFormatDate : "-",
                "${Strings.remaining_time}",
                ("$days : $hours : $minutes : $seconds" ?? "-")),*/
          ),

            widget.item.caseStatus!.equalsIgnoreCase("Raise and Close") || widget.item.caseStatus!.equalsIgnoreCase("Resolved")
                || widget.item.caseStatus!.equalsIgnoreCase("Closed")
              ? Column(
            children: [
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: basicDetailItem(
                  Strings.root_cause_name,
                  widget.item.finalResolutionName!,
                  Strings.resolution,
                  (resolutionValue != null && resolutionValue.isNotEmpty)
                      ? resolutionValue
                      : "-",
                ),
              ),
            ],
          ) : const SizedBox.shrink(),

          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          (widget.showActionBtn != null && widget.showActionBtn == true)
              ? Align(
                  alignment: FractionalOffset.topRight,
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SMALL_PADDING, vertical: 1),
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.end,
                        crossAxisAlignment: CrossAxisAlignment.end,
                        children: [
                          Row(
                              mainAxisAlignment: MainAxisAlignment.end,
                              children: [
                                showEdit == false
                                    ? buttonView(
                                        editSvg,
                                        AppTheme.custEditLight,
                                        AppTheme.custEditDark,
                                        widget.onTapEdit!)
                                    : buttonView(
                                        editSvg,
                                        AppTheme.colorTransparent
                                            .withOpacity(0.005),
                                        AppTheme.colorWhite,
                                        null),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                showPick == false
                                    ? buttonView(
                                        pickTicketSvg,
                                        AppTheme.custNearLocationLight,
                                        AppTheme.custNearLocationDark,
                                        widget.onTapPick!)
                                    : buttonView(
                                        pickTicketSvg,
                                        AppTheme.colorTransparent
                                            .withOpacity(0.005),
                                        AppTheme.colorWhite,
                                        null),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                                showSLACounter == false
                                    ? buttonView(
                                        statusSvg,
                                        AppTheme.custChangeStatusLight,
                                        AppTheme.custChangeStatusDark,
                                        widget.onTapSLATimeCounter!)
                                    : buttonView(
                                        statusSvg,
                                        AppTheme.colorTransparent
                                            .withOpacity(0.005),
                                        AppTheme.colorWhite,
                                        null),
                                const SizedBox(
                                  width: Constant.SMALL_PADDING,
                                ),
                              ])
                          // Row(
                          //     mainAxisAlignment: MainAxisAlignment.end,
                          //     children: [
                          //       showEdit == false
                          //           ? buttonView(
                          //               editSvg,
                          //               AppTheme.custEditLight,
                          //               AppTheme.custEditDark,
                          //               widget.onTapEdit!)
                          //           : buttonView(
                          //               editSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showAssignTicket == false
                          //           ? buttonView(
                          //               assignSvg,
                          //               AppTheme.custPaymentLinkLight,
                          //               AppTheme.custPaymentLinkDark,
                          //               widget.onTapAssignTicket!)
                          //           : buttonView(
                          //               assignSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showApprove == false
                          //           ? buttonView(
                          //               checkSvg,
                          //               AppTheme.custEditLight,
                          //               AppTheme.custEditDark,
                          //               widget.onTapApprove!)
                          //           : buttonView(
                          //               checkSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showReject == false
                          //           ? buttonView(
                          //               cancelSvg,
                          //               AppTheme.custDeleteLight,
                          //               AppTheme.custDeleteDark,
                          //               widget.onTapReject!)
                          //           : buttonView(
                          //               cancelSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showChangePriority == false
                          //           ? buttonView(
                          //               changePrioritySvg,
                          //               AppTheme.custChangeStatusLight,
                          //               AppTheme.custChangeStatusDark,
                          //               widget.onTapChangePriority!)
                          //           : buttonView(
                          //               changePrioritySvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showPick == false
                          //           ? buttonView(
                          //               pickTicketSvg,
                          //               AppTheme.custNearLocationLight,
                          //               AppTheme.custNearLocationDark,
                          //               widget.onTapPick!)
                          //           : buttonView(
                          //               pickTicketSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showLink == false
                          //           ? buttonView(
                          //               linkSvg,
                          //               AppTheme.custAssignInventoryLight,
                          //               AppTheme.custAssignInventoryDark,
                          //               widget.onTapLink!)
                          //           : buttonView(
                          //               linkSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showFollowup
                          //           ? buttonView(
                          //               followUpSvg,
                          //               AppTheme.custPaymentLinkLight,
                          //               AppTheme.custPaymentLinkDark,
                          //               widget.onTapFollowup!)
                          //           : Container(),
                          //       showFollowup
                          //           ? const SizedBox(
                          //               width: Constant.SMALL_PADDING,
                          //             )
                          //           : Container(),
                          //       showUploadDoc == false
                          //           ? buttonView(
                          //               documentUploadSvg,
                          //               AppTheme.custUploadFileLight,
                          //               AppTheme.custUploadFileDark,
                          //               widget.onTapUploadDoc!)
                          //           : buttonView(
                          //               documentUploadSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //     ]),
                          // const SizedBox(height: Constant.SMALL_PADDING),
                          // Row(
                          //     mainAxisAlignment: MainAxisAlignment.end,
                          //     children: [
                          //       buttonView(
                          //           ticketChangeStatusSvg,
                          //           AppTheme.custChangeStatusLight,
                          //           AppTheme.custChangeStatusDark,
                          //           widget.onTapTicketChangeStatus!),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showSLACounter == false
                          //           ? buttonView(
                          //               statusSvg,
                          //               AppTheme.custChangeStatusLight,
                          //               AppTheme.custChangeStatusDark,
                          //               widget.onTapSLATimeCounter!)
                          //           : buttonView(
                          //               statusSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showChangeProblemDomain == false
                          //           ? buttonView(
                          //               linkSvg,
                          //               AppTheme.custNearLocationLight,
                          //               AppTheme.custNearLocationDark,
                          //               widget.onTapChangeProblemDomain!)
                          //           : buttonView(
                          //               linkSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showETRTicket == false
                          //           ? buttonView(
                          //               ticketHoldSvg,
                          //               AppTheme.custAssignInventoryLight,
                          //               AppTheme.custAssignInventoryDark,
                          //               widget.onTapETRTicket!)
                          //           : buttonView(
                          //               ticketHoldSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       ),
                          //       showTicketRemark == false
                          //           ? buttonView(
                          //               msgRemarkSvg,
                          //               AppTheme.custNearLocationLight,
                          //               AppTheme.custNearLocationDark,
                          //               widget.onTapTicketRemark!)
                          //           : buttonView(
                          //               msgRemarkSvg,
                          //               AppTheme.colorTransparent
                          //                   .withOpacity(0.005),
                          //               AppTheme.colorWhite,
                          //               null),
                          //       const SizedBox(
                          //         width: Constant.SMALL_PADDING,
                          //       )
                          //     ]),
                        ],
                      ),
                    ),
                  ),
                )
              : Container(),
          (widget.showActionBtn != null && widget.showActionBtn == true)
              ? const SizedBox(
                  height: Constant.SMALL_PADDING,
                )
              : Container(),
        ]),
      ),
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
          height: Constant.BTN_HEIGHT_M - 10,
          width: Constant.BTN_HEIGHT_M - 10,
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

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(
                value1,
              ),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: InkWell(
            onTap:
                (title2.isNotEmpty && title2.equalsIgnoreCase(Strings.assignee))
                    ? widget.onTapStaffDetail
                    : null,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                titleWidget(title2),
                const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                valueWidget(
                  value2,
                ),
              ],
            ),
          ),
        )
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(
    String? value,
  ) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  void onEnd() {
    if (timeController != null) {
      timeController!.disposeTimer();
    }
  }

  String? getRootCauseNewValue(TicketDetail ticket) {
    for (final update in ticket.caseUpdateList ?? []) {
      for (final history in update.updateDetails ?? []) {
        if (history.operation == "Change Root Cause" &&
            history.newvalue != null &&
            history.newvalue!.isNotEmpty) {
          return history.newvalue; // ✅ Found match, return immediately
        }
      }
    }
    return null; // No matching record found
  }

// void setCountDown() {
//   final reduceSecondsBy = 1;
//   final seconds = myDuration!.inSeconds - reduceSecondsBy;
//
//   log("reduceSecondsBy=>$seconds");
//   if (seconds < 0) {
//     countdownTimer!.cancel();
//   } else {
//     myDuration = Duration(seconds: seconds);
//     log("myDuration$myDuration");
//   }
//   // widget.controller!.update();
// }
}
