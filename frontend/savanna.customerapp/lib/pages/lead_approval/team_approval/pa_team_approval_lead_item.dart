import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_approval/model/la_follow_up_lead_list_res.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval_lead_controller.dart';
import 'package:savbill/pages/lead_approval/team_follow_up/pa_team_follow_up_approval_lead_controller.dart';
import 'package:savbill/pages/lead_management/lead_follow_up/lead_re_schedule_follow_up_screen.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

class PendingApprovalTeamApprovalLeadItem extends StatelessWidget {
  LAAssignContent? item;
  PATeamApprovalLeadController? controller;
  final Function()? onTapApprove;
  final Function()? onTapReject;

  PendingApprovalTeamApprovalLeadItem({
    Key? key,
    required this.item,
    required this.controller,
    this.onTapApprove,
    this.onTapReject,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color? statusColor;
    String? leadSubSourceName;

    if(item!.leadSubSourceId != null){
      leadSubSourceName = item!.leadSubSourceName;
    }else if(item!.leadAgentId != null){
      leadSubSourceName = item!.leadAgentName;
    }else if (item!.leadBranchId != null){
      leadSubSourceName = item!.leadBranchName;
    }else if (item!.leadCustomerId != null){
      leadSubSourceName = item!.leadCustomerName;
    }else if (item!.leadPartnerId != null){
      leadSubSourceName = item!.leadPartnerName;
    }else if(item!.leadServiceAreaId != null){
      leadSubSourceName = item!.leadServiceAreaName;
    }else if (item!.leadStaffId != null){
      leadSubSourceName = item!.leadStaffName;
    }else{
      leadSubSourceName = "-";
    }



    if(item!.leadStatus!.equalsIgnoreCase("Inquiry")){
      statusColor = AppTheme.colorGreen;
    }else if(item!.leadStatus!.equalsIgnoreCase("Rejected")){
      statusColor = AppTheme.statusReject;
    }else if (item!.leadStatus!.equalsIgnoreCase("Re-Inquiry")){
      statusColor = AppTheme.statusPending;
    }else if(item!.leadStatus!.equalsIgnoreCase("Converted")){
      statusColor = AppTheme.colorGreen;
    }




    // followUpDetails?.status == 'Closed' || followUpDetails?.status == 'closed'
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
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.lead_name,
                "${item!.title ?? ""} ${item!.firstname ?? ""} ${item!.lastname ?? ""}",
                Strings.mobile_number,
                (item!.mobile != null && item!.mobile!.isNotEmpty)
                    ? item!.mobile
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.lead_source,
              (item!.leadSourceName != null) ? item!.leadSourceName!.toString() : "0",
              Strings.remarks,
              (item!.remarks != null) ? item!.remarks : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Expanded(
                    flex: 2,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        titleWidget(Strings.assigneeName),
                        const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                        valueWidget(item!.assigneeName ?? "-"),
                      ],
                    ),
                  ),
                  Expanded(
                    flex: 1,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        titleWidget(Strings.status),
                        const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: Constant.SMALL_PADDING,
                              vertical: Constant.VERY_SMALL_PADDING - 2),
                          decoration: BoxDecoration(
                            borderRadius:
                                BorderRadius.circular(Constant.LARGE_PADDING),
                            color: statusColor,
                          ),
                          child: CustomText(
                              title: item!.leadStatus ?? "-",
                              colors: AppTheme.colorWhite,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              maxLines: 2,
                              height: 1,
                              fontWeight: FontWeight.w500),
                        ),
                      ],
                    ),
                  ),
                ],
              )),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.MEDIUM_PADDING),
            child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              buttonView(checkSvg, AppTheme.custEditLight,
                  AppTheme.custEditDark, onTapApprove!),
              const SizedBox(
                width: Constant.SMALL_PADDING,
              ),
              buttonView(cancelSvg, AppTheme.custDeleteLight,
                  AppTheme.custDeleteDark, onTapReject!),
            ]),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function() onTap) {
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


  openScheduleRemarkFollowUp(int? followUpId,String? scheduleType) async {
    var result = await Get.to(LeadReScheduleFollowUpScreen(),arguments: {
      Constant.FOLLOW_UP_ID: followUpId,
      Constant.SCHEDULE_TYPE:scheduleType
    });

    if (result != null && result == true) {
      controller!.getPATeamApprovalLeadList();
    }
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
