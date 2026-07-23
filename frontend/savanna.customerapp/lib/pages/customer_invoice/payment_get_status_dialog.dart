
import 'package:savbill/pages/customer_invoice/response/payment_config_res.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class PaymentGetStatusDialog extends StatefulWidget {
  bool displayDialog;
  PaymentStatusAction paymentGatewayAction;
  List<ActivePaymentConfig> savedConfig;
  InvoiceDetail? plan;

  PaymentGetStatusDialog({
    required this.displayDialog,
    required this.paymentGatewayAction,
    required this.savedConfig,
    required this.plan,
  });

  @override
  State<PaymentGetStatusDialog> createState() => _PaymentGetStatusDialogState();
}

class _PaymentGetStatusDialogState extends State<PaymentGetStatusDialog> {
  @override
  Widget build(BuildContext context) {
    String title = Strings.payment_gateway_method;
    return widget.displayDialog
        ? Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(10.0),
      ),
      child: Container(
        width: MediaQuery.of(context).size.width * 2,
        decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(Constant.SMALL_PADDING)),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            Container(
              color: AppTheme.colorPrimary,
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.MEDIUM_PADDING,
                  horizontal: Constant.SCREEN_PADDING),
              child: Align(
                alignment: Alignment.centerLeft,
                child: CustomText(
                  title: title,
                  colors: AppTheme.colorWhite,
                  fontSize: AppTheme.large,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
            Flexible(
              child: SingleChildScrollView(
                child: Column(
                  children: widget.savedConfig
                      .map((config) => Card(
                    margin: const EdgeInsets.all(
                        Constant.SMALL_PADDING),
                    elevation: 6.0,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                    child: Padding(
                      padding: const EdgeInsets.all(
                          Constant.MEDIUM_PADDING),
                      child: Column(
                        children: [
                          CustomText(
                            title: config.paymentConfigName!,
                            fontSize: AppTheme.large,
                            colors: AppTheme.colorPrimary,
                            fontWeight: FontWeight.bold,
                          ),
                          const SizedBox(
                              height: Constant.SMALL_PADDING),
                          SizedBox(
                              width: 220,
                              child: CustomText(
                                title:
                                config.paymentGatewayInfo ??
                                    "",
                                colors: AppTheme.title_dark,
                                overflow: TextOverflow.ellipsis,
                              )),
                          const SizedBox(
                              height: Constant.SMALL_PADDING),
                          ElevatedButton(
                            style: ElevatedButton.styleFrom(
                                backgroundColor:
                                AppTheme.colorPrimary,
                                shadowColor: Colors.black),
                            onPressed: () => widget
                                .paymentGatewayAction
                                .paymentStatusBtnAction(plan: widget.plan!,
                                selectedItem: config),
                            child: CustomText(
                              title: Strings.proceed,
                              colors: AppTheme.colorWhite,
                            ),
                          ),
                          const SizedBox(
                              height: Constant.SMALL_PADDING),
                        ],
                      ),
                    ),
                  ))
                      .toList(),
                ),
              ),
            ),
            const SizedBox(height: Constant.SMALL_PADDING),
          ],
        ),
      ),
    )
        : SizedBox.shrink();
  }
}

abstract class PaymentStatusAction {
  void paymentStatusBtnAction({
    InvoiceDetail plan, ActivePaymentConfig selectedItem,
  });
}
