import 'dart:developer';

import 'package:savbill/pages/credit_note/response/credit_note_res.dart';
import 'package:savbill/pages/credit_note/view_credit_note_controller.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../util/resources.dart';
import '../../util/strings.dart';
import '../../widgets/coustom_text.dart';

class CreditViewListItem extends StatelessWidget {
  CreditNoteDetailsList item;
  int index, userId;
  final Function()? onApproveTap;
  final Function()? onRejectTap;
  final Function()? onDownloadTap;
  final Function()? onAuditStatusTap;
  final Function()? onInvoiceTap;
  final Function()? onCustomerTap;
  final Function()? onPickTab;
  final Function()? onReassignTab;
  CreditNoteController? controller;
  bool? isShowBtn;
  String? currency;
  bool showStatusBtn = true,
      showDownloadBtn = true,
      showApproveBtn = true,
      showPickBtn = true,
      showReassignBtn = true;

  CreditViewListItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.userId,
      this.onApproveTap,
      this.onRejectTap,
      this.onDownloadTap,
      this.onAuditStatusTap,
      this.onInvoiceTap,
      this.onCustomerTap,
      this.onPickTab,
      this.onReassignTab,
      this.controller,
      this.isShowBtn,
      this.currency})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String paymentDate = "", status = "";
    Color? statusTxtColor = AppTheme.statusPending,
        typeBgColor = AppTheme.statusUnAssignGray;

    if (item.paymentdate != null && item.paymentdate!.isNotEmpty) {
      paymentDate = item.paymentdate!;
    }
    if (item.status!.equalsIgnoreCase(Strings.pending) ||
        item.status!.equalsIgnoreCase(Strings.rejected)) {
      showDownloadBtn = false;
    } else {
      showDownloadBtn = true;
    }

    if (item.status!.equalsIgnoreCase(Strings.fully_adjusted) ||
        item.status!.equalsIgnoreCase(Strings.advance.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.approved.toLowerCase()) ||
        item.status!.equalsIgnoreCase("Partially Adjusted") ||
        item.status!.equalsIgnoreCase("Partialy Adjusted") ||
        item.status!.equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.approverid != null) {
      showPickBtn = false;
    } else {
      showPickBtn = true;
    }

/*______________________________ approve & rejected ________________________________________*/

    log("appovedId====>>>${item.approverid}");
    log("userIdStaff===>>>${controller!.userDetail!.userId}");

    if (item.status!.equalsIgnoreCase(Strings.fully_adjusted) ||
        item.status!.equalsIgnoreCase(Strings.advance.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.approved.toLowerCase()) ||
        item.status!.equalsIgnoreCase(Strings.rejected.toLowerCase()) ||
        item.status!.equalsIgnoreCase("Partialy Adjusted") ||
        item.status!.equalsIgnoreCase(Strings.partialy_adjusted) ||
        item.approverid != controller!.userDetail!.userId) {
      showApproveBtn = false;
    } else {
      showApproveBtn = true;
    }

