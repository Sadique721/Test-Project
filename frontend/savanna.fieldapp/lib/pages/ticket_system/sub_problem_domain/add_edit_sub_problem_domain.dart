import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/inventory/pop/service_area_selection_dialog.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_for_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/add_condition.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/add_edit_sub_problem_domain_controller.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/condition_tat_map_item.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/select_parent_category_dialog.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/sub_problem_domain_reason_item.dart';
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
import 'package:multi_select_flutter/multi_select_flutter.dart';

import '../../inventory/pop/service_area_selection_dialog.dart';

class AddEditSubProblemDomain extends StatefulWidget {
  @override
  _AddEditSubProblemDomainState createState() =>
      _AddEditSubProblemDomainState();
}

class _AddEditSubProblemDomainState extends State<AddEditSubProblemDomain>
    with WidgetsBindingObserver
    implements SelectParentCategoryAction {
  final addEditSubProblemDomainController =
      Get.put(AddEditSubProblemDomainController());
  final addEditSubProblemDomainFormKey = GlobalKey<FormState>();
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
      child:
          GetBuilder<AddEditSubProblemDomainController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditSubProblemDomainController.isLoading),
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
                    key: addEditSubProblemDomainFormKey,
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
                                        height: Constant.LARGE_PADDING),
                                    InputTitleRequire(
                                        title: Strings.sub_problem_domain_name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.root_cause_name,
                                        textEditingController:
                                            addEditSubProblemDomainController
                                                .subProblemDomainNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .enter_sub_problem_domain_name;
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
                                        title: Strings.parent_category,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings
                                            .please_select_parent_category,
                                        hintColor: AppTheme.colorIconGrey,
                                        textEditingController:
                                            addEditSubProblemDomainController
                                                .parentCategoryController,
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
                                                .please_select_parent_service_area;
                                          }
                                        },
                                        onTextFiledOnTap: () {
                                          showParentCategorySelectionDialog(
                                              Strings.parent_category);
                                        },
                                        readOnly: true),

                                    /*MultiSelectBottomSheetField(
                                      autovalidateMode: autoValidateMode,
                                      initialValue: [
                                        addEditSubProblemDomainController.newParentCategoryList
                                        // addEditSubProblemDomainController.selectTicketSubCategoryReasonCategoryMappingList.
                                      ],
                                      buttonText: Text(
                                        addEditSubProblemDomainController.selectTicketSubCategoryReasonCategoryMappingList != null ?
                                        addEditSubProblemDomainController.selectTicketSubCategoryReasonCategoryMappingList!.ticketReasonCategoryId.toString()
                                            : Strings.please_select_parent_category,
                                        style: TextStyle(
                                          fontSize: AppTheme.large,
                                          fontWeight: FontWeight.normal,
                                          height: 1,
                                          color: AppTheme.colorIconGrey,
                                          fontFamily: AppTheme.appFontName,
                                          decoration: TextDecoration.none,
                                        ),
                                      ),
                                      selectedColor: AppTheme.colorAccent,
                                      buttonIcon:
                                      const Icon(Icons.arrow_drop_down_outlined),
                                      decoration: BoxDecoration(
                                        borderRadius: BorderRadius.circular(
                                            Constant.VERY_SMALL_PADDING),
                                        border:
                                        Border.all(color: AppTheme.colorLightBlack),
                                        color: AppTheme.colorWhite,
                                      ),
                                      items: addEditSubProblemDomainController
                                          .parentCategoryList!
                                          .map((e) => MultiSelectItem(e, e.categoryName!))
                                          .toList(),
                                      listType: MultiSelectListType.CHIP,
                                      searchable: true,
                                      separateSelectedItems: true,
                                      onConfirm: (value) {
                                        log("MultiSelectChipDisplay_onConfirm");
                                        // setState(() {
                                          log("MultiSelectListType");
                                          addEditSubProblemDomainController
                                              .newParentCategoryList =
                                              value as List<ProblemDomainDetail>;
                                          for (var element in value) {
                                            addEditSubProblemDomainController
                                                .selParentCategory =
                                            element;
                                            addEditSubProblemDomainController
                                                .addTicketSubCategoryReasonMapping();
                                          }
                                        // });
                                        addEditSubProblemDomainController.update();
                                      },
                                      onSelectionChanged: (item){
                                        log("MultiSelectChipDisplay_onSelectionChanged");
                                        // setState(() {
                                          addEditSubProblemDomainController
                                              .newParentCategoryList =
                                          item as List<ProblemDomainDetail>;
                                          for (var element in item) {
                                            addEditSubProblemDomainController
                                                .selParentCategory =
                                            element;
                                          }
                                        // });
                                        addEditSubProblemDomainController.update();
                                      },
                                      selectedItemsTextStyle: TextStyle(
                                        color: AppTheme.colorWhite,
                                        fontSize: AppTheme.medium,
                                        fontWeight: FontWeight.normal,
                                        height: 1,
                                        fontFamily: AppTheme.appFontName,
                                        decoration: TextDecoration.none,
                                      ),
                                      searchTextStyle: TextStyle(
                                        color: AppTheme.colorBlack,
                                        fontSize: AppTheme.small + 1,
                                        fontWeight: FontWeight.normal,
                                        height: 1,
                                        fontFamily: AppTheme.appFontName,
                                        decoration: TextDecoration.none,
                                      ),
                                      itemsTextStyle: TextStyle(
                                        color: AppTheme.colorBlack,
                                        fontSize: AppTheme.small + 1,
                                        fontWeight: FontWeight.normal,
                                        height: 1,
                                        fontFamily: AppTheme.appFontName,
                                        decoration: TextDecoration.none,
                                      ),
                                      chipDisplay:addEditSubProblemDomainController
                                          .newParentCategoryList == null ||
                                          addEditSubProblemDomainController
                                          .newParentCategoryList!.isEmpty
                                          ? MultiSelectChipDisplay(
                                        chipColor: AppTheme.colorAccentTheme,
                                        textStyle: TextStyle(
                                          color: AppTheme.colorWhite,
                                          fontSize: AppTheme.small,
                                          fontWeight: FontWeight.normal,
                                          height: 1,
                                          fontFamily: AppTheme.appFontName,
                                          decoration: TextDecoration.none,
                                        ),
                                      ): MultiSelectChipDisplay(),
                                      validator: (value) {
                                        log("MultiSelectChipDisplay_validator");
                                        if (value == null ||
                                            addEditSubProblemDomainController
                                                .newParentCategoryList!.isEmpty) {
                                          return Strings.please_select_parent_category;
                                        }
                                      },
                                      confirmText: Text(
                                        Strings.ok,
                                        style: TextStyle(
                                          fontSize: AppTheme.large,
                                          fontWeight: FontWeight.normal,
                                          height: 1,
                                          color: AppTheme.colorAccent,
                                          fontFamily: AppTheme.appFontName,
                                          decoration: TextDecoration.none,
                                        ),
                                      ),
                                      cancelText: Text(
                                        Strings.cancel,
                                        style: TextStyle(
                                          fontSize: AppTheme.large,
                                          fontWeight: FontWeight.normal,
                                          height: 1,
                                          color: AppTheme.colorIconGrey,
                                          fontFamily: AppTheme.appFontName,
                                          decoration: TextDecoration.none,
                                        ),
                                      ),
                                    ),*/
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
                                        value: addEditSubProblemDomainController
                                            .selectedStatus,
                                        items: addEditSubProblemDomainController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditSubProblemDomainController
                                                  .selectedStatus =
                                              value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditSubProblemDomainController
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
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 20,
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
                                      height: Constant.LARGE_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.reason, require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.reason,
                                        textEditingController:
                                            addEditSubProblemDomainController
                                                .reasonController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.done,
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
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            if (addEditSubProblemDomainController
                                                .reasonController
                                                .text
                                                .isEmpty) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  Strings.enter_reason,
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                              return;
                                            }
                                            addEditSubProblemDomainController
                                                .ticketSubCategoryGroupReasonMappingList!
                                                .add(TicketSubCategoryGroupReasonMappingList(
                                                    reason:
                                                        addEditSubProblemDomainController
                                                            .reasonController
                                                            .text));
                                            addEditSubProblemDomainController
                                                .reasonController
                                                .clear();
                                            addEditSubProblemDomainController
                                                .update();
                                          },
                                          child: CustomText(
                                            title: Strings.plus_add,
                                            colors: AppTheme.colorPrimary,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.medium,
                                            fontWeight: FontWeight.w600,
                                          ),
                                        )),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
                                    (addEditSubProblemDomainController
                                                    .ticketSubCategoryGroupReasonMappingList !=
                                                null &&
                                            addEditSubProblemDomainController
                                                .ticketSubCategoryGroupReasonMappingList!
                                                .isNotEmpty)
                                        ? ListView.builder(
                                            physics:
                                                const NeverScrollableScrollPhysics(),
                                            shrinkWrap: true,
                                            itemCount:
                                                addEditSubProblemDomainController
                                                    .ticketSubCategoryGroupReasonMappingList!
                                                    .length,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              TicketSubCategoryGroupReasonMappingList
                                                  item =
                                                  addEditSubProblemDomainController
                                                          .ticketSubCategoryGroupReasonMappingList![
                                                      index];
                                              return Container(
                                                margin: EdgeInsets.only(
                                                    top: index == 0
                                                        ? 0
                                                        : Constant
                                                            .VERY_SMALL_PADDING),
                                                child:
                                                    SubProblemDomainReasonItem(
                                                        item: item,
                                                        isShowDelete: true,
                                                        onTapDelete: () {
                                                          addEditSubProblemDomainController
                                                              .ticketSubCategoryGroupReasonMappingList!
                                                              .removeAt(index);
                                                          addEditSubProblemDomainController
                                                              .update();
                                                        }),
                                              );
                                            })
                                        : Container(),
                                    const SizedBox(
                                        height: Constant.VERY_SMALL_PADDING),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 20,
                                top: 10,
                                child: Container(
                                  padding: const EdgeInsets.only(
                                      bottom: 3, left: 3, right: 3, top: 3),
                                  color: Colors.white,
                                  child: CustomText(
                                    title: Strings.reason_detail,
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
                                      height: Constant.LARGE_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.tat_for_ticket,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    // DropdownButtonHideUnderline(
                                    //   child: DropdownButtonFormField(
                                    //     icon: SvgPicture.asset(
                                    //       downArrowSvg,
                                    //       height: Constant.DROP_DOWN_ARROW_W_H,
                                    //       width: Constant.DROP_DOWN_ARROW_W_H,
                                    //       color: AppTheme.colorBlack,
                                    //       fit: BoxFit.fill,
                                    //     ),
                                    //     decoration: Utils.ddlDecoration(),
                                    //     hint: Align(
                                    //       alignment: Alignment.centerLeft,
                                    //       child: Text(
                                    //         Strings.tat_for_ticket,
                                    //         style: TextStyle(
                                    //           fontSize: AppTheme.medium,
                                    //           color: AppTheme.colorIconGrey,
                                    //           fontFamily: AppTheme.appFontName,
                                    //         ),
                                    //       ),
                                    //     ),
                                    //     style: AppTheme.dropdownTextStyle,
                                    //     isExpanded: false,
                                    //     isDense: true,
                                    //     value: addEditSubProblemDomainController
                                    //         .selectedTatTicket,
                                    //     items: addEditSubProblemDomainController
                                    //         .subProblemTATTicketList
                                    //         ?.map((SubProblemDomainDetail value) {
                                    //       return DropdownMenuItem<SubProblemDomainDetail>(
                                    //         value: value,
                                    //         child: Text(value.subCategoryName!),
                                    //       );
                                    //     }).toList(),
                                    //     onChanged: (value) {
                                    //       addEditSubProblemDomainController
                                    //           .selectedSubProblemDomainData =
                                    //       value as SubProblemDomainDetail?;
                                    //     },
                                    //     validator: (value) {
                                    //       return null;
                                    //     },
                                    //   ),
                                    // ),
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
                                            Strings.tat_for_ticket,
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
                                        value: addEditSubProblemDomainController
                                            .selectedTatForData,
                                        items: addEditSubProblemDomainController
                                            .tatForDataList
                                            .map((TatTicketDetail value) {
                                          return DropdownMenuItem<
                                              TatTicketDetail>(
                                            value: value,
                                            child: Text(value.name!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditSubProblemDomainController
                                                  .selectedTatForData =
                                              value as TatTicketDetail?;
                                        },
                                        validator: (value) {
                                          return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    InputTitleRequire(
                                        title: Strings.add_condition,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.add_condition,
                                        textEditingController:
                                            addEditSubProblemDomainController
                                                .conditionController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.done,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          return null;
                                        },
                                        onTextFiledOnTap: () {
                                          openAddConditionScreen();
                                        },
                                        borderCorner:
                                            Constant.INPUT_ROUNDED_CORNER,
                                        contentPadding:
                                            const EdgeInsets.symmetric(
                                                horizontal:
                                                    Constant.LARGE_PADDING),
                                        readOnly: true),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            // if (/*addEditSubProblemDomainController
                                            //     .selectedTatTicket ==
                                            //     null ||
                                            //     */(addEditSubProblemDomainController
                                            //         .selectedCondition ==
                                            //         null ||
                                            //         addEditSubProblemDomainController
                                            //             .selectedCondition!.isEmpty)) {
                                            //   Utils.showSnackbar(
                                            //       Strings.ERROR,
                                            //       Strings.enter_tat_mapping_detail,
                                            //       AppTheme.colorWhite,
                                            //       AppTheme.colorRed);
                                            //   return;
                                            // }

                                            if (addEditSubProblemDomainController
                                                    .selectedTatForData ==
                                                null) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  Strings
                                                      .enter_tat_mapping_detail,
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                            } else {
                                              addEditSubProblemDomainController
                                                  .addTicketMapping();
                                            }
                                          },
                                          child: CustomText(
                                            title: Strings.plus_add,
                                            colors: AppTheme.colorPrimary,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.medium,
                                            fontWeight: FontWeight.w600,
                                          ),
                                        )),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
                                    (addEditSubProblemDomainController.ticketSubCategoryTatMappingList !=
                                                null &&
                                            addEditSubProblemDomainController
                                                .ticketSubCategoryTatMappingList!
                                                .isNotEmpty)
                                        ? ListView.builder(
                                            physics:
                                                const NeverScrollableScrollPhysics(),
                                            shrinkWrap: true,
                                            itemCount:
                                                addEditSubProblemDomainController
                                                    .ticketSubCategoryTatMappingList!
                                                    .length,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              TicketSubCategoryTatMappingList
                                                  item =
                                                  addEditSubProblemDomainController
                                                          .ticketSubCategoryTatMappingList![
                                                      index];
                                              return Container(
                                                margin: EdgeInsets.only(
                                                    top: index == 0
                                                        ? 0
                                                        : Constant
                                                            .VERY_SMALL_PADDING),
                                                child: ConditionTatMapItem(
                                                    item: item,
                                                    onTapDelete: () {
                                                      addEditSubProblemDomainController
                                                          .ticketSubCategoryTatMappingList!
                                                          .removeAt(index);
                                                      addEditSubProblemDomainController
                                                          .update();
                                                    }),
                                              );
                                            })
                                        : Container(),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                  ],
                                ),
                              ),
                              Positioned(
                                left: 20,
                                top: 10,
                                child: Container(
                                  padding: const EdgeInsets.only(
                                      bottom: 3, left: 3, right: 3, top: 3),
                                  color: Colors.white,
                                  child: CustomText(
                                    title: Strings.tat_mapping_detail,
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
                        ]),
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
          ],
        ),
      ),
    );
  }

  openAddConditionScreen() async {
    var result = await Get.to(AddCondition(), arguments: {
      Constant.CONDITION: addEditSubProblemDomainController.selectedCondition
    });
    if (result != null) {
      List<TatQueryFieldMappingList> tatQueryFieldMappingList = result;
      String conditionText = "";
      if (tatQueryFieldMappingList.isNotEmpty) {
        for (TatQueryFieldMappingList element in tatQueryFieldMappingList) {
          String operator = "";
          if (element.selectedOperator != null &&
              element.selectedOperator!.isNotEmpty) {
            if (element.selectedOperator!.equalsIgnoreCase(Strings.equal_to)) {
              operator = "==";
            } else if (element.selectedOperator!
                .equalsIgnoreCase(Strings.less_than_or_equal_to)) {
              operator = "<=";
            } else if (element.selectedOperator!
                .equalsIgnoreCase(Strings.greater_than_or_equal_to)) {
              operator = ">=";
            } else if (element.selectedOperator!
                .equalsIgnoreCase(Strings.less_than)) {
              operator = "<";
            } else if (element.selectedOperator!
                .equalsIgnoreCase(Strings.greater_than)) {
              operator = ">";
            } else if (element.selectedOperator!
                .equalsIgnoreCase(Strings.not_equal_to)) {
              operator = "!=";
            }
          }
          element.queryField =
              element.selectedField != null ? element.selectedField!.value : "";
          element.queryOperator = operator;
          element.queryCondition = element.selectedCondition != null
              ? element.selectedCondition!
              : "";

          if (element.selectedCondition != null) {
            conditionText =
                "$conditionText${element.selectedField!.text!} $operator ${element.queryValue!} ${element.selectedCondition!} ";
          } else {
            conditionText =
                "$conditionText${element.selectedField!.text!} $operator ${element.queryValue!}";
          }
        }
        addEditSubProblemDomainController.conditionController.text =
            conditionText;
        addEditSubProblemDomainController.selectedCondition =
            tatQueryFieldMappingList;
        addEditSubProblemDomainController.update();
      }
    }
  }

  validateForm() {
    if (addEditSubProblemDomainFormKey.currentState!.validate()) {
      if(addEditSubProblemDomainController.ticketSubCategoryTatMappingList!.isNotEmpty) {
        addEditSubProblemDomainController.addEditProblemDomainApiCall();
      }else{
        Utils.showSnackbar(
            Strings.INFO, Strings.please_add_tat_mapping_detail, AppTheme.colorWhite, AppTheme.colorBlueRView);
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditSubProblemDomainController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_ticket_sub_problem_domain
            : Strings.create_ticket_sub_problem_domain,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  showParentCategorySelectionDialog(String from) {
    List<ProblemDomainDetail> item = [];
    if (from.equalsIgnoreCase(Strings.parent_category)) {
      if (addEditSubProblemDomainController.parentCategoryList != null &&
          addEditSubProblemDomainController.parentCategoryList!.isNotEmpty) {
        for (var element
            in addEditSubProblemDomainController.parentCategoryList!) {
          element.selected = false;
        }
        if (addEditSubProblemDomainController
            .selectedParentCategoryIds.isNotEmpty) {
          for (var element
              in addEditSubProblemDomainController.parentCategoryList!) {
            for (int selElement in addEditSubProblemDomainController
                .selectedParentCategoryIds) {
              if (selElement == element.id!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditSubProblemDomainController.parentCategoryList!);
      }
    }
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return SelectParentCategoryDialog(
              serviceAreaSelectionAction: this,
              fromFor: from,
              parentCategoryList: item);
        });
  }

  @override
  void selectParentCategoryBtnAction(
      {String? identifier, List<ProblemDomainDetail>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.parent_category) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditSubProblemDomainController.selectedParentCategoryIds.clear();
      addEditSubProblemDomainController.selectedSubCategoryResMappingList!
          .clear();
      for (ProblemDomainDetail element in selectedItem) {
        addEditSubProblemDomainController.selectedParentCategoryIds
            .add(element.id!);
        addEditSubProblemDomainController.selectedSubCategoryResMappingList!
            .add(TicketSubCategoryReasonCategoryMappingList(
          ticketReasonCategoryId: element.id,
        ));
        serviceAreaName = "$serviceAreaName${element.categoryName!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }
      addEditSubProblemDomainController.parentCategoryController.text =
          serviceAreaName;
    }
    // else if (identifier.toString().equalsIgnoreCase(Strings.service_area) &&
    //     selectedItem != null &&
    //     selectedItem.isNotEmpty) {
    //   String serviceAreaName = "";
    //   addEditWareHouseController.selectedServiceArea.clear();
    //   for (StaffServiceAreaDetail element in selectedItem) {
    //     addEditWareHouseController.selectedServiceArea.add(element.id!);
    //     serviceAreaName = "$serviceAreaName${element.name!}, ";
    //   }
    //   if (!serviceAreaName.isNullOrEmpty() &&
    //       serviceAreaName.contains(",") &&
    //       serviceAreaName.length >= 2) {
    //     serviceAreaName =
    //         serviceAreaName.substring(0, serviceAreaName.length - 2);
    //   }
    //   addEditWareHouseController.servicesAreaController.text = serviceAreaName;
    //   addEditWareHouseController.update();
    //   addEditWareHouseController.branchList!.clear();
    //   addEditWareHouseController.selBranch=null;
    //   addEditWareHouseController.getBranchServiceArea(addEditWareHouseController.selectedServiceArea);
    //   // addEditWareHouseController.getPinCodeFromArea();
    // }
    addEditSubProblemDomainController.update();
  }
}
