import 'package:savbill/pages/lead_approval/lead_followup/close_lead_workflow_follow_up_dialog.dart';
import 'package:savbill/pages/lead_approval/lead_followup/lead_workflow_reschedule_follow_up.dart';
import 'package:savbill/pages/lead_approval/lead_followup/pa_follow_up_lead_controller.dart';
import 'package:savbill/pages/lead_approval/lead_followup/pa_follow_up_lead_item.dart';
import 'package:savbill/pages/lead_approval/model/la_follow_up_lead_list_res.dart';
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

class PendingApprovalFollowUpLead extends StatefulWidget {
  @override
  _PendingApprovalFollowUpLeadState createState() => _PendingApprovalFollowUpLeadState();
}

class _PendingApprovalFollowUpLeadState extends State<PendingApprovalFollowUpLead>implements CloseFollowUpRemarkBtnAction  {
  final paFollowUpLeadController = Get.put(PAFollowUpLeadController());

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
      child: GetBuilder<PAFollowUpLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paFollowUpLeadController.isLoading),
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
                    title: Strings.lead_followup_list,
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
                child: (paFollowUpLeadController.followUpList != null &&
                    paFollowUpLeadController.followUpList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: paFollowUpLeadController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      paFollowUpLeadController.followUpList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            paFollowUpLeadController.followUpList?.length) {
                          if (paFollowUpLeadController.isShowLoadMore) {
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
                          FollowUpList? item = paFollowUpLeadController.followUpList![index];
                          return PendingApprovalFollowUpLeadItem(
                            item: item,
                            onTapRescheduleFollowUp: () {
                              openRescheduleRemarkFollowUp(item,item.leadMasterId,Strings.reschedule);
                            },
                            onTapCloseFollowUp: () {
                              showDialog(
                                  context: context,
                                  barrierDismissible: true,
                                  builder: (BuildContext context) {
                                    return CloseLeadWorkFlowRemarkFollowUpDialog(
                                      pageName: 'leadCloseFollowUp',
                                      closeFollowUpRemarkBtnAction: this,
                                      itemList: item,
                                    );
                                  });
                            },
                            onTapCallFollowUp: (){
                              Utils.showSnackbar(
                                  Strings.INFO,
                                  Strings.configCallMsg,
                                  AppTheme.colorWhite,
                                  AppTheme.colorGreen);
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


  // openRescheduleRemarkFollowUp(FollowUpList? followUpListData,String? scheduleType) async {
  //   var result = await Get.to(LeadReScheduleFollowUpScreen(),arguments: {
  //     Constant.FOLLOW_UP_DATA: followUpListData,
  //     Constant.SCHEDULE_TYPE:scheduleType,
  //     Constant.FOLLOW_UP_ID:followUpListData!.leadMasterId
  //   });
  //
  //   if (result != null && result == true) {
  //     paFollowUpLeadController.getPAFollowUpLeadList();
  //   }
  // }


  openRescheduleRemarkFollowUp(FollowUpList? followUpList,int? followUpId,String? scheduleType) async {
    var result = await Get.to(LeadWorkFlowReScheduleFollowUpScreen(),arguments: {
      Constant.FOLLOW_UP_ID: followUpId,
      Constant.FOLLOW_UP_DATA: followUpList,
      Constant.SCHEDULE_TYPE:scheduleType
    });

    if (result != null && result == true) {
      paFollowUpLeadController.getPAFollowUpLeadList();
    }
  }


  _appBar() {
    return DynamicAppBar(
        Strings.lead_followup_list,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void closeFollowUpRemarkBtnAction({String? identifier, TextEditingController? remarkController, int? followUpId}) {
    Get.back();
    if(identifier!.equalsIgnoreCase("leadCloseFollowUp")) {
      paFollowUpLeadController.closeRemarkFollowUp(
        followUpId: followUpId!,
        remark: remarkController!.text,
      );
    }
  }
}