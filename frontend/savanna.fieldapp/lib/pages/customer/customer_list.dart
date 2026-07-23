import 'package:savbill/pages/customer/add_edit_customer.dart';
import 'package:savbill/pages/customer/assign_inventory.dart';
import 'package:savbill/pages/customer/basic_details/customer_basic_details.dart';
import 'package:savbill/pages/customer/change_customer_status_dialog.dart';
import 'package:savbill/pages/customer/customer_detail.dart';
import 'package:savbill/pages/customer/customer_document.dart';
import 'package:savbill/pages/customer/customer_list_controller.dart';
import 'package:savbill/pages/customer/customer_list_view_item.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
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
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/custom_edit%20_note_dialog.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:geolocator/geolocator.dart';
import 'package:get/get.dart';
import 'dart:io';

class CustomerList extends StatefulWidget {
  @override
  _CustomerListState createState() => _CustomerListState();
}

class _CustomerListState extends State<CustomerList>
    with WidgetsBindingObserver
    implements
        LogoutClickEvent,
        ChangeCustomerStatusBtnAction,
        LocationBtnAction {
  final customerListController = Get.put(CustomerListController());
  final GlobalKey<ScaffoldState> _customerListKey = GlobalKey();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    customerListController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        //customerListController.setBtnClickEvent(false);
        return;
      case AppLifecycleState.resumed:
        if (customerListController.checkBtnClickEvent) {
          customerListController.setBtnClickEvent(false);
          locationPermissionStatus();
        }
        return;
      default:
        return;
    }
  }

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    customerListController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CustomerListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: _customerListKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: SafeArea(
                child: _body(),
              ),
            ),
          ),
          ProgressBar(isLoader: customerListController.isLoading),
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
                        title: Strings.customer_list,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    Row(children: [
                      InkWell(
                        onTap: () {
                          if (customerListController.filterViewOpen) {
                            customerListController.filterViewOpen = false;
                          } else {
                            customerListController.filterViewOpen = true;
                          }
                          customerListController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: customerListController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ])
                  ],
                ),
              ),
              const SizedBox(
                height: Constant.VERY_SMALL_PADDING,
              ),
              customerListController.filterViewOpen
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: Container(
                        // width: MediaQuery.of(context).size.width,
                        width: MediaQuery.of(context).size.width,
                        child: Material(
                          color: AppTheme.colorWhite, //AppTheme.colorFilterBg
                          elevation: 1.5,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(
                                  Constant.BTN_ROUNDED_CORNER - 2)),
                          child: Padding(
                            padding:
                                const EdgeInsets.all(Constant.SMALL_PADDING),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                DropdownButtonHideUnderline(
                                  child: DropdownButtonFormField(
                                    icon: SvgPicture.asset(
                                      downArrowSvg,
                                      height: Constant.DROP_DOWN_ARROW_W_H,
                                      width: Constant.DROP_DOWN_ARROW_W_H,
                                      color: AppTheme.colorBlack,
                                      fit: BoxFit.fill,
                                    ),
                                    decoration: InputDecoration(
                                        filled: true,
                                        contentPadding:
                                            const EdgeInsets.fromLTRB(
                                                Constant.LARGE_PADDING,
                                                0,
                                                Constant.LARGE_PADDING,
                                                0),
                                        fillColor: AppTheme.colorWhite,
                                        hintText: Strings.select_search_option,
                                        hintStyle: AppTheme.dropdownHintStyle,
                                        labelStyle: AppTheme.dropdownLabelStyle,
                                        errorStyle: AppTheme.dropdownErrorStyle,
                                        alignLabelWithHint: true,
                                        border: OutlineInputBorder(
                                          borderRadius: BorderRadius.circular(
                                              Constant
                                                  .DROP_DOWN_ROUNDED_CORNER),
                                          borderSide: BorderSide(
                                              color: AppTheme.colorIconGrey,
                                              width: 0.8),
                                        ),
                                        focusColor: Colors.transparent,
                                        focusedBorder: OutlineInputBorder(
                                          borderRadius: BorderRadius.circular(
                                              Constant
                                                  .DROP_DOWN_ROUNDED_CORNER),
                                          borderSide: BorderSide(
                                              color: AppTheme.colorIconGrey,
                                              width: 0.8),
                                        ),
                                        enabledBorder: OutlineInputBorder(
                                          borderRadius: BorderRadius.circular(
                                              Constant
                                                  .DROP_DOWN_ROUNDED_CORNER),
                                          borderSide: BorderSide(
                                            color: AppTheme.colorIconGrey,
                                            width: 1.0,
                                          ),
                                        ),
                                        errorMaxLines: 3),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerListController
                                        .selectedSearchCategory,
                                    items: customerListController
                                        .searchCategory!
                                        .map((CustomerSearchData value) {
                                      return DropdownMenuItem<
                                          CustomerSearchData>(
                                        value: value,
                                        child: Text(value.text!),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerListController
                                              .selectedSearchCategory =
                                          value as CustomerSearchData?;
                                      customerListController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                ),
                                CoustomTextField(
                                    labelText: Strings.search_your_text_here,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerListController.searchController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {},
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          customerListController.applyFilter();
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
                                    ),
                                    const SizedBox(
                                      width: Constant.LARGE_PADDING,
                                    ),
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          customerListController.clearFilter();
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
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      ),
                    )
                  : Container(),
              customerListController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
              Expanded(
                flex: 1,
                child: (customerListController.customerList != null &&
                        customerListController.customerList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: customerListController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                customerListController.customerList!.length + 1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  customerListController.customerList?.length) {
                                if (customerListController.isShowLoadMore) {
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
                                return InkWell(
                                  onTap: () async {
                                    customerListController
                                        .setBtnClickEvent(false);
                                    openCustomerDetailScreen(
                                        customerListController
                                            .customerList![index].id
                                        // customerListController.customerList![index].networkDetails!.serviceareaid
                                        );
                                  },
                                  child: CustomerListViewItem(
                                    index: index,
                                    custType:customerListController.type,
                                    item: customerListController
                                        .customerList![index],
                                    onTapNotes: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      openNotesScreen(customerListController
                                          .customerList![index].id);
                                    },
                                    onTapEdit: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      openBasicEditUpdateCustomerDetailsScreen(
                                          customerListController
                                              .customerList![index].id);
                                    },
                                    onTapDelete: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      showDialog(
                                        context: context,
                                        builder: (BuildContext context) {
                                          return AlertDialogHelper(
                                              title: Strings.app_name,
                                              message: Strings.msg_delete,
                                              positiveBtnText: Strings.yes,
                                              negativeBtnText: Strings.no,
                                              positiveBtnClick: () {
                                                Get.back();
                                                customerListController
                                                    .deleteCustomer(
                                                        customerListController
                                                            .customerList![
                                                                index]
                                                            .id,
                                                        index);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                    onTapDocumentUpload: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      openCustomerDocumentScreen(
                                          customerListController
                                              .customerList![index].id);
                                    },
                                    onTapNearByDevice: () {
                                      locationPermissionStatus();
                                    },
                                    // onTapSendPaymentLink: () {
                                    //   customerListController
                                    //       .setBtnClickEvent(false);
                                    //   customerListController
                                    //       .sendPaymentLinkToCustomer(
                                    //           customerListController
                                    //               .customerList![index].id);
                                    // },
                                    onTapAssignInventory: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      openAssignInventoryScreen(
                                          customerListController
                                              .customerList![index].id);

                                      /*  Utils.showSnackbar(
                                          Strings.SUCCESS,
                                          Strings.under_development,
                                          AppTheme.colorWhite,
                                          AppTheme.colorGreen);*/
                                    },
                                    onTapChangeStatus: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      customerListController
                                          .changeCustomerStatusUpPopup(index,
                                              this, customerListController);
                                    },

                                    // onTapCustomerInvoicePayment: (){
                                    //   customerListController
                                    //       .setBtnClickEvent(false);
                                    //   customerListController
                                    //       .customerInvoicePaymentLinkCall(
                                    //       customerListController
                                    //           .customerList![index].id,false);
                                    // },
                                    onTapRenewPayment: () {
                                      customerListController
                                          .setBtnClickEvent(false);
                                      customerListController
                                          .customerInvoicePaymentLinkCall(
                                              customerListController
                                                  .customerList![index].id,
                                              true);
                                    },
                                  ),
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              PermissionService().hasAclPermission([
                        customerListController.type.equalsIgnoreCase('Prepaid')
                            ? AclPreCustConstants.CREATE_PRE_CUST
                            : AclPostCustConstants.CREATE_POST_CUST_LIST
                      ]) ==
                      true
                  ? Row(
                      children: [
                        Expanded(
                            child: SimpleButton(
                          onTap: () {
                            openAddCustomerScreen();
                          },
                          radius: 0,
                          height: Constant.BOTTOM_BTN_HEIGHT,
                          bgColors: AppTheme.colorPrimary,
                          borderColors: AppTheme.colorPrimary,
                          child: CustomText(
                            title: customerListController.type
                                    .equalsIgnoreCase(Strings.prepaid)
                                ? Strings.create_prepaid_customer
                                : Strings.create_postpaid_customer,
                            fontSize: AppTheme.medium,
                            fontWeight: FontWeight.w400,
                          ),
                        ))
                      ],
                    )
                  : SizedBox.shrink()
            ]),
      ),
    );
  }

  openAddCustomerScreen() async {
    var result = await Get.to(AddEditCustomer(), arguments: {
      Constant.ACTION: Strings.add,
      Constant.CUSTOMER_TYPE: customerListController.type,
    });
    if (result != null && result == true) {
      customerListController.clearFilter();
    }
  }

  openCustomerDocumentScreen(int? customerId) async {
    Get.to(CustomerDocumentList(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  openAssignInventoryScreen(int? customerId) async {
    Get.to(() => AssignInventory(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  openCustomerDetailScreen(int? customerId) async {
    Get.to(() => CustomerDetailScreen(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_TYPE: customerListController.type
    });
  }

  openBasicEditUpdateCustomerDetailsScreen(int? customerId) async {
    Get.to(CustomerBasicDetails(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      // Constant.SERVICE_AREA_ID: serviceAreaId,
    });
  }

  openNotesScreen(int? customerId) async {
    showDialog(
      context: Get.context!,
      barrierDismissible: false,
      builder: (context) => CustomEditNoteDialog(
        title: Strings.addNotes,
        controller: customerListController.notesController,
        onSave: () {
          customerListController.addNoteCallApi(customerId);
          customerListController.notesController.clear();
          Get.back();
        },
        onCancel: () {
          Get.back();
        },
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (_customerListKey.currentState!.isDrawerOpen) {
      _customerListKey.currentState?.closeDrawer();
    } else {
      _customerListKey.currentState?.openDrawer();
    }
  }

  _appBar() {
    return DynamicAppBar(
        "${customerListController.type} ${Strings.customers}",
        '',
        AppTheme.colorPrimary,
        true,
        _onMenuClick,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void logoutClick() {
    customerListController.getStorage.remove(Constant.USER_DATA);
    customerListController.getStorage.remove(Constant.USER_TOKEN);
    customerListController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  @override
  void changeCustomerStatusBtnAction(
      {String? identifier,
      CustomerStatusDetail? customerStatus,
      CustomerDetail? custDetail,
      String? remark}) {
    Get.back();
    customerListController.changeCustomerStatusList(
        custDetail!.id!, customerStatus!.value!, remark);
  }

  locationPermissionStatus() async {
    if (Platform.isIOS) {
      getCurrentPosition(false);
    } else {
      PermissionService().requestLocationPermission(onPermissionSuccess: () {
        print("Location Service Permission approved");
        getCurrentPosition(false);
      }, onPermissionDenied: () async {
        print("Location Service Permission denied");
        getCurrentPosition(false);
      });
    }
  }

  getCurrentPosition(bool fromTryAgain) async {
    bool serviceEnabled = await checkLocationService();
    if (!serviceEnabled) {
      customerListController.setBtnClickEvent(true);
      locationSettingsDialog(false, fromTryAgain);
      return false;
    }
    LocationPermission permission = await geolocatorPlatform.checkPermission();
    if (permission == LocationPermission.denied) {
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        customerListController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }
    if (permission == LocationPermission.deniedForever) {
      // for app settings
      if (Platform.isIOS) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.deniedForever) {
          locationSettingsDialog(true, fromTryAgain);
          return false;
        }
      } else {
        customerListController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    customerListController.isLoading = true;
    customerListController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        customerListController.setBtnClickEvent(false);
        customerListController.isLoading = false;
        customerListController.update();
        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        customerListController.getNearByDevices(currentPosition);
      } else {
        customerListController.isLoading = false;
        customerListController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      customerListController.isLoading = false;
      customerListController.update();
      getCurrentPosition(false);
    });
  }

  Future<bool> checkLocationService() async {
    bool serviceEnabled;
    serviceEnabled = await geolocatorPlatform.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return false;
    } else {
      return true;
    }
  }

  locationSettingsDialog(bool isAppPermission, bool fromTryAgain) {
    if (!isAppPermission || fromTryAgain) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    } else if (isAppPermission && fromTryAgain) {
      showDialog(
          context: context,
          barrierDismissible: false,
          builder: (BuildContext context) {
            return LocationSettingsDialog(
                locationBtnAction: this,
                isAppPermission: isAppPermission,
                from: Constant.NEAR_BY_DEVICE);
          });
    }
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.try_again)) {
      getCurrentPosition(false);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.location_settings)) {
      geolocatorPlatform.openLocationSettings();
    } else if (btnIdentifier
        .equalsIgnoreCase(Strings.app_permission_settings)) {
      geolocatorPlatform.openAppSettings();
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
}
