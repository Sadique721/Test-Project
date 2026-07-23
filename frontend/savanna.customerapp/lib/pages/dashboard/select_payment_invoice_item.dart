import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/pages/dashboard/record_payment_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';

class SelectPaymentInvoiceItem extends StatefulWidget {
  final InvoiceSelectionAction invoiceSelectionAction;
  final List<InvoiceDetail> itemsOrgLst;
  final String fromFor;
  final RecordPaymentController controller;

  const SelectPaymentInvoiceItem({
    Key? key,
    required this.invoiceSelectionAction,
    required this.itemsOrgLst,
    required this.fromFor,
    required this.controller,
  }) : super(key: key);

  @override
  _SelectPaymentInvoiceState createState() => _SelectPaymentInvoiceState();
}

class _SelectPaymentInvoiceState extends State<SelectPaymentInvoiceItem> {
  List<InvoiceDetail> itemsLst = [];
  final selectPaymentInvoiceFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
    setState(() {
      itemsLst.addAll(widget.itemsOrgLst);
    });
  }

  @override
  void dispose() {
    widget.controller.textEditControllersAmount.clear();
    super.dispose();
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
          ProgressBar(isLoader: controller.isLoading),
        ]);
      }),
    );
  }

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _appBar() {
    return DynamicAppBar(Strings.select_invoice, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
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
                  child: Container(
                    width: MediaQuery.of(context).size.width,
                    color: AppTheme.colorTransparent,
                    child: Form(
                      key: selectPaymentInvoiceFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                          mainAxisSize: MainAxisSize.min,
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Padding(
                              padding: const EdgeInsets.only(
                                top: Constant.SCREEN_PADDING,
                                left: Constant.SCREEN_PADDING,
                                right: Constant.SCREEN_PADDING,
                              ),
                              child: Align(
                                alignment: Alignment.centerLeft,
                                child: CustomText(
                                  title: Strings.select_invoice,
                                  colors: AppTheme.title_dark,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                            const SizedBox(height: Constant.SMALL_PADDING),
                            ListView.builder(
                                shrinkWrap: true,
                                primary: false,
                                itemCount: itemsLst.length,
                                itemBuilder: (context, index) {
                                  // widget.controller.textEditControllers.add(TextEditingController());
                                  widget.controller.textEditControllersTDS
                                      .add(TextEditingController());
                                  widget.controller.textEditControllersABBS
                                      .add(TextEditingController());
                                  widget.controller.textEditControllersAmount
                                      .add(TextEditingController());
                                  InvoiceDetail? item;
                                  if (itemsLst.isNotEmpty) {
                                    item = itemsLst[index];
                                    widget
                                            .controller
                                            .textEditControllersAmount[index]
                                            .text =
                                        item.testamount!.toStringAsFixed(2);
                                  }
                                  return Column(
                                    children: [
                                      InkWell(
                                        onTap: () {
                                          for (var f in itemsLst) {
                                            if (f.id == item!.id!) {
                                              if (f.selected == null) {
                                                f.selected = true;
                                              } else {
                                                f.selected = !f.selected!;
                                              }
                                              break;
                                            }
                                          }
                                          setState(() {
                                            itemsLst = itemsLst;
                                          });
                                        },
                                        child: Padding(
                                            padding: const EdgeInsets.symmetric(
                                                vertical:
                                                    Constant.VERY_SMALL_PADDING,
                                                horizontal:
                                                    Constant.MEDIUM_PADDING),
                                            child: Card(
                                              color: item!.selected != null &&
                                                      item.selected == true
                                                  ? AppTheme.useCardBg
                                                  : AppTheme.colorWhite,
                                              child: Padding(
                                                padding: const EdgeInsets.only(
                                                  top: Constant
                                                      .EXPANTABLE_ITEM_MARGIN,
                                                  left: Constant
                                                      .EXPANTABLE_ITEM_MARGIN,
                                                  right: Constant
                                                      .EXPANTABLE_ITEM_MARGIN,
                                                  bottom: 0,
                                                ),
                                                child: Container(
                                                  alignment: Alignment.topLeft,
                                                  padding: const EdgeInsets.all(
                                                      Constant.SMALL_PADDING),
                                                  child: Column(
                                                      mainAxisAlignment:
                                                          MainAxisAlignment
                                                              .start,
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      children: [
                                                        basicDetailItem(
                                                          Strings.document_no,
                                                          item.docnumber ?? "-",
                                                          Strings.create_by,
                                                          item.createdByName ??
                                                              "-",
                                                          Strings.tax_amount,
                                                          item.tax.toString() ??
                                                              "-",
                                                        ),
                                                        const SizedBox(
                                                          height: Constant
                                                              .SMALL_PADDING,
                                                        ),
                                                        basicDetailItem(
                                                          Strings.total_invoice,
                                                          item.totalamount
                                                                  ?.toStringAsFixed(
                                                                      2) ??
                                                              "-",
                                                          Strings
                                                              .pending_amount_new,
                                                          (item.document !=
                                                                      null &&
                                                                  (item
                                                                      .docnumber
                                                                      .toString()
                                                                      .equalsIgnoreCase(
                                                                          Strings
                                                                              .advance)))
                                                              ? 0
                                                              : getPendingAmount(
                                                                  item),
                                                          Strings.ref_amount,
                                                          item.refundAbleAmount
                                                                  ?.toString() ??
                                                              "-",
                                                        ),
                                                        const SizedBox(
                                                          height: Constant
                                                              .SMALL_PADDING,
                                                        ),
                                                        Row(
                                                          mainAxisSize:
                                                              MainAxisSize.max,
                                                          crossAxisAlignment:
                                                              CrossAxisAlignment
                                                                  .end,
                                                          mainAxisAlignment:
                                                              MainAxisAlignment
                                                                  .spaceBetween,
                                                          children: [
                                                            Expanded(
                                                              flex: 1,
                                                              child: Column(
                                                                mainAxisAlignment:
                                                                    MainAxisAlignment
                                                                        .start,
                                                                crossAxisAlignment:
                                                                    CrossAxisAlignment
                                                                        .start,
                                                                children: [
                                                                  titleWidget(
                                                                    Strings
                                                                        .amount,
                                                                  ),
                                                                  const SizedBox(
                                                                    height: Constant
                                                                        .SMALL_PADDING,
                                                                  ),
                                                                  // CoustomTextField(
                                                                  //   labelText:
                                                                  //       Strings
                                                                  //           .amount,
                                                                  //
                                                                  //   textEditingController: widget
                                                                  //           .controller
                                                                  //           .textEditControllersAmount[
                                                                  //       index],
                                                                  //   keyboardType:
                                                                  //       TextInputType
                                                                  //           .number,
                                                                  //   borderEnableColors:
                                                                  //       AppTheme
                                                                  //           .colorBlack,
                                                                  //   textInputAction:
                                                                  //       TextInputAction
                                                                  //           .next,
                                                                  //   hintColor:
                                                                  //       AppTheme
                                                                  //           .colorIconGrey,
                                                                  //   onTextValidator:
                                                                  //       (String?
                                                                  //           value) {
                                                                  //     if (value!
                                                                  //         .isEmpty) {
                                                                  //       return Strings
                                                                  //           .enter_amount;
                                                                  //     }
                                                                  //     item.totalamount =
                                                                  //         double.parse(
                                                                  //             value.toString());
                                                                  //     return item
                                                                  //         .totalamount
                                                                  //         .toString();
                                                                  //   },
                                                                  //   onChanged:
                                                                  //       (value) {
                                                                  //     if (value
                                                                  //         .isEmpty) {
                                                                  //       widget
                                                                  //           .controller
                                                                  //           .tdsController
                                                                  //           .text = "0";
                                                                  //       widget
                                                                  //           .controller
                                                                  //           .abbsController
                                                                  //           .text = "0";
                                                                  //     } else {
                                                                  //       widget.controller.calculateABBSTDS(
                                                                  //           item.totalamount
                                                                  //               .toString(),
                                                                  //           index);
                                                                  //     }
                                                                  //     widget
                                                                  //         .controller
                                                                  //         .update();
                                                                  //   },
                                                                  //   borderCorner:
                                                                  //       Constant
                                                                  //           .INPUT_ROUNDED_CORNER,
                                                                  //
                                                                  //   // inputFormatters: [
                                                                  //   //   FilteringTextInputFormatter
                                                                  //   //       .allow(RegExp(
                                                                  //   //           r'^\d+\.?\d{0,2}')),
                                                                  //   // ],
                                                                  //   contentPadding: const EdgeInsets
                                                                  //           .symmetric(
                                                                  //       horizontal:
                                                                  //           Constant
                                                                  //               .LARGE_PADDING),
                                                                  // ),

                                                                  CoustomTextField(
                                                                    labelText:
                                                                        Strings
                                                                            .amount,
                                                                    textEditingController: widget
                                                                        .controller
                                                                        .textEditControllersAmount[index],
                                                                    // textEditingController: widget
                                                                    //     .controller
                                                                    //     .textEditControllersAmount[index],
                                                                    keyboardType:
                                                                        TextInputType
                                                                            .number,
                                                                    borderEnableColors:
                                                                        AppTheme
                                                                            .colorBlack,
                                                                    textInputAction:
                                                                        TextInputAction
                                                                            .next,
                                                                    hintColor:
                                                                        AppTheme
                                                                            .colorIconGrey,
                                                                    onTextValidator:
                                                                        (String?
                                                                            value) {
                                                                      return null;
                                                                    },
                                                                    onSubmitted:
                                                                        (value) {
                                                                      // widget.controller.textEditControllersAmount[index].text = value;
                                                                      item!.testamount =
                                                                          double.tryParse(
                                                                              value);
                                                                    },
                                                                    onChanged:
                                                                        (value) {
                                                                      item!.testamount =
                                                                          double.tryParse(
                                                                              value);
                                                                      // widget.controller.updateControllerValue(index, value);

                                                                      // if (value
                                                                      //     .isEmpty) {
                                                                      //   widget
                                                                      //       .controller
                                                                      //       .tdsController
                                                                      //       .text = "0";
                                                                      //   widget
                                                                      //       .controller
                                                                      //       .abbsController
                                                                      //       .text = "0";
                                                                      // } else {
                                                                      //   widget.controller.calculateABBSTDS(
                                                                      //       item!.totalamount.toString(),
                                                                      //       index);
                                                                      // }
                                                                      // widget
                                                                      //     .controller
                                                                      //     .update();
                                                                    },
                                                                    borderCorner:
                                                                        Constant
                                                                            .INPUT_ROUNDED_CORNER,
                                                                    contentPadding: const EdgeInsets
                                                                        .symmetric(
                                                                        horizontal:
                                                                            Constant.LARGE_PADDING),
                                                                    readOnly:
                                                                        false,
                                                                    isEnable:
                                                                        true,
                                                                  ),
                                                                ],
                                                              ),
                                                            ),
                                                            const SizedBox(
                                                                width: Constant
                                                                    .VERY_SMALL_PADDING),
                                                            Expanded(
                                                              flex: 1,
                                                              // fit: FlexFit.tight,
                                                              child: Column(
                                                                mainAxisAlignment:
                                                                    MainAxisAlignment
                                                                        .start,
                                                                crossAxisAlignment:
                                                                    CrossAxisAlignment
                                                                        .start,
                                                                children: [
                                                                  CheckboxListTile(
                                                                      activeColor:
                                                                          AppTheme
                                                                              .colorPrimary,
                                                                      // tileColor: item.docnumber!.equalsIgnoreCase(Strings
                                                                      //         .advance)
                                                                      //     ? AppTheme
                                                                      //         .colorGrayTxtBg
                                                                      //     : AppTheme
                                                                      //         .colorTransparent,
                                                                      // enabled: item.docnumber!.equalsIgnoreCase(Strings
                                                                      //         .advance)
                                                                      //     ? false
                                                                      //     : true,
                                                                      dense:
                                                                          true,
                                                                      title:
                                                                          CustomText(
                                                                        title: Strings
                                                                            .TDS,
                                                                        colors:
                                                                            AppTheme.title_dark,
                                                                        fontSize:
                                                                            AppTheme.verySmall,
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                      ),
                                                                      value: item
                                                                          .tdsCheck,
                                                                      onChanged:
                                                                          (bool?
                                                                              val) {
                                                                        setState(
                                                                            () {
                                                                          item!.tdsCheck =
                                                                              (val!);
                                                                          widget
                                                                              .controller
                                                                              .tds = !val;
                                                                          // if (type.equalsIgnoreCase(Strings.TDS)) {
                                                                          widget
                                                                              .controller
                                                                              .tds = !widget.controller.tds;
                                                                          if (widget.controller.tds == false &&
                                                                              item!.tdsCheck == false) {
                                                                            // widget.controller.textEditControllers[index].clear();
                                                                            widget.controller.textEditControllersTDS[index].clear();
                                                                          } else {
                                                                            widget.controller.calculateABBSTDS(item.totalamount.toString(),
                                                                                index);
                                                                          }
                                                                          widget
                                                                              .controller
                                                                              .update();
                                                                          // }
                                                                        });
                                                                        widget
                                                                            .controller
                                                                            .update();
                                                                      }),
                                                                  CoustomTextField(
                                                                      // fillColor: item.docnumber!.equalsIgnoreCase(Strings.advance)
                                                                      //     ? AppTheme
                                                                      //         .colorGrayTxtBg
                                                                      //     : AppTheme
                                                                      //         .colorWhite,
                                                                      labelText:
                                                                          "0.0",
                                                                      // textEditingController: widget.controller.textEditControllers[index],
                                                                      textEditingController: widget
                                                                              .controller
                                                                              .textEditControllersTDS[
                                                                          index],
                                                                      keyboardType:
                                                                          TextInputType
                                                                              .number,
                                                                      borderEnableColors:
                                                                          AppTheme
                                                                              .colorBlack,
                                                                      textInputAction:
                                                                          TextInputAction
                                                                              .next,
                                                                      hintColor:
                                                                          AppTheme
                                                                              .colorIconGrey,
                                                                      onTextValidator:
                                                                          (String?
                                                                              value) {
                                                                        return null;
                                                                      },
                                                                      borderCorner:
                                                                          Constant
                                                                              .INPUT_ROUNDED_CORNER,
                                                                      contentPadding:
                                                                          const EdgeInsets
                                                                              .symmetric(
                                                                              horizontal: Constant
                                                                                  .LARGE_PADDING),
                                                                      readOnly: widget.controller.tds ==
                                                                              false
                                                                          ? true
                                                                          : false),
                                                                ],
                                                              ),
                                                            ),
                                                            const SizedBox(
                                                                width: Constant
                                                                    .VERY_SMALL_PADDING),
                                                            Expanded(
                                                              flex: 1,
                                                              child: Column(
                                                                mainAxisAlignment:
                                                                    MainAxisAlignment
                                                                        .start,
                                                                crossAxisAlignment:
                                                                    CrossAxisAlignment
                                                                        .start,
                                                                children: [
                                                                  CheckboxListTile(
                                                                      activeColor:
                                                                          AppTheme
                                                                              .colorPrimary,
                                                                      // tileColor: item.docnumber!.equalsIgnoreCase(Strings
                                                                      //         .advance)
                                                                      //     ? AppTheme
                                                                      //         .colorGrayTxtBg
                                                                      //     : AppTheme
                                                                      //         .colorTransparent,
                                                                      // enabled: item.docnumber!.equalsIgnoreCase(Strings
                                                                      //         .advance)
                                                                      //     ? false
                                                                      //     : true,
                                                                      dense:
                                                                          true,
                                                                      title:
                                                                          CustomText(
                                                                        title: Strings
                                                                            .ABBS,
                                                                        colors:
                                                                            AppTheme.title_dark,
                                                                        fontSize:
                                                                            AppTheme.verySmall,
                                                                        fontWeight:
                                                                            FontWeight.w600,
                                                                      ),
                                                                      value: item
                                                                          .abbsCheck,
                                                                      onChanged:
                                                                          (bool?
                                                                              val) {
                                                                        setState(
                                                                            () {
                                                                          item!.abbsCheck =
                                                                              (val!);
                                                                          widget
                                                                              .controller
                                                                              .abbs = !val;
                                                                          // if (type.equalsIgnoreCase(Strings.TDS)) {
                                                                          widget
                                                                              .controller
                                                                              .abbs = !widget.controller.abbs;
                                                                          if (widget.controller.abbs == false &&
                                                                              item.abbsCheck == false) {
                                                                            // widget.controller.textEditControllers[index].clear();
                                                                            widget.controller.textEditControllersABBS[index].clear();
                                                                          } else {
                                                                            widget.controller.calculateABBSTDS(item.totalamount.toString(),
                                                                                index);
                                                                          }
                                                                          widget
                                                                              .controller
                                                                              .update();
                                                                          // }
                                                                        });
                                                                        widget
                                                                            .controller
                                                                            .update();
                                                                      }),
                                                                  CoustomTextField(
                                                                      // fillColor: item.docnumber!.equalsIgnoreCase(Strings.advance)
                                                                      //     ? AppTheme
                                                                      //     .colorGrayTxtBg
                                                                      //     : AppTheme
                                                                      //     .colorWhite,
                                                                      labelText:
                                                                          "0.0",
                                                                      // textEditingController: widget.controller.textEditControllers[index],
                                                                      textEditingController: widget
                                                                              .controller
                                                                              .textEditControllersABBS[
                                                                          index],
                                                                      keyboardType:
                                                                          TextInputType
                                                                              .number,
                                                                      borderEnableColors:
                                                                          AppTheme
                                                                              .colorBlack,
                                                                      textInputAction:
                                                                          TextInputAction
                                                                              .next,
                                                                      hintColor:
                                                                          AppTheme
                                                                              .colorIconGrey,
                                                                      onTextValidator:
                                                                          (String?
                                                                              value) {
                                                                        return null;
                                                                      },
                                                                      borderCorner:
                                                                          Constant
                                                                              .INPUT_ROUNDED_CORNER,
                                                                      contentPadding:
                                                                          const EdgeInsets
                                                                              .symmetric(
                                                                              horizontal: Constant
                                                                                  .LARGE_PADDING),
                                                                      readOnly: widget.controller.tds ==
                                                                              false
                                                                          ? true
                                                                          : false),
                                                                ],
                                                              ),
                                                            ),
                                                          ],
                                                        ),
                                                        const SizedBox(
                                                          width: Constant
                                                              .MEDIUM_PADDING,
                                                        ),
                                                      ]),
                                                ),
                                              ),
                                            )),
                                      ),
                                    ],
                                  );
                                }),
                            const SizedBox(height: Constant.SMALL_PADDING),
                          ]),
                    ),
                  ),
                ),
              ),
              Row(
                children: [
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        validateSelection();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT - 5,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.select,
                        fontWeight: FontWeight.bold,
                        fontSize: AppTheme.medium + 1,
                      ),
                    ),
                  ),
                  Expanded(
                    child: SimpleButton(
                      onTap: () {
                        Get.back();
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT - 5,
                      bgColors: AppTheme.colorTransparent,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.cancel,
                        fontWeight: FontWeight.bold,
                        colors: AppTheme.colorNagative,
                        fontSize: AppTheme.medium + 1,
                      ),
                    ),
                  ),
                ],
              ),
            ]),
      ),
    );
  }

  _backScreen() {
    Get.back(result: false);
  }

  validateSelection() {
    List<InvoiceDetail> selectedItem = [];
    for (var element in itemsLst) {
      if (element.selected != null && element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      log("selectedItem==>${jsonEncode(selectedItem)}");
      widget.invoiceSelectionAction.invoiceSelectionBtnAction(
          identifier: widget.fromFor, selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.INFO, Strings.select_at_list_one_item,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }
  }
}

basicDetailItem(String title1, String? value1, String title2, String? value2,
    String title3, String? value3) {
  return Row(
    mainAxisSize: MainAxisSize.max,
    crossAxisAlignment: CrossAxisAlignment.start,
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title1),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value1),
          ],
        ),
      ),
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title2),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value2),
          ],
        ),
      ),
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            titleWidget(title3),
            const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            valueWidget(value3),
          ],
        ),
      ),
    ],
  );
}

