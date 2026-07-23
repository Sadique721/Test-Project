import 'dart:developer';

import 'package:savbill/pages/change_plan/change_plan_controller.dart';
import 'package:savbill/pages/change_plan/remark_dialog.dart';
import 'package:savbill/pages/change_plan/select_plan_list_cotroller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:get/get.dart';

import '../../util/resources.dart';
import '../customer/model/response/plan_service_by_customer_res.dart';
import '../customer/model/response/postpaid_planlist_res.dart';

class SelectPlanGroupList extends StatefulWidget {
  @override
  _SelectPlanGroupListState createState() => _SelectPlanGroupListState();
}

class _SelectPlanGroupListState extends State<SelectPlanGroupList>
    implements PromisePayRemarkBtnAction {
  final selectPlanGroupListController = Get.put(SelectPlanListController());
  final changePlanController = Get.find<ChangePlanController>();

  // List<ChangePlanGroupScreen> changePlanGroup = [];
  // ChangePlanGroupScreen? selectPlanGroup;

  @override
  void initState() {
    super.initState();
    // changePlanGroup.add(
    //     ChangePlanGroupScreen(planGroupName: Strings.individual, groupId: 1));
    // changePlanGroup.add(
    //     ChangePlanGroupScreen(planGroupName: Strings.plan_group, groupId: 2));
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<SelectPlanListController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: selectPlanGroupListController.isLoading),
      ]);
    });
  }

  _appBar() {
    return DynamicAppBar(
        Strings.select_plan,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [
          // IconButton(
          //   constraints: const BoxConstraints(maxHeight: 36),
          //   padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
          //   icon: const Icon(
          //     Icons.check,
          //     color: Colors.white,
          //   ),
          //   onPressed: () {
          //     validateForm();
          //   },
          // ),
        ],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (selectPlanGroupListController.selectedPlanAllData!.isNotEmpty) {
      List<PostpaidPlanDetail> selectedPlanList = [];
      for (PostpaidPlanDetail element
      in selectPlanGroupListController.selectedPlanAllData!) {
        if (element.selected != null && element.selected == true) {
          selectedPlanList.add(element);
        }
      }
      if (selectedPlanList != null && selectedPlanList.isNotEmpty) {
        Get.back(result: selectedPlanList);
      } else {
        // Utils.showSnackbar(Strings.ERROR, "Please select at-least one plan",
        //     AppTheme.colorWhite, AppTheme.colorRed);
      }
    }
  }

  showRemark() {
    if (changePlanController.planServiceList != null &&
        changePlanController.planServiceList!.isNotEmpty) {
      showDialog(
          context: context,
          barrierDismissible: true,
          builder: (BuildContext context) {
            return RemarkDialog(promisePayRemarkBtnAction: this);
          });
    } else {}
  }

  _body() {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          flex: 1,
          child: Container(
            color: AppTheme.colorBG,
            padding: const EdgeInsets.only(
                top: Constant.SCREEN_PADDING,
                left: Constant.SMALL_PADDING,
                right: Constant.SMALL_PADDING),
            child: (selectPlanGroupListController.planServiceList != null &&
                selectPlanGroupListController.planServiceList!.isNotEmpty)
                ? ListView.builder(
                scrollDirection: Axis.vertical,
                itemCount:
                selectPlanGroupListController.planServiceList!.length,
                itemBuilder: (context, index) {
                  CustomerPlanServiceDetail item =
                  selectPlanGroupListController.planServiceList![index];
                  selectPlanGroupListController.filterPlanList(
                      selectPlanGroupListController.premierePlanAllData!,
                      item);
                  return Card(
                    elevation: 3,
                    color: item.isSelectedPlan == true
                        ? AppTheme.useCardBg
                        : AppTheme.colorWhite,
                    child: Column(
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Checkbox(
                              value: item.isSelectedPlan,
                              activeColor: AppTheme.colorPrimaryTheme,
                              onChanged: (value) {
                                item.isSelectedPlan = value;
                                if (value == true) {
                                  selectPlanGroupListController
                                      .planGroupToPlan(
                                      isSelectedPlan:
                                      item.isSelectedPlan,
                                      planServiceList: item,
                                      index: index,
                                      isChildPlan: false,
                                      childIndex: -1);
                                }
                                selectPlanGroupListController.update();
                              },
                            ),
                            Expanded(
                                child: cardDataRow(
                                    Strings.connection_no,
                                    "${item.connectionNo}",
                                    AppTheme.lable_noramal)),
                            Expanded(
                                child: cardDataRow(
                                    Strings.service_name,
                                    "${item.service}",
                                    AppTheme.lable_noramal)),
                          ],
                        ),
                        const SizedBox(
                          height: Constant.VERY_SMALL_PADDING,
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.start,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Expanded(
                                child: cardDataRow(
                                    Strings.current_plan,
                                    "${item.planName}",
                                    AppTheme.lable_noramal)),
                            Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.only(
                                      right:
                                      Constant.TEXT_FIELD_CONTENT_PADDING),
                                  child: IgnorePointer(
                                    ignoring: false,
                                    child: DropdownButtonHideUnderline(
                                      child: DropdownButtonFormField(
                                        icon: SvgPicture.asset(
                                          downArrowSvg,
                                          height: Constant.SPACE_BW_RADIO_BTN,
                                          width: Constant.SPACE_BW_RADIO_BTN,
                                          color: AppTheme.colorBlack,
                                          fit: BoxFit.fill,
                                        ),
                                        decoration: Utils.ddlDecoration(),
                                        hint: Align(
                                          alignment: Alignment.centerLeft,
                                          child: Text(
                                            Strings.select_plan,
                                            style: TextStyle(
                                              fontSize: AppTheme.medium,
                                              color: AppTheme.colorIconGrey,
                                              fontFamily: AppTheme.appFontName,
                                            ),
                                          ),
                                        ),
                                        style: AppTheme.dropdownTextStyle,
                                        isExpanded: true,
                                        isDense: true,
                                        value: (selectPlanGroupListController
                                            .selectedPlanGroupItem!
                                            .isNotEmpty ||
                                            selectPlanGroupListController
                                                .selectedPlanGroupItem !=
                                                "")
                                            ? selectPlanGroupListController
                                            .selectedPlanGroupItem!
                                            : "",
                                        items: selectPlanGroupListController
                                            .selectedPlanAllData!
                                            .map((PostpaidPlanDetail value) {
                                          return DropdownMenuItem<
                                              PostpaidPlanDetail>(
                                            value: value,
                                            child: Text(value.displayName!),
                                          );
                                        }).toList(),
                                        onChanged: (value) {
                                          selectPlanGroupListController
                                              .planItemData =
                                          value as PostpaidPlanDetail?;
                                          selectPlanGroupListController
                                              .selectedPlanGroupItem =
                                          value as String;
                                          selectPlanGroupListController
                                              .update();
                                        },
                                        validator: (value) {
                                          if (value == null) {
                                            return Strings
                                                .please_select_plan_screen;
                                          }
                                          return null;
                                        },
                                      ),
                                    ),
                                  ),
                                )),
                          ],
                        ),
                        const SizedBox(
                          height: Constant.SCREEN_PADDING,
                        )
                      ],
                    ),
                  );
                })
                : noDataFound(),
          ),
        ),
        Row(
          children: [
            Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: SimpleButton(
                    onTap: () {
                      // validateForm();
                      showRemark();
                    },
                    radius: 40,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.change,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                )),
            Expanded(
                child: Padding(
                  padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                  child: SimpleButton(
                    onTap: () {
                      Get.back();
                    },
                    radius: 40,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorGrey,
                    borderColors: AppTheme.colorGrey,
                    child: CustomText(
                      title: Strings.cancel,
                      fontSize: AppTheme.medium,
                      fontWeight: FontWeight.w400,
                    ),
                  ),
                ))
          ],
        )
      ],
    );
  }

  cardDataRow(String label, String value, Color? textColor) {
    return Padding(
      padding: const EdgeInsets.all(Constant.SMALL_PADDING),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CustomText(
              title: label,
              colors: AppTheme.title_dark,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small + 1,
              height: 1,
              fontWeight: FontWeight.w500),
          const SizedBox(height: Constant.SMALL_PADDING),
          CustomText(
              title: value.isNotEmpty ? value : "-",
              colors: textColor ?? AppTheme.lable_noramal,
              textAlign: TextAlign.start,
              fontSize: AppTheme.small,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
              height: 1,
              fontWeight: FontWeight.w400)
        ],
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  @override
  void promisePayRemarkBtnAction(
      {String? identifier, TextEditingController? remarkController}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.save)) {
      changePlanController.getPromiseToRemarkApi(remarkController!);
    }
  }
}
