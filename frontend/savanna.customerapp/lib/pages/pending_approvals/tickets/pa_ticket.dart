import 'package:savbill/pages/pending_approvals/model/response/approval_pending_ticket_res.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/pending_approvals/tickets/pa_ticket_controller.dart';
import 'package:savbill/pages/pending_approvals/tickets/pa_ticket_item.dart';
import 'package:savbill/pages/pending_approvals/tickets/ticket_assign_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class PendingApprovalTicket extends StatefulWidget {
  @override
  _PendingApprovalTicketState createState() => _PendingApprovalTicketState();
}

class _PendingApprovalTicketState extends State<PendingApprovalTicket>
    implements TicketAssignAction {
  final paTicketController = Get.put(PATicketController());

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
      child: GetBuilder<PATicketController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: paTicketController.isLoading),
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
                    title: Strings.ticket_detail,
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
                child: (paTicketController.ticketList != null &&
                        paTicketController.ticketList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: paTicketController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                paTicketController.ticketList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  paTicketController.ticketList?.length) {
                                if (paTicketController.isShowLoadMore) {
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
                                ApprovalPendingTicket item =
                                    paTicketController.ticketList![index];
                                return PendingApprovalTicketItem(
                                  item: item,
                                  onTapApprove: () {
                                    getTicketStaff(item, true);
                                  },
                                  onTapReject: () {
                                    getTicketStaff(item, false);
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

  getTicketStaff(ApprovalPendingTicket detail, bool isApproveRequest) {
    paTicketController.isLoading = true;
    paTicketController.update();
    PendingApprovalsProvider().getAssignTicketStaff(
      caseId: detail.caseId!,
      isApproveRequest: isApproveRequest,
      onSuccess: (ResponseModel responseModel) {
        paTicketController.isLoading = false;
        paTicketController.update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketAssignStaffRes responseData =
                  TicketAssignStaffRes.fromJson(map);
              if (isApproveRequest) {
                if ((responseData.status != null &&
                        responseData.status == 200) ||
                    (responseData.responseCode != null &&
                        responseData.responseCode == 200)) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    paTicketController.selectedTicket = detail;
                    paTicketController.update();
                    showAssignStaffDialog(responseData.dataList!);
                  } else {
                    Utils.showSnackbar(
                        Strings.ERROR,
                        "Staff data not available",
                        AppTheme.colorWhite,
                        AppTheme.colorRed);
                  }
                }
              } else {
                if ((responseData.status != null &&
                        responseData.status == 200) ||
                    (responseData.responseCode != null &&
                        (responseData.responseCode == 200 ||
                            responseData.responseCode == 0))) {
                  paTicketController.page = 1;
                  paTicketController.update();
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      "Ticket reject successfully.",
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                  paTicketController.getPATicketList();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        paTicketController.update();
      },
      onError: (ResponseModel error) {
        paTicketController.handleApiError(error);
      },
    );
  }

  showAssignStaffDialog(List<TicketAssignStaff> item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this, itemsOrgLst: item);
        });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.ticket_pending_approvals,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  // void ticketAssignBtnAction({TicketAssignStaff? selectedItem}) {
  void ticketAssignBtnAction({TicketAssignStaff? selectedItem,bool? isStaffSelected,String? approveRejectStatus}) {
    Get.back();
    if (selectedItem != null && paTicketController.selectedTicket != null) {
      paTicketController.assignTicket(selectedItem.id!);
    }
  }
}
