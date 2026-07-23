import 'package:savbill/pages/lead_approval/assigne_lead/lead_workflow_approve_reject_dialog.dart';
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval_lead_controller.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval_lead_item.dart';
import 'package:savbill/pages/lead_approval/team_approval/team_workflow_approve_reject_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalTeamApprovalLead extends StatefulWidget {
  @override
  _PendingApprovalAssignLeadState createState() => _PendingApprovalAssignLeadState();
}

class _PendingApprovalAssignLeadState extends State<PendingApprovalTeamApprovalLead> implements TeamWorkFlowApproveRejectBtnAction {
  final paTeamApprovalLeadController = Get.put(PATeamApprovalLeadController());

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
      child: GetBuilder<PATeamApprovalLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paTeamApprovalLeadController.isLoading),
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
                    title: Strings.team_lead_approval_list,
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
                child: (paTeamApprovalLeadController.assignList != null &&
                    paTeamApprovalLeadController.assignList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: paTeamApprovalLeadController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      paTeamApprovalLeadController.assignList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            paTeamApprovalLeadController.assignList?.length) {
                          if (paTeamApprovalLeadController.isShowLoadMore) {
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
                          LAAssignContent item =
                          paTeamApprovalLeadController.assignList![index];
                          return PendingApprovalTeamApprovalLeadItem(
                            item: item,
                            controller: paTeamApprovalLeadController,
                            onTapApprove: () {
                              addRemarkLeadDialog(context,Strings.approve,item,paTeamApprovalLeadController);
                            },
                            onTapReject: () {
                              addRemarkLeadDialog(context,Strings.reject,item,paTeamApprovalLeadController);
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


  addRemarkLeadDialog(BuildContext context, String? pageName,
      LAAssignContent? item, PATeamApprovalLeadController? controller) {
    if (item!.finalApproved == true) {
      if (pageName!.equalsIgnoreCase("Reject")) {
        Utils.showSnackbar(
            Strings.SUCCESS, "Assigned to the next staff",
            AppTheme.colorWhite, AppTheme.colorGreen);
      } else {
        Utils.showSnackbar(
            Strings.INFO,
            "Lead has been already prepared for 'Convert To CAF' operation. Go to 'Lead Management' screen for this.",
            AppTheme.colorWhite, AppTheme.colorBlueRView);
      }
    } else {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return TeamWorkFlowApproveRejectDialog(
                pageName: pageName,
                teamWorkFlowApproveRejectBtnAction: this,
                item: item,
                controller: controller);
          });
    }
  }


  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.team_lead_approval_list,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void teamWorkFlowApproveRejectStatus({String? identifier, TextEditingController? remarkController, int? caseId, BuildContext? context, LAAssignContent? item}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      paTeamApprovalLeadController.approveRejectStaffLead(
          status: Strings.approve,
          remark: remarkController!.text,
          context: context!,
          item: item);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      paTeamApprovalLeadController.approveRejectStaffLead(
          status: Strings.reject,
          remark: remarkController!.text,
          context: context!,
          item: item);
    }
  }








}