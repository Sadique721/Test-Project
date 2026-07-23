import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer_plan/active_item_plan_list.dart';
import 'package:savbill/pages/customer_plan/change_subscribe_trial_plan_dialog.dart';
import 'package:savbill/pages/customer_plan/current_plan_extend_validity.dart';
import 'package:savbill/pages/customer_plan/customer_plan_controller.dart';
import 'package:savbill/pages/customer_plan/display_note_dialog.dart';
import 'package:savbill/pages/customer_plan/future_item_plan_lis.dart';
import 'package:savbill/pages/customer_plan/plan_list_item.dart';
import 'package:savbill/pages/customer_plan/subscriber_trial_plan_dialog.dart';
import 'package:savbill/pages/customer_plan/trial_plan_list_item.dart';
import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/pages/dashboard/model/response/plan_detail_response.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/list_loader.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerPlanDetail extends StatefulWidget {
  @override
  _CustomerPlanDetailState createState() => _CustomerPlanDetailState();
}

class _CustomerPlanDetailState extends State<CustomerPlanDetail>
    with TickerProviderStateMixin, WidgetsBindingObserver
    implements ChangeSubScribePlanBtnAction, SubscriberTrialBtnAction {
  final customerPlanController = Get.put(CustomerPlanController());
  TabController? _tabController;
  List<Tab> myTabs = [];

  // List<Tab> myTabs = <Tab>[
  //   const Tab(
  //     child: Align(
  //       alignment: Alignment.center,
  //       child: Text(
  //         Strings.trial_plan,
  //         textAlign: TextAlign.center,
  //       ),
  //     ),
  //   ),
  //   const Tab(
  //     child: Align(
  //       alignment: Alignment.center,
  //       child: Text(
  //         Strings.current_plan,
  //         textAlign: TextAlign.center,
  //       ),
  //     ),
  //   ),
  //   const Tab(
  //     child: Align(
  //       alignment: Alignment.center,
  //       child: Text(
  //         Strings.future_plan,
  //         textAlign: TextAlign.center,
  //       ),
  //     ),
  //   ),
  //   const Tab(
  //     child: Align(
  //       alignment: Alignment.center,
  //       child: Text(
  //         Strings.expired_plan,
  //         textAlign: TextAlign.center,
  //       ),
  //     ),
  //   ),
  // ];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    customerPlanController.isCallAllApi = true;
    // customerPlanController.getArgumentData();
    customerPlanController.update();
    myTabs = <Tab>[
      const Tab(
        child: Align(
          alignment: Alignment.center,
          child: Text(
            Strings.trial_plan,
            textAlign: TextAlign.center,
          ),
        ),
      ),
      const Tab(
        child: Align(
          alignment: Alignment.center,
          child: Text(
            Strings.current_plan,
            textAlign: TextAlign.center,
          ),
        ),
      ),
      const Tab(
        child: Align(
          alignment: Alignment.center,
          child: Text(
            Strings.future_plan,
            textAlign: TextAlign.center,
          ),
        ),
      ),
      const Tab(
        child: Align(
          alignment: Alignment.center,
          child: Text(
            Strings.expired_plan,
            textAlign: TextAlign.center,
          ),
        ),
      ),
    ];

    // if(customerPlanController.trialPlanList != null &&
    //     customerPlanController.trialPlanList!.isNotEmpty){
    //   myTabs.add(const Tab( child: Align(
    //     alignment: Alignment.center,
    //     child: Text(
    //       Strings.trial_plan,
    //       textAlign: TextAlign.center,
    //     ),
    //   )));
    // }else{
    //   myTabs.remove(const Tab( child: Align(
    //     alignment: Alignment.center,
    //     child: Text(
    //       Strings.trial_plan,
    //       textAlign: TextAlign.center,
    //     ),
    //   )));
    // }

    _tabController =
        TabController(vsync: this, length: myTabs.length, initialIndex: 0)
          ..addListener(() {
            customerPlanController.tabIndex = _tabController!.index;
            customerPlanController.update();
          });

    // _tabController =
    //     TabController(vsync: this, length: myTabs.length, initialIndex:
    //         (customerPlanController.trialPlanList!.isNullOrEmpty() && customerPlanController.trialPlanList!.isNullOrEmpty())? 1 : 0)
    //       ..addListener(() {
    //         customerPlanController.tabIndex = _tabController!.index;
    //         customerPlanController.update();
    //       });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    customerPlanController.setBtnClickEvent(false);
    super.dispose();
  }

  _backScreen() {
    Get.back();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        //customerListController.setBtnClickEvent(false);
        return;
      case AppLifecycleState.resumed:
        if (customerPlanController.checkBtnClickEvent) {
          customerPlanController.setBtnClickEvent(false);
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerPlanController>(builder: (controller) {
      return Scaffold(
        backgroundColor: AppTheme.colorBG,
        appBar: _appBar(),
        body: _body(),
      );
    });
  }

  _body() {
    return Stack(children: <Widget>[
      customerPlanController.isLoading
          ? Padding(
              padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
              child: ListView.separated(
                itemCount: 6,
                itemBuilder: (context, index) => const ListLoader(),
                separatorBuilder: (context, index) =>
                    const SizedBox(height: Constant.SCREEN_PADDING),
              ),
            )
          : Container(
              color: AppTheme.colorBG,
              width: MediaQuery.of(context).size.width,
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Container(
                      padding: const EdgeInsets.only(
                          top: Constant.SCREEN_PADDING,
                          left: Constant.SCREEN_PADDING,
                          right: Constant.SCREEN_PADDING),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          CustomText(
                              title: Strings.plan_summary,
                              colors: AppTheme.colorBlack,
                              textAlign: TextAlign.start,
                              fontSize: AppTheme.medium + 1,
                              fontWeight: FontWeight.w500),
                          const SizedBox(
                            width: Constant.VERY_SMALL_PADDING,
                          ),
                          Expanded(
                            child: CustomText(
                                title: customerPlanController.customerName,
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.medium + 1,
                                maxLines: 2,
                                fontWeight: FontWeight.w600),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    ),
                    Container(
                      margin: const EdgeInsets.symmetric(
                        horizontal: Constant.SCREEN_PADDING,
                      ),
                      height: Constant.TABBAR_HEIGHT,
                      decoration: BoxDecoration(
                        color: AppTheme.colorTransparent,
                        border: Border(
                            bottom: BorderSide(
                                color: AppTheme.title_dark.withOpacity(0.9),
                                width: Constant.TABBAR_BOTTOM_LINE_H)),
                      ),
                      child: TabBar(
                        controller: _tabController,
                        unselectedLabelColor:
                            AppTheme.title_dark.withOpacity(0.8),
                        indicator: UnderlineTabIndicator(
                          borderSide: BorderSide(
                              width: Constant.TAB_INDICATOR_H,
                              color: AppTheme.title_dark),
                        ),
                        labelColor: AppTheme.title_dark,
                        labelStyle: const TextStyle(
                            fontSize: AppTheme.large,
                            fontWeight: FontWeight.w600),
                        unselectedLabelStyle: const TextStyle(
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w500),
                        tabs: myTabs,
                      ),
                    ),
                    Flexible(
                      child: TabBarView(
                        controller: _tabController,
                        children: [
                          _trialList(),
                          _activeList(),
                          _futureList(),
                          _expiredList(),
                        ], //_tabsContainer(),
                      ),
                    ),
                  ]),
            ),
    ]);
  }

  _trialList() {
    return (customerPlanController.trialPlanList != null &&
            customerPlanController.trialPlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerPlanController.trialPlanList!.length,
            itemBuilder: (BuildContext context, int index) {
              return TrialPlanListViewItem(
                index: index,
                item: customerPlanController.trialPlanList![index],
                onTapExtendTrial: () {
                  showExtendTrialConfirmDialog(Strings.extend,
                      customerPlanController.trialPlanList![index]);
                },
                onTapNotification: () {
                  CustPlanDataList item =
                      customerPlanController.trialPlanList![index];
                  customerPlanController.setBtnClickEvent(false);
                  customerPlanController.changeSubscribePlanPopup(
                      index, this, item);
                },
                onTapDelete: () {
                  showDialog(
                    context: context,
                    builder: (BuildContext context) {
                      CustPlanDataList item =
                          customerPlanController.trialPlanList![index];
                      return AlertDialogHelper(
                          title: Strings.app_name,
                          message: Strings.msg_delete,
                          positiveBtnText: Strings.ok,
                          negativeBtnText: Strings.cancel,
                          positiveBtnClick: () {
                            Get.back();
                            customerPlanController.cancelTrialPlanData(
                                item, index);
                            customerPlanController.update();
                          },
                          negativeBtnClick: () {
                            Get.back();
                          });
                    },
                  );
                },
              );
            })
        : noDataFound();
  }

  _activeList() {
    return (customerPlanController.activePlanList != null &&
            customerPlanController.activePlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerPlanController.activePlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return ActivePlanItemView(
                index: index,
                item: customerPlanController.activePlanList![index],
                activePlanList: customerPlanController.activePlanList,
                customerDetail: customerPlanController.customerDetail,
                futurePlanList: customerPlanController.futurePlanList!,
                controller: customerPlanController,
                onTapNotes: () {
                  showDisplayNoteDialog(Strings.plan,
                      customerPlanController.activePlanList!);
                },
                onTapExtendTrial: () {
                  openCurrentPlanExtentValidityScreen(
                      customerPlanController.activePlanList![index]);
                  customerPlanController.update();
                  /*showDialog(
                    context: context,
                    builder: (BuildContext context) {
                      CustPlanDataList item =
                      customerPlanController.activePlanList![index];
                      return AlertDialogHelper(
                          title: Strings.app_name,
                          message: Strings.extend_service_conf,
                          positiveBtnText: Strings.yes,
                          negativeBtnText: Strings.no,
                          positiveBtnClick: () {
                            Get.back();
                            openCurrentPlanExtentValidityScreen(item);
                            customerPlanController.update();
                          },
                          negativeBtnClick: () {
                            Get.back();
                          });
                    },
                  );*/
                },
              );
            })
        : noDataFound();
  }

  _futureList() {
    return (customerPlanController.futurePlanList != null &&
            customerPlanController.futurePlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerPlanController.futurePlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return FuturePlanItemView(
                index: index,
                item: customerPlanController.futurePlanList![index],
                futurePlanList: customerPlanController.futurePlanList!,
                customerDetail: customerPlanController.customerDetail,
                onTapNotes: () {},
                onTapExtendTrial: () {
                  openCurrentPlanExtentValidityScreen(
                      customerPlanController.futurePlanList![index]);
                  customerPlanController.update();
                },
              );
            })
        : noDataFound();
  }

  _expiredList() {
    return (customerPlanController.expiredPlanList != null &&
            customerPlanController.expiredPlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerPlanController.expiredPlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return PlanListViewItem(
                index: index,
                item: customerPlanController.expiredPlanList![index],
                expiredPlanList: customerPlanController.expiredPlanList,
                currentPlanList: customerPlanController.expiredPlanList,
                userData: customerPlanController.userData,
              );
            })
        : noDataFound();
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.customer_plan, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  showExtendTrialConfirmDialog(String type, CustPlanDataList detail) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return SubscriberTrialDialog(
            from: type,
            subscriberTrialBtnAction: this,
            item: detail,
          );
        });
  }


  showDisplayNoteDialog(String type,  List<CustPlanDataList>? custPlanDetail) {
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return DisplayNoteDialog(
            from: type,
            // subscriberTrialBtnAction: this,
            itemList: custPlanDetail,
          );
        });
  }

  @override
  void subscriberBtnAction(
      {String? identifier, String? extendsDays, CustPlanDataList? item}) {
    Get.back();
    if (extendsDays != null && identifier != null && identifier.isNotEmpty) {
      customerPlanController.extendDaysTrialPlanData(item!, extendsDays);
    }
  }

  /* @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.extend)) {
      // getCurrentPosition(false);
    }
  }*/

  @override
  void subscriberTrialBtnAction(
      {String? identifier,
      CustPlanDataList? item,
      DropdownDetail? selectBillingData,
      String? remarkController}) {
    Get.back();
    if (item != null && identifier != null && identifier.isNotEmpty) {
      customerPlanController.subscribeTrialPlanData(
          item: item,
          selectedBillingStartFrom: selectBillingData,
          remark: remarkController);
    }
  }

  openCurrentPlanExtentValidityScreen(CustPlanDataList item) async {
    var result = await Get.to(CurrentPlanExtendValidity(),
        arguments: {Constant.EXTEND_VALIDITY: item});
    if (result != null && result == true) {
      PlanDetail data = result;
      if (data != null) {
        // customerPlanController.selectedParentCustomer = data;
        // customerPlanController.billableToController.text = data.name!;
        // customerPlanController.billableCustomerId = data.id;
        customerPlanController.getTrialPlanListData();
        customerPlanController.update();
      }
      // Get.back();
      // customerPlanController.getTrialPlanListData();
      // customerPlanController.update();
    }
  }
}
