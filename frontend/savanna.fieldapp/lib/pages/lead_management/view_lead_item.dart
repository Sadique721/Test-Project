
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_management/lead_details/lead_details.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/lead_management/view_lead_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/material.dart';
import 'package:flutter_countdown_timer/countdown_timer_controller.dart';
import 'package:flutter_countdown_timer/current_remaining_time.dart';
import 'package:flutter_countdown_timer/flutter_countdown_timer.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewLeadItem extends StatefulWidget {
  LeadMasterListData item;
  int userid;
  ViewLeadController? controller;
  final Function()? onTapAddNotes;
  final Function()? onTapEdit;
  final Function()? onTapApproveLead;
  final Function()? onTapRejectLead;
  final Function()? onTapPickLead;
  final Function()? onTapCloseLead;
  final Function()? onTapReOpenLead;
  final Function()? onTapReassignLead;
  final Function()? onTapLeadStatus;
  final Function()? onTapLeadDocument;

  bool? showActionBtn = false, forSelection = false;

  ViewLeadItem({
    Key? key,
    required this.item,
    required this.userid,
    this.controller,
    required this.onTapAddNotes,
    this.onTapEdit,
    this.onTapApproveLead,
    this.onTapRejectLead,
    this.onTapPickLead,
    this.onTapCloseLead,
    this.onTapReOpenLead,
    this.onTapReassignLead,
    this.onTapLeadStatus,
    this.onTapLeadDocument
    // this.showActionBtn,
    // this.forSelection,
  }) : super(key: key);

  @override
  State<ViewLeadItem> createState() => _ViewLeadItemState();
}

class _ViewLeadItemState extends State<ViewLeadItem> {
  bool showEdit = false,
      showAddNote = false,
      showApprove = false,
      showReject = false,
      showPick = false,
      showUploadDoc = false,
      showLeadStatus = false,
      showCloseLead = true,
      showReassignLead = false,
      showReOpenLead = false;

  // bool? showAssignTicket, showSLACounter = false;
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
    Color? leadStatusBgColor = AppTheme.statusApprove,
        cafStatusBgColor = AppTheme.statusApprove;
    String? leadStatusTxt, cafStatusTxt, leadSubSourceTxt, assigneeNameTxt;

    if (widget.item.leadStatus != null && widget.item.leadStatus!.isNotEmpty) {
      if (widget.item.leadStatus!.equalsIgnoreCase("Inquiry")) {
        leadStatusBgColor = AppTheme.statusApprove;
        leadStatusTxt = widget.item.leadStatus;
      } else if (widget.item.leadStatus!.equalsIgnoreCase("Rejected")) {
        leadStatusBgColor = AppTheme.statusReject;
        leadStatusTxt = widget.item.leadStatus;
      } else if (widget.item.leadStatus!.equalsIgnoreCase("Re-Inquiry")) {
        leadStatusBgColor = AppTheme.statusPending;
        leadStatusTxt = widget.item.leadStatus;
      } else if (widget.item.leadStatus!.equalsIgnoreCase("Converted")) {
        leadStatusBgColor = AppTheme.statusApprove;
        leadStatusTxt = widget.item.leadStatus;
      }
    } else {
      leadStatusTxt = "-";
      leadStatusBgColor = AppTheme.statusApprove;
    }

    if (widget.item.cstatus != null && widget.item.leadStatus!.isNotEmpty) {
      if (widget.item.cstatus!.equalsIgnoreCase("Active") ||
          widget.item.cstatus!.equalsIgnoreCase("New Activation")|| widget.item.cstatus!.equalsIgnoreCase("NewActivation")) {
        cafStatusBgColor = AppTheme.statusApprove;
        cafStatusTxt = widget.item.cstatus;
      } else if (widget.item.cstatus!.equalsIgnoreCase("Rejected") ||
          widget.item.cstatus!.equalsIgnoreCase("Reject")) {
        cafStatusBgColor = AppTheme.statusReject;
        cafStatusTxt = widget.item.cstatus;
      }
    } else {
      cafStatusTxt = "-";
      cafStatusBgColor = AppTheme.title_dark;
    }

    if (widget.item.leadSubSourceId != null) {
      leadSubSourceTxt = widget.item.leadSubSourceName;
    } else if (widget.item.leadAgentId != null) {
      leadSubSourceTxt = widget.item.leadAgentName;
    } else if (widget.item.leadBranchId != null) {
      leadSubSourceTxt = widget.item.leadBranchName;
    } else if (widget.item.leadCustomerId != null) {
      leadSubSourceTxt = widget.item.leadCustomerName;
    } else if (widget.item.leadPartnerId != null) {
      leadSubSourceTxt = widget.item.leadPartnerName;
    } else if (widget.item.leadServiceAreaId != null) {
      leadSubSourceTxt = widget.item.leadServiceAreaName;
    } else if (widget.item.leadStaffId != null &&
        widget.item.leadSubSourceId == null &&
        widget.item.leadPartnerId == null) {
      leadSubSourceTxt = widget.item.leadStaffName;
    } else {
      leadSubSourceTxt = widget.item.leadStaffName ?? "-";
    }

