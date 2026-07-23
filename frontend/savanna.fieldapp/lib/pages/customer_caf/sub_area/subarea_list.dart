import 'package:flutter/material.dart';
import 'package:get/get.dart';
import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../widgets/coustom_text.dart';
import '../../../widgets/dynamic_appbar.dart';
import '../../../widgets/input_textfield.dart';
import '../../../widgets/no_data_found.dart';
import '../../../widgets/progress_bar.dart';
import '../../../widgets/simple_button.dart';
import 'subarea_controller.dart';
import 'subarea_view_item.dart';

class SubareaList extends StatefulWidget {
  @override
  _SubareaListState createState() => _SubareaListState();
}

class _SubareaListState extends State<SubareaList> {
  final creditCustomerController = Get.put(SubareaController());

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
      child: GetBuilder<SubareaController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: SafeArea(
              child: _body(),
            ),
          ),
          ProgressBar(isLoader: creditCustomerController.isLoading),
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
            Expanded(
              flex: 1,
              child: (creditCustomerController.parentCustomerList != null &&
                  creditCustomerController.parentCustomerList!.isNotEmpty)
                  ? Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: Constant.SCREEN_PADDING,
                ),
                child: Column(
                  children: [
                    CoustomTextField(
                      labelText: Strings.search_your_text_here,
                      hintColor: AppTheme.colorIconGrey,
                      textEditingController: creditCustomerController.searchController,
                      borderEnableColors: AppTheme.colorIconGrey,
                      borderFocusColors: AppTheme.colorIconGrey,
                      textColor: AppTheme.colorBlack,
                      fontSize: AppTheme.small,
                      fontWeight: FontWeight.w500,
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: Constant.MEDIUM_PADDING,
                      ),
                      borderCorner: Constant.BTN_ROUNDED_CORNER,
                      onTextValidator: (String? value) {},
                      onTextFiledOnTap: () {},
                      readOnly: false,
                    ),

                    const SizedBox(height: Constant.MEDIUM_PADDING),

                    /// Apply & Clear Buttons
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              creditCustomerController.applyFilter();
                            },
                            radius: Constant.BTN_HEIGHT_M,
                            height: Constant.BTN_HEIGHT_M,
                            bgColors: AppTheme.colorPrimary,
                            child: CustomText(
                              title: Strings.search,
                              fontSize: AppTheme.medium,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                        const SizedBox(width: Constant.MEDIUM_PADDING),
                        Expanded(
                          child: SimpleButton(
                            onTap: () {
                              creditCustomerController.clearFilter();
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

                    const SizedBox(height: Constant.MEDIUM_PADDING),

                    /// ✅ FIX – ListView Wrapped with Expanded
                    Expanded(
                      child: ListView.builder(
                        controller: creditCustomerController.controller,
                        scrollDirection: Axis.vertical,
                        itemCount: creditCustomerController
                            .parentCustomerList!.length +
                            1,
                        itemBuilder: (context, index) {
                          if (index ==
                              creditCustomerController.parentCustomerList!.length) {
                            return creditCustomerController.isShowLoadMore
                                ? Padding(
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
                            )
                                : Container();
                          } else {
                            return InkWell(
                              onTap: () async {
                                Get.back(
                                  result: creditCustomerController
                                      .parentCustomerList![index],
                                );
                              },
                              child: SubareaViewItem(
                                index: index,
                                item: creditCustomerController
                                    .parentCustomerList![index],
                              ),
                            );
                          }
                        },
                      ),
                    ),
                  ],
                ),
              )
                  : noDataFound(),
            )
          ],
        ),
      ),
    );
  }

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(Strings.subarea_list, '', AppTheme.colorPrimary, false,
        _backScreen, [], AppBar().preferredSize.height);
  }
}
