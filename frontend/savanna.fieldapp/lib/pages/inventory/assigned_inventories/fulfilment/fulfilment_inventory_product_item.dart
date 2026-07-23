import 'package:savbill/pages/inventory/assigned_inventories/fulfilment/inventory_fulfilment_controller.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:flutter/material.dart';

class FulfillmentInventoryReqProductItem extends StatefulWidget {
  FulfilmentProductMappings? item;
  int index;
  InventoryFulFilMentController? controller;

  FulfillmentInventoryReqProductItem(
      {Key? key,
      required this.index,
      required this.item,
        required this.controller})
      : super(key: key);

  @override
  State<FulfillmentInventoryReqProductItem> createState() => _FulfillmentInventoryReqProductItemState();
}

class _FulfillmentInventoryReqProductItemState extends State<FulfillmentInventoryReqProductItem> {

  @override
  void initState() {
    super.initState();
    widget.controller!.fulfilmentQtyController = TextEditingController(text: widget.item!.quantity.toString());
  }
  @override
  Widget build(BuildContext context) {
    // controller!.fulfilmentQtyController = TextEditingController(text: item!.quantity.toString());
    return Card(
      margin: EdgeInsets.symmetric(
        vertical: widget.index == 0 ? 0 : Constant.MEDIUM_PADDING,
        // horizontal: Constant.SCREEN_PADDING,
      ),
      elevation: 2,
      color: AppTheme.colorWhite,
      child: Padding(
        padding: const EdgeInsets.symmetric(
          vertical: Constant.SMALL_PADDING,
          horizontal: Constant.SMALL_PADDING,
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: Constant.VERY_SMALL_PADDING),
              child: detailItem(
                Strings.product_category,
                (widget.item!.productCategoryName != null &&
                        widget.item!.productCategoryName!.isNotEmpty)
                    ? widget.item!.productCategoryName!
                    : "-",
                Strings.product_name,
                (widget.item!.productName != null && widget.item!.productName!.isNotEmpty)
                    ? "${widget.item!.productName}"
                    : "-",
              ),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),

            Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Expanded(
                  flex: 3,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(
                        Strings.aval_qty,
                      ),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                        widget.controller!.availableQty.toString(),
                      ),
                    ],
                  ),
                ),
                Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // titleWidget(
                      //   Strings.qty,
                      // ),
                      InputTitleRequire(
                          title: Strings.qty, require: true),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      CoustomTextField(
                          autovalidateMode: AutovalidateMode.disabled,
                          fillColor: AppTheme.colorGrayTxtBg,
                          labelText: Strings.qty,
                          // initialValue: item!.quantity.toString() ?? "0",
                          textEditingController:
                          widget.controller!.fulfilmentQtyController,
                          borderEnableColors: AppTheme.colorDisableGray,
                          textInputAction: TextInputAction.next,
                          hintColor: AppTheme.colorIconGrey,
                          keyboardType: TextInputType.text,
                          onTextValidator: (String? value) {
                            if(value == null || value.isEmpty){
                              return "please fill the quantity other than 0";
                            }else if(value.equalsIgnoreCase("0")){
                              return "please fill the quantity other than 0";
                            }
                            return null;
                          },
                          onTextFiledOnTap: () {
                          },
                          onChanged: (String? value) {
                            // widget.controller!.fulfilmentQtyController = TextEditingController(text: value);
                            widget.controller!.fulfilmentQtyController.selection = TextSelection.fromPosition(
                                TextPosition(
                                    offset: widget.controller!.fulfilmentQtyController.text.length));
                          },
                          borderCorner: Constant.INPUT_ROUNDED_CORNER,
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: Constant.LARGE_PADDING),
                          readOnly:widget.item!.requestStatus!.equalsIgnoreCase("Open")? false : true),
                    ],
                  ),
                ),
              ],
            ),
            Align(
              alignment: Alignment.centerRight,
              child: Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.VERY_SMALL_PADDING,
                    vertical: Constant.VERY_SMALL_PADDING),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SMALL_PADDING,
                      vertical: Constant.VERY_SMALL_PADDING),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(Constant.LARGE_PADDING),
                    color: (widget.item!.requestStatus != null &&
                            widget.item!.requestStatus!.isNotEmpty &&
                            widget.item!.requestStatus!.equalsIgnoreCase("open"))
                        ? AppTheme.colorBlueRView
                        : AppTheme.colorGreen,
                  ),
                  child: CustomText(
                      title: widget.item!.requestStatus,
                      colors: AppTheme.colorWhite,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.small,
                      maxLines: 2,
                      height: 1,
                      fontWeight: FontWeight.w500),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  detailItem(String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex:3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String? title) {
    return CustomText(
      title: title,
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small,
      fontWeight: FontWeight.w600,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "-",
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }
}
