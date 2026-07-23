import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/credit_note/response/credit_invoice_list_res.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/credit_note/select_credit_note_invoice_dialog.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';
import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../util/resources.dart';
import '../../util/strings.dart';
import '../../widgets/coustom_text.dart';
import '../../widgets/dynamic_appbar.dart';
import '../../widgets/input_textfield.dart';
import '../../widgets/progress_bar.dart';
import '../../widgets/simple_button.dart';
import '../../widgets/title_widge.dart';
import 'create_credit_controller.dart';
import 'credit_customer_list.dart';

class CreateCreditNote extends StatefulWidget {
  @override
  _CreateCreditNoteState createState() => _CreateCreditNoteState();
}

class _CreateCreditNoteState extends State<CreateCreditNote>
    with WidgetsBindingObserver implements SelectCreditNoteInvoiceAction {
  final createCreditController = Get.find<CreateCreditController>();
  final createCreditFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
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
        // if (createCreditController.checkBtnClickEvent) {
        //   createCreditController.setBtnClickEvent(false);
        // }
        return;
      default:
        return;
    }
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<CreateCreditController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: createCreditController.isLoading),
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
        width: MediaQuery
            .of(context)
            .size
            .width,
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
                      key: createCreditFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.customer, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.select_a_customer,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createCreditController
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
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_customer;
                                } else {}
                                return null;
                              },
                              onTextFiledOnTap: () {
                                if (createCreditController.customerDetail == null) {
                                  openParentCustomerScreen();
                                }
                              },
                              readOnly: true),

                          /*_______________ invoice ______________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.invoice, require: true),
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
                                      Constant.MEDIUM_PADDING,
                                      Constant.MEDIUM_PADDING,
                                      Constant.MEDIUM_PADDING,
                                      Constant.MEDIUM_PADDING),
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
                              hint: CustomText(title: Strings.select_invoice,colors: AppTheme.colorLightBlack,),
                              value: createCreditController.selectedInvoice,
                              items: createCreditController.invoiceList!.isEmpty
                                  ? [
                                DropdownMenuItem<CreditInvoiceList>(
                                  value: null,
                                  enabled: false,
                                  child: CustomText(title: Strings.no_data_found,colors: AppTheme.title_dark,
                                  ), // Disable selection
                                ),
                              ] :
                              createCreditController.invoiceList!
                                  .map((CreditInvoiceList value) {
                                if (value.adjustedAmount == null) {
                                  createCreditController.pendingAmount =
                                  double.parse(value.totalamount.toString());
                                } else if (value.adjustedAmount != null) {
                                  createCreditController.pendingAmount =
                                  double.parse(value.totalamount.toString()) - double.parse(value.adjustedAmount.toString());
                                }
                                return DropdownMenuItem<CreditInvoiceList>(
                                  value: value,
                                  child: CustomText(
                                    title: "${value.docnumber ?? ""} | ${value
                                        .tax.toStringAsFixed(2)} | ${value.totalamount.toStringAsFixed(2)} | ${createCreditController.pendingAmount!.toStringAsFixed(2).toString()} | ${value.refundAbleAmount}",
                                    colors: AppTheme.title_dark,
                                    fontSize: AppTheme.small,),
                                );
                              }).toList(),
                              onChanged: (value) {
                                createCreditController.selectedInvoice =
                                value as CreditInvoiceList?;
                                createCreditController.creditAmount.isEmpty;
                                createCreditController.selectedInvoiceIds
                                    .clear();
                                log("selectedInvoiceAjustedamoumt =>${value!
                                    .adjustedAmount}");
                                log("selectedInvoiceTotalAmount =>${value
                                    .totalamount}");

                                double? finalPendingAmount = 0.0;
                                if (value.adjustedAmount != null) {
                                  finalPendingAmount = double.parse(
                                      value.totalamount.toString()) -
                                      double.parse(
                                          value.adjustedAmount.toString());
                                } else {
                                  finalPendingAmount =
                                      double.parse(value.totalamount);
                                }
                                // createCreditController.creditAmount.value = finalPendingAmount.toString();
                                createCreditController.creditAmount.value =
                                    value.refundAbleAmount.toString();
                                createCreditController.selectedInvoiceIds.add(
                                    value.id!);

                                log("selectedInvoiceIds==>>>${jsonEncode(
                                    createCreditController
                                        .selectedInvoiceIds)}");
                                createCreditController.update();
                              },
                              validator: (value) {
                                if (value == null ||
                                    createCreditController.selectedInvoice ==
                                        null ||
                                    createCreditController
                                        .selectedInvoice?.id ==
                                        0) {
                                  return Strings.please_select_invoice;
                                }
                                return null;
                              },
                            ),
                          ),

                          // CoustomTextField(
                          //     labelText: Strings.select_invoice,
                          //     hintColor: AppTheme.colorIconGrey,
                          //     textEditingController:
                          //     createCreditController.invoiceController,
                          //     borderEnableColors:
                          //     AppTheme.colorIconGrey,
                          //     borderFocusColors: AppTheme.colorIconGrey,
                          //     textColor: AppTheme.colorBlack,
                          //     keyboardType: TextInputType.text,
                          //     fontSize: AppTheme.small,
                          //     textInputAction: TextInputAction.next,
                          //     fontWeight: FontWeight.w500,
                          //     contentPadding:
                          //     const EdgeInsets.symmetric(
                          //         horizontal:
                          //         Constant.MEDIUM_PADDING,
                          //         vertical:
                          //         Constant.MEDIUM_PADDING),
                          //     borderCorner: Constant.BTN_ROUNDED_CORNER,
                          //     onTextValidator: (String? value) {
                          //       /* if (value!.isEmpty) {
                          //                 return Strings
                          //                     .please_select_parent_service_area;
                          //               }*/
                          //     },
                          //     onTextFiledOnTap: () {
                          //       showDialog(
                          //           context: context,
                          //           barrierDismissible: true,
                          //           builder: (BuildContext context) {
                          //             return SelectCreditNoteInvoiceDialog(
                          //                 creditNoteSelectionAction: this, fromFor: Strings.add, creditInvoiceList: createCreditController.invoiceList);
                          //           });
                          //     },
                          //     readOnly: true),

                          /*_________________ amount ___________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.amount, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          Obx(() =>
                              CoustomTextField(
                                  labelText: createCreditController.creditAmount
                                      .toString(),
                                  hintColor: AppTheme.colorIconGrey,
                                  textEditingController: TextEditingController()
                                    ..text = createCreditController.creditAmount
                                        .toString()
                                    ..selection = TextSelection.collapsed(
                                        offset: createCreditController
                                            .creditAmount
                                            .toString()
                                            .length),
                                  borderEnableColors: AppTheme.colorIconGrey,
                                  borderFocusColors: AppTheme.colorIconGrey,
                                  textColor: AppTheme.colorBlack,
                                  keyboardType: TextInputType.text,
                                  fontSize: AppTheme.small + 1,
                                  textInputAction: TextInputAction.done,
                                  fontWeight: FontWeight.w400,
                                  contentPadding: const EdgeInsets.symmetric(
                                      horizontal: Constant.MEDIUM_PADDING,
                                      vertical: Constant.MEDIUM_PADDING),
                                  borderCorner: Constant.BTN_ROUNDED_CORNER,
                                  onTextValidator: (String? value) {
                                    if (value!.isEmpty) {
                                      return Strings.amount_required;
                                    }
                                    return null;
                                  },
                                  onChanged: (value) {
                                    createCreditController.creditAmount.value =
                                        value;
                                    createCreditController.update();
                                  },
                                  readOnly: false),
                          ),

                          /*_______________ reference no___________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.reference_no, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.reference_no,
                              hintColor: AppTheme.colorIconGrey,
                              textEditingController: createCreditController
                                  .referenceNumberController,
                              borderEnableColors: AppTheme.colorIconGrey,
                              borderFocusColors: AppTheme.colorIconGrey,
                              textColor: AppTheme.colorBlack,
                              keyboardType: TextInputType.text,
                              fontSize: AppTheme.small + 1,
                              maxLength: 250,
                              maxLines: 3,
                              textInputAction: TextInputAction.done,
                              fontWeight: FontWeight.w400,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.MEDIUM_PADDING,
                                  vertical: Constant.MEDIUM_PADDING),
                              borderCorner: Constant.BTN_ROUNDED_CORNER,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_reference_no;
                                } else {}
                                return null;
                              },
                              readOnly: false),

                          /*___________________ Remarks___________________________*/

                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.remarks, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          Container(
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(8.0),
                              color: AppTheme.colorWhite,
                            ),
                            child: TextFormField(
                              controller: createCreditController
                                  .remarksController,
                              maxLines: 4,
                              maxLength: 250,
                              style: const TextStyle(fontSize: AppTheme.medium),
                              decoration: InputDecoration(
                                hintText: Strings.enter_remarks,
                                alignLabelWithHint: true,
                                contentPadding:
                                const EdgeInsets.all(
                                    Constant.TEXT_FIELD_CONTENT_PADDING),
                                focusColor: Colors.transparent,
                                focusedBorder: OutlineInputBorder(
                                  borderRadius:
                                  BorderRadius.circular(
                                      Constant.BTN_ROUNDED_CORNER),
                                  borderSide:
                                  BorderSide(
                                      color: AppTheme.colorPrimary, width: 1.0),
                                ),
                                enabledBorder: OutlineInputBorder(
                                  borderRadius:
                                  BorderRadius.circular(
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
                        title: Strings.add_credit_note,
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
        Strings.generate_credit_note,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (createCreditFormKey.currentState!.validate()) {
      createCreditController.callRecordPaymentApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  openParentCustomerScreen() async {
    var result = await Get.to(CreditCustomerList(), arguments: {});
    if (result != null) {
      CustomerCreditList data = result;
      if (data != null) {
        createCreditController.selectedCustomer = data;
        createCreditController.selectedInvoice = null;
        createCreditController.invoiceList!.clear();
        log("openParentCustomerScreen>> ${data.id}");
        createCreditController.getCreditInvoiceListData(data.id!);
        createCreditController.createCustomerController.text = data.name!;
        createCreditController.update();
      }
    }
  }

  @override
  void creditNoteSelectionBtnAction({String? identifier, List<CreditInvoiceList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.add) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String teamName = "";
      createCreditController.selectedAllTeamInventoryList!.clear();
      for (CreditInvoiceList element in selectedItem) {
        createCreditController.selectedAllTeamInventoryList!.add(element.id!);
        teamName = "$teamName${element.docnumber!}, ";
      }

      if (!teamName.isNullOrEmpty() &&
          teamName.contains(",") &&
          teamName.length >= 2) {
        teamName = teamName.substring(0, teamName.length - 2);
      }
      createCreditController.invoiceController.text = teamName;
    }
    createCreditController.update();
  }

}
