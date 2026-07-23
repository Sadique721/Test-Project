import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/credit_note/credit_customer_list.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/customer/model/response/payment_mode_list_res.dart';
import 'package:savbill/pages/dashboard/model/request/record_payment_req.dart';
import 'package:savbill/pages/dashboard/model/response/bank_list_res.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/pages/dashboard/record_payment_controller.dart';
import 'package:savbill/pages/dashboard/select_payment_invoice_item.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/model/file_detail.dart';
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
import 'package:flutter/services.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import 'package:image_picker/image_picker.dart';
import 'package:permission_handler/permission_handler.dart';

class RecordPayment extends StatefulWidget {
  @override
  _RecordPaymentState createState() => _RecordPaymentState();
}

class _RecordPaymentState extends State<RecordPayment>
    with WidgetsBindingObserver
    implements
        ImageOptionBtnAction,
        PermissionDenyBtnAction,
        InvoiceSelectionAction {
  final recordPaymentController = Get.put(RecordPaymentController());
  final recordPaymentFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  final ImagePicker imagePicker = ImagePicker();

  String type = Strings.prepaid;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back(result: false);
  }

  @override
  void initState() {
    WidgetsBinding.instance.addObserver(this);
    super.initState();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.paused:
        print("on pause method call");
        return;
      case AppLifecycleState.resumed:
        print("on resume method call");
        if (recordPaymentController.checkBtnClickEvent) {
          recordPaymentController.setBtnClickEvent(false);
          checkCameraPermission();
        }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<RecordPaymentController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: recordPaymentController.isLoading),
        ]);
      }), /**/
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
                      key: recordPaymentFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),

                          /* InputTitleRequire(
                              title: Strings.customer, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
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
                              decoration: InputDecoration(
                                  filled: true,
                                  contentPadding: const EdgeInsets.fromLTRB(
                                      Constant.LARGE_PADDING,
                                      0,
                                      Constant.LARGE_PADDING,
                                      0),
                                  fillColor: AppTheme.colorWhite,
                                  hintText: Strings.customer,
                                  hintStyle: AppTheme.dropdownHintStyle,
                                  labelStyle: AppTheme.dropdownLabelStyle,
                                  errorStyle: AppTheme.dropdownErrorStyle,
                                  alignLabelWithHint: true,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  focusColor: Colors.transparent,
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  errorMaxLines: 3),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: recordPaymentController.selectedCustomer,
                              items: recordPaymentController.customerList!
                                  .map((CustListDetails value) {
                                return DropdownMenuItem<CustListDetails>(
                                  value: value,
                                  child: Text(value.name!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                recordPaymentController.selectedCustomer =
                                    value as CustListDetails?;
                                recordPaymentController.selectedInvoice = [];
                                recordPaymentController.invoiceController
                                    .clear();
                                recordPaymentController.update();
                                recordPaymentController.getInvoiceListData();
                              },
                              validator: (value) {
                                if (value == null ||
                                    recordPaymentController.selectedCustomer ==
                                        null ||
                                    recordPaymentController
                                            .selectedCustomer?.id ==
                                        0) {
                                  return Strings.please_select_customer;
                                }
                                return null;
                              },
                            ),
                          ),*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.customer, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                            labelText: Strings.select_a_customer,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: recordPaymentController
                                .createCustomerController,
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
                            readOnly: true,
                            isEnable: recordPaymentController.form!
                                    .equalsIgnoreCase(Strings.customer_payment)
                                ? false
                                : true,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.please_select_customer;
                              } else {}
                              return null;
                            },
                            onTextFiledOnTap: () {
                              openParentCustomerScreen();
                            },
                          ),

                          const SizedBox(height: Constant.SMALL_PADDING),
                          InputTitleRequire(
                              title: Strings.invoice, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.invoice,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController:
                                  recordPaymentController.invoiceController,
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
                                  return Strings.please_select_invoice;
                                }
                              },
                              onTextFiledOnTap: () {
                                showInvoiceSelectionDialog(Strings.invoice);
                              },
                              readOnly: true),

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          // const SizedBox(
                          //   height: Constant.SMALL_PADDING,
                          // ),
                          InputTitleRequire(
                              title: Strings.payment_mode, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          // DropdownButtonHideUnderline(
                          //   child: DropdownButtonFormField(
                          //     icon: SvgPicture.asset(
                          //       downArrowSvg,
                          //       height: Constant.DROP_DOWN_ARROW_W_H,
                          //       width: Constant.DROP_DOWN_ARROW_W_H,
                          //       color: AppTheme.colorBlack,
                          //       fit: BoxFit.fill,
                          //     ),
                          //     decoration: InputDecoration(
                          //         filled: true,
                          //         contentPadding: const EdgeInsets.fromLTRB(
                          //             Constant.SMALL_PADDING,
                          //             0,
                          //             Constant.SMALL_PADDING,
                          //             0),
                          //         fillColor: AppTheme.colorWhite,
                          //         hintText: Strings.please_select_pay_mode,
                          //         hintStyle: AppTheme.dropdownHintStyle,
                          //         labelStyle: AppTheme.dropdownLabelStyle,
                          //         errorStyle: AppTheme.dropdownErrorStyle,
                          //         alignLabelWithHint: true,
                          //         border: OutlineInputBorder(
                          //           borderRadius: BorderRadius.circular(
                          //               Constant.DROP_DOWN_ROUNDED_CORNER),
                          //           borderSide: BorderSide(
                          //               color: AppTheme.colorBlack, width: 0.8),
                          //         ),
                          //         focusColor: Colors.transparent,
                          //         focusedBorder: OutlineInputBorder(
                          //           borderRadius: BorderRadius.circular(
                          //               Constant.DROP_DOWN_ROUNDED_CORNER),
                          //           borderSide: BorderSide(
                          //               color: AppTheme.colorBlack, width: 0.8),
                          //         ),
                          //         errorMaxLines: 1),
                          //     style: AppTheme.dropdownTextStyle,
                          //     isExpanded: false,
                          //     isDense: false,
                          //     value: recordPaymentController.selectedPayMode,
                          //     items: recordPaymentController.paymentModeList!
                          //         .map((PaymentModeDetail value) {
                          //       return DropdownMenuItem<PaymentModeDetail>(
                          //         value: value,
                          //         child: CustomText(
                          //           title: value.text!,
                          //           colors: AppTheme.colorBlack,
                          //           textAlign: TextAlign.start,
                          //           fontSize: AppTheme.small,
                          //         ),
                          //       );
                          //     }).toList(),
                          //     onChanged: (value) {
                          //       recordPaymentController.selectedPayMode =
                          //           value as PaymentModeDetail?;
                          //       recordPaymentController
                          //           .selectPaymentModeSource = null;
                          //       recordPaymentController
                          //           .getBankListData("operator");
                          //       // selectPaymentMode(value);
                          //
                          //       if (recordPaymentController
                          //               .selectedPayMode!.value!
                          //               .equalsIgnoreCase("NEFT_RTGS") ||
                          //           recordPaymentController
                          //               .selectedPayMode!.value!
                          //               .equalsIgnoreCase("barter") ||recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("EFTS")) {
                          //         recordPaymentController.isSourceTypeDisable.value =
                          //             true;
                          //         recordPaymentController.update();
                          //       } else if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("Direct Deposit")) {
                          //         recordPaymentController.isSourceTypeDisable.value =
                          //             true;
                          //         recordPaymentController.isDirectPayment.value =
                          //             true;
                          //         recordPaymentController.isDestinationBank.value =
                          //             true;
                          //         recordPaymentController.update();
                          //       } else if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("VatReceiveable")) {
                          //         recordPaymentController.isSourceTypeDisable.value =
                          //             true;
                          //         recordPaymentController.isDirectPayment.value =
                          //         true;
                          //         recordPaymentController.isDestinationBank.value =
                          //         false;
                          //         recordPaymentController.update();
                          //       } else if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("POS")) {
                          //         recordPaymentController.isSourceTypeDisable.value =
                          //         false;
                          //         recordPaymentController.isDirectPayment.value =
                          //             true;
                          //         recordPaymentController.update();
                          //       } else {
                          //         recordPaymentController.isSourceTypeDisable.value = false;
                          //         recordPaymentController.isDirectPayment.value = false;
                          //         recordPaymentController.isDestinationBank.value = false;
                          //         recordPaymentController.update();
                          //       }
                          //       recordPaymentController.update();
                          //       recordPaymentController.paymentModeSourceStatusList!.clear();
                          //
                          //       if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("Online")) {
                          //         recordPaymentController.isOnline.value = true;
                          //         recordPaymentController.isNEFTRTGS.value = false;
                          //         recordPaymentController.isChequeMode.value = false;
                          //         recordPaymentController.update();
                          //       } else if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("Cheque")) {
                          //         recordPaymentController.isChequeMode.value = true;
                          //         recordPaymentController.isNEFTRTGS.value = false;
                          //         recordPaymentController.isOnline.value = false;
                          //         recordPaymentController.update();
                          //       } else if (recordPaymentController
                          //           .selectedPayMode!.value!
                          //           .equalsIgnoreCase("NEFT_RTGS")) {
                          //         recordPaymentController.isNEFTRTGS.value = true;
                          //         recordPaymentController.isChequeMode.value = false;
                          //         recordPaymentController.isOnline.value = false;
                          //         recordPaymentController.update();
                          //       } else {
                          //         recordPaymentController.isOnline.value = false;
                          //         recordPaymentController.isChequeMode.value = false;
                          //         recordPaymentController.isNEFTRTGS.value = false;
                          //         recordPaymentController.update();
                          //       }
                          //       recordPaymentController
                          //           .getPaymentModeSourceType(
                          //               recordPaymentController
                          //                   .selectedPayMode!.value);
                          //       recordPaymentController.update();
                          //     },
                          //     validator: (value) {
                          //       if (value == null ||
                          //           recordPaymentController.selectedPayMode ==
                          //               null) {
                          //         return Strings.please_select_pay_mode;
                          //       }
                          //       return null;
                          //     },
                          //   ),
                          // ),
                          DropdownButtonHideUnderline(
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
                                  child: CustomText(
                                      title:
                                      Strings.select_a_payment_mode,
                                      colors:
                                      AppTheme.colorIconGrey)),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: recordPaymentController.selectedPayMode,
                              items: recordPaymentController.paymentModeList!
                                  .map((PaymentModeDetail value) {
                                return DropdownMenuItem<
                                    PaymentModeDetail>(
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
                                recordPaymentController.selectedPayMode =
                                value as PaymentModeDetail?;
                                recordPaymentController
                                    .selectPaymentModeSource = null;
                                recordPaymentController
                                    .getBankListData("operator");
                                // selectPaymentMode(value);

                                if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("NEFT_RTGS") ||
                                    recordPaymentController
                                        .selectedPayMode!.value!
                                        .equalsIgnoreCase("barter") ||recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("EFTS")) {
                                  recordPaymentController.isSourceTypeDisable.value =
                                  true;
                                  recordPaymentController.update();
                                } else if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("Direct Deposit")) {
                                  recordPaymentController.isSourceTypeDisable.value =
                                  true;
                                  recordPaymentController.isDirectPayment.value =
                                  true;
                                  recordPaymentController.isDestinationBank.value =
                                  true;
                                  recordPaymentController.update();
                                } else if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("VatReceiveable")) {
                                  recordPaymentController.isSourceTypeDisable.value =
                                  true;
                                  recordPaymentController.isDirectPayment.value =
                                  true;
                                  recordPaymentController.isDestinationBank.value =
                                  false;
                                  recordPaymentController.update();
                                } else if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("POS")) {
                                  recordPaymentController.isSourceTypeDisable.value =
                                  false;
                                  recordPaymentController.isDirectPayment.value =
                                  true;
                                  recordPaymentController.update();
                                } else {
                                  recordPaymentController.isSourceTypeDisable.value = false;
                                  recordPaymentController.isDirectPayment.value = false;
                                  recordPaymentController.isDestinationBank.value = false;
                                  recordPaymentController.update();
                                }
                                recordPaymentController.update();
                                recordPaymentController.paymentModeSourceStatusList!.clear();

                                if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("Online")) {
                                  recordPaymentController.isOnline.value = true;
                                  recordPaymentController.isNEFTRTGS.value = false;
                                  recordPaymentController.isChequeMode.value = false;
                                  recordPaymentController.update();
                                } else if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("Cheque")) {
                                  recordPaymentController.isChequeMode.value = true;
                                  recordPaymentController.isNEFTRTGS.value = false;
                                  recordPaymentController.isOnline.value = false;
                                  recordPaymentController.update();
                                } else if (recordPaymentController
                                    .selectedPayMode!.value!
                                    .equalsIgnoreCase("NEFT_RTGS")) {
                                  recordPaymentController.isNEFTRTGS.value = true;
                                  recordPaymentController.isChequeMode.value = false;
                                  recordPaymentController.isOnline.value = false;
                                  recordPaymentController.update();
                                } else {
                                  recordPaymentController.isOnline.value = false;
                                  recordPaymentController.isChequeMode.value = false;
                                  recordPaymentController.isNEFTRTGS.value = false;
                                  recordPaymentController.update();
                                }
                                recordPaymentController
                                    .getPaymentModeSourceType(
                                    recordPaymentController
                                        .selectedPayMode!.value);
                                recordPaymentController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    recordPaymentController.selectedPayMode ==
                                        null) {
                                  return Strings.please_select_pay_mode;
                                }
                                return null;
                              },
                            ),
                          ),


                          recordPaymentController.isSourceTypeDisable.value != true
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.source_type,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    // DropdownButtonFormField(
                                    //   icon: SvgPicture.asset(
                                    //     downArrowSvg,
                                    //     height: Constant.DROP_DOWN_ARROW_W_H,
                                    //     width: Constant.DROP_DOWN_ARROW_W_H,
                                    //     color: AppTheme.colorBlack,
                                    //     fit: BoxFit.fill,
                                    //   ),
                                    //   decoration: InputDecoration(
                                    //       filled: true,
                                    //       contentPadding:
                                    //           const EdgeInsets.fromLTRB(
                                    //               Constant.SMALL_PADDING,
                                    //               0,
                                    //               Constant.SMALL_PADDING,
                                    //               0),
                                    //       fillColor: AppTheme.colorWhite,
                                    //       hintText: Strings.please_select_pay_mode,
                                    //       hintStyle: AppTheme.dropdownHintStyle,
                                    //       labelStyle:
                                    //           AppTheme.dropdownLabelStyle,
                                    //       errorStyle:
                                    //           AppTheme.dropdownErrorStyle,
                                    //       // alignLabelWithHint: true,
                                    //       border: OutlineInputBorder(
                                    //         borderRadius: BorderRadius.circular(
                                    //             Constant
                                    //                 .DROP_DOWN_ROUNDED_CORNER),
                                    //         borderSide: BorderSide(
                                    //             color: AppTheme.colorBlack,
                                    //             width: 0.8),
                                    //       ),
                                    //       focusColor: Colors.transparent,
                                    //       focusedBorder: OutlineInputBorder(
                                    //         borderRadius: BorderRadius.circular(
                                    //             Constant
                                    //                 .DROP_DOWN_ROUNDED_CORNER),
                                    //         borderSide: BorderSide(
                                    //             color: AppTheme.colorBlack,
                                    //             width: 0.8),
                                    //       ),
                                    //       errorMaxLines: 1),
                                    //   style: AppTheme.dropdownTextStyle,
                                    //   isExpanded: false,
                                    //   isDense: false,
                                    //   value: recordPaymentController.selectPaymentModeSource,
                                    //   items: recordPaymentController.paymentModeSourceStatusList!
                                    //       .map((StatusDetail value) {
                                    //     return DropdownMenuItem<StatusDetail>(
                                    //       value: value,
                                    //       child: CustomText(
                                    //         title: value.text!,
                                    //         colors: AppTheme.colorBlack,
                                    //         textAlign: TextAlign.start,
                                    //         fontSize: AppTheme.small,
                                    //       ),
                                    //     );
                                    //   }).toList(),
                                    //   onChanged: (value) {
                                    //     recordPaymentController
                                    //             .selectPaymentModeSource =
                                    //         value as StatusDetail?;
                                    //     if (recordPaymentController
                                    //             .selectedPayMode!.text!
                                    //             .equalsIgnoreCase("Cash") &&
                                    //         recordPaymentController
                                    //             .selectPaymentModeSource!.value!
                                    //             .equalsIgnoreCase(
                                    //                 "Cash_via_Bank")) {
                                    //       recordPaymentController
                                    //           .isDestinationBank.value = true;
                                    //     } else {
                                    //       recordPaymentController
                                    //           .isDestinationBank.value = false;
                                    //     }
                                    //     recordPaymentController.update();
                                    //   },
                                    //   validator: (value) {
                                    //     if (value == null ||
                                    //         recordPaymentController
                                    //                 .selectPaymentModeSource ==
                                    //             null) {
                                    //       return Strings
                                    //           .please_select_source_type;
                                    //     }
                                    //     return null;
                                    //   },
                                    // ),

                                    DropdownButtonHideUnderline(
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
                                            child: CustomText(
                                                title:
                                                Strings.select_a_customer,
                                                colors:
                                                AppTheme.colorIconGrey)),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: false,
                                        isDense: false,
                                        value: recordPaymentController.selectPaymentModeSource,
                                        items: recordPaymentController.paymentModeSourceStatusList!
                                            .map((StatusDetail value) {
                                          return DropdownMenuItem<StatusDetail>(
                                            value: value,
                                            child: CustomText(
                                              title: value.text!,
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          recordPaymentController
                                              .selectPaymentModeSource =
                                          value as StatusDetail?;
                                          if (recordPaymentController
                                              .selectedPayMode!.text!
                                              .equalsIgnoreCase("Cash") &&
                                              recordPaymentController
                                                  .selectPaymentModeSource!.value!
                                                  .equalsIgnoreCase(
                                                  "Cash_via_Bank")) {
                                            recordPaymentController
                                                .isDestinationBank.value = true;
                                          } else {
                                            recordPaymentController
                                                .isDestinationBank.value = false;
                                          }
                                          recordPaymentController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              recordPaymentController
                                                  .selectPaymentModeSource ==
                                                  null) {
                                            return Strings
                                                .please_select_source_type;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.amount, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.amount,
                              textEditingController:
                                  recordPaymentController.amountController,
                              keyboardType: TextInputType.number,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_amount;
                                }
                                return null;
                              },
                              onChanged: (value) {
                                if (value.isEmpty) {
                                  recordPaymentController.tdsController.text =
                                      "0";
                                  recordPaymentController.abbsController.text =
                                      "0";
                                } else {
                                  // recordPaymentController.calculateABBSTDS();
                                }
                                recordPaymentController.update();
                              },
                              maxLength: 6,
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                    RegExp(r'^\d+\.?\d{0,2}')),
                              ],
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
//
                          recordPaymentController.isDestinationBank.value == true
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.destination_bank,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
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
                                        decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.LARGE_PADDING,
                                                    0,
                                                    Constant.LARGE_PADDING,
                                                    0),
                                            fillColor: AppTheme.colorWhite,
                                            hintText: Strings.destination_bank,
                                            hintStyle:
                                                AppTheme.dropdownHintStyle,
                                            labelStyle:
                                                AppTheme.dropdownLabelStyle,
                                            errorStyle:
                                                AppTheme.dropdownErrorStyle,
                                            alignLabelWithHint: true,
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            errorMaxLines: 3),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: recordPaymentController
                                            .selectedDestinationBank,
                                        items: recordPaymentController
                                            .destinationBankList!
                                            .map((BankDetail value) {
                                          return DropdownMenuItem<BankDetail>(
                                            value: value,
                                            child: CustomText(
                                              title:
                                                  "${value.displayName!} - ${value.accountnum ?? ""}",
                                              colors: AppTheme.colorBlack,
                                              textAlign: TextAlign.start,
                                              fontSize: AppTheme.small,
                                            ),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          recordPaymentController
                                                  .selectedDestinationBank =
                                              value as BankDetail?;

                                          log("bankManagementId==>${recordPaymentController.selectedDestinationBank!.id}");
                                          recordPaymentController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              recordPaymentController
                                                      .selectedDestinationBank ==
                                                  null) {
                                            return "${Strings.please_select_destination} ${Strings.bank.toLowerCase()}";
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.branch, require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_branch,
                                        textEditingController:
                                            recordPaymentController
                                                .branchController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        onChanged: (value) {
                                          // if (value.isEmpty) {
                                          //   recordPaymentController.tdsController.text = "0";
                                          // }
                                          recordPaymentController.update();
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: false),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          /// Online
                          recordPaymentController.isOnline.value == true
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.transaction_date,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.transaction_date,
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
                                            recordPaymentController
                                                .transactionDateController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .please_select_transaction_date;
                                          }
                                          return null;
                                        },
                                        onTextFiledOnTap: () {
                                          selectDate(
                                              context,
                                              Strings.transaction,
                                              DateTime(
                                                  DateTime.now().year - 10),
                                              DateTime(
                                                  DateTime.now().year + 10));
                                        },
                                        readOnly: true),
                                  ],
                                )
                              : const SizedBox.shrink(),

                        /// Direct Payment
                        recordPaymentController.isDirectPayment.value == true
                        ? Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        InputTitleRequire(
                            title: Strings.transaction_date,
                            require: true),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.transaction_date,
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
                            recordPaymentController
                                .transactionDateController,
                            borderEnableColors:
                            AppTheme.colorIconGrey,
                            borderFocusColors:
                            AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            contentPadding:
                            const EdgeInsets.symmetric(
                                horizontal:
                                Constant.MEDIUM_PADDING),
                            borderCorner:
                            Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings
                                    .please_select_transaction_date;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {
                              selectDate(
                                  context,
                                  Strings.transaction,
                                  DateTime(
                                      DateTime.now().year - 10),
                                  DateTime(
                                      DateTime.now().year + 10));
                            },
                            readOnly: true),
                      ],
                    )
                        : const SizedBox.shrink(),

                          ///Cheque
                          recordPaymentController.isChequeMode.value == true
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    /// Cheque No.
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.cheque_no,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.cheque_no,
                                        textEditingController:
                                            recordPaymentController
                                                .chequeNoController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (recordPaymentController
                                                      .isChequeMode.value ==
                                                  true &&
                                              value!.isEmpty) {
                                            return Strings.enter_cheque_no;
                                          }
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: false),

                                    /// Cheque Date
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.cheque_date,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.ddMMYYYY_format,
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
                                            recordPaymentController
                                                .chequeDateController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        fontSize: AppTheme.small,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {
                                          if (recordPaymentController
                                                      .isChequeMode.value ==
                                                  true &&
                                              value!.isEmpty) {
                                            return Strings
                                                .please_select_cheque_date;
                                          }
                                          return null;
                                        },
                                        onTextFiledOnTap: () {
                                          selectDate(
                                              context,
                                              Strings.cheque,
                                              DateTime(
                                                  DateTime.now().year - 10),
                                              DateTime(
                                                  DateTime.now().year + 10));
                                        },
                                        readOnly: true),

                                    /// Source Bank
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.source_bank,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
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
                                        decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.LARGE_PADDING,
                                                    0,
                                                    Constant.LARGE_PADDING,
                                                    0),
                                            fillColor: AppTheme.colorWhite,
                                            hintText: Strings.select_bank_name,
                                            hintStyle:
                                                AppTheme.dropdownHintStyle,
                                            labelStyle:
                                                AppTheme.dropdownLabelStyle,
                                            errorStyle:
                                                AppTheme.dropdownErrorStyle,
                                            alignLabelWithHint: true,
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            errorMaxLines: 3),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: recordPaymentController
                                            .selectedSourceBank,
                                        items: recordPaymentController
                                            .sourceBankList!
                                            .map((BankDetail value) {
                                          return DropdownMenuItem<BankDetail>(
                                            value: value,
                                            child: Text(
                                                "${value.displayName!} - ${value.accountnum ?? ""}"),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          recordPaymentController
                                                  .selectedSourceBank =
                                              value as BankDetail?;
                                          recordPaymentController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              recordPaymentController
                                                      .selectedSourceBank ==
                                                  null) {
                                            return Strings
                                                .please_enter_source_bank;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),

                                    /// Branch Name
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.branch_name,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_branch,
                                        textEditingController:
                                            recordPaymentController
                                                .branchController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        onChanged: (value) {
                                          // if (value.isEmpty) {
                                          //   recordPaymentController.tdsController.text = "0";
                                          // }
                                          recordPaymentController.update();
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: false),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          ///NEFT_RTGS
                          recordPaymentController.isNEFTRTGS.value == true
                              ? Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    /// Source Bank
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.source_bank,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
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
                                        decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.LARGE_PADDING,
                                                    0,
                                                    Constant.LARGE_PADDING,
                                                    0),
                                            fillColor: AppTheme.colorWhite,
                                            hintText: Strings.select_bank_name,
                                            hintStyle:
                                                AppTheme.dropdownHintStyle,
                                            labelStyle:
                                                AppTheme.dropdownLabelStyle,
                                            errorStyle:
                                                AppTheme.dropdownErrorStyle,
                                            alignLabelWithHint: true,
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            errorMaxLines: 3),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: recordPaymentController
                                            .selectedSourceBank,
                                        items: recordPaymentController
                                            .sourceBankList!
                                            .map((BankDetail value) {
                                          return DropdownMenuItem<BankDetail>(
                                            value: value,
                                            child: Text(
                                                "${value.displayName!} - ${value.accountnum ?? ""}"),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          recordPaymentController
                                                  .selectedSourceBank =
                                              value as BankDetail?;
                                          recordPaymentController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              recordPaymentController
                                                      .selectedSourceBank ==
                                                  null) {
                                            return Strings
                                                .please_enter_source_bank;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),

                                    /// Destination Bank
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.destination_bank,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
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
                                        decoration: InputDecoration(
                                            filled: true,
                                            contentPadding:
                                                const EdgeInsets.fromLTRB(
                                                    Constant.LARGE_PADDING,
                                                    0,
                                                    Constant.LARGE_PADDING,
                                                    0),
                                            fillColor: AppTheme.colorWhite,
                                            hintText: Strings.destination_bank,
                                            hintStyle:
                                                AppTheme.dropdownHintStyle,
                                            labelStyle:
                                                AppTheme.dropdownLabelStyle,
                                            errorStyle:
                                                AppTheme.dropdownErrorStyle,
                                            alignLabelWithHint: true,
                                            border: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            focusColor: Colors.transparent,
                                            focusedBorder: OutlineInputBorder(
                                              borderRadius:
                                                  BorderRadius.circular(Constant
                                                      .DROP_DOWN_ROUNDED_CORNER),
                                              borderSide: BorderSide(
                                                  color: AppTheme.colorBlack,
                                                  width: 0.8),
                                            ),
                                            errorMaxLines: 3),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: recordPaymentController
                                            .selectedDestinationBank,
                                        items: recordPaymentController
                                            .destinationBankList!
                                            .map((BankDetail value) {
                                          return DropdownMenuItem<BankDetail>(
                                            value: value,
                                            child: Text(
                                                "${value.displayName!} - ${value.accountnum ?? ""}"),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          recordPaymentController
                                                  .selectedDestinationBank =
                                              value as BankDetail?;
                                          recordPaymentController.update();
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              recordPaymentController
                                                      .selectedDestinationBank ==
                                                  null ||
                                              recordPaymentController
                                                  .selectedDestinationBank!
                                                  .isNullOrEmpty()) {
                                            return Strings
                                                .please_enter_destination_bank;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ],
                                )
                              : const SizedBox.shrink(),

                          /*getVisibilityBarterAmt()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBarterAmt()
                              ? InputTitleRequire(
                              title: Strings.barter_amount, require: true)
                              : Container(),
                          getVisibilityBarterAmt()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBarterAmt()
                              ? CoustomTextField(
                              labelText: Strings.barter_amount,
                              textEditingController: recordPaymentController
                                  .barterAmountController,
                              keyboardType: TextInputType.number,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_amount;
                                }
                                return null;
                              },
                              maxLength: 6,
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                    RegExp(r'^\d+\.?\d{0,2}')),
                              ],
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false)
                              : Container(),
                          getVisibilityPayRefNo()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityPayRefNo()
                              ? InputTitleRequire(
                              title: Strings.payment_reference_no,
                              require: false)
                              : Container(),
                          getVisibilityPayRefNo()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityPayRefNo()
                              ? CoustomTextField(
                              labelText: Strings.payment_reference_no,
                              textEditingController: recordPaymentController
                                  .paymentRefNoController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false)
                              : Container(),
                          getVisibilityCheck()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityCheck()
                              ? InputTitleRequire(
                              title: Strings.cheque_no, require: true)
                              : Container(),
                          getVisibilityCheck()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityCheck()
                              ? CoustomTextField(
                              labelText: Strings.cheque_no,
                              textEditingController: recordPaymentController
                                  .chequeNoController,
                              keyboardType: TextInputType.number,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_cheque_no;
                                }
                                return null;
                              },
                              maxLength: 6,
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              inputFormatters: [
                                FilteringTextInputFormatter.allow(
                                    RegExp(r'[0-9]'))
                              ],
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false)
                              : Container(),
                          getVisibilityCheck()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityCheck()
                              ? InputTitleRequire(
                              title: Strings.cheque_date, require: true)
                              : Container(),
                          getVisibilityCheck()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityCheck()
                              ? CoustomTextField(
                              labelText: Strings.cheque_date,
                              suffixIcon: Padding(
                                padding: const EdgeInsetsDirectional.all(
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
                              textEditingController: recordPaymentController
                                  .chequeDateController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_cheque_date;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                selectDate(
                                    context,
                                    Strings.cheque_date,
                                    DateTime(DateTime
                                        .now()
                                        .year - 10),
                                    DateTime(DateTime
                                        .now()
                                        .year + 10));
                              },
                              readOnly: true)
                              : Container(),
                          getVisibilityBankList()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBankList()
                              ? InputTitleRequire(
                              title: Strings.bank_name, require: true)
                              : Container(),
                          getVisibilityBankList()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBankList()
                              ? DropdownButtonHideUnderline(
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
                                  hintText: Strings.bank_name,
                                  hintStyle: AppTheme.dropdownHintStyle,
                                  labelStyle: AppTheme.dropdownLabelStyle,
                                  errorStyle: AppTheme.dropdownErrorStyle,
                                  alignLabelWithHint: true,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant
                                            .DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack,
                                        width: 0.8),
                                  ),
                                  focusColor: Colors.transparent,
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant
                                            .DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorBlack,
                                        width: 0.8),
                                  ),
                                  errorMaxLines: 3),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: true,
                              isDense: true,
                              value: recordPaymentController.selectedBank,
                              items: recordPaymentController.bankList!
                                  .map((BankDetail value) {
                                return DropdownMenuItem<BankDetail>(
                                  value: value,
                                  child: Text(value.bankname!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                recordPaymentController.selectedBank =
                                value as BankDetail?;
                                recordPaymentController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    recordPaymentController
                                        .selectedBank ==
                                        null) {
                                  return Strings.please_select_bank_name;
                                }
                                return null;
                              },
                            ),
                          )
                              : Container(),
                          getVisibilityBankName()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBankName()
                              ? InputTitleRequire(
                              title: Strings.bank_name, require: true)
                              : Container(),
                          getVisibilityBankName()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBankName()
                              ? CoustomTextField(
                              labelText: Strings.bank_name,
                              textEditingController:
                              recordPaymentController.bankController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false)
                              : Container(),
                          getVisibilityBranch()
                              ? const SizedBox(
                            height: Constant.SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBranch()
                              ? InputTitleRequire(
                              title: Strings.branch, require: true)
                              : Container(),
                          getVisibilityBranch()
                              ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                              : Container(),
                          getVisibilityBranch()
                              ? CoustomTextField(
                              labelText: Strings.branch,
                              textEditingController:
                              recordPaymentController.branchController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false)
                              : Container(),*/

                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.reference_no, require: false),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.reference_no,
                              textEditingController:
                                  recordPaymentController.refNoController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                // if (value!.isEmpty) {
                                //   return Strings.enter_reference_no;
                                // }
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.receipt_no, require: false),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.receipt_no,
                              textEditingController:
                                  recordPaymentController.receiptNoController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.LARGE_PADDING,
                          ),

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
                            height: Constant.MEDIUM_PADDING,
                          ),

                          /*  Row(
                            children: [
                              Flexible(
                                  flex: 1,
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    children: [
                                      tdsABBSWidget(Strings.TDS),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: "0",
                                          textEditingController:
                                              recordPaymentController
                                                  .tdsController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            return null;
                                          },
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.LARGE_PADDING),
                                          readOnly:
                                              recordPaymentController.tds ==
                                                      false
                                                  ? true
                                                  : false),
                                    ],
                                  )),
                              const SizedBox(
                                width: Constant.SMALL_PADDING,
                              ),
                              Flexible(
                                  flex: 1,
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                    children: [
                                      tdsABBSWidget(Strings.ABBS),
                                      const SizedBox(
                                        height: Constant.SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: "0",
                                          textEditingController:
                                              recordPaymentController
                                                  .abbsController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            return null;
                                          },
                                          borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                          contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal:
                                                      Constant.LARGE_PADDING),
                                          readOnly:
                                              recordPaymentController.abbs ==
                                                      false
                                                  ? true
                                                  : false),
                                    ],
                                  )),
                            ],
                          ),*/

                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.remarks, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(7.0),
                              // color: AppTheme.colorWhite,
                            ),
                            child: TextFormField(
                              controller:
                                  recordPaymentController.remarksController,
                              maxLines: 3,
                              maxLength: 250,
                              style: const TextStyle(fontSize: AppTheme.medium),
                              decoration: InputDecoration(
                                hintText: Strings.remarks,
                                alignLabelWithHint: true,
                                filled: true,
                                hoverColor: Colors.white,
                                fillColor: AppTheme.colorWhite,
                                contentPadding: const EdgeInsets.all(
                                    Constant.TEXT_FIELD_CONTENT_PADDING * 1.5),
                                focusColor: Colors.transparent,
                                focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(
                                      Constant.BTN_ROUNDED_CORNER),
                                  borderSide: BorderSide(
                                      color: AppTheme.colorPrimary, width: 1.0),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(
                                      Constant.BTN_ROUNDED_CORNER),
                                  borderSide: BorderSide(
                                    color: AppTheme.colorIconGrey,
                                    width: 1.0,
                                  ),
                                ),
                                border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.TEXT_FIELD_CONTENT_PADDING)),
                                isDense: true,
                                hintStyle: TextStyle(
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.normal,
                                    height: 1,
                                    color: AppTheme.colorGrey),
                                errorStyle: TextStyle(
                                  color: AppTheme.colorError,
                                  fontWeight: FontWeight.normal,
                                  fontSize: AppTheme.large - 1,
                                ),
                                labelStyle: TextStyle(
                                  color: AppTheme.colorGrey,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.normal,
                                  height: 1,
                                  fontFamily: AppTheme.appFontName,
                                  decoration: TextDecoration.none,
                                ),
                                counterText: "",
                              ),
                              keyboardType: TextInputType.multiline,
                              validator: (value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_remarks;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
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
                        title: Strings.add_payment,
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

  showInvoiceSelectionDialog(String from) {
    List<InvoiceDetail> item = [];
    // if (item.isEmpty) {
    //   item.add(InvoiceDetail(id: 0, docnumber: Strings.advance,totalamount: 0,adjustedAmount: 0,pendingAmt: 0));
    // }else {
    if (from.equalsIgnoreCase(Strings.invoice)) {
      if (recordPaymentController.invoiceList != null &&
          recordPaymentController.invoiceList!.isNotEmpty) {
        for (var element in recordPaymentController.invoiceList!) {
          element.selected = false;
        }
        if (recordPaymentController.selectedInvoice.isNotEmpty) {
          for (var element in recordPaymentController.invoiceList!) {
            for (int selElement in recordPaymentController.selectedInvoice) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(recordPaymentController.invoiceList!);
      }
    }
    // showDialog(
    //     context: context,
    //     barrierDismissible: true,
    //     builder: (BuildContext context) {
    //       return InvoiceSelectionDialog(
    //         invoiceSelectionAction: this,
    //         fromFor: from,
    //         itemsOrgLst: item,
    //         controller: recordPaymentController,
    //       );
    //     });

    // openRecordPaymentScreen() async {
    Get.to(
        SelectPaymentInvoiceItem(
          invoiceSelectionAction: this,
          fromFor: from,
          itemsOrgLst: item,
          controller: recordPaymentController,
        ),
        arguments: {});
    // }
  }

  tdsABBSWidget(String type) {
    return Container(
      margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
      child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            SizedBox(
              width: 12,
              height: 12,
              child: Checkbox(
                value: type.equalsIgnoreCase(Strings.TDS)
                    ? recordPaymentController.tds
                    : recordPaymentController.abbs,
                activeColor: AppTheme.colorPrimary,
                onChanged: (value) {
                  if (type.equalsIgnoreCase(Strings.TDS)) {
                    recordPaymentController.tds = !recordPaymentController.tds;
                    if (recordPaymentController.tds == false) {
                      recordPaymentController.tdsController.clear();
                    } else {
                      // recordPaymentController.calculateABBSTDS();
                    }
                  } else {
                    recordPaymentController.abbs =
                        !recordPaymentController.abbs;
                    if (recordPaymentController.abbs == false) {
                      recordPaymentController.abbsController.clear();
                    } else {
                      // recordPaymentController.calculateABBSTDS();
                    }
                  }
                  recordPaymentController.update();
                },
              ),
            ),
            const SizedBox(width: Constant.SMALL_PADDING),
            InkWell(
                child: CustomText(
                  title: type,
                  textAlign: TextAlign.start,
                  colors: AppTheme.colorBlack,
                  fontSize: AppTheme.medium - 1,
                  fontWeight: FontWeight.w400,
                ),
                onTap: () {
                  if (type.equalsIgnoreCase(Strings.TDS)) {
                    recordPaymentController.tds = !recordPaymentController.tds;
                    if (recordPaymentController.tds == false) {
                      recordPaymentController.tdsController.clear();
                    } else {
                      // recordPaymentController.calculateABBSTDS();
                    }
                  } else {
                    recordPaymentController.abbs =
                        !recordPaymentController.abbs;
                    if (recordPaymentController.abbs == false) {
                      recordPaymentController.abbsController.clear();
                    } else {
                      // recordPaymentController.calculateABBSTDS();
                    }
                  }
                  recordPaymentController.update();
                }),
          ]),
    );
  }

  checkCameraPermission() async {
    PermissionService().requestCameraAndStoragePermission(
        onPermissionDenied: () {
      if (Platform.isIOS) {
        //uploadImageOption();
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

  Future<void> selectDate(
    BuildContext context,
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.payment_date) {
      if (recordPaymentController.selectedPaymentDate != null) {
        selectedDate = recordPaymentController.selectedPaymentDate;
      } else {
        selectedDate = DateTime.now();
      }
    }

    if (identity == Strings.transaction) {
      if (recordPaymentController.selectedTransactionDate != null) {
        selectedDate = recordPaymentController.selectedTransactionDate;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.cheque) {
      if (recordPaymentController.selectedChequeDate != null) {
        selectedDate = recordPaymentController.selectedChequeDate;
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
      if (identity == Strings.payment_date) {
        recordPaymentController.selectedPaymentDate = picked;
        recordPaymentController.paymentDateController.text =
            recordPaymentController.dateFormat.format(picked);
        recordPaymentController.selectedPaymentDateApi =
            recordPaymentController.apiDateFormat.format(picked);
      }
      if (identity == Strings.transaction) {
        recordPaymentController.selectedTransactionDate = picked;
        recordPaymentController.transactionDateController.text =
            recordPaymentController.dateFormat.format(picked);
        recordPaymentController.selectedTransactionDateApi =
            recordPaymentController.apiDateFormat.format(picked);
      }
      if (identity == Strings.cheque) {
        recordPaymentController.selectedChequeDate = picked;
        recordPaymentController.chequeDateController.text =
            recordPaymentController.dateFormat.format(picked);
        recordPaymentController.selectedChequeDateApi =
            recordPaymentController.apiDateFormat.format(picked);
      }
      recordPaymentController.update();
    }
  }

  _appBar() {
    return DynamicAppBar(Strings.record_payment, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }

  validateForm() {
    if (recordPaymentFormKey.currentState!.validate()) {
      recordPaymentController.callRecordPaymentApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
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
      recordPaymentController.setBtnClickEvent(true);
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

  fileViewWidget() {
    return recordPaymentController.fileDetail != null
        ? FileGridItem(
            fileDetail: recordPaymentController.fileDetail!,
            onTapItem: () {},
            bottomAction: fileItemAction(),
          )
        : Container();
  }

  fileItemAction() {
    return recordPaymentController.fileDetail != null &&
            recordPaymentController.fileDetail!.isFileLocal == true
        ? Align(
            alignment: Alignment.topRight,
            child: InkWell(
              onTap: () {
                recordPaymentController.fileDetail = null;
                recordPaymentController.update();
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

  openCameraGallery(ImageSource source) async {
    try {
      XFile? image;
      image = await imagePicker.pickImage(source: source);

      if (image != null && !image.path.isNullOrEmpty()) {
        num size = await Utils.getFileSize(image.path, 1);
        print("image picker file size : ${size}");
        if (size <= 500) {
          recordPaymentController.fileDetail = FileDetail(
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
      recordPaymentController.update();
    } catch (e) {
      print("image picker exception : $e");
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
        recordPaymentController.fileDetail = FileDetail(
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
    recordPaymentController.update();
  }

  bool getVisibilityBarterAmt() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
            Constant.paymode_barter)) {
      visible = true;
    }

    return visible;
  }

  bool getVisibilityPayRefNo() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_online ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_efts)) {
      visible = true;
    }
    return visible;
  }

  bool getVisibilityCheck() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
            Constant.paymode_chequeh)) {
      visible = true;
    }
    return visible;
  }

  bool getVisibilityBankList() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_chequeh ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_direct_deposit ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_vat_receiveable ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_non_cash_adjustment ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_pos_adjustmnet ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_qr ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_opg_adjustment)) {
      visible = true;
    }

    return visible;
  }

  bool getVisibilityBankName() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_online ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_efts)) {
      visible = true;
    }
    return visible;
  }

  bool getVisibilityBranch() {
    bool visible = false;
    if (recordPaymentController.selectedPayMode != null &&
        (recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_chequeh ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_direct_deposit ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_vat_receiveable ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_non_cash_adjustment ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_pos_adjustmnet ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_qr ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_opg_adjustment ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_online ||
            recordPaymentController.selectedPayMode!.id! ==
                Constant.paymode_efts)) {
      visible = true;
    }

    return visible;
  }

  @override
  void invoiceSelectionBtnAction(
      {String? identifier, List<InvoiceDetail>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.invoice) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String invoiceName = "";
      recordPaymentController.selectedInvoice.clear();
      recordPaymentController.paymentListPojo.clear();
      recordPaymentController.amountController.clear();
      double totalValueShow = 0.0;
      for (int i = 0; i < selectedItem.length; i++) {
        InvoiceDetail element = selectedItem[i];


        if (element.tdsCheck == true && element.selected == true) {
          recordPaymentController.tdsValue!.value = double.parse(
              recordPaymentController.textEditControllersTDS[i].text);
          log("tdsValue===>>${recordPaymentController.tdsValue!.value}");
        }

        if (element.abbsCheck == true && element.selected == true) {
          recordPaymentController.abbsValue!.value = double.parse(
              recordPaymentController.textEditControllersABBS[i].text);
          log("abbsValue===>>${recordPaymentController.abbsValue!.value}");
        }

        log("element_TDSCHECK${recordPaymentController.textEditControllersTDS[i].text}");
        log("element_AbbsCheck${recordPaymentController.textEditControllersABBS[i].text}");
        log("element_Amount${recordPaymentController.textEditControllersAmount[i].text}");

        if (element.selected == true) {
          recordPaymentController.selectedInvoice.add(element.id!);
          recordPaymentController.paymentPojo = PaymentListPojos(
            tdsAmountAgainstInvoice:
                element.tdsCheck == true && element.selected == true
                    ? recordPaymentController.textEditControllersTDS[i].text
                    : 0.0,
            abbsAmountAgainstInvoice:
                element.abbsCheck == true && element.selected == true
                    ? recordPaymentController.textEditControllersABBS[i].text
                    : 0.0,
            amountAgainstInvoice: element.selected == true
                ? recordPaymentController.textEditControllersAmount[i].text
                : "0",
            invoiceId: element.id!,

          );
          recordPaymentController.paymentListPojo
              .add(recordPaymentController.paymentPojo);
          log("elementelement==>${element.testamount.toString()}");
          // recordPaymentController.amountController.text = element.testamount!.toStringAsFixed(2);
          totalValueShow += element.testamount!;
        }
        invoiceName = "$invoiceName${element.docnumber!}, ";
      }
      recordPaymentController.amountController.text = totalValueShow.toString();
      if (!invoiceName.isNullOrEmpty() &&
          invoiceName.contains(",") &&
          invoiceName.length >= 2) {
        invoiceName = invoiceName.substring(0, invoiceName.length - 2);
      }
      recordPaymentController.invoiceController.text = invoiceName;
    }
    recordPaymentController.update();
  }

  titleWithRequireWidget(String title, bool require) {
    return Row(
      children: [
        CustomText(
          title: title,
          colors: AppTheme.title_dark,
          textAlign: TextAlign.start,
          fontSize: AppTheme.small,
          fontWeight: FontWeight.normal,
        ),
        require
            ? CustomText(
                title: " *",
                colors: Colors.red,
                textAlign: TextAlign.start,
                fontSize: AppTheme.small,
                fontWeight: FontWeight.w600,
              )
            : Container(),
      ],
    );
  }

  openParentCustomerScreen() async {
    var result = await Get.to(CreditCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      // CustListDetails data = result;
      if (data != null) {
        recordPaymentController.newSelectedCustomer = data;
        log("openParentCustomerScreen>> ${data.id}");
        // recordPaymentController.getCreditInvoiceListData(data.id!);
        recordPaymentController.selectedInvoice = [];
        recordPaymentController.invoiceController.clear();
        recordPaymentController.getInvoiceListData(data.id!);
        recordPaymentController.createCustomerController.text = data.name!;
        recordPaymentController.update();
      }
    }
  }


}
