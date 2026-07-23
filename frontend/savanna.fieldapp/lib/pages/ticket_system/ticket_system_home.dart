import 'package:savbill/pages/dashboard/model/data_list_item.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/ticket_system/problem_domain/view_problem_domain.dart';
import 'package:savbill/pages/ticket_system/root_cause/view_root_cause.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/view_sub_problem_domain.dart';
import 'package:savbill/pages/ticket_system/tat_ticket/view_tat_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_system_home_controller.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class TicketSystemHome extends StatefulWidget {
  @override
  _TicketSystemHomeState createState() => _TicketSystemHomeState();
}

class _TicketSystemHomeState extends State<TicketSystemHome>
    implements LogoutClickEvent {
  final ticketSystemHomeController = Get.put(TicketSystemHomeController());
  final GlobalKey<ScaffoldState> ticketSystemHomeKey = GlobalKey();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    ticketSystemHomeController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TicketSystemHomeController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: ticketSystemHomeKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: _body(),
            ),
          ),
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
        padding:
            const EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
        child: Column(children: <Widget>[
          const SizedBox(height: Constant.SCREEN_PADDING),
          Expanded(
            child: ticketSystemHomeController.dataList.isNotEmpty
                ? ListView.builder(
                    itemCount: ticketSystemHomeController.dataList.length,
                    itemBuilder: (BuildContext context, int index) {
                      ItemList data =
                          ticketSystemHomeController.dataList[index];
                      String? icon = data.icon;
                      return Padding(
                        padding: EdgeInsets.only(
                          left: Constant.VERY_SMALL_PADDING,
                          right: Constant.VERY_SMALL_PADDING,
                          top: (index == 0) ? 0 : Constant.LARGE_PADDING,
                        ),
                        child: InkWell(
                          onTap: () {
                            if(data.id == 1) {
                              openTatTicketScreen();
                            } else if (data.id == 2) {
                              openProblemDomainScreen();
                              // openRootCauseScreen();
                            } else if (data.id == 3) {
                              openProblemDomainSubScreen();
                              // openProblemDomainScreen();
                            } else if (data.id == 4) {
                              openRootCauseScreen();
                              // openProblemDomainSubScreen();
                            } else if (data.id == 5) {
                              openTicketManagementScreen();
                            }
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              border: Border.all(
                                  color:
                                      AppTheme.colorPrimary, // Set border color
                                  width: 1.0), // Set border width
                              borderRadius: const BorderRadius.all(
                                  Radius.circular(
                                      6.0)), // Set rounded corner radius
                            ),
                            child: IntrinsicHeight(
                              child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Container(
                                      width: Constant.BTN_HEIGHT,
                                      height: Constant.BTN_HEIGHT,
                                      padding: const EdgeInsets.all(
                                          Constant.MEDIUM_PADDING),
                                      decoration: BoxDecoration(
                                        color: AppTheme.colorPrimary,
                                        borderRadius: const BorderRadius.only(
                                          topLeft: Radius.circular(5.0),
                                          bottomLeft: Radius.circular(5.0),
                                        ),
                                      ),
                                      child: Image.asset(
                                        icon!,
                                        height: Constant.ICON_SIZE,
                                        width: Constant.ICON_SIZE,
                                      ),
                                    ),
                                    const SizedBox(
                                        width: Constant.SCREEN_PADDING),
                                    Align(
                                      child: CustomText(
                                          title: data.title,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w400,
                                          colors: AppTheme.colorBlack),
                                    )
                                  ]),
                            ),
                          ),
                        ),
                      );
                    },
                  )
                : noDataFound(),
          ),
        ]),
      ),
    );
  }

  openTatTicketScreen() async {
    Get.to(ViewTatTicket()); //    var result = await
  }

  openRootCauseScreen() async {
    Get.to(ViewRootCause());
  }

  openProblemDomainScreen() async {
    Get.to(ViewProblemDomain());
  }

  openProblemDomainSubScreen() async {
    Get.to(ViewSubProblemDomain());
  }

  openTicketManagementScreen() async {
    Get.to(ViewTicket()); // var result = await
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (ticketSystemHomeKey.currentState!.isDrawerOpen) {
      ticketSystemHomeKey.currentState?.closeDrawer();
    } else {
      ticketSystemHomeKey.currentState?.openDrawer();
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.ticketing_system, '', AppTheme.colorPrimary,
        true, _onMenuClick, [], AppBar().preferredSize.height);
  }

  @override
  void logoutClick() {
    ticketSystemHomeController.getStorage.remove(Constant.USER_DATA);
    ticketSystemHomeController.getStorage.remove(Constant.USER_TOKEN);
    ticketSystemHomeController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      Get.offAllNamed(AppRoutes.DASHBOARD,
          arguments: {Constant.FROM: Strings.payment_system});
    }
  }
}