amountTdsAbbsItem(
    String title1,
    String value1,
    String title2,
    bool? tdsCheckValue,
    String title3,
    InvoiceDetail item,
    RecordPaymentController? controller,
    int index) {
  return Row(
    mainAxisSize: MainAxisSize.max,
    crossAxisAlignment: CrossAxisAlignment.center,
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: Constant.VERY_SMALL_PADDING),
            titleWidget(title1),
            const SizedBox(height: Constant.VERY_SMALL_PADDING + 1),
            // valueWidget(Strings.TDS),
            CoustomTextField(
                labelText: Strings.amount,
                // textEditingController:
                // widget.controller
                //     .amountController,
                // textEditingController: TextEditingController(text: value1),
                textEditingController: controller!.amountController,
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
                  log("valueEdit==>${value}");
                  if (value.isEmpty) {
                    controller!.tdsController.text = "0";
                    controller.abbsController.text = "0";
                  } else {
                    controller!.calculateABBSTDS(value, index);
                  }
                  controller.update();
                },
                maxLength: 6,
                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                inputFormatters: [
                  FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,2}')),
                ],
                contentPadding: const EdgeInsets.symmetric(
                    horizontal: Constant.LARGE_PADDING),
                readOnly: false),
          ],
        ),
      ),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      Flexible(
          flex: 1,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              tdsABBSWidget(
                  Strings.TDS, item.totalamount.toString(), controller!, index),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              CoustomTextField(
                  labelText: "0",
                  textEditingController: controller.tdsController,
                  keyboardType: TextInputType.number,
                  borderEnableColors: AppTheme.colorBlack,
                  textInputAction: TextInputAction.next,
                  hintColor: AppTheme.colorIconGrey,
                  onTextValidator: (String? value) {
                    return null;
                  },
                  borderCorner: Constant.INPUT_ROUNDED_CORNER,
                  contentPadding: const EdgeInsets.symmetric(
                      horizontal: Constant.LARGE_PADDING),
                  readOnly: controller.tds == false ? true : false),
            ],
          )),
      const SizedBox(
        width: Constant.SMALL_PADDING,
      ),
      Flexible(
        flex: 1,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.end,
          children: [
            // titleWidget(Strings.ABBS),
            // const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
            tdsABBSWidget(
                Strings.ABBS, item.totalamount.toString(), controller, index),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            CoustomTextField(
                labelText: "0",
                textEditingController: controller.abbsController,
                keyboardType: TextInputType.number,
                borderEnableColors: AppTheme.colorBlack,
                textInputAction: TextInputAction.next,
                hintColor: AppTheme.colorIconGrey,
                onTextValidator: (String? value) {
                  return null;
                },
                borderCorner: Constant.INPUT_ROUNDED_CORNER,
                contentPadding: const EdgeInsets.symmetric(
                    horizontal: Constant.LARGE_PADDING),
                readOnly: controller.abbs == false ? true : false),
          ],
        ),
      ),
    ],
  );
}

