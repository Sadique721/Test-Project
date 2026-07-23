import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/add_edit_sub_category/add_edit_task_sub_category_mgmt_controller.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/add_edit_sub_category/select_parent_sub_categroy_dialog.dart';
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

class AddEditTaskSubCategoryMgmt extends StatefulWidget {
  @override
  _AddEditTaskSubCategoryMgmtState createState() =>
      _AddEditTaskSubCategoryMgmtState();
}

class _AddEditTaskSubCategoryMgmtState extends State<AddEditTaskSubCategoryMgmt>
    with WidgetsBindingObserver implements SelectParentCategoryAction {
  final addEditTaskSubCatController = Get.put(AddEditTaskSubCategoryMgmtController());
  final addEditTaskCategoryMgmtFormKey = GlobalKey<FormState>();
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
      GetBuilder<AddEditTaskSubCategoryMgmtController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditTaskSubCatController.isLoading),
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
                    key: addEditTaskCategoryMgmtFormKey,
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
                                        title: Strings.sub_category,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_sub_category_name,
                                        textEditingController:
                                        addEditTaskSubCatController
                                            .categoryNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .pls_enter_sub_category_name;
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
                                        addEditTaskSubCatController
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
                                        value: addEditTaskSubCatController
                                            .selectedStatus,
                                        items: addEditTaskSubCatController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditTaskSubCatController
                                              .selectedStatus =
                                          value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditTaskSubCatController
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
                                        title: Strings.description,
                                        require: false),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.enter_description,
                                        textEditingController:
                                        addEditTaskSubCatController
                                            .descriptionController,
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
                                      height: Constant.LARGE_PADDING,
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
                      title: addEditTaskSubCatController.from.equalsIgnoreCase(Strings.edit)?Strings.update_category : Strings.submit,
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


  validateForm() {
    if (addEditTaskCategoryMgmtFormKey.currentState!.validate()) {
      addEditTaskSubCatController.addEditSubCategoryMgmtApiCall();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
  }


  showParentCategorySelectionDialog(String from) {
    List<TaskCategoryMgmtDataList> item = [];
    if (from.equalsIgnoreCase(Strings.parent_category)) {
      if (addEditTaskSubCatController.allActiveReasonCategoryList != null &&
          addEditTaskSubCatController.allActiveReasonCategoryList!.isNotEmpty) {
        for (var element
        in addEditTaskSubCatController.allActiveReasonCategoryList!) {
          element.selected = false;
        }
        if (addEditTaskSubCatController
            .selectedParentCategoryIds.isNotEmpty) {
          for (var element
          in addEditTaskSubCatController.allActiveReasonCategoryList!) {
            for (int selElement in addEditTaskSubCatController
                .selectedParentCategoryIds) {
              if (selElement == element.categoryId!) {
                element.selected = true;
              }
            }
          }
        }
        item.addAll(addEditTaskSubCatController.allActiveReasonCategoryList!);
      }
    }

    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return SelectParentSubCategoryDialog(
              serviceAreaSelectionAction: this,
              fromFor: from,
              parentCategoryList: item);
        });
  }


  @override
  void selectParentCategoryBtnAction(
      {String? identifier, List<TaskCategoryMgmtDataList>? selectedItem}) {
    Get.back();
    if (identifier.toString().equalsIgnoreCase(Strings.parent_category) &&
        selectedItem != null &&
        selectedItem.isNotEmpty) {
      String serviceAreaName = "";
      addEditTaskSubCatController.selectedParentCategoryIds.clear();
      addEditTaskSubCatController.selectedSubCategoryResMappingList!.clear();
      for (TaskCategoryMgmtDataList element in selectedItem) {
        addEditTaskSubCatController.selectedParentCategoryIds
            .add(element.categoryId!);
        addEditTaskSubCatController.selectedSubCategoryResMappingList!
            .add(CaseCategoryTatMappingList(
          caseCategoryId: element.categoryId,
        ));
        serviceAreaName = "$serviceAreaName${element.categoryName!}, ";
      }
      if (!serviceAreaName.isNullOrEmpty() &&
          serviceAreaName.contains(",") &&
          serviceAreaName.length >= 2) {
        serviceAreaName =
            serviceAreaName.substring(0, serviceAreaName.length - 2);
      }

      addEditTaskSubCatController.parentCategoryController.text = serviceAreaName;
    }
    addEditTaskSubCatController.update();
  }

  _appBar() {
    return DynamicAppBar(
        addEditTaskSubCatController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.update_sub_category
            : Strings.create_sub_category,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}