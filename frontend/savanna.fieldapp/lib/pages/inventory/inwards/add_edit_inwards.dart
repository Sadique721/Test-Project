import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/inwards/add_edit_inwards_controller.dart';
import 'package:savbill/pages/inventory/module/response/get_all_active_products_by_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
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

class AddEditInward extends StatefulWidget {
  @override
  _AddEditInwardState createState() => _AddEditInwardState();
}

class _AddEditInwardState extends State<AddEditInward> {
  final addEditInwardsController = Get.put(AddEditInwardsController());
  final addEditInwardFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditInwardsController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addEditInwardsController.isLoading),
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
                      key: addEditInwardFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(
                            height: Constant.SCREEN_PADDING +
                                Constant.SMALL_PADDING,
                          ),
                          addEditInwardsController.inwardsDetail != null
                              ? InputTitleRequire(
                                  title:
                                      "${Strings.inward_no} :- ${addEditInwardsController.inwardsDetail!.inwardNumber!}",
                                  require: false)
                              : Container(),
                          addEditInwardsController.inwardsDetail != null
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
                            ignoring:
                                addEditInwardsController.inwardsDetail != null
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
                                value: addEditInwardsController.selectedProduct,
                                items: addEditInwardsController.productList
                                    ?.map(
                                        (AllActiveProductsByProductData value) {
                                  return DropdownMenuItem<
                                      AllActiveProductsByProductData>(
                                    value: value,
                                    child: Text(value.name!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditInwardsController.selectedProduct =
                                      value as AllActiveProductsByProductData?;

                                  addEditInwardsController
                                      .durationCalculate(value!.id);
                                  addEditInwardsController.update();
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditInwardsController
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
                              title: Strings.ware_house, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          /* IgnorePointer(
                            ignoring:
                                addEditInwardsController.inwardsDetail != null
                                    ? true
                                    : false,
                            child:
                          ),*/
                          DropdownButtonHideUnderline(
                            child: DropdownButtonFormField(
                              icon: SvgPicture.asset(
                                downArrowSvg,
                                height: Constant.SPACE_BW_RADIO_BTN,
                                width: Constant.SPACE_BW_RADIO_BTN,
                                color: AppTheme.colorBlack,
                                fit: BoxFit.fill,
                              ),
                              decoration: Utils.ddlDecoration(),
                              hint: Align(
                                alignment: Alignment.centerLeft,
                                child: Text(
                                  Strings.ware_house,
                                  overflow: TextOverflow.ellipsis,
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
                              value: addEditInwardsController.selectedWarehouse,
                              items: addEditInwardsController.wareHouseList
                                  ?.map((WareHouseDetail value) {
                                return DropdownMenuItem<WareHouseDetail>(
                                  value: value,
                                  child: Text(value.name!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditInwardsController.selectedWarehouse =
                                    value as WareHouseDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditInwardsController
                                            .selectedWarehouse ==
                                        null) {
                                  return Strings.please_select_ware_house;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.inward_date, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.inward_date,
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
                              textEditingController: addEditInwardsController
                                  .inwardsDateController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_select_inward_date;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {
                                // if (addEditInwardsController.inwardsDetail !=
                                //     null) {
                                //   print("not editable");
                                // } else {
                                selectDate(
                                    Strings.inward_date,
                                    DateTime(DateTime.now().year - 10),
                                    DateTime(DateTime.now().year + 10));
                                // }
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: true),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          Row(
                            children: [
                              InputTitleRequire(
                                  title: Strings.quantity_in, require: true),
                              addEditInwardsController.inwardsDetail != null
                                  ? CustomText(
                                      title:
                                          "${Strings.used_quantity} :- ${addEditInwardsController.inwardsDetail!.usedQty} ",
                                      colors: AppTheme.lable_noramal,
                                      textAlign: TextAlign.start,
                                      fontSize: AppTheme.small,
                                      fontWeight: FontWeight.normal,
                                    )
                                  : Container()
                            ],
                          ),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.quantity_in,
                              textEditingController:
                                  addEditInwardsController.qtyController,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              keyboardType: TextInputType.number,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.please_enter_quantity_in;
                                }
                                return null;
                              },
                              onTextFiledOnTap: () {},
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.MEDIUM_PADDING,
                          ),
                          InputTitleRequire(title: Strings.type, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          IgnorePointer(
                            ignoring:
                                addEditInwardsController.inwardsDetail != null
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
                                decoration: Utils.ddlDecoration(),
                                hint: Align(
                                  alignment: Alignment.centerLeft,
                                  child: Text(
                                    Strings.type,
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
                                value: addEditInwardsController.selectedType,
                                items: addEditInwardsController.typeList
                                    ?.map((DropdownDetail value) {
                                  return DropdownMenuItem<DropdownDetail>(
                                    value: value,
                                    child: Text(value.text!),
                                  );
                                }).toList(),
                                onChanged: (value) {
                                  addEditInwardsController.selectedType =
                                      value as DropdownDetail?;
                                },
                                validator: (value) {
                                  if (value == null ||
                                      addEditInwardsController.selectedType ==
                                          null) {
                                    return Strings.select_type;
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
                              value: addEditInwardsController.selectedStatus,
                              items: addEditInwardsController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditInwardsController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditInwardsController.selectedStatus ==
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
                          if ((addEditInwardsController.selectedProduct !=
                                  null) &&
                              (addEditInwardsController
                                      .selectedProduct!.expiryTime! >
                                  0))
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                InputTitleRequire(
                                    title: Strings.warranty_start_date,
                                    require: true),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                CoustomTextField(
                                    labelText: Strings.warranty_start_date,
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
                                    textEditingController:
                                        addEditInwardsController
                                            .warrantyStartDateController,
                                    borderEnableColors: AppTheme.colorBlack,
                                    textInputAction: TextInputAction.next,
                                    hintColor: AppTheme.colorIconGrey,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      // if (addEditInwardsController.inwardsDetail !=
                                      //     null) {
                                      //   print("not editable");
                                      // } else {
                                      selectDate(
                                          Strings.warranty_start_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                      // }
                                    },
                                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.LARGE_PADDING),
                                    readOnly: true),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                ),
                                InputTitleRequire(
                                    title: Strings.warranty_end_date,
                                    require: false),
                                const SizedBox(
                                  height: Constant.VERY_SMALL_PADDING,
                                ),
                                CoustomTextField(
                                    labelText: Strings.warranty_end_date,
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
                                    textEditingController:
                                        addEditInwardsController
                                            .warrantyEndDateController,
                                    borderEnableColors: AppTheme.colorBlack,
                                    textInputAction: TextInputAction.next,
                                    hintColor: AppTheme.colorIconGrey,
                                    onTextValidator: (String? value) {
                                      return null;
                                    },
                                    onTextFiledOnTap: () {
                                      // if (addEditInwardsController.inwardsDetail !=
                                      //     null) {
                                      //   print("not editable");
                                      // } else {
                                      selectDate(
                                          Strings.warranty_end_date,
                                          DateTime(DateTime.now().year - 10),
                                          DateTime(DateTime.now().year + 10));
                                      // }
                                    },
                                    borderCorner: Constant.INPUT_ROUNDED_CORNER,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.LARGE_PADDING),
                                    readOnly: true),
                                const SizedBox(
                                  height: Constant.SMALL_PADDING,
                                ),
                              ],
                            ),
                          reviewEditor(),
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
            controller: addEditInwardsController.descriptionController,
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
        const SizedBox(height: Constant.SMALL_PADDING),
      ],
    );
  }

  validateForm() {
    if (addEditInwardFormKey.currentState!.validate()) {
      addEditInwardsController.addEditInwardsApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditInwardsController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_inward
            : Strings.create_inward,
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
    if (identity == Strings.inward_date) {
      if (addEditInwardsController.selectedDateTime != null) {
        selectedDate = addEditInwardsController.selectedDateTime;
      } else {
        selectedDate = DateTime.now();
      }
    }
    if (identity == Strings.warranty_start_date) {
      if (addEditInwardsController.selectedDateTime != null) {
        selectedDate = addEditInwardsController.selectedDateTime;
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
      if (identity == Strings.inward_date) {
        addEditInwardsController.selectedDateTime = picked;
        addEditInwardsController.update();
        _selectDateTime(identity);
      }
      if (identity == Strings.warranty_start_date) {
        addEditInwardsController.selectedDateTime = picked;

        addEditInwardsController.warrantyStartDateController.text =
            addEditInwardsController.apiDateFormat.format(picked);
        addEditInwardsController.startDate =
            addEditInwardsController.apiDateFormat.format(picked);
        // _selectDateTime(identity);
        addEditInwardsController.calculateMonth(picked);

        addEditInwardsController.update();
      }
    }
  }

  Future<void> _selectDateTime(String identity) async {
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

    if (identity == Strings.inward_date) {
      if (picked != null) {
        DateTime dt = DateTime(
          addEditInwardsController.selectedDateTime!.year,
          addEditInwardsController.selectedDateTime!.month,
          addEditInwardsController.selectedDateTime!.day,
          picked.hour,
          picked.minute,
        );
        addEditInwardsController.inwardsDateController.text =
            addEditInwardsController.dateFormat.format(dt);
        addEditInwardsController.inwardsDateTime =
            addEditInwardsController.apiDateTimeFormat.format(dt.toUtc());

        addEditInwardsController.update();
      }
    } else {
      if (picked != null) {
        // DateTime dt = DateTime(
        //   addEditInwardsController.selectedDateTime!.year,
        //   addEditInwardsController.selectedDateTime!.month,
        //   addEditInwardsController.selectedDateTime!.day,
        //   picked.hour,
        //   picked.minute,
        // );
        // addEditInwardsController.warrantyStartDateController.text =
        //     addEditInwardsController.apiDateFormat.format(dt);
        // addEditInwardsController.startDateTime =
        //     addEditInwardsController.apiDateFormat.format(dt);
        //
        // addEditInwardsController.update();
      }
    }
  }
}
