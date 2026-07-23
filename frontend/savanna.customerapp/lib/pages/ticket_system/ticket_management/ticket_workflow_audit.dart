import 'package:savbill/pages/dashboard/model/response/workflow_audit_res.dart';
import 'package:savbill/pages/dashboard/workflow_audit_item.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_detail_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_workflow_audit_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TicketWorkflowAudit extends StatefulWidget {
  @override
  _TicketWorkflowAuditState createState() => _TicketWorkflowAuditState();
}

class _TicketWorkflowAuditState extends State<TicketWorkflowAudit> {
  final ticketWorkflowAuditController = Get.put(TicketWorkflowAuditController());

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
      child: GetBuilder<TicketWorkflowAuditController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: ticketWorkflowAuditController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              /*Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: CustomText(
                    title: Strings.ticket,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),*/
              Expanded(
                flex: 1,
                child:
                    (ticketWorkflowAuditController.workflowAuditList != null &&
                            ticketWorkflowAuditController
                                .workflowAuditList!.isNotEmpty)
                        ? Padding(
                            padding: const EdgeInsets.symmetric(
                                horizontal: Constant.SCREEN_PADDING),
                            child: ListView.builder(
                                controller:
                                    ticketWorkflowAuditController.controller,
                                scrollDirection: Axis.vertical,
                                itemCount: ticketWorkflowAuditController
                                        .workflowAuditList!.length +
                                    1,
                                itemBuilder: (context, index) {
                                  if (index ==
                                      ticketWorkflowAuditController
                                          .workflowAuditList?.length) {
                                    if (ticketWorkflowAuditController
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
                                    WorkflowAuditDetail item =
                                        ticketWorkflowAuditController
                                            .workflowAuditList![index];
                                    return WorkflowAuditItem(
                                      item: item,
                                      onTapStaffDetail: () {
                                        ticketWorkflowAuditController.getAllTeamNameByStaffId(item.actionByStaffId);
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
        Strings.ticket_workflow_audit,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
