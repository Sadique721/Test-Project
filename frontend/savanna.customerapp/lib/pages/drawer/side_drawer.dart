import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/login/model/response/demo_graphic_mapping_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:package_info_plus/package_info_plus.dart';

import '../../widgets/alert_dialog.dart';

class SideDrawer extends StatefulWidget {
  // final
  final LogoutClickEvent logoutClickEvent;

  const SideDrawer({Key? key, required this.logoutClickEvent})
      : super(key: key);

  @override
  _SideDrawerState createState() => _SideDrawerState();
}

class _SideDrawerState extends State<SideDrawer> {
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  String userName = "";

  List drawerData = [];
  String appVersion = "";

  List<Demographicmappingtable>? demoGraphicMapping = [];

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    initPlatformState();
  }

  showExitDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: Strings.msg_exit,
            positiveBtnText: Strings.yes,
            negativeBtnText: Strings.no,
            positiveBtnClick: () {
              Navigator.pop(context);
              SystemChannels.platform.invokeMethod('SystemNavigator.pop');
            },
            negativeBtnClick: () {
              Navigator.pop(context);
            });
      },
    );
  }

  Future<void> initPlatformState() async {
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    setState(() {
      appVersion = packageInfo.version;
    });
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (!strUserData.isNullOrEmpty()) {
      setState(() {
        userDetail = UserDetail.fromJson(jsonDecode(strUserData));
        if (!userDetail!.fullName!.isNullOrEmpty()) {
          userName = userDetail!.userName!.toString().capitalizeFirst!;
        }
      });
    }


    String strDemoUserData = "";
    if (getStorage.hasData(Constant.DEMO_GRAPHIC_MAPPING)) {
      strDemoUserData =  await getStorage.read(Constant.DEMO_GRAPHIC_MAPPING);
    }

    if (!strUserData.isNullOrEmpty()) {
      setState(() {
        List<dynamic> jsonData = jsonDecode(strDemoUserData);
        demoGraphicMapping =
            jsonData.map((item) => Demographicmappingtable.fromJson(item))
                .toList();
        Utils.masterData(demoGraphicMapping);
      });
    }

    drawerData.clear();
    // setState(() {
    //   drawerData = Utils.getDrawerListData();
    // });
  }

  @override
  Widget build(BuildContext context) {

    return SizedBox(
      width: MediaQuery.of(context).size.width / 1.4,
      child: Drawer(
        child: Container(
          color: AppTheme.colorBlack,
          child: Column(
            children: <Widget>[
              SizedBox(
                height: 120.0,
                child: DrawerHeader(
                  margin: EdgeInsets.zero,
                  padding: const EdgeInsets.only(
                      top: Constant.SCREEN_PADDING, left: 12),
                  child: SizedBox(
                    width: Get.width,
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.start,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: <Widget>[
                        Row(
                          children: [
                            ClipRRect(
                              borderRadius: BorderRadius.circular(
                                  Constant.MENU_PROFILE_SIZE),
                              child: SizedBox(
                                  height: Constant.MENU_PROFILE_SIZE,
                                  width: Constant.MENU_PROFILE_SIZE,
                                  child: Image.asset(savannaTZLogo)),
                            ),
                            const SizedBox(
                              width: Constant.SMALL_PADDING,
                            ),
                            Flexible(
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.start,
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  CustomText(
                                      title: "Hi,",
                                      colors: AppTheme.colorPrimary,
                                      fontSize: AppTheme.medium,
                                      fontWeight: FontWeight.w500,
                                      textAlign: TextAlign.start),
                                  const SizedBox(
                                    height: Constant.SMALL_PADDING,
                                  ),
                                  CustomText(
                                      title: userName,
                                      colors: AppTheme.colorPrimary,
                                      fontSize: AppTheme.small + 1,
                                      fontWeight: FontWeight.w500,
                                      textAlign: TextAlign.start,
                                      maxLines: 2),
                                  const SizedBox(
                                    height: Constant.VERY_SMALL_PADDING,
                                  ),
                                ],
                              ),
                            )
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              // Divider(
              //     color: AppTheme.colorPrimary,
              //     thickness: 2,
              //     indent: Constant.SCREEN_PADDING),
              // Expanded(
              //   child: Container(
              //     color: AppTheme.colorBlack,
              //     child: ListView.builder(
              //         padding: const EdgeInsets.only(
              //           top: 0,
              //         ),
              //         itemCount: drawerData.length,
              //         itemBuilder: (BuildContext context, int index) {
              //           return _createDrawerItem(
              //               context: context,
              //               index: index,
              //               text: '${drawerData[index]}',
              //               onTap: () => {onDrawerItemClick(context, index)});
              //         }),
              //   ),
              // ),
              Expanded(
                child: SingleChildScrollView(
                  child: Container(
                    color: AppTheme.colorBlack,
                    child: Column(
                      children: [
                        ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.dashboard,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {Get.offAllNamed(AppRoutes.DASHBOARD)}),
                        PermissionService().hasAclPermission([AclSalesCRMs.SALES_CRM, AclSalesCRMs.LEAD]) == true ?  ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.lead_management,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                              Get.offAllNamed(AppRoutes.LEAD_MANAGEMENT,
                                  arguments: {
                                    Constant.FROM: Strings.menu,
                                  })
                            }): const SizedBox.shrink(),
                        PermissionService().hasAclPermission([AclPreCustConstants.PRE_CUST]) == true  ?
                        ExpansionTile(
                          collapsedIconColor: Colors.white,
                          leading: SvgPicture.asset(
                            prepaidCustomerSvg,
                            height: Constant.ICON_SIZE,
                            width: Constant.ICON_SIZE,
                            color: AppTheme.colorWhite,
                            // fit: BoxFit.fill,
                          ),
                          title: Padding(
                            padding: const EdgeInsets.only(
                              left: Constant.SMALL_PADDING,
                            ),
                            child: CustomText(
                                title: Utils.customerPrepaid,
                                colors: AppTheme.colorWhite,
                                fontWeight: FontWeight.w400,
                                fontSize: AppTheme.medium,
                                maxLines: 2,
                                textAlign: TextAlign.start),
                          ),
                          iconColor: AppTheme.colorWhite,
                          // leading: Icon(Icons.person),
                          children: [
                            PermissionService().hasAclPermission([AclPreCustConstants.PRE_CUSTS_LIST]) == true ?
                            ListTile(
                              dense: true,
                              tileColor: AppTheme.colorWhite,
                              leading: SvgPicture.asset(
                                prepaidCustomerSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: Utils.customerPrepaid,
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.CUSTOMER_LIST,
                                    arguments: {
                                      Constant.CUSTOMER_TYPE: Strings.prepaid,
                                    });
                              },
                            ) : const SizedBox.shrink(),
                            PermissionService().hasAclPermission([AclPreCustConstants.PRE_CUST_CAF_LIST]) == true ?
                            ListTile(
                              dense: true,
                              leading: SvgPicture.asset(
                                postpaidCustomerCAFSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              tileColor: AppTheme.colorWhite,
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: "${Utils.customerPrepaid} ${Strings.caf}",
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.CUSTOMER_CAF_LIST,
                                    arguments: {
                                      Constant.CUSTOMER_TYPE: Strings.prepaid,
                                    });
                              },
                            ) : const SizedBox.shrink(),
                          ],
                        ) : const SizedBox.shrink(),
                        PermissionService().hasAclPermission([AclPostCustConstants.POST_CUST]) == true ?
                        ExpansionTile(
                          collapsedIconColor: Colors.white,
                          leading: SvgPicture.asset(
                            postpaidCustomerSvg,
                            height: Constant.ICON_SIZE,
                            width: Constant.ICON_SIZE,
                            color: AppTheme.colorWhite,
                            // fit: BoxFit.fill,
                          ),
                          title: Padding(
                            padding: const EdgeInsets.only(
                              left: Constant.SMALL_PADDING,
                            ),
                            child: CustomText(
                                title: Utils.customerPostpaid,
                                colors: AppTheme.colorWhite,
                                fontWeight: FontWeight.w400,
                                fontSize: AppTheme.medium,
                                maxLines: 2,
                                textAlign: TextAlign.start),
                          ),
                          iconColor: AppTheme.colorWhite,
                          // leading: Icon(Icons.person),
                          children: [
                            PermissionService().hasAclPermission([AclPostCustConstants.POST_CUST_LIST]) == true ?
                            ListTile(
                              dense: true,
                              tileColor: AppTheme.colorWhite,
                              leading: SvgPicture.asset(
                                postpaidCustomerSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: Utils.customerPostpaid,
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.CUSTOMER_LIST,
                                    arguments: {
                                      Constant.CUSTOMER_TYPE: Strings.postpaid,
                                    });
                              },
                            ) : const SizedBox.shrink(),
                            PermissionService().hasAclPermission([AclPostCustConstants.POST_CUST_CAF]) == true ?
                            ListTile(
                              dense: true,
                              leading: SvgPicture.asset(
                                postpaidCustomerCAFSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              tileColor: AppTheme.colorWhite,
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: "${Utils.customerPostpaid} ${Strings.caf}",
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.CUSTOMER_CAF_LIST,
                                    arguments: {
                                      Constant.CUSTOMER_TYPE: Strings.postpaid,
                                    });
                              },
                            ) : const SizedBox.shrink(),
                          ],
                        ): const SizedBox.shrink(),

                        PermissionService().hasAclPermission([AclPaymentSystems.PAYMENT_SYSTEM]) == true ?  ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.payment_system,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                                  widget.logoutClickEvent.drawerItemClick(
                                      identity: Strings.payment_system)
                                }): const SizedBox.shrink(),

                        PermissionService().hasAclPermission([AclCreditNotes.CREDIT_NOTE]) == true ? ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.credit_note,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                                  Get.offAllNamed(AppRoutes.CREDIT_NOTE,
                                      arguments: {
                                        Constant.FROM: Strings.menu,
                                      })
                                }) : const SizedBox.shrink(),

                        PermissionService().hasAclPermission([AclInventorys.INVENTORY]) == true  ? ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.inventory_management,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                                  Get.offAllNamed(AppRoutes.INVENTORY_HOME,
                                      arguments: {
                                        Constant.FROM: Strings.menu,
                                      })
                                }): const SizedBox.shrink(),

                        PermissionService().hasAclPermission([AclTicketingSystems.TICKETING_SYSTEM]) == true ? ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.ticketing_system,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                                  Get.offAllNamed(AppRoutes.TICKET_SYSTEM,
                                      arguments: {
                                        Constant.FROM: Strings.menu,
                                      })
                                }) : const SizedBox.shrink(),


                        PermissionService().hasAclPermission([TaskSystems.TASK_DOMAIN]) == true  ? ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.task_management,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {

                              Get.offAllNamed(AppRoutes.TASK_MANAGEMENT,
                                  arguments: {
                                    Constant.FROM: Strings.menu,
                                  })
                            }): const SizedBox.shrink(),

                        PermissionService().hasAclPermission([AclNetworks.NETWORK]) == true ? ExpansionTile(
                          collapsedIconColor: Colors.white,
                          leading: SvgPicture.asset(
                            bindPortSvg,
                            height: Constant.ICON_SIZE,
                            width: Constant.ICON_SIZE,
                            color: AppTheme.colorWhite,
                            // fit: BoxFit.fill,
                          ),
                          title: Padding(
                            padding: const EdgeInsets.only(
                              left: Constant.SMALL_PADDING,
                            ),
                            child: CustomText(
                                title: Strings.network_management,
                                colors: AppTheme.colorWhite,
                                fontWeight: FontWeight.w400,
                                fontSize: AppTheme.medium,
                                maxLines: 2,
                                textAlign: TextAlign.start),
                          ),
                          iconColor: AppTheme.colorWhite,
                          // leading: Icon(Icons.person),
                          children: [
                            PermissionService().hasAclPermission([AclNetworks.NETWORK_DEVICE]) == true ? ListTile(
                              dense: true,
                              tileColor: AppTheme.colorWhite,
                              leading: SvgPicture.asset(
                                bindPortSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: Strings.network_device,
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.NETWORK_MANAGEMENT);
                              },
                            ): const SizedBox.shrink(),
                            PermissionService().hasAclPermission([AclNetworks.IP]) == true ? ListTile(
                              dense: true,
                              leading: SvgPicture.asset(
                                bindPortSvg,
                                height: Constant.ICON_SIZE,
                                width: Constant.ICON_SIZE,
                                color: AppTheme.colorWhite,
                                // fit: BoxFit.fill,
                              ),
                              tileColor: AppTheme.colorWhite,
                              title: Padding(
                                padding: const EdgeInsets.only(
                                  left: Constant.SMALL_PADDING,
                                ),
                                child: CustomText(
                                    title: Strings.ip_management,
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w400,
                                    fontSize: AppTheme.medium,
                                    maxLines: 2,
                                    textAlign: TextAlign.start),
                              ),
                              onTap: () {
                                Get.offAllNamed(AppRoutes.IP_MANAGEMENT);
                              },
                            ): const SizedBox.shrink(),
                          ],
                        ): const SizedBox.shrink(),

                        ListTile(
                            dense: true,
                            tileColor: AppTheme.colorWhite,
                            title: Padding(
                              padding: const EdgeInsets.only(
                                left: Constant.SMALL_PADDING,
                              ),
                              child: CustomText(
                                  title: Strings.logoff,
                                  colors: AppTheme.colorWhite,
                                  fontWeight: FontWeight.w400,
                                  fontSize: AppTheme.medium,
                                  maxLines: 2,
                                  textAlign: TextAlign.start),
                            ),
                            onTap: () => {
                                  Navigator.pop(context),
                                  showDialog(
                                    context: context,
                                    builder: (BuildContext context) {
                                      return AlertDialogHelper(
                                          title: Strings.app_name,
                                          message: Strings.logout_msg_exit,
                                          positiveBtnText: Strings.yes,
                                          negativeBtnText: Strings.no,
                                          positiveBtnClick: () {
                                            Navigator.pop(context);
                                            GetStorage().erase();
                                            getStorage.remove(Constant.ACL_ENTRIES);
                                            widget.logoutClickEvent.logoutClick();

                                            // SystemChannels.platform.invokeMethod('SystemNavigator.pop');
                                          },
                                          negativeBtnClick: () {
                                            Navigator.pop(context);
                                          });
                                    },
                                  )
                                }),
                      ],
                    ),
                  ),
                ),
              ),
              Container(
                color: AppTheme.colorBlack,
                child: Align(
                  child: Column(
                    children: <Widget>[
                      Padding(
                        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            CustomText(
                                title: Strings.powered_by,
                                colors: AppTheme.colorWhite,
                                fontWeight: FontWeight.w400,
                                fontSize: AppTheme.small,
                                textAlign: TextAlign.start),
                            Expanded(
                                child: CustomText(
                                    title: '${Strings.version} $appVersion',
                                    colors: AppTheme.colorWhite,
                                    fontWeight: FontWeight.w300,
                                    fontSize: AppTheme.small,
                                    maxLines: 2,
                                    textAlign: TextAlign.end)),
                          ],
                        ),
                      ),
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  onDrawerItemClick(BuildContext context, int index) {
    if (drawerData[index].toString().equalsIgnoreCase(Strings.dashboard)) {
      Get.offAllNamed(AppRoutes.DASHBOARD);
    }
    /*else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.prepaid_customer)) {
      Get.offAllNamed(AppRoutes.CUSTOMER_LIST, arguments: {
        Constant.CUSTOMER_TYPE: Strings.prepaid,
      });
    } */
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.lead_management)) {
      Get.offAllNamed(AppRoutes.LEAD_MANAGEMENT, arguments: {
        Constant.FROM: Strings.menu,
      });
    }
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.postpaid_customer)) {
      Get.offAllNamed(AppRoutes.CUSTOMER_LIST, arguments: {
        Constant.CUSTOMER_TYPE: Strings.postpaid,
      });
    }
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.inventory_management)) {
      Get.offAllNamed(AppRoutes.INVENTORY_HOME, arguments: {
        Constant.FROM: Strings.menu,
      });
    }
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.ticketing_system)) {
      Get.offAllNamed(AppRoutes.TICKET_SYSTEM, arguments: {
        Constant.FROM: Strings.menu,
      });
    }

    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.payment_system)) {
      widget.logoutClickEvent.drawerItemClick(identity: Strings.payment_system);
    }
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.credit_note)) {
      Get.offAllNamed(AppRoutes.CREDIT_NOTE, arguments: {
        Constant.FROM: Strings.menu,
      });
    }
    else if (drawerData[index]
        .toString()
        .equalsIgnoreCase(Strings.network_management)) {
      // Get.offAllNamed(AppRoutes.NETWORK_MANAGEMENT);
    }
    else if (drawerData[index].toString().equalsIgnoreCase(Strings.logoff)) {
      // widget.logoutClickEvent.logoutClick();
      Navigator.pop(context);
      showDialog(
        context: context,
        builder: (BuildContext context) {
          return AlertDialogHelper(
              title: Strings.app_name,
              message: Strings.logout_msg_exit,
              positiveBtnText: Strings.yes,
              negativeBtnText: Strings.no,
              positiveBtnClick: () {
                Navigator.pop(context);
                widget.logoutClickEvent.logoutClick();
                // SystemChannels.platform.invokeMethod('SystemNavigator.pop');
              },
              negativeBtnClick: () {
                Navigator.pop(context);
              });
        },
      );
    } else {
      Get.back();
    }
  }

  Widget _createDrawerItem(
      {BuildContext? context,
      int? index,
      String? text,
      GestureTapCallback? onTap}) {
    if (index == 1) {
      return ExpansionTile(
        collapsedIconColor: Colors.white,
        leading: SvgPicture.asset(
          prepaidCustomerSvg,
          height: Constant.ICON_SIZE,
          width: Constant.ICON_SIZE,
          color: AppTheme.colorWhite,
          // fit: BoxFit.fill,
        ),
        title: Padding(
          padding: const EdgeInsets.only(
            left: Constant.SMALL_PADDING,
          ),
          child: CustomText(
              title: text!,
              colors: AppTheme.colorWhite,
              fontWeight: FontWeight.w400,
              fontSize: AppTheme.medium,
              maxLines: 2,
              textAlign: TextAlign.start),
        ),
        iconColor: AppTheme.colorWhite,
        // leading: Icon(Icons.person),
        children: [
          ListTile(
            dense: true,
            tileColor: AppTheme.colorWhite,
            leading: SvgPicture.asset(
              prepaidCustomerSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.prepaid_customer,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.CUSTOMER_LIST, arguments: {
                Constant.CUSTOMER_TYPE: Strings.prepaid,
              });
            },
          ),
          ListTile(
            dense: true,
            leading: SvgPicture.asset(
              postpaidCustomerCAFSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            tileColor: AppTheme.colorWhite,
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.prepaid_customer_caf,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.CUSTOMER_CAF_LIST, arguments: {
                Constant.CUSTOMER_TYPE: Strings.prepaid,
              });
            },
          ),
        ],
      );
    }
    if (index == 2) {
      return ExpansionTile(
        collapsedIconColor: Colors.white,
        leading: SvgPicture.asset(
          postpaidCustomerSvg,
          height: Constant.ICON_SIZE,
          width: Constant.ICON_SIZE,
          color: AppTheme.colorWhite,
          // fit: BoxFit.fill,
        ),
        title: Padding(
          padding: const EdgeInsets.only(
            left: Constant.SMALL_PADDING,
          ),
          child: CustomText(
              title: text!,
              colors: AppTheme.colorWhite,
              fontWeight: FontWeight.w400,
              fontSize: AppTheme.medium,
              maxLines: 2,
              textAlign: TextAlign.start),
        ),
        iconColor: AppTheme.colorWhite,
        // leading: Icon(Icons.person),
        children: [
          ListTile(
            dense: true,
            tileColor: AppTheme.colorWhite,
            leading: SvgPicture.asset(
              postpaidCustomerSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.postpaid_customers,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.CUSTOMER_LIST, arguments: {
                Constant.CUSTOMER_TYPE: Strings.postpaid,
              });
            },
          ),
          ListTile(
            dense: true,
            leading: SvgPicture.asset(
              postpaidCustomerCAFSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            tileColor: AppTheme.colorWhite,
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.postpaid_customer_caf,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.CUSTOMER_CAF_LIST, arguments: {
                Constant.CUSTOMER_TYPE: Strings.postpaid,
              });
            },
          ),
        ],
      );
    } else if (index == 8) {
      return ExpansionTile(
        collapsedIconColor: Colors.white,
        leading: SvgPicture.asset(
          bindPortSvg,
          height: Constant.ICON_SIZE,
          width: Constant.ICON_SIZE,
          color: AppTheme.colorWhite,
          // fit: BoxFit.fill,
        ),
        title: Padding(
          padding: const EdgeInsets.only(
            left: Constant.SMALL_PADDING,
          ),
          child: CustomText(
              title: text!,
              colors: AppTheme.colorWhite,
              fontWeight: FontWeight.w400,
              fontSize: AppTheme.medium,
              maxLines: 2,
              textAlign: TextAlign.start),
        ),
        iconColor: AppTheme.colorWhite,
        // leading: Icon(Icons.person),
        children: [
          ListTile(
            dense: true,
            tileColor: AppTheme.colorWhite,
            leading: SvgPicture.asset(
              bindPortSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.network_device,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.NETWORK_MANAGEMENT);
            },
          ),
          ListTile(
            dense: true,
            leading: SvgPicture.asset(
              bindPortSvg,
              height: Constant.ICON_SIZE,
              width: Constant.ICON_SIZE,
              color: AppTheme.colorWhite,
              // fit: BoxFit.fill,
            ),
            tileColor: AppTheme.colorWhite,
            title: Padding(
              padding: const EdgeInsets.only(
                left: Constant.SMALL_PADDING,
              ),
              child: CustomText(
                  title: Strings.ip_management,
                  colors: AppTheme.colorWhite,
                  fontWeight: FontWeight.w400,
                  fontSize: AppTheme.medium,
                  maxLines: 2,
                  textAlign: TextAlign.start),
            ),
            onTap: () {
              Get.offAllNamed(AppRoutes.IP_MANAGEMENT);
            },
          ),
        ],
      );
    }
    return ListTile(
      dense: true,
      tileColor: AppTheme.colorWhite,
      title: Padding(
        padding: const EdgeInsets.only(
          left: Constant.SMALL_PADDING,
        ),
        child: CustomText(
            title: text!,
            colors: AppTheme.colorWhite,
            fontWeight: FontWeight.w400,
            fontSize: AppTheme.medium,
            maxLines: 2,
            textAlign: TextAlign.start),
      ),
      onTap: onTap,
    );
  }
}

abstract class LogoutClickEvent {
  void logoutClick();

  void drawerItemClick({String identity});
}