titleWidget(String title) {
  return CustomText(
    title: title,
    colors: AppTheme.title_dark,
    textAlign: TextAlign.start,
    fontSize: AppTheme.small,
    fontWeight: FontWeight.w700,
    maxLines: 2,
  );
}

valueWidget(String? value) {
  return CustomText(
    title: value!.isNotEmpty ? value : "-",
    colors: AppTheme.lable_noramal,
    textAlign: TextAlign.center,
    fontSize: AppTheme.small,
    fontWeight: FontWeight.w400,
    maxLines: 2,
  );
}

abstract class InvoiceSelectionAction {
  void invoiceSelectionBtnAction(
      {String identifier, List<InvoiceDetail> selectedItem});
}

getPendingAmount(InvoiceDetail item) {
  double? amount = 0.0;
  if (item.adjustedAmount != null &&
      item.adjustedAmount! > 0 &&
      item.pendingAmt != null &&
      item.pendingAmt! > 0) {
    amount = item.totalamount! - (item.pendingAmt! + item.adjustedAmount!);
  } else if (item.adjustedAmount != null && item.adjustedAmount! > 0) {
    amount = item.totalamount! - item.adjustedAmount!;
  } else if (item.pendingAmt != null && item.pendingAmt! > 0) {
    amount = item.totalamount! - item.pendingAmt!;
  } else {
    amount = item.totalamount ?? 0;
  }
  if (amount > 0) {
    return amount.toStringAsFixed(2);
  } else {
    return 0.toString();
  }
}

