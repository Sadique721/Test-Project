import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/tat_ticket/tat_ticket_mapping_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
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

import '../../../dashboard/model/response/show_tat_details_res.dart';
import 'addEdit_tat_task_controller.dart';

class AddEditTatTask extends StatefulWidget {
  @override
  _AddEditTatTaskState createState() => _AddEditTatTaskState();
}

class _AddEditTatTaskState extends State<AddEditTatTask >
    with WidgetsBindingObserver {
  final addEditTatTaskController = Get.put(AddEditTatTaskController());
  final addEditTatTaskFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<AddEditTatTaskController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditTatTaskController.isLoading),
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
                      key: addEditTatTaskFormKey,
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
                                        height: Constant.SPACE_BW_TEXT_FIELD),
                                    InputTitleRequire(
                                        title: Strings.task_name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.task_name,
                                        textEditingController:
                                        addEditTatTaskController
                                            .taskNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_task_name;
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
                                        value: addEditTatTaskController
                                            .selectedStatus,
                                        items: addEditTatTaskController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditTatTaskController
                                              .selectedStatus =
                                          value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditTatTaskController
                                                  .selectedStatus ==
                                                  null) {
                                            return Strings.please_select_status;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.response_time,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Row(
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.response_time,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .responseTimeController,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings
                                                      .enter_response_time;
                                                }
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
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
                                                  alignment:
                                                  Alignment.centerLeft,
                                                  child: Text(Strings.unit,
                                                      style: TextStyle(
                                                        fontSize:
                                                        AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: addEditTatTaskController
                                                  .selectedUnit,
                                              items: addEditTatTaskController
                                                  .unitList!
                                                  .map((String value) {
                                                return DropdownMenuItem<String>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addEditTatTaskController
                                                    .selectedUnit =
                                                value as String?;
                                                addEditTatTaskController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTaskController
                                                        .selectedUnit ==
                                                        null) {
                                                  return Strings.select_unit;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
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
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
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
                                      height: Constant.SCREEN_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.sla_time_p1,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Row(
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.sla_time_p1,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .slaTimeP1Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings.enter_sla_time;
                                                }
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
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
                                                  alignment:
                                                  Alignment.centerLeft,
                                                  child: Text(Strings.unit,
                                                      style: TextStyle(
                                                        fontSize:
                                                        AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: addEditTatTaskController
                                                  .selectedSLAUnitP1,
                                              items: addEditTatTaskController
                                                  .unitList!
                                                  .map((String value) {
                                                return DropdownMenuItem<String>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addEditTatTaskController
                                                    .selectedSLAUnitP1 =
                                                value as String?;
                                                addEditTatTaskController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTaskController
                                                        .selectedSLAUnitP1 ==
                                                        null) {
                                                  return Strings.select_unit;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.sla_time_p2,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Row(
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.sla_time_p2,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .slaTimeP2Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings.enter_sla_time;
                                                }
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
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
                                                  alignment:
                                                  Alignment.centerLeft,
                                                  child: Text(Strings.unit,
                                                      style: TextStyle(
                                                        fontSize:
                                                        AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: addEditTatTaskController
                                                  .selectedSLAUnitP2,
                                              items: addEditTatTaskController
                                                  .unitList!
                                                  .map((String value) {
                                                return DropdownMenuItem<String>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addEditTatTaskController
                                                    .selectedSLAUnitP2 =
                                                value as String?;
                                                addEditTatTaskController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTaskController
                                                        .selectedSLAUnitP2 ==
                                                        null) {
                                                  return Strings.select_unit;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.sla_time_p3,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Row(
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.sla_time_p3,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .slaTimeP3Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                if (value!.isEmpty) {
                                                  return Strings.enter_sla_time;
                                                }
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
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
                                                  alignment:
                                                  Alignment.centerLeft,
                                                  child: Text(Strings.unit,
                                                      style: TextStyle(
                                                        fontSize:
                                                        AppTheme.medium,
                                                        color: AppTheme
                                                            .colorIconGrey,
                                                        fontFamily: AppTheme
                                                            .appFontName,
                                                      ))),
                                              style: AppTheme.dropdownTextStyle,
                                              isExpanded: true,
                                              isDense: true,
                                              value: addEditTatTaskController
                                                  .selectedSLAUnitP3,
                                              items: addEditTatTaskController
                                                  .unitList!
                                                  .map((String value) {
                                                return DropdownMenuItem<String>(
                                                  value: value,
                                                  child: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: CustomText(
                                                      title: value,
                                                      colors:
                                                      AppTheme.colorBlack,
                                                      textAlign:
                                                      TextAlign.start,
                                                      fontSize: AppTheme.small,
                                                      fontWeight:
                                                      FontWeight.w500,
                                                    ), //Text(value.desig!),
                                                  ),
                                                );
                                              }).toList(),
                                              onChanged: (value) {
                                                addEditTatTaskController
                                                    .selectedSLAUnitP3 =
                                                value as String?;
                                                addEditTatTaskController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTaskController
                                                        .selectedSLAUnitP3 ==
                                                        null) {
                                                  return Strings.select_unit;
                                                }
                                                return null;
                                              },
                                            ),
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
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
                                    title: Strings.sla,
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
                                      height: Constant.SCREEN_PADDING,
                                    ),
                                    Row(
                                      mainAxisSize: MainAxisSize.max,
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      children: [
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title:
                                            "${Strings.order_no} : ${addEditTatTaskController.orderId}",
                                            colors: AppTheme.title_dark,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.normal,
                                          ),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Expanded(
                                          flex: 1,
                                          child: CustomText(
                                            title:
                                            "${Strings.level} : Level-${addEditTatTaskController.orderId}",
                                            colors: AppTheme.title_dark,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.normal,
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.tat_time, require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    Row(
                                      crossAxisAlignment:
                                      CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                      children: [
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.tat_time_p1,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .tatTimeP1Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                /* if (value!.isEmpty) {
                                        return Strings.enter_tat_time_p1;
                                      }*/
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.tat_time_p2,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .tatTimeP2Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                /* if (value!.isEmpty) {
                                        return Strings.enter_tat_time_p2;
                                      }*/
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                        const SizedBox(
                                          width: Constant.SMALL_PADDING,
                                        ),
                                        Flexible(
                                          flex: 1,
                                          child: CoustomTextField(
                                              labelText: Strings.tat_time_p3,
                                              textEditingController:
                                              addEditTatTaskController
                                                  .tatTimeP3Controller,
                                              keyboardType:
                                              TextInputType.number,
                                              borderEnableColors:
                                              AppTheme.colorBlack,
                                              textInputAction:
                                              TextInputAction.next,
                                              hintColor: AppTheme.colorIconGrey,
                                              onTextValidator: (String? value) {
                                                /* if (value!.isEmpty) {
                                        return Strings.enter_tat_time_p3;
                                      }*/
                                                return null;
                                              },
                                              borderCorner:
                                              Constant.INPUT_ROUNDED_CORNER,
                                              contentPadding:
                                              const EdgeInsets.symmetric(
                                                  horizontal: Constant
                                                      .LARGE_PADDING),
                                              readOnly: false),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Row(
                                        crossAxisAlignment:
                                        CrossAxisAlignment.center,
                                        mainAxisAlignment:
                                        MainAxisAlignment.spaceBetween,
                                        children: [
                                          Flexible(
                                            flex: 1,
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
                                                decoration:
                                                Utils.ddlDecoration(),
                                                hint: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: Text(Strings.unit,
                                                        style: TextStyle(
                                                          fontSize:
                                                          AppTheme.medium,
                                                          color: AppTheme
                                                              .colorIconGrey,
                                                          fontFamily: AppTheme
                                                              .appFontName,
                                                        ))),
                                                style:
                                                AppTheme.dropdownTextStyle,
                                                isExpanded: true,
                                                isDense: true,
                                                value:
                                                addEditTatTaskController
                                                    .selectedTATUnit,
                                                items:
                                                addEditTatTaskController
                                                    .unitList!
                                                    .map((String value) {
                                                  return DropdownMenuItem<
                                                      String>(
                                                    value: value,
                                                    child: Align(
                                                      alignment:
                                                      Alignment.centerLeft,
                                                      child: CustomText(
                                                        title: value,
                                                        colors:
                                                        AppTheme.colorBlack,
                                                        textAlign:
                                                        TextAlign.start,
                                                        fontSize:
                                                        AppTheme.small,
                                                        fontWeight:
                                                        FontWeight.w500,
                                                      ), //Text(value.desig!),
                                                    ),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditTatTaskController
                                                      .selectedTATUnit =
                                                  value as String?;
                                                  addEditTatTaskController
                                                      .update();
                                                },
                                                validator: (value) {
                                                  /* if (value == null ||
                                      addEditTatTaskController
                                          .selectedSLAUnitP3 ==
                                          null) {
                                    return Strings.select_unit;
                                  }*/
                                                  return null;
                                                },
                                              ),
                                            ),
                                          ),
                                          const SizedBox(
                                            width: Constant.MEDIUM_PADDING,
                                          ),
                                          Flexible(
                                            flex: 1,
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
                                                decoration:
                                                Utils.ddlDecoration(),
                                                hint: Align(
                                                    alignment:
                                                    Alignment.centerLeft,
                                                    child: Text(Strings.action,
                                                        style: TextStyle(
                                                          fontSize:
                                                          AppTheme.medium,
                                                          color: AppTheme
                                                              .colorIconGrey,
                                                          fontFamily: AppTheme
                                                              .appFontName,
                                                        ))),
                                                style:
                                                AppTheme.dropdownTextStyle,
                                                isExpanded: true,
                                                isDense: true,
                                                value:
                                                addEditTatTaskController
                                                    .selectedAction,
                                                items:
                                                addEditTatTaskController
                                                    .actionList!
                                                    .map((String value) {
                                                  return DropdownMenuItem<
                                                      String>(
                                                    value: value,
                                                    child: Align(
                                                      alignment:
                                                      Alignment.centerLeft,
                                                      child: CustomText(
                                                        title: value,
                                                        colors:
                                                        AppTheme.colorBlack,
                                                        textAlign:
                                                        TextAlign.start,
                                                        fontSize:
                                                        AppTheme.small,
                                                        fontWeight:
                                                        FontWeight.w500,
                                                      ), //Text(value.desig!),
                                                    ),
                                                  );
                                                }).toList(),
                                                onChanged: (value) {
                                                  addEditTatTaskController
                                                      .selectedAction =
                                                  value as String?;
                                                  addEditTatTaskController
                                                      .update();
                                                },
                                                validator: (value) {
                                                  /*  if (value == null ||
                                      addEditTatTaskController
                                          .selectedAction ==
                                          null) {
                                    return Strings.select_action;
                                  }*/
                                                  return null;
                                                },
                                              ),
                                            ),
                                          ),
                                        ]),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            if (addEditTatTaskController.tatTimeP1Controller.text.isEmpty ||
                                                addEditTatTaskController
                                                    .tatTimeP2Controller
                                                    .text
                                                    .isEmpty ||
                                                addEditTatTaskController
                                                    .tatTimeP3Controller
                                                    .text
                                                    .isEmpty ||
                                                (addEditTatTaskController
                                                    .selectedTATUnit ==
                                                    null ||
                                                    addEditTatTaskController
                                                        .selectedTATUnit!
                                                        .isEmpty) ||
                                                (addEditTatTaskController
                                                    .selectedAction ==
                                                    null ||
                                                    addEditTatTaskController
                                                        .selectedAction!
                                                        .isEmpty)) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  "Please enter the tat detail.",
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                              return;
                                            }
                                            num mTime1 = int.parse(
                                                addEditTatTaskController
                                                    .tatTimeP1Controller.text);
                                            num mTime2 = int.parse(
                                                addEditTatTaskController
                                                    .tatTimeP2Controller.text);
                                            num mTime3 = int.parse(
                                                addEditTatTaskController
                                                    .tatTimeP3Controller.text);
                                            addEditTatTaskController
                                                .tatMatrixMappings!
                                                .add(TatMatrixMappings(
                                                orderNo:
                                                addEditTatTaskController
                                                    .orderId,
                                                level:
                                                "Level-${addEditTatTaskController.orderId}",
                                                mtime1: mTime1,
                                                mtime2: mTime2,
                                                mtime3: mTime3,
                                                munit:
                                                addEditTatTaskController
                                                    .selectedTATUnit,
                                                action:
                                                addEditTatTaskController
                                                    .selectedAction));

                                            addEditTatTaskController.orderId =
                                                addEditTatTaskController
                                                    .orderId +
                                                    1;
                                            addEditTatTaskController
                                                .tatTimeP1Controller
                                                .clear();
                                            addEditTatTaskController
                                                .tatTimeP2Controller
                                                .clear();
                                            addEditTatTaskController
                                                .tatTimeP3Controller
                                                .clear();
                                            addEditTatTaskController
                                                .selectedTATUnit = null;
                                            addEditTatTaskController
                                                .selectedAction = null;
                                            addEditTatTaskController.update();
                                          },
                                          child: CustomText(
                                            title: "+ Add",
                                            colors: AppTheme.colorPrimary,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.medium,
                                            fontWeight: FontWeight.w600,
                                          ),
                                        )),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
                                    (addEditTatTaskController.tatMatrixMappings !=
                                        null &&
                                        addEditTatTaskController
                                            .tatMatrixMappings!.isNotEmpty)
                                        ? ListView.builder(
                                        physics:
                                        const NeverScrollableScrollPhysics(),
                                        shrinkWrap: true,
                                        itemCount:
                                        addEditTatTaskController
                                            .tatMatrixMappings!.length,
                                        itemBuilder: (BuildContext context,
                                            int index) {
                                          TatMatrixMappings item =
                                          addEditTatTaskController
                                              .tatMatrixMappings![
                                          index];
                                          return Container(
                                            margin: EdgeInsets.only(
                                                top: index == 0
                                                    ? 0
                                                    : Constant
                                                    .VERY_SMALL_PADDING),
                                            child: TatTicketMappingItem(
                                                item: item,
                                                isShowDelete: true,
                                                onTapDelete: () {
                                                  addEditTatTaskController
                                                      .tatMatrixMappings!
                                                      .removeAt(index);
                                                  addEditTatTaskController
                                                      .update();
                                                }),
                                          );
                                        })
                                        : Container(),
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
                                    title: Strings.tat,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
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
                        title: Strings.add,
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
    if (addEditTatTaskFormKey.currentState!.validate()) {
      if(addEditTatTaskController.tatMatrixMappings!.isNotEmpty) {
        addEditTatTaskController.addEditTatTaskApiCall();
      }else{
        Utils.showSnackbar(Strings.INFO, Strings.please_add_tat_task_detail,
            AppTheme.colorWhite, AppTheme.colorBlueRView);
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditTatTaskController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_tat
            : Strings.create_tat,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
