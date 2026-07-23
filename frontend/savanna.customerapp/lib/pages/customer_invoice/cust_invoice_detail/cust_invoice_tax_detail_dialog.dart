import 'package:savbill/pages/customer_invoice/cust_invoice_detail/model/cust_invoice_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustInvoiceTaxDialog extends StatefulWidget {
  final List<DebitDocumentTAXReels>? debitDocumentTAXReelsList;
  final List<DebitDocumentTAXRelDtos>? debitDocumentTAXRelDtosList;
  final String? type;

  const CustInvoiceTaxDialog({
    Key? key,
    this.debitDocumentTAXReelsList,
    this.debitDocumentTAXRelDtosList,
    required this.type,
  }) : super(key: key);

  @override
  _CustInvoiceTaxDialogState createState() => _CustInvoiceTaxDialogState();
}

class _CustInvoiceTaxDialogState extends State<CustInvoiceTaxDialog> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    String title = Strings.tax_details;
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
            backgroundColor: AppTheme.colorPrimary,
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
                    Container(
                      color: AppTheme.colorPrimary,
                      padding: const EdgeInsets.symmetric(
                          vertical: Constant.MEDIUM_PADDING,
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
                    const SizedBox(height: Constant.MEDIUM_PADDING),

                    Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: [
                        CustomText(
                          title: Strings.tax_name,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                        ),
                       widget.type!.equalsIgnoreCase(Strings.charge)? CustomText(
                          title: Strings.tax_level,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                        ):const SizedBox.shrink(),
                        CustomText(
                          title: Strings.percentage,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                        ),
                        CustomText(
                          title: Strings.amount,
                          colors: AppTheme.title_dark,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                        ),
                      ],
                    ),
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
                    widget.type!.equalsIgnoreCase(Strings.charge) ? Flexible(
                      child: ListView.builder(
                        shrinkWrap: true,
                        primary: false,
                        itemCount: widget.debitDocumentTAXReelsList!.length,
                        itemBuilder: (context, index) {
                          DebitDocumentTAXReels item =
                              widget.debitDocumentTAXReelsList![index];

                          return Column(
                            // mainAxisAlignment: MainAxisAlignment.spaceAround,
                            // crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Padding(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.SMALL_PADDING + 1,
                                    horizontal: Constant.MEDIUM_PADDING),
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                    Expanded(
                                      child: Row(
                                        mainAxisAlignment:
                                            MainAxisAlignment.spaceEvenly,
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        children: [
                                          CustomText(
                                            title: item.taxname!,
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),
                                          CustomText(
                                            title: item.taxlevel!,
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),
                                          CustomText(
                                            title: item.percentage!,
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),
                                          CustomText(
                                            title:
                                                item.amount!.toStringAsFixed(2),
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ],
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              index ==
                                      (widget.debitDocumentTAXReelsList!
                                              .length -
                                          1)
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
                      ),
                    ): Flexible(
                      child: ListView.builder(
                        shrinkWrap: true,
                        primary: false,
                        itemCount: widget.debitDocumentTAXRelDtosList!.length,
                        itemBuilder: (context, index) {
                          DebitDocumentTAXRelDtos item =
                          widget.debitDocumentTAXRelDtosList![index];

                          return Column(
                            // mainAxisAlignment: MainAxisAlignment.spaceAround,
                            // crossAxisAlignment: CrossAxisAlignment.center,
                            children: [
                              Padding(
                                padding: const EdgeInsets.symmetric(
                                    vertical: Constant.SMALL_PADDING + 1,
                                    horizontal: Constant.MEDIUM_PADDING),
                                child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.center,
                                  mainAxisAlignment:
                                  MainAxisAlignment.spaceBetween,
                                  children: [
                                    Expanded(
                                      child: Row(
                                        mainAxisAlignment:
                                        MainAxisAlignment.spaceEvenly,
                                        crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                        children: [
                                          CustomText(
                                            title: item.taxname!,
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),

                                          CustomText(
                                            title: item.percentage!,
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),
                                          CustomText(
                                            title:
                                            item.amount!.toStringAsFixed(2),
                                            textAlign: TextAlign.start,
                                            colors: AppTheme.title_dark,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ],
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              index ==
                                  (widget.debitDocumentTAXRelDtosList!
                                      .length -
                                      1)
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
                      ),
                    ),
                    const SizedBox(height: Constant.SMALL_PADDING),
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
}
