// import 'dart:developer';
//
// import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
// import 'package:savbill/pages/dashboard/record_payment_controller.dart';
// import 'package:savbill/theme/app_theme.dart';
// import 'package:savbill/util/Extensions.dart';
// import 'package:savbill/util/constant.dart';
// import 'package:savbill/util/strings.dart';
// import 'package:savbill/util/utils.dart';
// import 'package:savbill/widgets/coustom_text.dart';
// import 'package:savbill/widgets/input_textfield.dart';
// import 'package:flutter/material.dart';
// import 'package:flutter/services.dart';
// import 'package:get/get.dart';
//
// class InvoiceSelectionDialog extends StatefulWidget {
//   final InvoiceSelectionAction invoiceSelectionAction;
//   final List<InvoiceDetail> itemsOrgLst;
//   final String fromFor;
//   final RecordPaymentController controller;
//
//   const InvoiceSelectionDialog({
//     Key? key,
//     required this.invoiceSelectionAction,
//     required this.itemsOrgLst,
//     required this.fromFor,
//     required this.controller,
//   }) : super(key: key);
//
//   @override
//   _InvoiceSelectionState createState() => _InvoiceSelectionState();
// }
//
// class _InvoiceSelectionState extends State<InvoiceSelectionDialog> {
//   List<InvoiceDetail> itemsLst = [];
//
//   @override
//   void initState() {
//     super.initState();
//     setState(() {
//       itemsLst.addAll(widget.itemsOrgLst);
//     });
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     String title = "${Strings.select} ${Strings.invoice}";
//
//     return contentBox(context, title);
//   }
//
//   contentBox(BuildContext context, String title) {
//     return Padding(
//       padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
//       child: Stack(
//         children: [
//           AlertDialog(
//             insetPadding: const EdgeInsets.only(
//               top: Constant.SCREEN_PADDING * 2,
//             ),
//             contentPadding: const EdgeInsets.only(
//               top: Constant.SCREEN_PADDING,
//             ),
//             clipBehavior: Clip.antiAliasWithSaveLayer,
//             backgroundColor: AppTheme.colorWhite,
//             shape: const RoundedRectangleBorder(
//                 borderRadius:
//                     BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
//             content: Container(
//               width: MediaQuery.of(context).size.width,
//               color: AppTheme.colorWhite,
//               child: Column(
//                   mainAxisSize: MainAxisSize.min,
//                   mainAxisAlignment: MainAxisAlignment.start,
//                   crossAxisAlignment: CrossAxisAlignment.start,
//                   children: [
//                     Padding(
//                       padding: const EdgeInsets.symmetric(
//                           horizontal: Constant.SCREEN_PADDING),
//                       child: Align(
//                         alignment: Alignment.centerLeft,
//                         child: CustomText(
//                           title: title,
//                           colors: AppTheme.title_dark,
//                           fontSize: AppTheme.large,
//                           fontWeight: FontWeight.w600,
//                         ),
//                       ),
//                     ),
//                     const SizedBox(height: Constant.SMALL_PADDING),
//                     Padding(
//                       padding: const EdgeInsets.symmetric(
//                           horizontal: Constant.SCREEN_PADDING - 5),
//                       child: Divider(
//                         height: 5,
//                         color: AppTheme.dividerColor,
//                         thickness: 1,
//                       ),
//                     ),
//                     const SizedBox(height: Constant.SMALL_PADDING),
//                     Flexible(
//                         child: ListView.builder(
//                       shrinkWrap: true,
//                       primary: false,
//                       itemCount: itemsLst.length,
//                       itemBuilder: (context, index) {
//                         widget.controller.textEditControllers
//                             .add(TextEditingController());
//                         InvoiceDetail item = itemsLst[index];
//                         return Column(
//                           children: [
//                             InkWell(
//                               onTap: () {
//                                 for (var f in itemsLst) {
//                                   if (f.id == item.id!) {
//                                     if (f.selected == null) {
//                                       f.selected = true;
//                                     } else {
//                                       f.selected = !f.selected!;
//                                     }
//                                     break;
//                                   }
//                                 }
//                                 setState(() {
//                                   itemsLst = itemsLst;
//                                 });
//                               },
//                               child: Padding(
//                                   padding: const EdgeInsets.symmetric(
//                                       vertical: Constant.SMALL_PADDING + 1,
//                                       horizontal: Constant.MEDIUM_PADDING),
//                                   child: Card(
//                                     color: AppTheme.colorWhite,
//                                     child: Padding(
//                                       padding: const EdgeInsets.only(
//                                           top: Constant.EXPANTABLE_ITEM_MARGIN,
//                                           left: Constant.EXPANTABLE_ITEM_MARGIN,
//                                           right:
//                                               Constant.EXPANTABLE_ITEM_MARGIN,
//                                           bottom: 0),
//                                       child: Container(
//                                         alignment: Alignment.topLeft,
//                                         padding: const EdgeInsets.all(
//                                             Constant.SMALL_PADDING),
//                                         child: Column(
//                                             mainAxisAlignment:
//                                                 MainAxisAlignment.start,
//                                             crossAxisAlignment:
//                                                 CrossAxisAlignment.start,
//                                             children: [
//                                               basicDetailItem(
//                                                   Strings.document_no,
//                                                   item.docnumber ?? "-",
//                                                   Strings.create_by,
//                                                   item.createdByName ?? "-",
//                                                   Strings.tax_amount,
//                                                   item.tax.toString() ?? "-"),
//                                               const SizedBox(
//                                                 height: Constant.SMALL_PADDING,
//                                               ),
//                                               basicDetailItem(
//                                                   Strings.total_invoice,
//                                                   item.totalamount.toString() ??
//                                                       "-",
//                                                   Strings.pending_amount,
//                                                   item.totaldue.toString() ??
//                                                       "-",
//                                                   Strings.ref_amount,
//                                                   item.refundAbleAmount ?? "-"),
//                                               const SizedBox(
//                                                 height: Constant.SMALL_PADDING,
//                                               ),
//                                               Row(
//                                                 // mainAxisSize: MainAxisSize.max,
//                                                 crossAxisAlignment:
//                                                     CrossAxisAlignment.end,
//                                                 mainAxisAlignment:
//                                                     MainAxisAlignment
//                                                         .spaceBetween,
//                                                 children: [
//                                                   const SizedBox(
//                                                       height: Constant
//                                                               .VERY_SMALL_PADDING +
//                                                           1),
//                                                   Flexible(
//                                                     flex: 1,
//                                                     fit :FlexFit.loose,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .start,
//                                                       children: [
//                                                         const SizedBox(
//                                                             height: Constant
//                                                                 .VERY_SMALL_PADDING),
//                                                         titleWidget(
//                                                             Strings.amount),
//                                                         const SizedBox(
//                                                             height: Constant
//                                                                     .VERY_SMALL_PADDING +
//                                                                 1),
//                                                         // valueWidget(Strings.TDS),
//                                                         CoustomTextField(
//                                                             labelText:
//                                                                 Strings.amount,
//                                                             // textEditingController:
//                                                             // widget.controller
//                                                             //     .amountController,
//                                                             textEditingController:
//                                                                 TextEditingController(
//                                                                     text: item
//                                                                         .totalamount
//                                                                         .toString()),
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               if (value!
//                                                                   .isEmpty) {
//                                                                 return Strings
//                                                                     .enter_amount;
//                                                               }
//                                                               return null;
//                                                             },
//                                                             onChanged: (value) {
//                                                               if (value
//                                                                   .isEmpty) {
//                                                                 widget
//                                                                     .controller
//                                                                     .tdsController
//                                                                     .text = "0";
//                                                                 widget
//                                                                     .controller
//                                                                     .abbsController
//                                                                     .text = "0";
//                                                               } else {
//                                                                 widget
//                                                                     .controller
//                                                                     .calculateABBSTDS(
//                                                                         item.totalamount
//                                                                             .toString(),
//                                                                         index);
//                                                               }
//                                                               widget.controller
//                                                                   .update();
//                                                             },
//                                                             maxLength: 6,
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             inputFormatters: [
//                                                               FilteringTextInputFormatter
//                                                                   .allow(RegExp(
//                                                                       r'^\d+\.?\d{0,2}')),
//                                                             ],
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: false)
//                                                       ],
//                                                     ),
//                                                   ),
//                                                   const SizedBox(
//                                                       width: Constant
//                                                               .VERY_SMALL_PADDING),
//                                                   Flexible(
//                                                     flex: 1,
//                                                     fit :FlexFit.tight,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .center,
//                                                       children: [
//                                                         CheckboxListTile(
//                                                             activeColor: AppTheme
//                                                                 .colorPrimary,
//                                                             // dense: true,
//                                                             title: CustomText(
//                                                               title:
//                                                                   Strings.TDS,
//                                                               colors: AppTheme
//                                                                   .title_dark,
//                                                               fontSize: AppTheme
//                                                                   .verySmall,
//                                                               fontWeight:
//                                                                   FontWeight
//                                                                       .w600,
//                                                             ),
//                                                             value:
//                                                                 item.tdsCheck,
//                                                             onChanged:
//                                                                 (bool? val) {
//                                                               setState(() {
//                                                                 item.tdsCheck =
//                                                                     (val!);
//                                                                 widget
//                                                                     .controller
//                                                                     .tds = !val;
//                                                                 // if (type.equalsIgnoreCase(Strings.TDS)) {
//                                                                 widget.controller
//                                                                         .tds =
//                                                                     !widget
//                                                                         .controller
//                                                                         .tds;
//                                                                 if (widget.controller
//                                                                             .tds ==
//                                                                         false &&
//                                                                     item.tdsCheck ==
//                                                                         false) {
//                                                                   widget
//                                                                       .controller
//                                                                       .textEditControllers[
//                                                                           index]
//                                                                       .clear();
//                                                                 } else {
//                                                                   widget
//                                                                       .controller
//                                                                       .calculateABBSTDS(
//                                                                           item.totalamount
//                                                                               .toString(),
//                                                                           index);
//                                                                 }
//                                                                 widget
//                                                                     .controller
//                                                                     .update();
//                                                                 // }
//                                                               });
//                                                               widget.controller
//                                                                   .update();
//                                                             }),
//                                                         const SizedBox(
//                                                           height: Constant
//                                                               .VERY_SMALL_PADDING,
//                                                         ),
//                                                         CoustomTextField(
//                                                             labelText: "0",
//                                                             textEditingController: widget
//                                                                     .controller
//                                                                     .textEditControllers[
//                                                                 index],
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               return null;
//                                                             },
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: widget
//                                                                         .controller
//                                                                         .tds ==
//                                                                     false
//                                                                 ? true
//                                                                 : false),
//                                                       ],
//                                                     ),
//                                                   ),
//                                                   const SizedBox(
//                                                       width: Constant
//                                                               .VERY_SMALL_PADDING),
//                                                   Flexible(
//                                                     flex: 1,
//                                                     fit :FlexFit.tight,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .center,
//                                                       children: [
//                                                         CheckboxListTile(
//                                                             activeColor: AppTheme
//                                                                 .colorPrimary,
//                                                             dense: true,
//                                                             //font change
//                                                             title: CustomText(
//                                                               title:
//                                                                   Strings.ABBS,
//                                                               colors: AppTheme
//                                                                   .title_dark,
//                                                               fontSize: AppTheme
//                                                                   .verySmall,
//                                                               fontWeight:
//                                                                   FontWeight
//                                                                       .w600,
//                                                             ),
//                                                             value:
//                                                                 item.tdsCheck,
//                                                             onChanged:
//                                                                 (bool? val) {
//                                                               setState(() {
//                                                                 item.tdsCheck =
//                                                                     (val!);
//                                                                 widget
//                                                                     .controller
//                                                                     .tds = !val;
//                                                                 // if (type.equalsIgnoreCase(Strings.TDS)) {
//                                                                 widget.controller
//                                                                         .tds =
//                                                                     !widget
//                                                                         .controller
//                                                                         .tds;
//                                                                 if (widget.controller
//                                                                             .tds ==
//                                                                         false &&
//                                                                     item.tdsCheck ==
//                                                                         false) {
//                                                                   widget
//                                                                       .controller
//                                                                       .textEditControllers[
//                                                                           index]
//                                                                       .clear();
//                                                                 } else {
//                                                                   widget
//                                                                       .controller
//                                                                       .calculateABBSTDS(
//                                                                           item.totalamount
//                                                                               .toString(),
//                                                                           index);
//                                                                 }
//                                                                 widget
//                                                                     .controller
//                                                                     .update();
//                                                                 // }
//                                                               });
//                                                               widget.controller
//                                                                   .update();
//                                                             }),
//                                                         const SizedBox(
//                                                           height: Constant
//                                                               .VERY_SMALL_PADDING,
//                                                         ),
//                                                         CoustomTextField(
//                                                             labelText: "0",
//                                                             textEditingController: widget
//                                                                     .controller
//                                                                     .textEditControllers[
//                                                                 index],
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               return null;
//                                                             },
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: widget
//                                                                         .controller
//                                                                         .tds ==
//                                                                     false
//                                                                 ? true
//                                                                 : false),
//                                                       ],
//                                                     ),
//                                                   ),
//                                                 ],
//                                               ),
//                                               const SizedBox(
//                                                 width: Constant.SMALL_PADDING,
//                                               ),
//
//                                               /*CheckboxListTile(
//                                                   activeColor: AppTheme.colorPrimary,
//                                                   dense: true,
//                                                   //font change
//                                                   title: const Text(
//                                                     Strings.TDS,
//                                                     style: TextStyle(
//                                                         fontSize: 14,
//                                                         fontWeight:
//                                                             FontWeight.w600,
//                                                         letterSpacing: 0.5),
//                                                   ),
//                                                   value: item.tdsCheck,
//                                                   onChanged: (bool? val) {
//                                                     setState(() {
//                                                       item.tdsCheck = (val!);
//                                                       widget.controller.tds = !val;
//                                                       // if (type.equalsIgnoreCase(Strings.TDS)) {
//                                                       widget.controller.tds =
//                                                           !widget
//                                                               .controller.tds;
//                                                       if (widget.controller.tds == false && item.tdsCheck == false) {
//                                                         widget.controller
//                                                             .textEditControllers[index]
//                                                             .clear();
//                                                       } else {
//                                                         widget.controller
//                                                             .calculateABBSTDS(
//                                                                 item.totalamount
//                                                                     .toString(),
//                                                                 index);
//                                                       }
//                                                       widget.controller.update();
//                                                       // }
//                                                     });
//                                                     widget.controller.update();
//                                                   }),
//                                               CoustomTextField(
//                                                   labelText: "0",
//                                                   textEditingController: widget
//                                                           .controller
//                                                           .textEditControllers[
//                                                       index],
//                                                   keyboardType:
//                                                       TextInputType.number,
//                                                   borderEnableColors:
//                                                       AppTheme.colorBlack,
//                                                   textInputAction:
//                                                       TextInputAction.next,
//                                                   hintColor:
//                                                       AppTheme.colorIconGrey,
//                                                   onTextValidator:
//                                                       (String? value) {
//                                                     return null;
//                                                   },
//                                                   borderCorner: Constant
//                                                       .INPUT_ROUNDED_CORNER,
//                                                   contentPadding:
//                                                       const EdgeInsets.symmetric(
//                                                           horizontal: Constant
//                                                               .LARGE_PADDING),
//                                                   readOnly:
//                                                       widget.controller.tds ==
//                                                               false
//                                                           ? true
//                                                           : false),
// */
//
//                                               /*amountTdsAbbsItem(
//                                                   Strings.amount,
//                                                   item.totalamount.toString(),
//                                                   Strings.TDS,
//                                                   item.tdsCheck,
//                                                   Strings.ABBS,
//                                                   item,
//                                                   widget.controller,
//                                                   index),*/
//
//                                               /* Row(
//                                                 mainAxisSize: MainAxisSize.max,
//                                                 crossAxisAlignment:
//                                                     CrossAxisAlignment.center,
//                                                 mainAxisAlignment:
//                                                     MainAxisAlignment
//                                                         .spaceBetween,
//                                                 children: [
//                                                   Flexible(
//                                                     flex: 1,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .start,
//                                                       children: [
//                                                         const SizedBox(
//                                                             height: Constant
//                                                                 .VERY_SMALL_PADDING),
//                                                         titleWidget(
//                                                             Strings.amount),
//                                                         const SizedBox(
//                                                             height: Constant
//                                                                     .VERY_SMALL_PADDING +
//                                                                 1),
//                                                         // valueWidget(Strings.TDS),
//                                                         CoustomTextField(
//                                                             labelText:
//                                                                 Strings.amount,
//                                                             textEditingController:
//                                                                 widget.controller
//                                                                     .amountController,
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               if (value!
//                                                                   .isEmpty) {
//                                                                 return Strings
//                                                                     .enter_amount;
//                                                               }
//                                                               return null;
//                                                             },
//                                                             onChanged: (value) {
//                                                               if (value
//                                                                   .isEmpty) {
//                                                                 widget
//                                                                     .controller
//                                                                     .tdsController
//                                                                     .text = "0";
//                                                                 widget
//                                                                     .controller
//                                                                     .abbsController
//                                                                     .text = "0";
//                                                               } else {
//                                                                 widget
//                                                                     .controller
//                                                                     .calculateABBSTDS();
//                                                               }
//                                                               widget.controller
//                                                                   .update();
//                                                             },
//                                                             maxLength: 6,
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             inputFormatters: [
//                                                               FilteringTextInputFormatter
//                                                                   .allow(RegExp(
//                                                                       r'^\d+\.?\d{0,2}')),
//                                                             ],
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: false),
//                                                       ],
//                                                     ),
//                                                   ),
//                                                   const SizedBox(
//                                                     width:
//                                                         Constant.SMALL_PADDING,
//                                                   ),
//                                                   Flexible(
//                                                     flex: 1,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .end,
//                                                       children: [
//                                                         // titleWidget(Strings.TDS),
//                                                         // const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//                                                         tdsABBSWidget(
//                                                             Strings.TDS,
//                                                             widget.controller),
//                                                         const SizedBox(
//                                                           height: Constant
//                                                               .SMALL_PADDING,
//                                                         ),
//                                                         CoustomTextField(
//                                                             labelText: "0",
//                                                             textEditingController: widget
//                                                                 .controller
//                                                                 .tdsController,
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               return null;
//                                                             },
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: widget
//                                                                         .controller
//                                                                         .tds ==
//                                                                     false
//                                                                 ? true
//                                                                 : false),
//                                                       ],
//                                                     ),
//                                                   ),
//                                                   const SizedBox(
//                                                     width:
//                                                         Constant.SMALL_PADDING,
//                                                   ),
//                                                   Flexible(
//                                                     flex: 1,
//                                                     child: Column(
//                                                       mainAxisAlignment:
//                                                           MainAxisAlignment
//                                                               .start,
//                                                       crossAxisAlignment:
//                                                           CrossAxisAlignment
//                                                               .end,
//                                                       children: [
//                                                         // titleWidget(Strings.ABBS),
//                                                         // const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//                                                         tdsABBSWidget(
//                                                             Strings.ABBS,
//                                                             widget.controller),
//                                                         const SizedBox(
//                                                           height: Constant
//                                                               .SMALL_PADDING,
//                                                         ),
//                                                         CoustomTextField(
//                                                             labelText: "0",
//                                                             textEditingController:
//                                                                 widget.controller
//                                                                     .abbsController,
//                                                             keyboardType:
//                                                                 TextInputType
//                                                                     .number,
//                                                             borderEnableColors:
//                                                                 AppTheme
//                                                                     .colorBlack,
//                                                             textInputAction:
//                                                                 TextInputAction
//                                                                     .next,
//                                                             hintColor: AppTheme
//                                                                 .colorIconGrey,
//                                                             onTextValidator:
//                                                                 (String?
//                                                                     value) {
//                                                               return null;
//                                                             },
//                                                             borderCorner: Constant
//                                                                 .INPUT_ROUNDED_CORNER,
//                                                             contentPadding:
//                                                                 const EdgeInsets
//                                                                         .symmetric(
//                                                                     horizontal:
//                                                                         Constant
//                                                                             .LARGE_PADDING),
//                                                             readOnly: widget
//                                                                         .controller
//                                                                         .abbs ==
//                                                                     false
//                                                                 ? true
//                                                                 : false),
//                                                       ],
//                                                     ),
//                                                   ),
//                                                 ],
//                                               ),*/
//                                               const SizedBox(
//                                                   height:
//                                                       Constant.SMALL_PADDING),
//                                             ]),
//                                       ),
//                                     ),
//                                   )
//
//                                   /*Row(
//                                   children: [
//                                     item.selected == true
//                                         ? Icon(
//                                             Icons.check,
//                                             color: AppTheme.colorPrimary,
//                                             size: Constant.ICON_SIZE_M,
//                                           )
//                                         : const Icon(
//                                             Icons.check,
//                                             color: Colors.white,
//                                             size: Constant.ICON_SIZE_M,
//                                           ),
//                                     const SizedBox(
//                                       width: Constant.SMALL_PADDING,
//                                     ),
//                                     CustomText(
//                                       title: item.docnumber,
//                                       textAlign: TextAlign.start,
//                                       colors: item.selected != null &&
//                                               item.selected == true
//                                           ? AppTheme.colorPrimary
//                                           : AppTheme.lable_noramal,
//                                       fontSize: AppTheme.small + 1,
//                                       fontWeight: item.selected != null &&
//                                               item.selected == true
//                                           ? FontWeight.w500
//                                           : FontWeight.w700,
//                                     ),
//                                     CustomText(
//                                       title: item.createdByName,
//                                       textAlign: TextAlign.start,
//                                       colors: item.selected != null &&
//                                               item.selected == true
//                                           ? AppTheme.colorPrimary
//                                           : AppTheme.lable_noramal,
//                                       fontSize: AppTheme.small + 1,
//                                       fontWeight: item.selected != null &&
//                                               item.selected == true
//                                           ? FontWeight.w500
//                                           : FontWeight.w700,
//                                     ),
//                                     CustomText(
//                                       title: item.totalamount,
//                                       textAlign: TextAlign.start,
//                                       colors: item.selected != null &&
//                                               item.selected == true
//                                           ? AppTheme.colorPrimary
//                                           : AppTheme.lable_noramal,
//                                       fontSize: AppTheme.small + 1,
//                                       fontWeight: item.selected != null &&
//                                               item.selected == true
//                                           ? FontWeight.w500
//                                           : FontWeight.w700,
//                                     ),
//                                     CustomText(
//                                       title: item.totalamount,
//                                       textAlign: TextAlign.start,
//                                       colors: item.selected != null &&
//                                               item.selected == true
//                                           ? AppTheme.colorPrimary
//                                           : AppTheme.lable_noramal,
//                                       fontSize: AppTheme.small + 1,
//                                       fontWeight: item.selected != null &&
//                                               item.selected == true
//                                           ? FontWeight.w500
//                                           : FontWeight.w700,
//                                     ),
//                                     CustomText(
//                                       title: item.refundAbleAmount,
//                                       textAlign: TextAlign.start,
//                                       colors: item.selected != null &&
//                                               item.selected == true
//                                           ? AppTheme.colorPrimary
//                                           : AppTheme.lable_noramal,
//                                       fontSize: AppTheme.small + 1,
//                                       fontWeight: item.selected != null &&
//                                               item.selected == true
//                                           ? FontWeight.w500
//                                           : FontWeight.w700,
//                                     ),
//                                   ],
//                                 ),*/
//                                   ),
//                             ),
//                             index == (itemsLst.length - 1)
//                                 ? Container()
//                                 : Padding(
//                                     padding: const EdgeInsets.symmetric(
//                                         horizontal:
//                                             Constant.SCREEN_PADDING - 5),
//                                     child: Divider(
//                                       height: 5,
//                                       color: AppTheme.dividerColor,
//                                       thickness: 0.5,
//                                     ),
//                                   ),
//                           ],
//                         );
//                       },
//                     )),
//                     const SizedBox(height: Constant.SMALL_PADDING),
//                     Row(
//                       children: [
//                         Expanded(
//                           child: InkWell(
//                             onTap: () {
//                               validateSelection();
//                             },
//                             child: Container(
//                               padding: const EdgeInsets.only(
//                                   top: Constant.SCREEN_PADDING,
//                                   bottom: Constant.SCREEN_PADDING),
//                               decoration: BoxDecoration(
//                                 border: Border.all(
//                                   color: AppTheme.colorIconGrey,
//                                   width: 1.0,
//                                 ),
//                                 borderRadius: const BorderRadius.only(
//                                     bottomLeft: Radius.circular(
//                                         Constant.SMALL_PADDING)),
//                               ),
//                               child: Text(
//                                 Strings.select,
//                                 style: TextStyle(
//                                   fontWeight: FontWeight.bold,
//                                   fontSize: AppTheme.medium + 1,
//                                   color: AppTheme.colorPositive,
//                                 ),
//                                 textAlign: TextAlign.center,
//                               ),
//                             ),
//                           ),
//                         ),
//                         Expanded(
//                           child: InkWell(
//                             onTap: () {
//                               Get.back();
//                             },
//                             child: Container(
//                               padding: const EdgeInsets.only(
//                                   top: Constant.SCREEN_PADDING,
//                                   bottom: Constant.SCREEN_PADDING),
//                               decoration: BoxDecoration(
//                                 border: Border.all(
//                                   color: AppTheme.colorIconGrey,
//                                   width: 1.0,
//                                 ),
//                                 borderRadius: const BorderRadius.only(
//                                     bottomRight: Radius.circular(
//                                         Constant.SMALL_PADDING)),
//                               ),
//                               child: Text(
//                                 Strings.cancel,
//                                 style: TextStyle(
//                                   fontWeight: FontWeight.bold,
//                                   fontSize: AppTheme.medium + 1,
//                                   color: AppTheme.colorNagative,
//                                 ),
//                                 textAlign: TextAlign.center,
//                               ),
//                             ),
//                           ),
//                         ),
//                       ],
//                     ),
//                   ]),
//             ),
//           ),
//           Positioned(
//             child: GestureDetector(
//               onTap: () {
//                 Get.back();
//               },
//               child: Align(
//                 alignment: Alignment.topRight,
//                 child: Icon(Icons.close, color: AppTheme.colorWhite),
//               ),
//             ),
//           ),
//         ],
//       ),
//     );
//   }
//
//   validateSelection() {
//     List<InvoiceDetail> selectedItem = [];
//     for (var element in itemsLst) {
//       if (element.selected != null && element.selected == true) {
//         selectedItem.add(element);
//       }
//     }
//     if (selectedItem.isNotEmpty) {
//       widget.invoiceSelectionAction.invoiceSelectionBtnAction(
//           identifier: widget.fromFor, selectedItem: selectedItem);
//     } else {
//       Utils.showSnackbar(Strings.ERROR, "Please select at-lease one item",
//           AppTheme.colorWhite, AppTheme.colorRed);
//     }
//   }
// }
//
// basicDetailItem(String title1, String? value1, String title2, String? value2,
//     String title3, String? value3) {
//   return Row(
//     mainAxisSize: MainAxisSize.max,
//     crossAxisAlignment: CrossAxisAlignment.center,
//     mainAxisAlignment: MainAxisAlignment.spaceBetween,
//     children: [
//       Flexible(
//         flex: 1,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.start,
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             titleWidget(title1),
//             const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//             valueWidget(value1),
//           ],
//         ),
//       ),
//       Flexible(
//         flex: 1,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.start,
//           crossAxisAlignment: CrossAxisAlignment.end,
//           children: [
//             titleWidget(title2),
//             const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//             valueWidget(value2),
//           ],
//         ),
//       ),
//       Flexible(
//         flex: 1,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.start,
//           crossAxisAlignment: CrossAxisAlignment.end,
//           children: [
//             titleWidget(title3),
//             const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//             valueWidget(value3),
//           ],
//         ),
//       ),
//     ],
//   );
// }
//
// amountTdsAbbsItem(
//     String title1,
//     String value1,
//     String title2,
//     bool? tdsCheckValue,
//     String title3,
//     InvoiceDetail item,
//     RecordPaymentController? controller,
//     int index) {
//   return Row(
//     mainAxisSize: MainAxisSize.max,
//     crossAxisAlignment: CrossAxisAlignment.center,
//     mainAxisAlignment: MainAxisAlignment.spaceBetween,
//     children: [
//       Flexible(
//         flex: 1,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.start,
//           crossAxisAlignment: CrossAxisAlignment.start,
//           children: [
//             const SizedBox(height: Constant.VERY_SMALL_PADDING),
//             titleWidget(title1),
//             const SizedBox(height: Constant.VERY_SMALL_PADDING + 1),
//             // valueWidget(Strings.TDS),
//             CoustomTextField(
//                 labelText: Strings.amount,
//                 // textEditingController:
//                 // widget.controller
//                 //     .amountController,
//                 textEditingController: TextEditingController(text: value1),
//                 keyboardType: TextInputType.number,
//                 borderEnableColors: AppTheme.colorBlack,
//                 textInputAction: TextInputAction.next,
//                 hintColor: AppTheme.colorIconGrey,
//                 onTextValidator: (String? value) {
//                   if (value!.isEmpty) {
//                     return Strings.enter_amount;
//                   }
//                   return null;
//                 },
//                 onChanged: (value) {
//                   if (value.isEmpty) {
//                     controller!.tdsController.text = "0";
//                     controller.abbsController.text = "0";
//                   } else {
//                     controller!.calculateABBSTDS(value1, index);
//                   }
//                   controller.update();
//                 },
//                 maxLength: 6,
//                 borderCorner: Constant.INPUT_ROUNDED_CORNER,
//                 inputFormatters: [
//                   FilteringTextInputFormatter.allow(RegExp(r'^\d+\.?\d{0,2}')),
//                 ],
//                 contentPadding: const EdgeInsets.symmetric(
//                     horizontal: Constant.LARGE_PADDING),
//                 readOnly: false),
//           ],
//         ),
//       ),
//       const SizedBox(
//         width: Constant.SMALL_PADDING,
//       ),
//       Flexible(
//           flex: 1,
//           child: Column(
//             mainAxisAlignment: MainAxisAlignment.start,
//             crossAxisAlignment: CrossAxisAlignment.center,
//             children: [
//               tdsABBSWidget(
//                   Strings.TDS, item.totalamount.toString(), controller!, index),
//               const SizedBox(
//                 height: Constant.SMALL_PADDING,
//               ),
//               CoustomTextField(
//                   labelText: "0",
//                   textEditingController: controller.tdsController,
//                   keyboardType: TextInputType.number,
//                   borderEnableColors: AppTheme.colorBlack,
//                   textInputAction: TextInputAction.next,
//                   hintColor: AppTheme.colorIconGrey,
//                   onTextValidator: (String? value) {
//                     return null;
//                   },
//                   borderCorner: Constant.INPUT_ROUNDED_CORNER,
//                   contentPadding: const EdgeInsets.symmetric(
//                       horizontal: Constant.LARGE_PADDING),
//                   readOnly: controller.tds == false ? true : false),
//             ],
//           )),
//       const SizedBox(
//         width: Constant.SMALL_PADDING,
//       ),
//       Flexible(
//         flex: 1,
//         child: Column(
//           mainAxisAlignment: MainAxisAlignment.start,
//           crossAxisAlignment: CrossAxisAlignment.end,
//           children: [
//             // titleWidget(Strings.ABBS),
//             // const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
//             tdsABBSWidget(
//                 Strings.ABBS, item.totalamount.toString(), controller, index),
//             const SizedBox(
//               height: Constant.SMALL_PADDING,
//             ),
//             CoustomTextField(
//                 labelText: "0",
//                 textEditingController: controller.abbsController,
//                 keyboardType: TextInputType.number,
//                 borderEnableColors: AppTheme.colorBlack,
//                 textInputAction: TextInputAction.next,
//                 hintColor: AppTheme.colorIconGrey,
//                 onTextValidator: (String? value) {
//                   return null;
//                 },
//                 borderCorner: Constant.INPUT_ROUNDED_CORNER,
//                 contentPadding: const EdgeInsets.symmetric(
//                     horizontal: Constant.LARGE_PADDING),
//                 readOnly: controller.abbs == false ? true : false),
//           ],
//         ),
//       ),
//     ],
//   );
// }
//
// titleWidget(String title) {
//   return CustomText(
//     title: title,
//     colors: AppTheme.title_dark,
//     textAlign: TextAlign.start,
//     fontSize: AppTheme.small,
//     fontWeight: FontWeight.w700,
//     maxLines: 2,
//   );
// }
//
// valueWidget(String? value) {
//   return CustomText(
//     title: value!.isNotEmpty ? value : "-",
//     colors: AppTheme.lable_noramal,
//     textAlign: TextAlign.center,
//     fontSize: AppTheme.small,
//     fontWeight: FontWeight.w400,
//     maxLines: 2,
//   );
// }
//
// abstract class InvoiceSelectionAction {
//   void invoiceSelectionBtnAction(
//       {String identifier, List<InvoiceDetail> selectedItem});
// }
//
// tdsABBSWidget(String type, String amountValue,
//     RecordPaymentController controller, int index) {
//   return Container(
//     margin: const EdgeInsets.fromLTRB(Constant.SMALL_PADDING, 0, 0, 0),
//     child: Row(
//         crossAxisAlignment: CrossAxisAlignment.center,
//         mainAxisAlignment: MainAxisAlignment.start,
//         children: [
//           InkWell(
//             /*onTap: () {
//               if (type.equalsIgnoreCase(Strings.TDS)) {
//                 controller.tds = !controller.tds;
//                 if (controller.tds == false) {
//                   controller.tdsController.clear();
//                 } else {
//                   controller.calculateABBSTDS(amountValue);
//                 }
//               } else {
//                 controller.abbs = !controller.abbs;
//                 if (controller.abbs == false) {
//                   controller.abbsController.clear();
//                 } else {
//                   controller.calculateABBSTDS(amountValue);
//                 }
//               }
//               controller.update();
//             },*/
//             child: SizedBox(
//               width: 12,
//               height: 12,
//               child: Checkbox(
//                 value: type.equalsIgnoreCase(Strings.TDS)
//                     ? controller.tds
//                     : controller.abbs,
//                 activeColor: AppTheme.colorPrimary,
//                 onChanged: (value) {
//                   log("type=>>$value");
//                   if (type.equalsIgnoreCase(Strings.TDS)) {
//                     controller.tds = !controller.tds;
//                     if (controller.tds == false) {
//                       controller.tdsController.clear();
//                     } else {
//                       controller.calculateABBSTDS(amountValue, index);
//                     }
//                   } else {
//                     controller.abbs = !controller.abbs;
//                     if (controller.abbs == false) {
//                       controller.abbsController.clear();
//                     } else {
//                       controller.calculateABBSTDS(amountValue, index);
//                     }
//                   }
//                   controller.update();
//                 },
//               ),
//             ),
//           ),
//           const SizedBox(width: Constant.SMALL_PADDING),
//           InkWell(
//               child: CustomText(
//                 title: type,
//                 textAlign: TextAlign.start,
//                 colors: AppTheme.colorBlack,
//                 fontSize: AppTheme.medium - 1,
//                 fontWeight: FontWeight.w400,
//               ),
//               onTap: () {
//                 if (type.equalsIgnoreCase(Strings.TDS)) {
//                   controller.tds = !controller.tds;
//                   if (controller.tds == false) {
//                     controller.tdsController.clear();
//                   } else {
//                     controller.calculateABBSTDS(amountValue, index);
//                   }
//                 } else {
//                   controller.abbs = !controller.abbs;
//                   if (controller.abbs == false) {
//                     controller.abbsController.clear();
//                   } else {
//                     controller.calculateABBSTDS(amountValue, index);
//                   }
//                 }
//                 controller.update();
//               }),
//         ]),
//   );
// }
