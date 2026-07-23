import 'package:savbill/pages/customer_caf/caf_customer_plan/caf_active_item_plan_list.dart';
import 'package:savbill/pages/customer_caf/caf_customer_plan/caf_expired_plan_item_list.dart';
import 'package:savbill/pages/customer_caf/caf_customer_plan/customer_caf_plan_controller.dart';
import 'package:savbill/pages/customer_plan/change_subscribe_trial_plan_dialog.dart';
import 'package:savbill/pages/customer_plan/current_plan_extend_validity.dart';
import 'package:savbill/pages/customer_plan/display_note_dialog.dart';
import 'package:savbill/pages/customer_plan/subscriber_trial_plan_dialog.dart';
import 'package:savbill/pages/dashboard/model/response/cust_plan_detail_res.dart';
import 'package:savbill/pages/dashboard/model/response/plan_detail_response.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/list_loader.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerCafPlanDetail extends StatefulWidget {
  @override
  _CustomerPlanDetailState createState() => _CustomerPlanDetailState();
}

class _CustomerPlanDetailState extends State<CustomerCafPlanDetail>
    with TickerProviderStateMixin, WidgetsBindingObserver
    implements ChangeSubScribePlanBtnAction, SubscriberTrialBtnAction {
  final customerCafPlanController = Get.put(CustomerCafPlanController());
  TabController? _tabController;
  List<Tab> myTabs = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    customerCafPlanController.isCallAllApi = true;
    // customerCafPlanController.getArgumentData();
    customerCafPlanController.update();
    myTabs = <Tab>[
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

    // if(customerCafPlanController.trialPlanList != null &&
    //     customerCafPlanController.trialPlanList!.isNotEmpty){
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
            customerCafPlanController.tabIndex = _tabController!.index;
            customerCafPlanController.update();
          });

    // _tabController =
    //     TabController(vsync: this, length: myTabs.length, initialIndex:
    //         (customerCafPlanController.trialPlanList!.isNullOrEmpty() && customerCafPlanController.trialPlanList!.isNullOrEmpty())? 1 : 0)
    //       ..addListener(() {
    //         customerCafPlanController.tabIndex = _tabController!.index;
    //         customerCafPlanController.update();
    //       });
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    customerCafPlanController.setBtnClickEvent(false);
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
        if (customerCafPlanController.checkBtnClickEvent) {
          customerCafPlanController.setBtnClickEvent(false);
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<CustomerCafPlanController>(builder: (controller) {
      return Scaffold(
        backgroundColor: AppTheme.colorBG,
        appBar: _appBar(),
        body: SafeArea(
          child: _body(),
        ),
      );
    });
  }

  _body() {
    return Stack(children: <Widget>[
      customerCafPlanController.isLoading
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
                                title: customerCafPlanController.customerName,
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

  _activeList() {
    return (customerCafPlanController.activePlanList != null &&
            customerCafPlanController.activePlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerCafPlanController.activePlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return CafActivePlanItemView(
                index: index,
                item: customerCafPlanController.activePlanList![index],
                activePlanList: customerCafPlanController.activePlanList,
                customerDetail: customerCafPlanController.customerDetail,
                futurePlanList: customerCafPlanController.futurePlanList!,
                controller: customerCafPlanController,
              );
            })
        : noDataFound();
  }

  _futureList() {
    return (customerCafPlanController.futurePlanList != null &&
            customerCafPlanController.futurePlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerCafPlanController.futurePlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return CafActivePlanItemView(
                index: index,
                item: customerCafPlanController.futurePlanList![index],
                activePlanList: customerCafPlanController.activePlanList!,
                futurePlanList: customerCafPlanController.futurePlanList!,
                customerDetail: customerCafPlanController.customerDetail,
                controller: customerCafPlanController,
              );
            })
        : noDataFound();
  }

  _expiredList() {
    return (customerCafPlanController.expiredPlanList != null &&
            customerCafPlanController.expiredPlanList!.isNotEmpty)
        ? ListView.builder(
            padding: const EdgeInsets.symmetric(
                horizontal: Constant.EXTRA_LARGE_PADDING,
                vertical: Constant.SMALL_PADDING),
            itemCount: customerCafPlanController.expiredPlanList?.length,
            itemBuilder: (BuildContext context, int index) {
              return CafExpiredPlanListViewItem(
                index: index,
                item: customerCafPlanController.expiredPlanList![index],
                expiredPlanList: customerCafPlanController.expiredPlanList,
                currentPlanList: customerCafPlanController.expiredPlanList,
                userData: customerCafPlanController.userData,
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
      customerCafPlanController.extendDaysTrialPlanData(item!, extendsDays);
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
      customerCafPlanController.subscribeTrialPlanData(
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
        // customerCafPlanController.selectedParentCustomer = data;
        // customerCafPlanController.billableToController.text = data.name!;
        // customerCafPlanController.billableCustomerId = data.id;
        // customerCafPlanController.getTrialPlanListData();
        customerCafPlanController.update();
      }
      // Get.back();
      // customerCafPlanController.getTrialPlanListData();
      // customerCafPlanController.update();
    }
  }
}