tdsABBSWidget(String type, String amountValue,
    RecordPaymentController controller, int index) {
  return Container(
    margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
    child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          InkWell(
            /*onTap: () {
              if (type.equalsIgnoreCase(Strings.TDS)) {
                controller.tds = !controller.tds;
                if (controller.tds == false) {
                  controller.tdsController.clear();
                } else {
                  controller.calculateABBSTDS(amountValue);
                }
              } else {
                controller.abbs = !controller.abbs;
                if (controller.abbs == false) {
                  controller.abbsController.clear();
                } else {
                  controller.calculateABBSTDS(amountValue);
                }
              }
              controller.update();
            },*/
            child: SizedBox(
              width: 12,
              height: 12,
              child: Checkbox(
                value: type.equalsIgnoreCase(Strings.TDS)
                    ? controller.tds
                    : controller.abbs,
                activeColor: AppTheme.colorPrimary,
                onChanged: (value) {
                  log("type=>>$value");
                  if (type.equalsIgnoreCase(Strings.TDS)) {
                    controller.tds = !controller.tds;
                    if (controller.tds == false) {
                      controller.tdsController.clear();
                    } else {
                      controller.calculateABBSTDS(amountValue, index);
                    }
                  } else if (type.equalsIgnoreCase(Strings.ABBS)) {
                    controller.abbs = !controller.abbs;
                    if (controller.abbs == false) {
                      controller.abbsController.clear();
                    } else {
                      controller.calculateABBSTDS(amountValue, index);
                    }
                  }
                  controller.update();
                },
              ),
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
                  controller.tds = !controller.tds;
                  if (controller.tds == false) {
                    controller.tdsController.clear();
                  } else {
                    controller.calculateABBSTDS(amountValue, index);
                  }
                } else {
                  controller.abbs = !controller.abbs;
                  if (controller.abbs == false) {
                    controller.abbsController.clear();
                  } else {
                    controller.calculateABBSTDS(amountValue, index);
                  }
                }
                controller.update();
              }),
        ]),
  );
}
