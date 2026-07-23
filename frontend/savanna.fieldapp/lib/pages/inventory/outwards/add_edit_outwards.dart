import 'dart:developer';

import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/outwards/add_edit_outwards_controller.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:savbill/widgets/title_widge.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AddEditOutwards extends StatefulWidget {
  @override
  _AddEditOutwardState createState() => _AddEditOutwardState();
}

class _AddEditOutwardState extends State<AddEditOutwards> {
  final addEditOutwardController = Get.put(AddEditOutwardsController());
  final addEditOutwardFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<AddEditOutwardsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addEditOutwardController.isLoading),
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
                      key: addEditOutwardFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          addEditOutwardController.isReadOnly
                              ? InputTitleRequire(
                                  title:
                                      "${Strings.outward_no} :- ${addEditOutwardController.outwardDetail!.outwardNumber!}",
                                  require: false)
                              : Container(),
                          addEditOutwardController.isReadOnly
                              ? const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                )
                              : Container(),
                          InputTitleRequire(
                              title: Strings.product, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
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
                                    Strings.product,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: addEditOutwardController.selectedProduct,
                                items: addEditOutwardController.productList
                                    ?.map((ProductDetail value) {
                                  return DropdownMenuItem<ProductDetail>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedProduct =
                                      value as ProductDetail?;
                                  addEditOutwardController.update();
                                  // addEditOutwardController.getInwardsDetail();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditOutwardController
                                              .selectedProduct ==
                                          null) {
                                    return Strings.select_product;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.source_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key: addEditOutwardController.keySourceType,
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
                                    Strings.source_type,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value:
                                    addEditOutwardController.selectedSourceType,
                                items: addEditOutwardController.sourceTypeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedSourceType =
                                      value as DropdownDetail?;
                                  addEditOutwardController.selectedSource =
                                      null;
                                  addEditOutwardController.sourceList!.clear();
                                  addEditOutwardController.update();
                                  addEditOutwardController.manageSourceType();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditOutwardController
                                              .selectedSourceType ==
                                          null) {
                                    return Strings.please_select_source_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.select_source, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key: addEditOutwardController.keySource,
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
                                    Strings.select_source,
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
                                value: addEditOutwardController.selectedSource,
                                items: addEditOutwardController.sourceList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedSource =
                                      value as DropdownDetail?;
                                  addEditOutwardController.update();
                                  addEditOutwardController
                                      .availableQtyProductDestination();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditOutwardController.selectedSource ==
                                          null) {
                                    return Strings.please_select_source;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          Row(
                            children: [
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                              addEditOutwardController
                                          .availableQtyProductList !=
                                      null
                                  ? CustomText(
                                      title:
                                          "${Strings.available_quantity} : ${addEditOutwardController.availableQty.toString()}",
                                      colors: AppTheme.title_dark,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    )
                                  : const SizedBox.shrink(),
                              const SizedBox(
                                height: Constant.MEDIUM_PADDING,
                              ),
                            ],
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),

                          /*InputTitleRequire(
                              title: Strings.inwards, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
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
                                    Strings.inwards,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: addEditOutwardController.selectedInward,
                                items: addEditOutwardController.inwardList
                                    ?.map((OutwardInwardDetail value) {
                                  return DropdownMenuItem<OutwardInwardDetail>(
                                    value: value,
                                    child: Text(value.inwardNumber!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedInward =
                                      value as OutwardInwardDetail?;
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditOutwardController.selectedInward ==
                                          null) {
                                    return Strings.please_select_inward;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),*/

                          InputTitleRequire(
                              title: Strings.destination_type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownButtonHideUnderline(
                              child: DropdownButtonFormField(
                                key:
                                    addEditOutwardController.keyDestinationType,
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
                                    Strings.destination_type,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ),
                                  ),
                                ),
                                style: AppTheme.dropdownTextStyle,
                                isExpanded: false,
                                isDense: true,
                                value: addEditOutwardController
                                    .selectedDestinationType,
                                items: addEditOutwardController
                                    .destinationTypeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditOutwardController.selectedDestinationType =
                                      value as DropdownDetail?;
                                  addEditOutwardController.selectedDestination = null;
                                  addEditOutwardController.destinationList!.clear();
                                  addEditOutwardController.manageDestinationType();
                                  addEditOutwardController.update();
                                },
                                validator: (value) {
                                  if (addEditOutwardController.outwardDetail !=
                                          null &&
                                      (value == null ||
                                          addEditOutwardController
                                                  .selectedDestinationType ==
                                              null)) {
                                    return Strings
                                        .please_select_destination_type;
                                  }
                                  return null;
                                },
                              ),
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.select_destination, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          // IgnorePointer(
                          //   ignoring: addEditOutwardController.isReadOnly,
                          //   child: DropdownButtonHideUnderline(
                          //     child: DropdownButtonFormField(
                          //       key: addEditOutwardController.keyDestination,
                          //       icon: SvgPicture.asset(
                          //         downArrowSvg,
                          //         height: Constant.DROP_DOWN_ARROW_W_H,
                          //         width: Constant.DROP_DOWN_ARROW_W_H,
                          //         color: AppTheme.colorBlack,
                          //         fit: BoxFit.fill,
                          //       ),
                          //       decoration: Utils.ddlDecoration(),
                          //       hint: Align(
                          //         alignment: Alignment.centerLeft,
                          //         child: Text(
                          //           Strings.select_destination,
                          //           style: TextStyle(
                          //             fontSize: AppTheme.medium,
                          //             color: AppTheme.colorIconGrey,
                          //             fontFamily: AppTheme.appFontName,
                          //           ),
                          //         ),
                          //       ),
                          //       style: AppTheme.dropdownTextStyle,
                          //       isExpanded: true,
                          //       isDense: true,
                          //       value: addEditOutwardController.selectedDestination,
                          //       items: addEditOutwardController.destinationList
                          //           ?.map((DropdownDetail value) {
                          //         return DropdownMenuItem<DropdownDetail>(
                          //           value: value,
                          //           child: Text(value.text!),
                          //         );
                          //       }).toList(),
                          //       onChanged: (DropdownDetail? value) {
                          //         addEditOutwardController.selectedDestination =
                          //             value;
                          //       },
                          //       validator: (value) {
                          //         if (value == null ||
                          //             addEditOutwardController
                          //                     .selectedDestination ==
                          //                 null) {
                          //           return Strings.please_select_destination;
                          //         }
                          //         return null;
                          //       },
                          //     ),
                          //   ),
                          // ),

                          IgnorePointer(
                            ignoring: addEditOutwardController.isReadOnly,
                            child: DropdownSearch<DropdownDetail>(
                              key: addEditOutwardController.destinationDropDownKey,
                              mode: Mode.form,
                              selectedItem: addEditOutwardController.selectedDestination,
                              items: (filter, infiniteScrollProps) =>
                              addEditOutwardController.destinationList!,
                              compareFn: (item1, item2) => item1.id == item2.id,
                              itemAsString: (item) => item.text!,
                              decoratorProps: DropDownDecoratorProps(
                                baseStyle: TextStyle(
                                    color: AppTheme.title_dark, fontSize: AppTheme.small),
                                // Change text color
                                decoration: InputDecoration(
                                  hintText: Strings.select_destination,
                                  // ✅ Hint text for dropdown
                                  hintStyle: AppTheme.dropdownHintStyle,
                                  // labelStyle: TextStyle(color: Colors.black, fontSize: AppTheme.small),
                                  labelStyle: AppTheme.dropdownHintStyle,
                                  border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide:
                                    BorderSide(color: AppTheme.colorBlack, width: 0.8),
                                  ),
                                  focusColor: Colors.black,
                                  focusedBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                        color: AppTheme.colorPrimary, width: 0.8),
                                  ),
                                  enabledBorder: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(
                                        Constant.DROP_DOWN_ROUNDED_CORNER),
                                    borderSide: BorderSide(
                                      color: AppTheme.colorBlack,
                                      width: 1.0,
                                    ),
                                  ),
                                ),
                              ),
                              popupProps: PopupProps.menu(
                                showSearchBox: true,
                                fit: FlexFit.loose,
                                constraints: BoxConstraints(),
                                menuProps: MenuProps(
                                  backgroundColor: Colors.white,
                                  borderRadius: BorderRadius.circular(
                                      Constant.DROP_DOWN_ROUNDED_CORNER),
                                ),
                                searchFieldProps: TextFieldProps(
                                  decoration: InputDecoration(
                                    hintText: Strings.select_destination,
                                    hintStyle: AppTheme.dropdownHintStyle,
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(
                                          Constant.DROP_DOWN_ROUNDED_CORNER),
                                      borderSide: BorderSide(
                                          color: AppTheme.colorBlack, width: 0.8),
                                    ),
                                  ),
                                ),
                                listViewProps: ListViewProps(
                                  shrinkWrap: true,
                                ),
                              ),
                              onChanged: (value) {
                                addEditOutwardController.selectedDestination =
                                    value;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditOutwardController
                                        .selectedDestination ==
                                        null) {
                                  return Strings.please_select_destination;
                                }
                                return null;
                              },
                            ),
                          ),


                          /*const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.quantity_detail, require: false),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.available_qty_inward} :- ${addEditOutwardController.selectedInward!.unusedQty} "
                                : "${Strings.available_qty_inward} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.available_qty_outward} :- ${addEditOutwardController.selectedInward!.inTransitQty} "
                                : "${Strings.available_qty_outward} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CustomText(
                            title: addEditOutwardController.selectedInward !=
                                    null
                                ? "${Strings.used_qty} :- ${addEditOutwardController.selectedInward!.usedQty} "
                                : "${Strings.used_qty} :- 0",
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.normal,
                          ),*/
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.quantity_in, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                            // autovalidateMode : addEditOutwardController.from.equalsIgnoreCase(Strings.add) ?
                            // AutovalidateMode.onUserInteraction :
                            // AutovalidateMode.disabled ,
                              labelText: Strings.quantity_in,
                              textEditingController:
                                  addEditOutwardController.qtyController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.number,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_quantity_in;
                                } else {
                                  int enterQty;
                                  if(addEditOutwardController.from.equalsIgnoreCase(Strings.edit)){
                                    enterQty = int.parse(addEditOutwardController.qtyController.text);
                                    }else {
                                      enterQty = int.parse(value);
                                      if (enterQty <= 0 ||
                                          (enterQty >
                                              addEditOutwardController
                                                  .availableQty!)) {
                                        return Strings.enter_valid_quantity;
                                      }
                                    }
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              onChanged: (String? value) {
                                if(value!.isNotEmpty) {
                                  int enterQty = int.parse(value.toString());
                                  if (enterQty <= 0 ||
                                      (enterQty >
                                          addEditOutwardController
                                              .availableQty!)) {
                                    return Strings.enter_valid_quantity;
                                  } else {
                                    return null;
                                  }
                                }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: addEditOutwardController.from.equalsIgnoreCase(Strings.edit) ? true : false),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.outward_date, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.outward_date,
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
                              textEditingController: addEditOutwardController
                                  .outwardDateController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_outward_date;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                if (addEditOutwardController.isReadOnly) {
                                  print("not editable");
                                } else {
                                  selectDate(
                                      Strings.outward_date,
                                      DateTime(DateTime.now().year - 10),
                                      DateTime(DateTime.now().year + 10));
                                }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.status, require: true),
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
                              decoration: Utils.ddlDecoration(),
                              hint: Align(
                                alignment: Alignment.centerLeft,
                                child: Text(
                                  Strings.status,
                                  style: TextStyle(
                                    fontSize: AppTheme.medium,
                                    color: AppTheme.colorIconGrey,
                                    fontFamily: AppTheme.appFontName,
                                  ),
                                ),
                              ),
                              style: AppTheme.dropdownTextStyle,
                              isExpanded: false,
                              isDense: true,
                              value: addEditOutwardController.selectedStatus,
                              items: addEditOutwardController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditOutwardController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditOutwardController.selectedStatus ==
                                        null) {
                                  return Strings.please_select_status;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          reviewEditor()
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
                        title: Strings.submit,
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

  validateForm() {
    if (addEditOutwardFormKey.currentState!.validate()) {
      addEditOutwardController.addEditOutwardApiCall();
    } else {
      // setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
        addEditOutwardController.update();
      // });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditOutwardController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_outward
            : Strings.create_outward,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  Future<void> selectDate(
    String identity,
    DateTime firstDate,
    DateTime lastDate,
  ) async {
    DateTime? selectedDate;
    if (identity == Strings.outward_date) {
      if (addEditOutwardController.selectedDateTime != null) {
        selectedDate = addEditOutwardController.selectedDateTime;
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
      if (identity == Strings.outward_date) {
        addEditOutwardController.selectedDateTime = picked;
        addEditOutwardController.update();
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
        addEditOutwardController.selectedDateTime!.year,
        addEditOutwardController.selectedDateTime!.month,
        addEditOutwardController.selectedDateTime!.day,
        picked.hour,
        picked.minute,
      );
      addEditOutwardController.outwardDateController.text =
          addEditOutwardController.dateFormat.format(dt);
      addEditOutwardController.outwardDateTime =
          addEditOutwardController.apiDateTimeFormat.format(dt.toUtc());
      addEditOutwardController.update();
    }
  }

  reviewEditor() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        InputTitleRequire(title: Strings.description, require: true),
        const SizedBox(
          height: Constant.SMALL_PADDING,
        ),
        Container(
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(7.0),
              color: AppTheme.colorWhite),
          child: TextFormField(
            controller: addEditOutwardController.descriptionController,
            maxLines: 3,
            maxLength: 250,
            style: const TextStyle(fontSize: AppTheme.medium),
            decoration: InputDecoration(
              hintText: Strings.description,
              alignLabelWithHint: true,
              fillColor: Colors.transparent,
              contentPadding:
              const EdgeInsets.all(Constant.TEXT_FIELD_CONTENT_PADDING),
              focusColor: Colors.transparent,
              focusedBorder: OutlineInputBorder(
                borderRadius:
                BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
                borderSide:
                BorderSide(color: AppTheme.colorPrimary, width: 1.0),
              ),
              enabledBorder: OutlineInputBorder(
                borderRadius:
                BorderRadius.circular(Constant.BTN_ROUNDED_CORNER),
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
                return Strings.please_enter_description;
              }
              return null;
            },
          ),
        ),
        const SizedBox(height: Constant.MEDIUM_PADDING),
      ],
    );
  }

}
