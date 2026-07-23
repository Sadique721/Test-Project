import 'package:savbill/pages/dashboard/savbill_care_tab.dart';
import 'package:savbill/pages/dashboard/dashboard_controller.dart';
import 'package:savbill/pages/dashboard/home_tab.dart';
import 'package:savbill/pages/dashboard/payment_tab.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class DashboardScreen extends StatefulWidget {
  @override
  _DashboardScreenState createState() => _DashboardScreenState();
}

class _DashboardScreenState extends State<DashboardScreen>
    with TickerProviderStateMixin
    implements LogoutClickEvent, HomeItemClickEvent {
  final dashboardController = Get.put(DashboardController());
  final GlobalKey<ScaffoldState> _dashKey = GlobalKey();
  List<Widget> tabDetailFragment = [];
  late Animation<double> _animation;
  late AnimationController _controller;


  Future<bool> _onWillPop() async {
    if (dashboardController.tabIndex != 0) {
      // If not on Home tab, go to Home tab instead of exiting
      dashboardController.tabIndex = 0;
      dashboardController.update();
      return false; // Do not exit the app
    } else {
      // If already on Home tab, show exit dialog
      return (await _showExitDialog()) ?? false;
    }
  }

  Future<bool?> _showExitDialog() {
    return showDialog<bool>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
          title: Strings.app_name,
          message: Strings.msg_exit,
          positiveBtnText: Strings.yes,
          negativeBtnText: Strings.no,
          positiveBtnClick: () {
            Navigator.of(context).pop(true); // Exit confirmed
          },
          negativeBtnClick: () {
            Navigator.of(context).pop(false); // Exit canceled
          },
        );
      },
    );
  }

  @override
  void initState() {
    super.initState();
    tabDetailFragment.clear();
    tabDetailFragment.add(HomeTab(homeItemClickEvent: this,));
    //  tabDetailFragment.add(MyPlanTab(homeItemClickEvent: this));
    tabDetailFragment.add(PaymentTab());
    tabDetailFragment.add(SavbillCareTab());

    _controller = AnimationController(
      duration: const Duration(seconds: 2),
      vsync: this,
    )..repeat(reverse: true);
    final tween = Tween<double>(begin: 0.75, end: 1.3);
    _animation = ReverseTween(tween).animate(_controller);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<DashboardController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            // key: _dashKey,
            // extendBody: true,
            // extendBodyBehindAppBar:true,
            appBar: _appBar(),
            body: Scaffold(
              key: _dashKey,
              drawer: SideDrawer(logoutClickEvent: this),
              body: /*IndexedStack(
                index: dashboardController.tabIndex,
                children: tabDetailFragment,
              ), */
                  tabDetailFragment[dashboardController.tabIndex],
            ),
            // bottomNavigationBar: buildMyNavBar(context),
            // _body(),
            // drawer: SideDrawer(logoutClickEvent: this),
          ),
          ProgressBar(isLoader: dashboardController.isLoading),
        ]);
      }),
    );
  }

  navBarItem(int id, String title, String icon) {
    return BottomNavigationBarItem(
      icon: IconButton(
        padding: const EdgeInsets.all(0.0),
        icon: SvgPicture.asset(
          icon,
          height: Constant.ACTION_ICON_SIZE,
          width: Constant.ACTION_ICON_SIZE,
          color: dashboardController.tabIndex == id
              ? AppTheme.colorPrimary
              : AppTheme.colorWhite,
          fit: BoxFit.fill,
        ),
        onPressed: () {
          _clickOnNavBarItem(id);
        },
      ),
      /*Column(
        children: [
          IconButton(
            padding: const EdgeInsets.all(0.0),
            icon: SvgPicture.asset(
              icon,
              height: Constant.ACTION_ICON_SIZE,
              width: Constant.ACTION_ICON_SIZE,
              color: dashboardController.tabIndex == id
                  ? AppTheme.colorPrimary
                  : AppTheme.colorWhite,
              fit: BoxFit.fill,
            ),
            onPressed: () {
              _clickOnNavBarItem(id);
            },
          ),
          CustomText(
            title: title,
            colors: dashboardController.tabIndex == id
                ? AppTheme.colorPrimary
                : AppTheme.colorWhite,
            textAlign: TextAlign.center,
            fontSize: AppTheme.small,
            fontWeight: FontWeight.normal,
            maxLines: 2,
          ),
        ],
      ),*/
      label: title,
      backgroundColor: AppTheme.colorBlack,
    );
  }

  _clickOnNavBarItem(int index) {
    if (_dashKey.currentState!.isDrawerOpen) {
      _onMenuClick();
    }
    dashboardController.tabIndex = index;
    dashboardController.update();
  }

  Widget buildMyNavBar(BuildContext context) {
    return BottomNavigationBar(
      elevation: 4,
      unselectedItemColor: AppTheme.colorWhite,
      showUnselectedLabels: true,
      backgroundColor: AppTheme.colorBlack,
      selectedLabelStyle: const TextStyle(
        fontSize: AppTheme.small,
        fontWeight: FontWeight.w500,
      ),
      type: BottomNavigationBarType.fixed,
      items: <BottomNavigationBarItem>[
        navBarItem(0, Strings.home, homeSvg),
        // navBarItem(1, Strings.my_plan, myPlanSvg),
        PermissionService().hasAclPermission([AclPaymentSystems.PAYMENT_SYSTEM]) == true ?
          navBarItem(1, Strings.payments, paymentSvg): navBarItem(2, Strings.tickets, savbillCareSvg),
        // PermissionService().hasAclPermission([AclTicketingSystems.TICKETING_SYSTEM]) == true ?
        navBarItem(2, Strings.tickets, savbillCareSvg),
      ],
      currentIndex: dashboardController.tabIndex,
      selectedItemColor: AppTheme.colorPrimary,
      onTap: _clickOnNavBarItem,
    );
  }

  _appBar() {
    return DynamicAppBar(
        Strings.dashboard,
        '',
        AppTheme.colorPrimary,
        true,
        _onMenuClick,
        [
          /* IconButton(
            constraints: const BoxConstraints(maxHeight: 36),
            padding: const EdgeInsets.all(0.0),
            icon: const Icon(
              Icons.search,
              color: Colors.white,
            ),
            onPressed: () {
              if (_dashKey.currentState!.isDrawerOpen) {
                _onMenuClick();
              }
              // dashboardController.openNotificationListScreen();
            },
          ),
          Padding(
            padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
            child: Stack(alignment: Alignment.center, children: <Widget>[
              IconButton(
                padding: const EdgeInsets.all(0.0),
                icon: SvgPicture.asset(
                  notificationSvg,
                  height: Constant.ACTION_ICON_SIZE,
                  width: Constant.ACTION_ICON_SIZE,
                  color: AppTheme.colorWhite,
                  fit: BoxFit.fill,
                ),
                onPressed: () {
                  if (_dashKey.currentState!.isDrawerOpen) {
                    _onMenuClick();
                  }
                },
              ),
              Positioned(
                right: 10,
                top: 14,
                child: Container(
                  padding: const EdgeInsets.all(6),
                  decoration: BoxDecoration(
                    color: AppTheme.colorRed,
                    borderRadius: BorderRadius.circular(6),
                  ),
                ),
              )
            ]),
          ),*/
        ],
        AppBar().preferredSize.height);
  }

  _onMenuClick() {
    if (_dashKey.currentState!.isDrawerOpen) {
      _dashKey.currentState?.closeDrawer();
    } else {
      _dashKey.currentState?.openDrawer();
    }
  }

  @override
  void logoutClick() {
    dashboardController.getStorage.remove(Constant.USER_DATA);
    dashboardController.getStorage.remove(Constant.USER_TOKEN);
    dashboardController.getStorage.remove(Constant.USER_SERVICES_AREA);
    dashboardController.getStorage.remove(Constant.DEMO_GRAPHIC_MAPPING);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  @override
  void itemClick(int identity) {
    _clickOnNavBarItem(identity);
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      _clickOnNavBarItem(1);
    }
  }
}
