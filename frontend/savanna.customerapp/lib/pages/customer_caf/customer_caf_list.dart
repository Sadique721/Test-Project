import 'dart:developer';
import 'package:savbill/main.dart';
import 'package:savbill/pages/customer/assign_inventory.dart';
import 'package:savbill/pages/customer/basic_details/customer_basic_details.dart';
import 'package:savbill/pages/customer/change_customer_status_dialog.dart';
import 'package:savbill/pages/customer/customer_document.dart';
import 'package:savbill/pages/customer/location_settings_dialog.dart';
import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
import 'package:savbill/pages/customer_caf/add_edit_caf_customer.dart';
import 'package:savbill/pages/customer_caf/basic_caf_details/customer_caf_basic_details.dart';
import 'package:savbill/pages/customer_caf/close_caf/close_caf_status_screen.dart';
import 'package:savbill/pages/customer_caf/customer_caf_detail/customer_caf_detail.dart';
import 'package:savbill/pages/customer_caf/customer_caf_list_controller.dart';
import 'package:savbill/pages/customer_caf/customer_caf_list_view_item.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_drop_down_res.dart';
import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class CustomerCafList extends StatefulWidget {
  @override
  _CustomerListState createState() => _CustomerListState();
}

class _CustomerListState extends State<CustomerCafList>
    with WidgetsBindingObserver
    implements
        LogoutClickEvent,
        ChangeCustomerStatusBtnAction,
        LocationBtnAction /*CafCustomerApproveRejectBtnAction*/ {
  final customerCafListController = Get.put(CustomerCafListController());
  final GlobalKey<ScaffoldState> _customerCafListKey = GlobalKey();
  GeolocatorPlatform geolocatorPlatform = GeolocatorPlatform.instance;

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    customerCafListController.setBtnClickEvent(false);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        //customerCafListController.setBtnClickEvent(false);
        return;
      case AppLifecycleState.resumed:
        if (customerCafListController.checkBtnClickEvent) {
          customerCafListController.setBtnClickEvent(false);
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
    customerCafListController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CustomerCafListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              key: _customerCafListKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: _body(),
            ),
          ),
          ProgressBar(isLoader: customerCafListController.isLoading),
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
                          if (customerCafListController.filterViewOpen) {
                            customerCafListController.filterViewOpen = false;
                          } else {
                            customerCafListController.filterViewOpen = true;
                          }
                          customerCafListController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: customerCafListController.isFilterApply
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
              customerCafListController.filterViewOpen
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
                                    decoration: Utils.ddlDecoration(),
                                    hint: Align(
                                        alignment: Alignment.centerLeft,
                                        child: Text(
                                            Strings.select_search_option,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerCafListController
                                        .selectedSearchCategory,
                                    items: customerCafListController
                                        .searchCategory!
                                        .map((CustomerSearchData value) {
                                      return DropdownMenuItem<
                                          CustomerSearchData>(
                                        value: value,
                                        child: Text(value.text!),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerCafListController
                                              .selectedSearchCategory =
                                          value as CustomerSearchData?;
                                      customerCafListController
                                          .selectedStatusList = null;
                                      if (value!.value!
                                              .equalsIgnoreCase("status") ||
                                          value.value!
                                              .equalsIgnoreCase("cafStatus")) {
                                        customerCafListController
                                            .getCustomerStatusList();
                                      }
                                      customerCafListController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                                ((customerCafListController
                                    .selectedSearchCategory !=
                                    null &&
                                    customerCafListController
                                        .selectedSearchCategory!.value!
                                        .equalsIgnoreCase(
                                        "currentAssigneeName"))) ? SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ): SizedBox.shrink(),
                                ((customerCafListController
                                                .selectedSearchCategory !=
                                            null &&
                                        customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "currentAssigneeName")))
                                    ? DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: Text(Strings.select_staff,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ))),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: customerCafListController
                                              .selectedCustomerAllStaffList,
                                          items: customerCafListController
                                              .customerAllStaffList!
                                              .map((CustomerCafDropDownStaffList
                                                  value) {
                                            return DropdownMenuItem<
                                                CustomerCafDropDownStaffList>(
                                              value: value,
                                              child: Text(value.username!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            customerCafListController
                                                    .selectedCustomerAllStaffList =
                                                value
                                                    as CustomerCafDropDownStaffList?;
                                            customerCafListController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      )
                                    : SizedBox.shrink(),
                                customerCafListController.selectedSearchCategory != null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("status") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("cafStatus") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("custtype") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "currentAssigneeName") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "cafCreatedDate") &&
                                        customerCafListController
                                                .selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "currentAssignedTeam") &&
                                        customerCafListController
                                                .selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("subscriptionMode")
                                    ? const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      )
                                    : SizedBox.shrink(),
                                customerCafListController.selectedSearchCategory != null &&
                                        !customerCafListController.selectedSearchCategory!.value!
                                            .equalsIgnoreCase("status") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController.selectedSearchCategory!.value!
                                            .equalsIgnoreCase("cafStatus") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController.selectedSearchCategory!.value!
                                            .equalsIgnoreCase("custtype") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController.selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "currentAssigneeName") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "cafCreatedDate") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "currentAssignedTeam") &&
                                        customerCafListController.selectedSearchCategory !=
                                            null &&
                                        !customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase(
                                                "subscriptionMode")
                                    ? CoustomTextField(
                                        labelText: Strings.enter_search_detail,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController: customerCafListController.searchController,
                                        borderEnableColors: AppTheme.colorIconGrey,
                                        borderFocusColors: AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding: const EdgeInsets.symmetric(horizontal: Constant.MEDIUM_PADDING),
                                        borderCorner: Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {},
                                        readOnly: false)
                                    : SizedBox.shrink(),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                ),
                                ((customerCafListController
                                                    .selectedSearchCategory !=
                                                null &&
                                            customerCafListController
                                                .selectedSearchCategory!.value!
                                                .equalsIgnoreCase("status")) ||
                                        (customerCafListController
                                                    .selectedSearchCategory !=
                                                null &&
                                            customerCafListController
                                                .selectedSearchCategory!.value!
                                                .equalsIgnoreCase("cafStatus")))
                                    ? DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: Text(Strings.select_status,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ))),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: customerCafListController
                                              .selectedStatusList,
                                          items: customerCafListController
                                              .statusList!
                                              .map(
                                                  (CustomerStatusDetail value) {
                                            return DropdownMenuItem<
                                                CustomerStatusDetail>(
                                              value: value,
                                              child: Align(
                                                alignment: Alignment.centerLeft,
                                                child: CustomText(
                                                  title: value.value!,
                                                  colors: AppTheme.colorBlack,
                                                  textAlign: TextAlign.start,
                                                  fontSize: AppTheme.small,
                                                  fontWeight: FontWeight.w500,
                                                ), //Text(value.desig!),
                                              ),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            customerCafListController
                                                    .selectedStatusList =
                                                value as CustomerStatusDetail?;
                                            customerCafListController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      )
                                    : SizedBox.shrink(),
                                ((customerCafListController
                                                    .selectedSearchCategory !=
                                                null &&
                                            customerCafListController
                                                .selectedSearchCategory!.value!
                                                .equalsIgnoreCase("status")) ||
                                        (customerCafListController
                                                    .selectedSearchCategory !=
                                                null &&
                                            customerCafListController
                                                .selectedSearchCategory!.value!
                                                .equalsIgnoreCase("cafStatus")))
                                    ? SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      )
                                    : SizedBox.shrink(),
                                ((customerCafListController
                                                .selectedSearchCategory !=
                                            null &&
                                        customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("custtype")))
                                    ? DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width: Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: Text(
                                                  Strings.select_customer_type,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ))),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: true,
                                          isDense: true,
                                          value: customerCafListController
                                              .selectedCustType,
                                          items: customerCafListController
                                              .custTypeList!
                                              .map((DropdownDetail value) {
                                            return DropdownMenuItem<
                                                DropdownDetail>(
                                              value: value,
                                              child: Align(
                                                alignment: Alignment.centerLeft,
                                                child: CustomText(
                                                  title: value.text!,
                                                  colors: AppTheme.colorBlack,
                                                  textAlign: TextAlign.start,
                                                  fontSize: AppTheme.small,
                                                  fontWeight: FontWeight.w500,
                                                ), //Text(value.desig!),
                                              ),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            customerCafListController
                                                    .selectedCustType =
                                                value as DropdownDetail?;
                                            customerCafListController.update();
                                          },
                                          validator: (value) {
                                            return null;
                                          },
                                        ),
                                      )
                                    : SizedBox.shrink(),
                                ((customerCafListController
                                                .selectedSearchCategory !=
                                            null &&
                                        customerCafListController
                                            .selectedSearchCategory!.value!
                                            .equalsIgnoreCase("custtype")))
                                    ? SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      )
                                    : SizedBox.shrink(),
                                CoustomTextField(
                                    labelText: Strings.date_format,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerCafListController
                                            .startDateController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    maxLength: 6,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      selectDate(
                                          context,
                                          Strings.start_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                    },
                                    readOnly: true),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                CoustomTextField(
                                    labelText: Strings.date_format,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerCafListController
                                            .endDateController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    maxLength: 6,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      selectDate(
                                          context,
                                          Strings.end_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                    },
                                    readOnly: true),
                                SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                                Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          customerCafListController
                                              .applyFilter();
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
                                          customerCafListController
                                              .clearFilter();
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
              customerCafListController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
              Expanded(
                flex: 1,
                child: (customerCafListController.customerList != null &&
                        customerCafListController.customerList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: customerCafListController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                customerCafListController.customerList!.length +
                                    1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  customerCafListController
                                      .customerList?.length) {
                                if (customerCafListController.isShowLoadMore) {
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
                                return CustomerCafListViewItem(
                                  index: index,
                                  custType: customerCafListController.type,
                                  item: customerCafListController
                                      .customerList![index],
                                  controller: customerCafListController,

                                  onTapNotes: () {
                                    customerCafListController
                                        .setBtnClickEvent(false);
                                    openNotesScreen(customerCafListController
                                        .customerList![index].id);
                                  },
                                  onTapEdit: () {
                                    customerCafListController
                                        .setBtnClickEvent(false);
                                    openBasicEditUpdateCustomerDetailsScreen(
                                        customerCafListController
                                            .customerList![index].id,
                                        customerCafListController
                                            .customerList![index]
                                            .serviceAreaId);
                                  },

                                  onTapApprove: () {
                                    customerCafListController
                                        .isCustomerDocumentPendingCall(
                                            customerCafListController
                                                .customerList![index].id,
                                            context,
                                            customerCafListController
                                                .customerList![index]);
                                    // addRemarkCafCustomerDialog(
                                    //     context,
                                    //     Strings.approve,
                                    //     customerCafListController
                                    //         .customerList![index]);
                                  },
                                  onTapReject: () {
                                    customerCafListController
                                        .addRemarkCafCustomerDialog(
                                            context,
                                            Strings.reject,
                                            customerCafListController
                                                .customerList![index]);
                                  },
                                  // onTapDelete: () {
                                  //   customerCafListController
                                  //       .setBtnClickEvent(false);
                                  //   showDialog(
                                  //     context: context,
                                  //     builder: (BuildContext context) {
                                  //       return AlertDialogHelper(
                                  //           title: Strings.app_name,
                                  //           message: Strings.msg_delete,
                                  //           positiveBtnText: Strings.yes,
                                  //           negativeBtnText: Strings.no,
                                  //           positiveBtnClick: () {
                                  //             Get.back();
                                  //             customerCafListController
                                  //                 .deleteCustomer(
                                  //                     customerCafListController
                                  //                         .customerList![
                                  //                             index]
                                  //                         .id,
                                  //                     index);
                                  //           },
                                  //           negativeBtnClick: () {
                                  //             Get.back();
                                  //           });
                                  //     },
                                  //   );
                                  // },

                                  onTapPick: () {
                                    customerCafListController.pickUpCreditNote(
                                        customerCafListController
                                            .customerList![index].id);
                                  },
                                  onTapCloseCaf: () {
                                    openCloseCafScreen(customerCafListController
                                        .customerList![index]);
                                  },
                                  onTapDocumentUpload: () {
                                    customerCafListController
                                        .setBtnClickEvent(false);
                                    openCustomerDocumentScreen(
                                        customerCafListController
                                            .customerList![index].id);
                                  },
                                  onTapNearByDevice: () {
                                    locationPermissionStatus();
                                  },
                                  // onTapSendPaymentLink: () {
                                  //   customerCafListController
                                  //       .setBtnClickEvent(false);
                                  //   customerCafListController
                                  //       .sendPaymentLinkToCustomer(
                                  //           customerCafListController
                                  //               .customerList![index].id);
                                  // },
                                  onTapAssignInventory: () {
                                    customerCafListController.entityId =
                                        customerCafListController
                                            .customerList![index].id;

                                    customerCafListController
                                        .reassignWorkflowGetStaff(
                                            customerCafListController
                                                .customerList![index].id,
                                            "CAF");
                                    // customerCafListController
                                    //     .setBtnClickEvent(false);
                                    // openAssignInventoryScreen(
                                    //     customerCafListController
                                    //         .customerList![index].id);
                                  },
                                  onTapReActivate: () {
                                    log("onTapReActivate-->CustomerId==>>${customerCafListController.customerList![index].id}");
                                    customerCafListController
                                        .onReActivateService(
                                            customerCafListController
                                                .customerList![index].id);
                                  },
                                  onTapCustomerInvoicePayment: () {
                                    customerCafListController
                                        .setBtnClickEvent(false);
                                    customerCafListController
                                        .customerInvoicePaymentLinkCall(
                                            customerCafListController
                                                .customerList![index].id,
                                            false);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              PermissionService().hasAclPermission([
                        customerCafListController.type
                                .equalsIgnoreCase('Prepaid')
                            ? AclPreCustConstants.CREATE_PRE_CUST_CAF_LIST
                            : AclPostCustConstants.CREATE_POST_CUST_CAF
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
                              title: customerCafListController.type
                                      .equalsIgnoreCase(Strings.prepaid)
                                  ? Strings.create_prepaid_caf_customer
                                  : Strings.create_postpaid_caf_customer,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w400,
                            ),
                          ),
                        )
                      ],
                    )
                  : SizedBox.shrink()
            ]),
      ),
    );
  }

  openAddCustomerScreen() async {
    var result = await Get.to(AddEditCafCustomer(), arguments: {
      Constant.ACTION: Strings.add,
      Constant.CUSTOMER_TYPE: customerCafListController.type,
    });
    if (result != null && result == true) {
      customerCafListController.clearFilter();
    }
  }

  openCustomerDocumentScreen(int? customerId) async {
    Get.to(CustomerDocumentList(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  openAssignInventoryScreen(int? customerId) async {
    Get.to(AssignInventory(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  openCustomerDetailScreen(int? customerId) async {
    Get.to(() => CustomerCafDetailScreen(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      Constant.CUSTOMER_TYPE: customerCafListController.type,
      Constant.CUST_APPROVAL: false,
    });
  }

  openBasicEditUpdateCustomerDetailsScreen(
    int? customerId,
    int? serviceAreaId,
  ) async {
    Get.to(CustomerCAFBasicDetails(), arguments: {
      Constant.CUSTOMER_ID: customerId,
      Constant.SERVICE_AREA_ID: serviceAreaId,
    });
  }

  noDataFound() {
    return const NoDataFound();
  }

  _onMenuClick() {
    if (_customerCafListKey.currentState!.isDrawerOpen) {
      _customerCafListKey.currentState?.closeDrawer();
    } else {
      _customerCafListKey.currentState?.openDrawer();
    }
  }

  _appBar() {
    return DynamicAppBar(
        "${customerCafListController.type} ${Strings.customers} ${Strings.caf_list}",
        '',
        AppTheme.colorPrimary,
        true,
        _onMenuClick,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void logoutClick() {
    customerCafListController.getStorage.remove(Constant.USER_DATA);
    customerCafListController.getStorage.remove(Constant.USER_TOKEN);
    customerCafListController.getStorage.remove(Constant.USER_SERVICES_AREA);
    Get.offAllNamed(AppRoutes.LOGIN);
  }

  @override
  void changeCustomerStatusBtnAction(
      {String? identifier,
      CustomerStatusDetail? customerStatus,
      CustomerDetail? custDetail,
      String? remark}) {
    Get.back();
    customerCafListController.changeCustomerStatusList(
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
      customerCafListController.setBtnClickEvent(true);
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
        customerCafListController.setBtnClickEvent(true);
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
        customerCafListController.setBtnClickEvent(true);
        locationSettingsDialog(true, fromTryAgain);
        return false;
      }
    }

    customerCafListController.isLoading = true;
    customerCafListController.update();

    LocationSettings settings = const LocationSettings(
        accuracy: LocationAccuracy.bestForNavigation,
        timeLimit: Duration(seconds: 20));
    geolocatorPlatform
        .getCurrentPosition(
      locationSettings: settings,
    )
        .then((position) {
      if (position != null) {
        customerCafListController.setBtnClickEvent(false);
        customerCafListController.isLoading = false;
        customerCafListController.update();
        Position currentPosition = position;
        print(
            "Location :- ${currentPosition.latitude}, ${currentPosition.longitude}");
        customerCafListController.getNearByDevices(currentPosition);
      } else {
        customerCafListController.isLoading = false;
        customerCafListController.update();
        getCurrentPosition(false);
      }
    }).catchError((error) {
      customerCafListController.isLoading = false;
      customerCafListController.update();
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

  openNotesScreen(int? customerId) async {
    showDialog(
      context: Get.context!,
      barrierDismissible: false,
      builder: (context) => CustomEditNoteDialog(
        title: Strings.addNotes,
        controller: customerCafListController.notesController,
        onSave: () {
          customerCafListController.addNoteCallApi(customerId);
          customerCafListController.notesController.clear();
          Get.back();
        },
        onCancel: () {
          Get.back();
        },
      ),
    );
  }

  // @override
  // void closeCafStatusBtnAction({String? identifier, String? remark}) {
  //   Get.back();
  //   // customerCafListController.changeCustomerStatusList(
  //   //     custDetail!.id!, customerStatus!.value!,remark);
  // }

  openCloseCafScreen(CustomerDetail? customerDetail) async {
    var result = await Get.to(CloseCafStatusScreen(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetail,
    });

    if (result != null && result == true) {
      // cafFollowUpController.getCustomerCafFollowUPData();
    }
  }

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;

    if (identity == Strings.start_date) {
      if (customerCafListController.selectedStartDate != null) {
        selectedDate = customerCafListController.selectedStartDate;
      } else {
        selectedDate = DateTime.now();
      }
    } else if (identity == Strings.end_date) {
      if (customerCafListController.selectedEndDate != null) {
        selectedDate = customerCafListController.selectedEndDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: selectedDate!,
      firstDate: firstDate,
      lastDate: lastDate,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      builder: (BuildContext? context, Widget? child) {
        return Theme(
          data: ThemeData.light().copyWith(
            primaryColor: AppTheme.colorPrimary,
            colorScheme: ColorScheme.light(primary: AppTheme.colorPrimary),
            buttonTheme:
                const ButtonThemeData(textTheme: ButtonTextTheme.primary),
          ),
          child: child!,
        );
      },
    );
    if (picked != null) {
      if (identity == Strings.start_date) {
        customerCafListController.selectedStartDate = picked;
        customerCafListController.startDateController.text =
            customerCafListController.apiDateFormat.format(picked);
      } else if (identity == Strings.end_date) {
        customerCafListController.selectedEndDate = picked;
        customerCafListController.endDateController.text =
            customerCafListController.apiDateFormat.format(picked);
      }
      customerCafListController.update();
    }
  }

// addRemarkCafCustomerDialog(
//     BuildContext context, String? pageName, CustomerDetail? customerDetail) {
//   showDialog(
//       context: context,
//       barrierDismissible: true,
//       builder: (BuildContext context) {
//         return CafCustomerApproveRejectDialog(
//           pageName: pageName,
//           cafCustomerApproveRejectBtnAction: this,
//           customerDetail: customerDetail,
//           // caseId: item.caseId,
//         );
//       });
// }

// @override
// void cafCustomerApproveRejectStatus(
//     {String? identifier,
//     TextEditingController? remarkController,
//       BuildContext? context,
//     CustomerDetail? customerDetail}) {
//   Get.back();
//   if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
//     customerCafListController.approveRejectCafCustomer(
//         status: Strings.approved.toLowerCase(),
//         remark: remarkController!.text,
//         customerDetail: customerDetail,
//         context: context!);
//   } else if (identifier != null &&
//       identifier.equalsIgnoreCase(Strings.reject)) {
//     customerCafListController.approveRejectCafCustomer(
//         status: Strings.rejected.toLowerCase(),
//         remark: remarkController!.text,
//         customerDetail: customerDetail,
//         context: context!);
//   }
// }
}
