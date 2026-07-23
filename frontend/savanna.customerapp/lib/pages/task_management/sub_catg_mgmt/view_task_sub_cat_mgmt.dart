import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/model/response/task_sub_category_mgmt_res.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/task_sub_category_item.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/task_sub_catg_detail_screen/task_sub_category_detail.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/view_task_sub_cat_mgmt_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';
import '../../../widgets/simple_button.dart';

class ViewTaskSubCategoryManagement extends StatefulWidget {
  const ViewTaskSubCategoryManagement({super.key});

  @override
  State<ViewTaskSubCategoryManagement> createState() =>
      _ViewCategoryManagementState();
}

class _ViewCategoryManagementState extends State<ViewTaskSubCategoryManagement> {
  final subCategoryManagementController =
  Get.put(ViewTaskSubCategoryManagementController());

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
      child: GetBuilder<ViewTaskSubCategoryManagementController>(
          builder: (controller) {
            return Stack(children: [
              Scaffold(
                backgroundColor: AppTheme.colorBG,
                appBar: _appBar(),
                body: _body(),
              ),
              ProgressBar(isLoader: subCategoryManagementController.isLoading),
            ]);
          }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(
              height: Constant.SCREEN_PADDING,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SCREEN_PADDING),
              child: Row(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    CustomText(
                        title: Strings.sub_category_management,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    InkWell(
                      onTap: () {
                        if (subCategoryManagementController
                            .filterViewOpen) {
                          subCategoryManagementController.filterViewOpen =
                          false;
                        } else {
                          subCategoryManagementController.filterViewOpen =
                          true;
                        }
                        subCategoryManagementController.update();
                      },
                      child: Container(
                          height: 38,
                          margin: const EdgeInsets.only(right: 0), //
                          child: Icon(
                            Icons.filter_alt_rounded,
                            color: subCategoryManagementController
                                .isFilterApply
                                ? AppTheme.colorPrimary
                                : AppTheme.colorBlack,
                            size: 32,
                          )),
                    ),
                  ]),
            ),
            const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            subCategoryManagementController.filterViewOpen
                ? Container(
              width: MediaQuery.of(context).size.width,
              margin: const EdgeInsets.symmetric(
                  horizontal: Constant.SCREEN_PADDING,vertical: Constant.SMALL_PADDING),
              child: Material(
                color: AppTheme.colorWhite,
                elevation: 1.5,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(
                        Constant.BTN_ROUNDED_CORNER - 2)),
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                      CoustomTextField(
                          labelText: Strings.enter_sub_category_name,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                          subCategoryManagementController
                              .searchController,
                          borderEnableColors: AppTheme.colorBlack,
                          borderFocusColors: AppTheme.colorBlack,
                          textColor: AppTheme.colorBlack,
                          fontSize: AppTheme.small,
                          fontWeight: FontWeight.w500,
                          contentPadding: const EdgeInsets.symmetric(
                              horizontal: Constant.MEDIUM_PADDING),
                          borderCorner: Constant.BTN_ROUNDED_CORNER,
                          keyboardType: TextInputType.text,
                          maxLines: 1,
                          onTextValidator: (String? value) {},
                          onTextFiledOnTap: () {},
                          readOnly: false),
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
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
                              Strings.please_select_parent_category,
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
                          value: subCategoryManagementController
                              .selectedActiveReasonCategory,
                          items: subCategoryManagementController
                              .allActiveReasonCategoryList!
                              .map((TaskCategoryMgmtDataList value) {
                            return DropdownMenuItem<
                                TaskCategoryMgmtDataList>(
                              value: value,
                              child: Text(value.categoryName!),
                            );
                          }).toList(),
                          onChanged: (value) {
                            subCategoryManagementController
                                .selectedActiveReasonCategory =
                            value as TaskCategoryMgmtDataList?;
                          },
                          validator: (value) {
                            return null;
                          },
                        ),
                      ),
                      const SizedBox(
                        height: Constant.SMALL_PADDING,
                      ),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Expanded(
                              child: SimpleButton(
                                onTap: () {
                                  subCategoryManagementController
                                      .applyFilter();
                                },
                                radius: Constant.BTN_HEIGHT_M,
                                height: Constant.BTN_HEIGHT_M,
                                bgColors: AppTheme.colorPrimary,
                                child: CustomText(
                                  title: Strings.apply,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                            const SizedBox(
                              width: Constant.LARGE_PADDING,
                            ),
                            Expanded(
                              child: SimpleButton(
                                onTap: () {
                                  subCategoryManagementController
                                      .clearFilter();
                                },
                                radius: Constant.BTN_HEIGHT_M,
                                height: Constant.BTN_HEIGHT_M,
                                bgColors: AppTheme.colorBlack,
                                borderColors: AppTheme.colorBlack,
                                child: CustomText(
                                  title: Strings.clear,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w500,
                                ),
                              ),
                            ),
                          ]),
                    ],
                  ),
                ),
              ),
            )
                : Container(),
            Expanded(
                flex: 1,
                child: (subCategoryManagementController.taskSubCategoryList !=
                    null &&
                    subCategoryManagementController
                        .taskSubCategoryList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller:
                      subCategoryManagementController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount: subCategoryManagementController
                          .taskSubCategoryList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            subCategoryManagementController
                                .taskSubCategoryList?.length) {
                          if (subCategoryManagementController
                              .isShowLoadMore) {
                            return Padding(
                              padding: const EdgeInsets.all(
                                  Constant.SMALL_PADDING),
                              child: Center(
                                child: SizedBox(
                                  width: Constant.SCREEN_PADDING,
                                  height: Constant.SCREEN_PADDING,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.5,
                                    valueColor:
                                    AlwaysStoppedAnimation<Color>(
                                        AppTheme.colorProgress),
                                    backgroundColor:
                                    AppTheme.colorProgressBg,
                                  ),
                                ),
                              ),
                            );
                          } else {
                            return Container();
                          }
                        } else {
                          TaskSubCategoryDataList item =
                          subCategoryManagementController
                              .taskSubCategoryList![index];
                          return TaskSubCategoryItem(
                            index: index,
                            item: item,
                            onTapDetail:(){
                              Get.to(TaskSubCategoryDetailScreen(),
                                  arguments: {Constant.TSCM_DETAIL: item});
                            },
                            onTapEdit: () {
                              subCategoryManagementController
                                  .addEditTaskCategoryScreen(
                                  Strings.edit, item);
                            },
                            onTapDelete: () {
                              showDialog(
                                context: context,
                                builder: (BuildContext context) {
                                  return AlertDialogHelper(
                                      title: Strings.app_name,
                                      message: Strings.msg_delete,
                                      positiveBtnText: Strings.ok,
                                      negativeBtnText: Strings.cancel,
                                      positiveBtnClick: () {
                                        Get.back();
                                        subCategoryManagementController
                                            .deleteTaskCategory(
                                            item, index);
                                      },
                                      negativeBtnClick: () {
                                        Get.back();
                                      });
                                },
                              );
                            },
                          );
                        }
                      }),
                )
                    : noDataFound()),
            Row(
              children: [
                Expanded(
                    child: SimpleButton(
                      onTap: () {
                        subCategoryManagementController.addEditTaskCategoryScreen(
                            Strings.add, null);
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.create_sub_category,
                        fontSize: AppTheme.medium,
                        fontWeight: FontWeight.w400,
                      ),
                    ))
              ],
            )
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.sub_category_managment, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
