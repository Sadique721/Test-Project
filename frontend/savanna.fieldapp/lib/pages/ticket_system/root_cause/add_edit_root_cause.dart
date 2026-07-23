import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';
import 'package:savbill/pages/ticket_system/root_cause/add_edit_root_cause_controller.dart';
import 'package:savbill/pages/ticket_system/root_cause/root_cause_mapping_item.dart';
import 'package:savbill/pages/ticket_system/root_cause/sub_problem_root_cause_item.dart';
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
import '../model/response/root_cause_sub_problem_res.dart';

class AddEditRootCause extends StatefulWidget {
  @override
  _AddEditRootCauseState createState() => _AddEditRootCauseState();
}

class _AddEditRootCauseState extends State<AddEditRootCause>
    with WidgetsBindingObserver {
  final addEditRootCauseController = Get.put(AddEditRootCauseController());
  final addEditRootCauseFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditRootCauseController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditRootCauseController.isLoading),
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
                      key: addEditRootCauseFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          /*___________________ Root Cause Domain __________________________*/

                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: Strings.root_cause_name, require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.root_cause_name,
                              textEditingController: addEditRootCauseController
                                  .rootCauseNameController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_root_cause_name;
                                }
                                return null;
                              },
                              borderCorner: Constant.INPUT_ROUNDED_CORNER,
                              contentPadding: const EdgeInsets.symmetric(
                                  horizontal: Constant.LARGE_PADDING),
                              readOnly: false),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),

                          /*___________________ Status __________________________*/

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
                              value: addEditRootCauseController.selectedStatus,
                              items: addEditRootCauseController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditRootCauseController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditRootCauseController.selectedStatus ==
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

                          /*___________________ sub_problem_domain __________________________*/

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
                                        title: Strings.sub_problem_domain,
                                        require: true),
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
                                            Strings.select_sub_problem_domain,
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
                                        value: addEditRootCauseController
                                            .selectedRootSubProblem,
                                        items: addEditRootCauseController
                                            .rootCauseList
                                            ?.map((RootCauseSubProblemDataList
                                                value) {
                                          return DropdownMenuItem<
                                              RootCauseSubProblemDataList>(
                                            value: value,
                                            child: Text(value.subCategoryName
                                                .toString()),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditRootCauseController
                                                  .selectedRootSubProblem =
                                              value
                                                  as RootCauseSubProblemDataList?;
                                          addEditRootCauseController.update();
                                        },
                                        validator: (value) {
                                          if (addEditRootCauseController
                                                      .rootCauseSubProblemDomain ==
                                                  null &&
                                              addEditRootCauseController
                                                  .rootCauseSubProblemDomain!
                                                  .isEmpty) {
                                            return Strings
                                                .please_select_sub_problem_domain;
                                          }
                                          // return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            if (addEditRootCauseController
                                                    .selectedRootSubProblem ==
                                                null) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  "Please select the sub problem domain.",
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                              return;
                                            }
                                            addEditRootCauseController
                                                .rootCauseSubProblemDomain!
                                                .add(ResoSubCategoryMappingList(
                                                    // id: addEditRootCauseController
                                                    //     .selectedRootSubProblem!.id!,
                                                    resId: addEditRootCauseController
                                                                .rootCauseDetail !=
                                                            null
                                                        ? addEditRootCauseController
                                                            .rootCauseDetail!.id
                                                        : null,
                                                    subcateId:
                                                        addEditRootCauseController
                                                            .selectedRootSubProblem!
                                                            .id,
                                                    subCateName:
                                                        addEditRootCauseController
                                                            .selectedRootSubProblem!
                                                            .subCategoryName));

                                            addEditRootCauseController
                                                .selectedRootSubProblem = null;
                                            addEditRootCauseController.update();
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
                                        height: Constant.MEDIUM_PADDING),
                                    (addEditRootCauseController.rootCauseSubProblemDomain !=
                                                null &&
                                            addEditRootCauseController
                                                .rootCauseSubProblemDomain!
                                                .isNotEmpty)
                                        ? ListView.builder(
                                            physics:
                                                const NeverScrollableScrollPhysics(),
                                            shrinkWrap: true,
                                            itemCount:
                                                addEditRootCauseController
                                                    .rootCauseSubProblemDomain!
                                                    .length,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              ResoSubCategoryMappingList item =
                                                  addEditRootCauseController
                                                          .rootCauseSubProblemDomain![
                                                      index];
                                              return Container(
                                                margin: EdgeInsets.only(
                                                    top: index == 0
                                                        ? 0
                                                        : Constant
                                                            .VERY_SMALL_PADDING),
                                                child: SubProblemRootCauseItem(
                                                  item: item,
                                                  isShowDelete: true,
                                                  onTapDelete: () {
                                                    addEditRootCauseController
                                                        .rootCauseSubProblemDomain!
                                                        .removeAt(index);
                                                    addEditRootCauseController
                                                        .update();
                                                  },
                                                ),
                                              );
                                            })
                                        : Container(),
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
                                    title: Strings.sub_problem_domain,
                                    colors: AppTheme.title_dark,
                                    textAlign: TextAlign.start,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ],
                          ),

                          /*___________________ resolution __________________________*/

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
                                        title: Strings.resolution,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.resolution,
                                        textEditingController:
                                            addEditRootCauseController
                                                .resolutionController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.done,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (addEditRootCauseController
                                                      .rootCauseResolutionMappings ==
                                                  null ||
                                              addEditRootCauseController
                                                  .rootCauseResolutionMappings!
                                                  .isEmpty) {
                                            return Strings
                                                .enter_or_add_resolution;
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
                                    Align(
                                        alignment: Alignment.centerRight,
                                        child: InkWell(
                                          onTap: () {
                                            if (addEditRootCauseController
                                                .resolutionController
                                                .text
                                                .isEmpty) {
                                              Utils.showSnackbar(
                                                  Strings.ERROR,
                                                  "Please enter the resolution.",
                                                  AppTheme.colorWhite,
                                                  AppTheme.colorRed);
                                              return;
                                            }
                                            addEditRootCauseController
                                                .rootCauseResolutionMappings!
                                                .add(RootCauseResolutionMapping(
                                                    rootCauseReason:
                                                        addEditRootCauseController
                                                            .resolutionController
                                                            .text));
                                            addEditRootCauseController
                                                .resolutionController
                                                .clear();
                                            addEditRootCauseController.update();
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
                                        height: Constant.MEDIUM_PADDING),
                                    (addEditRootCauseController
                                                    .rootCauseResolutionMappings !=
                                                null &&
                                            addEditRootCauseController
                                                .rootCauseResolutionMappings!
                                                .isNotEmpty)
                                        ? ListView.builder(
                                            physics:
                                                const NeverScrollableScrollPhysics(),
                                            shrinkWrap: true,
                                            itemCount: addEditRootCauseController
                                                .rootCauseResolutionMappings!
                                                .length,
                                            itemBuilder: (BuildContext context,
                                                int index) {
                                              RootCauseResolutionMapping item =
                                                  addEditRootCauseController
                                                          .rootCauseResolutionMappings![
                                                      index];
                                              return Container(
                                                margin: EdgeInsets.only(
                                                    top: index == 0
                                                        ? 0
                                                        : Constant
                                                            .VERY_SMALL_PADDING),
                                                child: RootCauseMapItem(
                                                    item: item,
                                                    isShowDelete: true,
                                                    onTapDelete: () {
                                                      addEditRootCauseController
                                                          .rootCauseResolutionMappings!
                                                          .removeAt(index);
                                                      addEditRootCauseController
                                                          .update();
                                                    }),
                                              );
                                            })
                                        : Container(),
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
                                    title: Strings.resolution,
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
    if (addEditRootCauseFormKey.currentState!.validate()) {
      if (addEditRootCauseController.rootCauseSubProblemDomain!.isEmpty) {
        Utils.showSnackbar(Strings.ERROR, Strings.please_add_sub_problem_domain,
            AppTheme.colorWhite, AppTheme.colorRed);
      } else {
        addEditRootCauseController.addEditRootCauseApiCall();
      }
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditRootCauseController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_root_cause
            : Strings.create_root_cause,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
