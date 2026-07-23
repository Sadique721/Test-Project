import 'package:savbill/pages/dashboard/model/response/payment_team_hierarchy_res.dart';
import 'package:savbill/pages/dashboard/model/response/workflow_audit_res.dart';
import 'package:savbill/pages/dashboard/payment_audit_controller.dart';
import 'package:savbill/pages/dashboard/workflow_audit_item.dart';
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

class PaymentAudit extends StatefulWidget {
  @override
  _PaymentAuditState createState() => _PaymentAuditState();
}

class _PaymentAuditState extends State<PaymentAudit>
    with TickerProviderStateMixin {
  final paymentAuditController = Get.put(PaymentAuditController());
  TabController? _tabController;

  List<Tab> myTabs = <Tab>[
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.payment_status,
          textAlign: TextAlign.center,
        ),
      ),
    ),
    const Tab(
      child: Align(
        alignment: Alignment.center,
        child: Text(
          Strings.workflow_audit,
          textAlign: TextAlign.center,
        ),
      ),
    ),
  ];

  @override
  void initState() {
    super.initState();
    _tabController =
        TabController(vsync: this, length: myTabs.length, initialIndex: 0)
          ..addListener(() {
            paymentAuditController.tabIndex = _tabController!.index;
            paymentAuditController.update();
          });
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<PaymentAuditController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: _body(),
        ),
        ProgressBar(isLoader: paymentAuditController.isLoading),
      ]);
    });
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      width: MediaQuery.of(context).size.width,
      child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
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
                unselectedLabelColor: AppTheme.title_dark.withOpacity(0.8),
                indicator: UnderlineTabIndicator(
                  borderSide: BorderSide(
                      width: Constant.TAB_INDICATOR_H,
                      color: AppTheme.title_dark),
                ),
                labelColor: AppTheme.title_dark,
                labelStyle: const TextStyle(
                    fontSize: AppTheme.large, fontWeight: FontWeight.w600),
                unselectedLabelStyle: const TextStyle(
                    fontSize: AppTheme.medium, fontWeight: FontWeight.w500),
                tabs: myTabs,
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Flexible(
              child: TabBarView(
                controller: _tabController,
                children: [
                  _teamHierarchyDetail(),
                  _workflowAuditDetail(),
                ], //_tabsContainer(),
              ),
            ),
          ]),
    );
  }

  _teamHierarchyDetail() {
    Size size = MediaQuery.of(context).size;
    return (paymentAuditController.teamHierarchyList != null &&
            paymentAuditController.teamHierarchyList!.isNotEmpty)
        ? ListView.builder(
            itemCount: paymentAuditController.teamHierarchyList!.length,
            itemBuilder: (context, i) {
              TeamHierarchyDetail item =
                  paymentAuditController.teamHierarchyList![i];
              return Stack(
                children: [
                  Padding(
                    padding: const EdgeInsets.symmetric(
                        vertical: 40, horizontal: 35),
                    child: Row(
                      children: [
                        SizedBox(width: size.width * 0.1),
                        SizedBox(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisAlignment: MainAxisAlignment.start,
                            children: [
                              CustomText(
                                  title: item.teamName,
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.medium,
                                  height: 1,
                                  fontWeight: FontWeight.w500)
                            ],
                          ),
                        )
                      ],
                    ),
                  ),
                  Positioned(
                    left: 50,
                    child: Container(
                      height: size.height * 0.5,
                      width: 2.0,
                      color: item.status != null &&
                              item.status!.isNotEmpty &&
                              item.status!.equalsIgnoreCase("Approved")
                          ? AppTheme.colorGreenRView
                          : AppTheme.colorIconGrey,
                    ),
                  ),
                  Positioned(
                    bottom: 0,
                    child: Padding(
                      padding: const EdgeInsets.all(40.0),
                      child: Container(
                        height: 20.0,
                        width: 20.0,
                        decoration: BoxDecoration(
                          color: item.status != null &&
                                  item.status!.isNotEmpty &&
                                  item.status!.equalsIgnoreCase("Approved")
                              ? AppTheme.colorGreenRView
                              : AppTheme.colorIconGrey,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Icon(
                          Icons.check,
                          size: Constant.ICON_SIZE_M,
                          color: AppTheme.colorWhite,
                        ),
                      ),
                    ),
                  ),
                ],
              );
            })
        : noDataFound();
  }

  _workflowAuditDetail() {
    return (paymentAuditController.workflowAuditList != null &&
            paymentAuditController.workflowAuditList!.isNotEmpty)
        ? ListView.builder(
            controller: paymentAuditController.controller,
            padding: const EdgeInsets.symmetric(
              horizontal: Constant.EXTRA_LARGE_PADDING,
            ),
            itemCount: paymentAuditController.workflowAuditList!.length + 1,
            itemBuilder: (BuildContext context, int index) {
              if (index == paymentAuditController.workflowAuditList?.length) {
                if (paymentAuditController.isShowLoadMore) {
                  return Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
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
                WorkflowAuditDetail item =
                    paymentAuditController.workflowAuditList![index];
                return WorkflowAuditItem(
                  item: item,
                  onTapStaffDetail: () {

                  },
                );
              }
            })
        : noDataFound();
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.payment_status, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
