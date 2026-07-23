import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_approval/model/la_follow_up_lead_list_res.dart';
import 'package:savbill/pages/lead_approval/team_follow_up/pa_team_follow_up_approval_lead_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class PendingApprovalTeamFollowUpApprovalLeadItem extends StatelessWidget {
  FollowUpList? item;
  PATeamFollowUpApprovalLeadController? controller;
  final Function()? onTapRescheduleFollowUpTeam;
  final Function()? onTapCloseFollowUpTeam;
  final Function()? onTapCallFollowUpTeam;

  PendingApprovalTeamFollowUpApprovalLeadItem({
    Key? key,
    required this.item,
    required this.controller,
    this.onTapRescheduleFollowUpTeam,
    this.onTapCloseFollowUpTeam,
    this.onTapCallFollowUpTeam,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    // String? leadSubSourceName;
    Color? statusColor;

    bool? isFollowUpTeamLead = true;
    if (item!.status!.equalsIgnoreCase("Closed") ||
        item!.status!.equalsIgnoreCase("closed")) {
      statusColor = AppTheme.statusPending;
    } else if (item!.status!.equalsIgnoreCase("Pending") ||
        item!.status!.equalsIgnoreCase("pending")) {
      statusColor = AppTheme.statusReject;
    } else if (item!.status!.equalsIgnoreCase("ReSchedule") ||
        item!.status!.equalsIgnoreCase("reschedule")) {
      statusColor = AppTheme.statusPending;
    } else {
      statusColor = AppTheme.statusPending;
    }


    if (item!.status!.equalsIgnoreCase("Closed") ||
        item!.status!.equalsIgnoreCase("status")) {
      isFollowUpTeamLead = false;
    } else {
      isFollowUpTeamLead = true;
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
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.lead_name,
                (item!.leadMasterName != null &&
                    item!.leadMasterName!.isNotEmpty)
                    ? "${item!.leadMasterName}"
                    : "-",
                Strings.followup_name,
                (item!.followUpName != null && item!.followUpName!.isNotEmpty)
                    ? item!.followUpName
                    : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
              Strings.followup_date_time,
              (item!.followUpDatetime != null) ? item!.followUpDatetime : "-",
              Strings.remarks,
              (item!.remarks != null) ? item!.remarks : "-",
            ),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
            const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.status),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: Constant.VERY_SMALL_PADDING,
                        ),
                        child: Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: Constant.SMALL_PADDING,
                              vertical: Constant.VERY_SMALL_PADDING),
                          decoration: BoxDecoration(
                            borderRadius:
                            BorderRadius.circular(Constant.LARGE_PADDING),
                            color: statusColor,
                          ),
                          child: CustomText(
                              title: item!.status ?? "-",
                              colors: AppTheme.colorWhite,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.small,
                              maxLines: 2,
                              height: 1,
                              fontWeight: FontWeight.w500),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            )
          ),
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          // Padding(
          //   padding:
          //   const EdgeInsets.symmetric(horizontal: Constant.MEDIUM_PADDING),
          //   child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
          //     buttonView(checkSvg, AppTheme.custEditLight,
          //         AppTheme.custEditDark, onTapApprove!),
          //     const SizedBox(
          //       width: Constant.SMALL_PADDING,
          //     ),
          //     buttonView(cancelSvg, AppTheme.custDeleteLight,
          //         AppTheme.custDeleteDark, onTapReject!),
          //   ]),
          // ),

          Align(
            alignment: Alignment.topRight,
            child: Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.SMALL_PADDING,
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  // crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    isFollowUpTeamLead == false
                        ? buttonView(
                      rescheduleFollowUpSvg,
                      AppTheme.colorDisableGray,
                      AppTheme.colorWhite,
                      null,
                    ) :
                    buttonView(
                      rescheduleFollowUpSvg,
                      AppTheme.colorPrimary,
                      AppTheme.colorWhite,
                      onTapRescheduleFollowUpTeam,
                    ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING + 3,
                    ),
                    isFollowUpTeamLead == false
                        ? buttonView(
                      rejectRemoveSvg,
                      AppTheme.colorDisableGray,
                      AppTheme.colorWhite,
                      null,
                    ) :
                    buttonView(
                      rejectRemoveSvg,
                      AppTheme.colorPrimary,
                      AppTheme.colorWhite,
                      onTapCloseFollowUpTeam,
                    ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING + 3,
                    ),
                    buttonView(
                      phoneIconSvg,
                      AppTheme.colorPrimary,
                      AppTheme.colorWhite,
                      onTapCallFollowUpTeam,
                    ),
                  ]),
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
