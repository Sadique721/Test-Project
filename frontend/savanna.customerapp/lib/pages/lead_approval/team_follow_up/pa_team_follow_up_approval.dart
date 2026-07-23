import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead_controller.dart';
import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead_item.dart';
import 'package:savbill/pages/lead_approval/lead_followup/close_lead_workflow_follow_up_dialog.dart';
import 'package:savbill/pages/lead_approval/lead_followup/lead_workflow_reschedule_follow_up.dart';
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_approval/model/la_follow_up_lead_list_res.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval_lead_controller.dart';
import 'package:savbill/pages/lead_approval/team_approval/pa_team_approval_lead_item.dart';
import 'package:savbill/pages/lead_approval/team_follow_up/pa_team_follow_up_approval_lead_controller.dart';
import 'package:savbill/pages/lead_approval/team_follow_up/pa_team_follow_up_approval_lead_item.dart';
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

class PendingApprovalTeamFollowUpApprovalLead extends StatefulWidget {
  @override
  _PendingApprovalAssignLeadState createState() =>
      _PendingApprovalAssignLeadState();
}

class _PendingApprovalAssignLeadState
    extends State<PendingApprovalTeamFollowUpApprovalLead>
    implements CloseFollowUpRemarkBtnAction {
  final paTeamFollowUpApprovalLeadController =
      Get.put(PATeamFollowUpApprovalLeadController());

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
      child: GetBuilder<PATeamFollowUpApprovalLeadController>(
          builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paTeamFollowUpApprovalLeadController.isLoading),
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
                child: (paTeamFollowUpApprovalLeadController.followUpList !=
                            null &&
                        paTeamFollowUpApprovalLeadController
                            .followUpList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                paTeamFollowUpApprovalLeadController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: paTeamFollowUpApprovalLeadController
                                    .followUpList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paTeamFollowUpApprovalLeadController
                                      .followUpList?.length) {
                                if (paTeamFollowUpApprovalLeadController
                                    .isShowLoadMore) {
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
                                FollowUpList item =
                                    paTeamFollowUpApprovalLeadController
                                        .followUpList![index];
                                return PendingApprovalTeamFollowUpApprovalLeadItem(
                                    item: item,
                                    controller:
                                        paTeamFollowUpApprovalLeadController,
                                    onTapRescheduleFollowUpTeam: () {
                                      openRescheduleRemarkFollowUp(
                                          item,
                                          item.leadMasterId,
                                          Strings.reschedule);
                                    },
                                    onTapCloseFollowUpTeam: () {
                                      showDialog(
                                          context: context,
                                          barrierDismissible: true,
                                          builder: (BuildContext context) {
                                            return CloseLeadWorkFlowRemarkFollowUpDialog(
                                              pageName: 'leadCloseFollowUp',
                                              closeFollowUpRemarkBtnAction:
                                                  this,
                                              itemList: item,
                                            );
                                          });
                                    },
                                    onTapCallFollowUpTeam: () {
                                      Utils.showSnackbar(
                                          Strings.INFO,
                                          Strings.configCallMsg,
                                          AppTheme.colorWhite,
                                          AppTheme.colorGreen);
                                    });
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  openRescheduleRemarkFollowUp(
      FollowUpList? followUpList, int? followUpId, String? scheduleType) async {
    var result =
        await Get.to(LeadWorkFlowReScheduleFollowUpScreen(), arguments: {
      Constant.FOLLOW_UP_ID: followUpId,
      Constant.FOLLOW_UP_DATA: followUpList,
      Constant.SCHEDULE_TYPE: scheduleType
    });

    if (result != null && result == true) {
      paTeamFollowUpApprovalLeadController.getPATeamApprovalLeadList();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.team_lead_followup_list,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void closeFollowUpRemarkBtnAction(
      {String? identifier,
      TextEditingController? remarkController,
      int? followUpId}) {
    Get.back();
    if (identifier!.equalsIgnoreCase("leadCloseFollowUp")) {
      paTeamFollowUpApprovalLeadController.closeRemarkFollowUp(
        followUpId: followUpId!,
        remark: remarkController!.text,
      );
    }
  }
}
