import 'package:savbill/pages/pending_approvals/invoice/pa_invoice_controller.dart';
import 'package:savbill/pages/pending_approvals/invoice/pa_invoice_item.dart';
import 'package:savbill/pages/pending_approvals/model/response/ap_invoice_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_plan_res.dart';
import 'package:savbill/pages/pending_approvals/special_plan/pa_special_plan_controller.dart';
import 'package:savbill/pages/pending_approvals/special_plan/pa_special_plan_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SpecialPlanMapping extends StatefulWidget {
  @override
  _SpecialPlanMappingState createState() => _SpecialPlanMappingState();
}

class _SpecialPlanMappingState extends State<SpecialPlanMapping> {
  final paSpecialPlanController = Get.put(PASpecialPlanController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<PASpecialPlanController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paSpecialPlanController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.special_plan_approval,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (paSpecialPlanController.invoiceList != null &&
                    paSpecialPlanController.invoiceList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: paSpecialPlanController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                            paSpecialPlanController.invoiceList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paSpecialPlanController.invoiceList?.length) {
                                if (paSpecialPlanController.isShowLoadMore) {
                                  return Padding(
                                    padding: const EdgeInsets.all(
                                        Constant.SMALL_PADDING),
                                    child: Center(
                                      child: SizedBox(
                                        width: Constant.SCREEN_PADDING,
                                        height: Constant.SCREEN_PADDING,
                                        child: CircularProgressIndicator(
                                          strokeWidth: 2.5,
                                          valueColor:
                                              AlwaysStoppedAnimation<Color>(
                                                  AppTheme.colorProgress),
                                          backgroundColor:
                                              AppTheme.colorProgressBg,
                                        ),
                                      ),
                                    ),
                                  );
                                } else {
                                  return Container();
                                }
                              } else {
                                ApprovalPendingPlan item =
                                paSpecialPlanController.invoiceList![index];
                                return PendingApprovalPASpecialPlanItem(
                                  item: item,
                                  onTapApprove: () {
                                    /*showChangeStatusDialog(
                                        item, Strings.approve);*/
                                  },
                                  onTapReject: () {
                                    /* showChangeStatusDialog(
                                        item, Strings.reject);*/
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.special_plan_approval,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
