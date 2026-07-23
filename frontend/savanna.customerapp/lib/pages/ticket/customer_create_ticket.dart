import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/credit_note/credit_customer_list.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/pages/ticket/customer_create_ticket_controller.dart';
import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_source_type_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/cust_service_area_ticket.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/permission_service.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/file_grid_item.dart';
import 'package:savbill/widgets/image_option_dialog.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/permisstion_deny_dialog.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

class CustomerCreateTicket extends StatefulWidget {
  @override
  _CreateTicketState createState() => _CreateTicketState();
}

class _CreateTicketState extends State<CustomerCreateTicket>
    with WidgetsBindingObserver
    implements
        ImageOptionBtnAction,
        PermissionDenyBtnAction,
        CustServiceAreaAction {
  final customerTicketController = Get.put(CustomerCreateTicketController());
  final addTicketFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CustomerCreateTicketController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: customerTicketController.isLoading),
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
        color: AppTheme.colorBG,
        width: MediaQuery.of(context).size.width,
        child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Expanded(
                child: SingleChildScrollView(
                  child: Padding(
                    padding: const EdgeInsets.only(
                        left: Constant.SCREEN_PADDING,
                        right: Constant.SCREEN_PADDING),
                    child: Form(
                      key: addTicketFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.case_title, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.case_title,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .caseTitleController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.next,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings.enter_case_title;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.customer, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.parent_customer,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .customerController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.service, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.select_service,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .serviceController,
                                    suffixIcon: Padding(
                                      padding: const EdgeInsetsDirectional.all(
                                          Constant.LARGE_PADDING - 2),
                                      child: SvgPicture.asset(
                                        downArrowSvg,
                                        color: AppTheme.colorBlack,
                                        width: Constant.ICON_SIZE_S,
                                        height: Constant.ICON_SIZE_S,
                                      ),
                                    ),
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.done,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings.please_select_service;
                                      } else {}
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      showServicesAreaSelectionDialog(
                                          Strings.service);
                                    },
                                    readOnly: true),

                                /*IgnorePointer(
                                  ignoring:
                                      customerTicketController.ticketDetail != null
                                          ? true
                                          : false,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                          fillColor: customerTicketController
                                                      .ticketDetail !=
                                                  null
                                              ? Colors.black12
                                              : AppTheme.colorWhite),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.customer,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value:
                                          customerTicketController.selectedCustomer,
                                      items: customerTicketController.customerList!
                                          .map((CustomerDetail value) {
                                        return DropdownMenuItem<CustomerDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.username!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        customerTicketController.selectedCustomer =
                                            value as CustomerDetail?;
                                        customerTicketController.update();

                                        customerTicketController.getCustomerDetail();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            customerTicketController
                                                    .selectedCustomer ==
                                                null) {
                                          return Strings.please_select_customer;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),*/
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: "${Strings.register_mobile_no}",
                                    require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.mobile_number,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .registerMobileNoController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.additional_mobile_no,
                                    require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.additional_mobile_no,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .mobileNoController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.number,
                                    maxLength: 15,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: "${Strings.register_email}",
                                    require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.email,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .registerEmailController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.additional_email,
                                    require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.additional_email,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .emailController,
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
                                      if (value!.isNotEmpty &&
                                          !value.isValidEmail()) {
                                        return Strings.enter_valid_email;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.serial_no, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.select_serial_no,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .custSerialNumberController,
                                    suffixIcon: Padding(
                                      padding: const EdgeInsetsDirectional.all(
                                          Constant.LARGE_PADDING - 2),
                                      child: SvgPicture.asset(
                                        downArrowSvg,
                                        color: AppTheme.colorBlack,
                                        width: Constant.ICON_SIZE_S,
                                        height: Constant.ICON_SIZE_S,
                                      ),
                                    ),
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.done,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      showServicesAreaSelectionDialog(
                                          Strings.serial_no);
                                    },
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.username, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.username,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .cusUsernameController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.service_area, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.service_area,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .cusServiceAreaController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fillColor: Colors.black12,
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
                                    onTextFiledOnTap: () {},
                                    readOnly: true),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.type, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                        child: Text(Strings.type,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedCaseType,
                                    items: customerTicketController
                                        .caseTypeList!
                                        .map((CaseTypeDetail value) {
                                      return DropdownMenuItem<CaseTypeDetail>(
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
                                      customerTicketController
                                              .selectedCaseType =
                                          value as CaseTypeDetail?;
                                      customerTicketController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          customerTicketController
                                                  .selectedCaseType ==
                                              null) {
                                        return Strings.enter_case_type;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.ticket_type, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                        child: Text(Strings.ticket_type,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedDepartment,
                                    items: customerTicketController
                                        .departmentTypeList!
                                        .map((DepartmentType value) {
                                      return DropdownMenuItem<DepartmentType>(
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
                                      customerTicketController
                                              .selectedDepartment =
                                          value as DepartmentType?;
                                      customerTicketController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          customerTicketController
                                                  .selectedDepartment ==
                                              null) {
                                        return Strings.select_ticket_type;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.ticket_problem_domain,
                                    require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                            Strings.ticket_problem_domain,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedReasonCategoryData,
                                    items: customerTicketController
                                        .reasonCategoryDataList!
                                        .map((ReasonCategoryDataList value) {
                                      return DropdownMenuItem<
                                          ReasonCategoryDataList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.categoryName!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerTicketController
                                              .selectedReasonCategoryData =
                                          value as ReasonCategoryDataList?;
                                      customerTicketController.update();
                                      customerTicketController.getSubCategory(
                                          customerTicketController
                                              .selectedReasonCategoryData!.id);
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          customerTicketController
                                                  .selectedReasonCategoryData ==
                                              null) {
                                        return Strings
                                            .select_ticket_problem_domain;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.ticket_sub_problem_domain,
                                    require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                            Strings.ticket_sub_problem_domain,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedSubProblemDomain,
                                    items: customerTicketController
                                        .subProblemDomainList!
                                        .map((SubProblemDomainDetail value) {
                                      return DropdownMenuItem<
                                          SubProblemDomainDetail>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.subCategoryName!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerTicketController
                                              .selectedSubProblemDomain =
                                          value as SubProblemDomainDetail?;
                                      customerTicketController.update();
                                      customerTicketController.setReasonData();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          customerTicketController
                                                  .selectedSubProblemDomain ==
                                              null) {
                                        return Strings
                                            .select_ticket_sub_problem_domain;
                                      }
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.ticket_reason,
                                    require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                        child: Text(Strings.ticket_reason,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedReasonMapping,
                                    items: customerTicketController
                                        .ticketReasonMappingList!
                                        .map(
                                            (TicketSubCategoryGroupReasonMappingList
                                                value) {
                                      return DropdownMenuItem<
                                          TicketSubCategoryGroupReasonMappingList>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.reason!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerTicketController
                                              .selectedReasonMapping =
                                          value
                                              as TicketSubCategoryGroupReasonMappingList?;
                                      customerTicketController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.priority, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: true,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                        fillColor: Colors.black12,
                                      ),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.priority,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: customerTicketController
                                          .selectedTicketPriority,
                                      items: customerTicketController
                                          .ticketPriorityList!
                                          .map((TicketPriority value) {
                                        return DropdownMenuItem<TicketPriority>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        customerTicketController
                                                .selectedTicketPriority =
                                            value as TicketPriority?;
                                        customerTicketController.update();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            customerTicketController
                                                    .selectedTicketPriority ==
                                                null) {
                                          return Strings.select_priority;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.root_cause, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: true,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                        fillColor: Colors.black12,
                                      ),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.root_cause,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: customerTicketController
                                          .selectedRootCause,
                                      items: customerTicketController
                                          .rootCauseList!
                                          .map((String value) {
                                        return DropdownMenuItem<String>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        customerTicketController
                                                .selectedRootCause =
                                            value as String?;
                                        customerTicketController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.resolution, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring: true,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                        fillColor: Colors.black12,
                                      ),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.resolution,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: customerTicketController
                                          .selectedResolution,
                                      items: customerTicketController
                                          .resolutionList!
                                          .map((String value) {
                                        return DropdownMenuItem<String>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        customerTicketController
                                                .selectedResolution =
                                            value as String?;
                                        customerTicketController.update();
                                      },
                                      validator: (value) {
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.source, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                        child: Text(Strings.source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedSourceType,
                                    items: customerTicketController
                                        .ticketSourceTypeList!
                                        .map((TicketSourceType value) {
                                      return DropdownMenuItem<TicketSourceType>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.text!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerTicketController
                                              .selectedSourceType =
                                          value as TicketSourceType?;
                                      customerTicketController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.sub_source, require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
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
                                        child: Text(Strings.sub_source,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ))),
                                    style: AppTheme.dropdownTextStyle,
                                    isExpanded: true,
                                    isDense: true,
                                    value: customerTicketController
                                        .selectedSubSourceType,
                                    items: customerTicketController
                                        .ticketSourceTypeList!
                                        .map((TicketSourceType value) {
                                      return DropdownMenuItem<TicketSourceType>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.text!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      customerTicketController
                                              .selectedSubSourceType =
                                          value as TicketSourceType?;
                                      customerTicketController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.status, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: IgnorePointer(
                                  ignoring:
                                      customerTicketController.ticketDetail !=
                                              null
                                          ? true
                                          : false,
                                  child: DropdownButtonHideUnderline(
                                    child: DropdownButtonFormField(
                                      icon: SvgPicture.asset(
                                        downArrowSvg,
                                        height: Constant.DROP_DOWN_ARROW_W_H,
                                        width: Constant.DROP_DOWN_ARROW_W_H,
                                        color: AppTheme.colorBlack,
                                        fit: BoxFit.fill,
                                      ),
                                      decoration: Utils.ddlDecoration(
                                          fillColor: customerTicketController
                                                      .ticketDetail !=
                                                  null
                                              ? Colors.black12
                                              : AppTheme.colorWhite),
                                      hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(Strings.status,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ))),
                                      style: AppTheme.dropdownTextStyle,
                                      isExpanded: true,
                                      isDense: true,
                                      value: customerTicketController
                                          .selectedCaseStatus,
                                      items: customerTicketController
                                          .caseStatusList!
                                          .map((CaseStatusDetail value) {
                                        return DropdownMenuItem<
                                            CaseStatusDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        customerTicketController
                                                .selectedCaseStatus =
                                            value as CaseStatusDetail?;
                                        customerTicketController.update();
                                      },
                                      validator: (value) {
                                        if (customerTicketController
                                                    .ticketDetail ==
                                                null &&
                                            (value == null ||
                                                customerTicketController
                                                        .selectedCaseStatus ==
                                                    null)) {
                                          return Strings.select_status;
                                        }
                                        return null;
                                      },
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              Flexible(
                                flex: 1,
                                child: InputTitleRequire(
                                    title: Strings.remarks, require: true),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.remarks,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        customerTicketController
                                            .remarksController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    keyboardType: TextInputType.text,
                                    fontSize: AppTheme.small,
                                    textInputAction: TextInputAction.done,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING,
                                        vertical: Constant.MEDIUM_PADDING),
                                    borderCorner: Constant.BTN_ROUNDED_CORNER,
                                    onTextValidator: (String? value) {
                                      if (value!.isEmpty) {
                                        return Strings.please_enter_remarks;
                                      }
                                      return null;
                                    },
                                    onTextFiledOnTap: () {},
                                    readOnly: false),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          GestureDetector(
                            onTap: () {
                              checkCameraPermission();
                            },
                            child: Row(
                              mainAxisSize: MainAxisSize.max,
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                Icon(
                                  Icons.add_circle_outline_rounded,
                                  color: AppTheme.title_dark,
                                  size: 18,
                                ),
                                CustomText(
                                  title: " ${Strings.select_file} :",
                                  colors: AppTheme.title_dark,
                                  textAlign: TextAlign.center,
                                  fontSize: AppTheme.small + 1,
                                  fontWeight: FontWeight.w500,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          fileViewWidget(),
                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateForm();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.save,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  _appBar() {
    return DynamicAppBar(
        customerTicketController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_ticket
            : Strings.create_ticket,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (addTicketFormKey.currentState!.validate()) {
      customerTicketController.createTicketApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  checkCameraPermission() async {
    PermissionService().requestCameraAndStoragePermission(
        onPermissionDenied: () {
      if (Platform.isIOS) {
        uploadImageOption();
      } else {
        permissionDenyDialog();
      }
    }, onPermissionSuccess: () {
      uploadImageOption();
    });
  }

  void uploadImageOption() async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ImageOptionDialog(
              imageOptionBtnAction: this,
              showFileSelect: true,
              showCameraSelect: false);
        });
  }

  void permissionDenyDialog() async {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PermissionDenyDialog(
              permissionDenyBtnAction: this,
              titleMsg: Strings.camera_storage_permission_denied_msg);
        });
  }

  @override
  void btnClickAction({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.app_permission_settings)) {
      customerTicketController.setBtnClickEvent(true);
      openAppSettings();
    }
  }

  @override
  void imageOptionSelection({String? btnIdentifier}) {
    Get.back();
    if (btnIdentifier!.equalsIgnoreCase(Strings.take_photo)) {
      openCameraGallery(ImageSource.camera);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.choose_from_gallery)) {
      openCameraGallery(ImageSource.gallery);
    } else if (btnIdentifier.equalsIgnoreCase(Strings.pdf_or_xl)) {
      openFilePicker();
    }
  }

  openFilePicker() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles(
      allowMultiple: false,
      type: FileType.custom,
      allowedExtensions: ['pdf', 'xlsx', "xls"],
    );
    if (result != null && result.files.isNotEmpty) {
      num size = await Utils.getFileSize(result.files.single.path!, 1);
      if (size <= 500) {
        customerTicketController.fileDetail = FileDetail(
            fileName: result.files.single.name,
            filePath: "",
            filePathLocal: result.files.single.path!,
            isFileLocal: true,
            fileType: result.files.single.extension);
      } else {
        Utils.showSnackbar(
            Strings.ERROR,
            "Your file size is very large, please select up to 500kb file size.",
            AppTheme.colorWhite,
            AppTheme.colorRed);
      }
    }
    customerTicketController.update();
  }

  openCameraGallery(ImageSource source) async {
    try {
      XFile? image;
      image = await imagePicker.pickImage(source: source);

      if (image != null && !image.path.isNullOrEmpty()) {
        num size = await Utils.getFileSize(image.path, 1);
        print("image picker file size : ${size}");
        if (size <= 500) {
          customerTicketController.fileDetail = FileDetail(
              fileName: image.name,
              filePath: "",
              filePathLocal: image.path,
              isFileLocal: true,
              fileType: Strings.image);
        } else {
          Utils.showSnackbar(
              Strings.ERROR,
              "Your file size is very large, please select up to 500kb file size.",
              AppTheme.colorWhite,
              AppTheme.colorRed);
        }
      }
      customerTicketController.update();
    } catch (e) {
      print("image picker exception : $e");
    }
  }

  fileViewWidget() {
    return customerTicketController.fileDetail != null
        ? FileGridItem(
            fileDetail: customerTicketController.fileDetail!,
            onTapItem: () {},
            bottomAction: fileItemAction(),
          )
        : Container();
  }

  fileItemAction() {
    return customerTicketController.fileDetail != null &&
            customerTicketController.fileDetail!.isFileLocal == true
        ? Align(
            alignment: Alignment.topRight,
            child: InkWell(
              onTap: () {
                customerTicketController.fileDetail = null;
                customerTicketController.update();
              },
              child: Container(
                height: 22,
                width: 22,
                decoration: BoxDecoration(
                  color: AppTheme.colorRed,
                  border: Border.all(
                    color: AppTheme.colorWhite,
                  ),
                  borderRadius: BorderRadius.circular(30.0),
                ),
                child: Center(
                  child: Icon(
                    Icons.close,
                    color: AppTheme.colorWhite,
                    size: 14,
                  ),
                ),
              ),
            ))
        : Container();
  }

  openParentCustomerScreen() async {
    var result = await Get.to(CreditCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      if (data != null) {
        customerTicketController.selectedCust = data;
        log("openParentCustomerScreen>> ${data.id}");
        // customerTicketController.getCreditInvoiceListData(data.id!);
        customerTicketController.servicesAreaList!.clear();
        customerTicketController.selectedServicesArea!.clear();
        customerTicketController.customerController.text = data.name!;
        // customerTicketController.getCustomerDetail();
        customerTicketController.update();
      }
    }
  }

  showServicesAreaSelectionDialog(String from) {
    List<GetActiveServiceDataList> item = [];

    List<SerialNumberDataList> serialItem = [];

    if (from.equalsIgnoreCase(Strings.service)) {
      if (customerTicketController.servicesAreaList != null &&
          customerTicketController.servicesAreaList!.isNotEmpty) {
        for (var element in customerTicketController.servicesAreaList!) {
          element.selected = false;
        }
        if (customerTicketController.selectedServicesArea!.isNotEmpty) {
          for (var element in customerTicketController.servicesAreaList!) {
            for (GetActiveServiceDataList selElement
                in customerTicketController.selectedServicesArea!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(customerTicketController.servicesAreaList!);
      }
    } else if (from.equalsIgnoreCase(Strings.serial_no)) {
      if (customerTicketController.getSerialNumberDataList != null &&
          customerTicketController.getSerialNumberDataList!.isNotEmpty) {
        for (var element in customerTicketController.getSerialNumberDataList!) {
          element.selected = false;
        }
        if (customerTicketController.selectedSerialNumberDataList!.isNotEmpty) {
          for (var element
              in customerTicketController.getSerialNumberDataList!) {
            for (SerialNumberDataList selElement
                in customerTicketController.selectedSerialNumberDataList!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        serialItem.addAll(customerTicketController.getSerialNumberDataList!);
      }
    }

    for (var element in serialItem) {
      customerTicketController.selectedSerialNumberIDs!.add(element.id!);
      customerTicketController.update();
    }

    for (var element in item) {
      customerTicketController.selectedServiceIDs!.add(element.id!);
      customerTicketController.update();
    }
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return CustServiceAreaDialog(
            serviceAreaAction: this,
            fromFor: from,
            itemsOrgLst: item,
            serialItemsOrgLst: serialItem,
          );
        });
  }

  @override
  void serviceAreaBtnAction(
      {String? identifier, List<GetActiveServiceDataList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.service) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      String custServiceId = "";
      customerTicketController.selectedServicesArea!.clear();
      customerTicketController.servicesAreaList!.clear();
      for (GetActiveServiceDataList element in selectedItem) {
        customerTicketController.selectedServicesArea!.add(element);
        serviceAreaName = "$serviceAreaName${element.serviceName!}, ";
        custServiceId = "$custServiceId${element.id!},";
      }
      List<int> serviceIDS = [];
      customerTicketController.selectedServicesArea!.forEach((element) {
        serviceIDS.add(element.id!);
      });

      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      customerTicketController.serviceController.text = serviceAreaName;
      customerTicketController.getSerialNumberTicket(serviceIDS.join(","));
      customerTicketController
          .getTicketReasonCategoryByActiveServices(serviceIDS);
    }
    customerTicketController.update();
  }

  @override
  void serialNoBtnAction(
      {String? identifier, List<SerialNumberDataList>? selectedSerialItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.service) &&
        selectedSerialItem != null &&
        selectedSerialItem.isNotEmpty) {
      String serialNumber = "";
      String custSerialNumberId = "";
      customerTicketController.selectedSerialNumberDataList!.clear();
      for (SerialNumberDataList element in selectedSerialItem) {
        customerTicketController.selectedSerialNumberDataList!.add(element);
        serialNumber = "$serialNumber${element.serialNumber!}, ";
        custSerialNumberId = "$custSerialNumberId${element.id!},";
      }
      if (!serialNumber.isNullOrEmpty() &&
          serialNumber.contains(",") &&
          serialNumber.length >= 2) {
        serialNumber = serialNumber.substring(0, serialNumber.length - 2);
      }
      customerTicketController.serviceController.text = serialNumber;
      log("custSerialNumberController==>${customerTicketController.custSerialNumberController}");
    }

    customerTicketController.update();
  }
}
