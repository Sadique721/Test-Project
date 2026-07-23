
import 'dart:developer';

import 'package:savbill/pages/drawer/side_drawer.dart';
import 'package:savbill/pages/lead_management/add_edit_lead/add_edit_lead_screen.dart';
import 'package:savbill/pages/lead_management/close_lead/close_lead_screen.dart';
import 'package:savbill/pages/lead_management/create_lead_screen.dart';
import 'package:savbill/pages/lead_management/lead_add_note_dialog.dart';
import 'package:savbill/pages/lead_management/lead_doc/view_lead_doc.dart';
import 'package:savbill/pages/lead_management/lead_status/lead_status_screen.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/lead_management/view_lead_controller.dart';
import 'package:savbill/pages/lead_management/view_lead_item.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/acl_constant.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewLead extends StatefulWidget {
  @override
  _ViewLeadState createState() => _ViewLeadState();
}

class _ViewLeadState extends State<ViewLead>
    implements LogoutClickEvent, LeadAddNoteBtnAction {
  final viewLeadController = Get.put(ViewLeadController());
  final GlobalKey<ScaffoldState> leadSystemHomeKey = GlobalKey();

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
    viewLeadController.moveToDashboard();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewLeadController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            appBar: _appBar(),
            body: Scaffold(
              resizeToAvoidBottomInset: false,
              key: leadSystemHomeKey,
              drawer: SideDrawer(logoutClickEvent: this),
              backgroundColor: AppTheme.colorBG,
              body: _body(),
            ),
          ),
          ProgressBar(isLoader: viewLeadController.isLoading),
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
                          title: Strings.lead_list,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewLeadController.filterViewOpen) {
                            viewLeadController.filterViewOpen = false;
                          } else {
                            viewLeadController.filterViewOpen = true;
                          }
                          viewLeadController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: viewLeadController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ]),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              viewLeadController.filterViewOpen
                  ? Container(
                      width: MediaQuery.of(context).size.width,
                      margin: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: Material(
                        color: AppTheme.colorWhite,
                        elevation: 1.5,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(
                                Constant.BTN_ROUNDED_CORNER - 2)),
                        child: Padding(
                          padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
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
                                      ),
                                    ),
                                  ),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: viewLeadController.selectSearchOption,
                                  items: viewLeadController
                                      .leadSearchOptionList!
                                      .map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    viewLeadController.selectSearchOption =
                                        value as DropdownDetail?;
                                    log("LeadStatus===>${viewLeadController.selectSearchOption!.type}");
                                    viewLeadController.update();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ),

                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              (viewLeadController.selectSearchOption != null &&
                                  viewLeadController.selectSearchOption!.type!.equalsIgnoreCase("status")) ?
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
                                      Strings.select_status,
                                      style: TextStyle(
                                        fontSize: AppTheme.medium,
                                        color: AppTheme.colorIconGrey,
                                        fontFamily: AppTheme.appFontName,
                                      ),
                                    ),
                                  ),
                                  style: AppTheme.dropdownTextStyle,
                                  isExpanded: true,
                                  isDense: true,
                                  value: viewLeadController.selectStatusOption,
                                  items: viewLeadController
                                      .leadStatusOptionList!
                                      .map((DropdownDetail value) {
                                    return DropdownMenuItem<DropdownDetail>(
                                      value: value,
                                      child: Text(value.text!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    viewLeadController.selectStatusOption =
                                    value as DropdownDetail?;
                                    viewLeadController.update();
                                  },
                                  validator: (value) {
                                    return null;
                                  },
                                ),
                              ) :
                              (viewLeadController.selectSearchOption != null &&
                                  viewLeadController.selectSearchOption!.type!.equalsIgnoreCase("lastUpdateOn")) ? CoustomTextField(
                                  labelText: Strings.select_date,
                                  suffixIcon: Padding(
                                    padding:
                                    const EdgeInsetsDirectional.all(
                                        Constant.MEDIUM_PADDING),
                                    child: SvgPicture.asset(
                                      calendarSvg,
                                      color: AppTheme.colorBlack,
                                      width: Constant.ICON_SIZE_S,
                                      height: Constant.ICON_SIZE_S,
                                      // myIcon is a 48px-wide widget.
                                    ),
                                  ),
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: viewLeadController.lastUpdateDateController,
                                  borderEnableColors:
                                  AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  fontSize: AppTheme.small,
                                  fontWeight: FontWeight.w500,
                                  contentPadding:
                                  const EdgeInsets.symmetric(
                                      horizontal:
                                      Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {},
                                  onTextFiledOnTap: () {
                                    selectDate(
                                        context,
                                        Strings.last_update_date,
                                        DateTime(DateTime.now().year - 10),
                                        DateTime(DateTime.now().year + 10));
                                  },
                                  readOnly: true) : CoustomTextField(
                                  labelText: Strings.enter_search_detail,
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController:
                                      viewLeadController.searchDetailController,
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.emailAddress,
                                  fontSize: AppTheme.small,
                                  textInputAction: TextInputAction.next,
                                  fontWeight: FontWeight.w500,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.please_enter_value;
                                    }
                                    return null;
                                  },
                                  onTextFiledOnTap: () {},
                                  readOnly: false),
                              const SizedBox(height: Constant.SMALL_PADDING,),
                              viewLeadController.selectStatusOption!= null && viewLeadController.selectStatusOption!.text!.equalsIgnoreCase("Converted") ? Row(
                                mainAxisAlignment: MainAxisAlignment.start,
                                children: [
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.selectConvertedDate,
                                        suffixIcon: Padding(
                                          padding:
                                          const EdgeInsetsDirectional.all(
                                              Constant.MEDIUM_PADDING),
                                          child: SvgPicture.asset(
                                            calendarSvg,
                                            color: AppTheme.colorBlack,
                                            width: Constant.ICON_SIZE_S,
                                            height: Constant.ICON_SIZE_S,
                                            // myIcon is a 48px-wide widget.
                                          ),
                                        ),
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController: viewLeadController.leadConvertedDateController,
                                        borderEnableColors:
                                        AppTheme.colorIconGrey,
                                        borderFocusColors: AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                        const EdgeInsets.symmetric(
                                            horizontal:
                                            Constant.MEDIUM_PADDING),
                                        borderCorner: Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {
                                          selectDate(
                                              context,
                                              Strings.convertedDate,
                                              DateTime(DateTime.now().year - 10),
                                              DateTime(DateTime.now().year + 10));
                                        },
                                        readOnly: true),
                                  ),
                                  const SizedBox(width: Constant.SMALL_PADDING,),
                                  Expanded(
                                    child: CoustomTextField(
                                        labelText: Strings.select_credit_to_date,
                                        suffixIcon: Padding(
                                          padding:
                                          const EdgeInsetsDirectional.all(
                                              Constant.MEDIUM_PADDING),
                                          child: SvgPicture.asset(
                                            calendarSvg,
                                            color: AppTheme.colorBlack,
                                            width: Constant.ICON_SIZE_S,
                                            height: Constant.ICON_SIZE_S,
                                            // myIcon is a 48px-wide widget.
                                          ),
                                        ),
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                        viewLeadController.leadCreditDateController,
                                        borderEnableColors:
                                        AppTheme.colorIconGrey,
                                        borderFocusColors: AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                        const EdgeInsets.symmetric(
                                            horizontal:
                                            Constant.MEDIUM_PADDING),
                                        borderCorner: Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {},
                                        onTextFiledOnTap: () {
                                          selectDate(
                                              context,
                                              Strings.credit_to_date,
                                              DateTime(DateTime.now().year - 10),
                                              DateTime(DateTime.now().year + 10));
                                        },
                                        readOnly: true),
                                  ),
                                ],
                              ) : const SizedBox.shrink(),

                              const SizedBox(
                                height: Constant.SMALL_PADDING,
                              ),
                              Row(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Expanded(
                                      child: SimpleButton(
                                        onTap: () {
                                          viewLeadController.applyFilter();
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
                                          viewLeadController.clearFilter();
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
                                  ]),
                            ],
                          ),
                        ),
                      ),
                    )
                  : Container(),
              viewLeadController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : Container(),
              Expanded(
                flex: 1,
                child: (viewLeadController.leadMasterList != null &&
                        viewLeadController.leadMasterList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewLeadController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount:
                                viewLeadController.leadMasterList!.length +
                                    1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewLeadController
                                      .leadMasterList?.length) {
                                if (viewLeadController.isShowLoadMore) {
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
                                LeadMasterListData item = viewLeadController
                                    .leadMasterList![index];
                                return ViewLeadItem(
                                  item: item,
                                  controller: viewLeadController,
                                  userid:
                                      viewLeadController.userDetail!.userId!,
                                  onTapAddNotes: () {
                                    addNoteLeadDialog(
                                        context, Strings.save, item.id);
                                  },
                                  onTapEdit: () {
                                    openCreateAddLeadScreen(Strings.edit,item);
                                    // Utils.showSnackbar(
                                    //     Strings.SUCCESS,
                                    //     Strings.under_development,
                                    //     AppTheme.colorWhite,
                                    //     AppTheme.colorGreen);
                                  },
                                  onTapApproveLead: () {
                                    viewLeadController.getLeadAllRejectedReason(
                                        context,
                                        Strings.approve,
                                        item,
                                        viewLeadController);
                                  },
                                  onTapRejectLead: () {
                                    viewLeadController.getLeadAllRejectedReason(
                                        context,
                                        Strings.reject,
                                        item,
                                        viewLeadController);
                                  },
                                  onTapPickLead: () {
                                    viewLeadController.pickUpLead(item.id);
                                  },
                                  onTapCloseLead: () async {
                                    await openCloseLeadStatusScreen(
                                        item, Strings.closeLead);
                                  },
                                  onTapReOpenLead: (){
                                    showExitDialog(item.id);
                                  },
                                  onTapReassignLead: () async{
                                    await openCloseLeadStatusScreen(
                                    item, Strings.reassignLead);
                                  },
                                  onTapLeadStatus: (){
                                    openLeadStatusWorkFlowScreen(item.id);
                                  },
                                  onTapLeadDocument: (){
                                    openCustomerDocumentScreen(item.id);
                                  },
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
              PermissionService().hasAclPermission([AclSalesCRMs.CREATE_LEAD]) == true ? Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                    onTap: () {
                      openCreateAddLeadScreen(Strings.add,null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.createLead,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ))
                ],
              ): SizedBox.shrink()
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  openCloseLeadStatusScreen(LeadMasterListData? leadMasterId, String? pageTitle) async {
    var result = await Get.to(() => CloseLeadScreen(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
      Constant.PAGE_TITLE: pageTitle,
    });
    if (result != null && result == true) {
      viewLeadController.getLeadManagement();
    }
  }


  openCreateAddLeadScreen(String? from, LeadMasterListData? leadViewContentData) async {
    var result = await Get.to(() => AddEditLeadScreen(), arguments: {
      Constant.FROM: from,
      Constant.LEAD_DETAIL: leadViewContentData,
    });
    if (result != null && result == true) {
      viewLeadController.getLeadManagement();
    }
  }



  showExitDialog(int? leadId) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.leadReOpenConfirmation,
            message: Strings.reopenLeadMsg,
            positiveBtnText: Strings.yes,
            negativeBtnText: Strings.no,
            positiveBtnClick: () {
              Navigator.pop(context);
              viewLeadController.getReOpenLead(leadId);
            },
            negativeBtnClick: () {
              Navigator.pop(context);
            });
      },
    );
  }

  _appBar() {
    return DynamicAppBar(Strings.lead_management, '', AppTheme.colorPrimary,
        true, _onMenuClick, [], AppBar().preferredSize.height);
  }

  _onMenuClick() {
    if (leadSystemHomeKey.currentState!.isDrawerOpen) {
      leadSystemHomeKey.currentState?.closeDrawer();
    } else {
      leadSystemHomeKey.currentState?.openDrawer();
    }
  }

  @override
  void logoutClick() {
    viewLeadController.getStorage.remove(Constant.USER_DATA);
    viewLeadController.getStorage.remove(Constant.USER_TOKEN);
    viewLeadController.getStorage.remove(Constant.USER_SERVICES_AREA);
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


  openCustomerDocumentScreen(int? customerId) async {
    Get.to(ViewLeadDoc(), arguments: {
      Constant.CUSTOMER_ID: customerId,
    });
  }

  openLeadStatusWorkFlowScreen(int? leadMasterId) async {
    var result = await Get.to(LeadStatusWorkFlow(), arguments: {
      Constant.LEAD_MASTER_ID: leadMasterId,
    });
    if (result != null && result == true) {
      // customerCafDetailController.getCustomerDetail();
    }
  }

  addNoteLeadDialog(BuildContext context, String? pageName, int? leadMasterId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return AddLeadNoteDialog(
            pageName: pageName,
            leadMasterId: leadMasterId,
            leadAddNoteBtnAction: this,
          );
        });
  }

  @override
  void addLeadNoteDetails(
      {String? identifier,
      TextEditingController? remarkController,
      int? leadMasterId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.save)) {
      viewLeadController.addMasterLeadNotes(
          leadMasterId, remarkController!.text);
    }
  }


  Future<void> selectDate(
      BuildContext context,
      String identity,
      DateTime firstDate,
      DateTime lastDate,
      ) async {
    DateTime? selectedDate;
    if (identity == Strings.convertedDate) {
      if (viewLeadController.selectedLeadFromDate != null) {
        selectedDate = viewLeadController.selectedLeadFromDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.credit_to_date) {
      if (viewLeadController.selectedLeadToDate != null) {
        selectedDate = viewLeadController.selectedLeadToDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.last_update_date) {
      if (viewLeadController.selectedLastUpdateDate != null) {
        selectedDate = viewLeadController.selectedLastUpdateDate;
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
    if (picked != null && picked != selectedDate) {
      if (identity == Strings.convertedDate) {
        viewLeadController.selectedLeadFromDate = picked;
        viewLeadController.leadConvertedDateController.text =
            viewLeadController.dateFormat.format(picked);
        viewLeadController.selectedLeadFromDateApi =
            viewLeadController.apiDateFormat.format(picked);
      }
      if (identity == Strings.credit_to_date) {
        viewLeadController.selectedLeadToDate = picked;
        viewLeadController.leadCreditDateController.text =
            viewLeadController.dateFormat.format(picked);
        viewLeadController.selectedLeadToDateApi =
            viewLeadController.apiDateFormat.format(picked);
      }
      if (identity == Strings.last_update_date) {
        viewLeadController.selectedLastUpdateDate = picked;
        viewLeadController.lastUpdateDateController.text =
            viewLeadController.dateFormat.format(picked);
        viewLeadController.selectedLastUpdateDateApi =
            viewLeadController.apiDateFormat.format(picked);
      }
      viewLeadController.update();
    }
  }
}
