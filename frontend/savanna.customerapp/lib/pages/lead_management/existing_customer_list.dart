import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/lead_management/existing_customer_controller.dart';
import 'package:savbill/pages/lead_management/existing_customer_view_item.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class ExistingCustomerList extends StatefulWidget {
  @override
  _ExistingCustomerListState createState() => _ExistingCustomerListState();
}

class _ExistingCustomerListState extends State<ExistingCustomerList> {
  final existingCustomerController = Get.put(ExistingCustomerController());

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
      child: GetBuilder<ExistingCustomerController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: existingCustomerController.isLoading),
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
                        title: Strings.select_customer,
                        colors: AppTheme.colorBlack,
                        textAlign: TextAlign.start,
                        fontSize: AppTheme.medium + 1,
                        fontWeight: FontWeight.w500),
                    Row(children: [
                      InkWell(
                        onTap: () {
                          if (existingCustomerController.filterViewOpen) {
                            existingCustomerController.filterViewOpen = false;
                          } else {
                            existingCustomerController.filterViewOpen = true;
                          }
                          existingCustomerController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //12
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: existingCustomerController.isFilterApply
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
              existingCustomerController.filterViewOpen
                  ? Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: Constant.SCREEN_PADDING),
                      child: SizedBox(
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
                                        Strings.customer_type,
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
                                    value: existingCustomerController
                                        .selectedCustomerType,
                                    items: existingCustomerController
                                        .customerTypeList!
                                        .map((DropdownDetail value) {
                                      return DropdownMenuItem<DropdownDetail>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.text!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      existingCustomerController
                                              .selectedCustomerType =
                                          value as DropdownDetail?;
                                      existingCustomerController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
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
                                        Strings.customer_type,
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
                                    value: existingCustomerController
                                        .selectedSearchOption,
                                    items: existingCustomerController
                                        .searchOptionList!
                                        .map((DropdownDetail value) {
                                      return DropdownMenuItem<DropdownDetail>(
                                        value: value,
                                        child: Align(
                                          alignment: Alignment.centerLeft,
                                          child: CustomText(
                                            title: value.id!,
                                            colors: AppTheme.colorBlack,
                                            textAlign: TextAlign.start,
                                            fontSize: AppTheme.small,
                                            fontWeight: FontWeight.w500,
                                          ), //Text(value.desig!),
                                        ),
                                      );
                                    }).toList(),
                                    onChanged: (value) {
                                      existingCustomerController
                                              .selectedSearchOption =
                                          value as DropdownDetail?;
                                      existingCustomerController.update();
                                    },
                                    validator: (value) {
                                      return null;
                                    },
                                  ),
                                ),
                                const SizedBox(
                                  height: Constant.MEDIUM_PADDING,
                                ),
                                CoustomTextField(
                                    labelText: Strings.search_your_text_here,
                                    hintColor: AppTheme.colorIconGrey,
                                    textEditingController:
                                        existingCustomerController
                                            .searchController,
                                    borderEnableColors: AppTheme.colorIconGrey,
                                    borderFocusColors: AppTheme.colorIconGrey,
                                    textColor: AppTheme.colorBlack,
                                    fontSize: AppTheme.small,
                                    fontWeight: FontWeight.w500,
                                    contentPadding: const EdgeInsets.symmetric(
                                        horizontal: Constant.MEDIUM_PADDING),
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
                                          existingCustomerController
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
                                          existingCustomerController
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
              existingCustomerController.filterViewOpen
                  ? const SizedBox(
                      height: Constant.MEDIUM_PADDING,
                    )
                  : const SizedBox(
                      height: Constant.SMALL_PADDING,
                    ),
              Expanded(
                child: (existingCustomerController.customerList != null &&
                        existingCustomerController.customerList!.isNotEmpty)
                    ? Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: Constant.SCREEN_PADDING),
                        child: ListView.builder(
                            controller: existingCustomerController.controller,
                            scrollDirection: Axis.vertical,
                            itemCount: existingCustomerController
                                    .customerList!.length +
                                1,
                            itemBuilder: (context, index) {
                              if (index ==
                                  existingCustomerController
                                      .customerList?.length) {
                                if (existingCustomerController.isShowLoadMore) {
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
                                          result: existingCustomerController
                                              .customerList![index]);

                                  },
                                  child: ExistingCustomerViewItem(
                                      index: index,
                                      item: existingCustomerController
                                          .customerList![index]),
                                );
                              }
                            }),
                      )
                    : noDataFound(),
              ),
            ]),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.existing_customer, '', AppTheme.colorPrimary,
        false, _backScreen, [], AppBar().preferredSize.height);
  }
}