/*_____________________________ reassign ______________________________________*/



    if (item.status!.isNotEmpty) {
      if (item.status!.equalsIgnoreCase(Strings.pending.toLowerCase())) {
        statusTxtColor = AppTheme.statusPending;
        status = Strings.generated;
      } else if (item.status!.equalsIgnoreCase(Strings.approved) ||
          item.status!.equalsIgnoreCase(Strings.fully_adjusted)) {
        statusTxtColor = AppTheme.statusApprove;
        status = Strings.adjusted;
      } else if (item.status!.equalsIgnoreCase(Strings.partialy_adjusted)) {
        statusTxtColor = AppTheme.statusApprove;
        status = Strings.partialy_adjusted;
      } else if (item.status!.equalsIgnoreCase(Strings.rejected)) {
        statusTxtColor = AppTheme.statusReject;
        status = Strings.rejected;
      }
      // status = item.status!;
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
              height: Constant.MEDIUM_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    flex: 1,
                    child: InkWell(
                      onTap: onCustomerTap,
                      child: CustomText(
                          title: item.customerName ?? "",
                          colors: AppTheme.colorAccent,
                          textAlign: TextAlign.start,
                          maxLines: 2,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500),
                    ),
                  ),
                  Container(
                    decoration: BoxDecoration(
                        color: statusTxtColor,
                        borderRadius:
                            BorderRadius.circular(Constant.MEDIUM_PADDING)),
                    padding: const EdgeInsets.symmetric(
                        vertical: Constant.STATUS_PADDING_TB,
                        horizontal: Constant.SMALL_PADDING),
                    child: CustomText(
                        title: status,
                        colors: AppTheme.colorWhite,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.w500),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title:
                    "${isShowBtn == false ? Strings.payment_by : Strings.create_by} : ${isShowBtn == false ? item.customerName ?? "" : item.createbyname ?? ""}",
                colors: isShowBtn == false
                    ? AppTheme.lable_noramal
                    : AppTheme.lable_noramal,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title: "${Strings.credit_date} : ${item.paymentdate ?? ""}",
                colors: AppTheme.lable_noramal,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: Row(
                children: [
                  CustomText(
                    title: "${Strings.amount} : ",
                    colors: AppTheme.lable_noramal,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.verySmall + 1,
                    fontWeight: FontWeight.w400,
                  ),
                  InkWell(
                    onTap: onCustomerTap,
                    child: CustomText(
                      title: "$currency${item.amount ?? ""}",
                      colors: AppTheme.colorAccent,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.verySmall + 1,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title: "${Strings.document_no} : ${item.documentno ?? ""}",
                colors: AppTheme.lable_noramal,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title: "${Strings.reference_no} : ${item.referenceno ?? ""}",
                colors: AppTheme.lable_noramal,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: CustomText(
                title: "${Strings.remarks} : ${item.remarks ?? ""}",
                colors: AppTheme.lable_noramal,
                textAlign: TextAlign.start,
                fontSize: AppTheme.verySmall + 1,
                fontWeight: FontWeight.w400,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            cardButtonRow(),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
          ],
        ),
      ),
    );
  }

  cardButtonRow() {
    return Row(mainAxisAlignment: MainAxisAlignment.end, children: [
      showDownloadBtn
          ? buttonView(fileDownloadSvg, AppTheme.custUploadFileLight,
              AppTheme.custUploadFileDark, onDownloadTap!)
          : buttonView(fileDownloadSvg, AppTheme.colorLightGrey,
              AppTheme.colorDisableGray, null),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      showPickBtn
          ? buttonView(pickTicketSvg, AppTheme.custAssignInventoryLight,
              AppTheme.custAssignInventoryDark, onPickTab!)
          : buttonView(pickTicketSvg, AppTheme.colorLightGrey,
              AppTheme.colorDisableGray, null),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      showApproveBtn
          ? buttonView(checkSvg, AppTheme.custEditLight, AppTheme.custEditDark,
              onApproveTap!)
          : buttonView(checkSvg, AppTheme.colorLightGrey,
              AppTheme.colorDisableGray, null),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      showApproveBtn
          ? buttonView(cancelSvg, AppTheme.custDeleteLight,
              AppTheme.custDeleteDark, onRejectTap!)
          : buttonView(cancelSvg, AppTheme.colorLightGrey,
              AppTheme.colorDisableGray, null),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      buttonView(invoiceDetailSvg, AppTheme.custPaymentLinkLight,
          AppTheme.custPaymentLinkDark, onInvoiceTap!),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      (isShowBtn != null && isShowBtn == true)
          ? buttonView(auditStatusSvg, AppTheme.custAssignInventoryLight,
              AppTheme.custAssignInventoryDark, onAuditStatusTap!)
          : Container(),
      (isShowBtn != null && isShowBtn == true)
          ? const SizedBox(
              width: Constant.SMALL_PADDING,
            )
          : Container(),
      !(item.status!.equalsIgnoreCase("Partialy Adjusted") ||
          item.status!.equalsIgnoreCase(Strings.fully_adjusted)) ? buttonView(
          assignSvg,
          showReassignBtn ? AppTheme.custEditLight : AppTheme.custDeleteLight,
          AppTheme.custEditDark,
          onReassignTab!) : buttonView(
          assignSvg,
          showReassignBtn ? AppTheme.colorLightGrey : AppTheme.colorDisableGray,
          AppTheme.custEditDark,
          null),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      )
    ]);
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
}
