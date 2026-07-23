import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/task_category_item.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/task_catg_detail_screen/task_category_detail.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/view_task_catg_mgmt_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

import '../../../widgets/simple_button.dart';

class ViewTaskCategoryManagement extends StatefulWidget {
  const ViewTaskCategoryManagement({super.key});

  @override
  State<ViewTaskCategoryManagement> createState() =>
      _ViewCategoryManagementState();
}

class _ViewCategoryManagementState extends State<ViewTaskCategoryManagement> {
  final viewTaskCategoryManagementController =
  Get.put(ViewTaskCategoryManagementController());

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
      child: GetBuilder<ViewTaskCategoryManagementController>(
          builder: (controller) {
            return Stack(children: [
              Scaffold(
                backgroundColor: AppTheme.colorBG,
                appBar: _appBar(),
                body: _body(),
              ),
              ProgressBar(isLoader: viewTaskCategoryManagementController.isLoading),
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
                        title: Strings.category,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    InkWell(
                      onTap: () {
                        if (viewTaskCategoryManagementController
                            .filterViewOpen) {
                          viewTaskCategoryManagementController.filterViewOpen =
                          false;
                        } else {
                          viewTaskCategoryManagementController.filterViewOpen =
                          true;
                        }
                        viewTaskCategoryManagementController.update();
                      },
                      child: Container(
                          height: 38,
                          margin: const EdgeInsets.only(right: 0), //
                          child: Icon(
                            Icons.filter_alt_rounded,
                            color: viewTaskCategoryManagementController
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
            viewTaskCategoryManagementController.filterViewOpen
                ? Container(
              width: MediaQuery.of(context).size.width,
              margin: const EdgeInsets.symmetric(
                  horizontal: Constant.SCREEN_PADDING),
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
                          labelText: Strings.search_your_text_here,
                          hintColor: AppTheme.colorIconGrey,
                          textEditingController:
                          viewTaskCategoryManagementController
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
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Expanded(
                              child: SimpleButton(
                                onTap: () {
                                  viewTaskCategoryManagementController
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
                                  viewTaskCategoryManagementController
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
                child: (viewTaskCategoryManagementController.taskCategoryList !=
                    null &&
                    viewTaskCategoryManagementController
                        .taskCategoryList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller:
                      viewTaskCategoryManagementController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount: viewTaskCategoryManagementController
                          .taskCategoryList!.length +
                          1,
                      itemBuilder: (context, index) {
                        if (index ==
                            viewTaskCategoryManagementController
                                .taskCategoryList?.length) {
                          if (viewTaskCategoryManagementController
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
                          TaskCategoryMgmtDataList item =
                          viewTaskCategoryManagementController
                              .taskCategoryList![index];
                          return TaskCategoryItem(
                            index: index,
                            item: item,
                            onTapDetails: (){
                              Get.to(TaskCategoryDetailScreen(),
                                  arguments: {Constant.TCM_DETAIL: item});
                            },
                            onTapEdit: () {
                              viewTaskCategoryManagementController
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
                                        viewTaskCategoryManagementController
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
                        viewTaskCategoryManagementController.addEditTaskCategoryScreen(
                            Strings.add, null);
                      },
                      radius: 0,
                      height: Constant.BOTTOM_BTN_HEIGHT,
                      bgColors: AppTheme.colorPrimary,
                      borderColors: AppTheme.colorPrimary,
                      child: CustomText(
                        title: Strings.create_task_category_management,
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
    return DynamicAppBar(Strings.category_management, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
