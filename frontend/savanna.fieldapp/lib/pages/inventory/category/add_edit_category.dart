import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/inventory/category/add_edit_category_controller.dart';
import 'package:savbill/pages/inventory/category/select_category_type_dialog.dart';
import 'package:savbill/pages/inventory/module/response/category_type_res.dart';
import 'package:savbill/pages/inventory/module/response/get_dtv_category_res.dart';
import 'package:savbill/pages/inventory/module/response/inventroy_uom_data_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/model/response/network_device_type_res.dart';
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

class AddEditCategory extends StatefulWidget {
  @override
  _AddEditCategoryState createState() => _AddEditCategoryState();
}

class _AddEditCategoryState extends State<AddEditCategory>
    implements SelectCategoryTypeAction {
  final addEditCategoryController = Get.put(AddEditCategoryController());
  final addEditCategoryFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditCategoryController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: addEditCategoryController.isLoading),
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
                      key: addEditCategoryFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Stack(
                            children: <Widget>[
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
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: Constant.SCREEN_PADDING +
                                          Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.product_category_name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText:
                                            Strings.product_category_name,
                                        textEditingController:
                                            addEditCategoryController
                                                .categoryNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .enter_product_category_name;
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
                                            addEditCategoryController
                                                .productIdController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
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
                                        title: Strings.uom, require: true),
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
                                            Strings.select_uom,
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
                                        value: addEditCategoryController
                                            .selectedUomData,
                                        items: addEditCategoryController
                                            .uomDataList!
                                            .map((UOMDataList value) {
                                          return DropdownMenuItem<UOMDataList>(
                                            value: value,
                                            child: Text(value.displayName!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditCategoryController
                                                  .selectedUomData =
                                              value as UOMDataList?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditCategoryController
                                                      .selectedUomData ==
                                                  null) {
                                            return Strings.select_uom;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),

                                    /*CoustomTextField(
                                        labelText: Strings.uom,
                                        textEditingController:
                                            addEditCategoryController
                                                .uomController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_uom;
                                          }
                                          return null;
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: false),*/

                                    const SizedBox(
                                      height: Constant.LARGE_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.type, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    /* IgnorePointer(
                                      ignoring: addEditCategoryController
                                                  .categoryDetail !=
                                              null
                                          ? true
                                          : false,
                                      child: DropdownButtonHideUnderline(
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
                                            child: Text(
                                              Strings.type,
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
                                          value: addEditCategoryController
                                              .selectedCatType,
                                          items: addEditCategoryController
                                              .catTypeList
                                              ?.map((CategoryType value) {
                                            return DropdownMenuItem<
                                                CategoryType>(
                                              value: value,
                                              child: Text(value.text!),
                                            );
                                          }).toList(),
                                          onChanged: (value) {
                                            addEditCategoryController
                                                    .selectedCatType =
                                                value as CategoryType?;
                                          },
                                          validator: (value) {
                                            if (value == null ||
                                                addEditCategoryController
                                                        .selectedCatType ==
                                                    null) {
                                              return Strings.select_type;
                                            }
                                            return null;
                                          },
                                        ),
                                      ),
                                    ),*/
                                    CoustomTextField(
                                        labelText: Strings.type,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            addEditCategoryController
                                                .categoryTypeController,
                                        borderEnableColors:
                                            AppTheme.colorIconGrey,
                                        borderFocusColors:
                                            AppTheme.colorIconGrey,
                                        textColor: AppTheme.colorBlack,
                                        keyboardType: TextInputType.text,
                                        fontSize: AppTheme.small,
                                        textInputAction: TextInputAction.next,
                                        fontWeight: FontWeight.w500,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.MEDIUM_PADDING,
                                                vertical:
                                                    Constant.MEDIUM_PADDING),
                                        borderCorner:
                                            Constant.BTN_ROUNDED_CORNER,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .please_select_plan_type;
                                          }
                                        },
                                        onTextFiledOnTap: () {
                                          if (addEditCategoryController
                                                  .categoryDetail !=
                                              null) {
                                            print("not editable");
                                          } else {
                                            showSelectCategoryTypeDialog(
                                                Strings.category_type);
                                          }
                                        },
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.MEDIUM_PADDING,
                                    ),

                                    Visibility(
                                        visible: addEditCategoryController
                                            .isDeviceTypeVisible.value,
                                        child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          mainAxisAlignment:
                                              MainAxisAlignment.start,
                                          children: [
                                            InputTitleRequire(
                                                title: Strings.device_type,
                                                require: true),
                                            const SizedBox(
                                              height:
                                                  Constant.VERY_SMALL_PADDING,
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
                                                decoration:
                                                    Utils.ddlDecoration(),
                                                hint: Align(
                                                  alignment:
                                                      Alignment.centerLeft,
                                                  child: Text(
                                                    Strings.select_device_type,
                                                    style: TextStyle(
                                                      fontSize: AppTheme.medium,
                                                      color: AppTheme
                                                          .colorIconGrey,
                                                      fontFamily:
                                                          AppTheme.appFontName,
                                                    ),
                                                  ),
                                                ),
                                                style:
                                                    AppTheme.dropdownTextStyle,
                                                isExpanded: false,
                                                isDense: true,
                                                value: addEditCategoryController
                                                    .selectedDeviceType,
                                                items: addEditCategoryController
                                                    .deviceTypeList
                                                    ?.map((NetworkDeviceType
                                                        value) {
                                                  return DropdownMenuItem<
                                                      NetworkDeviceType>(
                                                    value: value,
                                                    child: Text(value.text!),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditCategoryController
                                                          .selectedDeviceType =
                                                      value
                                                          as NetworkDeviceType?;
                                                },
                                                validator: (value) {
                                                  if (value == null ||
                                                      addEditCategoryController
                                                              .selectedDeviceType ==
                                                          null) {
                                                    return Strings
                                                        .select_device_type;
                                                  }
                                                  return null;
                                                },
                                              ),
                                            ),
                                            const SizedBox(
                                              height: Constant.MEDIUM_PADDING,
                                            ),
                                          ],
                                        )),

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
                                        value: addEditCategoryController
                                            .selectedStatus,
                                        items: addEditCategoryController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditCategoryController
                                                  .selectedStatus =
                                              value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditCategoryController
                                                      .selectedStatus ==
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
                                  ],
                                ),
                              ),
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
                          Stack(
                            children: <Widget>[
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
                                          height: Constant.EXTRA_LARGE_PADDING,
                                        ),
                                        Row(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Row(
                                              children: [
                                                SizedBox(
                                                  width: 15,
                                                  height: 10,
                                                  child: Checkbox(
                                                    value:
                                                        addEditCategoryController
                                                            .macAddress,
                                                    activeColor:
                                                        AppTheme.colorPrimary,
                                                    onChanged: (value) {
                                                      addEditCategoryController
                                                              .macAddress =
                                                          !addEditCategoryController
                                                              .macAddress;
                                                      addEditCategoryController
                                                              .serialNo =
                                                          addEditCategoryController
                                                              .macAddress;
                                                      addEditCategoryController
                                                              .hasTrackable =
                                                          addEditCategoryController
                                                              .macAddress;
                                                      addEditCategoryController
                                                          .update();
                                                    },
                                                  ),
                                                ),
                                                const SizedBox(
                                                  width: Constant.SMALL_PADDING,
                                                ),
                                                InkWell(
                                                  onTap: () {
                                                    addEditCategoryController
                                                            .macAddress =
                                                        !addEditCategoryController
                                                            .macAddress;
                                                    addEditCategoryController
                                                            .serialNo =
                                                        addEditCategoryController
                                                            .macAddress;
                                                    addEditCategoryController
                                                            .hasTrackable =
                                                        addEditCategoryController
                                                            .macAddress;
                                                    addEditCategoryController
                                                        .update();
                                                  },
                                                  child: CustomText(
                                                    title:
                                                        Strings.has_mac_address,
                                                    colors: AppTheme.title_dark,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight:
                                                        FontWeight.normal,
                                                  ),
                                                ),
                                              ],
                                            ),
                                            const SizedBox(
                                                width: Constant.MEDIUM_PADDING),
                                            Row(
                                              children: [
                                                SizedBox(
                                                  width: 15,
                                                  height: 10,
                                                  child: Checkbox(
                                                    value:
                                                        addEditCategoryController
                                                            .serialNo,
                                                    activeColor:
                                                        AppTheme.colorPrimary,
                                                    onChanged:
                                                        addEditCategoryController
                                                                    .macAddress ==
                                                                true
                                                            ? null
                                                            : (value) {
                                                                addEditCategoryController
                                                                        .serialNo =
                                                                    !addEditCategoryController
                                                                        .serialNo;
                                                                addEditCategoryController
                                                                        .hasTrackable =
                                                                    addEditCategoryController
                                                                        .serialNo;

                                                                addEditCategoryController
                                                                    .update();
                                                              },
                                                  ),
                                                ),
                                                const SizedBox(
                                                  width: Constant.SMALL_PADDING,
                                                ),
                                                InkWell(
                                                  onTap: () {
                                                    if (addEditCategoryController
                                                            .macAddress ==
                                                        false) {
                                                      addEditCategoryController
                                                              .serialNo =
                                                          !addEditCategoryController
                                                              .serialNo;
                                                      addEditCategoryController
                                                              .hasTrackable =
                                                          addEditCategoryController
                                                              .serialNo;
                                                      addEditCategoryController
                                                          .update();
                                                    }
                                                  },
                                                  child: CustomText(
                                                    title: Strings
                                                        .has_serial_number,
                                                    colors: AppTheme.title_dark,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight:
                                                        FontWeight.normal,
                                                  ),
                                                ),
                                              ],
                                            ),
                                          ],
                                        ),
                                        const SizedBox(
                                          height: Constant.EXTRA_LARGE_PADDING,
                                        ),
                                        Row(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.center,
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Row(
                                              children: [
                                                SizedBox(
                                                  width: 15,
                                                  height: 10,
                                                  child: Checkbox(
                                                    value:
                                                        addEditCategoryController
                                                            .hasTrackable,
                                                    activeColor:
                                                        AppTheme.colorPrimary,
                                                    onChanged: (addEditCategoryController
                                                                    .macAddress ==
                                                                true &&
                                                            addEditCategoryController
                                                                    .serialNo ==
                                                                true)
                                                        ? null
                                                        : (value) {
                                                            addEditCategoryController
                                                                    .hasTrackable =
                                                                !addEditCategoryController
                                                                    .hasTrackable;

                                                            addEditCategoryController
                                                                .update();
                                                          },
                                                  ),
                                                ),
                                                const SizedBox(
                                                  width: Constant.SMALL_PADDING,
                                                ),
                                                InkWell(
                                                  onTap: () {
                                                    if (addEditCategoryController
                                                                .macAddress ==
                                                            false &&
                                                        addEditCategoryController
                                                                .serialNo ==
                                                            false) {
                                                      addEditCategoryController
                                                              .hasTrackable =
                                                          !addEditCategoryController
                                                              .hasTrackable;

                                                      addEditCategoryController
                                                          .update();
                                                    }
                                                  },
                                                  child: CustomText(
                                                    title:
                                                        Strings.has_trackable,
                                                    colors: AppTheme.title_dark,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight:
                                                        FontWeight.normal,
                                                  ),
                                                ),
                                              ],
                                            ),
                                            const SizedBox(
                                                width: Constant.MEDIUM_PADDING),
                                            Row(
                                              crossAxisAlignment:
                                                  CrossAxisAlignment.start,
                                              mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                              children: [
                                                SizedBox(
                                                  width: 15,
                                                  height: 10,
                                                  child: Checkbox(
                                                    value:
                                                        addEditCategoryController
                                                            .hasPort,
                                                    activeColor:
                                                        AppTheme.colorPrimary,
                                                    onChanged: (value) {
                                                      addEditCategoryController
                                                              .hasPort =
                                                          !addEditCategoryController
                                                              .hasPort;
                                                      addEditCategoryController
                                                          .update();
                                                    },
                                                  ),
                                                ),
                                                const SizedBox(
                                                  width: Constant.SMALL_PADDING,
                                                ),
                                                InkWell(
                                                  onTap: () {
                                                    if (addEditCategoryController
                                                            .hasPort ==
                                                        false) {
                                                      addEditCategoryController
                                                              .hasPort =
                                                          !addEditCategoryController
                                                              .hasPort;
                                                      addEditCategoryController
                                                          .update();
                                                    }
                                                  },
                                                  child: CustomText(
                                                    title: Strings.has_port,
                                                    colors: AppTheme.title_dark,
                                                    textAlign: TextAlign.start,
                                                    fontSize: AppTheme.small,
                                                    fontWeight:
                                                        FontWeight.normal,
                                                  ),
                                                ),
                                              ],
                                            ),
                                          ],
                                        ),
                                        const SizedBox(
                                          height: Constant.EXTRA_LARGE_PADDING,
                                        ),
                                        Row(
                                            crossAxisAlignment:
                                                CrossAxisAlignment.center,
                                            mainAxisAlignment:
                                                MainAxisAlignment.spaceBetween,
                                            children: [
                                              Flexible(
                                                flex: 1,
                                                child: Row(
                                                  children: [
                                                    SizedBox(
                                                      width: 15,
                                                      height: 10,
                                                      child: Checkbox(
                                                        value:
                                                            addEditCategoryController
                                                                .hasCas,
                                                        activeColor: AppTheme
                                                            .colorPrimary,
                                                        onChanged: (value) {
                                                          log("onChanged=>${addEditCategoryController.hasCas}");
                                                          addEditCategoryController
                                                                  .hasCas =
                                                              !addEditCategoryController
                                                                  .hasCas;
                                                          addEditCategoryController
                                                              .update();
                                                        },
                                                      ),
                                                    ),
                                                    const SizedBox(
                                                      width: Constant
                                                          .SMALL_PADDING,
                                                    ),
                                                    InkWell(
                                                      onTap: () {
                                                        log("inkWEllClick=>${addEditCategoryController.hasCas}");
                                                        //
                                                        // if (addEditCategoryController.hasCas == false) {
                                                        addEditCategoryController
                                                                .hasCas =
                                                            !addEditCategoryController
                                                                .hasCas;
                                                        addEditCategoryController
                                                            .update();
                                                        // }
                                                      },
                                                      child: CustomText(
                                                        title: Strings.has_cas,
                                                        colors:
                                                            AppTheme.title_dark,
                                                        textAlign:
                                                            TextAlign.start,
                                                        fontSize:
                                                            AppTheme.small,
                                                        fontWeight:
                                                            FontWeight.normal,
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                              ),
                                              addEditCategoryController
                                                          .hasCas ==
                                                      true
                                                  ? Flexible(
                                                      flex: 1,
                                                      child: Column(
                                                        crossAxisAlignment:
                                                            CrossAxisAlignment
                                                                .start,
                                                        mainAxisAlignment:
                                                            MainAxisAlignment
                                                                .start,
                                                        children: [
                                                          InputTitleRequire(
                                                              title: Strings
                                                                  .dtv_category,
                                                              require: true),
                                                          const SizedBox(
                                                            height: Constant
                                                                .VERY_SMALL_PADDING,
                                                          ),
                                                          DropdownButtonHideUnderline(
                                                            child:
                                                                DropdownButtonFormField(
                                                              icon: SvgPicture
                                                                  .asset(
                                                                downArrowSvg,
                                                                height: Constant
                                                                    .DROP_DOWN_ARROW_W_H,
                                                                width: Constant
                                                                    .DROP_DOWN_ARROW_W_H,
                                                                color: AppTheme
                                                                    .colorBlack,
                                                                fit:
                                                                    BoxFit.fill,
                                                              ),
                                                              decoration: Utils
                                                                  .ddlDecoration(),
                                                              hint: Align(
                                                                alignment: Alignment
                                                                    .centerLeft,
                                                                child: Text(
                                                                  Strings.type,
                                                                  style:
                                                                      TextStyle(
                                                                    fontSize:
                                                                        AppTheme
                                                                            .medium,
                                                                    color: AppTheme
                                                                        .colorIconGrey,
                                                                    fontFamily:
                                                                        AppTheme
                                                                            .appFontName,
                                                                  ),
                                                                ),
                                                              ),
                                                              style: AppTheme
                                                                  .dropdownTextStyle,
                                                              isExpanded: false,
                                                              isDense: true,
                                                              value: addEditCategoryController
                                                                  .selectedDtvCategoryData,
                                                              items: addEditCategoryController
                                                                  .dtvCategoryDataList!
                                                                  .map((DtvCategoryDataList
                                                                      value) {
                                                                return DropdownMenuItem<
                                                                    DtvCategoryDataList>(
                                                                  value: value,
                                                                  child: Text(
                                                                      value.text ??
                                                                          ""),
                                                                );
                                                              }).toList(),
                                                              onChanged:
                                                                  (value) {
                                                                addEditCategoryController
                                                                        .selectedDtvCategoryData =
                                                                    value
                                                                        as DtvCategoryDataList;
                                                              },
                                                              validator:
                                                                  (value) {
                                                                if (value ==
                                                                        null ||
                                                                    addEditCategoryController
                                                                            .selectedDtvCategoryData ==
                                                                        null) {
                                                                  return Strings
                                                                      .select_dtv_category_type;
                                                                }
                                                                return null;
                                                              },
                                                            ),
                                                          ),
                                                        ],
                                                      ),
                                                    )
                                                  : const SizedBox.shrink(),
                                            ]),
                                        const SizedBox(
                                          height: Constant.EXTRA_LARGE_PADDING,
                                        ),
                                      ]))
                            ],
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
    if (addEditCategoryFormKey.currentState!.validate()) {
      addEditCategoryController.addEditCategoryApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  showSelectCategoryTypeDialog(String from) {
    List<CategoryType> item = [];
    if (from.equalsIgnoreCase(Strings.category_type)) {
      if (addEditCategoryController.catTypeList != null &&
          addEditCategoryController.catTypeList!.isNotEmpty) {
        for (var element in addEditCategoryController.catTypeList!) {
          element.selected = false;
        }
        if (addEditCategoryController.selectedCategoryTypeList.isNotEmpty) {
          for (var element in addEditCategoryController.catTypeList!) {
            for (int selElement
                in addEditCategoryController.selectedCategoryTypeList) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditCategoryController.catTypeList!);

        addEditCategoryController.update();
      }
    }
    if (from.equalsIgnoreCase(Strings.category_type)) {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return SelectCategoryTypeDialog(
              fromFor: from,
              itemsOrgLst: item,
              selectCategoryTypeAction: this,
            );
          });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditCategoryController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_product_category
            : Strings.add_product_category,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  @override
  void selectCategoryTypeBtnAction(
      {String? identifier, List<CategoryType>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.category_type) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditCategoryController.selectedCategoryTypeList.clear();

      for (CategoryType element in selectedItem) {
        addEditCategoryController.selectedCategoryTypeList.add(element.id!);
        // serviceAreaName = "$serviceAreaName${element.text!}, ";
      }
      // if (!serviceAreaName.isNullOrEmpty() &&
      //     serviceAreaName.contains(",") &&
      //     serviceAreaName.length >= 2) {
      //   serviceAreaName =
      //       serviceAreaName.substring(0, serviceAreaName.length - 2);
      // }


      if (selectedItem.length == 1) {
        if (selectedItem[0].value!.contains("CustomerBind")) {
          serviceAreaName = "CustomerBind";
          addEditCategoryController.isDeviceTypeVisible.value = false;
        } else if (selectedItem[0].value!.contains("NetworkBind")) {
          serviceAreaName = "NetworkBind";
          addEditCategoryController.isDeviceTypeVisible.value = true;
        } else if (selectedItem[0].value!.contains("NA")) {
          serviceAreaName = "NA";
          addEditCategoryController.isDeviceTypeVisible.value = false;
        }
      } else if (selectedItem.length == 2) {
        if (selectedItem[0].value!.contains("CustomerBind") &&
                selectedItem[1].value!.contains("NetworkBind") ||
            selectedItem[0].value!.contains("NetworkBind") &&
                selectedItem[1].value!.contains("CustomerBind")) {
          serviceAreaName = "CustomerBind, NetworkBind";
          addEditCategoryController.isDeviceTypeVisible.value = true;
        } else if (selectedItem[0].value!.contains("NA") &&
                selectedItem[1].value!.contains("NetworkBind") ||
            selectedItem[0].value!.contains("NetworkBind") &&
                selectedItem[1].value!.contains("NA")) {
          serviceAreaName = "NA, NetworkBind";
          addEditCategoryController.isDeviceTypeVisible.value = true;
        } else if (selectedItem[0].value!.contains("NA") &&
                selectedItem[1].value!.contains("CustomerBind") ||
            selectedItem[0].value!.contains("CustomerBind") &&
                selectedItem[1].value!.contains("NA")) {
          serviceAreaName = "CustomerBind, NA";
          addEditCategoryController.isDeviceTypeVisible.value = false;
        } else if (selectedItem[0].value!.contains("CustomerBind") &&
                selectedItem[1].value!.contains("NA") ||
            selectedItem[0].value!.contains("NA") &&
                selectedItem[1].value!.contains("CustomerBind")) {
          serviceAreaName = "CustomerBind, NA";
          addEditCategoryController.isDeviceTypeVisible.value = false;
        } else if (selectedItem[0].value!.contains("NetworkBind") &&
                selectedItem[1].value!.contains("NA") ||
            selectedItem[0].value!.contains("NA") &&
                selectedItem[1].value!.contains("NetworkBind")) {
          serviceAreaName = "NA, NetworkBind";
          addEditCategoryController.isDeviceTypeVisible.value = true;
        }
      } else if (selectedItem.length == 3) {
        serviceAreaName = "CustomerBind, NA, NetworkBind";
        addEditCategoryController.isDeviceTypeVisible.value = true;
      }

      addEditCategoryController.categoryTypeController.text = serviceAreaName;
    }
    addEditCategoryController.update();
  }
}
