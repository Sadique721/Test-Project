import 'package:savbill/pages/customer_payment/online_payment/online_payment_controller.dart';
import 'package:savbill/pages/customer_payment/response/online_payment_audit_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';

class OnlinePaymentListItem extends StatelessWidget {
  OnlineAuditData item;
  int index, userId;
  OnlinePaymentController? controller;
  bool showStatusBtn = false, showDownloadBtn = false;
  final Function()? onTapRetryPayment;
  final Function()? onTapAddToWallet;

  OnlinePaymentListItem(
      {Key? key,
      required this.index,
      required this.item,
      required this.userId,
      this.controller,
      this.onTapRetryPayment,
      this.onTapAddToWallet})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    String status = "", gatewayStatus = "";
    String merchantName = "";
    Color? statusTxtColor = AppTheme.statusPending;
    Color? gatewayStatusTxtColor = AppTheme.statusPending;

    bool isRetryDisabled = item.gatewayStatus?.toLowerCase() == 'success' ||
        item.gatewayStatus?.toLowerCase() == 'successful';

    bool isManualSettlementDisabled =
        !(item.pgTransactionId == null || item.pgTransactionId == 'NA');

    if (item.status!.isNotEmpty) {
      if (item.status!.toLowerCase().equalsIgnoreCase("success")) {
        statusTxtColor = AppTheme.statusApprove;
        status = item.status!;
      } else if (item.status!.toLowerCase().equalsIgnoreCase("successful")) {
        statusTxtColor = AppTheme.statusApprove;
        status = item.status!;
      } else if (item.status!.toLowerCase().equalsIgnoreCase("initiate")) {
        statusTxtColor = AppTheme.statusPending;
        status = item.status!;
      } else if (item.status!.toLowerCase().equalsIgnoreCase("failed")) {
        statusTxtColor = AppTheme.statusReject;
        status = item.status!;
      } else if (item.status!.toLowerCase().equalsIgnoreCase("pending")) {
        statusTxtColor = AppTheme.statusPending;
        status = item.status!;
      } else {
        status = item.status!;
      }
    }

    if (item.gatewayStatus != null  && item.gatewayStatus!.isNotEmpty) {
      if (item.gatewayStatus!.toLowerCase().equalsIgnoreCase("success")) {
        gatewayStatusTxtColor = AppTheme.statusApprove;
        gatewayStatus = item.gatewayStatus!;
      } else if (item.gatewayStatus!
          .toLowerCase()
          .equalsIgnoreCase("successful")) {
        gatewayStatusTxtColor = AppTheme.statusApprove;
        gatewayStatus = item.gatewayStatus!;
      } else if (item.gatewayStatus!
          .toLowerCase()
          .equalsIgnoreCase("initiate")) {
        gatewayStatusTxtColor = AppTheme.statusPending;
        gatewayStatus = item.gatewayStatus!;
      } else if (item.gatewayStatus!.toLowerCase().equalsIgnoreCase("failed")) {
        gatewayStatusTxtColor = AppTheme.statusReject;
        gatewayStatus = item.gatewayStatus!;
      } else if (item.gatewayStatus!
          .toLowerCase()
          .equalsIgnoreCase("pending")) {
        gatewayStatusTxtColor = AppTheme.statusPending;
        gatewayStatus = item.gatewayStatus!;
      } else {
        gatewayStatus = item.gatewayStatus!;
      }
    }

    if (item.merchantName != null) {
      if (item.merchantName!.equalsIgnoreCase("MoMo Pay")) {
        merchantName = Strings.momo_pay;
      } else {
        merchantName = item.merchantName!;
      }
    } else {
      merchantName = "";
    }