    if (widget.item.assigneeName != null) {
      assigneeNameTxt = widget.item.assigneeName;
    } else if (widget.item.assigneeName == null &&
        (widget.item.leadStatus!.equalsIgnoreCase("Inquiry") ||
            widget.item.leadStatus!.equalsIgnoreCase("Re-Inquiry"))) {
      assigneeNameTxt = "In Progress";
    } else if (widget.item.assigneeName == null &&
        (widget.item.leadStatus!.equalsIgnoreCase("Converted") ||
            widget.item.leadStatus!.equalsIgnoreCase("Rejected"))) {
      assigneeNameTxt = "-";
    }

    int? endTime;
    String followUpTimeFormated = "";

    if (widget.item.status != "Active") {
      if (widget.item.nextfollowupdate != null &&
          widget.item.nextfollowupdate!.isNotEmpty) {
        followUpTimeFormated = "${widget.item.nextfollowupdate} ${widget.item.nextfollowuptime}";
        DateTime followTime = DateTime.parse(followUpTimeFormated);
        endTime = followTime.toLocal().millisecondsSinceEpoch;
        timeController = CountdownTimerController(endTime: endTime, onEnd: onEnd);
      }
    }

    if (!(widget.controller!.userDetail!.userId ==
            widget.item.nextApproveStaffId) ||
        widget.item.leadStatus!.equalsIgnoreCase("Converted") ||
        widget.item.leadStatus!.equalsIgnoreCase("Rejected")) {
      showEdit = true;
    } else {
      showEdit = false;
    }

    if ((widget.item.nextApproveStaffId != null &&
            ((!widget.item.leadStatus!.equalsIgnoreCase("Inquiry")) ||
                (!widget.item.leadStatus!.equalsIgnoreCase("Re-Inquiry")))) ||
        (widget.item.nextApproveStaffId == null &&
            widget.item.leadStatus!.equalsIgnoreCase("Rejected"))) {
      showPick = true;
    } else {
      showPick = false;
    }

    if (widget.controller!.userDetail!.userId !=
            widget.item.nextApproveStaffId ||
        widget.item.leadStatus!.equalsIgnoreCase("Rejected") ||
        widget.item.leadStatus!.equalsIgnoreCase("Converted")) {
      showApprove = true;
    } else {
      showApprove = false;
    }

    if (!(widget.controller!.userDetail!.userId ==
            widget.item.nextApproveStaffId) ||
        widget.item.leadStatus!.equalsIgnoreCase("Rejected") ||
        widget.item.leadStatus!.equalsIgnoreCase("Converted")) {
      showReject = true;
    } else {
      showReject = false;
    }

    if (widget.item.leadStatus!.equalsIgnoreCase("Rejected")) {
      showLeadStatus = true;
    } else {
      showLeadStatus = false;
    }

    if (widget.item.leadStatus!.equalsIgnoreCase("Rejected") ||
        widget.item.leadStatus!.equalsIgnoreCase("Converted") ||
        !(widget.controller!.userDetail!.userId ==
            widget.item.nextApproveStaffId)) {
      showCloseLead = true;
    } else if (widget.item.leadReopenAllow == false) {
      showCloseLead = false;
    }

    if (!(widget.controller!.userDetail!.userId ==
            widget.item.nextApproveStaffId) ||
        widget.item.leadStatus!.equalsIgnoreCase("Converted")) {
      showUploadDoc = true;
    } else {
      showUploadDoc = false;
    }

    if (widget.item.leadStatus!.equalsIgnoreCase("Rejected")) {
      showReassignLead = true;
    } else {
      showReassignLead = false;
    }

    if (widget.item.leadStatus!.equalsIgnoreCase("Rejected")) {
      showReOpenLead = true;
    } else {
      if (widget.item.leadReopenAllow != true) {
        showReOpenLead = false;
      }
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
            onTap: (){
              openLeadDetailsScreen(widget.item.id);
            },
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
                      RichText(
                        maxLines: 2,
                        softWrap: false,
                        text: TextSpan(
                          text: "${widget.item.title?? ""} ${widget.item.firstname ?? "" } ${widget.item.lastname ?? ""} " ,
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            fontSize: AppTheme.small + 1,
                            color: AppTheme.colorPrimary,
                          ),
                        ),
                      ),

