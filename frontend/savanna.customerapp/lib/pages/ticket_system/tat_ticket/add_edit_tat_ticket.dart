import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/pages/ticket_system/tat_ticket/add_edit_tat_ticket_controller.dart';
import 'package:savbill/pages/ticket_system/tat_ticket/tat_ticket_mapping_item.dart';
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

import '../../dashboard/model/response/show_tat_details_res.dart';

class AddEditTatTicket extends StatefulWidget {
  @override
  _AddEditTatTicketState createState() => _AddEditTatTicketState();
}

class _AddEditTatTicketState extends State<AddEditTatTicket>
    with WidgetsBindingObserver {
  final addEditTatTicketController = Get.put(AddEditTatTicketController());
  final addEditTatTicketFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditTatTicketController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditTatTicketController.isLoading),
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
                      key: addEditTatTicketFormKey,
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
                                        title: Strings.tat_name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_tat_name,
                                        textEditingController:
                                            addEditTatTicketController
                                                .ticketNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings.enter_ticket_name;
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
                                        value: addEditTatTicketController
                                            .selectedStatus,
                                        items: addEditTatTicketController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditTatTicketController
                                                  .selectedStatus =
                                              value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                              value: addEditTatTicketController
                                                  .selectedUnit,
                                              items: addEditTatTicketController
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
                                                addEditTatTicketController
                                                        .selectedUnit =
                                                    value as String?;
                                                addEditTatTicketController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                              value: addEditTatTicketController
                                                  .selectedSLAUnitP1,
                                              items: addEditTatTicketController
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
                                                addEditTatTicketController
                                                        .selectedSLAUnitP1 =
                                                    value as String?;
                                                addEditTatTicketController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                              value: addEditTatTicketController
                                                  .selectedSLAUnitP2,
                                              items: addEditTatTicketController
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
                                                addEditTatTicketController
                                                        .selectedSLAUnitP2 =
                                                    value as String?;
                                                addEditTatTicketController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                              value: addEditTatTicketController
                                                  .selectedSLAUnitP3,
                                              items: addEditTatTicketController
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
                                                addEditTatTicketController
                                                        .selectedSLAUnitP3 =
                                                    value as String?;
                                                addEditTatTicketController
                                                    .update();
                                              },
                                              validator: (value) {
                                                if (value == null ||
                                                    addEditTatTicketController
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
                                                "${Strings.order_no} : ${addEditTatTicketController.orderId}",
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
                                                "${Strings.level} : Level-${addEditTatTicketController.orderId}",
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
                                                  addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                                  addEditTatTicketController
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
                                                    addEditTatTicketController
                                                        .selectedTATUnit,
                                                items:
                                                    addEditTatTicketController
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
                                                  addEditTatTicketController
                                                          .selectedTATUnit =
                                                      value as String?;
                                                  addEditTatTicketController
                                                      .update();
                                                },
                                                validator: (value) {
                                                  /* if (value == null ||
                                      addEditTatTicketController
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
                                                    addEditTatTicketController
                                                        .selectedAction,
                                                items:
                                                    addEditTatTicketController
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
                                                  addEditTatTicketController
                                                          .selectedAction =
                                                      value as String?;
                                                  addEditTatTicketController
                                                      .update();
                                                },
                                                validator: (value) {
                                                  /*  if (value == null ||
                                      addEditTatTicketController
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
                                            if (addEditTatTicketController.tatTimeP1Controller.text.isEmpty ||
                                                addEditTatTicketController
                                                    .tatTimeP2Controller
                                                    .text
                                                    .isEmpty ||
                                                addEditTatTicketController
                                                    .tatTimeP3Controller
                                                    .text
                                                    .isEmpty ||
                                                (addEditTatTicketController
                                                            .selectedTATUnit ==
                                                        null ||
                                                    addEditTatTicketController
                                                        .selectedTATUnit!
                                                        .isEmpty) ||
                                                (addEditTatTicketController
                                                            .selectedAction ==
                                                        null ||
                                                    addEditTatTicketController
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
                                                addEditTatTicketController
                                                    .tatTimeP1Controller.text);
                                            num mTime2 = int.parse(
                                                addEditTatTicketController
                                                    .tatTimeP2Controller.text);
                                            num mTime3 = int.parse(
                                                addEditTatTicketController
                                                    .tatTimeP3Controller.text);
                                            addEditTatTicketController
                                                .tatMatrixMappings!
                                                .add(TatMatrixMappings(
                                                    orderNo:
                                                        addEditTatTicketController
                                                            .orderId,
                                                    level:
                                                        "Level-${addEditTatTicketController.orderId}",
                                                    mtime1: mTime1,
                                                    mtime2: mTime2,
                                                    mtime3: mTime3,
                                                    munit:
                                                        addEditTatTicketController
                                                            .selectedTATUnit,
                                                    action:
                                                        addEditTatTicketController
                                                            .selectedAction));

                                            addEditTatTicketController.orderId =
                                                addEditTatTicketController
                                                        .orderId +
                                                    1;
                                            addEditTatTicketController
                                                .tatTimeP1Controller
                                                .clear();
                                            addEditTatTicketController
                                                .tatTimeP2Controller
                                                .clear();
                                            addEditTatTicketController
                                                .tatTimeP3Controller
                                                .clear();
                                            addEditTatTicketController
                                                .selectedTATUnit = null;
                                            addEditTatTicketController
                                                .selectedAction = null;
                                            addEditTatTicketController.update();
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
                                    (addEditTatTicketController.tatMatrixMappings !=
                                                null &&
                                            addEditTatTicketController
                                                .tatMatrixMappings!.isNotEmpty)
                                        ? ListView.builder(
                                            physics:
                                                const NeverScrollableScrollPhysics(),
                                            shrinkWrap: true,
                                            itemCount:
                                                addEditTatTicketController
                                                    .tatMatrixMappings!.length,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              TatMatrixMappings item =
                                                  addEditTatTicketController
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
                                                      addEditTatTicketController
                                                          .tatMatrixMappings!
                                                          .removeAt(index);
                                                      addEditTatTicketController
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
    if (addEditTatTicketFormKey.currentState!.validate()) {
      if(addEditTatTicketController.tatMatrixMappings!.isNotEmpty) {
        addEditTatTicketController.addEditTatTicketApiCall();
      }else{
        Utils.showSnackbar(Strings.INFO, Strings.please_add_tat_tat_detail,
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
        addEditTatTicketController.from.equalsIgnoreCase(Strings.edit)
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
