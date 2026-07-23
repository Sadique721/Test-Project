
import 'package:savbill/pages/task_management/model/response/tat_task_list_res.dart';
import 'package:savbill/pages/task_management/tat_task/tat_mapping_detail/tat_mapping_details.dart';
import 'package:savbill/pages/task_management/tat_task/view_task/tat_task_item.dart';
import 'package:savbill/pages/task_management/tat_task/view_task/view_tat_task_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class ViewTatTask extends StatefulWidget {
  const ViewTatTask({super.key});

  @override
  State<ViewTatTask> createState() => _ViewTatTaskState();
}

class _ViewTatTaskState extends State<ViewTatTask> {
  final viewTatTaskController = Get.put(ViewTatTaskController());

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
      child: GetBuilder<ViewTatTaskController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewTatTaskController.isLoading),
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
                child: CustomText(
                    title: Strings.tat_for_task,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Flexible(
                        child: Card(
                          margin: const EdgeInsets.all(0),
                          elevation: 0.5,
                          child: Container(
                            height: 50,
                            padding: const EdgeInsets.symmetric(
                                horizontal:
                                Constant.SEARCH_BAR_CARD_PADDING - 2,
                                vertical: Constant.SEARCH_BAR_CARD_PADDING - 4),
                            child: CoustomTextField(
                                labelText: Strings.search_your_text_here,
                                textEditingController:
                                viewTatTaskController.searchController,
                                keyboardType: TextInputType.text,
                                borderEnableColors: AppTheme.colorPrimary,
                                textInputAction: TextInputAction.done,
                                onChanged: (value) {},
                                onTextValidator: (String? value) {
                                  return null;
                                },
                                prefixIcon: Icon(
                                  Icons.search,
                                  color: AppTheme.colorPrimary,
                                ),
                                borderCorner: Constant.BTN_ROUNDED_CORNER_M,
                                contentPadding: const EdgeInsets.symmetric(
                                    horizontal: Constant.LARGE_PADDING),
                                readOnly: false),
                          ),
                        ),
                      ),
                      const SizedBox(
                        width: Constant.SMALL_PADDING,
                      ),
                      Row(children: [
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewTatTaskController.applyFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.statusClosedGreen,
                                borderRadius:
                                const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.check,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                        const SizedBox(
                          width: Constant.SMALL_PADDING,
                        ),
                        Material(
                          color: AppTheme.colorWhite,
                          elevation: 2,
                          shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(6)),
                          child: InkWell(
                            onTap: () {
                              viewTatTaskController.clearFilter();
                            },
                            child: Container(
                              decoration: BoxDecoration(
                                color: AppTheme.colorRed,
                                borderRadius:
                                const BorderRadius.all(Radius.circular(6)),
                              ),
                              padding: const EdgeInsets.all(5),
                              child: Icon(
                                Icons.close,
                                color: AppTheme.colorWhite,
                                size: 22,
                              ),
                            ),
                          ),
                        ),
                      ]),
                    ]),
              ),
              const SizedBox(
                height: Constant.MEDIUM_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (viewTatTaskController.tatTaskList != null &&
                    viewTatTaskController.tatTaskList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: viewTatTaskController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount: viewTatTaskController.tatTaskList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            viewTatTaskController
                                .tatTaskList?.length) {
                          if (viewTatTaskController.isShowLoadMore) {
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
                          TatTaskListDetails item = viewTatTaskController
                              .tatTaskList![index];
                          return InkWell(
                            onTap: () {
                              openTatMappingDetail(item);
                            },
                            child: TatTaskItem(
                              index: index,
                              item: item,
                              onTapEdit: () {
                                viewTatTaskController
                                    .addEditTatTaskScreen(
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
                                          viewTatTaskController
                                              .deleteTatForTask(
                                              item, index);
                                        },
                                        negativeBtnClick: () {
                                          Get.back();
                                        });
                                  },
                                );
                              },
                            ),
                          );
                        }
                      }),
                )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                        onTap: () {
                          viewTatTaskController.addEditTatTaskScreen(
                              Strings.add, null);
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.create_tat,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w400,
                        ),
                      ))
                ],
              )
            ]),
      ),
    );
  }

  openTatMappingDetail(TatTaskListDetails? item) {
    Get.to(TatTaskMappingDetails(), arguments: {Constant.TAT_TASK_DETAIL: item});
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.task_master_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}

