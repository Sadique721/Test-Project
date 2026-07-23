import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class CustomerSelectionDialog extends StatefulWidget {
  const CustomerSelectionDialog({
    Key? key,
  }) : super(key: key);

  @override
  _CustomerSelectionDialogState createState() =>
      _CustomerSelectionDialogState();
}

class _CustomerSelectionDialogState extends State<CustomerSelectionDialog> {
  int selectedCustomer = -1;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
      ),
      elevation: 0,
      // clipBehavior: Clip.antiAliasWithSaveLayer,
      clipBehavior: Clip.hardEdge,
      insetPadding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      backgroundColor: Colors.transparent,
      child: contentBox(context),
    );
  }

  contentBox(BuildContext context) {
    return Stack(children: [
      AlertDialog(
        insetPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING * 2,
        ),
        contentPadding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING,
        ),
        clipBehavior: Clip.antiAliasWithSaveLayer,
        backgroundColor: AppTheme.colorWhite,
        shape: const RoundedRectangleBorder(
            borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
        content: StatefulBuilder(
            builder: (BuildContext context, StateSetter _setState) {
          return Container(
            width: MediaQuery.of(context).size.width,
            color: AppTheme.colorWhite,
            child: SingleChildScrollView(
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: "Select the customer type...",
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.large,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.MEDIUM_PADDING),
                    IntrinsicHeight(
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          Expanded(
                              child: customerSelectionCard(
                                  Strings.prepaid,
                                  prepaidCustomerSvg,
                                  selectedCustomer == 1 ? true : false,
                                   setState)),
                          VerticalDivider(
                            color: AppTheme.colorPrimary,
                            thickness: 1,
                            indent: Constant.VERY_EXTRA_LARGE_PADDING,
                            endIndent: Constant.VERY_EXTRA_LARGE_PADDING,
                          ),
                          Expanded(
                              child: customerSelectionCard(
                                  Strings.postpaid,
                                  postpaidCustomerSvg,
                                  selectedCustomer == 2 ? true : false,
                                   setState)),
                        ],
                      ),
                    ),
                    const SizedBox(height: Constant.MEDIUM_PADDING * 2),
                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              if (selectedCustomer == -1) {
                                Utils.showSnackbar(
                                    Strings.INFO,
                                    Strings.please_select_customer_type,
                                    AppTheme.colorWhite,
                                    AppTheme.colorBlueRView);
                              } else {
                                if (selectedCustomer == 1) {
                                  Get.offAllNamed(AppRoutes.CUSTOMER_LIST,
                                      arguments: {
                                        Constant.CUSTOMER_TYPE: Strings.prepaid,
                                      });
                                } else if (selectedCustomer == 2) {
                                  Get.offAllNamed(AppRoutes.CUSTOMER_LIST,
                                      arguments: {
                                        Constant.CUSTOMER_TYPE:
                                            Strings.postpaid,
                                      });
                                }
                              }
                            },
                            child: Container(
                              padding: const EdgeInsets.symmetric(
                                  vertical: Constant.LARGE_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorLightGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(6.0)),
                              ),
                              child: CustomText(
                                title: Strings.next,
                                colors: AppTheme.colorPositive,
                                fontSize: AppTheme.medium,
                                fontWeight: FontWeight.w500,
                              ),
                            ),
                          ),
                        )
                      ],
                    ),
                  ]),
            ),
          );
        }),
      ),
      Positioned(
        child: GestureDetector(
          onTap: () {
            Get.back();
          },
          child: Align(
            alignment: Alignment.topRight,
            child: Icon(Icons.close, color: AppTheme.colorWhite),
          ),
        ),
      ),
    ]);
  }

  customerSelectionCard(String title, String resource, bool isSelected,StateSetter setState) {
    return Card(
      elevation: 2,
      margin: const EdgeInsets.all(Constant.LARGE_PADDING),
      child: InkWell(
        onTap: () {
          if (title.equalsIgnoreCase(Strings.prepaid)) {
            selectedCustomer = 1;
          }

          if (title.equalsIgnoreCase(Strings.postpaid)) {
            selectedCustomer = 2;
          }

          setState(() => selectedCustomer = selectedCustomer);
        },
        child: Container(
          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
          alignment: Alignment.center,
          decoration: BoxDecoration(
              border: Border.all(
                  width: 1,
                  color: isSelected ? AppTheme.colorPrimary : Colors.transparent),
              borderRadius: BorderRadius.circular(4),
              color: title.equalsIgnoreCase(Strings.prepaid)
                  ? AppTheme.colorGreenRoundView
                  : AppTheme.colorRedRoundView),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisSize: MainAxisSize.max,
            children: [
              const SizedBox(height: Constant.LARGE_PADDING),
              SvgPicture.asset(
                resource,
                height: Constant.BIG_ICON_SIZE,
                width: Constant.BIG_ICON_SIZE,
                color: AppTheme.title_dark,
                fit: BoxFit.fill,
              ),
              const SizedBox(height: Constant.LARGE_PADDING),
              CustomText(
                  title: title,
                  fontSize: AppTheme.medium + 1,
                  fontWeight: FontWeight.w500,
                  colors: AppTheme.title_dark,
                  maxLines: 2),
              const SizedBox(height: Constant.LARGE_PADDING),
            ],
          ),
        ),
      ),
    );
  }
}

abstract class AddFollowUpBtnAction {
  void followUpBtnAction(
      {String identifier, TicketDetail caseDetail, String remarks});
}
