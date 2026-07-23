import 'package:savbill/pages/pending_approvals/model/request/plan_approve_reject_req.dart';
import 'package:savbill/pages/pending_approvals/model/response/approval_pending_plan_res.dart';
import 'package:savbill/pages/pending_approvals/plan/pa_plan_controller.dart';
import 'package:savbill/pages/pending_approvals/plan/pa_plan_item.dart';
import 'package:savbill/pages/pending_approvals/plan/pa_plan_status_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalPlan extends StatefulWidget {
  @override
  _PendingApprovalPlanState createState() => _PendingApprovalPlanState();
}

class _PendingApprovalPlanState extends State<PendingApprovalPlan>
    implements PAPlanStatusBtnAction {
  final paPlanController = Get.put(PAPlanController());

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
      child: GetBuilder<PAPlanController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paPlanController.isLoading),
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
                    title: Strings.plan_detail,
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
                child: (paPlanController.planList != null &&
                        paPlanController.planList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: paPlanController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: paPlanController.planList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index == paPlanController.planList?.length) {
                                if (paPlanController.isShowLoadMore) {
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
                                    paPlanController.planList![index];
                                return PendingApprovalPlanItem(
                                  item: item,
                                  onTapApprove: () {
                                    showChangeStatusDialog(
                                        item, Strings.approve);
                                  },
                                  onTapReject: () {
                                    showChangeStatusDialog(
                                        item, Strings.reject);
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

  showChangeStatusDialog(ApprovalPendingPlan detail, String from) {
    String status = "";

    if (from.equalsIgnoreCase(Strings.approve)) {
      status = Strings.approved;
    } else if (from.equalsIgnoreCase(Strings.reject)) {
      status = Strings.rejected;
    }

    PlanApproveRejectReq request = PlanApproveRejectReq(
        nextStaffId: "",
        planId: detail.id,
        flag: status,
        remark: "",
        staffId: paPlanController.userDetail!.userId.toString());

    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return PAPlanStatusDialog(
            paPlanStatusBtnAction: this,
            planApproveRejectReq: request,
            from: from,
            id:1
          );
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.plans_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void paPlanStatusBtnAction(
      {String? identifier, PlanApproveRejectReq? planApproveRejectReq}) {
    Get.back();
    paPlanController.approveRejectPlan(planApproveRejectReq!);
  }
}
