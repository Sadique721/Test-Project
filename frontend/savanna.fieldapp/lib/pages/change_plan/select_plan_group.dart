import 'package:savbill/pages/change_plan/select_plan_group_controller.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class SelectPlanGroup extends StatefulWidget {
  @override
  _SelectPlanGroupState createState() => _SelectPlanGroupState();
}

class _SelectPlanGroupState extends State<SelectPlanGroup> {
  final selectPlanController = Get.put(SelectPlanController());

  @override
  void initState() {
    super.initState();
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return GetBuilder<SelectPlanController>(builder: (controller) {
      return Stack(children: <Widget>[
        Scaffold(
          backgroundColor: AppTheme.colorBG,
          appBar: _appBar(),
          body: SafeArea(
            child: _body(),
          ),
        ),
        ProgressBar(isLoader: selectPlanController.isLoading),
      ]);
    });
  }

  _appBar() {
    return DynamicAppBar(
        Strings.select_plan_to_renew,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [
          IconButton(
            constraints: const BoxConstraints(maxHeight: 36),
            padding: const EdgeInsets.only(right: Constant.SMALL_PADDING),
            icon: const Icon(
              Icons.check,
              color: Colors.white,
            ),
            onPressed: () {
              validateForm();
            },
          ),
        ],
        AppBar().preferredSize.height);
  }

  validateForm() {
    if (selectPlanController.planList!.isNotEmpty) {
      List<PostpaidPlanDetail> selectedPlanList = [];
      for (PostpaidPlanDetail element in selectPlanController.planList!) {
        if (element.selected != null && element.selected == true) {
          selectedPlanList.add(element);
        }
      }

      if (selectedPlanList != null && selectedPlanList.isNotEmpty) {
        Get.back(result: selectedPlanList);
      } else {
        Utils.showSnackbar(Strings.ERROR, "Please select at-least one plan",
            AppTheme.colorWhite, AppTheme.colorRed);
      }
    }
  }

  _body() {
    return Container(
      color: AppTheme.colorBG,
      padding: const EdgeInsets.only(
          top: Constant.SCREEN_PADDING,
          left: Constant.SCREEN_PADDING,
          right: Constant.SCREEN_PADDING),
      child: (selectPlanController.planList != null &&
              selectPlanController.planList!.isNotEmpty)
          ? ListView.builder(
              scrollDirection: Axis.vertical,
              itemCount: selectPlanController.planList!.length,
              itemBuilder: (context, index) {
                PostpaidPlanDetail item = selectPlanController.planList![index];
                return InkWell(
                  onTap: () async {
                    if (item.selected != null && item.selected!) {
                      item.selected = false;
                    } else {
                      item.selected = true;
                    }
                    selectPlanController.update();
                  },
                  child: Card(
                    margin: const EdgeInsets.symmetric(
                      vertical: Constant.SMALL_PADDING,
                    ),
                    elevation: 2,
                    color: AppTheme.colorWhite,
                    child: Padding(
                      padding: const EdgeInsets.symmetric(
                        vertical: Constant.SMALL_PADDING,
                        horizontal: Constant.SMALL_PADDING,
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            mainAxisAlignment: MainAxisAlignment.spaceBetween,
                            children: [
                              CustomText(
                                  title: item.displayName,
                                  colors: AppTheme.colorBlack,
                                  textAlign: TextAlign.start,
                                  fontSize: AppTheme.medium,
                                  fontWeight: FontWeight.w500),
                              Padding(
                                padding: const EdgeInsets.only(right: 10),
                                child: SizedBox(
                                  width: 10,
                                  height: 10,
                                  child: Checkbox(
                                    value: item.selected ?? false,
                                    activeColor: AppTheme.colorPrimary,
                                    onChanged: (value) {
                                      if (item.selected != null &&
                                          item.selected!) {
                                        item.selected = false;
                                      } else {
                                        item.selected = true;
                                      }
                                      selectPlanController.update();
                                    },
                                  ),
                                ),
                              ),
                            ],
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          Divider(
                            color: AppTheme.title_dark,
                            height: 1,
                          ),
                          const SizedBox(
                            height: Constant.SMALL_PADDING,
                          ),
                          IntrinsicHeight(
                            child: Row(
                              crossAxisAlignment: CrossAxisAlignment.center,
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                Expanded(
                                  flex: 1,
                                  child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                        CustomText(
                                          title: item.offerprice ?? 0,
                                          fontSize: AppTheme.medium,
                                          maxLines: 2,
                                          colors: AppTheme.lable_noramal,
                                          textAlign: TextAlign.start,
                                          fontWeight: FontWeight.w600,
                                        ),
                                        const SizedBox(
                                          height: Constant.SMALL_PADDING,
                                        ),
                                        CustomText(
                                          title: Strings.price,
                                          fontSize: AppTheme.verySmall,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontWeight: FontWeight.normal,
                                        ),
                                      ]),
                                ),
                                VerticalDivider(
                                  color: AppTheme.title_dark,
                                  thickness: 0.4,
                                ),
                                Expanded(
                                  flex: 1,
                                  child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.center,
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                        CustomText(
                                          title:
                                              "${item.validity ?? 0} ${item.unitsOfValidity!}",
                                          fontSize: AppTheme.medium,
                                          maxLines: 2,
                                          colors: AppTheme.lable_noramal,
                                          textAlign: TextAlign.start,
                                          fontWeight: FontWeight.w600,
                                        ),
                                        const SizedBox(
                                          height: Constant.SMALL_PADDING,
                                        ),
                                        CustomText(
                                          title: Strings.validity,
                                          fontSize: AppTheme.verySmall,
                                          colors: AppTheme.title_dark,
                                          textAlign: TextAlign.start,
                                          fontWeight: FontWeight.normal,
                                        ),
                                      ]),
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              })
          : noDataFound(),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }
}
