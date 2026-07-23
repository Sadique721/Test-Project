import 'package:savbill/pages/task_management/model/response/view_task_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/link_task/link_task_controller.dart';
import 'package:savbill/pages/task_management/task_mgmt/link_task/link_task_view_item.dart';
import 'package:savbill/pages/ticket_system/ticket_management/view_ticket_item.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class LinkTask extends StatefulWidget {
  const LinkTask({super.key});

  @override
  State<LinkTask> createState() => _LinkTaskState();
}

class _LinkTaskState extends State<LinkTask> {
  final linkTaskController = Get.put(LinkTaskController());
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
      child: GetBuilder<LinkTaskController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: linkTaskController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: SizedBox(
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
                    title: Strings.task,
                    colors: AppTheme.colorBlack,
                    textAlign: TextAlign.start,
                    fontSize: AppTheme.medium + 1,
                    fontWeight: FontWeight.w500),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              Expanded(
                flex: 1,
                child: (linkTaskController.taskList != null &&
                    linkTaskController.taskList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: linkTaskController.controller,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      linkTaskController.taskList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            linkTaskController.taskList?.length) {
                          if (linkTaskController.isShowLoadMore) {
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
                          ViewTaskDataList item =
                          linkTaskController.taskList![index];
                          return LinkTaskViewItem(
                            controller: linkTaskController,
                            item: item,
                            showActionBtn: false,
                            forSelection: true,
                            userid:
                            linkTaskController.userDetail!.userId!,
                            onTapTicketDetail: () {},
                            onTapStaffDetail: () {},
                            onTapEdit: () {},
                            onTapAssignTicket: () {},
                            onTapApprove: () {},
                            onTapReject: () {},
                            onTapChangePriority: () {},
                            onTapPick: () {},
                            onTapFollowup: () {},
                            onTapLink: () {},
                            onTapUploadDoc: () {},
                            onTapChangeProblemDomain: () {},
                            onTapSelectItem: () {
                              for (ViewTaskDataList element
                              in linkTaskController.taskList!) {
                                if (element.caseId == item.caseId) {
                                  element.selected = true;
                                } else {
                                  element.selected = false;
                                }
                              }
                              linkTaskController.update();
                            },
                            onTapTicketChangeStatus: () {},
                            onTapETRTicket: () {},
                            onTapTicketRemark: () {},
                            onTapSLATimeCounter: () {},
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
                          ViewTaskDataList? selectedTask;
                          List<int> linkTicketIds =[];
                          for (ViewTaskDataList element
                          in linkTaskController.taskList!) {
                            if (element.selected != null &&
                                element.selected == true) {
                              selectedTask = element;
                              break;
                            }
                          }
                          if (selectedTask != null) {
                            linkTicketIds.add(selectedTask.caseId!);
                            linkTaskController.linkTicketApiCall(linkTicketIds);
                          } else {
                            Utils.showSnackbar(
                                Strings.ERROR,
                                "Please select the tickets!",
                                AppTheme.colorWhite,
                                AppTheme.colorRed);
                          }
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.link_ticket,
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

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        "${Strings.link_task} (${linkTaskController.castTitle})",
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}