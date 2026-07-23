import 'package:savbill/pages/customer_inventory/inventory_team_work_flow_controller.dart';
import 'package:savbill/pages/customer_inventory/request/team_hierarchy_approval_flow_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_work_flow_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class InventoryTeamWorkFlow extends StatefulWidget {
  const InventoryTeamWorkFlow({super.key});

  @override
  _InventoryTeamWorkFlowState createState() => _InventoryTeamWorkFlowState();
}

class _InventoryTeamWorkFlowState extends State<InventoryTeamWorkFlow> {
  final inventoryTeamWorkFlowController =
      Get.put(InventoryTeamWorkFlowController());
  final teamWorkInventoryFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
      child: GetBuilder<InventoryTeamWorkFlowController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: inventoryTeamWorkFlowController.isLoading),
        ]);
      }), /**/
    );
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
                Container(
                  height: 100,
                  padding: const EdgeInsets.all(Constant.MEDIUM_PADDING),
                  child:
                      (inventoryTeamWorkFlowController.teamHierarchyDataList !=
                                  null &&
                              inventoryTeamWorkFlowController
                                  .teamHierarchyDataList!.isNotEmpty)
                          ? ListView.builder(
                              shrinkWrap: true,
                              controller:
                                  inventoryTeamWorkFlowController.controller,
                              scrollDirection: Axis.horizontal,
                              itemCount: inventoryTeamWorkFlowController
                                      .teamHierarchyDataList!.length +
                                  1,
                              itemBuilder: (context, index) {
                                if (index ==
                                    inventoryTeamWorkFlowController
                                        .teamHierarchyDataList?.length) {
                                  if (inventoryTeamWorkFlowController
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
                                  TeamHierarchyDataList item =
                                      inventoryTeamWorkFlowController
                                          .teamHierarchyDataList![index];
                                  return teamItemList(item, index);
                                }
                              })
                          : noDataFound(),
                ),
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
                      fontWeight: FontWeight.w500),
                ),
                (inventoryTeamWorkFlowController.workFlowAuditDataList !=
                            null &&
                        inventoryTeamWorkFlowController
                            .workFlowAuditDataList!.isNotEmpty)
                    ? Container(
                        padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                        child: ListView.builder(
                            scrollDirection: Axis.vertical,
                            shrinkWrap: true,
                            controller:
                                inventoryTeamWorkFlowController.controller,
                            itemCount: inventoryTeamWorkFlowController
                                    .workFlowAuditDataList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  inventoryTeamWorkFlowController
                                      .workFlowAuditDataList?.length) {
                                if (inventoryTeamWorkFlowController
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
                                WorkFlowAuditDataList item =
                                    inventoryTeamWorkFlowController
                                        .workFlowAuditDataList![index];
                                return workflowAuditItem(item: item);
                              }
                            }),
                      )
                    : noDataFound(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  _appBar() {
    String? statusBarName;
    if (inventoryTeamWorkFlowController.eventName!
        .equalsIgnoreCase("CUSTOMER_DISCOUNT")) {
      statusBarName = "${Strings.invoice} ${Strings.status}";
    } else if (inventoryTeamWorkFlowController.eventName!
        .equalsIgnoreCase("CUSTOMER_SERVICE_TERMINATION")) {
      statusBarName = "${Strings.cust_service_termination} ${Strings.status}";
    } else if (inventoryTeamWorkFlowController.eventName!
        .equalsIgnoreCase("PAYMENT")) {
      statusBarName = "${Strings.payment} ${Strings.status}";
    } else if (inventoryTeamWorkFlowController.eventName!
        .equalsIgnoreCase("DOCUMENT_VERIFICATION")) {
      statusBarName = Strings.document_status;
    } else {
      statusBarName = "${Strings.credit_note} ${Strings.status}";
    }

    return DynamicAppBar(statusBarName, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }

  noDataFound() {
    return const NoDataFound();
  }

  validateForm() {
    if (teamWorkInventoryFormKey.currentState!.validate()) {
      // inventoryTeamWorkFlowController.assignInventory();
    } else {
      setState(() {
        autoValidateMode = AutovalidateMode.onUserInteraction;
      });
    }
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
                        color: AppTheme.colorLightGrey),
                    child: Icon(
                      Icons.check_circle,
                      color: AppTheme.colorBlack,
                    ),
                  ),
            inventoryTeamWorkFlowController.teamHierarchyDataList!.length - 1 ==
                    index
                ? const SizedBox.shrink()
                : Container(
                    height: 3,
                    width: 100,
                    color: AppTheme.colorGreen,
                  ),
          ],
        ),
        const SizedBox(
          height: Constant.SMALL_PADDING,
        ),
        CustomText(
            title: item.teamName,
            colors: AppTheme.colorBlack,
            textAlign: TextAlign.center,
            fontSize: AppTheme.small,
            fontWeight: FontWeight.w400)
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
            child: Row(
              mainAxisSize: MainAxisSize.max,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.staff_name),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      valueWidget(
                        (item.actionByName != null &&
                                item.actionByName!.isNotEmpty)
                            ? item.actionByName
                            : "-",
                      ),
                    ],
                  ),
                ),
                Expanded(
                  flex: 1,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      titleWidget(Strings.remarks),
                      const SizedBox(height: Constant.VERY_SMALL_PADDING - 1),
                      CustomText(
                        title: item.remark,
                        overflow: TextOverflow.ellipsis,
                        colors: AppTheme.colorBlack,
                        fontSize: AppTheme.small,
                        fontWeight: FontWeight.normal,
                        textAlign: TextAlign.start,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            // child: basicDetailItem(
            //     Strings.staff_name,
            //     (item.actionByName != null && item.actionByName!.isNotEmpty)
            //         ? item.actionByName
            //         : "-",
            //     Strings.remarks,
            //     (item.remark != null && item.remark!.isNotEmpty)
            //         ? item.remark
            //         : "-"),
          ),
          const SizedBox(
            height: Constant.SMALL_PADDING,
          ),
        ]),
      ),
    );
  }

  basicDetailItem(
      String title1, String? value1, String title2, String? value2) {
    return Row(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        Expanded(
          flex: 1,
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
          flex: 1,
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
