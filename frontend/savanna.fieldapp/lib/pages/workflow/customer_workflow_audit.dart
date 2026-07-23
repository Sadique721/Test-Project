import 'package:savbill/pages/customer_inventory/request/team_hierarchy_approval_flow_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/pages/workflow/cust_workflow_audit_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

import '../../webservices/url_constants.dart';

class CustomerWorkFlowAudit extends StatefulWidget {
  @override
  _CustomerWorkFlowAuditState createState() => _CustomerWorkFlowAuditState();
}

class _CustomerWorkFlowAuditState extends State<CustomerWorkFlowAudit> {
  final custWorkflowController = Get.put(WorkFlowAuditController());

  final shiftLocationFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<WorkFlowAuditController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: custWorkflowController.isLoading),
      ]);
    });
  }

  _body() {
    int activeStep = 2;
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
        child: SingleChildScrollView(
          child: Container(
            color: AppTheme.colorBG,
            width: MediaQuery.of(context).size.width,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                (custWorkflowController.teamHierarchyDataList != null &&
                        custWorkflowController
                            .teamHierarchyDataList!.isNotEmpty)
                    ? Container(
                        height: 150,
                        padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                        child: ListView.builder(
                            shrinkWrap: true,
                            controller: custWorkflowController.controller,
                            scrollDirection: Axis.horizontal,
                            itemCount: custWorkflowController
                                    .teamHierarchyDataList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  custWorkflowController
                                      .teamHierarchyDataList?.length) {
                                if (custWorkflowController.isShowLoadMore) {
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
                                TeamHierarchyDataList item =
                                    custWorkflowController
                                        .teamHierarchyDataList![index];
                                return teamItemList(item, index);
                              }
                            }))
                    : const SizedBox.shrink(),
                const SizedBox(
                  height: Constant.SMALL_PADDING,
                ),
                Align(
                  alignment: Alignment.centerLeft,
                  child: CustomText(
                      title: Strings.workflow_audit,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w600),
                ),
                Container(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: (custWorkflowController.workFlowAuditDataList !=
                              null &&
                          custWorkflowController
                              .workFlowAuditDataList!.isNotEmpty)
                      ? ListView.builder(
                          scrollDirection: Axis.vertical,
                          shrinkWrap: true,
                          controller: custWorkflowController.controller,
                          itemCount: (custWorkflowController
                                      .workFlowAuditDataList?.length ??
                                  0) +
                              1,
                          itemBuilder: (context, index) {
                            final list =
                                custWorkflowController.workFlowAuditDataList ??
                                    [];
                            if (index == list.length) {
                              if (custWorkflowController.isShowLoadMore) {
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
                                return const SizedBox.shrink();
                              }
                            } else {
                              return workflowAuditItem(item: list[index]);
                            }
                          },
                        )
                      : SizedBox(
                          child: noDataFound(),
                          height: MediaQuery.of(context).size.height * 0.7,
                        ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        custWorkflowController.statusName!,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  teamItemList(TeamHierarchyDataList item, int index) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            item.status!.equalsIgnoreCase(Strings.approved)
                ? Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                        borderRadius:
                            const BorderRadius.all(Radius.circular(50)),
                        color: AppTheme.colorGreen),
                    child: Icon(
                      Icons.check_circle,
                      color: AppTheme.colorWhite,
                    ),
                  )
                : Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                        borderRadius:
                            const BorderRadius.all(Radius.circular(50)),
                        color: AppTheme.colorRed),
                    child: Icon(
                      Icons.cancel_rounded,
                      color: AppTheme.colorWhite,
                    ),
                  ),
            custWorkflowController.teamHierarchyDataList!.length - 1 == index
                ? const SizedBox.shrink()
                : item.status!.equalsIgnoreCase(Strings.approved)
                    ? Container(
                        height: 3,
                        width: 130,
                        color: AppTheme.colorGreen,
                      )
                    : Container(
                        height: 3,
                        width: 130,
                        color: AppTheme.colorGrey,
                      ),
          ],
        ),
        const SizedBox(
          height: Constant.SMALL_PADDING,
        ),
        Container(
          width: 90,
          child: CustomText(
              title: item.teamName,
              colors: AppTheme.colorBlack,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small - 1,
              fontWeight: FontWeight.w400),
        )
      ],
    );
  }

  workflowAuditItem({required WorkFlowAuditDataList item}) {
    String actionDate = "";
    if (item.actionDateTime != null && item.actionDateTime!.isNotEmpty) {
      DateTime date =
          DateFormat(Constant.DATE_TIME_FORMAT_API).parse(item.actionDateTime!);
      actionDate =
          DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}")
              .format(date);
    }
    return Container(
      margin: const EdgeInsets.only(
        bottom: Constant.MEDIUM_PADDING,
      ),
      child: Material(
        color: AppTheme.colorWhite,
        elevation: 0.5,
        shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(Constant.BTN_ROUNDED_CORNER)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                    child: CustomText(
                        title: item.entityName != null &&
                                item.entityName!.isNotEmpty
                            ? item.entityName
                            : "",
                        colors: AppTheme.title_dark,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.small,
                        maxLines: 2,
                        height: 1,
                        fontWeight: FontWeight.w500)),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.end,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    item.action != null && item.action!.isNotEmpty
                        ? CustomText(
                            title: item.action,
                            colors: AppTheme.colorPrimary,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.small,
                            maxLines: 1,
                            height: 1,
                            fontWeight: FontWeight.w500)
                        : Container(),
                    item.action != null && item.action!.isNotEmpty
                        ? const SizedBox(
                            height: Constant.VERY_SMALL_PADDING,
                          )
                        : Container(),
                    actionDate.isNotEmpty
                        ? CustomText(
                            title: actionDate,
                            colors: AppTheme.lable_noramal,
                            textAlign: TextAlign.start,
                            fontSize: AppTheme.verySmall,
                            maxLines: 1,
                            height: 1,
                            fontWeight: FontWeight.w500)
                        : Container(),
                  ],
                )
              ],
            ),
          ),
          Divider(
            color: AppTheme.title_dark,
            thickness: 0.5,
            height: Constant.MEDIUM_PADDING,
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: Constant.SMALL_PADDING),
            child: basicDetailItem(
                Strings.staff_name,
                (item.actionByName != null && item.actionByName!.isNotEmpty)
                    ? item.actionByName
                    : "-",
                Strings.remarks,
                (item.remark != null && item.remark!.isNotEmpty)
                    ? item.remark
                    : "-"),
          ),
          const SizedBox(
            height: Constant.VERY_SMALL_PADDING,
          ),
          item.action != null &&
                  item.action!.isNotEmpty &&
                  item.action! == "Rejected" &&
                  item.fileList != null &&
                  item.fileList!.isNotEmpty
              ? Padding(
                  padding: const EdgeInsets.only(
                      left: Constant.SMALL_PADDING,
                      right: Constant.SMALL_PADDING,
                      bottom: Constant.SMALL_PADDING),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      CustomText(
                          title: "Attachments",
                          colors: AppTheme.lable_noramal,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.verySmall,
                          maxLines: 1,
                          height: 1,
                          fontWeight: FontWeight.w500),
                      ListView.builder(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: item.fileList!.length,
                        itemBuilder: (context, index) {
                          final file = item.fileList![index];
                          return Padding(
                            padding: const EdgeInsets.symmetric(
                                vertical: Constant.VERY_SMALL_PADDING),
                            child: GestureDetector(
                              child: CustomText(
                                title: file.fileName ?? "",
                                colors: AppTheme.colorPrimary,
                                textAlign: TextAlign.start,
                                fontSize: AppTheme.small + 1,
                                maxLines: 2,
                                height: 1,
                                fontWeight: FontWeight.normal,
                              ),
                              onTap: () {
                                if (file.uniqueName != null &&
                                    file.customerCafImageMappingId != null) {
                                  custWorkflowController.downloadFile(
                                      "${UrlConstants.cust_reject_download_doc}${file.customerCafImageMappingId}/${file.uniqueName}/",
                                      file);
                                }
                              },
                            ),
                          );
                        },
                      )
                    ],
                  ),
                )
              : Container(),
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        Expanded(
          flex: 3,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title1),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value1),
            ],
          ),
        ),
        Expanded(
          flex: 2,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              titleWidget(title2),
              const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
              valueWidget(value2),
            ],
          ),
        ),
      ],
    );
  }

  titleWidget(String title) {
    return CustomText(
      title: title,
      colors: AppTheme.lable_noramal,
      textAlign: TextAlign.start,
      fontSize: AppTheme.verySmall,
      fontWeight: FontWeight.w400,
      maxLines: 2,
    );
  }

  valueWidget(String? value) {
    return CustomText(
      title: value!.isNotEmpty ? value : "",
      colors: AppTheme.title_dark,
      textAlign: TextAlign.start,
      fontSize: AppTheme.small + 1,
      fontWeight: FontWeight.normal,
      maxLines: 2,
    );
  }
}
