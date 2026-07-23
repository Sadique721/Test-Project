import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/status_bg_view.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

import '../../util/utils.dart';

class TicketListViewItem extends StatelessWidget {
  TicketDetail item;
  final Function()? onFollowupTap;
  final Function()? onAssignTap;
  //UserDetail? userDetail;
  bool? showBtn;

  TicketListViewItem(
      {Key? key,
      required this.item,
    //  this.userDetail,
      this.showBtn,
      this.onFollowupTap,
      this.onAssignTap})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String raisedDate = "",followUpTime = "";
    DateTime followUpDateTime;
    Color? statusTxtColor, typeBgColor = AppTheme.typeInquiry;

    if (item.nextFollowupDate == null || item.nextFollowupDate!.isEmpty) {
      raisedDate = "";
       // raisedDate = Utils.changeDateFormat(item.nextFollowupDate!, Constant.API_DATE_FORMAT);
    }else{
      // raisedDate = item.nextFollowupDate! ?? "";
      raisedDate = Utils.changeDateFormat(item.nextFollowupDate!, Constant.DATE_FORMAT);
    }

    if (item.nextFollowupTime == null || item.nextFollowupTime!.isEmpty) {
      followUpTime = "";
    }else{
      followUpDateTime = DateFormat(Constant.TIME_FORMAT_24).parse(item.nextFollowupTime!);
      followUpTime = DateFormat(Constant.APP_TIME_FORMAT).format(followUpDateTime);
    }
    if (item.caseStatus!.equalsIgnoreCase("Resolved")) {
      statusTxtColor = AppTheme.statusClosedGreen;
    } else if (item.caseStatus!.equalsIgnoreCase("In Progress")) {
      statusTxtColor = AppTheme.statusAssignOrange;
    } else if (item.caseStatus!.equalsIgnoreCase("On Hold")) {
      statusTxtColor = AppTheme.statusOnHold;
    } else if (item.caseStatus!.equalsIgnoreCase("Completed")) {
      statusTxtColor = AppTheme.statusClosedGreen;
    } else if (item.caseStatus!.equalsIgnoreCase("Closed")) {
      statusTxtColor = AppTheme.statusReject;
    } else if (item.caseStatus!.equalsIgnoreCase("Assigned")) {
      statusTxtColor = AppTheme.statusAssignOrange;
    } else {
      //if (item.ticketStatus == "Unassigned")
      statusTxtColor = AppTheme.statusUnAssignGray;
    }

    if (item.caseType!.equalsIgnoreCase("Issue")) {
      typeBgColor = AppTheme.typeIssue;
    } else if (item.caseType!.equalsIgnoreCase("Inquiry")) {
      typeBgColor = AppTheme.typeInquiry;
    } else if (item.caseType!.equalsIgnoreCase("Request")) {
      typeBgColor = AppTheme.typeRequest;
    }

    return Container(
      margin: const EdgeInsets.only(
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  Expanded(
                    flex: 2,
                    child: Row(
                      children: [
                        statusBgView(
                          status: item.caseType!,
                          bgColor: typeBgColor,
                          textColor: AppTheme.colorWhite,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.normal,
                        ),
                      ],
                    ),
                  ),
                  Row(
                    children: [
                      statusBgView(
                        status: item.caseStatus!,
                        bgColor: statusTxtColor,
                        textColor: AppTheme.colorWhite,
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.normal,
                      ),
                    ],
                  ),
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
                Strings.name,
                item.caseTitle?.capitalizeFirst ?? "-",
                Strings.number,
                "#${item.caseNumber}",
              ),
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Padding(
              padding:
              const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.assign,
                item.currentAssigneeName?.capitalizeFirst ?? "-",
                Strings.followup_date,
                "$raisedDate $followUpTime" ,
                // "${item.nextFollowupDate} ${item.nextFollowupTime}",
              ),
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),


            Padding(
              padding:
              const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.create_date,
                item.createdate ?? "-" ,
                Strings.update_date,
                item.updatedate ?? "-",
              ),
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),

            // showBtn == true
            //     ? cardButtonRow(
            //         (item.currentAssigneeId != null &&
            //                 (!item.caseStatus!.isNullOrEmpty() &&
            //                     !item.caseStatus!.equalsIgnoreCase("Closed")))
            //             ? true
            //             : false,
            //         (item.currentAssigneeId == null ||
            //                 (!item.caseStatus!.isNullOrEmpty() &&
            //                     item.caseStatus!
            //                         .equalsIgnoreCase("Unassigned")) ||
            //                 item.currentAssigneeId == userDetail?.userId)
            //             ? true
            //             : false)
            //     : Container(),
          ],
        ),
      ),
    );
  }

  cardButtonRow(followUp, assign) {
    return Row(children: <Widget>[
      followUp
          ? buttonView(
              Strings.comment,
              Constant.BTN_ROUNDED_CORNER,
              assign ? 0 : Constant.BTN_ROUNDED_CORNER,
              AppTheme.colorPrimary,
              onFollowupTap!)
          : Container(),
      (followUp && assign)
          ? Container(
              width: 0.4,
              height: 30,
              color: AppTheme.lable_noramal,
            )
          : Container(),
      assign
          ? buttonView(
              Strings.assign,
              followUp ? 0 : Constant.BTN_ROUNDED_CORNER,
              Constant.BTN_ROUNDED_CORNER,
              AppTheme.colorGreen,
              onAssignTap!)
          : Container(),
    ]);
  }

  buttonView(String btnName, double leftBottom, double rightBottom,
      Color txtColor, Function() onTap) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        child: Container(
          height: Constant.CARD_BOTTOM_BUTTON_H,
          alignment: Alignment.center,
          decoration: BoxDecoration(
              color: AppTheme.colorCardBtn,
              borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(leftBottom),
                  bottomRight: Radius.circular(rightBottom))),
          child: CustomText(
            title: btnName,
            colors: txtColor,
            textAlign: TextAlign.center,
            fontSize: AppTheme.small + 1,
            fontWeight: FontWeight.w500,
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
