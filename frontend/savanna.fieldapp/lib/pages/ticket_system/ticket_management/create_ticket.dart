import 'dart:developer';
import 'dart:io';

import 'package:savbill/pages/credit_note/credit_customer_list.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/case_type_response.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/pages/task_management/active_customer/active_customer_list.dart';
import 'package:savbill/pages/ticket_system/model/response/create_ticket_active_service_res.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/model/response/get_reason_category_active_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_classification.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_get_serial_number_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_source_type_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/create_ticket_controller.dart';
import 'package:savbill/pages/ticket_system/ticket_management/cust_service_area_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_resolution_reasons_res.dart';
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
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

class CreateTicket extends StatefulWidget {
  @override
  _CreateTicketState createState() => _CreateTicketState();
}

class _CreateTicketState extends State<CreateTicket>
    with WidgetsBindingObserver
    implements
        ImageOptionBtnAction,
        PermissionDenyBtnAction,
        CustServiceAreaAction {
  final addTicketController = Get.put(CreateTicketController());
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
      child: GetBuilder<CreateTicketController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addTicketController.isLoading),
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
                                        addTicketController.caseTitleController,
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
                                    labelText: Strings.select_customer,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        addTicketController.customerController,
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
                                        return Strings.please_select_customer;
                                      } else {}
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      openParentCustomerScreen();
                                    },
                                    readOnly: true),

                                /*IgnorePointer(
                                  ignoring:
                                      addTicketController.ticketDetail != null
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
                                          fillColor: addTicketController
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
                                          addTicketController.selectedCustomer,
                                      items: addTicketController.customerList!
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
                                        addTicketController.selectedCustomer =
                                            value as CustomerDetail?;
                                        addTicketController.update();

                                        addTicketController.getCustomerDetail();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            addTicketController
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
                                        addTicketController.serviceController,
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
                                      addTicketController.ticketDetail != null
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
                                          fillColor: addTicketController
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
                                          addTicketController.selectedCustomer,
                                      items: addTicketController.customerList!
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
                                        addTicketController.selectedCustomer =
                                            value as CustomerDetail?;
                                        addTicketController.update();

                                        addTicketController.getCustomerDetail();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            addTicketController
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
                                    textEditingController: addTicketController
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
                                        addTicketController.mobileNoController,
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
                                    require: false),
                              ),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                flex: 2,
                                child: CoustomTextField(
                                    labelText: Strings.email,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController: addTicketController
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

                         // const SizedBox(height: Constant.MEDIUM_PADDING),
                          // Row(
                          //   crossAxisAlignment: CrossAxisAlignment.center,
                          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          //   children: [
                          //     Flexible(
                          //       flex: 1,
                          //       child: InputTitleRequire(
                          //           title: Strings.additional_email,
                          //           require: false),
                          //     ),
                          //     const SizedBox(
                          //       width: Constant.SMALL_PADDING,
                          //     ),
                          //     Flexible(
                          //       flex: 2,
                          //       child: CoustomTextField(
                          //           labelText: Strings.additional_email,
                          //           hintColor: AppTheme.colorIconGrey,
                          //           textEditingController:
                          //               addTicketController.emailController,
                          //           borderEnableColors: AppTheme.colorIconGrey,
                          //           borderFocusColors: AppTheme.colorIconGrey,
                          //           textColor: AppTheme.colorBlack,
                          //           keyboardType: TextInputType.emailAddress,
                          //           fontSize: AppTheme.small,
                          //           textInputAction: TextInputAction.next,
                          //           fontWeight: FontWeight.w500,
                          //           contentPadding: const EdgeInsets.symmetric(
                          //               horizontal: Constant.MEDIUM_PADDING,
                          //               vertical: Constant.MEDIUM_PADDING),
                          //           borderCorner: Constant.BTN_ROUNDED_CORNER,
                          //           onTextValidator: (String? value) {
                          //             if (value!.isNotEmpty &&
                          //                 !value.isValidEmail()) {
                          //               return Strings.enter_valid_email;
                          //             }
                          //             return null;
                          //           },
                          //           onTextFiledOnTap: () {},
                          //           readOnly: false),
                          //     ),
                          //   ],
                          // ),

                          // const SizedBox(height: Constant.MEDIUM_PADDING),
                          // Row(
                          //   crossAxisAlignment: CrossAxisAlignment.center,
                          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          //   children: [
                          //     Flexible(
                          //       flex: 1,
                          //       child: InputTitleRequire(
                          //           title: Strings.serial_no, require: false),
                          //     ),
                          //     const SizedBox(
                          //       width: Constant.SMALL_PADDING,
                          //     ),
                          //     Flexible(
                          //       flex: 2,
                          //       child: CoustomTextField(
                          //           labelText: Strings.select_serial_no,
                          //           hintColor: AppTheme.colorIconGrey,
                          //           textEditingController: addTicketController
                          //               .custSerialNumberController,
                          //           suffixIcon: Padding(
                          //             padding: const EdgeInsetsDirectional.all(
                          //                 Constant.LARGE_PADDING - 2),
                          //             child: SvgPicture.asset(
                          //               downArrowSvg,
                          //               color: AppTheme.colorBlack,
                          //               width: Constant.ICON_SIZE_S,
                          //               height: Constant.ICON_SIZE_S,
                          //             ),
                          //           ),
                          //           borderEnableColors: AppTheme.colorIconGrey,
                          //           borderFocusColors: AppTheme.colorIconGrey,
                          //           textColor: AppTheme.colorBlack,
                          //           keyboardType: TextInputType.text,
                          //           fontSize: AppTheme.small,
                          //           textInputAction: TextInputAction.done,
                          //           fontWeight: FontWeight.w500,
                          //           contentPadding: const EdgeInsets.symmetric(
                          //               horizontal: Constant.MEDIUM_PADDING,
                          //               vertical: Constant.MEDIUM_PADDING),
                          //           borderCorner: Constant.BTN_ROUNDED_CORNER,
                          //           onTextValidator: (String? value) {
                          //             /*if (value!.isEmpty) {
                          //               return Strings.please_select_serial_no;
                          //             } else {}*/
                          //             return null;
                          //           },
                          //           onTextFiledOnTap: () {
                          //             showServicesAreaSelectionDialog(
                          //                 Strings.serial_no);
                          //           },
                          //           readOnly: true),
                          //     ),
                          //   ],
                          // ),
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
                                    textEditingController: addTicketController
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
                                    textEditingController: addTicketController
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
                                    value: addTicketController.selectedCaseType,
                                    items: addTicketController.caseTypeList!
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
                                      addTicketController.selectedCaseType =
                                          value as CaseTypeDetail?;
                                      addTicketController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTicketController
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
                                    value:
                                        addTicketController.selectedDepartment,
                                    items: addTicketController
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
                                      addTicketController.selectedDepartment =
                                          value as DepartmentType?;
                                      addTicketController
                                          .filteredReasonCategoryList!.clear();
                                      addTicketController
                                          .selectedReasonCategoryData = null;
                                      addTicketController.departmentSelected(
                                          addTicketController
                                              .selectedDepartment);
                                      addTicketController.update();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTicketController
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
                              // Flexible(
                              //   flex: 2,
                              //   child: IgnorePointer(
                              //     ignoring:
                              //         addTicketController.ticketDetail != null
                              //             ? true
                              //             : false,
                              //     child: DropdownButtonHideUnderline(
                              //       child: DropdownButtonFormField(
                              //         icon: SvgPicture.asset(
                              //           downArrowSvg,
                              //           height: Constant.DROP_DOWN_ARROW_W_H,
                              //           width: Constant.DROP_DOWN_ARROW_W_H,
                              //           color: AppTheme.colorBlack,
                              //           fit: BoxFit.fill,
                              //         ),
                              //         decoration: Utils.ddlDecoration(),
                              //         hint: Align(
                              //             alignment: Alignment.centerLeft,
                              //             child: Text(
                              //                 Strings.ticket_problem_domain,
                              //                 style: TextStyle(
                              //                   fontSize: AppTheme.medium,
                              //                   color: AppTheme.colorIconGrey,
                              //                   fontFamily: AppTheme.appFontName,
                              //                 ))),
                              //         style: AppTheme.dropdownTextStyle,
                              //         isExpanded: true,
                              //         isDense: true,
                              //         value: addTicketController
                              //             .selectedReasonCategoryData,
                              //         items: addTicketController
                              //             .reasonCategoryDataList!
                              //             .map((ReasonCategoryDataList value) {
                              //           return DropdownMenuItem<
                              //               ReasonCategoryDataList>(
                              //             value: value,
                              //             child: Align(
                              //               alignment: Alignment.centerLeft,
                              //               child: CustomText(
                              //                 title: value.categoryName!,
                              //                 colors: AppTheme.colorBlack,
                              //                 textAlign: TextAlign.start,
                              //                 fontSize: AppTheme.small,
                              //                 fontWeight: FontWeight.w500,
                              //               ), //Text(value.desig!),
                              //             ),
                              //           );
                              //         }).toList(),
                              //         onChanged: (value) {
                              //           addTicketController
                              //                   .selectedReasonCategoryData =
                              //               value as ReasonCategoryDataList?;
                              //           addTicketController.update();
                              //           addTicketController.getSubCategory(addTicketController.selectedReasonCategoryData!.id);
                              //         },
                              //         validator: (value) {
                              //           if (value == null ||
                              //               addTicketController
                              //                       .selectedReasonCategoryData ==
                              //                   null) {
                              //             return Strings
                              //                 .select_ticket_problem_domain;
                              //           }
                              //           return null;
                              //         },
                              //       ),
                              //     ),
                              //   ),
                              // ),
                              Flexible(
                                flex: 2,
                                child: DropdownButtonHideUnderline(
                                  child: DropdownButtonFormField<ReasonCategoryDataList>(
                                    icon: SvgPicture.asset(
                                      downArrowSvg,
                                      height: Constant.DROP_DOWN_ARROW_W_H,
                                      width: Constant.DROP_DOWN_ARROW_W_H,
                                      color: AppTheme.colorBlack,
                                      fit: BoxFit.fill,
                                    ),
                                    decoration: Utils.ddlDecoration(
                                        fillColor: AppTheme.colorWhite),
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
                                    value: addTicketController
                                        .selectedReasonCategoryData,
                                    items: addTicketController
                                        .filteredReasonCategoryList!.isEmpty && addTicketController
                                        .filteredReasonCategoryList == null
                                        ? [
                                      DropdownMenuItem<ReasonCategoryDataList>(
                                        value: null,
                                        enabled: false,
                                        child: CustomText(title: Strings.no_data_found,colors: AppTheme.title_dark,
                                        ), // Disable selection
                                      ),
                                    ]
                                        :  addTicketController
                                        .filteredReasonCategoryList!
                                        .map((ReasonCategoryDataList value) {
                                      return DropdownMenuItem<ReasonCategoryDataList>(
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
                                      addTicketController
                                              .selectedReasonCategoryData =
                                          value;
                                      addTicketController
                                          .selectedSubProblemDomain = null;
                                      addTicketController
                                          .subProblemDomainList!.clear();
                                      addTicketController.getSubCategory(
                                          addTicketController
                                              .selectedReasonCategoryData!.id);
                                      addTicketController.update();
                                    },
                                    validator: (value) {
                                      if (addTicketController.ticketDetail ==
                                              null &&
                                          (value == null ||
                                              addTicketController
                                                      .selectedReasonCategoryData ==
                                                  null)) {
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
                                    value: addTicketController
                                        .selectedSubProblemDomain,
                                    items: addTicketController
                                        .subProblemDomainList!.isEmpty
                                        ? [
                                      DropdownMenuItem<SubProblemDomainDetail>(
                                        value: null,
                                        enabled: false,
                                        child: CustomText(title: Strings.no_data_found,colors: AppTheme.title_dark,
                                        ), // Disable selection
                                      ),
                                    ] :
                                    addTicketController
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
                                      addTicketController
                                              .selectedSubProblemDomain =
                                          value as SubProblemDomainDetail?;
                                      addTicketController
                                              .ticketReasonSubCategoryId =
                                          addTicketController
                                              .selectedSubProblemDomain!.id;
                                      addTicketController.update();
                                      addTicketController.setReasonData();
                                    },
                                    validator: (value) {
                                      if (value == null ||
                                          addTicketController
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
                                    title: Strings.classification, require: true),
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
                                    value:
                                    addTicketController.selectedClassification,
                                    items: addTicketController
                                        .ticketClassificationList!
                                        .map((TicketClassificationType value) {
                                      return DropdownMenuItem<TicketClassificationType>(
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
                                      addTicketController.selectedClassification =
                                      value as TicketClassificationType?;
                                      addTicketController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          addTicketController.from.equalsIgnoreCase(Strings.edit) ? const SizedBox(height: Constant.MEDIUM_PADDING) : Container(),

                          addTicketController.from.equalsIgnoreCase(Strings.edit) ?
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
                                    value: addTicketController
                                        .selectedReasonMapping,
                                    items: addTicketController
                                        .ticketReasonMappingList!.isEmpty
                                        ? [
                                      DropdownMenuItem<TicketSubCategoryGroupReasonMappingList>(
                                        value: null,
                                        enabled: false,
                                        child: CustomText(title: Strings.no_data_found,colors: AppTheme.title_dark,
                                        ), // Disable selection
                                      ),
                                    ] :addTicketController
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
                                      addTicketController
                                              .selectedReasonMapping =
                                          value
                                              as TicketSubCategoryGroupReasonMappingList?;
                                      addTicketController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ) : Container(),
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
                                      value: addTicketController
                                          .selectedTicketPriority,
                                      items: addTicketController
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
                                        addTicketController
                                                .selectedTicketPriority =
                                            value as TicketPriority?;
                                        addTicketController.update();
                                      },
                                      validator: (value) {
                                        if (value == null ||
                                            addTicketController
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
                          // const SizedBox(height: Constant.MEDIUM_PADDING),
                          // Row(
                          //   crossAxisAlignment: CrossAxisAlignment.center,
                          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          //   children: [
                          //     Flexible(
                          //       flex: 1,
                          //       child: InputTitleRequire(
                          //           title: Strings.root_cause, require: false),
                          //     ),
                          //     const SizedBox(
                          //       width: Constant.SMALL_PADDING,
                          //     ),
                          //     Flexible(
                          //       flex: 2,
                          //       child: IgnorePointer(
                          //         ignoring: true,
                          //         child: DropdownButtonHideUnderline(
                          //           child: DropdownButtonFormField(
                          //             icon: SvgPicture.asset(
                          //               downArrowSvg,
                          //               height: Constant.DROP_DOWN_ARROW_W_H,
                          //               width: Constant.DROP_DOWN_ARROW_W_H,
                          //               color: AppTheme.colorBlack,
                          //               fit: BoxFit.fill,
                          //             ),
                          //             decoration: Utils.ddlDecoration(
                          //               fillColor: Colors.black12,
                          //             ),
                          //             hint: Align(
                          //                 alignment: Alignment.centerLeft,
                          //                 child: Text(Strings.root_cause,
                          //                     style: TextStyle(
                          //                       fontSize: AppTheme.medium,
                          //                       color: AppTheme.colorIconGrey,
                          //                       fontFamily:
                          //                           AppTheme.appFontName,
                          //                     ))),
                          //             style: AppTheme.dropdownTextStyle,
                          //             isExpanded: true,
                          //             isDense: true,
                          //             value:
                          //                 addTicketController.selectedRootCause,
                          //             items: addTicketController.rootCauseList!
                          //                 .map((String value) {
                          //               return DropdownMenuItem<String>(
                          //                 value: value,
                          //                 child: Align(
                          //                   alignment: Alignment.centerLeft,
                          //                   child: CustomText(
                          //                     title: value,
                          //                     colors: AppTheme.colorBlack,
                          //                     textAlign: TextAlign.start,
                          //                     fontSize: AppTheme.small,
                          //                     fontWeight: FontWeight.w500,
                          //                   ),
                          //                 ),
                          //               );
                          //             }).toList(),
                          //             onChanged: (value) {
                          //               addTicketController.selectedRootCause =
                          //                   value as String?;
                          //               addTicketController.update();
                          //             },
                          //             validator: (value) {
                          //               return null;
                          //             },
                          //           ),
                          //         ),
                          //       ),
                          //     ),
                          //   ],
                          // ),
                          // const SizedBox(height: Constant.MEDIUM_PADDING),
                          // Row(
                          //   crossAxisAlignment: CrossAxisAlignment.center,
                          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          //   children: [
                          //     Flexible(
                          //       flex: 1,
                          //       child: InputTitleRequire(
                          //           title: Strings.resolution, require: false),
                          //     ),
                          //     const SizedBox(
                          //       width: Constant.SMALL_PADDING,
                          //     ),
                          //     Flexible(
                          //       flex: 2,
                          //       child: IgnorePointer(
                          //         ignoring: true,
                          //         child: DropdownButtonHideUnderline(
                          //           child: DropdownButtonFormField(
                          //             icon: SvgPicture.asset(
                          //               downArrowSvg,
                          //               height: Constant.DROP_DOWN_ARROW_W_H,
                          //               width: Constant.DROP_DOWN_ARROW_W_H,
                          //               color: AppTheme.colorBlack,
                          //               fit: BoxFit.fill,
                          //             ),
                          //             decoration: Utils.ddlDecoration(
                          //               fillColor: Colors.black12,
                          //             ),
                          //             hint: Align(
                          //                 alignment: Alignment.centerLeft,
                          //                 child: Text(Strings.resolution,
                          //                     style: TextStyle(
                          //                       fontSize: AppTheme.medium,
                          //                       color: AppTheme.colorIconGrey,
                          //                       fontFamily:
                          //                           AppTheme.appFontName,
                          //                     ))),
                          //             style: AppTheme.dropdownTextStyle,
                          //             isExpanded: true,
                          //             isDense: true,
                          //             value: addTicketController
                          //                 .selectedResolution,
                          //             items: addTicketController.resolutionList!
                          //                 .map((String value) {
                          //               return DropdownMenuItem<String>(
                          //                 value: value,
                          //                 child: Align(
                          //                   alignment: Alignment.centerLeft,
                          //                   child: CustomText(
                          //                     title: value,
                          //                     colors: AppTheme.colorBlack,
                          //                     textAlign: TextAlign.start,
                          //                     fontSize: AppTheme.small,
                          //                     fontWeight: FontWeight.w500,
                          //                   ),
                          //                 ),
                          //               );
                          //             }).toList(),
                          //             onChanged: (value) {
                          //               addTicketController.selectedResolution =
                          //                   value as String?;
                          //               addTicketController.update();
                          //             },
                          //             validator: (value) {
                          //               return null;
                          //             },
                          //           ),
                          //         ),
                          //       ),
                          //     ),
                          //   ],
                          // ),
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
                                    value:
                                        addTicketController.selectedSourceType,
                                    items: addTicketController
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
                                      addTicketController.selectedSourceType =
                                          value as TicketSourceType?;
                                      addTicketController.update();
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
                          // Row(
                          //   crossAxisAlignment: CrossAxisAlignment.center,
                          //   mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          //   children: [
                          //     Flexible(
                          //       flex: 1,
                          //       child: InputTitleRequire(
                          //           title: Strings.sub_source, require: false),
                          //     ),
                          //     const SizedBox(
                          //       width: Constant.SMALL_PADDING,
                          //     ),
                          //     Flexible(
                          //       flex: 2,
                          //       child: DropdownButtonHideUnderline(
                          //         child: DropdownButtonFormField(
                          //           icon: SvgPicture.asset(
                          //             downArrowSvg,
                          //             height: Constant.DROP_DOWN_ARROW_W_H,
                          //             width: Constant.DROP_DOWN_ARROW_W_H,
                          //             color: AppTheme.colorBlack,
                          //             fit: BoxFit.fill,
                          //           ),
                          //           decoration: Utils.ddlDecoration(),
                          //           hint: Align(
                          //               alignment: Alignment.centerLeft,
                          //               child: Text(Strings.sub_source,
                          //                   style: TextStyle(
                          //                     fontSize: AppTheme.medium,
                          //                     color: AppTheme.colorIconGrey,
                          //                     fontFamily: AppTheme.appFontName,
                          //                   ))),
                          //           style: AppTheme.dropdownTextStyle,
                          //           isExpanded: true,
                          //           isDense: true,
                          //           value: addTicketController
                          //               .selectedSubSourceType,
                          //           items: addTicketController
                          //               .ticketSourceTypeList!
                          //               .map((TicketSourceType value) {
                          //             return DropdownMenuItem<TicketSourceType>(
                          //               value: value,
                          //               child: Align(
                          //                 alignment: Alignment.centerLeft,
                          //                 child: CustomText(
                          //                   title: value.text!,
                          //                   colors: AppTheme.colorBlack,
                          //                   textAlign: TextAlign.start,
                          //                   fontSize: AppTheme.small,
                          //                   fontWeight: FontWeight.w500,
                          //                 ),
                          //               ),
                          //             );
                          //           }).toList(),
                          //           onChanged: (value) {
                          //             addTicketController
                          //                     .selectedSubSourceType =
                          //                 value as TicketSourceType?;
                          //             addTicketController.update();
                          //           },
                          //           validator: (value) {
                          //             return null;
                          //           },
                          //         ),
                          //       ),
                          //     ),
                          //   ],
                          // ),
                          // const SizedBox(height: Constant.MEDIUM_PADDING),
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
                                      addTicketController.ticketDetail != null
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
                                          fillColor: addTicketController
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
                                      value: addTicketController
                                          .selectedCaseStatus,
                                      items: addTicketController.caseStatusList!
                                          .map((CaseStatusDetail value) {
                                        return DropdownMenuItem<
                                            CaseStatusDetail>(
                                          value: value,
                                          child: Align(
                                            alignment: Alignment.centerLeft,
                                            child: CustomText(
                                              title: addTicketController.ticketDetail != null
                                                  ? value.text!.equalsIgnoreCase("Open")
                                                      ? value.text!.equalsIgnoreCase("Follow Up")
                                                          ? "In Progress"
                                                          : "In Progress"
                                                      : value.text!
                                                  : value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                              fontWeight: FontWeight.w500,
                                            ),
                                          ),
                                        );
                                      }).toList(),
                                      onChanged: (value) {
                                        addTicketController.selectedCaseStatus =
                                            value as CaseStatusDetail?;

                                        addTicketController
                                            .rootCauseResolutionList!
                                            .clear();
                                        addTicketController
                                            .rootCauseResolutionList!
                                            .clear();
                                        addTicketController
                                            .resolutionReasonsList!
                                            .clear();

                                        addTicketController.followUpScheduleDate= null;
                                        addTicketController.followUpScheduleTime= null;
                                        addTicketController.selectedRootCauseResolution= null;
                                        addTicketController
                                            .selectedResolutionReason = null;

                                        if (addTicketController
                                                    .selectedCaseStatus !=
                                                null &&
                                            addTicketController
                                                .selectedCaseStatus!.value!
                                                .equalsIgnoreCase(
                                                    "Raise and Close")) {
                                          addTicketController
                                              .checkTicketResolutionReasons();
                                        }
                                        addTicketController.update();
                                      },
                                      validator: (value) {
                                        if (addTicketController.ticketDetail ==
                                                null &&
                                            (value == null ||
                                                addTicketController
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
                          addTicketController.selectedCaseStatus != null &&
                                  addTicketController.selectedCaseStatus!.value!
                                      .equalsIgnoreCase("Raise and Close")
                              ? Column(
                                  children: [
                                    Row(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: InputTitleRequire(
                                              title: Strings.root_cause,
                                              require: true),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
                                          child: IgnorePointer(
                                            ignoring: addTicketController
                                                        .ticketDetail !=
                                                    null
                                                ? true
                                                : false,
                                            child: DropdownButtonHideUnderline(
                                              child: DropdownButtonFormField(
                                                icon: SvgPicture.asset(
                                                  downArrowSvg,
                                                  height: Constant
                                                      .DROP_DOWN_ARROW_W_H,
                                                  width: Constant
                                                      .DROP_DOWN_ARROW_W_H,
                                                  color: AppTheme.colorBlack,
                                                  fit: BoxFit.fill,
                                                ),
                                                decoration: Utils.ddlDecoration(
                                                    fillColor: addTicketController
                                                                .ticketDetail !=
                                                            null
                                                        ? Colors.black12
                                                        : AppTheme.colorWhite),
                                                hint: Align(
                                                    alignment:
                                                        Alignment.centerLeft,
                                                    child:
                                                        Text(Strings.root_cause,
                                                            style: TextStyle(
                                                              fontSize: AppTheme
                                                                  .medium,
                                                              color: AppTheme
                                                                  .colorIconGrey,
                                                              fontFamily: AppTheme
                                                                  .appFontName,
                                                            ))),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: true,
                                                isDense: true,
                                                value: addTicketController
                                                    .selectedResolutionReason,
                                                items: addTicketController
                                                    .resolutionReasonsList!
                                                    .map(
                                                        (ResolutionReasonsDataList?
                                                            value) {
                                                  return DropdownMenuItem<
                                                      ResolutionReasonsDataList>(
                                                    value: value,
                                                    child: Align(
                                                      alignment:
                                                          Alignment.centerLeft,
                                                      child: CustomText(
                                                        title: value!.name,
                                                        colors:
                                                            AppTheme.colorBlack,
                                                        textAlign:
                                                            TextAlign.start,
                                                        fontSize:
                                                            AppTheme.small,
                                                        fontWeight:
                                                            FontWeight.w500,
                                                      ),
                                                    ),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addTicketController
                                                          .selectedResolutionReason =
                                                      value
                                                          as ResolutionReasonsDataList?;
                                                  addTicketController
                                                      .rootCauseResolutionList!
                                                      .clear();
                                                  if (addTicketController
                                                          .selectedResolutionReason !=
                                                      null) {
                                                    addTicketController
                                                        .rootCauseResolutionList!
                                                        .addAll(addTicketController
                                                            .selectedResolutionReason!
                                                            .rootCauseResolutionMappingList!);
                                                  }
                                                  addTicketController.update();
                                                },
                                                validator: (value) {
                                                  if (addTicketController
                                                              .ticketDetail ==
                                                          null &&
                                                      (value == null ||
                                                          addTicketController
                                                                  .selectedResolutionReason ==
                                                              null)) {
                                                    return Strings
                                                        .select_root_cause;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                    Row(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: InputTitleRequire(
                                              title: Strings.resolution,
                                              require: true),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
                                          child: IgnorePointer(
                                            ignoring: addTicketController
                                                        .ticketDetail !=
                                                    null
                                                ? true
                                                : false,
                                            child: DropdownButtonHideUnderline(
                                              child: DropdownButtonFormField(
                                                icon: SvgPicture.asset(
                                                  downArrowSvg,
                                                  height: Constant
                                                      .DROP_DOWN_ARROW_W_H,
                                                  width: Constant
                                                      .DROP_DOWN_ARROW_W_H,
                                                  color: AppTheme.colorBlack,
                                                  fit: BoxFit.fill,
                                                ),
                                                decoration: Utils.ddlDecoration(
                                                    fillColor: addTicketController
                                                                .ticketDetail !=
                                                            null
                                                        ? Colors.black12
                                                        : AppTheme.colorWhite),
                                                hint: Align(
                                                    alignment:
                                                        Alignment.centerLeft,
                                                    child:
                                                        Text(Strings.resolution,
                                                            style: TextStyle(
                                                              fontSize: AppTheme
                                                                  .medium,
                                                              color: AppTheme
                                                                  .colorIconGrey,
                                                              fontFamily: AppTheme
                                                                  .appFontName,
                                                            ))),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: true,
                                                isDense: true,
                                                value: addTicketController
                                                    .selectedRootCauseResolution,
                                                items: addTicketController
                                                    .rootCauseResolutionList!
                                                    .map(
                                                        (RootCauseResolutionMappingList
                                                            value) {
                                                  return DropdownMenuItem<
                                                      RootCauseResolutionMappingList>(
                                                    value: value,
                                                    child: Align(
                                                      alignment:
                                                          Alignment.centerLeft,
                                                      child: CustomText(
                                                        title: value
                                                            .rootCauseReason,
                                                        colors:
                                                            AppTheme.colorBlack,
                                                        textAlign:
                                                            TextAlign.start,
                                                        fontSize:
                                                            AppTheme.small,
                                                        fontWeight:
                                                            FontWeight.w500,
                                                      ),
                                                    ),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addTicketController
                                                          .selectedRootCauseResolution =
                                                      value
                                                          as RootCauseResolutionMappingList?;
                                                  addTicketController.update();
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
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          const SizedBox(height: Constant.MEDIUM_PADDING),
                          addTicketController.selectedCaseStatus != null &&
                                  addTicketController.selectedCaseStatus!.value!
                                      .equalsIgnoreCase("Follow Up")
                              ? Column(
                                  children: [
                                    Row(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: InputTitleRequire(
                                              title: Strings.followup_date_time,
                                              require: true),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 2,
                                          child: CoustomTextField(
                                              labelText:
                                                  Strings.followup_date_time,
                                              suffixIcon: Padding(
                                                padding:
                                                    const EdgeInsetsDirectional
                                                        .all(Constant
                                                            .MEDIUM_PADDING),
                                                child: SvgPicture.asset(
                                                  calendarSvg,
                                                  color: AppTheme.colorBlack,
                                                  width: Constant.ICON_SIZE_S,
                                                  height: Constant.ICON_SIZE_S,
                                                  // myIcon is a 48px-wide widget.
                                                ),
                                              ),
                                              textEditingController:
                                                  addTicketController
                                                      .followupDateTimeController,
                                              borderEnableColors:
                                                  AppTheme.colorGrey,
                                              textInputAction:
                                                  TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings
                                                      .select_followup_date_time;
                                                }
                                                return null;
                                              },
                                              onTextFiledOnTap: () {
                                                // if (addEditInwardsController.inwardsDetail !=
                                                //     null) {
                                                //   print("not editable");
                                                // } else {
                                                selectDate(
                                                    Strings.followup_date_time,
                                                    DateTime(
                                                        DateTime.now().year -
                                                            10),
                                                    DateTime(
                                                        DateTime.now().year +
                                                            10));
                                                // }
                                              },
                                              borderCorner:
                                                  Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                                  const EdgeInsets.symmetric(
                                                      horizontal: Constant
                                                          .LARGE_PADDING),
                                              readOnly: true),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                        height: Constant.MEDIUM_PADDING),
                                  ],
                                )
                              : const SizedBox.shrink(),

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
                                        addTicketController.remarksController,
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
                        title: addTicketController.from
                                .equalsIgnoreCase(Strings.edit)
                            ? Strings.update_ticket
                            : Strings.save,
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
        addTicketController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.update_ticket
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
      addTicketController.createTicketApiCall();
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
      addTicketController.setBtnClickEvent(true);
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
        addTicketController.fileDetail = FileDetail(
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
    addTicketController.update();
  }

  openCameraGallery(ImageSource source) async {
    try {
      XFile? image;
      image = await imagePicker.pickImage(source: source);

      if (image != null && !image.path.isNullOrEmpty()) {
        num size = await Utils.getFileSize(image.path, 1);
        print("image picker file size : ${size}");
        if (size <= 500) {
          addTicketController.fileDetail = FileDetail(
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
      addTicketController.update();
    } catch (e) {
      print("image picker exception : $e");
    }
  }

  fileViewWidget() {
    return addTicketController.fileDetail != null
        ? FileGridItem(
            fileDetail: addTicketController.fileDetail!,
            onTapItem: () {},
            bottomAction: fileItemAction(),
          )
        : Container();
  }

  fileItemAction() {
    return addTicketController.fileDetail != null &&
            addTicketController.fileDetail!.isFileLocal == true
        ? Align(
            alignment: Alignment.topRight,
            child: InkWell(
              onTap: () {
                addTicketController.fileDetail = null;
                addTicketController.update();
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
    // var result = await Get.to(CreditCustomerList(), arguments: {});
    var result = await Get.to(ActiveCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      if (data != null) {
        addTicketController.selectedCust = data;
        log("openParentCustomerScreen>> ${data.id}");
        // addTicketController.getCreditInvoiceListData(data.id!);
        addTicketController.servicesAreaList!.clear();
        addTicketController.selectedServicesArea!.clear();
        addTicketController.customerController.text = data.name!;
        addTicketController.getCustomerDetail();
        addTicketController.update();
      }
    }
  }

  showServicesAreaSelectionDialog(String from) {
    List<GetActiveServiceDataList> item = [];

    List<SerialNumberDataList> serialItem = [];

    if (from.equalsIgnoreCase(Strings.service)) {
      if (addTicketController.servicesAreaList != null &&
          addTicketController.servicesAreaList!.isNotEmpty) {
        for (var element in addTicketController.servicesAreaList!) {
          element.selected = false;
        }
        if (addTicketController.selectedServicesArea!.isNotEmpty) {
          for (var element in addTicketController.servicesAreaList!) {
            for (GetActiveServiceDataList selElement
                in addTicketController.selectedServicesArea!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addTicketController.servicesAreaList!);
      }
    } else if (from.equalsIgnoreCase(Strings.serial_no)) {
      if (addTicketController.getSerialNumberDataList != null &&
          addTicketController.getSerialNumberDataList!.isNotEmpty) {
        for (var element in addTicketController.getSerialNumberDataList!) {
          element.selected = false;
        }
        if (addTicketController.selectedSerialNumberDataList!.isNotEmpty) {
          for (var element in addTicketController.getSerialNumberDataList!) {
            for (SerialNumberDataList selElement
                in addTicketController.selectedSerialNumberDataList!) {
              if (selElement.id == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        serialItem.addAll(addTicketController.getSerialNumberDataList!);
      }
    }

    for (var element in item) {
      addTicketController.selectedServiceIDs!.add(element.id!);
      addTicketController.update();
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
      addTicketController.selectedServicesArea!.clear();
      addTicketController.servicesAreaList!.clear();
      for (GetActiveServiceDataList element in selectedItem) {
        addTicketController.selectedServicesArea!.add(element);
        serviceAreaName = "$serviceAreaName${element.serviceName!}, ";
        custServiceId = "$custServiceId${element.id!},";
      }

      addTicketController.selectedServicesArea!.forEach((element) {
        addTicketController.serviceIDS.add(element.id!);
      });

      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addTicketController.serviceController.text = serviceAreaName;
      addTicketController
          .getSerialNumberTicket(addTicketController.serviceIDS.join(","));
      addTicketController.getTicketReasonCategoryByActiveServices(
          addTicketController.serviceIDS);
    }
    addTicketController.update();
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
      addTicketController.selectedSerialNumberDataList!.clear();
      for (SerialNumberDataList element in selectedSerialItem) {
        addTicketController.selectedSerialNumberDataList!.add(element);
        serialNumber = "$serialNumber${element.serialNumber!}, ";
        custSerialNumberId = "$custSerialNumberId${element.id!},";
      }
      addTicketController.custSerialNumberController.text = serialNumber;
    }
    addTicketController.update();
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.followup_date_time) {
      if (addTicketController.selectedFollowUpDate != null) {
        selectedDate = addTicketController.selectedFollowUpDate;
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
      if (identity == Strings.followup_date_time) {
        addTicketController.selectedFollowUpDate = picked;
        addTicketController.update();
        _selectDateTime();
      }
    }
  }

  Future<void> _selectDateTime() async {
    TimeOfDay? selectedDateTime = TimeOfDay.now();
    final TimeOfDay? picked = await showTimePicker(
      context: context,
      initialTime: selectedDateTime,
      builder: (BuildContext? context, Widget? child) {
        return MediaQuery(
          data: MediaQuery.of(context!).copyWith(alwaysUse24HourFormat: false),
          child: child!,
        );
      },
    );
    if (picked != null) {
      DateTime dt = DateTime(
        addTicketController.selectedFollowUpDate!.year,
        addTicketController.selectedFollowUpDate!.month,
        addTicketController.selectedFollowUpDate!.day,
        picked.hour,
        picked.minute,
      );
      addTicketController.followupDateTimeController.text =
          addTicketController.dateFormat.format(dt);
      addTicketController.followUpScheduleDate = addTicketController.apiDateFormat.format(dt);
      addTicketController.followUpScheduleTime = addTicketController.apiTimeFormat.format(dt);
      addTicketController.update();
    }
  }
}
