import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/view_task_sub_cat_mgmt.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/view_task_category_mgmt.dart';
import 'package:savbill/pages/task_management/task_mgmt/view_task_mgmt.dart';
import 'package:savbill/pages/task_management/task_system_home_controller.dart';
import 'package:savbill/pages/task_management/tat_task/view_task/view_tat_task.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../routes/app_routes.dart';
import '../../util/constant.dart';
import '../../util/strings.dart';
import '../dashboard/model/data_list_item.dart';

class TaskSystemHome extends StatefulWidget {
  const TaskSystemHome({super.key});

  @override
  State<TaskSystemHome> createState() => _TaskSystemHomeState();
}

class _TaskSystemHomeState extends State<TaskSystemHome>
    implements LogoutClickEvent {
  final taskSystemHomeController = Get.put(TaskSystemHomeController());
  final GlobalKey<ScaffoldState> taskSystemHomeKey = GlobalKey();

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
    taskSystemHomeController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<TaskSystemHomeController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: taskSystemHomeKey,
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
        padding: EdgeInsets.symmetric(horizontal: Constant.SCREEN_PADDING),
        child: Column(children: <Widget>[
          SizedBox(height: Constant.SCREEN_PADDING),
          Expanded(
            child: taskSystemHomeController.dataList.isNotEmpty
                ? ListView.builder(
              itemCount: taskSystemHomeController.dataList.length,
              itemBuilder: (BuildContext context, int index) {
                ItemList data = taskSystemHomeController.dataList[index];
                String? icon = data.icon;
                return Padding(
                  padding: EdgeInsets.only(
                    left: Constant.VERY_SMALL_PADDING,
                    right: Constant.VERY_SMALL_PADDING,
                    top: (index == 0) ? 0 : Constant.LARGE_PADDING,
                  ),
                  child: InkWell(
                    onTap: () {
                      if (data.id == 1) {
                        openTatTaskScreen();
                      } else if (data.id == 2) {
                        openCategoryScreen();
                      } else if (data.id == 3) {
                        openSubCategoryScreen();
                      } else if (data.id == 4) {
                        openTasManagement();
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

  //1
  openTatTaskScreen() async {
    Get.to(ViewTatTask()); //    var result = await
  }
  openCategoryScreen() async {
    Get.to(ViewTaskCategoryManagement());
  }
  openSubCategoryScreen() async {
    Get.to(ViewTaskSubCategoryManagement());
  }
  openTasManagement() async {
    // Get.to(ViewTaskManagement());
    Get.to(ViewTaskMgmt()); //    var result = await
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (taskSystemHomeKey.currentState!.isDrawerOpen) {
      taskSystemHomeKey.currentState?.closeDrawer();
    } else {
      taskSystemHomeKey.currentState?.openDrawer();
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.task_management, '', AppTheme.colorPrimary,
        true, _onMenuClick, [], AppBar().preferredSize.height);
  }

  @override
  void drawerItemClick({String? identity}) {
    if (identity!.isNotEmpty &&
        identity.equalsIgnoreCase(Strings.payment_system)) {
      Get.offAllNamed(AppRoutes.DASHBOARD,
          arguments: {Constant.FROM: Strings.payment_system});
    }
  }

  @override
  void logoutClick() {
    taskSystemHomeController.getStorage.remove(Constant.USER_DATA);
    taskSystemHomeController.getStorage.remove(Constant.USER_TOKEN);
    taskSystemHomeController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }
}
