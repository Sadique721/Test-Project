import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/problem_domain/add_edit_problem_domain_controller.dart';
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

class AddEditProblemDomain extends StatefulWidget {
  @override
  _AddEditProblemDomainState createState() => _AddEditProblemDomainState();
}

class _AddEditProblemDomainState extends State<AddEditProblemDomain>
    with WidgetsBindingObserver {
  final addEditProblemDomainController =
      Get.put(AddEditProblemDomainController());
  final addEditProblemDomainFormKey = GlobalKey<FormState>();
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
      child: GetBuilder<AddEditProblemDomainController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditProblemDomainController.isLoading),
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
                      key: addEditProblemDomainFormKey,
                      autovalidateMode: autoValidateMode,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          const SizedBox(height: Constant.SCREEN_PADDING),
                          InputTitleRequire(
                              title: "${Strings.ticket} ${Strings.problem_domain}", require: true),
                          const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          ),
                          CoustomTextField(
                              labelText: Strings.problem_domain_name,
                              textEditingController:
                                  addEditProblemDomainController
                                      .problemDomainNameController,
                              keyboardType: TextInputType.text,
                              borderEnableColors: AppTheme.colorBlack,
                              textInputAction: TextInputAction.next,
                              hintColor: AppTheme.colorIconGrey,
                              onTextValidator: (String? value) {
                                if (value!.isEmpty) {
                                  return Strings.enter_problem_domain_name;
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
                          InputTitleRequire(
                              title: Strings.service, require: true),
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
                                  Strings.service,
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
                                  addEditProblemDomainController.selPlanService,
                              items: addEditProblemDomainController
                                  .planServiceList
                                  ?.map((PlanServiceDetail value) {
                                return DropdownMenuItem<PlanServiceDetail>(
                                  value: value,
                                  child: Text(value.name!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditProblemDomainController.selPlanService =
                                    value as PlanServiceDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditProblemDomainController
                                            .selPlanService ==
                                        null) {
                                  return Strings.select_service;
                                }
                                return null;
                              },
                            ),
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          InputTitleRequire(
                              title: Strings.department, require: false),
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
                                  Strings.department,
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
                              value: addEditProblemDomainController
                                  .selectedDepartment,
                              items: addEditProblemDomainController
                                  .departmentTypeList
                                  ?.map((DepartmentType value) {
                                return DropdownMenuItem<DepartmentType>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditProblemDomainController
                                        .selectedDepartment =
                                    value as DepartmentType?;
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
                              value:
                                  addEditProblemDomainController.selectedStatus,
                              items: addEditProblemDomainController.statusList
                                  ?.map((DropdownDetail value) {
                                return DropdownMenuItem<DropdownDetail>(
                                  value: value,
                                  child: Text(value.text!),
                                );
                              }).toList(),
                              onChanged: (value) {
                                addEditProblemDomainController.selectedStatus =
                                    value as DropdownDetail?;
                              },
                              validator: (value) {
                                if (value == null ||
                                    addEditProblemDomainController
                                            .selectedStatus ==
                                        null) {
                                  return Strings.please_select_status;
                                }
                                return null;
                              },
                            ),
                          ),


                          const SizedBox(
                            height: Constant.EXTRA_LARGE_PADDING,
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
    if (addEditProblemDomainFormKey.currentState!.validate()) {
      addEditProblemDomainController.addEditProblemDomainApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }

  _appBar() {
    return DynamicAppBar(
        addEditProblemDomainController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_ticket_problem_domain
            : Strings.create_ticket_problem_domain,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
