import 'dart:developer';

import 'package:savbill/pages/lead_management/model/view_lead_doc_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

class LeadDocItemListView extends StatefulWidget {
  LeadDocContent item;
  UserDetail? userDetail;
  final Function()? onTapEdit;
  final Function()? onTapDelete;

  LeadDocItemListView({
    Key? key,
    required this.item,
    required this.userDetail,
    this.onTapEdit,
    this.onTapDelete,
  }) : super(key: key);

  @override
  State<LeadDocItemListView> createState() => _LeadDocItemListViewState();
}

class _LeadDocItemListViewState extends State<LeadDocItemListView> {
  bool showEdit = false,
      showApprove = false,
      showReject = false,
      showReassign = false,
      showEditAccess = false,
      showVerifyDoc = true,
      showPick = false;

  @override
  Widget build(BuildContext context) {
    // if (widget.item.nextStaff != widget.userDetail!.userId) {
    //   showReject = true;
    //   showApprove = true;
    //   showReassign = true;
    // } else {
    //   showReject = false;
    //   showApprove = false;
    //   showReassign = false;
    // }

    // if (widget.item.docStatus!
    //     .equalsIgnoreCase(Strings.pending.toLowerCase())) {
    //   if (widget.item.nextStaff == widget.userDetail!.userId) {
    //     showPick = true;
    //   } else {
    //     showPick = false;
    //   }
    // } else if (widget.item.docStatus!
    //     .equalsIgnoreCase(Strings.verified.toLowerCase())) {
    //   if (widget.item.nextStaff != widget.userDetail!.userId) {
    //     showPick = true;
    //   } else {
    //     showPick = false;
    //   }
    // }

    if (widget.item.docStatus!.equalsIgnoreCase("Rejected")) {
      showEditAccess = true;
    } else {
      showEditAccess = false;
    }

    log("DocumentMode==>${widget.item.mode!}");

    log("DocumentStatus==>${widget.item.docStatus!}");

    if (widget.item.mode!.equalsIgnoreCase("Online")) {
      if ((widget.item.docStatus!.equalsIgnoreCase("Verified") &&
              widget.item.mode!.equalsIgnoreCase("Online")) ||
          widget.item.docStatus!.equalsIgnoreCase("verified")) {
        showVerifyDoc = true;
      } else {
        showVerifyDoc = false;
      }
    }

    return Container(
      margin: const EdgeInsets.only(
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING,
          bottom: Constant.MEDIUM_PADDING),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Container(
          // padding: const EdgeInsets.all(Constant.SMALL_PADDING),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.MEDIUM_PADDING),
                child: basicDetailItem(
                    Strings.document_no,
                    widget.item.documentNumber ?? "-",
                    Strings.document_status,
                    widget.item.docStatus ?? "-"),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.MEDIUM_PADDING),
                child: basicDetailItem(
                    Strings.document_type,
                    widget.item.docType,
                    Strings.document_sub_type,
                    widget.item.docSubType),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.MEDIUM_PADDING),
                child: basicDetailItem(
                    Strings.filename,
                    widget.item.filename ?? "-",
                    Strings.document_mode,
                    widget.item.mode ?? "-"),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.MEDIUM_PADDING),
                child: basicDetailItem(
                    Strings.remarks, widget.item.remark ?? "-", "-", "-"),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              cardButtonRow()
            ],
          ),
        ),
      ),
    );
  }

  cardButtonRow() {
    return Column(
      children: [
        Row(mainAxisAlignment: MainAxisAlignment.end, children: <Widget>[
          showEditAccess == false
              ? buttonIconView(
                  editSvg,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  AppTheme.colorPrimaryTheme,
                  widget.onTapEdit!)
              : buttonIconView(
                  editSvg,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  Constant.BTN_ROUNDED_CORNAR + 1 + 1,
                  Constant.BTN_ROUNDED_CORNAR,
                  Constant.BTN_ROUNDED_CORNAR + 1,
                  AppTheme.colorLightGrey,
                  null),
          const SizedBox(
            width: Constant.SMALL_PADDING,
          ),
          buttonIconView(
              deleteSvg,
              Constant.BTN_ROUNDED_CORNAR + 1,
              Constant.BTN_ROUNDED_CORNAR + 1,
              Constant.BTN_ROUNDED_CORNAR + 1,
              Constant.BTN_ROUNDED_CORNAR + 1,
              AppTheme.statusReject,
              widget.onTapDelete!),
          const SizedBox(
            width: Constant.SMALL_PADDING,
          ),
        ]),
        const SizedBox(
          height: Constant.SMALL_PADDING,
        ),
      ],
    );
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

  buttonIconView(String bindPortSvg, double leftBottom, double rightBottom,
      double leftTop, double rightTop, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: EdgeInsets.symmetric(
            horizontal: Constant.SMALL_PADDING+2,
            vertical: Constant.SMALL_PADDING),
        alignment: Alignment.center,
        decoration: BoxDecoration(
            color: txtColor,
            borderRadius: BorderRadius.only(
                bottomLeft: Radius.circular(leftBottom),
                bottomRight: Radius.circular(rightBottom),
                topLeft: Radius.circular(leftTop),
                topRight: Radius.circular(rightTop))),
        child: SvgPicture.asset(
          bindPortSvg,
          height: Constant.ACTION_ICON_SIZE-5,
          width: Constant.ACTION_ICON_SIZE-5,
          color: AppTheme.colorWhite,
          // fit: BoxFit.fill,
        ),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Flexible(
          flex: 1,
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
        Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.end,
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

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
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
}
