import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/network_management/ip/create_ip_screen.dart';
import 'package:savbill/pages/network_management/ip/ip_management_controller.dart';
import 'package:savbill/pages/network_management/ip/ip_pool_item_list.dart';
import 'package:savbill/pages/network_management/model/response/ip_management_list_res.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class IpManagementList extends StatefulWidget {
  @override
  _IpManagementListState createState() => _IpManagementListState();
}

class _IpManagementListState extends State<IpManagementList> implements LogoutClickEvent {
  final ipManagementController = Get.put(IpPoolController());
  final GlobalKey<ScaffoldState> _deviceListKey = GlobalKey();

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    ipManagementController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<IpPoolController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: _deviceListKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: _body(),
            ),
          ),
          ProgressBar(isLoader: ipManagementController.isLoading),
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
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: Strings.ip,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (ipManagementController.ipManagementList != null &&
                    ipManagementController.ipManagementList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: ipManagementController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      ipManagementController.ipManagementList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            ipManagementController.ipManagementList?.length) {
                          if (ipManagementController.isShowLoadMore) {
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
                          IpManagementDataList item =
                          ipManagementController.ipManagementList![index];
                          return InkWell(
                            onTap: () {},
                            child: IpPoolItemList(
                              item: item,
                              onTapDetail: () {
                                /*ipManagementController.getDeviceDetail(
                                    item.id!, Strings.device_detail);*/
                              },

                              onTapUpdateIp: () {
                                ipManagementController.getIpListData(item.poolId);
                              },
                              onTapDelete: () {
                                showDialog(
                                  context: context,
                                  builder: (BuildContext context) {
                                    return AlertDialogHelper(
                                        title: Strings.app_name,
                                        message: Strings.msg_delete,
                                        positiveBtnText: Strings.ok,
                                        negativeBtnText: Strings.cancel,
                                        positiveBtnClick: () {
                                          Get.back();
                                          ipManagementController
                                              .deleteDevice(item, index);
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              },
                            ),
                          );
                        }
                      }),
                )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                        onTap: () {
                          openCreateIpManagementScreen(Strings.add,null);
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.create_ip,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w400,
                        ),
                      ))
                ],
              )
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.ip_management, '', AppTheme.colorPrimary,
        true, _onMenuClick, [], AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (_deviceListKey.currentState!.isDrawerOpen) {
      _deviceListKey.currentState?.closeDrawer();
    } else {
      _deviceListKey.currentState?.openDrawer();
    }
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
    ipManagementController.getStorage.remove(Constant.USER_DATA);
    ipManagementController.getStorage.remove(Constant.USER_TOKEN);
    ipManagementController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }


  openCreateIpManagementScreen(String from, IpManagementDataList? item) async {
    var result = await Get.to(CreateIpScreen(), arguments: {
      Constant.FROM: from, Constant.IM_DETAIL: item
    });
    if (result != null && result == true) {
      ipManagementController.clearFilter();
    }
  }
}