import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/dashboard/add_followup_dialog.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/ticket_customer_detail.dart';
import 'package:savbill/pages/dashboard/ticket_detail.dart';
import 'package:savbill/pages/dashboard/ticket_detail_controller.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/pending_approvals/tickets/ticket_assign_dialog.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/change_ticket_priority_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_change_status.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_etr/ticket_etr_screen.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_pick_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_remark/ticket_remark.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_sla_time_counter_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_status_approve_reject_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket_controller.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewTicket extends StatefulWidget {
  @override
  _ViewTicketState createState() => _ViewTicketState();
}

class _ViewTicketState extends State<ViewTicket>
    with TickerProviderStateMixin
    implements
        AddFollowUpBtnAction,
        TicketAssignAction,
        TicketPickBtnAction,
        TicketPriorityBtnAction,
        TicketApproveRejectBtnAction {
  final viewTicketController = Get.put(ViewTicketController());
  TabController? _tabController;
  // TabController? _subTabController;

  List<Tab> myTabs = <Tab>[
    Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.all_ticket,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
    Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.assigned_to_me_ticket,
          textAlign: TextAlign.center,
          style: TextStyle(
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w500,
          ),
        ),
      ),
    ),
  ];

  @override
  void initState() {
    super.initState();
    /* customerPlanController.isCallAllApi = true;
    customerPlanController.update();
    customerPlanController.getArgumentData();*/

    _tabController =
        TabController(vsync: this, length: myTabs.length, initialIndex: 0)
          ..addListener(() {
            viewTicketController.tabIndex = _tabController!.index;
            viewTicketController.update();
          });
  }

  @override
  void dispose() {
    super.dispose();
    _tabController!.dispose();
  }


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
      child: GetBuilder<ViewTicketController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
            bottomNavigationBar:  Row(
              children: [
                Expanded(
                    child: SimpleButton(
                      onTap: () {
                        viewTicketController.addEditTicketScreen(Strings.add, null);
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.create_ticket,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ))
              ],
            ),
          ),
          ProgressBar(isLoader: viewTicketController.isLoading),

        ]);
      }),
    );
  }

  _body() {
    return Stack(children: <Widget>[
      Container(
        color: AppTheme.colorBG,
        // width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Container(
                margin: const EdgeInsets.symmetric(
                  horizontal: Constant.SMALL_PADDING,
                ),
                height: Constant.TABBAR_HEIGHT,
                child: TabBar(
                  controller: _tabController,
                  unselectedLabelColor:
                  AppTheme.title_dark.withOpacity(0.6),
                  labelColor: AppTheme.colorWhite,
                  labelStyle: const TextStyle(
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w600),
                  unselectedLabelStyle: TextStyle(
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500),
                  tabs: myTabs,
                  dividerColor: Colors.transparent,
                  indicator: BoxDecoration(
                      borderRadius: BorderRadius.circular(7), // Creates border
                      color: AppTheme.colorPrimary),
                  // indicatorColor: AppTheme.colorPrimary,
                ),
              ),
              Flexible(
                child: TabBarView(
                  controller: _tabController,
                  children: [
                    _allITicketList(),
                    _assignToMeTicket(),
                  ], //_tabsContainer(),
                ),
              ),
            ]),
      )
      // viewTicketController.isLoading
      //     ? ProgressBar(isLoader: viewTicketController.isLoading)
      //     : Container(
      //         color: AppTheme.colorBG,
      //         width: MediaQuery.of(context).size.width,
      //         child: Column(
      //             crossAxisAlignment: CrossAxisAlignment.start,
      //             mainAxisAlignment: MainAxisAlignment.start,
      //             children: [
      //               const SizedBox(
      //                 height: Constant.MEDIUM_PADDING,
      //               ),
      //               Container(
      //                 margin: const EdgeInsets.symmetric(
      //                   horizontal: Constant.SCREEN_PADDING,
      //                 ),
      //                 height: Constant.TABBAR_HEIGHT,
      //                 decoration: BoxDecoration(
      //                   color: AppTheme.colorTransparent,
      //                   border: Border(
      //                       bottom: BorderSide(
      //                           color: AppTheme.title_dark.withOpacity(0.9),
      //                           width: Constant.TABBAR_BOTTOM_LINE_H)),
      //                 ),
      //                 child: TabBar(
      //                   controller: _tabController,
      //                   unselectedLabelColor:
      //                       AppTheme.title_dark.withOpacity(0.6),
      //                   indicator: UnderlineTabIndicator(
      //                     borderSide: BorderSide(
      //                         width: Constant.TAB_INDICATOR_H,
      //                         color: AppTheme.title_dark),
      //                   ),
      //                   labelColor: AppTheme.title_dark,
      //                   labelStyle: const TextStyle(
      //                       fontSize: AppTheme.medium,
      //                       fontWeight: FontWeight.w600),
      //                   unselectedLabelStyle: const TextStyle(
      //                       fontSize: AppTheme.small,
      //                       fontWeight: FontWeight.w500),
      //                   tabs: myTabs,
      //                 ),
      //               ),
      //               Flexible(
      //                 child: TabBarView(
      //                   controller: _tabController,
      //                   children: [
      //                     _allITicketList(),
      //                     _assignToMeTicket(),
      //                     // _assignedInventoriesList(
      //                     //     Strings.inventory_assigned_customer),
      //                     // _assignedCustomerList(),
      //                   ], //_tabsContainer(),
      //                 ),
      //               ),
      //             ]),
      //       ),

    ]);
  }

  // openCaseAssignScreen(TicketDetail ticketDetail) async {
  //   bool chkRefresh = await Get.to(CaseAssign(), arguments: {
  //     Constant.TICKET_DETAIL: ticketDetail,
  //   });
  //
  //   if (chkRefresh) {
  //     viewTicketController.clearFilter();
  //   }
  // }

  openTicketDetailScreen(int? ticketId) async {
    Get.to(()=> TicketDetailScreen(), arguments: {
      Constant.TICKET_ID: ticketId,
      Constant.TICKET_ID: ticketId,
    });
  }

  ticketCustomerDetail(int? customerId) async {
    Get.to(TicketCustomerDetail(), arguments: {
      Constant.CUSTOMER_DETAIL: customerId,
    });
  }

  showFollowUpPopup(TicketDetail ticketDetail) {
    String title = "Ticket Comment (${ticketDetail.caseNumber})";
    viewTicketController.remarksController.clear();
    viewTicketController.update();
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return AddFollowUpDialog(
              addFollowUpBtnAction: this,
              caseDetail: ticketDetail,
              title: title);
        });
  }

  showTicketPickDialog(TicketDetail ticketDetail) {
    bool showPopup = false;
    if (ticketDetail.ticketAssignStaffMappings != null &&
        ticketDetail.ticketAssignStaffMappings!.isNotEmpty) {
      ticketDetail.ticketAssignStaffMappings!.forEach((element) {
        if (element.staffId == viewTicketController.userDetail!.userId) {
          showPopup = true;
        }
      });
    }
    if (!showPopup) {
      Utils.showSnackbar(
          Strings.INFO,
          "You are not eligible to pick this ticket..",
          AppTheme.colorBlack,
          AppTheme.colorBlueRView);
      return;
    }

    viewTicketController.selectedTicket = ticketDetail;
    viewTicketController.update();
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return TicketPickDialog(
            ticketPickBtnAction: this,
            from: Strings.pick_ticket,
          );
        });
  }

  showTicketPriorityDialog(TicketDetail? ticketDetail) {
    if (viewTicketController.ticketPriorityList != null &&
        viewTicketController.ticketPriorityList!.isNotEmpty) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return ChangeTicketPriorityDialog(
                ticketPriorityBtnAction: this,
                ticketDetail: ticketDetail,
                priorityList: viewTicketController.ticketPriorityList!);
          });
    }
  }

  showSLATimeCounterDialog(TicketDetail ticketDetail) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return TicketSLATimeCounterDialog(ticketDetail: ticketDetail);
        });
  }

  // openLinkTicketScreen(TicketDetail ticketDetail) async {
  //   var result = Get.to(LinkTicket(), arguments: {
  //     Constant.TICKET_DETAIL: ticketDetail,
  //   });
  //   if (result != null && result == true) {
  //     viewTicketController.clearFilter();
  //   }
  // }

  // openTicketDocumentUploadScreen(TicketDetail ticketDetail) async {
  //   Get.to(UploadDocumentScreen(), arguments: {
  //     Constant.TICKET_ID: ticketDetail.caseId,
  //   });
  // }

  openTicketChangeStatusScreen(TicketDetail ticketDetail) async {
    var result = await Get.to(TicketChangeStatusScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });

    if (result != null && result == true) {
      viewTicketController.clearFilter();
    }
  }

  onTicketETRScreen(TicketDetail ticketDetail) async {
    var result = Get.to(TicketETRScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      viewTicketController.getAllProblemDomain();
    }
  }

  onTicketRemarkScreen(TicketDetail ticketDetail) async {
    var result = Get.to(TicketRemarkScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      viewTicketController.getAllProblemDomain();
    }
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.ticket_master_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void followUpBtnAction(
      {String? identifier, TicketDetail? caseDetail, String? remarks}) {
    Get.back();
    viewTicketController.caseFollowUpApiCall(caseDetail, remarks!);
  }

  getTicketStaff(TicketDetail ticketDetail, bool isApproveRequest) {
    viewTicketController.isLoading = true;
    viewTicketController.update();
    PendingApprovalsProvider().getAssignTicketStaff(
      caseId: ticketDetail.caseId!,
      isApproveRequest: isApproveRequest,
      onSuccess: (ResponseModel responseModel) {
        viewTicketController.isLoading = false;
        viewTicketController.update();
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
                    viewTicketController.selectedTicket = ticketDetail;
                    viewTicketController.update();
                    showAssignStaffDialog(responseData.dataList!);
                  } else {
                    Utils.showSnackbar(Strings.INFO, "Staff data not available",
                        AppTheme.colorWhite, AppTheme.colorBlueRView);
                  }
                }
              } else {
                if ((responseData.status != null &&
                        responseData.status == 200) ||
                    (responseData.responseCode != null &&
                        (responseData.responseCode == 200 ||
                            responseData.responseCode == 0))) {
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      "Ticket reject successfully.",
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                  viewTicketController.clearFilter();
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
        viewTicketController.update();
      },
      onError: (ResponseModel error) {
        viewTicketController.handleApiError(error);
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

  @override
  // void ticketAssignBtnAction({TicketAssignStaff? selectedItem}) {
  void ticketAssignBtnAction(
      {TicketAssignStaff? selectedItem,
      bool? isStaffSelected,
      String? approveRejectStatus}) {
    Get.back();
    if (selectedItem != null && viewTicketController.selectedTicket != null) {
      viewTicketController.assignTicket(selectedItem.id!);
    }
  }

  @override
  void ticketPickBtnAction({String? identifier, String? remark}) {
    Get.back();
    if (remark != null &&
        remark.isNotEmpty &&
        viewTicketController.selectedTicket != null) {
      viewTicketController.pickTicket(remark.trim());
    }
  }

  @override
  void ticketPriorityBtnAction(
      {TicketPriority? priority, TicketDetail? ticketDetail}) {
    Get.back();

    if (priority != null && ticketDetail != null) {
      // call update api call
      viewTicketController.changePriorityTicket(priority, ticketDetail);
    }
  }

  addRemarkTicketDialog(
      BuildContext context, String? pageName, TicketDetail item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketApproveRejectDialog(
            pageName: pageName,
            ticketApproveRejectBtnAction: this,
            caseId: item.caseId,
          );
        });
  }

  @override
  void ticketApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      int? caseId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      viewTicketController.approveRejectTicket(
          status: Strings.approve.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      viewTicketController.approveRejectTicket(
          status: Strings.reject.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    }
  }

  _allITicketList() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                CustomText(
                    title: Strings.all_ticket,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
                Row(children: [
                  InkWell(
                    onTap: () {
                      if (viewTicketController.filterViewOpen) {
                        viewTicketController.filterViewOpen = false;
                      } else {
                        viewTicketController.filterViewOpen = true;
                      }
                      viewTicketController.update();
                    },
                    child: Container(
                        height: 38,
                        margin: const EdgeInsets.only(right: 0), //12
                        child: Icon(
                          Icons.filter_alt_rounded,
                          color: viewTicketController.isFilterApply
                              ? AppTheme.colorPrimary
                              : AppTheme.colorBlack,
                          size: 32,
                        )),
                  ),
                ])
              ],
            ),
          ),
          const SizedBox(height: Constant.SCREEN_PADDING),
      (viewTicketController.filterViewOpen)
          ? Container(
        width: MediaQuery.of(context).size.width,
        margin: const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
        child: Column(
          children: [
            Material(
              color: AppTheme.colorWhite,
              elevation: 1.5,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER - 2),
              ),
              child: Padding(
                padding: const EdgeInsets.all( Constant.SMALL_PADDING),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: Constant.SMALL_PADDING),
                    DropdownButtonHideUnderline(
                      child: DropdownButtonFormField(
                        icon: SvgPicture.asset(
                          downArrowSvg,
                          height: Constant.DROP_DOWN_ARROW_W_H,
                          width: Constant.DROP_DOWN_ARROW_W_H,
                          color: AppTheme.colorBlack,
                          fit: BoxFit.fill,
                        ),
                        decoration: Utils.ddlDecoration(),
                        hint: Align(
                          alignment: Alignment.centerLeft,
                          child: Text(
                            Strings.select_search_option,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ),
                          ),
                        ),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: viewTicketController.selectSearchOption,
                        items: viewTicketController.ticketSearchOptionList!
                            .map((PlanTypeDetail value) {
                          return DropdownMenuItem<PlanTypeDetail>(
                            value: value,
                            child: Text(value.text!),
                          );
                        }).toList(),
                        onChanged: (value) {
                          viewTicketController.selectSearchOption = value as PlanTypeDetail?;
                          viewTicketController.update();
                        },
                        validator: (value) {
                          return null;
                        },
                      ),
                    ),
                    const SizedBox(height: Constant.SCREEN_PADDING),
                    CoustomTextField(
                      labelText: Strings.enter_search_detail,
                      hintColor: AppTheme.colorIconGrey,
                      textEditingController: viewTicketController.searchDetailController,
                      borderEnableColors: AppTheme.colorIconGrey,
                      borderFocusColors: AppTheme.colorIconGrey,
                      textColor: AppTheme.colorBlack,
                      keyboardType: TextInputType.emailAddress,
                      fontSize: AppTheme.small,
                      textInputAction: TextInputAction.next,
                      fontWeight: FontWeight.w500,
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                        vertical: Constant.MEDIUM_PADDING,
                      ),
                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                      onTextValidator: (String? value) {
                        if (value!.isEmpty) {
                          return Strings.please_enter_value;
                        }
                        return null;
                      },
                      onTextFiledOnTap: () {},
                      readOnly: false,
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              viewTicketController.clearFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorBlack,
                            borderColors: AppTheme.colorBlack,
                            child: CustomText(
                              title: Strings.clear,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.LARGE_PADDING,
                        ),
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              viewTicketController.applyFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorPrimary,
                            child: CustomText(
                              title: Strings.apply,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        )


                      ],
                    ),
                  ],
                ),
              ),
            ),
            const SizedBox(height: Constant.SCREEN_PADDING),
          ],
        ),
      )
          : SizedBox.shrink(),
          // const SizedBox(height: Constant.SCREEN_PADDING),

          Expanded(
            flex: 1,
            child: (viewTicketController.ticketList != null &&
                    viewTicketController.ticketList!.isNotEmpty)
                ? Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING),
                    child: ListView.builder(
                        controller: viewTicketController.controller,
                        scrollDirection: Axis.vertical,
                        itemCount: viewTicketController.ticketList!.length + 1,
                        itemBuilder: (context, index) {
                          if (index == viewTicketController.ticketList?.length) {
                            if (viewTicketController.isShowLoadMore) {
                              return Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Center(
                                  child: SizedBox(
                                    width: Constant.SCREEN_PADDING,
                                    height: Constant.SCREEN_PADDING,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2.5,
                                      valueColor: AlwaysStoppedAnimation<Color>(
                                          AppTheme.colorProgress),
                                      backgroundColor: AppTheme.colorProgressBg,
                                    ),
                                  ),
                                ),
                              );
                            } else {
                              return Container();
                            }
                          } else {
                            TicketDetail item =
                                viewTicketController.ticketList![index];
                            viewTicketController.assignStaffParentId =
                                item.currentAssigneeId;
                            return InkWell(
                              // onTap: () {
                              //   openTicketDetailScreen(item.caseId);
                              // },
                              child: ViewTicketItem(
                                item: item,
                                showActionBtn: true,
                                forSelection: false,
                                controller: viewTicketController,
                                userid:
                                    viewTicketController.userDetail!.userId!,
                                onTapTicketDetail: () {
                                  openTicketDetailScreen(item.caseId);
                                },
                                onTapCustomerDetail: () {
                                  ticketCustomerDetail(item.customersId);
                                },
                                onTapStaffDetail: () {
                                  if (item.currentAssigneeId != null) {
                                    viewTicketController.getTicketStaffDetail(
                                        item.currentAssigneeId!);
                                  }
                                },
                                onTapEdit: () {
                                  viewTicketController.addEditTicketScreen(
                                      Strings.edit, item);
                                },
                                onTapPick: () {
                                  showTicketPickDialog(item);
                                },
                                onTapSLATimeCounter: () {
                                  showSLATimeCounterDialog(item);
                                },
                              ),
                            );
                          }
                        }),
                  )
                : noDataFound(),
          ),
        ],
      ),
    );
  }

  _assignToMeTicket() {
    return Container(
      width: MediaQuery.of(context).size.width,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const SizedBox(
            height: Constant.MEDIUM_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                CustomText(
                    title: Strings.assigned_to_me_ticket,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
                // Row(children: [
                //   InkWell(
                //     onTap: () {
                //       if (viewTicketController.filterViewOpen) {
                //         viewTicketController.filterViewOpen = false;
                //       } else {
                //         viewTicketController.filterViewOpen = true;
                //       }
                //       viewTicketController.update();
                //     },
                //     child: Container(
                //         height: 38,
                //         margin: const EdgeInsets.only(right: 0), //12
                //         child: Icon(
                //           Icons.filter_alt_rounded,
                //           color: viewTicketController.isFilterApply
                //               ? AppTheme.colorPrimary
                //               : AppTheme.colorBlack,
                //           size: 32,
                //         )),
                //   ),
                // ])
              ],
            ),
          ),
          const SizedBox(height: Constant.SCREEN_PADDING),
          (viewTicketController.filterViewOpen)
              ? Container(
            width: MediaQuery.of(context).size.width,
            margin: const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
            child:Column(children: [ Material(
              color: AppTheme.colorWhite,
              elevation: 1.5,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER - 2),
              ),
              child: Padding(
                padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: Constant.SMALL_PADDING),
                    DropdownButtonHideUnderline(
                      child: DropdownButtonFormField(
                        icon: SvgPicture.asset(
                          downArrowSvg,
                          height: Constant.DROP_DOWN_ARROW_W_H,
                          width: Constant.DROP_DOWN_ARROW_W_H,
                          color: AppTheme.colorBlack,
                          fit: BoxFit.fill,
                        ),
                        decoration: Utils.ddlDecoration(),
                        hint: Align(
                          alignment: Alignment.centerLeft,
                          child: Text(
                            Strings.select_search_option,
                            style: TextStyle(
                              fontSize: AppTheme.medium,
                              color: AppTheme.colorIconGrey,
                              fontFamily: AppTheme.appFontName,
                            ),
                          ),
                        ),
                        style: AppTheme.dropdownTextStyle,
                        isExpanded: true,
                        isDense: true,
                        value: viewTicketController.selectSearchOption,
                        items: viewTicketController.ticketSearchOptionList!
                            .map((PlanTypeDetail value) {
                          return DropdownMenuItem<PlanTypeDetail>(
                            value: value,
                            child: Text(value.text!),
                          );
                        }).toList(),
                        onChanged: (value) {
                          viewTicketController.selectSearchOption = value as PlanTypeDetail?;
                          viewTicketController.update();
                        },
                        validator: (value) {
                          return null;
                        },
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    CoustomTextField(
                      labelText: Strings.enter_search_detail,
                      hintColor: AppTheme.colorIconGrey,
                      textEditingController: viewTicketController.searchDetailController,
                      borderEnableColors: AppTheme.colorIconGrey,
                      borderFocusColors: AppTheme.colorIconGrey,
                      textColor: AppTheme.colorBlack,
                      keyboardType: TextInputType.emailAddress,
                      fontSize: AppTheme.small,
                      textInputAction: TextInputAction.next,
                      fontWeight: FontWeight.w500,
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                        vertical: Constant.MEDIUM_PADDING,
                      ),
                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                      onTextValidator: (String? value) {
                        if (value!.isEmpty) {
                          return Strings.please_enter_value;
                        }
                        return null;
                      },
                      onTextFiledOnTap: () {},
                      readOnly: false,
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              // viewTicketController.clearFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorBlack,
                            borderColors: AppTheme.colorBlack,
                            child: CustomText(
                              title: Strings.clear,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.LARGE_PADDING,
                        ),
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              // viewTicketController.applyFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorPrimary,
                            child: CustomText(
                              title: Strings.apply,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        )


                      ],
                    ),
                  ],
                ),
              ),
            ), const SizedBox(height: Constant.SCREEN_PADDING)],)
          )
              : SizedBox.shrink(),
          Expanded(
            flex: 1,
            child: (viewTicketController.ticketAssignMeList != null &&
                    viewTicketController.ticketAssignMeList!.isNotEmpty)
                ? Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING),
                    child: ListView.builder(
                        controller: viewTicketController.controller,
                        scrollDirection: Axis.vertical,
                        itemCount:
                            viewTicketController.ticketAssignMeList!.length + 1,
                        itemBuilder: (context, index) {
                          if (index ==
                              viewTicketController.ticketAssignMeList?.length) {
                            if (viewTicketController.isShowLoadMore) {
                              return Padding(
                                padding: const EdgeInsets.all(
                                    Constant.SMALL_PADDING),
                                child: Center(
                                  child: SizedBox(
                                    width: Constant.SCREEN_PADDING,
                                    height: Constant.SCREEN_PADDING,
                                    child: CircularProgressIndicator(
                                      strokeWidth: 2.5,
                                      valueColor: AlwaysStoppedAnimation<Color>(
                                          AppTheme.colorProgress),
                                      backgroundColor: AppTheme.colorProgressBg,
                                    ),
                                  ),
                                ),
                              );
                            } else {
                              return Container();
                            }
                          } else {
                            TicketDetail item =
                                viewTicketController.ticketAssignMeList![index];
                            viewTicketController.assignStaffParentId =
                                item.currentAssigneeId;
                            return InkWell(
                              // onTap: () {
                              //   openTicketDetailScreen(item.caseId);
                              // },
                              child: ViewTicketItem(
                                item: item,
                                showActionBtn: true,
                                forSelection: false,
                                controller: viewTicketController,
                                userid:
                                    viewTicketController.userDetail!.userId!,
                                onTapTicketDetail: () {
                                  openTicketDetailScreen(item.caseId);
                                },
                                onTapCustomerDetail: () {
                                  ticketCustomerDetail(item.customersId);
                                },
                                onTapStaffDetail: () {
                                  if (item.currentAssigneeId != null) {
                                    viewTicketController.getTicketStaffDetail(
                                        item.currentAssigneeId!);
                                  }
                                },
                                onTapEdit: () {
                                  viewTicketController.addEditTicketScreen(
                                      Strings.edit, item);
                                },
                                // onTapAssignTicket: () {
                                //   viewTicketController.getStaffListData(
                                //       item.caseId, item);
                                //   // openCaseAssignScreen(item);
                                // },
                                // onTapApprove: () {
                                //   viewTicketController.selectedCaseId =
                                //       viewTicketController
                                //           .ticketList![index].caseId;
                                //   addRemarkTicketDialog(
                                //       context,
                                //       Strings.approve,
                                //       viewTicketController
                                //           .ticketList![index]);
                                //   // getTicketStaff(item, true);
                                // },
                                // onTapReject: () {
                                //   viewTicketController.selectedCaseId =
                                //       viewTicketController
                                //           .ticketList![index].caseId;
                                //   addRemarkTicketDialog(
                                //       context,
                                //       Strings.reject,
                                //       viewTicketController
                                //           .ticketList![index]);
                                //   // getTicketStaff(item, false);
                                // },
                                // onTapChangePriority: () {
                                //   showTicketPriorityDialog(item);
                                // },
                                onTapPick: () {
                                  showTicketPickDialog(item);
                                },
                                // onTapFollowup: () {
                                //   showFollowUpPopup(item);
                                // },
                                // onTapLink: () {
                                //   openLinkTicketScreen(item);
                                // },
                                // onTapUploadDoc: () {
                                //   openTicketDocumentUploadScreen(item);
                                // },
                                // onTapChangeProblemDomain: () {
                                //   viewTicketController
                                //       .checkTicketReAssign(item);
                                // },
                                // onTapSelectItem: () {},
                                // onTapTicketChangeStatus: () {
                                //   openTicketChangeStatusScreen(item);
                                // },
                                onTapSLATimeCounter: () {
                                  showSLATimeCounterDialog(item);
                                },
                                // onTapETRTicket: () {
                                //   onTicketETRScreen(item);
                                // },
                                // onTapTicketRemark: () {
                                //   onTicketRemarkScreen(item);
                                // },
                              ),
                            );
                          }
                        }),
                  )
                : noDataFound(),
          ),
        ],
      ),
    );
  }
}
