import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/problem_domain/details/ticket_problem_domain_details.dart';
import 'package:savbill/pages/ticket_system/problem_domain/problem_domain_item.dart';
import 'package:savbill/pages/ticket_system/problem_domain/view_problem_domain_controller.dart';
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
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewProblemDomain extends StatefulWidget {
  @override
  _ViewProblemDomainState createState() => _ViewProblemDomainState();
}

class _ViewProblemDomainState extends State<ViewProblemDomain> {
  final viewProblemDomainController = Get.put(ViewProblemDomainController());

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
      child: GetBuilder<ViewProblemDomainController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewProblemDomainController.isLoading),
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
                          title: Strings.ticket_problem_domain,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewProblemDomainController.filterViewOpen) {
                            viewProblemDomainController.filterViewOpen = false;
                          } else {
                            viewProblemDomainController.filterViewOpen = true;
                          }
                          viewProblemDomainController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: viewProblemDomainController.isFilterApply
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
              viewProblemDomainController.filterViewOpen
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
                                      viewProblemDomainController
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
                                      Strings.select_service,
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
                                  value: viewProblemDomainController
                                      .selPlanService,
                                  items: viewProblemDomainController
                                      .planServiceList!
                                      .map((PlanServiceDetail value) {
                                    return DropdownMenuItem<PlanServiceDetail>(
                                      value: value,
                                      child: Text(value.name!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    viewProblemDomainController.selPlanService =
                                        value as PlanServiceDetail?;
                                    viewProblemDomainController.update();
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
                                          viewProblemDomainController
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
                                          viewProblemDomainController
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
              viewProblemDomainController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : Container(),
              Expanded(
                flex: 1,
                child: (viewProblemDomainController.problemDomainList != null &&
                        viewProblemDomainController
                            .problemDomainList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: viewProblemDomainController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewProblemDomainController
                                    .problemDomainList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewProblemDomainController
                                      .problemDomainList?.length) {
                                if (viewProblemDomainController
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
                                ProblemDomainDetail item =
                                    viewProblemDomainController
                                        .problemDomainList![index];
                                return ProblemDomainItem(
                                    index: index,
                                    item: item,
                                    onTapEdit: () {
                                      viewProblemDomainController
                                          .addEditProblemDomainScreen(
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
                                                viewProblemDomainController
                                                    .deleteProblemDomain(
                                                        item, index);
                                              },
                                              negativeBtnClick: () {
                                                Get.back();
                                              });
                                        },
                                      );
                                    },
                                    onTapProblemDomainDetails: () {
                                      viewProblemDomainDetailsScreen(problemDomainDetails: item);
                                    });
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
                      viewProblemDomainController.addEditProblemDomainScreen(
                          Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_ticket_problem_domain,
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



  viewProblemDomainDetailsScreen({ProblemDomainDetail? problemDomainDetails}) async {
    var result = await Get.to(()=> TicketProblemDomainDetails(),
        arguments: {Constant.PROBLEM_DOMAIN_DETAILS: problemDomainDetails});

    if (result != null && result == true) {
      viewProblemDomainController.clearFilter();
    }
  }
  _appBar() {
    return DynamicAppBar(
        Strings.problem_domain_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
