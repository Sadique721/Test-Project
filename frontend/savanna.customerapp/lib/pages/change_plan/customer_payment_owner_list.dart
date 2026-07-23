import 'package:savbill/pages/change_plan/customer_payment_owner_item.dart';
import 'package:savbill/pages/change_plan/customer_payment_owner_list_controller.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class CustomerPaymentOwnerList extends StatefulWidget {
  @override
  _ParentCustomerListState createState() => _ParentCustomerListState();
}

class _ParentCustomerListState extends State<CustomerPaymentOwnerList> {
  final parentCustomerController = Get.put(CustomerPaymentOwnerListController());

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
      child: GetBuilder<CustomerPaymentOwnerListController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: parentCustomerController.isLoading),
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
                        if (parentCustomerController.filterViewOpen) {
                          parentCustomerController.filterViewOpen = false;
                        } else {
                          parentCustomerController.filterViewOpen = true;
                        }
                        parentCustomerController.update();
                      },
                      child: Container(
                          height: 38,
                          margin: const EdgeInsets.only(right: 0), //12
                          child: Icon(
                            Icons.filter_alt_rounded,
                            color: parentCustomerController.isFilterApply
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
            parentCustomerController.filterViewOpen
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
                        CoustomTextField(
                            labelText: Strings.enter_search_detail,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: parentCustomerController.searchController,
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            fontSize: AppTheme.small,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(horizontal: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {},
                            onTextFiledOnTap: () {},
                            readOnly: false),

                        const SizedBox(
                          height: Constant.MEDIUM_PADDING,
                        ),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Expanded(
                              child: SimpleButton(
                                onTap: () {
                                  parentCustomerController
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
                                  parentCustomerController
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
            parentCustomerController.filterViewOpen
                ? const SizedBox(
              height: Constant.MEDIUM_PADDING,
            )
                : const SizedBox(
              height: Constant.SMALL_PADDING,
            ),
            Expanded(
              flex: 1,
              child: (parentCustomerController.staffUserlist != null &&
                  parentCustomerController.staffUserlist!.isNotEmpty)
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: ListView.builder(
                    controller: parentCustomerController.controller,
                    scrollDirection: Axis.vertical,
                    itemCount: parentCustomerController
                        .staffUserlist!.length +
                        1,
                    itemBuilder: (context, index) {
                      if (index ==
                          parentCustomerController
                              .staffUserlist?.length) {
                        if (parentCustomerController.isShowLoadMore) {
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
                                result: parentCustomerController
                                    .staffUserlist![index]);
                          },
                          child: CustomerPaymentOwnerViewItem(
                            index: index,
                            item: parentCustomerController
                                .staffUserlist![index],
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
    return DynamicAppBar(Strings.select_staff, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
