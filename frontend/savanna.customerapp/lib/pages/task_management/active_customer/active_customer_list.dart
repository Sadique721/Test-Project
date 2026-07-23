import 'package:savbill/pages/credit_note/customer_view_item.dart';
import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/task_management/active_customer/active_customer_list_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ActiveCustomerList extends StatefulWidget {
  const ActiveCustomerList({super.key});

  @override
  State<ActiveCustomerList> createState() => _ActiveCustomerListState();
}

class _ActiveCustomerListState extends State<ActiveCustomerList> {
  final activeCustomerListController = Get.put(ActiveCustomerListController());

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
      child: GetBuilder<ActiveCustomerListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: activeCustomerListController.isLoading),
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
                      title: Strings.customer_list,
                      colors: AppTheme.colorBlack,
                      textAlign: TextAlign.start,
                      fontSize: AppTheme.medium + 1,
                      fontWeight: FontWeight.w500),
                  Row(children: [
                    InkWell(
                      onTap: () {
                        if (activeCustomerListController.filterViewOpen) {
                          activeCustomerListController.filterViewOpen = false;
                        } else {
                          activeCustomerListController.filterViewOpen = true;
                        }
                        activeCustomerListController.update();
                      },
                      child: Container(
                          height: 38,
                          margin: const EdgeInsets.only(right: 0), //12
                          child: Icon(
                            Icons.filter_alt_rounded,
                            color: activeCustomerListController.isFilterApply
                                ? AppTheme.colorPrimary
                                : AppTheme.colorBlack,
                            size: 32,
                          )),
                    ),
                  ])
                ],
              ),
            ),
            const SizedBox(
              height: Constant.VERY_SMALL_PADDING,
            ),
            activeCustomerListController.filterViewOpen
                ? Padding(
              padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SCREEN_PADDING),
              child: Container(
                // width: MediaQuery.of(context).size.width,
                width: MediaQuery.of(context).size.width,
                child: Material(
                  color: AppTheme.colorWhite, //AppTheme.colorFilterBg
                  elevation: 1.5,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          Constant.BTN_ROUNDED_CORNER - 2)),
                  child: Padding(
                    padding:
                    const EdgeInsets.all(Constant.SMALL_PADDING),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
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
                                    Strings.select_search_option,
                                    style: TextStyle(
                                      fontSize: AppTheme.medium,
                                      color: AppTheme.colorIconGrey,
                                      fontFamily: AppTheme.appFontName,
                                    ))),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value: activeCustomerListController
                                .selectedSearchCategory,
                            items: activeCustomerListController
                                .searchCategory!
                                .map((CustomerSearchData value) {
                              return DropdownMenuItem<
                                  CustomerSearchData>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              activeCustomerListController
                                  .selectedSearchCategory =
                              value as CustomerSearchData?;
                              activeCustomerListController.update();
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        ),

                        activeCustomerListController.selectedSearchCategory != null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase("status") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase("cafStatus") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase("custtype") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "currentAssigneeName") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "cafCreatedDate") &&
                            activeCustomerListController
                                .selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "currentAssignedTeam") &&
                            activeCustomerListController
                                .selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase("subscriptionMode")
                            ? const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        )
                            : SizedBox.shrink(),
                        activeCustomerListController.selectedSearchCategory != null &&
                            !activeCustomerListController.selectedSearchCategory!.value!
                                .equalsIgnoreCase("status") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController.selectedSearchCategory!.value!
                                .equalsIgnoreCase("cafStatus") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController.selectedSearchCategory!.value!
                                .equalsIgnoreCase("custtype") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController.selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "currentAssigneeName") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "cafCreatedDate") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "currentAssignedTeam") &&
                            activeCustomerListController.selectedSearchCategory !=
                                null &&
                            !activeCustomerListController
                                .selectedSearchCategory!.value!
                                .equalsIgnoreCase(
                                "subscriptionMode")
                            ? CoustomTextField(
                            labelText: Strings.enter_search_detail,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: activeCustomerListController.searchController,
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(horizontal: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {},
                            onTextFiledOnTap: () {},
                            readOnly: false)
                            : SizedBox.shrink(),
                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),

                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Expanded(
                              child: SimpleButton(
                                onTap: () {
                                  activeCustomerListController
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
                                  activeCustomerListController
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
                          ],
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            )
                : Container(),
            activeCustomerListController.filterViewOpen
                ? const SizedBox(
              height: Constant.MEDIUM_PADDING,
            )
                : const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Expanded(
              flex: 1,
              child: (activeCustomerListController.parentCustomerList != null &&
                  activeCustomerListController.parentCustomerList!.isNotEmpty)
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: ListView.builder(
                    controller: activeCustomerListController.controller,
                    scrollDirection: Axis.vertical,
                    itemCount: activeCustomerListController
                        .parentCustomerList!.length +
                        1,
                    itemBuilder: (context, index) {
                      if (index ==
                          activeCustomerListController
                              .parentCustomerList?.length) {
                        if (activeCustomerListController.isShowLoadMore) {
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
                        return InkWell(
                          onTap: () async {
                            Get.back(
                                result: activeCustomerListController
                                    .parentCustomerList![index]);
                          },
                          child: CustomerViewItem(
                            index: index,
                            item: activeCustomerListController
                                .parentCustomerList![index],
                          ),
                        );
                      }
                    }),
              )
                  : noDataFound(),
            ),
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.customer_list, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
