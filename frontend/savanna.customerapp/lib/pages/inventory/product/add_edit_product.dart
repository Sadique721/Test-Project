import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
import 'package:savbill/pages/inventory/module/response/case_packege_res.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_taxes_all_res.dart';
import 'package:savbill/pages/inventory/module/response/product_manufacturer_list_Res.dart';
import 'package:savbill/pages/inventory/product/add_edit_product_controller.dart';
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
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class AddEditProduct extends StatefulWidget {
  @override
  _AddEditProductState createState() => _AddEditProductState();
}

class _AddEditProductState extends State<AddEditProduct> {
  final addEditProductController = Get.put(AddEditProductController());
  final addEditProductFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditProductController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditProductController.isLoading),
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
                      key: addEditProductFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Stack(
                            children: [
                              Container(
                                  width: double.infinity,
                                  margin:
                                      const EdgeInsets.fromLTRB(0, 20, 0, 10),
                                  padding: const EdgeInsets.only(
                                      bottom: 5, left: 15, right: 15),
                                  decoration: BoxDecoration(
                                    border: Border.all(
                                        color: AppTheme.colorBlackEnd,
                                        width: 1),
                                    borderRadius: BorderRadius.circular(5),
                                    shape: BoxShape.rectangle,
                                  ),
                                  child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      mainAxisAlignment:
                                          MainAxisAlignment.start,
                                      children: [
                                        const SizedBox(
                                            height: Constant.SCREEN_PADDING),
                                        InputTitleRequire(
                                            title: Strings.product_name,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        CoustomTextField(
                                            labelText: Strings.product_name,
                                            textEditingController:
                                                addEditProductController
                                                    .productNameController,
                                            keyboardType: TextInputType.text,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
                                            hintColor: AppTheme.colorIconGrey,
                                            onTextValidator: (String? value) {
                                              if (value!.isEmpty) {
                                                return Strings
                                                    .enter_product_name;
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
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.product_id,
                                            require: false),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        CoustomTextField(
                                            labelText: Strings.product_id,
                                            textEditingController:
                                                addEditProductController
                                                    .productIdController,
                                            keyboardType: TextInputType.text,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
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
                                            readOnly: false),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.ledger_id,
                                            require: false),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        CoustomTextField(
                                            labelText: Strings.ledger_id,
                                            textEditingController:
                                                addEditProductController
                                                    .ledgerIdController,
                                            keyboardType: TextInputType.text,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
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
                                            readOnly: false),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.product_category,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        IgnorePointer(
                                          ignoring: addEditProductController
                                                      .productDetail !=
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
                                              decoration: Utils.ddlDecoration(),
                                              hint: Align(
                                                alignment: Alignment.centerLeft,
                                                child: Text(
                                                  Strings.product_category,
                                                  style: TextStyle(
                                                    fontSize: AppTheme.medium,
                                                    color:
                                                        AppTheme.colorIconGrey,
                                                    fontFamily:
                                                        AppTheme.appFontName,
                                                  ),
                                                ),
                                              ),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: false,
                                              isDense: true,
                                              value: addEditProductController
                                                  .selectedCategory,
                                              items: addEditProductController
                                                  .categoryList
                                                  ?.map((CategoryDetail value) {
                                                return DropdownMenuItem<
                                                    CategoryDetail>(
                                                  value: value,
                                                  child: Text(value.name!),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addEditProductController
                                                        .selectedCategory =
                                                    value as CategoryDetail?;
                                                addEditProductController
                                                    .selectedCASData = null;
                                                addEditProductController.productDeviceTypeEvent(value!);
                                                setState(() {
                                                  addEditProductController
                                                      .update();
                                                });

                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditProductController
                                                            .selectedCategory ==
                                                        null) {
                                                  return Strings
                                                      .please_select_category;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        addEditProductController.ifSplitterCASDropdownShow.value == true ? Column(
                                          crossAxisAlignment: CrossAxisAlignment.start,
                                          children: [
                                            InputTitleRequire(
                                                title: Strings.cas,
                                                require: true),
                                            const SizedBox(
                                              height: Constant.VERY_SMALL_PADDING,
                                            ),
                                            DropdownButtonHideUnderline(
                                              child: DropdownButtonFormField(
                                                icon: SvgPicture.asset(
                                                  downArrowSvg,
                                                  height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                                  width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                                  color: AppTheme.colorBlack,
                                                  fit: BoxFit.fill,
                                                ),
                                                decoration: Utils.ddlDecoration(),
                                                hint: Align(
                                                  alignment: Alignment.centerLeft,
                                                  child: Text(
                                                    Strings.please_select_product_type,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme.colorIconGrey,
                                                      fontFamily:
                                                      AppTheme.appFontName,
                                                    ),
                                                  ),
                                                ),
                                                style: AppTheme.dropdownTextStyle,
                                                isExpanded: false,
                                                isDense: true,
                                                value: addEditProductController
                                                    .selectedCASData,
                                                items: addEditProductController
                                                    .casDataList
                                                    ?.map((CASDataList
                                                value) {
                                                  return DropdownMenuItem<
                                                      CASDataList>(
                                                    value: value,
                                                    child: Text(value.casname!),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditProductController
                                                      .selectedCASData =
                                                  value
                                                  as CASDataList?;
                                                  addEditProductController.update();
                                                },
                                                validator: (value) {
                                                  if (value == null ||
                                                      addEditProductController
                                                          .selectedCASData ==
                                                          null) {
                                                    return Strings
                                                        .please_select_cas;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                            const SizedBox(
                                              height: Constant.MEDIUM_PADDING,
                                            ),
                                          ],
                                        ):const SizedBox.shrink(),


                                        InputTitleRequire(
                                            title: Strings.manufacturer,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        DropdownButtonHideUnderline(
                                          child: DropdownButtonFormField(
                                            icon: SvgPicture.asset(
                                              downArrowSvg,
                                              height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              color: AppTheme.colorBlack,
                                              fit: BoxFit.fill,
                                            ),
                                            decoration: Utils.ddlDecoration(),
                                            hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: Text(
                                                Strings.Select_Manufacturer,
                                                style: TextStyle(
                                                  fontSize: AppTheme.medium,
                                                  color: AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ),
                                              ),
                                            ),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: false,
                                            isDense: true,
                                            value: addEditProductController
                                                .selectedManufacturerData,
                                            items: addEditProductController
                                                .manufacturerDataList
                                                ?.map((ManufacturerDataList
                                                    value) {
                                              return DropdownMenuItem<
                                                  ManufacturerDataList>(
                                                value: value,
                                                child: Text(value.name!),
                                              );
                                            }).toList(),
                                            onChanged: (value) {
                                              addEditProductController
                                                      .selectedManufacturerData =
                                                  value
                                                      as ManufacturerDataList?;
                                              addEditProductController.update();
                                            },
                                            validator: (value) {
                                              if (value == null ||
                                                  addEditProductController
                                                          .selectedManufacturerData ==
                                                      null) {
                                                return Strings
                                                    .please_select_Manufacturer;
                                              }
                                              return null;
                                            },
                                          ),
                                        ),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.warranty_time,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        CoustomTextField(
                                            labelText: Strings.enter_unit,
                                            textEditingController:
                                                addEditProductController
                                                    .warrantyTimeController,
                                            keyboardType: TextInputType.number,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
                                            hintColor: AppTheme.colorIconGrey,
                                            onTextValidator: (String? value) {
                                              if (value!.isEmpty) {
                                                return Strings
                                                    .enter_warranty_unit;
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
                                        const SizedBox(
                                          height: Constant.SMALL_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.warranty_time_unit,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        DropdownButtonHideUnderline(
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
                                            decoration: Utils.ddlDecoration(),
                                            hint: Align(
                                              alignment: Alignment.centerLeft,
                                              child: Text(
                                                Strings
                                                    .select_expiry_time_unit,
                                                style: TextStyle(
                                                  fontSize:
                                                      AppTheme.medium - 1,
                                                  color:
                                                      AppTheme.colorIconGrey,
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ),
                                              ),
                                            ),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: false,
                                            isDense: true,
                                            value: addEditProductController
                                                .selectedWarrantyTimeUnitData,
                                            items: addEditProductController
                                                .warrantyTimeUnitList
                                                ?.map((CustomerStatusDetail
                                                    value) {
                                              return DropdownMenuItem<
                                                  CustomerStatusDetail>(
                                                value: value,
                                                child: Text(value.text!),
                                              );
                                            }).toList(),
                                            onChanged: (value) {
                                              addEditProductController
                                                      .selectedWarrantyTimeUnitData =
                                                  value
                                                      as CustomerStatusDetail?;
                                              addEditProductController
                                                  .update();
                                            },
                                            validator: (value) {
                                              if (value == null ||
                                                  addEditProductController
                                                          .selectedWarrantyTimeUnitData ==
                                                      null) {
                                                return Strings
                                                    .select_expiry_time_unit;
                                              }
                                              return null;
                                            },
                                          ),
                                        ),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.status,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        DropdownButtonHideUnderline(
                                          child: DropdownButtonFormField(
                                            icon: SvgPicture.asset(
                                              downArrowSvg,
                                              height:
                                                  Constant.DROP_DOWN_ARROW_W_H,
                                              width:
                                                  Constant.DROP_DOWN_ARROW_W_H,
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
                                                  fontFamily:
                                                      AppTheme.appFontName,
                                                ),
                                              ),
                                            ),
                                            style: AppTheme.dropdownTextStyle,
                                            isExpanded: false,
                                            isDense: true,
                                            value: addEditProductController
                                                .selectedStatus,
                                            items: addEditProductController
                                                .statusList
                                                ?.map((DropdownDetail value) {
                                              return DropdownMenuItem<
                                                  DropdownDetail>(
                                                value: value,
                                                child: Text(value.text!),
                                              );
                                            }).toList(),
                                            onChanged: (value) {
                                              addEditProductController
                                                      .selectedStatus =
                                                  value as DropdownDetail?;
                                            },
                                            validator: (value) {
                                              if (value == null ||
                                                  addEditProductController
                                                          .selectedStatus ==
                                                      null) {
                                                return Strings
                                                    .please_select_status;
                                              }
                                              return null;
                                            },
                                          ),
                                        ),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                        InputTitleRequire(
                                            title: Strings.description,
                                            require: true),
                                        const SizedBox(
                                          height: Constant.VERY_SMALL_PADDING,
                                        ),
                                        CoustomTextField(
                                            labelText: Strings.description,
                                            textEditingController:
                                                addEditProductController
                                                    .descriptionController,
                                            keyboardType: TextInputType.text,
                                            borderEnableColors:
                                                AppTheme.colorBlack,
                                            textInputAction:
                                                TextInputAction.next,
                                            hintColor: AppTheme.colorIconGrey,
                                            onTextValidator: (String? value) {
                                              if (value!.isEmpty) {
                                                return Strings
                                                    .please_select_description;
                                              }
                                              return null;
                                              return null;
                                            },
                                            borderCorner:
                                                Constant.INPUT_ROUNDED_CORNER,
                                            contentPadding:
                                                const EdgeInsets.symmetric(
                                                    horizontal:
                                                        Constant.LARGE_PADDING),
                                            readOnly: false),
                                        const SizedBox(
                                          height: Constant.MEDIUM_PADDING,
                                        ),
                                      ])),
                              Positioned(
                                left: 50,
                                top: 10,
                                child: Container(
                                  padding: const EdgeInsets.only(
                                      bottom: 3, left: 3, right: 3, top: 3),
                                  color: Colors.white,
                                  child: CustomText(
                                    title: Strings.basic_details,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Stack(
                            children: [
                              Container(
                                width: double.infinity,
                                margin: const EdgeInsets.fromLTRB(0, 20, 0, 10),
                                padding: const EdgeInsets.only(
                                    bottom: 5, left: 15, right: 15),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                      color: AppTheme.colorBlackEnd, width: 1),
                                  borderRadius: BorderRadius.circular(5),
                                  shape: BoxShape.rectangle,
                                ),
                                child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      const SizedBox(
                                          height: Constant.SCREEN_PADDING),
                                      InputTitleRequire(
                                          title: Strings.actual_price,
                                          require: false),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings.enter_actual_price,
                                          textEditingController:
                                              addEditProductController
                                                  .actualPriceController,
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
                                          readOnly: false),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title: Strings.tax, require: false),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.select_tax,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: false,
                                          isDense: true,
                                          value: addEditProductController
                                              .selectedTaxData,
                                          items: addEditProductController
                                              .taxList
                                              ?.map((Taxlist value) {
                                            return DropdownMenuItem<
                                                Taxlist>(
                                              value: value,
                                              child: Text(value.name!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addEditProductController
                                                    .selectedTaxData =
                                                value as Taxlist?;
                                            addEditProductController.update();
                                          },
                                          validator: (value) {
                                            // if (value == null ||
                                            //     addEditProductController
                                            //             .selectedTaxData ==
                                            //         null) {
                                            //   return Strings
                                            //       .select_tax;
                                            // }
                                            return null;
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title:
                                              "${Strings.refund_amount} (${Strings.in_warranty})",
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings
                                              .enter_refund_amount_warranty,
                                          textEditingController:
                                              addEditProductController
                                                  .refundInAmountController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .enter_refund_amount_warranty;
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
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title:
                                              "${Strings.refund_amount} (${Strings.post_warranty})",
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings
                                              .enter_refund_amount_post_warranty,
                                          textEditingController:
                                              addEditProductController
                                                  .refundPostAmountController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .enter_refund_amount_post_warranty;
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
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                    ]),
                              ),
                              Positioned(
                                left: 50,
                                top: 10,
                                child: Container(
                                  padding: const EdgeInsets.only(
                                      bottom: 3, left: 3, right: 3, top: 3),
                                  color: Colors.white,
                                  child: CustomText(
                                    title: Strings.new_product_details,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          Stack(
                            children: [
                              Container(
                                width: double.infinity,
                                margin: const EdgeInsets.fromLTRB(0, 20, 0, 10),
                                padding: const EdgeInsets.only(
                                    bottom: 5, left: 15, right: 15),
                                decoration: BoxDecoration(
                                  border: Border.all(
                                      color: AppTheme.colorBlackEnd, width: 1),
                                  borderRadius: BorderRadius.circular(5),
                                  shape: BoxShape.rectangle,
                                ),
                                child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    children: [
                                      const SizedBox(
                                          height: Constant.SCREEN_PADDING),
                                      InputTitleRequire(
                                          title: Strings.actual_price,
                                          require: false),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings.enter_actual_price,
                                          textEditingController:
                                              addEditProductController
                                                  .refurActualPriceController,
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
                                          readOnly: false),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title: Strings.tax, require: false),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      DropdownButtonHideUnderline(
                                        child: DropdownButtonFormField(
                                          icon: SvgPicture.asset(
                                            downArrowSvg,
                                            height:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            width:
                                                Constant.DROP_DOWN_ARROW_W_H,
                                            color: AppTheme.colorBlack,
                                            fit: BoxFit.fill,
                                          ),
                                          decoration: Utils.ddlDecoration(),
                                          hint: Align(
                                            alignment: Alignment.centerLeft,
                                            child: Text(
                                              Strings.select_tax,
                                              style: TextStyle(
                                                fontSize: AppTheme.medium,
                                                color: AppTheme.colorIconGrey,
                                                fontFamily:
                                                    AppTheme.appFontName,
                                              ),
                                            ),
                                          ),
                                          style: AppTheme.dropdownTextStyle,
                                          isExpanded: false,
                                          isDense: true,
                                          value: addEditProductController
                                              .selectedRefurTaxData,
                                          items: addEditProductController
                                              .taxList
                                              ?.map((Taxlist value) {
                                            return DropdownMenuItem<
                                                Taxlist>(
                                              value: value,
                                              child: Text(value.name!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addEditProductController
                                                    .selectedRefurTaxData =
                                                value as Taxlist?;
                                            addEditProductController.update();
                                          },
                                          validator: (value) {
                                            // if (value == null ||
                                            //     addEditProductController
                                            //             .selectedTaxData ==
                                            //         null) {
                                            //   return Strings
                                            //       .select_tax;
                                            // }
                                            return null;
                                          },
                                        ),
                                      ),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title:
                                              "${Strings.refund_amount} (${Strings.in_warranty})",
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings
                                              .enter_refund_amount_warranty,
                                          textEditingController:
                                              addEditProductController
                                                  .refurRefundInAmountController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .enter_refund_amount_warranty;
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
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                      InputTitleRequire(
                                          title:
                                              "${Strings.refund_amount} (${Strings.post_warranty})",
                                          require: true),
                                      const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING,
                                      ),
                                      CoustomTextField(
                                          labelText: Strings
                                              .enter_refund_amount_post_warranty,
                                          textEditingController:
                                              addEditProductController
                                                  .refurRefundPostAmountController,
                                          keyboardType: TextInputType.number,
                                          borderEnableColors:
                                              AppTheme.colorBlack,
                                          textInputAction: TextInputAction.next,
                                          hintColor: AppTheme.colorIconGrey,
                                          onTextValidator: (String? value) {
                                            if (value!.isEmpty) {
                                              return Strings
                                                  .enter_refund_amount_post_warranty;
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
                                      const SizedBox(
                                        height: Constant.MEDIUM_PADDING,
                                      ),
                                    ]),
                              ),
                              Positioned(
                                left: 50,
                                top: 10,
                                child: Container(
                                  padding: const EdgeInsets.only(
                                      bottom: 3, left: 3, right: 3, top: 3),
                                  color: Colors.white,
                                  child: CustomText(
                                    title: Strings.refurburshied_product_details,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ],
                          ),

                          const SizedBox(
                            height: Constant.LARGE_PADDING,
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
    if (addEditProductFormKey.currentState!.validate()) {
      addEditProductController.addEditProductApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditProductController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_product
            : Strings.add_product,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
