import 'package:savbill/pages/shift_location/request/approve_customer_address_req.dart';
import 'package:savbill/pages/shift_location/response/new_address_shift_location_res.dart';
import 'package:savbill/pages/shift_location/shift_location_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:get/get_core/src/get_main.dart';

import 'approve_reject_shift_location_dialog.dart';
import 'customer_shift_locaiton_work_flow.dart';

class ViewShiftLocationItem extends StatefulWidget
    implements ApproveRejectShiftBtnAction {
  NewcustomerAddress item;
  ShiftLocationController? controller;
  final Function()? onTapPick;
  final Function()? onTapApprove;
  final Function()? onTapReject;
  final Function()? onTapWorkFlow;
  final Function()? onTapReassign;

  ViewShiftLocationItem({
    Key? key,
    required this.item,
    this.controller,
    this.onTapApprove,
    this.onTapPick,
    this.onTapReject,
    this.onTapWorkFlow,
    this.onTapReassign,

  }) : super(key: key);

  @override
  State<ViewShiftLocationItem> createState() => _ViewShiftLocationItemState();

  @override
  void approveRejectShiftDetails(
      {String? identifier,
      TextEditingController? remarkController,
      ApproveCustomerAddressReq? approveCustomerAddressReq}) {
    // TODO: implement approveRejectShiftDetails
  }
}

class _ViewShiftLocationItemState extends State<ViewShiftLocationItem> {
  bool? approveBtnDisable = false,
      rejectBtnDisable = false,
      pickBtnDisable = false,
      assignShiftLocation = false;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    if(widget.item.status != null && ! widget.item.status!.equalsIgnoreCase("NewActivation") || widget.item.nextStaff != null){
      pickBtnDisable = false;
    }else{
      pickBtnDisable = false;
    }

    // if(widget.item.nextStaff != null ||
    //     (widget.item.nextStaff != widget.controller!.userDetail!.userId) ||
    //     (widget.item.status != null && !widget.item.status!.equalsIgnoreCase("NewActivation"))){
    //   approveBtnDisable = true;
    //   rejectBtnDisable = true;
    // }else{
    //   approveBtnDisable = false;
    //   rejectBtnDisable = false;
    // }

    if (widget.item != null) {
      if (widget.item.nextStaff == null ||
          widget.item.nextStaff !=
              widget.controller!.userDetail!.userId ||
          !widget.item.status!
              .equalsIgnoreCase("NewActivation")) {
        approveBtnDisable = true;
      } else {
        approveBtnDisable = false;
      }
    }


    if (widget.item.nextStaff == null ||
        widget.item.nextStaff !=
            widget.controller!.userDetail!.userId ||
        (widget.item.status != null &&
            !widget.item.status!
                .equalsIgnoreCase("NewActivation"))) {
      rejectBtnDisable = true;
    } else {
      rejectBtnDisable = false;
    }


    // if(widget.item.status != null && widget.item.status!.equalsIgnoreCase("Active") ||
    //     (widget.item.nextStaff != widget.controller!.userDetail!.userId) || widget.item.status == null){
    //   assignShiftLocation = true;
    // }else{
    //   assignShiftLocation = false;
    // }

    if (widget.item.nextStaff == null ||
        widget.item.status!
            .equalsIgnoreCase("Active") ||
        widget.item.nextStaff !=
            widget.controller!.userDetail!.userId) {
      assignShiftLocation = true;
    } else {
      assignShiftLocation = false;
    }

    // address?.status == 'Active' ||
    //     address?.nextStaff != loggedInStaffId ||
    //     address?.status == null

   /* if (widget.controller!.newCustomerAddressData != null) {
      if (widget.controller!.newCustomerAddressData!.nextStaff == null ||
          widget.controller!.newCustomerAddressData!.nextStaff !=
              widget.controller!.userDetail!.userId ||
          !widget.controller!.newCustomerAddressData!.status!
              .equalsIgnoreCase("NewActivation")) {
     approveBtnDisable = true;
      } else {
       approveBtnDisable = false;
      }

      if (widget.controller!.newCustomerAddressData!.nextStaff == null ||
          widget.controller!.newCustomerAddressData!.nextStaff !=
              widget.controller!.userDetail!.userId ||
          (widget.controller!.newCustomerAddressData!.status != null &&
              !widget.controller!.newCustomerAddressData!.status!
                  .equalsIgnoreCase("NewActivation"))) {
       rejectBtnDisable = true;
      } else {
       rejectBtnDisable = false;
      }

      if (widget.controller!.newCustomerAddressData!.nextStaff == null ||
          widget.controller!.newCustomerAddressData!.status!
              .equalsIgnoreCase("Active") ||
          widget.controller!.newCustomerAddressData!.nextStaff !=
              widget.controller!.userDetail!.userId) {
       assignShiftLocation = true;
      } else {
        assignShiftLocation = false;
      }
    }*/

    return Padding(
      padding: const EdgeInsets.all(8.0),
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
              child: basicDetailItem(
                Strings.new_address,
               widget.item.fullAddress ?? "",
                Strings.status,
                widget.item.status ?? "",
                // "${item.nextFollowupDate} ${item.nextFollowupTime}",
              ),
            ),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.requester_by,
                  widget.item.requestedByName ??
                      "",
                  Strings.requested_date,
                  widget.item.requestedDate ??
                      ""),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.SMALL_PADDING,
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  // crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    pickBtnDisable  == true ? buttonView(pickTicketSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, widget.onTapPick)
                        : buttonView(pickTicketSvg, AppTheme.unUseCardBg,
                            AppTheme.colorWhite, null),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                approveBtnDisable == false
                        ? buttonView(checkSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, widget.onTapApprove)
                        : buttonView(checkSvg, AppTheme.unUseCardBg,
                            AppTheme.colorWhite, null),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                  rejectBtnDisable == false
                        ? buttonView(cancelSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite, widget.onTapReject)
                        : buttonView(cancelSvg, AppTheme.unUseCardBg,
                            AppTheme.colorWhite, null),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                    buttonView(assignInventorySvg, AppTheme.colorPrimary,
                        AppTheme.colorWhite,widget.onTapWorkFlow ),
                    const SizedBox(
                      width: Constant.SMALL_PADDING,
                    ),
                 assignShiftLocation == false
                        ? buttonView(assignSvg, AppTheme.colorPrimary,
                            AppTheme.colorWhite,widget.onTapReassign)
                        : buttonView(assignSvg, AppTheme.unUseCardBg,
                            AppTheme.colorWhite, null),
                  ]),
            ),
            const SizedBox(
              height: Constant.VERY_SMALL_PADDING,
            ),
          ],
        ),
      ),
    );
  }

  buttonView(String btnName, Color bgColor, Color txtColor, Function()? onTap) {
    return InkWell(
      onTap: onTap,
      child: Material(
        elevation: 3.0,
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
            height: Constant.ICON_SIZE + 5,
            width: Constant.ICON_SIZE + 5,
            color: txtColor,
            fit: BoxFit.fitWidth,
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
