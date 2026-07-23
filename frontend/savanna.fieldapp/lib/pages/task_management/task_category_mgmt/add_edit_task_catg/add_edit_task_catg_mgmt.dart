
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/response/task_cat_search_by_status_res.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/add_edit_task_catg/add_edit_task_catg_mgmt_controller.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/add_edit_task_catg/task_tat_map_item.dart';
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

class AddEditTaskCategoryMgmt extends StatefulWidget {
  @override
  _AddEditTaskCategoryMgmtState createState() =>
      _AddEditTaskCategoryMgmtState();
}

class _AddEditTaskCategoryMgmtState extends State<AddEditTaskCategoryMgmt>
    with WidgetsBindingObserver {
  final addEditTaskCategoryMgmtController =
  Get.put(AddEditTaskCategoryMgmtController());
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
      GetBuilder<AddEditTaskCategoryMgmtController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: addEditTaskCategoryMgmtController.isLoading),
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
                                        title: Strings.category_Name,
                                        require: true),
                                    const SizedBox(
                                      height: Constant.VERY_SMALL_PADDING,
                                    ),
                                    CoustomTextField(
                                        labelText: Strings.root_cause_name,
                                        textEditingController:
                                        addEditTaskCategoryMgmtController
                                            .categoryNameController,
                                        keyboardType: TextInputType.text,
                                        borderEnableColors: AppTheme.colorBlack,
                                        textInputAction: TextInputAction.next,
                                        hintColor: AppTheme.colorIconGrey,
                                        onTextValidator: (String? value) {
                                          if (value!.isEmpty) {
                                            return Strings
                                                .enter_task_category_mgmt_name;
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
                                        value: addEditTaskCategoryMgmtController
                                            .selectedStatus,
                                        items: addEditTaskCategoryMgmtController
                                            .statusList
                                            ?.map((DropdownDetail value) {
                                          return DropdownMenuItem<
                                              DropdownDetail>(
                                            value: value,
                                            child: Text(value.text!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditTaskCategoryMgmtController
                                              .selectedStatus =
                                          value as DropdownDetail?;
                                        },
                                        validator: (value) {
                                          if (value == null ||
                                              addEditTaskCategoryMgmtController
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
                                        title: Strings.tat_for_task,
                                        require: false),
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
                                            Strings.tat_for_task,
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
                                        value: addEditTaskCategoryMgmtController
                                            .selectTaskCatSearchByData,
                                        items: addEditTaskCategoryMgmtController
                                            .taskCatSearchByDataList!
                                            .map((TaskCatSearchByDataList value) {
                                          return DropdownMenuItem<
                                              TaskCatSearchByDataList>(
                                            value: value,
                                            child: Text(value.name!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          addEditTaskCategoryMgmtController
                                              .selectTaskCatSearchByData =
                                          value as TaskCatSearchByDataList?;
                                          addEditTaskCategoryMgmtController.selectTaskCatSearchByData!.tatForTicketID = value!.id;
                                        },
                                        validator: (value) {
                                          return null;
                                        },
                                      ),
                                    ),
                                    const SizedBox(
                                      height: Constant.SMALL_PADDING,
                                    ),
                                    Align(
                                      alignment: Alignment.centerRight,
                                      child: InkWell(
                                        onTap: addEditTaskCategoryMgmtController.taskCatSearchDataList!.length == 1
                                            ? null
                                            : () {
                                          if (addEditTaskCategoryMgmtController.taskCatSearchDataList == null) {
                                            Utils.showSnackbar(
                                              Strings.ERROR,
                                              Strings.enter_tat_mapping_detail,
                                              AppTheme.colorWhite,
                                              AppTheme.colorRed,
                                            );
                                          } else {
                                            addEditTaskCategoryMgmtController.addTicketMapping();
                                            addEditTaskCategoryMgmtController.orderId = 1;
                                            addEditTaskCategoryMgmtController.update();
                                          }
                                        },
                                        child: CustomText(
                                          title: Strings.plus_add,
                                          colors: addEditTaskCategoryMgmtController.taskCatSearchDataList!.length == 1
                                              ? AppTheme.colorGrey // Disabled color
                                              : AppTheme.colorPrimary, // Enabled color
                                          textAlign: TextAlign.start,
                                          fontSize: AppTheme.medium,
                                          fontWeight: FontWeight.w600,
                                        ),
                                      ),

                                    ),
                                    const SizedBox(
                                        height: Constant.SMALL_PADDING),
                                    (addEditTaskCategoryMgmtController.taskCatSearchDataList !=
                                        null &&
                                        addEditTaskCategoryMgmtController
                                            .taskCatSearchDataList!
                                            .isNotEmpty)
                                        ? ListView.builder(
                                        physics:
                                        const NeverScrollableScrollPhysics(),
                                        shrinkWrap: true,
                                        itemCount:
                                        addEditTaskCategoryMgmtController
                                            .taskCatSearchDataList!
                                            .length,
                                        itemBuilder: (BuildContext context,
                                            int index) {
                                          TaskCatSearchByDataList
                                          item =
                                          addEditTaskCategoryMgmtController
                                              .taskCatSearchDataList![
                                          index];
                                          return Container(
                                            margin: EdgeInsets.only(
                                                top: index == 0
                                                    ? 0
                                                    : Constant
                                                    .VERY_SMALL_PADDING),
                                            child: TaskTatMapItem(
                                                item: item,
                                                onTapDelete: () {
                                                  addEditTaskCategoryMgmtController
                                                      .taskCatSearchDataList!
                                                      .removeAt(index);
                                                  addEditTaskCategoryMgmtController
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
                      title: addEditTaskCategoryMgmtController.from.equalsIgnoreCase(Strings.edit)?Strings.update_category : Strings.submit,
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
      if(addEditTaskCategoryMgmtController.taskCatSearchDataList!.isNotEmpty) {
        addEditTaskCategoryMgmtController.addEditCategoryMgmtApiCall();
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
        addEditTaskCategoryMgmtController.from.equalsIgnoreCase(Strings.edit)
            ? Strings.edit_task_category
            : Strings.create_category,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