    return Card(
      margin: const EdgeInsets.only(
        left: Constant.SCREEN_PADDING,
        right: Constant.SCREEN_PADDING,
        bottom: Constant.MEDIUM_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
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
                  Strings.reference_no,
                  item.orderId.toString() ?? "",
                  Strings.transaction_no,
                  (item.pgTransactionId != null &&
                          item.pgTransactionId!.isNotEmpty)
                      ? item.pgTransactionId
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.account_number,
                  (item.accountNumber != null && item.accountNumber!.isNotEmpty)
                      ? item.accountNumber
                      : "-",
                  Strings.customer_username,
                  (item.customerUsername != null &&
                          item.customerUsername!.isNotEmpty)
                      ? item.customerUsername
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.payment_amount,
                  item.payment!.toStringAsFixed(2) ?? "",
                  Strings.payment_date,
                  item.paymentDate != null
                      ? getDateTimeFormat(item.paymentDate)
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                  Strings.merchant_name,
                  merchantName,
                  Strings.transaction_date,
                  item.transactionDate != null
                      ? getDateTimeFormat(item.transactionDate)
                      : "-"),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING),
              child: basicDetailItem(
                Strings.payer_mobile_number,
                (item.payerMobileNumber != null &&
                        item.payerMobileNumber!.isNotEmpty)
                    ? item.payerMobileNumber
                    : "-",
                Strings.auto_payment_initiator,
                (item.autoPaymentInitiator != null &&
                        item.autoPaymentInitiator!.isNotEmpty)
                    ? item.autoPaymentInitiator
                    : "-",
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: Row(
                  // mainAxisSize: MainAxisSize.max,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  // mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Expanded(
                      flex: 3,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          titleWidget(Strings.status),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.VERY_SMALL_PADDING),
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: Constant.SMALL_PADDING,
                                  vertical: Constant.VERY_SMALL_PADDING),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(
                                    Constant.LARGE_PADDING),
                                color: statusTxtColor,
                              ),
                              child: CustomText(
                                  title: status,
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
                    const SizedBox(height: Constant.VERY_SMALL_PADDING),
                    Expanded(
                      flex: 2,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          titleWidget(Strings.gateway_status),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.VERY_SMALL_PADDING),
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: Constant.SMALL_PADDING,
                                  vertical: Constant.VERY_SMALL_PADDING),
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(
                                    Constant.LARGE_PADDING),
                                color: gatewayStatusTxtColor,
                              ),
                              child: CustomText(
                                  title: gatewayStatus,
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
                )),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
            Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SMALL_PADDING),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Expanded(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          titleWidget(Strings.failure_reason),
                          const SizedBox(
                              height: Constant.VERY_SMALL_PADDING - 1),
                          item.failureDescription == null
                              ? CustomText(title: "-",colors: AppTheme.title_dark,)
                              : Padding(
                                  padding: const EdgeInsets.symmetric(
                                      vertical: Constant.VERY_SMALL_PADDING),
                                  child: Container(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: Constant.SMALL_PADDING,
                                        vertical: Constant.VERY_SMALL_PADDING),
                                    decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(
                                          Constant.LARGE_PADDING),
                                      color: AppTheme.colorGrey,
                                    ),
                                    child: CustomText(
                                        title: item.failureDescription,
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
                    const SizedBox(height: Constant.VERY_SMALL_PADDING),
                    Expanded(
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.end,
                        mainAxisAlignment: MainAxisAlignment.end,
                        children: [
                          buttonView(
                              customerRenewPaymentSvg,
                              isRetryDisabled
                                  ? AppTheme.buttonDisableColor
                                  : AppTheme.colorPrimary,
                              AppTheme.colorWhite,
                              isRetryDisabled ? () {} : onTapRetryPayment!),
                          const SizedBox(
                            width: Constant.MEDIUM_PADDING,
                          ),
                          buttonView(
                              ticketPromiseToPaySvg,
                              isManualSettlementDisabled
                                  ? AppTheme.buttonDisableColor
                                  : AppTheme.colorPrimary,
                              AppTheme.colorWhite,
                              isManualSettlementDisabled
                                  ? () {}
                                  : onTapAddToWallet!),
                        ],
                      ),
                    ),
                  ],
                )),
            const SizedBox(
              height: Constant.MEDIUM_PADDING,
            ),
          ],
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
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
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
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }

  String getDateTimeFormat(String? startEndExpiryDate) {
    DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API_H_M)
        .parse(startEndExpiryDate!);
    return DateFormat(Constant.API_DATE_TIME_FORMAT_H_M).format(date);
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
}
