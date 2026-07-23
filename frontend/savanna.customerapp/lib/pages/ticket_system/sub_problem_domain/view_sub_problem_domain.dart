import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/sub_problem_domain_detail.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/sub_problem_domain_item.dart';
import 'package:savbill/pages/ticket_system/sub_problem_domain/view_sub_problem_domain_controller.dart';
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

class ViewSubProblemDomain extends StatefulWidget {
  @override
  _ViewSubProblemDomainState createState() => _ViewSubProblemDomainState();
}

class _ViewSubProblemDomainState extends State<ViewSubProblemDomain> {
  final viewSubProblemDomainController =
      Get.put(ViewSubProblemDomainController());

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
      child: GetBuilder<ViewSubProblemDomainController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewSubProblemDomainController.isLoading),
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
                          title: Strings.ticket_sub_problem_domain,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewSubProblemDomainController.filterViewOpen) {
                            viewSubProblemDomainController.filterViewOpen =
                                false;
                          } else {
                            viewSubProblemDomainController.filterViewOpen =
                                true;
                          }
                          viewSubProblemDomainController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color:
                                  viewSubProblemDomainController.isFilterApply
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
              viewSubProblemDomainController.filterViewOpen
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
                                      viewSubProblemDomainController
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
                                      Strings.parent_category,
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
                                  value: viewSubProblemDomainController
                                      .selParentCategory,
                                  items: viewSubProblemDomainController
                                      .parentCategoryList!
                                      .map((ProblemDomainDetail value) {
                                    return DropdownMenuItem<
                                        ProblemDomainDetail>(
                                      value: value,
                                      child: Text(value.categoryName!),
                                    );
                                  }).toList(),
                                  onChanged: (value) {
                                    viewSubProblemDomainController
                                            .selParentCategory =
                                        value as ProblemDomainDetail?;
                                    viewSubProblemDomainController.update();
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
                                          viewSubProblemDomainController
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
                                          viewSubProblemDomainController
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
              viewSubProblemDomainController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : Container(),
              Expanded(
                flex: 1,
                child: (viewSubProblemDomainController.subProblemDomainList !=
                            null &&
                        viewSubProblemDomainController
                            .subProblemDomainList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller:
                                viewSubProblemDomainController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: viewSubProblemDomainController
                                    .subProblemDomainList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  viewSubProblemDomainController
                                      .subProblemDomainList?.length) {
                                if (viewSubProblemDomainController
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
                                SubProblemDomainDetail item =
                                    viewSubProblemDomainController
                                        .subProblemDomainList![index];
                                return InkWell(
                                  onTap: () {
                                    Get.to(SubProblemDomainDetailScreen(),
                                        arguments: {Constant.SPD_DETAIL: item});
                                  },
                                  child: SubProblemDomainItem(
                                    index: index,
                                    item: item,
                                    onTapEdit: () {
                                      viewSubProblemDomainController
                                          .addEditSubProblemDomainScreen(
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
                                                viewSubProblemDomainController
                                                    .deleteSubProblemDomain(
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
                      viewSubProblemDomainController
                          .addEditSubProblemDomainScreen(Strings.add, null);
                    },
                    radius: 0,
                    height: Constant.BOTTOM_BTN_HEIGHT,
                    bgColors: AppTheme.colorPrimary,
                    borderColors: AppTheme.colorPrimary,
                    child: CustomText(
                      title: Strings.create_ticket_sub_problem_domain,
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
        Strings.sub_problem_domain_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }
}
