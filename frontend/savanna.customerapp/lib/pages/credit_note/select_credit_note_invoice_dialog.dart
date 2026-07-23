
import 'package:savbill/pages/credit_note/response/credit_invoice_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SelectCreditNoteInvoiceDialog extends StatefulWidget {
  final SelectCreditNoteInvoiceAction creditNoteSelectionAction;
  final List<CreditInvoiceList>? creditInvoiceList;
  final String fromFor;


  const SelectCreditNoteInvoiceDialog({
    Key? key,
    required this.creditNoteSelectionAction,
    this.creditInvoiceList,
    required this.fromFor,
  }) : super(key: key);

  @override
  _SelectCreditNoteInvoiceState createState() => _SelectCreditNoteInvoiceState();
}

class _SelectCreditNoteInvoiceState extends State<SelectCreditNoteInvoiceDialog> {
  List<CreditInvoiceList>? itemsLst = [];
  double? pendingAmount;

  @override
  void initState() {
    super.initState();
    setState(() {
      if(widget.creditInvoiceList!.isNotEmpty) {
        itemsLst!.addAll(widget.creditInvoiceList!);
      }

    });
  }

  @override
  Widget build(BuildContext context) {
    String title = "";
    if (widget.fromFor.equalsIgnoreCase(Strings.add)) {
      title = "${Strings.select} ${Strings.invoice}";
    }
    return contentBox(context, title);
  }

