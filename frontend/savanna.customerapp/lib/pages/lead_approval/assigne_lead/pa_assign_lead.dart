import 'package:savbill/pages/lead_approval/assigne_lead/lead_workflow_approve_reject_dialog.dart';
import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead_controller.dart';
import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead_item.dart';
import 'package:savbill/pages/lead_approval/model/la_assign_list_res.dart';
import 'package:savbill/pages/lead_management/lead_details/lead_details.dart';
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

class PendingApprovalAssignLead extends StatefulWidget {
  @override
  _PendingApprovalAssignLeadState createState() => _PendingApprovalAssignLeadState();
}

class _PendingApprovalAssignLeadState extends State<PendingApprovalAssignLead> implements LeadWorkFlowApproveRejectBtnAction {
  final paAssignLeadController = Get.put(PAAssignLeadController());

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
      child: GetBuilder<PAAssignLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paAssignLeadController.isLoading),
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
                    title: Strings.assigned_lead_list,
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
                child: (paAssignLeadController.assignList != null &&
                    paAssignLeadController.assignList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: paAssignLeadController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      paAssignLeadController.assignList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            paAssignLeadController.assignList?.length) {
                          if (paAssignLeadController.isShowLoadMore) {
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
                          paAssignLeadController.assignList![index];
                          return PendingApprovalAssignLeadItem(
                            item: item,
                            onTapCustName: (){
                              openLeadDetailsScreen(item.id);
                            },
                            onTapApprove: () {
                              addRemarkLeadDialog(context,Strings.approve,item,paAssignLeadController);
                            },
                            onTapReject: () {
                              addRemarkLeadDialog(context,Strings.reject,item,paAssignLeadController);
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


  openLeadDetailsScreen(int? leadMasterId) async {
    var result = await Get.to(LeadDetailScreen(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
      Constant.LEAD_DASHBOARD_FLAG: true,
    });
    if (result != null && result == true) {
      // Get.back();
    }
  }

  addRemarkLeadDialog(BuildContext context, String? pageName,
      LAAssignContent? item, PAAssignLeadController? controller) {
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
            return LeadWorkFlowApproveRejectDialog(
                pageName: pageName,
                leadWorkFlowApproveRejectBtnAction: this,
                item: item,
                controller: controller);
          });
    }
  }

  _appBar() {
    return DynamicAppBar(
        Strings.assigned_lead_list,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void leadWorkFlowApproveRejectStatus({String? identifier, TextEditingController? remarkController, int? caseId, BuildContext? context, LAAssignContent?  item}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      paAssignLeadController.approveRejectStaffLead(
          status: Strings.approve,
          remark: remarkController!.text,
          context: context!,
          item: item);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      paAssignLeadController.approveRejectStaffLead(
          status: Strings.reject,
          remark: remarkController!.text,
          context: context!,
          item: item);
    }
  }

}