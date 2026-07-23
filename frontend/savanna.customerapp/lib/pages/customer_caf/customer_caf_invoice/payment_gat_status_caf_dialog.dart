import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/pages/customer_invoice/response/payment_config_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';

class PaymentGetCAFStatusDialog extends StatefulWidget {
  bool displayDialog;
  PaymentCAFStatusAction paymentGatewayAction;
  List<ActivePaymentConfig> savedConfig;
  Invoicesearchlist? plan;

  PaymentGetCAFStatusDialog({
    required this.displayDialog,
    required this.paymentGatewayAction,
    required this.savedConfig,
    required this.plan,
  });

  @override
  State<PaymentGetCAFStatusDialog> createState() => _PaymentGetCAFStatusDialogState();
}

class _PaymentGetCAFStatusDialogState extends State<PaymentGetCAFStatusDialog> {
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
                                .paymentCAFStatusBtnAction(plan: widget.plan!,
                                selectedItem: config,context: context),
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

abstract class PaymentCAFStatusAction {
  void paymentCAFStatusBtnAction({
    Invoicesearchlist plan, ActivePaymentConfig selectedItem,BuildContext? context
  });
}