  contentBox(BuildContext context, String title) {

    return Padding(
      padding: const EdgeInsets.all(Constant.SCREEN_PADDING),
      child: Stack(
        children: [
          AlertDialog(
            insetPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING * 2,
            ),
            contentPadding: const EdgeInsets.only(
              top: Constant.SCREEN_PADDING,
            ),
            clipBehavior: Clip.antiAliasWithSaveLayer,
            backgroundColor: AppTheme.colorWhite,
            shape: const RoundedRectangleBorder(
                borderRadius:
                BorderRadius.all(Radius.circular(Constant.SMALL_PADDING))),
            content: Container(
              width: MediaQuery.of(context).size.width,
              color: AppTheme.colorWhite,
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: Align(
                        alignment: Alignment.centerLeft,
                        child: CustomText(
                          title: title,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.large,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING - 5),
                      child: Divider(
                        height: 5,
                        color: AppTheme.dividerColor,
                        thickness: 1,
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),

                    IntrinsicHeight(
                      child: Row(
                        children: [
                          SizedBox(width: Constant.MEDIUM_PADDING,),
                          Expanded(
                            child: CustomText(
                              title: Strings.doc_no,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                          Container(height:30,color: AppTheme.title_dark,width: 1,),
                          Expanded(
                            flex: 1,
                            child: CustomText(
                              title: Strings.create_by,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                          Container(height:30,color: AppTheme.title_dark,width: 1,),
                          Expanded(
                            flex: 1,
                            child: CustomText(
                              title: Strings.tax_amount,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                          Container(height:30,color: AppTheme.title_dark,width: 1,),
                          Expanded(
                            flex: 1,
                            child: CustomText(
                              title: Strings.total_invoice,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                         Container(height:30,color: AppTheme.title_dark,width: 1,),
                         Expanded(
                           flex: 1,
                            child: CustomText(
                              title: Strings.pending_amount,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                          Container(height:30,color: AppTheme.title_dark,width: 1,),
                          Expanded(
                            flex: 1,
                            child: CustomText(
                              title: Strings.refund_amount,
                              colors: AppTheme.title_dark,
                              textAlign: TextAlign.center,
                              fontSize: AppTheme.small,
                              fontWeight: FontWeight.w500,
                              maxLines: 2,
                            ),
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),

                    Flexible(
                        child: ListView.builder(
                          shrinkWrap: true,
                          primary: false,
                          itemCount: itemsLst!.length,
                          itemBuilder: (context, index) {
                            CreditInvoiceList item = itemsLst![index];

                            if (item.adjustedAmount == null) {
                                      pendingAmount =
                                      double.parse(item.totalamount.toString());
                                    } else if (item.adjustedAmount != null) {
                                      pendingAmount =
                                      double.parse(item.totalamount.toString()) - double.parse(item.adjustedAmount.toString());
                                    }
                            return Column(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                InkWell(
                                  onTap: () {
                                    for (var f in itemsLst!) {
                                      if (f.id == item.id!) {
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
                                        vertical: Constant.SMALL_PADDING + 1,
                                        horizontal: Constant.MEDIUM_PADDING),
                                    child: Row(
                                      children: [
                                        item.selected == true
                                            ? Icon(
                                          Icons.check,
                                          color: AppTheme.colorPrimary,
                                          size: Constant.ICON_SIZE_M,
                                        )
                                            : const Icon(
                                          Icons.check,
                                          color: Colors.white,
                                          size: Constant.ICON_SIZE_M,
                                        ),
                                        SizedBox(width: Constant.VERY_SMALL_PADDING,),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title: item.docnumber ?? "-",
                                            textAlign: TextAlign.start,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small-1,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title: item.createdByName ?? "-",
                                            textAlign: TextAlign.center,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                        Expanded(
                                          flex:1,
                                          child: CustomText(
                                            title: item.tax!.toStringAsFixed(2) ?? "-",
                                            textAlign: TextAlign.center,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title: item.totalamount!.toStringAsFixed(2) ?? "-",
                                            textAlign: TextAlign.center,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title: pendingAmount!.toStringAsFixed(2).toString() ?? "-",
                                            textAlign: TextAlign.center,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title: item.refundAbleAmount ?? "-",
                                            textAlign: TextAlign.right,
                                            colors: item.selected != null &&
                                                item.selected == true
                                                ? AppTheme.colorPrimary
                                                : AppTheme.lable_noramal,
                                            fontSize: AppTheme.small,
                                            fontWeight: item.selected != null &&
                                                item.selected == true
                                                ? FontWeight.w700
                                                : FontWeight.w700,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ),
                                index == (itemsLst!.length - 1)
                                    ? Container()
                                    : Padding(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal:
                                      Constant.SCREEN_PADDING - 5),
                                  child: Divider(
                                    height: 5,
                                    color: AppTheme.dividerColor,
                                    thickness: 0.5,
                                  ),
                                ),
                              ],
                            );
                          },
                        )),
                    const SizedBox(height: Constant.SMALL_PADDING),
                    Row(
                      children: [
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              validateSelection();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomLeft: Radius.circular(
                                        Constant.SMALL_PADDING)),
                              ),
                              child: Text(
                                Strings.select,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorPositive,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                        Expanded(
                          child: InkWell(
                            onTap: () {
                              Get.back();
                            },
                            child: Container(
                              padding: const EdgeInsets.only(
                                  top: Constant.SCREEN_PADDING,
                                  bottom: Constant.SCREEN_PADDING),
                              decoration: BoxDecoration(
                                border: Border.all(
                                  color: AppTheme.colorIconGrey,
                                  width: 1.0,
                                ),
                                borderRadius: const BorderRadius.only(
                                    bottomRight: Radius.circular(
                                        Constant.SMALL_PADDING)),
                              ),
                              child: Text(
                                Strings.cancel,
                                style: TextStyle(
                                  fontWeight: FontWeight.bold,
                                  fontSize: AppTheme.medium + 1,
                                  color: AppTheme.colorNagative,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ),
                        ),
                      ],
                    ),
                  ]),
            ),
          ),
          Positioned(
            child: GestureDetector(
              onTap: () {
                Get.back();
              },
              child: Align(
                alignment: Alignment.topRight,
                child: Icon(Icons.close, color: AppTheme.colorWhite),
              ),
            ),
          ),
        ],
      ),
    );
  }

  validateSelection() {
    List<CreditInvoiceList> selectedItem = [];
    for (var element in itemsLst!) {
      if (element.selected != null && element.selected == true) {
        selectedItem.add(element);
      }
    }
    if (selectedItem.isNotEmpty) {
      widget.creditNoteSelectionAction.creditNoteSelectionBtnAction(
          identifier: widget.fromFor, selectedItem: selectedItem);
    } else {
      Utils.showSnackbar(Strings.ERROR, "Please select at-lease one item",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }
}

abstract class SelectCreditNoteInvoiceAction {
  void creditNoteSelectionBtnAction(
      {String identifier, List<CreditInvoiceList>? selectedItem});
}