                      CustomText(
                        title: "(${widget.item.leadNo})",
                        colors: AppTheme.lable_noramal,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.w400,
                        maxLines: 2,
                      )
                    ],
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                      statusBgView(
                        status: leadStatusTxt!,
                        bgColor: leadStatusBgColor,
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
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.mobile_number,
                widget.item.mobile ?? "-",
                Strings.lead_source,
                widget.item.leadSourceName ?? "-"),
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
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.lead_sub_source),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                        leadSubSourceTxt,
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
                        Strings.caf_status,
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      CustomText(
                        title: cafStatusTxt,
                        colors: cafStatusBgColor,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small + 1,
                        fontWeight: FontWeight.normal,
                        maxLines: 2,
                      ),
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
                Strings.assigneeName,
                assigneeNameTxt,
                Strings.isp_name,
                (widget.item.mvnoName != null &&
                        widget.item.mvnoName!.isNotEmpty)
                    ? "${widget.item.mvnoName}"
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.createdByDetails,
                widget.item.createdByName ?? "-",
                Strings.convertedDate,
                (widget.item.cafConvertedDate != null &&
                        widget.item.cafConvertedDate!.isNotEmpty)
                    ? "${widget.item.cafConvertedDate}"
                    : "-"),
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
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(
                        Strings.convertedBy,
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                        (widget.item.cafCovertedStaffName != null &&
                                widget.item.cafCovertedStaffName!.isNotEmpty)
                            ? "${widget.item.cafCovertedStaffName}"
                            : "-",
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
                      titleWidget(Strings.remaining_time),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      CountdownTimer(
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
                      ),
                    ],
                  ),
                )
              ],
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Align(
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
                    Row(mainAxisAlignment: MainAxisAlignment.end, children: [
// Add Note
                      showAddNote == false
                          ? buttonView(
                              openTicketInvoice,
                              AppTheme.custUploadFileLight,
                              AppTheme.custUploadFileDark,
                              widget.onTapAddNotes)
                          : buttonView(
                              editSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),

// Edit Button
                      showEdit == false
                          ? buttonView(editSvg, AppTheme.custEditLight,
                              AppTheme.custEditDark, widget.onTapEdit)
                          : buttonView(
                              editSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// Pick Button
                      showPick == false
                          ? buttonView(
                              pickTicketSvg,
                              AppTheme.custNearLocationLight,
                              AppTheme.custNearLocationDark,
                              widget.onTapPickLead)
                          : buttonView(
                              pickTicketSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// Approve Button
                      showApprove == false
                          ? buttonView(checkSvg, AppTheme.custEditLight,
                              AppTheme.custEditDark, widget.onTapApproveLead)
                          : buttonView(
                              checkSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// Reject Button
                      showReject == false
                          ? buttonView(cancelSvg, AppTheme.custDeleteLight,
                              AppTheme.custDeleteDark, widget.onTapRejectLead)
                          : buttonView(
                              cancelSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      //View Lead Status Button
                      showLeadStatus == false
                          ? buttonView(
                              statusSvg,
                              AppTheme.custChangeStatusLight,
                              AppTheme.custChangeStatusDark,
                          widget.onTapLeadStatus)
                          : buttonView(
                              statusSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// close Lead Button
                      showCloseLead == false
                          ? buttonView(
                              closeCafSVG,
                              AppTheme.custAssignInventoryLight,
                              AppTheme.custAssignInventoryDark,
                              widget.onTapCloseLead)
                          : buttonView(
                              closeCafSVG,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                    ])
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Align(
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
                    Row(mainAxisAlignment: MainAxisAlignment.end, children: [
// Lead Reopen
                      showReOpenLead == true
                          ? buttonView(
                              reOpenSvg,
                              AppTheme.custChangeStatusLight,
                              AppTheme.custChangeStatusDark,
                              widget.onTapReOpenLead)
                          : const SizedBox.shrink(),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// Upload Document
                      showUploadDoc == false
                          ? buttonView(
                              documentUploadSvg,
                              AppTheme.custUploadFileLight,
                              AppTheme.custUploadFileDark,
                              widget.onTapLeadDocument)
                          : buttonView(
                              documentUploadSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
// Reassign Lead
                      showReassignLead == false
                          ? buttonView(
                              assignSvg,
                              AppTheme.custAssignInventoryLight,
                              AppTheme.custAssignInventoryDark,
                              widget.onTapReassignLead)
                          : buttonView(
                              documentUploadSvg,
                              AppTheme.colorTransparent.withOpacity(0.005),
                              AppTheme.colorWhite,
                              null),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                    ])
                  ],
                ),
              ),
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
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
          height: Constant.BTN_HEIGHT_M - 5,
          width: Constant.BTN_HEIGHT_M - 5,
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
            // onTap:
            // (title2.isNotEmpty && title2.equalsIgnoreCase(Strings.assignee))
            //     ? widget.onTapStaffDetail
            //     : null,
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



  openLeadDetailsScreen(int? leadMasterId) async {
    var result = await Get.to(LeadDetailScreen(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
      Constant.LEAD_DASHBOARD_FLAG: false,
    });
    if (result != null && result == true) {
      // Get.back();
    }
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
